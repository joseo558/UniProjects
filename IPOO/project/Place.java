import java.util.HashSet;
/**
 * Stores the characteristics of a place.
 *
 * @author David Maurício Ferreira, nº 202300443
 * @author José Luís Parreira de Oliveira, nº 202300558
 * @version 29/01/2024
 */
public class Place
{
    /** Name of the place */
    private String name;
    /** List of rooms in the place */
    private HashSet<Room> roomList;
    /**
     * Constructor to create a new place
     * @param name String name of the place
     */
    public Place(String name)
    {
        //keyword "voltar" is reserved for the console
        if(name != null && !name.equalsIgnoreCase("voltar")){
            roomList = new HashSet<Room>();
            this.name = name;
        }
    }
    /**
     * Constructor to create an existing place
     * @param name String name of the place
     * @param roomList HashSet&lt;Room&gt; the list of rooms in the place
     */
    public Place(String name, HashSet<Room> roomList){
        if(name != null && !name.equalsIgnoreCase("voltar") && roomList != null){
            this.name = name;
            this.roomList = roomList;
        }
    }
    // Selectors
    /**
     * Returns the list of rooms in the place
     * @return HashSet&lt;Room&gt; the list of rooms in the place
     */
    public HashSet<Room> getRoomList(){return roomList;}
    /**
     * Returns the number of rooms in the place
     * @return int the number of rooms in the place
     */
    public int getNumRooms(){return roomList.size();}
    /**
     * Returns the place name
     * @return String the place name
     */
    public String getName(){return name;}
    // Modifiers
    /**
     * Change the place name
     * @param name String the new name for the place
     */
    public void setName(String name){
        if(name != null && !name.equalsIgnoreCase("voltar")){this.name = name;}
    }
    // Other methods
    /**
     * Add a room to the place if not already in the place's room list
     * @param room Room the room to add to the place
     * @see #hasRoom(Room room)
     */
    public void addRoom(Room room){
        if(room != null && !hasRoom(room)){
            roomList.add(room);
        }
    }
    /**
     * Remove the room from the place's room list if it is in the list
     * @param room Room the room to remove from the place's room list
     * @see #hasRoom(Room room)
     */
    public void removeRoom(Room room){
        if(room != null && hasRoom(room)) {
            roomList.remove(room);
        }
    }
    /**
     * Returns true if the place's room list contains the room
     * @param room Room the room to check if it's in the place's room list
     * @return boolean true if the place's room list contains the room
     */
    public boolean hasRoom(Room room){
        return roomList.contains(room);
    }
    /**
     * Returns true if the place's room list contains the room
     * @param name String the room's name to check if it's in the place's room list
     * @return boolean true if the place's room list contains the room
     * @see Room#getName
     */
    public boolean hasRoom(String name){
        boolean isFound = false;
        if(name != null){
            for(Room room : roomList){
                if(name.equalsIgnoreCase(room.getName())){
                    isFound = true;
                    break;
                }
            }
        }
        return isFound;
    }
    /**
     * Get the room with this name in this place
     * @param nameInput String the name to compare to the name of the existing rooms in this place
     * @return Room the room with this name in this place
     * @see Room#getName
     */
    public Room getRoom(String nameInput){
        Room roomFound = null;
        if(nameInput != null){
            for(Room room : roomList){
                if(nameInput.equalsIgnoreCase(room.getName())){
                    roomFound = room;
                    break;
                }
            }
        }
        return roomFound;
    }
    /**
     * Prints to the console the place's characteristics
     * @see #getNumRooms
     * @see Room#show
     */
    public void show(){
        System.out.println();
        System.out.println("Nome do local: " + name);
        System.out.println("Salas (" + getNumRooms() + "): ");
        for(Room room : roomList){
            room.show();
        }
    }
}