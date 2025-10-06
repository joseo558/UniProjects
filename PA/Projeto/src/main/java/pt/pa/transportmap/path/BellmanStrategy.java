package pt.pa.transportmap.path;

import com.brunomnsilva.smartgraph.graph.Edge;
import com.brunomnsilva.smartgraph.graph.InvalidVertexException;
import com.brunomnsilva.smartgraph.graph.Vertex;
import pt.pa.transportmap.path.cost.CostStrategy;
import pt.pa.transportmap.*;
import pt.pa.transportmap.userconfiguration.UserConfiguration;

import java.util.*;

/**
 * Bellman class to find the less cost path in a graph with negative edge values
 */
public class BellmanStrategy implements PathStrategy {
    /** The transport map graph */
    TransportMap graph;
    /** The cost strategy to use */
    CostStrategy costStrategy;

    /**
     * Constructor for BellmanStrategy
     * @param costStrategy CostStrategy the cost strategy to use
     */
    public BellmanStrategy(CostStrategy costStrategy){
        this.costStrategy = costStrategy;
        this.graph = null;
    }

    /**
     * Find the less cost path using Bellman-Ford algorithm
     * @param transportList EnumSet<TransportType> A list of transport types that can be used in the path
     * @param origin Vertex<Stop> the origin vertex
     * @return HashMap< Vertex<Stop>, PathSegmentInfo > a map with the less cost path to each vertex
     * @throws IllegalStateException if graph contains negative-weight cycles
     */
    private HashMap<Vertex<Stop>, PathSegmentInfo> lessCostPath(EnumSet<TransportType> transportList, Vertex<Stop> origin) throws IllegalStateException {
        HashMap<Vertex<Stop>, PathSegmentInfo> result = new HashMap<>();

        // for each vertex in the graph, assign infinite cost and null predecessor
        for (Vertex<Stop> vertex : graph.vertices()) {
            result.put(vertex, new PathSegmentInfo());
        }
        // set origin cost to 0
        result.get(origin).getRouteInfo().setSustainability(0.0).setDuration(0.0).setDistance(0.0);

        UserConfiguration userConfiguration = graph.getUserConfiguration();

        // relax all edges |V| - 1 times
        int vertexCount = graph.getNumberOfStops();
        for (int i = 1; i < vertexCount; i++) {
            for (Edge<Route, Stop> edge : graph.edges()) {
                if(userConfiguration.isRouteDisabled(edge)){
                    continue; // Skip edge
                }
                relaxEdge(edge, transportList, result);
            }
        }

        // check for negative-weight cycles
        for (Edge<Route, Stop> edge : graph.edges()) {
            if(userConfiguration.isRouteDisabled(edge)){
                continue; // Skip edge
            }
            if (isRelaxationPossible(edge, transportList, result)) {
                throw new IllegalStateException("Graph contains a negative-weight cycle.");
            }
        }

        return result;
    }

    /**
     * Relax an edge if a shorter path is found
     * @param edge Edge<Route, Stop> a graph edge
     * @param transportList EnumSet<TransportType> A list of transport types that can be used in the path
     * @param result HashMap< Vertex<Stop>, PathSegmentInfo > a map with the less cost path to each vertex
     */
    private void relaxEdge(Edge<Route, Stop> edge, EnumSet<TransportType> transportList, HashMap<Vertex<Stop>, PathSegmentInfo> result) {
        // Find minimum transport cost for the edge
        Map.Entry<TransportType, Double> minimumTransportEntry = PathUtils.getMinimumTransportCost(graph, transportList, edge, costStrategy);
        TransportType minType = minimumTransportEntry.getKey();
        double minTransportCost = minimumTransportEntry.getValue();
        if (minType == null){ return; } // Skip edge if no valid transport type

        for(Vertex<Stop> u : edge.vertices()){
            Vertex<Stop> v = graph.opposite(u, edge);
            RouteInfo uRouteInfo = result.get(u).getRouteInfo();
            RouteInfo vRouteInfo = result.get(v).getRouteInfo();

            // if distance[u] + w < distance[v] then
            //      distance[v] := distance[u] + w
            //      predecessor[v] := u
            if(costStrategy.getCost(uRouteInfo) == Double.MAX_VALUE){ continue; } // skip
            double newCost = costStrategy.getCost(uRouteInfo) + minTransportCost + 1000; // fix for negative cycle in corroios-charneca

            if (newCost < costStrategy.getCost(vRouteInfo)) {
                costStrategy.setCost(vRouteInfo, newCost - 1000); // due to fix above
                PathUtils.setOtherCosts(uRouteInfo, vRouteInfo, edge.element().getTransportInfo(minType), costStrategy);
                result.get(v).setPredecessor(u);
                result.get(v).setTransportType(minType);
            }
        }
    }

