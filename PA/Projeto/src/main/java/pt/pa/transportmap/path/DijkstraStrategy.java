package pt.pa.transportmap.path;

import com.brunomnsilva.smartgraph.graph.Edge;
import com.brunomnsilva.smartgraph.graph.InvalidVertexException;
import com.brunomnsilva.smartgraph.graph.Vertex;
import pt.pa.transportmap.path.cost.CostStrategy;
import pt.pa.transportmap.*;
import pt.pa.transportmap.userconfiguration.UserConfiguration;

import java.util.*;

/**
 * Dijkstra class to find the less cost path in a graph
 */
public class DijkstraStrategy implements PathStrategy {
    /** The transport map graph */
    TransportMap graph;
    /** The cost strategy to use */
    CostStrategy costStrategy;

    /**
     * Constructor for DijkstraStrategy
     * @param costStrategy CostStrategy the cost strategy to use
     */
    public DijkstraStrategy(CostStrategy costStrategy){
        this.costStrategy = costStrategy;
        graph = null;
    }

    /**
     * Find the less cost path in a graph
     * @param transportList EnumSet<TransportType> A list of transport types that can be used in the path
     * @param origin Vertex<Stop> the origin vertex
     * @return HashMap< Vertex<Stop>, PathSegmentInfo > a map with the less cost path to each vertex
     */
    private HashMap< Vertex<Stop>, PathSegmentInfo > lessCostPath(EnumSet<TransportType> transportList, Vertex<Stop> origin){
        HashMap< Vertex<Stop>, PathSegmentInfo> result = new HashMap<>();

        // changed to an ordered collection, a priority queue to optimize finding minimum vs normal set
        PriorityQueue<Vertex<Stop>> unvisited = new PriorityQueue<>(
                Comparator.comparingDouble(v -> costStrategy.getCost(result.get(v).getRouteInfo()))
        );

        // for each vertex in the graph, assign infinite cost and null predecessor
        // add to unvisited collection
        for(Vertex<Stop> vertex : graph.vertices()){
            result.put(vertex, new PathSegmentInfo());
            unvisited.add(vertex);
        }

        // set origin cost to 0
        result.get(origin).getRouteInfo().setSustainability(0.0).setDuration(0.0).setDistance(0.0);

        Vertex<Stop> current;
        UserConfiguration userConfiguration = graph.getUserConfiguration();

        // while there are unvisited vertices
        while(!unvisited.isEmpty()){

            // selected unvisited vertex with less cost
            current = unvisited.poll();
            if(current == null){ break; }
            // mark vertex as visited -> poll already removes

            Iterable<Edge<Route, Stop>> currentIncidentEdges = graph.incidentEdges(current);

            // for each neighbor W of V (current)
            for(Edge<Route, Stop> edge : currentIncidentEdges){
                if(userConfiguration.isRouteDisabled(edge)){
                    continue; // Skip edge
                }

                Vertex<Stop> w = graph.opposite(current, edge);

                // get minimum cost of transport types in list
                Map.Entry<TransportType, Double> minimumTransportEntry = PathUtils.getMinimumTransportCost(graph, transportList, edge, costStrategy);
                TransportType minType = minimumTransportEntry.getKey();
                double minTransportCost = minimumTransportEntry.getValue();
                if(minType == null){ continue; } // Skip edge

                // if the current cost of [V (current) + weight of edge (V, W)] is less than the current cost of W
                RouteInfo currentRouteInfo = result.get(current).getRouteInfo();
                double costVWeight = costStrategy.getCost(currentRouteInfo) + minTransportCost;
                RouteInfo wRouteInfo = result.get(w).getRouteInfo();

                if( costVWeight < costStrategy.getCost(wRouteInfo) ){
                    // update the cost of W
                    costStrategy.setCost(wRouteInfo, costVWeight);
                    // update the other costs not considered in the cost strategy
                    PathUtils.setOtherCosts(currentRouteInfo, wRouteInfo, edge.element().getTransportInfo(minType), costStrategy);
                    // set V (current) as the predecessor of W
                    result.get(w).setPredecessor(current);
                    // set transport type used
                    result.get(w).setTransportType(minType);
                    // Add the updated vertex to the priority queue
                    unvisited.remove(w);
                    unvisited.add(w);
                }
            }
        }
        return result;
    }

    @Override
    public PathResult findLessCostPathBetweenTwoVertices(TransportMap graph, EnumSet<TransportType> transportList, Vertex<Stop> origin, Vertex<Stop> destination) throws IllegalArgumentException, InvalidVertexException {
        PathUtils.validateLessCostPathBetweenTwoVerticesParameters(graph, transportList, origin, destination);
        this.graph = graph;
        // get dijkstra result
        HashMap< Vertex<Stop>, PathSegmentInfo > result = lessCostPath(transportList, origin);

        List<Vertex<Stop>> path = new LinkedList<>();
        List<TransportType> pathTransportList = new LinkedList<>();
        List<RouteInfo> pathInfo = new LinkedList<>();

        // get the path from the destination to the origin, but in order origin -> destination
        Vertex<Stop> current = destination;
        while(current != null){
            path.add(0, current); // add at start to avoid using collection.reverse
            if(result.get(current).getPredecessor() != null) {
                // skip for origin vertex
                pathTransportList.add(0, result.get(current).getTransportType());
                pathInfo.add(0, result.get(current).getRouteInfo());
            }
            current = result.get(current).getPredecessor();
        }

        // get path totals
        RouteInfo destinationRouteInfo = result.get(destination).getRouteInfo();

        return new PathResult(destinationRouteInfo.getSustainability(),
                destinationRouteInfo.getDuration(),
                destinationRouteInfo.getDistance(),
                path, pathTransportList, pathInfo);
    }

    @Override
    public PathResult findLessCostPathBetweenTwoVerticesPersonalized(TransportMap graph, EnumSet<TransportType> transportList, Vertex<Stop> origin, Vertex<Stop> destination, Vertex<Stop>[] intermediaryVertices) throws IllegalArgumentException, InvalidVertexException {
        if(intermediaryVertices == null || intermediaryVertices.length == 0){
            return findLessCostPathBetweenTwoVertices(graph, transportList, origin, destination);
        }

        PathUtils.validateLessCostPathBetweenTwoVerticesPersonalizedParameters(graph, transportList, origin, destination, intermediaryVertices);
        this.graph = graph;

        List<Vertex<Stop>> fullPath = new LinkedList<>();
        List<TransportType> fullPathTransportList = new LinkedList<>();
        List<RouteInfo> fullPathInfo = new LinkedList<>();

        Vertex<Stop> u = origin;
        // get best u-v path
        for (Vertex<Stop> v : intermediaryVertices) {
            PathResult partialResult = findLessCostPathBetweenTwoVertices(graph, transportList, u, v);
            // skip v in path, add in next iteration
            fullPath.addAll(partialResult.getPath().subList(0, partialResult.getPath().size() - 1));
            fullPathTransportList.addAll(partialResult.getTransportList());
            fullPathInfo.addAll(partialResult.getPathInfo());
            // set u as v for next iteration
            u = v;
        }

        PathResult finalResult = findLessCostPathBetweenTwoVertices(graph, transportList, u, destination);
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