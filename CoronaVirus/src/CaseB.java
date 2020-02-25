import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

public class CaseB {
    private Double arrivalRate = 0.04, serviceRate = 0.05;

    private Integer totalResponseTime, totalWaitingTime;

    private Integer totalPassengersInSystem, totalWaitingPassengers, totalServicedPassengers, currentTime;

    private Integer totalPassengers = 1000000;

    private Integer simulationTime = 10000;

    private List<Integer> arrivalTime, leavingTime, serviceEnteringTime;

    private Random random;

    private List<Server> servers;

    private Util util;

    public static void main(String[] args){
        new CaseB().simulate();
    }

    CaseB() {
        this.util = new Util();
        initialize();
    }


    CaseB(Double arrivalRate, Double serviceRate, Integer totalPassengers, Integer simulationTime, Util util){
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
        random = new Random();
        totalResponseTime = 0;
        totalWaitingTime = 0;
        totalWaitingPassengers = 0;
        totalPassengersInSystem = 0;
        totalServicedPassengers = 0;
        currentTime = 0;

        //Normalize rates
        double normalizeDenom = util.normalizeValue(arrivalRate,serviceRate);
        this.arrivalRate = this.arrivalRate/normalizeDenom;
        this.serviceRate = this.serviceRate/normalizeDenom;


        servers = new ArrayList<>();
        servers.add(new Server(serviceRate, util));
        servers.add(new Server(serviceRate, util));
        servers.add(new Server(serviceRate, util));

    }

    private Server selectAppropriateServer(){
        List<Server> freeServers = new ArrayList<>();

        for (Server server : servers) {
            if (server.getState() == 0)
                freeServers.add(server);
        }
        if(freeServers.size() == 1) return freeServers.get(0);
        int randomIndex = random.nextInt(freeServers.size() - 1);
        return freeServers.get(randomIndex);
    }

    void simulate(){
        if(arrivalRate>=3*serviceRate) {
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
            if(currentServicingPassengers<servers.size() && headQ<tailQ && headQ < arrivalTime.size() && currentPassengersInSystem>0) {
                Server allocatedServer = selectAppropriateServer();
                int departTime = allocatedServer.enterService(currentTime, headQ);
                leavingTime.add(departTime);
                serviceEnteringTime.add(currentTime);
                currentServicingPassengers++;
                headQ++;
            }

            //Check each and every server if it can be relieved of service
            for(int j = 0;j<servers.size();j++){
                Server currentServer = servers.get(j);
                if(currentServer.getState() == 1){
                    int indexOfServicingPassenger = currentServer.getCurrentPassengerServicing();
                    if(indexOfServicingPassenger == -1) continue;
                    if(leavingTime.get(indexOfServicingPassenger).equals(currentTime)){
                        currentServer.depart();
                        currentPassengersInSystem--;
                        currentServicingPassengers--;
                        totalResponseTime+= leavingTime.get(indexOfServicingPassenger) - arrivalTime.get(indexOfServicingPassenger);
                        totalWaitingTime+= serviceEnteringTime.get(indexOfServicingPassenger) - arrivalTime.get(indexOfServicingPassenger);
                    }
                }
            }
            totalPassengersInSystem+=currentPassengersInSystem;
            totalWaitingPassengers+=currentPassengersInSystem-currentServicingPassengers;
            if(currentServicingPassengers == 0 && headQ == arrivalTime.size())
                break;
            currentTime++;

        }
        for(int j = 0;j<servers.size();j++)
            totalServicedPassengers+= servers.get(j).getTotalInspectedPassengers();

        util.printResults(totalServicedPassengers, totalResponseTime, totalWaitingTime,
                totalWaitingPassengers, totalPassengersInSystem, currentTime);
    }
}
