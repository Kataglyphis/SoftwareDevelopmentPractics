package edu.kit.valaris.rendering;

import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.light.DirectionalLight;
import com.jme3.light.LightList;
import com.jme3.math.Vector3f;
import com.jme3.post.Filter;
import com.jme3.post.FilterPostProcessor;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Node;
import com.jme3.shadow.DirectionalLightShadowFilter;
import edu.kit.valaris.Metadata;
import edu.kit.valaris.menu.gui.Hud;
import edu.kit.valaris.menu.gui.NiftyAppState;
import edu.kit.valaris.menu.menuconfig.GraphicsConfig;
import edu.kit.valaris.rendering.tick.TickProcessor;

/**
 * {@link com.jme3.app.state.AppState} that manages rendering during the race.
 *
 * @author Frederik Lingg
 */
public class SceneManager extends AbstractAppState {

    public enum SplitScreenMode {
        /**
         * Single viewport over the whole screen.
         */
        MODE_SINGLE(1, 0.0f, 0.0f, 1.0f, 1.0f),

        /**
         * 2 viewports, Screen split horizontally.
         */
        MODE_DOUBLE_HORIZONTAL(2, 0.0f, 0.5f, 1.0f, 1.0f,
                0.0f, 0.0f, 1.0f, 0.5f),

        /**
         * 2 viewports, Screen split vertically.
         */
        MODE_DOUBLE_VERTICAL(2, 0.0f, 0.0f, 0.5f, 1.0f,
                0.5f, 0.0f, 1.0f, 1.0f),

        /**
         * 4 viewports, screen split horizontally and vertically.
         */
        MODE_TRIPLE(3, 0.0f, 0.5f, 0.5f, 1.0f,
                0.5f, 0.5f, 1.0f, 1.0f,
                0.0f, 0.0f, 0.5f, 0.5f),

        /**
         * 4 viewports, screen split horizontally and vertically.
         */
        MODE_QUAD(4, 0.0f, 0.5f, 0.5f, 1.0f,
                0.5f, 0.5f, 1.0f, 1.0f,
                0.0f, 0.0f, 0.5f, 0.5f,
                0.5f, 0.0f, 1.0f, 0.5f);

        /**
         * The parameters for the size of the viewports.
         */
        private float[] m_vpParams;

        /**
         * How many views there are.
         */
        private int m_count;

        /**
         * Creates a new {@link SplitScreenMode} with the given params.
         * @param vpParams the width and height of all viewports to create.
         */
        SplitScreenMode(int count, float... vpParams) {
            m_count = count;
            m_vpParams = vpParams;
        }

        /**
         * How many {@link com.jme3.renderer.ViewPort}s there need to be.
         * @return how many {@link com.jme3.renderer.ViewPort}s are needed.
         */
        public int getCount() {
            return m_count;
        }

        /**
         * The parameters for the viewports that are used.
         * @return the corners of the {@link com.jme3.renderer.ViewPort}s
         */
        public float[] getViewPortParams() {
            return m_vpParams;
        }
    }

    /**
     * How many views there are.
     */
    private SplitScreenMode m_mode;

    /**
     * The ids of the created {@link com.jme3.renderer.ViewPort}s.
     */
    private int[] m_viewPorts;

    /**
     * The Viewport used to clear the screen.
     */
    private int m_clearVP;
    private Node m_clearRoot;

    private ViewPortManager m_viewPortManager;

    private NiftyAppState[] m_huds;
    private Hud[] m_hudControllers;

    /**
     * The root of the scene.
     */
    private Node m_root;

    /**
     * Creates a new {@link SceneManager}.
     * @param mode the {@link SplitScreenMode} to use for rendering
     */
    public SceneManager(SplitScreenMode mode) {
        m_mode = mode;
    }

    /**
     * Accesses the {@link ViewPort} of the given player
     * @param player the player
     * @return the {@link ViewPort} of the given player
     */
    public ViewPort getViewPort(int player) {
        return m_viewPortManager.getViewPort(m_viewPorts[player]);
    }

    /**
     * Accesses the {@link Hud} of the given player
     * @param player the player
     * @return the {@link Hud} of the given player.
     */
    public Hud getHud(int player) {
        return m_hudControllers[player];
    }

