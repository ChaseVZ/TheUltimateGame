import com.sun.tools.javac.Main;
import processing.core.PImage;
import java.util.List;
import java.util.Set;

public class MainCharacter extends ResourceEntity{

    private static MainCharacter m;

    private static final String CAVE_EXIT = "caveEntrance";
    private static final String CAVE_ENTER = "caveExit";
    private static final String TOWN_TO_COMBAT = "barrier";
    private static final String INIT_EXIT = "barrier1";

    private final Point TOWN_EXIT1 = new Point(21, 24);
    private final Point TOWN_EXIT2 = new Point(22, 24);
    private final Point TOWN_EXIT3 = new Point(23, 24);
    private final Point TOWN_EXIT4 = new Point(24, 24);
    private final Point TOWN_EXIT5 = new Point(25, 24);
    private final Point TOWN_EXIT6 = new Point(26, 24);
    private final Point TOWN_EXIT7 = new Point(27, 24);
    private final Point COMBAT_EXIT1 = new Point(21, 0);
    private final Point COMBAT_EXIT2 = new Point(22, 0);
    private final Point COMBAT_EXIT3 = new Point(23, 0);
    private final Point COMBAT_EXIT4 = new Point(24, 0);
    private final Point COMBAT_EXIT5 = new Point(25, 0);
    private final Point COMBAT_EXIT6 = new Point(26, 0);
    private final Point COMBAT_EXIT7 = new Point(27, 0);

    private String worldType;
    private int WeaponRange;
    private HeartFactory h = new HeartFactory();

    private MainCharacter(String id, Point position, List<PImage> images, int resourceLimit, int resourceCount, int actionPeriod, int animationPeriod)
    {
        super(id, position, images, 0, resourceLimit, resourceCount, actionPeriod, animationPeriod);
        this.WeaponRange = 4;
    }

    public void setWorldType(String s) {this.worldType = s;}
    public String getWorldType() {return worldType;}
    public HeartFactory getH() {return h;}
    public int getWeaponRange() {return WeaponRange;}
    public void incrementWeaponRange() {WeaponRange += 1;}
    public void LoadHearts() { h.loadHearts(); }

    public boolean move(WorldModel world, int horiz, int vert, String direction) {
        Point newPos = new Point(super.getPosition().x + horiz, super.getPosition().y + vert);

        if (!(world.withinBounds(newPos)) && worldType.equals("Combat") && checkExit(getPosition(), direction))
        {
            for (Entity e: getV().getWorld().getEntities()) {
                if (!(e instanceof MainCharacter))
                    getV().getScheduler().unscheduleAllEvents(e);
            }
            getV().getWorld().removeAllEntities();
            Point oldPos = getPosition();

            setPosition(new Point(3, 2));  // in hopes of avoiding worldCreation issues
            getV().Town();

            Point p = new Point(oldPos.x, oldPos.y + 23);
            setPosition(p);
            world.setOccupancyCell(oldPos, null);
            return false;
        }

        if (world.withinBounds(newPos)) {
            if (!(world.isOccupied(newPos))) {
                if (worldType.equals("Combat") && checkExit(newPos, direction)) {

                    for (Entity e: getV().getWorld().getEntities()) {
                        if (!(e instanceof MainCharacter))
                            getV().getScheduler().unscheduleAllEvents(e);
                    }
                    getV().getWorld().removeAllEntities();
                    Point oldPos = getPosition();

                    setPosition(new Point(3, 2));  // in hopes of avoiding worldCreation issues
                    getV().Town();

                    Point p = new Point(oldPos.x, oldPos.y + 23);
                    setPosition(p);
                    world.setOccupancyCell(oldPos, null);
                    return false;
                } else {
                    if (world.withinReach(getPosition(), TOWN_EXIT4, 4) && worldType.equals("Town"))
                        getV().getParsing().processLine("background ArrowDown 24 23", getV().getImageStore());
                    else if (world.withinReach(getPosition(), COMBAT_EXIT4, 4) && worldType.equals("Combat"))
                        getV().getParsing().processLine("background ArrowUp 24 0", getV().getImageStore());
                    else if (worldType.equals("Town"))
                        getV().getParsing().processLine("background DirtPath 24 23", getV().getImageStore());
                    else if (worldType.equals("Combat"))
                        getV().getParsing().processLine("background DirtPath 24 0", getV().getImageStore());

                    if (world.getOccupancyCell(newPos) instanceof Laser)
                        return false;

                    world.moveEntity(this, newPos);
                    return false;
                }
            } else {
                if (world.getOccupancyCell(newPos) instanceof Obstacle) {
                    Obstacle o = (Obstacle) world.getOccupancyCell(newPos);
                    if (o.getId().equals(CAVE_EXIT)) {

                        for (Entity e: getV().getWorld().getEntities()) {
                            if (!(e instanceof MainCharacter))
                                getV().getScheduler().unscheduleAllEvents(e);
                        }
                        getV().getWorld().removeAllEntities();
                        getV().Town();
                        setImageIndex(0);
                        setImages(getV().getImageStore().getImageList("mainDown"));
                        return true;
                    }
                    if (o.getId().equals(CAVE_ENTER)) {

                        for (Entity e: getV().getWorld().getEntities()) {
                            if (!(e instanceof MainCharacter))
                                getV().getScheduler().unscheduleAllEvents(e);
                        }
                        getV().getWorld().removeAllEntities();
                        getV().Cave();
                        setImageIndex(0);
                        setImages(getV().getImageStore().getImageList("mainDown"));
                        return true;
                    }
                    if (o.getId().equals(TOWN_TO_COMBAT)) {
                        if (worldType.equals("Town") && checkExit(newPos, direction)) {

                            for (Entity e: getV().getWorld().getEntities()) {
                                if (!(e instanceof MainCharacter))
                                    getV().getScheduler().unscheduleAllEvents(e);
                            }
                            getV().getWorld().removeAllEntities();
                            Point oldPos = getPosition();

                            setPosition(new Point(3, 2));
                            getV().Combat();

                            Point p = new Point(oldPos.x, oldPos.y - 23);
                            setPosition(p);
                            world.setOccupancyCell(oldPos, null);  // so the world knows that the main isn't here anymore
                        }
                        return false;
                    }
                    if (o.getId().equals(INIT_EXIT)) {

                        for (Entity e: getV().getWorld().getEntities()) {
                            if (!(e instanceof MainCharacter))
                                getV().getScheduler().unscheduleAllEvents(e);
                        }
                        getV().getWorld().removeAllEntities();
                        Point oldPos = this.getPosition();

                        setPosition(new Point(3, 2));
                        getV().Town();

                        Point p = new Point(oldPos.x - 39, oldPos.y);
                        setPosition(p);
                        world.setOccupancyCell(oldPos, null);
                        return false;
                    }
                }
                return false;
            }
        }
        return false;
    }

