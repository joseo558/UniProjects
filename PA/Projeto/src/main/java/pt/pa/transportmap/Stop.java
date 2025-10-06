package pt.pa.transportmap;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * Represents a stop in the transport network
 */
public class Stop implements Serializable {

    /** The unique code of the stop */
    private String code;
    /** The name of the stop */
    private String name;
    /** The latitude of the stop */
    private double latitude;
    /** The longitude of the stop */
    private double longitude;
    /** Serial version UID */
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new Stop instance with the specified code, name, latitude and longitude.
     * Coordinates are initially set to null.
     *
     * @param code String the unique code of the stop
     * @param name String the name of the stop
     * @param latitude double the latitude of the stop
     * @param longitude double the longitude of the stop
     * @throws IllegalArgumentException if the stop code or name are null or empty
     */
    public Stop(String code, String name, double latitude, double longitude) throws IllegalArgumentException{
        if(code == null || code.isEmpty()) {
            throw new IllegalArgumentException("Stop code must not be null or empty.");
        }
        if(name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Stop name must not be null or empty.");
        }
        this.code = code;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // Getters
    /**
     * Returns the unique code of the stop
     *
     * @return String the stop unique code
     */
    public String getCode() {
        return code;
    }

    /**
     * Returns the name of the stop
     *
     * @return String the stop name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the latitude of the stop
     *
     * @return double the stop latitude
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Returns the longitude of the stop
     *
     * @return double the stop longitude
     */
    public double getLongitude() {
        return longitude;
    }


    // Setters
    /**
     * Sets the stop code
     *
     * @param code String the stop name to set
     * @throws IllegalArgumentException if the stop name is null or empty
     */
    public void setCode(String code) throws IllegalArgumentException {
        if(code == null || code.isEmpty()) {
            throw new IllegalArgumentException("Stop code must not be null or empty.");
        }
        this.code = code;
    }

    /**
     * Sets the stop name
     *
     * @param name String the stop name to set
     * @throws IllegalArgumentException if the stop name is null or empty
     */
    public void setName(String name) throws IllegalArgumentException {
        if(name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Stop name must not be null or empty.");
        }
        this.name = name;
    }

    /**
     * Sets the stop latitude
     *
     * @param latitude double the stop latitude to set
     */
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    /**
     * Sets the stop longitude
     *
     * @param longitude double the stop longitude to set
     */
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    /**
     * Checks if some other Object is the same as this one.
     *
     * @param o Object the other Object
     * @return true if the Object is the same as this one (same stop code), false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) { return true;}
        if (o == null || getClass() != o.getClass()) { return false;}

        Stop stop = (Stop) o;
        return Objects.equals(code, stop.getCode()); // if null, returns false
    }

    /**
     * Generates and returns a hash code for this object.
     * @return int hash code value of this Object
     */
    @Override
    public int hashCode() {
        return Objects.hash(code); // code is unique
    }

    /**
     * Returns a string representation of the object (name, latitude and longitude)
     * @return String the string representation of the object
     */
    public String toStringAll() {
        return new StringBuilder(name)
                .append(":").append('\n')
                .append("Latitude: ").append(latitude).append('\n')
                .append("Longitude: ").append(longitude)
                .toString();
    }

    @Override
    public String toString() {
        return name;
    }
}
