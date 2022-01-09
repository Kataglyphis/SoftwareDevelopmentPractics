package edu.kit.valaris.rendering.tick.dynamics.properties;

import com.jme3.scene.Node;
import edu.kit.valaris.assets.AssetControl;
import edu.kit.valaris.assets.controls.VelocityControl;
import edu.kit.valaris.rendering.tick.dynamics.DynamicGameObjectProcessor;
import edu.kit.valaris.tick.DynamicGameObject;
import edu.kit.valaris.tick.properties.IProperty;
import edu.kit.valaris.tick.properties.VelocityProperty;

/**
 * Used to Process VelocityProperties
 * @author Frederik Lingg
 */
public class VelocityPropertyProcessor implements IPropertyProcessor {

    private Node m_target;

    public VelocityPropertyProcessor(Node target) {
        m_target = target;
    }

    @Override
    public void process(IProperty property, DynamicGameObject dgo, DynamicGameObjectProcessor dgoProcessor) {
        if (property instanceof VelocityProperty) {
            float velocity = ((VelocityProperty) property).getMomentum();
            ((VelocityControl) m_target.getControl(AssetControl.class).getActiveControl("BreakLightControl")).setVelocity(velocity / 50f);
        } else {
            throw new IllegalStateException("Wrong PropertyType for VelocityPropertyProcessor");
        }
    }
}
