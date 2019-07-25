import sun.nio.ch.Net;

public class Entity0 extends Entity
{    
    // For convenience
    private final int entityNum = 0;

    // Perform any necessary initialization in the constructor
    public Entity0()
    {
        // Print that we are initalizing the node
        NetworkSimulator.printDebug("Entity"+entityNum+"() -> Initializing link costs for neighbors 1,2,3");

        // Start everything to infinity
        /*for(int i = 0; i < NetworkSimulator.NUMENTITIES; i++)
            for(int j = 0; j < NetworkSimulator.NUMENTITIES; j++)
                distanceTable[i][j] = UNITIALIZED;*/

        // Initialize table
        distanceTable[entityNum][entityNum] = 0;
        distanceTable[1][1] = 1;
        distanceTable[2][2] = 3;
        distanceTable[3][3] = 7;

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
            NetworkSimulator.printDebug("Entity"+entityNum+".update() -> Error: Destination of packet is "+p.getDest());
            return;
        }

        // Print that we have recieved a packet
        NetworkSimulator.printDebug("Entity"+entityNum+".update() -> Recieved Packet: "+p.toString());

        boolean doNotify = false;

        // Update the current cost to the destination node
        for(int dest = 0; dest < NetworkSimulator.NUMENTITIES; dest++){
           if(dest == entityNum) continue;

           int prevMin = getDestMinCost(dest);

           int costToRecieved = distanceTable[dest][dest];
           //int costToNeighbor = (distanceTable[via][via] == UNITIALIZED) ? 0 : distanceTable[via][via];

           distanceTable[dest][p.getSource()] = Math.min(INFINITY,costToRecieved + p.getMincost(dest));

           if(prevMin != getDestMinCost(dest))
               doNotify = true;

       }

        // Print the current table
        printDT();

        // If the minium distance has changed notify neighbors
        if(doNotify)
        {
            NetworkSimulator.printDebug("Entity"+entityNum+".update() -> Distance table has changed");
            notifyNeighbors();
        }


    }
    
    public void linkCostChangeHandler(int whichLink, int newCost)
    {
        NetworkSimulator.printDebug("Entity"+entityNum+".linkCostChangeHandler() -> Link "+whichLink+" cost has changed to "+newCost);

        // Record previous minimum distance
        int prevMinDist = getDestMinCost(whichLink);

        // Update the table
        distanceTable[whichLink][whichLink] = newCost;

        // If the minium distance has changed notify neighbors
        if(getDestMinCost(whichLink) != prevMinDist)
            notifyNeighbors();
    }

    private void notifyNeighbors()
    {

        // Create and fill the distance vector to be sent in the neighbor packets
        int [] distanceVector = new int[NetworkSimulator.NUMENTITIES];

        for(int dest = 0; dest < NetworkSimulator.NUMENTITIES; dest++)
            distanceVector[dest] = getDestMinCost(dest);



        NetworkSimulator.printDebug("Entity"+entityNum+".notifyNeighbors() -> Sending update packets to neighbors 1,2,3");

        // Notify our directly connected neighbors
        NetworkSimulator.toLayer2(new Packet(entityNum,1,distanceVector));
        NetworkSimulator.toLayer2(new Packet(entityNum,2,distanceVector));
        NetworkSimulator.toLayer2(new Packet(entityNum,3,distanceVector));


    }

    /* Convenience function to get the minimum cost to a destiation via all of the nodes in the row */
    private int getDestMinCost(int dest)
    {
        if(dest == entityNum)
            return 0;


        int minCost = distanceTable[dest][dest];
        for(int via = 0; via < NetworkSimulator.NUMENTITIES; via++)
        {
            int viaCost = distanceTable[dest][via];

            if(viaCost > 0 && viaCost < minCost)
                minCost = distanceTable[dest][via];

        }

        return minCost;

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
