package edu.kit.valaris.assets.controls;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector4f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.control.AbstractControl;
import com.jme3.shader.VarType;
import edu.kit.valaris.assets.AbstractAssetControl;

/**
 * Used to control the breaklight color of a cart according to the velocity of the vehicle.
 *
 * @author Frederik Lingg
 */
public class VelocityControl extends AbstractAssetControl {

    private Vector4f[] m_colors = new Vector4f[]{
            new Vector4f(0.0f, 1.0f, 0.0f, 0.0f),
            new Vector4f(1.0f, 0.0f, 0.0f, 1.0f)
    };

    private Material m_material;

    /**
     * Sets the velocity as an amount of the maximum velocity
     * @param velocity the velocity as a float between 0 and 1
     */
    public void setVelocity(float velocity) {
        //search for material
        if(m_material == null) {
            m_material = ((Geometry) getSpatial()).getMaterial();
            m_material.setParam("EmissivePoints", VarType.Vector4Array, m_colors);
            m_material.setInt("NumEmissivePoints", m_colors.length);
            m_material.setColor("Emissive", ColorRGBA.Black);
        }

        m_material.setFloat("RampFactor", velocity);
    }

    @Override
    protected void controlUpdate(float tpf) {

    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {

    }

    @Override
    public void cleanup() {
    }
}
