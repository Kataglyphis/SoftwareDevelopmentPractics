package edu.kit.valaris.rendering.tick.dynamics.properties;

import edu.kit.valaris.rendering.tick.dynamics.DynamicGameObjectProcessor;
import edu.kit.valaris.tick.DynamicGameObject;
import edu.kit.valaris.tick.properties.IProperty;

/**
 * Used to process an {@link IProperty} of a specific {@link DynamicGameObject}.
 *
 * @author Frederik Lingg
 */
public interface IPropertyProcessor {

    /**
     * Processes the given {@link IProperty}.
     * @param property the {@link IProperty} to process.
     * @param dgo the {@link DynamicGameObject} that is currently processed
     * @param dgoProcessor the {@link DynamicGameObjectProcessor} that called this method.
     */
    public void process(IProperty property, DynamicGameObject dgo, DynamicGameObjectProcessor dgoProcessor);
}
