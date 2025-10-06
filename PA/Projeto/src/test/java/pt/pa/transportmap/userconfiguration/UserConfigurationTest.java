package pt.pa.transportmap.userconfiguration;

import com.brunomnsilva.smartgraph.graph.Edge;
import com.brunomnsilva.smartgraph.graph.Graph;
import com.brunomnsilva.smartgraph.graph.GraphEdgeList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pt.pa.transportmap.Route;
import pt.pa.transportmap.Stop;
import pt.pa.transportmap.TransportMap;
import pt.pa.transportmap.TransportType;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for UserConfiguration
 */
class UserConfigurationTest {
    /** User configuration fixture */
    private UserConfiguration userConfiguration;
    /** The model */
    private TransportMap model;
    /** Palmela Stop Code */
    private static final String PALMELA_STOP_CODE = "PAL018";
    /** Coina Stop Code */
    private static final String COINA_STOP_CODE = "COI017";
    /** Cais do Sodré Stop Code */
    private static final String CAIS_DO_SODRE_STOP_CODE = "CAIS019";
    /** Algés Stop Code */
    private static final String ALGES_STOP_CODE = "ALG011";

    @BeforeEach
    void setUp() {
        Graph<Stop, Route> graph = new GraphEdgeList<>();
        model = new TransportMap(graph);
        model.update();
        EdgeConverter edgeConverter = new EdgeConverter(model);
        userConfiguration = new UserConfiguration(edgeConverter);
    }

    @Test
    void getBicycleDurationScale() {
        assertEquals(1.0, userConfiguration.getBicycleDurationScale());
        userConfiguration.setBicycleDurationScale(0.25);
        assertEquals(0.25, userConfiguration.getBicycleDurationScale());
        userConfiguration.setBicycleDurationScale(2.0);
        assertEquals(2.0, userConfiguration.getBicycleDurationScale());
    }

    @Test
    void setBicycleDurationScale() {
        assertEquals(1.0, userConfiguration.getBicycleDurationScale());
        userConfiguration.setBicycleDurationScale(0.25);
        assertEquals(0.25, userConfiguration.getBicycleDurationScale());
        userConfiguration.setBicycleDurationScale(2.0);
        assertEquals(2.0, userConfiguration.getBicycleDurationScale());

        assertThrows(IllegalArgumentException.class, () -> userConfiguration.setBicycleDurationScale(0.24));
        assertThrows(IllegalArgumentException.class, () -> userConfiguration.setBicycleDurationScale(2.01));
    }

    @Test
    void applyBicycleDurationScale() {
        assertEquals(1.0, userConfiguration.getBicycleDurationScale());
        assertEquals(1.0, userConfiguration.applyBicycleDurationScale(1.0));

        userConfiguration.setBicycleDurationScale(0.25);
        assertEquals(0.25, userConfiguration.getBicycleDurationScale());
        assertEquals(0.25, userConfiguration.applyBicycleDurationScale(1.0));

        userConfiguration.setBicycleDurationScale(2.0);
        assertEquals(2.0, userConfiguration.getBicycleDurationScale());
        assertEquals(2.0, userConfiguration.applyBicycleDurationScale(1.0));
        assertEquals(4.0, userConfiguration.applyBicycleDurationScale(2.0));

        assertThrows(IllegalArgumentException.class, () -> userConfiguration.applyBicycleDurationScale(0.0));
        assertThrows(IllegalArgumentException.class, () -> userConfiguration.applyBicycleDurationScale(-1.0));
    }

