import processing.core.PImage;

import java.util.List;
import java.util.Random;

public class BasicOre extends InteractiveEntity {

    private int health;
    private int CashValue;

    public BasicOre(String id, Point position, List<PImage> images, int imageIndex, int CashValue) {
        super(id, position, images, imageIndex);
        this.health = 4;
        this.CashValue = CashValue;
    }

    public boolean destroyed()
    {
        if(health != 0)
            return false;
        return true;
    }

    public void takeHit() {this.health -=1;}

   public void Interact()
   {
        takeHit();
        ItemShop.getItemShop().incrementCurrency(CashValue);
   }
}