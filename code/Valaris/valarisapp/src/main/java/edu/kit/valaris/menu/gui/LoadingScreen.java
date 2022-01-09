package edu.kit.valaris.menu.gui;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.scene.Node;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.screen.Screen;
import edu.kit.valaris.BaseApplication;
import edu.kit.valaris.datastructure.IMapBody;
import edu.kit.valaris.menu.controls.ProgressBar;
import edu.kit.valaris.rendering.RenderUtil;
import edu.kit.valaris.rendering.SceneManager;
import javax.annotation.Nonnull;

/**
 * This class controls the graphical user interface of the loading screen.
 * This includes the provision of information which screen is currently opened.
 * In order to do this the current screen will be attached/detached to/of the StateManager.
 * It inherits NiftyAppstate get access to needed methods. In order to control the Nifty Screens and implements the
 * Interface ScreenController.
 * @author Artur Wesner, Frederik Lingg
 */
public class LoadingScreen extends OwnScreenController {

    /**
     * Used to setup a simulation after loading.
     */
    public interface SimulationSetup {

        /**
         * Sets up the simulation with the given {@link IMapBody}.
         * @param mapbody the map to use in the simulation.
         */
        public void setup(IMapBody mapbody, AppStateManager stateManager);
    }

    /**
     * Path where the correspondending .xml file is located.
     */
    public static final String m_xmlPath = "edu.kit.valaris/menu/LoadingScreen.xml";

    /**
     * The screen id which is defined in the .xml file.
     */
    public static final String m_screenID = "loadingscreen";

    /**
     * Reference to the progressbar.
     */
    private ProgressBar m_progressBar;

    /**
     * The {@link SimulationSetup} to use.
     */
    private SimulationSetup m_setup;

    /**
     * Saves the current split screen mode
     */
    private SceneManager.SplitScreenMode m_splitScreenMode;

    /**
     * Constructor calling the super constructor and initializing the split screen mode depending on the
     * player count.
     */
    public LoadingScreen(SimulationSetup setup, AppStateManager stateManager, Application app) {
        super(m_screenID, m_xmlPath);
        m_app = (BaseApplication) app;
        m_stateManager = stateManager;
        m_setup = setup;
        switch (m_internalGameSettings.getProperty(("PlayerCount"))) {
            case "2":
                m_splitScreenMode = SceneManager.SplitScreenMode.MODE_DOUBLE_VERTICAL;
                break;
            case "3":
                m_splitScreenMode = SceneManager.SplitScreenMode.MODE_TRIPLE;
                break;
            case "4":
                m_splitScreenMode = SceneManager.SplitScreenMode.MODE_QUAD;
                break;
            default:
                m_splitScreenMode = SceneManager.SplitScreenMode.MODE_SINGLE;
        }
    }

    /**
     * Initializes in a new Thread all needed AppStates while the game is running.
     * Sets the progress depending on what is already initialized and sets the corresponding text in the progressbar.
     */
    public void startGame() {
        LoadingUtil loadingUtil = new LoadingUtil(m_app, m_stateManager);
        final Node staticsRoot = new Node();

        LoadingCallback mainCallback = new LoadingCallback() {
            @Override
            public void setProgress(double progress, String message) {
                m_progressBar.setProgress((float) progress);
                m_progressBar.setText(message);
            }

            @Override
            public void finished(IMapBody body) {
                //when loading is done, init and start rendering synchronously to the renderthread
                m_app.enqueue(() -> {
                    //hide loadingscreen before starting rendering systems
                    m_stateManager.detach(getNiftyAppState());

                    RenderUtil.setupRendering(staticsRoot, m_splitScreenMode, m_stateManager);

                    m_setup.setup(body, m_stateManager);
                });
            }
        };

        //start loading process
        new Thread(() -> loadingUtil.load(staticsRoot, mainCallback)).start();
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
        m_progressBar = screen.findNiftyControl("progressbar", ProgressBar.class);
        startGame();
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
