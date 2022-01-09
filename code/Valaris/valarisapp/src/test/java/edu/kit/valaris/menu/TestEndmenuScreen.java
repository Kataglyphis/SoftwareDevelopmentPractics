package edu.kit.valaris.menu;

import com.jme3.math.ColorRGBA;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeContext;
import de.lessvoid.nifty.elements.render.PanelRenderer;
import de.lessvoid.nifty.elements.render.TextRenderer;
import edu.kit.valaris.BaseApplication;
import edu.kit.valaris.menu.gui.EndmenuScreen;
import edu.kit.valaris.menu.gui.NiftyAppState;
import edu.kit.valaris.menu.menuconfig.GraphicsConfig;
import edu.kit.valaris.menu.menuconfig.InternalGameConfig;
import edu.kit.valaris.menu.menuconfig.SeedConfig;
import edu.kit.valaris.menu.menudatastructures.SeedEntry;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.*;

/**
 * @author Artur Wesner
 */
public class TestEndmenuScreen {
    public static BaseApplication app;
    public static EndmenuScreen endmenuScreen;
    public static NiftyAppState endNiftyAppState;

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
        endmenuScreen = new EndmenuScreen(app.getStateManager(), app, null);
        endNiftyAppState = new NiftyAppState("Test1", endmenuScreen);
        endNiftyAppState.initialize(app.getStateManager(), app);
        app.getStateManager().attach(endNiftyAppState);
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
        Assert.assertEquals("Screen ID wrong", EndmenuScreen.m_screenID, endmenuScreen.getScreenID());
        Assert.assertEquals("XML Path wrong", EndmenuScreen.m_xmlPath, endmenuScreen.getScreenXMLPath());
    }

    @Test
    public void testSaveSeed() {
        SeedConfig.setCurrentSeed(new SeedEntry("JUnit2"));
        endmenuScreen.saveSeed();
        Properties test = SeedConfig.getConfig();
        Assert.assertEquals("Seed not saved", true, SeedConfig.getConfig().containsKey("JUnit2#normal"));
    }
}
