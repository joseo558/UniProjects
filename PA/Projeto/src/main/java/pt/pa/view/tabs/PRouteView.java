package pt.pa.view.tabs;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import pt.pa.controller.TransportMapController;
import pt.pa.observerpattern.Observable;
import pt.pa.observerpattern.Observer;
import pt.pa.transportmap.path.PathCriteria;
import pt.pa.transportmap.path.PathCriteriaManager;
import pt.pa.transportmap.TransportMap;

import java.util.ArrayList;
import java.util.List;

public class PRouteView extends VBox implements Observable {
    private List<Observer> observers;

    private VBox vboxLeft;
    private VBox vboxActions;
    private TabPane tabPane;
    private Label labelCost;
    private Label lbduration;
    private Label lbdistance;
    private VBox vboxTrip;
    private final TextArea textArea = new TextArea();

    private TransportMap model;

    // Checkboxes de Parametros
   public final RadioButton rbDistance = new RadioButton("Distância");
   public final RadioButton rbDuration = new RadioButton("Duração");
   public final RadioButton  rbSustainability  = new RadioButton("Sustentabilidade");

    /**
     * Constructs the PersonalizeRouteView UI component, which contains a list of optimizable parameters and a
     * sequential list of stops.
     */
    public PRouteView(TransportMap model) {
        this.model = model;
        observers = new ArrayList<>();

        // Informação da Tab de Ações
        createActionsTabInfo();

        // Tab de Ações
        createActionsTab();

        // Informação da Tab de Viagem
        createTripTabInfo();

        // Tab de Viagem
        createTripTab();

        this.getChildren().add(vboxLeft);
    }

