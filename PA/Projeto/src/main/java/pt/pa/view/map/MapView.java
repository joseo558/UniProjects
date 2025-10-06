package pt.pa.view.map;

import com.brunomnsilva.smartgraph.graph.Edge;
import com.brunomnsilva.smartgraph.containers.ContentZoomScrollPane;
import com.brunomnsilva.smartgraph.graph.Graph;
import com.brunomnsilva.smartgraph.graph.Vertex;
import com.brunomnsilva.smartgraph.graphview.*;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import pt.pa.controller.TransportMapController;
import pt.pa.observerpattern.Observable;
import pt.pa.observerpattern.Observer;
import pt.pa.transportmap.path.PathCriteria;
import pt.pa.transportmap.path.PathCriteriaManager;
import pt.pa.transportmap.path.PathResult;
import pt.pa.transportmap.path.PathStrategyFactory;
import pt.pa.transportmap.*;
import pt.pa.utils.PropertiesUtil;
import pt.pa.view.Coordinate;
import pt.pa.view.common.Controls;
import pt.pa.view.common.Panels;

import java.io.InputStream;
import java.net.URL;
import java.util.*;

/**
 * Class that represents the map view of the application
 */
public class MapView implements Observable {
    /** The BorderPane that contains the map */
    private final BorderPane mapViewPane;
    /** The SmartGraphPanel<V, E> */
    private SmartGraphPanel<Stop, Route> graphView;
    /** The selected vertices */
    private final List<Vertex<Stop>> selectedVertices;
    /** The model */
    private final TransportMap model;
    /** List of observers */
    private final List<Observer> observers;

    private List<String> listDetails = new ArrayList<>();
    private double duration;
    private double distance;
    private double totalCost;

    /**
     * Constructs a MapView instance
     * @param graph Graph<Stop, Route> the graph to be displayed
     * @param model TransportMap the model to be used
     */
    public MapView(Graph<Stop, Route> graph, TransportMap model) {
        if(model == null) {
            throw new IllegalArgumentException("Graph must not be null.");
        }
        this.model = model;
        mapViewPane = new BorderPane();
        selectedVertices = new LinkedList<>();
        observers = new ArrayList<>();

        try {
            // get css and properties files
            InputStream smartgraphProperties = getClass().getClassLoader().getResourceAsStream("smartgraph.properties");
            URL css = MapView.class.getClassLoader().getResource("styles/smartgraph.css");

            // load the graph
            ImportCsv.update(model);
            model.loadUserConfiguration();

            // create the SmartGraphPanel
            if(css == null || smartgraphProperties == null) {
                throw new Exception("Error loading css or properties file.");
            }

            this.graphView = new SmartGraphPanel<>(graph, new SmartGraphProperties(smartgraphProperties), new SmartRandomPlacementStrategy(), css.toURI());
            // set the max height and width of the map
            graphView.setMaxHeight(Integer.parseInt(PropertiesUtil.getInstance().getProperty("map.height")));
            graphView.setMaxWidth(Integer.parseInt(PropertiesUtil.getInstance().getProperty("map.width")));

            ContentZoomScrollPane contentZoomPane = new ContentZoomScrollPane(graphView, 3.0, 0.1);
            mapViewPane.setCenter(contentZoomPane);

            mapViewPane.setMaxWidth(900);
            mapViewPane.setMaxHeight(725);
            mapViewPane.setId("mapView");
        } catch (Exception e) {
            System.out.println("Error loading map view: " + e.getMessage());
            Platform.exit();
        }
    }

    /**
     * Returns the graph view pane
     * @return BorderPane the graph view pane
     */
    public BorderPane getMapViewPane() {
        return mapViewPane;
    }

    /**
     * Initializes the graph display
     */
    public void initGraphDisplay() {
        this.graphView.init(); // Resets vertex positions
        setVertexPositions();
        colorDisabledEdges();
    }

    /**
     * Updates the graph display on graph changes
     */
    public void updateGraphDisplay() {
        // reset positions
        setVertexPositions();
        colorDisabledEdges();
        this.graphView.update();
    }

    /**
     * Sets the triggers for the map view
     * @param controller TransportMapController the controller to be used
     */
    public void setTriggers(TransportMapController controller){
        // Sets double click action for stops
        graphView.setVertexDoubleClickAction(vertex -> controller.showStopInfo(vertex.getUnderlyingVertex()));

        // Sets double click action for routes
        graphView.setEdgeDoubleClickAction(edge -> controller.showRouteInfo(edge.getUnderlyingEdge()));

        // Sets the mouse click action for the map vertices
        for (Vertex<Stop> v : model.vertices()) {
            Node node = (Node) graphView.getStylableVertex(v);
            node.setOnMouseClicked(e -> {
                if(controller.isPRView()) {
                    if (selectedVertices.contains(v)) {
                        controller.mapActionUpdatePersonalizedPath(removeStop(v));
                    } else {
                        try {
                            controller.mapActionUpdatePersonalizedPath(selectStop(v));
                        } catch (Exception ex) {
                            controller.showError(ex.getMessage());
                            controller.mapActionNotifyPRouteObservers(ex.getMessage());
                        }
                    }
                }
            });
        }
    }

