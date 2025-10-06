package pt.pa.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/**
 * Utility class with a singleton approach to load the
 * config.properties file within this project
 *
 * @author amfs
 */
public class PropertiesUtil {

    /**
     * The static instance of this class
     */
    private static PropertiesUtil instance;

    /**
     * The Java properties object used by this class
     */
    private final Properties properties = new Properties();

    /**
     * Constructor, private
     *
     * @throws IOException if the config.properties file is missing
     */
    private PropertiesUtil() throws IOException {
        // Load the config.properties file
        loadProperties();
    }

    /**
     * Returns the instance of this class in a singleton approach
     *
     * @return the instance of this class
     * @throws IOException if the config.properties file is missing
     */
    public static PropertiesUtil getInstance() throws IOException {
        if (instance == null) {
            instance = new PropertiesUtil();
        }
        return instance;
    }

    /**
     * Method to load the properties file
     *
     * @throws IOException if the file does not exist
     */
    private void loadProperties() throws IOException {
        Path path = Path.of("src/main/resources", "config.properties");
        try (var reader = Files.newBufferedReader(path)) {
            properties.load(reader);
        }
    }

    /**
     *
     * Returns the value of a given property
     *
     * @param key the key of the property
     * @return the value of the property
     */
    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    /**
     *
     *
     * @param key the key of the property
     * @param defaultValue the default value to be used
     * @return the value of the property if the key exists, or the default value
     */
    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    /**
     * Checks if a key exists
     *
     * @param key the key of the property
     * @return whether the property exists or not
     */
    public boolean containsKey(String key) {
        return properties.containsKey(key);
    }
}
