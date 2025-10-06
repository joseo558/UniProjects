package pt.pa.view.menu;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import pt.pa.view.common.Controls;

/**
 * Class that represents the top tab pane of the application which allows navigation between the different views
 */
public class TopTabPane {
    private final TabPane tabPane;
    private final Tab startTab;
    private final Tab pRouteTab;
    private final Tab metricTab;
    private final Tab loggerMap;

    /**
     * Constructor for TopTabPane
     */
    public TopTabPane() {
        // create tabs
        startTab = new Tab("Início");
        pRouteTab = new Tab("Personalizar");
        metricTab = new Tab("Métricas");
        loggerMap = new Tab("Histórico");

        tabPane = Controls.createTabPane(100, 25, startTab, pRouteTab, metricTab, loggerMap);
    }

    /**
     * Gets the tab pane
     * @return TabPane the tab pane
     */
    public TabPane getTabPane() {
        return tabPane;
    }

    /**
     * Checks if the current tab is the personalized route view
     * @return true if the current tab is the personalized route view, false otherwise
     */
    public boolean isPRView() {
        return tabPane.getSelectionModel().getSelectedItem().equals(pRouteTab);
    }

    /**
     * Sets the tab event callbacks
     * @param showStartView Runnable the callback to show the start view
     * @param showPRouteView Runnable the callback to show the personalized route view
     * @param showMetricView Runnable the callback to show the metric view
     * @param showLoggerView Runnable the callback to show the logger view
     * @throws IllegalArgumentException if any of the callbacks is null
     */
    public void setTabEvents(Runnable showStartView, Runnable showPRouteView, Runnable showMetricView, Runnable showLoggerView) throws IllegalArgumentException {
        if (showStartView == null || showPRouteView == null || showMetricView == null || showLoggerView == null) {
            throw new IllegalArgumentException("All tab event callbacks must be non-null.");
        }

        tabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldTab, newTab) -> {
            if (newTab == startTab) {
                showStartView.run();
            } else if (newTab == pRouteTab) {
                showPRouteView.run();
            } else if (newTab == metricTab) {
                showMetricView.run();
            } else if (newTab == loggerMap) {
                showLoggerView.run();
            }
        });
    }
}