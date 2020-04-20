import java.util.ArrayList;
import java.util.List;

//POJO of an output buffer
class OutputPort {
    private int bufferSize = Constants.DEFAULT_BUFFER_SIZE;
    private List<Packet> outputBuffer;

    OutputPort(int bufferSize) {
        this.bufferSize = bufferSize;
        outputBuffer = new ArrayList<>();
    }

    //Check if buffer full
    boolean isBufferFull(){
        return outputBuffer.size() == bufferSize;
    }

    //Check if buffer empty
    boolean isBufferEmpty(){
        return outputBuffer.size() == 0;
    }

    //Add packet to output buffer
    void addToBuffer(Packet packet){
        outputBuffer.add(packet);
    }

    //Remove packet at the head of output buffer
    void removeFromBuffer(){
        if(outputBuffer.size() == 0)
            return;
        outputBuffer.remove(0);
    }

    //get packet at the head of output buffer
    Packet getPacketAtHead(){
        if(isBufferEmpty()) return null;
        return outputBuffer.get(0);
    }

    //get buffer size
    int getOutputBufferSize(){
        return outputBuffer.size();
    }

    //get output buffer
    List<Packet> getOutputBuffer(){
        return outputBuffer;
    }

}
