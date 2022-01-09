package edu.kit.valaris.menu.gui;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.math.ColorRGBA;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.PanelRenderer;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.input.NiftyInputEvent;
import de.lessvoid.nifty.screen.KeyInputHandler;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.tools.Color;
import edu.kit.valaris.BaseApplication;
import edu.kit.valaris.datastructure.ResultObject;
import edu.kit.valaris.menu.menuconfig.SeedConfig;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.annotation.Nonnull;

/**
 * This class controls the graphical user interface of the Endmenu Screen.
 * This includes the provision of information which screen is currently opened.
 * In order to do this the current screen will be attached/detached to/of the StateManager.
 * It inherits OwnScreenController to control the Nifty Screens and implements the Interface KeyInputHandler for
 * being able to react to User Input and Actions.
 * @author Artur Wesner
 */
public class EndmenuScreen extends OwnScreenController implements KeyInputHandler {
    /**
     * Path where the correspondending .xml file is located.
     */
    public static final String m_xmlPath = "edu.kit.valaris/menu/EndScreen.xml";

    /**
     * The screem id which is defined in the .xml file.
     */
    public static final String m_screenID = "endscreen";

    /**
     * Hashmap containing driven times of all players.
     */
    private Map<String, Integer> m_playerEndTimes;

    /**
     * Saves all Element objects defined for players in the xml file.
     */
    private Element[] m_playerElements = new Element[10];

    /**
     * Saves als TextRenderer of the time displayed on the screen.
     */
    private TextRenderer[] m_playerTimesEndScreen = new TextRenderer[10];

    /**
     * Saves als TextRenderer of the player text displayed on the screen.
     */
    private TextRenderer[] m_playerTextEndScreen = new TextRenderer[10];

    /**
     * Saves all the PanelRenderer of the player's colors displayed on the screen.
     */
    private PanelRenderer[] m_playerColorsEndSceen = new PanelRenderer[10];

    /**
     * Saves alle the player informations when the races finishes.
     */
    private List<Object> m_playerInformations;

    /**
     * Constructor calling the super constructor with his static variables and setting the private references.
     * @param stateManager The applications statemanager.
     * @param app The application.
     */
    public EndmenuScreen(AppStateManager stateManager, Application app, List<Object> playerInformations) {
        super(m_screenID, m_xmlPath);
        m_app = (BaseApplication) app;
        m_stateManager = stateManager;
        m_playerInformations = playerInformations;
    }

    /**
     * Goes back to main menu.
     */
    @SuppressWarnings("all")
    public void backToMainmenu() {
        m_app.enqueue(() -> {
            for (OwnScreenController current : getOtherController()) {
                if (current.getScreenID().equals(MainmenuScreen.m_screenID)) {
                    m_nifty.gotoScreen(MainmenuScreen.m_screenID);
                    return;
                }
            }
            MainmenuScreen mainmenuScreen = new MainmenuScreen(m_stateManager, m_app);
            SinglePlayerModeScreen single = new SinglePlayerModeScreen(m_stateManager, m_app);
            m_stateManager.detach(getNiftyAppState());

            LoadingUtil lu = new LoadingUtil(m_app, m_stateManager);
            lu.shutDownAndSwitchScreen("MainnSingle", mainmenuScreen, single);
        });
    }

    /**
     * Opens the loading screen again in order to play the game again.
     */
    public void playAgain() {
        m_app.enqueue(() -> {
            LoadingUtil lu = new LoadingUtil(m_app, m_stateManager);
            lu.shutDownAndSwitchScreen("newLoading", new LoadingScreen(new RaceSetup(), m_stateManager, m_app));
        });
    }

    /**
     * Saves the current seed which is played.
     */
    public void saveSeed() {
        if (m_seedConfig.containsValue(SeedConfig.getCurrentSeed().getName())
                || m_seedConfig.containsValue(SeedConfig.getCurrentSeed().getName()
                + "#" + SeedConfig.getCurrentSeed().getRoadType())) {
            createPopupMessage(m_bundleInfo.getString("seedalreadysaved",
                    Locale.forLanguageTag(m_graphicsConfig.getProperty("language"))));
            return;
        }
        if (SeedConfig.addSeed(SeedConfig.getCurrentSeed())) {
            createPopupMessage(m_bundleInfo.getString("successfullSave",
                    Locale.forLanguageTag(m_graphicsConfig.getProperty("language")))
                    + SeedConfig.getCurrentSeed().getName() + "#" + SeedConfig.getCurrentSeed().getRoadType());
        } else {
            createPopupMessage(m_bundleInfo.getString("notsuccessfullSave",
                    Locale.forLanguageTag(m_graphicsConfig.getProperty("language"))));
        }
    }

    /**
     * Updates all the player informations available in the screen.
     */
    private void updateAllPlayerInformations() {
        if (m_playerInformations != null) {
            Iterator<Object> iterator = m_playerInformations.iterator();
            while (iterator.hasNext()) {
                ResultObject resultObject = (ResultObject) iterator.next();
                updatePlayerEndTimeAndColor(
                        resultObject.getRanking() -1,
                        resultObject.isPlayer(),
                        resultObject.getFinishTime(),
                        resultObject.getColor());
            }
        }
    }

    /**
     * Updates a player finish time and color which will be visualized after the game has ended.
     * @param player The player id between 0 and 9.
     * @param timeInSeconds The time in second.
     * @param color The color.
     * @throws IllegalArgumentException If the player id isn't in between 0-9 or the color format has no # in the
     *                                  beginning
     */
    private void updatePlayerEndTimeAndColor(int player, boolean isHuman,
                                            float timeInSeconds, ColorRGBA color) throws IllegalArgumentException {
        if (player < 0 || player > 9) {
            throw  new IllegalArgumentException("Argument player must be between 0 and 9.");
        }
        m_playerElements[player].show();
        if(!isHuman) {
            m_playerTextEndScreen[player].setText("KI");
        }
        m_playerColorsEndSceen[player].setBackgroundColor(new Color(toHex(color.getRed(), color.getGreen(), color.getBlue())));
        m_playerTimesEndScreen[player].setText(getTime(timeInSeconds));
    }

    private String toHex(float red, float green, float blue) {
        return "#" + String.format("%02X", (int)(red * 255f))
                + String.format("%02X", (int)(green * 255f))
                + String.format("%02X", (int)(blue * 255f));
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
        super.bind(nifty, screen);
        for (int i = 0; i < m_playerElements.length; i++) {
            m_playerElements[i] = m_screen.findElementById("player" + String.valueOf((i + 1)));
            m_playerTimesEndScreen[i] =
                    m_screen.findElementById("player" + String.valueOf((i + 1)) + "Time").getRenderer(TextRenderer.class);
            m_playerTextEndScreen[i] =
                    m_screen.findElementById("player" + String.valueOf((i + 1)) + "Text").getRenderer(TextRenderer.class);
            m_playerColorsEndSceen[i] =
                    m_screen.findElementById("player" + String.valueOf((i + 1)) + "Color").getRenderer(PanelRenderer.class);
        }
    }

    /**
     * called right after the onStartScreen event ENDED.
     */
    @Override
    public void onStartScreen() {
        if (m_app.getInputManager() != null) {
            m_app.getInputManager().setCursorVisible(true);
        }
        updateAllPlayerInformations();
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
