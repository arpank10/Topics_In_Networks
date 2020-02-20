import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CaseB {
    Double arrivalRate = 0.01, serviceRate = 0.08;

    Integer totalResponseTime, totalWaitingTime;

    Integer totalPassengersInSystem, totalWaitingPassengers, totalServicedPassengers, currentTime;

    Integer totalPassengers = 10000;

    Integer simulationTime = 10000;

    List<Integer> arrivalTime, leavingTime, serviceEnteringTime;

    Random random;

    Server server1, server2, server3;

    public static void main(String[] args){
        new CaseB().simulate();
    }

    public CaseB() {
        arrivalTime = new ArrayList<>(totalPassengers);
        leavingTime = new ArrayList<>(totalPassengers);
        serviceEnteringTime = new ArrayList<>(totalPassengers);
        random = new Random();
        totalResponseTime = new Integer(0);
        totalWaitingTime = new Integer(0);
        totalWaitingPassengers = new Integer(0);
        totalPassengersInSystem = new Integer(0);
        totalServicedPassengers = new Integer(0);
        currentTime = new Integer(0);

        server1 = new Server(serviceRate);
        server2 = new Server(serviceRate);
        server3 = new Server(serviceRate);
    }

    private Integer generateExponentiallyDistributedValue(Double rate){
        Double value;
        Double rand = 0.0;
        while(rand == 0.0)
            rand = random.nextDouble();
        value = -(Math.log(1 - rand))/rate;
        return value.intValue();
    }

    private Integer getMax(Integer a, Integer b){
        if(a>b) return a;
        return b;
    }

    private Server selectAppropriateServer(){
        List<Integer> freeServers = new ArrayList<>();
        if(server1.getState()  == 0) freeServers.add(1);
        if(server2.getState()  == 0) freeServers.add(2);
        if(server3.getState()  == 0) freeServers.add(3);

        int randomIndex = random.nextInt(freeServers.size() - 1);
        int randomServer = freeServers.get(randomIndex);
        if(randomServer == 1) return server1;
        if(randomServer == 2) return server2;
        return server3;
    }

    public void simulate(){
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

        Integer headQ = 0, tailQ = 0;

        Integer currentPassengersInSystem = 0;
        Integer currentServicingPassengers = 0;
        //Simulate for each second
        while(currentTime<simulationTime*60*60 && headQ<=totalPassengers){
            //If currentTime is equal to some passengers arrival time, add the passenger to the queue
            while(tailQ<arrivalTime.size() && arrivalTime.get(tailQ).equals(currentTime)){
                tailQ++;
                currentPassengersInSystem++;
            }
            //If any server is idle, passenger at the head of the queue is to be inspected
            if(currentServicingPassengers< 3 && headQ < arrivalTime.size() && currentPassengersInSystem>0) {
                int departTime = server1.enterService(currentTime, headQ);
                leavingTime.add(departTime);
                serviceEnteringTime.add(currentTime);
                currentServicingPassengers++;
                headQ++;
            }

            //If current time is equal to the leavingTime of the currently serviced passenger, depart the passenger from the server
            if(headQ-1<leavingTime.size() && leavingTime.get(headQ-1).equals(currentTime)){
//                server.depart();
                currentPassengersInSystem--;
                currentServicingPassengers--;
                totalResponseTime+= leavingTime.get(headQ-1) - arrivalTime.get(headQ-1);
                totalWaitingTime+= serviceEnteringTime.get(headQ - 1) - arrivalTime.get(headQ - 1);
            }

            totalPassengersInSystem+=currentPassengersInSystem;
            totalWaitingPassengers+=currentPassengersInSystem-currentServicingPassengers;
            if(server1.getTotalInspectedPassengers() ==  totalPassengers)
                break;
            currentTime++;

        }
        totalServicedPassengers = server1.getTotalInspectedPassengers();
        printResults();

    }

    private void printResults(){
        Integer totalServiced = totalPassengersInSystem - totalWaitingPassengers;

        System.out.println(totalServicedPassengers);
        System.out.println(currentTime);

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

    Double getAverage(Integer value, Integer total){
        Double average = new Double(value);
        Double totalV = new Double(total);
        average = average/totalV;
        return average;
    }

}
