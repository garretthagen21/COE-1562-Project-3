import java.util.*;

public abstract class Entity
{
    // Convenience variable
    protected final static int INFINITY = 999;
    protected final static int UNITIALIZED = 0;

    // Each entity will have a distance table
    protected int[][] distanceTable = new int[NetworkSimulator.NUMENTITIES]
                                           [NetworkSimulator.NUMENTITIES];

    
    // The update function.  Will have to be written in subclasses by you
    public abstract void update(Packet p);
    
    // The link cost change handler.  Will have to be written in appropriate
    // subclasses by you.  Note that only Entity0 and Entity1 will need
    // this
    public abstract void linkCostChangeHandler(int whichLink, int newCost);

    // Print the distance table of the current entity.
    protected abstract void printDT();

}
