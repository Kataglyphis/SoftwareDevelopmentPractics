package edu.kit.valaris.menu.gui;

import com.jme3.app.state.AppStateManager;
import com.jme3.scene.Node;
import edu.kit.valaris.BaseApplication;
import edu.kit.valaris.InputHandler;
import edu.kit.valaris.RaceAppState;
import edu.kit.valaris.assets.AssetProvider;
import edu.kit.valaris.datastructure.IMapBody;
import edu.kit.valaris.dummysim.SimulationDummy;
import edu.kit.valaris.generation.mapgeneration.MapGenerator;
import edu.kit.valaris.menu.menuconfig.InternalGameConfig;
import edu.kit.valaris.menu.menuconfig.SeedConfig;
import edu.kit.valaris.rendering.RenderUtil;
import edu.kit.valaris.tick.Ticker;

/**
 * Utility methods to initialize stuff in the loading screen.
 * @author Artur Wesner, Frederik Lingg
 */
public class LoadingUtil {
    /**
     * Reference to the application.
     */
    private BaseApplication m_app;

    /**
     * Reference to the statemanager.
     */
    private AppStateManager m_stateManager;

    /**
     * Constructor initializing the variables.
     * @param app The app.
     * @param stateManager The stateManager.
     */
    public LoadingUtil(BaseApplication app, AppStateManager stateManager) {
        m_app = app;
        m_stateManager = stateManager;
    }

    /**
     * Loads allneeded Assets and generates a map in the given node.
     * @param sceneRoot the {@link Node} the map should be placed in.
     * @param callback callback to update progress.
     */
    public void load(final Node sceneRoot, final LoadingCallback callback) {
        loadAssets(callback, 0.0f, 0.35f);
        IMapBody result = generateMap(sceneRoot, callback, 0.35f, 1.0f);
        callback.finished(result);
    }

    /**
     * Loads all necessary assets.
     * @param callback the {@link LoadingCallback} to set progress to.
     * @param minProgress the lower end of the interval.
     * @param maxProgress the higher end of the interval.
     */
    public void loadAssets(final LoadingCallback callback, final float minProgress, final float maxProgress) {
        edu.kit.valaris.assets.LoadingCallback assetsCallback = new edu.kit.valaris.assets.LoadingCallback() {
            @Override
            public void setProgress(double progress, String message) {
                callback.setProgress(minProgress + progress * (maxProgress - minProgress), message);
            }

            @Override
            public void finished() {
                //do nothing here
            }
        };

        //this is already called on a seperate thread, so no need to load asymchronously
        m_stateManager.getState(AssetProvider.class).load("/edu.kit.valaris/assets/AssetPack.json", assetsCallback);
    }

    /**
     * Generates the map for the race.
     * @param root the {@link Node} to save the map to.
     * @param callback the {@link LoadingCallback} to use to set progress.
     * @param minProgress the lower end of the interval.
     * @param maxProgress the higher end of the interval.
     */
    public IMapBody generateMap(final Node root, final LoadingCallback callback, final float minProgress, final float maxProgress) {
        LoadingCallback generationCallback = new LoadingCallback() {
            @Override
            public void setProgress(double progress, String message) {
                callback.setProgress(minProgress + progress * (maxProgress - minProgress), message);
            }

            @Override
            public void finished(IMapBody body) {
                //do nothing here
            }
        };


        generationCallback.setProgress(0, "generating Map");
        System.out.println("Current seed name: " + SeedConfig.getCurrentSeed().getName()
                        + "\nCurrent seed number: " + SeedConfig.getCurrentSeed().getNumber()
                        + "\nCurrent seed road type: " + SeedConfig.getCurrentSeed().getRoadType());
        MapGenerator mapGen = new MapGenerator(m_app.getAssetManager(), m_app.getStateManager(),
                            SeedConfig.getCurrentSeed().getRoadType() + "Map",
                Integer.parseInt(InternalGameConfig.getConfig().getProperty("KICount"))
                        + Integer.parseInt(InternalGameConfig.getConfig().getProperty("PlayerCount")));
        IMapBody result = mapGen.generate(SeedConfig.getCurrentSeed().getNumber().hashCode(), root, generationCallback);

        generationCallback.setProgress(1, "done generating map");

        return result;
    }

    /**
     * Destroys all attached States which were added in the process of starting the game from the loading screen
     * and attaches a new NiftyAppState with the given OwnScreenController.
     * @param viewPortTag The viewportTag to set.
     * @param ownScreenControllers The OwnScreenController.
     */
    public void shutDownAndSwitchScreen(String viewPortTag, OwnScreenController... ownScreenControllers) {
        m_app.enqueue(() -> {
            //stop running simulation if any
            //other team
            RaceAppState race = m_stateManager.getState(RaceAppState.class);
            if(race != null) {
                m_stateManager.detach(race);
            }
            InputHandler input = m_stateManager.getState(InputHandler.class);
            if(input != null) {
                m_stateManager.detach(input);
            }

            //simulationdummy
            SimulationDummy dummy = m_stateManager.getState(SimulationDummy.class);
            if(dummy != null) {
                m_stateManager.detach(dummy);
            }

            //drop ticker instance
            Ticker.drop();

            //remove rendering, if any
            //rendering
            try {
                RenderUtil.tearDownRendering(m_stateManager);
            } catch (IllegalStateException e) {
                //rendering was not initialized
            }

            //unload loaded assets
            m_stateManager.getState(AssetProvider.class).unloadModels();

            //remove any active gui
            //gui
            NiftyAppState gui = null;
            while((gui = m_stateManager.getState(NiftyAppState.class)) != null) {
                m_stateManager.detach(gui);
            }

            //open next gui
            m_stateManager.attach(new NiftyAppState(viewPortTag, ownScreenControllers));
        });
    }

    /**
     * Destroys all attached States which were added in the process of starting the game from the loading screen
     * and attaches a new NiftyAppState with the given OwnScreenController.
     * @param viewPortTag The viewportTag to set.
     * @param ownScreenControllers The OwnScreenController.
     */
    public void shutDownAndSwitchScreen(String viewPortTag, float minX, float minY, float maxX, float maxY,
                                        OwnScreenController... ownScreenControllers) {
        m_app.enqueue(() -> {
            m_stateManager.detach(m_stateManager.getState(NiftyAppState.class));
            m_stateManager.detach(m_stateManager.getState(SimulationDummy.class));
            RenderUtil.tearDownRendering(m_stateManager);
            Ticker.drop();
            m_stateManager.getState(AssetProvider.class).unloadModels();
            m_stateManager.attach(new NiftyAppState(viewPortTag, minX, minY, maxX, maxY, ownScreenControllers));
        });
    }
}
