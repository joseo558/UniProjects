package pt.pa.view.tabs;
import com.brunomnsilva.smartgraph.graph.Vertex;
import javafx.collections.FXCollections;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import pt.pa.observerpattern.Observable;
import pt.pa.observerpattern.Observer;
import pt.pa.controller.TransportMapController;
import pt.pa.transportmap.path.PathCriteria;
import pt.pa.transportmap.path.PathResult;
import pt.pa.transportmap.Stop;
import pt.pa.transportmap.TransportMap;
import pt.pa.transportmap.TransportType;
import pt.pa.utils.ComboBoxUtil;

import java.util.*;

/**
 * Class that represents the start view of the application, which contains actions for trip and route searching
 */
public class StartView extends VBox implements Observable {
    /**
     * The model
     */
    private final TransportMap graph;
    /**
     * The list of observers
     */
    private final List<Observer> observerList;
    /**
     * The actions grid pane
     */
    private final ActionGrid actionGrid;
    /**
     * The paths tab pane
     */
    private final PathTabPane pathTabPane;
    /**
     * The trip box
     */
    private TripBox tripBox;
    /**
     * The routes box
     */
    private RoutesBox routesBox;
    /**
     * Constructs the Start View UI component, which contains actions for trip and route searching
     */
    public StartView(TransportMap model) {
        // Set the model
        if(model == null) {
            throw new IllegalArgumentException("Model cannot be null.");
        }
        graph = model;
        // Initialize observers list
        observerList = new ArrayList<>();
        // Create the grids
        actionGrid = new ActionGrid();
        tripBox = new TripBox();
        routesBox = new RoutesBox();
        pathTabPane = new PathTabPane();
        this.getChildren().addAll(actionGrid, pathTabPane);
    }

    @Override
    public void addObserver(Observer o) {
        if (!observerList.contains(o)) {
            observerList.add(o);
        }
    }

    @Override
    public void removeObserver(Observer observer) {
        observerList.remove(observer);
    }

    @Override
    public void notifyObservers(Object command) {
        for (Observer o : observerList) {
            o.update(this, command);
        }
    }

    /**
     * Creates the action grid pane
     */
    private class ActionGrid extends GridPane {
        /**
         * The origin stop combobox
         */
        public ComboBox<Vertex<Stop>> originComboBox;
        /**
         * The destination stop combobox
         */
        public ComboBox<Vertex<Stop>> destinationComboBox;
        /**
         * The distance parameter checkbox
         */
        public RadioButton distanceRadio;
        /**
         * The duration parameter checkbox
         */
        public RadioButton durationRadio;
        /**
         * The sustainability parameter checkbox
         */
        public RadioButton sustainabilityRadio;
        /**
         * The train transport checkbox
         */
        public CheckBox trainCheckBox;
        /**
         * The bus transport checkbox
         */
        public CheckBox busCheckBox;
        /**
         * The boat transport checkbox
         */
        public CheckBox boatCheckBox;
        /**
         * The walk transport checkbox
         */
        public CheckBox walkCheckBox;
        /**
         * The bicycle transport checkbox
         */
        public CheckBox bicycleCheckBox;
        /**
         * The search button
         */
        public Button searchButton;

