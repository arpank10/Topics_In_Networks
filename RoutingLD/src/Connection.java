public class Connection {
    private int id;
    private int source;
    private int destination;
    private int bMin;
    private int bAvg;
    private int bMax;

    public Connection(int id, int source, int destination, int bMin, int bAvg, int bMax) {
        this.id = id;
        this.source = source;
        this.destination = destination;
        this.bMin = bMin;
        this.bAvg = bAvg;
        this.bMax = bMax;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSource() {
        return source;
    }

    public void setSource(int source) {
        this.source = source;
    }

    public int getDestination() {
        return destination;
    }

    public void setDestination(int destination) {
        this.destination = destination;
    }

    public int getbMin() {
        return bMin;
    }

    public void setbMin(int bMin) {
        this.bMin = bMin;
    }

    public int getbAvg() {
        return bAvg;
    }

    public void setbAvg(int bAvg) {
        this.bAvg = bAvg;
    }

    public int getbMax() {
        return bMax;
    }

    public void setbMax(int bMax) {
        this.bMax = bMax;
    }
}
