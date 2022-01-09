package edu.kit.valaris.assets;

import com.jme3.app.state.AbstractAppState;
import com.jme3.asset.AssetKey;
import com.jme3.asset.AssetManager;
import com.jme3.asset.MaterialKey;
import com.jme3.asset.ModelKey;
import com.jme3.scene.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * {@link com.jme3.app.state.AppState} used to asynchronously load and access resources.
 *
 * @author Frederik Lingg
 */
public class AssetProvider extends AbstractAppState {

    /**
     * The {@link AssetManager} to use for loading resources.
     */
    private AssetManager m_assetManager;

    /**
     * The {@link InfoLoader}s used by this {@link AssetProvider} mapped to their respective file.
     */
    private Map<String, InfoLoader> m_loaders;

    /**
     * Contains all loaded assets in their respective groups.
     */
    private Map<String, Map<String, AssetContainer>> m_assets;

    /**
     * A List of all loaded assets.
     */
    private List<AssetKey> m_loadedAssets;

    /**
     * Creates a new {@link AssetProvider} using the given {@link AssetManager}.
     * @param assetManager the {@link AssetManager} to use for loading resources.
     */
    public AssetProvider(AssetManager assetManager) {
        m_assetManager = assetManager;
        m_loaders = new HashMap<>();
        m_assets = new HashMap<>();
        m_loadedAssets = new ArrayList<>();
    }

    /**
     * Gets the right {@link InfoLoader} for the given file.
     * @param path the path to the file.
     * @return an {@link InfoLoader} capable of loading the file.
     */
    private InfoLoader getInfoLoader(String path) {
        String[] spath = path.split("\\.");
        return m_loaders.get(spath[spath.length - 1]);
    }

    /**
     * Loads assets from a specific {@link GroupInfo}.
     * @param info the {@link GroupInfo} to load from.
     * @return a {@link Map} containing the loaded assets.
     */
    private Map<String, AssetContainer> loadGroup(GroupInfo info) {
        final Map<String, AssetContainer> assets = new HashMap<>();

        Stream<String> assetRefs = info.getAssets();
        assetRefs.forEachOrdered(ref -> {
            //load asset
            AssetInfo assetInfo = getInfoLoader(ref).loadAssetInfo(ref);
            AssetContainer asset = loadAsset(assetInfo);

            //put asset into map
            assets.put(assetInfo.getKey(), asset);
        });

        return assets;
    }

    /**
     * Loads an asset from a specific {@link AssetInfo}.
     * @param info the {@link AssetInfo} to load from.
     * @return the loaded asset.
     */
    private AssetContainer loadAsset(AssetInfo info) {
        //load asset
        ModelKey modelKey = new ModelKey(info.getPath());
        Node asset = (Node) m_assetManager.loadAsset(modelKey);

        //register loaded asset
        m_loadedAssets.add(modelKey);

        //load materials
        info.getMeshes().forEachOrdered(mesh -> {
            MaterialKey matkey = new MaterialKey(mesh.getMaterial());

            //load material and register as loaded
            m_assetManager.loadAsset(matkey);
            m_loadedAssets.add(matkey);
        });

        return new AssetContainer(asset, info, m_assetManager);
    }

    /**
     * Sets the {@link InfoLoader} to use for the specific file type.
     * @param fileending the filetype.
     * @param loader the {@link InfoLoader} to use.
     */
    public void setInfoLoader(String fileending, InfoLoader loader) {
        m_loaders.put(fileending, loader);
    }

    /**
     * Accesses the {@link AssetManager}.
     * @return the {@link AssetManager} to use for loading resources
     */
    protected AssetManager getAssetManager() {
        return m_assetManager;
    }

    /**
     * Loads the resources on the given path in a separate thread.
     * @param path the path to the resources.
     * @param callback the {@link LoadingCallback} to update the loading screen.
     */
    public void loadAsynchronous(final String path, final LoadingCallback callback) {
        Thread loadingThread = new Thread(() -> load(path, callback), "AssetProviderLoadingThread");
        loadingThread.start();
    }

    /**
     * Executed in a separate thread to load resources from the given path.
     * @param path the path to the resources.
     * @param callback the {@link LoadingCallback} to update the loading screen.
     */
    public void load(String path, LoadingCallback callback) {
        AssetPack pack = getInfoLoader(path).loadAssetPack(path);

        //load each group in sequence
        final int maxGroups = (int) pack.getGroups().count();
        pack.getGroups().forEachOrdered(group -> {
            //update loading screen
            callback.setProgress(m_assets.size() / maxGroups, "Loading " + group.getKey());

            //load group and add it to assets
            Map<String, AssetContainer> groupAssets = loadGroup(group);
            m_assets.put(group.getKey(), groupAssets);
        });

        //loading is finished
        callback.finished();
    }

    /**
     * Accesses a full group of assets.
     * @param key the name of the group.
     * @return the group itself.
     */
    public Map<String, AssetContainer> provideGroup(String key) {
        return m_assets.get(key);
    }

    /**
     * A usable copy of the resource with the given key.
     * @param key the key of the resource.
     * @return a {@link Node} containing the resource or null if there is no resource with the given name.
     */
    public Node provide(String key) {
        return provide(key, false);
    }

    /**
     * A usable copy of the resource with the given key.
     * @param key the key of the resource.
     * @param forceDeepClone whether to force a deepclone of the asset.
     * @return a {@link Node} containing the resource or null if there is no resource with the given name.
     */
    public Node provide(String key, boolean forceDeepClone) {
        if (key != null) {
            String[] skey = key.split("/");

            //use deep-copy
            Map<String, AssetContainer> group = provideGroup(skey[0]);
            return group != null ? group.get(skey[1]).newUsableInstance(forceDeepClone) : null;
        } else {
            throw new IllegalArgumentException("key cannot be null");
        }
    }

    /**
     * Clear loaded models from {@link AssetManager} cache.
     */
    public void unloadModels() {
        //clear loaded groups
        m_assets.clear();

        //delete all assets from cache
        for (AssetKey key : m_loadedAssets) {
            m_assetManager.deleteFromCache(key);
        }

        //clear loaded models
        m_loadedAssets.clear();
    }

    @Override
    public void cleanup() {
        unloadModels();
    }
}
