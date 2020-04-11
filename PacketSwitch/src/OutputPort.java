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
        if(outputBuffer.size() == bufferSize)
            return true;
        return false;
    }

    boolean isBufferEmpty(){
        if(outputBuffer.size() == 0)
            return true;
        return false;
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

}
