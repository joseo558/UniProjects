package pt.pa.controller;

import com.brunomnsilva.smartgraph.graph.Edge;
import com.brunomnsilva.smartgraph.graph.InvalidVertexException;
import com.brunomnsilva.smartgraph.graph.Vertex;
import javafx.application.Platform;
import pt.pa.transportmap.path.PathCriteria;
import pt.pa.transportmap.path.PathResult;
import pt.pa.transportmap.path.PathStrategyFactory;
import pt.pa.transportmap.path.PathUtils;
import pt.pa.transportmap.Route;
import pt.pa.transportmap.Stop;
import pt.pa.transportmap.TransportMap;
import pt.pa.transportmap.TransportType;
import pt.pa.transportmap.userconfiguration.*;
import pt.pa.view.MainView;
import pt.pa.view.TransportMapGUI;
import pt.pa.view.map.MapType;

import java.util.EnumSet;
import java.util.List;

/**
 * Class that represents the controller of the application
 */
public class TransportMapController {
    /** The model */
    private final TransportMap model;
    /** The view */
    private final TransportMapGUI view;
    /** User configuration manager */
    private final UserConfigurationManager userConfigurationManager;
    /** User Configuration */
    private final UserConfiguration userConfiguration;

    /**
     * Constructs a BaseViewController instance
     * @param view TransportMapUI the view to be used
     * @throws IllegalStateException if the view is null
     */
    public TransportMapController(TransportMap model, TransportMapGUI view) throws IllegalStateException {
        if(model == null){
            throw new IllegalStateException("No model in use.");
        }
        this.model = model;
        if(view == null){
            throw new IllegalStateException("No view in use.");
        }
        this.view = view;

        /* binds user interface actions to this controller */
        this.view.setTriggers(this);

        /* bind model to the user interface */
        this.model.addObserver(this.view);

        this.userConfigurationManager = new UserConfigurationManager();
        this.userConfiguration = model.getUserConfiguration();
    }

    /**
     * Gets the main view
     * @return TransportMapUI the view
     */
    public TransportMapGUI getView() {
        return view;
    }

    // Main actions

    /**
     * Action to show an error message
     * @param message String the message to show
     */
    public void showError(String message){
        view.showError(message);
    }

    /**
     * Action to show an information message
     * @param message String the message to show
     */
    public void showInfo(String message){
        view.showInfo(message);
    }

    /**
     * Check if the current tab is the personalized route tab
     * @return true if the current tab is the personalized route tab
     */
    public boolean isPRView(){
        return view.isPRView();
    }

    // Menu actions

    /**
     * Menu action to import model data
     */
    public void importData(){
        model.update();
    }

    /**
     * Action to clear the map
     */
    public void clearMap(){
        view.clearMap();
    }

    /**
     * Action to show the popup to disable route
     */
    public void displayDisableRoute(){view.displayDisableRoute();}

    /**
     * Action to show the popup to change the time in a bike
     */
    public void displayBikeChangeTime(){view.displayBikeChangeTime();}

    /**
     * Menu action to change the map
     * @param mapType MapType the map type to change to
     */
    public void changeMap(MapType mapType) {
        view.changeMap(mapType);
    }

    /**
     * Menu action to close the application
     */
    public void exitApplication(){
        userConfiguration.saveToFile();
        Platform.exit();
    }

    // Start actions

    /**
     * Action to determine the graph vertices that are maxRoutes apart from the origin
     * @param origin Vertex<Stop> the origin vertex
     * @param maxRoutes int the maximum number of routes apart
     */
    public void startActionStopsMaxRoutesApart(Vertex<Stop> origin, int maxRoutes){
        ((MainView) view).getStartView().updateRoutesTextArea( PathUtils.bfsLimited( model, origin, maxRoutes ) );
    }

    /**
     * Action to get the less cost path between two vertices
     * @param pathCriteria PathCriteria the criteria to use
     * @param transportType EnumSet<TransportType> the transport types to use
     * @param origin Vertex<Stop> the origin vertex
     * @param destination Vertex<Stop> the destination vertex
     * @throws IllegalStateException if the path strategy is not set
     * @throws IllegalArgumentException if the path criteria is not valid
     * @throws InvalidVertexException if the origin or destination vertex is not valid
     */
    public void startActionLessCostPathBetweenTwoVertices(PathCriteria pathCriteria, EnumSet<TransportType> transportType, Vertex<Stop> origin, Vertex<Stop> destination) throws IllegalStateException, IllegalArgumentException, InvalidVertexException {
        model.setPathStrategy(PathStrategyFactory.create(pathCriteria));
        PathResult result = model.getPathStrategy().findLessCostPathBetweenTwoVertices(model, transportType, origin, destination);
        mapActionVisualizeGraphColors(result);
        ((MainView) view).getStartView().showPathResult(result);
    }

    // Map actions

