import com.sun.tools.javac.Main;

public abstract class BossFactory {

    protected VirtualWorld getV() {return VirtualWorld.getVirtualWorld();}

    abstract void create(int LEVEL);
}