   /**
    * Sets each vertex position on the map.
    */
    private void setVertexPositions() {
        Map<String, Coordinate> coordinateMap = ImportCsv.readCoords();
        for (Vertex<Stop> v : model.vertices()) {
            try {
                Coordinate coordinate = coordinateMap.get(v.element().getCode());
                graphView.setVertexPosition(v, coordinate.getPosX(), coordinate.getPosY());
            } catch (NullPointerException ignored) {
                // continue, don't show vertex without coordinates
            }
        }

    }

    /**
     * Add the css and the data to the respective variables to make a route personalized by the user
     * @param vertex Vertex<Stop> the Stop that was clicked
     */
    private List<String> selectStop(Vertex<Stop> vertex) {
        if (vertex == null) {
            throw new IllegalArgumentException("Vertex must not be null.");
        }
        List<String> list = new ArrayList<>();

        try{
            EnumSet<TransportType> transportType = EnumSet.allOf(TransportType.class);

            PathCriteria currentCriteria = PathCriteriaManager.getPathCriteria();
            if (currentCriteria == null) {
                throw new IllegalStateException("Critério de caminho não definido");
            }

            // if empty, add the first vertex
            if (selectedVertices.isEmpty()) {
                selectedVertices.add(vertex);
                if (graphView != null) {
                    graphView.getStylableVertex(vertex.element()).setStyleClass("vertex-start");
                }
                return list;
            }

            // get last vertex
            Vertex<Stop> lastVertex = selectedVertices.get(selectedVertices.size() - 1);

            if (!model.areAdjacent(lastVertex, vertex)) {
                clearMap();
                throw new IllegalArgumentException("Vértices não são adjacentes");
            }

            // add the new vertex
            selectedVertices.add(vertex);

            // get path
            model.setPathStrategy(PathStrategyFactory.create(currentCriteria));
            PathResult result = model.getPathStrategy().findLessCostPathBetweenTwoVertices(model, transportType, lastVertex, vertex);

            if (listDetails == null) {
                listDetails = new ArrayList<>();
            }
            // Update metrics
            if (result != null) {
                duration += result.getTotalDuration();
                distance += result.getTotalDistance();
                listDetails.add(result.getRoute());
                totalCost += result.getTotalSustainability();
            }

            // Formatação segura
            list.add(String.format("%.2f", duration));
            list.add(String.format("%.2f", distance));
            list.add(String.valueOf(listDetails));
            list.add(String.format("%.2f", totalCost));

            // Visualização do grafo
            if (result != null) {
                visualizeGraphColorsPersonalized(result);
            }
        }
        catch(Exception ex){
            // Limpa o estado do grafo em caso de erro
            clearGraphColors();
            throw new IllegalArgumentException("Erro ao selecionar paragem: " + ex.getMessage());
        }
        return list;
    }

    /**
     * Removes the css and the data from the last stop selected
     * @param vertex is the Stop who is clicked
     */
    private List<String> removeStop(Vertex<Stop> vertex) {
        List<String> listReturn = new ArrayList<>();

        // Validações iniciais
        if (vertex == null || selectedVertices == null || selectedVertices.isEmpty()) {
            System.err.println("Operação de remoção inválida");
            return listReturn;
        }

        EnumSet<TransportType> transportType = EnumSet.allOf(TransportType.class);


        try {
            // Verifica se o último vértice pode ser removido
            if (vertex != selectedVertices.get(selectedVertices.size() - 1)) {
                System.err.println("Só é possível remover o último vértice");
                return listReturn;
            }

            // Remove o vértice
            selectedVertices.remove(vertex);

            // Se lista ficar vazia, limpa o grafo
            if (selectedVertices.isEmpty()) {
                clearGraphColors();
                resetMetrics();
                return listReturn;
            }

            // Obtém o novo último vértice
            Vertex<Stop> newLastVertex = selectedVertices.get(selectedVertices.size() - 1);

            // Recalcula métricas
            PathCriteria currentCriteria = PathCriteriaManager.getPathCriteria();
            model.setPathStrategy(PathStrategyFactory.create(currentCriteria));
            PathResult result = model.getPathStrategy().findLessCostPathBetweenTwoVertices(model, transportType, newLastVertex, vertex);

            // Atualiza estilos do grafo
            updateGraphStyles(vertex, newLastVertex);

            // Subtrai métricas
            if (result != null) {
                duration -= result.getTotalDuration();
                distance -= result.getTotalDistance();
                listDetails.remove(result.getRoute());
                totalCost -= result.getTotalSustainability();
            }

            // Formata retorno
            listReturn.add(String.format("%.2f", duration));
            listReturn.add(String.format("%.2f", distance));
            listReturn.add(String.valueOf(listDetails));
            listReturn.add(String.format("%.2f", totalCost));

        } catch (Exception e) {
            System.err.println("Erro ao remover parada: " + e.getMessage());
            clearGraphColors();
        }

        return listReturn;
    }


