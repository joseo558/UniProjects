import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
/**
 * Stores the speakers, events and places. Basic management options for this elements (add, find or remove).
 *
 * @author David Maurício Ferreira, nº 202300443
 * @author José Luís Parreira de Oliveira, nº 202300558
 * @version 29/01/2024
 */
public class Manager
{
    /** List of event editions */
    private HashSet<Speaker> speakerList;
    /** List of event editions */
    private HashSet<Event> eventList;
    /** List of event editions */
    private HashSet<Place> placeList;
    /** Módulo de estatísticas */
    private Statistics statistics;
    /**
     * Constructor to create a new manager
     */
    public Manager()
    {
        speakerList = new HashSet<>();
        eventList = new HashSet<>();
        placeList = new HashSet<>();
        statistics = new Statistics(speakerList);
    }
    // Selectors
    /**
     * Returns the list of speakers
     * @return HashSet&lt;Speaker&gt; the list of speakers
     */
    public HashSet<Speaker> getSpeakerList(){return speakerList;}
    /**
     * Returns the list of events
     * @return HashSet&lt;Event&gt; the list of events
     */
    public HashSet<Event> getEventList(){return eventList;}
    /**
     * Returns the list of places
     * @return HashSet&lt;Place&gt; the list of places
     */
    public HashSet<Place> getPlaceList(){return placeList;}
    /**
     * Returns the number of speakers
     * @return int the number of speakers
     */
    public int getNumSpeakers(){return speakerList.size();}
    /**
     * Returns the number of events
     * @return int the number of events
     */
    public int getNumEvents(){return eventList.size();}
    /**
     * Returns the number of places
     * @return int the number of places
     */
    public int getNumPlaces(){return placeList.size();}
    /**
     * Returns the statistics module
     * @return Statistics the statistics module
     */
    public Statistics getStatistics(){return statistics;}
    // Other methods
    /**
     * Checks if the manager doesn't have data
     * @return boolean true if the manager has no data
     * @see #getNumSpeakers()
     * @see #getNumEvents()
     * @see #getNumPlaces()
     */
    public boolean hasNoData(){
        boolean noData = false;
        if(getNumSpeakers() == 0 && getNumEvents() == 0 && getNumPlaces() == 0){
            noData = true;
        }
        return noData;
    }
    // Add
    /**
     * Add a speaker to the list of speakers
     * @param speaker Speaker the speaker to add
     * @see #hasSpeaker(Speaker speaker)
     */
    public void addSpeaker(Speaker speaker){
        if(speaker != null && !hasSpeaker(speaker)){
            speakerList.add(speaker);
        }
    }
    /**
     * Add an event to the list of events
     * @param event Event the event to add
     * @see #hasEvent(Event event)
     */
    public void addEvent(Event event){
        if(event != null && !hasEvent(event)){
            eventList.add(event);
        }
    }
    /**
     * Add a place to the list of places
     * @param place Place the place to add
     * @see #hasPlace(Place place)
     */
    public void addPlace(Place place){
        if(place != null && !hasPlace(place)){
            placeList.add(place);
        }
    }
    // Check and get
    /**
     * Check if the speaker already exists
     * @param speaker Speaker the speaker to check if already exists
     * @return boolean true if the speaker already exists
     */
    public boolean hasSpeaker(Speaker speaker){
        return speakerList.contains(speaker);
    }
    /**
     * Check if a speaker already exists with this name
     * @param nameInput String the name to compare to the name of the existing speakers
     * @return boolean true if the speaker already exists
     */
    public boolean hasSpeaker(String nameInput){
        boolean isFound = false;
        if(nameInput != null){
            for(Speaker speaker : speakerList){
                if(nameInput.equalsIgnoreCase(speaker.getName())){
                    isFound = true;
                    break;
                }
            }
        }
        return isFound;
    }
    /**
     * Get the speaker with this name
     * @param nameInput String the name to compare to the name of the existing speakers
     * @return Speaker the speaker with this name
     */
    public Speaker getSpeaker(String nameInput){
        Speaker speakerFound = null;
        if(nameInput != null){
            for(Speaker speaker : speakerList){
                if(nameInput.equalsIgnoreCase(speaker.getName())){
                    speakerFound = speaker;
                    break;
                }
            }
        }
        return speakerFound;
    }
    /**
     * Check if the event already exists
     * @param event Event the event to check if already exists
     * @return boolean true if the event already exists
     */
    public boolean hasEvent(Event event){
        return eventList.contains(event);
    }
    /**
     * Check if an event already exists with this name
     * @param nameInput String the name to compare to the name of the existing events
     * @return boolean true if the event already exists
     */
    public boolean hasEvent(String nameInput){
        boolean isFound = false;
        if(nameInput != null){
            for(Event event : eventList){
                if(nameInput.equalsIgnoreCase(event.getName())){
                    isFound = true;
                    break;
                }
            }
        }
        return isFound;
    }
    /**
     * Get the event with this name
     * @param nameInput String the name to compare to the name of the existing events
     * @return Event the event with this name
     */
    public Event getEvent(String nameInput){
        Event eventFound = null;
        if(nameInput != null){
            for(Event event : eventList){
                if(nameInput.equalsIgnoreCase(event.getName())){
                    eventFound = event;
                    break;
                }
            }
        }
        return eventFound;
    }
    /**
     * Check if the place already exists
     * @param place Place the place to check if already exists
     * @return boolean true if the place already exists
     */
    public boolean hasPlace(Place place){
        return placeList.contains(place);
    }
    /**
     * Check if a place already exists with this name
     * @param nameInput String the name to compare to the name of the existing places
     * @return boolean true if the place already exists
     * @see Place#getName
     */
    public boolean hasPlace(String nameInput){
        boolean isFound = false;
        if(nameInput != null){
            for(Place place : placeList){
                if(nameInput.equalsIgnoreCase(place.getName())){
                    isFound = true;
                    break;
                }
            }
        }
        return isFound;
    }
    /**
     * Get the place with this name
     * @param nameInput String the name to compare to the name of the existing places
     * @return Place the place with this name
     * @see Place#getName
     */
    public Place getPlace(String nameInput){
        Place placeFound = null;
        if(nameInput != null){
            for(Place place : placeList){
                if(nameInput.equalsIgnoreCase(place.getName())){
                    placeFound = place;
                    break;
                }
            }
        }
        return placeFound;
    }
    // Remove
    /**
     * Remove a speaker from the list of speakers
     * @param speaker Speaker the speaker to remove
     * @see #hasSpeaker(Speaker speaker)
     */
    public void removeSpeaker(Speaker speaker){
        if(speaker != null && hasSpeaker(speaker)){
            speakerList.remove(speaker);
        }
    }
    /**
     * Remove an event from the list of events
     * @param event Event the event to remove
     * @see #hasEvent(Event event)
     */
    public void removeEvent(Event event){
        if(event != null && hasEvent(event)){
            eventList.remove(event);
        }
    }
    /**
     * Remove a place from the list of places
     * @param place Place the place to remove
     * @see #hasPlace(Place place)
     */
    public void removePlace(Place place){
        if(place != null && hasPlace(place)){
            placeList.remove(place);
        }
    }
    // Print
    /**
     * Print to the console every speaker's characteristics
     * @see Speaker#show
     */
    public void listAllSpeakers(){
        for(Speaker speaker : speakerList){
            speaker.show();
        }
    }
    /**
     * Print to the console every event's characteristics
     * @see Event#show
     */
    public void listAllEvents(){
        for(Event event : eventList){
            event.show();
        }
    }
    /**
     * Print to the console every place's characteristics
     * @see Place#show
     */
    public void listAllPlaces(){
        for(Place place : placeList){
            place.show();
        }
    }
}