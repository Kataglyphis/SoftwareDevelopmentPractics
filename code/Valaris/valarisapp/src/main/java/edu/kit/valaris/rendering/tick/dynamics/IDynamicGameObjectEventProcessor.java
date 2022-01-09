package edu.kit.valaris.rendering.tick.dynamics;

import edu.kit.valaris.tick.DynamicGameObject;
import edu.kit.valaris.tick.DynamicGameObjectEvent;

/**
 * Used to process certain {@link DynamicGameObjectEvent}s.
 *
 * @author Frederik Lingg
 */
public interface IDynamicGameObjectEventProcessor {

    /**
     * Sets the {@link DynamicGameObjectProcessor} that uses this {@link IDynamicGameObjectEventProcessor}.
     * @param processor the {@link DynamicGameObjectProcessor}.
     */
    public void setDynamicGameObjectProcessor(DynamicGameObjectProcessor processor);

    /**
     * Processes the given {@link DynamicGameObjectEvent}.
     * @param event the {@link DynamicGameObjectEvent} to process.
     * @param dgo the {@link DynamicGameObject} the {@link DynamicGameObjectEvent} occured on.
     */
    public void process(DynamicGameObjectEvent event, DynamicGameObject dgo);
}
