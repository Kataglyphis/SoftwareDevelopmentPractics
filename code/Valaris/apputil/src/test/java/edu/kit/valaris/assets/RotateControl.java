package edu.kit.valaris.assets;

import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

public class RotateControl extends AbstractControl {

    private float m_speed = 0;

    public RotateControl() {
    }

    public RotateControl(String params) {
        m_speed = Float.parseFloat(params);
    }

    public void setSpeed(float speed) {
        m_speed = speed;
    }

    @Override
    protected void controlUpdate(float tpf) {
        getSpatial().rotate(0, m_speed * tpf, 0);
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        // do nothing
    }
}
