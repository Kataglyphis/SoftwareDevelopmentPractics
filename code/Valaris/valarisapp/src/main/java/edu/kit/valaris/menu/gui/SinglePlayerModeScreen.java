package edu.kit.valaris.menu.gui;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.NiftyEventSubscriber;
import de.lessvoid.nifty.controls.DropDown;
import de.lessvoid.nifty.controls.DropDownSelectionChangedEvent;
import de.lessvoid.nifty.controls.TextField;
import de.lessvoid.nifty.controls.TextFieldChangedEvent;
import de.lessvoid.nifty.controls.textfield.filter.input.FilterAcceptRegex;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.input.NiftyInputEvent;
import de.lessvoid.nifty.input.NiftyStandardInputEvent;
import de.lessvoid.nifty.screen.KeyInputHandler;
import de.lessvoid.nifty.screen.Screen;
import edu.kit.valaris.BaseApplication;
import edu.kit.valaris.menu.menuconfig.InternalGameConfig;
import edu.kit.valaris.menu.menuconfig.SeedConfig;
import edu.kit.valaris.menu.menudatastructures.SeedEntry;
import java.util.*;
import javax.annotation.Nonnull;

/**
 * This class controls the graphical user interface of the single player menu screen.
 * This includes the provision of information which screen is currently opened.
 * In order to do this the current screen will be attached/detached to/of the StateManager.
 * It inherits OwnScreenController to control the Nifty Screens and implements the Interface KeyInputHandler for
 * being able to react to User Input and Actions.
 * @author Artur Wesner
 */
public class SinglePlayerModeScreen extends OwnScreenController implements KeyInputHandler {
    /**
     * Path where the corresponding .xml file is located.
     */
    public static final String m_xmlPath = "edu.kit.valaris/menu/SinglePlayerMenu.xml";

    /**
     * The screem id which is defined in the .xml file.
     */
    public static final String m_screenID = "singleplayerscreen";

    /**
     * Reference to the Textfield of editing the current seed's name.
     */
    private TextField m_editSeednameTextfield;

    /**
     * Reference to the Textfield's Element of editing the current seed's name.
     */
    private Element m_editSeednameTextfieldElement;

    /**
     * Reference to the seed drop down object.
     */
    private DropDown<String> m_dropDown;

    /**
     * Reference to the Button's Element to activate editing the seed name.
     */
    private Element m_editSeedNameButton;

    /**
     * Reference to the Button's Element to save the seed name.
     */
    private Element m_saveSeedNameButton;

    /**
     * Reference to the Button's Element to save the seed number.
     */
    private Element m_saveSeedNumberButton;

    /**
     * Reference to the text field where the seed number can be edited.
     */
    private TextField m_seedNumberTextfield;

    /**
     * Reference to the text field where the ki count can be edited.
     */
    private TextField m_kiTextfield;

    /**
     * Reference to the text field where the player count can be edited.
     */
    private TextField m_playerTextfield;

    /**
     * Reference to the Button's Element to start the Game.
     */
    private Element m_startButton;

    /**
     * Saves the state whether a new seed was defined by editing the seed number.
     */
    private boolean m_newSeedDefined = false;

    /**
     * Saves the state whether a seed was selected in the drop down menu.
     */
    private boolean m_seedSelected = false;

    /**
     * HashMap containing all Seeds. The seed number is the unique key.
     */
    private Map<String, String> m_allSeeds;

    /**
     * HashMap containing all road types and their resource key in the local file.
     */
    private Map<String, String> m_allRoadTypes;

    /**
     * Reference to the language drop down object.
     */
    private DropDown<String> m_roadTypeDropDown;

    /**
     * Saves the Element of the language drop down.
     */
    private Element m_roadTypeDropDownElement;

    /**
     * Saves the state whether a dropdown event gets called from another dropdown menu.
     */
    private boolean m_fromOtherDropdown = false;