    @Test
    void addDisabledRoute() {
        assertThrows(IllegalArgumentException.class, () -> userConfiguration.addDisabledRoute(null));
        Edge<Route, Stop> edge = model.getEdge(model.getVertex(PALMELA_STOP_CODE), model.getVertex(COINA_STOP_CODE));
        userConfiguration.addDisabledRoute(edge);
        assertTrue(userConfiguration.isRouteDisabled(edge));
        userConfiguration.removeDisabledRoute(edge);
        assertFalse(userConfiguration.isRouteDisabled(edge));

        edge = model.getEdge(model.getVertex(ALGES_STOP_CODE), model.getVertex(CAIS_DO_SODRE_STOP_CODE));
        userConfiguration.addDisabledRoute(edge);
        assertTrue(userConfiguration.isRouteDisabled(edge));
        userConfiguration.removeDisabledRoute(edge);
        assertFalse(userConfiguration.isRouteDisabled(edge));
    }

    @Test
    void removeDisabledRoute() {
        assertThrows(IllegalArgumentException.class, () -> userConfiguration.addDisabledRoute(null));
        Edge<Route, Stop> edge = model.getEdge(model.getVertex(PALMELA_STOP_CODE), model.getVertex(COINA_STOP_CODE));
        userConfiguration.addDisabledRoute(edge);
        assertTrue(userConfiguration.isRouteDisabled(edge));
        userConfiguration.removeDisabledRoute(edge);
        assertFalse(userConfiguration.isRouteDisabled(edge));

        edge = model.getEdge(model.getVertex(ALGES_STOP_CODE), model.getVertex(CAIS_DO_SODRE_STOP_CODE));
        userConfiguration.addDisabledRoute(edge);
        assertTrue(userConfiguration.isRouteDisabled(edge));
        userConfiguration.removeDisabledRoute(edge);
        assertFalse(userConfiguration.isRouteDisabled(edge));
    }

