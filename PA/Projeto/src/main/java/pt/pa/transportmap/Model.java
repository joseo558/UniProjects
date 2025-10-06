package pt.pa.transportmap;

import com.brunomnsilva.smartgraph.graph.Edge;
import com.brunomnsilva.smartgraph.graph.InvalidEdgeException;
import com.brunomnsilva.smartgraph.graph.InvalidVertexException;
import com.brunomnsilva.smartgraph.graph.Vertex;
import pt.pa.observerpattern.Observable;
import pt.pa.transportmap.path.PathStrategy;
import pt.pa.transportmap.userconfiguration.UserConfiguration;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * The MVC model entity based on ADT Graph, implementing Observable
 */
public interface Model extends Observable {
    /**
     * Update the model with the information from the CSV files
     */
    void update();

    /**
     * Get the path finding strategy
     * @return PathStrategy the path finding strategy
     */
    PathStrategy getPathStrategy();

    /**
     * Set the path finding strategy
     * @param strategy PathStrategy the path finding strategy
     */
    void setPathStrategy(PathStrategy strategy);

    /**
     * Get the user configuration
     * @return UserConfiguration the user configuration
     */
    UserConfiguration getUserConfiguration();

    /**
     * Return the number of stops in the model
     * @return int the number of stops in the model
     */
    int getNumberOfStops();

    /**
     * Counts the number of stops in the model that are isolated (have no connections to other stops)
     * @return int the number of isolated stops in the model
     */
    int getNumberOfIsolatedStops();

    /**
     * Counts the number of stops in the model that are connected to at least one other stop
     * @return int the count of connected stops in the model
     */
    int getNumberOfNotIsolatedStops();

    /**
     * Returns the number of routes in the model
     * @return the number of routes in the model
     */
    int getNumberOfRoutes();

    /**
     * Calculates the number of routes available for each TransportType.
     * @return Map<TransportType, Integer> a map with the TransportType as Key and number of available routes as Value
     */
    Map<TransportType, Integer> getNumberOfRoutesByTransportType();

    /**
     * Return a list of tuples <Stop, Number of adjacent Stops> sorted Desc by Number of adjacent stops and then Asc by Stop name
     * @return List<Map.Entry<Stop, Integer>> a list of tuples <Stop, Number of adjacent Stops> sorted Desc by Number of adjacent stops and then Asc by Stop name
     */
    List<Map.Entry<Stop, Integer>> getStopCentrality();

    /**
     * Get the graph vertices
     * @return Collection<Vertex<Stop>> the graph vertices
     */
    Collection<Vertex<Stop>> vertices();

    /**
     * Get the vertex with the stop
     * @param stop Stop the stop
     * @return Vertex<Stop> the vertex with the stop or null if the stop is not in the model
     */
    Vertex<Stop> getVertex(Stop stop);

    /**
     * Get the vertex with the given stop code
     * @param code String the stop code
     * @return Vertex<Stop> the vertex with the stop code or null if the stop is not in the model
     */
    Vertex<Stop> getVertex(String code);

    /**
     * Get the stops in the model
     * @return Collection<Stop> the stops in the model
     */
    Collection<Stop> getStops();

    /**
     * Check if the model has a stop
     * @param stop Stop the stop to check
     * @return true if the model has the stop, false otherwise or if the stop is null
     */
    boolean hasStop(Stop stop);

    /**
     * Check if the model has a stop with the given code
     * @param code String the code of the stop
     * @return true if the model has the stop, false otherwise or if the code is invalid
     */
    boolean hasStop(String code);

    /**
     * Get the graph edges
     * @return Collection<Edge<Route, Stop>> the graph edges
     */
    Collection<Edge<Route, Stop>> edges();

    /**
     * Get the routes in the model
     * @return Collection<Route> the routes in the model
     */
    Collection<Route> getRoutes();

    /**
     * Check if there is a route between two stops
     * @param firstStopCode String the code of the first stop
     * @param secondStopCode String the code of the second stop
     * @return true if there is a route between the stops, false otherwise or if the codes are invalid
     */
    boolean areAdjacent(String firstStopCode, String secondStopCode);

    /**
     * Check if there is a route between two stops
     * @param firstStop Stop the first stop
     * @param secondStop Stop the second stop
     * @return true if there is a route between the stops, false otherwise or if the stops are null
     */
    boolean areAdjacent(Stop firstStop, Stop secondStop);

    /**
     * Get the edges incident to a vertex
     * @param v Vertex<Stop> the vertex
     * @return Collection<Edge<Route, Stop>> the edges incident to the vertex
     * @throws InvalidVertexException if the vertex is not in the graph
     */
    Collection<Edge<Route, Stop>> incidentEdges(Vertex<Stop> v) throws InvalidVertexException;

    /**
     * Get the edge between two vertices
     * @param vertex1 Vertex<Stop> the first vertex
     * @param vertex2 Vertex<Stop> the second vertex
     * @return Edge<Route, Stop> the edge between the two vertices
     * @throws InvalidVertexException if the vertices are not in the graph
     */
    Edge<Route, Stop> getEdge(Vertex<Stop> vertex1, Vertex<Stop> vertex2) throws InvalidVertexException;

    /**
     * Check if there is no edge between two vertices
     * @param vertex1 Vertex<Stop> the first vertex
     * @param vertex2 Vertex<Stop> the second vertex
     * @return true if there is no edge between the two vertices or the vertices are null or not in the graph, false otherwise
     */
    boolean areNotAdjacent(Vertex<Stop> vertex1, Vertex<Stop> vertex2);

    /**
     * Check if there is an edge between two vertices
     * @param vertex1 Vertex<Stop> the first vertex
     * @param vertex2 Vertex<Stop> the second vertex
     * @return true if there is an edge between the two vertices, false otherwise or if the vertices are null or not in the graph
     */
    boolean areAdjacent(Vertex<Stop> vertex1, Vertex<Stop> vertex2);

    /**
     * Get the opposite vertex of a vertex in an edge
     * @param v Vertex<Stop> the vertex
     * @param e Edge<Route, Stop> the edge
     * @return Vertex<Stop> the opposite vertex or null if the vertex is not in this edge
     * @throws InvalidVertexException if the vertex is not in the graph
     * @throws InvalidEdgeException if the edge is not in the graph
     */
    Vertex<Stop> opposite(Vertex<Stop> v, Edge<Route, Stop> e) throws InvalidVertexException, InvalidEdgeException;

    /**
     * Insert a vertex in the graph
     * @param vElement Stop the stop to insert
     * @return Vertex<Stop> the vertex inserted or the existing vertex if the stop is already in the graph or null if the stop is null
     */
    Vertex<Stop> insertVertex(Stop vElement);

    /**
     * Insert a new edge between two vertices
     * @param vElement1 Stop the first vertex
     * @param vElement2 Stop the second vertex
     * @param edgeElement Route the route element
     * @return Edge<Route, Stop> the new edge or the existing edge if the edge is already in the graph or null if route is null
     * @throws InvalidVertexException if there are no vertices with these stops in the graph
     */
    Edge<Route, Stop> insertEdge(Stop vElement1, Stop vElement2, Route edgeElement) throws InvalidVertexException;

    /**
     * Remove a vertex from the graph
     * @param v Vertex<Stop> the vertex to remove
     * @return Stop the stop removed
     * @throws InvalidVertexException if the vertex is null or is not in the graph
     */
    Stop removeVertex(Vertex<Stop> v) throws InvalidVertexException;
}
