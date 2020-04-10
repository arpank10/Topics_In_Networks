import java.util.ArrayList;
import java.util.List;

class PacketSwitch {
    private int switchCount = Constants.DEFAULT_PORT_COUNT;
    private int bufferSize = Constants.DEFAULT_BUFFER_SIZE;
    private double packetGenProbability = Constants.DEFAULT_PACKET_GEN_PROBABILITY;
    private Constants.Technique technique = Constants.DEFAULT_TECHNIQUE;
    private List<InputPort> inputPorts;
    private List<OutputPort> outputPorts;
    private Util util;

    PacketSwitch(int switchCount, int bufferSize, double packetGenProbability, Constants.Technique technique, Util util){
        this.switchCount = switchCount;
        this.bufferSize = bufferSize;
        this.packetGenProbability = packetGenProbability;
        this.technique = technique;
        this.util = util;
        initialize();
    }

    private void initialize(){
        inputPorts = new ArrayList<>();
        outputPorts = new ArrayList<>();
        for(int i = 0; i<switchCount;i++){
            InputPort inputPort = new InputPort(bufferSize);
            OutputPort outputPort = new OutputPort(bufferSize);
            inputPorts.add(inputPort);
            outputPorts.add(outputPort);
        }
    }

    //Function which simulates the switch for given time
    public void simulate(int simulationTime){
        int time = 0;
        while(time <= simulationTime){
            //Phase 1: Corresponds to traffic generation.

            //Phase 2: Corresponds to packet scheduling.

            //Phase 3: Corresponds to packet transmission.


            time++;
        }
    }

    private void inqScheduling(){

    }

    private void kouqScheduling() {

    }

    private void islipScheduling() {

    }

    private void generateResults() {

    }

}
