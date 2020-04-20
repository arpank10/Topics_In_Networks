import java.util.ArrayList;
import java.util.List;

//POJO of an input port
class InputPort {
    private int bufferSize = Constants.DEFAULT_BUFFER_SIZE;
    private List<Packet> inputBuffer;

    InputPort(int bufferSize){
        this.bufferSize = bufferSize;
        inputBuffer = new ArrayList<>();
    }

    //Check if buffer full
    boolean isBufferFull(){
        return inputBuffer.size() == bufferSize;
    }

    //Check if buffer empty
    boolean isBufferEmpty(){
        return inputBuffer.size() == 0;
    }

    //Add new packet to buffer
    void addToBuffer(Packet packet){
        inputBuffer.add(packet);
    }

    //Remove particular packet from buffer
    void removeFromBuffer(Packet packet){
        if(inputBuffer.size() == 0)
            return;
        inputBuffer.remove(packet);
    }

    //Get packet at any index
    Packet getPacketAtIndex(int i){
        if(i>=inputBuffer.size()) return null;
        return inputBuffer.get(i);
    }

    //Get the input buffer
    List<Packet> getInputBuffer(){
        return inputBuffer;
    }
}