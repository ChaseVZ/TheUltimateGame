import processing.core.PImage;

import java.awt.event.WindowStateListener;
import java.util.List;
import java.util.Optional;

public class Boss extends Moveable {

    private static final String MAIN_CHARACTER_PRESENCE = "HE'S BEEN SPOTTED";
    private final int SanHoloAnimation = 1100;
    private final int TerminatorAnimation = 1003;
    private AStarPathingStrategy p = new AStarPathingStrategy();
    private static final String QUAKE_KEY = "quake";

    public Boss(String id, Point position, List<PImage> images, int actionPeriod, int animationPeriod)
    {
        super(id, position, images, 0, actionPeriod, animationPeriod);
    }

    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler)
    {
        Optional<Entity> bossTarget = world.findNearest(this.getPosition(), MainCharacter.class);
        long nextPeriod = super.getActionPeriod();

        if (bossTarget.isPresent())
        {
            Point tgtPos = bossTarget.get().getPosition();

            String suffix = getDirectionToTarget(tgtPos, getPosition());
            String direction;
            boolean noChange = false;
            if (getAnimationPeriod() == SanHoloAnimation)
            {
                if(suffix == null) {
                    noChange = true;
                    direction = "previous"; //placeholder
                }
                else
                    direction = "sanHolo" + suffix;
            }
            else if (getAnimationPeriod() == TerminatorAnimation)
                direction = "mainBoss" + suffix;
            else
                direction = "placeholder";


            if(!noChange) {
                setImageIndex(0);
                setImages(getV().getImageStore().getImageList(direction));
            }


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

}
