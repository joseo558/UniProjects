package pt.pa.transportmap.path;

import com.brunomnsilva.smartgraph.graph.Edge;
import com.brunomnsilva.smartgraph.graph.InvalidVertexException;
import com.brunomnsilva.smartgraph.graph.Vertex;
import pt.pa.transportmap.path.cost.CostStrategy;
import pt.pa.transportmap.*;
import pt.pa.transportmap.userconfiguration.UserConfiguration;

import java.util.*;

/**
 * Utility methods for path strategies
 */
public class PathUtils {
    /**
     * Return the minimum transport cost for an edge
     * @param graph TransportMap the graph
     * @param transportList EnumSet<TransportType> A list of transport types that can be used in the path
     * @param edge Edge<Route, Stop> a graph edge
     * @param costStrategy CostStrategy the cost strategy
     * @return Map.Entry<TransportType, Double> the transport type with the minimum cost and that cost
     */
    public static Map.Entry<TransportType, Double> getMinimumTransportCost(TransportMap graph, EnumSet<TransportType> transportList, Edge<Route, Stop> edge, CostStrategy costStrategy) {
        TransportType minType = null;
        double minTransportCost = Double.MAX_VALUE;
        UserConfiguration userConfiguration = graph.getUserConfiguration();

        for (TransportType type : transportList) {
            if (edge.element().hasTransport(type)) {
                if(userConfiguration.isTransportTypeDisabled(edge, type)){
                    continue; // Skip transport type
                }
                double transportCost = costStrategy.getCost(edge.element().getTransportInfo(type));
                if(type == TransportType.BICYCLE && costStrategy.getPathCriteria() == PathCriteria.DURATION){
                    transportCost = userConfiguration.applyBicycleDurationScale(transportCost);
                }
                if (transportCost < minTransportCost) {
                    minType = type;
                    minTransportCost = transportCost;
                }
            }
        }
        return new AbstractMap.SimpleEntry<>(minType, minTransportCost);
    }

    /**
     * Update the other costs for a vertex w (adjacent of v/current) in the result map
     * @param vRouteInfo RouteInfo the current/v vertex route info object
     * @param wRouteInfo RouteInfo the vertex w (adjacent of v/current) route info object
     * @param edgeInfo RouteInfo the edge v-w route info object
     * @param costStrategy CostStrategy the cost strategy
     * @throws IllegalArgumentException if a parameter is null or a path criteria is not implemented
     */
    public static void setOtherCosts(RouteInfo vRouteInfo, RouteInfo wRouteInfo, RouteInfo edgeInfo, CostStrategy costStrategy) throws IllegalArgumentException {
        if(vRouteInfo == null || wRouteInfo == null || edgeInfo == null || costStrategy == null){
            throw new IllegalArgumentException("Route info objects and cost strategy must not be null.");
        }

        EnumSet<PathCriteria> otherCosts = costStrategy.getOtherCosts();

        for(PathCriteria otherCost : otherCosts){
            switch(otherCost){
                case DISTANCE:
                    if(vRouteInfo.getDistance() != Double.MAX_VALUE) {
                        wRouteInfo.setDistance(vRouteInfo.getDistance() + edgeInfo.getDistance());
                    }else{
                        wRouteInfo.setDistance(edgeInfo.getDistance());
                    }
                    break;
                case DURATION:
                    if(vRouteInfo.getDuration() != Double.MAX_VALUE) {
                        wRouteInfo.setDuration(vRouteInfo.getDuration() + edgeInfo.getDuration());
                    }else{
                        wRouteInfo.setDuration(edgeInfo.getDuration());
                    }
                    break;
                case SUSTAINABILITY:
                    if(vRouteInfo.getSustainability() != Double.MAX_VALUE) {
                        wRouteInfo.setSustainability(vRouteInfo.getSustainability() + edgeInfo.getSustainability());
                    }else{
                        wRouteInfo.setSustainability(edgeInfo.getSustainability());
                    }
                    break;
                default:
                    throw new IllegalArgumentException("Path criteria not implemented.");
            }
        }
    }

