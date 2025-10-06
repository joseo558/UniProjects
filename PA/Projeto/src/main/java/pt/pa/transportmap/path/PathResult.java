package pt.pa.transportmap.path;

import com.brunomnsilva.smartgraph.graph.Vertex;
import pt.pa.transportmap.RouteInfo;
import pt.pa.transportmap.Stop;
import pt.pa.transportmap.TransportType;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * The path found and its associated information
 */
public class PathResult {
    /** Sustainability cost of the path */
    private final double totalCost;
    /** Duration of the path */
    private final double totalDuration;
    /** Distance of the path */
    private final double totalDistance;
    /** Path */
    private final List<Vertex<Stop>> path;
    /** List of TransportType used in path */
    private final List<TransportType> transportList;
    /** List of RouteInfo for each segment in the path */
    private final List<RouteInfo> pathInfo;

    /**
     * Constructor for PathResult
     * @param totalCost double The sustainability cost of the path
     * @param totalDuration double The duration of the path
     * @param totalDistance double The distance of the path
     * @param path List<Vertex<Stop>> The path
     * @param transportList List<TransportType> The list of TransportType used in path
     * @param pathInfo List<RouteInfo> The list of RouteInfo for each segment in the path
     * @throws IllegalStateException if path or transportList are null or empty ( as in no path found )
     * @throws IllegalArgumentException if transportList is not path size -1 or if totalDuration or totalDistance are negative
     */
    public PathResult(double totalCost, double totalDuration, double totalDistance, List<Vertex<Stop>> path, List<TransportType> transportList, List<RouteInfo> pathInfo) throws IllegalStateException, IllegalArgumentException {
        if(path == null || transportList == null || path.isEmpty() || transportList.isEmpty()){
            throw new IllegalStateException("Path and transport list can't be null or empty.");
        }
        if(path.size() -1 != transportList.size()){
            throw new IllegalArgumentException("The transport list must have the path size -1.");
        }
        if(totalDistance < 0.0 || totalDuration < 0.0){
            throw new IllegalArgumentException("The path distance and duration must be positive values.");
        }
        this.totalCost = totalCost;
        this.totalDuration = totalDuration;
        this.totalDistance = totalDistance;
        this.path = path;
        this.transportList = transportList;
        this.pathInfo = pathInfo;
    }

    /**
     * Return the path total cost
     * @return double The path total cost
     */
    public double getTotalSustainability() {
        return totalCost;
    }

    /**
     * Return the path total duration
     * @return double The path total duration
     */
    public double getTotalDuration() {
        return totalDuration;
    }

    /**
     * Return the path total distance
     * @return double The path total distance
     */
    public double getTotalDistance() {
        return totalDistance;
    }

    /**
     * Return the path
     * @return List<Vertex<Stop>> The path
     */
    public List<Vertex<Stop>> getPath() {
        return path;
    }

    /**
     * Return the list of transports used in the path
     * @return List<TransportType> the list of transports used in the path
     */
    public List<TransportType> getTransportList() {
        return transportList;
    }

    /**
     * Return the list of RouteInfo for each segment in the path
     * @return List<RouteInfo> the list of RouteInfo for each segment in the path
     */
    public List<RouteInfo> getPathInfo() {
        return pathInfo;
    }

    /**
     * Get the round trip (reverse) of this path result
     * @return PathResult the round trip (reverse) of this path result
     */
    public PathResult getRoundTrip(){
        PathResult roundTrip = new PathResult(totalCost, totalDuration, totalDistance, path, transportList, pathInfo);
        Collections.reverse(roundTrip.getPath());
        Collections.reverse(roundTrip.getTransportList());
        Collections.reverse(roundTrip.getPathInfo());
        return roundTrip;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(String.format("Total Sustainability Cost: %.2f", totalCost)).append("\n")
                .append(String.format("Total Duration: %.2f", totalDuration)).append("\n")
                .append(String.format("Total Distance: %.2f", totalDistance)).append("\n")
                .append("Path: ");
        Iterator<Vertex<Stop>> pathIterator = path.iterator();
        Iterator<TransportType> transportIterator = transportList.iterator();
        while(pathIterator.hasNext()){
            sb.append(pathIterator.next().element().toString());
            if(pathIterator.hasNext()){
                sb.append(" (").append(transportIterator.next().toString()).append(") => ");
            }
        }
        return sb.append("\n").toString();
    }

    /**
     * Get the route trip of this path result
     * @return String with the route for the user to see
     */
    public String getRoute(){
        StringBuilder sb = new StringBuilder();
        Iterator<Vertex<Stop>> pathIterator = path.iterator();
        Iterator<TransportType> transportIterator = transportList.iterator();
        while(pathIterator.hasNext()){
            sb.append(pathIterator.next().element().toString());
            if(pathIterator.hasNext()){
                sb.append(" (").append(transportIterator.next().toString()).append(") => ");
            }
        }
        return sb.append("\n").toString();
    }

    /**
     * Return the details of the path as a string. The costs of the subsequent stops include the previous cost.
     * @return String the details of the path as a string
     */
    public String pathDetails(){
        int i = 1;
        StringBuilder sb = new StringBuilder("Route Details:\n\n");
        for(RouteInfo ri : pathInfo){
            sb.append("Paragem ").append(i++).append(":\n\n").append(ri.toString()).append("\n");
        }
        return sb.toString();
    }
}