import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

//Util class which contains helper functions
class Util {
    private Random random;
    private String outputFilePath;

    Util(String outputFilePath){
        this.outputFilePath = outputFilePath;
        random = new Random();
    }

    //returns 1 to generate packet, otherwise 0
    //Rounding probability to 2 decimal places
    boolean generatePacketWithProbability(double prob){
        prob = (double)Math.round(prob * 100d) / 100d;
        int rand = random.nextInt(100);
        double val = getAverage(rand, 100);
        return val <= prob;
    }

    //Generates an index from 0 to N-1 to get the output port
    int getOutputPortIndex(int portCount){
        return random.nextInt(portCount);
    }

    //Generates an index from 0 to k-1 to select a packet in contention
    int generatePacketIndex(int k){
        return random.nextInt(k);
    }

    //Display the result on terminal, and save the result to output file
    void outputResults(
            int portCount,
            Double packetGenProbability,
            Constants.Technique technique,
            long totalDelay,
            long totalSquareDelay,
            long totalPacketsTransmitted,
            long totalTime,
            double totalProbability) {

        double averageProbability = packetGenProbability;
        double averageDroppingProbability = totalProbability/totalTime;
        double averageDelay = getAverage(totalDelay, totalPacketsTransmitted);
        double averageSquaredDelay  = getAverage(totalSquareDelay, totalPacketsTransmitted);
        double standardDev = Math.sqrt(averageSquaredDelay - averageDelay*averageDelay);
        double linkUtil = getAverage(totalPacketsTransmitted, totalTime*portCount);

        printResults(portCount, averageProbability, technique, averageDelay, standardDev, linkUtil, averageDroppingProbability);
        printToFile(portCount, averageProbability, technique, averageDelay, standardDev, linkUtil, averageDroppingProbability);
    }

    private void printResults(double portCount, double averageProbability, Constants.Technique technique,
                                  double averageDelay, double standardDev, double linkUtil, double averageDroppingProbability){
        System.out.println(Constants.outputFormat);
        System.out.println(String.format(Constants.outputFormatDimensions, portCount,
                averageProbability, technique.toString(),
                averageDelay, standardDev, linkUtil));
        if(technique == Constants.Technique.KOUQ)
            System.out.println("Average Dropping Probability: " + averageDroppingProbability);
    }

    private void printToFile(double portCount, double averageProbability, Constants.Technique technique,
                     double averageDelay, double standardDev, double linkUtil, double averageDroppingProbability){

        //Check if file exists
        File file = new File(outputFilePath);
        boolean fileExists = file.exists();


        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(outputFilePath, true);
            PrintWriter printWriter = new PrintWriter(fileWriter);

            //If file doesn't exist, append the headings
            if(!fileExists)
                printWriter.println(Constants.outputFormat);

            printWriter.println(String.format(Constants.outputFormatDimensions, portCount,
                    averageProbability, technique.toString(),
                    averageDelay, standardDev, linkUtil));
            if(technique == Constants.Technique.KOUQ)
                printWriter.println("Average Dropping Probability: " + averageDroppingProbability);

            printWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Calculate average
    double getAverage(long value, long total){
        double average = (double) value;
        double totalV = (double) total;
        average = average/totalV;
        return average;
    }
}
