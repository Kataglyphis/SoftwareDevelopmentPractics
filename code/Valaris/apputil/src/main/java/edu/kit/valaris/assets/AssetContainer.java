package edu.kit.valaris.assets;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Node;

/**
 * Contains an asset with its {@link AssetInfo}.
 *
 * @author Frederik Lingg
 */
public class AssetContainer {

    /**
     * The Asset itself.
     */
    private final Node m_asset;

    /**
     * The {@link AssetInfo} for this asset.
     */
    private final AssetInfo m_info;

    /**
     * The {@link AssetManager} for loading materials.
     */
    private AssetManager m_assetManager;

    /**
     * Creates a new {@link AssetContainer} with the given asset and {@link AssetInfo}.
     * @param asset the asset itself.
     * @param info the {@link AssetInfo}.
     * @param assetManager the {@link AssetManager} to load materials with.
     */
    public AssetContainer(Node asset, AssetInfo info, AssetManager assetManager) {
        m_asset = asset;
        m_info = info;
        m_assetManager = assetManager;
    }

    /**
     * Creates a usable copy of the asset.
     * @return the usable copy.
     */
    public Node newUsableInstance() {
        return newUsableInstance(false);
    }

    /**
     * Creates a usable copy of the asset.
     * @param forceDeepClone whether to force a deepclone or not.
     * @return the usable copy.
     */
    public Node newUsableInstance(boolean forceDeepClone) {
        Node instance = m_info.hasAnimations() || forceDeepClone ? (Node) m_asset.deepClone() : m_asset.clone(false);
        instance.addControl(new AssetControl(m_info, m_assetManager));

        return instance;
    }
}
