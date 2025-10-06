package pt.pa.transportmap.userconfiguration;

import com.brunomnsilva.smartgraph.graph.Edge;
import pt.pa.transportmap.Route;
import pt.pa.transportmap.Stop;
import pt.pa.transportmap.TransportMap;
import pt.pa.transportmap.TransportType;

import java.io.*;
import java.util.*;

/**
 * User configuration for the transport map
 */
public class UserConfiguration implements Serializable {
    /** Serial version UID */
    @Serial
    private static final long serialVersionUID = 1L;
    /** User configurations file name */
    private static final String CONFIGURATION_FILE = "userConfiguration.ser";
    /** Minimum bicycle duration scale */
    private static final double MIN_BICYCLE_DURATION_SCALE = 0.25;
    /** Maximum bicycle duration scale */
    private static final double MAX_BICYCLE_DURATION_SCALE = 2.0;
    /** Duration scale for bicycle */
    private double bicycleDurationScale;
    /** Collection of disabled routes */
    transient private Set<Edge<Route, Stop>> disabledRoutes;
    /** Disabled transport types in user selected routes */
    transient private Map<Edge<Route, Stop>, EnumSet<TransportType>> disabledTransportTypes;
    /** The edge converter */
    public static EdgeConverter edgeConverter;

    /**
     * Constructor for UserConfiguration
     * @param edgeConverter EdgeConverter the edge converter
     */
    public UserConfiguration(EdgeConverter edgeConverter) {
        disabledRoutes = new HashSet<>();
        disabledTransportTypes = new HashMap<>();
        bicycleDurationScale = 1.0;
        UserConfiguration.edgeConverter = edgeConverter;
    }

    /**
     * Return the bicycle duration scale
     * @return double the bicycle duration scale
     */
    public double getBicycleDurationScale() {
        return bicycleDurationScale;
    }

    /**
     * Set the bicycle duration scale
     * @param bicycleDurationScale double the bicycle duration scale
     */
    public void setBicycleDurationScale(double bicycleDurationScale) {
        if(bicycleDurationScale < MIN_BICYCLE_DURATION_SCALE || bicycleDurationScale > MAX_BICYCLE_DURATION_SCALE) {
            throw new IllegalArgumentException("Bicycle duration scale must be between " + MIN_BICYCLE_DURATION_SCALE + " and " + MAX_BICYCLE_DURATION_SCALE + ".");
        }
        this.bicycleDurationScale = bicycleDurationScale;
    }

    /**
     * Apply the bicycle duration scale to a duration
     * @param duration double the duration to apply the scale
     * @return double the duration with the scale applied
     */
    public double applyBicycleDurationScale(double duration) {
        if(duration <= 0) {
            throw new IllegalArgumentException("Duration must be positive.");
        }
        return duration * bicycleDurationScale;
    }

    /**
     * Add a disabled route to the configuration
     * @param route Edge<Route, Stop> the route to disable
     * @throws IllegalArgumentException if route is null
     */
    public void addDisabledRoute(Edge<Route, Stop> route) throws IllegalArgumentException {
        if(route == null){
            throw new IllegalArgumentException("Route cannot be null.");
        }
        disabledRoutes.add(route);
    }

    /**
     * Remove a disabled route from the configuration
     * @param route Edge<Route, Stop> the route to enable
     * @throws IllegalArgumentException if route is null
     */
    public void removeDisabledRoute(Edge<Route, Stop> route) throws IllegalArgumentException {
        if(route == null){
            throw new IllegalArgumentException("Route cannot be null.");
        }
        disabledRoutes.remove(route);
    }

    /**
     * Add a disabled transport type to the configuration
     * @param route Edge<Route, Stop> the route to disable
     * @param transportType TransportType the transport type to disable
     * @throws IllegalArgumentException if route or transport type is null
     */
    public void addDisabledTransportType(Edge<Route, Stop> route, TransportType transportType) throws IllegalArgumentException {
        if(route == null || transportType == null) {
            throw new IllegalArgumentException("Route and transport type cannot be null.");
        }
        if(disabledTransportTypes.containsKey(route)) {
            disabledTransportTypes.get(route).add(transportType);
        } else {
            EnumSet<TransportType> transportTypes = EnumSet.noneOf(TransportType.class);
            transportTypes.add(transportType);
            disabledTransportTypes.put(route, transportTypes);
        }
    }

