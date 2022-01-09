package edu.kit.valaris.menu;

import edu.kit.valaris.BaseApplication;
import edu.kit.valaris.menu.gui.ErrorScreen;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Artur Wesner
 */
public class TestErrorScreen {
    public static BaseApplication app;
    public static ErrorScreen errorScreen;

    @SuppressWarnings("all")
    @BeforeClass
    public static void beforeClass() {
        app = new BaseApplication() {
            @Override
            protected void init() {
            }
        };
        errorScreen = new ErrorScreen(app.getStateManager(), app);
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
        Assert.assertEquals("Screen ID wrong", ErrorScreen.m_screenID, errorScreen.getScreenID());
        Assert.assertEquals("XML Path wrong", ErrorScreen.m_xmlPath, errorScreen.getScreenXMLPath());
    }
}
