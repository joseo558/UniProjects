package pt.pa.transportmap;

import com.brunomnsilva.smartgraph.graph.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pt.pa.logger.Logger;
import pt.pa.transportmap.path.DijkstraStrategy;
import pt.pa.transportmap.path.PathCriteria;
import pt.pa.transportmap.path.cost.DurationCostStrategy;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for TransportMap class.
 */
class TransportMapTest {
    /** The model used  */
    TransportMap model;
    /** The graph in the model */
    Graph<Stop, Route> graph;
    /** Palmela Stop Code */
    private static final String PALMELA_STOP_CODE = "PAL018";
    /** Coina Stop Code */
    private static final String COINA_STOP_CODE = "COI017";
    /** Cais do Sodré Stop Code */
    private static final String CAIS_DO_SODRE_STOP_CODE = "CAIS019";
    /** Algés Stop Code */
    private static final String ALGES_STOP_CODE = "ALG011";
    /** Text for the PathCriteria Distance */
    static final String distanceString = PathCriteria.DISTANCE.toString();
    /** Text for the PathCriteria Duration */
    static final String durationString = PathCriteria.DURATION.toString();
    /** Text for the PathCriteria Sustainability */
    static final String sustainabilityString = PathCriteria.SUSTAINABILITY.toString();
    /** Text for the PathCriteria Distance unit */
    static final String distanceUnit = PathCriteria.DISTANCE.getUnit();
    /** Text for the PathCriteria Duration unit */
    static final String durationUnit = PathCriteria.DURATION.getUnit();
    /** Text for the PathCriteria Sustainability unit */
    static final String sustainabilityUnit = PathCriteria.SUSTAINABILITY.getUnit();

    /**
     * Appends the expected route info to the StringBuilder
     * @param sb StringBuilder to append the info
     * @param distance double distance value
     * @param duration double duration value
     * @param sustainability double sustainability value
     */
    private void appendExpectedRouteInfo(StringBuilder sb, double distance, double duration, double sustainability){
        if(sb == null){ return; }
        sb.append(distanceString).append(": ").append(String.format("%.1f", distance)).append(" ").append(distanceUnit).append("\n")
                .append(durationString).append(": ").append(String.format("%.0f", duration)).append(" ").append(durationUnit).append("\n")
                .append(sustainabilityString).append(": ").append(String.format("%.2f", sustainability)).append(" ").append(sustainabilityUnit);
    }

    @BeforeEach
    void setUp() {
        graph = new GraphEdgeList<>();
        model = new TransportMap(graph);
        model.update();
    }

    @Test
    void addObserver() {
        // list is empty by default, null is not added to arraylist but does not throw exception
        model.addObserver(null);
        model.removeObserver(null);

        Logger logger = new Logger(); // logger is a observer
        model.addObserver(logger);
        model.notifyObservers("Test");
        List<String> commands = logger.readFromFile();
        assertEquals("Test", commands.get(commands.size() - 1));

        model.removeObserver(logger);
        model.notifyObservers("Test2");
        commands = logger.readFromFile();
        assertNotEquals("Test2", commands.get(commands.size() - 1));
    }

    @Test
    void removeObserver() {
        // list is empty by default, null is not added to arraylist but does not throw exception
        model.addObserver(null);
        model.removeObserver(null);

        Logger logger = new Logger(); // logger is a observer
        model.addObserver(logger);
        model.notifyObservers("Test");
        List<String> commands = logger.readFromFile();
        assertEquals("Test", commands.get(commands.size() - 1));

        model.removeObserver(logger);
        model.notifyObservers("Test2");
        commands = logger.readFromFile();
        assertNotEquals("Test2", commands.get(commands.size() - 1));
    }

    @Test
    void notifyObservers() {
        // list is empty by default, null is not added to arraylist but does not throw exception
        model.addObserver(null);
        model.removeObserver(null);

        Logger logger = new Logger(); // logger is a observer
        model.addObserver(logger);
        model.notifyObservers("Test");
        List<String> commands = logger.readFromFile();
        assertEquals("Test", commands.get(commands.size() - 1));

        model.removeObserver(logger);
        model.notifyObservers("Test2");
        commands = logger.readFromFile();
        assertNotEquals("Test2", commands.get(commands.size() - 1));
    }

