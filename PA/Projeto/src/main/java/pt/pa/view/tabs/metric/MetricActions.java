package pt.pa.view.tabs.metric;

import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import pt.pa.controller.TransportMapController;
import pt.pa.view.common.Controls;
import pt.pa.view.common.Panels;

/**
 * Manages the action buttons for the metric view
 */
public class MetricActions {
    /** Metric view actions pane */
    private GridPane metricActionsPane;
    /** Number of stops button */
    private Button btNumberStops;
    /** Number of routes button */
    private Button btNumberRoutes;
    /** Centrality button */
    private Button btCentrality;
    /** Top 5 button */
    private Button btTop5;

    /**
     * Constructs the pane with the action buttons
     */
    public MetricActions(){
        metricActionsPane = Panels.createGridPane(10, 10, 40, 560);

        // Create buttons
        btNumberStops = Controls.createButton("Número de Paragens", "metricButton");
        btNumberRoutes = Controls.createButton("Número de Rotas", "metricButton");
        btCentrality = Controls.createButton("Centralidade", "metricButton");
        btTop5 = Controls.createButton("Top 5", "metricButton");

        // Add buttons to the grid
        metricActionsPane.add(btNumberStops, 0, 0);
        metricActionsPane.add(btNumberRoutes, 1, 0);
        metricActionsPane.add(btCentrality, 0, 1);
        metricActionsPane.add(btTop5, 1, 1);
    }

    /**
     * Returns the metric actions pane
     * @return GridPane the metric actions pane
     */
    public GridPane getMetricActionsPane() {
        return metricActionsPane;
    }

    /**
     * Sets the triggers for the action buttons
     * @param controller TransportMapController the controller to set the triggers
     */
    public void setTriggers(TransportMapController controller) {
        btNumberStops.setOnAction(e -> controller.showMetricNumberStops());
        btNumberRoutes.setOnAction(e -> controller.showMetricNumberRoutes());
        btCentrality.setOnAction(e -> controller.showMetricCentrality());
        btTop5.setOnAction(e -> controller.showMetricTop5());
    }
}