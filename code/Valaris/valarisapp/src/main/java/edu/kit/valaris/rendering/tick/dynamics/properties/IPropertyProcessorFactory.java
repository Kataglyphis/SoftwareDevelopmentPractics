package edu.kit.valaris.rendering.tick.dynamics.properties;

import com.jme3.scene.Node;

/**
 * Used to create new {@link IPropertyProcessor}s of a given type.
 *
 * @author Frederik Lingg
 */
public interface IPropertyProcessorFactory {

    /**
     * Creates a new {@link IPropertyProcessor} working on the given {@link Node}.
     * @param target the {@link Node} the new {@link IPropertyProcessor} should work on.
     */
    public IPropertyProcessor newProcessor(Node target);
}
