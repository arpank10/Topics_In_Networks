import java.io.File;

public class RoutingLD {
    public static void main(String[] args){
        String topologyFile = args[0];
        String connectionsFile = args[1];
        String routingTableFile = args[2];
        String forwardingTableFile = args[3];
        String pathsFile = args[4];
        String metric = args[5];
        String approach = args[6];

        String filePath = new File("").getAbsolutePath();

        System.out.println(topologyFile);
        System.out.println(connectionsFile);
        System.out.println(routingTableFile);
        System.out.println(forwardingTableFile);
        System.out.println(pathsFile);
        System.out.println(metric);
        System.out.println(approach);

        System.out.println(filePath);
    }
}