    @Test
    void update() {
        assertEquals(31, model.getNumberOfStops());
        assertEquals(39, model.getNumberOfRoutes());
        model.update(); // next updates should not add any new vertices or edges (files have same)
        assertDoesNotThrow(model::update);
        assertEquals(31, model.getNumberOfStops());
        assertEquals(39, model.getNumberOfRoutes());

        assertTrue(model.hasStop(PALMELA_STOP_CODE));
        assertTrue(model.hasStop(COINA_STOP_CODE));

        assertTrue(model.areAdjacent(PALMELA_STOP_CODE, COINA_STOP_CODE));
        assertTrue(model.areAdjacent(CAIS_DO_SODRE_STOP_CODE, ALGES_STOP_CODE));
    }

    @Test
    void getPathStrategy() {
        assertNull(model.getPathStrategy());
        model.setPathStrategy(new DijkstraStrategy(new DurationCostStrategy()));
        assertEquals(DijkstraStrategy.class, model.getPathStrategy().getClass());
    }

    @Test
    void setPathStrategy() {
        assertNull(model.getPathStrategy());
        model.setPathStrategy(new DijkstraStrategy(new DurationCostStrategy()));
        assertEquals(DijkstraStrategy.class, model.getPathStrategy().getClass());
    }

    @Test
    void getUserConfiguration() {
        model.loadUserConfiguration();
        assertNotNull(model.getUserConfiguration());
        assertEquals(model.getUserConfiguration().getBicycleDurationScale(), 1.0);
    }

    @Test
    void getNumberOfStops() {
        assertEquals(31, model.getNumberOfStops());

        List<Vertex<Stop>> list = (List<Vertex<Stop>>) model.vertices();
        Vertex<Stop> vertex = list.get(5);
        model.removeVertex(vertex);
        vertex = list.get(7);
        model.removeVertex(vertex);

        assertEquals(29, model.getNumberOfStops());
        assertFalse(model.vertices().contains(vertex));

        Graph<Stop, Route> graph = new GraphEdgeList<>();
        model = new TransportMap(graph);
        assertEquals(0, model.getNumberOfStops());
    }

    @Test
    void getNumberOfIsolatedStops() {
        assertEquals(2, model.getNumberOfIsolatedStops());

        Graph<Stop, Route> graph = new GraphEdgeList<>();
        model = new TransportMap(graph);
        assertEquals(0, model.getNumberOfIsolatedStops());
    }

    @Test
    void getNumberOfNotIsolatedStops() {
        assertEquals(29, model.getNumberOfNotIsolatedStops());

        Graph<Stop, Route> graph = new GraphEdgeList<>();
        model = new TransportMap(graph);
        assertEquals(0, model.getNumberOfNotIsolatedStops());
    }

    @Test
    void getNumberOfRoutes() {
        assertEquals(39, model.getNumberOfRoutes());

        Graph<Stop, Route> graph = new GraphEdgeList<>();
        model = new TransportMap(graph);
        assertEquals(0, model.getNumberOfRoutes());
    }

    @Test
    void getNumberOfRoutesByTransportType() {
        Map<TransportType, Integer> map = model.getNumberOfRoutesByTransportType();
        // match excel not null cell counts
        assertEquals(15, map.get(TransportType.WALK));
        assertEquals(29, map.get(TransportType.TRAIN));
        assertEquals(4, map.get(TransportType.BOAT));
        assertEquals(35, map.get(TransportType.BUS));
        assertEquals(23, map.get(TransportType.BICYCLE));

        Graph<Stop, Route> graph = new GraphEdgeList<>();
        model = new TransportMap(graph);
        map = model.getNumberOfRoutesByTransportType();
        assertEquals(0, map.get(TransportType.WALK));
        assertEquals(0, map.get(TransportType.TRAIN));
        assertEquals(0, map.get(TransportType.BOAT));
        assertEquals(0, map.get(TransportType.BUS));
        assertEquals(0, map.get(TransportType.BICYCLE));
    }

