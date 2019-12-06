public class SanHolo extends BossFactory {

    public SanHolo(){}

    public void create(int LEVEL)
    {
        Boss sanH = new Boss("sanH", new Point (25,20), getV().getImageStore().getImageList("sanHoloR"), 3000 / LEVEL, 1100);
        getV().getWorld().addEntity(sanH);
    }
}
