package edu.kit.valaris.menu;

import com.jme3.system.AppSettings;
import com.jme3.system.JmeContext;
import edu.kit.valaris.BaseApplication;
import edu.kit.valaris.menu.gui.LoadingScreen;
import edu.kit.valaris.menu.gui.NiftyAppState;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Artur Wesner
 */
public class TestLoadingScreen {
    public static BaseApplication app;
    public static LoadingScreen loadingScreen;
    public static NiftyAppState loadNiftyAppState;

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
        loadingScreen = new LoadingScreen(null, app.getStateManager(), app);
        loadNiftyAppState = new NiftyAppState("Test1", loadingScreen);
        loadNiftyAppState.initialize(app.getStateManager(), app);
        app.getStateManager().attach(loadNiftyAppState);
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
        Assert.assertEquals("Screen ID wrong", LoadingScreen.m_screenID, loadingScreen.getScreenID());
        Assert.assertEquals("XML Path wrong", LoadingScreen.m_xmlPath, loadingScreen.getScreenXMLPath());
    }
}
