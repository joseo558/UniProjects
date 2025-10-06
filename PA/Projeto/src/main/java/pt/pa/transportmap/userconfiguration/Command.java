package pt.pa.transportmap.userconfiguration;

import java.io.Serializable;

/**
 * Represents a command that can be executed and undone
 */
public interface Command extends Serializable {
    /**
     * Execute the command
     */
    void execute();

    /**
     * Undo the command
     */
    void undo();
}