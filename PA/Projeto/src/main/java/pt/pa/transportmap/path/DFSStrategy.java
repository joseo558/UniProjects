package pt.pa.transportmap.path;

import com.brunomnsilva.smartgraph.graph.Edge;
import com.brunomnsilva.smartgraph.graph.InvalidVertexException;
import com.brunomnsilva.smartgraph.graph.Vertex;
import pt.pa.transportmap.path.cost.CostStrategy;
import pt.pa.transportmap.*;
import pt.pa.transportmap.userconfiguration.UserConfiguration;

import java.util.*;

public class DFSStrategy implements PathStrategy {
    /** The transport map graph */
    TransportMap graph;
    /** The cost strategy to use */
    CostStrategy costStrategy;

    /**
     * Constructor for DFSStrategy
     * @param costStrategy CostStrategy the cost strategy to use
     */
    public DFSStrategy(CostStrategy costStrategy){
        this.costStrategy = costStrategy;
        this.graph = null;
    }

    /**
     * Find the less cost path using a modified DFS algorithm
     * @param transportList EnumSet<TransportType> A list of transport types that can be used in the path
     * @param origin Vertex<Stop> the origin vertex
     * @return HashMap< Vertex<Stop>, PathSegmentInfo > a map with the less cost path to each vertex
     * @throws IllegalStateException if graph contains negative-weight cycles
     */
    private HashMap<Vertex<Stop>, PathSegmentInfo> lessCostPath (EnumSet<TransportType> transportList, Vertex<Stop> origin) throws IllegalStateException {
        HashMap<Vertex<Stop>, PathSegmentInfo> result = new HashMap<>();

        // for each vertex in the graph, assign infinite cost and null predecessor
        for (Vertex<Stop> vertex : graph.vertices()) {
            result.put(vertex, new PathSegmentInfo());
        }
        // set origin cost to 0
        result.get(origin).getRouteInfo().setSustainability(0.0).setDuration(0.0).setDistance(0.0);

        // dfs
        Set<Vertex<Stop>> visited = new HashSet<>();
        Stack<Vertex<Stop>> stack = new Stack<>();

        visited.add(origin); // mark as visited
        stack.push(origin);

        UserConfiguration userConfiguration = graph.getUserConfiguration();

        while(!stack.isEmpty()){
            Vertex<Stop> w = stack.pop();
            for(Edge<Route, Stop> edge : graph.incidentEdges(w)){
                if(userConfiguration.isRouteDisabled(edge)){
                    continue; // Skip edge
                }

                // Find minimum transport cost for the edge
                Map.Entry<TransportType, Double> minimumTransportEntry = PathUtils.getMinimumTransportCost(graph, transportList, edge, costStrategy);
                TransportType minType = minimumTransportEntry.getKey();
                double minTransportCost = minimumTransportEntry.getValue();
                if (minType == null){ continue; } // Skip edge if no valid transport type

                for(Vertex<Stop> u : edge.vertices()) {
                    // get opposite
                    Vertex<Stop> v = graph.opposite(u, edge);
                    RouteInfo uRouteInfo = result.get(u).getRouteInfo();
                    RouteInfo vRouteInfo = result.get(v).getRouteInfo();

                    double newCost = costStrategy.getCost(uRouteInfo) + minTransportCost + 1000; // fix for negative cycle in corroios-charneca

                    if (newCost < costStrategy.getCost(vRouteInfo)) {
                        costStrategy.setCost(vRouteInfo, newCost - 1000); // due to fix above
                        PathUtils.setOtherCosts(uRouteInfo, vRouteInfo, edge.element().getTransportInfo(minType), costStrategy);
                        result.get(v).setPredecessor(u);
                        result.get(v).setTransportType(minType);
                    }

                    if (!visited.contains(v)) {
                        visited.add(v);
                        stack.push(v);
                    }
                }
            }
        }

        return result;
    }

    @Override
    public PathResult findLessCostPathBetweenTwoVertices(TransportMap graph, EnumSet<TransportType> transportList, Vertex<Stop> origin, Vertex<Stop> destination) throws IllegalArgumentException, InvalidVertexException {
        PathUtils.validateLessCostPathBetweenTwoVerticesParameters(graph, transportList, origin, destination);
        this.graph = graph;
        // get DFS result
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
