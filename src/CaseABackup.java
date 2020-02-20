import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.lang.Math;

public class CaseABackup {
    Double arrivalRate = 0.01, serviceRate = 0.08;

    Integer totalResponseTime, totalWaitingTime;

    Integer totalPassengersInSystem, totalWaitingPassengers, totalServicedPassengers, currentTime;

    Integer totalPassengers = 100000;

    Integer simulationTime = 10000;

    List<Integer> arrivalTime, leavingTime, serviceEnteringTime;

    Random random;

    public static void main(String[] args){
        new CaseABackup().simulate();
    }

    public CaseABackup() {
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
    }

    private Integer generateExponentiallyDistributedValue(Double rate){
        Double value = 0.0;
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
        i = 0;
/*
        while(i<totalPassengers)
            System.out.println(arrivalTime.get(i++));
        i = 0;
*/
        Integer j = 0;
        Integer currentPassengersInSystem = 0;
        Integer currentServicingPassengers = 0;
        //Simulate for each second
        while(currentTime<simulationTime*60*60 && j<totalPassengers){
            while(i<arrivalTime.size() && arrivalTime.get(i).equals(currentTime)){
                serviceEnteringTime.add(currentPassengersInSystem == 0?currentTime:getMax(leavingTime.get(i-1)+1, currentTime));
                Integer serviceTime = generateExponentiallyDistributedValue(serviceRate);
                leavingTime.add(serviceEnteringTime.get(i)+serviceTime);
                /*System.out.print(arrivalTime.get(i));
                System.out.print(" ");
                System.out.print(serviceEnteringTime.get(i));
                System.out.print(" ");
                System.out.println(leavingTime.get(i));
                */
                currentPassengersInSystem++;
                i++;
            }
            if(j<serviceEnteringTime.size() && serviceEnteringTime.get(j).equals(currentTime)){
                currentServicingPassengers++;
            }
            while(j<leavingTime.size() && leavingTime.get(j).equals(currentTime)){
                currentPassengersInSystem--;
                currentServicingPassengers--;
                totalResponseTime+= leavingTime.get(j) - arrivalTime.get(j);
                totalWaitingTime+= serviceEnteringTime.get(j) - arrivalTime.get(j);
                j++;
            }
//            System.out.println(currentServicingPassengers);
            totalPassengersInSystem+=currentPassengersInSystem;
            totalWaitingPassengers+=currentPassengersInSystem-currentServicingPassengers;
            currentTime++;
        }
//        System.out.println(i);
        totalServicedPassengers = j;
        printResults();
    }

    private void printResults(){
        Integer totalServiced = totalPassengersInSystem - totalWaitingPassengers;
        /*System.out.println(totalServicedPassengers);
        System.out.println(currentTime);
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
