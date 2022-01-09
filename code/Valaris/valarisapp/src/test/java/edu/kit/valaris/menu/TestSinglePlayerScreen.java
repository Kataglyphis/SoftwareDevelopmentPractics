package edu.kit.valaris.menu;

import com.jme3.system.AppSettings;
import com.jme3.system.JmeContext;
import de.lessvoid.nifty.controls.DropDown;
import de.lessvoid.nifty.controls.DropDownSelectionChangedEvent;
import de.lessvoid.nifty.controls.TextField;
import de.lessvoid.nifty.controls.TextFieldChangedEvent;
import edu.kit.valaris.BaseApplication;
import edu.kit.valaris.menu.gui.NiftyAppState;
import edu.kit.valaris.menu.gui.SinglePlayerModeScreen;
import edu.kit.valaris.menu.menuconfig.GraphicsConfig;
import edu.kit.valaris.menu.menuconfig.InternalGameConfig;
import edu.kit.valaris.menu.menuconfig.SeedConfig;
import edu.kit.valaris.menu.menudatastructures.SeedEntry;
import org.junit.*;

import java.util.*;

/**
 * @author Artur Wesner
 */
public class TestSinglePlayerScreen {
    public static BaseApplication app;
    public static SinglePlayerModeScreen singleScreen;
    public static NiftyAppState singleNiftyAppState;

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
        singleScreen = new SinglePlayerModeScreen(app.getStateManager(), app);
        singleNiftyAppState = new NiftyAppState("Test1", singleScreen);
        singleNiftyAppState.initialize(app.getStateManager(), app);
        app.getStateManager().attach(singleNiftyAppState);
    }

    @AfterClass
    public static void tearDownClass() {
        try {
            singleNiftyAppState.cleanup();
            app.stop();
        } catch (Exception ex) {

        }
        GraphicsConfig.reset();
        InternalGameConfig.reset();
        SeedConfig.reset();
    }

    @Test
    public void testConstructor() {
        Assert.assertEquals("Screen ID wrong", SinglePlayerModeScreen.m_screenID, singleScreen.getScreenID());
        Assert.assertEquals("XML Path wrong", SinglePlayerModeScreen.m_xmlPath, singleScreen.getScreenXMLPath());
    }

    @SuppressWarnings("all")
    @Test
    public void testEditCurrentSeed() {
        Map<String, String> m_allSeeds = new HashMap<>();
        for (Map.Entry<Object, Object> entry : SeedConfig.getConfig().entrySet()) {
            m_allSeeds.put((String) entry.getKey(), (String) entry.getValue());
        }
        List<String> sortedSeedEntries = new LinkedList<>(m_allSeeds.values());
        Collections.sort(sortedSeedEntries);
        singleScreen.getScreen().findNiftyControl("seedDro"
                + "pDown", DropDown.class).selectItemByIndex(1);
        DropDownSelectionChangedEvent event =
                new DropDownSelectionChangedEvent(singleScreen.getScreen().findNiftyControl("seed"
                        + "DropDown", DropDown.class), (((LinkedList<String>) sortedSeedEntries).getFirst()), 1);
        singleScreen.editCurrentSeed(singleScreen.getScreen().findNiftyControl("seed"
                + "DropDown", DropDown.class).getId(), event);
        Assert.assertEquals("Seed not correctly set", ((LinkedList<String>) sortedSeedEntries).getFirst(),
                SeedConfig.getCurrentSeed().getName());
    }

    @Test
    public void testEditCurrentSeedNumber() {
        TextFieldChangedEvent textChanged = new TextFieldChangedEvent(singleScreen.getScreen().
                findNiftyControl("editSeedNumber", TextField.class), "123");
        singleScreen.editCurrentSeedNumber("editSeedNumber", textChanged);
        Assert.assertEquals("Dropdown selecetion should be empty", "",
                singleScreen.getScreen().findNiftyControl("seedDropDown", DropDown.class).getSelection());
        Assert.assertEquals("Save seed number button should be visible", true,
                singleScreen.getScreen().findElementById("saveSeedNumberButton").isVisible());
    }

    @Test
    public void testEditKICount() {
        TextFieldChangedEvent textChanged = new TextFieldChangedEvent(singleScreen.getScreen().
                findNiftyControl("editKICount", TextField.class), "9");
        singleScreen.editKICount("editKICount", textChanged);
        Assert.assertEquals("KICount not correctly saved", "9",
                InternalGameConfig.getConfig().getProperty("KICount"));
    }

    @Test
    public void testSaveSeedName() {
        SeedConfig.setCurrentSeed(new SeedEntry("JUnit", "123", "small"));
        SeedConfig.addSeed(new SeedEntry("JUnit", "123", "small"));
        singleScreen.getScreen().findNiftyControl("editSeedNameField", TextField.class).setText("JUnitt");
        singleScreen.saveSeedName();
        Assert.assertEquals("Seed name not saved", "JUnitt", SeedConfig.getConfig().
                getProperty("123#small"));
        Assert.assertEquals("Save seed name button should not be visible", false,
                singleScreen.getScreen().findElementById("saveSeedNameButton").isVisible());
        Assert.assertEquals("Edit seed name button should be visible", true,
                singleScreen.getScreen().findElementById("editSeedNameButton").isVisible());
        Assert.assertEquals("Dropdown should have selected the new changed name", "JUnitt",
            singleScreen.getScreen().findNiftyControl("seedDropDown", DropDown.class).getSelection().toString());
    }

    @SuppressWarnings("all")
    @Test
    public void testEditSeednameVisibility() {
        Map<String, String> m_allSeeds = new HashMap<>();
        for (Map.Entry<Object, Object> entry : SeedConfig.getConfig().entrySet()) {
            m_allSeeds.put((String) entry.getKey(), (String) entry.getValue());
        }
        List<String> sortedSeedEntries = new LinkedList<>(m_allSeeds.values());
        Collections.sort(sortedSeedEntries);
        singleScreen.getScreen().findNiftyControl("seedDro"
                + "pDown", DropDown.class).selectItemByIndex(1);
        DropDownSelectionChangedEvent event =
                new DropDownSelectionChangedEvent(singleScreen.getScreen().findNiftyControl("seed"
                        + "DropDown", DropDown.class), (((LinkedList<String>) sortedSeedEntries).getFirst()), 1);
        singleScreen.editCurrentSeed(singleScreen.getScreen().findNiftyControl("seed"
                + "DropDown", DropDown.class).getId(), event);
        singleScreen.editSeednameTextfieldVisibility();
        Assert.assertEquals("Textfield should have the name of the seed",
                ((LinkedList<String>) sortedSeedEntries).getFirst(),
                singleScreen.getScreen().findNiftyControl("editSeedNameField", TextField.class).getRealText());
        Assert.assertEquals("Save seed name button should not be visible", true,
                singleScreen.getScreen().findElementById("saveSeedNameButton").isVisible());
        Assert.assertEquals("Edit seed name button should be visible", false,
                singleScreen.getScreen().findElementById("editSeedNameButton").isVisible());
    }

    @Test
    public void testSaveSeedNumber() {
        singleScreen.getScreen().findNiftyControl("editSeedNumber", TextField.class).setText("12");
        singleScreen.saveSeedNumber();
        Assert.assertEquals("Seed name not set", "12", SeedConfig.getCurrentSeed().getName());
        Assert.assertEquals("Seed number not set", "12", SeedConfig.getCurrentSeed().getNumber());
    }

    @Test
    public void testOnStartScreen() {
        singleScreen.getScreen().findElementById("StartButton").setVisible(false);
        singleScreen.onStartScreen();
        Assert.assertEquals("Start button should be visible", true,
                singleScreen.getScreen().findElementById("StartButton").isVisible());
    }
}
