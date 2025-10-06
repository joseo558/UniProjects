import java.util.HashSet;
import java.util.HashMap;
/**
 * Calculate Statistics.
 *
 * @author David Maurício Ferreira, nº 202300443
 * @author José Luís Parreira de Oliveira, nº 202300558
 * @version 29/01/2024
 */
public class Statistics
{
    /** List of speakers */
    HashSet<Speaker> speakerList;
    /** List of TOP speakers */
    HashSet<Speaker> topSpeakerList;
    /** List of BIG speakers */
    HashSet<Speaker> bigSpeakerList;
    /** List of NEW speakers */
    HashSet<Speaker> newSpeakerList;
    /** List of WEAK speakers */
    HashSet<Speaker> weakSpeakerList;
    /** List of UNKNOWN speakers */
    HashSet<Speaker> unknownSpeakerList;
    /** List of Best editions for each event */
    HashMap<Event, Edition> bestEditionForEvent;
    // cada alteraçao no speakerlist, actualizar
    /**
     * Constructor for objects of the class Statistics
     * @param speakerList HashSet<Speaker> the list with all speakers
     * @see Speaker#getPopularity
     */
    public Statistics(HashSet<Speaker> speakerList)
    {
        if(speakerList != null){
            this.speakerList = speakerList;
            topSpeakerList = new HashSet<>();
            bigSpeakerList = new HashSet<>();
            newSpeakerList = new HashSet<>();
            weakSpeakerList = new HashSet<>();
            unknownSpeakerList = new HashSet<>();
            Event.PopularityLevel popularity;
            for(Speaker s : speakerList){
                popularity = s.getPopularity();
                getList(popularity).add(s);
            }
            bestEditionForEvent = new HashMap<>();
        }
    }
    /**
     * Get the list that has the speakers with the popularity supplied
     * @param popularity Event.PopularityLevel the popularity of the speaker
     * @return HashSet<Speaker> the list that has the speakers with the popularity supplied
     */
    private HashSet<Speaker> getList(Event.PopularityLevel popularity){
        switch(popularity){
            case TOP:
                return topSpeakerList;
            case BIG:
                return bigSpeakerList;
            case NEW:
                return newSpeakerList;
            case WEAK:
                return weakSpeakerList;
            case UNKNOWN:
                return unknownSpeakerList;
            default:
                return null;
        }
    }
    /**
     * Update popularity in collection
     * @param speaker Speaker the speaker whose popularity we need to update
     * @param oldPopularity Event.PopularityLevel the old popularity for that speaker
     * @return String the text with the popularity change to print to the console by another fuction
     * @see Speaker#getPopularity
     * @see Event.PopularityLevel
     * @see ManageEditions#simulateLecture(Manager manager, Event event, Edition edition, Lecture lecture)
     */
    public String editSpeakerPopularity(Speaker speaker, Event.PopularityLevel oldPopularity){
        if(speaker != null && oldPopularity != null){
            Event.PopularityLevel newPopularity = speaker.getPopularity();
            // Remove from old popularity list
            getList(oldPopularity).remove(speaker);
            // Add to new popularity list
            getList(newPopularity).add(speaker);
            if(newPopularity.getWeight() > oldPopularity.getWeight()){ // New popularity is higher
                String text = "\u25B2 " + speaker.getName() + " - Popularidade antiga: " + oldPopularity.toString() + " - Popularidade nova: " + newPopularity.toString();
                return text;
            }else{
                String text = "\u25BC " + speaker.getName() + " - Popularidade antiga: " + oldPopularity.toString() + " - Popularidade nova: " + newPopularity.toString();
                return text;
            }
        }
        return null;
    }
    /**
     * Print to the console the speakers ranking
     * @see Speaker#getName
     * @see Speaker#getNumLectures
     * @see calculateAverage(Speaker speaker)
     */
    public void showSpeakerRanking(){
        System.out.println();
        System.out.println("*** Topo ***");
        for(Speaker s : topSpeakerList){
            System.out.println(s.getName() + " - Número de palestras: " + s.getNumLectures() + " - Média de participantes: " + calculateAverage(s));
        }
        System.out.println("*** Grande ***");
        for(Speaker s : bigSpeakerList){
            System.out.println(s.getName() + " - Número de palestras: " + s.getNumLectures() + " - Média de participantes: " + calculateAverage(s));
        }
        System.out.println("*** Novidade ***");
        for(Speaker s : newSpeakerList){
            System.out.println(s.getName() + " - Número de palestras: " + s.getNumLectures() + " - Média de participantes: " + calculateAverage(s));
        }
        System.out.println("*** Fraca ***");
        for(Speaker s : weakSpeakerList){
            System.out.println(s.getName() + " - Número de palestras: " + s.getNumLectures() + " - Média de participantes: " + calculateAverage(s));
        }
        System.out.println("*** Desconhecida ***");
        for(Speaker s : unknownSpeakerList){
            System.out.println(s.getName() + " - Número de palestras: " + s.getNumLectures() + " - Média de participantes: " + calculateAverage(s));
        }
    }
    /**
     * Calculate the average of participants in this speaker's lectures
     * @param speaker Speaker the speaker for which to calculate the average
     * @return double the average of participants in this speaker's lectures
     * @see Speaker#getNumLectures
     * @see Speaker#getLectureList
     * @see Lecture#getNumRegistrationsLecture
     */
    private double calculateAverage(Speaker speaker){
        double average = 0.0;
        int total = 0;
        double numLectures = (double) speaker.getNumLectures();
        HashSet<Lecture> lectureList = speaker.getLectureList();
        for(Lecture lec : lectureList){
            total += lec.getNumRegistrationsLecture();
        }
        double totalDouble = (double) total;
        average = total/numLectures;
        return average;
    }
    /**
     * Calculate the average of participants per lecture in this edition. Saves the result in the edition object as well as return the value.
     * @param edition Edition the edition with the lectures to calculate and set the average
     * @return double the average of participants per lecture in this edition
     * @see Edition#getNumRegistrationsEdition
     * @see Edition#getNumLectures
     * @see Edition#setAverageParticipantsPerLecture(double average)
     */
    private double calculateAverageParticipantsPerLecture(Edition edition){
        if(edition != null){
            double averageParticipantsPerLecture = edition.getNumRegistrationsEdition()/edition.getNumLectures();
            edition.setAverageParticipantsPerLecture(averageParticipantsPerLecture);
            return averageParticipantsPerLecture;
        }
        return 0.0;
    }
    /**
     * Return the event with the best edition of all
     * @param manager Manager the manager object handling the events
     * @param bestEdition Edition the best edition of all
     * @see #bestEditionForEvent
     */
    public Event getBestEvent(Manager manager, Edition bestEdition){
        Event bestEvent = null;
        for(Event event : bestEditionForEvent.keySet()){
            if(bestEditionForEvent.get(event) == bestEdition){
                bestEvent = event;
                break;
            }
        }
        return bestEvent;
    }
    /**
     * Print the best edition's statistics
     * @param manager Manager the manager object handling the events
     * @see #getEditionWithHighestAverageInAllEvents(Manager manager, double highest)
     * @see #getBestEvent(Manager manager, Edition bestEdition)
     * @see Edition#getEditionNumber
     * @see #editionStats(Edition edition)
     */
    public void bestEditionStats(Manager manager){
        Edition bestEdition = getEditionWithHighestAverageInAllEvents(manager, getHighestAverageAllEvents(manager));
        Event eventWithBest = getBestEvent(manager, bestEdition);
        if(eventWithBest != null){
            System.out.println("Evento: " + eventWithBest.getName());
            System.out.println("Melhor edição foi a número " + bestEdition.getEditionNumber());
            editionStats(bestEdition);
        }
    }
    /**
     * Show the edition's statistics
     * @param edition Edition the edition of which to show the statistics
     * @see Edition#getNumRegistrationsEdition
     * @see Edition#getNumLectures
     * @see Edition#getNumSpeakers
     * @see #calculateAverageParticipantsPerLecture(Edition edition)
     */
    public void editionStats(Edition edition){
        System.out.println("Número de participantes :" + edition.getNumRegistrationsEdition());
        System.out.println("Número de palestras :" + edition.getNumLectures());
        System.out.println("Número de palestrantes :" + edition.getNumSpeakers());
        System.out.println("Média de participantes por palestra :" + calculateAverageParticipantsPerLecture(edition));
    }
    /**
     * Return the highest average of participants per lecture among all editions in all the events
     * @param manager Manager the manager object handling the events
     * @return double the highest average of participants per lecture among all editions in all the events
     * @see Event#getEventList
     * @see #getHighestAverage(Event event)
     */
    public double getHighestAverageAllEvents(Manager manager){
        double highest = 0.0;
        double temp = 0.0;
        for(Event event : manager.getEventList()){
            temp = getHighestAverage(event);
            if(temp > highest){highest = temp;}
        }
        return highest;
    }
    /**
     * Return the edition with the highest average of participants per lecture among all editions in all the events
     * @param manager Manager the manager object handling the events
     * @param highest double the highest average of participants per lecture among all editions in all the events
     * @return Edition the edition with the highest average of participants per lecture among all editions in all the events
     * @see Manager#getEventList
     * @see #getEditionWithHighestAverage(Event event, double highest)
     */
    public Edition getEditionWithHighestAverageInAllEvents(Manager manager, double highest){
        Edition edition = null;
        for(Event event : manager.getEventList()){
            edition = getEditionWithHighestAverage(event, highest);
            if(edition != null){return edition;}
        }
        return null;
    }
    /**
     * Return the highest average of participants per lecture among all the event's editions
     * @param event Event the event in which editions to find the highest average
     * @return double the highest average of participants per lecture among all the event's editions
     * @see Event#getEditionList
     * @see Edition#getAverageParticipantsPerLecture
     */
    public double getHighestAverage(Event event){
        double highest = 0.0;
        double average = 0.0;
        for(Edition edition : event.getEditionList()){
            average = edition.getAverageParticipantsPerLecture();
            if(average > highest){highest = average;}
        }
        return highest;
    }
    /**
     * Return the edition with the highest average of participants per lecture in this event. Saves the best edition for this event.
     * @param event Event the event in which editions to find the one with the highest average
     * @param highest double the highest average of participants per lecture among all the event's editions
     * @return Edition the edition with the highest average of participants per lecture in this event
     * @see Event#getEditionList
     * @see Edition#getAverageParticipantsPerLecture
     * @see #bestEditionForEvent
     */
    public Edition getEditionWithHighestAverage(Event event, double highest){
        for(Edition edition : event.getEditionList()){
            if(edition.getAverageParticipantsPerLecture() == highest){
                bestEditionForEvent.put(event, edition);
                return edition;
            }
        }
        return null;
    }
    /**
     * Print to the console the list of best editions per event
     * @param manager Manager the manager object handling the events
     * @see #bestEditionForEvent
     * @see Event#getName
     * @see Edition#getEditionNumber
     * @see #editionStats(Edition edition)
     * @see #bestEditionStats(Manager manager)
     */
    public void listBestEditionsPerEvent(Manager manager){
        Edition edition;
        for(Event event : bestEditionForEvent.keySet()){
            System.out.println("-----------------------");
            System.out.println("Evento: " + event.getName());
            System.out.println("Melhor edição foi a número " + bestEditionForEvent.get(event).getEditionNumber());
            editionStats(bestEditionForEvent.get(event));
            System.out.println("-----------------------");
        }
        System.out.println();
        System.out.println("*** Este é o melhor evento: *** ");
        bestEditionStats(manager);
    }
}