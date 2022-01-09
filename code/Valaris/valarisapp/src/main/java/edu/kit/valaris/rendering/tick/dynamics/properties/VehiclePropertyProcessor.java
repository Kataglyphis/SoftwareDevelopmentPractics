package edu.kit.valaris.rendering.tick.dynamics.properties;

import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import edu.kit.valaris.assets.AssetControl;
import edu.kit.valaris.assets.controls.VisibilityControl;
import edu.kit.valaris.rendering.tick.dynamics.DynamicGameObjectProcessor;
import edu.kit.valaris.tick.DynamicGameObject;
import edu.kit.valaris.tick.properties.IProperty;
import edu.kit.valaris.tick.properties.VehicleProperty;

/**
 * PropertyProcessor for {@link VehicleProperty}s.
 *
 * @author Frederik Lingg
 */
public class VehiclePropertyProcessor implements IPropertyProcessor {

    /**
     * The node this {@link VehiclePropertyProcessor} works on.
     */
    private Node m_target;

    /**
     * The {@link AssetControl} of the target.
     */
    private AssetControl m_assetControl;

    /**
     * Creates a new {@link VehiclePropertyProcessor} working on the given target.
     * @param target the {@link Node} this {@link VehiclePropertyProcessor} works on.
     */
    public VehiclePropertyProcessor(Node target) {
        m_target = target;
        m_assetControl = target.getControl(AssetControl.class);
    }

    @Override
    public void process(IProperty property, DynamicGameObject dgo, DynamicGameObjectProcessor dgoProcessor) {
        if (property instanceof VehicleProperty) {

            //make sure that all energyballs needed are displayed
            int energyLevel = ((VehicleProperty) property).getEnergyLevel();
            for (int i = 0; i < 8; i++) {
                ((VisibilityControl) m_assetControl.getActiveControl("Energy" + i)).setVisible(i < ((float) energyLevel * 8f / 100f));
            }

            //update strip color
            ((Geometry) m_assetControl.getSubNode("PlayerStrip")).getMaterial().setColor("Emissive", ((VehicleProperty) property).getColor());
            ((Geometry) m_assetControl.getSubNode("VehicleStrip")).getMaterial().setColor("Emissive", ((VehicleProperty) property).getColor());

            //update shield
            ((VisibilityControl) m_assetControl.getActiveControl("Shield")).setVisible(((VehicleProperty) property).getIsImmune());
        } else {
            throw new IllegalStateException("Wrong Propertytype for VehiclePropertyProcessor");
        }
    }
}
