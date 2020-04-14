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
    void simulate(int simulationTime){
        while(time < simulationTime){
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
//            System.out.println("-----------------------------GENERATION-----------------------");
//            printStatus();
            //Phase 2: Corresponds to packet scheduling.
            switch (technique){
                case INQ:inqScheduling();break;
                case KOUQ:kouqScheduling();break;
                case ISLIP:islipScheduling();break;
            }
//            System.out.println("-----------------------------SCHEDULING-----------------------");
//            printStatus();
            //Phase 3: Corresponds to packet transmission.
            //Do necessary calculations here
            for(OutputPort outputPort: outputPorts){
                if(outputPort.isBufferEmpty()) continue;

                Packet packet = outputPort.getPacketAtHead();

                totalPacketDelay+= packet.getTransmissionTime() - packet.getArrivalTime();
                transmittedPacketCounts++;

                outputPort.removeFromBuffer();
            }
//            System.out.println("-----------------------------TRANSMISSION-----------------------");
//            printStatus();
            time++;
        }
        generateResults();
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
        //Grant pointers and accept pointers
        List<Integer> grantPointers = new ArrayList<>();
        List<Integer> acceptPointers = new ArrayList<>();

        //Data structure to allocate a packet to each output port
        List<Packet> outputPortPacketAllocated = new ArrayList<>();
        for(int i = 0;i<portCount;i++){
            outputPortPacketAllocated.add(null);
            grantPointers.add(0);
            acceptPointers.add(0);
        }

        //Temporary Data structure to keep track of valid requests in each iteration.
        List<List<Packet>> requestLists = new ArrayList<>();
        for(InputPort inputPort: inputPorts){
            List<Packet> tempInputBuffer = new ArrayList<>(inputPort.getInputBuffer());
            requestLists.add(tempInputBuffer);
        }

        boolean moreIteration = true;
        int iterationNumber = 0;
        Set<Integer> alreadyAllocatedInputPorts = new HashSet<>();
        Set<Integer> alreadyAllocatedOutputPorts = new HashSet<>();

        //moreIteration is true if more packets can be transmitted in the same time slot.
        //i.e if one or more output ports are idle and a packet is bound for that.
        while(moreIteration){
            //---------------------------------GRANT PHASE------------------------------//
            //Each output port will choose a packet, whose INPUT PORT INDEX IS GREATER THAN OR EQUAL TO GRANT POINTER
            //Multiple output ports can choose packets from same input port, in that case contention is resolved
            //in the later stage
            for(int i = 0;i<portCount;i++){
                List<Packet> inputRequests = requestLists.get(i);
                //If the index of the output port number is >= GRANT POINTER, allocate it
                for(Packet p: inputRequests){
                    //Get the index of output port for the packet
                    //If null, then allocate
                    //Otherwise check if currentIndex < allocatedIndex of input ports and allot
                    int outputPortIndex = outputPorts.indexOf(p.getDestinationPort());
                    if(outputPortPacketAllocated.get(outputPortIndex) == null){
                        outputPortPacketAllocated.set(outputPortIndex, p);
                    }
                    else if(i >= grantPointers.get(outputPortIndex)){
                        int allocatedInputIndex = inputPorts.indexOf(outputPortPacketAllocated.get(outputPortIndex).getSourcePort());
                        if(i < allocatedInputIndex)
                            outputPortPacketAllocated.set(outputPortIndex, p);
                    }
                }
            }

            //---------------------------------ACCEPT PHASE------------------------------//
            //Each input port may have multiple outstanding requests from o/p port
            //Choose the output port with the LEAST INDEX GREATER THAN OR EQUAL TO THE ACCEPT POINTER
            for(int i = 0;i<portCount;i++){
                //Get the inputPortIndex
                //For each input port allocate the first pkt with output port index >= accept pointer
                Packet p = outputPortPacketAllocated.get(i);
                int inputPortIndex = inputPorts.indexOf(p.getSourcePort());
                if(i<acceptPointers.get(inputPortIndex) || alreadyAllocatedInputPorts.contains(inputPortIndex)){
                    outputPortPacketAllocated.set(i, null);
                } else {
                    alreadyAllocatedInputPorts.add(inputPortIndex);
                    //Updating the accept pointer(only first iteration) since here we are finally allocating to a input port
                    //a packet whose output port index is >= accept pointer
                    if(iterationNumber == 0)
                        acceptPointers.set(inputPortIndex, (i+1)%portCount);
                }
            }
            //Updating the grant pointers(only first iteration) after all the contentions have been resolved
            //Finding the alreadyAllocatedOutputPorts
            for(int i = 0;i<portCount;i++){
                Packet p = outputPortPacketAllocated.get(i);
                if(p == null) continue;
                int inputIndex = inputPorts.indexOf(p.getSourcePort());
                if(iterationNumber == 0)
                    grantPointers.set(i, (inputIndex+1)%portCount);
                alreadyAllocatedOutputPorts.add(i);
            }
            //---------------------------------REQUEST PHASE------------------------------//
            //Removing already allocated input and output port requests
            moreIteration = false;
            for(int i = 0;i<portCount;i++){
                if(alreadyAllocatedInputPorts.contains(i)){
                    requestLists.get(i).clear();
                }
                for(Packet p : requestLists.get(i)){
                    int outputPortIndex = outputPorts.indexOf(p.getDestinationPort());
                    if(alreadyAllocatedOutputPorts.contains(outputPortIndex)){
                        requestLists.get(i).remove(p);
                    }
                }
                if(requestLists.get(i).size()>0)
                    moreIteration = true;
            }
            iterationNumber++;
        }
        //Add the allocated packets in that round to output buffer, and remove packet from input buffer.
        for(int i = 0;i<portCount;i++){
            Packet p = outputPortPacketAllocated.get(i);
            if(p == null) continue;
            p.getSourcePort().removeFromBuffer(p);
            p.getDestinationPort().addToBuffer(p);
        }
    }

    private void generateResults() {
        util.printResults(technique, totalProbability, totalPacketDelay,  time);
    }

    private void printStatus(){
        int i = 1;
        for(InputPort inputPort: inputPorts){
            System.out.println("Input Port: " + String.valueOf(i));
            for(Packet p : inputPort.getInputBuffer()){
                System.out.print(outputPorts.indexOf(p.getDestinationPort()) + " ");
            }
            System.out.println();
            i++;
        }
        i = 1;
        for(OutputPort outputPort: outputPorts){
            System.out.println("Output Port: " + String.valueOf(i));
            for(Packet p : outputPort.getOutputBuffer()){
                System.out.print(inputPorts.indexOf(p.getSourcePort()) + " ");
            }
            System.out.println();
            i++;
        }
    }

}
