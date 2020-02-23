import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class ConnectionParser {
    private String filePath;
    private int totalConnections;

    ArrayList<Connection> connections;


    public static void main(String[] args) throws FileNotFoundException {
        new ConnectionParser(".\\Data\\NSFNET_100.txt").parseFile();
    }

    ConnectionParser(String filePath) {
        this.filePath = filePath;
        connections = new ArrayList<>();
        totalConnections = 0;
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
                totalConnections = Integer.parseInt(lineElements[0]);
            } else {
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
        printMatrix();
    }

    private void printMatrix(){
        for(int i=0;i<totalConnections;i++){
            Connection connection = connections.get(i);
            System.out.print(connection.getId() + " ");
            System.out.print(connection.getSource() + " ");
            System.out.print(connection.getDestination() + " ");
            System.out.print(connection.getbMin() + " ");
            System.out.print(connection.getbAvg() + " ");
            System.out.print(connection.getbMax() + " ");
            System.out.print(connection.getbEq());
            System.out.println();
        }
    }

    public ArrayList<Connection> getConnections() {
        return connections;
    }
}
