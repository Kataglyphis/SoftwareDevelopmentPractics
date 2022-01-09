package edu.kit.valaris.menu.gui;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.NiftyEventSubscriber;
import de.lessvoid.nifty.controls.CheckBox;
import de.lessvoid.nifty.controls.CheckBoxStateChangedEvent;
import de.lessvoid.nifty.controls.DropDown;
import de.lessvoid.nifty.controls.DropDownSelectionChangedEvent;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.input.NiftyInputEvent;
import de.lessvoid.nifty.screen.KeyInputHandler;
import de.lessvoid.nifty.screen.Screen;
import edu.kit.valaris.BaseApplication;
import edu.kit.valaris.Metadata;
import edu.kit.valaris.config.Config;
import edu.kit.valaris.menu.menuconfig.GraphicsConfig;
import edu.kit.valaris.menu.menuconfig.InternalGameConfig;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

/**
 * This class controls the graphical user interface of the settings menu screen.
 * This includes the provision of information which screen is currently opened.
 * In order to do this the current screen will be attached/detached to/of the StateManager.
 * It inherits OwnScreenController to control the Nifty Screens and implements the Interface KeyInputHandler for
 * being able to react to User Input and Actions.
 * @author Artur Wesner
 */
public class SettingsmenuScreen extends OwnScreenController implements KeyInputHandler {
    /**
     * Path where the correspondending .xml file is located.
     */
    public static final String m_xmlPath = "edu.kit.valaris/menu/Settingsmenu.xml";

    /**
     * The screem id which is defined in the .xml file.
     */
    public static final String m_screenID = "settingsmenuscreen";

    /**
     * Reference to the screen from which this menu was opened.
     */
    private String m_lastScreen;

    /**
     * Saves the Element object of the graphics tab button.
     */
    private Element m_graphicsTab;

    /**
     * Saves the Element object ob the intern tab button.
     */
    private Element m_internTab;

    /**
     * Reference to the language drop down object.
     */
    private DropDown<String> m_langDropDown;

    /**
     * Saves the Element of the language drop down.
     */
    private Element m_langDropDownElement;

    /**
     * Reference to the language drop down object.
     */
    private DropDown<String> m_resDropDown;

    /**
     * Saves the ELement object of the resolution drop down.
     */
    private Element m_resDropDownElement;

    /**
     * Reference to the ki drop down object.
     */
    private DropDown<String> m_kidiffDropDown;

    /**
     * Saves the Element object of the ki difficulty drop down.
     */
    private Element m_kidiffDropDownElement;

    /**
     * Reference to the quality drop down object.
     */
    private DropDown<String> m_qualityDropDown;

    /**
     * Saves the Element object of the quality difficulty drop down.
     */
    private Element m_qualityDropDownElement;

    /**
     * Saves the refresh rate of the monitor.
     */
    private int m_refreshrate;

    /**
     * Saves the bit depth of the monitor.
     */
    private int m_bitDepth;

    /**
     * Saves a sorted list of all possible display modes.
     */
    private List<DisplayMode> m_sortedDisplayModes;

    /**
     * Saves the Element object of the fullscreen checkbox.
     */
    private Element m_fullscreenCheckbox;

    /**
     * Saves the Element object of the V-Sync checkbox.
     */
    private Element m_vsyncCheckbox;

    /**
     * HashMap containing all road types and their resource key in the local file.
     */
    private Map<String, String> m_allQualityTypes;

    /**
     * HashMap containing all road types and their resource key in the local file.
     */
    private Map<String, String> m_allKIDiffTypes;

    /**
     * Constructor calling the super constructor and initializing the private variables.
     * @param stateManager The application's StateManager.
     * @param app The application.
     */
    public SettingsmenuScreen(AppStateManager stateManager, Application app, String lastScreen) {
        super(m_screenID, m_xmlPath);
        m_app = (BaseApplication) app;
        m_stateManager = stateManager;
        m_lastScreen = lastScreen;
        m_allQualityTypes = new HashMap<>();
        m_allKIDiffTypes = new HashMap<>();
    }

