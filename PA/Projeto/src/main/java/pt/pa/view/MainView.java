package pt.pa.view;

import com.brunomnsilva.smartgraph.graph.Edge;
import com.brunomnsilva.smartgraph.graph.Graph;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import pt.pa.controller.TransportMapController;
import pt.pa.observerpattern.Observable;
import pt.pa.transportmap.Route;
import pt.pa.transportmap.Stop;
import pt.pa.transportmap.TransportMap;
import pt.pa.transportmap.TransportType;
import pt.pa.transportmap.userconfiguration.UserConfiguration;
import pt.pa.view.common.Windows;
import pt.pa.view.map.MapType;
import pt.pa.view.map.MapView;
import pt.pa.view.menu.TopMenu;
import pt.pa.view.menu.TopTabPane;
import pt.pa.view.popups.BikeChangeTimeView;
import pt.pa.view.popups.DisableRouteView;
import pt.pa.view.tabs.LoggerView;
import pt.pa.view.tabs.metric.MetricView;
import pt.pa.view.tabs.PRouteView;
import pt.pa.view.tabs.StartView;

import java.util.List;
import java.util.Map;

/**
 * Class that represents the main view of the application
 */
public class MainView implements TransportMapGUI {
    /** GUI Pane */
    private final BorderPane mainViewPane;
    /** TransportMap model */
    private final TransportMap model;
    /** The CSS file */
    private final String CSS_FILE;
    /** The top menu */
    private final TopMenu topMenu;
    /** The tab pane */
    private final TopTabPane topTabPane;
    /** The map view */
    private final MapView mapView;
    /** The content of the tab */
    private final BorderPane tabContent;
    /** The start view */
    private final StartView startView;
    /** The personalized route view */
    private final PRouteView pRouteView;
    /** The metric view */
    private final MetricView metricView;
    /** The logger view */
    private final LoggerView loggerView;
    /** The Popup to change Bike time view */
    private final BikeChangeTimeView bikeChangeTimeView;
    /** The Popup to change Route view */
    private final DisableRouteView disableRouteView;

    /**
     * Constructor for MainView which includes tabs that allow navigation and serves as the base for all the views
     * @param graph Graph<Stop, Route> the graph in the model to bind to the map view
     * @param model TransportMap the model to be used
     * @param cssFile String the CSS file to be used
     */
    public MainView(Graph<Stop, Route> graph, TransportMap model, String cssFile) {
        if(model == null) {
            throw new IllegalArgumentException("Model cannot be null.");
        }
        this.model = model;
        this.CSS_FILE = cssFile;
        mainViewPane = new BorderPane();

        topMenu = new TopMenu();
        topTabPane = new TopTabPane();
        mapView = new MapView(graph, model);
        tabContent = new BorderPane();

        // views in tabs
        startView = new StartView(model);
        pRouteView = new PRouteView(model);
        metricView = new MetricView();
        loggerView = new LoggerView();
        bikeChangeTimeView = new BikeChangeTimeView(model);
        disableRouteView = new DisableRouteView(model);

        // Add Observers
        startView.addObserver(loggerView);
        pRouteView.addObserver(loggerView);
        metricView.addObserver(loggerView);
        mapView.addObserver(loggerView);
        bikeChangeTimeView.addObserver(loggerView);
        disableRouteView.addObserver(loggerView);


        // Create the top container with the menu bar and the tab pane
        mainViewPane.setTop( new VBox(topMenu.getMenuBar(), topTabPane.getTabPane()) );

        // Set the content as the center of the window
        mainViewPane.setCenter(tabContent);
        // map always on the right of content
        tabContent.setRight(mapView.getMapViewPane());

        // Set the tab selection callbacks
        topTabPane.setTabEvents(
                this::showStartView,
                this::showPRouteView,
                this::showMetricView,
                this::showLoggerView
        );

        // Show the start view
        showStartView();
    }

    // Getters

    /**
     * Gets the GUI Pane
     * @return BorderPane the GUI Pane
     */
    public BorderPane getMainViewPane() {
        return mainViewPane;
    }

    /**
     * Gets the map view
     * @return MapView the map view
     */
    public MapView getMapView() {
        return mapView;
    }

