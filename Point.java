final class Point implements Comparable
{
   public final int x;
   public final int y;
   public int g;
   public int f;
   public int h;
   public Point prev;


   public Point(int x, int y) {
      this.x = x;
      this.y = y;
      this.g = 0;
      this.f = 0;
      this.h = -1;
      this.prev = null;
   }

   public void setG(int g) {
      this.g = g;
   }
   public void setH(int h) {
      this.h = h;
   }
   public void setF(int f) {
      this.f = f;
   }
   public int getF() {
      return this.f;
   }
   public void setPrev(Point newPrev) {
      this.prev = newPrev;
   }

   public String toString()
   {
      return "(" + x + "," + y + ")";
   }

   public boolean equals(Object other)
   {
      return other instanceof Point &&
         ((Point)other).x == this.x &&
         ((Point)other).y == this.y;
   }

   public int hashCode()
   {
      int result = 17;
      result = result * 31 + x;
      result = result * 31 + y;
      return result;
   }

   public static boolean adjacent(Point p1, Point p2)
   {
      return (p1.x == p2.x && Math.abs(p1.y - p2.y) == 1) ||
              (p1.y == p2.y && Math.abs(p1.x - p2.x) == 1);
   }

   public int compareTo(Object o) {
      Point p = (Point)o;
      return this.getF() - p.getF();
   }
}
