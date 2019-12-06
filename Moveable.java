import processing.core.PImage;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public abstract class Moveable extends AnimatedEntity {

    private AStarPathingStrategy strategy = new AStarPathingStrategy();

    public Moveable(String id, Point position, List<PImage> images, int imageIndex, int actionPeriod, int animationPeriod){
        super(id, position, images, imageIndex, actionPeriod, animationPeriod);
    }

    //getter
    public AStarPathingStrategy getStrategy() {return this.strategy;}

    //implemented methods
    public Point nextPosition( WorldModel world, Point destPos) {
        List<Point> points;
        points = strategy.computePath(this.getPosition(), destPos, p -> world.withinBounds(p) && !world.isOccupied(p),
                (p1, p2) -> neighbors(p1, p2), PathingStrategy.CARDINAL_NEIGHBORS);

        Point returnPoint;
        if (points.size() != 0) {
            returnPoint = points.get(0);
        } else
            returnPoint = this.getPosition();

        return returnPoint;
    }


    public boolean moveTo(WorldModel world, Entity target, EventScheduler scheduler){
        if (Point.adjacent(this.getPosition(), target.getPosition())){
            if (target instanceof MainCharacter)
            {
                if(getV().getMain().getH().takeDamage()) {
//                world.removeEntity( target);
//                scheduler.unscheduleAllEvents( target);
                getV().getMain().die();
                return true;
            }
                // Display losing message on screen
//                System.out.println("You lose: the boss came too close.");

            }
            return false;
        }
        else
        {
            Point nextPos = nextPosition(world, target.getPosition());

            if (!this.getPosition().equals(nextPos))
            {
                Optional<Entity> occupant = world.getOccupant( nextPos);
                if (occupant.isPresent())
                {
                    scheduler.unscheduleAllEvents( occupant.get());
                }

                world.moveEntity( this, nextPos);
            }
            return false;
        }
    }

    protected static boolean neighbors(Point p1, Point p2)
    {
        return p1.x+1 == p2.x && p1.y == p2.y ||
                p1.x-1 == p2.x && p1.y == p2.y ||
                p1.x == p2.x && p1.y+1 == p2.y ||
                p1.x == p2.x && p1.y-1 == p2.y;
    }

    public String getDirectionToTarget(Point targetPos, Point myPos)
    {
        final int TerminatorAnimation = 1003;

        if (targetPos.y > myPos.y)
            if (this.getAnimationPeriod() == TerminatorAnimation)
                return "D";
        if (targetPos.y < myPos.y)
            if (this.getAnimationPeriod() == TerminatorAnimation)
                return "U";
        if (targetPos.x > myPos.x)
            return "R";
        if (targetPos.x < myPos.x)
            return "L";
        else
            return "R";
    }


    //abstract methods
}
