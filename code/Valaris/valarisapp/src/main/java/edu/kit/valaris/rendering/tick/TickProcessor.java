package edu.kit.valaris.rendering.tick;

import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.scene.Node;
import edu.kit.valaris.assets.AssetControl;
import edu.kit.valaris.rendering.tick.dynamics.DynamicGameObjectProcessor;
import edu.kit.valaris.tick.DynamicGameObject;
import edu.kit.valaris.tick.Tick;
import edu.kit.valaris.tick.TickEvent;
import edu.kit.valaris.tick.Ticker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Processes {@link Tick}s using {@link DynamicGameObjectProcessor}s
 * and {@link ITickEventProcessor}s.
 *
 * @author Frederik Lingg
 */
public class TickProcessor extends AbstractAppState {

    /**
     * The {@link AppStateManager} this {@link TickProcessor} is currently attached to.
     */
    private AppStateManager m_stateManager;

    /**
     * Contains all active {@link DynamicGameObjectProcessor}s.
     */
    private List<DynamicGameObjectProcessor> m_dgoProcessors;

    /**
     * Contains {@link ITickEventProcessor}s for each action that triggers a {@link TickEvent}.
     */
    private Map<String, ITickEventProcessor> m_eventProcessors;

    /**
     * The {@link Node} all {@link DynamicGameObject}s are attached to.
     */
    private Node m_dynamicsRoot;

    /**
     * The {@link Ticker} used to get {@link Tick}s from.
     */
    private Ticker m_ticker;

    /**
     * Creates a new {@link TickProcessor} with no {@link DynamicGameObjectProcessor}s and no {@link ITickEventProcessor}s.
     */
    public TickProcessor() {
        m_dgoProcessors = new ArrayList<>();
        m_eventProcessors = new HashMap<>();
        m_dynamicsRoot = new Node("Dynamics Root");
    }

    /**
     * Accesses the current {@link AppStateManager}.
     * @return the {@link AppStateManager} this {@link com.jme3.app.state.AppState} is currently attached to.
     */
    public AppStateManager getAppStateManager() {
        return m_stateManager;
    }

    /**
     * Accesses the {@link Node} containing all currently active {@link DynamicGameObject}s.
     * @return the {@link Node}.
     */
    public Node getDynamicsRoot() {
        return m_dynamicsRoot;
    }

    /**
     * Adds a new {@link DynamicGameObjectProcessor} for processing a specific {@link DynamicGameObjectProcessor}.
     * @param dgo the index of the {@link DynamicGameObject}.
     * @param processor the {@link DynamicGameObjectProcessor}.
     */
    public void addDynamiGameObjectProcessor(int dgo, DynamicGameObjectProcessor processor) {
        //ensure index exists
        while (dgo >= m_dgoProcessors.size()) {
            m_dgoProcessors.add(null);
        }

        //add dgoprocessor
        processor.setTickProcessor(this);
        m_dgoProcessors.set(dgo, processor);

        //add model
        m_dynamicsRoot.attachChild(processor.getSpatial());
    }

    /**
     * Removes a {@link DynamicGameObjectProcessor}.
     * @param dgo the index of the {@link DynamicGameObjectProcessor} to remove.
     */
    public void removeDynamicGameObjectProcessor(int dgo) {
        //if index does not exist, do nothing
        if (dgo >= m_dgoProcessors.size()) {
            return;
        }

        //remove model from scene
        if (m_dgoProcessors.get(dgo) != null) {
            //notify assetcontrol
            m_dgoProcessors.get(dgo).getSpatial().getControl(AssetControl.class).cleanup();
            m_dynamicsRoot.detachChild(m_dgoProcessors.get(dgo).getSpatial());
        }

        //set as null to not change other ids
        m_dgoProcessors.set(dgo, null);
    }

    /**
     * Sets the {@link ITickEventProcessor} to process {@link TickEvent}s triggered by a specific action.
     * @param action the action.
     * @param processor the {@link ITickEventProcessor}.
     */
    public void setEventProcessor(String action, ITickEventProcessor processor) {
        processor.setTickProcessor(this);
        m_eventProcessors.put(action, processor);
    }

    /**
     * Processes the given {@link Tick}.
     * @param tick the {@link Tick} to process.
     */
    public void processTick(Tick tick) {
        //process events first
        Stream<TickEvent> events = tick.getEventsAsStream();
        events.forEachOrdered(event -> m_eventProcessors.get(event.getAction()).process(event));

        //process dgos
        List<DynamicGameObject> dgos = tick.getDynamicGameObjects(false);
        for (int i = 0; i < m_dgoProcessors.size(); i++) {
            if (m_dgoProcessors.get(i) != null && dgos.get(i) != null) {
                m_dgoProcessors.get(i).processDynamicGameObject(dgos.get(i));
            } else if (m_dgoProcessors.get(i) != null || dgos.get(i) != null) {
                throw new IllegalStateException("DGOProcessor without DGO or DGO without DGOProcessor at index " + i);
            }
        }
    }

    @Override
    public void stateAttached(AppStateManager stateManager) {
        super.stateAttached(stateManager);

        m_stateManager = stateManager;
        m_ticker = m_stateManager.getState(Ticker.class);
    }

    @Override
    public void stateDetached(AppStateManager stateManager) {
        super.stateDetached(stateManager);

        m_stateManager = null;
        m_ticker = null;
    }

    @Override
    public void postRender() {
        //after scene is rendered,  get data for the new frame
        m_ticker.swapRenderTick();
        processTick(m_ticker.getRenderBuffer());
    }
}
