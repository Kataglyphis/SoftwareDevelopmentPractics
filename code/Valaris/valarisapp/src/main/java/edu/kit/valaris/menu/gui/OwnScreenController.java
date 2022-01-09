package edu.kit.valaris.menu.gui;

import com.jme3.app.state.AppStateManager;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.Label;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import de.lessvoid.xml.tools.BundleInfo;
import edu.kit.valaris.BaseApplication;
import edu.kit.valaris.menu.menuconfig.GraphicsConfig;
import edu.kit.valaris.menu.menuconfig.InternalGameConfig;
import edu.kit.valaris.menu.menuconfig.SeedConfig;

import java.util.HashSet;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;

/**
 * An own ScreenController Class
 */
public class OwnScreenController implements ScreenController {

    /**
     * Whether this screen is currently active.
     */
    private boolean m_initialized = false;

    /**
     * Saves the popup message.
     */
    private String m_popupMessage;

    /**
     * The screen id which is defined in the .xml file.
     */
    private String m_screenID;

    /**
     * Path where the corresponding .xml file is located.
     */
    private String m_screenXMLPath;

    /**
     * Reference to the NiftyAppState.
     */
    private NiftyAppState m_niftyAppState;

    /**
     * List of all screencontroller attached to the same NiftyAppState.
     */
    private OwnScreenController[] m_otherController;

    /**
     * Reference to the nifty object.
     */
    protected Nifty m_nifty;

    /**
     * Reference to the corresponding screen.
     */
    protected Screen m_screen;

    /**
     * Reference to the application.
     */
    protected BaseApplication m_app;

    /**
     * Reference to the applications statemanager.
     */
    protected AppStateManager m_stateManager;

    /**
     * Saves the popup Element.
     */
    protected Element m_popup;

    /**
     * Saves the resource bundle.
     */
    protected BundleInfo m_bundleInfo;

    /**
     * Reference to the SeedConfig Properties object.
     */
    protected Properties m_seedConfig;

    /**
     * Reference to the InternalGameConfig Properties object.
     */
    protected Properties m_internalGameSettings;

    /**
     * Reference to the graphicsConfig Properties object.
     */
    protected Properties m_graphicsConfig;

    /**
     * Current locale.
     */
    protected Locale m_currentLocale;

    /**
     * Constructor initializing the screen id and the xml path.
     * @param screenID The screen id to be set.
     * @param screenXMLPath The xml path to be set.
     */
    public OwnScreenController(String screenID, String screenXMLPath) {
        m_screenID = screenID;
        m_screenXMLPath = screenXMLPath;
        m_seedConfig = SeedConfig.getConfig();
        m_internalGameSettings = InternalGameConfig.getConfig();
        m_graphicsConfig = GraphicsConfig.getConfig();
    }

    /**
     * Updates the OwnScreencontroller.
     * @param tpf tpf.
     */
    public void updateOwnScreencontroller(float tpf) {
    }

    /**
     * Getter for the nifty object.
     * @return The nifty object.
     */
    public Nifty getNifty() {
        return m_nifty;
    }

    /**
     * Getter for the screen object.
     * @return The screen object.
     */
    public Screen getScreen() {
        return m_screen;
    }

    /**
     * Getter for the NiftyAppState.
     * @return The NiftyAppState.
     */
    public NiftyAppState getNiftyAppState() {
        return m_niftyAppState;
    }

    /**
     * Sets the local NiftyAppState to the given one.
     * @param niftyAppState The NiftyAppState to be set.
     */
    public void setNiftyAppState(NiftyAppState niftyAppState) {
        m_niftyAppState = niftyAppState;
    }

    /**
     * Getter for the OwnScreenController List.
     * @return The OwnScreenController List.
     */
    public OwnScreenController[] getOtherController() {
        return m_otherController;
    }

    /**
     * Setter for the OwnScreenController List.
     * @param otherController The OwnScreenController List. to be set.
     */
    public void setOtherController(OwnScreenController[] otherController) {
        m_otherController = otherController;
    }

    /**
     * Getter for the screen id.
     * @return The screen id.
     */
    public String getScreenID() {
        return m_screenID;
    }

    /**
     * Getter for the xml path.
     * @return The xml path.
     */
    public String getScreenXMLPath() {
        return m_screenXMLPath;
    }

    /**
     * Creates and shows a popup message on the screen with the given String.
     * @param message The message to be shown in the popup.
     */
    public void createPopupMessage(String message) {
        if(m_initialized) {
            m_popup = m_nifty.createPopup("messagePopup");
            m_popup.findNiftyControl("messagePopupTitel", Label.class).setText(message);
            m_nifty.showPopup(m_screen, m_popup.getId(), null);
        } else {
            m_popupMessage = message;
        }
    }

    /**
     * Closes the shown popup.
     */
    @SuppressWarnings("unused")
    public void quitPopup() {
        m_nifty.closePopup(m_popup.getId());
    }

    /**
     * This method takes a time duration in seconds and returns a String in the form of MM:SS.
     * @param time The time to be converted.
     * @return The converted String.
     */
    protected String getTime(float time) {
        float second = TimeUnit.SECONDS.convert((long)time, TimeUnit.SECONDS) % 60;
        float minute = TimeUnit.MINUTES.convert((long)time, TimeUnit.SECONDS) % 60;
        return String.format("%02.0f:%02.0f", minute, second);
    }


    /**
     * Return a locale array with all available locales.
     * @return The avaialable locales.
     */
    protected Locale[] getAvailableLocales() {
        Set<Locale> locales = new HashSet<>();
        locales.add(Locale.forLanguageTag("de"));
        locales.add(Locale.forLanguageTag("en"));
        return locales.toArray(new Locale[0]);
    }

    /**
     * Bind this ScreenController to a screen. This happens
     * right before the onStartScreen STARTED and only exactly once for a screen!
     *
     * @param nifty  nifty
     * @param screen screen
     */
    @Override
    public void bind(@Nonnull Nifty nifty, @Nonnull Screen screen) {
        m_nifty = nifty;
        m_screen = screen;
        m_bundleInfo = m_nifty.getResourceBundles().get("menudialog");
        m_currentLocale = Locale.forLanguageTag(m_graphicsConfig.getProperty("language"));

        //create popup delayed
        m_initialized = true;
        if(m_popupMessage != null) {
            createPopupMessage(m_popupMessage);
        }

    }

    /**
     * called right after the onStartScreen event ENDED.
     */
    @Override
    public void onStartScreen() {
    }

    /**
     * called right after the onEndScreen event ENDED.
     */
    @Override
    public void onEndScreen() {
    }
}