        /**
         * Constructs the actions grid pane
         */
        public ActionGrid() {
            // Set the padding and gaps
            setPadding(new Insets(50));
            setHgap(130);
            setVgap(15);
            setPrefWidth(560);


            // Left side
            Label originLabel = new Label("Paragem de Origem");
            originLabel.setFont(Font.font(14));
            originLabel.setPadding(new Insets(5));
            add(originLabel, 0, 0);
            GridPane.setHalignment(originLabel, HPos.CENTER);

            originComboBox = ComboBoxUtil.getVertexComboBox(graph);
            add(originComboBox, 0, 1);
            GridPane.setHalignment(originComboBox, HPos.CENTER);

            Label destinationLabel = new Label("Paragem de Destino");
            destinationLabel.setFont(Font.font(14));
            destinationLabel.setPadding(new Insets(5));
            add(destinationLabel, 0, 2);
            GridPane.setHalignment(destinationLabel, HPos.CENTER);

            destinationComboBox = ComboBoxUtil.getVertexComboBox(graph);
            add(destinationComboBox, 0, 3);
            GridPane.setHalignment(destinationComboBox, HPos.CENTER);

            Label parametersLabel = new Label("Parâmetros a otimizar");
            parametersLabel.setFont(Font.font(14));
            parametersLabel.setPadding(new Insets(5));
            add(parametersLabel, 0, 4);
            GridPane.setHalignment(parametersLabel, HPos.CENTER);

            // Radio buttons
            ToggleGroup toggleGroup = new ToggleGroup();
            distanceRadio = new RadioButton(PathCriteria.DISTANCE.toString());
            distanceRadio.setToggleGroup(toggleGroup);
            add(distanceRadio, 0, 5);
            GridPane.setHalignment(distanceRadio, HPos.LEFT);

            durationRadio = new RadioButton(PathCriteria.DURATION.toString());
            durationRadio.setToggleGroup(toggleGroup);
            add(durationRadio, 0, 6);
            GridPane.setHalignment(durationRadio, HPos.LEFT);

            sustainabilityRadio = new RadioButton(PathCriteria.SUSTAINABILITY.toString());
            sustainabilityRadio.setToggleGroup(toggleGroup);
            add(sustainabilityRadio, 0, 7);
            GridPane.setHalignment(sustainabilityRadio, HPos.LEFT);

            // Right side
            Label transportLabel = new Label("Meios de Transporte");
            transportLabel.setFont(Font.font(14));
            transportLabel.setPadding(new Insets(5));
            add(transportLabel, 1, 0);
            GridPane.setHalignment(transportLabel, HPos.CENTER);

            trainCheckBox = new CheckBox(TransportType.TRAIN.toString());
            add(trainCheckBox, 1, 1);
            busCheckBox = new CheckBox(TransportType.BUS.toString());
            add(busCheckBox, 1, 2);
            boatCheckBox = new CheckBox(TransportType.BOAT.toString());
            add(boatCheckBox, 1, 3);
            walkCheckBox = new CheckBox(TransportType.WALK.toString());
            add(walkCheckBox, 1, 4);
            bicycleCheckBox = new CheckBox(TransportType.BICYCLE.toString());
            add(bicycleCheckBox, 1, 5);


            // Search button
            searchButton = new Button("Pesquisar");
            add(searchButton, 1, 8);
            GridPane.setHalignment(searchButton, HPos.CENTER);
        }
    }

    /**
     * Creates the information of the trip tab with the duration, distance and sustainability.
     */
    private class PathTabPane extends TabPane {
        /**
         * The trip tab
         */
        public Tab tripTab;
        /**
         * The routes tab
         */
        public Tab routesTab;

        /**
         * Constructs the path tab pane
         */
        public PathTabPane() {
            // Create the tabs
            tripTab = new Tab("Viagem"); // constctor com content
            tripTab.setContent(tripBox);
            tripTab.setClosable(false);

            routesTab = new Tab("Rotas");
            routesTab.setContent(routesBox);
            routesTab.setClosable(false);

            // Style
            this.setTabMaxWidth(50);
            this.setTabMaxHeight(25);
            this.setPadding(new Insets(30));

            // Add the tabs
            getTabs().addAll(tripTab, routesTab);
        }
    }

    /**
     * Creates the information of the trip tab with the duration, distance and sustainability.
     */
    private class TripBox extends VBox {
        /**
         * The text area with the trip stops
         */
        public TextArea tripTextArea;
        /**
         * The duration label
         */
        public Label durationLabel;
        /**
         * The distance label
         */
        public Label distanceLabel;
        /**
         * The sustainability label
         */
        public Label sustainabilityLabel;

        /**
         * Constructs the trip box
         */
        public TripBox() {
            super();
            Label listLabel = new Label("Lista sequencial de paragens");
            listLabel.setFont(Font.font(16));
            listLabel.setPadding(new Insets(5));

            tripTextArea = new TextArea();
            tripTextArea.setEditable(false); // only read
            tripTextArea.setWrapText(true); // enable automatic line break

            durationLabel = new Label("Tempo de Duração: Nenhuma viagem criada");
            durationLabel.setFont(Font.font(13));
            durationLabel.setPadding(new Insets(5));

            distanceLabel = new Label("Distância: Nenhuma viagem criada");
            distanceLabel.setFont(Font.font(13));
            distanceLabel.setPadding(new Insets(5));

            sustainabilityLabel = new Label("Custo Total da Viagem: Nenhuma viagem criada");
            sustainabilityLabel.setFont(Font.font(14));
            sustainabilityLabel.setPadding(new Insets(5));

            this.setMaxWidth(500);

            this.getChildren().addAll(listLabel, tripTextArea, durationLabel, distanceLabel, sustainabilityLabel);
        }
    }

