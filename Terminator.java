public class Terminator extends BossFactory {

    private Point pos = new Point(10, 20);

    public Terminator() {}

    public void create(int LEVEL)
    {
        MainBossSpawner b = new MainBossSpawner("mainBoss", pos, getV().getImageStore().getImageList("mainBossU"), 0, 1500, 1000, LEVEL);
        getV().getWorld().addEntity(b);
    }

    public MainBossSpawner getMainBossSpawner()
    {
        if(getV().getWorld().isOccupied(pos)) {
            if (!(getV().getWorld().getOccupancyCell(pos) instanceof Quake))
                return (MainBossSpawner) getV().getWorld().getOccupancyCell(pos);
        }
        return null;
    }

}
