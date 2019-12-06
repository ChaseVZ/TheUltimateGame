import java.util.Random;

public class Ore extends OreFactory {

    private static final Random rand = new Random();
    private static int count = 0;

    public Ore(){}

    public void create(int LEVEL)    {

        while (count < 8*LEVEL)
        {
            Point openPt = new Point(32,4);

            while(getV().getWorld().getOccupancyCell(openPt) instanceof Obstacle)
            {
                openPt = new Point(2 + rand.nextInt(37 - 2), 3 + rand.nextInt(22 - 3));
            }

            BasicOre basicOre = new BasicOre("basicOre", openPt, getV().getImageStore().getImageList("BasicOre1"), 0, 100*LEVEL);
            getV().getWorld().addEntity(basicOre);
//            basicOre.scheduleActions(getV().getScheduler(), getV().getWorld(), getV().getImageStore());
            count++;
        }
    }

    public int getCount() {return count;}
    public void setCount(int c) {count = c;}

}
