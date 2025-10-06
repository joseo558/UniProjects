package pt.pa.transportmap.userconfiguration;

import com.brunomnsilva.smartgraph.graph.Edge;
import pt.pa.transportmap.Route;
import pt.pa.transportmap.Stop;

/**
 * Command to enable a route in the transport map
 */
public class EnableRouteCommand implements RouteCommand {
    /** User configuration reference */
    private final UserConfiguration userConfiguration;
    /** Route to enable */
    private final Edge<Route, Stop> route;

    /**
     * Constructor for EnableRouteCommand
     * @param userConfiguration UserConfiguration the user configuration
     * @param route Edge<Route, Stop> the route to enable
     * @throws IllegalArgumentException if user configuration or route is null
     */
    public EnableRouteCommand(UserConfiguration userConfiguration, Edge<Route, Stop> route) throws IllegalArgumentException {
        super();
        if(userConfiguration == null || route == null) {
            throw new IllegalArgumentException("User configuration and route cannot be null.");
        }
        this.userConfiguration = userConfiguration;
        this.route = route;
    }
    @Override
    public void execute() {
        userConfiguration.removeDisabledRoute(route);
    }
    @Override
    public void undo() {
        userConfiguration.addDisabledRoute(route);
    }
    @Override
    public String toString() {
        return "Activar rota " + route.vertices()[0].element() + " -> " + route.vertices()[1].element();
    }
}