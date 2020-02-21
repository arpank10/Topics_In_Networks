import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CaseA {
    private Double arrivalRate = 0.01, serviceRate = 0.08;

    private Integer totalResponseTime, totalWaitingTime;

    private Integer totalPassengersInSystem, totalWaitingPassengers, totalServicedPassengers, currentTime;

    private Integer totalPassengers = 10000;

    private Integer simulationTime = 10000;

    private List<Integer> arrivalTime, leavingTime, serviceEnteringTime;

    private Random random;

    private Server server;

    public static void main(String[] args){
        new CaseA().simulate();
    }

    CaseA() {
        initialize();
    }

    CaseA(Double arrivalRate, Double serviceRate, Integer totalPassengers, Integer simulationTime){
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

        server = new Server(serviceRate);
    }

    private Integer generateExponentiallyDistributedValue(Double rate){
        Double value = 0.0;
        Double rand = 0.0;
        while(rand == 0.0)
            rand = random.nextDouble();
        value = -(Math.log(1 - rand))/rate;
        return value.intValue();
    }

    void simulate(){
        Integer i = new Integer(0);

        //populate all the arrival times for the passengers
        while(i<totalPassengers) {
            if (i.equals(0)) arrivalTime.add(0);
            else {
                Integer interArrivalTime = generateExponentiallyDistributedValue(arrivalRate);
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
        printResults();

    }

    private void printResults(){
        Integer totalServiced = totalPassengersInSystem - totalWaitingPassengers;


        System.out.println("Total Response Time = " + totalResponseTime);
        System.out.println("Total Waiting Time = " + totalWaitingTime);
        System.out.println("Total Waiting Passengers = " + totalWaitingPassengers);
        System.out.println("Total Passengers in System= " + totalPassengersInSystem);
        System.out.println("Total Inspected Passengers = " + totalServiced);

        Double averageWaitingTime = getAverage(totalWaitingTime, totalServicedPassengers);
        Double averageResponseTime = getAverage(totalResponseTime, totalServicedPassengers);
        Double averageWaitingPassengers = getAverage(totalWaitingPassengers, currentTime);
        Double averageTotalPassengers = getAverage(totalPassengersInSystem, currentTime);
        Double averageServicedPassenger = getAverage(totalServiced, currentTime);

        System.out.println("Average Response Time = " + averageResponseTime);
        System.out.println("Average Waiting Time = " + averageWaitingTime);
        System.out.println("Average Waiting Passengers = " + averageWaitingPassengers);
        System.out.println("Average Passengers in System = " + averageTotalPassengers);
        System.out.println("Average Inspected Passengers = " + averageServicedPassenger);
    }

    private Double getAverage(Integer value, Integer total){
        Double average = new Double(value);
        Double totalV = new Double(total);
        average = average/totalV;
        return average;
    }

}
