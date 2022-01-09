package edu.kit.valaris.assets;

/**
 * Contains information about {@link com.jme3.scene.control.AbstractControl}s that need to be used at runtime.
 *
 * @author Frederik Lingg
 */
public class ControlInfo {

    /**
     * The name the {@link com.jme3.scene.control.AbstractControl} can be accessed over at runtime.
     */
    private String m_key;

    /**
     * The path of the {@link com.jme3.scene.Node} the {@link com.jme3.scene.control.AbstractControl} is attached to in the asset.
     */
    private String m_path;

    /**
     * The java type of the {@link com.jme3.scene.control.AbstractControl} containing the whole package name as well.
     */
    private String m_type;

    /**
     * Whether the {@link com.jme3.scene.control.AbstractControl} exists after loading the asset.
     */
    private boolean m_isPreload;

    private String m_params;

    /**
     * Sets the key of this {@link AssetInfo}.
     * @param key the key.
     */
    public void setKey(String key) {
        m_key = key;
    }

    /**
     * Accesses the name of this {@link ControlInfo}.
     * @return the key to use when accessing the {@link com.jme3.scene.control.AbstractControl} at runtime.
     */
    public String getKey() {
        return m_key;
    }

    /**
     * Sets the path to the subnode using this {@link ControlInfo}.
     * @param path the path to the subnode.
     */
    public void setPath(String path) {
        m_path = path;
    }

    /**
     * Accesses the path of the {@link com.jme3.scene.Node} the {@link com.jme3.scene.control.AbstractControl} is attached to.
     * @return the path.
     */
    public String getPath() {
        return m_path;
    }

    /**
     * Sets the type of this {@link ControlInfo}.
     * @param type the full java Type name.
     */
    public void setType(String type) {
        m_type = type;
    }

    /**
     * Accesses the Type of the {@link com.jme3.scene.control.AbstractControl}.
     * @return the type.
     */
    public String getType() {
        return m_type;
    }

    /**
     * Sets whether the {@link com.jme3.scene.control.Control} is loaded by the {@link com.jme3.asset.AssetManager}
     * or has to be created afterwards.
     * @param isPreload whether the {@link com.jme3.scene.control.Control} is loaded by the {@link com.jme3.asset.AssetManager}.
     */
    public void setIsPreload(boolean isPreload) {
        m_isPreload = isPreload;
    }

    /**
     * Checks whether the {@link com.jme3.scene.control.AbstractControl} exists after loading the asset.
     * @return whether the {@link com.jme3.scene.control.AbstractControl} exists after loading the asset.
     */
    public boolean isPreload() {
        return m_isPreload;
    }

    /**
     * Sets the starting parameters of this control.
     * @param params the parameters as an array of strings.
     */
    public void setParams(String params) {
        m_params = params;
    }

    /**
     * Accesses the parameters of the control.
     * @return the parameters as an array of strings.
     */
    public String getParams() {
        return m_params;
    }
}
