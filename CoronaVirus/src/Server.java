import java.util.Random;

public class Server {
    //0 IDLE, 1 BUSY
    private int state;
    private int currentPassengerServicing = -1;
    int currentTime = 0;
    private Double serviceRate = 0.08;
    private int totalInspectedPassengers;
    private int totalWaitingTime;
    private int totalResponseTime;
    private int lastArrivalTime;

    private Util util;

    public Server(Double serviceRate, Util util){
        this.serviceRate = serviceRate;
        this.totalInspectedPassengers = 0;
        this.totalWaitingTime = 0;
        this.totalResponseTime = 0;
        this.lastArrivalTime = 0;
        this.util = util;
    }


    public int enterService(int currentTime, int currentPassengerIndex){
        int serviceTime = util.generateExponentiallyDistributedValue(serviceRate);
        state = 1;
        currentPassengerServicing = currentPassengerIndex;
        lastArrivalTime = currentTime;
        return serviceTime + currentTime;
    }

    public int getState() {
        return state;
    }

    public void depart() {
        state = 0;
        totalInspectedPassengers++;
        currentPassengerServicing = -1;
    }

    public int getTotalInspectedPassengers() {
        return totalInspectedPassengers;
    }

    public int getCurrentPassengerServicing() {
        return currentPassengerServicing;
    }
}