    /**
     * Creates the information of the routes tab with the number of routes and stops.
     */
    private class RoutesBox extends VBox {
        /**
         * The number of routes label
         */
        public Label numberRoutesLabel;
        /**
         * The stop label
         */
        public Label stopLabel;
        /**
         * The number of routes combobox
         */
        public ComboBox<Integer> numberRoutesComboBox;
        /**
         * The stop combobox
         */
        public ComboBox<Vertex<Stop>> stopComboBox;
        /**
         * The routes button
         */
        public Button routesButton;
        /**
         * The text area with the routes stops
         */
        public TextArea routesTextArea = new TextArea();
        //private Button btSearch = new Button();

        /**
         * Constructs the routes box
         */
        public RoutesBox() {
            super();
            Label routesLabel = new Label("Lista de rotas e paragens");
            routesLabel.setFont(Font.font(16));
            routesLabel.setPadding(new Insets(5));

            // left HBox
            HBox leftHBox = new HBox();
            leftHBox.setPrefWidth(280);
            leftHBox.setPrefHeight(150);
            leftHBox.setStyle("-fx-border-color: black; -fx-border-width: 1; -fx-padding: 10;");

            numberRoutesLabel = new Label("Número de Rotas");
            numberRoutesLabel.setFont(Font.font(13));
            numberRoutesLabel.setPadding(new Insets(5));

            numberRoutesComboBox = new ComboBox<>();
            for (int i = 1; i < graph.getNumberOfStops(); i++) {
                numberRoutesComboBox.getItems().add(i);
            }

            routesButton = new Button("Calcular Rotas");

            HBox numberRoutesHBox = new HBox();
            numberRoutesHBox.setPrefWidth(280);
            numberRoutesHBox.setPrefHeight(50);
            numberRoutesHBox.setSpacing(10);
            numberRoutesHBox.getChildren().addAll(numberRoutesLabel, numberRoutesComboBox);

            stopLabel = new Label("Paragem");
            stopLabel.setFont(Font.font(13));
            stopLabel.setPadding(new Insets(5));

            stopComboBox = ComboBoxUtil.getVertexComboBox(graph);

            HBox stopHBox = new HBox();
            stopHBox.setPrefWidth(280);
            stopHBox.setPrefHeight(50);
            stopHBox.setSpacing(10);
            stopHBox.getChildren().addAll(stopLabel, stopComboBox);

            VBox containerVBox = new VBox();
            containerVBox.setSpacing(10);
            containerVBox.getChildren().addAll(numberRoutesHBox, stopHBox, routesButton);

            leftHBox.getChildren().add(containerVBox);

            routesTextArea.setPrefWidth(200);
            routesTextArea.setPrefHeight(150);
            routesTextArea.setEditable(false);
            routesTextArea.setWrapText(true);

            HBox rightHBox = new HBox();
            rightHBox.setPrefWidth(200);
            rightHBox.setPrefHeight(150);
            rightHBox.setStyle("-fx-border-color: black; -fx-border-width: 1; -fx-padding: 10;");

            HBox containerHBox = new HBox();
            containerHBox.setSpacing(10);
            containerHBox.getChildren().addAll(leftHBox, routesTextArea);
            containerHBox.setStyle("-fx-border-color: black; -fx-border-width: 1;");

            this.getChildren().addAll(routesLabel, containerHBox);
        }
    }

    /**
     * Put the list of stops into the TextArea
     * @param list Collection<Vertex<Stop>> a collection of stops
     */
    public void updateRoutesTextArea(Collection<Vertex<Stop>> list) {
        StringBuilder sb = new StringBuilder();
        for (Vertex<Stop> vertex : list) {
            sb.append(" [").append(vertex.element().getName()).append("] ");
        }
        routesBox.routesTextArea.setText(sb.toString());
    }

    /**
     * Return the path criteria selected or null if none is selected
     * @return PathCriteria the path criteria selected or null if none is selected
     */
    private PathCriteria getPathCriteria() {
        if (actionGrid.distanceRadio.isSelected()) {
            return PathCriteria.DISTANCE;
        } else if (actionGrid.durationRadio.isSelected()) {
            return PathCriteria.DURATION;
        } else if (actionGrid.sustainabilityRadio.isSelected()) {
            return PathCriteria.SUSTAINABILITY;
        } else {
            return null;
        }
    }

