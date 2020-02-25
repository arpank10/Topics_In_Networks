import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Route {
    private ArrayList<ArrayList<Integer>> graphMatrix;
    private String metric;
    private int nodes;
    private String routingFilePath;

    private ArrayList<ArrayList<ArrayList<Integer>>> shortestPaths;
    private ArrayList<ArrayList<ArrayList<Integer>>> nextShortestPaths;

    public static void main(String[] args) throws FileNotFoundException {
        TopologyParser topologyParser = new TopologyParser(".\\Data\\top14.txt");
        topologyParser.parseFile();

        new Route("distance", topologyParser.getLinkCosts(), ".\\Data\\routingTables.txt").setUpShortestPaths();
    }

    Route(String metric, ArrayList<ArrayList<Integer>> matrix, String routingFilePath){
        this.graphMatrix = matrix;
        this.metric = metric;
        this.nodes = matrix.size();
        this.routingFilePath = routingFilePath;

        initShortestPaths();
    }

    //Initialize shortest path lists with empty lists
    private void initShortestPaths() {
        shortestPaths = new ArrayList<>();
        nextShortestPaths = new ArrayList<>();

        for(int i = 0;i<nodes;i++){
            ArrayList<ArrayList<Integer>> paths = new ArrayList<>();
            for(int j = 0;j<nodes;j++){
                ArrayList<Integer> path = new ArrayList<>();
                paths.add(path);
            }
            shortestPaths.add(paths);
        }
        for(int i = 0;i<nodes;i++){
            ArrayList<ArrayList<Integer>> paths = new ArrayList<>();
            for(int j = 0;j<nodes;j++){
                ArrayList<Integer> path = new ArrayList<>();
                paths.add(path);
            }
            nextShortestPaths.add(paths);
        }
    }

    //Returns minimum distance node among the nodes not visited till now
    private int getMinimumNode(Set<Integer> nodesVisited, ArrayList<Integer> distances){
        int nodeIndex = -1;
        int minDistance = Integer.MAX_VALUE;
        for(int i = 0;i<nodes;i++){
            if(distances.get(i)<minDistance && !nodesVisited.contains(i)){
                minDistance = distances.get(i);
                nodeIndex = i;
            }
        }
        return nodeIndex;
    }

    //Add shortest path to either the shortest list or nextShortest list based on the shortest parameter
    private void addShortestPath(int source, int destination, int shortest, int node, ArrayList<Integer> parent){
        if(node == -1) return;
        addShortestPath(source, destination, shortest, parent.get(node), parent);
        if(shortest == 1)
            shortestPaths.get(source).get(destination).add(node);
        else nextShortestPaths.get(source).get(destination).add(node);
    }

    //Runs dijkstra's algorithm to find the shortest path betwen a pair of nodes in graph
    private void findShortestPath(int source, int destination, ArrayList<ArrayList<Integer>> graph, int shortest){
        ArrayList<Integer> distances = new ArrayList<>();
        ArrayList<Integer> parent = new ArrayList<>();

        //Initialize distances to max value
        for(int i = 0;i<nodes;i++){
            distances.add(Integer.MAX_VALUE);
            parent.add(-1);
        }

        //This set contains the nodes that have been visited
        Set<Integer> nodesVisited = new HashSet<>();
        //Set distance to source as 0
        distances.set(source, 0);


        //Run till all the nodes are visited
        while(nodesVisited.size() < nodes){
            //Get the minimum distance node from the unvisited nodes
            int currentNode = getMinimumNode(nodesVisited, distances);
            //Add it to the visited nodes
            nodesVisited.add(currentNode);

            if(currentNode==-1) break;

            //Add the adjacent nodes while calculating their shortest distances and setting their parents
            for(int i = 0;i<nodes;i++){
                if(graph.get(currentNode).get(i)>0 && !nodesVisited.contains(i)){
                    int linkCost = metric.equals("hop")?1:graph.get(currentNode).get(i);
                    if(distances.get(currentNode)+linkCost<distances.get(i)){
                        distances.set(i, distances.get(currentNode)+linkCost);
                        parent.set(i, currentNode);
                    }
                }
            }
        }
        //Get the actual path
        if(distances.get(destination)!=Integer.MAX_VALUE)
            addShortestPath(source, destination, shortest, destination, parent);

    }

    //Removes a path from the graph to create a new graph
    private ArrayList<ArrayList<Integer>> removePath(ArrayList<Integer> path) {
        ArrayList<ArrayList<Integer>> newGraph = new ArrayList<>();

        //Deep clone the original graph
        for(int i = 0;i<nodes;i++){
            ArrayList<Integer> temp = new ArrayList<>();
            for(int j = 0;j<nodes;j++){
                temp.add(graphMatrix.get(i).get(j));
            }
            newGraph.add(temp);
        }

        //Remove the edges
        for(int i = 0;i<path.size()-1;i++){
            int node1 = path.get(i);
            int node2 = path.get(i+1);
            newGraph.get(node1).set(node2, 0);
            newGraph.get(node2).set(node1, 0);
        }
        return newGraph;
    }

    void setUpShortestPaths() {
        for(int source = 0; source< nodes; source++) {
            for(int destination = 0; destination< nodes; destination++) {
                //First find the shortest path, denoted by 1 in argument
                findShortestPath(source, destination, graphMatrix, 1);
                //Remove the shortest path from the graph to get a temmporary graph
                ArrayList<ArrayList<Integer>> tempMatrix = removePath(shortestPaths.get(source).get(destination));
                //Find the next shortest path, denoted by 0
                findShortestPath(source, destination, tempMatrix, 0);
            }
        }
    }

    private void printMatrix(){
        for(int i=0;i<nodes;i++){
            for(int j = 0;j<nodes;j++){
                System.out.print(graphMatrix.get(i).get(j));
                System.out.print(" ");
            }
            System.out.println();
        }
    }

    //Getter functions
    ArrayList<ArrayList<ArrayList<Integer>>> getShortestPaths() {
        return shortestPaths;
    }

    ArrayList<ArrayList<ArrayList<Integer>>> getNextShortestPaths() {
        return nextShortestPaths;
    }

    ArrayList<ArrayList<Integer>> getGraphMatrix() {
        return graphMatrix;
    }

    String getMetric() {
        return metric;
    }
}