    @Test
    void addDisabledTransportType() {
        Edge<Route, Stop> edge = model.getEdge(model.getVertex(PALMELA_STOP_CODE), model.getVertex(COINA_STOP_CODE));
        assertThrows(IllegalArgumentException.class, () -> userConfiguration.addDisabledTransportType(null, null));
        assertThrows(IllegalArgumentException.class, () -> userConfiguration.addDisabledTransportType(edge, null));
        assertThrows(IllegalArgumentException.class, () -> userConfiguration.addDisabledTransportType(null, TransportType.BUS));

        userConfiguration.addDisabledTransportType(edge, TransportType.BUS);
        assertFalse(userConfiguration.isRouteDisabled(edge));
        assertTrue(userConfiguration.isTransportTypeDisabled(edge, TransportType.BUS));
        assertFalse(userConfiguration.isTransportTypeDisabled(edge, TransportType.TRAIN));
        assertFalse(userConfiguration.isTransportTypeDisabled(edge, TransportType.BOAT));
        assertFalse(userConfiguration.isTransportTypeDisabled(edge, TransportType.WALK));
        assertFalse(userConfiguration.isTransportTypeDisabled(edge, TransportType.BICYCLE));

        userConfiguration.addDisabledTransportType(edge, TransportType.TRAIN);
        assertFalse(userConfiguration.isRouteDisabled(edge));
        assertTrue(userConfiguration.isTransportTypeDisabled(edge, TransportType.BUS));
        assertTrue(userConfiguration.isTransportTypeDisabled(edge, TransportType.TRAIN));
        assertFalse(userConfiguration.isTransportTypeDisabled(edge, TransportType.BOAT));
        assertFalse(userConfiguration.isTransportTypeDisabled(edge, TransportType.WALK));
        assertFalse(userConfiguration.isTransportTypeDisabled(edge, TransportType.BICYCLE));

        userConfiguration.addDisabledTransportType(edge, TransportType.BOAT); // route has no boat transport type, but still set configuration
        assertFalse(userConfiguration.isRouteDisabled(edge));
        assertTrue(userConfiguration.isTransportTypeDisabled(edge, TransportType.BUS));
        assertTrue(userConfiguration.isTransportTypeDisabled(edge, TransportType.TRAIN));
        assertTrue(userConfiguration.isTransportTypeDisabled(edge, TransportType.BOAT));
        assertFalse(userConfiguration.isTransportTypeDisabled(edge, TransportType.WALK));
        assertFalse(userConfiguration.isTransportTypeDisabled(edge, TransportType.BICYCLE));

        userConfiguration.addDisabledTransportType(edge, TransportType.BUS); // already set, should not change
        assertFalse(userConfiguration.isRouteDisabled(edge));
        assertTrue(userConfiguration.isTransportTypeDisabled(edge, TransportType.BUS));
        assertTrue(userConfiguration.isTransportTypeDisabled(edge, TransportType.TRAIN));
        assertTrue(userConfiguration.isTransportTypeDisabled(edge, TransportType.BOAT));
        assertFalse(userConfiguration.isTransportTypeDisabled(edge, TransportType.WALK));
        assertFalse(userConfiguration.isTransportTypeDisabled(edge, TransportType.BICYCLE));

        userConfiguration.removeDisabledTransportType(edge, TransportType.BUS);
        assertFalse(userConfiguration.isRouteDisabled(edge));
        assertFalse(userConfiguration.isTransportTypeDisabled(edge, TransportType.BUS));
        assertTrue(userConfiguration.isTransportTypeDisabled(edge, TransportType.TRAIN));
        assertTrue(userConfiguration.isTransportTypeDisabled(edge, TransportType.BOAT));
        assertFalse(userConfiguration.isTransportTypeDisabled(edge, TransportType.WALK));
        assertFalse(userConfiguration.isTransportTypeDisabled(edge, TransportType.BICYCLE));

        userConfiguration.removeDisabledTransportType(edge, TransportType.TRAIN);
        assertFalse(userConfiguration.isRouteDisabled(edge));
        assertFalse(userConfiguration.isTransportTypeDisabled(edge, TransportType.BUS));
        assertFalse(userConfiguration.isTransportTypeDisabled(edge, TransportType.TRAIN));
        assertTrue(userConfiguration.isTransportTypeDisabled(edge, TransportType.BOAT));
        assertFalse(userConfiguration.isTransportTypeDisabled(edge, TransportType.WALK));
        assertFalse(userConfiguration.isTransportTypeDisabled(edge, TransportType.BICYCLE));

        userConfiguration.removeDisabledTransportType(edge, TransportType.BUS); // already removed, should not change
        assertFalse(userConfiguration.isRouteDisabled(edge));
        assertFalse(userConfiguration.isTransportTypeDisabled(edge, TransportType.BUS));
        assertFalse(userConfiguration.isTransportTypeDisabled(edge, TransportType.TRAIN));
        assertTrue(userConfiguration.isTransportTypeDisabled(edge, TransportType.BOAT));
        assertFalse(userConfiguration.isTransportTypeDisabled(edge, TransportType.WALK));
        assertFalse(userConfiguration.isTransportTypeDisabled(edge, TransportType.BICYCLE));
    }

