//POJO of an individual packet
class Packet {
    private InputPort sourcePort;
    private OutputPort destinationPort;
    private int arrivalTime;

    Packet(InputPort inputPort, OutputPort outputPort, int arrivalTime){
        this.sourcePort = inputPort;
        this.destinationPort = outputPort;
        this.arrivalTime = arrivalTime;
    }

    //Get the arrival time of the packet
    int getArrivalTime() {
        return arrivalTime;
    }

    //Get the destination port
    OutputPort getDestinationPort() {
        return destinationPort;
    }

    //Get the source port
    InputPort getSourcePort() {
        return sourcePort;
    }
}
