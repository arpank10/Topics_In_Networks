import java.io.FileNotFoundException;
import java.util.InputMismatchException;
import java.util.Scanner;

//Main entry point of the program
public class PacketSwitchController {

    public static void main(String[] args) throws FileNotFoundException {
        int switchCount = Constants.DEFAULT_PORT_COUNT;
        int bufferSize = Constants.DEFAULT_BUFFER_SIZE;
        double packetGenProbability = Constants.DEFAULT_PACKET_GEN_PROBABILITY;
        Constants.Technique technique = Constants.DEFAULT_TECHNIQUE;
        int simulationTime = Constants.DEFAULT_SIMULATION_TIME;

        Scanner scanner = new Scanner(System.in);
        try {
            System.out.println("Enter the number of input and output ports in the switch");
            switchCount = scanner.nextInt();
            System.out.println("Enter buffer size");
            bufferSize = scanner.nextInt();

            System.out.println("Enter packet generation probability");
            packetGenProbability = scanner.nextDouble();
            if(packetGenProbability>1 || packetGenProbability<0) throwErrorMessage();

            System.out.println("Enter Technique of scheduling");
            String techniqueString = scanner.nextLine().toUpperCase();
            if(techniqueString.equals(Constants.Technique.INQ.toString()))
                technique = Constants.Technique.INQ;
            else if(techniqueString.equals(Constants.Technique.KOUQ.toString()))
                technique = Constants.Technique.KOUQ;
            else if(techniqueString.equals(Constants.Technique.ISLIP.toString()))
                technique = Constants.Technique.ISLIP;
            else throwErrorMessage();

            System.out.println("Enter maximum simulation time in seconds");
            simulationTime = scanner.nextInt();
            if (simulationTime < 0) throwErrorMessage();


            Util util = new Util();
            //Create a packet switch with given configuration
            PacketSwitch packetSwitch = new PacketSwitch(switchCount, bufferSize, packetGenProbability, technique, util);

            //Run different cases
            switch (technique){
                case INQ:
                    break;
                case KOUQ:
                    break;
                case ISLIP:
                    break;
            }
        } catch (InputMismatchException e){
            throwErrorMessage();
        }
    }

    private static void throwErrorMessage(){
        System.out.println("Wrong input");
        System.exit(0);
    }

}