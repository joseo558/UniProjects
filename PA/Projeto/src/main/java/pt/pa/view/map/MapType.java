package pt.pa.view.map;

/**
 * Enum type for the type of map
 */
public enum MapType {
    DEFAULT ("Default", "/images/map.png"),
    NIGHT ("Night", "/images/dark.png"),
    SATELLITE ("Satellite", "/images/satellite.png"),
    TERRAIN ("Terrain", "/images/terrain.png");

    /** Type designation */
    private final String text;
    /** Path to the image */
    private final String path;

    /**
     * Private constructor for MapType
     * @param text String the designation of the type to present in the UI
     */
    private MapType(String text, String path){
        this.text = text;
        this.path = path;
    }

    /**
     * Return the designation of the type for UI
     * @return String the designation of the type for UI
     */
    @Override
    public String toString(){return text;}

    /**
     * Return the path to the image
     * @return String the path to the image
     */
    public String getPath(){return path;}
}