package edu.kit.valaris.menu.gui;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.input.NiftyInputEvent;
import de.lessvoid.nifty.input.NiftyStandardInputEvent;
import de.lessvoid.nifty.screen.KeyInputHandler;
import de.lessvoid.nifty.screen.Screen;
import edu.kit.valaris.BaseApplication;
import javax.annotation.Nonnull;

/**
 * This class controls the graphical user interface of the main menu Screen.
 * This includes the provision of information which screen is currently opened.
 * In order to do this the current screen will be attached/detached to/of the StateManager.
 * It inherits OwnScreenController to control the Nifty Screens and implements the Interface KeyInputHandler for
 * being able to react to User Input and Actions.
 * @author Artur Wesner
 */
public class MainmenuScreen extends OwnScreenController implements KeyInputHandler {
    /**
     * Path where the corresponding .xml file is located.
     */
    public static final String m_xmlPath = "edu.kit.valaris/menu/Mainmenu.xml";

    /**
     * The screen id which is defined in the .xml file.
     */
    public static final String m_screenID = "mainmenu";

    /**
     * Constructor calling the super constructor with his static variables and setting the private references.
     * @param stateManager The applications statemanager.
     * @param app The application.
     */
    public MainmenuScreen(AppStateManager stateManager, Application app) {
        super(m_screenID, m_xmlPath);
        m_app = (BaseApplication) app;
        m_stateManager = stateManager;
    }

    /**
     * Opens the single player menu. Checks if the single player menu is in the same appstate as the main menu
     * and then calls goToScreen(). If its not the case then it attaches a new NiftyAppState with a new single player
     * menu controller and then detaches the current NiftyAppState.
     */
    public void openSinglePlayerMode() {
        m_app.enqueue(() -> {
            for (OwnScreenController current : getOtherController()) {
                if (current.getScreenID().equals(SinglePlayerModeScreen.m_screenID)) {
                    m_nifty.gotoScreen(SinglePlayerModeScreen.m_screenID);
                    return;
                }
            }
            m_stateManager.detach(getNiftyAppState());
            SinglePlayerModeScreen singleScreen = new SinglePlayerModeScreen(m_stateManager, m_app);
            m_stateManager.attach(new NiftyAppState("Single", singleScreen));
        });
    }

    /**
     * Opens the settings menu. Checks if the settings menu is in the same appstate as the main menu
     * and then calls goToScreen(). If its not the case then it attaches a new NiftyAppState with a new settings
     * menu controller and then detaches the current NiftyAppState.
     */
    public void openSettingsmenu() {
        m_app.enqueue(() -> {
            for (OwnScreenController current : getOtherController()) {
                if (current.getScreenID().equals(SettingsmenuScreen.m_screenID)) {
                    m_nifty.gotoScreen(SettingsmenuScreen.m_screenID);
                    return;
                }
            }
            m_stateManager.detach(getNiftyAppState());
            SettingsmenuScreen settingsmenuScreen = new SettingsmenuScreen(m_stateManager, m_app, m_screen.getScreenId());
            m_stateManager.attach(new NiftyAppState("Settings", settingsmenuScreen));
        });
    }

    /**
     * Terminates the application.
     */
    public void exit() {
        m_app.stop();
    }

    /**
     * Opens the credits popup.
     */
    public void openCredits() {
        m_popup = m_nifty.createPopup("creditsPopup");
        m_nifty.showPopup(m_screen, m_popup.getId(), null);
    }

    /**
     * Bind this ScreenController to a screen. This happens
     * right before the onStartScreen STARTED and only exactly once for a screen!
     *
     * @param nifty  nifty
     * @param screen screen
     */
    @Override
    public void bind(@Nonnull Nifty nifty, @Nonnull Screen screen) {
        super.bind(nifty, screen);

    }

    /**
     * handle a key event.
     *
     * @param inputEvent key event to handle
     * @return true when the event has been consumend and false if not
     */
    @Override
    public boolean keyEvent(NiftyInputEvent inputEvent) {
        if (inputEvent.equals(NiftyStandardInputEvent.Escape)) {
            exit();
            return true;
        }
        return false;
    }

    /**
     * called right after the onEndScreen event ENDED.
     */
    @Override
    public void onEndScreen() {
    }
}
