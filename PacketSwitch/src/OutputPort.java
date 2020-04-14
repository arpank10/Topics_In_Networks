import java.util.ArrayList;
import java.util.List;

class OutputPort {
    private int bufferSize = Constants.DEFAULT_BUFFER_SIZE;
    private List<Packet> outputBuffer;

    OutputPort(int bufferSize) {
        this.bufferSize = bufferSize;
        outputBuffer = new ArrayList<>();
    }

    boolean isBufferFull(){
        return outputBuffer.size() == bufferSize;
    }

    boolean isBufferEmpty(){
        return outputBuffer.size() == 0;
    }

    void addToBuffer(Packet packet){
        outputBuffer.add(packet);
    }

    void removeFromBuffer(){
        if(outputBuffer.size() == 0)
            return;
        outputBuffer.remove(0);
    }

    Packet getPacketAtHead(){
        if(isBufferEmpty()) return null;
        return outputBuffer.get(0);
    }

    int getOutputBufferSize(){
        return outputBuffer.size();
    }

    List<Packet> getOutputBuffer(){
        return outputBuffer;
    }
}
