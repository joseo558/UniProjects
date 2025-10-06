package pt.pa.transportmap.path;

import com.brunomnsilva.smartgraph.graph.Vertex;
import pt.pa.transportmap.TransportMap;
import pt.pa.transportmap.TransportType;
import java.util.EnumSet;
import pt.pa.transportmap.Stop;
import com.brunomnsilva.smartgraph.graph.InvalidVertexException;

/**
 * Define a strategy for finding a path in the transport map
 */
public interface PathStrategy {
    /**
     * Find the path between two vertices that minimizes the cost
     * @param transportList EnumSet<TransportType> A list of transport types that can be used in the path
     * @param origin Vertex<Stop> The origin stop
     * @param destination Vertex<Stop> The destination stop
     * @return PathResult The path found and its associated information
     * @throws IllegalArgumentException if graph, origin or destination are null or if no transports are provided
     * @throws InvalidVertexException if origin or destination are not in the graph
     */
    PathResult findLessCostPathBetweenTwoVertices(TransportMap model, EnumSet<TransportType> transportList, Vertex<Stop> origin, Vertex<Stop> destination) throws IllegalArgumentException, InvalidVertexException;

    /**
     * Find the path between two vertices that minimizes the cost, with intermediary vertices.
     * If no intermediary vertices are provided it, it calls the findLessCostPathBetweenTwoVertices method.
     * @param transportList EnumSet<TransportType> A list of transport types that can be used in the path
     * @param origin Vertex<Stop> The origin vertex
     * @param destination Vertex<Stop> The destination vertex
     * @param intermediaryVertices Vertex<Stop>[] Several intermediary vertices to include in the path
     * @return PathResult The path found and its associated information
     * @throws IllegalArgumentException if graph, origin or destination are null or if no transports are provided
     * @throws InvalidVertexException if origin, destination and intermediaryVertices are not in the graph
     */
    PathResult findLessCostPathBetweenTwoVerticesPersonalized(TransportMap model, EnumSet<TransportType> transportList, Vertex<Stop> origin, Vertex<Stop> destination, Vertex<Stop>[] intermediaryVertices) throws IllegalArgumentException, InvalidVertexException;
}
