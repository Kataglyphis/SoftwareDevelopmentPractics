package edu.kit.valaris.menu.gui;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.ImageRenderer;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.tools.Color;
import edu.kit.valaris.BaseApplication;
import edu.kit.valaris.tick.ItemType;
import edu.kit.valaris.tick.Ticker;

import javax.annotation.Nonnull;

/**
 * This class controls the graphical user interface of the Hud Screen.
 * This includes the provision of information which screen is currently opened.
 * In order to do this the current screen will be attached/detached to/of the StateManager.
 * It inherits OwnScreenController to control the Nifty Screens.
 * @author Artur Wesner
 */
public class Hud extends OwnScreenController {
    /**
     * Path where the correspondending .xml file is located.
     */
    public static final String m_xmlPath = "edu.kit.valaris/menu/Hud.xml";

    /**
     * The screem id which is defined in the .xml file.
     */
    public static final String m_screenID = "hud";

    /**
     * Reference to the renderer for the time text in the hud screen.
     */
    private TextRenderer m_timeElement;

    /**
     * Reference to the renderer for the rank text in the hud screen.
     */
    private TextRenderer m_rankElement;

    /**
     * Reference to the renderer for the countdown text in the hud screen.
     */
    private TextRenderer m_countdown;

    /**
     * Reference to the element for the item in the hud screen.
     */
    private Element m_shieldItem;

    /**
     * Reference to the element for the item in the hud screen.
     */
    private Element m_rocketItem;

    /**
     * Reference to the element for the item in the hud screen.
     */
    private Element m_gravitationItem;

    /**
     * Reference to the current item in the hud screen.
     */
    private Element currentElement;

    /**
     * Saves a boolean whether the countdown element is hidden or not.
     */
    private boolean m_isCountdownHidden = false;

    /**
     * Constructor calling the super constructor with his static variables and setting the private references.
     * @param stateManager The applications statemanager.
     * @param app The application.
     */
    public Hud(AppStateManager stateManager, Application app) {
        super(m_screenID, m_xmlPath);
        m_app = (BaseApplication) app;
        m_stateManager = stateManager;
    }

    /**
     * Updates the OwnScreencontroller.
     *
     * @param tpf tpf.
     */
    @Override
    public void updateOwnScreencontroller(float tpf) {
    }

    /**
     * Updates the Rank Text in the hud screen with the given parameter.
     * @param newRank The new rank to set.
     */
    public void updateRank(int newRank) {
        if (newRank == 1) {
            m_rankElement.setColor(new Color("#FFD700"));
        } else if (newRank == 2) {
            m_rankElement.setColor(new Color("#9a9a9a"));
        } else if (newRank == 3) {
            m_rankElement.setColor(new Color("#CD7F32"));
        } else {
            m_rankElement.setColor(new Color("#FFFFFF"));
        }

        m_rankElement.setText("" + newRank);
    }

    /**
     * Updates the driven time of the player displayed in the hud screen.
     * @param time The time to set.
     */
    public void updateTime(float time) {
        m_timeElement.setText(getTime(time));
    }

    /**
     * Updates the Countdown Text in the hud screen with the given parameter.
     * @param newCount The new count to set.
     */
    public void updateCountdown(String newCount) {
        if (m_isCountdownHidden) {
            m_isCountdownHidden = false;
            m_screen.findElementById("countdown").show();
        }
        m_countdown.setText(newCount);
    }

    /**
     * Updates the Item Text in the hud screen with the given parameter.
     * @param newItem The new item to set.
     */
    public void updateItem(String newItem) {
        if (newItem.equals(ItemType.HOMING_MISSILE.name())) {
            currentElement.hide();
            m_rocketItem.show();
            currentElement = m_rocketItem;
        } else if (newItem.equals(ItemType.GRAVITATION_TRAP.name())) {
            currentElement.hide();
            m_gravitationItem.show();
            currentElement = m_gravitationItem;
        } else if (newItem.equals(ItemType.SHIELD.name())) {
            currentElement.hide();
            m_shieldItem.show();
            currentElement = m_shieldItem;
        } else {
            currentElement.hide();
        }
    }

    /**
     * Hides the countdown element.
     */
    public void hideCountdown() {
        m_screen.findElementById("countdown").hide();
        m_isCountdownHidden = true;
    }

    /**
     * Return a boolean whether the countdown element is hidden or not.
     * @return True if hidden, false otherwise.
     */
    public boolean isCountdownHidden() {
        return m_isCountdownHidden;
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
        m_timeElement = m_screen.findElementById("timer").getRenderer(TextRenderer.class);
        m_rankElement = m_screen.findElementById("currentRank").getRenderer(TextRenderer.class);
        m_countdown = m_screen.findElementById("countdown").getRenderer(TextRenderer.class);
        m_gravitationItem = m_screen.findElementById("gravitationItem");
        m_rocketItem = m_screen.findElementById("rocketItem");
        m_shieldItem = m_screen.findElementById("shieldItem");
        m_gravitationItem.hide();
        m_shieldItem.hide();
        m_rocketItem.hide();
        currentElement = m_gravitationItem;
    }

    /**
     * called right after the onStartScreen event ENDED.
     */
    @Override
    public void onStartScreen() {
        m_timeElement.setText("");
    }

    /**
     * called right after the onEndScreen event ENDED.
     */
    @Override
    public void onEndScreen() {
    }

}
