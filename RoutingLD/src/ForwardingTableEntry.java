public class ForwardingTableEntry {
    private int routerId;
    private int incomingId;
    private int inVCID;
    private int outgoingId;
    private int outVCID;

    public ForwardingTableEntry(int routerId, int incomingId, int inVCID, int outgoingId, int outVCID) {
        this.routerId = routerId;
        this.incomingId = incomingId;
        this.inVCID = inVCID;
        this.outgoingId = outgoingId;
        this.outVCID = outVCID;
    }

    public int getRouterId() {
        return routerId;
    }

    public int getIncomingId() {
        return incomingId;
    }

    public int getInVCID() {
        return inVCID;
    }

    public int getOutgoingId() {
        return outgoingId;
    }

    public int getOutVCID() {
        return outVCID;
    }
}
