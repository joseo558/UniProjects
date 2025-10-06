package pt.pa.transportmap;

/**
 * Enum type for type of transport
 */
public enum TransportType {
    TRAIN ("Comboio"),
    BUS ("Autocarro"),
    BOAT ("Barco"),
    WALK ("Caminhada"),
    BICYCLE ("Bicicleta");

    /** Type designation */
    private final String text;

    /**
     * Private constructor for TransportType
     * @param text String the designation of the type to present in the UI
     */
    private TransportType(String text){
         this.text = text;
    }

    /**
     * Return the designation of the type for UI
     * @return String the designation of the type for UI
     */
    @Override
    public String toString(){return text;}
}