    @Test
    void removeDisabledTransportType() {
        Edge<Route, Stop> edge = model.getEdge(model.getVertex(PALMELA_STOP_CODE), model.getVertex(COINA_STOP_CODE));
        assertThrows(IllegalArgumentException.class, () -> userConfiguration.addDisabledTransportType(null, null));
        assertThrows(IllegalArgumentException.class, () -> userConfiguration.addDisabledTransportType(edge, null));
        assertThrows(IllegalArgumentException.class, () -> userConfiguration.addDisabledTransportType(null, TransportType.BUS));

        userConfiguration.addDisabledTransportType(edge, TransportType.BUS);
        assertFalse(userConfiguration.isRouteDisabled(edge));
        assertTrue(userConfiguration.isTransportTypeDisabled(edge, TransportType.BUS));
        assertFalse(userConfiguration.isTransportTypeDisabled(edge, TransportType.TRAIN));
        assertFalse(userConfiguration.isTransportTypeDisabled(edge, TransportType.BOAT));
        assertFalse(userConfiguration.isTransportTypeDisabled(edge, TransportType.WALK));
        assertFalse(userConfiguration.isTransportTypeDisabled(edge, TransportType.BICYCLE));

        userConfiguration.addDisabledTransportType(edge, TransportType.TRAIN);
        assertFalse(userConfiguration.isRouteDisabled(edge));
        assertTrue(userConfiguration.isTransportTypeDisabled(edge, TransportType.BUS));
        assertTrue(userConfiguration.isTransportTypeDisabled(edge, TransportType.TRAIN));
        assertFalse(userConfiguration.isTransportTypeDisabled(edge, TransportType.BOAT));
        assertFalse(userConfiguration.isTransportTypeDisabled(edge, TransportType.WALK));
        assertFalse(userConfiguration.isTransportTypeDisabled(edge, TransportType.BICYCLE));

        userConfiguration.addDisabledTransportType(edge, TransportType.BOAT); // route has no boat transport type, but still set configuration
        assertFalse(userConfiguration.isRouteDisabled(edge));
        assertTrue(userConfiguration.isTransportTypeDisabled(edge, TransportType.BUS));
        assertTrue(userConfiguration.isTransportTypeDisabled(edge, TransportType.TRAIN));
        assertTrue(userConfiguration.isTransportTypeDisabled(edge, TransportType.BOAT));
        assertFalse(userConfiguration.isTransportTypeDisabled(edge, TransportType.WALK));
        assertFalse(userConfiguration.isTransportTypeDisabled(edge, TransportType.BICYCLE));

        userConfiguration.addDisabledTransportType(edge, TransportType.BUS); // already set, should not change
        assertFalse(userConfiguration.isRouteDisabled(edge));
        assertTrue(userConfiguration.isTransportTypeDisabled(edge, TransportType.BUS));
        assertTrue(userConfiguration.isTransportTypeDisabled(edge, TransportType.TRAIN));
        assertTrue(userConfiguration.isTransportTypeDisabled(edge, TransportType.BOAT));
        assertFalse(userConfiguration.isTransportTypeDisabled(edge, TransportType.WALK));
        assertFalse(userConfiguration.isTransportTypeDisabled(edge, TransportType.BICYCLE));

        userConfiguration.removeDisabledTransportType(edge, TransportType.BUS);
        assertFalse(userConfiguration.isRouteDisabled(edge));
        assertFalse(userConfiguration.isTransportTypeDisabled(edge, TransportType.BUS));
        assertTrue(userConfiguration.isTransportTypeDisabled(edge, TransportType.TRAIN));
        assertTrue(userConfiguration.isTransportTypeDisabled(edge, TransportType.BOAT));
        assertFalse(userConfiguration.isTransportTypeDisabled(edge, TransportType.WALK));
        assertFalse(userConfiguration.isTransportTypeDisabled(edge, TransportType.BICYCLE));

        userConfiguration.removeDisabledTransportType(edge, TransportType.TRAIN);
        assertFalse(userConfiguration.isRouteDisabled(edge));
        assertFalse(userConfiguration.isTransportTypeDisabled(edge, TransportType.BUS));
        assertFalse(userConfiguration.isTransportTypeDisabled(edge, TransportType.TRAIN));
        assertTrue(userConfiguration.isTransportTypeDisabled(edge, TransportType.BOAT));
        assertFalse(userConfiguration.isTransportTypeDisabled(edge, TransportType.WALK));
        assertFalse(userConfiguration.isTransportTypeDisabled(edge, TransportType.BICYCLE));

        userConfiguration.removeDisabledTransportType(edge, TransportType.BUS); // already removed, should not change
        assertFalse(userConfiguration.isRouteDisabled(edge));
        assertFalse(userConfiguration.isTransportTypeDisabled(edge, TransportType.BUS));
        assertFalse(userConfiguration.isTransportTypeDisabled(edge, TransportType.TRAIN));
        assertTrue(userConfiguration.isTransportTypeDisabled(edge, TransportType.BOAT));
        assertFalse(userConfiguration.isTransportTypeDisabled(edge, TransportType.WALK));
        assertFalse(userConfiguration.isTransportTypeDisabled(edge, TransportType.BICYCLE));
    }