    /**
     * Validate findLessCostPathBetweenTwoVertices method parameters
     * @param transportList EnumSet<TransportType> A list of transport types that can be used in the path
     * @param origin Vertex<Stop> the origin vertex
     * @param destination Vertex<Stop> the destination vertex
     * @throws IllegalArgumentException if graph, origin or destination are null or if no transports are provided
     * @throws InvalidVertexException if origin or destination are not in the graph
     */
    public static void validateLessCostPathBetweenTwoVerticesParameters(TransportMap graph, EnumSet<TransportType> transportList, Vertex<Stop> origin, Vertex<Stop> destination) throws IllegalArgumentException, InvalidVertexException {
        if(graph == null || origin == null || destination == null){
            throw new IllegalArgumentException("Graph, origin and destination vertices must not be null");
        }
        if(transportList == null || transportList.isEmpty()){
            throw new IllegalArgumentException("No transports selected.");
        }
        if(!graph.vertices().contains(origin) || !graph.vertices().contains(destination)){
            throw new InvalidVertexException("Origin and destination vertices must be in the graph");
        }
    }

    /**
     * Validate findLessCostPathBetweenTwoVerticesPersonalizedParameters method parameters. Assumes intermediaryVertices is not null or empty (verify before calling this, and if so use the other method for path without intermediary vertices).
     * @param transportList EnumSet<TransportType> A list of transport types that can be used in the path
     * @param origin Vertex<Stop> the origin vertex
     * @param destination Vertex<Stop> the destination vertex
     * @param intermediaryVertices Vertex<Stop>[] a collection of the intermediary vertices, in order from origin to destination (exclusive)
     * @throws IllegalArgumentException if graph, origin, destination or any intermediary vertex are null or if no transports are provided or if intermediary vertices are not in correct order
     * @throws InvalidVertexException if origin, destination or any intermediary vertex are not in the graph
     */
    public static void validateLessCostPathBetweenTwoVerticesPersonalizedParameters(TransportMap graph, EnumSet<TransportType> transportList, Vertex<Stop> origin, Vertex<Stop> destination, Vertex<Stop>[] intermediaryVertices) throws IllegalArgumentException, InvalidVertexException {
        validateLessCostPathBetweenTwoVerticesParameters(graph, transportList, origin, destination);

        Vertex<Stop> u = origin; // previous

        // check u-v connection
        for(Vertex<Stop> v : intermediaryVertices){
            if(v == null){
                throw new IllegalArgumentException("Intermediary vertices must not be null.");
            }
            if(!graph.vertices().contains(v)){
                throw new InvalidVertexException("Intermediary vertices must be in the graph.");
            }

            // is u-v?
            if (graph.areNotAdjacent(u, v)) {
                throw new IllegalArgumentException("Each intermediary vertex must be the opposite of the previous one.");
            }

            // set current as the new previous for next iteration
            u = v;
        }

        // Check if the last intermediary vertex is the opposite of the destination
        if (graph.areNotAdjacent(u, destination)) {
            throw new IllegalArgumentException("The last intermediary vertex must be the opposite of the destination");
        }
    }

