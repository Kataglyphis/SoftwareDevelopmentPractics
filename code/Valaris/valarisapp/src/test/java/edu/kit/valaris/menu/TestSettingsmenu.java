package edu.kit.valaris.menu;

import com.jme3.system.AppSettings;
import com.jme3.system.JmeContext;
import de.lessvoid.nifty.controls.*;
import edu.kit.valaris.BaseApplication;
import edu.kit.valaris.menu.gui.MainmenuScreen;
import edu.kit.valaris.menu.gui.NiftyAppState;
import edu.kit.valaris.menu.gui.PausemenuScreen;
import edu.kit.valaris.menu.gui.SettingsmenuScreen;
import edu.kit.valaris.menu.menuconfig.GraphicsConfig;
import edu.kit.valaris.menu.menuconfig.InternalGameConfig;
import edu.kit.valaris.menu.menuconfig.SeedConfig;
import org.junit.*;
import org.lwjgl.opengl.Display;

import java.util.Properties;

/**
 * @author Artur Wesner
 */
public class TestSettingsmenu {
    public static BaseApplication app;
    public static SettingsmenuScreen settingsmenuScreen;
    public static NiftyAppState settingsNiftyAppState;

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
        settingsmenuScreen = new SettingsmenuScreen(app.getStateManager(), app, MainmenuScreen.m_screenID);
        settingsNiftyAppState = new NiftyAppState("Test1", settingsmenuScreen);
        settingsNiftyAppState.initialize(app.getStateManager(), app);
        app.getStateManager().attach(settingsNiftyAppState);
    }

    @AfterClass
    public static void tearDownClass() {
        try {
            settingsNiftyAppState.cleanup();
            app.stop();
        } catch (Exception ex) {

        }
        GraphicsConfig.reset();
        InternalGameConfig.reset();
    }

    @Test
    public void testConstructor() {
        Assert.assertEquals("Screen ID wrong", SettingsmenuScreen.m_screenID, settingsmenuScreen.getScreenID());
        Assert.assertEquals("XML Path wrong", SettingsmenuScreen.m_xmlPath, settingsmenuScreen.getScreenXMLPath());
    }

    @SuppressWarnings("all")
    @Test
    public void testChangingResolution() {
        settingsmenuScreen.getScreen().findNiftyControl("resolutionDro"
                + "pDown", DropDown.class).selectItem("800 x 600");
        DropDownSelectionChangedEvent event =
                new DropDownSelectionChangedEvent(settingsmenuScreen.getScreen().findNiftyControl("resol"
                        + "utionDropDown", DropDown.class), "800 x 600", 2);
        settingsmenuScreen.editCurrentResolution(settingsmenuScreen.getScreen().findNiftyControl("resol"
                + "utionDropDown", DropDown.class).getId(), event);
        Assert.assertEquals("Resolution not correctly set", "800 x 600", GraphicsConfig.getConfig().
                getProperty("resolution"));
    }

    @SuppressWarnings("all")
    @Test
    public void testChangingLaguage() {
        settingsmenuScreen.getScreen().findNiftyControl("languageDro"
                + "pDown", DropDown.class).selectItemByIndex(0);
        DropDownSelectionChangedEvent langevent =
                new DropDownSelectionChangedEvent(settingsmenuScreen.getScreen().findNiftyControl("langu"
                        + "ageDropDown", DropDown.class), "Deutsch", 0);
        settingsmenuScreen.editCurrentLanguage(settingsmenuScreen.getScreen().findNiftyControl("langu"
                + "ageDropDown", DropDown.class).getId(), langevent);
        Assert.assertEquals("Language not correctly set", "de", GraphicsConfig.getConfig().
                getProperty("language"));
    }

    @SuppressWarnings("all")
    @Test
    public void testChangingKIDiff() {
        settingsmenuScreen.getScreen().findNiftyControl("kidiffDro"
                + "pDown", DropDown.class).selectItemByIndex(0);
        DropDownSelectionChangedEvent kidiffevent =
                new DropDownSelectionChangedEvent(settingsmenuScreen.getScreen().findNiftyControl("kidiff"
                        + "DropDown", DropDown.class), "Einfach", 0);
        settingsmenuScreen.editCurrentKIDifficulty(settingsmenuScreen.getScreen().findNiftyControl("kidiff"
                + "DropDown", DropDown.class).getId(), kidiffevent);
        Assert.assertEquals("KI Diff not correctly set", "easy", InternalGameConfig.getConfig().
                getProperty("KIDifficulty"));
    }

    @SuppressWarnings("all")
    @Test
    public void testChangingQuality() {
        settingsmenuScreen.getScreen().findNiftyControl("qualityDro"
                + "pDown", DropDown.class).selectItemByIndex(0);
        DropDownSelectionChangedEvent kidiffevent =
                new DropDownSelectionChangedEvent(settingsmenuScreen.getScreen().findNiftyControl("quality"
                        + "DropDown", DropDown.class), "Niedrig", 0);
        settingsmenuScreen.editCurrentQuality(settingsmenuScreen.getScreen().findNiftyControl("quality"
                + "DropDown", DropDown.class).getId(), kidiffevent);
        Assert.assertEquals("Quality not correctly set", "low", GraphicsConfig.getConfig().
                getProperty("quality"));
    }

    @SuppressWarnings("all")
    @Test
    public void testChangingFullscreen() {
        settingsmenuScreen.getScreen().findNiftyControl("fullscreenCheckbox",
                CheckBox.class).setChecked(true);
        CheckBoxStateChangedEvent fullscreenevent =
                new CheckBoxStateChangedEvent(settingsmenuScreen.getScreen().findNiftyControl("fullscreen" +
                        "Checkbox", CheckBox.class), true);
        settingsmenuScreen.toggleFullscreen(settingsmenuScreen.getScreen().findNiftyControl("fullscreen" +
                "Checkbox", CheckBox.class).getId(), fullscreenevent);
        Assert.assertEquals("Fullscreen not correctly set", true, Display.isFullscreen());
    }

    @Test
    public void showGraphicsTab() {
        settingsmenuScreen.showGraphicsTab();
        Assert.assertTrue( settingsmenuScreen.getScreen().findElementById("graphicsTab").isVisible());
    }

    @Test
    public void showInternTab() {
        settingsmenuScreen.showInternTab();
        Assert.assertTrue( settingsmenuScreen.getScreen().findElementById("internTab").isVisible());
    }

    @Test
    public void resetSettings() {
        settingsmenuScreen.getScreen().findNiftyControl("qualityDro"
                + "pDown", DropDown.class).selectItemByIndex(0);
        DropDownSelectionChangedEvent kidiffevent =
                new DropDownSelectionChangedEvent(settingsmenuScreen.getScreen().findNiftyControl("quality"
                        + "DropDown", DropDown.class), "Niedrig", 0);
        settingsmenuScreen.editCurrentQuality(settingsmenuScreen.getScreen().findNiftyControl("quality"
                + "DropDown", DropDown.class).getId(), kidiffevent);
        Properties graph = GraphicsConfig.getConfig();
        Properties intern = InternalGameConfig.getConfig();
        settingsmenuScreen.resetSettings();
        Properties newgraph = GraphicsConfig.getConfig();
        Properties newintern = InternalGameConfig.getConfig();
        Assert.assertFalse(graph.equals(newgraph));
        Assert.assertTrue(intern.equals(newintern));
    }

    @Test
    public void testOnStartScreen() {
        settingsmenuScreen.setLastScreen(PausemenuScreen.m_screenID);
        settingsmenuScreen.onStartScreen();
        Assert.assertFalse(settingsmenuScreen.getScreen().findElementById("kidiffDropDown").isEnabled());
    }
}