    /**
     * this gets called when a resolution drop down item gets selected.
     * @param id The id.
     * @param event The event.
     */
    @SuppressWarnings("unused")
    @NiftyEventSubscriber(id = "resolutionDropDown")
    public void editCurrentResolution(String id, DropDownSelectionChangedEvent event) {
        try {
            Display.setDisplayMode(m_sortedDisplayModes.get(event.getSelectionItemIndex()));
        } catch (final LWJGLException e) {
            e.printStackTrace();
        }

        m_nifty.resolutionChanged();
        m_graphicsConfig.setProperty("resolution", event.getSelection().toString());
        GraphicsConfig.saveConfig();
    }

    /**
     * this gets called when a resolution drop down item gets selected.
     * @param id The id.
     * @param event The event.
     */
    @SuppressWarnings("unused")
    @NiftyEventSubscriber(id = "languageDropDown")
    public void editCurrentLanguage(String id, DropDownSelectionChangedEvent event) {
        m_graphicsConfig.setProperty("language", languageTextToTag(event.getSelection().toString()));
        GraphicsConfig.saveConfig();
        Logger niftyLogger = Logger.getLogger(Nifty.class.getName());
        Level save = niftyLogger.getLevel();
        niftyLogger.setLevel(Level.SEVERE);
        m_nifty.setLocale(Locale.forLanguageTag(m_graphicsConfig.getProperty("language")));
        niftyLogger.setLevel(save);

        m_stateManager.getState(Metadata.class).getApp().enqueue(() -> {
            m_stateManager.detach(getNiftyAppState());
            m_stateManager.attach(new NiftyAppState("Settings", this));
        });
    }

    /**
     * this gets called when a ki difficulty drop down item gets selected.
     * @param id The id.
     * @param event The event.
     */
    @SuppressWarnings("unused")
    @NiftyEventSubscriber(id = "kidiffDropDown")
    public void editCurrentKIDifficulty(String id, DropDownSelectionChangedEvent event) {
        m_internalGameSettings.setProperty("KIDifficulty", m_allKIDiffTypes.get(event.getSelection().toString()));
        InternalGameConfig.saveConfig();
    }

    /**
     * this gets called when a quality drop down item gets selected.
     * @param id The id.
     * @param event The event.
     */
    @SuppressWarnings("unused")
    @NiftyEventSubscriber(id = "qualityDropDown")
    public void editCurrentQuality(String id, DropDownSelectionChangedEvent event) {
        m_graphicsConfig.setProperty("quality", m_allQualityTypes.get(event.getSelection().toString()));
        GraphicsConfig.saveConfig();
    }

