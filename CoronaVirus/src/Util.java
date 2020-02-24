import java.util.Random;

public class Util {
    private Random random;

    Util(){
        random = new Random();
    }

    void printResults(int totalServicedPassengers, int totalResponseTime, int totalWaitingTime, int totalWaitingPassengers,
                              int totalPassengersInSystem, int currentTime){
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

    Integer generateExponentiallyDistributedValue(Double rate){
        Double value = 0.0;
        Double rand = 0.0;
        while(rand == 0.0)
            rand = random.nextDouble();
        value = -(Math.log(1 - rand))/rate;
        return value.intValue();
    }


}