    /**
     * Constructor calling the super constructor and initializing the private variables.
     * @param stateManager The application's StateManager.
     * @param app The application.
     */
    public SinglePlayerModeScreen(AppStateManager stateManager, Application app) {
        super(m_screenID, m_xmlPath);
        m_app = (BaseApplication) app;
        m_stateManager = stateManager;
        m_allSeeds = new HashMap<>();
        m_allRoadTypes = new HashMap<>();
        transformConfigtoHashmap();
    }

    /**
     * this gets called when a drop down item getrs selected.
     * @param id The id.
     * @param event The event.
     */
    @SuppressWarnings("unused")
    @NiftyEventSubscriber(id = "seedDropDown")
    public void editCurrentSeed(String id, DropDownSelectionChangedEvent event) {
        if (event.getSelection().equals("")) {
            if (!m_newSeedDefined) {
                m_seedNumberTextfield.setText("");
            }
            return;
        } else if (!setSeedentryforgivenName((String) event.getSelection())) {
            createPopupMessage(m_bundleInfo.getString("seedNameNotFoundPopup",
                    Locale.forLanguageTag(m_graphicsConfig.getProperty("language"))));
            return;
        }
        String[] seedNumb = getSeednumberforgivenName((String) event.getSelection());
        if (seedNumb[0].equals("Err: Corresponding Number not found")) {
            createPopupMessage(m_bundleInfo.getString("seedNameNotFoundPopup",
                    Locale.forLanguageTag(m_graphicsConfig.getProperty("language"))));
            return;
        }
        m_seedNumberTextfield.setText(seedNumb[0]);
        m_fromOtherDropdown = true;
        if (seedNumb.length == 2) {
            m_roadTypeDropDown.selectItem(m_bundleInfo.getString(seedNumb[1], m_currentLocale));
        }
        if (!m_seedSelected) {
            m_seedSelected = true;
            m_newSeedDefined = false;
        }
        if (m_saveSeedNumberButton.isVisible()) {
            m_saveSeedNumberButton.setVisible(false);
        }
    }

    /**
     * This methods gets called when the seed number text field gets edited.
     * @param id The id.
     * @param event The event.
     */
    @SuppressWarnings("unused")
    @NiftyEventSubscriber(id = "editSeedNumber")
    public void editCurrentSeedNumber(String id, TextFieldChangedEvent event) {
        if (!m_newSeedDefined) {
            m_newSeedDefined = true;
            m_seedSelected = false;
        }
        if (m_dropDown.getSelection() != null) {
            if (!m_dropDown.getSelection().equals("")) {
                m_dropDown.selectItem("");
            }
        }
        if (!m_saveSeedNumberButton.isVisible()) {
            m_saveSeedNumberButton.setVisible(true);
        }
    }

    /**
     * This methods gets called when the road type gets edited.
     * @param id The id.
     * @param event The event.
     */
    @SuppressWarnings("unused")
    @NiftyEventSubscriber(id = "roadTypeDropDown")
    public void editRoadTypeDropDown(String id, DropDownSelectionChangedEvent event) {
        if (m_fromOtherDropdown) {
            m_fromOtherDropdown = false;
            return;
        }
        if (!m_newSeedDefined) {
            m_newSeedDefined = true;
            m_seedSelected = false;
        }
        if (m_dropDown.getSelection() != null) {
            if (!m_dropDown.getSelection().equals("")) {
                m_dropDown.selectItem("");
            }
        }
    }

