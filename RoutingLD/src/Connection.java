//POJO for connection
//Contains all the information needed for a connection
public class Connection {
    private int id;
    private int source;
    private int destination;
    private int bMin;
    private int bAvg;
    private int bMax;
    private double bEq;
    private int pathAlloted;

    private double getMin(double a, double b){
        if(a<b) return a;
        return b;
    }
    Connection(int id, int source, int destination, int bMin, int bAvg, int bMax) {
        this.id = id;
        this.source = source;
        this.destination = destination;
        this.bMin = bMin;
        this.bAvg = bAvg;
        this.bMax = bMax;
        //Calculate and store bEquiv for the connection
        this.bEq = getMin((double)(bMax), (double)bAvg + ((double)bMax - (double)bMin)*0.25);
        this.pathAlloted = -1;
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

    public double getbEq() {
        return bEq;
    }

    public int getPathAlloted() {
        return pathAlloted;
    }

    public void setPathAlloted(int pathAlloted) {
        this.pathAlloted = pathAlloted;
    }
}
