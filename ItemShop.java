import com.sun.tools.javac.Main;
import processing.core.PImage;
import java.util.List;

public class ItemShop extends InteractiveEntity {

    private int Currency;
    private int HeartLevelCost;
    private int AddHeartCost;
    private int WeaponRangeCost;
    private int WeaponRangeMax;
    private int BabyYodaKill;
    private int TerminatorKill;
    private int TerminatorBrainKill;
    private int SanHoloKill;
    private MainCharacter m = MainCharacter.getMainCharacter();
    private HeartFactory h = m.getH();

    private static ItemShop i;

    private ItemShop(String id, Point position, List<PImage> images) {
        super(id, position, images, 0);
        this.Currency = 0;
        this.HeartLevelCost = 5000;
        this.AddHeartCost = 1200;
        this.WeaponRangeCost = 500;
        this.WeaponRangeMax = 20;
        this.BabyYodaKill = 200;
        this.TerminatorKill = 100;
        this.SanHoloKill = 500;
        this.TerminatorBrainKill = 1500;
    }

    public static ItemShop getItemShop() {
        if (i == null)
            return i = new ItemShop("itemShop", null, null);
        return i;
    }

    public int getHeartLevelCost() {return this.HeartLevelCost;}
    public int getAddHeartCost() {return this.AddHeartCost;}
    public int getWeaponRangeCost() {return this.WeaponRangeCost;}
    public int getCurrency() {return Currency;}
    public void incrementCurrency(int s) {this.Currency += s;}

    public void BabyYodaKill(){this.Currency += BabyYodaKill;}
    public void TerminatorKill(){this.Currency += TerminatorKill;}
    public void TerminatorBrainKill(){this.Currency += TerminatorBrainKill;}
    public void SanHoloKill(){this.Currency += SanHoloKill;}

    public void NewSoloMoney(int LEVEL) {this.SanHoloKill = this.SanHoloKill*LEVEL;}
    public void NewBabyYodaMoney(int LEVEL) {this.BabyYodaKill = this.BabyYodaKill*LEVEL;}
    public void TerminatorMoney(int LEVEL) {this.TerminatorKill = this.TerminatorKill*LEVEL;}
    public void TerminatorBrainMoney(int LEVEL) {this.TerminatorBrainKill = this.TerminatorBrainKill*LEVEL;}



    public void Interact()
    {
        CalcHeartCosts();
        getV().Store();
    }

    public boolean makePurchase(int cost, String type)
    {
        if ((Currency - cost) > 0)
        {
            Currency -= cost;

            if(type.equals("AddHeart"))
                h.addHeart();
            else if (type.equals("HeartLevel"))
                h.increaseLevel();

            CalcHeartCosts();
            return true;
        }
        return false;
    }


    public void CalcHeartCosts()
    {
        int lvl = h.getHeart_level();
        int num = h.getNum_hearts();
        this.HeartLevelCost = 1400 * lvl * (num-2);
        this.AddHeartCost = 1200 * lvl * (num-2);
        if(this.HeartLevelCost <= 0)
            this.HeartLevelCost = 500;

        if(this.AddHeartCost <= 0)
            this.AddHeartCost = 500;
    }

    public void WeaponRangePurchase() {
        m.incrementWeaponRange();
        this.WeaponRangeCost += 500;
        this.Currency -= 500;
    }
    public boolean HeartLevelAvailable()
    {
        if (h.getHeart_level() == h.getMax_heart_level() || Currency < HeartLevelCost)
            return false;
        return true;
    }
    public boolean AddHeartAvailable()
    {
        if (h.getNum_hearts() == h.getMax_heart_num() || Currency < AddHeartCost)
            return false;

        return true;
    }
    public boolean WeaponRangeAvailable()
    {
        if (m.getWeaponRange() == WeaponRangeMax || Currency < WeaponRangeCost)
            return false;
        return true;
    }

}
