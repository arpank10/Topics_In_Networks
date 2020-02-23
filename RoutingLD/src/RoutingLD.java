import java.io.FileNotFoundException;

public class RoutingLD {
    public static void main(String[] args) throws FileNotFoundException {
        String topologyFile = args[0];
        String connectionsFile = args[1];
        String routingTableFile = args[2];
        String forwardingTableFile = args[3];
        String pathsFile = args[4];
        String metric = "hop";
        String approach = "0";

        TopologyParser topologyParser = new TopologyParser(topologyFile);
        topologyParser.parseFile();

        Route route = new Route(metric, topologyParser.getLinkCosts(), routingTableFile);
        route.setUpShortestPaths();

        ConnectionParser connectionParser = new ConnectionParser(connectionsFile);
        connectionParser.parseFile();

        ConnectionSetup connectionSetup = new ConnectionSetup(approach, connectionParser.getConnections(),
                route, topologyParser.getLinkCapacity());
        connectionSetup.setupConnections();

    }
}
