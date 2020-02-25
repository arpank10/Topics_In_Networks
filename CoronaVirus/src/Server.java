//Class for a server
public class Server {
    //0 IDLE, 1 BUSY
    private int state;
    //Index of passenger in arrivalTime array
    private int currentPassengerServicing = -1;
    private Double serviceRate = 0.08;
    private int totalInspectedPassengers;
    private int totalWaitingTime;
    private int totalResponseTime;
    private int lastArrivalTime;

    private Util util;

    Server(Double serviceRate, Util util){
        this.serviceRate = serviceRate;
        this.totalInspectedPassengers = 0;
        this.totalWaitingTime = 0;
        this.totalResponseTime = 0;
        this.lastArrivalTime = 0;
        this.util = util;
    }

    //A passenger is entering for inspection
    int enterService(int currentTime, int currentPassengerIndex){
        //Generate and return service time
        int serviceTime = util.generateExponentiallyDistributedValue(serviceRate);
        state = 1;
        currentPassengerServicing = currentPassengerIndex;
        lastArrivalTime = currentTime;
        return serviceTime + currentTime;
    }

    //A passenger's inspection is completed
    void depart() {
        state = 0;
        totalInspectedPassengers++;
        currentPassengerServicing = -1;
    }

    //Getter functions
    int getState() {
        return state;
    }

    int getTotalInspectedPassengers() {
        return totalInspectedPassengers;
    }

    int getCurrentPassengerServicing() {
        return currentPassengerServicing;
    }
}