    /**
     * Return a collection with the graph vertices using breath first search
     * @param origin Vertex<Stop> the origin vertex
     * @return Collection<Vertex<Stop>> a collection with the graph vertices using breath first search
     * @throws IllegalArgumentException if the graph or origin vertex are null
     * @throws InvalidVertexException if the origin vertex is not in the graph
     */
    public static Collection<Vertex<Stop>> bfs(TransportMap graph, Vertex<Stop> origin) throws IllegalArgumentException, InvalidVertexException {
        if(graph == null || origin == null){
            throw new IllegalArgumentException("Graph and origin vertex must not be null");
        }
        if(!graph.vertices().contains(origin)){
            throw new InvalidVertexException("Origin vertex must be in the graph");
        }
        List<Vertex<Stop>> list = new ArrayList<>();

        Set<Vertex<Stop>> visited = new HashSet<>();
        Queue<Vertex<Stop>> queue = new ArrayDeque<>();

        visited.add(origin); // mark as visited
        queue.add(origin);
        while(!queue.isEmpty()){
            Vertex<Stop> v = queue.remove();
            list.add(v);
            for(Edge<Route, Stop> edge : graph.incidentEdges(v)){
                Vertex<Stop> w = graph.opposite(v, edge);
                if(!visited.contains(w)){
                    visited.add(w);
                    queue.add(w);
                }
            }
        }
        return list;
    }

    /**
     * Return a collection with the graph vertices that are maxRoutes apart from the origin, using breath first search
     * @param origin Vertex<Stop> the origin vertex
     * @param maxRoutes int the maximum number of routes apart
     * @return Collection<Vertex<Stop>> a collection with the graph vertices that are maxRoutes apart from the origin, using breath first search
     * @throws IllegalArgumentException if the graph or origin vertex are null
     * @throws InvalidVertexException if the origin vertex is not in the graph
     */
    public static Collection<Vertex<Stop>> bfsLimited(TransportMap graph, Vertex<Stop> origin, int maxRoutes) throws IllegalArgumentException, InvalidVertexException {
        if(graph == null || origin == null){
            throw new IllegalArgumentException("Graph and origin vertex must not be null");
        }
        if(!graph.vertices().contains(origin)){
            throw new InvalidVertexException("Origin vertex must be in the graph");
        }
        List<Vertex<Stop>> list = new LinkedList<>();

        Set<Vertex<Stop>> visited = new HashSet<>();
        Queue<Vertex<Stop>> queue = new ArrayDeque<>();

        int countRoutes = 0;

        visited.add(origin); // mark as visited
        queue.add(origin);
        while(!queue.isEmpty()){
            Vertex<Stop> v = queue.remove();
            list.add(v);
            if(countRoutes < maxRoutes) {
                for (Edge<Route, Stop> edge : graph.incidentEdges(v)) {
                    Vertex<Stop> w = graph.opposite(v, edge);
                    if (!visited.contains(w)) {
                        visited.add(w);
                        queue.add(w);
                    }
                }
                countRoutes++;
            }
        }
        list.remove(0); // remove origin
        return list;
    }

    /**
     * Return a collection with the graph vertices using depth first search
     * @param origin Vertex<Stop> the origin vertex
     * @return Collection<Vertex<Stop>> a collection with the graph vertices using depth first search
     * @throws IllegalArgumentException if the graph or origin vertex are null
     * @throws InvalidVertexException if the origin vertex is not in the graph
     */
    public static Collection<Vertex<Stop>> dfs(TransportMap graph, Vertex<Stop> origin) throws IllegalArgumentException, InvalidVertexException {
        if(graph == null || origin == null){
            throw new IllegalArgumentException("Graph and origin vertex must not be null");
        }
        if(!graph.vertices().contains(origin)){
            throw new InvalidVertexException("Origin vertex must be in the graph");
        }
        List<Vertex<Stop>> list = new ArrayList<>();

        Set<Vertex<Stop>> visited = new HashSet<>();
        Stack<Vertex<Stop>> stack = new Stack<>();

        visited.add(origin); // mark as visited
        stack.push(origin);
        while(!stack.isEmpty()){
            Vertex<Stop> v = stack.pop();
            list.add(v);
            for(Edge<Route, Stop> edge : graph.incidentEdges(v)){
                Vertex<Stop> w = graph.opposite(v, edge);
                if(!visited.contains(w)){
                    visited.add(w);
                    stack.push(w);
                }
            }
        }
        return list;
    }
}