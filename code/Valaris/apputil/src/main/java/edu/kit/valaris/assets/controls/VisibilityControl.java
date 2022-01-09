package edu.kit.valaris.assets.controls;

import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import edu.kit.valaris.assets.AbstractAssetControl;

/**
 * Used to set visibility of spatials in the scenegraph
 *
 * @author Frederik Lingg
 */
public class VisibilityControl extends AbstractAssetControl {

    /**
     * Sets whether the Spatial this {@link VisibilityControl} is attached to is visible or not.
     * @param flag
     */
    public void setVisible(boolean flag) {
        getSpatial().setCullHint(flag ? Spatial.CullHint.Inherit : Spatial.CullHint.Always);
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
