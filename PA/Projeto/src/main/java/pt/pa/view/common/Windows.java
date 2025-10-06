package pt.pa.view.common;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.Objects;

/**
 * Utility class for creating windows
 */
public class Windows {
    /**
     * Applies a CSS stylesheet to a scene
     * @param scene Scene the scene to apply the stylesheet
     * @param cssFilePath String the path to the CSS file
     */
    private static void applyStylesheet(Scene scene, String cssFilePath) {
        String cssFile = Objects.requireNonNull(Windows.class.getResource(cssFilePath)).toExternalForm();
        scene.getStylesheets().add(cssFile);
    }

    /**
     * Creates an alert with an error message
     * @param message String the message to show
     * @return Alert the alert
     */
    public static Alert createErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message);
        alert.setTitle("Erro - TransportMap");
        alert.setHeaderText("Ocorreu um erro");
        return alert;
    }

    public static Alert createInfoAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message);
        alert.setTitle("Informação - TransportMap");
        alert.setHeaderText("Informação");
        return alert;
    }

    /**
     * Creates a popup window centered in the main view
     * @param content Pane the content of the popup
     * @param width double the width of the popup
     * @param height double the height of the popup
     * @param cssFilePath String the path to the CSS file
     * @param mainViewPane Pane the main view pane
     * @return Stage the popup window
     */
    public static Stage createPopup(Pane content, double width, double height, String cssFilePath, Pane mainViewPane) {
        Stage popupStage = new Stage(StageStyle.UNDECORATED);
        popupStage.initModality(Modality.APPLICATION_MODAL); // no borders, blocks app

        Button closeButton = new Button("Fechar");
        closeButton.setOnAction(e -> popupStage.close());

        VBox popupRoot = new VBox(15, content, closeButton);
        popupRoot.setId("popUp");
        popupRoot.setAlignment(Pos.CENTER);

        Scene popupScene = new Scene(popupRoot, Color.TRANSPARENT);
        applyStylesheet(popupScene, cssFilePath);
        popupStage.setScene(popupScene);

        popupStage.setWidth(width);
        popupStage.setHeight(height);

        // Center the popup in the middle of the screen
        popupStage.setX(mainViewPane.getScene().getWindow().getX() + mainViewPane.getScene().getWidth() / 2 - popupStage.getWidth() / 2);
        popupStage.setY(mainViewPane.getScene().getWindow().getY() + mainViewPane.getScene().getHeight() / 2 - popupStage.getHeight() / 2);

        return popupStage;
    }
}