    private void resetMetrics() {
        duration = 0;
        distance = 0;
        totalCost = 0;
        if (listDetails != null) {
            listDetails.clear();
        }
    }


    private void updateGraphStyles(Vertex<Stop> removedVertex, Vertex<Stop> lastVertex) {
        if (graphView != null) {
            graphView.getStylableVertex(removedVertex.element()).setStyleClass("vertex");
            Edge<Route, Stop> edge = model.getEdge(removedVertex, lastVertex);
            if(edge != null){
                graphView.getStylableEdge(edge).setStyleClass("edge");
            }
        }
    }

    /**
     * Colors the vertices and edges of a given path with their respective transport type color but don't change.
     *
     * @param pathResult the path to color on the map
     */
    public void visualizeGraphColorsPersonalized(PathResult pathResult) {
        Iterator<Vertex<Stop>> pathIterator = pathResult.getPath().iterator();
        Iterator<TransportType> transportIterator = pathResult.getTransportList().iterator();

        String css = "";
        Vertex<Stop> lastVertex = null;

        while(pathIterator.hasNext()) {
            Vertex<Stop> currentVertex = pathIterator.next();

            colorEdge(css, currentVertex, lastVertex);
            graphView.getStylableVertex(selectedVertices.get(0).element()).removeStyleClass(css);
            graphView.getStylableVertex(selectedVertices.get(0).element()).addStyleClass("vertex-start");

            css = getCssFromTransportType(transportIterator, css);
            System.out.println(currentVertex.element() + " " + css);

            graphView.getStylableVertex(currentVertex.element()).addStyleClass(css);
            lastVertex = currentVertex;
        }

        colorDisabledEdges();
    }

    /**
     * Colors the vertices and edges of a given path with their respective transport type color.
     *
     * @param pathResult PathResult the path to color on the map
     */
    public void visualizeGraphColors(PathResult pathResult) {
        Iterator<Vertex<Stop>> pathIterator = pathResult.getPath().iterator();
        Iterator<TransportType> transportIterator = pathResult.getTransportList().iterator();

        clearGraphColors();

        String css = "";
        Vertex<Stop> lastVertex = null;

        while(pathIterator.hasNext()) {
            Vertex<Stop> currentVertex = pathIterator.next();

            colorEdge(css, currentVertex, lastVertex);

            css = getCssFromTransportType(transportIterator, css);

            graphView.getStylableVertex(currentVertex.element()).addStyleClass(css);
            lastVertex = currentVertex;
        }

        colorDisabledEdges();
    }

    /**
     * Return the css class depending on the next transport in transportIterator.
     * <p>
     * If transportIterator doesn't have a next transport, return previous css.
     *
     * @param transportIterator the iterator with the next transports in the path
     * @return the css to color edges and vertices
     */
    private String getCssFromTransportType(Iterator<TransportType> transportIterator, String css) {
        if (transportIterator.hasNext()) {
            TransportType transport = transportIterator.next();

            switch (transport) {
                case TRAIN -> css = "edge-train";
                case BUS -> css = "edge-bus";
                case BOAT -> css = "edge-boat";
                case WALK -> css = "edge-walk";
                case BICYCLE -> css = "edge-bicycle";
            }
        }
        return css;
    }

    /**
     * Finds and colors the edge between two vertices with the respective css class.
     *
     * @param css String name of css class to add to the edge that connects both vertices
     * @param startVertex Vertex<Stop> the vertex where the edge starts
     * @param endVertex Vertex<Stop> the vertex where the edge finishes
     */
    public void colorEdge(String css, Vertex<Stop> startVertex, Vertex<Stop> endVertex) {
        if (css == null || css.isEmpty() || startVertex == null || endVertex == null) {
            return;
        }
        Edge<Route, Stop> edge = model.getEdge(startVertex, endVertex);
        if(edge != null){
            graphView.getStylableEdge(edge).addStyleClass(css);
        }
    }

