import com.sun.tools.javac.Main;
import processing.core.PImage;

import java.util.List;
import java.util.Optional;

public class MinionBoss extends Moveable {

    private static final String QUAKE_KEY = "quake";

    public MinionBoss(String id, Point position, List<PImage> images, int actionPeriod, int animationPeriod)
    {
        super(id, position, images, 0, actionPeriod, animationPeriod);
    }

    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler)
    {
        Optional<Entity> bossTarget = world.findNearest(this.getPosition(), BasicOre.class);
        long nextPeriod = super.getActionPeriod();

        if(!bossTarget.isPresent())
            bossTarget = world.findNearest(this.getPosition(), MainCharacter.class);

        if (bossTarget.isPresent())
        {
            Point tgtPos = bossTarget.get().getPosition();

            if (moveTo(world, bossTarget.get(), scheduler))
            {
                Quake quake = new Quake(QUAKE_KEY, tgtPos, imageStore.getImageList(QUAKE_KEY), 7000, 1000);

                world.addEntity(quake);
                nextPeriod += super.getActionPeriod();
                quake.scheduleActions(scheduler, world, imageStore);
            }
        }
        scheduler.scheduleEvent(this,
                new Activity(this, world, imageStore),
                nextPeriod);
    }

    public boolean moveTo(WorldModel world, Entity target, EventScheduler scheduler){
        Ore o = getV().getOre();
        if (Point.adjacent(this.getPosition(), target.getPosition())){
            if(target instanceof BasicOre) {
                world.removeEntity(target);
                scheduler.unscheduleAllEvents(target);
                return true;
            }
            if (target instanceof MainCharacter)
            {

                if(getV().getMain().getH().takeDamage()) {
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
}
