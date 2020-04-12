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
        int rand = random.nextInt();
        rand = rand%100;
        double val =rand/100;
        if(val<=prob) return true;
        return false;
    }

    //Generates an index from 0 to N-1 to get the output port
    int getOutputPortIndex(int portCount){
        int rand = random.nextInt();
        return rand%portCount;
    }

    //Generates an index from 0 to k-1 to select a packet in contention
    int generatePacketIndex(int k){
        int rand = random.nextInt();
        return rand%k;
    }

    void printResults(){

    }
}
