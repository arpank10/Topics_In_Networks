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
    private ArrayList<HashMap<Integer, Integer>> linkDelays;

    public static void main(String[] args) throws FileNotFoundException {
        TopologyParser topologyParser = new TopologyParser(".\\Data\\top14.txt");
        topologyParser.parseFile();

        new Route("distance", topologyParser.getLinkCosts(), topologyParser.getLinkDelay(), ".\\Data\\routingTables.txt").setUpShortestPaths();
    }

    Route(String metric, ArrayList<ArrayList<Integer>> matrix, ArrayList<HashMap<Integer, Integer>> linkDelays, String routingFilePath){
        this.graphMatrix = matrix;
        this.metric = metric;
        this.nodes = matrix.size();
        this.linkDelays = linkDelays;
        this.routingFilePath = routingFilePath;

        initShortestPaths();
    }

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

    private void addShortestPath(int source, int destination, int shortest, int node, ArrayList<Integer> parent){
        if(node == -1) return;
        addShortestPath(source, destination, shortest, parent.get(node), parent);
        if(shortest == 1)
            shortestPaths.get(source).get(destination).add(node);
        else nextShortestPaths.get(source).get(destination).add(node);
    }

    private void findShortestPath(int source, int destination, ArrayList<ArrayList<Integer>> graph, int shortest){
        ArrayList<Integer> distances = new ArrayList<>();
        ArrayList<Integer> parent = new ArrayList<>();
        for(int i = 0;i<nodes;i++){
            distances.add(Integer.MAX_VALUE);
            parent.add(-1);
        }

        Set<Integer> nodesVisited = new HashSet<>();
        distances.set(source, 0);

        while(nodesVisited.size() < nodes){
            int currentNode = getMinimumNode(nodesVisited, distances);
            nodesVisited.add(currentNode);

            if(currentNode==-1) break;

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
        if(distances.get(destination)!=Integer.MAX_VALUE)
            addShortestPath(source, destination, shortest, destination, parent);

    }

    private ArrayList<ArrayList<Integer>> removePath(ArrayList<Integer> path) {
        ArrayList<ArrayList<Integer>> newGraph = new ArrayList<>();

        for(int i = 0;i<nodes;i++){
            ArrayList<Integer> temp = new ArrayList<>();
            for(int j = 0;j<nodes;j++){
                temp.add(graphMatrix.get(i).get(j));
            }
            newGraph.add(temp);
        }

        for(int i = 0;i<path.size()-1;i++){
            int node1 = path.get(i);
            int node2 = path.get(i+1);
            newGraph.get(node1).set(node2, 0);
            newGraph.get(node2).set(node1, 0);
        }
        return newGraph;
    }

    private void setUpShortestPaths() {
        for(int source = 0; source< nodes; source++) {
            for(int destination = 0; destination< nodes; destination++) {
                findShortestPath(source, destination, graphMatrix, 1);
                ArrayList<ArrayList<Integer>> tempMatrix = removePath(shortestPaths.get(source).get(destination));
                findShortestPath(source, destination, tempMatrix, 0);
            }
        }

        outputToRoutingFile();
    }

    private void outputToRoutingFile() {
        try{
            FileWriter fileWriter = new FileWriter(routingFilePath);
            PrintWriter printWriter = new PrintWriter(fileWriter);
            for(int i = 0;i<nodes;i++){
                printWriter.println("Routing Table for Node " + i);
                printWriter.println(String.format("%-15s %-50s %-15s %-15s","Destination", "Path", "Path Delay","Path Cost"));
                for(int j = 0;j<nodes;j++){
                    if(i==j) continue;
                    ArrayList<Integer> path = shortestPaths.get(i).get(j);
                    if(path.isEmpty()) continue;
                    StringBuilder p = new StringBuilder();
                    for(int k = 0;k<path.size();k++){
                        p.append(path.get(k));
                        if(k!=path.size()-1)
                            p.append(" ");
                    }
                    printWriter.println(String.format("%-15s %-50s %-15s %-15s",j, p.toString(), getPathDelay(path), getPathCost(path)));
                    path = nextShortestPaths.get(i).get(j);
                    if(path.isEmpty()) continue;
                    p = new StringBuilder();
                    for(int k = 0;k<path.size();k++){
                        p.append(path.get(k));
                        if(k!=path.size()-1)
                            p.append(" ");
                    }
                    printWriter.println(String.format("%-15s %-50s %-15s %-15s",j, p.toString(), getPathDelay(path), getPathCost(path)));
                }
                printWriter.println();
            }
            printWriter.close();
        } catch (IOException e){
            System.out.println("IO Exception, please retry");
        }
    }

    private int getPathDelay(ArrayList<Integer> path){
        int delay = 0;
        for(int i = 0;i<path.size() - 1;i++){
            int node1 = path.get(i);
            int node2 = path.get(i+1);
            if(linkDelays.get(node1).containsKey(node2))
                delay+=linkDelays.get(node1).get(node2);
        }
        return delay;
    }

    private int getPathCost(ArrayList<Integer> path){

        if(metric.equals("hop")) return path.size() - 1;
        int cost = 0;
        for(int i = 0;i<path.size() - 1;i++){
            int node1 = path.get(i);
            int node2 = path.get(i+1);
            cost+= graphMatrix.get(node1).get(node2);
        }
        return cost;
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
}
