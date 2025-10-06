import java.time.LocalDateTime;
/**
 * Static methods to manage speakers (advanced options). Manager class only adds, finds or removes.
 *
 * @author David Maurício Ferreira, nº 202300443
 * @author José Luís Parreira de Oliveira, nº 202300558
 * @version 29/01/2024
 */
public class ManageLectures
{
    // Lecture
    /**
     * Change the lecture's start date and time
     * @param lecture Lecture the edition to edit
     * @param yearInput String the text with the year
     * @param monthInput String the text with the month
     * @param dayInput String the text with the day
     * @param hoursInput String the text with the hours
     * @param minutesInput String the text with the minutes
     * @see Dates#validateDateTime(String yearInput, String monthInput, String dayInput, String hoursInput, String minutes)
     * @see Lecture#getStartDateTime
     * @see Lecture#setStartDateTime(LocalDateTime date)
     * @see Dates
     * @see Lecture#isSimulated
     * @see Lecture#getSpeakersList
     * @see Edition#hasIntersectingLectures(Lecture lecture)
     * @see Edition#isInEditionDate(Lecture lecture)
     * @see Speaker#hasIntersectingLectures(Lecture lecture)
     */
    public static void setStartDateTime(Edition edition, Lecture lecture, String yearInput, String monthInput, String dayInput, String hoursInput, String minutesInput){
        LocalDateTime newStartDateTime = Dates.validateDateTime(yearInput, monthInput, dayInput, hoursInput, minutesInput);
        if(newStartDateTime != null){
            LocalDateTime oldStartDateTime = lecture.getStartDateTime();
            lecture.setStartDateTime(newStartDateTime);
            if(!lecture.isSimulated()){
                if(!edition.hasIntersectingLectures(lecture) && edition.isInEditionDate(lecture)){
                    boolean incompatibleSpeaker = false;
                    for(Speaker speaker : lecture.getSpeakersList()){
                        if(speaker.hasIntersectingLectures(lecture)){
                            incompatibleSpeaker = true;
                            break;
                        }
                    }
                    if(incompatibleSpeaker){
                        System.out.println("Há palestrantes com palestras agendadas que intersectam com a nova data. Não é possível mudar.");
                        lecture.setStartDateTime(oldStartDateTime);
                    }else{
                        System.out.println("Data de início alterada.");
                    }
                }else{
                    System.out.println("A nova data é incompatível com a data da edição e/ou as suas palestras actuais.");
                    lecture.setStartDateTime(oldStartDateTime);
                }
            }
        }else{
            if(Dates.validYear(yearInput) == -1){
                System.out.println("Ano não válido.");
            }else if(Dates.validMonth(monthInput) == -1){
                System.out.println("Mês não válido.");
            }else if(Dates.validDay(dayInput) == -1){
                System.out.println("Dia não válido.");
            }else if(Dates.validHours(hoursInput) == -1){
                System.out.println("Hora não válida.");
            }else if(Dates.validMinutes(minutesInput) == -1){
                System.out.println("Minutos não válidos.");
            }else{System.out.println("Data não válida");}
        }
    }
    /**
     * Change the lecture's end date and time
     * @param lecture Lecture the edition to edit
     * @param yearInput String the text with the year
     * @param monthInput String the text with the month
     * @param dayInput String the text with the day
     * @param hoursInput String the text with the hours
     * @param minutesInput String the text with the minutes
     * @see Dates#validateDateTime(String yearInput, String monthInput, String dayInput, String hoursInput, String minutes)
     * @see Lecture#getEndDateTime
     * @see Lecture#setEndDateTime(LocalDateTime date)
     * @see Dates
     * @see Lecture#isSimulated
     * @see Lecture#getSpeakersList
     * @see Edition#hasIntersectingLectures(Lecture lecture)
     * @see Edition#isInEditionDate(Lecture lecture)
     * @see Speaker#hasIntersectingLectures(Lecture lecture)
     */
    public static void setEndDateTime(Edition edition, Lecture lecture, String yearInput, String monthInput, String dayInput, String hoursInput, String minutesInput){
        LocalDateTime newEndDateTime = Dates.validateDateTime(yearInput, monthInput, dayInput, hoursInput, minutesInput);
        if(newEndDateTime != null){
            LocalDateTime oldEndDateTime = lecture.getEndDateTime();
            lecture.setEndDateTime(newEndDateTime);
            if(!lecture.isSimulated()){
                if(!edition.hasIntersectingLectures(lecture) && edition.isInEditionDate(lecture)){
                    boolean incompatibleSpeaker = false;
                    for(Speaker speaker : lecture.getSpeakersList()){
                        if(speaker.hasIntersectingLectures(lecture)){
                            incompatibleSpeaker = true;
                            break;
                        }
                    }
                    if(incompatibleSpeaker){
                        System.out.println("Há palestrantes com palestras agendadas que intersectam com a nova data. Não é possível mudar.");
                        lecture.setEndDateTime(oldEndDateTime);
                    }else{
                        System.out.println("Data de fim alterada.");
                    }
                }else{
                    System.out.println("A nova data é incompatível com a data da edição e/ou as suas palestras actuais.");
                    lecture.setEndDateTime(oldEndDateTime);
                }
            }
        }else{
            if(Dates.validYear(yearInput) == -1){
                System.out.println("Ano não válido.");
            }else if(Dates.validMonth(monthInput) == -1){
                System.out.println("Mês não válido.");
            }else if(Dates.validDay(dayInput) == -1){
                System.out.println("Dia não válido.");
            }else if(Dates.validHours(hoursInput) == -1){
                System.out.println("Hora não válida.");
            }else if(Dates.validMinutes(minutesInput) == -1){
                System.out.println("Minutos não válidos.");
            }else{System.out.println("Data não válida.");}
        }
    }
}