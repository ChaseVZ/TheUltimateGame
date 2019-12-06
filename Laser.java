import processing.core.PImage;
import java.util.List;

public class Laser extends ActiveEntity {

    private int range;
    private String direction;
    private int distance;
    private static String QUAKE_KEY = "quake";
    private static Point prevPoint = new Point(42, 2);
    private static Point prevPrevPoint = new Point(42, 3);
    private static int SanHoloAnimationFlag = 1100;
    private static int TerminatorAnimationFlag = 1003;
    private static int TerminatorBrainAnimationFlag = 1000;
    private static int BabyYodaAnimationFlag = 2001;

    public Laser(String id, Point position, List<PImage> images, int imageIndex, int actionPeriod, int range, String direction){
        super(id, position, images, imageIndex, actionPeriod);
        this.range = range;
        this.direction = direction;
        this.distance = 0;
    }

    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler)
    {
        Point nextPos = getNextPoint(direction, getPosition());
        long nextPeriod = super.getActionPeriod();

        if (distance < range)
        {

            if (moveTo(world, scheduler, nextPos, imageStore)) //if T then the laser needs to be destroyed otherwise continue
            {
                world.removeEntity(this);
                scheduler.unscheduleAllEvents(this);
            }
            else {
                world.moveEntity(this, nextPos);
                prevPrevPoint = prevPoint;
                prevPoint = getPosition();
            }


            scheduler.scheduleEvent(this, new Activity(this, world, imageStore), nextPeriod);
            distance ++;
        }
        else {  //reached end of path
            world.removeEntity(this);
            scheduler.unscheduleAllEvents(this);
        }
    }


    public boolean moveTo(WorldModel world, EventScheduler scheduler, Point nextPos, ImageStore imageStore) {
        if(world.isOccupied(prevPoint) && ((world.getOccupancyCell(prevPoint) instanceof Boss) || (world.getOccupancyCell(prevPoint) instanceof MinionBoss)
                || world.getOccupancyCell(prevPoint) instanceof MainBossSpawner))
        {
            removeEntity(world.getOccupancyCell(prevPoint), prevPoint, world, scheduler, imageStore);
            return true;
        }

        if(world.isOccupied(prevPrevPoint) && ((world.getOccupancyCell(prevPrevPoint) instanceof Boss) || (world.getOccupancyCell(prevPrevPoint) instanceof MinionBoss
                || world.getOccupancyCell(prevPrevPoint) instanceof MainBossSpawner)))
        {
            removeEntity(world.getOccupancyCell(prevPrevPoint), prevPrevPoint, world, scheduler, imageStore);
            return true;
        }

        if (world.withinBounds(nextPos)) {
            if(world.isOccupied(nextPos))
            {
                Entity e = world.getOccupancyCell(nextPos);
                if (e instanceof MinionBoss || e instanceof Boss || e instanceof MainBossSpawner) {
                    removeEntity(e, nextPos, world, scheduler, imageStore);
                    return true;
                }
                return true;
            }
            return false;
        }
        return true;
    }


    public void removeEntity(Entity e, Point pos, WorldModel world, EventScheduler scheduler, ImageStore imageStore)
    {
        incrementCurrency(e);
        if (e instanceof Boss && (((Boss) e).getAnimationPeriod() == 1003))
        {
            if(getV().getTerminator().getMainBossSpawner() != null)
                getV().getTerminator().getMainBossSpawner().scheduleActions(scheduler, world, getV().getImageStore());
        }

        world.removeEntity(e);
        scheduler.unscheduleAllEvents(e);

        Quake quake = new Quake(QUAKE_KEY, pos, imageStore.getImageList(QUAKE_KEY), 7000, 1000);

        world.addEntity(quake);
        quake.scheduleActions(scheduler, world, imageStore);
    }

    public void incrementCurrency(Entity e)
    {
       ItemShop itemShop = ItemShop.getItemShop();
        int AnimationFlag = 0;
        if(e instanceof Boss)
            AnimationFlag = ((Boss) e).getAnimationPeriod();

        else if(e instanceof MinionBoss)
            AnimationFlag = ((MinionBoss)e).getAnimationPeriod();

        else if(e instanceof MainBossSpawner)
            AnimationFlag = ((MainBossSpawner) e).getAnimationPeriod();

        if(AnimationFlag == SanHoloAnimationFlag) {
            getV().setSanHoloFlag(true);
            itemShop.SanHoloKill();
        }
        else if(AnimationFlag == BabyYodaAnimationFlag)
            itemShop.BabyYodaKill();
        else if(AnimationFlag == TerminatorAnimationFlag)
            itemShop.TerminatorKill();
        else if(AnimationFlag == TerminatorBrainAnimationFlag) {
            getV().incrementLevel();
            getV().setTerminatorBrainFlag(false);
            getV().setSanHoloFlag(false);
//            getV().setTerminatorBrainFlag(true);
            itemShop.TerminatorBrainKill();
        }

    }


}
