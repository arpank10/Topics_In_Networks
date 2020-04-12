import java.util.ArrayList;
import java.util.List;

class InputPort {
    private int bufferSize = Constants.DEFAULT_BUFFER_SIZE;
    private List<Packet> inputBuffer;

    InputPort(int bufferSize){
        this.bufferSize = bufferSize;
        inputBuffer = new ArrayList<>();
    }

    boolean isBufferFull(){
        return inputBuffer.size() == bufferSize;
    }

    boolean isBufferEmpty(){
        return inputBuffer.size() == 0;
    }

    void addToBuffer(Packet packet){
        inputBuffer.add(packet);
    }

    void removeFromBuffer(Packet packet){
        if(inputBuffer.size() == 0)
            return;
        inputBuffer.remove(packet);
    }

    Packet getPacketAtIndex(int i){
        if(i>=inputBuffer.size()) return null;
        return inputBuffer.get(i);
    }

    List<Packet> getInputBuffer(){
        return inputBuffer;
    }
}