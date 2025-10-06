package pt.pa.view.common;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;

public class Panels {
    public static GridPane createGridPane(int hgap, int vgap, int padding, int prefWidth) {
        GridPane gridPane = new GridPane();
        gridPane.setHgap(hgap);
        gridPane.setVgap(vgap);
        gridPane.setPadding(new Insets(padding));
        gridPane.setPrefWidth(prefWidth);
        gridPane.setAlignment(Pos.CENTER);
        return gridPane;
    }
}