    @Test
    void getStopCentrality() {
        List<Map.Entry<Stop, Integer>> list = model.getStopCentrality();
        assertEquals(31, list.size());
        assertEquals("Cais do Sodré", list.get(0).getKey().toString());
        assertEquals(5, list.get(0).getValue());
        assertEquals("Entrecampos", list.get(1).getKey().toString());
        assertEquals(5, list.get(1).getValue());
        assertEquals("Coina", list.get(2).getKey().toString());
        assertEquals(4, list.get(2).getValue());
        assertEquals("Odivelas", list.get(3).getKey().toString());
        assertEquals(4, list.get(3).getValue());
        assertEquals("Pragal", list.get(4).getKey().toString());
        assertEquals(4, list.get(4).getValue());
        assertEquals("Reboleira", list.get(5).getKey().toString());
        assertEquals(4, list.get(5).getValue());
        assertEquals("Algés", list.get(6).getKey().toString());
        assertEquals(3, list.get(6).getValue());

        Graph<Stop, Route> graph = new GraphEdgeList<>();
        model = new TransportMap(graph);
        list = model.getStopCentrality();
        assertEquals(0, list.size());
    }

    @Test
    void vertices() {
        List<Vertex<Stop>> list = (List<Vertex<Stop>>) model.vertices();
        assertEquals(31, list.size());
        assertTrue(list.contains(model.getVertex(CAIS_DO_SODRE_STOP_CODE)));
        assertTrue(list.contains(model.getVertex(ALGES_STOP_CODE)));
        assertTrue(list.contains(model.getVertex(COINA_STOP_CODE)));
        assertTrue(list.contains(model.getVertex(PALMELA_STOP_CODE)));

        Graph<Stop, Route> graph = new GraphEdgeList<>();
        model = new TransportMap(graph);
        list = (List<Vertex<Stop>>) model.vertices();
        assertEquals(0, list.size());
    }

    @Test
    void getVertexByStop() {
        assertNotNull(model.getVertex(new Stop(CAIS_DO_SODRE_STOP_CODE, "Cais do Sodré", 0, 0)));
        assertNotNull(model.getVertex(new Stop(ALGES_STOP_CODE, "Algés", 0, 0)));
        assertNotNull(model.getVertex(new Stop(COINA_STOP_CODE, "Coina", 0, 0)));
        assertNotNull(model.getVertex(new Stop(PALMELA_STOP_CODE, "Palmela", 0, 0)));

        assertNull(model.getVertex(new Stop("Invalid", "Invalid", 0, 0)));
        assertNull(model.getVertex((Stop) null));
    }

    @Test
    void getVertexByStopCode() {
        assertNotNull(model.getVertex(CAIS_DO_SODRE_STOP_CODE));
        assertNotNull(model.getVertex(ALGES_STOP_CODE));
        assertNotNull(model.getVertex(COINA_STOP_CODE));
        assertNotNull(model.getVertex(PALMELA_STOP_CODE));

        assertNull(model.getVertex("Invalid"));
        assertNull(model.getVertex((String) null));
        assertNull(model.getVertex(""));
    }

    @Test
    void getStops() {
        Collection<Stop> collection = model.getStops();
        assertEquals(31, collection.size());

        assertTrue(collection.contains(new Stop(CAIS_DO_SODRE_STOP_CODE, "Cais do Sodré", 0, 0)));
        assertTrue(collection.contains(new Stop(ALGES_STOP_CODE, "Algés", 0, 0)));
        assertTrue(collection.contains(new Stop(COINA_STOP_CODE, "Coina", 0, 0)));
        assertTrue(collection.contains(new Stop(PALMELA_STOP_CODE, "Palmela", 0, 0)));

        Graph<Stop, Route> graph = new GraphEdgeList<>();
        model = new TransportMap(graph);
        collection = model.getStops();
        assertEquals(0, collection.size());
    }

