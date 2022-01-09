package edu.kit.valaris.rendering.tick.dynamics.properties;

import com.jme3.scene.Node;

/**
 * Used to create VelocityPropertyProcessors
 *
 * @author Frederik Lingg
 */
public class VelocityPropertyProcessorFactory implements IPropertyProcessorFactory{

    @Override
    public IPropertyProcessor newProcessor(Node target) {
        return new VelocityPropertyProcessor(target);
    }
}