    /**
     * This method gets called when the ki count text field gets edited.
     * @param id The id.
     * @param event The event.
     */
    @SuppressWarnings("unused")
    @NiftyEventSubscriber(id = "editKICount")
    public void editKICount(String id, TextFieldChangedEvent event) {
        if (event.getText().equals("")) {
            return;
        }
        if (Integer.parseInt(event.getText()) + Integer.parseInt(m_internalGameSettings.getProperty("PlayerCount")) > 10) {
            createPopupMessage(m_bundleInfo.getString("counttoohigh",
                    Locale.forLanguageTag(m_graphicsConfig.getProperty("language"))));
            m_kiTextfield.setText(m_internalGameSettings.getProperty("KICount"));
            return;
        }
        try {
            m_internalGameSettings.setProperty("KICount", "" + event.getText());
            InternalGameConfig.saveConfig();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * This method gets called when the ki count text field gets edited.
     * @param id The id.
     * @param event The event.
     */
    @SuppressWarnings("unused")
    @NiftyEventSubscriber(id = "editPlayerCount")
    public void editPlayerCount(String id, TextFieldChangedEvent event) {
        if (event.getText().equals("")) {
            return;
        } else if (Integer.parseInt(event.getText()) == 0) {
            createPopupMessage(m_bundleInfo.getString("playercountinvalid",
                    Locale.forLanguageTag(m_graphicsConfig.getProperty("language"))));
            m_playerTextfield.setText(m_internalGameSettings.getProperty("PlayerCount"));
            return;
        }
        if (Integer.parseInt(event.getText()) + Integer.parseInt(m_internalGameSettings.getProperty("KICount")) > 10) {
            createPopupMessage(m_bundleInfo.getString("counttoohigh",
                    Locale.forLanguageTag(m_graphicsConfig.getProperty("language"))));
            m_playerTextfield.setText(m_internalGameSettings.getProperty("PlayerCount"));
            return;
        }
        try {
            m_internalGameSettings.setProperty("PlayerCount", "" + event.getText());
            InternalGameConfig.saveConfig();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Opens the main menu. Checks if the main menu is in the same appstate as the single player menu
     * and then calls goToScreen(). If its not the case then it attaches a new NiftyAppState with a new main
     * menu controller and then detaches the current NiftyAppState.
     * it also disables the effect of the start button which would be called otherwise.
     */
    @SuppressWarnings("unused")
    public void backToMainmenu() {
        m_startButton.hideWithoutEffect();
        m_app.enqueue(() -> {
            for (OwnScreenController current : getOtherController()) {
                if (current.getScreenID().equals(MainmenuScreen.m_screenID)) {
                    m_nifty.gotoScreen(MainmenuScreen.m_screenID);
                    return;
                }
            }
            m_stateManager.detach(getNiftyAppState());
            MainmenuScreen mainmenuScreen = new MainmenuScreen(m_stateManager, m_app);
            m_stateManager.attach(new NiftyAppState("Main", mainmenuScreen));
        });
    }

    /**
     * Switches to loading screen by attaching a new AppState and detaching the current NiftyAppState.
     */
    @SuppressWarnings("unused")
    public void openLoadingScreen() {
        if (m_kiTextfield.getRealText().equals("")) {
            createPopupMessage(m_bundleInfo.getString("kicountinvalid",
                    Locale.forLanguageTag(m_graphicsConfig.getProperty("language"))));
            return;
        } else if (m_playerTextfield.getRealText().equals("")) {
            createPopupMessage(m_bundleInfo.getString("playercountinvalid",
                    Locale.forLanguageTag(m_graphicsConfig.getProperty("language"))));
            return;
        }
        if (m_dropDown.getSelection() != null) {
            if (m_dropDown.getSelection().equals("") && m_seedNumberTextfield.getRealText().equals("")) {
                String seed = String.valueOf((int)(Math.random() * Math.pow(2,30)));
                String roadTypeResourceKey = transformRaodTypeToResourceKey();
                if (roadTypeResourceKey != null) {
                    SeedConfig.setCurrentSeed(new SeedEntry(seed, seed, roadTypeResourceKey));
                }
            } else if (m_dropDown.getSelection().equals("") && (!m_seedNumberTextfield.getRealText().equals(""))) {
                saveSeedNumber();
            }
        }

        m_app.enqueue(() -> {
            m_stateManager.detach(getNiftyAppState());
            LoadingScreen loadingScreen = new LoadingScreen(new RaceSetup(), m_stateManager, m_app);
            m_stateManager.attach(new NiftyAppState("Loading", loadingScreen));
        });
    }

    /**
     * Fills the drop down.
     */
    private void fillSeedDropDown() {
        List<String> seedNames = new LinkedList<>();
        try {
            seedNames = getSortedSeedNames();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        m_dropDown.addItem("");
        for (String seedName : seedNames) {
            m_dropDown.addItem(seedName);
        }
        if (!m_seedSelected) {
            m_dropDown.selectItem("");
        } else {
            m_dropDown.selectItem(SeedConfig.getCurrentSeed().getName());
        }
    }

    /**
     * Fills the road type dropdown.
     */
    private void fillRoadTypeDropDown() {
        m_roadTypeDropDown.clear();
        m_roadTypeDropDown.addItem(m_bundleInfo.getString("small", m_currentLocale));
        m_roadTypeDropDown.addItem(m_bundleInfo.getString("normal", m_currentLocale));
        m_roadTypeDropDown.addItem(m_bundleInfo.getString("large", m_currentLocale));

        for (Locale loc : getAvailableLocales()) {
            m_allRoadTypes.put(m_bundleInfo.getString("small", loc), "small");
            m_allRoadTypes.put(m_bundleInfo.getString("normal", loc), "normal");
            m_allRoadTypes.put(m_bundleInfo.getString("large", loc), "large");
        }
        try {
            m_roadTypeDropDown.selectItem(m_bundleInfo.getString(m_internalGameSettings.getProperty("RoadType"), m_currentLocale));
        } catch (MissingResourceException ex) {
            InternalGameConfig.reset();
            m_internalGameSettings = InternalGameConfig.getConfig();
            m_roadTypeDropDown.selectItem(m_bundleInfo.getString(m_internalGameSettings.getProperty("RoadType"), m_currentLocale));
        }

    }

    /**
     * Saves the seed name, updated the config references and fills the drop down menu again.
     * Sets the m_saveSeedNameButton to true.
     */
    public void saveSeedName() {
        try {
            editCurrentSeedEntryName(m_editSeednameTextfield.getRealText());
        } catch (IllegalStateException | IllegalArgumentException ex) {
            if (ex.getMessage().equals("The given name is invalid.")) {
                createPopupMessage(m_bundleInfo.getString("nameIsInvalid",
                        Locale.forLanguageTag(m_graphicsConfig.getProperty("language"))));
            } else if (ex.getMessage().equals("Please select a Seed before doing this.")) {
                createPopupMessage(m_bundleInfo.getString("pleaseSelectaSeed",
                        Locale.forLanguageTag(m_graphicsConfig.getProperty("language"))));
            } else if (ex.getMessage().equals("This name already exists.")) {
                createPopupMessage(m_bundleInfo.getString("nameAlreadyExists",
                        Locale.forLanguageTag(m_graphicsConfig.getProperty("language"))));
            }
        }
        updateAllSeeds();
        m_dropDown.clear();
        fillSeedDropDown();
        editSeednameTextfieldVisibility();
        m_saveSeedNameButton.setVisible(false);
        m_editSeedNameButton.setVisible(true);
        m_dropDown.selectItem(SeedConfig.getCurrentSeed().getName());
    }

    /**
     * Sets the current seed's name to the given one.
     * @param name The name to be set.
     * @throws IllegalStateException If there is no current seed selected.
     * @throws IllegalArgumentException If the given name is null.
     */
    private void editCurrentSeedEntryName(String name) throws IllegalStateException, IllegalArgumentException {
        if (name == null || name.equals("")) {
            throw new IllegalArgumentException("The given name is invalid.");
        } else if (SeedConfig.getCurrentSeed() == null) {
            throw new IllegalStateException("Please select a Seed before doing this.");
        } else if (hasAlreadySeedName(name)) {
            throw new IllegalStateException("This name already exists.");
        }
        SeedConfig.getCurrentSeed().setName(name);
        m_seedConfig.setProperty(SeedConfig.getCurrentSeed().getNumber() + "#" + SeedConfig.getCurrentSeed().getRoadType()
                , SeedConfig.getCurrentSeed().getName());
        SeedConfig.saveConfig();
    }

    /**
     * Sets the Textfield for editing the seed name to true and fills it with the currentlty selected seed of the drop
     * down menu.
     */
    public void editSeednameTextfieldVisibility() {
        if (m_editSeednameTextfieldElement.isVisible()) { // if it is already visible then set it invisible.
            m_editSeednameTextfieldElement.setVisible(false);
            if (m_dropDown.getElement() != null) {
                m_dropDown.getElement().setVisible(true); // and set the drop down menu to visible again
            }
        } else if (m_dropDown.getSelectedIndex() == -1) { // Show popup if there is no selected seed in the dropdown
            createPopupMessage(m_bundleInfo.getString("pleaseSelectaSeed",
                    Locale.forLanguageTag(m_graphicsConfig.getProperty("language"))));
            return;
        } else {
            // Set the current text
            m_editSeednameTextfield.setText(m_dropDown.getItems().get(m_dropDown.getSelectedIndex()));
            if (m_editSeednameTextfield.getRealText().equals("")) {
                m_editSeedNameButton.disableFocus();
                createPopupMessage(m_bundleInfo.getString("pleaseSelectaSeed",
                        Locale.forLanguageTag(m_graphicsConfig.getProperty("language"))));
                return;
            }
            if (m_dropDown.getElement() != null) {
                m_dropDown.getElement().setVisible(false);
                m_editSeednameTextfieldElement.setVisible(true);
            }
        }
        m_editSeedNameButton.setVisible(false);
        m_saveSeedNameButton.setVisible(true);
    }

    /**
     * Saves the typed in number into the current seed.
     */
    public void saveSeedNumber() {
        String roadTypeResourceKey = transformRaodTypeToResourceKey();
        if (roadTypeResourceKey != null) {
            editCurrentSeedEntry(new SeedEntry(m_seedNumberTextfield.getRealText(), m_seedNumberTextfield.getRealText(),
                    roadTypeResourceKey));
        }
        m_saveSeedNumberButton.setVisible(false);
    }

    /**
     * Sets the current seed to the given one.
     * @param seed The seed to be set.
     * @throws IllegalStateException If there is no current seed selected.
     * @throws IllegalArgumentException If the given seed is null.
     */
    private void editCurrentSeedEntry(SeedEntry seed) throws IllegalStateException, IllegalArgumentException {
        if (seed == null) {
            throw new IllegalArgumentException();
        }
        SeedConfig.setCurrentSeed(seed);
    }

    /**
     * Updates the HashMap.
     */
    private void updateAllSeeds() {
        m_allSeeds = new HashMap<>();
        transformConfigtoHashmap();
    }

    /**
     * Returns a sorted List of Strings containing all seed names.
     * @return The sorted List.
     * @throws IllegalStateException If there is no saved seed.
     */
    private List<String> getSortedSeedNames() throws IllegalStateException {
        if (m_seedConfig.isEmpty()) {
            throw new IllegalStateException("Seed List is empty.");
        }
        List<String> sortedSeedEntries = new LinkedList<>(m_allSeeds.values());
        Collections.sort(sortedSeedEntries);
        return sortedSeedEntries;
    }

    /**
     * Transform the Properties Object to a HashMap and saves it.
     */
    private void transformConfigtoHashmap() {
        for (Map.Entry<Object, Object> entry : m_seedConfig.entrySet()) {
            m_allSeeds.put((String) entry.getKey(), (String) entry.getValue());
        }
    }

    /**
     * Sets the current seed equivalent for the selected name in the drop down. If it finds it.
     * @param seedName The name to be seacrhed in saved seeds.
     * @return True if name was found in saved seeds. False otherwise.
     */
    private boolean setSeedentryforgivenName(String seedName) {
        for (Map.Entry<String, String> entry : m_allSeeds.entrySet()) {
            if (entry.getValue().equals(seedName)) {
                String[] parts = entry.getKey().split("#");
                if (parts.length == 1) {
                    editCurrentSeedEntry(new SeedEntry(entry.getValue(), parts[0]));
                    return true;
                } else if (parts.length == 2) {
                    editCurrentSeedEntry(new SeedEntry(entry.getValue(), parts[0], parts[1]));
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Gets the corresponding number of a seed for a given name.
     * The first part of the string will be the actual number and the second part will be the road type.
     * @param seedName The selected name.
     * @return String "Err: Corresponding Number not found" if not found. If found then return the number.
     */
    private String[] getSeednumberforgivenName(String seedName) {
        String res = "Err: Corresponding Number not found";
        for (Map.Entry<String, String> entry : m_allSeeds.entrySet()) {
            if (entry.getValue().equals(seedName)) {
                res = entry.getKey();
            }
        }
        String[] parts = res.split("#");
        return parts;
    }

    private String transformRaodTypeToResourceKey() {
        for (Map.Entry<String, String> entry : m_allRoadTypes.entrySet()) {
            if (entry.getKey().equals(m_roadTypeDropDown.getSelection())) {
                return entry.getValue();
            }
        }
        return null;
    }

    /**
     * Checks if the seed list has already the given name.
     * @param seedName
     * @return
     */
    private boolean hasAlreadySeedName(String seedName) {
        boolean result = false;
        for (Map.Entry<String, String> entry : m_allSeeds.entrySet()) {
            if (entry.getValue().equals(seedName) && (!m_dropDown.getSelection().equals(seedName))) {
                result = true;
            }
        }
        return result;
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
        m_seedNumberTextfield = m_screen.findNiftyControl("editSeedNumber", TextField.class);
        m_seedNumberTextfield.enableInputFilter(new FilterAcceptRegex("\\d+"));
        m_editSeedNameButton = m_screen.findElementById("editSeedNameButton");
        m_saveSeedNameButton = m_screen.findElementById("saveSeedNameButton");
        m_saveSeedNumberButton = m_screen.findElementById("saveSeedNumberButton");
        m_kiTextfield =  m_screen.findNiftyControl("editKICount", TextField.class);
        m_kiTextfield.enableInputFilter(new FilterAcceptRegex("[0-9]{1}"));
        m_kiTextfield.setText(m_internalGameSettings.getProperty("KICount"));
        m_playerTextfield =  m_screen.findNiftyControl("editPlayerCount", TextField.class);
        m_playerTextfield.enableInputFilter(new FilterAcceptRegex("[1-4]{1}"));
        m_playerTextfield.setText(m_internalGameSettings.getProperty("PlayerCount"));
        m_editSeednameTextfield = m_screen.findNiftyControl("editSeedNameField", TextField.class);
        m_editSeednameTextfield.enableInputFilter(new FilterAcceptRegex(".+"));
        m_editSeednameTextfieldElement = m_editSeednameTextfield.getElement();
        m_dropDown =  m_screen.findNiftyControl("seedDropDown", DropDown.class);
        if (m_dropDown != null) {
            m_dropDown.clear();
            fillSeedDropDown();
        }
        m_roadTypeDropDown =  m_screen.findNiftyControl("roadTypeDropDown", DropDown.class);
        m_roadTypeDropDownElement = m_screen.findElementById("languageDropDown");
        fillRoadTypeDropDown();
        m_startButton = m_screen.findElementById("StartButton");
    }

    /**
     * called right after the onStartScreen event ENDED.
     */
    @Override
    public void onStartScreen() {
        if (!m_startButton.isVisible()) {
            m_startButton.setVisible(true);
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
        if (inputEvent.equals(NiftyStandardInputEvent.SubmitText) && m_editSeednameTextfieldElement.isVisible()
                && m_editSeednameTextfield.hasFocus()) {
            saveSeedName();
            return true;
        } else if (inputEvent.equals(NiftyStandardInputEvent.SubmitText) && m_saveSeedNumberButton.isVisible()
                && m_seedNumberTextfield.hasFocus()) {
            saveSeedNumber();
            return true;
        } else if (inputEvent.equals(NiftyStandardInputEvent.SubmitText) && m_kiTextfield.hasFocus()) {
            m_startButton.setFocus();
            return true;
        } else if (inputEvent.equals(NiftyStandardInputEvent.SubmitText) && m_playerTextfield.hasFocus()) {
            m_kiTextfield.setFocus();
            return true;
        }
        return false;
    }
}
