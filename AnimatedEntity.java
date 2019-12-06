import processing.core.PImage;
import java.util.List;
import java.util.Optional;

public abstract class AnimatedEntity extends ActiveEntity{  //contains animationPeriod, nextImage

    private int animationPeriod;

    public AnimatedEntity(String id, Point position, List<PImage> images, int imageIndex, int actionPeriod, int animationPeriod){
        super(id, position, images, imageIndex, actionPeriod);
        this.animationPeriod = animationPeriod;
    }

    //getter
    protected int getAnimationPeriod(){return this.animationPeriod;}
    public void setAnimationPeriod(int s) {this.animationPeriod = s;}

    //implemented methods
    public Action createAnimationAction(int repeatCount) {
        return new Animation(this, repeatCount);
    }
    public void nextImage() {super.setImageIndex((super.getImageIndex() + 1) % super.getImages().size());}
    public void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore)
    {
        scheduler.scheduleEvent(this, new Activity(this, world, imageStore), super.getActionPeriod());
        scheduler.scheduleEvent(this, createAnimationAction( 0), this.getAnimationPeriod());
    }

    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler){
        {
            scheduler.unscheduleAllEvents(this);
            world.removeEntity(this);
        }
    }


    //abstract methods

}
