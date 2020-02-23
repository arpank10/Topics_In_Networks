import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class TopologyParser {
    private String filePath;
    private int nodes = 0;
    private int edges = 0;

    private ArrayList<ArrayList<Integer>> linkCosts;

    private ArrayList<HashMap<Integer, Double>> linkCapacity;

    public static void main(String[] args) throws FileNotFoundException{
        new TopologyParser(".\\Data\\top14.txt").parseFile();
    }

    TopologyParser(String filePath){
        this.filePath = filePath;
        initialize();
    }

    private void initialize(){
        linkCosts = new ArrayList<>();
        linkCapacity = new ArrayList<>();

        //Created the matrix denoting the graph
        for(int i=0;i<nodes;i++){
            ArrayList<Integer> temp = new ArrayList<>();
            HashMap<Integer, Double> tempDelay = new HashMap<>();

            for(int j=0;j<nodes;j++){
                temp.add(0);
            }

            linkCosts.add(temp);
            linkCapacity.add(tempDelay);
        }
    }

    void parseFile() throws FileNotFoundException {
        File file = new File(filePath);
        Scanner sc = new Scanner(file);

        int lineCount = 0;
        String line = "";
        while (sc.hasNextLine()) {
            line = sc.nextLine();
            String[] lineElements = line.split(" ");
            if (lineCount == 0) {
                nodes = Integer.parseInt(lineElements[0]);
                edges = Integer.parseInt(lineElements[1]);
                initialize();
            } else {
                int node1 = Integer.parseInt(lineElements[0]);
                int node2 = Integer.parseInt(lineElements[1]);
                int delay = Integer.parseInt(lineElements[2]);
                double capacity = Double.parseDouble(lineElements[3]);

                //One direction
                linkCosts.get(node1).set(node2, delay);
                linkCapacity.get(node1).put(node2, capacity);

                //Other direction
                linkCosts.get(node2).set(node1, delay);
                linkCapacity.get(node2).put(node1, capacity);
            }
            lineCount++;
        }

//        printMatrix();
    }

    private void printMatrix(){
        for(int i=0;i<nodes;i++){
            for(int j = 0;j<nodes;j++){
                System.out.print(linkCosts.get(i).get(j));
                System.out.print(" ");
            }
            System.out.println();
        }
    }


    public ArrayList<ArrayList<Integer>> getLinkCosts() {
        return linkCosts;
    }

    public ArrayList<HashMap<Integer, Double>> getLinkCapacity() {
        return linkCapacity;
    }
}
