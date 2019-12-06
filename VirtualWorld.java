import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.MalformedInputException;
import java.util.Scanner;

import processing.core.*;

/*
VirtualWorld is our main wrapper
It keeps track of data necessary to use Processing for drawing but also keeps track of the necessary
components to make our world run (eventScheduler), the data in our world (WorldModel) and our
current view (think virtual camera) into that world (WorldView)
 */

public final class VirtualWorld
   extends PApplet
{
   private static final int TIMER_ACTION_PERIOD = 100;
   private static final String QUAKE_KEY = "quake";

   private static final int VIEW_WIDTH = 1280;
   private static final int VIEW_HEIGHT = 768;
   private static final int TILE_WIDTH = 32;
   private static final int TILE_HEIGHT = 32;
   private static final int WORLD_WIDTH_SCALE = 2;
   private static final int WORLD_HEIGHT_SCALE = 2;

   private static final int VIEW_COLS = VIEW_WIDTH / TILE_WIDTH;
   private static final int VIEW_ROWS = VIEW_HEIGHT / TILE_HEIGHT;
   private static final int WORLD_COLS = VIEW_COLS * WORLD_WIDTH_SCALE;
   private static final int WORLD_ROWS = VIEW_ROWS * WORLD_HEIGHT_SCALE;

   private static final String IMAGE_LIST_FILE_NAME = "imagelist";
   private static final String DEFAULT_IMAGE_NAME = "background_default";
   private static final int DEFAULT_IMAGE_COLOR = 0x808080;

   private static final String LOAD_FILE_INIT = "Initial.sav";
   private static final String LOAD_FILE_CAVE = "CaveWorld.sav";
   private static final String LOAD_FILE_TOWN = "TownWorld.sav";
   private static final String LOAD_FILE_COMBAT = "CombatWorld.sav";
   private static final String LOAD_FILE_REMOVE_STORE = "RemoveStore.sav";
   private static final String END = "End.sav";
   private static final String WIN = "Win.sav";
   private static final String LOAD_FILE_STORE = "Store.sav";

   private static final String FAST_FLAG = "-fast";
   private static final String FASTER_FLAG = "-faster";
   private static final String FASTEST_FLAG = "-fastest";
   private static final double FAST_SCALE = 0.5;
   private static final double FASTER_SCALE = 0.25;
   private static final double FASTEST_SCALE = 0.10;

   private static double timeScale = 1.0;
   public String flag = "main";
   private int LEVEL = 1;

   private ImageStore imageStore;
   private WorldModel world;
   private WorldView view;
   private EventScheduler scheduler;
   private Parsing parsing;
   private MainCharacter main;
   private ItemShop itemShop;
   private boolean StoreActive;
   private boolean redraw;
   private long next_time;
   private static VirtualWorld v1;
   private static Ore ore = new Ore();
   private static Terminator terminator = new Terminator();
   private static BabyYoda babyYoda = new BabyYoda();
   private static SanHolo sanHolo = new SanHolo();
   private boolean TerminatorBrainFlag = false;
   private boolean SanHoloFlag = false;
   private boolean LEVEL_OVER = false;

   private VirtualWorld(ImageStore imageStore, WorldModel world, WorldView view, EventScheduler scheduler, Parsing parsing, MainCharacter main, ItemShop itemShop){
      this.imageStore = imageStore;
      this.world = world;
      this.view = view;
      this.scheduler = scheduler;
      this.parsing = parsing;
      this.main = main;
      this.StoreActive = false;
      this.itemShop = itemShop;
   }

   public VirtualWorld () {}
   public static VirtualWorld getVirtualWorld(){ return v1; }

   public void setLEVEL_OVER() {this.LEVEL_OVER = true;}
   public boolean getLEVEL_OVER() {return this.LEVEL_OVER;}
   public WorldView getView(){ return v1.view;}
   public WorldModel getWorld(){ return v1.world;}
   public ImageStore getImageStore(){ return v1.imageStore;}
   public EventScheduler getScheduler(){ return v1.scheduler;}
   public Parsing getParsing(){ return v1.parsing;}
   public MainCharacter getMain() {return this.main;}
   public void setStoreActive() {this.StoreActive = true;}
   public void setStoreInactive() {this.StoreActive = false;}
   public ItemShop getItemShop() {return this.itemShop;}
   public Ore getOre() {return ore;}
   public Terminator getTerminator() {return terminator;}
   public void setTerminatorBrainFlag(boolean flag) {TerminatorBrainFlag = flag;}
   public void setSanHoloFlag(boolean flag) {SanHoloFlag = flag;}
   public int getLEVEL() {return this.LEVEL;}
   public void incrementLevel(){
      this.LEVEL += 1;
      getItemShop().NewBabyYodaMoney(LEVEL);
      getItemShop().NewSoloMoney(LEVEL);
      getItemShop().TerminatorBrainMoney(LEVEL);
      getItemShop().TerminatorMoney(LEVEL);

      if(LEVEL == 3)
         win();
   }


   public void settings()
   {
      size(VIEW_WIDTH, VIEW_HEIGHT);
   }

   public void setup()
   {
      ImageStore imageStore1 = new ImageStore(createImageColored(TILE_WIDTH, TILE_HEIGHT, DEFAULT_IMAGE_COLOR));
      WorldModel world1 = new WorldModel(WORLD_ROWS, WORLD_COLS, createDefaultBackground(imageStore1));
      WorldView view1 = new WorldView(VIEW_ROWS, VIEW_COLS, this, world1, TILE_WIDTH, TILE_HEIGHT);
      EventScheduler scheduler1 = new EventScheduler(timeScale);
      Parsing parsing1 = new Parsing(world1);
      MainCharacter m1 = MainCharacter.getMainCharacter();
      ItemShop itemShop = ItemShop.getItemShop();

      v1 = new VirtualWorld(imageStore1, world1, view1, scheduler1, parsing1, m1, itemShop);

      loadImages(IMAGE_LIST_FILE_NAME, getImageStore(), this);
      loadWorld(LOAD_FILE_INIT, getImageStore(), getParsing());
      scheduleActions(getWorld(), getScheduler(), getImageStore());

      next_time = System.currentTimeMillis() + TIMER_ACTION_PERIOD;
      m1.setWorldType("Init");
      m1.LoadHearts();
   }

   public void Town()
   {
//      NumOre = ore.getCount();
      loadWorld(LOAD_FILE_TOWN, getImageStore(), getParsing());
      if(!SanHoloFlag)
         sanHolo.create(LEVEL);
      scheduleActions(getWorld(), getScheduler(), getImageStore());
      getMain().scheduleActions(getScheduler(), getWorld(), getImageStore());
      getMain().setWorldType("Town");
   }

   public void Cave()
   {
      loadWorld(LOAD_FILE_CAVE, getImageStore(), getParsing());
      ore.setCount(0);
      ore.create(LEVEL);
      babyYoda.create(LEVEL);
      scheduleActions(getWorld(), getScheduler(), getImageStore());
      getMain().scheduleActions(getScheduler(), getWorld(), getImageStore());
      getMain().setWorldType("Cave");
   }

   public void Combat()
   {
      loadWorld(LOAD_FILE_COMBAT, getImageStore(), getParsing());
      if(!TerminatorBrainFlag)
         terminator.create(LEVEL);
      scheduleActions(getWorld(), getScheduler(), getImageStore());
      getMain().scheduleActions(getScheduler(), getWorld(), getImageStore());
      getMain().setWorldType("Combat");
   }

   public void Store()
   {
      setStoreActive();
      loadWorld(LOAD_FILE_STORE, getImageStore(), getParsing());
   }

   public void ExitStore() {
      setStoreInactive();
      loadWorld(LOAD_FILE_REMOVE_STORE, getImageStore(), getParsing());
   }

   public void mainDied()
   {
      loadWorld(END, getImageStore(), getParsing());
   }

   public void win()
   {
      setLEVEL_OVER();
      world.removeAllEntities();
      for (Entity e : world.getEntities())
         scheduler.unscheduleAllEvents(e);
      loadWorld(WIN, getImageStore(), getParsing());
   }

   public void mousePressed()
   {
      int x = Math.round(mouseX/TILE_WIDTH);
      int y = Math.round(mouseY/TILE_HEIGHT);
      int THRESHOLD = 2;

      Point p = new Point(x, y);
      if (getWorld().isOccupied(p) && getWorld().getOccupancyCell(p) instanceof InteractiveEntity)
      {
         if (getWorld().withinReach(MainCharacter.getMainCharacter().getPosition(), p, THRESHOLD)) { //if the character is next to the anvil
            if (getWorld().getOccupancyCell(p) instanceof ItemShop) {
               ((InteractiveEntity) getWorld().getOccupancyCell(p)).Interact();
               setStoreActive();
            }
            else
               ((InteractiveEntity) getWorld().getOccupancyCell(p)).Interact();
         }
      }
   }


   public void draw()
   {
      if(redraw)
      {
         textSize(32);
         fill(255, 242, 0);
         text("$" + ItemShop.getItemShop().getCurrency(), 0, 32);
         redraw = false;
      }
      if(!StoreActive) {
         long time = System.currentTimeMillis();
         if (time >= next_time) {
            getScheduler().updateOnTime(time);
            next_time = time + TIMER_ACTION_PERIOD;
         }

         getView().drawViewport();

         textSize(38);
         text(" ", 1088, 96);

         textSize(32);
         fill(255, 242, 0);
         text("$" + ItemShop.getItemShop().getCurrency(), 0, 32);
      }

      if(StoreActive)
      {
         MainCharacter m = MainCharacter.getMainCharacter();
         long time = System.currentTimeMillis();
         if (time >= next_time) {
            getScheduler().updateOnTime(time);
            next_time = time + TIMER_ACTION_PERIOD;
         }
         getView().drawViewport();

         String AddHeartCost = Integer.toString(ItemShop.getItemShop().getAddHeartCost());
         String IncreaseHeartLvlCost = Integer.toString(ItemShop.getItemShop().getHeartLevelCost());
         String WeaponRangeCost = Integer.toString(ItemShop.getItemShop().getWeaponRangeCost());
         String Range = Integer.toString(m.getWeaponRange());

         textSize(36);
         fill(0, 162, 232);
         text("Store", 1125, 93);

         textSize(32);
         fill(0, 0, 0);
         text("Extra Heart (Q)", 1064, 128);
         text("Heart Lvl (F)", 1064, 192);
         text("Range (R): " + Range, 1064, 256);

         textSize(28);
         fill(255, 242, 0);
         text("$" + AddHeartCost, 1070, 160);
         text("$" + IncreaseHeartLvlCost, 1070, 224);
         text("$" + WeaponRangeCost, 1070, 288);

         textSize(32);
         text("$" + ItemShop.getItemShop().getCurrency(), 0, 32);
      }
   }

   public void keyPressed()
   {
         MainCharacter m = MainCharacter.getMainCharacter();
         WorldModel w;
         ImageStore i;

         w = getWorld();
         i = getImageStore();
         boolean directionChange = false;

         if(!StoreActive) {

            Entity b = null;
            BasicOre ore;

            if(key == ' ') {
               m.Attack(flag);
               redraw = true;
            }


            if(key == 'b') {
               b = getWorld().OreWithinReach(m.getPosition(), flag);
               if (b != null) {
                  ore = (BasicOre) b;
                  ore.Interact();
                  if (ore.destroyed()) {
                     Point p = ore.getPosition();
                     getWorld().removeEntityAt(ore.getPosition());
                     Quake quake = new Quake(QUAKE_KEY, p, getImageStore().getImageList(QUAKE_KEY), 6000, 1000);
                     getWorld().addEntity(quake);
                     quake.scheduleActions(getScheduler(), getWorld(), getImageStore());
                  }
               }
            }



            if (key == 'w' || key == 'W') {
               if (flag.equals("up"))
                  directionChange = m.move(w, 0, -1, flag);
               else
                  flag = "up";

               if (!directionChange) {
                  m.setImageIndex(0);
                  m.setImages(i.getImageList("mainUpward"));
               }
            }
            if (key == 's' || key == 'S') {
               if (flag.equals("down"))
                  m.move(w, 0, 1, flag);
               else
                  flag = "down";
               m.setImageIndex(0);
               m.setImages(i.getImageList("mainDown"));
            }

            if (key == 'a' || key == 'A') {
               if (flag.equals("left"))
                  m.move(w, -1, 0, flag);
               else
                  flag = "left";
               m.setImageIndex(0);
               m.setImages(i.getImageList("mainLeft"));
            }

            if (key == 'd' || key == 'D') {
               if (flag.equals("right"))
                  m.move(w, 1, 0, flag);
               else
                  flag = "right";
               m.setImageIndex(0);
               m.setImages(i.getImageList("main"));
            }


         }
         if(StoreActive) {
            if (key == 'f' && ItemShop.getItemShop().HeartLevelAvailable()) {
               ItemShop.getItemShop().makePurchase(ItemShop.getItemShop().getHeartLevelCost(), "HeartLevel");
            }
            if (key == 'q' && ItemShop.getItemShop().AddHeartAvailable()) {
               ItemShop.getItemShop().makePurchase(ItemShop.getItemShop().getAddHeartCost(), "AddHeart");
            }
            if (key == 'r' && ItemShop.getItemShop().WeaponRangeAvailable()) {
               ItemShop.getItemShop().WeaponRangePurchase();
            }
            if (key == 'e') {
               ExitStore();
            }
         }


   }


   public static Background createDefaultBackground(ImageStore imageStore)
   {
      return new Background(DEFAULT_IMAGE_NAME,
         imageStore.getImageList(DEFAULT_IMAGE_NAME));
   }

   public static PImage createImageColored(int width, int height, int color)
   {
      PImage img = new PImage(width, height, RGB);
      img.loadPixels();
      for (int i = 0; i < img.pixels.length; i++)
      {
         img.pixels[i] = color;
      }
      img.updatePixels();
      return img;
   }

   private void loadImages(String filename, ImageStore imageStore,
      PApplet screen)
   {
      try
      {
         Scanner in = new Scanner(new File(filename));
         imageStore.loadImages(in, screen);
      }
      catch (FileNotFoundException e)
      {
         System.err.println(e.getMessage());
      }
   }

   public static void loadWorld(String filename, ImageStore imageStore, Parsing parsing)
   {
      try
      {
         Scanner in = new Scanner(new File(filename));
         parsing.load(in, imageStore);
      }
      catch (FileNotFoundException e)
      {
         System.err.println(e.getMessage());
      }
   }

   public static void scheduleActions(WorldModel world,
      EventScheduler scheduler, ImageStore imageStore)
   {
      for (Entity entity : world.getEntities()) {
         //Only start actions for entities that include action (not those with just animations)
            if (entity instanceof ActiveEntity)
               ((ActiveEntity) entity).scheduleActions(scheduler, world, imageStore);
      }
   }

   public static void parseCommandLine(String [] args)
   {
      for (String arg : args)
      {
         switch (arg)
         {
            case FAST_FLAG:
               timeScale = Math.min(FAST_SCALE, timeScale);
               break;
            case FASTER_FLAG:
               timeScale = Math.min(FASTER_SCALE, timeScale);
               break;
            case FASTEST_FLAG:
               timeScale = Math.min(FASTEST_SCALE, timeScale);
               break;
         }
      }
   }

   public static void main(String [] args)
   {
      parseCommandLine(args);
      PApplet.main(VirtualWorld.class);
   }
}
