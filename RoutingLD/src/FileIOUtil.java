import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class FileIOUtil {
    private ArrayList<ArrayList<ArrayList<Integer>>> shortestPaths;
    private ArrayList<ArrayList<ArrayList<Integer>>> nextShortestPaths;
    private ArrayList<ArrayList<ForwardingTableEntry>> forwadingTables;
    private ArrayList<ArrayList<Integer>> graphMatrix;

    private String metric;
    private int totalConnections, admittedConnections;

    private int nodes;

    FileIOUtil(ArrayList<ArrayList<ArrayList<Integer>>> shortestPaths,
               ArrayList<ArrayList<ArrayList<Integer>>> nextShortestPaths,
               ArrayList<ArrayList<Integer>> graphMatrix,
               ArrayList<ArrayList<ForwardingTableEntry>> forwadingTables,
               String metric, int totalConnections, int admittedConnections) {

        this.shortestPaths = shortestPaths;
        this.nextShortestPaths = nextShortestPaths;
        this.graphMatrix = graphMatrix;
        this.metric = metric;
        this.totalConnections = totalConnections;
        this.admittedConnections = admittedConnections;
        this.forwadingTables = forwadingTables;

        nodes = graphMatrix.size();
    }

    void outputRouteFile(String filePath) throws IOException{
        FileWriter fileWriter = new FileWriter(filePath);
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
    }

    void outputForwardingTable(String filePath) throws IOException{
        FileWriter fileWriter = new FileWriter(filePath);
        PrintWriter printWriter = new PrintWriter(fileWriter);
        for(int i = 0;i<nodes;i++){
            printWriter.println("Forwarding Table for Node " + i);
            printWriter.println(String.format("%-15s %-30s %-15s %-30s %-15s","Router's ID", "ID of Incoming Port", "VCID","ID of Outgoing Port", "VCID"));
            ArrayList<ForwardingTableEntry> forwardingTableForCurrentNode = forwadingTables.get(i);

            for(ForwardingTableEntry f: forwardingTableForCurrentNode){
                printWriter.println(String.format("%-15s %-30s %-15s %-30s %-15s",
                        f.getRouterId(), f.getIncomingId(), f.getInVCID(), f.getOutgoingId(), f.getOutVCID()));
            }
        }
        printWriter.close();
    }

    void outputPathsFile(String filePath) throws IOException {
        FileWriter fileWriter = new FileWriter(filePath);
        PrintWriter printWriter = new PrintWriter(fileWriter);
        StringBuilder sb = new StringBuilder();
        sb.append("Total Connections = ");
        sb.append(totalConnections);
        sb.append(" Admitted Connections = ");
        sb.append(admittedConnections);
        printWriter.println(sb.toString());
        printWriter.close();

    }



    private int getPathDelay(ArrayList<Integer> path){
        int delay = 0;
        for(int i = 0;i<path.size() - 1;i++){
            int node1 = path.get(i);
            int node2 = path.get(i+1);
            delay+=graphMatrix.get(node1).get(node2);
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
}
