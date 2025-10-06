package pt.pa.observerpattern;

/**
 * Interface for Observer objects
 */
public interface Observer {
    /**
     * Method called by the Observable object to notify the Observer of a change
     * @param subject Observable The Observable object that changed
     * @param arg Object The argument passed by the Observable object
     */
    void update(Observable subject, Object arg);
}
