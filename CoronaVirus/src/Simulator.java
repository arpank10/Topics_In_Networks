import java.util.InputMismatchException;
import java.util.Scanner;

//Main entry point of the program
public class Simulator {
    public static void main(String[] args){
        Scanner scanner = new Scanner(System.in);
        try {
            //Take input from the terminal
            System.out.println("Enter 1 for Case A, 2 for Case B, 3 for Case C");
            int c = scanner.nextInt();
            if (c < 1 || c > 3) throwErrorMessage();
            System.out.println("Enter arrivalRate(lambda) in decimal");
            Double lambda = scanner.nextDouble();
            System.out.println("Enter serviceRate(mu) in decimal");
            Double mu = scanner.nextDouble();
            System.out.println("Enter maximum number of passengers to simulate on:");
            Integer maxPassengers = scanner.nextInt();
            if (maxPassengers < 0) throwErrorMessage();
            System.out.println("Enter maximum simulation time in hours");
            Integer simulationTime = scanner.nextInt();
            if (simulationTime < 0) throwErrorMessage();

            Util util = new Util();

            //Run different cases
            switch (c){
                case 1: new CaseA(lambda/3.0, mu, maxPassengers, simulationTime, util).simulate();
                        break;
                case 2: new CaseB(lambda, mu, maxPassengers, simulationTime, util).simulate();
                        break;
                case 3: new CaseC(lambda, mu, maxPassengers, simulationTime, util).simulate();
                        break;
            }
        } catch (InputMismatchException e){
            throwErrorMessage();
        }
    }

    private static void throwErrorMessage(){
        System.out.println("Incorrect Input");
        System.exit(0);
    }
}
