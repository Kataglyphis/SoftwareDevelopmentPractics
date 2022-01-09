package edu.kit.valaris.menu.gui;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.screen.Screen;
import edu.kit.valaris.BaseApplication;
import javax.annotation.Nonnull;

/**
 * @author Artur Wesner
 */
public class SplashScreen extends OwnScreenController {
    /**
     * Path where the correspondending .xml file is located.
     */
    public static final String m_xmlPath = "edu.kit.valaris/menu/Splash.xml";

    /**
     * The screem id which is defined in the .xml file.
     */
    public static final String m_screenID = "splash";

    /**
     * Constructor calling the super constructor and initializing the protected variables.
     * @param stateManager The application's StateManager.
     * @param app The application.
     */
    public SplashScreen(AppStateManager stateManager, Application app) {
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
        m_nifty.gotoScreen(MainmenuScreen.m_screenID);
    }

    /**
     * called right after the onEndScreen event ENDED.
     */
    @Override
    public void onEndScreen() {
        super.onEndScreen();
    }
}
