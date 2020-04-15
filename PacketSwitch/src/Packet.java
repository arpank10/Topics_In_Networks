class Packet {
    private InputPort sourcePort;
    private OutputPort destinationPort;
    private int arrivalTime;

    Packet(InputPort inputPort, OutputPort outputPort, int arrivalTime){
        this.sourcePort = inputPort;
        this.destinationPort = outputPort;
        this.arrivalTime = arrivalTime;
    }

    int getArrivalTime() {
        return arrivalTime;
    }

    OutputPort getDestinationPort() {
        return destinationPort;
    }

    InputPort getSourcePort() {
        return sourcePort;
    }
}