    /**
     * Remove a disabled transport type from the configuration
     * @param route Edge<Route, Stop> the route to enable
     * @param transportType TransportType the transport type to enable
     * @throws IllegalArgumentException if route or transport type is null
     */
    public void removeDisabledTransportType(Edge<Route, Stop> route, TransportType transportType) {
        if(route == null || transportType == null) {
            throw new IllegalArgumentException("Route and transport type cannot be null.");
        }
        if(disabledTransportTypes.containsKey(route)) {
            EnumSet<TransportType> types = disabledTransportTypes.get(route);
            types.remove(transportType);
            if(types.isEmpty()) {
                disabledTransportTypes.remove(route);
            }
        }
    }

    /**
     * Return the disabled transport types as a string
     * @return String the disabled transport types
     */
    public String getDisabledRoutes() {
        List<Edge<Route, Stop>> disabledRoutesSorted = new ArrayList<>(disabledRoutes);
        Collections.sort(disabledRoutesSorted, Comparator.comparing(Edge::toString));
        StringBuilder sb = new StringBuilder();
        for(Edge<Route, Stop> edge : disabledRoutesSorted) {
            sb.append(edge.vertices()[0]).append(" -> ").append(edge.vertices()[1]).append("\n");
        }
        if(!sb.isEmpty()){
            sb.deleteCharAt(sb.length() - 1); // remove last \n
        }
        return sb.toString();
    }

    /**
     * Return the disabled transport types as a string
     * @return String the disabled transport types
     */
    public String getDisabledTransportTypes() {
        TreeMap<Edge<Route, Stop>, EnumSet<TransportType>> disabledTransportTypesSorted = new TreeMap<>(Comparator.comparing(Edge::toString));
        disabledTransportTypesSorted.putAll(disabledTransportTypes);
        StringBuilder sb = new StringBuilder();
        List<TransportType> transportTypesSorted;
        for(Map.Entry<Edge<Route, Stop>, EnumSet<TransportType>> entry : disabledTransportTypesSorted.entrySet()) {
            sb.append(entry.getKey().vertices()[0]).append(" -> ").append(entry.getKey().vertices()[1]).append(" :");
            transportTypesSorted = new ArrayList<>(entry.getValue());
            Collections.sort(transportTypesSorted, Comparator.comparing(Enum::toString));
            for(TransportType transportType : transportTypesSorted) {
                sb.append(" ").append(transportType);
            }
            sb.append("\n");
        }
        if(!sb.isEmpty()){
            sb.deleteCharAt(sb.length() - 1); // remove last \n
        }
        return sb.toString();
    }

    /**
     * Resets both the routes and transport types that have been disabled.
     */
    public void resetDisabledRoutes() {
        disabledRoutes.clear();
        disabledTransportTypes.clear();
    }

    /**
     * Return true if a route is disabled
     * @param route Edge<Route, Stop> the route to verify
     * @return true if a route is disabled
     * @throws IllegalArgumentException if route is null
     */
    public boolean isRouteDisabled(Edge<Route, Stop> route) throws IllegalArgumentException {
        if(route == null) {
            throw new IllegalArgumentException("Route cannot be null.");
        }
        return disabledRoutes.contains(route);
    }

    /**
     * Return true if a transport type is disabled
     * @param route Edge<Route, Stop> the route to verify
     * @param transportType TransportType the transport type to verify
     * @return true if a transport type is disabled
     * @throws IllegalArgumentException if route or transport type is null
     */
    public boolean isTransportTypeDisabled(Edge<Route, Stop> route, TransportType transportType) throws IllegalArgumentException {
        if(route == null || transportType == null) {
            throw new IllegalArgumentException("Route and transport type cannot be null.");
        }
        if(disabledTransportTypes.containsKey(route)) {
            return disabledTransportTypes.get(route).contains(transportType);
        }
        return false;
    }

