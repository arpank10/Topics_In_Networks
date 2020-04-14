import java.io.FileNotFoundException;
import java.util.InputMismatchException;
import java.util.Scanner;

//Main entry point of the program
public class PacketSwitchController {

    //java PacketSwitchController.Java  switchportcount buffersize packetgenprob queue knockout outputfile maxtimeslots
    public static void main(String[] args) throws FileNotFoundException {
        int switchCount = Constants.DEFAULT_PORT_COUNT;
        int bufferSize = Constants.DEFAULT_BUFFER_SIZE;
        double packetGenProbability = Constants.DEFAULT_PACKET_GEN_PROBABILITY;
        Constants.Technique technique = Constants.DEFAULT_TECHNIQUE;
        int simulationTime = Constants.DEFAULT_SIMULATION_TIME;
        int knockout = Constants.DEFAULT_KNOCKOUT;

        try{
//            terminalInput(switchCount, bufferSize, packetGenProbability, technique, simulationTime, knockout);
            commandLineInput(args, switchCount, bufferSize, packetGenProbability, technique, simulationTime, knockout);

            System.out.println(switchCount);
            System.out.println(bufferSize);
            System.out.println(packetGenProbability);
            System.out.println(technique);
            System.out.println(simulationTime);
            System.out.println(knockout);

            Util util = new Util();
            //Create a packet switch with given configuration
            PacketSwitch packetSwitch = new PacketSwitch(switchCount, bufferSize, packetGenProbability,knockout, technique, util);
            packetSwitch.simulate(simulationTime);
        } catch (InputMismatchException e){
            throwErrorMessage();
        }
    }

    private static void commandLineInput(String[] args, int switchCount, int bufferSize, double packetGenProbability,
                                         Constants.Technique technique, int simulationTime, int knockout){
        switchCount = Integer.parseInt(args[0]);
        bufferSize = Integer.parseInt(args[1]);
        packetGenProbability = Double.parseDouble(args[2]);
        String techniqueString = args[3];
        if(techniqueString.equals(Constants.Technique.INQ.toString()))
            technique = Constants.Technique.INQ;
        else if(techniqueString.equals(Constants.Technique.KOUQ.toString()))
            technique = Constants.Technique.KOUQ;
        else if(techniqueString.equals(Constants.Technique.ISLIP.toString()))
            technique = Constants.Technique.ISLIP;
        else throwErrorMessage();
        bufferSize = Integer.parseInt(args[1]);
        knockout = Integer.parseInt(args[4]);
        System.out.println(args[5]);
        simulationTime = Integer.parseInt(args[6]);
    }

    private static void terminalInput(int switchCount, int bufferSize, double packetGenProbability,
                                      Constants.Technique technique, int simulationTime, int knockout){
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter the number of input and output ports in the switch");
        switchCount = scanner.nextInt();
        knockout = (int)(0.6 * switchCount);

        System.out.println("Enter buffer size");
        bufferSize = scanner.nextInt();

        System.out.println("Enter packet generation probability");
        packetGenProbability = scanner.nextDouble();
        if(packetGenProbability>1.0 || packetGenProbability<0.0) throwErrorMessage();

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
    }

    private static void throwErrorMessage(){
        System.out.println("Wrong input");
        System.exit(0);
    }

}