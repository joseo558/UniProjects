import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
/**
 * Provides pre-defined values to test the application
 *
 * @author David Maurício Ferreira, nº 202300443
 * @author José Luís Parreira de Oliveira, nº 202300558
 * @version 29/01/2024
 */
public class TestApp
{
    /**
     * Provides pre-defined values to test the application, only if it has no pre-existing data
     * @param manager Manager the manager object
     * @see Manager
     * @see Room
     * @see Place
     * @see Speaker
     * @see Lecture
     * @see Edition
     * @see Event
     */
    public static void testApplication(Manager manager){
        if(manager != null && manager.hasNoData()){
            // Rooms
            Room roomLisboa1 = new Room("Anfiteatro A", 300);
            Room roomLisboa2 = new Room("Anfiteatro B", 300);
            Room roomLisboa3 = new Room("Sala de conferências", 1000);
            Room roomPorto1 = new Room("Anfiteatro Norte", 400);
            Room roomPorto2 = new Room("Anfiteatro Sul", 400);
            Room roomPorto3 = new Room("Sala nobre", 2000);
            HashSet<Room> roomsLisboa = new HashSet<>();
            roomsLisboa.add(roomLisboa1);
            roomsLisboa.add(roomLisboa2);
            roomsLisboa.add(roomLisboa3);
            HashSet<Room> roomsPorto = new HashSet<>();
            roomsLisboa.add(roomPorto1);
            roomsLisboa.add(roomPorto2);
            roomsLisboa.add(roomPorto3);
            // Places
            Place placeLisboa = new Place("Lisboa", roomsLisboa);
            Place placePorto = new Place("Porto", roomsPorto);
            manager.addPlace(placeLisboa);
            manager.addPlace(placePorto);
            // Speakers
            Speaker speakerLisboa1 = new Speaker("João", Event.PopularityLevel.NEW);
            Speaker speakerLisboa2 = new Speaker("Ana", Event.PopularityLevel.WEAK);
            Speaker speakerLisboa3 = new Speaker("Guilherme", Event.PopularityLevel.UNKNOWN);
            Speaker speakerLisboa4 = new Speaker("Paula", Event.PopularityLevel.NEW);
            Speaker speakerLisboa5 = new Speaker("Anabela", Event.PopularityLevel.BIG);
            Speaker speakerLisboa6 = new Speaker("Francisco", Event.PopularityLevel.TOP);
            Speaker speakerPorto1 = new Speaker("Tiago", Event.PopularityLevel.NEW);
            Speaker speakerPorto2 = new Speaker("Mariana", Event.PopularityLevel.UNKNOWN);
            Speaker speakerPorto3 = new Speaker("Mário", Event.PopularityLevel.TOP);
            Speaker speakerPorto4 = new Speaker("Filipa", Event.PopularityLevel.NEW);
            Speaker speakerPorto5 = new Speaker("Joana", Event.PopularityLevel.BIG);
            Speaker speakerPorto6 = new Speaker("António", Event.PopularityLevel.TOP);
            Speaker[] allSpeakers = {speakerLisboa1, speakerLisboa2, speakerLisboa3, speakerLisboa4, speakerLisboa5, speakerLisboa6, speakerPorto1, speakerPorto2, speakerPorto3, speakerPorto4, speakerPorto5, speakerPorto6};
            for (Speaker speaker : allSpeakers){
                manager.addSpeaker(speaker);
            }
            // Lectures
            LocalDateTime dateTime1 = LocalDateTime.of(2023, 4, 1, 9, 0); // 1 Abril 9h
            LocalDateTime dateTime1end = LocalDateTime.of(2023, 4, 1, 11, 0); // 1 Abril 11h
            LocalDateTime dateTime2 = LocalDateTime.of(2023, 4, 1, 10, 30); // 1 Abril 10h30, date intersects
            LocalDateTime dateTime2end = LocalDateTime.of(2023, 4, 1, 13, 30); // 1 Abril 13h30
            LocalDateTime dateTime3 = LocalDateTime.of(2023, 4, 1, 15, 0); // 1 Abril 15h
            LocalDateTime dateTime3end = LocalDateTime.of(2023, 4, 1, 18, 30); // 1 Abril 18h30 fim
            LocalDateTime dateTime4 = LocalDateTime.of(2023, 9, 8, 9, 0); // 7 Setembro 9h
            LocalDateTime dateTime4end = LocalDateTime.of(2023, 9, 8, 13, 0); // 7 Setembro 13h
            LocalDateTime dateTime5 = LocalDateTime.of(2023, 9, 8, 10, 30); // 8 Setembro 10h30
            LocalDateTime dateTime5end = LocalDateTime.of(2023, 9, 8, 13, 30); // 8 Setembro 13h30
            LocalDateTime dateTime6 = LocalDateTime.of(2023, 9, 8, 15, 0); // 8 Setembro 15h
            LocalDateTime dateTime6end = LocalDateTime.of(2023, 9, 8, 18, 30); // 8 Setembro 18h30 fim
            Lecture lectureLisboa1 = new Lecture("Programação em Java", dateTime1, dateTime1end, roomLisboa1);
            Lecture lectureLisboa2 = new Lecture("Programação em C++", dateTime2, dateTime2end, roomLisboa2);
            Lecture lectureLisboa3 = new Lecture("Programação em Go", dateTime3, dateTime3end, roomLisboa3);
            Lecture lecturePorto1 = new Lecture("Programação em C#", dateTime4, dateTime4end, roomPorto1);
            Lecture lecturePorto2 = new Lecture("Programação em JavaScript", dateTime5, dateTime5end, roomPorto2);
            Lecture lecturePorto3 = new Lecture("Programação em Python", dateTime6, dateTime6end, roomPorto3);
            // Add speakers to lectures and lectures to speakers
            lectureLisboa1.addSpeaker(speakerLisboa1);
            lectureLisboa1.addSpeaker(speakerLisboa2);
            lectureLisboa2.addSpeaker(speakerLisboa3);
            lectureLisboa2.addSpeaker(speakerLisboa4);
            lectureLisboa3.addSpeaker(speakerLisboa5);
            lectureLisboa3.addSpeaker(speakerLisboa6);
            lecturePorto1.addSpeaker(speakerPorto1);
            lecturePorto1.addSpeaker(speakerPorto2);
            lecturePorto2.addSpeaker(speakerPorto3);
            lecturePorto2.addSpeaker(speakerPorto4);
            lecturePorto3.addSpeaker(speakerPorto5);
            lecturePorto3.addSpeaker(speakerPorto6);
            // Editions
            HashSet<Lecture> lectureListLisboa = new HashSet<>();
            lectureListLisboa.add(lectureLisboa1);
            lectureListLisboa.add(lectureLisboa2);
            lectureListLisboa.add(lectureLisboa2);
            HashSet<Lecture> lectureListPorto = new HashSet<>();
            lectureListPorto.add(lecturePorto1);
            lectureListPorto.add(lecturePorto2);
            lectureListPorto.add(lecturePorto3);
            LocalDate date1 = LocalDate.of(2023, 4, 1);
            LocalDate date1end = LocalDate.of(2023, 4, 1);
            LocalDate date2 = LocalDate.of(2023, 9, 7);
            LocalDate date2end = LocalDate.of(2023, 9, 8);
            Edition lisboaEdition = new Edition(placeLisboa, 0, date1, date1end, lectureListLisboa);
            Edition portoEdition = new Edition(placePorto, 1, date2, date2end, lectureListPorto);
            // Event
            HashSet<Edition> allEditions = new HashSet<>();
            allEditions.add(lisboaEdition);
            allEditions.add(portoEdition);
            Event programming = new Event("Programação", Event.PopularityLevel.NEW, allEditions);
            manager.addEvent(programming);
        }
    }
}