    /**
     * Checks if a route has one or more transport types disabled.
     * @param route Edge<Route, Stop> the route to verify
     * @return true if the route has any transport type disabled, false otherwise
     * @throws IllegalArgumentException if the route is null
     */
    public boolean routeHasDisabledTransportType(Edge<Route, Stop> route) throws IllegalArgumentException {
        if(route == null) {
            throw new IllegalArgumentException("Route cannot be null.");
        }
        return disabledTransportTypes.containsKey(route);
    }

    /**
     * Save user configuration to file
     */
    public void saveToFile() {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(CONFIGURATION_FILE));
            oos.writeObject(this);
            oos.flush();
            oos.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error saving user configuration to file.");
        }
    }

    /**
     * Load user configuration from file
     * @param model TransportMap the transport map model
     * @return UserConfiguration the user configuration loaded from file
     */
    public static UserConfiguration loadFromFile(TransportMap model) {
        UserConfiguration.edgeConverter = new EdgeConverter(model);
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(CONFIGURATION_FILE));
            UserConfiguration userConfiguration = (UserConfiguration) ois.readObject();
            ois.close();
            return userConfiguration;
        } catch (Exception e) {
            System.out.println("Error loading user configuration from file. Returning to default configuration.");
            return new UserConfiguration(edgeConverter);
        }
    }

    /**
     * Custom serialization for transient fields
     * @param oos ObjectOutputStream the object output stream
     * @throws IOException if an I/O error occurs
     */
    @Serial
    private void writeObject(ObjectOutputStream oos) throws IOException{
        oos.defaultWriteObject();

        // Serialize the disabledRoutes as SerializableEdge objects
        Set<SerializableEdge> disabledSerializableRoutes = new HashSet<>();
        for(Edge<Route, Stop> edge : disabledRoutes) {
            disabledSerializableRoutes.add(new SerializableEdge(edge));
        }
        oos.writeObject(disabledSerializableRoutes);

        // Serialize the disabledTransportTypes as SerializableEdgeTransportType objects
        Map<SerializableEdge, EnumSet<TransportType>> disabledTransportTypesSerializable = new HashMap<>();
        for(Map.Entry<Edge<Route, Stop>, EnumSet<TransportType>> entry : disabledTransportTypes.entrySet()) {
            disabledTransportTypesSerializable.put(new SerializableEdge(entry.getKey()), entry.getValue());
        }
        oos.writeObject(disabledTransportTypesSerializable);
    }

    /**
     * Custom deserialization for transient fields
     * @param ois ObjectInputStream the object input stream
     * @throws ClassNotFoundException if the class of a serialized object cannot be found
     * @throws IOException if an I/O error occurs
     */
    @Serial
    private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException{
        ois.defaultReadObject();

        if(edgeConverter == null) {
            throw new IOException("EdgeConverter must be set before deserialization.");
        }

        disabledRoutes = new HashSet<>();
        disabledTransportTypes = new HashMap<>();

        // Deserialize the disabledRoutes
        Set<SerializableEdge> disabledSerializableRoutes = (Set<SerializableEdge>) ois.readObject();
        for (SerializableEdge serializableEdge : disabledSerializableRoutes) {
            disabledRoutes.add(edgeConverter.getEdge(serializableEdge));
        }

        // Deserialize the disabledTransportTypes
        Map<SerializableEdge, EnumSet<TransportType>> disabledTransportTypesSerializable = (Map<SerializableEdge, EnumSet<TransportType>>) ois.readObject();
        for (Map.Entry<SerializableEdge, EnumSet<TransportType>> entry : disabledTransportTypesSerializable.entrySet()) {
            disabledTransportTypes.put( edgeConverter.getEdge(entry.getKey()), entry.getValue());
        }

    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("User configurations:\n");
        sb.append("Bicycle duration scale: ").append(bicycleDurationScale).append("\n");
        sb.append("Disabled routes: \n");
        sb.append(getDisabledRoutes()).append("\n");
        sb.append("Disabled transport types: \n");
        sb.append(getDisabledTransportTypes());
        return sb.toString();
    }
}