    @Test
    void hasStopByStop() {
        assertTrue(model.hasStop(new Stop(CAIS_DO_SODRE_STOP_CODE, "Cais do Sodré", 0, 0)));
        assertTrue(model.hasStop(new Stop(ALGES_STOP_CODE, "Algés", 0, 0)));
        assertTrue(model.hasStop(new Stop(COINA_STOP_CODE, "Coina", 0, 0)));
        assertTrue(model.hasStop(new Stop(PALMELA_STOP_CODE, "Palmela", 0, 0)));

        assertFalse(model.hasStop(new Stop("Invalid", "Invalid", 0, 0)));
        assertFalse(model.hasStop((Stop) null));
    }

    @Test
    void hasStopByStopCode() {
        assertTrue(model.hasStop(CAIS_DO_SODRE_STOP_CODE));
        assertTrue(model.hasStop(ALGES_STOP_CODE));
        assertTrue(model.hasStop(COINA_STOP_CODE));
        assertTrue(model.hasStop(PALMELA_STOP_CODE));

        assertFalse(model.hasStop("Invalid"));
        assertFalse(model.hasStop((String) null));
        assertFalse(model.hasStop(""));
    }

    @Test
    void edges() {
        List<Edge<Route, Stop>> list = (List<Edge<Route, Stop>>) model.edges();
        assertEquals(39, list.size());

        assertTrue(model.areAdjacent(PALMELA_STOP_CODE, COINA_STOP_CODE));
        assertTrue(model.areAdjacent(CAIS_DO_SODRE_STOP_CODE, ALGES_STOP_CODE));
        assertNotNull(model.getEdge(model.getVertex(PALMELA_STOP_CODE), model.getVertex(COINA_STOP_CODE)));

        Graph<Stop, Route> graph = new GraphEdgeList<>();
        model = new TransportMap(graph);
        list = (List<Edge<Route, Stop>>) model.edges();
        assertEquals(0, list.size());
    }

    @Test
    void getRoutes() {
        Collection<Route> collection = model.getRoutes();
        assertEquals(39, collection.size());

        assertTrue(model.areAdjacent(PALMELA_STOP_CODE, COINA_STOP_CODE));
        assertTrue(model.areAdjacent(CAIS_DO_SODRE_STOP_CODE, ALGES_STOP_CODE));
        assertNotNull(model.getEdge(model.getVertex(PALMELA_STOP_CODE), model.getVertex(COINA_STOP_CODE)));

        Graph<Stop, Route> graph = new GraphEdgeList<>();
        model = new TransportMap(graph);
        collection = model.getRoutes();
        assertEquals(0, collection.size());
    }

    @Test
    void areAdjacentByStopCode() {
        assertTrue(model.areAdjacent(PALMELA_STOP_CODE, COINA_STOP_CODE));
        assertTrue(model.areAdjacent(CAIS_DO_SODRE_STOP_CODE, ALGES_STOP_CODE));

        assertFalse(model.areAdjacent(CAIS_DO_SODRE_STOP_CODE, COINA_STOP_CODE));
        assertFalse(model.areAdjacent(ALGES_STOP_CODE, PALMELA_STOP_CODE));

        assertFalse(model.areAdjacent("Invalid", COINA_STOP_CODE));
        assertFalse(model.areAdjacent(CAIS_DO_SODRE_STOP_CODE, "Invalid"));
        assertFalse(model.areAdjacent("Invalid", "Invalid"));
        assertFalse(model.areAdjacent((String) null, COINA_STOP_CODE));
        assertFalse(model.areAdjacent(CAIS_DO_SODRE_STOP_CODE, (String) null));
        assertFalse(model.areAdjacent((String) null, (String) null));
    }

