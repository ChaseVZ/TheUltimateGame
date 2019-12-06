import processing.core.PImage;
import java.util.List;

public abstract class InteractiveEntity extends Entity{

    public InteractiveEntity(String id, Point position, List< PImage > images, int imageIndex){
            super(id, position, images, imageIndex);
    }

    abstract void Interact();

}
