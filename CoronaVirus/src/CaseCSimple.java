import java.util.*;

//In this we have simulated 1 M/M/1/10 queue where lambda = initial_lamdba*0.33, and packets are dropped if queue is full
public class CaseCSimple {
    private static final int QUEUE_SIZE = 10;

    private Double normalizedDom = 1.0;

    private Double arrivalRate = 0.04, serviceRate = 0.05;

    private Integer totalResponseTime, totalWaitingTime;

    private Integer totalPassengersInSystem, totalWaitingPassengers, totalServicedPassengers, currentTime;

    private Integer totalPassengers = 1000000;

    private Integer simulationTime = 10000;

    private List<Integer> arrivalTime, leavingTime, serviceEnteringTime;

    private Random random;

    private List<Server> servers;

    private Util util;

    private List<Queue<Integer>>  queues;

    public static void main(String[] args){
        new CaseCSimple().simulate();
    }

    CaseCSimple() {
        this.util = new Util();
        initialize();
    }


    CaseCSimple(Double arrivalRate, Double serviceRate, Integer totalPassengers, Integer simulationTime, Util util){
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
        normalizedDom = util.normalizeValue(arrivalRate,serviceRate);
        this.arrivalRate = this.arrivalRate/normalizedDom;
        this.serviceRate = this.serviceRate/normalizedDom;


        servers = new ArrayList<>();
        servers.add(new Server(serviceRate, util));

        queues = new ArrayList<>();
        queues.add(new LinkedList<>());


        for(int i = 0;i<totalPassengers;i++){
            leavingTime.add(0);
            serviceEnteringTime.add(0);
        }
    }

    //Select appropriate queue for the passenger, if all of three queues are full drop the passenger
    private Queue<Integer> selectAppropriateQueue(){
        List<Queue<Integer>> freeQueues = new ArrayList<>();

        for (Queue<Integer> queue: queues) {
            if(queue.size()<QUEUE_SIZE)
                freeQueues.add(queue);
        }
        if(freeQueues.size() == 0) return null;
        if(freeQueues.size() == 1) return freeQueues.get(0);
        int randomIndex = random.nextInt(freeQueues.size() - 1);
        return freeQueues.get(randomIndex);
    }

    void simulate(){
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
                currentPassengersInSystem++;
                //Select a queue for the passenger
                Queue<Integer> allotedQueue = selectAppropriateQueue();
                if(allotedQueue!=null){
                    allotedQueue.add(tailQ);
                }
                //Drop
                tailQ++;
            }


            //Check if head of each queue can be serviced at this point of time
            for(int k = 0;k<queues.size();k++){
                Queue<Integer> queue = queues.get(k);
                Server server = servers.get(k);
                if(server.getState() == 1 || queue.size() == 0) continue;

                int currentHead = queue.peek();
                int departTime = server.enterService(currentTime, currentHead);
                leavingTime.set(currentHead, departTime);
                serviceEnteringTime.set(currentHead, currentTime);

                currentServicingPassengers++;
                queue.poll();
            }

            //Check each and every server if it can be relieved of service
            for (Server currentServer : servers) {
                if (currentServer.getState() == 1) {
                    int indexOfServicingPassenger = currentServer.getCurrentPassengerServicing();
                    if (indexOfServicingPassenger == -1) continue;
                    if (leavingTime.get(indexOfServicingPassenger).equals(currentTime)) {
                        currentServer.depart();
                        currentPassengersInSystem--;
                        currentServicingPassengers--;
                        totalResponseTime += leavingTime.get(indexOfServicingPassenger) - arrivalTime.get(indexOfServicingPassenger);
                        totalWaitingTime += serviceEnteringTime.get(indexOfServicingPassenger) - arrivalTime.get(indexOfServicingPassenger);
                    }
                }
            }
            totalPassengersInSystem+=currentPassengersInSystem;
            totalWaitingPassengers+=currentPassengersInSystem-currentServicingPassengers;
            if(currentServicingPassengers == 0 && headQ == arrivalTime.size())
                break;
            currentTime++;

        }
        for (Server server : servers) totalServicedPassengers += server.getTotalInspectedPassengers();

        util.printResults(totalServicedPassengers, totalResponseTime, totalWaitingTime,
                totalWaitingPassengers, totalPassengersInSystem, currentTime, normalizedDom);
    }




}
