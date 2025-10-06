package pt.pa.view.common;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

/**
 * Utility class for creating JavaFX controls
 */
public class Controls {
    /**
     * Creates an unclosable tab pane with the given tabs
     * @param maxWidth int the max width of the tabs
     * @param maxHeight int the max height of the tabs
     * @param tabs Tab... the tabs to add to the tab pane
     * @return TabPane the tab pane
     */
    public static TabPane createTabPane(int maxWidth, int maxHeight, Tab...tabs) {
        TabPane tabPane = new TabPane(tabs);

        tabPane.setTabMaxWidth(maxWidth);
        tabPane.setTabMaxHeight(maxHeight);

        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        return tabPane;
    }

//    public static Tab createTab(String text, Node content) {
//        return new Tab(text, content);
//    }

    /**
     * Create a centered control with the given style classes
     * @param control Control the control to be styled
     * @param classNames String... the optional style classes
     * @return Control the control created
     */
    private static <T extends Control> T createControl(T control, String... classNames) {
        GridPane.setHalignment(control, HPos.CENTER);
        if (classNames != null) {
            control.getStyleClass().addAll(classNames);
        }
        return control;
    }

    /**
     * Create a centered button with the given text and optional style classes
     * @param text String the text of the button
     * @param classNames String... the optional style classes
     * @return Button the button created
     */
    public static Button createButton(String text, String... classNames) {
        Button button = createControl(new Button(text), classNames);
        button.setAlignment(Pos.CENTER);
        button.setPadding(new Insets(5));
        return button;
    }

    /**
     * Create a centered label with the given text and optional style classes
     * @param text String the text of the label
     * @param classNames String... the optional style classes
     * @return Label the label created
     */
    public static Label createLabel(String text, String... classNames) {
        return createControl(new Label(text), classNames);
    }
}
