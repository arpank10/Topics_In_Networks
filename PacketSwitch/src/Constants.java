class Constants {
    static final int DEFAULT_PORT_COUNT = 8;
    static final int DEFAULT_BUFFER_SIZE = 4;
    static final int DEFAULT_KNOCKOUT = (int)(0.6 * DEFAULT_PORT_COUNT);
    static final double DEFAULT_PACKET_GEN_PROBABILITY = 0.5;
    static final Technique DEFAULT_TECHNIQUE = Technique.INQ;
    static final int DEFAULT_SIMULATION_TIME = 10000;
    static final String outputFormatDimensions = "%-10s %-10s %-20s %-50s %-50s %-50s";
    static final String outputFormat = String.format(outputFormatDimensions,"N", "p", "Queue Type",
            "Average PD", "Std Dev of PD", "Avg Link Util");

    enum Technique{
        INQ,
        KOUQ,
        ISLIP
    }
}