    public static MainCharacter getMainCharacter() {
        if (m == null)
            return m = new MainCharacter("main_1", null, null, 0, 0, 0, 0);
        return m;
    }

    public boolean checkExit(Point newPos, String direction)
    {
        if (newPos.equals(TOWN_EXIT1) || newPos.equals(TOWN_EXIT2) || newPos.equals(TOWN_EXIT3) ||
                newPos.equals(TOWN_EXIT4) || newPos.equals(TOWN_EXIT5) || newPos.equals(TOWN_EXIT6) || newPos.equals(TOWN_EXIT7)) {
            if (direction.equals("down"))
                return true;
        }
        if (getPosition().equals(COMBAT_EXIT1) || getPosition().equals(COMBAT_EXIT2) || getPosition().equals(COMBAT_EXIT3) ||
                getPosition().equals(COMBAT_EXIT4) || getPosition().equals(COMBAT_EXIT5) || getPosition().equals(COMBAT_EXIT6)|| getPosition().equals(COMBAT_EXIT7)) {
            if (direction.equals("up"))
                return true;
        }
        return false;
    }

    public void die()
    {
        getV().getWorld().removeAllEntities();
        getV().getWorld().removeEntity( this);
        getV().getScheduler().unscheduleAllEvents( this);
        getV().mainDied();
    }

    public void Attack(String direction)
    {
        String key;

        if(direction.equals("up"))
            key = "LaserBlueUp";
        else if(direction.equals("left"))
            key = "LaserBlueLeft";
        else if(direction.equals("right")) {
            key = "LaserBlueRight";
            direction = "right";
        }
        else {
            key = "LaserBlueDown";
            direction = "down";
        }


        Point nextPos = getNextPoint(direction, getPosition());
        if(!getV().getWorld().isOccupied(nextPos)) {
            Laser l = new Laser("laser", nextPos, getV().getImageStore().getImageList(key), 0, 4000, WeaponRange, direction);
            getV().getWorld().addEntity(l);
//        v1.getWorld().getEntities().add(l);  // don't set occupancy cell with world.addEntity so that the bosses don't path around it
            l.scheduleActions(getV().getScheduler(), getV().getWorld(), getV().getImageStore());
        }
    }

    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) { }

}
