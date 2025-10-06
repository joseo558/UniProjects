package pt.pa.transportmap;

import com.brunomnsilva.smartgraph.graph.*;
import pt.pa.observerpattern.Observer;
import pt.pa.transportmap.path.PathStrategy;
import pt.pa.transportmap.userconfiguration.UserConfiguration;

import java.util.*;
import java.util.stream.Collectors;

/**
 * The transport map is the MVC model entity containing the graph of stops and routes
 */
public class TransportMap implements Model {
    /** Graph of stops and routes */
    private final Graph<Stop, Route> graph;
    /** Path finding strategy */
    private PathStrategy pathStrategy;
    /** List of observers */
    private final List<Observer> observers;
    /** User configuration */
    private UserConfiguration userConfiguration;

    /**
     * Constructor for TransportMap
     * @param graph Graph<Stop, Route> the graph of stops and routes
     * @throws IllegalArgumentException if the graph is null
     */
    public TransportMap(Graph<Stop, Route> graph) throws IllegalArgumentException {
        if(graph == null) {
            throw new IllegalArgumentException("Graph cannot be null.");
        }
        this.graph = graph;
        pathStrategy = null;
        observers = new ArrayList<>();
        userConfiguration = null;
    }

    @Override
    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(Object event) {
        for (Observer observer : observers) {
            observer.update(this, event);
        }
    }

    @Override
    public void update(){
        ImportCsv.update(this);
        notifyObservers("Dados importados.");
    }

    @Override
    public PathStrategy getPathStrategy() {
        return pathStrategy;
    }

    @Override
    public void setPathStrategy(PathStrategy strategy) {
        pathStrategy = strategy;
    }

    @Override
    public UserConfiguration getUserConfiguration() {
        return userConfiguration;
    }

    /**
     * Load the user configuration from file
     */
    public void loadUserConfiguration() {
        userConfiguration = UserConfiguration.loadFromFile(this);
    }

    @Override
    public int getNumberOfStops() {
        return graph.numVertices();
    }

    @Override
    public int getNumberOfIsolatedStops() {
        int count = 0;
        for (Vertex<Stop> vertex : graph.vertices()) {
            if (graph.incidentEdges(vertex).isEmpty()) {
                count++;
            }
        }
        return count;
    }

    @Override
    public int getNumberOfNotIsolatedStops() {
        int count = 0;
        for (Vertex<Stop> vertex : graph.vertices()) {
            if (!graph.incidentEdges(vertex).isEmpty()) {
                count++;
            }
        }
        return count;
    }

    @Override
    public int getNumberOfRoutes() {
        return graph.numEdges();
    }

    @Override
    public Map<TransportType, Integer> getNumberOfRoutesByTransportType() {
        // Initialize a map with the TransportType as Key and the count as Value
        Map<TransportType, Integer> map = new EnumMap<>(TransportType.class);
        // Set counter as 0 for each type
        for (TransportType type : TransportType.values()) {
            map.put(type, 0);
        }
        // Increment the count of each type found
        for (Edge<Route, Stop> edge : graph.edges()) {
            for (TransportType type : edge.element().getTransportList()) {
                map.put(type, map.get(type) + 1);
            }
        }
        return map;
    }

    @Override
    public List<Map.Entry<Stop, Integer>> getStopCentrality() {
        Map<Stop, Integer> centralityMap = new HashMap<>();

        List<Vertex<Stop>> stopList = (List<Vertex<Stop>>) graph.vertices();
        for (Vertex<Stop> vertex : stopList) {
            try {
                centralityMap.put(vertex.element(), graph.incidentEdges(vertex).size());
            }
            catch(InvalidVertexException e){
                // continue;
            }
        }

        List<Map.Entry<Stop, Integer>> centralityList = new ArrayList<>(centralityMap.entrySet());
        centralityList.sort(
                (Map.Entry<Stop, Integer> m1, Map.Entry<Stop, Integer> m2) ->
                        m2.getValue() - m1.getValue() == 0 ?
                                m1.getKey().getName().compareToIgnoreCase(m2.getKey().getName())
                                : m2.getValue() - m1.getValue()
        );
        return centralityList;
    }

    @Override
    public Collection<Vertex<Stop>> vertices() {
        return graph.vertices();
    }

