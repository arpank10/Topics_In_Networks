import java.io.FileNotFoundException;
import java.io.IOException;

public class RoutingLD {
    public static void main(String[] args) throws FileNotFoundException {
        String topologyFile = args[0];
        String connectionsFile = args[1];
        String routingTableFile = args[2];
        String forwardingTableFile = args[3];
        String pathsFile = args[4];
        String metric = args[5];
        String approach = args[6];

        TopologyParser topologyParser = new TopologyParser(topologyFile);
        topologyParser.parseFile();

        Route route = new Route(metric, topologyParser.getLinkCosts(), routingTableFile);
        route.setUpShortestPaths();

        ConnectionParser connectionParser = new ConnectionParser(connectionsFile);
        connectionParser.parseFile();

        ConnectionSetup connectionSetup = new ConnectionSetup(approach, connectionParser.getConnections(),
                route, topologyParser.getLinkCapacity());
        connectionSetup.setupConnections();

        FileIOUtil fileIOUtil = new FileIOUtil(route.getShortestPaths(), route.getNextShortestPaths(), topologyParser.getLinkCosts(),
                connectionSetup.getForwadingTables(), metric,
                connectionSetup.getTotalConnections(), connectionSetup.getAdmittedConnections());

        try{
            fileIOUtil.outputRouteFile(routingTableFile);
            fileIOUtil.outputForwardingTable(forwardingTableFile);
            fileIOUtil.outputPathsFile(pathsFile);
        } catch (IOException e){
            System.out.println("IO Exception, please retry");
        }

    }
}