    /**
     * Gets called when the fullscreen checkbox gets edited.
     * @param id The id.
     * @param event The event.
     */
    @SuppressWarnings("unused")
    @NiftyEventSubscriber(id = "fullscreenCheckbox")
    public void toggleFullscreen(String id, CheckBoxStateChangedEvent event) {
        if (!m_sortedDisplayModes.get(m_resDropDown.getSelectedIndex()).isFullscreenCapable()) {
            createPopupMessage(m_bundleInfo.getString("notfullscreensupported",
                    Locale.forLanguageTag(m_graphicsConfig.getProperty("language"))));
        }
        if (event.isChecked()) {
            try {
                Display.setDisplayModeAndFullscreen(m_sortedDisplayModes.get(m_resDropDown.getSelectedIndex()));
                m_graphicsConfig.setProperty("fullscreen", "1");
                GraphicsConfig.saveConfig();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            try {
                Display.setFullscreen(false);
                m_graphicsConfig.setProperty("fullscreen", "0");
                GraphicsConfig.saveConfig();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Gets called when the vsync checkbox gets edited.
     * @param id The id.
     * @param event The event.
     */
    @SuppressWarnings("unused")
    @NiftyEventSubscriber(id = "vsyncCheckbox")
    public void toggleVSync(String id, CheckBoxStateChangedEvent event) {
        if (event.isChecked()) {
            Display.setVSyncEnabled(true);
            m_graphicsConfig.setProperty("vsync", "1");
            GraphicsConfig.saveConfig();
        } else {
            Display.setVSyncEnabled(false);
            m_graphicsConfig.setProperty("vsync", "0");
            GraphicsConfig.saveConfig();
        }
    }

    /**
     * Sinmple Getter for the last screen.
     * @return The last screen.
     */
    public String getLastScreen() {
        return m_lastScreen;
    }

    /**
     * Simple setter for the last screen.
     * @param lastScreen The last screen to set.
     */
    public void setLastScreen(String lastScreen) {
        this.m_lastScreen = lastScreen;
    }

    /**
     * Opens the main menu. Checks if the main menu is in the same appstate as the settings menu
     * and then calls goToScreen(). If its not the case then it attaches a new NiftyAppState with a new main
     * menu controller and then detaches the current NiftyAppState.
     */
    public void backToMainmenu() {
        m_app.enqueue(() -> {
            for (OwnScreenController current : getOtherController()) {
                if (current.getScreenID().equals(MainmenuScreen.m_screenID)) {
                    m_nifty.gotoScreen(MainmenuScreen.m_screenID);
                    return;
                }
            }
            m_stateManager.detach(getNiftyAppState());
            SinglePlayerModeScreen single = new SinglePlayerModeScreen(m_stateManager, m_app);
            MainmenuScreen mainmenuScreen = new MainmenuScreen(m_stateManager, m_app);
            m_stateManager.attach(new NiftyAppState("MainnSingle", mainmenuScreen, single));
        });
    }

    /**
     * Goes back to Pausemenu.
     */
    public void backToPausemenu() {
        m_app.enqueue(() -> {
            for (OwnScreenController current : getOtherController()) {
                if (current.getScreenID().equals(PausemenuScreen.m_screenID)) {
                    m_nifty.gotoScreen(PausemenuScreen.m_screenID);
                    return;
                }
            }
            m_stateManager.detach(getNiftyAppState());
            PausemenuScreen pausemenuScreen = new PausemenuScreen(m_stateManager, m_app);
            m_stateManager.attach(new NiftyAppState("Pause", pausemenuScreen));
        });
    }

    /**
     * Depending on which was the last screen this methode decides which method will be called if the settings menu is
     * gonna be closed.
     */
    @SuppressWarnings("unused")
    public void backtoMenu() {
        if (m_lastScreen.equals(MainmenuScreen.m_screenID)) {
            backToMainmenu();
        } else if (m_lastScreen.equals(PausemenuScreen.m_screenID)) {
            backToPausemenu();
        }
    }

    /**
     * Shows the graphics tab.
     */
    @SuppressWarnings("unused")
    public void showGraphicsTab() {
        m_internTab.setVisible(false);
        m_graphicsTab.setVisible(true);
    }

    /**
     * Shows the intern settings tab.
     */
    @SuppressWarnings("unused")
    public void showInternTab() {
        m_graphicsTab.setVisible(false);
        m_internTab.setVisible(true);
    }

    /**
     * Converts a language tag to a full text version of it.
     * @param languageTag The tag to convert.
     * @return The converted text.
     */
    private String languageTagToText(String languageTag) {
        if (languageTag.equals("de")) {
            return "Deutsch";
        } else if (languageTag.equals("en")) {
            return "English";
        }
        return "";
    }

    /**
     * Converts a language full text to a tag.
     * @param languageText The text to convert.
     * @return The converted tag.
     */
    private String languageTextToTag(String languageText) {
        if (languageText.equals("Deutsch") || languageText.equals("German")) {
            return "de";
        } else if (languageText.equals("Englisch") || languageText.equals("English")) {
            return "en";
        }
        return "";
    }

    /**
     * Fills the language drop down.
     */
    private void fillLangDropDown() {
        m_langDropDown.clear();
        m_langDropDown.addItem(m_bundleInfo.getString("german",
                Locale.forLanguageTag(m_graphicsConfig.getProperty("language"))));
        m_langDropDown.addItem(m_bundleInfo.getString("english",
                Locale.forLanguageTag(m_graphicsConfig.getProperty("language"))));
        try {
            m_langDropDown.selectItem(languageTagToText(m_graphicsConfig.getProperty("language")));
        } catch (MissingResourceException ex) {
            GraphicsConfig.reset();
            m_graphicsConfig = GraphicsConfig.getConfig();
            m_langDropDown.selectItem(languageTagToText(m_graphicsConfig.getProperty("language")));
        }
    }

    /**
     * Fills the resolution drop down.
     */
    @SuppressWarnings("unused")
    private void fillResolutionDropDown() {
        m_resDropDown.clear();
        try {
            m_sortedDisplayModes = new ArrayList<>();
            DisplayMode currentDisplayMode = Display.getDisplayMode();
            DisplayMode[] modes = Display.getAvailableDisplayModes();
            for (DisplayMode mode: modes) {
                if (mode.getBitsPerPixel() == m_bitDepth && mode.getFrequency() == m_refreshrate) {
                    m_sortedDisplayModes.add(mode);
                }
            }
            Collections.sort(m_sortedDisplayModes, new Comparator<DisplayMode>() {
                @Override
                public int compare(DisplayMode o1, DisplayMode o2) {
                    int widthCompare = Integer.valueOf(o1.getWidth()).compareTo(Integer.valueOf(o2.getWidth()));
                    if (widthCompare != 0) {
                        return widthCompare;
                    }
                    int heightCompare = Integer.valueOf(o1.getHeight()).compareTo(Integer.valueOf(o2.getHeight()));
                    if (heightCompare != 0) {
                        return heightCompare;
                    }
                    return o1.toString().compareTo(o2.toString());
                }
            });

            for (DisplayMode modeToDropDown : m_sortedDisplayModes) {
                m_resDropDown.addItem(modeToDropDown.getWidth() + " x " + modeToDropDown.getHeight());
            }
            m_resDropDown.selectItem(m_graphicsConfig.getProperty("resolution"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Fills the ki difficulty drop down.
     */
    private void fillKIDiffDropDown() {
        m_kidiffDropDown.clear();
        m_kidiffDropDown.addItem(m_bundleInfo.getString("easy", m_currentLocale));
        m_kidiffDropDown.addItem(m_bundleInfo.getString("normal", m_currentLocale));
        m_kidiffDropDown.addItem(m_bundleInfo.getString("hard", m_currentLocale));

        for (Locale loc : getAvailableLocales()) {
            m_allKIDiffTypes.put(m_bundleInfo.getString("easy", loc), "easy");
            m_allKIDiffTypes.put(m_bundleInfo.getString("normal", loc), "normal");
            m_allKIDiffTypes.put(m_bundleInfo.getString("hard", loc), "hard");
        }
        try {
            m_kidiffDropDown.selectItem(m_bundleInfo.getString(m_internalGameSettings.getProperty("KIDifficulty"), m_currentLocale));
        } catch (MissingResourceException ex) {
            InternalGameConfig.reset();
            m_internalGameSettings = InternalGameConfig.getConfig();
            m_kidiffDropDown.selectItem(m_bundleInfo.getString(m_internalGameSettings.getProperty("KIDifficulty"), m_currentLocale));
        }
    }

    private void fillQualityDropDown() {
        m_qualityDropDown.clear();
        m_qualityDropDown.addItem(m_bundleInfo.getString("low", m_currentLocale));
        m_qualityDropDown.addItem(m_bundleInfo.getString("medium", m_currentLocale));
        m_qualityDropDown.addItem(m_bundleInfo.getString("high", m_currentLocale));

        for (Locale loc : getAvailableLocales()) {
            m_allQualityTypes.put(m_bundleInfo.getString("low", loc), "low");
            m_allQualityTypes.put(m_bundleInfo.getString("medium", loc), "medium");
            m_allQualityTypes.put(m_bundleInfo.getString("high", loc), "high");
        }
        try {
            m_qualityDropDown.selectItem(m_bundleInfo.getString(m_graphicsConfig.getProperty("quality"), m_currentLocale));
        } catch (MissingResourceException ex) {
            GraphicsConfig.reset();
            m_graphicsConfig = GraphicsConfig.getConfig();
            m_qualityDropDown.selectItem(m_bundleInfo.getString(m_graphicsConfig.getProperty("quality"), m_currentLocale));
        }
    }

    /**
     * Resets the settings.
     */
    public void resetSettings() {
        if ((!GraphicsConfig.reset()) || (!InternalGameConfig.reset())) {
            createPopupMessage(m_bundleInfo.getString("resetfailed",
                    Locale.forLanguageTag(m_graphicsConfig.getProperty("language"))));
            return;
        }
        m_graphicsConfig = GraphicsConfig.getConfig();
        m_internalGameSettings = InternalGameConfig.getConfig();
        m_kidiffDropDown.selectItem(m_internalGameSettings.getProperty("KIDifficulty"));
        m_qualityDropDown.selectItem(m_graphicsConfig.getProperty("quality"));
        m_resDropDown.selectItem(m_graphicsConfig.getProperty("resolution"));
        createPopupMessage(m_bundleInfo.getString("resetok",
                Locale.forLanguageTag(m_graphicsConfig.getProperty("language"))));
        onStartScreen();
    }

    /**
     * Bind this ScreenController to a screen. This happens
     * right before the onStartScreen STARTED and only exactly once for a screen!
     *
     * @param nifty  nifty
     * @param screen screen
     */
    @SuppressWarnings("unchecked")
    @Override
    public void bind(@Nonnull Nifty nifty, @Nonnull Screen screen) {
        super.bind(nifty, screen);
        m_graphicsTab = m_screen.findElementById("graphicsTab");
        m_internTab = m_screen.findElementById("internTab");
        m_langDropDown =  m_screen.findNiftyControl("languageDropDown", DropDown.class);
        m_langDropDownElement = m_screen.findElementById("languageDropDown");
        fillLangDropDown();
        m_resDropDown =  m_screen.findNiftyControl("resolutionDropDown", DropDown.class);
        m_resDropDownElement = m_screen.findElementById("resolutionDropDown");
        GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        m_refreshrate = device.getDisplayMode().getRefreshRate();
        m_bitDepth = device.getDisplayMode().getBitDepth();
        fillResolutionDropDown();
        m_kidiffDropDown =  m_screen.findNiftyControl("kidiffDropDown", DropDown.class);
        m_kidiffDropDownElement = m_screen.findElementById("kidiffDropDown");
        fillKIDiffDropDown();
        m_qualityDropDown = m_screen.findNiftyControl("qualityDropDown", DropDown.class);
        m_qualityDropDownElement = m_screen.findElementById("qualityDropDown");
        fillQualityDropDown();
        m_fullscreenCheckbox = m_screen.findElementById("fullscreenCheckbox");
        if (Display.isFullscreen()) {
            m_fullscreenCheckbox.getNiftyControl(CheckBox.class).setChecked(true);
        }
        m_vsyncCheckbox = m_screen.findElementById("vsyncCheckbox");
        try {
            if (Integer.parseInt(m_graphicsConfig.getProperty("vsync")) == 1) {
                m_vsyncCheckbox.getNiftyControl(CheckBox.class).setChecked(true);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * called right after the onStartScreen event ENDED.
     */
    @Override
    public void onStartScreen() {
        if (m_lastScreen.equals(PausemenuScreen.m_screenID)) {
            m_kidiffDropDownElement.disable();
            m_qualityDropDownElement.disable();
        }
    }

    /**
     * called right after the onEndScreen event ENDED.
     */
    @Override
    public void onEndScreen() {
    }

    /**
     * handle a key event.
     *
     * @param inputEvent key event to handle
     * @return true when the event has been consumend and false if not
     */
    @Override
    public boolean keyEvent(@Nonnull NiftyInputEvent inputEvent) {
        return false;
    }
}