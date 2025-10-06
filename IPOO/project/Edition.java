import java.util.HashSet;
import java.time.LocalDate;
/**
 * Stores the characteristics of an event edition.
 *
 * @author David Maurício Ferreira, nº 202300443
 * @author José Luís Parreira de Oliveira, nº 202300558
 * @version 29/01/2024
 */
public class Edition
{
    /** The edition number */
    private int editionNumber;
    /** The edition's start date */
    private LocalDate startDate;
    /** The edition's end date */
    private LocalDate endDate;
    /** The edition place */
    private Place place;
    /** The edition list of lectures */
    private HashSet<Lecture> lectureList;
    /** The number of registrations in the edition */
    private int numRegistrationsEdition;
    /** Average of participants per lecture */
    double averageParticipantsPerLecture;
    /**
     * Constructor to create a new event edition
     * @param place Place the place of the edition
     * @param totalEditions int the event's total number of editions
     */
    public Edition(Place place, int totalEditions){
        if(place != null){
            editionNumber = totalEditions+1;
            startDate = LocalDate.now();
            endDate = null;
            this.place = place;
            lectureList = new HashSet<Lecture>();
            numRegistrationsEdition = -1;
            averageParticipantsPerLecture = 0.0;
        }
    }
    /**
     * Constructor to create an edition with existing data
     * @param place Place the place of the edition
     * @param totalEditions int the event's total number of editions
     * @param startDate LocalDate the start date of the edition
     * @param endDate LocalDate the end date of the edition
     * @param lectureList HashSet&lt;Lecture&gt; the list of lectures in this edition
     */
    public Edition(Place place, int totalEditions, LocalDate startDate, LocalDate endDate, HashSet<Lecture> lectureList){
        if(place != null && startDate !=null && endDate !=null && lectureList !=null){
            editionNumber = totalEditions+1;
            this.startDate = startDate;
            this.endDate = endDate;
            this.place = place;
            this.lectureList = lectureList;
            numRegistrationsEdition = -1;
            averageParticipantsPerLecture = 0.0;
        }
    }
    // Selectors
    /**
     * Returns the edition number
     * @return int the edition number
     */
    public int getEditionNumber(){return editionNumber;}
    /**
     * Returns the start date of the edition
     * @return LocalDate the start date of the edition
     */
    public LocalDate getStartDate(){return startDate;}
    /**
     * Returns the end date of the edition
     * @return LocalDate the end date of the edition
     */
    public LocalDate getEndDate(){return endDate;}
    /**
     * Returns the edition's place
     * @return Place the edition's place
     */
    public Place getPlace(){return place;}
    /**
     * Returns the list of lectures in this edition
     * @return HashSet&lt;Lecture&gt; the list of lectures in this edition
     */
    public HashSet<Lecture> getLectureList(){return lectureList;}
    /**
     * Returns the number of lectures in this edition
     * @return int the number of lectures in this edition
     */
    public int getNumLectures(){return lectureList.size();}
    /**
     * Returns the number of registrations on this edition
     * @return int the number of registrations on this edition. Returns -1 if not yet simulated.
     */
    public int getNumRegistrationsEdition(){return numRegistrationsEdition;}
    /**
     * Returns the number of unique speakers in all this edition's lectures
     * @return int the number of unique speakers in all this edition's lectures
     * @see Lecture#getSpeakersList
     */
    public int getNumSpeakers(){
        int count = 0;
        HashSet<Speaker> speakersInEdition = new HashSet<>();
        if(lectureList != null){
            for(Lecture lect : lectureList){
                if(lect.getSpeakersList() != null){
                    for(Speaker s : lect.getSpeakersList()){
                        speakersInEdition.add(s);
                    }
                }
            }
        }
        return count=speakersInEdition.size();
    }
    /**
     * Returns the edition's average of participants per lecture
     * @return double the edition's average of participants per lecture
     */
    public double getAverageParticipantsPerLecture(){
        return averageParticipantsPerLecture;
    }
    // Modifiers
    /**
     * Change the edition start date. The class {@link Manager} calls this after creating a date with the {@link Dates} class.
     * @param date LocalDate the edition's new start date
     */
    public void setStartDate(LocalDate date){
        if(date != null){startDate = date;}
    }
    /**
     * Change the edition end date. The class {@link Manager} calls this after creating a date with the {@link Dates} class.
     * @param date LocalDate the edition's new end date
     */
    public void setEndDate(LocalDate date){
        if(date != null){endDate = date;}
    }
    /**
     * Change the edition's place
     * @param place Place the edition's new place
     */
    public void setPlace(Place place){
        if(place != null){this.place = place;}
    }
    /**
     * Set the edition's average of participants per lecture
     * @param average the edition's average of participants per lecture
     * @see Statistics#editionStats(Manager manager, Event event, Edition edition)
     */
    public void setAverageParticipantsPerLecture(double average){
        if(average >= 0){
            averageParticipantsPerLecture = average;
        }
    }
    // Other methods
    /**
     * Add a lecture to this edition. Only adds if theres no other lecture ocorring at the same room and time.
     * @param lecture Lecture the lecture to add to the edition
     * @see #findIntersectingLectures(Lecture lecture)
     */
    public void addLecture(Lecture lecture){
        if(lecture != null){
            if(!hasIntersectingLectures(lecture)){
                lectureList.add(lecture);
            }
        }
    }
    /**
     * Remove an lecture from the edition if it's in the edition's lectures list
     * @param lecture Lecture the lecture to remove from the edition's lectures list
     * @see #hasLecture(Lecture lecture)
     */
    public void removeLecture(Lecture lecture){
        if(lecture != null && hasLecture(lecture)){
            lectureList.remove(lecture);
        }
    }
    /**
     * Checks if a lecture exists in the edition's lectures list
     * @param lecture Lecture the lecture to check if it's in the edition's lectures list
     * @return boolean true if the lecture exists in the edition's lectures list
     */
    public boolean hasLecture(Lecture lecture){
        boolean isFound = false;
        if(lecture != null){
            isFound = lectureList.contains(lecture);
        }
        return isFound;
    }
    /**
     * Checks if a lecture exists in the edition's lectures list
     * @param titleInput String the title to compare to the titles of the existing lectures in this edition
     * @return boolean true if the lecture exists in the edition's lectures list
     */
    public boolean hasLecture(String titleInput){
        boolean isFound = false;
        if(titleInput != null){
            for(Lecture lecture : lectureList){
                if(titleInput.equalsIgnoreCase(lecture.getTitle())){
                    isFound = true;
                    break;
                }
            }
        }
        return isFound;
    }
    /**
     * Returns the lecture with this title in this edition
     * @param titleInput String the title to compare to the titles of the existing lectures in this edition
     * @return Lecture the lecture with this title in this edition or a null Lecture if not found
     */
    public Lecture getLecture(String titleInput){
        Lecture lectureFound = null;
        if(titleInput != null){
            for(Lecture lecture : lectureList){
                if(titleInput.equalsIgnoreCase(lecture.getTitle())){
                    lectureFound = lecture;
                    break;
                }
            }
        }
        return lectureFound;
    }
    /**
     * Returns true if there are other lectures ocorring in the same room and time as the one given
     * @param lecture Lecture the lecture to compare to the existing lectures in this edition in terms of room and time set
     * @return boolean true if there are other lectures ocorring in the same room and time as the one provided as an parameter
     */
    public boolean hasIntersectingLectures(Lecture lecture){
        if(lecture != null && lectureList !=null){
            for(Lecture lectureToCompare : lectureList){
                if(lecture.getRoom() == lectureToCompare.getRoom() && (lecture.getEndDateTime().isAfter(lectureToCompare.getStartDateTime()) && lecture.getStartDateTime().isBefore(lectureToCompare.getEndDateTime()))){
                    return true;
                }
            }
        }
        return false;
    }
    /**
     * Returns true if the lecture's date is in the edition's date
     * @param lecture Lecture the lecture to compare to edition's date
     * @return boolean true if the lecture's date is in the edition's date
     */
    public boolean isInEditionDate(Lecture lecture){
        if(lecture != null){
            if(lecture.getEndDateTime().isAfter(startDate.atStartOfDay()) && lecture.getStartDateTime().isBefore(endDate.atStartOfDay())){
                return true;
            }
        }
        return false;
    }
    /**
     * Returns a list of lectures ocurring at the same time as the one given
     * @param lecture Lecture the lecture to compare to the existing lectures in this edition in terms of time set
     * @return HashSet&lt;Lecture&gt; a list of lectures ocurring at the same time as the one provided as an parameter
     */
    public HashSet<Lecture> findIntersectingLectures(Lecture lecture){
        HashSet<Lecture> intersectingLectures = new HashSet<>();
        if(lectureList !=null){
            for(Lecture lectureToCompare : lectureList){
                if(lecture.getEndDateTime().isAfter(lectureToCompare.getStartDateTime()) && lecture.getStartDateTime().isBefore(lectureToCompare.getEndDateTime())){
                    intersectingLectures.add(lectureToCompare);
                }
            }
        }
        return intersectingLectures;
    }
    /**
     * Prints to the console the edition's characteristics
     */
    public void show(){
        System.out.println();
        System.out.println("   Número da edição: " + editionNumber);
        System.out.println("   Data de início: " + Dates.toStringDate(startDate));
        String endDatePrint;
        if(endDate == null){
            endDatePrint = "A decorrer.";
        }else{endDatePrint = Dates.toStringDate(endDate);}
        System.out.println("   Data de fim: " + endDatePrint);
        System.out.println("   Local: " + place.getName());
        System.out.println("   Palestras (" + getNumLectures() + "): ");
        for(Lecture lecture : lectureList){
            lecture.show();
        }
    }
    /**
     * Returns true if the edition is already simulated
     * @return boolean true if the edition is already simulated (has registrations)
     */
    public boolean isSimulated(){
        if(numRegistrationsEdition == -1){return false;}else{return true;}
    }
    /**
     * Returns true if all the lectures in the edition have been simulated
     * @return boolean true if all the lectures in the edition have been simulated
     */
    public boolean isAllLecturesSimulated(){
        boolean isAll = true;
        for(Lecture lecture : lectureList){
            if(lecture.isSimulated() == false){
                isAll = false;
                break;
            }
        }
        return isAll;
    }
    /**
     * Simulate registrations in the edition if all lectures have been simulated.
     * @see #isAllLecturesSimulated
     * @see Lecture#getNumRegistrationsLecture()
     */
    public void simulateRegistrations(){
        if(isAllLecturesSimulated()){
            double contribution = 1;
            double numRegistrationsEditionDouble = 0.0;
            for(Lecture lecture: lectureList){
                if(contribution == 0.0){break;}
                numRegistrationsEditionDouble += lecture.getNumRegistrationsLecture() * contribution;
                contribution = contribution - 0.10;
            }
            numRegistrationsEdition = (int) numRegistrationsEditionDouble;
        }
    }
}