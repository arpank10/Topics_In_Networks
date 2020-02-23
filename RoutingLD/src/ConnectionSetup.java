import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class ConnectionSetup {
    private String approach;
    private Route routes;
    private ArrayList<Connection> connections;
    private int totalConnections;
    private int admittedConnections;
    private int nodes;

    private ArrayList<ArrayList<ArrayList<Integer>>> shortestPaths;
    private ArrayList<ArrayList<ArrayList<Integer>>> nextShortestPaths;
    private ArrayList<ArrayList<Integer>> graphMatrix;
    private String metric;

    private ArrayList<ArrayList<ForwardingTableEntry>> forwadingTables;

    private ArrayList<HashMap<Integer, Double>> linkCapacity;
    private ArrayList<ArrayList<Integer>> linkVcid;


    public static void main(String[] args){

    }

    ConnectionSetup(String approach, ArrayList<Connection> connections, Route routes, ArrayList<HashMap<Integer, Double>> linkCapacity) {
        this.approach = approach;
        this.routes = routes;
        this.connections = connections;
        this.linkCapacity = linkCapacity;
        initialize();
    }

    private void initialize() {
        this.shortestPaths = routes.getShortestPaths();
        this.nextShortestPaths = routes.getNextShortestPaths();
        this.graphMatrix = routes.getGraphMatrix();
        this.metric = routes.getMetric();
        this.totalConnections = connections.size();
        this.admittedConnections = 0;
        this.nodes = shortestPaths.size();
        linkVcid = new ArrayList<>();
        for(int i = 0;i<nodes;i++){
            ArrayList<Integer> temp = new ArrayList<>();
            for(int j = 0;j<nodes;j++){
                temp.add(1);
            }
            linkVcid.add(temp);
        }
        forwadingTables = new ArrayList<>();
        for(int i = 0;i<nodes;i++){
            ArrayList<ForwardingTableEntry> temp = new ArrayList<>();
            forwadingTables.add(temp);
        }
    }

    void setupConnections() {
        System.out.println(String.format("%-15s %-10s %-15s %-50s %-50s %-10s","Connection ID", "Source", "Destination",
                "Path", "VCID List", "Path Cost"));
        for(Connection connection: connections) {
            if(trySettingUpConnection(connection, 1)){
                connection.setPathAlloted(0);
                admittedConnections++;
            } else if(trySettingUpConnection(connection, 0)){
                connection.setPathAlloted(1);
                admittedConnections++;
            }
        }

        printStats();
    }

    private boolean trySettingUpConnection(Connection connection, int shortest){
        int source = connection.getSource();
        int destination = connection.getDestination();

        ArrayList<Integer> path = shortest == 1?shortestPaths.get(source).get(destination):nextShortestPaths.get(source).get(destination);

        boolean isAdmissible = true;
        int i;
        for(i = 0;i<path.size()-1;i++){
            int node1 = path.get(i);
            int node2 = path.get(i+1);

            double capacity = linkCapacity.get(node1).get(node2);
            double requiredCapacity = approach.equals("0")?connection.getbEq():(double)connection.getbMax();
            if(requiredCapacity>capacity){
                isAdmissible = false;
                break;
            }
            linkCapacity.get(node1).put(node2, capacity - requiredCapacity);
            linkCapacity.get(node2).put(node1, capacity - requiredCapacity);

        }
        if(!isAdmissible){
            for(int j = 0;j<i;j++){
                int node1 = path.get(j);
                int node2 = path.get(j+1);

                double capacity = linkCapacity.get(node1).get(node2);
                double requiredCapacity = approach.equals("0")?connection.getbEq():(double)connection.getbMax();

                linkCapacity.get(node1).put(node2, capacity + requiredCapacity);
                linkCapacity.get(node2).put(node1, capacity + requiredCapacity);
            }
        }

        if(isAdmissible) setUpVcid(path, connection);

        return isAdmissible;
    }

    //Each node can be connected to at most n-1 nodes, so the ports are numbered as (1,2...,n-1)
    private void setUpVcid(ArrayList<Integer> path, Connection connection) {
        //Suppose path is 1->3->12->11

        int prevOutVcid = linkVcid.get(path.get(0)).get(path.get(1));
        linkVcid.get(path.get(0)).set(path.get(1), prevOutVcid + 1);
        linkVcid.get(path.get(1)).set(path.get(0), prevOutVcid + 1);

        ArrayList<Integer> connectionVcidList = new ArrayList<>();
        connectionVcidList.add(prevOutVcid);
        //Add entries in intermediate nodes i.e 3 and 12
        //OutgoingVCID of previous node should be equal to incomingVCID of next
        for(int i=1;i<path.size()-1;i++){
            int incomingPort = path.get(i-1);
            int routerId = path.get(i);
            int outgoingPort = path.get(i+1);

            int inVcid = prevOutVcid;
            int outVcid = linkVcid.get(routerId).get(outgoingPort);

            connectionVcidList.add(outVcid);
            linkVcid.get(routerId).set(outgoingPort, outVcid+1);
            linkVcid.get(outgoingPort).set(routerId, outVcid+1);
            prevOutVcid = outVcid;

            if(incomingPort>routerId) incomingPort--;
            if(outgoingPort>routerId) outgoingPort--;

            ForwardingTableEntry forwardingTableEntry = new ForwardingTableEntry(routerId, incomingPort, inVcid, outgoingPort, outVcid);
            forwadingTables.get(routerId).add(forwardingTableEntry);
        }

        printConnectionDetails(connection, path, connectionVcidList);
    }

    private void printConnectionDetails(Connection connection, ArrayList<Integer> path, ArrayList<Integer> vcidList){
        StringBuilder p = new StringBuilder();
        StringBuilder v = new StringBuilder();
        for(Integer node: path){
            p.append(node);
            p.append(" ");
        }
        for(Integer vcid: vcidList){
            v.append(vcid);
            v.append(" ");
        }

        System.out.println(String.format("%-15s %-10s %-15s %-50s %-50s %-10s",connection.getId(), connection.getSource(), connection.getDestination(),
                p.toString(), v.toString(), getPathCost(path)));

    }

    private int getPathCost(ArrayList<Integer> path){

        if(metric.equals("hop")) return path.size() - 1;
        int cost = 0;
        for(int i = 0;i<path.size() - 1;i++){
            int node1 = path.get(i);
            int node2 = path.get(i+1);
            cost+= routes.getGraphMatrix().get(node1).get(node2);
        }
        return cost;
    }


    private void printStats(){
        int shortest = 0, failed = 0, nextShortest = 0;
        for(Connection connection: connections){
            if(connection.getPathAlloted() == 0) shortest++;
            else if(connection.getPathAlloted() == 1) nextShortest++;
            else if(connection.getPathAlloted() == -1) failed++;
        }
        System.out.println("Shortest Path in Connection = " + shortest);
        System.out.println("Next Shortest Path in Connection = " + nextShortest);
        System.out.println("Failed Connections= " + failed);
    }

    public int getTotalConnections() {
        return totalConnections;
    }

    public int getAdmittedConnections() {
        return admittedConnections;
    }

    public ArrayList<ArrayList<ForwardingTableEntry>> getForwadingTables() {
        return forwadingTables;
    }
}
