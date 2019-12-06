import java.util.Random;

public class BabyYoda extends BossFactory {

    private static int count = 0;
    private static final Random rand = new Random();

    public BabyYoda() {}

    public void create(int LEVEL) {

        while (count < 2*LEVEL) {

            Point pos = new Point(32,4);

            while(getV().getWorld().getOccupancyCell(pos) instanceof Obstacle)
            {
                pos = new Point(2 + rand.nextInt(37 - 2), 3 + rand.nextInt(22 -3));
            }

            MinionBoss minion = new MinionBoss("minion", pos, getV().getImageStore().getImageList("YodaMinionR"), 7000, 2001);
            getV().getWorld().addEntity(minion);
            minion.scheduleActions(getV().getScheduler(), getV().getWorld(), getV().getImageStore());
            count++;
        }
        count = 0;
    }
}