    @Test
    void areAdjacentByStop() {
        Stop caisDoSodre = new Stop(CAIS_DO_SODRE_STOP_CODE, "Cais do Sodré", 0, 0);
        Stop alges = new Stop(ALGES_STOP_CODE, "Algés", 0, 0);
        Stop coina = new Stop(COINA_STOP_CODE, "Coina", 0, 0);
        Stop palmela = new Stop(PALMELA_STOP_CODE, "Palmela", 0, 0);

        assertTrue(model.areAdjacent(caisDoSodre, alges));
        assertTrue(model.areAdjacent(coina, palmela));

        assertFalse(model.areAdjacent(caisDoSodre, coina));
        assertFalse(model.areAdjacent(alges, palmela));
        assertFalse(model.areAdjacent(new Stop("Invalid", "Invalid", 0, 0), coina));
        assertFalse(model.areAdjacent(caisDoSodre, new Stop("Invalid", "Invalid", 0, 0)));
        assertFalse(model.areAdjacent(new Stop("Invalid", "Invalid", 0, 0), new Stop("Invalid", "Invalid", 0, 0)));
        assertFalse(model.areAdjacent((Stop) null, coina));
        assertFalse(model.areAdjacent(caisDoSodre, (Stop) null));
        assertFalse(model.areAdjacent((Stop) null, (Stop) null));
    }

    @Test
    void incidentEdges() {
        Vertex<Stop> vertex = model.getVertex(CAIS_DO_SODRE_STOP_CODE);
        Collection<Edge<Route, Stop>> collection = model.incidentEdges(vertex);
        assertEquals(5, collection.size());
        assertTrue(model.areAdjacent(CAIS_DO_SODRE_STOP_CODE, ALGES_STOP_CODE));
        assertTrue(collection.contains(model.getEdge(model.getVertex(CAIS_DO_SODRE_STOP_CODE), model.getVertex(ALGES_STOP_CODE))));

        vertex = model.getVertex(ALGES_STOP_CODE);
        collection = model.incidentEdges(vertex);
        assertEquals(3, collection.size());
        assertTrue(model.areAdjacent(ALGES_STOP_CODE, CAIS_DO_SODRE_STOP_CODE));
        assertTrue(collection.contains(model.getEdge(model.getVertex(ALGES_STOP_CODE), model.getVertex(CAIS_DO_SODRE_STOP_CODE))));

        vertex = model.getVertex(COINA_STOP_CODE);
        collection = model.incidentEdges(vertex);
        assertEquals(4, collection.size());
        assertTrue(model.areAdjacent(COINA_STOP_CODE, PALMELA_STOP_CODE));
        assertTrue(collection.contains(model.getEdge(model.getVertex(COINA_STOP_CODE), model.getVertex(PALMELA_STOP_CODE))));

        vertex = model.getVertex(PALMELA_STOP_CODE);
        collection = model.incidentEdges(vertex);
        assertEquals(3, collection.size());
        assertTrue(model.areAdjacent(PALMELA_STOP_CODE, COINA_STOP_CODE));
        assertTrue(collection.contains(model.getEdge(model.getVertex(PALMELA_STOP_CODE), model.getVertex(COINA_STOP_CODE))));

        Graph<Stop, Route> graph = new GraphEdgeList<>();
        model = new TransportMap(graph);
        assertThrows(InvalidVertexException.class, () -> model.incidentEdges(model.getVertex(PALMELA_STOP_CODE)));
    }

    @Test
    void getEdge() {
        // true routes
        Vertex<Stop> vertex1 = model.getVertex(CAIS_DO_SODRE_STOP_CODE);
        Vertex<Stop> vertex2 = model.getVertex(ALGES_STOP_CODE);
        Edge<Route, Stop> edge12 = model.getEdge(vertex1, vertex2);
        Edge<Route, Stop> edge21 = model.getEdge(vertex2, vertex1);
        assertNotNull(edge12);
        assertNotNull(edge21);
        assertEquals(edge12.element(), edge21.element());

        Vertex<Stop> vertex3 = model.getVertex(COINA_STOP_CODE);
        Vertex<Stop> vertex4 = model.getVertex(PALMELA_STOP_CODE);
        Edge<Route, Stop> edge34 = model.getEdge(vertex3, vertex4);
        Edge<Route, Stop> edge43 = model.getEdge(vertex4, vertex3);
        assertNotNull(edge34);
        assertNotNull(edge43);
        assertEquals(edge34.element(), edge43.element());

        // false routes
        assertNull(model.getEdge(vertex1, vertex3));
        assertNull(model.getEdge(vertex2, vertex4));

        assertThrows(InvalidVertexException.class, () -> model.getEdge(model.getVertex("Invalid"), vertex3));
        assertThrows(InvalidVertexException.class, () -> model.getEdge(vertex1, model.getVertex("Invalid")));

        Graph<Stop, Route> graph = new GraphEdgeList<>();
        model = new TransportMap(graph);
        assertThrows(InvalidVertexException.class, () -> model.getEdge(model.getVertex(PALMELA_STOP_CODE), vertex3));
    }

