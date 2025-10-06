/**
 * Stores the characteristics of a room.
 *
 * @author David Maurício Ferreira, nº 202300443
 * @author José Luís Parreira de Oliveira, nº 202300558
 * @version 29/01/2024
 */
public class Room
{
    /** Name of the room */
    private String name;
    /** Capacity of the room */
    private int capacity;
    //Constructors
    /**
     * Constructor to create a new room
     * @param name String the name of the room
     * @param capacity int the capacity of the room
     */
    public Room(String name, int capacity)
    {
        //keyword "voltar" is reserved for the console
        if(name != null && !name.equalsIgnoreCase("voltar") && capacity >0){
            this.name = name;
            this.capacity = capacity;
        }
    }
    // Selectors
    /**
     * Returns the name of the room
     * @return String the name of the room
     */
    public String getName(){return name;}
    /**
     * Returns the capacity of the room
     * @return int the capacity of the room
     */
    public int getCapacity(){return capacity;}
    // Modifiers
    /**
     * Change the room's name
     * @param name String the new name for the room
     */
    public void setName(String name){
        if(name != null && !name.equalsIgnoreCase("voltar")){this.name = name;}
    }
    /**
     * Change the room's capacity
     * @param capacity int the new capacity for the room
     */
    public void setCapacity(int capacity){
        if(capacity > 0){this.capacity = capacity;}
    }
    // Other methods
    /**
     * Prints to the console the room's characteristics
     */
    public void show(){
        System.out.println();
        System.out.println("         Nome da sala: " + name);
        System.out.println("         Capacidade: " + capacity);
    }
}