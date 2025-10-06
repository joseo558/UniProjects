/**
 * Static methods to manage events (advanced options). Manager class only adds, finds or removes.
 *
 * @author David Maurício Ferreira, nº 202300443
 * @author José Luís Parreira de Oliveira, nº 202300558
 * @version 29/01/2024
 */
public class ManageEvents
{
    // Event
    /**
     * Create a new event with the name provided
     * @param manager Manager the reference to the manager
     * @param name String the name for the new event
     * @see Manager#hasEvent(String nameInput)
     * @see Event
     * @see Manager#addEvent(Event event)
     */
    public static void createEvent(Manager manager, String name){
        if(manager != null && name != null){
            if(manager.hasEvent(name)){
                System.out.println("Já existe um evento com o nome '" + name + "'. Não foi possível criar.");
            }else{
                // Add
                Event event = new Event(name);
                manager.addEvent(event);
                System.out.println("Foi criado o evento " + name + ".");
            }
        }
    }
    /**
     * Change the event's name to the one provided
     * @param manager Manager the reference to the manager
     * @param event Event the event whose name is changing
     * @param newName String the new name for the event
     * @see Manager#hasEvent(String nameInput)
     * @see Event#setName(String name)
     */
    public static void editEventName(Manager manager, Event event, String newName){
        if(event != null && newName != null){
            if(manager.hasEvent(newName)){
                System.out.print("Existe um evento com este nome.");
            }else{
                event.setName(newName);
                System.out.println("Nome do evento alterado para " + newName + ".");
            }
        }
    }
    // Editions in the event
    /**
     * Create a new edition and add it to the event
     * @param event Event the event to which to add the edition
     * @param place Place the place for the edition
     * @see Edition
     * @see Event#getNumEditions
     * @see Event#addEdition(Event event)
     */
    public static void createEdition(Event event, Place place){  
        if(event != null && place != null){
            Edition edition = new Edition(place, event.getNumEditions());
            event.addEdition(edition);
        }
    }
    /**
     * Find an edition in the list of editions of the events
     * @see Edition
     * @see Dates#getNumber(String input)
     * @see Event#getEdition(int editionNum)
     */
    public static Edition findEdition(Event event, String inputString){
        Edition edition = null;
        if(event != null && inputString != null){
            int editionNum = Dates.getNumber(inputString);
            if(editionNum != -1){
                edition = event.getEdition(editionNum);
            }
        }
        return edition;
    }
    /**
     * Simulate an edition if it's has the conditions to permit it
     * @see Edition#simulateRegistrations
     * @see Edition#isSimulated
     * @see Event#calculateEventPopularity
     * @see Manager#getStatistics
     * @see Statistics#editionStats(Edition edition)
     * @see Statistics#getEditionWithHighestAverage(Event event, double highest)
     * @see Statistics#bestEditionStats(Manager manager)
     */
    public static boolean simulateEdition(Manager manager, Event event, Edition edition){
        boolean simulated = false;
        if(edition != null){
            edition.simulateRegistrations();
            simulated = edition.isSimulated();
            if(simulated){
                event.calculateEventPopularity();
                Statistics stat = manager.getStatistics();
                // Print edition stats
                System.out.println("--- Estatísticas da edição ---");
                stat.editionStats(edition);
                if(edition == stat.getEditionWithHighestAverage(event, stat.getHighestAverage(event))){
                    System.out.println("*Esta é a melhor edição do evento*");
                }else{
                    System.out.println();
                    System.out.println("Estatísticas da melhor edição de todos os eventos:");
                    stat.bestEditionStats(manager);
                }
            }
        }
        return simulated;
    }
}