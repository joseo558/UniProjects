package pt.pa.view.tabs.metric;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import pt.pa.controller.TransportMapController;
import pt.pa.observerpattern.Observable;
import pt.pa.observerpattern.Observer;
import pt.pa.transportmap.Stop;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import pt.pa.transportmap.TransportType;
import pt.pa.view.common.Controls;
import pt.pa.view.common.Panels;

/**
 * View that shows the metrics of the transport map
 */
public class MetricView implements Observable {
    /** Pane with the metric view */
    private final Pane metricViewPane;
    /** The metric view actions */
    private final MetricActions metricActions;
    /** Pane with metric information */
    private final Pane informationPane;
    /** List of observers */
    private final List<Observer> observers;

    /**
     * Constructs the Metric View UI component, that contains action buttons for metric calculations
     */
    public MetricView() {
        metricActions = new MetricActions();

        informationPane = new StackPane(createDefaultInformation());
        informationPane.setPadding(new Insets(10));

        observers = new ArrayList<>();

        metricViewPane = new VBox(metricActions.getMetricActionsPane(), informationPane);
    }

    /**
     * Returns the pane with the metric view
     * @return Pane the pane with the metric view
     */
    public Pane getMetricViewPane() {
        return metricViewPane;
    }

    /**
     * Sets the triggers for the action buttons in the metric view
     * @param controller TransportMapController the controller to be used
     */
    public void setTriggers(TransportMapController controller) {
        metricActions.setTriggers(controller);
    }

    @Override
    public void addObserver(Observer o) {
        observers.add(o);
    }

    @Override
    public void removeObserver(Observer o) {
        observers.remove(o);
    }

    @Override
    public void notifyObservers(Object arg) {
        for(Observer o : observers) {
            o.update(this, arg);
        }
    }

    /**
     * Shows the number of stops metric in the information pane
     * @param totalStops int the total number of stops
     * @param isolatedStops int the number of isolated stops
     * @param nonIsolatedStops int the number of non isolated stops
     */
    public void showMetricNumberStops(int totalStops, int isolatedStops, int nonIsolatedStops) {
        showInformation( createPaneNumberStops(totalStops, isolatedStops, nonIsolatedStops) );
        notifyObservers("Métrica: Número de Paragens");
    }

    /**
     * Shows the number of routes metric in the information pane
     * @param totalRoutes int the total number of routes
     * @param map Map<TransportType, Integer> the map with the number of routes by transport type
     */
    public void showMetricNumberRoutes(int totalRoutes, Map<TransportType, Integer> map) {
        showInformation( createPaneNumberRoutes(totalRoutes, map) );
        notifyObservers("Métrica: Número de Rotas");
    }

    /**
     * Shows the top 5 centrality of the stops in the information pane
     * @param centralityList List<Map.Entry<Stop, Integer>> the list with the centrality of the stops
     */
    public void showMetricTop5(List<Map.Entry<Stop, Integer>> centralityList) {
        showInformation( createPaneTop5(centralityList) );
        notifyObservers("Métrica: Top 5");
    }

    /**
     * Shows the centrality of the stops in the information pane
     * @param centralityList List<Map.Entry<Stop, Integer>> the list with the centrality of the stops
     */
    public void showMetricCentrality(List<Map.Entry<Stop, Integer>> centralityList) {
        showInformation( createPaneCentrality(centralityList) );
        notifyObservers("Métrica: Centralidade");
    }

    /**
     * Shows the information in the information pane
     * @param information Pane the pane with the information to be shown
     */
    private void showInformation(Pane information) {
        informationPane.getChildren().clear();
        informationPane.getChildren().add(information);
    }

    /**
     * Creates the default information box
     */
    private VBox createDefaultInformation() {
        Label labelInformation = Controls.createLabel("Informações", "h1");
        Label labelResults = Controls.createLabel("Selecione uma métrica para visualizar os resultados.", "h3");
        return new VBox(labelInformation, labelResults);
    }

    /**
     * Creates a VBox with the number of stops in the transport map
     * @param totalStops int the total number of stops
     * @param isolatedStops int the number of isolated stops
     * @param nonIsolatedStops int the number of non isolated stops
     * @return VBox the VBox with the information
     */
    private VBox createPaneNumberStops(int totalStops, int isolatedStops, int nonIsolatedStops) {
        Label titleLabel = Controls.createLabel("Número de Paragens na Rede", "h1");
        Label numberStopsLabel = Controls.createLabel("Número de Paragens no Total: " + totalStops, "h3");
        Label isolatedStopsLabel = Controls.createLabel("Número de Paragens Isoladas: " + isolatedStops, "h3");
        Label nonIsolatedStopsLabel = Controls.createLabel("Número de Paragens não Isoladas: " + nonIsolatedStops, "h3");
        return new VBox(15, titleLabel, numberStopsLabel, isolatedStopsLabel, nonIsolatedStopsLabel);
    }

