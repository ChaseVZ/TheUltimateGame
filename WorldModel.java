import com.sun.tools.javac.Main;
import processing.core.PImage;

import java.util.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/*
WorldModel ideally keeps track of the actual size of our grid world and what is in that world
in terms of entities and background elements
 */

final class WorldModel {

   private static final int FISH_REACH = 1;

   private int numRows;
   private int numCols;
   private Background background[][];
   private Entity occupancy[][];
   private Set<Entity> entities;


   public WorldModel(int numRows, int numCols, Background defaultBackground) {
      this.numRows = numRows;
      this.numCols = numCols;
      this.background = new Background[numRows][numCols];
      this.occupancy = new Entity[numRows][numCols];
      this.entities = new HashSet<>();

      for (int row = 0; row < numRows; row++) {
         Arrays.fill(this.background[row], defaultBackground);
      }
   }

   public int getnumRow() {return this.numRows;}
   public int getnumCol() {return this.numCols;}
   public Set<Entity> getEntities() {return this.entities;}

   public boolean withinBounds(Point pos) {
      return pos.y >= 0 && pos.y < numRows && pos.x >= 0 && pos.x < numCols;
   }

   public boolean isOccupied(Point pos) {
      return (!withinBounds(pos) || (getOccupancyCell(pos) != null) && !(getOccupancyCell(pos) instanceof Laser));
//      return withinBounds(pos) && getOccupancyCell(pos) != null;
   }

   public boolean withinReach(Point pos, Point target, int threshold)
   {
      int xDiff = Math.abs(pos.x - target.x);
      int yDiff = Math.abs(pos.y - target.y);
      int max = Math.max(xDiff, yDiff);

      if (threshold < max)
         return false;
      return true;
   }

   public Entity OreWithinReach(Point myPos, String direction)
   {
      Point up = new Point(myPos.x, myPos.y - 1);
      Point down = new Point(myPos.x, myPos.y + 1);
      Point right = new Point(myPos.x + 1, myPos.y);
      Point left = new Point(myPos.x - 1, myPos.y);

      if(direction.equals("up")) {
         if (isOccupied(up) && getOccupancyCell(up) instanceof BasicOre)
            return getOccupancyCell(up);
      }
      else if(direction.equals("down")) {
         if (isOccupied(down) && getOccupancyCell(down) instanceof BasicOre)
            return getOccupancyCell(down);
      }
      else if(direction.equals("right")) {
         if (isOccupied(right) && getOccupancyCell(right) instanceof BasicOre)
            return getOccupancyCell(right);
      }
      else if(direction.equals("left")) {
         if (isOccupied(left) && getOccupancyCell(left) instanceof BasicOre)
            return getOccupancyCell(left);
      }
      return null;
   }


   public void removeEntityAt(Point pos)
   {
      if (withinBounds(pos) && getOccupancyCell(pos) != null)
      {
         Entity entity = getOccupancyCell(pos);

         /* this moves the entity just outside of the grid for
            debugging purposes */
         entity.setPosition(new Point(42, 1));
         entities.remove(entity);
         setOccupancyCell(pos, null);
      }
   }

   public void removeEntity(Entity entity)
   {
      removeEntityAt(entity.getPosition());
   }

   public void removeAllEntities()
   {
      Object[] eList = getEntities().toArray();
      int size = this.entities.size();
      for (int i = 0; i < size; i++)
      {
         if(!(eList[i] instanceof MainCharacter) && !(eList[i] instanceof Heart))
            removeEntity((Entity)eList[i]);
      }

   }

   public void setBackground(Point pos, Background background)
   {
      if (withinBounds(pos))
      {
         setBackgroundCell(pos, background);
      }
   }

   public Optional<Entity> getOccupant(Point pos)
   {
      if (isOccupied(pos))
      {
         return Optional.of(getOccupancyCell(pos));
      }
      else
      {
         return Optional.empty();
      }
   }

   public Entity getOccupancyCell(Point pos)
   {
       return occupancy[pos.y][pos.x];
   }

   public void setOccupancyCell(Point pos, Entity entity)
   {
      occupancy[pos.y][pos.x] = entity;
   }

   public Background getBackgroundCell(Point pos)
   {
      return this.background[pos.y][pos.x];
   }

   public void setBackgroundCell(Point pos, Background background)
   {
      this.background[pos.y][pos.x] = background;
   }

   public Optional<Entity> findNearest(Point pos, Class kind)
   {
      List<Entity> ofType = new LinkedList<>();
      for (Entity entity : entities)
      {
         if (kind.isInstance(entity))
         {
            ofType.add(entity);
         }
      }

      return nearestEntity(ofType, pos);
   }

   public Optional<Entity> nearestEntity(List<Entity> entities, Point pos)  //Could stay in main
   {
      if (entities.isEmpty())
      {
         return Optional.empty();
      }
      else
      {
         Entity nearest = entities.get(0);
         int nearestDistance = distanceSquared(nearest.getPosition(), pos);

         for (Entity other : entities)
         {
            int otherDistance = distanceSquared(other.getPosition(), pos);

            if (otherDistance < nearestDistance)
            {
               nearest = other;
               nearestDistance = otherDistance;
            }
         }

         return Optional.of(nearest);
      }
   }

   private static int distanceSquared(Point p1, Point p2)  //Could remain in main
   {
      int deltaX = p1.x - p2.x;
      int deltaY = p1.y - p2.y;

      return deltaX * deltaX + deltaY * deltaY;
   }

   public void addEntity(Entity entity)
   {
      if (withinBounds(entity.getPosition()))
      {
         setOccupancyCell(entity.getPosition(), entity);
         entities.add(entity);
      }
   }

   public void moveEntity(Entity entity, Point pos)
   {
      Point oldPos = entity.getPosition();
      if (withinBounds(pos) && !pos.equals(oldPos))
      {
         setOccupancyCell(oldPos, null);
         removeEntityAt(pos);
         setOccupancyCell(pos, entity);
         entity.setPosition(pos);
      }
   }

   public Optional<PImage> getBackgroundImage(Point pos)
   {
      if (this.withinBounds(pos))
      {
         return Optional.of(this.getBackgroundCell(pos).getCurrentImage());
      }
      else
      {
         return Optional.empty();
      }
   }

   public Optional<Point> findOpenAround(Point pos)
   {
      for (int dy = -FISH_REACH; dy <= FISH_REACH; dy++)
      {
         for (int dx = -FISH_REACH; dx <= FISH_REACH; dx++)
         {
            Point newPt = new Point(pos.x + dx, pos.y + dy);
            if (withinBounds(newPt) && !isOccupied(newPt))
            {
               return Optional.of(newPt);
            }
         }
      }

      return Optional.empty();
   }

   public void tryAddEntity(Entity entity)
   {
      if (isOccupied(entity.getPosition())) {
         // arguably the wrong type of exception, but we are not
         // defining our own exceptions yet
         throw new IllegalArgumentException("position occupied");
      }
   addEntity(entity);
   }

//   public Action createActivityAction(ActiveEntity entity,
//                                      ImageStore imageStore) {
//      return new Activity(entity, this, imageStore);  //new Action >> ActionKind.ACTIVITY, entity, this, imageStore, 0)
//   }
}
