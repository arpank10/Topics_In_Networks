import java.util.Random;

public class Server {
    //0 IDLE, 1 BUSY
    int state;
    int currentPassengerServicing = -1;
    int currentTime = 0;
    Double serviceRate = 0.08;
    int totalInspectedPassengers;
    int totalWaitingTime;
    int totalResponseTime;
    int lastArrivalTime;
    Random random;

    public Server(Double serviceRate){
        this.serviceRate = serviceRate;
        this.totalInspectedPassengers = 0;
        this.totalWaitingTime = 0;
        this.totalResponseTime = 0;
        this.lastArrivalTime = 0;
        this.random = new Random();
    }
    private Integer generateExponentiallyDistributedValue(Double rate){
        Double value = 0.0;
        Double rand = 0.0;
        while(rand == 0.0)
            rand = random.nextDouble();
        value = -(Math.log(1 - rand))/rate;
        return value.intValue();
    }

    public int enterService(int currentTime, int currentPassengerIndex){
        int serviceTime = generateExponentiallyDistributedValue(serviceRate);
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
