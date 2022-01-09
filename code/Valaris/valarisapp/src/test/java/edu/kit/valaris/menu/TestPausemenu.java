package edu.kit.valaris.menu;

import com.jme3.system.AppSettings;
import com.jme3.system.JmeContext;
import de.lessvoid.nifty.input.NiftyStandardInputEvent;
import edu.kit.valaris.BaseApplication;
import edu.kit.valaris.menu.gui.NiftyAppState;
import edu.kit.valaris.menu.gui.PausemenuScreen;
import edu.kit.valaris.menu.menuconfig.GraphicsConfig;
import edu.kit.valaris.menu.menuconfig.InternalGameConfig;
import edu.kit.valaris.menu.menuconfig.SeedConfig;
import edu.kit.valaris.menu.menudatastructures.SeedEntry;
import org.junit.*;

import java.util.Properties;

/**
 * @author Artur Wesner
 */
public class TestPausemenu {
    public static BaseApplication app;
    public static PausemenuScreen pausemenuScreen;
    public static NiftyAppState pauseNiftyAppState;


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
        pausemenuScreen = new PausemenuScreen(app.getStateManager(), app);
        pauseNiftyAppState = new NiftyAppState("Test1", pausemenuScreen);
        pauseNiftyAppState.initialize(app.getStateManager(), app);
        app.getStateManager().attach(pauseNiftyAppState);
    }

    @AfterClass
    public static void tearDownClass() {
        try {
            app.stop();
        } catch (Exception ex) {

        }
        SeedConfig.reset();
        InternalGameConfig.reset();
        GraphicsConfig.reset();
    }

    @Test
    public void testConstructor() {
        Assert.assertEquals("Screen ID wrong", PausemenuScreen.m_screenID, pausemenuScreen.getScreenID());
        Assert.assertEquals("XML Path wrong", PausemenuScreen.m_xmlPath, pausemenuScreen.getScreenXMLPath());
    }

    @Test
    public void testSaveSeed() {
        SeedConfig.setCurrentSeed(new SeedEntry("JUnit", "JUnit", "small"));
        pausemenuScreen.saveSeed();
        Properties test = SeedConfig.getConfig();
        Assert.assertEquals("Seed not saved", true, SeedConfig.getConfig().containsKey("JUnit#small"));
    }

    @Test
    public void testKeyEvent() {
        Assert.assertTrue(pausemenuScreen.keyEvent(NiftyStandardInputEvent.Escape));
    }
}
