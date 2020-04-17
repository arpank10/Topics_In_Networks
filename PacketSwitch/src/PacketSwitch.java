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
    private long transmittedPacketCounts;
    private long totalPacketDelay;
    private long totalSquarePacketDelay;
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

                int currentPacketDelay = time - packet.getArrivalTime();
                totalPacketDelay+= currentPacketDelay;
                totalSquarePacketDelay+= currentPacketDelay*currentPacketDelay;
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
                inputPort.removeFromBuffer(packet);
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
                p.getDestinationPort().addToBuffer(p);
            }
        }
        totalProbability+= util.getAverage(numberOfPortsWherePacketDropped, portCount);
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

            printIteration(iterationNumber, outputPortPacketAllocated);
            //---------------------------------ACCEPT PHASE------------------------------//
            //Each input port may have multiple outstanding requests from o/p port
            //Choose the output port with the LEAST INDEX GREATER THAN OR EQUAL TO THE ACCEPT POINTER
            Set<Integer> alreadyAllocatedInputPorts = new HashSet<>();
            for(int i = 0;i<portCount;i++){
                //Get the inputPortIndex
                //For each input port allocate the first pkt with output port index >= accept pointer
                Packet p = outputPortPacketAllocated.get(i);
                if(p == null) continue;
                int inputPortIndex = inputPorts.indexOf(p.getSourcePort());
                if(alreadyAllocatedInputPorts.contains(inputPortIndex)){
                    outputPortPacketAllocated.set(i, null);
                } else {
                    alreadyAllocatedInputPorts.add(inputPortIndex);
                }
            }
            printIteration(iterationNumber, outputPortPacketAllocated);
            //Updating the grant and accept pointers(only first iteration) after all the contentions have been resolved
            //Finding the alreadyAllocatedOutputPorts
            for(int i = 0;i<portCount;i++){
                Packet p = outputPortPacketAllocated.get(i);
                if(p == null) continue;
                int inputIndex = inputPorts.indexOf(p.getSourcePort());
                if(iterationNumber == 0){
                    grantPointers.set(i, (inputIndex+1)%portCount);
                    acceptPointers.set(inputIndex, (i+1)%portCount);
                }
                alreadyAllocatedOutputPorts.add(i);
            }
            //---------------------------------REQUEST PHASE------------------------------//
            //Removing already allocated input and output port requests
            moreIteration = false;
            for(int i = 0;i<portCount;i++){
                if(alreadyAllocatedInputPorts.contains(i)){
                    requestLists.get(i).clear();
                }
                Iterator<Packet> packetIterator = requestLists.get(i).iterator();

                while(packetIterator.hasNext()) {
                    Packet p = packetIterator.next();
                    int outputPortIndex = outputPorts.indexOf(p.getDestinationPort());
                    if(alreadyAllocatedOutputPorts.contains(outputPortIndex)){
                        packetIterator.remove();
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
        util.outputResults(portCount, packetGenProbability, technique, totalPacketDelay,
                totalSquarePacketDelay, transmittedPacketCounts, time, totalProbability);
    }


    private void printIteration(int iterationCount, List<Packet> outputPacket){
        System.out.println("Iteration Number: " + iterationCount);
        int i = 0;
        for(Packet p : outputPacket){
            i++;
            System.out.print("Output Port " + i + ": ");
            if(p==null) System.out.println();
            else
                System.out.println(inputPorts.indexOf(p.getSourcePort()) + 1);
        }

    }
    private void printStatus(){
        int i = 1;
        for(InputPort inputPort: inputPorts){
            System.out.println("Input Port: " + String.valueOf(i));
            for(Packet p : inputPort.getInputBuffer()){
                System.out.print(outputPorts.indexOf(p.getDestinationPort())+1 + " ");
            }
            System.out.println();
            i++;
        }
        i = 1;
        for(OutputPort outputPort: outputPorts){
            System.out.println("Output Port: " + String.valueOf(i));
            for(Packet p : outputPort.getOutputBuffer()){
                System.out.print(inputPorts.indexOf(p.getSourcePort())+1 + " ");
            }
            System.out.println();
            i++;
        }
    }

}
