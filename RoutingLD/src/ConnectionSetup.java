import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class ConnectionSetup {
    private String approach;
    private Route routes;
    private ArrayList<Connection> connections;
    private int totalConnections;

    private ArrayList<ArrayList<ArrayList<Integer>>> shortestPaths;
    private ArrayList<ArrayList<ArrayList<Integer>>> nextShortestPaths;

    private ArrayList<HashMap<Integer, Double>> linkCapacity;


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
        this.totalConnections = connections.size();
    }

    public void setupConnections() {
        for(Connection connection: connections) {
            if(trySettingUpShortestPath(connection)){
                connection.setPathAlloted(0);
                System.out.println("Shortest path chosen");
            } else if(trySettingUpNextShortestPath(connection)){
                connection.setPathAlloted(1);
                System.out.println("Next shortest path chosen");
            } else {
                System.out.println("Failed connection");
            }
        }

        printStats();
    }

    private boolean trySettingUpShortestPath(Connection connection){
        int source = connection.getSource();
        int destination = connection.getDestination();

        ArrayList<Integer> path = shortestPaths.get(source).get(destination);
//        printPath(path);
//        printPathLinks(path);

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

        }
//        printPathLinks(path);
        if(!isAdmissible){
            for(int j = 0;j<i;j++){
                int node1 = path.get(j);
                int node2 = path.get(j+1);

                double capacity = linkCapacity.get(node1).get(node2);
                double requiredCapacity = approach.equals("0")?connection.getbEq():(double)connection.getbMax();

                linkCapacity.get(node1).put(node2, capacity + requiredCapacity);
            }
        }
//        printPathLinks(path);

        return isAdmissible;
    }

    private boolean trySettingUpNextShortestPath(Connection connection){
        int source = connection.getSource();
        int destination = connection.getDestination();

        ArrayList<Integer> path = nextShortestPaths.get(source).get(destination);

        boolean isAdmissible = true;
        int i;
//        printPath(path);
//        printPathLinks(path);

        for(i = 0;i<path.size()-1;i++){
            int node1 = path.get(i);
            int node2 = path.get(i+1);

            double capacity = (double)linkCapacity.get(node1).get(node2);
            double requiredCapacity = approach.equals("0")?connection.getbEq():(double)connection.getbMax();
            if(requiredCapacity>capacity){
                isAdmissible = false;
                break;
            }
            linkCapacity.get(node1).put(node2, capacity - requiredCapacity);

        }

//        printPathLinks(path);
        if(!isAdmissible){
            for(int j = 0;j<i;j++){
                int node1 = path.get(j);
                int node2 = path.get(j+1);

                double capacity = linkCapacity.get(node1).get(node2);
                double requiredCapacity = approach.equals("0")?connection.getbEq():(double)connection.getbMax();

                linkCapacity.get(node1).put(node2, capacity + requiredCapacity);
            }
        }
//        printPathLinks(path);

        return isAdmissible;
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
        System.out.println(approach);
    }
}