    @Test
    void areNotAdjacent() {
        Vertex<Stop> vertex1 = model.getVertex(PALMELA_STOP_CODE);
        Vertex<Stop> vertex2 = model.getVertex(COINA_STOP_CODE);
        Vertex<Stop> vertex3 = model.getVertex(CAIS_DO_SODRE_STOP_CODE);
        Vertex<Stop> vertex4 = model.getVertex(ALGES_STOP_CODE);

        assertFalse(model.areNotAdjacent(vertex1, vertex2));
        assertFalse(model.areNotAdjacent(vertex2, vertex1));
        assertFalse(model.areNotAdjacent(vertex3, vertex4));
        assertFalse(model.areNotAdjacent(vertex4, vertex3));

        assertTrue(model.areNotAdjacent(vertex3, vertex2));
        assertTrue(model.areNotAdjacent(vertex2, vertex3));
        assertTrue(model.areNotAdjacent(vertex4, vertex1));
        assertTrue(model.areNotAdjacent(vertex1, vertex4));

        assertTrue(model.areNotAdjacent(model.getVertex("Invalid"), vertex2));
        assertTrue(model.areNotAdjacent(vertex3, model.getVertex("Invalid")));
        assertTrue(model.areNotAdjacent(model.getVertex("Invalid"), model.getVertex("Invalid")));
        assertTrue(model.areNotAdjacent((Vertex<Stop>) null, vertex2));
        assertTrue(model.areNotAdjacent(vertex3, (Vertex<Stop>) null));
        assertTrue(model.areNotAdjacent((Vertex<Stop>) null, (Vertex<Stop>) null));
    }

    @Test
    void areAdjacentByVertex() {
        Vertex<Stop> vertex1 = model.getVertex(PALMELA_STOP_CODE);
        Vertex<Stop> vertex2 = model.getVertex(COINA_STOP_CODE);
        Vertex<Stop> vertex3 = model.getVertex(CAIS_DO_SODRE_STOP_CODE);
        Vertex<Stop> vertex4 = model.getVertex(ALGES_STOP_CODE);

        assertTrue(model.areAdjacent(vertex1, vertex2));
        assertTrue(model.areAdjacent(vertex2, vertex1));
        assertTrue(model.areAdjacent(vertex3, vertex4));
        assertTrue(model.areAdjacent(vertex4, vertex3));

        assertFalse(model.areAdjacent(vertex3, vertex2));
        assertFalse(model.areAdjacent(vertex2, vertex3));
        assertFalse(model.areAdjacent(vertex4, vertex1));
        assertFalse(model.areAdjacent(vertex1, vertex4));

        assertFalse(model.areAdjacent(model.getVertex("Invalid"), vertex2));
        assertFalse(model.areAdjacent(vertex3, model.getVertex("Invalid")));
        assertFalse(model.areAdjacent(model.getVertex("Invalid"), model.getVertex("Invalid")));
        assertFalse(model.areAdjacent((Vertex<Stop>) null, vertex2));
        assertFalse(model.areAdjacent(vertex3, (Vertex<Stop>) null));
        assertFalse(model.areAdjacent((Vertex<Stop>) null, (Vertex<Stop>) null));
    }

