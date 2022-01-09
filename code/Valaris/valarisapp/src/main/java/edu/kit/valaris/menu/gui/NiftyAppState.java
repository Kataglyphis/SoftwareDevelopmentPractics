package edu.kit.valaris.menu.gui;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.screen.ScreenController;
import edu.kit.valaris.BaseApplication;
import edu.kit.valaris.console.ConsoleManager;
import edu.kit.valaris.menu.menuconfig.GraphicsConfig;
import edu.kit.valaris.rendering.ViewPortManager;

import java.util.Locale;

/**
 * This class controls and initializes controller classes of the game's graphical user interface.
 * It inherits AbstractAppState in order to get access to the Game Application. This includes the provision of
 * information which screen is currently opened. In order to do this the current screen will be attached/detached
 * to/of the StateManager if needed. It implements the Interface ScreenController to control the Nifty Screens.
 * @author Artur Wesner
 */
public class NiftyAppState extends AbstractAppState {
    /**
     * Reference to the m_nifty object.
     */
    protected Nifty m_nifty;

    /**
     * Reference to the application's AppStateManager.
     */
    protected AppStateManager m_stateManager;

    /**
     * Reference to the application.
     */
    protected BaseApplication m_app;

    /**
     * Saves the created viewport id.
     */
    protected int m_vieportID;

    /**
     * Saves the created node.
     */
    protected Node m_root;

    /**
     * Saves the created Viewport for this NiftyAppstate.
     */
    protected ViewPort m_niftyViewPort;

    /**
     * Saves the minimum x variable which is needed when creating the Viewport.
     */
    protected float m_minX;

    /**
     * Saves the minimum y variable which is needed when creating the Viewport.
     */
    protected float m_minY;

    /**
     * Saves the maximum x variable which is needed when creating the Viewport.
     */
    protected float m_maxX;

    /**
     * Saves the maximum y variable which is needed when creating the Viewport.
     */
    protected float m_maxY;

    /**
     * Saves all OwnScreenController attached to this NiftyAppState.
     */
    protected OwnScreenController[] m_screenControllers;

    /**
     * Saves the created NiftyJmeDisplay.
     */
    protected NiftyJmeDisplay m_niftyDisplay;

    /**
     * The given viewportTag of this NiftyAppState.
     */
    private String m_viewPortTag;

    /**
     * The console status listener, used to prevent blocking of input for the console
     */
    private ConsoleManager.ConsoleStatusListener m_consoleListener = (visible) -> {
        if(m_nifty != null) {
            m_nifty.setIgnoreKeyboardEvents(visible);
        }
    };

    /**
     * Constructor saving the given screen controllers and initializing the minimum variables for the viewport with 0
     * and the maximum ones with 1.
     * @param viewPortTag The viewPortTag to set.
     * @param screenControllers The screencontrollers attached to this state.
     */
    public NiftyAppState(String viewPortTag, OwnScreenController... screenControllers) {
        m_minX = 0;
        m_minY = 0;
        m_maxX = 1;
        m_maxY = 1;
        m_screenControllers = screenControllers;
        m_viewPortTag = viewPortTag;
    }

    /**
     * Constructor saving the given screen controllers and initializing the minimum and maximum variables for the
     * viewport with the given arguments.
     * @param viewPortTag The viewPortTag to set.
     * @param minX The minimum x variable.
     * @param minY The minimum y variable.
     * @param maxX The maximum x variable.
     * @param maxY The maximum y variable.
     * @param screenControllers The screencontrollers attached to this state.
     */
    public NiftyAppState(String viewPortTag, float minX, float minY, float maxX, float maxY,
                         OwnScreenController... screenControllers) {
        m_minX = minX;
        m_minY = minY;
        m_maxX = maxX;
        m_maxY = maxY;
        m_screenControllers = screenControllers;
        m_viewPortTag = viewPortTag;
    }