    @Override
    public void stateAttached(AppStateManager stateManager) {
        //create root node
        m_root = new Node("Scene Root");

        //attach statics
        CullableRegister cullables = stateManager.getState(CullableRegister.class);
        if (cullables != null) {
            Node cullRoot = cullables.getCullRoot();

            //copy lights to root node
            LightList lights = cullRoot.getLocalLightList();
            for(int i = 0; i < lights.size(); i++) {
                m_root.addLight(lights.get(i));
            }

            //remove lights from cullroot
            lights = m_root.getLocalLightList();
            for(int i = 0; i < lights.size(); i++) {
                cullRoot.removeLight(lights.get(i));
            }
            cullRoot.setShadowMode(RenderQueue.ShadowMode.Receive);
            m_root.attachChild(cullRoot);
        } else {
            throw new IllegalStateException("Did not find CullableRegister");
        }
        //attach dynamics
        TickProcessor dynamics = stateManager.getState(TickProcessor.class);
        if (dynamics != null) {
            dynamics.getDynamicsRoot().setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
            m_root.attachChild(dynamics.getDynamicsRoot());
        } else {
            throw new IllegalStateException("Did not find TickProcessor");
        }

        //update root
        m_root.updateLogicalState(0);
        m_root.updateGeometricState();

        //find viewportmanager and metadata
        m_viewPortManager = stateManager.getState(ViewPortManager.class);
        Metadata metadata = stateManager.getState(Metadata.class);
        if (m_viewPortManager != null && metadata != null) {
            //create clear viewport
            m_clearRoot = new Node("Clear Root");
            m_clearRoot.updateLogicalState(0);
            m_clearRoot.updateGeometricState();
            m_clearVP = m_viewPortManager.addViewPort("Clear", ViewPortManager.ViewPortType.TYPE_SCENE, m_clearRoot,
                    0, 0, 1, 1);

            //create viewports (2 for each player, sky and scene)
            m_viewPorts = new int[m_mode.getCount()];
            m_huds = new NiftyAppState[m_mode.getCount()];
            m_hudControllers = new Hud[m_mode.getCount()];
            DirectionalLight light0;
            DirectionalLight light1;
            DirectionalLight light2;
            DirectionalLight light3;
            DirectionalLight[] allLights = null;
            switch (GraphicsConfig.getConfig().getProperty("quality")) {
                case "low":
                    break;
                default:
                    light0 = new DirectionalLight(new Vector3f(0.7f, -1.0f, 0.7f).normalizeLocal());
                    light1 = new DirectionalLight(new Vector3f(-0.7f, -1.0f, 0.7f).normalizeLocal());
                    light2 = new DirectionalLight(new Vector3f(0.7f, -1.0f, -0.7f).normalizeLocal());
                    light3 = new DirectionalLight(new Vector3f(-0.7f, -1.0f, -0.7f).normalizeLocal());
                    allLights = new DirectionalLight[]{light0, light1, light2, light3};
            }
            for (int i = 0; i < m_mode.getCount(); i++) {
                //create scene viewport
                m_viewPorts[i] = m_viewPortManager.addViewPort("Scene-Player-" + i, ViewPortManager.ViewPortType.TYPE_SCENE, m_root,
                        m_mode.getViewPortParams()[i * 4 + 0],
                        m_mode.getViewPortParams()[i * 4 + 1],
                        m_mode.getViewPortParams()[i * 4 + 2],
                        m_mode.getViewPortParams()[i * 4 + 3]);

                //create hud for player
                m_hudControllers[i] = new Hud(stateManager, metadata.getApp());
                m_huds[i] = new NiftyAppState("Hud-Player-" + i,
                        m_mode.getViewPortParams()[i * 4 + 0],
                        m_mode.getViewPortParams()[i * 4 + 1],
                        m_mode.getViewPortParams()[i * 4 + 2],
                        m_mode.getViewPortParams()[i * 4 + 3],
                        m_hudControllers[i]);
                stateManager.attach(m_huds[i]);

                //create culling manager for sceneViewport
                m_viewPortManager.getViewPort(m_viewPorts[i]).addProcessor(new CullingManager(cullables.getCullRoot()));

                // create shadows
                FilterPostProcessor fpp;
                switch (GraphicsConfig.getConfig().getProperty("quality")) {
                    case "low":
                        break;
                    case "high":
                        if (allLights != null) {
                            fpp = new FilterPostProcessor(stateManager.getApplication().getAssetManager());
                            m_viewPortManager.getViewPort(m_viewPorts[i]).addProcessor(fpp);
                            for (int j = 0; j < allLights.length; j++) {
                                DirectionalLightShadowFilter dlsf =
                                        new DirectionalLightShadowFilter(stateManager.getApplication().getAssetManager(),
                                                1024, 3);
                                dlsf.setLight(allLights[j]);
                                dlsf.setShadowIntensity(.2f);
                                fpp.addFilter(dlsf);
                            }
                        }
                        break;
                    default:
                        if (allLights != null) {
                            fpp = new FilterPostProcessor(stateManager.getApplication().getAssetManager());
                            m_viewPortManager.getViewPort(m_viewPorts[i]).addProcessor(fpp);
                            for (int j = 0; j < allLights.length; j++) {
                                DirectionalLightShadowFilter dlsf =
                                        new DirectionalLightShadowFilter(stateManager.getApplication().getAssetManager(),
                                                512, 2);
                                dlsf.setLight(allLights[j]);
                                dlsf.setShadowIntensity(.2f);
                                fpp.addFilter(dlsf);
                            }
                        }

                }

            }
        } else {
            throw new IllegalStateException("Did not find ViewPortManager");
        }

        //hide cursor
        stateManager.getState(Metadata.class).getApp().getInputManager().setCursorVisible(false);
    }

    @Override
    public void stateDetached(AppStateManager stateManager) {
        ViewPortManager viewPortManager = stateManager.getState(ViewPortManager.class);
        if (viewPortManager != null) {
            //destroy viewports
            viewPortManager.removeViewPort(m_clearVP);
            for (int i = 0; i < m_viewPorts.length; i++) {
                viewPortManager.removeViewPort(m_viewPorts[i]);
            }

            //destroy huds
            for (NiftyAppState hud : m_huds) {
                stateManager.detach(hud);
            }
        } else {
            throw new IllegalStateException("No ViewPortManager found");
        }

        //Show Cursor
        stateManager.getState(Metadata.class).getApp().getInputManager().setCursorVisible(true);
    }

    @Override
    public void update(float tpf) {
        //update scenegraph
        m_root.updateLogicalState(tpf);
        m_root.updateGeometricState();
    }
}
