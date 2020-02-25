import java.io.FileNotFoundException;
import java.io.IOException;

//Main Entry Point of the Program
//Arguments to the program are topologyFile connectionsFile routingTableFile forwardingTableFile pathsFile metric approach
public class RoutingLD {

    public static void main(String[] args) throws FileNotFoundException {
        //Parse command line arguments
        String topologyFile = args[0];
        String connectionsFile = args[1];
        String routingTableFile = args[2];
        String forwardingTableFile = args[3];
        String pathsFile = args[4];
        String metric = args[5];
        String approach = args[6];

        //Parse the topology file to create the underlying graph
        TopologyParser topologyParser = new TopologyParser(topologyFile);
        topologyParser.parseFile();

        //Create the link-disjoint shortest paths
        Route route = new Route(metric, topologyParser.getLinkCosts(), routingTableFile);
        route.setUpShortestPaths();

        //Parse the connections file
        ConnectionParser connectionParser = new ConnectionParser(connectionsFile);
        connectionParser.parseFile();

        //Setup up connections and populate forwarding tables
        ConnectionSetup connectionSetup = new ConnectionSetup(approach, connectionParser.getConnections(),
                route, topologyParser.getLinkCapacity());
        connectionSetup.setupConnections();

        //File Output is handled in this class
        FileIOUtil fileIOUtil = new FileIOUtil(route.getShortestPaths(), route.getNextShortestPaths(), topologyParser.getLinkCosts(),
                connectionSetup.getForwadingTables(), metric,
                connectionSetup.getTotalConnections(), connectionSetup.getAdmittedConnections());

        try{
            //Output routing table to file
            fileIOUtil.outputRouteFile(routingTableFile);
            //Output Forwarding table to file
            fileIOUtil.outputForwardingTable(forwardingTableFile);
            //Output connection admission details to file
            fileIOUtil.outputPathsFile(pathsFile);
        } catch (IOException e){
            System.out.println("IO Exception, please retry");
        }

    }
}
