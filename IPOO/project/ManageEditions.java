import java.util.HashSet;
import java.time.LocalDate;
/**
 * Static methods to manage speakers (advanced options). Manager class only adds, finds or removes.
 *
 * @author David Maurício Ferreira, nº 202300443
 * @author José Luís Parreira de Oliveira, nº 202300558
 * @version 29/01/2024
 */
public class ManageEditions
{
    // Edition
    /**
     * Create a new lecture with the name provided
     * @param edition Edition the edition to which add the lecture
     * @param titleInput String the title for the new lecture
     * @see Edition#isSimulated
     * @see Edition#hasLecture(String titleInput)
     * @see Lecture
     * @see Edition#addLecture(Lecture lecture)
     */
    public static void createLecture(Edition edition, String titleInput){
        if(edition != null && titleInput != null && !edition.isSimulated()){
            if(edition.hasLecture(titleInput)){
                System.out.println("Já existe uma palestra nesta edição com o nome " + titleInput + ". Não foi possível criar.");
            }else{
                // Add
                Lecture lecture = new Lecture(titleInput);
                edition.addLecture(lecture);
                System.out.println("Foi criada a palestra.");
            }
        }
    }
    /**
     * Change the edition's start date
     * @param edition Edition the edition to edit
     * @param yearInput String the text with the year
     * @param monthInput String the text with the month
     * @param dayInput String the text with the day
     * @see Edition#isSimulated
     * @see Dates#validateDate(String yearInput, String monthInput, String dayInput)
     * @see Edition#setStartDate(LocalDate date)
     * @see Dates
     */
    public static void setStartDate(Edition edition, String yearInput, String monthInput, String dayInput){
        LocalDate newStartDate = Dates.validateDate(yearInput, monthInput, dayInput);
        if(newStartDate != null && !edition.isSimulated()){
            edition.setStartDate(newStartDate);
        }else{
            if(Dates.validYear(yearInput) == -1){
                System.out.println("Ano não válido.");
            }else if(Dates.validMonth(monthInput) == -1){
                System.out.println("Mês não válido.");
            }else if(Dates.validDay(dayInput) == -1){
                System.out.println("Dia não válido.");
            }else{System.out.println("Data não válida.");}
        }
    }
    /**
     * Change the edition's end date
     * @param edition Edition the edition to edit
     * @param yearInput String the text with the year
     * @param monthInput String the text with the month
     * @param dayInput String the text with the day
     * @see Edition#isSimulated
     * @see Dates#validateDate(String yearInput, String monthInput, String dayInput)
     * @see Edition#setEndDate(LocalDate date)
     * @see Dates
     */
    public static void setEndDate(Edition edition, String yearInput, String monthInput, String dayInput){
        LocalDate newEndDate = Dates.validateDate(yearInput, monthInput, dayInput);
        if(newEndDate != null && !edition.isSimulated()){
            edition.setEndDate(newEndDate);
        }else{
            if(Dates.validYear(yearInput) == -1){
                System.out.println("Ano não válido.");
            }else if(Dates.validMonth(monthInput) == -1){
                System.out.println("Mês não válido.");
            }else if(Dates.validDay(dayInput) == -1){
                System.out.println("Dia não válido.");
            }else{System.out.println("Data não válida.");}
        }
    }
    /**
     * Simulate a lecture if it's has the conditions to permit it
     * @param manager Manager the manager
     * @param event Event the event with the lecture
     * @param edition Edition the edition with the lecture
     * @param lecture Lecture the lecture to simulate
     * @see Lecture#simulateRegistrations(Event event, Edition edition)
     * @see Lecture#isSimulated
     * @see Lecture#getSpeakerList
     * @see Event.PopularityLevel
     * @see Manager#getStatistics
     * @see Speaker#getPopularity
     * @see Speaker#calculateSpeakerPopularity
     * @see Statistics#editSpeakerPopularity(Speaker speaker, Event.PopularityLevel oldPopularity)
     * @see Statistics#showSpeakerRanking
     */
    public static boolean simulateLecture(Manager manager, Event event, Edition edition, Lecture lecture){
        boolean simulated = false;
        if(lecture != null){
            lecture.simulateRegistrations(event, edition);
            simulated = lecture.isSimulated();
            if(simulated){
                HashSet<Speaker> speakerList = lecture.getSpeakersList();
                Event.PopularityLevel oldPopularity;
                Statistics stat = manager.getStatistics();
                HashSet<String> topSpeakersChanged = new HashSet<>();
                HashSet<String> bigSpeakersChanged = new HashSet<>();
                HashSet<String> newSpeakersChanged = new HashSet<>();
                HashSet<String> weakSpeakersChanged = new HashSet<>();
                HashSet<String> unknownSpeakersChanged = new HashSet<>();
                String text;
                for(Speaker speaker : speakerList){
                    oldPopularity = speaker.getPopularity();
                    if(speaker.calculateSpeakerPopularity()){
                        text = stat.editSpeakerPopularity(speaker, oldPopularity);
                        switch(oldPopularity){
                            case TOP:
                                topSpeakersChanged.add(text);
                                break;
                            case BIG:
                                bigSpeakersChanged.add(text);
                                break;
                            case NEW:
                                newSpeakersChanged.add(text);
                                break;
                            case WEAK:
                                weakSpeakersChanged.add(text);
                                break;
                            case UNKNOWN:
                            default:
                                unknownSpeakersChanged.add(text);
                        }
                    }
                }
                // Ranking
                stat.showSpeakerRanking();
                // Ranking changes
                System.out.println();
                System.out.println("--- Mudanças no ranking de plaestrantes ---");
                System.out.println("*** Topo ***");
                for(String s : topSpeakersChanged){System.out.println(s);}
                System.out.println("*** Big ***");
                for(String s : bigSpeakersChanged){System.out.println(s);}
                System.out.println("*** New ***");
                for(String s : newSpeakersChanged){System.out.println(s);}
                System.out.println("*** Weak ***");
                for(String s : weakSpeakersChanged){System.out.println(s);}
                System.out.println("*** Unknown ***");
                for(String s : unknownSpeakersChanged){System.out.println(s);}
            }
        }
        return simulated;
    }
}