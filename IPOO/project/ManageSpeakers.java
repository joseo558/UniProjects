/**
 * Static methods to manage speakers (advanced options). Manager class only adds, finds or removes.
 *
 * @author David Maurício Ferreira, nº 202300443
 * @author José Luís Parreira de Oliveira, nº 202300558
 * @version 29/01/2024
 */
public class ManageSpeakers
{
    /**
     * Create a new speaker with the name provided
     * @param manager Manager the reference to the manager
     * @param name String the name for the new speaker
     * @see Manager#hasSpeaker(String nameInput)
     * @see Speaker
     * @see Manager#addSpeaker(Speaker speaker)
     */
    public static void createSpeaker(Manager manager, String name){
        if(manager != null && name != null){
            if(manager.hasSpeaker(name)){
                System.out.println("Já existe um palestrante com o nome '" + name + "'. Não foi possível criar.");
            }else{
                // Add
                Speaker speaker = new Speaker(name);
                manager.addSpeaker(speaker);
                System.out.println("Foi criado o palestrante " + name + ".");
            }
        }
    }
    /**
     * Change the speaker's name to the one provided
     * @param manager Manager the reference to the manager
     * @param speaker Speaker the speaker whose name is changing
     * @param newName String the new name for the speaker
     * @see Manager#hasSpeaker(String nameInput)
     * @see Speaker#setName(String name)
     */
    public static void editSpeakerName(Manager manager, Speaker speaker, String newName){
        if(speaker != null && newName != null){
            if(manager.hasSpeaker(newName)){
                System.out.print("Existe um palestrante com este nome.");
            }else{
                speaker.setName(newName);
                System.out.println("Nome do palestrante alterado para " + newName + ".");
            }
        }
    }
}