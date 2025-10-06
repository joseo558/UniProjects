package pt.pa.transportmap;

import pt.pa.transportmap.path.PathCriteria;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * Store the information of a route, for a specific transport. Match PathCriteria.
 */
public class RouteInfo implements Serializable {
    /** The route distance */
    private double distance;
    /** The route duration */
    private double duration;
    /** The route sustainability cost */
    private double sustainability; // can be negative
    /** Serial version UID */
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Constructor for RouteInfo
     * @param distance double The route distance
     * @param duration double The route duration
     * @param sustainability double The route cost
     * @throws IllegalArgumentException If distance or duration are negative
     */
    public RouteInfo(double distance, double duration, double sustainability) throws IllegalArgumentException {
        if (distance < 0.0) {
            throw new IllegalArgumentException("Distance must be a positive value.");
        }
        if (duration < 0.0) {
            throw new IllegalArgumentException("Duration must be a positive value.");
        }
        this.distance = distance;
        this.duration = duration;
        this.sustainability = sustainability;
    }

    /**
     * Get the route distance
     * @return double The route distance
     */
    public double getDistance() { return distance; }

    /**
     * Get the route duration
     * @return double The route duration
     */
    public double getDuration() { return duration; }

    /**
     * Get the route cost
     * @return double The route cost
     */
    public double getSustainability() { return sustainability; }

    /**
     * Set the route distance. Must be a positive value.
     * @param distance double The route distance
     * @return RouteInfo The RouteInfo object (self) for method chaining
     * @throws IllegalArgumentException If distance is negative
     */
    public RouteInfo setDistance(double distance){
        if (distance < 0.0) {
            throw new IllegalArgumentException("Distance must be a positive value.");
        }
        this.distance = distance;
        return this;
    }

    /**
     * Set the route duration. Must be a positive value.
     * @param duration double The route duration
     * @return RouteInfo The RouteInfo object (self) for method chaining
     * @throws IllegalArgumentException If duration is negative
     */
    public RouteInfo setDuration(double duration) throws IllegalArgumentException {
        if (duration < 0.0) {
            throw new IllegalArgumentException("Duration must be a positive value.");
        }
        this.duration = duration;
        return this;
    }

    /**
     * Set the route cost. Can be a negative value.
     * @param sustainability double The route cost
     * @return RouteInfo The RouteInfo object (self) for method chaining
     */
    public RouteInfo setSustainability(double sustainability){
        this.sustainability = sustainability;
        return this;
    }

    /**
     * Check if this RouteInfo is equal to another object
     * @param obj Object The object to compare with
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if(this == obj){ return true;}
        if(obj == null || getClass() != obj.getClass()){ return false;}
        RouteInfo routeInfo = (RouteInfo) obj;
        return Objects.equals(distance, routeInfo.distance) &&
                Objects.equals(duration, routeInfo.duration) &&
                Objects.equals(sustainability, routeInfo.sustainability);
    }

    /**
     * Generate a hash code for this RouteInfo
     * @return int The hash code for this RouteInfo
     */
    @Override
    public int hashCode() {
        return Objects.hash(distance, duration, sustainability);
    }

    /**
     * Get a string representation of this RouteInfo
     * @return String The string representation
     */
    @Override
    public String toString() {
        return new StringBuilder("Distância: ")
                .append(String.format("%.1f %s", distance, PathCriteria.DISTANCE.getUnit())) // 1 decimal place
                .append("\n").append("Duração: ")
                .append(String.format("%.0f %s", duration, PathCriteria.DURATION.getUnit()))
                .append("\n").append("Sustentabilidade: ")
                .append(String.format("%.2f %s", sustainability, PathCriteria.SUSTAINABILITY.getUnit()))
                .toString();
    }
}