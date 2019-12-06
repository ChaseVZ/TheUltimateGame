import processing.core.PImage;
import java.util.List;

public abstract class ActiveEntity extends Entity{  //actionPeriod, executeActivity

    private int actionPeriod;

    public ActiveEntity(String id, Point position, List<PImage> images, int imageIndex, int actionPeriod){
        super(id, position, images, imageIndex);
        this.actionPeriod = actionPeriod;
    }

    //getter
    protected int getActionPeriod(){return this.actionPeriod;}
    public void setActionPeriod(int i) {this.actionPeriod = i;}

    //implemented methods
    public void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore)
    { scheduler.scheduleEvent(this, new Activity(this, world, imageStore), this.actionPeriod); }

    //abstract methods
    abstract void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler);

    public Point getNextPoint(String direction, Point myPos)
    {
        Point up = new Point(myPos.x, myPos.y - 1);
        Point down = new Point(myPos.x, myPos.y + 1);
        Point right = new Point(myPos.x + 1, myPos.y);
        Point left = new Point(myPos.x - 1, myPos.y);

        if(direction.equals("up"))
            return up;
        if(direction.equals("down"))
            return down;
        if(direction.equals("right"))
            return right;
        if(direction.equals("left"))
            return left;
        return new Point(0,0);
    }
}
