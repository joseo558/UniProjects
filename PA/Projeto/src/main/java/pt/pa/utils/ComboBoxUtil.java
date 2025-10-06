package pt.pa.utils;

import com.brunomnsilva.smartgraph.graph.Vertex;
import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.Tooltip;
import pt.pa.transportmap.Stop;
import pt.pa.transportmap.TransportMap;

public class ComboBoxUtil {

    public ComboBoxUtil() {}

    /**
     * Create a vertex combobox
     * @return ComboBox<Vertex<Stop>> the vertex combobox
     */
    public static ComboBox<Vertex<Stop>> getVertexComboBox(TransportMap model) {
        ComboBox<Vertex<Stop>> cb = new ComboBox<>(FXCollections.observableArrayList(model.vertices()).sorted());
        cb.setPromptText("Selecione uma paragem");

        // Override the selected item text
        cb.setButtonCell(updateVertexComboBox());

        // Dropdown list
        cb.setCellFactory(lv -> updateVertexComboBox());

        return cb;
    }

    private static ListCell<Vertex<Stop>> updateVertexComboBox() {
        return new ListCell<>() {
            @Override
            protected void updateItem(Vertex<Stop> item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setTooltip(null);
                } else {
                    setText(item.element().toString());
                    setTooltip(new Tooltip(item.element().getCode()));
                }
            }
        };
    }
}
