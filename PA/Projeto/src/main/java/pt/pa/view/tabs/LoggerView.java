package pt.pa.view.tabs;

import javafx.geometry.Insets;
import pt.pa.logger.Logger;
import pt.pa.observerpattern.Observable;
import pt.pa.observerpattern.Observer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;

/**
 * Class that represents the logger view, which is a ListView that displays all commands executed by the user
 */
public class LoggerView extends ListView<String> implements Observer {
    /** The Logger instance */
    private final Logger logger;
    /** List of commands */
    private final List<String> commandList;
    /** Observable list of commands */
    private final ObservableList<String> commandListObs;

    /**
     * Constructs a new LoggerView instance and displays it.
     */
    public LoggerView() {
        logger = new Logger();
        commandList = new LinkedList<>();

        // ListView
        commandListObs = FXCollections.observableArrayList(commandList);
        this.setItems(commandListObs);

        // Style
        this.setPadding(new Insets(10));
        this.setPrefWidth(560);
        this.setMaxHeight(725);
        this.setMouseTransparent(true); // Disable user interaction
    }

    @Override
    public void update(Observable o, Object arg) {
        if (!(arg instanceof String)) {
            return;
        }

        LocalDateTime time = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        String commandWithTime = String.format("%s > %s",
                time.format(formatter),
                arg
        );

        commandList.add(commandWithTime);
        commandListObs.setAll(commandList);
        this.setItems(commandListObs);
        // relay the update to the logger
        logger.update(o, commandWithTime);
        saveMemory();
    }

    /**
     * Saves memory space by eliminating the oldest commands in the list
     */
    private void saveMemory(){
        if(commandList.size() > 100){
            for(int i = 0; i < 50; i++){
                commandList.remove(i);
            }
        }
    }
}