package edu.kit.valaris.menu;

import com.jme3.system.AppSettings;
import com.jme3.system.JmeContext;
import de.lessvoid.nifty.Nifty;
import edu.kit.valaris.BaseApplication;
import edu.kit.valaris.menu.gui.MainmenuScreen;
import edu.kit.valaris.menu.gui.NiftyAppState;
import edu.kit.valaris.menu.gui.SinglePlayerModeScreen;
import edu.kit.valaris.menu.menuconfig.GraphicsConfig;
import edu.kit.valaris.menu.menuconfig.InternalGameConfig;
import edu.kit.valaris.menu.menuconfig.SeedConfig;
import edu.kit.valaris.rendering.ViewPortManager;
import org.junit.*;

/**
 * @author Artur Wesner
 */
public class TestNiftyAppState {
    public static BaseApplication app;

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
    }

    @AfterClass
    public static void tearDownClass() {
        try {
            app.stop();
        } catch (Exception ex) {

        }
    }

    @Test
    public void testConstructorWithOneOwnScreenController() {
        MainmenuScreen mainmenuScreen = new MainmenuScreen(app.getStateManager(), app);
        NiftyAppState niftyNiftyAppState = new NiftyAppState("Test1", mainmenuScreen);
        niftyNiftyAppState.initialize(app.getStateManager(), app);
        Nifty nifty = mainmenuScreen.getNifty();
        Assert.assertEquals("NiftyAppState not correctly initialized", niftyNiftyAppState, mainmenuScreen.getNiftyAppState());
        Assert.assertEquals("Viewport Name not correct", "NiftyTest1", app.getStateManager().getState(ViewPortManager.class).getViewPort(2).getName());
        Assert.assertEquals("NiftyAppState reference wrong", niftyNiftyAppState, mainmenuScreen.getNiftyAppState());
        Assert.assertEquals("Locale not de", GraphicsConfig.getConfig().getProperty("language"), nifty.getLocale().getLanguage());
        Assert.assertEquals("Current screen is not main menu", MainmenuScreen.m_screenID, nifty.getCurrentScreen().getScreenId());
        Assert.assertEquals("There should only be one OwnScreencontroller", 1, mainmenuScreen.getOtherController().length);
        niftyNiftyAppState.cleanup();
    }

    @Test
    public void testConstructorWithTwoOwnScreenController() {
        MainmenuScreen mainmenuScreen = new MainmenuScreen(app.getStateManager(), app);
        SinglePlayerModeScreen singlePlayerModeScreen = new SinglePlayerModeScreen(app.getStateManager(), app);
        NiftyAppState twoNiftyAppState = new NiftyAppState("Test2", mainmenuScreen, singlePlayerModeScreen);
        twoNiftyAppState.initialize(app.getStateManager(), app);
        Nifty mainNifty = mainmenuScreen.getNifty();
        Assert.assertEquals("Both screens should hve the same reference to all screens", mainmenuScreen.getOtherController(), singlePlayerModeScreen.getOtherController());
        Assert.assertEquals("NiftyAppstate should be the same for both screens", mainmenuScreen.getNiftyAppState(), singlePlayerModeScreen.getNiftyAppState());
        Assert.assertEquals("Current screen should be the first screen given to niftyappstate", MainmenuScreen.m_screenID, mainNifty.getCurrentScreen().getScreenId());
        twoNiftyAppState.cleanup();
    }

    @Test
    public void testCleanup() {
        MainmenuScreen mainmenuScreen = new MainmenuScreen(app.getStateManager(), app);
        NiftyAppState oneNiftyAppState = new NiftyAppState("Test3", mainmenuScreen);
        oneNiftyAppState.initialize(app.getStateManager(), app);
        oneNiftyAppState.cleanup();
        Assert.assertEquals("Cleanup not successfull", null, app.getStateManager().getState(ViewPortManager.class).getViewPort(2));
    }
}