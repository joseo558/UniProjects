package pt.pa.controller;

import com.brunomnsilva.smartgraph.graph.Graph;
import com.brunomnsilva.smartgraph.graph.GraphEdgeList;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import pt.pa.transportmap.Route;
import pt.pa.transportmap.Stop;
import pt.pa.transportmap.TransportMap;
import pt.pa.view.MainView;

import java.util.Objects;

/**
 * Class that represents the facade of the application
 */
public class TransportMapFacade {
    /** The controller of the application */
    private final TransportMapController controller;
    /** The CSS file */
    private final String CSS_FILE = "/styles/application.css";

    /**
     * Constructor for TransportMapFacade
     */
    public TransportMapFacade() {
        Graph<Stop, Route> graph = new GraphEdgeList<>();
        TransportMap model = new TransportMap(graph);
        MainView mainView = new MainView(graph, model, CSS_FILE);

        controller = new TransportMapController(model, mainView);
    }

    /**
     * Gets the controller of the application
     * @return TransportMapController the controller of the application
     */
    public TransportMapController getController() {
        return controller;
    }

    /**
     * Gets the view of the application
     * @return Parent the view of the application
     */
    public Parent getView() {
        return ((MainView) controller.getView()).getMainViewPane();
    }

    /**
     * Starts the application
     * @param primaryStage Stage the primary stage
     */
    public void start(Stage primaryStage) {
        // scene
        Scene scene = new Scene(getView(), 1504, 810);
        try {
            String cssFile = Objects.requireNonNull(getClass().getResource(CSS_FILE)).toExternalForm();
            scene.getStylesheets().add(cssFile);
        } catch (Exception e) {
            System.err.println("Error loading CSS file: " + e.getMessage());
            throw new RuntimeException(e);
        }
        // stage
        primaryStage.initStyle(StageStyle.DECORATED);
        primaryStage.setTitle("TransportMap");
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(event -> {
            controller.exitApplication();
        });
        primaryStage.show();
        // initialize graph display
        controller.getView().initGraphDisplay();
    }

    /**
     * Starts the application
     */
    public void start() {
        Stage primaryStage = new Stage();
        start(primaryStage);
    }
}