package edu.kit.valaris.menu;

import edu.kit.valaris.menu.menuconfig.ConfigManager;
import edu.kit.valaris.menu.menuconfig.GraphicsConfig;
import edu.kit.valaris.menu.menuconfig.InternalGameConfig;
import edu.kit.valaris.menu.menuconfig.SeedConfig;
import edu.kit.valaris.menu.menudatastructures.SeedEntry;
import java.util.Properties;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests MenucConfig functionality.
 * @author Artur Wesner
 * @version 1.0
 */
public class TestMenuConfig {

    @Before
    public void setUp() {
        GraphicsConfig.reset();
        InternalGameConfig.reset();
        SeedConfig.reset();
    }

    @After
    public void tearDown() {
        GraphicsConfig.reset();
        InternalGameConfig.reset();
        SeedConfig.reset();
    }

    @Test
    public void testGetGraphicsConfig() {
        Properties prop = GraphicsConfig.getConfig();
        String result  = prop.getProperty("language");
        Assert.assertEquals("Getting the GraphicsConfig failed.", "de", result);
    }

    @Test
    public void testSaveGraphicsConfig() {
        Properties prop = GraphicsConfig.getConfig();
        prop.setProperty("language", "en");
        String result;
        if (!GraphicsConfig.saveConfig()) {
            result = "FAIL";
        }
        Properties newprop = GraphicsConfig.getConfig();
        result  = newprop.getProperty("language");
        Assert.assertEquals("Saving the GraphicsConfig failed.", "en", result);
        Assert.assertNotEquals("Error occured while trying to write in the GraphicsConfig file.",
                "FAIL", result);
    }

    @Test
    public void testResetGraphicsConfig() {
        Properties prop = GraphicsConfig.getConfig();
        prop.setProperty("language", "en");
        GraphicsConfig.reset();
        prop = GraphicsConfig.getConfig();
        Properties defaultprop = ConfigManager.getConfig("/edu.kit.valaris/con"
                + "fig/Default/DefaultGraphicsConfig.properties", "/edu.kit.valaris/con"
                + "fig/Default/DefaultGraphicsConfig.properties");
        boolean result  = defaultprop.equals(prop);
        Assert.assertEquals("Resetting the GraphicsConfig failed.", true, result);
    }

    @Test
    public void testGetInternalGameConfig() {
        Properties prop = InternalGameConfig.getConfig();
        String result  = prop.getProperty("KICount");
        Assert.assertEquals("Getting the InternalGameConfig failed.", "0", result);
    }

    @Test
    public void testSaveInternalGameConfig() {
        Properties prop = InternalGameConfig.getConfig();
        prop.setProperty("KICount", "" + 2);
        String result;
        if (!InternalGameConfig.saveConfig()) {
            result = "FAIL";
        }
        Properties newprop = InternalGameConfig.getConfig();
        result  = newprop.getProperty("KICount");
        Assert.assertEquals("Saving the InternalGameConfig failed.", "2", result);
        Assert.assertNotEquals("Error occured while trying to write in the InternalGameConfig file.",
                "FAIL", result);
    }

    @Test
    public void testResetInternalGameConfig() {
        Properties prop = InternalGameConfig.getConfig();
        prop.setProperty("quality", "medium");
        InternalGameConfig.reset();
        prop = InternalGameConfig.getConfig();
        Properties defaultprop = ConfigManager.getConfig("/edu.kit.valaris/con"
                + "fig/Default/DefaultInternalGameConfig.properties", "/edu.kit.valaris/con"
                + "fig/Default/DefaultInternalGameConfig.properties");
        boolean result  = defaultprop.equals(prop);
        Assert.assertEquals("Resetting the InternalGameConfig failed.", true, result);
    }

    @Test
    public void testGetSeedConfig() {
        SeedEntry s = new SeedEntry("Coole Map", "11312312", "small");
        SeedConfig.addSeed(s);
        Properties prop = SeedConfig.getConfig();
        String result  = prop.getProperty("11312312#small");
        Assert.assertEquals("Getting the SeedConfig failed.", "Coole Map#small", result);
    }

    @Test
    public void testSaveSeedConfig() {
        SeedEntry s = new SeedEntry("Coole Map", "113123123");
        String result;
        if (!SeedConfig.addSeed(s)) {
            result = "FAIL";
        }
        Properties prop = SeedConfig.getConfig();
        prop.setProperty("113123123", "Noch coolere Map");
        if (!SeedConfig.saveConfig()) {
            result = "FAIL";
        }
        Properties newprop = SeedConfig.getConfig();
        result  = newprop.getProperty("113123123");
        Assert.assertEquals("Saving the SeedConfig failed.", "Noch coolere Map", result);
        Assert.assertNotEquals("Error occured while trying to write in the SeedConfig file.",
                "FAIL", result);
    }

    @Test
    public void testAddASeed() {
        SeedEntry s = new SeedEntry("Coole Map", "11312312", "small");
        String result;
        if (!SeedConfig.addSeed(s)) {
            result = "FAIL";
        }
        Properties newprop = SeedConfig.getConfig();
        result  = newprop.getProperty("11312312#small");
        Assert.assertEquals("Adding a Seed failed.", "Coole Map#small", result);
        Assert.assertNotEquals("Error occured while trying to write in the SeedConfig file.",
                "FAIL", result);
    }

    @Test
    public void testResetSeedConfig() {
        SeedEntry s = new SeedEntry("Coole Map", "11312312");
        String couldBeAdded = "";
        if (!SeedConfig.addSeed(s)) {
            couldBeAdded = "FAIL";
        }
        SeedConfig.reset();
        Properties prop = SeedConfig.getConfig();
        Properties defaultprop = ConfigManager.getConfig("/edu.kit.valaris/con"
                + "fig/Default/DefaultSavedSeeds.properties", "/edu.kit.valaris/con"
                + "fig/Default/DefaultSavedSeeds.properties");
        boolean result  = defaultprop.equals(prop);
        Assert.assertEquals("Resetting the InternalGameConfig failed.", true, result);
        Assert.assertNotEquals("Error occured while trying to write in the SeedConfig file.",
                "FAIL", result);
    }
}
