package pt.pa.path;

import com.brunomnsilva.smartgraph.graph.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pt.pa.transportmap.path.PathUtils;
import pt.pa.transportmap.path.cost.CostStrategy;
import pt.pa.transportmap.path.cost.DistanceCostStrategy;
import pt.pa.transportmap.path.cost.DurationCostStrategy;
import pt.pa.transportmap.path.cost.SustainabilityCostStrategy;
import pt.pa.transportmap.*;

import java.util.EnumSet;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class PathUtilsTest {
    TransportMap graph;

    @BeforeEach
    void setUp() {
        Graph<Stop, Route> g = new GraphEdgeList<>();
        graph = new TransportMap(g);
        graph.update();
    }

    @Test
    void getMinimumTransportCost() {
        EnumSet<TransportType> transportList = EnumSet.allOf(TransportType.class);
        CostStrategy costStrategy = new DistanceCostStrategy();

        Vertex<Stop> setubal = graph.getVertex("SET014");
        if (setubal == null) {
            fail("Setúbal vertex not found");
        }
        Vertex<Stop> palmela = graph.getVertex("PAL018");
        if(palmela == null){
            fail("Palmela vertex was not found.");
        }

        // edge of setubal to palmela
        Edge<Route, Stop> edge = graph.getEdge(setubal, palmela);
        if (edge == null) {
            fail("Edge not found");
        }

        // get entry for distance, set-pal should be train 5.19 rounded in view to 5.2
        Map.Entry<TransportType, Double> entry = PathUtils.getMinimumTransportCost(graph, transportList, edge, costStrategy);
        assertEquals(TransportType.TRAIN, entry.getKey());
        assertEquals(5.19, entry.getValue());

        // duration is train 4 min
        costStrategy = new DurationCostStrategy();
        entry = PathUtils.getMinimumTransportCost(graph, transportList, edge, costStrategy);
        assertEquals(TransportType.TRAIN, entry.getKey());
        assertEquals(4.0, entry.getValue());

        // sustainabilty is bycycle 0.5
        costStrategy = new SustainabilityCostStrategy();
        entry = PathUtils.getMinimumTransportCost(graph, transportList, edge, costStrategy);
        assertEquals(TransportType.BICYCLE, entry.getKey());
        assertEquals(0.5, entry.getValue());

        // test a route with negative sustainability (corroios to charneca)
        Vertex<Stop> corroios = graph.getVertex("COR025");
        if (corroios == null) {
            fail("Corroios vertex not found.");
        }
        Vertex<Stop> charneca = graph.getVertex("CHA021");
        if (charneca == null){
            fail("Charneca vertex not found.");
        }

        // edge of corroios to charneca
        Edge<Route, Stop> edge2 = graph.getEdge(corroios, charneca);
        if (edge2 == null) {
            fail("Edge2 not found");
        }

        // get entry for distance, corroios-charneca should be train 4.71, rounded to 4.7 in view
        costStrategy = new DistanceCostStrategy();
        entry = PathUtils.getMinimumTransportCost(graph, transportList, edge2, costStrategy);
        assertEquals(TransportType.TRAIN, entry.getKey());
        assertEquals(4.71, entry.getValue());

        // duration is train 4 min
        costStrategy = new DurationCostStrategy();
        entry = PathUtils.getMinimumTransportCost(graph, transportList, edge2, costStrategy);
        assertEquals(TransportType.TRAIN, entry.getKey());
        assertEquals(4.0, entry.getValue());

        // sustainabilty is bycycle -1.45
        costStrategy = new SustainabilityCostStrategy();
        entry = PathUtils.getMinimumTransportCost(graph, transportList, edge2, costStrategy);
        assertEquals(TransportType.BICYCLE, entry.getKey());
        assertEquals(-1.45, entry.getValue());
    }

    @Test
    void setOtherCosts() {
        CostStrategy costStrategy = new DistanceCostStrategy();
        RouteInfo maxRouteInfo = new RouteInfo(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE);
        RouteInfo vRouteInfo = new RouteInfo(1.0, 2.0, -3.0);
        RouteInfo wRouteInfo = new RouteInfo(4.0, 5.0, -6.0);
        RouteInfo edgeInfo = new RouteInfo(7.0, 8.0, -9.0);

        // distance
        PathUtils.setOtherCosts(vRouteInfo, wRouteInfo, edgeInfo, costStrategy);
        assertEquals(4.0, wRouteInfo.getDistance()); // not change distance
        assertEquals(2.0 + 8.0, wRouteInfo.getDuration()); // w = v + e
        assertEquals(-3.0 - 9.0, wRouteInfo.getSustainability()); // w = v + e

        wRouteInfo = new RouteInfo(4.0, 5.0, -6.0); // reset
        // test with max
        PathUtils.setOtherCosts(maxRouteInfo, wRouteInfo, edgeInfo, costStrategy);
        assertEquals(4.0, wRouteInfo.getDistance()); // not change distance
        assertEquals(8.0, wRouteInfo.getDuration()); // w = e
        assertEquals(-9.0, wRouteInfo.getSustainability()); // w = e

        // duration
        costStrategy = new DurationCostStrategy();
        wRouteInfo = new RouteInfo(4.0, 5.0, -6.0); // reset
        PathUtils.setOtherCosts(vRouteInfo, wRouteInfo, edgeInfo, costStrategy);
        assertEquals(1.0 + 7.0, wRouteInfo.getDistance()); // w = v + e
        assertEquals(5.0, wRouteInfo.getDuration()); // not change duration
        assertEquals(-3.0 - 9.0, wRouteInfo.getSustainability()); // w = v + e

        wRouteInfo = new RouteInfo(4.0, 5.0, -6.0); // reset
        // test with max
        PathUtils.setOtherCosts(maxRouteInfo, wRouteInfo, edgeInfo, costStrategy);
        assertEquals(7.0, wRouteInfo.getDistance()); // w = e
        assertEquals(5.0, wRouteInfo.getDuration()); // not change duration
        assertEquals(-9.0, wRouteInfo.getSustainability()); // w = e

        // sustainability
        costStrategy = new SustainabilityCostStrategy();
        wRouteInfo = new RouteInfo(4.0, 5.0, -6.0); // reset
        PathUtils.setOtherCosts(vRouteInfo, wRouteInfo, edgeInfo, costStrategy);
        assertEquals(1.0 + 7.0, wRouteInfo.getDistance()); // w = v + e
        assertEquals(2.0 + 8.0, wRouteInfo.getDuration()); // w = v + e
        assertEquals(-6.0, wRouteInfo.getSustainability()); // not change sustainability

        wRouteInfo = new RouteInfo(4.0, 5.0, -6.0); // reset
        // test with max
        PathUtils.setOtherCosts(maxRouteInfo, wRouteInfo, edgeInfo, costStrategy);
        assertEquals(7.0, wRouteInfo.getDistance()); // w = e
        assertEquals(8.0, wRouteInfo.getDuration()); // w = e
        assertEquals(-6.0, wRouteInfo.getSustainability()); // not change sustainability

        // throws
        RouteInfo finalWRouteInfo = wRouteInfo;
        CostStrategy finalCostStrategy = costStrategy;
        assertThrows(IllegalArgumentException.class, () -> PathUtils.setOtherCosts(null, null, null, null));
        assertThrows(IllegalArgumentException.class, () -> PathUtils.setOtherCosts(vRouteInfo, null, null, null));
        assertThrows(IllegalArgumentException.class, () -> PathUtils.setOtherCosts(vRouteInfo, finalWRouteInfo, null, null));
        assertThrows(IllegalArgumentException.class, () -> PathUtils.setOtherCosts(vRouteInfo, finalWRouteInfo, edgeInfo, null));
        assertThrows(IllegalArgumentException.class, () -> PathUtils.setOtherCosts(null, finalWRouteInfo, edgeInfo, finalCostStrategy));
        assertThrows(IllegalArgumentException.class, () -> PathUtils.setOtherCosts(vRouteInfo, null, edgeInfo, finalCostStrategy));
        assertThrows(IllegalArgumentException.class, () -> PathUtils.setOtherCosts(vRouteInfo, finalWRouteInfo, null, finalCostStrategy));
        assertThrows(IllegalArgumentException.class, () -> PathUtils.setOtherCosts(null, null, edgeInfo, finalCostStrategy));
        assertThrows(IllegalArgumentException.class, () -> PathUtils.setOtherCosts(null, finalWRouteInfo, null, finalCostStrategy));
        assertThrows(IllegalArgumentException.class, () -> PathUtils.setOtherCosts(vRouteInfo, null, null, finalCostStrategy));
        assertThrows(IllegalArgumentException.class, () -> PathUtils.setOtherCosts(null, null, null, finalCostStrategy));
    }

    @Test
    void validateLessCostPathBetweenTwoVerticesParameters() {
        EnumSet<TransportType> transportList = EnumSet.allOf(TransportType.class);
        EnumSet<TransportType> transportListEmpty = EnumSet.noneOf(TransportType.class);
        Vertex<Stop> setubal = graph.getVertex("SET014");
        Vertex<Stop> palmela = graph.getVertex("PAL018");
        Vertex<Stop> notInGraph = graph.insertVertex( new Stop("AXNOTING", "Not in graph", 80, 80) );
        graph.removeVertex(notInGraph);

        // null
        assertThrows(IllegalArgumentException.class, () -> PathUtils.validateLessCostPathBetweenTwoVerticesParameters(null, null, null, null));
        assertThrows(IllegalArgumentException.class, () -> PathUtils.validateLessCostPathBetweenTwoVerticesParameters(graph, null, null, null));
        assertThrows(IllegalArgumentException.class, () -> PathUtils.validateLessCostPathBetweenTwoVerticesParameters(graph, transportList, null, null));
        assertThrows(IllegalArgumentException.class, () -> PathUtils.validateLessCostPathBetweenTwoVerticesParameters(graph, transportList, setubal, null));
        assertThrows(IllegalArgumentException.class, () -> PathUtils.validateLessCostPathBetweenTwoVerticesParameters(graph, null, setubal, palmela));
        assertThrows(IllegalArgumentException.class, () -> PathUtils.validateLessCostPathBetweenTwoVerticesParameters(graph, transportList, null, palmela));
        assertThrows(IllegalArgumentException.class, () -> PathUtils.validateLessCostPathBetweenTwoVerticesParameters(graph, null, setubal, null));
        assertThrows(IllegalArgumentException.class, () -> PathUtils.validateLessCostPathBetweenTwoVerticesParameters(graph, null, null, palmela));

        assertThrows(IllegalArgumentException.class, () -> PathUtils.validateLessCostPathBetweenTwoVerticesParameters(graph, null, notInGraph, notInGraph));
        assertThrows(IllegalArgumentException.class, () -> PathUtils.validateLessCostPathBetweenTwoVerticesParameters(graph, transportList, null, notInGraph));
        assertThrows(IllegalArgumentException.class, () -> PathUtils.validateLessCostPathBetweenTwoVerticesParameters(graph, transportList, notInGraph, null));
        assertThrows(IllegalArgumentException.class, () -> PathUtils.validateLessCostPathBetweenTwoVerticesParameters(graph, null, notInGraph, palmela));

        // empty transport list
        assertThrows(IllegalArgumentException.class, () -> PathUtils.validateLessCostPathBetweenTwoVerticesParameters(graph, transportListEmpty, setubal, palmela));
        assertThrows(IllegalArgumentException.class, () -> PathUtils.validateLessCostPathBetweenTwoVerticesParameters(graph, transportListEmpty, setubal, notInGraph));
        assertThrows(IllegalArgumentException.class, () -> PathUtils.validateLessCostPathBetweenTwoVerticesParameters(graph, transportListEmpty, notInGraph, palmela));
        assertThrows(IllegalArgumentException.class, () -> PathUtils.validateLessCostPathBetweenTwoVerticesParameters(graph, transportListEmpty, notInGraph, notInGraph));

        assertThrows(InvalidVertexException.class, () -> PathUtils.validateLessCostPathBetweenTwoVerticesParameters(graph, transportList, setubal, notInGraph));
        assertThrows(InvalidVertexException.class, () -> PathUtils.validateLessCostPathBetweenTwoVerticesParameters(graph, transportList, notInGraph, palmela));
        assertThrows(InvalidVertexException.class, () -> PathUtils.validateLessCostPathBetweenTwoVerticesParameters(graph, transportList, notInGraph, notInGraph));
    }

    @Test
    void validateLessCostPathBetweenTwoVerticesPersonalizedParameters() {
        // do the above test
        validateLessCostPathBetweenTwoVerticesParameters();

        EnumSet<TransportType> transportList = EnumSet.allOf(TransportType.class);
        Vertex<Stop> setubal = graph.getVertex("SET014");
        Vertex<Stop> palmela = graph.getVertex("PAL018");
        Vertex<Stop> coina = graph.getVertex("COI017");

        /**
         * Validate findLessCostPathBetweenTwoVerticesPersonalizedParameters method parameters. Assumes intermediaryVertices is not null or empty (verify before calling this, and if so use the other method for path without intermediary vertices).
         * @param transportList EnumSet<TransportType> A list of transport types that can be used in the path
         * @param origin Vertex<Stop> the origin vertex
         * @param destination Vertex<Stop> the destination vertex
         * @param intermediaryVertices Vertex<Stop>[] a collection of the intermediary vertices, in order from origin to destination (exclusive)
         * @throws IllegalArgumentException if graph, origin, destination or any intermediary vertex are null or if no transports are provided or if intermediary vertices are not in correct order
         * @throws InvalidVertexException if origin, destination or any intermediary vertex are not in the graph
         */
        //public static void validateLessCostPathBetweenTwoVerticesPersonalizedParameters(EnumSet<TransportType> transportList, Vertex<Stop> origin, Vertex<Stop> destination, Vertex<Stop>[] intermediaryVertices) throws IllegalArgumentException, InvalidVertexException {
    }

    @Test
    void bfs() {
    }

    @Test
    void bfsLimited() {
    }

    @Test
    void dfs() {
    }
}