    /**
     * Action to show the stop info after double-clicking on a vertex
     * @param vertex Vertex<Stop> the vertex to show the info
     */
    public void showStopInfo(Vertex<Stop> vertex){
        view.showStopInfo(vertex.element());
    }

    /**
     * Action to show the route info after double-clicking on an edge
     * @param edge Edge<Route, Stop> the edge to show the info
     */
    public void showRouteInfo(Edge<Route, Stop> edge){
        view.showRouteInfo(edge);
    }

    /**
     * Action to update the personalized path information
     * @param stringList List<String> the list of strings to show
     */
    public void mapActionUpdatePersonalizedPath(List<String> stringList){
        ((MainView) view).getPRouteView().updateInformation(stringList);
    }

    /**
     * Action to show the path result after double-clicking on a vertex
     * @param result PathResult the result to show
     */
    public void mapActionVisualizeGraphColors(PathResult result){
       ((MainView) view).getMapView().visualizeGraphColors(result);
    }

   /**
    * Action to notify to the personalized route observers
    * @param message String the message to notify
    */
   public void mapActionNotifyPRouteObservers(String message){
       ((MainView) view).getPRouteView().notifyObservers(message);
   }

    // Metric actions

    /**
     * Action to show the number of stops
     */
    public void showMetricNumberStops(){
        view.showMetricNumberStops(
                model.getNumberOfStops(),
                model.getNumberOfIsolatedStops(),
                model.getNumberOfNotIsolatedStops()
        );
    }

    /**
     * Action to show the number of routes
     */
    public void showMetricNumberRoutes(){
        view.showMetricNumberRoutes(
                model.getNumberOfRoutes(),
                model.getNumberOfRoutesByTransportType()
        );
    }

    /**
     * Action to show the top 5 of the number of routes by transport type
     */
    public void showMetricTop5(){
        view.showMetricTop5( model.getStopCentrality() );
    }

    /**
     * Action to show the number of routes by transport type
     */
    public void showMetricCentrality(){
        view.showMetricCentrality( model.getStopCentrality() );
    }

    // User configuration actions

    /**
     * Disable a route
     * @param route Edge<Route, Stop> the route to disable
     * @throws IllegalArgumentException if the route is null
     */
    public void disableRoute(Edge<Route, Stop> route) throws IllegalArgumentException {
        userConfigurationManager.execute(new DisableRouteCommand(userConfiguration, route));
    }

    /**
     * Enable a route
     * @param route Edge<Route, Stop> the route to enable
     * @throws IllegalArgumentException if the route is null
     */
    public void enableRoute(Edge<Route, Stop> route) throws IllegalArgumentException {
        userConfigurationManager.execute(new EnableRouteCommand(userConfiguration, route));
    }

    /**
     * Undo the last changes on disabled routes
     */
    public void undoChangesOnDisabledRoutes() throws IllegalArgumentException {
        try {
            userConfigurationManager.undo(RouteCommand.class);
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        }
    }

    /**
     * Disable a transport type in a route
     * @param route Edge<Route, Stop> the route to disable the transport type
     * @param transportType TransportType the transport type to disable
     * @throws IllegalArgumentException if the route or the transport type are null or the transport type is not in the route
     */
    public void disableTransportType(Edge<Route, Stop> route, TransportType transportType) throws IllegalArgumentException {
        userConfigurationManager.execute(new DisableRouteTransportTypeCommand(userConfiguration, route, transportType));
    }

    /**
     * Enable a transport type in a route
     * @param route Edge<Route, Stop> the route to enable the transport type
     * @param transportType TransportType the transport type to enable
     * @throws IllegalArgumentException if the route or the transport type are null or the transport type is already in the route
     */
    public void enableTransportType(Edge<Route, Stop> route, TransportType transportType) throws IllegalArgumentException {
        userConfigurationManager.execute(new EnableRouteTransportTypeCommand(userConfiguration, route, transportType));
    }

    /**
     * Undo the last changes on disabled transport types
     */
    public void undoChangesOnDisabledTransportTypes() throws IllegalArgumentException {
        try {
            userConfigurationManager.undo(RouteTransportTypeCommand.class);
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        }
    }

    /**
     * Resets the disabled routes and transport types.
     */
    public void resetDisabledRoutes() {
        try {
            userConfigurationManager.resetDisabledRoutes(userConfiguration);
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        }
    }

    /**
     * Change the duration scale of a bicycle route
     * @param durationScale double A multiplier applied to the original duration value to scale it proportionally
     */
    public void changeBicycleDurationScale(double durationScale){
        userConfigurationManager.execute(new ChangeBicycleDurationScaleCommand(userConfiguration, durationScale));
    }

    /**
     * Undo the last change in the bicycle duration scale
     * @return double the current bicycle duration scale after undo
     */
    public double undoChangeBicycleDurationScale(){
        try {
            userConfigurationManager.undo(BicycleDurationScaleCommand.class);
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        }
        return userConfiguration.getBicycleDurationScale();
    }
}