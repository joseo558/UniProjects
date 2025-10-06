package pt.pa.view.popups;

import com.brunomnsilva.smartgraph.graph.Edge;
import com.brunomnsilva.smartgraph.graph.Vertex;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import pt.pa.controller.TransportMapController;
import pt.pa.observerpattern.Observable;
import pt.pa.observerpattern.Observer;
import pt.pa.transportmap.Route;
import pt.pa.transportmap.Stop;
import pt.pa.transportmap.TransportMap;
import pt.pa.transportmap.TransportType;
import pt.pa.utils.ComboBoxUtil;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public class DisableRouteView implements Observable {

    /** Observer List */
    private final List<Observer> observers;
    /** GUI Pane */
    private final BorderPane popupView;
    /** Transport Map */
    private final TransportMap model;
    /**
     * ComboBox for selecting the starting stop
     */
    private ComboBox<Vertex<Stop>> cbStart;
    /**
     *  ComboBox for selecting the finishing stop
     */
    private ComboBox<Vertex<Stop>> cbFinish;
    /**
     * The bus transport checkbox
     */
    public CheckBox trainCheckBox;
    /**
     * The boat transport checkbox
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
     * The button to deactivate de route
     */
    public Button btDeactivate;
    /**
     * The button to UNDO Route
     */
    public Button btUndoRoute;
    /**
     * The button to UNDO Transport Type
     */
    public Button btUndoTransport;
    /**
     * The button to Reset to the original state
     */
    public Button btReset;
    /**
     * The main Hbox of the PopUp
     */
    public HBox hBoxMain;
    /**
     * The left side of the Vbox of the PopUp
     */
    public VBox vBoxLeft;
    /**
     * The right side of the Vbox of the PopUp
     */
    public VBox vBoxRight;
    /**
     * The top Hbox of the PopUp
     */
    public HBox hBoxTop;
    /**
     * The bottom Hbox of the PopUp
     */
    public HBox hBoxBottom;


    public DisableRouteView(TransportMap model) {
        popupView = new BorderPane();
        this.model = model;
        observers = new ArrayList<>();
        showDesign();
    }

    /**
     * Creates the UI for the route disabling pop-up.
     */
    public void showDesign(){
        hBoxMain= new HBox();
        hBoxMain.setAlignment(Pos.CENTER);
        hBoxMain.setSpacing(20);

        hBoxTop = new HBox();
        hBoxTop.setAlignment(Pos.TOP_CENTER);
        hBoxTop.setSpacing(20);

        Label lbTitle = new Label("Desativar Rotas");
        lbTitle.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #333333; -fx-margin-bottom: 20px; -fx-text-transform: uppercase; -fx-letter-spacing: 2px;");

        vBoxLeft = new VBox();
        vBoxLeft.setAlignment(Pos.TOP_LEFT);
        vBoxLeft.setSpacing(20);
        vBoxLeft.setStyle("-fx-padding: 35;");

        Label lbStart = new Label("Paragem de Origem");
        cbStart = ComboBoxUtil.getVertexComboBox(model);

        Label lbFinish = new Label("Paragem de Destino");
        cbFinish = ComboBoxUtil.getVertexComboBox(model);

        vBoxRight = new VBox();
        vBoxRight.setAlignment(Pos.TOP_LEFT);
        vBoxRight.setSpacing(20);
        vBoxRight.setStyle("-fx-padding: 20;");

        Label lbTransport = new Label("Meios de Transporte");
        trainCheckBox = new CheckBox(TransportType.TRAIN.toString());
        busCheckBox = new CheckBox(TransportType.BUS.toString());
        boatCheckBox = new CheckBox(TransportType.BOAT.toString());
        walkCheckBox = new CheckBox(TransportType.WALK.toString());
        bicycleCheckBox = new CheckBox(TransportType.BICYCLE.toString());

        hBoxBottom = new HBox();
        hBoxBottom.setAlignment(Pos.BOTTOM_CENTER);
        hBoxBottom.setSpacing(20);

        btDeactivate = new Button("Desativar Rota");
        btUndoRoute = new Button("UNDO Rota");
        btUndoTransport = new Button("UNDO Transporte");
        btReset = new Button("Reset");

        hBoxTop.getChildren().addAll(lbTitle);
        vBoxLeft.getChildren().addAll(lbStart, cbStart, lbFinish, cbFinish);
        vBoxRight.getChildren().addAll(lbTransport,trainCheckBox,busCheckBox,boatCheckBox,walkCheckBox,bicycleCheckBox);
        hBoxBottom.getChildren().addAll(btDeactivate, btUndoRoute, btUndoTransport, btReset);

        hBoxMain.getChildren().addAll(vBoxLeft,vBoxRight);

        popupView.setTop(hBoxTop);
        popupView.setCenter(hBoxMain);
        popupView.setBottom(hBoxBottom);
    }

    /**
     * Sets the on-click actions for the buttons.
     * @param controller the TransportMapController to handle the commands
     */
    public void setTriggers(TransportMapController controller) {
        // Disable a route or transport type(s)
        btDeactivate.setOnAction(e -> {
            Vertex<Stop> start = cbStart.getValue();
            Vertex<Stop> end = cbFinish.getValue();
            if (start == null || end == null) {return;}

            Edge<Route, Stop> route = model.getEdge(start, end);
            if (route == null) {return;}

            Set<TransportType> routeTransports = route.element().getTransportList();
            EnumSet<TransportType> selectedTypes = getSelectedTransportTypes();

            if (selectedTypes.isEmpty()) {
                controller.disableRoute(route);
            } else {
                for (TransportType type : selectedTypes) {
                    if (routeTransports.contains(type)) {
                        controller.disableTransportType(route, type);
                    }
                }
            }
            this.updateMap(controller);
            notifyObservers("Rota desativada entre " + start.element() + " e " + end.element());
        });

        // Undo last disabled route
        btUndoRoute.setOnAction(e -> {
            controller.undoChangesOnDisabledRoutes();
            this.updateMap(controller);
            notifyObservers("Restabelecer última rota desativada.");
        });

        // Undo last disabled transport type
        btUndoTransport.setOnAction(e -> {
            controller.undoChangesOnDisabledTransportTypes();
            this.updateMap(controller);
            notifyObservers("Restabelecer último transporte desativado.");
        });

        // Reset route and transport types disabled
        btReset.setOnAction(e -> {
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setContentText("Confirm Reset?");

            confirmation.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    controller.resetDisabledRoutes();
                    this.updateMap(controller);
                    notifyObservers("Restabelecer rotas desativadas.");
                }
            });
        });
    }

    /**
     * Returns an EnumSet of the transport types that are selected to disable.
     * @return the EnumSet of TransportTypes
     */
    private EnumSet<TransportType> getSelectedTransportTypes() {
        EnumSet<TransportType> transportTypes = EnumSet.noneOf(TransportType.class);
        if (trainCheckBox.isSelected()) {
            transportTypes.add(TransportType.TRAIN);
        }
        if (busCheckBox.isSelected()) {
            transportTypes.add(TransportType.BUS);
        }
        if (boatCheckBox.isSelected()) {
            transportTypes.add(TransportType.BOAT);
        }
        if (walkCheckBox.isSelected()) {
            transportTypes.add(TransportType.WALK);
        }
        if (bicycleCheckBox.isSelected()) {
            transportTypes.add(TransportType.BICYCLE);
        }
        return transportTypes;
    }

    /**
     * Returns the Pane containing all components.
     * @return BorderPane
     */
    public BorderPane getPane() {
        return popupView;
    }

    /**
     * Updates the map in order to show disabled edges.
     * @param controller the TransportMapController to handle the commands
     */
    public void updateMap(TransportMapController controller) {
        controller.getView().update(this, null);
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
    public void notifyObservers(Object arg) {
        for (Observer observer : observers) {
            observer.update(this, arg);
        }
    }
}
