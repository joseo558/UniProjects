package pt.pa.observerpattern;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a subject that can be observed by observers
 */
public class Subject implements Observable {
    /** List of observers */
    private List<Observer> observers;

    /**
     * Constructs a Subject instance
     */
    public Subject() {
        observers = new ArrayList<>();
    }

    @Override
    public void addObserver(Observer observer) {
        if(!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    @Override
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(Object arg) {
        for(Observer o : observers) {
            o.update(this, arg);
        }
    }
}