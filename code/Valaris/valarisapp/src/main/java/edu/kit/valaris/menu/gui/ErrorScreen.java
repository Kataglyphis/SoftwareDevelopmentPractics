package edu.kit.valaris.menu.gui;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.input.NiftyInputEvent;
import de.lessvoid.nifty.screen.KeyInputHandler;
import de.lessvoid.nifty.screen.Screen;
import edu.kit.valaris.BaseApplication;
import javax.annotation.Nonnull;

/**
 * Error Screen for showing exceptions or other stuff on the screen.
 * @author Artur Wesner
 */
public class ErrorScreen extends OwnScreenController implements KeyInputHandler {
    /**
     * Path where the correspondending .xml file is located.
     */
    public static final String m_xmlPath = "edu.kit.valaris/menu/ErrorScreen.xml";

    /**
     * The screem id which is defined in the .xml file.
     */
    public static final String m_screenID = "errorscreen";

    /**
     * Constructor calling the super constructor and initializing the protected variables.
     * @param stateManager The application's StateManager.
     * @param app The application.
     */
    public ErrorScreen(AppStateManager stateManager, Application app) {
        super(m_screenID, m_xmlPath);
        m_app = (BaseApplication) app;
        m_stateManager = stateManager;
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
        super.onStartScreen();
    }

    /**
     * called right after the onEndScreen event ENDED.
     */
    @Override
    public void onEndScreen() {
        super.onEndScreen();
    }

    /**
     * handle a key event.
     *
     * @param inputEvent key event to handle
     * @return true when the event has been consumend and false if not
     */
    @Override
    public boolean keyEvent(@Nonnull NiftyInputEvent inputEvent) {
        return false;
    }
}
