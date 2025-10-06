package pt.pa.transportmap.userconfiguration;

import com.brunomnsilva.smartgraph.graph.Edge;
import pt.pa.transportmap.Route;
import pt.pa.transportmap.Stop;
import pt.pa.transportmap.TransportType;

/**
 * Command to disable a transport type in a route in the transport map
 */
public class DisableRouteTransportTypeCommand implements RouteTransportTypeCommand {
    /** User configuration reference */
    private final UserConfiguration userConfiguration;
    /** Route with transport type to disable */
    private final Edge<Route, Stop> route;
    /** Transport type to disable */
    private final TransportType transportType;

    /**
     * Constructor for DisableRouteTransportTypeCommand
     * @param userConfiguration UserConfiguration the user configuration
     * @param route Edge<Route, Stop> the route with the transport type to disable
     * @param transportType TransportType the transport type to disable
     * @throws IllegalArgumentException if user configuration, route or transport type are null or if the route does not have the specified transport type
     */
    public DisableRouteTransportTypeCommand(UserConfiguration userConfiguration, Edge<Route, Stop> route, TransportType transportType) throws IllegalArgumentException {
        super();
        if(userConfiguration == null || route == null || transportType == null) {
            throw new IllegalArgumentException("User configuration, route and transport type cannot be null.");
        }
        if(!route.element().hasTransport(transportType)) {
            throw new IllegalArgumentException("Route does not have the specified transport type.");
        }
        this.userConfiguration = userConfiguration;
        this.route = route;
        this.transportType = transportType;
    }

    @Override
    public void execute() {
        userConfiguration.addDisabledTransportType(route, transportType);
    }

    @Override
    public void undo() {
        userConfiguration.removeDisabledTransportType(route, transportType);
    }

    @Override
    public String toString() {
        return "Desactivar tipo de transporte " + transportType + " na rota " + route.vertices()[0].element() + " -> " + route.vertices()[1].element();
    }
}
