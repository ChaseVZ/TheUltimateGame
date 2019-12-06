import processing.core.PApplet;
import processing.core.PImage;

import java.util.Optional;

/*
WorldView ideally mostly controls drawing the current part of the whole world
that we can see based on the viewport
*/

//Class.method - static methods
//Object.method - non-static methods

final class WorldView
{
   private PApplet screen;
   private WorldModel world;
   private int tileWidth;
   private int tileHeight;
   private Viewport viewport;

   public WorldView(int numRows, int numCols, PApplet screen, WorldModel world,
      int tileWidth, int tileHeight)
   {
      this.screen = screen;
      this.world = world;
      this.tileWidth = tileWidth;
      this.tileHeight = tileHeight;
      this.viewport = new Viewport(numRows, numCols);
   }

   public void drawViewport()
   {
      drawBackground();
      drawEntities();
   }

   public Point worldToViewport(Viewport viewport, int col, int row)
   {
      return new Point(col - viewport.getCol(), row - viewport.getRow());
   }

   public void drawEntities()
   {
      for (Entity entity : world.getEntities())
      {
         Point pos = entity.getPosition();

         if (viewport.contains(pos))
         {
            Point viewPoint = worldToViewport(viewport, pos.x, pos.y);
            screen.image(entity.getCurrentImage(),
                    viewPoint.x * tileWidth, viewPoint.y * tileHeight);
         }
      }
   }

   public void drawBackground()
   {
      for (int row = 0; row < this.viewport.getnumRows(); row++)
      {
         for (int col = 0; col < this.viewport.getnumCols(); col++)
         {
            Point worldPoint = viewport.viewportToWorld(col, row);
            Optional<PImage> image = world.getBackgroundImage(worldPoint);
            if (image.isPresent())
            {
               this.screen.image(image.get(), col * this.tileWidth, row * this.tileHeight);
            }
         }
      }
   }


   public void shiftView(int colDelta, int rowDelta)   // can probably update this method
   {
      int newCol = viewport.clamp(viewport.getCol() + colDelta, 0,
              world.getnumCol() - viewport.getnumCols());
      int newRow = viewport.clamp(viewport.getRow() + rowDelta, 0,
              world.getnumRow() - viewport.getnumRows());

      viewport.shift(newCol, newRow);
   }
}
