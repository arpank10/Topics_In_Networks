import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CaseC {
    private Double arrivalRate = 0.04, serviceRate = 0.05;

    private Integer totalResponseTime, totalWaitingTime;

    private Integer totalPassengersInSystem, totalWaitingPassengers, totalServicedPassengers, currentTime;

    private Integer totalPassengers = 1000000;

    private Integer simulationTime = 10000;

    private List<Integer> arrivalTime, leavingTime, serviceEnteringTime;

    private Random random;

    private List<Server> servers;

    public static void main(String[] args){
        new CaseB().simulate();
    }

    CaseC() {
        initialize();
    }


    CaseC(Double arrivalRate, Double serviceRate, Integer totalPassengers, Integer simulationTime){
        this.arrivalRate = arrivalRate;
        this.serviceRate = serviceRate;
        this.totalPassengers = totalPassengers;
        this.simulationTime = simulationTime;
        initialize();
    }

    private void initialize(){
        arrivalTime = new ArrayList<>(totalPassengers);
        leavingTime = new ArrayList<>(totalPassengers);
        serviceEnteringTime = new ArrayList<>(totalPassengers);
        random = new Random();
        totalResponseTime = 0;
        totalWaitingTime = 0;
        totalWaitingPassengers = 0;
        totalPassengersInSystem = 0;
        totalServicedPassengers = 0;
        currentTime = 0;

        servers = new ArrayList<>();
        servers.add(new Server(serviceRate));
        servers.add(new Server(serviceRate));
        servers.add(new Server(serviceRate));
    }

}
