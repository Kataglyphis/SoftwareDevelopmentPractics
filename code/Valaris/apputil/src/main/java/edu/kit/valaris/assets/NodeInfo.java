package edu.kit.valaris.assets;

/**
 * Contains information about a {@link com.jme3.scene.Node} in an asset that needs to be accessable at runtime.
 *
 * @author Frederik Lingg
 */
public class NodeInfo {

    /**
     * The name used to access the {@link com.jme3.scene.Node} at runtime.
     */
    private String m_key;

    /**
     * The location of the node in the asset.
     */
    private String m_path;

    /**
     * Sets the key of this {@link NodeInfo}.
     * @param key the key.
     */
    public void setKey(String key) {
        m_key = key;
    }

    /**
     * Accesses the key of this {@link NodeInfo}.
     * @return the key used to access the {@link com.jme3.scene.Node} at runtime.
     */
    public String getKey() {
        return m_key;
    }

    /**
     * Sets the path to this {@link NodeInfo}.
     * @param path the path.
     */
    public void setPath(String path) {
        m_path = path;
    }

    /**
     * Accesses the path of the {@link com.jme3.scene.Node} inside the asset.
     * @return the path of the {@link com.jme3.scene.Node} inside the asset.
     */
    public String getPath() {
        return m_path;
    }
}
