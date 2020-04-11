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
        if(inputBuffer.size() == bufferSize)
            return true;
        return false;
    }

    boolean isBufferEmpty(){
        if(inputBuffer.size() == 0)
            return true;
        return false;
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