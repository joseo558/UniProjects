package pt.pa.transportmap.path;

import com.brunomnsilva.smartgraph.graph.Vertex;
import pt.pa.transportmap.RouteInfo;
import pt.pa.transportmap.Stop;
import pt.pa.transportmap.TransportType;

/**
 * Store the costs (route info object), transport type of route and predecessor of a vertex for a path segment
 */
public class PathSegmentInfo {
    /** Cost to reach the vertex */
    private final RouteInfo routeInfo;
    /** Predecessor vertex */
    private Vertex<Stop> predecessor;
    /** TransportType used */
    private TransportType transportType;

    /**
     * Constructor for PathSegmentInfo
     * By default assigns max value to cost and predecessor as null.
     */
    public PathSegmentInfo(){
        this.routeInfo = new RouteInfo(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE);
        this.predecessor = null;
        this.transportType = null;
    }

    /**
     * Get the route info object containing the cost to reach the vertex
     * @return RouteInfo the route info object containing the cost to reach the vertex
     */
    public RouteInfo getRouteInfo(){ return routeInfo; }

    /**
     * Get the predecessor vertex
     * @return Vertex<Stop> the predecessor vertex
     */
    public Vertex<Stop> getPredecessor(){ return predecessor; }

    /**
     * Get the transport type
     * @return TransportType the transport type
     */
    public TransportType getTransportType(){ return transportType; }

    /**
     * Set the predecessor vertex
     * @param predecessor Vertex<Stop> the predecessor vertex
     */
    public void setPredecessor(Vertex<Stop> predecessor){ this.predecessor = predecessor; }

    /**
     * Set the transport type
     * @param transportType TransportType the transport type
     */
    public void setTransportType(TransportType transportType) {
        this.transportType = transportType;
    }

    @Override
    public String toString(){
        return "[ " + routeInfo + ", " + transportType + ", " + predecessor + " ]";
    }
}