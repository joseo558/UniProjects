package pt.pa;

import javafx.application.Application;
import javafx.stage.Stage;
import pt.pa.controller.TransportMapFacade;

/**
 * Main class of the application
 */
public class Main extends Application {

    /**
     * The default entry point of the application
     * @param args String[] the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        TransportMapFacade transportMapApplication = new TransportMapFacade();
        transportMapApplication.start(primaryStage);
    }
}