    @Override
    public Vertex<Stop> getVertex(Stop stop) {
        if(stop == null) { return null; }
        for (Vertex<Stop> vertex : graph.vertices()) {
            if (vertex.element().equals(stop)) {
                return vertex;
            }
        }
        return null;
    }

    @Override
    public Vertex<Stop> getVertex(String code) {
        try{
            return getVertex(new Stop(code, "temp", 0, 0));
        }
        catch (IllegalArgumentException e){
            return null;
        }
    }

    @Override
    public Collection<Stop> getStops() {
        return graph.vertices().stream().map(Vertex::element).collect(Collectors.toSet());
    }

    @Override
    public boolean hasStop(Stop stop) {
        if(stop == null) { return false; }
        return getVertex(stop) != null;
    }

    @Override
    public boolean hasStop(String code) {
        try{
            return hasStop(new Stop(code, "temp", 0, 0));
        }
        catch (IllegalArgumentException e){
            return false;
        }
    }

    @Override
    public Collection<Edge<Route, Stop>> edges() {
        return graph.edges();
    }

    @Override
    public Collection<Route> getRoutes() {
        return graph.edges().stream().map(Edge::element).collect(Collectors.toSet());
    }

    @Override
    public boolean areAdjacent(String firstStopCode, String secondStopCode) {
        try{
            Stop firstStop = new Stop(firstStopCode, "temp", 0, 0);
            Stop secondStop = new Stop(secondStopCode, "temp", 0, 0);
            return areAdjacent(firstStop, secondStop);
        }
        catch (IllegalArgumentException e){
            return false;
        }
    }

    @Override
    public boolean areAdjacent(Stop firstStop, Stop secondStop) {
        Vertex<Stop> vertex1 = getVertex(firstStop);
        Vertex<Stop> vertex2 = getVertex(secondStop);
        if(vertex1 == null || vertex2 == null) { return false; }
        return areAdjacent(vertex1, vertex2);
    }

    @Override
    public Collection<Edge<Route, Stop>> incidentEdges(Vertex<Stop> v) throws InvalidVertexException {
        return graph.incidentEdges(v);
    }

    @Override
    public Edge<Route, Stop> getEdge(Vertex<Stop> vertex1, Vertex<Stop> vertex2) throws InvalidVertexException {
        if(vertex1 == null || vertex2 == null) {
            throw new InvalidVertexException("Vertex cannot be null.");
        }
        Collection<Vertex<Stop>> vertices = graph.vertices();
        if (!vertices.contains(vertex1) || !vertices.contains(vertex2)) {
            throw new InvalidVertexException("Vertex not in graph.");
        }
        for (Edge<Route, Stop> edge : graph.incidentEdges(vertex1)) {
            if (graph.opposite(vertex1, edge).equals(vertex2)) {
                return edge;
            }
        }
        return null;
    }

    @Override
    public boolean areNotAdjacent(Vertex<Stop> vertex1, Vertex<Stop> vertex2) throws InvalidVertexException {
        return !areAdjacent(vertex1, vertex2);
    }

    @Override
    public boolean areAdjacent(Vertex<Stop> vertex1, Vertex<Stop> vertex2) throws InvalidVertexException {
        try {
            return graph.areAdjacent(vertex1, vertex2);
        } catch (InvalidVertexException e) {
            return false;
        }
    }

    @Override
    public Vertex<Stop> opposite(Vertex<Stop> v, Edge<Route, Stop> e) throws InvalidVertexException, InvalidEdgeException {
        return graph.opposite(v, e);
    }

    @Override
    public Vertex<Stop> insertVertex(Stop vElement) {
        if(vElement == null) { return null; }
        try {
            return graph.insertVertex(vElement);
        } catch (InvalidVertexException e) {
            return getVertex(vElement);
        }
    }

    @Override
    public Edge<Route, Stop> insertEdge(Stop vElement1, Stop vElement2, Route edgeElement) throws InvalidVertexException {
        if(edgeElement == null) { return null; }
        try {
            return graph.insertEdge(vElement1, vElement2, edgeElement);
        } catch (InvalidVertexException e) {
            throw new InvalidVertexException(e.getMessage());
        } catch (InvalidEdgeException e) {
            return getEdge(getVertex(vElement1), getVertex(vElement2));
        }
    }

    @Override
    public Stop removeVertex(Vertex<Stop> v) throws InvalidVertexException {
        return graph.removeVertex(v);
    }

    @Override
    public String toString() {
        return graph.toString();
    }
}