    @Test
    void resetDisabledRoutes() {
        Edge<Route, Stop> edge = model.getEdge(model.getVertex(PALMELA_STOP_CODE), model.getVertex(COINA_STOP_CODE));
        userConfiguration.addDisabledRoute(edge);
        userConfiguration.addDisabledTransportType(edge, TransportType.BUS);
        assertTrue(userConfiguration.isRouteDisabled(edge));
        assertTrue(userConfiguration.isTransportTypeDisabled(edge, TransportType.BUS));
        assertFalse(userConfiguration.isTransportTypeDisabled(edge, TransportType.TRAIN));
        assertFalse(userConfiguration.isTransportTypeDisabled(edge, TransportType.BOAT));
        assertFalse(userConfiguration.isTransportTypeDisabled(edge, TransportType.WALK));
        assertFalse(userConfiguration.isTransportTypeDisabled(edge, TransportType.BICYCLE));

        Edge<Route, Stop> edge2 = model.getEdge(model.getVertex(ALGES_STOP_CODE), model.getVertex(CAIS_DO_SODRE_STOP_CODE));
        userConfiguration.addDisabledTransportType(edge2, TransportType.TRAIN);
        assertFalse(userConfiguration.isRouteDisabled(edge2));
        assertTrue(userConfiguration.isTransportTypeDisabled(edge2, TransportType.TRAIN));
        assertFalse(userConfiguration.isTransportTypeDisabled(edge2, TransportType.BUS));
        assertFalse(userConfiguration.isTransportTypeDisabled(edge2, TransportType.BOAT));
        assertFalse(userConfiguration.isTransportTypeDisabled(edge2, TransportType.WALK));
        assertFalse(userConfiguration.isTransportTypeDisabled(edge2, TransportType.BICYCLE));

        userConfiguration.resetDisabledRoutes();
        assertFalse(userConfiguration.isRouteDisabled(edge));
        assertFalse(userConfiguration.isTransportTypeDisabled(edge, TransportType.BUS));
        assertFalse(userConfiguration.isTransportTypeDisabled(edge, TransportType.TRAIN));
        assertFalse(userConfiguration.isTransportTypeDisabled(edge, TransportType.BOAT));
        assertFalse(userConfiguration.isTransportTypeDisabled(edge, TransportType.WALK));
        assertFalse(userConfiguration.isTransportTypeDisabled(edge, TransportType.BICYCLE));

        assertFalse(userConfiguration.isRouteDisabled(edge2));
        assertFalse(userConfiguration.isTransportTypeDisabled(edge2, TransportType.TRAIN));
        assertFalse(userConfiguration.isTransportTypeDisabled(edge2, TransportType.BUS));
        assertFalse(userConfiguration.isTransportTypeDisabled(edge2, TransportType.BOAT));
        assertFalse(userConfiguration.isTransportTypeDisabled(edge2, TransportType.WALK));
        assertFalse(userConfiguration.isTransportTypeDisabled(edge2, TransportType.BICYCLE));
    }

    @Test
    void isRouteDisabled() {
        assertThrows(IllegalArgumentException.class, () -> userConfiguration.addDisabledRoute(null));
        Edge<Route, Stop> edge = model.getEdge(model.getVertex(PALMELA_STOP_CODE), model.getVertex(COINA_STOP_CODE));
        assertFalse(userConfiguration.isRouteDisabled(edge));
        userConfiguration.addDisabledRoute(edge);
        assertTrue(userConfiguration.isRouteDisabled(edge));
        userConfiguration.removeDisabledRoute(edge);
        assertFalse(userConfiguration.isRouteDisabled(edge));

        edge = model.getEdge(model.getVertex(ALGES_STOP_CODE), model.getVertex(CAIS_DO_SODRE_STOP_CODE));
        assertFalse(userConfiguration.isRouteDisabled(edge));
        userConfiguration.addDisabledRoute(edge);
        assertTrue(userConfiguration.isRouteDisabled(edge));
        userConfiguration.removeDisabledRoute(edge);
        assertFalse(userConfiguration.isRouteDisabled(edge));
    }

