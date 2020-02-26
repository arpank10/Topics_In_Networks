import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CaseA {
    //Default values
    private Double arrivalRate = 0.01, serviceRate = 0.08;

    private Double normalizedDom = 1.0;
    private Integer totalResponseTime, totalWaitingTime;

    private Integer totalPassengersInSystem, totalWaitingPassengers, totalServicedPassengers, currentTime;

    //Default values
    private Integer totalPassengers = 100000;

    private Integer simulationTime = 10000;

    private List<Integer> arrivalTime, leavingTime, serviceEnteringTime;

    private Server server;

    private Util util;

    public static void main(String[] args){
        new CaseA().simulate();
    }

    CaseA() {
        initialize();
    }

    CaseA(Double arrivalRate, Double serviceRate, Integer totalPassengers, Integer simulationTime, Util util){
        this.arrivalRate = arrivalRate;
        this.serviceRate = serviceRate;
        this.totalPassengers = totalPassengers;
        this.simulationTime = simulationTime;
        this.util = util;
        initialize();
    }

    private void initialize(){
        arrivalTime = new ArrayList<>(totalPassengers);
        leavingTime = new ArrayList<>(totalPassengers);
        serviceEnteringTime = new ArrayList<>(totalPassengers);
        totalResponseTime = 0;
        totalWaitingTime = 0;
        totalWaitingPassengers = 0;
        totalPassengersInSystem = 0;
        totalServicedPassengers = 0;
        currentTime = 0;
        //Normalize rates
        normalizedDom = util.normalizeValue(arrivalRate,serviceRate);
        this.arrivalRate = this.arrivalRate/normalizedDom;
        this.serviceRate = this.serviceRate/normalizedDom;
        //Create a single server
        server = new Server(serviceRate, util);
    }

    void simulate(){
        //Exit if unstable
        if(arrivalRate>=serviceRate) {
            System.out.println("Unstable System");
            System.exit(0);
        }
        Integer i = 0;

        //populate all the arrival times for the passengers
        while(i<totalPassengers) {
            if (i.equals(0)) arrivalTime.add(0);
            else {
                Integer interArrivalTime = util.generateExponentiallyDistributedValue(arrivalRate);
                arrivalTime.add(arrivalTime.get(i - 1) + interArrivalTime);
            }
            i++;
        }

        int headQ = 0, tailQ = 0;

        Integer currentPassengersInSystem = 0;
        Integer currentServicingPassengers = 0;
        //Simulate for each second
        while(currentTime<simulationTime*60*60 && headQ<=totalPassengers){
            //If currentTime is equal to some passengers arrival time, add the passenger to the queue
            while(tailQ<arrivalTime.size() && arrivalTime.get(tailQ).equals(currentTime)){
                tailQ++;
                currentPassengersInSystem++;
            }
            //If server is idle, passenger at the head of the queue is to be inspected
            if(server.getState() == 0 && headQ < arrivalTime.size() && currentPassengersInSystem>0) {
                int departTime = server.enterService(currentTime, headQ);
                leavingTime.add(departTime);
                serviceEnteringTime.add(currentTime);
                currentServicingPassengers++;
                headQ++;
            }

            //If current time is equal to the leavingTime of the currently serviced passenger, depart the passenger from the server
            if(headQ-1<leavingTime.size() && leavingTime.get(headQ-1).equals(currentTime)){
                server.depart();
                currentPassengersInSystem--;
                currentServicingPassengers--;
                totalResponseTime+= leavingTime.get(headQ-1) - arrivalTime.get(headQ-1);
                totalWaitingTime+= serviceEnteringTime.get(headQ - 1) - arrivalTime.get(headQ - 1);
            }

            totalPassengersInSystem+=currentPassengersInSystem;
            totalWaitingPassengers+=currentPassengersInSystem-currentServicingPassengers;
            if(server.getTotalInspectedPassengers() ==  totalPassengers)
                break;
            currentTime++;

        }
        totalServicedPassengers = server.getTotalInspectedPassengers();
        util.printResults(totalServicedPassengers, totalResponseTime, totalWaitingTime,
                totalWaitingPassengers, totalPassengersInSystem, currentTime, normalizedDom);

    }

}
