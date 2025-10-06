import java.util.HashSet;
/**
 * Stores the characteristics of an event.
 *
 * @author David Maurício Ferreira, nº 202300443
 * @author José Luís Parreira de Oliveira, nº 202300558
 * @version 29/01/2024
 */
public class Event
{
    /** Name of event */
    private String name;
    /** List of event editions */
    private HashSet<Edition> editionList;
    /** 
     * Event popularity
     * @see PopularityLevel
     */
    private PopularityLevel popularity;
    /**
     * Constructor to create a new event
     * @param name String name of event
     */
    public Event(String name)
    {
        //keyword "voltar" is reserved for the console
        if(name!=null && !name.equalsIgnoreCase("voltar")){
            this.name = name;
            editionList = new HashSet<Edition>();
            popularity = PopularityLevel.UNKNOWN; // default is UNKNOWN
        }
    }
    /**
     * Constructor to create an event with existing data
     * @param name String name of event
     * @param popularity PopularityLevel the event popularity
     * @param editionList HashSet&lt;Edition&gt; the list of event editions
     */
    public Event(String name, PopularityLevel popularity, HashSet<Edition> editionList){
        if(name!=null && !name.equalsIgnoreCase("voltar") && popularity !=null && editionList != null){
            this.name = name;
            this.editionList = editionList;
            this.popularity = popularity;
        }
    }
    /**
     * Define the popularity constant (enum type)
     * Used by {@link Event} and {@link Speaker} classes
     */
    public enum PopularityLevel {
        UNKNOWN ("Desconhecida", 1/3), 
        WEAK ("Fraca", 0.5), 
        NEW ("Novidade", 1.0), 
        BIG ("Grande", 1.5), 
        TOP ("Topo", 2.0);
        /** Popularity as a text for UI */
        private String text;
        /** The popularity weight, used in the calculation of edition participants */
        private double weight;
        /** Private constructor for PopularityLevel */
        private PopularityLevel(String text, double weight){
            this.text = text;
            this.weight = weight;
        }
        /**
         * Returns the popularity weight
         * @return double the popularity weight, used in the calculation of lectures participants
         * @see Lecture#simulateRegistrations
        */
        public double getWeight(){return weight;}
        /**
         * Returns the popularity as a text for UI
         * @return String popularity as a text for UI
        */
        @Override
        public String toString(){return text;}
    }
    // Selectors
    /**
     * Returns the event name
     * @return String event name
     */
    public String getName(){return name;}
    /**
     * Returns the list of event editions
     * @return HashSet&lt;Edition&gt; the list of event editions
     */
    public HashSet<Edition> getEditionList(){return editionList;}
    /**
     * Returns the event popularity (enum type)
     * @return PopularityLevel the event popularity (enum type)
     */
    public PopularityLevel getPopularity(){return popularity;}
    /**
     * Returns the number of event editions
     * @return int the number of event editions
     */
    public int getNumEditions(){return editionList.size();}
    // Modifiers
    /**
     * Change the event name
     * @param name String the new event name
     */
    public void setName(String name){
        if(name != null && !name.equalsIgnoreCase("voltar")){
            this.name = name;
        }
    }
    // Other Methods
    /**
     * Adds an edition to the event. Only adds if theres no other edition ocorring at the same place and time.
     * @param edition Edition the edition to add to the event
     * @see #findIntersectingEdition(Edition edition)
     */
    public void addEdition(Edition edition){
        if(edition != null){
            if(!findIntersectingEdition(edition)){
                editionList.add(edition);
            }
        }
    }
    /**
     * Remove an edition from the event if it's in the event's editions list
     * @param edition Edition the edition to remove from the event's editions list
     * @see #hasEdition(Edition edition)
     */
    public void removeEdition(Edition edition){
        if(edition != null && hasEdition(edition)){
            editionList.remove(edition);
        }
    }
    /**
     * Checks if an edition exists in the event's list of editions
     * @param edition Edition the edition to check if it's in the event's editions list
     * @return boolean true if the edition exists in the event's list of editions
     */
    public boolean hasEdition(Edition edition){
        boolean isFound = false;
        if(edition != null){
            isFound = editionList.contains(edition);
        }
        return isFound;
    }
    /**
     * Returns the edition with the supplied edition number
     * @param editionNum int the edition number to check if it's in the event's editions list
     * @return Edition the edition with the supplied edition number or an null Edition otherwise
     * @see Edition#getEditionNumber
     */
    public Edition getEdition(int editionNum){
        Edition editionFound = null;
        if(editionNum > 0){
            for(Edition edition : editionList){
                if(editionNum == edition.getEditionNumber()){
                    editionFound = edition;
                    break;
                }
            }
        }
        return editionFound;
    }
    /**
     * Checks if an edition exists in the event's list of editions with the supplied edition number
     * @param editionNum int the edition number to check if it's in the event's editions list
     * @return boolean true if an edition exists in the event's list of editions with the supplied edition number
     * @see Edition#getEditionNumber
     */
    public boolean hasEdition(int editionNum){
        boolean isFound = false;
        if(editionNum > 0){
            for(Edition edition : editionList){
                if(editionNum == edition.getEditionNumber()){
                    isFound = true;
                    break;
                }
            }
        }
        return isFound;
    }
    /**
     * Prints to the console the event's characteristics
     * @see PopularityLevel#toString
     * @see #getNumEditions
     * @see Edition#show
     */
    public void show(){
        System.out.println();
        System.out.println("Nome do evento: " + name);
        System.out.println("Popularidade: " + popularity.toString());
        System.out.println("Edições (" + getNumEditions() + "): ");
        for(Edition edition : editionList){
            edition.show();
        }
    }
    /**
     * Calculate the event's popularity based on the editions simulated. Calculates the occupancy rate of the event (participants per capacity) which is used by another method to return the popularity. This popularity is assigned to the event.
     * @see #popularity
     * @see Edition#isSimulated
     * @see Edition#getLectureList
     * @see Edition#getNumRegistrationsEdition
     * @see Lecture#getRoom
     * @see Room#getCapacity
     * @see #calculatePopularity(double occupancyRate)
     */
    public void calculateEventPopularity(){
        HashSet<Lecture> lectureList;
        int sumParticipants = 0;
        int sumCapacity = 0;
        for(Edition edition : editionList){
            if(edition.isSimulated()){
                lectureList = edition.getLectureList();
                sumParticipants += edition.getNumRegistrationsEdition();
                for(Lecture lecture : lectureList){
                    sumCapacity += lecture.getRoom().getCapacity();
                }
            }
        }
        if(sumCapacity !=0){
            double sumParticipantsDouble = (double) sumParticipants;
            double sumCapacityDouble = (double) sumCapacity;
            double occupancyRate = (sumParticipantsDouble / sumCapacityDouble);
            popularity = calculatePopularity(occupancyRate);
        }
    }
    /**
     * Return the popularity (enum type) based on the occupancy rate of an event/lecture. Used in the calculation of the event popularity and the speakers popularity.
     * @param occupancyRate double the occupancy rate of an event/lecture
     * @return PopularityLevel the popularity (enum type)
     * @see #calculateEventPopularity
     * @see Speaker#calculateSpeakerPopularity
     */
    public static PopularityLevel calculatePopularity(double occupancyRate){
        if(occupancyRate > 0.0 && occupancyRate < 0.25){
            return PopularityLevel.WEAK;
        }else if(occupancyRate >= 0.25 && occupancyRate < 0.5){
            return PopularityLevel.NEW;
        }else if(occupancyRate >= 0.5 && occupancyRate < 0.75){
            return PopularityLevel.BIG;
        }else if(occupancyRate >= 0.75 && occupancyRate <= 1){
            return PopularityLevel.TOP;
        }else{return PopularityLevel.UNKNOWN;}
    }
    /**
     * Returns true if there are other editions ocorring in the same place and time as the one given
     * @param edition Edition the edition to compare to the existing editions in terms of place and time set
     * @return boolean true if there are other editions ocorring in the same place and time as the one provided as an parameter
     * @see #addEdition(Edition edition)
     * @see Edition#getPlace
     * @see Edition#getEndDate
     * @see Edition#getStartDate
     */
    public boolean findIntersectingEdition(Edition edition){
        if(editionList !=null){
            for(Edition editionToCompare : editionList){
                if(edition.getPlace() == editionToCompare.getPlace() && (edition.getEndDate().isAfter(editionToCompare.getStartDate()) && edition.getStartDate().isBefore(editionToCompare.getEndDate()))){
                    return true;
                }
            }
        }
        return false;
    }
}