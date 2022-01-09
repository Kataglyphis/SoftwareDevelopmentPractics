package edu.kit.valaris.assets;

import com.jme3.asset.AssetManager;
import com.jme3.math.FastMath;
import com.jme3.math.Transform;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;

/**
 * Used to give the itembox its rotation and bounce effect
 */
public class ItemBoxControl extends AbstractAssetControl {

    /**
     * Accumulator for the time this control has been active
     */
    private float m_accu;

    private float m_period;
    private float m_amplitude;
    private int m_bounceFrequency;

    private Vector3f m_basePosition;

    public ItemBoxControl(String params, AssetManager assetManager) {
        String[] args = params.split(",");
        m_period = Float.parseFloat(args[0]);
        m_amplitude = Float.parseFloat(args[1]);
        m_bounceFrequency = Integer.parseInt(args[2]);
    }


    @Override
    public void setSpatial(Spatial target) {
        super.setSpatial(target);
        m_basePosition = target.getLocalTranslation().add(new Vector3f(0, m_amplitude, 0));
    }

    @Override
    protected void controlUpdate(float tpf) {
        m_accu = (m_accu + tpf) % m_period;
        float phase = m_accu / m_period;

        Transform transform = getSpatial().getLocalTransform();
        transform.setTranslation(m_basePosition.add(0, FastMath.sin(FastMath.PI * 2f * m_bounceFrequency * phase) * m_amplitude, 0));
        transform.setRotation(transform.getRotation().fromAngleAxis(FastMath.PI * 2f * phase, Vector3f.UNIT_Y));
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }

    @Override
    public void cleanup() {
    }
}