    /**
     * Creates a GridPane with the number of routes in the transport map
     * @param totalRoutes int the total number of routes
     * @param map Map<TransportType, Integer> the map with the number of routes by transport type
     * @return GridPane the GridPane with the information
     */
    public GridPane createPaneNumberRoutes(int totalRoutes, Map<TransportType, Integer> map) {
        GridPane grid = Panels.createGridPane(10, 10, 10, 500);

        // Title
        Label title = Controls.createLabel("Número de Rotas");
        title.setId("metricNumberRoutesTitle");

        // Top Row Labels
        Label typeLabel = Controls.createLabel("Tipo", "metricNumberRoutesLabels", "bold");
        Label totalLabel = Controls.createLabel("Total", "metricNumberRoutesLabels");

        // Bottom Row Labels
        Label numberRoutesLabel = Controls.createLabel("Nº de Rotas", "metricNumberRoutesLabels", "bold");
        Label totalAmountLabel = Controls.createLabel(String.valueOf(totalRoutes), "metricNumberRoutesLabels");

        grid.add(title, 0, 0, 8, 1);

        grid.add(typeLabel, 0, 1);
        grid.add(totalLabel, 1, 1);
        grid.add(numberRoutesLabel, 0, 2);
        grid.add(totalAmountLabel, 1, 2);

        int columnIndex = 2;

        // Add Transport Types and Amounts to the Grid
        for (Map.Entry<TransportType, Integer> entry : map.entrySet()) {
            Label type = Controls.createLabel(entry.getKey().toString(), "metricNumberRoutesLabels");
            Label amount = Controls.createLabel(entry.getValue().toString(), "metricNumberRoutesLabels");

            grid.add(type, columnIndex, 1);
            grid.add(amount, columnIndex, 2);

            columnIndex++;
        }

        return grid;
    }

    /**
     * Creates a VBox with the top 5 centrality of the stops in the transport map
     * @param centralityList List<Map.Entry<Stop, Integer>> the list with the centrality of the stops
     * @return VBox the VBox with the information
     */
    public VBox createPaneTop5(List<Map.Entry<Stop, Integer>> centralityList) {
        // Define axis (eixos)
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Nome da Paragem");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Nº Paragens");

        // Create XYChart (gráfico de linhas)
        XYChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
        chart.setTitle("Top 5 paragens mais centrais");

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Nº paragens adjacentes");

        // Find the 5 first stops (already sorted)
        List<Map.Entry<Stop, Integer>> top5Stops = centralityList.subList(0, Math.min(5, centralityList.size()));
        for (Map.Entry<Stop, Integer> entry : top5Stops) {
            series.getData().add(new XYChart.Data<>(entry.getKey().getName(), entry.getValue()));
        }

        // Add the series to the chart
        chart.getData().add(series);

        return new VBox(15, chart);
    }

    /**
     * Creates a VBox with the centrality of the stops in the transport map (table)
     * @param centralityList List<Map.Entry<Stop, Integer>> the list with the centrality of the stops
     * @return VBox the VBox with the information (table)
     */
    public VBox createPaneCentrality(List<Map.Entry<Stop, Integer>> centralityList) {
        // Create table model with the data
        ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();
        for (Map.Entry<Stop, Integer> entry : centralityList) {
            ObservableList<String> row = FXCollections.observableArrayList();
            row.add(entry.getKey().getName());
            row.add(entry.getValue().toString());
            data.add(row);
        }

        // Create the table
        TableView<ObservableList<String>> table = new TableView<>();
        TableColumn<ObservableList<String>, String> column1 = new TableColumn<>("Paragem");
        column1.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().get(0)));
        column1.setStyle("-fx-alignment: CENTER;");
        TableColumn<ObservableList<String>, String> column2 = new TableColumn<>("Número de Paragens Adjacentes");
        column2.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().get(1)));
        column2.setStyle("-fx-alignment: CENTER;");
        table.getColumns().addAll(column1, column2);
        table.setItems(data);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(500);

        return new VBox(15, new Label("Centralidade das Paragens"), table);
    }
}