import processing.core.PImage;
import java.util.List;

public class AnimatedObject extends AnimatedEntity {

    public AnimatedObject(String id, Point position, List<PImage> images, int actionPeriod, int animationPeriod) {
        super(id, position, images, 0, actionPeriod, animationPeriod);
    }


}
