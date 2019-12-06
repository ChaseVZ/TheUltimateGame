import java.util.LinkedList;

public class HeartFactory {  //turn into an abstract maybe?? but idk why

    private int num_hearts;
    private int heart_level;
    private int lastHeartLevel;
    private int health;
    private int max_heart_num;
    private int max_heart_level;
    private LinkedList<Point> InitialPositions = new LinkedList<Point>();
    private LinkedList<Heart> hearts = new LinkedList<Heart>();
    private LinkedList<Obstacle> barriers = new LinkedList<Obstacle>();

    public HeartFactory()
    {
        this.num_hearts = 3;
        this.heart_level = 1;
        this.lastHeartLevel = 1;
        this.health = 3;         //num hearts times heart level minus damage
        this.InitialPositions.add(new Point(38, 0));
        this.InitialPositions.add(new Point(36, 0));
        this.InitialPositions.add(new Point(34, 0));
        this.max_heart_num = 10;
        this.max_heart_level = 3;
    }

    public int getMax_heart_num() {return this.max_heart_num;}
    public int getMax_heart_level() {return this.max_heart_level;}
    public int getNum_hearts() {return this.num_hearts;}
    public int getHeart_level() {return this.heart_level;}
    public void setLastHeartLevel(int l) {this.lastHeartLevel = l;}
    public int getLastHeartLevel() {return this.lastHeartLevel;}
    public int getHealth() {return this.health;}
    public void addEntityToWorld(Entity e) {getV().getWorld().addEntity(e);}
    protected VirtualWorld getV() {return VirtualWorld.getVirtualWorld();}


    public String getHeartType()
    {
        String key = "heart";
        if (heart_level == 2)
            key = "heartSilver";
        else if (heart_level == 3)
            key = "heartGold";
        return key;
    }


    public void loadHearts()
    {
        for (Point p : InitialPositions) {
            Heart h = new Heart("heart", p, getV().getImageStore().getImageList(getHeartType()), 1);
            Obstacle o = new Obstacle("barrier", new Point(p.x+1, p.y), getV().getImageStore().getImageList("barrier"));
            addEntityToWorld(h);
            addEntityToWorld(o);
            this.hearts.add(h);
            this.barriers.add(o);
        }
        setLastHeartLevel(1);
    }

    public boolean addHeart()  //returns T/F if a heart was added
    {
        if (num_hearts != max_heart_num) {
            Heart h = hearts.get(hearts.size() - 1);
            Obstacle o = barriers.get(hearts.size() - 1);

            if(getLastHeartLevel() != heart_level) //if the last heart is damaged
            {
                Point PosForNewHeart = h.getPosition();

                h.setPosition(new Point(h.getPosition().x - 2, h.getPosition().y)); // move heart to the left
                o.setPosition(new Point(o.getPosition().x - 2, o.getPosition().y));  //and its barrier

                Heart h2 = new Heart("heart", PosForNewHeart, getV().getImageStore().getImageList(getHeartType()), heart_level);
                Obstacle o2 = new Obstacle("barrier", new Point(PosForNewHeart.x+1, 0), getV().getImageStore().getImageList("barrier"));

                addEntityToWorld(h2);
                addEntityToWorld(o2);

                hearts.removeLast(); //freshen up order
                barriers.removeLast();

                hearts.add(h2);
                hearts.add(h);

                barriers.add(o2);
                barriers.add(o);

                this.num_hearts += 1;

            }


            else
            {
                Point p = h.getPosition();
                Heart h2 = new Heart("heart", new Point(p.x - 2, p.y), getV().getImageStore().getImageList(getHeartType()), heart_level);
                Obstacle o2 = new Obstacle("barrier", new Point(p.x - 1, p.y), getV().getImageStore().getImageList("barrier"));

                addEntityToWorld(h2);
                addEntityToWorld(o2);

                hearts.add(h2);
                barriers.add(o2);

                this.num_hearts += 1;
            }

            health += heart_level;

            return true;
        }
        return false;
    }

    public boolean increaseLevel()
    {
        if (heart_level != max_heart_level){
            heart_level += 1;

            for (Heart h : hearts)
            {
                h.setImages(getV().getImageStore().getImageList(getHeartType()));  //increasing level gives you full HP
                setLastHeartLevel(heart_level);
            }

            health = (hearts.size()*heart_level);
            return true;
        }
        return false;
    }

    public boolean takeDamage()  //returns T/F if dead after damage is taken
    {
        if(!(getV().getLEVEL_OVER())) {
            if (health == 1) {
                health -= 1;
                return true; //means dead
                
            } else {
                Heart h = hearts.get(hearts.size() - 1);
                if (getLastHeartLevel() == 3) {
                    h.setImages(getV().getImageStore().getImageList("heartSilver"));
                    setLastHeartLevel(2);
                } else if (getLastHeartLevel() == 2) {
                    h.setImages(getV().getImageStore().getImageList("heart"));
                    setLastHeartLevel(1);
                } else if (getLastHeartLevel() == 1) {
                    h.setPosition(new Point(40, 0));
                    getV().getWorld().removeEntityAt(h.getPosition());
                    getV().getWorld().removeEntityAt(new Point(h.getPosition().x + 1, h.getPosition().y));  //also its barrier

                    num_hearts -= 1;
                    hearts.removeLast();
                    barriers.removeLast();
                    setLastHeartLevel(heart_level);
                }
            }

            health -= 1;
            return false;
        }
        return false;
    }


}
