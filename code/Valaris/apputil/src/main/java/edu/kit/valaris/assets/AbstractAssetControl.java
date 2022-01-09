package edu.kit.valaris.assets;

import com.jme3.scene.control.AbstractControl;

/**
 * Used for Controls loaded via the AssetProvider.
 */
public abstract class AbstractAssetControl extends AbstractControl {

    /**
     * Called when the DGO the asset was used for is removed.
     */
    public abstract void cleanup();
}