    /**
     * Creates the VBoxes, Labels and Checkboxes for the actions tab.
     */
    private void createActionsTabInfo() {
        vboxActions = new VBox();

        vboxActions.setPrefWidth(500);
        vboxActions.setPrefHeight(300);
        vboxActions.setMinWidth(500);
        vboxActions.setMaxWidth(500);
        vboxActions.setMinHeight(300);
        vboxActions.setMaxHeight(300);

        vboxActions.setStyle("-fx-border-color: black; -fx-border-width: 1; -fx-padding: 10; -fx-margin: 10;");
        vboxActions.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));

        // VBox para todas as ações na tab de ações
        VBox vboxLeftActions = new VBox();
        vboxLeftActions.setPrefWidth(250);
        vboxLeftActions.setPrefHeight(300);

        // VBox para os parametros
        VBox vboxParameters = new VBox();
        vboxParameters.setPrefWidth(250);
        vboxParameters.setPrefHeight(75);

        Label labelParameters = new Label("Parametros a otimizar");
        labelParameters.setStyle("-fx-font-size: 14px; -fx-padding: 5;");


        rbDistance.setOnAction(event -> PathCriteriaManager.setPathCriteria(PathCriteria.DISTANCE));

        rbDuration.setOnAction(event -> PathCriteriaManager.setPathCriteria(PathCriteria.DURATION));

        rbSustainability.setOnAction(event -> PathCriteriaManager.setPathCriteria(PathCriteria.SUSTAINABILITY));


        ToggleGroup toggleGroup = new ToggleGroup();
        rbDistance.setToggleGroup(toggleGroup);
        rbDuration.setToggleGroup(toggleGroup);
        rbSustainability.setToggleGroup(toggleGroup);

        // VBox para as Checkboxes
        VBox vboxTransports = new VBox();
        vboxTransports.setPrefWidth(250);
        vboxTransports.setPrefHeight(90);
        vboxTransports.setStyle("-fx-border-color: black; -fx-border-width: 1;");

        vboxTransports.getChildren().addAll(rbDistance, rbDuration, rbSustainability);

        vboxParameters.getChildren().addAll(labelParameters,vboxTransports);

        vboxLeftActions.getChildren().addAll(vboxParameters);

        vboxActions.setPadding(new Insets(10, 10, 10, 10));
        vboxActions.getChildren().add(vboxLeftActions);
    }

    /**
     * Creates the actions tab and sets its content as the vbox with the actions tab information.
     */
    private void createActionsTab() {
        tabPane = new TabPane();

        tabPane.setTabMinWidth(50);
        tabPane.setTabMinHeight(25);
        tabPane.setTabMaxWidth(50);
        tabPane.setTabMaxHeight(25);
        tabPane.setStyle("-fx-padding: 30px;");

        Tab tab = new Tab("Ações");
        tab.setContent(vboxActions);
        tab.setClosable(false);
        tabPane.getTabs().add(tab);
    }

    /**
     * Creates the VBoxes and Labels for the trip tab.
     */
    private void createTripTabInfo() {
        vboxTrip = getTripAndRoutesVBox();

        Label labelSeq = new Label("Lista sequencial de paragens");
        labelSeq.setStyle("-fx-font-size: 16px; -fx-padding: 5;");


        textArea.setEditable(false); // Torna o TextArea somente leitura
        textArea.setWrapText(true); // Ativar quebra de linha automática

        lbduration = new Label("Tempo de Duração: Nenhuma viagem criada");
        lbduration.setStyle("-fx-font-size: 12px; -fx-padding: 5;");

        lbdistance = new Label("Distancia: Nenhuma viagem criada");
        lbdistance.setStyle("-fx-font-size: 12px; -fx-padding: 5;");

        VBox vBoxInfoV = new VBox();

        vBoxInfoV.setPrefWidth(250);
        vBoxInfoV.setPrefHeight(200);
        vBoxInfoV.setStyle("-fx-border-color: black; -fx-border-width: 1;");


        vBoxInfoV.getChildren().addAll(lbduration,lbdistance,textArea);


        labelCost = new Label("Custo Total da Viagem: Nenhuma viagem criada");
        labelCost.setStyle("-fx-font-size: 16px; -fx-padding: 5;");


        vboxTrip.getChildren().addAll(labelSeq,vBoxInfoV, labelCost);
    }

    /**
     * Creates the trip tab and sets its content as the trip information.
     */
    private void createTripTab() {
        TabPane tabPaneInformations = new TabPane();

        tabPaneInformations.setTabMinWidth(50);
        tabPaneInformations.setTabMinHeight(25);
        tabPaneInformations.setTabMaxWidth(50);
        tabPaneInformations.setTabMaxHeight(25);
        tabPaneInformations.setStyle("-fx-padding: 30px;");

        Tab tabV = new Tab("Viagem");
        tabV.setContent(vboxTrip);
        tabV.setClosable(false);
        tabPaneInformations.getTabs().addAll(tabV);

        vboxLeft = new VBox();
        vboxLeft.getChildren().addAll(tabPane,tabPaneInformations);
    }

    /**
     * Creates a new VBox and sets its Width, Height, Margin and Style.
     * For Trip and routes tabs usage only.
     *
     * @return The created VBox
     */
    private VBox getTripAndRoutesVBox() {
        VBox vbox = new VBox();

        vbox.setPrefWidth(500);
        vbox.setPrefHeight(240);
        vbox.setMinWidth(500);
        vbox.setMaxWidth(500);
        vbox.setMinHeight(240);
        vbox.setMaxHeight(240);

        VBox.setMargin(vbox, new Insets(20, 30, 20, 30));

        vbox.setStyle("-fx-border-color: black; -fx-border-width: 1; -fx-padding: 10;");

        return vbox;
    }

    /**
     * Updates the information show to the user  (duration,distance,trip,totalcost)
     * @param stringList list of data to insert into the labels
     *
     */
    public void updateInformation(List<String> stringList){
        if (!stringList.isEmpty()) {
            lbduration.setText("Tempo de Duração: " + stringList.get(0) + " " + PathCriteria.DURATION.getUnit());
            lbdistance.setText("Distancia: " + stringList.get(1) + " " + PathCriteria.DISTANCE.getUnit());
            textArea.setText(stringList.get(2));
            labelCost.setText("Custo Total da Viagem: " + stringList.get(3) + " " + PathCriteria.SUSTAINABILITY.getUnit());
        }
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

    public void setTriggers(TransportMapController controller) {
        // TODO
    }
}