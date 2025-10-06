package pt.pa.transportmap.path;

/**
 * Enum type for path criteria. Match RouteInfo.
 */
public enum PathCriteria {
    DISTANCE("Distância", "Km"),
    DURATION("Duração", "Min"),
    SUSTAINABILITY("Sustentabilidade", "Carbono");

    /** Criteria designation */
    private final String text;
    /** Criteria unit */
    private final String unit;

    /**
     * Private constructor for PathCriteria
     * @param text String the designation of the criteria to present in the UI
     */
    private PathCriteria(String text, String unit) {
        this.text = text;
        this.unit = unit;
    }

    /**
     * Return the designation of the criteria for UI
     * @return String the designation of the criteria for UI
     */
    @Override
    public String toString(){return text;}

    /**
     * Return the unit of the criteria for UI
     * @return String the unit of the criteria for UI
     */
    public String getUnit() {
        return unit;
    }
}
