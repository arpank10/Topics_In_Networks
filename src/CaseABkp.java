import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.lang.Math;

public class CaseABkp {
    Double arrivalRate = 0.01, serviceRate = 0.08;

    Double totalResponseTime, totalWaitingTime;

    Integer totalPassengersInSystem, totalWaitingPassengers;

    Integer totalPassengers = 100000;

    List<Double> arrivalTime, leavingTime, serviceEnteringTime;

    Random random;

    public static void main(String[] args){
        new CaseABkp().simulate();
    }

    public CaseABkp() {
        arrivalTime = new ArrayList<>(totalPassengers);
        leavingTime = new ArrayList<>(totalPassengers);
        serviceEnteringTime = new ArrayList<>(totalPassengers);
        random = new Random();
        totalResponseTime = new Double(0.0);
        totalWaitingTime = new Double(0.0);
        totalWaitingPassengers = new Integer(0);
        totalPassengersInSystem = new Integer(0);
    }

    private Double generateExponentiallyDistributedValue(Double rate){
        Double value = 0.0;
        Double rand = 0.0;
        while(rand == 0.0)
            rand = random.nextDouble();
        value = -(Math.log(1 - rand))/rate;
        return value;
    }

    private Double getMax(Double a, Double b){
        if(a>b) return a;
        return b;
    }

    private Integer getWaitingPassengerCount(Double latestArrivalTime){
        Integer i = leavingTime.size();
        i--;
        while(i>=0){
            if(latestArrivalTime<serviceEnteringTime.get(i))
                i--;
            else break;
        }
        return leavingTime.size() - i;
    }

    private Integer getCurrentServicedPassengerIndex(Double time){
        Integer i = serviceEnteringTime.size();
        i--;

        while(serviceEnteringTime.get(i)>time)
            i--;

        return i;
    }

    public void simulate(){

        Integer i = new Integer(0);

        //Simulate for each arriving passenger in a queue
        while(i<totalPassengers){
            if(i==0) {
                arrivalTime.add(0.0);
                serviceEnteringTime.add(0.0);
                Double serviceTime = generateExponentiallyDistributedValue(serviceRate);
                leavingTime.add(serviceEnteringTime.get(i) + serviceTime);

                totalPassengersInSystem +=1;
                totalWaitingPassengers+=0;

                totalResponseTime+= leavingTime.get(i) - arrivalTime.get(i);
                totalWaitingTime+= serviceEnteringTime.get(i) - arrivalTime.get(i);

            } else {
                Double interArrivalTime = generateExponentiallyDistributedValue(arrivalRate);
                arrivalTime.add(arrivalTime.get(i-1)+interArrivalTime);

                serviceEnteringTime.add(getMax(leavingTime.get(i-1), arrivalTime.get(i)));
                Double serviceTime = generateExponentiallyDistributedValue(serviceRate);
                leavingTime.add(serviceEnteringTime.get(i) + serviceTime);

                Integer currentServicedPassengerIndex = getCurrentServicedPassengerIndex(arrivalTime.get(i));
                System.out.print("Current service index = " + currentServicedPassengerIndex);
                System.out.println(" Current index = " + i);
                totalPassengersInSystem+= i - currentServicedPassengerIndex + 1;
                totalWaitingPassengers+= i - currentServicedPassengerIndex;

                totalResponseTime+= leavingTime.get(i) - arrivalTime.get(i);
                totalWaitingTime+= serviceEnteringTime.get(i) - arrivalTime.get(i);
            }
            i++;
        }
        printResults();
    }

    private void printResults(){
        Integer totalServiced = totalPassengersInSystem - totalWaitingPassengers;

        System.out.println("Total Response Time = " + totalResponseTime);
        System.out.println("Total Waiting Time = " + totalWaitingTime);
        System.out.println("Total Waiting Passengers = " + totalWaitingPassengers);
        System.out.println("Total Passengers in System= " + totalPassengersInSystem);
        System.out.println("Total Inspected Passengers = " + totalServiced);

        Double averageWaitingTime = getAverage(totalWaitingTime);
        Double averageResponseTime = getAverage(totalResponseTime);
        Double averageWaitingPassengers = getAverage(totalWaitingPassengers);
        Double averageTotalPassengers = getAverage(totalPassengersInSystem);
        Double averageServicedPassenger = getAverage(totalServiced);

        System.out.println("Average Response Time = " + averageResponseTime);
        System.out.println("Average Waiting Time = " + averageWaitingTime);
        System.out.println("Average Waiting Passengers = " + averageWaitingPassengers);
        System.out.println("Average Passengers in System = " + averageTotalPassengers);
        System.out.println("Average Inspected Passengers = " + averageServicedPassenger);
    }

    Double getAverage(Integer value){
        Double average = new Double(value);
        average = average/totalPassengers;
        return average;
    }

    Double getAverage(Double value){
        return value/totalPassengers;
    }
}
