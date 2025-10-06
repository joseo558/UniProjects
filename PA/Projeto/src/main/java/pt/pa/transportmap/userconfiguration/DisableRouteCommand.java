package pt.pa.transportmap.userconfiguration;

import com.brunomnsilva.smartgraph.graph.Edge;
import pt.pa.transportmap.Route;
import pt.pa.transportmap.Stop;

/**
 * Command to disable a route in the transport map
 */
public class DisableRouteCommand implements RouteCommand {
    /** User configuration reference */
    private final UserConfiguration userConfiguration;
    /** Route to disable */
    private final Edge<Route, Stop> route;

    /**
     * Constructor for DisableRouteCommand
     * @param userConfiguration UserConfiguration the user configuration
     * @param route Edge<Route, Stop> the route to disable
     * @throws IllegalArgumentException if user configuration or route is null
     */
    public DisableRouteCommand(UserConfiguration userConfiguration, Edge<Route, Stop> route) throws IllegalArgumentException {
        super();
        if(userConfiguration == null || route == null) {
            throw new IllegalArgumentException("User configuration and route cannot be null.");
        }
        this.userConfiguration = userConfiguration;
        this.route = route;
    }

    @Override
    public void execute() {
        userConfiguration.addDisabledRoute(route);
    }

    @Override
    public void undo() {
        userConfiguration.removeDisabledRoute(route);
    }

    @Override
    public String toString() {
        return "Desactivar rota " + route.vertices()[0].element() + " -> " + route.vertices()[1].element();
    }
}
