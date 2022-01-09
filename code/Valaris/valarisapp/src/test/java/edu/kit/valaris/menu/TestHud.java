package edu.kit.valaris.menu;

import com.jme3.system.AppSettings;
import com.jme3.system.JmeContext;
import de.lessvoid.nifty.elements.render.TextRenderer;
import edu.kit.valaris.BaseApplication;
import edu.kit.valaris.menu.gui.Hud;
import edu.kit.valaris.menu.gui.NiftyAppState;
import edu.kit.valaris.tick.ItemType;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Artur Wesner
 */
public class TestHud {
    public static BaseApplication app;
    public static Hud hud;
    public static NiftyAppState hudNiftyAppState;

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
        hud = new Hud(app.getStateManager(), app);
        hudNiftyAppState = new NiftyAppState("Test1", hud);
        hudNiftyAppState.initialize(app.getStateManager(), app);
        app.getStateManager().attach(hudNiftyAppState);
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
        Assert.assertEquals("Screen ID wrong", Hud.m_screenID, hud.getScreenID());
        Assert.assertEquals("XML Path wrong", Hud.m_xmlPath, hud.getScreenXMLPath());
    }

    @Test
    public void testUpdateRank() {
        hud.updateRank(2);
        Assert.assertEquals("Rank not set", "2",
                hud.getScreen().findElementById("currentRank").getRenderer(TextRenderer.class).getOriginalText());
    }

    @Test
    public void testUpdateTime() {
        hud.updateTime(20);
        Assert.assertEquals("Time not set", "00:20",
                hud.getScreen().findElementById("timer").getRenderer(TextRenderer.class).getOriginalText());
    }

    @Test
    public void testUpdateCountdown() {
        hud.updateCountdown("1");
        Assert.assertEquals("Countdown not set", "1",
                hud.getScreen().findElementById("countdown").getRenderer(TextRenderer.class).getOriginalText());
    }

    @Test
    public void testUpdateItem() {
        hud.updateItem(ItemType.HOMING_MISSILE.name());
        Assert.assertEquals("Item not set", true,
                hud.getScreen().findElementById("rocketItem").isVisible());
    }
}