    /**
     * Check if relaxation is still possible (used for negative cycle detection)
     */
    private boolean isRelaxationPossible(Edge<Route, Stop> edge, EnumSet<TransportType> transportList, HashMap<Vertex<Stop>, PathSegmentInfo> result) {
        // Find minimum transport cost for the edge
        Map.Entry<TransportType, Double> minimumTransportEntry = PathUtils.getMinimumTransportCost(graph, transportList, edge, costStrategy);
        double minTransportCost = minimumTransportEntry.getValue();
        if (minTransportCost == Double.MAX_VALUE){ return false; } // Skip edge if no valid transport type

        Vertex<Stop> u = edge.vertices()[0];
        Vertex<Stop> v = edge.vertices()[1];

        double uCost = costStrategy.getCost(result.get(u).getRouteInfo());
        double vCost = costStrategy.getCost(result.get(v).getRouteInfo());

        if (uCost != Double.MAX_VALUE && uCost + minTransportCost + 1000 < vCost) {
            return true;
        }
        if (vCost != Double.MAX_VALUE && vCost + minTransportCost + 1000 < uCost) {
            return true;
        }
        return false;
    }

    @Override
    public PathResult findLessCostPathBetweenTwoVertices(TransportMap model, EnumSet<TransportType> transportList, Vertex<Stop> origin, Vertex<Stop> destination) throws IllegalArgumentException, InvalidVertexException {
        PathUtils.validateLessCostPathBetweenTwoVerticesParameters(model, transportList, origin, destination);
        graph = model;
        // get bellman result
        HashMap<Vertex<Stop>, PathSegmentInfo> result = lessCostPath(transportList, origin);

        // Extract path and totals
        List<Vertex<Stop>> path = new LinkedList<>();
        List<TransportType> pathTransportList = new LinkedList<>();
        List<RouteInfo> pathInfo = new LinkedList<>();

        // get path
        Vertex<Stop> current = destination;
        while (current != null) {
            path.add(0, current);
            if (result.get(current).getPredecessor() != null) {
                pathTransportList.add(0, result.get(current).getTransportType());
                pathInfo.add(0, result.get(current).getRouteInfo());
            }
            current = result.get(current).getPredecessor();
        }

        // get totals
        RouteInfo destinationInfo = result.get(destination).getRouteInfo();
        return new PathResult(destinationInfo.getSustainability(),
                destinationInfo.getDuration(),
                destinationInfo.getDistance(),
                path, pathTransportList, pathInfo);
    }

    @Override
    public PathResult findLessCostPathBetweenTwoVerticesPersonalized(TransportMap model, EnumSet<TransportType> transportList, Vertex<Stop> origin, Vertex<Stop> destination, Vertex<Stop>[] intermediaryVertices) throws IllegalArgumentException, InvalidVertexException {
        if(intermediaryVertices == null || intermediaryVertices.length == 0){
            return findLessCostPathBetweenTwoVertices(model, transportList, origin, destination);
        }

        PathUtils.validateLessCostPathBetweenTwoVerticesPersonalizedParameters(model, transportList, origin, destination, intermediaryVertices);
        graph = model;
        List<Vertex<Stop>> fullPath = new LinkedList<>();
        List<TransportType> fullPathTransportList = new LinkedList<>();
        List<RouteInfo> fullPathInfo = new LinkedList<>();

        Vertex<Stop> u = origin;
        // get best u-v path
        for (Vertex<Stop> v : intermediaryVertices) {
            PathResult partialResult = findLessCostPathBetweenTwoVertices(model, transportList, u, v);
            // skip v in path, add in next iteration
            fullPath.addAll(partialResult.getPath().subList(0, partialResult.getPath().size() - 1));
            fullPathTransportList.addAll(partialResult.getTransportList());
            fullPathInfo.addAll(partialResult.getPathInfo());
            // set u as v for next iteration
            u = v;
        }

        PathResult finalResult = findLessCostPathBetweenTwoVertices(model, transportList, u, destination);
        fullPath.addAll(finalResult.getPath());
        fullPathTransportList.addAll(finalResult.getTransportList());
        fullPathInfo.addAll(finalResult.getPathInfo());

        RouteInfo totalRouteInfo = new RouteInfo(
                finalResult.getTotalSustainability(),
                finalResult.getTotalDuration(),
                finalResult.getTotalDistance()
        );

        return new PathResult(totalRouteInfo.getSustainability(),
                totalRouteInfo.getDuration(),
                totalRouteInfo.getDistance(),
                fullPath, fullPathTransportList, fullPathInfo);
    }
}