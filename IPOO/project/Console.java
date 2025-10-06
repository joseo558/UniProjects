import java.util.Scanner;
/**
 * Provides a console (UI) and a manager for the user to be able to interact with the program
 *
 * @author David Maurício Ferreira, nº 202300443
 * @author José Luís Parreira de Oliveira, nº 202300558
 * @version 29/01/2024
 */
public class Console
{
    /** The console's scanner to obtain the user input */
    private Scanner sc;
    /** the console's manager to interact with the program */
    private Manager manager;
    /**
     * Main method to welcome the user and start the console
     * @param args String[] the first name of the user
     * @see #Console
     * @see #startConsole
     */
    public static void main(String[] args){
        if(args.length == 1){
            System.out.println("Olá " + args[0] + ".");
        }
        Console console = new Console();
        console.startConsole();
    }
    /**
     * Constructor to create a new console and manager.
     * @see #sc
     * @see #manager
     * @see #main(String[] args)
     */
    private Console()
    {
        sc = new Scanner(System.in);
        manager = new Manager();
    }
    /**
     * Start the console loop, when it ends the console is closed
     * @see TestApp#testApplication(Manager manager)
     */
    // Start loop, close console if terminated
    private void startConsole(){
        TestApp.testApplication(manager); // Populate testing values
        next(); // Console loop
        System.out.println("Consola encerrada.");
    }
    // Menu navigation
    /**
     * The console's loop, when it ends the console is closed. By default starts with the main menu.
     * @see #startConsole
     * @see #logout
     * @see #mainMenu
     * @see #manageEvents
     * @see #managePlaces
     * @see #manageSpeakers
     * @see #findEvent
     * @see #editEvent(Event event)
     * @see #findPlace
     * @see #editPlace(Place place)
     * @see #findEdition(Event event)
     * @see #editEdition(Event event, Edition edition)
     * @see #findLecture(Edition edition)
     * @see #editLecture(Event event, Edition edition, Lecture lecture)
     */
    private void next(){
        int select = 0;
        Event event = null;
        Edition edition = null;
        Lecture lecture = null;
        Place place = null;
        do{
            switch(select){
                case -1:
                    select = logout();
                    break;
                case 0:
                    select = mainMenu();
                    break;
                case 1:
                    event = null;
                    select = manageEvents();
                    break;
                case 2:
                    place = null;
                    select = managePlaces();
                    break;
                case 3:
                    select = manageSpeakers();
                    break;
                case 4:
                    select = listStatistics();
                    break;
                case 12:
                    edition = null;
                    if(event == null){
                        event = findEvent();
                        if(event == null){
                            System.out.println("Evento não encontrado.");
                            select = 1;
                        }
                    }else{select = editEvent(event);}
                    break;
                case 22:
                    if(place == null){
                        place = findPlace();
                        if(place == null){
                            System.out.println("Local não encontrado.");
                            select = 2;
                        }
                    }else{select = editPlace(place);}
                    break;
                case 122:
                    lecture = null;
                    if(edition == null){
                        edition = findEdition(event);
                        if(edition == null){
                            System.out.println("Edição do evento não encontrada.");
                            select = 12;
                        }
                    }else{select = editEdition(event, edition);}
                    break;
                case 1222:
                    if(lecture == null){
                        lecture = findLecture(edition);
                        if(lecture == null){
                            System.out.println("Palestra não encontrada.");
                            select = 122;
                        }
                    }else{select = editLecture(event, edition, lecture);}
                    break;
                default:
                    select = -2; //Close console
            }
        }while(select != -2);
    }
    /**
     * The console's main menu, returns to the main loop with the option selected in order to invoke the respective menu.
     * @return int the menu option selected
     * @see #next
     */
    private int mainMenu(){
        System.out.println();
        System.out.println("-- Sistema de Gestão de Eventos e Conferências --");
        System.out.println("1 - Gerir eventos");
        System.out.println("2 - Gerir locais");
        System.out.println("3 - Gerir palestrantes");
        System.out.println("4 - Listar estatísticas");
        System.out.println("0 - Sair");
        System.out.println("Escolha uma opção:");
        String input;
        char test;
        int result;
        do{
            input = sc.nextLine();
            if(input.length() > 0){
                test = input.charAt(0);
            }else{test = '?';}
            switch(test){
                case '1':
                    result = 1;
                    break;
                case '2':
                    result = 2;
                    break;
                case '3':
                    result = 3;
                    break;
                case '4':
                    result = 4;
                    break;
                case '0':
                    result = -1;
                    break;
                default:
                    System.out.println("Não válido. Tente novamente.");
                    result = -2;
            }
        }while(result == -2);
        return result;
    }
    /**
     * The logout confirmation, returns to the main loop with the option selected.
     * @return int the menu option selected. If the logout is confirmed, the main loop ends.
     * @see #next
     */
    private int logout(){
        System.out.println();
        System.out.println("Confirma que quer terminar o programa?");
        System.out.println("1 - Sim. Encerrar a consola.");
        System.out.println("2 - Não. Retomar ao menu principal.");
        String input;
        char test;
        int result;
        do{
            input = sc.nextLine();
            if(input.length() >0){
                test = input.charAt(0);
            }else{test = '?';}
            switch(test){
                case '1':
                    result = -2;
                    break;
                case '2':
                    result = 0;
                    break;
                default:
                    System.out.println("Não válido. Tente novamente.");
                    result = -3;
            }
        }while(result == -3);
        return result;
    }
    // Events
    /**
     * The menu to manage events. 
     * @return int the menu option selected. Except for "0" and "2", the other options return to this menu
     * @see #next
     * @see #createEvent
     * @see #searchEvent
     * @see Manager#listAllEvents
     * @see #removeEvent
     */
    private int manageEvents(){
        System.out.println();
        System.out.println("-- Gerir Eventos --");
        System.out.println("1 - Criar novo evento");
        System.out.println("2 - Editar evento");
        System.out.println("3 - Pesquisar evento");
        System.out.println("4 - Listar todos os eventos");
        System.out.println("5 - Remover um evento");
        System.out.println("0 - Sair");
        System.out.println("Escolha uma opção:");
        String input;
        char test;
        int result;
        do{
            input = sc.nextLine();
            if(input.length() >0){
                test = input.charAt(0);
            }else{test = '?';}
            switch(test){
                case '1':
                    createEvent();
                    result = 1;
                    break;
                case '2':
                    result = 12;
                    break;
                case '3':
                    searchEvent();
                    result = 1;
                    break;
                case '4':
                    manager.listAllEvents();
                    result = 1;
                    break;
                case '5':
                    removeEvent();
                    result = 1;
                    break;
                case '0':
                    result = 0;
                    break;
                default:
                    result = -1;
                    System.out.println("Não válido. Tente novamente.");
            }
        }while(result == -1);
        return result;
    }
    /**
     * Create a new event
     * @see ManageEvents#createEvent(Manager manager, String name)
     */
    private void createEvent(){
        System.out.println();
        System.out.println("Escreva um nome para o evento ou 'voltar' para retornar:");
        String nameInput = sc.nextLine();
        if(nameInput != null && !nameInput.equalsIgnoreCase("voltar")){
            ManageEvents.createEvent(manager, nameInput);
        }
    }
    /**
     * Find an event in the list of events
     * @Manager#listAllEvents
     * @Event
     * @see Manager#getEvent(String nameInput)
     */
    private Event findEvent(){
        manager.listAllEvents();
        System.out.println();
        System.out.println("Escreva o nome do evento ou 'voltar' para retornar: ");
        String nameInput = sc.nextLine();
        Event event = null;
        if(nameInput != null && !nameInput.equalsIgnoreCase("voltar")){
            event = manager.getEvent(nameInput);
        }
        return event;
    }
    /**
     * If an event is found, print to the console the event's characteristics
     * @see #findEvent
     * @see Event#show
     */
    private void searchEvent(){
        Event event = findEvent();
        if(event == null){
            System.out.println("Evento não encontrado.");
        }else{
            event.show();
        }
    }
    /**
     * Remove an event from the list of events
     * @see #findEvent
     * @see Manager#removeEvent(Event event)
     */
    private void removeEvent(){
        Event event = findEvent();
        if(event == null){
            System.out.println("Evento não encontrado.");
        }else{
            manager.removeEvent(event);
            System.out.println("Evento removido.");
        }
    }
    /**
     * The menu to edit an event (and manage the editions)
     * @param event Event the event to edit
     * @return int the menu option selected. Except for "0" and "2", the other options return to this menu
     * @see #next
     * @see #createEdition(Event event)
     * @see #searchEdition(Event event)
     * @see Event#show
     * @see #simulateEdition(Event event)
     * @see #editEventName(Event event)
     * @see #removeEdition(Event event)
     */
    private int editEvent(Event event){
        System.out.println();
        System.out.println("-- Editar evento --");
        System.out.println("1 - Adicionar nova edição");
        System.out.println("2 - Editar edição");
        System.out.println("3 - Pesquisar edição");
        System.out.println("4 - Listar todas as edições");
        System.out.println("5 - Simular edição");
        System.out.println("6 - Mudar o nome do evento");
        System.out.println("7 - Remover edição");
        System.out.println("0 - Sair");
        System.out.println("Escolha uma opção:");
        String input;
        char test;
        int result;
        do{
            input = sc.nextLine();
            if(input.length() >0){
                test = input.charAt(0);
            }else{test = '?';}
            switch(test){
                case '1':
                    createEdition(event);
                    result = 12;
                    break;
                case '2':
                    result = 122;
                    break;
                case '3':
                    searchEdition(event);
                    result = 12;
                    break;
                case '4':
                    event.show();
                    result = 12;
                    break;
                case '5':
                    simulateEdition(event);
                    result = 12;
                    break;
                case '6':
                    editEventName(event);
                    result = 12;
                    break;
                case '7':
                    removeEdition(event);
                    result = 12;
                    break;
                case '0':
                    result = 1;
                    break;
                default:
                    result = -1;
                    System.out.println("Não válido. Tente novamente.");
            }
        }while(result == -1);
        return result;
    }
    /**
     * Change a event's name
     * @param event Event the event whose name is changing
     * @see ManageEvents#editEventName(Manager manager, Event event, String newName)
     */
    private void editEventName(Event event){
        System.out.println();
        System.out.println("Escreva o novo nome do evento ou 'voltar' para retornar: ");
        String newName = sc.nextLine();
        if(newName != null && !newName.equalsIgnoreCase("voltar")){
            ManageEvents.editEventName(manager, event, newName);
        }
    }
    /**
     * Create and add an edition to the event
     * @param event Event the event in which to add the edition
     * @see #findPlace
     * @see ManageEvents#createEdition(Event event, Place place)
     */
    private void createEdition(Event event){
        Place place = findPlace();
        if(place == null){
            System.out.println("Local não encontrado.");
        }else{
            ManageEvents.createEdition(event, place);
            System.out.println("Foi criada a edição.");
        }
    }
    /**
     * Find an edition in the list of editions of the events
     * @param event Event the event in which to find the edition
     * @return Edition the edition in the event's editions list with the number provided or null if not found
     * @see Event#show
     * @see ManageEvents#findEdition(Event event, String inputString)
     */
    private Edition findEdition(Event event){
        event.show();
        System.out.println();
        System.out.println("Escreva o número da edição ou 'voltar' para retornar: ");
        String inputString = sc.nextLine();
        Edition edition = null;
        if(inputString != null && !inputString.equalsIgnoreCase("voltar")){
            edition = ManageEvents.findEdition(event, inputString);
        }
        return edition;
    }
    /**
     * If an event is found, print to the console the event's characteristics
     * @param event Event the event in which to search for an edition
     * @see #findEdition(Event event)
     * @see Edition#show
     */
    private void searchEdition(Event event){
        Edition edition = findEdition(event);
        if(edition == null){
            System.out.println("Edição não encontrada.");
        }else{
            edition.show();
        }
    }
    /**
     * Simulate an edition if it's has the conditions to permit it
     * @param event Event the event in which to simulate an edition
     * @see #findEdition(Event event)
     * @see ManageEvents.simulateEdition(Manager manager, Event event, Edition edition)
     */
    private void simulateEdition(Event event){
        Edition edition = findEdition(event);
        if(edition == null){
            System.out.println("Edição não encontrada.");
        }else{
            if(ManageEvents.simulateEdition(manager, event, edition)){
                System.out.println("Edição simulada.");
            }else{System.out.println("É necessário antes simular todas as palestras.");}
        }
    }
    /**
     * Remove an edition from the event's list of editions
     * @param event Event the event in which to remove an edition
     * @see #findEdition(Event event)
     * @see Edition#isSimulated
     * @see Event#removeEdition(Edition edition)
     */
    private void removeEdition(Event event){
        Edition edition = findEdition(event);
        if(edition == null){
            System.out.println("Edição não encontrada.");
        }else{
            if(!edition.isSimulated()){
                event.removeEdition(edition);
                System.out.println("Edição removida.");
            }else{System.out.println("Edição já simulada, não é possível remover.");}
        }
    }
    /**
     * The menu to edit an edition of an event (and manage the lectures in it)
     * @param event Event the event in which is the edition to edit
     * @param edition Edition the edition to edit
     * @return int the menu option selected. Except for "0" and "2", the other options return to this menu
     * @see #next
     * @see #createLecture(Edition edition)
     * @see #searchLecture(Edition edition)
     * @see Edition#show
     * @see #simulateLecture(Event event, Edition edition)
     * @see #setStartDate(Edition edition)
     * @see #setEndDate(Edition edition)
     * @see #setPlace(Edition edition)
     * @see #removeLecture(Edition edition)
     */
    private int editEdition(Event event, Edition edition){
        System.out.println();
        System.out.println("-- Editar Edição --");
        System.out.println("1 - Adicionar nova palestra");
        System.out.println("2 - Editar palestra");
        System.out.println("3 - Pesquisar palestra");
        System.out.println("4 - Listar todas as palestras");
        System.out.println("5 - Simular palestra");
        System.out.println("6 - Alterar data de início da edição");
        System.out.println("7 - Alterar data de fim da edição");
        System.out.println("8 - Alterar local da edição");
        System.out.println("9 - Remover palestra");
        System.out.println("0 - Sair");
        System.out.println("Escolha uma opção:");
        String input;
        char test;
        int result;
        do{
            input = sc.nextLine();
            if(input.length() >0){
                test = input.charAt(0);
            }else{test = '?';}
            switch(test){
                case '1':
                    createLecture(edition);
                    result = 122;
                    break;
                case '2':
                    result = 1222;
                    break;
                case '3':
                    searchLecture(edition);
                    result = 122;
                    break;
                case '4':
                    edition.show();
                    result = 122;
                    break;
                case '5':
                    simulateLecture(event, edition);
                    result = 122;
                    break;
                case '6':
                    setStartDate(edition);
                    result = 122;
                    break;
                case '7':
                    setEndDate(edition);
                    result = 122;
                    break;
                case '8':
                    setPlace(edition);
                    result = 122;
                    break;
                case '9':
                    removeLecture(edition);
                    result = 122;
                    break;
                case '0':
                    result = 12;
                    break;
                default:
                    result = -1;
                    System.out.println("Não válido. Tente novamente.");
            }
        }while(result == -1);
        return result;
    }
    /**
     * Create and add an lecture to the edition
     * @param edition Edition the edition in which to add the lecture
     * @see Edition#isSimulated
     * @see ManageEditions#createLecture(Edition edition, String titleInput)
     */
    private void createLecture(Edition edition){
        if(!edition.isSimulated()){
            System.out.println();
            System.out.println("Escreva o título da palestra ou 'voltar' para retornar:");
            String titleInput = sc.nextLine();
            if(titleInput != null && !titleInput.equalsIgnoreCase("voltar")){
                ManageEditions.createLecture(edition, titleInput);
            }
        }else{System.out.println("Edição já simulada.");}
    }
    /**
     * Find a lecture in the list of lectures of the edition
     * @param edition Edition the edition in which to find a lecture
     * @return Lecture the lecture in the edition's lectures list with the title provided or null if not found
     * @see Edition#show
     * @see Edition#getLecture(String titleInput)
     */
    private Lecture findLecture(Edition edition){
        edition.show();
        System.out.println();
        System.out.println("Escreva o título da palestra ou 'voltar' para retornar: ");
        String titleInput = sc.nextLine();
        Lecture lecture = null;
        if(titleInput != null && !titleInput.equalsIgnoreCase("voltar")){
            lecture = edition.getLecture(titleInput);
        }
        return lecture;
    }
    /**
     * If an lecture is found, print to the console the lecture's characteristics
     * @param edition Edition the edition in which to find a lecture
     * @see #findLecture(Edition edition)
     * @see Lecture#show
     */
    private void searchLecture(Edition edition){
        Lecture lecture = findLecture(edition);
        if(lecture == null){
            System.out.println("Palestra não encontrada.");
        }else{
            lecture.show();
        }
    }
    /**
     * Remove a lecture from the edition's list of lectures
     * @param edition Edition the edition in which to remove a lecture
     * @see Edition#isSimulated
     * @see #findLecture(Edition edition)
     * @see Lecture#isSimulated
     * @see Edition#removeLecture(Lecture lecture)
     */
    private void removeLecture(Edition edition){
        if(!edition.isSimulated()){
            Lecture lecture = findLecture(edition);
            if(lecture == null){
                System.out.println("Palestra não encontrada.");
            }else{
                if(!lecture.isSimulated()){
                    edition.removeLecture(lecture);
                    System.out.println("Palestra removida.");
                }else{System.out.println("Palestra já simulada, não é possível remover.");}
            }
        }else{System.out.println("Edição já simulada.");}
    }
    /**
     * Get a date from the user.
     * @return String[] a array with the date in the order: year, month, day
     */
    private String[] getDateInput(){
        String[] date = new String[3];
        System.out.println();
        System.out.println("Escreva o dia ou 'voltar' para retornar:");
        String input = sc.nextLine();
        if(input != null && !input.equalsIgnoreCase("voltar")){
            date[0] = input;
            System.out.println("Escreva o mês ou 'voltar' para retornar:");
            input = sc.nextLine();
            if(input != null && !input.equalsIgnoreCase("voltar")){
                date[1] = input;
                System.out.println("Escreva o ano ou 'voltar' para retornar:");
                input = sc.nextLine();
                if(input != null && !input.equalsIgnoreCase("voltar")){
                    date[2] = input;
                    return date;
                }
            }
        }
        return null;
    }
    /**
     * Change the edition's start date
     * @param edition Edition the edition to edit
     * @see Edition#isSimulated
     * @see #getDateInput
     * @see ManageEditions#setStartDate(Edition edition, String yearInput, String monthInput, String dayInput);
     */
    private void setStartDate(Edition edition){
        if(!edition.isSimulated()){
            String[] date = getDateInput();
            if(date != null){
                ManageEditions.setStartDate(edition, date[2], date[1], date[0]);
            }
        }else{System.out.println("Edição já simulada.");}
    }
    /**
     * Change the edition's end date
     * @param edition Edition the edition to edit
     * @see Edition#isSimulated
     * @see #getDateInput
     * @see ManageEditions#setEndDate(Edition edition, String yearInput, String monthInput, String dayInput);
     */
    private void setEndDate(Edition edition){
        if(!edition.isSimulated()){
            String[] date = getDateInput();
            if(date != null){
                ManageEditions.setEndDate(edition, date[2], date[1], date[0]);
            }
        }else{System.out.println("Edição já simulada.");}
    }
    /**
     * Change the edition's place
     * @param edition Edition the edition to edit
     * @see Edition#isSimulated
     * @see #findPlace
     * @see Edition#setPlace(Place place)
     */
    private void setPlace(Edition edition){
        if(!edition.isSimulated()){
            Place place = findPlace();
            if(place == null){
                System.out.println("Local não encontrado.");
            }else{
                edition.setPlace(place);
                System.out.println("Local alterado.");
            }
        }else{System.out.println("Edição já simulada.");}
    }
    /**
     * Simulate a lecture if it's has the conditions to permit it
     * @param event Event the event with the edition in which to simulate a lecture
     * @param edition Edition the edition in which to simulate a lecture
     * @see #findLecture(Edition edition)
     * @see ManageEditions.simulateLecture(Manager manager, Event event, Edition edition, Lecture lecture)
     */
    private void simulateLecture(Event event, Edition edition){
        Lecture lecture = findLecture(edition);
        if(lecture == null){
            System.out.println("Palestra não encontrada.");
        }else{
            if(ManageEditions.simulateLecture(manager, event, edition, lecture)){
                System.out.println("Palestra simulada.");
            }else{System.out.println("A palestra deve ter palestrantes e sala antes de simular.");}
        }
    }
    /**
     * The menu to edit an lecture
     * @param event Event the event in which is the edition with the lecture to edit
     * @param edition Edition the edition with the lecture to edit
     * @param lecture Lecture the lecture to edit
     * @return int the menu option selected. Except for "0", the other options return to this menu
     * @see #next
     * @see #addSpeaker(Lecture lecture)
     * @see #setTitle(Edition edition, Lecture lecture)
     * @see #setRoom(Edition edition, Lecture lecture)
     * @see Lecture#show
     * @see #setStartDateTime(Edition edition, Lecture lecture)
     * @see #setEndDateTime(Edition edition, Lecture lecture)
     * @see #removeSpeaker(Lecture lecture)
     */
    private int editLecture(Event event, Edition edition, Lecture lecture){
        System.out.println();
        System.out.println("-- Editar Palestra --");
        System.out.println("1 - Adicionar palestrante");
        System.out.println("2 - Alterar o título");
        System.out.println("3 - Alterar a sala");
        System.out.println("4 - Listar todos os palestrantes");
        System.out.println("5 - Alterar data de início da palestra");
        System.out.println("6 - Alterar data de fim da palestra");
        System.out.println("7 - Remover palestrante");
        System.out.println("0 - Sair");
        System.out.println("Escolha uma opção:");
        String input;
        char test;
        int result;
        do{
            input = sc.nextLine();
            if(input.length() >0){
                test = input.charAt(0);
            }else{test = '?';}
            switch(test){
                case '1':
                    addSpeaker(lecture);
                    result = 1222;
                    break;
                case '2':
                    setTitle(edition, lecture);
                    result = 1222;
                    break;
                case '3':
                    setRoom(edition, lecture);
                    result = 1222;
                    break;
                case '4':
                    lecture.show();
                    result = 1222;
                    break;
                case '5':
                    setStartDateTime(edition, lecture);
                    result = 1222;
                    break;
                case '6':
                    setEndDateTime(edition, lecture);
                    result = 1222;
                    break;
                case '7':
                    removeSpeaker(lecture);
                    result = 1222;
                    break;
                case '0':
                    result = 122;
                    break;
                default:
                    result = -1;
                    System.out.println("Não válido. Tente novamente.");
            }
        }while(result == -1);
        return result;
    }
    /**
     * Add a speaker to the lecture
     * @param lecture Lecture the lecture in which to add the speaker
     * @see Lecture#isSimulated
     * @see #findSpeaker
     * @see Lecture#addSpeaker(Speaker speaker)
     */
    private void addSpeaker(Lecture lecture){
        if(!lecture.isSimulated()){
            Speaker speaker = findSpeaker();
            if(speaker == null){
                System.out.println("Palestrante não encontrado.");
            }else{
                lecture.addSpeaker(speaker);
            }
        }else{System.out.println("Palestra já simulada.");}
    }
    /**
     * Change the lecture's title
     * @param edition Edition the edition with the lecture
     * @param lecture Lecture the lecture to edit
     * @see Lecture#isSimulated
     * @see Edition#hasLecture(String nameInput)
     * @see Lecture#setTitle(String title)
     */
    private void setTitle(Edition edition, Lecture lecture){
        if(!lecture.isSimulated()){
            System.out.println();
            System.out.println("Escreva o novo nome da palestra ou 'voltar' para retornar: ");
            String nameInput = sc.nextLine();
            if(nameInput != null && !nameInput.equalsIgnoreCase("voltar") && !edition.hasLecture(nameInput)){
                lecture.setTitle(nameInput);
                System.out.println("Título alterado.");
            }
        }else{System.out.println("Palestra já simulada.");}
    }
    /**
     * Change the lecture's room
     * @param edition Edition the edition with the lecture
     * @param lecture Lecture the lecture to edit
     * @see Lecture#isSimulated
     * @see Edition#getPlace
     * @see #findRoom
     * @see Lecture#setRoom(Room room)
     */
    private void setRoom(Edition edition, Lecture lecture){
        if(!lecture.isSimulated()){
            Place place = edition.getPlace();
            Room room = findRoom(place);
            if(place == null){
                System.out.println("Local não encontrado.");
            }else{
                lecture.setRoom(room);
                System.out.println("Sala adicionada.");
            }
        }else{System.out.println("Palestra já simulada.");}
    }
    /**
     * Get a time from the user.
     * @return String[] a array with the time in the order: hours, minutes
     */
    private String[] getTimeInput(){
        String[] time = new String[2];
        System.out.println();
        System.out.println("Escreva a hora (0-23) ou 'voltar' para retornar:");
        String input = sc.nextLine();
        if(input != null && !input.equalsIgnoreCase("voltar")){
            time[0] = input;
            System.out.println("Escreva os minutos (0-59) ou 'voltar' para retornar:");
            input = sc.nextLine();
            if(input != null && !input.equalsIgnoreCase("voltar")){
                time[1] = input;
                return time;
            }
        }
        return null;
    }
    /**
     * Change the lectures's start date and time
     * @param edition Edition the edition with the lecture
     * @param lecture Lecture the lecture to edit
     * @see Lecture#isSimulated
     * @see #getDateInput
     * @see #getTimeInput
     * @see ManageLectures#setStartDateTime(Lecture lecture, String yearInput, String monthInput, String dayInput, String hoursInput, String minutesInput);
     */
    private void setStartDateTime(Edition edition, Lecture lecture){
        if(!lecture.isSimulated()){
            String[] date = getDateInput();
            String[] time = getTimeInput();
            if(date != null && time != null){
                ManageLectures.setStartDateTime(edition, lecture, date[2], date[1], date[0], time[0], time[1]);
            }
        }else{System.out.println("Palestra já simulada.");}
    }
    /**
     * Change the lectures's end date and time
     * @param edition Edition the edition with the lecture
     * @param lecture Lecture the lecture to edit
     * @see Lecture#isSimulated
     * @see #getDateInput
     * @see #getTimeInput
     * @see ManageLectures#setEndDateTime(Lecture lecture, String yearInput, String monthInput, String dayInput, String hoursInput, String minutesInput);
     */
    private void setEndDateTime(Edition edition, Lecture lecture){
        if(!lecture.isSimulated()){
            String[] date = getDateInput();
            String[] time = getTimeInput();
            if(date != null && time != null){
                ManageLectures.setEndDateTime(edition, lecture, date[2], date[1], date[0], time[0], time[1]);
            }
        }else{System.out.println("Palestra já simulada.");}
    }
    /**
     * Remove a speaker from the lecture's list of speakers
     * @param lecture Lecture the lecture from which to remove the speaker
     * @see Lecture#isSimulated
     * @see Lecture#show
     * @see Lecture#getSpeaker(String nameInput)
     * @see Lecture#removeSpeaker(Speaker speaker)
     */
    private void removeSpeaker(Lecture lecture){
        if(!lecture.isSimulated()){
            lecture.show();
            System.out.println();
            System.out.println("Escreva o nome do palestrante ou 'voltar' para retornar: ");
            String nameInput = sc.nextLine();
            if(nameInput != null && !nameInput.equalsIgnoreCase("voltar")){
                Speaker speaker = lecture.getSpeaker(nameInput);
                if(speaker == null){
                    System.out.println("Palestrante não encontrado.");
                }else{
                    lecture.removeSpeaker(speaker);
                }
            }
        }else{System.out.println("Palestra já simulada.");}
    }
    // Places
    /**
     * The menu to manage places
     * @return int the menu option selected. Except for "0" and "2", the other options return to this menu
     * @see #next
     * @see #createPlace
     * @see #searchPlace
     * @see Manager#listAllPlaces
     * @see #removePlace
     */
    private int managePlaces(){
        System.out.println();
        System.out.println("-- Gerir Locais --");
        System.out.println("1 - Criar novo local");
        System.out.println("2 - Editar local");
        System.out.println("3 - Pesquisar local");
        System.out.println("4 - Listar todos os locais");
        System.out.println("5 - Remover um local");
        System.out.println("0 - Sair");
        System.out.println("Escolha uma opção:");
        String input;
        char test;
        int result;
        do{
            input = sc.nextLine();
            if(input.length() >0){
                test = input.charAt(0);
            }else{test = '?';}
            switch(test){
                case '1':
                    createPlace();
                    result = 2;
                    break;
                case '2':
                    result = 22;
                    break;
                case '3':
                    searchPlace();
                    result = 2;
                    break;
                case '4':
                    manager.listAllPlaces();
                    result = 2;
                    break;
                case '5':
                    removePlace();
                    result = 2;
                    break;
                case '0':
                    result = 0;
                    break;
                default:
                    result = -1;
                    System.out.println("Não válido. Tente novamente.");
            }
        }while(result == -1);
        return result;
    }
    /**
     * Create a new place
     * @see ManagePlaces#createPlace(Manager manager, String name)
     */
    private void createPlace(){
        System.out.println();
        System.out.println("Escreva um nome ou 'voltar' para retornar:");
        String nameInput = sc.nextLine();
        if(nameInput != null && !nameInput.equalsIgnoreCase("voltar")){
            ManagePlaces.createPlace(manager, nameInput);
        }
    }
    /**
     * Find a place in the list of places
     * @see Manager#listAllPlaces
     * @see Manager#getPlace(String nameInput)
     */
    private Place findPlace(){
        manager.listAllPlaces();
        System.out.println();
        System.out.println("Escreva o nome do local ou 'voltar' para retornar: ");
        String nameInput = sc.nextLine();
        Place place = null;
        if(nameInput != null && !nameInput.equalsIgnoreCase("voltar")){
            place = manager.getPlace(nameInput);
        }
        return place;
    }
    /**
     * If a place is found, print to the console the place's characteristics
     * @see #findPlace
     * @see Place#show
     */
    private void searchPlace(){
        Place place = findPlace();
        if(place == null){
            System.out.println("Local não encontrado.");
        }else{
            place.show();
        }
    }
    /**
     * Remove a place from the list of places
     * @see #findPlace
     * @see Manager#removePlace(Place place)
     */
    private void removePlace(){
        Place place = findPlace();
        if(place == null){
            System.out.println("Local não encontrado.");
        }else{
            manager.removePlace(place);
            System.out.println("Local removido.");
        }
    }
    /**
     * The menu to edit places (advanced options)
     * @param place Place the place to edit
     * @return int the menu option selected. Except for "0", the other options return to this menu
     * @see #next
     * @see #editPlaceName(Place place)
     * @see #addRoom(Place place)
     * @see #editRoom(Place place)
     * @see #removeRoom(Place place)
     * @see Place#show
     */
    private int editPlace(Place place){
        System.out.println();
        System.out.println("-- Editar Local --");
        System.out.println("1 - Alterar o nome");
        System.out.println("2 - Adicionar salas");
        System.out.println("3 - Editar salas");
        System.out.println("4 - Remover salas");
        System.out.println("5 - Listar salas");
        System.out.println("0 - Sair");
        System.out.println("Escolha uma opção:");
        String input;
        char test;
        int result;
        do{
            input = sc.nextLine();
            if(input.length() >0){
                test = input.charAt(0);
            }else{test = '?';}
            switch(test){
                case '1':
                    editPlaceName(place);
                    result = 22;
                    break;
                case '2':
                    addRoom(place);
                    result = 22;
                    break;
                case '3':
                    editRoom(place);
                    result = 22;
                    break;
                case '4':
                    removeRoom(place);
                    result = 22;
                    break;
                case '5':
                    place.show();
                    result = 22;
                    break;
                case '0':
                    result = 2;
                    break;
                default:
                    result = -1;
                    System.out.println("Não válido. Tente novamente.");
            }
        }while(result == -1);
        return result;
    }
    /**
     * Change a place's name
     * @param place Place the place whose name is changing
     * @see ManagePlaces#editPlaceName(Manager manager, Place place, String newName)
     */
    private void editPlaceName(Place place){
        System.out.println();
        System.out.println("Escreva o novo nome do local ou 'voltar' para retornar: ");
        String newName = sc.nextLine();
        if(newName != null && !newName.equalsIgnoreCase("voltar")){
            ManagePlaces.editPlaceName(manager, place, newName);
        }
    }
    /**
     * Add a room to the place
     * @param place Place the place in which to add the room
     * @see Place#hasRoom(String name)
     * @see ManagePlaces#createRoom(Place place, String name, String capacityString)
     */
    private void addRoom(Place place){
        System.out.println();
        System.out.println("Escreva o nome da nova sala ou 'voltar' para retornar: ");
        String name = sc.nextLine();
        if(name != null && !name.equalsIgnoreCase("voltar")){
            if(place.hasRoom(name)){
                System.out.println("Já existe uma sala com o nome " + name + " neste local.");
            }else{
                System.out.println("Escreva a capacidade (número inteiro) da sala ou 'voltar' para retornar: ");
                String capacityString = sc.nextLine();
                if(capacityString != null && !capacityString.equalsIgnoreCase("voltar")){
                    ManagePlaces.createRoom(place, name, capacityString);
                }
            }
        }
    }
    /**
     * Find a room in the place's list of rooms
     * @param place Place the place in which to find a room
     * @see Place#getRoom(String nameInput)
     */
    private Room findRoom(Place place){
        System.out.println();
        System.out.println("Escreva o nome da sala ou 'voltar' para retornar: ");
        String nameInput = sc.nextLine();
        Room room = null;
        if(nameInput != null && !nameInput.equalsIgnoreCase("voltar")){
            room = place.getRoom(nameInput);
        }
        return room;
    }
    /**
     * Change a room's name and capacity
     * @param place Place the place in which is the room we want to change
     * @see #findRoom(Place place)
     * @see ManagePlaces#editRoom(Place place, Room room, String newName, String capacityString)
     */
    private void editRoom(Place place){
        Room room = findRoom(place);
        if(room == null){
            System.out.println("Sala não encontrada.");
        }else{
            System.out.println("Escreva o novo nome da sala ou 'voltar' para retornar: ");
            String newName = sc.nextLine();
            if(newName != null && !newName.equalsIgnoreCase("voltar")){
                System.out.println("Escreva a nova capacidade (número inteiro) da sala ou 'voltar' para retornar: ");
                String capacityString = sc.nextLine();
                if(capacityString != null && !capacityString.equalsIgnoreCase("voltar")){
                    ManagePlaces.editRoom(place, room, newName, capacityString);
                }
            }
        }
    }
    /**
     * Remove a room from the place's list of rooms
     * @param place Place the place from which to remove a room
     * @see #findRoom(Place place)
     * @see Place#removeRoom(Room room)
     */
    private void removeRoom(Place place){
        Room room = findRoom(place);
        if(room == null){
            System.out.println("Sala não encontrada.");
        }else{
            place.removeRoom(room);
            System.out.println("Sala removida.");
        }
    }
    // Speakers
    /**
     * The menu to manage speakers 
     * @return int the menu option selected. Except for "0", the other options return to this menu
     * @see #next
     * @see #createSpeaker
     * @see #editSpeakerName
     * @see #searchSpeaker
     * @see Manager#listAllSpeakers
     * @see #removeSpeaker
     */
    private int manageSpeakers(){
        System.out.println();
        System.out.println("-- Gerir Palestrantes --");
        System.out.println("1 - Criar novo palestrante");
        System.out.println("2 - Alterar o nome de um palestrante");
        System.out.println("3 - Pesquisar palestrante");
        System.out.println("4 - Listar todos os palestrantes");
        System.out.println("5 - Remover um palestrante");
        System.out.println("0 - Sair");
        System.out.println("Escolha uma opção:");
        String input;
        char test;
        int result;
        do{
            input = sc.nextLine();
            if(input.length() >0){
                test = input.charAt(0);
            }else{test = '?';}
            switch(test){
                case '1':
                    createSpeaker();
                    result = 3;
                    break;
                case '2':
                    editSpeakerName();
                    result = 3;
                    break;
                case '3':
                    searchSpeaker();
                    result = 3;
                    break;
                case '4':
                    manager.listAllSpeakers();
                    result = 3;
                    break;
                case '5':
                    removeSpeaker();
                    result = 3;
                    break;
                case '0':
                    result = 0;
                    break;
                default:
                    result = -1;
                    System.out.println("Não válido. Tente novamente.");
            }
        }while(result == -1);
        return result;
    }
    /**
     * Create a new speaker
     * @see ManageSpeakers#createSpeaker(Manager manager, String name)
     */
    private void createSpeaker(){
        System.out.println();
        System.out.println("Escreva um nome ou 'voltar' para retornar:");
        String nameInput = sc.nextLine();
        if(nameInput != null && !nameInput.equalsIgnoreCase("voltar")){
            ManageSpeakers.createSpeaker(manager, nameInput);
        }
    }
    /**
     * Find a speaker in the list of speakers
     * @see Manager#getSpeaker(String nameInput)
     */
    private Speaker findSpeaker(){
        System.out.println();
        System.out.println("Escreva o nome do palestrante ou 'voltar' para retornar: ");
        String nameInput = sc.nextLine();
        Speaker speaker = null;
        if(nameInput != null && !nameInput.equalsIgnoreCase("voltar")){
            speaker = manager.getSpeaker(nameInput);
        }
        return speaker;
    }
    /**
     * Change a speaker's name
     * @see #findSpeaker
     * @see ManageSpeakers#editSpeakerName(Manager manager, Speaker speaker, String newName)
     */
    private void editSpeakerName(){
        Speaker speaker = findSpeaker();
        if(speaker == null){
            System.out.println("Palestrante não encontrado.");
        }else{
            System.out.println();
            System.out.println("Escreva o novo nome do palestrante ou 'voltar' para retornar: ");
            String newName = sc.nextLine();
            if(newName != null && !newName.equalsIgnoreCase("voltar")){
                ManageSpeakers.editSpeakerName(manager, speaker, newName);
            }
        }
    }
    /**
     * If a speaker is found, print to the console the speaker's characteristics
     * @see #findSpeaker
     * @see Speaker#show
     */
    private void searchSpeaker(){
        Speaker speaker = findSpeaker();
        if(speaker == null){
            System.out.println("Palestrante não encontrado.");
        }else{
            speaker.show();
        }
    }
    /**
     * Remove a speaker from the list of speakers
     * @see #findSpeaker
     * @see Manager#removeSpeaker(Speaker speaker)
     */
    private void removeSpeaker(){
        Speaker speaker = findSpeaker();
        if(speaker == null){
            System.out.println("Palestrante não encontrado.");
        }else{
            manager.removeSpeaker(speaker);
            System.out.println("Palestrante removido.");
        }
    }
    // Statistics
    /**
     * The menu to list statistics
     * @return int the menu option selected. Except for "0", the other options return to this menu
     * @see #next
     * @see #listRanking
     * @see #listBestEditions
     */
    private int listStatistics(){
        System.out.println();
        System.out.println("-- Listar Estatísticas --");
        System.out.println("1 - Listar ranking de palestrantes");
        System.out.println("2 - Listar melhores edições por evento");
        System.out.println("0 - Sair");
        System.out.println("Escolha uma opção:");
        String input;
        char test;
        int result;
        do{
            input = sc.nextLine();
            if(input.length() >0){
                test = input.charAt(0);
            }else{test = '?';}
            switch(test){
                case '1':
                    listRanking();
                    result = 4;
                    break;
                case '2':
                    listBestEditions();
                    result = 4;
                    break;
                case '0':
                    result = 0;
                    break;
                default:
                    result = -1;
                    System.out.println("Não válido. Tente novamente.");
            }
        }while(result == -1);
        return result;
    }
    /**
     * List speaker's ranking
     * @see Manager#getStatistics
     * @see Statistics#showSpeakerRanking
     */
    private void listRanking(){
        manager.getStatistics().showSpeakerRanking();
    }
    /**
     * List best editions for each event
     * @see Manager#getStatistics
     * @see Statistics#listBestEditionsPerEvent(Manager manager)
     */
    private void listBestEditions(){
        manager.getStatistics().listBestEditionsPerEvent(manager);
    }
}