    /**
     * Gets the start view
     * @return StartView the start view
     */
    public StartView getStartView() {
        return startView;
    }

    /**
     * Gets the personalized route view
     * @return PRouteView the personalized route view
     */
    public PRouteView getPRouteView() {
        return pRouteView;
    }

    /**
     * Gets the metric view
     * @return MetricView the metric view
     */
    public MetricView getMetricView() {
        return metricView;
    }

    /**
     * Gets the logger view
     * @return LoggerView the logger view
     */
    public LoggerView getLoggerView() {
        return loggerView;
    }

    // Set the tab view

    /**
     * Sets the content shown as the start view.
     */
    private void showStartView( ) {
        tabContent.setLeft(startView);
    }

    /**
     * Sets the content shown as the pRoute view.
     */
    private void showPRouteView( ) {
        tabContent.setLeft(pRouteView);
    }

    /**
     * Sets the content shown as the metric view
     */
    private void showMetricView( ) {
        tabContent.setLeft(metricView.getMetricViewPane());
    }

    /**
     * Sets the content shown as the logger view
     */
    private void showLoggerView() {
        tabContent.setLeft(loggerView);
    }

    // TransportMapGUI methods

    @Override
    public void update(Observable subject, Object arg) {
        if (arg instanceof String) {
            // relay the message to the logger view
            loggerView.update(subject, ((String) arg));
        } else {
            // Reload mapView
            mapView.updateGraphDisplay();
        }
    }

    @Override
    public boolean isPRView(){
        return topTabPane.isPRView();
    }

    @Override
    public void setTriggers(TransportMapController controller) {
        topMenu.setTriggers(controller);

        mapView.setTriggers(controller);
        startView.setTriggers(controller);
        pRouteView.setTriggers(controller);
        metricView.setTriggers(controller);
        bikeChangeTimeView.setTriggers(controller);
        disableRouteView.setTriggers(controller);

    }

    @Override
    public void initGraphDisplay() {
        mapView.initGraphDisplay();
    }

    @Override
    public void changeMap(MapType mapType){
        mapView.changeMap(mapType);
    }

    @Override
    public void clearMap(){
        mapView.clearMap();
    }

    @Override
    public void showMetricNumberStops(int totalStops, int isolatedStops, int nonIsolatedStops){
        metricView.showMetricNumberStops(totalStops, isolatedStops, nonIsolatedStops);
    }

    @Override
    public void showMetricNumberRoutes(int totalRoutes, Map<TransportType, Integer> map){
        metricView.showMetricNumberRoutes(totalRoutes, map);
    }

    @Override
    public void showMetricTop5(List<Map.Entry<Stop, Integer>> centralityList){
        metricView.showMetricTop5(centralityList);
    }

    @Override
    public void showMetricCentrality(List<Map.Entry<Stop, Integer>> centralityList){
        metricView.showMetricCentrality(centralityList);
    }

    @Override
    public void showStopInfo(Stop stop) {
        showPopup(mapView.getStopInfoPane(stop), 300, 200);
    }

    @Override
    public void showRouteInfo(Edge<Route, Stop> edge) {
        showPopup(mapView.getRouteInfoPane(edge), 500, 300);
    }

    @Override
    public void showError(String message) {
        Alert alert = Windows.createErrorAlert(message);
        alert.showAndWait();
    }

    @Override
    public void showInfo(String message) {
        Alert alert = Windows.createInfoAlert(message);
        alert.showAndWait();
    }

    @Override
    public void displayDisableRoute() {
        Pane disableRoutePane = disableRouteView.getPane();
        showPopup(disableRoutePane, 500, 400);
    }

    @Override
    public void displayBikeChangeTime() {
        Pane bikeChangeTimePane = bikeChangeTimeView.getPane();
        showPopup(bikeChangeTimePane, 500, 400);
    }

    /**
     * Shows a pop-up with the given content
     * @param content Pane the content to be displayed in the pop-up
     * @param width double the width of the pop-up
     * @param height double the height of the pop-up
     */
    public void showPopup(Pane content, double width, double height) {
        Stage popupStage = Windows.createPopup(content, width, height, CSS_FILE, mainViewPane);
        popupStage.show();
    }
}