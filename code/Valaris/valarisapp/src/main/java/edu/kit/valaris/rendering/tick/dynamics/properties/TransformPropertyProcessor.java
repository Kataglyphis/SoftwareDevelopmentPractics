package edu.kit.valaris.rendering.tick.dynamics.properties;

import com.jme3.scene.Node;
import edu.kit.valaris.rendering.tick.dynamics.DynamicGameObjectProcessor;
import edu.kit.valaris.tick.DynamicGameObject;
import edu.kit.valaris.tick.properties.IProperty;
import edu.kit.valaris.tick.properties.TransformProperty;

/**
 * {@link IPropertyProcessor} that processes the {@link TransformProperty}.
 *
 * @author Frederik Lingg
 */
public class TransformPropertyProcessor implements IPropertyProcessor {

    /**
     * The {@link Node} this {@link IPropertyProcessor} works on.
     */
    private Node m_target;

    /**
     * Creates a new {@link TransformPropertyProcessor} with the given {@link Node} as a target.
     * @param target the {@link Node} this {@link IPropertyProcessor} works on.
     */
    public TransformPropertyProcessor(Node target) {
        m_target = target;
    }

    @Override
    public void process(IProperty property, DynamicGameObject dgo, DynamicGameObjectProcessor dgoProcessor) {

        //just update the transform
        if (property instanceof TransformProperty) {
            m_target.setLocalTransform(((TransformProperty) property).getTransform());
        } else {
            throw new IllegalStateException("Wrong PropertyType for TransformPropertyProcessor");
        }
    }
}
