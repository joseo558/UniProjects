package pt.pa.logger;

import pt.pa.observerpattern.Observable;
import pt.pa.observerpattern.Observer;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Class that represents a Logger, which saves commands to a text file
 */
public class Logger implements Observer {
    /** File name to save the commands */
    private static final String fileName = "logs.txt";

    /**
     * Constructs a Logger instance
     */
    public Logger() {}

    @Override
    public void update(Observable o, Object arg) {
        if (!(arg instanceof String)) {
            return;
        }

        writeToFile( (String) arg );
    }

    /**
     * Creates a logs text file, if it doesn't exist.
     * Writes a command to the command logs text file.
     * @param command String the command to be written to the file
     */
    private void writeToFile(String command) {
        try {
            // Creates the file if it doesn't exist
            // Append: true, to write commands at the end of the file
            BufferedWriter fileWriter = new BufferedWriter(new FileWriter(fileName, true));

            fileWriter.write(command + "\n");
            fileWriter.close();

        } catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    /**
     * Reads the logs text file and returns a list of commands
     * @return List<String> the list of commands
     */
    synchronized public List<String> readFromFile() {
        List<String> commands = new ArrayList<>();
        try {
            // Reads the file
            BufferedReader fileReader = new BufferedReader(new FileReader(fileName));

            String line;
            while ((line = fileReader.readLine()) != null) {
                commands.add(line);
            }

            fileReader.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return commands;
    }

    /**
     * Clears the logs text file
     */
    synchronized public void clearFile() {
        try (FileOutputStream fileOutputStream = new FileOutputStream(fileName)) {
            fileOutputStream.getChannel().truncate(0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}