package edu.kit.valaris.menu;

import com.jme3.app.state.AppState;
import com.jme3.system.AppSettings;
import edu.kit.valaris.BaseApplication;
import edu.kit.valaris.menu.gui.*;
import edu.kit.valaris.menu.menuconfig.GraphicsConfig;
import edu.kit.valaris.tick.ItemType;


/**
 * @author Artur Wesner
 */
public class MenuMain extends BaseApplication {

    public static void main(String[] args) {
        MenuMain app = new MenuMain();

        AppSettings settings = new AppSettings(true);
        settings.put("Width", 1280);
        settings.put("Height", 720);
        settings.put("Title", "Valaris");
        settings.put("VSync", true);
        settings.put("Samples", 16);
        GraphicsConfig.setIcons(settings);
        app.setSettings(settings);
        app.start();
    }

    /**
     * Attaches base {@link AppState}s
     */
    @Override
    protected void init() {
        /* normal start

        SplashScreen splash = new SplashScreen(stateManager, stateManager.getApplication());
        MainmenuScreen mainmenu = new MainmenuScreen(stateManager, stateManager.getApplication());
        SinglePlayerModeScreen single = new SinglePlayerModeScreen(stateManager, stateManager.getApplication());
        stateManager.attach(new NiftyAppState("MainnSingle", splash, mainmenu, single));
        */

        Hud hud = new Hud(stateManager, stateManager.getApplication());
        stateManager.attach(new NiftyAppState("load", hud));
        //hud.updateItem(ItemType.GRAVITATION_TRAP.name());
    }


}
