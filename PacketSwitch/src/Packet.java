class Packet {
    InputPort sourcePort;
    OutputPort destinationPort;

    Packet(InputPort inputPort, OutputPort outputPort){
        this.sourcePort = inputPort;
        this.destinationPort = outputPort;
    }


}