    @Test
    void opposite() {
        Vertex<Stop> vertex1 = model.getVertex(PALMELA_STOP_CODE);
        Vertex<Stop> vertex2 = model.getVertex(COINA_STOP_CODE);
        Vertex<Stop> vertex3 = model.getVertex(CAIS_DO_SODRE_STOP_CODE);
        Vertex<Stop> vertex4 = model.getVertex(ALGES_STOP_CODE);

        Edge<Route, Stop> edge12 = model.getEdge(vertex1, vertex2);
        Edge<Route, Stop> edge21 = model.getEdge(vertex2, vertex1);
        assertEquals(edge21, edge12);

        Edge<Route, Stop> edge34 = model.getEdge(vertex3, vertex4);
        Edge<Route, Stop> edge43 = model.getEdge(vertex4, vertex3);
        assertEquals(edge43, edge34);

        assertEquals(vertex2, model.opposite(vertex1, edge12));
        assertEquals(vertex1, model.opposite(vertex2, edge12));
        assertEquals(vertex2, model.opposite(vertex1, edge21));
        assertEquals(vertex1, model.opposite(vertex2, edge21));

        assertEquals(vertex4, model.opposite(vertex3, edge34));
        assertEquals(vertex3, model.opposite(vertex4, edge34));
        assertEquals(vertex4, model.opposite(vertex3, edge43));
        assertEquals(vertex3, model.opposite(vertex4, edge43));

        assertThrows(InvalidVertexException.class, () -> model.opposite(null, edge12));
        assertThrows(InvalidEdgeException.class, () -> model.opposite(vertex1, null));
        assertThrows(InvalidVertexException.class, () -> model.opposite(null, null)); // vertice exception is first
        assertNull(model.opposite(vertex1, edge34)); // vertex not in edge
        assertNull(model.opposite(vertex4, edge12)); // vertex not in edge

        Graph<Stop, Route> graph = new GraphEdgeList<>();
        model = new TransportMap(graph);
        assertThrows(InvalidVertexException.class, () -> model.opposite(vertex1, edge12));
    }

    @Test
    void insertVertex() {
        Stop stop = new Stop("TEST", "Test", 0, 0);
        Vertex<Stop> vertex1 = model.insertVertex(stop);
        assertNotNull(vertex1);
        assertEquals(stop, vertex1.element());
        assertTrue(model.hasStop(stop));

        Vertex<Stop> vertex2 = model.insertVertex(stop);
        assertNotNull(vertex2);
        assertEquals(vertex1, vertex2);
        assertEquals(stop, vertex2.element());
        assertTrue(model.hasStop(stop));

        Stop stop2 = new Stop("TEST", "Test2", 5, 5);
        Vertex<Stop> vertex3 = model.insertVertex(stop2);
        assertNotNull(vertex3);
        assertEquals(stop2, vertex3.element());
        assertTrue(model.hasStop(stop2));
        assertEquals(vertex2, vertex3);
        assertEquals(vertex1, vertex3);

        vertex2 = model.insertVertex(null);
        assertNull(vertex2);

        Graph<Stop, Route> graph = new GraphEdgeList<>();
        model = new TransportMap(graph);
        vertex1 = model.insertVertex(stop);
        assertNotNull(vertex1);
        assertEquals(stop, vertex1.element());
        assertTrue(model.hasStop(stop));
    }

