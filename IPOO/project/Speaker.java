import java.util.HashSet;
/**
 * Stores the characteristics of a speaker.
 *
 * @author David Maurício Ferreira, nº 202300443
 * @author José Luís Parreira de Oliveira, nº 202300558
 * @version 29/01/2024
 */
public class Speaker
{
    /** The speaker's name */
    private String name;
    /** The speaker's list of lectures */
    private HashSet<Lecture> lectureList;
    /**
     * The speaker's popularity
     * @see Event.PopularityLevel
     */
    private Event.PopularityLevel popularity;
    /**
     * Constructor to create a new speaker
     * @param name String the speaker's name
     */
    public Speaker(String name)
    {
        //keyword "voltar" is reserved for the console
        if(name != null && !name.equalsIgnoreCase("voltar")){
            this.name = name;
            lectureList = new HashSet<Lecture>();
            popularity = Event.PopularityLevel.UNKNOWN;
        }
    }
    /**
     * Constructor to create a speaker with existing data
     * @param name String the speaker's name
     * @param popularity Event.PopularityLevel the speaker's popularity
     */
    public Speaker(String name, Event.PopularityLevel popularity)
    {
        if(name != null && !name.equalsIgnoreCase("voltar") && popularity != null){
            this.name = name;
            lectureList = new HashSet<Lecture>();
            this.popularity = popularity;
        }
    }
    // Selectors
    /**
     * Returns the speaker's name
     * @return String the speaker's name
     */
    public String getName(){return name;}
    /**
     * Returns the speaker's list of lectures
     * @return HashSet&lt;Lecture&gt; the speaker's list of lectures
     */
    public HashSet<Lecture> getLectureList(){return lectureList;}
    /**
     * Returns the speaker's number of lectures
     * @return int the speaker's number of lectures
     */
    public int getNumLectures(){return lectureList.size();}
    /**
     * Returns the speaker's popularity
     * @return Event.PopularityLevel the speaker's popularity (enum type)
     * @see Event.PopularityLevel
     */
    public Event.PopularityLevel getPopularity(){return popularity;}
    // Modifiers
    /**
     * Change the speaker's name
     * @param name String the speaker's new name
     */
    public void setName(String name){
        if(name != null && !name.equalsIgnoreCase("voltar")){
            this.name = name;
        }
    }
    // Other methods
    /**
     * Add a lecture to this speaker. Only adds if the speaker is in lecture's speakers list.
     * @param lecture Lecture the lecture to add to the speaker
     * @see Lecture#hasSpeaker(Speaker speaker)
     */
    public void addLecture (Lecture lecture){
        if(lecture.hasSpeaker(this)){
            lectureList.add(lecture);
        }
    }
    /**
     * Prints to the console the speaker's characteristics
     * @see Lecture#getTitle
     * @see Event.PopularityLevel#toString
     */
    public void show(){
        System.out.println();
        System.out.println("         Nome: " + name);
        System.out.println("         Palestras (" + lectureList.size() + "): ");
        for(Lecture lecture : lectureList){
            System.out.println("            " + lecture.getTitle());
        }
        System.out.println("         Popularidade: " + popularity.toString());
    }
    /**
     * Calculate the speaker's popularity based on the lectures simulated. Calculates the occupancy rate of the lecture (participants per capacity) which is used by another method to return the popularity. This popularity is assigned to the speaker.
     * @return boolean true if speaker's popularity changed
     * @see Lecture#getNumRegistrationsLecture
     * @see Lecture#getRoom
     * @see Room#getCapacity
     * @see #popularity
     * @see Event#calculatePopularity(double occupancyRate)
     * 
     */
    public boolean calculateSpeakerPopularity(){
        if(lectureList != null){
            int sumParticipants = 0;
            int sumCapacity = 0;
            for(Lecture lecture : lectureList){
                if(lecture.getNumRegistrationsLecture() != -1){
                    sumParticipants += lecture.getNumRegistrationsLecture();
                    sumCapacity += lecture.getRoom().getCapacity();
                }
            }
            if(sumCapacity != 0){
                double sumParticipantsDouble = (double) sumParticipants;
                double sumCapacityDouble = (double) sumCapacity;
                double occupancyRate = (sumParticipantsDouble / sumCapacityDouble);
                Event.PopularityLevel tempPopularity = Event.calculatePopularity(occupancyRate);
                if(popularity != tempPopularity){
                    popularity = tempPopularity;
                    return true;
                }
            }
        }
        return false;
    }
    /**
     * Returns true if the speaker is in other lectures ocorring at the same date and time as the one given
     * @param lecture Lecture the lecture to compare to the existing lectures in this speaker's lectures list in terms of time set
     * @return boolean true if the speaker is in other lectures ocorring at the same date and time as the one provided as an parameter
     * @see Lecture#getEndDateTime
     * @see Lecture#getStartDateTime
     */
    public boolean hasIntersectingLectures(Lecture lecture){
        if(lecture !=null && lectureList !=null){
            for(Lecture lectureToCompare : lectureList){
                if(lecture.getEndDateTime().isAfter(lectureToCompare.getStartDateTime()) && lecture.getStartDateTime().isBefore(lectureToCompare.getEndDateTime())){
                    return true;
                }
            }
        }
        return false;
    }
}