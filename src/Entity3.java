public class Entity3 extends Entity
{
    // For convenience
    private final int entityNum = 3;

    // Perform any necessary initialization in the constructor
    public Entity3()
    {
        // Print that we are initalizing the node
        NetworkSimulator.printDebug("Entity"+entityNum+"() -> Initializing link costs for neighbors 0,2");

        // Init the table
        initTable();

        // Notify all of the directly connected neighbors
        notifyNeighbors();

    }

    private void initTable()
    {

        // Initialize everything to INFINITY
        for(int dest = 0; dest < NetworkSimulator.NUMENTITIES; dest++)
            for(int via = 0; via < NetworkSimulator.NUMENTITIES; via++)
                distanceTable[dest][via] = INFINITY;


        // Initialize our neighboring links
        distanceTable[0][0] = 7;
        distanceTable[1][1] = INFINITY;
        distanceTable[2][2] = 2;
        distanceTable[entityNum][entityNum] = 0;

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
        int costToSource = distanceTable[p.getSource()][p.getSource()]; // 1


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
            NetworkSimulator.printDebug("Entity"+entityNum+".update() -> Distance vector has changed");
            notifyNeighbors();
        }


    }

    public void linkCostChangeHandler(int whichLink, int newCost)
    {
       // Not in use
    }

    private void notifyNeighbors()
    {

        // Create and fill the distance vector to be sent in the neighbor packets
        int [] distanceVector = new int[NetworkSimulator.NUMENTITIES];

        for(int dest = 0; dest < NetworkSimulator.NUMENTITIES; dest++)
            distanceVector[dest] = getDestMinCost(dest);



        NetworkSimulator.printDebug("Entity"+entityNum+".notifyNeighbors() -> Sending update packets to neighbors 1,2,3");

        // Notify our directly connected neighbors
        NetworkSimulator.toLayer2(new Packet(entityNum,0,distanceVector));
        NetworkSimulator.toLayer2(new Packet(entityNum,2,distanceVector));



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
        System.out.println("         via");
        System.out.println(" D3 |   0   2");
        System.out.println("----+--------");
        for (int i = 0; i < NetworkSimulator.NUMENTITIES; i++)
        {
            if (i == 3)
            {
                continue;
            }
            
            System.out.print("   " + i + "|");
            for (int j = 0; j < NetworkSimulator.NUMENTITIES; j += 2)
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