    /**
     * Colors the disabled routes in gray.
     */
    public void colorDisabledEdges() {
        for (Edge<Route, Stop> edge : model.edges()) {
            SmartStylableNode node = graphView.getStylableEdge(edge);
            node.removeStyleClass("edge-disabled");
            node.removeStyleClass("edge-transport-disabled");

            if (model.getUserConfiguration().isRouteDisabled(edge)) {
                node.addStyleClass("edge-disabled");
            } else if (model.getUserConfiguration().routeHasDisabledTransportType(edge)) {
                node.addStyleClass("edge-transport-disabled");
            }
        }
    }

    /**
     * Clears the colors of the stops and routes on the map and clears the list of chosen stops.
     */
    public void clearMap(){
        clearGraphColors();
        clearChosenVertices();
        notifyObservers("Mapa limpo.");
    }

    /**
     * Clears the colors of the stops and routes on the map,
     * by setting their css style as their respective default value.
     */
    public void clearGraphColors() {
        for (Vertex<Stop> v : model.vertices()) {
            graphView.getStylableVertex(v.element()).setStyleClass("vertex");
        }
        for (Edge<Route, Stop> e : model.edges()) {
            graphView.getStylableEdge(e).setStyleClass("edge");
        }
    }

    /**
     * Clears the list of chosen stops to allow the choice of a new path.
     */
    public void clearChosenVertices() {
        if (selectedVertices != null) {
            this.selectedVertices.clear();
        }
    }


    /**
     * Return a Pane with the information of a stop that has been double-clicked on the map
     * @param stop Stop the stop to show the information
     */
    public Pane getStopInfoPane(Stop stop) {
        // Stop Information
        Label stopNameLabel = Controls.createLabel(stop.getName(), "h2", "bold");
        Label latitudeLabel = Controls.createLabel("Latitude: " + stop.getLatitude(), "h3");
        Label longitudeLabel = Controls.createLabel("Longitude: " + stop.getLongitude(), "h3");

        // Container Pane
        VBox infoVBox = new VBox(10, stopNameLabel, latitudeLabel, longitudeLabel);
        infoVBox.setAlignment(Pos.CENTER);
        return infoVBox;
    }

    /**
     * Return a Pane with the information of a route that has been double-clicked on the map
     * @param edge Edge<Route, Stop> the route to show the information
     */
    public Pane getRouteInfoPane(Edge<Route, Stop> edge) {
        EnumMap<TransportType, RouteInfo> transportMap = edge.element().getTransportMap();

        // Grid with Transport Types and their Costs
        GridPane grid = Panels.createGridPane(10, 10, 10, 500);

        // Table headers
        Label transportLabel = Controls.createLabel("Transporte", "h4", "bold");
        Label distanceLabel = Controls.createLabel("Distância", "h4", "bold");
        Label durationLabel = Controls.createLabel("Duração", "h4", "bold");
        Label sustainabilityLabel = Controls.createLabel("Sustentabilidade", "h4", "bold");

        grid.add(transportLabel, 0, 0);
        grid.add(distanceLabel, 1, 0);
        grid.add(durationLabel, 2, 0);
        grid.add(sustainabilityLabel, 3, 0);

        // Table data
        int rowIndex = 1;

        for (Map.Entry<TransportType, RouteInfo> entry : transportMap.entrySet()) {
            TransportType type = entry.getKey();
            RouteInfo routeInfo = entry.getValue();

            Label transportData;
            if (model.getUserConfiguration().isTransportTypeDisabled(edge, type)) {
                transportData = Controls.createLabel(type.toString() + " (Desativado)");
            } else {
                transportData = Controls.createLabel(type.toString());
            }

            Label distanceData = Controls.createLabel(routeInfo.getDistance() + " " + PathCriteria.DISTANCE.getUnit());
            double duration = routeInfo.getDuration();
            if(type == TransportType.BICYCLE){
                duration = model.getUserConfiguration().applyBicycleDurationScale(duration);
            }
            Label durationData = Controls.createLabel(duration + " " + PathCriteria.DURATION.getUnit());
            Label sustainabilityData = Controls.createLabel(routeInfo.getSustainability() + " " + PathCriteria.SUSTAINABILITY.getUnit());

            grid.add(transportData, 0, rowIndex);
            grid.add(distanceData, 1, rowIndex);
            grid.add(durationData, 2, rowIndex);
            grid.add(sustainabilityData, 3, rowIndex);

            rowIndex++;
        }

        return grid;
    }

    /**
     * Changes the map to the given map type
     * @param mapType MapType the map type to change to
     */
    public void changeMap(MapType mapType){
        this.graphView.setStyle("-fx-background-image: url('" + mapType.getPath() + "');");
        updateGraphDisplay();
        this.notifyObservers("Mapa mudado para " + mapType + ".");
    }

    @Override
    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(Object event) {
        for (Observer observer : observers) {
            observer.update(this, event);
        }
    }
}