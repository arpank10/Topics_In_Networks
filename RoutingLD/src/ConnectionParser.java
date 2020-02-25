import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;


public class ConnectionParser {
    private String filePath;
    private int totalConnections;

    //To store the connections
    private ArrayList<Connection> connections;


    public static void main(String[] args) throws FileNotFoundException {
        new ConnectionParser(".\\Data\\NSFNET_100.txt").parseFile();
    }

    ConnectionParser(String filePath) {
        this.filePath = filePath;
        connections = new ArrayList<>();
        totalConnections = 0;
    }

    //Parses the connection file
    void parseFile() throws FileNotFoundException {
        File file = new File(filePath);
        Scanner sc = new Scanner(file);

        int lineCount = 0;
        String line = "";
        while (sc.hasNextLine()) {
            line = sc.nextLine();
            String[] lineElements = line.split(" ");
            //First line contains the total number of connections
            if (lineCount == 0) {
                totalConnections = Integer.parseInt(lineElements[0]);
            } else {
                //Next lines contain five integers denoting the connections
                int source = Integer.parseInt(lineElements[0]);
                int destination = Integer.parseInt(lineElements[1]);
                int bMin = Integer.parseInt(lineElements[2]);
                int bAvg = Integer.parseInt(lineElements[3]);
                int bMax = Integer.parseInt(lineElements[4]);
                Connection newConnection = new Connection(lineCount, source, destination, bMin, bAvg, bMax);
                connections.add(newConnection);

            }
            lineCount++;
        }
    }

    //Getter functions
    ArrayList<Connection> getConnections() {
        return connections;
    }
}
