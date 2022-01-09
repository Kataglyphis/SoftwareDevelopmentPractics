package edu.kit.valaris.rendering;

import com.jme3.app.state.AppStateManager;
import com.jme3.scene.Node;
import edu.kit.valaris.assets.AssetProvider;
import edu.kit.valaris.rendering.tick.*;
import edu.kit.valaris.rendering.tick.dynamics.properties.*;
import edu.kit.valaris.threading.JobManager;
import edu.kit.valaris.tick.TickEvent;
import edu.kit.valaris.tick.Ticker;
import edu.kit.valaris.tick.properties.CameraProperty;
import edu.kit.valaris.tick.properties.TransformProperty;
import edu.kit.valaris.tick.properties.VehicleProperty;
import edu.kit.valaris.tick.properties.VelocityProperty;

/**
 * Utility methods for rendering states.
 *
 * @author Frederik Lingg
 */
public class RenderUtil {

    /**
     * Sets up the Rendering environment.
     * @param splitScreenMode the {@link SceneManager.SplitScreenMode} to use.
     * @param stateManager the {@link AppStateManager} that hosts the rendering environment.
     */
    public static void setupRendering(Node staticScene, SceneManager.SplitScreenMode splitScreenMode, AppStateManager stateManager) {
        //add job manager
        int numThreads = Math.max(Runtime.getRuntime().availableProcessors() - 3, 1);
        JobManager.initInstance(numThreads, 100);
        stateManager.attach(JobManager.getInstance());

        //add Ticker
        stateManager.attach(Ticker.getInstance());

        //add processor factories
        PropertyProcessorFactoryRegister register = new PropertyProcessorFactoryRegister();
        register.setPropertyProcessorFactory(TransformProperty.class, new TransformPropertyProcessorFactory());
        register.setPropertyProcessorFactory(VehicleProperty.class, new VehiclePropertyProcessorFactory(stateManager.getState(AssetProvider.class)));
        register.setPropertyProcessorFactory(CameraProperty.class, new CameraPropertyProcessorFactory());
        register.setPropertyProcessorFactory(VelocityProperty.class, new VelocityPropertyProcessorFactory());
        stateManager.attach(register);

        //add TickProcessor
        TickProcessor tickProcessor = new TickProcessor();
        tickProcessor.setEventProcessor(TickEvent.EVENT_DYNAMIC_GAME_OBJECT_ADDED, new AddDynamicGameObjectEventProzessor());
        tickProcessor.setEventProcessor(TickEvent.EVENT_DYNAMIC_GAME_OBJECT_REMOVED, new RemoveDynamicGameObjectEventProcessor());
        tickProcessor.setEventProcessor(TickEvent.EVENT_GAME_FINISHED, new FinishedEventProcessor());
        tickProcessor.setEventProcessor(TickEvent.EVENT_GAME_PAUSED, new PausedEventProcessor());
        tickProcessor.setEventProcessor(TickEvent.EVENT_SIMULATION_EXCEPTION, new ExceptionEventProcessor());
        stateManager.attach(tickProcessor);

        //add cullableregister
        stateManager.attach(new CullableRegister(staticScene));

        //add SceneManager
        stateManager.attach(new SceneManager(splitScreenMode));
    }

    /**
     * Removes all {@link com.jme3.app.state.AppState}s used for rendering from the given {@link AppStateManager}.
     * @param stateManager the {@link AppStateManager} to remove the states from.
     */
    public static void tearDownRendering(AppStateManager stateManager) {
        //find states
        PropertyProcessorFactoryRegister register = stateManager.getState(PropertyProcessorFactoryRegister.class);
        Ticker ticker = stateManager.getState(Ticker.class);
        TickProcessor processor = stateManager.getState(TickProcessor.class);
        SceneManager sceneManager = stateManager.getState(SceneManager.class);
        CullableRegister cullables = stateManager.getState(CullableRegister.class);
        JobManager jobManager = stateManager.getState(JobManager.class);

        //if not all those appstates are attached, something went wrong
        if ((register != null) && (ticker != null) && (processor != null) && (sceneManager != null) && (cullables != null) && (jobManager != null)) {
            stateManager.detach(sceneManager);
            stateManager.detach(cullables);
            stateManager.detach(processor);
            stateManager.detach(register);
            stateManager.detach(ticker);
            stateManager.detach(jobManager);
        } else {
            throw new IllegalStateException("Tried to tear down rendering without it being fully existent");
        }
    }
}
