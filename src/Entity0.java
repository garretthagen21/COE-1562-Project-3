import sun.nio.ch.Net;

public class Entity0 extends Entity
{    
    // For convenience
    private final int entityNum = 0;

    // Perform any necessary initialization in the constructor
    public Entity0()
    {
        // Print that we are initalizing the node
        NetworkSimulator.printDebug("Entity0() -> Initializing link costs for neighbors 1,2,3");

        // Initialize our neighboring links
        distanceTable[entityNum][0] = 0;
        distanceTable[entityNum][1] = 1;
        distanceTable[entityNum][2] = 3;
        distanceTable[entityNum][3] = 7;

        // Notify all of the directly connected neighbors
        notifyNeighbors();

    }
    
    // Handle updates when a packet is received.  You will need to call
    // NetworkSimulator.toLayer2() with new packets based upon what you
    // send to update.  Be careful to construct the source and destination of
    // the packet correctly.  Read the warning in NetworkSimulator.java for more
    // details.
    public void update(Packet p)
    {        
        // If we were not meant to recieve this packet something has gone wrong
        if(p.getDest() != entityNum){
            NetworkSimulator.printDebug("Entity0.update() -> Error: Destination of packet is "+p.getDest());
            return;
        }

        // Print that we have recieved a packet
        NetworkSimulator.printDebug("Entity0.update() -> Recieved Packet: "+p.toString());

        boolean didChange = false;
        for(int i = 0; i < NetworkSimulator.NUMENTITIES; i++)
        {
            int nodeCost = p.getMincost(entityNum) + p.getMincost(i);
            if(nodeCost < distanceTable[entityNum][i]){
                distanceTable[entityNum][i] = nodeCost;
                didChange = true;
            }
        }

        // Print the current table
        printDT();

        if(didChange)
        {
            NetworkSimulator.printDebug("Entity0.update() -> Distance table has changed");
            notifyNeighbors();
        }



    }
    
    public void linkCostChangeHandler(int whichLink, int newCost)
    {
        NetworkSimulator.printDebug("Entity0.linkCostChangeHandler() -> Link "+whichLink+" cost has changed to "+newCost);
    }

    private void notifyNeighbors()
    {

        // Create and fill the distance vector to be sent in the neighbor packets
        int [] distanceVector = new int[NetworkSimulator.NUMENTITIES];

        for(int i = 0; i < NetworkSimulator.NUMENTITIES; i++)
        {
            distanceVector[i] = distanceTable[entityNum][i];
        }

        NetworkSimulator.printDebug("Entity0.notifyNeighbors() -> Sending update packets to neighbors 1,2,3");

        // Notify our directly connected neighbors
        NetworkSimulator.toLayer2(new Packet(entityNum,1,distanceVector));
        NetworkSimulator.toLayer2(new Packet(entityNum,2,distanceVector));
        NetworkSimulator.toLayer2(new Packet(entityNum,3,distanceVector));


    }
    
    public void printDT()
    {
        System.out.println();
        System.out.println("           via");
        System.out.println(" D0 |   1   2   3");
        System.out.println("----+------------");
        for (int i = 1; i < NetworkSimulator.NUMENTITIES; i++)
        {
            System.out.print("   " + i + "|");
            for (int j = 1; j < NetworkSimulator.NUMENTITIES; j++)
            {
                if (distanceTable[i][j] < 10)
                {    
                    System.out.print("   ");
                }
                else if (distanceTable[i][j] < 100)
                {
                    System.out.print("  ");
                }
                else 
                {
                    System.out.print(" ");
                }
                
                System.out.print(distanceTable[i][j]);
            }
            System.out.println();
        }
    }
}