    /**
     * Initializes the private attributes e.g. getting a new viewport. Initializes a new NiftyJmeDisplay, adds the
     * xml files, registers screencontroller and shows the screen of the first given controller.
     * @param stateManager The state manager.
     * @param app The application.
     */
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        m_stateManager = stateManager;
        m_app = (BaseApplication) app;

        m_root = new Node("Gui Root");
        // Create ViewPort and save the id.
        m_vieportID = m_stateManager.getState(ViewPortManager.class).addViewPort("Nifty" + m_viewPortTag,
                ViewPortManager.ViewPortType.TYPE_GUI, m_root, m_minX, m_minY, m_maxX, m_maxY);
        m_niftyViewPort = m_stateManager.getState(ViewPortManager.class).getViewPort(m_vieportID);
        // Create the nifty display.
        m_niftyDisplay = new NiftyJmeDisplay(m_app.getAssetManager(), m_app.getInputManager(),
                m_app.getAudioRenderer(), m_niftyViewPort);
        m_nifty = m_niftyDisplay.getNifty();
        // The screens were organised in 1280x720 resolution so auto scale from this!
        m_nifty.enableAutoScaling(1280,720);
        m_nifty.resolutionChanged();
        // Set the menulocal which is currently set.
        m_nifty.setLocale(Locale.forLanguageTag(GraphicsConfig.getConfig().getProperty("language")));
        // Adds the .xml where all Styles are loaded.
        m_nifty.addXml("edu/kit/valaris/menuSkin/OwnStyles/StyleDefine.xml");
        // For every given OwnScreenController set the niftyappstate reference, the othercontroller reference,
        // register the screen controller and add the xml.
        for (int i = 0; i < m_screenControllers.length; i++) {
            m_screenControllers[i].setNiftyAppState(this);
            m_nifty.registerScreenController((ScreenController) m_screenControllers[i]);
            m_nifty.addXml(m_screenControllers[i].getScreenXMLPath());
        }
        for (int i = 0; i < m_screenControllers.length; i++) {
            m_screenControllers[i].setOtherController(m_screenControllers);
        }
        // Go to the first screen given.
        m_nifty.gotoScreen(m_screenControllers[0].getScreenID());

        m_niftyViewPort.addProcessor(m_niftyDisplay);

        //if a console exists, add a listener
        ConsoleManager console = m_stateManager.getState(ConsoleManager.class);
        if(console != null) {
            console.addStatusListener(m_consoleListener);
        }
    }

    @Override
    public void stateDetached(AppStateManager stateManager) {
        super.stateDetached(stateManager);
    }

    /**
     * Cleans all initialized stuff.
     */
    @Override
    public void cleanup() {
        super.cleanup();
        for (int i = 0; i < m_screenControllers.length; i++) {
            m_nifty.unregisterScreenController(m_screenControllers[i]);
            m_nifty.removeScreen(m_screenControllers[i].getScreenID());
            m_screenControllers[i].onEndScreen();
        }
        m_nifty.exit();
        m_nifty.getEventService().clearAllSubscribers();
        m_niftyDisplay.cleanup();
        m_niftyViewPort.removeProcessor(m_niftyDisplay);
        m_stateManager.getState(ViewPortManager.class).removeViewPort(m_vieportID);

        //if a console exists, remove the listener
        ConsoleManager console = m_stateManager.getState(ConsoleManager.class);
        if(console != null) {
            console.removeStatusListener(m_consoleListener);
        }
    }

    /**
     * Calls updateLogicalState(), m_root.updateGeometricState() and updates all OwnScreenController
     * @param tpf tpf.
     */
    @Override
    public void update(float tpf) {
        m_root.updateLogicalState(tpf);
        m_root.updateGeometricState();

        for (int i = 0; i < m_screenControllers.length; i++) {
            m_screenControllers[i].updateOwnScreencontroller(tpf);
        }
    }
}
