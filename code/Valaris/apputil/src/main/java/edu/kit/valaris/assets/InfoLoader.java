package edu.kit.valaris.assets;

/**
 * Used to load information about assets.
 *
 * @author Frederik Lingg
 */
public interface InfoLoader {

    /**
     * Loads the {@link AssetPack} at the given path.
     * @param path the path of the {@link AssetPack}.
     * @return the loaded {@link AssetPack}.
     */
    public AssetPack loadAssetPack(String path);

    /**
     * Loads the {@link AssetInfo} at the given path.
     * @param path the path of the {@link AssetInfo}.
     * @return the loaded {@link AssetInfo}.
     */
    public AssetInfo loadAssetInfo(String path);
}
