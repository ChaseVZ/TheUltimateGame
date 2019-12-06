import processing.core.PImage;
import java.util.List;

public abstract class ResourceEntity extends Moveable {
    private int resourceLimit;
    private int resourceCount;

    public ResourceEntity(String id, Point position, List<PImage> images, int imageIndex, int resourceLimit, int resourceCount, int actionPeriod, int animationPeriod) {
        super(id, position, images, imageIndex, actionPeriod, animationPeriod);
        this.resourceCount = resourceCount;
        this.resourceLimit = resourceLimit;
    }

    //getter
    public int getResourceLimit() { return this.resourceLimit; }
    public int getResourceCount() { return this.resourceCount; }
    public void setResourceCount(int s) { this.resourceCount += s; }
    public void setResourceLimit(int s) {this.resourceLimit = s;}

    //implemented methods


    //abstract methods
}
