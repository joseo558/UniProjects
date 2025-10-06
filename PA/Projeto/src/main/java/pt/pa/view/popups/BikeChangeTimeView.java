package pt.pa.view.popups;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import pt.pa.controller.TransportMapController;
import pt.pa.observerpattern.Observable;
import pt.pa.observerpattern.Observer;
import pt.pa.transportmap.TransportMap;

import java.util.ArrayList;
import java.util.List;

public class BikeChangeTimeView implements Observable {

    /** Observer List */
    private final List<Observer> observers;
    /** GUI Pane */
    private final BorderPane popupview;
    /**Title to the PopUp**/
    private Label lb_Title;
    /** Slider to facilitate user interface */
    private Slider slider;
    /** The button to deactivate de route */
    public Button bt_Change;
    /** The button to make UNDO */
    public Button bt_Undo;
    /** The button to restart to the original state */
    public Button bt_Restart;
    /** The top Vbox of the PopUp */
    public VBox vBoxTop;
    /** The main Vbox of the PopUp */
    public VBox vBoxMain;
    /** The bottom Vbox of the PopUp */
    public VBox vBoxBottom;
    /** The bottom Hbox of the PopUp */
    public HBox hBoxBottom;
    /** TransportMap model */
    private final TransportMap model;

    public BikeChangeTimeView(TransportMap model){
        popupview = new BorderPane();
        this.model = model;
        observers = new ArrayList<>();
        showDesign();
    }

    public void showDesign(){


        lb_Title = new Label("Alterar tempo de Bicicleta");
        lb_Title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #333333; -fx-margin-bottom: 20px; -fx-text-transform: uppercase; -fx-letter-spacing: 1.5px;");


        slider = new Slider(0.25, 2, model.getUserConfiguration().getBicycleDurationScale());
        slider.setShowTickMarks(true);
        slider.setShowTickLabels(true);
        slider.setMajorTickUnit(0.25f);
        slider.setMinorTickCount(0);
        slider.setBlockIncrement(0.25f);


        slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            double roundedValue = Math.round(newValue.doubleValue() * 4) / 4.0;
            slider.setValue(roundedValue);
        });



        slider.setLabelFormatter(new StringConverter<Double>() {
            @Override
            public String toString(Double value) {

                if (value == 0.25) return "Elite";
                if (value == 0.5) return "Pro";
                if (value == 0.75) return "Avançado";
                if (value == 1.0) return "Normal";
                if (value == 1.25) return "Intermediário";
                if (value == 1.50) return "Moderado";
                if (value == 1.75) return "Recreativo";
                if (value == 2.0) return "Iniciante";

                return "";
            }

            @Override
            public Double fromString(String string) {
                return null;
            }
        });


        bt_Change = new Button("Mudar de Nivel");
        bt_Undo = new Button("UNDO");
        bt_Restart = new Button("Restart");

        vBoxTop = new VBox();
        vBoxTop.setAlignment(Pos.TOP_CENTER);
        vBoxTop.getChildren().addAll(lb_Title);

        vBoxMain = new VBox();
        vBoxMain.setAlignment(Pos.CENTER);
        vBoxMain.getChildren().addAll(slider);

        vBoxBottom = new VBox();
        vBoxBottom.setAlignment(Pos.CENTER);
        vBoxBottom.setSpacing(20);

        hBoxBottom = new HBox();
        hBoxBottom.setSpacing(20);
        hBoxBottom.setAlignment(Pos.CENTER);
        hBoxBottom.setStyle("-fx-padding: 20;");
        hBoxBottom.getChildren().addAll(bt_Change,bt_Undo,bt_Restart);

        vBoxBottom.getChildren().addAll(hBoxBottom);

        popupview.setTop(vBoxTop);
        popupview.setCenter(vBoxMain);
        popupview.setBottom(vBoxBottom);

    }

    public void setTriggers(TransportMapController controller) {
        bt_Change.setOnAction(event -> {
            double value = slider.getValue();
            controller.changeBicycleDurationScale(value);
            controller.showInfo("Tempos alterados com sucesso.");
            notifyObservers("Tempos de bicicleta alterado para: " + value + ".");
        });

        bt_Restart.setOnAction(event -> {
            controller.changeBicycleDurationScale(1);
            slider.setValue(1);
            controller.showInfo("Tempos restaurados para o original.");
            notifyObservers("Tempos de bicicleta restaurados para o original.");
        });

       bt_Undo.setOnAction(event -> {
           slider.setValue(controller.undoChangeBicycleDurationScale());
           notifyObservers("Últimos tempos de bicicleta restaurado.");
       });

    }

    /**
     * Returns the Pane containing all components.
     * @return BorderPane
     */
    public BorderPane getPane() {
        return popupview;
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


