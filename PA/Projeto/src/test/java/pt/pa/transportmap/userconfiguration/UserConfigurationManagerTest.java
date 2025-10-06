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
 * Test class for UserConfigurationManager
 */
class UserConfigurationManagerTest {
    /** UserConfigurationManager to test */
    UserConfigurationManager userConfigurationManager;
    /** UserConfiguration to test */
    UserConfiguration userConfiguration;
    /** The model */
    TransportMap model;
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
        userConfigurationManager = new UserConfigurationManager();
        Graph<Stop, Route> graph = new GraphEdgeList<>();
        model = new TransportMap(graph);
        model.update();
        userConfiguration = new UserConfiguration(new EdgeConverter(model));
    }

    @Test
    void execute() {
        Edge<Route, Stop> edge1 = model.getEdge(model.getVertex(PALMELA_STOP_CODE), model.getVertex(COINA_STOP_CODE));
        Edge<Route, Stop> edge2 = model.getEdge(model.getVertex(ALGES_STOP_CODE), model.getVertex(CAIS_DO_SODRE_STOP_CODE));
        RouteCommand routeCommand = new DisableRouteCommand(userConfiguration, edge1);
        userConfigurationManager.execute(routeCommand);
        assertEquals("Desactivar rota Palmela -> Coina", userConfigurationManager.getLastRouteCommand(RouteCommand.class));
        routeCommand = new EnableRouteCommand(userConfiguration, edge1);
        userConfigurationManager.execute(routeCommand);
        assertEquals("Activar rota Palmela -> Coina", userConfigurationManager.getLastRouteCommand(RouteCommand.class));
        routeCommand = new DisableRouteCommand(userConfiguration, edge2);
        userConfigurationManager.execute(routeCommand);
        assertEquals("Desactivar rota Cais do Sodré -> Algés", userConfigurationManager.getLastRouteCommand(RouteCommand.class));

        RouteTransportTypeCommand routeTransportTypeCommand = new DisableRouteTransportTypeCommand(userConfiguration, edge1, TransportType.BUS);
        userConfigurationManager.execute(routeTransportTypeCommand);
        assertEquals("Desactivar tipo de transporte Autocarro na rota Palmela -> Coina", userConfigurationManager.getLastRouteCommand(RouteTransportTypeCommand.class));
        routeTransportTypeCommand = new EnableRouteTransportTypeCommand(userConfiguration, edge1, TransportType.BUS);
        userConfigurationManager.execute(routeTransportTypeCommand);
        assertEquals("Activar tipo de transporte Autocarro na rota Palmela -> Coina", userConfigurationManager.getLastRouteCommand(RouteTransportTypeCommand.class));
        routeTransportTypeCommand = new DisableRouteTransportTypeCommand(userConfiguration, edge2, TransportType.BUS);
        userConfigurationManager.execute(routeTransportTypeCommand);
        assertEquals("Desactivar tipo de transporte Autocarro na rota Cais do Sodré -> Algés", userConfigurationManager.getLastRouteCommand(RouteTransportTypeCommand.class));
    }

    @Test
    void undo() {
        Edge<Route, Stop> edge1 = model.getEdge(model.getVertex(PALMELA_STOP_CODE), model.getVertex(COINA_STOP_CODE));
        Edge<Route, Stop> edge2 = model.getEdge(model.getVertex(ALGES_STOP_CODE), model.getVertex(CAIS_DO_SODRE_STOP_CODE));
        RouteCommand routeCommand = new DisableRouteCommand(userConfiguration, edge1);
        userConfigurationManager.execute(routeCommand);
        routeCommand = new DisableRouteCommand(userConfiguration, edge2);
        userConfigurationManager.execute(routeCommand);
        userConfigurationManager.undo(RouteCommand.class);
        assertEquals("Desactivar rota Palmela -> Coina", userConfigurationManager.getLastRouteCommand(RouteCommand.class));

        RouteTransportTypeCommand routeTransportTypeCommand = new DisableRouteTransportTypeCommand(userConfiguration, edge1, TransportType.BUS);
        userConfigurationManager.execute(routeTransportTypeCommand);
        routeTransportTypeCommand = new DisableRouteTransportTypeCommand(userConfiguration, edge2, TransportType.BUS);
        userConfigurationManager.execute(routeTransportTypeCommand);
        userConfigurationManager.undo(RouteTransportTypeCommand.class);
        assertEquals("Desactivar tipo de transporte Autocarro na rota Palmela -> Coina", userConfigurationManager.getLastRouteCommand(RouteTransportTypeCommand.class));
    }

    @Test
    void resetDisabledRoutes() {
        Edge<Route, Stop> edge1 = model.getEdge(model.getVertex(PALMELA_STOP_CODE), model.getVertex(COINA_STOP_CODE));
        Edge<Route, Stop> edge2 = model.getEdge(model.getVertex(ALGES_STOP_CODE), model.getVertex(CAIS_DO_SODRE_STOP_CODE));
        RouteCommand routeCommand = new DisableRouteCommand(userConfiguration, edge1);
        userConfigurationManager.execute(routeCommand);
        routeCommand = new DisableRouteCommand(userConfiguration, edge2);
        userConfigurationManager.execute(routeCommand);
        userConfigurationManager.resetDisabledRoutes(userConfiguration);
        assertNull(userConfigurationManager.getLastRouteCommand(RouteCommand.class));
        assertNull(userConfigurationManager.getLastRouteCommand(RouteTransportTypeCommand.class));
    }

    @Test
    void testToString() {
        Edge<Route, Stop> edge1 = model.getEdge(model.getVertex(PALMELA_STOP_CODE), model.getVertex(COINA_STOP_CODE));
        Edge<Route, Stop> edge2 = model.getEdge(model.getVertex(ALGES_STOP_CODE), model.getVertex(CAIS_DO_SODRE_STOP_CODE));
        RouteCommand routeCommand = new DisableRouteCommand(userConfiguration, edge1);
        userConfigurationManager.execute(routeCommand);

    }

    @Test
    void getLastRouteCommand() {
        Edge<Route, Stop> edge1 = model.getEdge(model.getVertex(PALMELA_STOP_CODE), model.getVertex(COINA_STOP_CODE));
        Edge<Route, Stop> edge2 = model.getEdge(model.getVertex(ALGES_STOP_CODE), model.getVertex(CAIS_DO_SODRE_STOP_CODE));
        RouteCommand routeCommand = new DisableRouteCommand(userConfiguration, edge1);
        userConfigurationManager.execute(routeCommand);
        assertEquals("Desactivar rota Palmela -> Coina", userConfigurationManager.getLastRouteCommand(RouteCommand.class));
        routeCommand = new EnableRouteCommand(userConfiguration, edge1);
        userConfigurationManager.execute(routeCommand);
        assertEquals("Activar rota Palmela -> Coina", userConfigurationManager.getLastRouteCommand(RouteCommand.class));
        routeCommand = new DisableRouteCommand(userConfiguration, edge2);
        userConfigurationManager.execute(routeCommand);
        assertEquals("Desactivar rota Cais do Sodré -> Algés", userConfigurationManager.getLastRouteCommand(RouteCommand.class));

        RouteTransportTypeCommand routeTransportTypeCommand = new DisableRouteTransportTypeCommand(userConfiguration, edge1, TransportType.BUS);
        userConfigurationManager.execute(routeTransportTypeCommand);
        assertEquals("Desactivar tipo de transporte Autocarro na rota Palmela -> Coina", userConfigurationManager.getLastRouteCommand(RouteTransportTypeCommand.class));
        routeTransportTypeCommand = new EnableRouteTransportTypeCommand(userConfiguration, edge1, TransportType.BUS);
        userConfigurationManager.execute(routeTransportTypeCommand);
        assertEquals("Activar tipo de transporte Autocarro na rota Palmela -> Coina", userConfigurationManager.getLastRouteCommand(RouteTransportTypeCommand.class));
        routeTransportTypeCommand = new DisableRouteTransportTypeCommand(userConfiguration, edge2, TransportType.BUS);
        userConfigurationManager.execute(routeTransportTypeCommand);
        assertEquals("Desactivar tipo de transporte Autocarro na rota Cais do Sodré -> Algés", userConfigurationManager.getLastRouteCommand(RouteTransportTypeCommand.class));
    }
}