    @Test
    void isTransportTypeDisabled() {
        Edge<Route, Stop> edge = model.getEdge(model.getVertex(PALMELA_STOP_CODE), model.getVertex(COINA_STOP_CODE));
        assertThrows(IllegalArgumentException.class, () -> userConfiguration.addDisabledTransportType(null, null));
        assertThrows(IllegalArgumentException.class, () -> userConfiguration.addDisabledTransportType(edge, null));
        assertThrows(IllegalArgumentException.class, () -> userConfiguration.addDisabledTransportType(null, TransportType.BUS));

        userConfiguration.addDisabledTransportType(edge, TransportType.BUS);
        assertFalse(userConfiguration.isRouteDisabled(edge));
        assertTrue(userConfiguration.isTransportTypeDisabled(edge, TransportType.BUS));
        assertFalse(userConfiguration.isTransportTypeDisabled(edge, TransportType.TRAIN));
        assertFalse(userConfiguration.isTransportTypeDisabled(edge, TransportType.BOAT));
        assertFalse(userConfiguration.isTransportTypeDisabled(edge, TransportType.WALK));
        assertFalse(userConfiguration.isTransportTypeDisabled(edge, TransportType.BICYCLE));

        userConfiguration.addDisabledTransportType(edge, TransportType.TRAIN);
        assertFalse(userConfiguration.isRouteDisabled(edge));
        assertTrue(userConfiguration.isTransportTypeDisabled(edge, TransportType.BUS));
        assertTrue(userConfiguration.isTransportTypeDisabled(edge, TransportType.TRAIN));
        assertFalse(userConfiguration.isTransportTypeDisabled(edge, TransportType.BOAT));
        assertFalse(userConfiguration.isTransportTypeDisabled(edge, TransportType.WALK));
        assertFalse(userConfiguration.isTransportTypeDisabled(edge, TransportType.BICYCLE));

        userConfiguration.addDisabledTransportType(edge, TransportType.BOAT); // route has no boat transport type, but still set configuration
        assertFalse(userConfiguration.isRouteDisabled(edge));
        assertTrue(userConfiguration.isTransportTypeDisabled(edge, TransportType.BUS));
        assertTrue(userConfiguration.isTransportTypeDisabled(edge, TransportType.TRAIN));
        assertTrue(userConfiguration.isTransportTypeDisabled(edge, TransportType.BOAT));
        assertFalse(userConfiguration.isTransportTypeDisabled(edge, TransportType.WALK));
        assertFalse(userConfiguration.isTransportTypeDisabled(edge, TransportType.BICYCLE));

        userConfiguration.addDisabledTransportType(edge, TransportType.BUS); // already set, should not change
        assertFalse(userConfiguration.isRouteDisabled(edge));
        assertTrue(userConfiguration.isTransportTypeDisabled(edge, TransportType.BUS));
        assertTrue(userConfiguration.isTransportTypeDisabled(edge, TransportType.TRAIN));
        assertTrue(userConfiguration.isTransportTypeDisabled(edge, TransportType.BOAT));
        assertFalse(userConfiguration.isTransportTypeDisabled(edge, TransportType.WALK));
        assertFalse(userConfiguration.isTransportTypeDisabled(edge, TransportType.BICYCLE));

        userConfiguration.removeDisabledTransportType(edge, TransportType.BUS);
        assertFalse(userConfiguration.isRouteDisabled(edge));
        assertFalse(userConfiguration.isTransportTypeDisabled(edge, TransportType.BUS));
        assertTrue(userConfiguration.isTransportTypeDisabled(edge, TransportType.TRAIN));
        assertTrue(userConfiguration.isTransportTypeDisabled(edge, TransportType.BOAT));
        assertFalse(userConfiguration.isTransportTypeDisabled(edge, TransportType.WALK));
        assertFalse(userConfiguration.isTransportTypeDisabled(edge, TransportType.BICYCLE));

        userConfiguration.removeDisabledTransportType(edge, TransportType.TRAIN);
        assertFalse(userConfiguration.isRouteDisabled(edge));
        assertFalse(userConfiguration.isTransportTypeDisabled(edge, TransportType.BUS));
        assertFalse(userConfiguration.isTransportTypeDisabled(edge, TransportType.TRAIN));
        assertTrue(userConfiguration.isTransportTypeDisabled(edge, TransportType.BOAT));
        assertFalse(userConfiguration.isTransportTypeDisabled(edge, TransportType.WALK));
        assertFalse(userConfiguration.isTransportTypeDisabled(edge, TransportType.BICYCLE));

        userConfiguration.removeDisabledTransportType(edge, TransportType.BUS); // already removed, should not change
        assertFalse(userConfiguration.isRouteDisabled(edge));
        assertFalse(userConfiguration.isTransportTypeDisabled(edge, TransportType.BUS));
        assertFalse(userConfiguration.isTransportTypeDisabled(edge, TransportType.TRAIN));
        assertTrue(userConfiguration.isTransportTypeDisabled(edge, TransportType.BOAT));
        assertFalse(userConfiguration.isTransportTypeDisabled(edge, TransportType.WALK));
        assertFalse(userConfiguration.isTransportTypeDisabled(edge, TransportType.BICYCLE));
    }

