package edu.kit.valaris.rendering.tick.dynamics.properties;

import com.jme3.scene.Node;

/**
 * {@link IPropertyProcessorFactory} for {@link CameraPropertyProcessor}s.
 *
 * @author Frederik Lingg
 */
public class CameraPropertyProcessorFactory implements IPropertyProcessorFactory {

    @Override
    public IPropertyProcessor newProcessor(Node target) {
        return new CameraPropertyProcessor(target);
    }
}
