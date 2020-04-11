class Packet {
    private InputPort sourcePort;
    private OutputPort destinationPort;
    private int arrivalTime;
    private int transmissionTime;

    Packet(InputPort inputPort, OutputPort outputPort, int arrivalTime){
        this.sourcePort = inputPort;
        this.destinationPort = outputPort;
        this.arrivalTime = arrivalTime;
    }


    public int getTransmissionTime() {
        return transmissionTime;
    }

    public void setTransmissionTime(int transmissionTime) {
        this.transmissionTime = transmissionTime;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public OutputPort getDestinationPort() {
        return destinationPort;
    }

    public InputPort getSourcePort() {
        return sourcePort;
    }
}
