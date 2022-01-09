package edu.kit.valaris.menu;

import com.jme3.system.AppSettings;
import com.jme3.system.JmeContext;
import de.lessvoid.nifty.input.NiftyStandardInputEvent;
import edu.kit.valaris.BaseApplication;
import edu.kit.valaris.menu.gui.MainmenuScreen;
import edu.kit.valaris.menu.gui.NiftyAppState;
import edu.kit.valaris.menu.gui.SinglePlayerModeScreen;
import edu.kit.valaris.menu.menuconfig.GraphicsConfig;
import edu.kit.valaris.menu.menuconfig.InternalGameConfig;
import edu.kit.valaris.menu.menuconfig.SeedConfig;
import org.junit.*;

public class TestMainmenu {
    public static BaseApplication app;
    public static MainmenuScreen mainmenuScreen;
    public static SinglePlayerModeScreen single;
    public static NiftyAppState mainNiftyAppState;

    @SuppressWarnings("all")
    @BeforeClass
    public static void beforeClass() {
        app = new BaseApplication() {
            @Override
            protected void init() {
            }
        };
        AppSettings settings = new AppSettings(true);
        settings.setUseInput(false);
        app.setSettings(settings);
        app.start(JmeContext.Type.Headless);
        app.initialize();
        SeedConfig.reset();
        InternalGameConfig.reset();
        GraphicsConfig.reset();
        mainmenuScreen = new MainmenuScreen(app.getStateManager(), app);
        single = new SinglePlayerModeScreen(app.getStateManager(), app);
        mainNiftyAppState = new NiftyAppState("Test1", mainmenuScreen, single);
        mainNiftyAppState.initialize(app.getStateManager(), app);
        app.getStateManager().attach(mainNiftyAppState);
    }

    @AfterClass
    public static void tearDownClass() {
        try {
            app.stop();
        } catch (Exception ex) {

        }
    }

    @Test
    public void testConstructor() {
        Assert.assertEquals("Screen ID wrong", MainmenuScreen.m_screenID, mainmenuScreen.getScreenID());
        Assert.assertEquals("XML Path wrong", MainmenuScreen.m_xmlPath, mainmenuScreen.getScreenXMLPath());
    }

    @Test
    public void testKeyEvent() {
        Assert.assertTrue(mainmenuScreen.keyEvent(NiftyStandardInputEvent.Escape));
    }
}
