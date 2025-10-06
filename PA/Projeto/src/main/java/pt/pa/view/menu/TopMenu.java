package pt.pa.view.menu;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import pt.pa.controller.TransportMapController;
import pt.pa.view.map.MapType;

/**
 * Represents the top menu bar of the main application view
 */
public class TopMenu {
    /** The menu bar */
    private final MenuBar menuBar;
    /** Setup menu item to import data */
    private final MenuItem setupMenuImport;
    /** Setup menu item to disable route */
    private final MenuItem setupMenuDisableroute;
    /** Setup menu item to disable route */
    private final MenuItem setupMenuBikeChangeTime;
    /** Setup menu item to clean the map */
    private final MenuItem setupCleanMap;
    /** Change map menu item to change to night map */
    private final MenuItem changeMapMenuNight;
    /** Change map menu item to change to default map */
    private final MenuItem changeMapMenuDefault;
    /** Change map menu item to change to satellite map */
    private final MenuItem changeMapMenuSatellite;
    /** Change map menu item to change to terrain map */
    private final MenuItem changeMapMenuTerrain;
    /** Exit menu item to close the application */
    private final MenuItem exitMenuClose;

    /**
     * Constructor for TopMenu
     */
    public TopMenu() {
        menuBar = new MenuBar();

        // Setup Menu
        setupMenuImport = new MenuItem("Importar dados");
        setupCleanMap = new MenuItem("Limpar Mapa");
        setupMenuDisableroute = new MenuItem("Desativar rota");
        setupMenuBikeChangeTime = new MenuItem("Alterar Tempo de Bicicleta");

        // Change Map Menu
        changeMapMenuNight = new MenuItem("Noturno");
        changeMapMenuDefault = new MenuItem("Pré-definido");
        changeMapMenuSatellite = new MenuItem("Satélite");
        changeMapMenuTerrain = new MenuItem("Terreno");

        // Exit Menu
        exitMenuClose = new MenuItem("Fechar");

        // Create and add all menus to the menu bar
        menuBar.getMenus().addAll(
                createMenu("Configurar", setupMenuImport, setupCleanMap,setupMenuDisableroute,setupMenuBikeChangeTime),
                createMenu("Alterar Mapa", changeMapMenuNight, changeMapMenuDefault, changeMapMenuSatellite, changeMapMenuTerrain),
                createMenu("Sair", exitMenuClose)
        );
    }

    /**
     * Create a menu with the given title and items
     * @param title String the title of the menu
     * @param items MenuItem... the items of the menu
     * @return Menu the created menu
     */
    private Menu createMenu(String title, MenuItem... items) {
        Menu menu = new Menu(title);
        menu.getItems().addAll(items);
        return menu;
    }

    /**
     * Get the menu bar
     * @return MenuBar the menu bar
     */
    public MenuBar getMenuBar() {
        return menuBar;
    }

    /**
     * Attaches event handlers to menu items using the given controller
     * @param controller TransportMapController the controller to handle menu actions
     */
    public void setTriggers(TransportMapController controller) {
        setupMenuImport.setOnAction(event -> controller.importData());
        setupCleanMap.setOnAction(event -> controller.clearMap());
        setupMenuDisableroute.setOnAction(event -> controller.displayDisableRoute());
        setupMenuBikeChangeTime.setOnAction(event -> controller.displayBikeChangeTime());
        changeMapMenuNight.setOnAction(event -> controller.changeMap(MapType.NIGHT));
        changeMapMenuDefault.setOnAction(event -> controller.changeMap(MapType.DEFAULT));
        changeMapMenuSatellite.setOnAction(event -> controller.changeMap(MapType.SATELLITE));
        changeMapMenuTerrain.setOnAction(event -> controller.changeMap(MapType.TERRAIN));
        exitMenuClose.setOnAction(event -> controller.exitApplication());
    }
}