    @Test
    void saveToFile() {
        Edge<Route, Stop> edge = model.getEdge(model.getVertex(PALMELA_STOP_CODE), model.getVertex(COINA_STOP_CODE));
        userConfiguration.addDisabledRoute(edge);
        userConfiguration.addDisabledTransportType(edge, TransportType.BUS);
        Edge<Route, Stop> edge2 = model.getEdge(model.getVertex(ALGES_STOP_CODE), model.getVertex(CAIS_DO_SODRE_STOP_CODE));
        userConfiguration.addDisabledTransportType(edge2, TransportType.TRAIN);
        userConfiguration.saveToFile();

        UserConfiguration loadedUserConfiguration = UserConfiguration.loadFromFile(model);
        assertTrue(loadedUserConfiguration.isRouteDisabled(edge));
        assertTrue(loadedUserConfiguration.isTransportTypeDisabled(edge, TransportType.BUS));
        assertFalse(loadedUserConfiguration.isTransportTypeDisabled(edge, TransportType.TRAIN));
        assertFalse(loadedUserConfiguration.isTransportTypeDisabled(edge, TransportType.BOAT));
        assertFalse(loadedUserConfiguration.isTransportTypeDisabled(edge, TransportType.WALK));
        assertFalse(loadedUserConfiguration.isTransportTypeDisabled(edge, TransportType.BICYCLE));

        assertFalse(loadedUserConfiguration.isRouteDisabled(edge2));
        assertTrue(loadedUserConfiguration.isTransportTypeDisabled(edge2, TransportType.TRAIN));
        assertFalse(loadedUserConfiguration.isTransportTypeDisabled(edge2, TransportType.BUS));
        assertFalse(loadedUserConfiguration.isTransportTypeDisabled(edge2, TransportType.BOAT));
        assertFalse(loadedUserConfiguration.isTransportTypeDisabled(edge2, TransportType.WALK));
        assertFalse(loadedUserConfiguration.isTransportTypeDisabled(edge2, TransportType.BICYCLE));
    }

