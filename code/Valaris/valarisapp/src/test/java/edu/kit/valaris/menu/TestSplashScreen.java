package edu.kit.valaris.menu;

import edu.kit.valaris.BaseApplication;
import edu.kit.valaris.menu.gui.SplashScreen;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Artur Wesner
 */
public class TestSplashScreen {
    public static BaseApplication app;
    public static SplashScreen splashScreen;

    @SuppressWarnings("all")
    @BeforeClass
    public static void beforeClass() {
        app = new BaseApplication() {
            @Override
            protected void init() {
            }
        };
        splashScreen = new SplashScreen(app.getStateManager(), app);
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
        Assert.assertEquals("Screen ID wrong", SplashScreen.m_screenID, splashScreen.getScreenID());
        Assert.assertEquals("XML Path wrong", SplashScreen.m_xmlPath, splashScreen.getScreenXMLPath());
    }
}