    /**
     * Get the transport types selected
     * @return EnumSet<TransportType> the transport types selected
     */
    private EnumSet<TransportType> getTransportTypes() {
        EnumSet<TransportType> transportTypes = EnumSet.noneOf(TransportType.class);
        if(actionGrid.trainCheckBox.isSelected()){
            transportTypes.add(TransportType.TRAIN);
        }
        if(actionGrid.busCheckBox.isSelected()){
            transportTypes.add(TransportType.BUS);
        }
        if(actionGrid.boatCheckBox.isSelected()){
            transportTypes.add(TransportType.BOAT);
        }
        if(actionGrid.walkCheckBox.isSelected()){
            transportTypes.add(TransportType.WALK);
        }
        if(actionGrid.bicycleCheckBox.isSelected()){
            transportTypes.add(TransportType.BICYCLE);
        }
        return transportTypes;
    }

    /**
     * Show the path result in the trip box or defaults if null
     * @param result PathResult the path result
     */
    public void showPathResult(PathResult result){
        if(result != null) {
            tripBox.tripTextArea.setText(result.getRoute());
            tripBox.durationLabel.setText("Tempo de Duração: " + String.format("%.2f", result.getTotalDuration()) + " " + PathCriteria.DURATION.getUnit());
            tripBox.distanceLabel.setText("Distância: " + String.format("%.2f", result.getTotalDistance()) + " " + PathCriteria.DISTANCE.getUnit());
            tripBox.sustainabilityLabel.setText("Sustentabilidade da Viagem: " + String.format("%.2f", result.getTotalSustainability()) + " " + PathCriteria.SUSTAINABILITY.getUnit());
        }else {
            tripBox.tripTextArea.clear();
            tripBox.durationLabel.setText("Tempo de Duração: Nenhuma viagem criada");
            tripBox.distanceLabel.setText("Distância: Nenhuma viagem criada");
            tripBox.sustainabilityLabel.setText("Custo Total da Viagem: Nenhuma viagem criada");
        }
    }

    /**
     * Set the triggers for the actions
     * @param controller TransportMapController the controller
     */
    public void setTriggers(TransportMapController controller) {
        routesBox.routesButton.setOnAction(e -> {
            Vertex<Stop> stop = routesBox.stopComboBox.getValue();
            int numberOfRoutes = routesBox.numberRoutesComboBox.getValue();

            controller.startActionStopsMaxRoutesApart(
                    stop,
                    numberOfRoutes
            );

            notifyObservers("Procura de caminhos com " + numberOfRoutes
                    + " rotas até " + stop.element().getName()
            );
        });

        actionGrid.searchButton.setOnAction(e -> {
            PathCriteria pathCriteria = getPathCriteria();

            if (pathCriteria == null) {
                controller.showError("Selecione um parâmetro a otimizar.");
                notifyObservers("Erro na procura de caminho, parâmetro a otimizar não selecionado.");
                return;
            }
            if (actionGrid.originComboBox.getValue() == null) {
                controller.showError("Selecione uma paragem de origem.");
                notifyObservers("Erro na procura de caminho, paragem de origem não selecionada.");
                return;
            }
            if (actionGrid.destinationComboBox.getValue() == null) {
                controller.showError("Selecione uma paragem de destino.");
                notifyObservers("Erro na procura de caminho, paragem de destino não selecionada.");
                return;
            }
            if (Objects.equals(actionGrid.originComboBox.getValue(), actionGrid.destinationComboBox.getValue())) {
                controller.showError("Não pode ter a mesma paragem de inicio e de destino");
                notifyObservers("Erro na procura de caminho, paragem de inicio e de destino são as mesmas.");
                return;
            }

            Vertex<Stop> origin = actionGrid.originComboBox.getSelectionModel().getSelectedItem();
            Vertex<Stop> destination = actionGrid.destinationComboBox.getSelectionModel().getSelectedItem();

            EnumSet<TransportType> transportTypes = getTransportTypes();
            if (transportTypes.isEmpty()) {
                controller.showError("Selecione pelo menos um transporte.");
                notifyObservers("Erro na procura de caminho, nenhum transporte selecionado.");
                return;
            }

            try {
                controller.startActionLessCostPathBetweenTwoVertices(pathCriteria, transportTypes, origin, destination);
                notifyObservers("Procura de caminho entre "
                                + origin.element().getName() + " e "
                                + destination.element().getName() + "."
                );
            } catch (Exception ex) {
                controller.showError("Não pode efetuar este percurso com os meios de transporte escolhidos");
                notifyObservers(("Transportes inválidos entre "
                        + origin.element().getName() + " e "
                        + destination.element().getName() + "."
                ));
                showPathResult(null);
            }
        });
    }
}

