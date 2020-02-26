import java.util.Random;

public class Util {
    private Random random;

    Util(){
        random = new Random();
    }

    //Print the corresponding results
    void printResults(int totalServicedPassengers, int totalResponseTime, int totalWaitingTime, int totalWaitingPassengers,
                              int totalPassengersInSystem, int currentTime, double normalizedDom){
        Integer totalServiced = totalPassengersInSystem - totalWaitingPassengers;
/*

        System.out.println("Total Response Time = " + totalResponseTime);
        System.out.println("Total Waiting Time = " + totalWaitingTime);
        System.out.println("Total Waiting Passengers = " + totalWaitingPassengers);
        System.out.println("Total Passengers in System= " + totalPassengersInSystem);
        System.out.println("Total Inspected Passengers = " + totalServiced);
*/

        Double averageWaitingTime = getAverage(totalWaitingTime, totalServicedPassengers);
        Double averageResponseTime = getAverage(totalResponseTime, totalServicedPassengers);
        Double averageWaitingPassengers = getAverage(totalWaitingPassengers, currentTime);
        Double averageTotalPassengers = getAverage(totalPassengersInSystem, currentTime);
        Double averageServicedPassenger = getAverage(totalServiced, currentTime);

        averageResponseTime/=normalizedDom;
        averageWaitingTime/=normalizedDom;
        System.out.println("Average Response Time = " + averageResponseTime);
        System.out.println("Average Waiting Time = " + averageWaitingTime);
        System.out.println("Average Waiting Passengers = " + averageWaitingPassengers);
        System.out.println("Average Passengers in System = " + averageTotalPassengers);
        System.out.println("Average Inspected Passengers = " + averageServicedPassenger);
    }

    //Calculate average
    private Double getAverage(Integer value, Integer total){
        Double average = new Double(value);
        Double totalV = new Double(total);
        average = average/totalV;
        return average;
    }

    //Generate exponential values from cumulative distributive function
    Integer generateExponentiallyDistributedValue(Double rate){
        Double value = 0.0;
        Double rand = 0.0;
        while(rand == 0.0)
            rand = random.nextDouble();
        value = -(Math.log(1 - rand))/rate;
        return value.intValue();
    }

    double normalizeValue(Double rate1, Double rate2){
        double val = 1.0;
        double r1 = rate1;
        double r2 = rate2;
        int d1 = 0;
        int d2 = 0;
        while(r1>1) {
            d1++;
            r1/=10.0;
        }
        while(r2>1) {
            d2++;
            r2/=10.0;
        }
        d1 = d1>d2?d1:d2;
        for(int i =0;i<=d1;i++) val = val* 10.0;
        return val;
    }

}
