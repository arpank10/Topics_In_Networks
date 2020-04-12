import java.util.*;

class PacketSwitch {
    private int portCount = Constants.DEFAULT_PORT_COUNT;
    private int bufferSize = Constants.DEFAULT_BUFFER_SIZE;
    private double packetGenProbability = Constants.DEFAULT_PACKET_GEN_PROBABILITY;
    private Constants.Technique technique = Constants.DEFAULT_TECHNIQUE;
    private List<InputPort> inputPorts;
    private List<OutputPort> outputPorts;
    private Util util;
    private int time;
    private int knockout = (int)(0.6 * portCount);
    private int transmittedPacketCounts;
    private int totalPacketDelay;
    private double totalProbability;


    PacketSwitch(int portCount, int bufferSize, double packetGenProbability, int knockout, Constants.Technique technique, Util util){
        this.portCount = portCount;
        this.bufferSize = bufferSize;
        this.packetGenProbability = packetGenProbability;
        this.technique = technique;
        this.util = util;
        this.knockout = knockout;
        initialize();
    }

    private void initialize(){
        transmittedPacketCounts = 0;
        totalPacketDelay = 0;
        time = 0;
        totalProbability = 0.0;

        inputPorts = new ArrayList<>();
        outputPorts = new ArrayList<>();
        for(int i = 0; i< portCount; i++){
            InputPort inputPort = new InputPort(bufferSize);
            OutputPort outputPort = new OutputPort(bufferSize);
            inputPorts.add(inputPort);
            outputPorts.add(outputPort);
        }
    }

    //Function which simulates the switch for given time
    //Assumption given in question: Only one packet can be transmitted in a single time slot to each output port.
    public void simulate(int simulationTime){
        while(time <= simulationTime){
            //Phase 1: Corresponds to traffic generation.
            //Generate packets for all the input ports with given probability

            for(InputPort inputPort: inputPorts){
                //Buffer already full, cannot take a packet unless one is transmitted
                if(inputPort.isBufferFull()) continue;
                boolean shouldGeneratePacket = util.generatePacketWithProbability(packetGenProbability);
                if(shouldGeneratePacket){
                    int outputPortIndex = util.getOutputPortIndex(portCount);
                    //Create new packet
                    Packet packet = new Packet(inputPort, outputPorts.get(outputPortIndex), time);
                    //Allocate packet to input port's buffer
                    inputPort.addToBuffer(packet);
                }
            }
            //Phase 2: Corresponds to packet scheduling.
            switch (technique){
                case INQ:inqScheduling();break;
                case KOUQ:kouqScheduling();break;
                case ISLIP:islipScheduling();break;
            }
            //Phase 3: Corresponds to packet transmission.
            //Do necessary calculations here
            for(OutputPort outputPort: outputPorts){
                if(outputPort.isBufferEmpty()) continue;

                Packet packet = outputPort.getPacketAtHead();

                totalPacketDelay = packet.getTransmissionTime() - packet.getArrivalTime();
                transmittedPacketCounts++;

                outputPort.removeFromBuffer();
            }

            time++;
        }
    }

    private void inqScheduling(){
        //Creating temporary list for each Output Port
        List<List<Packet>> outputPortContention = new ArrayList<>();
        for(int i  = 0;i<portCount;i++){
            List<Packet> tempList = new ArrayList<>();
            outputPortContention.add(tempList);
         }

        for(InputPort inputPort : inputPorts){
            Packet packet = inputPort.getPacketAtIndex(0);
            if(packet!=null){
                OutputPort destinationPort = packet.getDestinationPort();
                int outputPortIndex = outputPorts.indexOf(destinationPort);
                //Adding it to that list
                outputPortContention.get(outputPortIndex).add(packet);
            }
        }

        //Choose one randomly from each output port's list to transfer
        for(int i = 0;i<portCount;i++){
            if(outputPortContention.get(i).isEmpty()) continue;
            int packetIndex = util.generatePacketIndex(outputPortContention.get(i).size());
            //Get that packet
            Packet packetSelected = outputPortContention.get(i).get(packetIndex);
            //Remove from input port's buffer and add to output port's buffer
            packetSelected.getSourcePort().removeFromBuffer(packetSelected);
            packetSelected.getDestinationPort().addToBuffer(packetSelected);
            //Set transmission time
            packetSelected.setTransmissionTime(time);
        }
    }

    private void kouqScheduling() {
        //Creating temporary list for each Output Port
        List<List<Packet>> outputPortContention = new ArrayList<>();
        for(int i  = 0;i<portCount;i++){
            List<Packet> tempList = new ArrayList<>();
            outputPortContention.add(tempList);
        }

        for(InputPort inputPort : inputPorts){
            Packet packet = inputPort.getPacketAtIndex(0);
            if(packet!=null){
                OutputPort destinationPort = packet.getDestinationPort();
                int outputPortIndex = outputPorts.indexOf(destinationPort);
                //Adding it to that list
                outputPortContention.get(outputPortIndex).add(packet);
            }
        }

        int numberOfPortsWherePacketDropped = 0;
        //For each output port if more than K packet
        for(int i = 0;i<portCount;i++){
            if(outputPortContention.get(i).isEmpty()) continue;

            //list of packets that need to be transmitted
            List<Packet> packetsToBeTransmitted = new ArrayList<>();

            //Sort the packets according to arrival time
            outputPortContention.get(i).sort(Comparator.comparingInt(Packet::getArrivalTime));

            //First min(knockout, total packets for output port) packets are to be considered
            int K = knockout < outputPortContention.get(i).size()? knockout : outputPortContention.get(i).size();
            if(knockout < outputPortContention.get(i).size())
                numberOfPortsWherePacketDropped++;

            //Randomly choose K indexes if more packets than K are in contention
            for(int j = 0;j<K;j++){
                int randomPacketIndex = util.generatePacketIndex(outputPortContention.get(i).size());
                packetsToBeTransmitted.add(outputPortContention.get(i).get(randomPacketIndex));
                outputPortContention.get(i).remove(randomPacketIndex);
            }

            //Max packets that the output port buffer can accomodate
            int maxPackets = bufferSize - outputPorts.get(i).getOutputBufferSize();

            //Remove from input port's buffer and add to output port's buffer
            int j = 0;
            for(j = 0;j<Math.min(maxPackets, K);j++){
                Packet p = packetsToBeTransmitted.get(j);
                p.getSourcePort().removeFromBuffer(p);
                p.getDestinationPort().addToBuffer(p);
                p.setTransmissionTime(time);
            }
            while(j<K){
                Packet p = packetsToBeTransmitted.get(j);
                p.getSourcePort().removeFromBuffer(p);
                j++;
            }
        }

        totalProbability+= numberOfPortsWherePacketDropped/portCount;
    }

    private void islipScheduling() {

    }

    private void generateResults() {

    }

}
