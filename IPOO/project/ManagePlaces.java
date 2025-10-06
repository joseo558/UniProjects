/**
 * Static methods to manage places (advanced options). Manager class only adds, finds or removes.
 *
 * @author David Maurício Ferreira, nº 202300443
 * @author José Luís Parreira de Oliveira, nº 202300558
 * @version 29/01/2024
 */
public class ManagePlaces
{
    /**
     * Create a new place with the name provided
     * @param manager Manager the reference to the manager
     * @param name String the name for the new place
     * @see Manager#hasPlace(String nameInput)
     * @see Place
     * @see Manager#addPlace(Place place)
     */
    public static void createPlace(Manager manager, String name){
        if(manager != null && name != null){
            if(manager.hasPlace(name)){
                System.out.println("Já existe um local com o nome '" + name + "'. Não foi possível criar.");
            }else{
                // Add
                Place place = new Place(name);
                manager.addPlace(place);
                System.out.println("Foi criado o local " + name + ".");
            }
        }
    }
    /**
     * Change the place's name to the one provided
     * @param manager Manager the reference to the manager
     * @param place Place the place whose name is changing
     * @param newName String the new name for the place
     * @see Manager#hasPlace(String nameInput)
     * @see Place#setName(String name)
     */
    public static void editPlaceName(Manager manager, Place place, String newName){
        if(place != null && newName != null){
            if(manager.hasPlace(newName)){
                System.out.print("Existe um local com este nome.");
            }else{
                place.setName(newName);
                System.out.println("Nome do local alterado para " + newName + ".");
            }
        }
    }
    // Room
    /**
     * Add a room to the place
     * @param place Place the place in which we want to add a room
     * @param name String the name for the room
     * @param capacityString String the capacity of the room
     * @see Dates#getNumber(String input)
     * @see Place#hasRoom(String name)
     * @see Room
     * @see Place#addRoom(Room room)
     */
    public static void createRoom(Place place, String name, String capacityString){
        if(place != null && name != null && capacityString != null){
            int capacity = Dates.getNumber(capacityString);
            if(capacity >0 && !place.hasRoom(name)){
                Room room = new Room(name, capacity);
                place.addRoom(room);
                System.out.println("Sala " + name + " com capacidade de " + capacity + " adicionada ao local.");
            }else{System.out.println("Não foi possível criar a sala. Capacidade tem de ser maior que zero e o nome único no local.");}
        }
    }
    /**
     * Change a room's name and capacity
     * @param place Place the place with the room
     * @param room Room the room we want to change
     * @param newName String the new name for the room
     * @param capacityString String the new capacity of the room
     * @see Dates#getNumber(String input)
     * @see Place#hasRoom(String name)
     * @see Room#setName(String name)
     * @see Room#setCapacity(int capacity)
     */
    public static void editRoom(Place place, Room room, String newName, String capacityString){
        if(room != null && newName != null && capacityString != null){
            int capacity = Dates.getNumber(capacityString);
            if(capacity >0 && !place.hasRoom(newName)){
                room.setName(newName);
                room.setCapacity(capacity);
                System.out.println("Sala editada.");
            }else{System.out.println("Não foi possível criar a sala. Capacidade tem de ser maior que zero e o nome único no local.");}
        }
    }
}