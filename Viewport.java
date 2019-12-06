/*
Viewport ideally helps control what part of the world we are looking at for drawing only what we see
Includes helpful helper functions to map between the viewport and the real world
 */



final class Viewport
{
   private int row;
   private int col;
   private int numRows;
   private int numCols;

   public Viewport(int numRows, int numCols)
   {
      this.numRows = numRows;
      this.numCols = numCols;
   }

   public void shift(int col, int row)
   {
      this.col = col;
      this.row = row;
   }

   public Point viewportToWorld(int col, int row)
   {
      return new Point(col + this.col, row + this.row);
   }

   public int clamp(int value, int low, int high)
   {
      return Math.min(high, Math.max(value, low));
   }

   public boolean contains(Point p)
   {
      return p.y >= row && p.y < row + numRows &&
              p.x >= col && p.x < col + numCols;
   }

   public int getRow() {return this.row;}
   public int getCol() {return this.col;}
   public int getnumRows() {return this.numRows;}
   public int getnumCols() {return this.numCols;}

//   public void setRow() {}
//   public void setCol() {}
//   public void setnumRows() {}
//   public void setnumCols() {}

}
