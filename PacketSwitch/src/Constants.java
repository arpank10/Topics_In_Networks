public class Constants {
    public static final int DEFAULT_PORT_COUNT = 8;
    public static final int DEFAULT_BUFFER_SIZE = 4;
    public static final double DEFAULT_PACKET_GEN_PROBABILITY = 0.5;
    public static final Technique DEFAULT_TECHNIQUE = Technique.INQ;
    public static final int DEFAULT_SIMULATION_TIME = 10000;


    public enum Technique{
        INQ,
        KOUQ,
        ISLIP
    }
}
