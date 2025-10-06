package pt.pa.transportmap;

import java.io.Serial;
import java.io.Serializable;
import java.util.EnumMap;
import java.util.Objects;
import java.util.Set;

/**
 * Store the information of a route
 */
public class Route implements Serializable {
    /** The route list of transports */
    private final EnumMap<TransportType, RouteInfo> transportList;
    /** Serial version UID */
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Constructor for Route
     */
    public Route() {
        transportList = new EnumMap<>(TransportType.class);
    }

    /**
     * Get the list of transports available in the route
     * @return Set<TransportType> The list of transports available in the route
     */
    public Set<TransportType> getTransportList() {
        return transportList.keySet();
    }

    /**
     * Get the map of transports and their cost
     * @return EnumMap<TransportType, RouteInfo> The map of transports and their cost
     */
    public EnumMap<TransportType, RouteInfo> getTransportMap() { return transportList; }

    /**
     * Get the route information for a specific transport
     * @param type TransportType The type of transport
     * @return RouteInfo The route information for the specified transport
     * @throws IllegalArgumentException If the transport is not available in the route
     */
    public RouteInfo getTransportInfo(TransportType type) throws IllegalArgumentException {
        if (!hasTransport(type)) {
            throw new IllegalArgumentException("Transport not available in the route");
        }
        return transportList.get(type);
    }

    /**
     * Get the distance of the route for a specific transport
     * @param type TransportType The type of transport
     * @return double The distance of the route for the specified transport
     * @throws IllegalArgumentException If the transport is not available in the route
     */
    public double getTransportDistance(TransportType type) throws IllegalArgumentException {
        if (!hasTransport(type)) {
            throw new IllegalArgumentException("Transport not available in the route");
        }
        return transportList.get(type).getDistance();
    }

    /**
     * Get the duration of the route for a specific transport
     * @param type TransportType The type of transport
     * @return double The duration of the route for the specified transport in minutes
     * @throws IllegalArgumentException If the transport is not available in the route
     */
    public double getTransportDuration(TransportType type) throws IllegalArgumentException {
        if (!hasTransport(type)) {
            throw new IllegalArgumentException("Transport not available in the route");
        }
        return transportList.get(type).getDuration();
    }

    /**
     * Get the sustainability cost of the route for a specific transport
     * @param type TransportType The type of transport
     * @return double The sustainability cost of the route for the specified transport in carbons
     * @throws IllegalArgumentException If the transport is not available in the route
     */
    public double getTransportSustainability(TransportType type) throws IllegalArgumentException {
        if (!hasTransport(type)) {
            throw new IllegalArgumentException("Transport not available in the route");
        }
        return transportList.get(type).getSustainability();
    }

    /**
     * Return true if the route is empty (no transports available)
     * @return true if the route is empty (no transports available)
     */
    public boolean isEmpty() {
        return transportList.isEmpty();
    }

    /**
     * Add transport to the route. If the transport already exists, it will be replaced!
     * @param type TransportType The type of transport
     * @param info RouteInfo The information of the route
     * @return Route The route (self) with the added transport for method chaining
     * @throws IllegalArgumentException If type or info are null
     */
    public Route addTransport(TransportType type, RouteInfo info) throws IllegalArgumentException {
        if (type == null || info == null) {
            throw new IllegalArgumentException("TransportType and RouteInfo cannot be null");
        }
        transportList.put(type, info);
        return this;
    }

    /**
     * Add transport to the route. If the transport already exists, it will be replaced!
     * @param type TransportType The type of transport
     * @param distance double The distance of the route
     * @param duration double The duration of the route
     * @param sustainability double The sustainability cost of the route
     * @return Route The route (self) with the added transport for method chaining
     * @throws IllegalArgumentException If type is null or the other parameters are invalid (see RouteInfo constructor)
     */
    public Route addTransport(TransportType type, double distance, double duration, double sustainability) throws IllegalArgumentException {
        if (type == null) {
            throw new IllegalArgumentException("TransportType cannot be null");
        }
        transportList.put(type, new RouteInfo(distance, duration, sustainability));
        return this;
    }

    /**
     * Returns true if the route has the transport
     * @param type TransportType The type of transport
     * @return true if the route has the transport or false if the route is empty or the transport is not available
     */
    public boolean hasTransport(TransportType type) {
        if(type == null || isEmpty()) {
            return false;
        }
        return transportList.containsKey(type);
    }

    /**
     * Remove transport from the route
     * @param type TransportType The type of transport
     * @return true if the transport was removed or false if the route is empty or the transport is not available
     */
    public boolean removeTransport(TransportType type) {
        if(type == null || isEmpty() || !hasTransport(type)) {
            return false;
        }
        transportList.remove(type);
        return true;
    }

    /**
     * Set the information of the transport in the route
     * @param type TransportType The type of transport
     * @param info RouteInfo The information of the route
     * @return Route The route (self) with the updated transport information for method chaining
     * @throws IllegalArgumentException If the transport is not available in the route
     */
    public Route setTransportInfo(TransportType type, RouteInfo info) throws IllegalArgumentException {
        if(!hasTransport(type)) {
            throw new IllegalArgumentException("Transport not available in the route");
        }
        return addTransport(type, info); // put replaces value if key exists
    }

    /**
     * Set the information of the transport in the route
     * @param type TransportType The type of transport
     * @param distance The distance of the route
     * @param duration The duration of the route
     * @param sustainability The sustainability cost of the route
     * @return Route The route (self) with the updated transport information for method chaining
     * @throws IllegalArgumentException If the transport is not available in the route or the other parameters are invalid (see RouteInfo constructor)
     */
    public Route setTransportInfo(TransportType type, double distance, double duration, double sustainability) throws IllegalArgumentException {
        if(!hasTransport(type)) {
            throw new IllegalArgumentException("Transport not available in the route");
        }
        return addTransport(type, distance, duration, sustainability); // put replaces value if key exists
    }

    /**
     * Check if this Route is equal to another object
     * @param obj Object The object to compare with
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if(this == obj){ return true;}
        if(obj == null || getClass() != obj.getClass()){ return false;}
        Route route = (Route) obj;
        return Objects.equals(transportList, route.transportList);
    }

    /**
     * Generates and returns a hash code for this object.
     * @return int hash code value of this Object
     */
    @Override
    public int hashCode() {
        return Objects.hash(transportList);
    }

    /**
     * Generates and returns a string representation of the route (available transports)
     * @return String the string representation of the route (available transports)
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Transportes disponíveis: \n");
        getTransportList().forEach(
                type -> sb.append('\n').append('\t').append(type).append(": ").append('\n')
                .append(getTransportInfo(type).toString())
        );
        return sb.toString();
    }
}