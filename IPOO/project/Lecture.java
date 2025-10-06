import java.util.HashSet;
import java.time.LocalDateTime;
/**
 * Stores the characteristics of a lecture.
 *
 * @author David Maurício Ferreira, nº 202300443
 * @author José Luís Parreira de Oliveira, nº 202300558
 * @version 29/01/2024
 */
public class Lecture
{
    /** The lecture's title */
    private String title;
    /** The list of speakers for this lecture */
    private HashSet<Speaker> speakersList;
    /** The lecture's start date and time */
    private LocalDateTime startDateTime;
    /** The lecture's end date and time */
    private LocalDateTime endDateTime;
    /** The lecture's room */
    private Room room;
    /** The number of registrations in the lecture */
    private int numRegistrationsLecture;
    /**
     * Constructor to create a new lecture
     * @param title String the title of the lecture
     */
    public Lecture(String title)
    {
        //keyword "voltar" is reserved for the console
        if(title != null && !title.equalsIgnoreCase("voltar")){
            this.title = title;
            speakersList = new HashSet<Speaker>();
            startDateTime = LocalDateTime.now();
            endDateTime = null;
            room = null;
            numRegistrationsLecture = -1;
        }
    }
    /**
     * Constructor to create a lecture with existing data
     * @param title String the title of the lecture
     * @param startDateTime LocalDateTime the lecture's start date and time
     * @param endDateTime LocalDateTime the lecture's end date and time
     * @param room Room the lecture's room
     */
    public Lecture(String title, LocalDateTime startDateTime, LocalDateTime endDateTime, Room room)
    {
        if(title != null && !title.equalsIgnoreCase("voltar") && startDateTime != null && endDateTime != null && room != null){
            this.title = title;
            speakersList = new HashSet<Speaker>();
            this.startDateTime = startDateTime;
            this.endDateTime = endDateTime;
            this.room = room;
            numRegistrationsLecture = -1;
        }
    }
    // Selectors
    /**
     * Returns the lecture's title
     * @return String the lecture's title
     */
    public String getTitle(){return title;}
    /**
     * Returns the list of speakers for this lecture
     * @return HashSet&lt;Speaker&gr; the list of speakers for this lecture
     */
    public HashSet<Speaker> getSpeakersList(){return speakersList;}
    /**
     * Returns the number of speakers in this lecture
     * @return int the number of speakers in this lecture
     */
    public int getNumSpeakers(){return speakersList.size();}
    /**
     * Returns the lecture's start date and time
     * @return LocalDateTime the lecture's start date and time
     */
    public LocalDateTime getStartDateTime(){return startDateTime;}
    /**
     * Returns the lecture's end date and time
     * @return LocalDateTime the lecture's end date and time
     */
    public LocalDateTime getEndDateTime(){return endDateTime;}
    /**
     * Returns the lecture's room
     * @return Room the lecture's room
     */
    public Room getRoom(){return room;}
    /**
     * Returns the number of registrations on this edition
     * @return int the number of registrations on this edition. Returns -1 if not yet simulated.
     */
    public int getNumRegistrationsLecture(){return numRegistrationsLecture;}
    // Modifiers
    /**
     * Change the lecture's start date and time. The class {@link Manager} calls this after creating a date with the {@link Dates} class.
     * @param date LocalDateTime the lecture's new start date and time
     * @see #isSimulated
     */
    public void setStartDateTime(LocalDateTime dateTime){
        if(dateTime != null){
            if(!isSimulated()){
               startDateTime = dateTime; 
            }else{System.out.println("Não é possível reagender uma palestra já simulada.");}
        }
    }
    /**
     * Change the lecture's end date and time. The class {@link Manager} calls this after creating a date with the {@link Dates} class.
     * @param date LocalDateTime the lecture's new end date and time
     * @see #isSimulated
     */
    public void setEndDateTime(LocalDateTime dateTime){
        if(dateTime != null){
            if(!isSimulated()){
                endDateTime = dateTime;
            }else{System.out.println("Não é possível reagender uma palestra já simulada.");}
        }
    }
    /**
     * Change the lecture's room
     * @param room Room the lecture's new room
     */
    public void setRoom(Room room){
        if(room != null){this.room = room;}
    }
    /**
     * Change the lecture's title
     * @param title String the lecture's new title
     */
    public void setTitle(String title){
        if(title != null && !title.equalsIgnoreCase("voltar")){this.title = title;}
    }
    // Other
    /**
     * Add a speaker to this lecture. Only adds if the speaker is not in another lecture ocorring at the same time.
     * @param speaker Speaker the speaker to add to the lecture
     * @see Speaker#hasIntersectingLectures(Lecture lecture)
     * @see #hasSpeaker(Speaker speaker)
     * @see Speaker#addLecture(Lecture lecture)
     */
    public void addSpeaker(Speaker speaker){
        if(speaker != null){
            //Check if this lectures intersects any already added to speaker
            if(speaker.hasIntersectingLectures(this)){
                // hasIntersectingLectures
                System.out.println("O palestrante já tem uma palestra no mesmo intervalo de tempo que esta.");
            }else{
                // Check if already added
                if(hasSpeaker(speaker)){
                    System.out.println("O palestrante já se encontra inscrito.");
                }else{
                    // Add speaker to lecture
                    speakersList.add(speaker);
                    System.out.println("Palestrante adicionado.");
                    // Add lecture to speaker
                    speaker.addLecture(this);
                }
            }
        }
    }
    /**
     * Returns true if the speaker is in the lecture's speakers list
     * @param speaker Speaker the speaker to find in the lecture's speakers list
     * @return boolean true if the speaker is in the lecture's speakers list
     */
    public boolean hasSpeaker(Speaker speaker){
        boolean isFound = false;
        if(speaker != null && speakersList != null){
            isFound = speakersList.contains(speaker);
        }
        return isFound;
    }
    /**
     * Checks if a speaker exists in the lectures's speaker list
     * @param nameInput String the name to compare to the names of the existing speakers in this lecture
     * @return boolean true if the speaker exists in the lectures's speaker list
     * @see Speaker#getName
     */
    public boolean hasSpeaker(String nameInput){
        boolean isFound = false;
        if(nameInput != null){
            for(Speaker speaker : speakersList){
                if(nameInput.equalsIgnoreCase(speaker.getName())){
                    isFound = true;
                    break;
                }
            }
        }
        return isFound;
    }
    /**
     * Returns the speaker with this name in this lecture
     * @param nameInput String the name to compare to the names of the existing speakers in this lecture
     * @return Speaker the speaker with this name in this lecture or a null Speaker if not found
     * @see Speaker#getName
     */
    public Speaker getSpeaker(String nameInput){
        Speaker speakerFound = null;
        if(nameInput != null){
            for(Speaker speaker : speakersList){
                if(nameInput.equalsIgnoreCase(speaker.getName())){
                    speakerFound = speaker;
                    break;
                }
            }
        }
        return speakerFound;
    }
    /**
     * Remove a speaker from the lecture if it's in the lecture's speakers list
     * @param speaker Speaker the speaker to remove from the lecture's speakers list
     * @see #hasSpeaker(Speaker speaker)
     */
    public void removeSpeaker(Speaker speaker){
        if(speaker != null && hasSpeaker(speaker)){
            speakersList.remove(speaker);
            System.out.println("Palestrante removido.");
        }
    }
    /**
     * Prints to the console the lecture's characteristics
     * @see Speaker#getName
     * @see Dates#toStringDateTime(LocalDateTime dateTime)
     * @see Room#getName
     */
    public void show(){
        System.out.println();
        System.out.println("      Título da palestra : " + title);
        System.out.println("      Palestrantes (" + speakersList.size() + ") :");
        for(Speaker speaker : speakersList){
            System.out.println("         " + speaker.getName());
        }
        System.out.println("      Data de início: " + Dates.toStringDateTime(startDateTime));
        String endDateTimePrint;
        if(endDateTime == null){
            endDateTimePrint = "A decorrer.";
        }else{
            endDateTimePrint = Dates.toStringDateTime(endDateTime);
        }
        System.out.println("      Data de fim: " + endDateTimePrint);
        String roomName;
        if(room != null){roomName = room.getName();}else{roomName = "Não atribuida.";}
        System.out.println("      Sala: " + roomName);
    }
    /**
     * Returns true if the lecture is already simulated
     * @return boolean true if the lecture is already simulated (has registrations)
     */
    public boolean isSimulated(){
        if(numRegistrationsLecture == -1){return false;}else{return true;}
    }
    /**
     * Returns true if the lecture has the conditions to be simulated (has speakers and a room)
     * @return boolean true if the lecture has the conditions to be simulated (has speakers and a room)
     */
    public boolean isPossibleToSimulate(){
        if(speakersList.size() > 0 && room != null){return true;}
        return false;
    }
    /**
     * Finds the speaker in the lecture with the highest popularity and returns the weight associated with that popularity for the calculations in the simulations
     * @param lecture Lecture the lecture in which to search for the speaker with the highest popularity
     * @return double the weight associated with the highest popularity among the lecture's speakers
     * @see Event.PopularityLevel#weight
     * @see #getSpeakersList
     * @see Speaker#getPopularity
     */
    public double highestSpeakerPopularity(Lecture lecture){
        double weight = 0.0;
        double temp = 0.0;
        if(lecture != null){
            for(Speaker speaker : lecture.getSpeakersList()){
                temp = speaker.getPopularity().getWeight();
                if(temp > weight){weight = temp;}
            }
        }
        return weight;
    }
    /**
     * Simulate registrations in the lecture if the lecture has the conditions to be simulated.
     * @see #isPossibleToSimulate
     * @see Event#getPopularity
     * @see Event.PopularityLevel
     * @see #highestSpeakerPopularity(Lecture lecture)
     * @see Edition#hasIntersectingLectures(Lecture lecture)
     * @see Edition#findIntersectingLectures(Lecture lecture)
     */
    public void simulateRegistrations(Event event, Edition edition){
        if(isPossibleToSimulate()){
            Event.PopularityLevel eventPopularity = event.getPopularity();
            double baseParticipants = 100.0;
            double weight = highestSpeakerPopularity(this);
            if(edition.hasIntersectingLectures(this) == true){
                // There's intersecting lectures
                HashSet<Lecture> intersectingLecturesList = edition.findIntersectingLectures(this);
                double weightIntersect = 0.0;
                double temp = 0.0;
                for(Lecture lecture : intersectingLecturesList){
                    temp = highestSpeakerPopularity(lecture);
                    if(temp > weightIntersect){
                        weightIntersect = temp;
                    }
                }
                if(weightIntersect >= 1.5){
                    double numRegistrationsLecture2 = (baseParticipants * weight * eventPopularity.getWeight())* 0.75;
                    numRegistrationsLecture = (int) numRegistrationsLecture2;
                }
                else{
                    double numRegistrationsLecture2 = baseParticipants * weight * eventPopularity.getWeight();
                    numRegistrationsLecture = (int) numRegistrationsLecture2;
                }
            }else{
                // No intersecting lectures
                double numRegistrationsLecture2 = baseParticipants * weight * eventPopularity.getWeight();
                numRegistrationsLecture = (int) numRegistrationsLecture2;
            }
        }
    }
}