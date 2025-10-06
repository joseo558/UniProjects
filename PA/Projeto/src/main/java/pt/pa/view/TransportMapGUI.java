package pt.pa.view;

import com.brunomnsilva.smartgraph.graph.Edge;
import pt.pa.controller.TransportMapController;
import pt.pa.observerpattern.Observer;
import pt.pa.transportmap.Route;
import pt.pa.transportmap.Stop;
import pt.pa.transportmap.TransportType;
import pt.pa.view.map.MapType;

import java.util.List;
import java.util.Map;

/**
 * Interface that represents the graphic view of the TransportMap, implements Observer pattern and MVC
 */
public interface TransportMapGUI extends Observer {
    /**
     * Checks if the current tab is the personalized route view
     * @return true if the current tab is the personalized route view, false otherwise
     */
    boolean isPRView();

    /**
     * Method that sets the triggers for the view
     * @param controller TransportMapController the controller to be used
     */
    void setTriggers(TransportMapController controller);

    /**
     * Initializes the graph display
     */
    void initGraphDisplay();

    /**
     * Changes the map to the given type
     * @param mapType MapType the type of the map to change to
     */
    void changeMap(MapType mapType);

    /**
     * Clears the map
     */
    void clearMap();

    /**
     * Shows the number of stops metric
     * @param totalStops int the total number of stops
     * @param isolatedStops int the number of isolated stops
     * @param nonIsolatedStops int the number of non isolated stops
     */
    void showMetricNumberStops(int totalStops, int isolatedStops, int nonIsolatedStops);

    /**
     * Shows the number of routes metric
     * @param totalRoutes int the total number of routes
     * @param map Map<TransportType, Integer> the map with the number of routes by transport type
     */
    void showMetricNumberRoutes(int totalRoutes, Map<TransportType, Integer> map);

    /**
     * Shows the top 5 centrality of the stops metric
     * @param centralityList List<Map.Entry<Stop, Integer>> the list with the centrality of the stops
     */
    void showMetricTop5(List<Map.Entry<Stop, Integer>> centralityList);

    /**
     * Shows the centrality of the stops metric
     * @param centralityList List<Map.Entry<Stop, Integer>> the list of stops and their centrality
     */
    void showMetricCentrality(List<Map.Entry<Stop, Integer>> centralityList);

    /**
     * Shows the information of a stop
     * @param stop Stop the stop to show the information
     */
    void showStopInfo(Stop stop);

    /**
     * Shows the information of a route
     * @param route Edge<Route, Stop> the route to show the information
     */
    void showRouteInfo(Edge<Route, Stop> route);

    /**
     * Displays an error message in an alert dialog.
     * @param message String the error message to be displayed in the alert dialog
     */
    void showError(String message);

    /**
     * Displays an information message in an alert dialog.
     * @param message String the information message to be displayed in the alert dialog
     */
    void showInfo(String message);

    /**
     * Show the PopUp to Disable Route
     */
    void displayDisableRoute();


    /**
     * Show the PopUp to Change Bike Time
     */
    void displayBikeChangeTime();
}
