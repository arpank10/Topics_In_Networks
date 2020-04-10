import java.util.ArrayList;
import java.util.List;

class InputPort {
    private int bufferSize = Constants.DEFAULT_BUFFER_SIZE;
    private List<Packet> inputBuffer;

    InputPort(int bufferSize){
        this.bufferSize = bufferSize;
        inputBuffer = new ArrayList<>();
    }
}