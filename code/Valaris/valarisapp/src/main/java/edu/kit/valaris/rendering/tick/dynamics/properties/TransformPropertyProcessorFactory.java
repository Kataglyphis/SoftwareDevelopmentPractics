package edu.kit.valaris.rendering.tick.dynamics.properties;

import com.jme3.scene.Node;

/**
 * Factory used for creating {@link TransformPropertyProcessor}s.
 *
 * @author Frederik Lingg
 */
public class TransformPropertyProcessorFactory implements IPropertyProcessorFactory {

    @Override
    public IPropertyProcessor newProcessor(Node target) {
        return new TransformPropertyProcessor(target);
    }
}