    @Test
    void loadFromFile() {
        Edge<Route, Stop> edge = model.getEdge(model.getVertex(PALMELA_STOP_CODE), model.getVertex(COINA_STOP_CODE));
        userConfiguration.addDisabledRoute(edge);
        userConfiguration.addDisabledTransportType(edge, TransportType.BUS);
        Edge<Route, Stop> edge2 = model.getEdge(model.getVertex(ALGES_STOP_CODE), model.getVertex(CAIS_DO_SODRE_STOP_CODE));
        userConfiguration.addDisabledTransportType(edge2, TransportType.TRAIN);
        userConfiguration.saveToFile();

        UserConfiguration loadedUserConfiguration = UserConfiguration.loadFromFile(model);
        assertTrue(loadedUserConfiguration.isRouteDisabled(edge));
        assertTrue(loadedUserConfiguration.isTransportTypeDisabled(edge, TransportType.BUS));
        assertFalse(loadedUserConfiguration.isTransportTypeDisabled(edge, TransportType.TRAIN));
        assertFalse(loadedUserConfiguration.isTransportTypeDisabled(edge, TransportType.BOAT));
        assertFalse(loadedUserConfiguration.isTransportTypeDisabled(edge, TransportType.WALK));
        assertFalse(loadedUserConfiguration.isTransportTypeDisabled(edge, TransportType.BICYCLE));

        assertFalse(loadedUserConfiguration.isRouteDisabled(edge2));
        assertTrue(loadedUserConfiguration.isTransportTypeDisabled(edge2, TransportType.TRAIN));
        assertFalse(loadedUserConfiguration.isTransportTypeDisabled(edge2, TransportType.BUS));
        assertFalse(loadedUserConfiguration.isTransportTypeDisabled(edge2, TransportType.BOAT));
        assertFalse(loadedUserConfiguration.isTransportTypeDisabled(edge2, TransportType.WALK));
        assertFalse(loadedUserConfiguration.isTransportTypeDisabled(edge2, TransportType.BICYCLE));
    }

    @Test
    void getDisabledRoutes() {
        Edge<Route, Stop> edge = model.getEdge(model.getVertex(PALMELA_STOP_CODE), model.getVertex(COINA_STOP_CODE));
        userConfiguration.addDisabledRoute(edge);
        userConfiguration.addDisabledTransportType(edge, TransportType.BUS);
        Edge<Route, Stop> edge2 = model.getEdge(model.getVertex(ALGES_STOP_CODE), model.getVertex(CAIS_DO_SODRE_STOP_CODE));
        userConfiguration.addDisabledTransportType(edge2, TransportType.TRAIN);
        assertEquals("Vertex{Palmela} -> Vertex{Coina}", userConfiguration.getDisabledRoutes());
        userConfiguration.addDisabledRoute(edge2);
        assertEquals("Vertex{Palmela} -> Vertex{Coina}\nVertex{Cais do Sodré} -> Vertex{Algés}", userConfiguration.getDisabledRoutes());
    }

    @Test
    void getDisabledTransportTypes() {
        Edge<Route, Stop> edge = model.getEdge(model.getVertex(PALMELA_STOP_CODE), model.getVertex(COINA_STOP_CODE));
        userConfiguration.addDisabledRoute(edge);
        userConfiguration.addDisabledTransportType(edge, TransportType.BUS);
        Edge<Route, Stop> edge2 = model.getEdge(model.getVertex(ALGES_STOP_CODE), model.getVertex(CAIS_DO_SODRE_STOP_CODE));
        userConfiguration.addDisabledTransportType(edge2, TransportType.TRAIN);
        assertEquals("Vertex{Palmela} -> Vertex{Coina} : Autocarro\nVertex{Cais do Sodré} -> Vertex{Algés} : Comboio", userConfiguration.getDisabledTransportTypes());
        userConfiguration.addDisabledTransportType(edge2, TransportType.BUS);
        assertEquals("Vertex{Palmela} -> Vertex{Coina} : Autocarro\nVertex{Cais do Sodré} -> Vertex{Algés} : Autocarro Comboio", userConfiguration.getDisabledTransportTypes());
    }
}