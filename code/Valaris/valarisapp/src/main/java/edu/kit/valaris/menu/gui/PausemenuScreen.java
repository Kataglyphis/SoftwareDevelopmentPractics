package edu.kit.valaris.menu.gui;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.input.NiftyInputEvent;
import de.lessvoid.nifty.input.NiftyStandardInputEvent;
import de.lessvoid.nifty.screen.KeyInputHandler;
import de.lessvoid.nifty.screen.Screen;
import edu.kit.valaris.BaseApplication;
import edu.kit.valaris.InputHandler;
import edu.kit.valaris.RaceAppState;
import edu.kit.valaris.dummysim.SimulationDummy;
import edu.kit.valaris.menu.menuconfig.SeedConfig;

import java.util.Locale;
import javax.annotation.Nonnull;

/**
 * This class controls the graphical user interface of the pause menu screen.
 * This includes the provision of information which screen is currently opened.
 * In order to do this the current screen will be attached/detached to/of the StateManager.
 * It inherits OwnScreenController to control the Nifty Screens and implements the Interface KeyInputHandler for
 * being able to react to User Input and Actions.
 * @author Artur Wesner
 */
public class PausemenuScreen extends OwnScreenController implements KeyInputHandler {
    /**
     * Path where the correspondending .xml file is located.
     */
    public static final String m_xmlPath = "edu.kit.valaris/menu/Pausemenu.xml";

    /**
     * The screem id which is defined in the .xml file.
     */
    public static final String m_screenID = "pausemenu";

    /**
     * Constructor calling the super constructor and initializing the private variables.
     * @param stateManager The application's StateManager.
     * @param app The application.
     */
    public PausemenuScreen(AppStateManager stateManager, Application app) {
        super(m_screenID, m_xmlPath);
        m_app = (BaseApplication) app;
        m_stateManager = stateManager;
    }

    /**
     * Goes back to the game by detaching the current NiftyAppState
     */
    public void backtoGame() {
        m_app.enqueue(() -> {
            if (m_app.getInputManager() != null) {
                m_app.getInputManager().setCursorVisible(false);
            }
            m_stateManager.detach(getNiftyAppState());

            //unpause simulation
            if (m_stateManager.getState(SimulationDummy.class) != null) {
                m_stateManager.getState(SimulationDummy.class).setEnabled(true);
            }

            //unpause other teams simulation
            if(m_stateManager.getState(RaceAppState.class) != null) {
                m_stateManager.getState(RaceAppState.class).setEnabled(true);
            }

            //unpause input handler
            if(m_stateManager.getState(InputHandler.class) != null) {
                m_stateManager.getState(InputHandler.class).setEnabled(true);
            }
        });
    }

    /**
     * Opens the settings menu.
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
     * Saves the current seed.
     */
    public void saveSeed() {
        if (m_seedConfig.containsValue(SeedConfig.getCurrentSeed().getName())
                || m_seedConfig.containsValue(SeedConfig.getCurrentSeed().getName()
                + "#" + SeedConfig.getCurrentSeed().getRoadType())) {
            createPopupMessage(m_bundleInfo.getString("seedalreadysaved",
                    Locale.forLanguageTag(m_graphicsConfig.getProperty("language"))));
            return;
        }
        if (SeedConfig.addSeed(SeedConfig.getCurrentSeed())) {
            createPopupMessage(m_bundleInfo.getString("successfullSave",
                    Locale.forLanguageTag(m_graphicsConfig.getProperty("language")))
                    + SeedConfig.getCurrentSeed().getName() + "#" + SeedConfig.getCurrentSeed().getRoadType());
        } else {
            createPopupMessage(m_bundleInfo.getString("notsuccessfullSave",
                    Locale.forLanguageTag(m_graphicsConfig.getProperty("language"))));
        }
    }

    /**
     * Opens the main menu. Checks if the main menu is in the same appstate as the pause menu
     * and then calls goToScreen(). If its not the case then it attaches a new NiftyAppState with a new main
     * menu controller and then detaches the current NiftyAppState.
     */
    @SuppressWarnings("all")
    public void backToMainmenu() {
        m_app.enqueue(() -> {
            for (OwnScreenController current : getOtherController()) {
                if (current.getScreenID().equals(MainmenuScreen.m_screenID)) {
                    m_nifty.gotoScreen(MainmenuScreen.m_screenID);
                    return;
                }
            }
            MainmenuScreen mainmenuScreen = new MainmenuScreen(m_stateManager, m_app);
            SinglePlayerModeScreen single = new SinglePlayerModeScreen(m_stateManager, m_app);
            m_stateManager.detach(getNiftyAppState());

            LoadingUtil lu = new LoadingUtil(m_app, m_stateManager);
            lu.shutDownAndSwitchScreen("MainnSingle", mainmenuScreen, single);
        });
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
     * called right after the onStartScreen event ENDED.
     */
    @Override
    public void onStartScreen() {
        if (m_app.getInputManager() != null) {
            m_app.getInputManager().setCursorVisible(true);
        }
    }

    /**
     * called right after the onEndScreen event ENDED.
     */
    @Override
    public void onEndScreen() {
    }

    /**
     * handle a key event.
     *
     * @param inputEvent key event to handle
     * @return true when the event has been consumend and false if not
     */
    @Override
    public boolean keyEvent(@Nonnull NiftyInputEvent inputEvent) {
        if (inputEvent.equals(NiftyStandardInputEvent.Escape)) {
            backtoGame();
            return true;
        }
        return false;
    }

}