    @Test
    void insertEdge() {
        Stop stop1 = new Stop("TEST1", "Test1", 0, 0);
        Stop stop2 = new Stop("TEST2", "Test2", 0, 0);
        Route route = new Route();
        route.addTransport(TransportType.BUS, 4, 5, -3);

        Vertex<Stop> vertex1 = model.insertVertex(stop1);
        Vertex<Stop> vertex2 = model.insertVertex(stop2);
        Edge<Route, Stop> edge = model.insertEdge(stop1, stop2, route);
        assertNotNull(edge);
        assertEquals(route, edge.element());
        assertTrue(model.areAdjacent(vertex1, vertex2));
        assertEquals(edge, model.getEdge(vertex1, vertex2));

        Edge<Route, Stop> edge2 = model.insertEdge(stop1, stop2, route); // same connection return existing edge
        assertNotNull(edge2);
        assertEquals(edge, edge2);
        assertEquals(route, edge2.element());
        assertTrue(model.areAdjacent(vertex1, vertex2));
        assertEquals(edge2, model.getEdge(vertex1, vertex2));

        Edge<Route, Stop> edge3 = model.insertEdge(stop2, stop1, route); // reverse connection return existing edge
        assertNotNull(edge3);
        assertEquals(edge, edge3);
        assertEquals(route, edge3.element());
        assertTrue(model.areAdjacent(vertex2, vertex1));
        assertEquals(edge3, model.getEdge(vertex2, vertex1));

        Edge<Route, Stop> edge4 = model.insertEdge(stop1, stop2, null); // null edge element
        assertNull(edge4);

        assertThrows(InvalidVertexException.class, () -> model.insertEdge(null, stop2, route));
        assertThrows(InvalidVertexException.class, () -> model.insertEdge(stop1, null, route));

        Graph<Stop, Route> graph = new GraphEdgeList<>();
        model = new TransportMap(graph);
        assertTrue(model.getRoutes().isEmpty());
        vertex1 = model.insertVertex(stop1);
        vertex2 = model.insertVertex(stop2);
        edge = model.insertEdge(stop1, stop2, route);
        assertNotNull(edge);
        assertEquals(1, model.getRoutes().size());
        assertTrue(model.areAdjacent(stop1, stop2));
        assertEquals(route, model.getEdge(vertex1, vertex2).element());
    }

    @Test
    void removeVertex() {
        Stop stop1 = new Stop("TEST", "Test", 0, 0);
        Vertex<Stop> vertex1 = model.insertVertex(stop1);
        assertNotNull(vertex1);
        assertEquals(stop1, vertex1.element());
        assertTrue(model.hasStop(stop1));

        Stop stop2 = model.removeVertex(vertex1);
        assertNotNull(stop2);
        assertEquals(stop2, vertex1.element());
        assertEquals(stop1, stop2);
        assertFalse(model.hasStop(stop2));

        Vertex<Stop> finalVertex = vertex1;
        assertThrows(InvalidVertexException.class, () -> model.removeVertex(finalVertex));
        assertThrows(InvalidVertexException.class, () -> model.removeVertex(null));

        Graph<Stop, Route> graph = new GraphEdgeList<>();
        model = new TransportMap(graph);
        vertex1 = model.insertVertex(stop1);
        assertNotNull(vertex1);
        assertEquals(stop1, vertex1.element());
        assertTrue(model.hasStop(stop1));

        stop2 = model.removeVertex(vertex1);
        assertNotNull(stop2);
        assertEquals(stop2, vertex1.element());
        assertEquals(stop1, stop2);
        assertFalse(model.hasStop(stop2));
    }

    @Test
    void testToString() {
        Graph<Stop, Route> graph = new GraphEdgeList<>();
        model = new TransportMap(graph);
        assertEquals("Graph with 0 vertices and 0 edges:\n--- Vertices: \n\n--- Edges: \n", model.toString());

        Stop stop1 = new Stop("TEST1", "Test1", 2, 2);
        Stop stop2 = new Stop("TEST2", "Test2", 3, 3);
        Route route = new Route();
        route.addTransport(TransportType.BUS, 4, 5, -3);
        route.addTransport(TransportType.WALK, 1, 2, -5);
        model.insertVertex(stop1);
        model.insertVertex(stop2);
        model.insertEdge(stop1, stop2, route);
        StringBuilder sb = new StringBuilder("Graph with 2 vertices and 1 edges:\n--- Vertices: \n\tVertex{Test1}\n\tVertex{Test2}\n\n--- Edges: \n\tEdge{{Transportes disponíveis: \n\n");
        sb.append("\t").append("Autocarro: ").append("\n");
        appendExpectedRouteInfo(sb, 4, 5, -3);
        sb.append("\n").append("\t").append("Caminhada: ").append("\n");
        appendExpectedRouteInfo(sb, 1, 2, -5);
        sb.append("}, vertexOutbound=Vertex{Test1}, vertexInbound=Vertex{Test2}}\n");
        assertEquals(sb.toString(), model.toString());
    }
}