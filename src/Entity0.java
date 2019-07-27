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

        // Initialize the table
        initTable();

        // Notify all of the directly connected neighbors
        notifyNeighbors();

    }

    // Init the table
    private void initTable()
    {
        // Initialize everything to INFINITY
        for(int dest = 0; dest < NetworkSimulator.NUMENTITIES; dest++)
            for(int via = 0; via < NetworkSimulator.NUMENTITIES; via++)
                distanceTable[dest][via] = INFINITY;

        // Initialize table
        distanceTable[entityNum][entityNum] = 0;
        distanceTable[1][1] = 1;
        distanceTable[2][2] = 3;
        distanceTable[3][3] = 7;


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

        // Get the cost to the neighboring node
        int costToSource = distanceTable[p.getSource()][p.getSource()];

        // Update the current cost to the destination node
        for(int dest = 0; dest < NetworkSimulator.NUMENTITIES; dest++){

            if(dest == entityNum) continue;

            int prevMin = getDestMinCost(dest);

            // Update the table
            //System.out.println("distanceTable["+dest+"]["+p.getSource()+"] = "+costToSource+" + "+p.getMincost(dest));
            distanceTable[dest][p.getSource()] = Math.min(INFINITY,costToSource + p.getMincost(dest));

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

        boolean doNotify = false;

        // Get old cost
        int oldCost = distanceTable[whichLink][whichLink];

        // Update the current cost to the destination node
        for(int dest = 0; dest < NetworkSimulator.NUMENTITIES; dest++){
            if(dest == entityNum) continue;

            int prevMin = getDestMinCost(dest);

            // Assign here so we can get the prev min for the changed link first
            distanceTable[whichLink][whichLink] = newCost;

            // Skip over adding the distance if it is the changed link
            if(dest != whichLink){
                distanceTable[dest][whichLink] = Math.min(INFINITY,newCost + (distanceTable[dest][whichLink] - oldCost));
            }


            if(prevMin != getDestMinCost(dest))
                doNotify = true;

        }

        printDT();

        if(doNotify)
        {
            NetworkSimulator.printDebug("Entity"+entityNum+".linkCostChangeHandler() -> Distance vector has changed");
            notifyNeighbors();
        }


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

            if(viaCost < minCost)
                minCost = viaCost;

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
