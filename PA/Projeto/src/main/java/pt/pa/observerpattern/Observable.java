package pt.pa.observerpattern;

/**
 * Interface that represents an observable entity.
 */
public interface Observable {
    /**
     * Add an observer to the observable entity.
     * @param observer Observer the observer to be added
     */
    void addObserver(Observer observer);

    /**
     * Remove an observer from the observable entity.
     * @param observer Observer the observer to be removed
     */
    void removeObserver(Observer observer);

    /**
     * Notify all observers of a change in the observable entity.
     * @param arg Object the argument to be passed to the observers
     */
    void notifyObservers(Object arg);
}
