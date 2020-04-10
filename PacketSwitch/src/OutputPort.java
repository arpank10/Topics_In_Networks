import java.util.ArrayList;
import java.util.List;

class OutputPort {
    private int bufferSize = Constants.DEFAULT_BUFFER_SIZE;
    private List<Packet> outputBuffer;

    OutputPort(int bufferSize) {
        this.bufferSize = bufferSize;
        outputBuffer = new ArrayList<>();
    }

}
