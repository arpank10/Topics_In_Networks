import java.util.HashSet;
import java.util.List;
import java.util.Random;

class Util {
    private Random random;

    Util(){
        random = new Random();
    }

    //returns 1 to generate packet, otherwise 0
    //Rounding probability to 2 decimal places
    boolean generatePacketWithProbability(double prob){
        prob = (double)Math.round(prob * 100d) / 100d;
        int rand = random.nextInt(100);
        double val =rand/100;
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

    void printResults(Constants.Technique technique, Double totalProbability, int totalDelay, int totalTime){
        Double averageDelay = getAverage(totalDelay, totalTime);
        System.out.println("Average Delay = " + averageDelay);
        System.out.println("Total Delay = " + totalDelay);
    }

    //Calculate average
    private Double getAverage(Integer value, Integer total){
        Double average = new Double(value);
        Double totalV = new Double(total);
        average = average/totalV;
        return average;
    }
}
