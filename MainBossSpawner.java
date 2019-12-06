import processing.core.PImage;

import java.util.List;
import java.util.Set;

public class MainBossSpawner extends Moveable {

    private int LEVEL;

    public MainBossSpawner(String id, Point position, List<PImage> images, int imageIndex, int actionPeriod, int animationPeriod, int LEVEL) {
        super(id, position, images, imageIndex, actionPeriod, animationPeriod);
        this.LEVEL = LEVEL;
    }

    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {

            Boss b = new Boss("mainBoss", new Point(10, 19), getV().getImageStore().getImageList("mainBossU"), 2500 / LEVEL, 1003);
            getV().getWorld().addEntity(b);
            b.scheduleActions(getV().getScheduler(), getV().getWorld(), getV().getImageStore());
        }

}
