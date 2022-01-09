package edu.kit.valaris.assets;

/**
 * Holds information about a playable animation.
 *
 * @author Frederik Lingg
 */
public class AnimationInfo {

    /**
     * The key this animation can be used with later.
     */
    private String m_key;

    /**
     * The name of the animation.
     */
    private String m_name;

    /**
     * The path to the {@link com.jme3.scene.Spatial} that can execute this animation.
     */
    private String m_path;

    /**
     * Sets the key of this {@link AnimationInfo}.
     * @param key the key.
     */
    public void setKey(String key) {
        m_key = key;
    }

    /**
     * Accesses the key of this {@link AnimationInfo}.
     * @return the key.
     */
    public String getKey() {
        return m_key;
    }

    /**
     * Sets the name of this {@link AnimationInfo}.
     * @param name the name.
     */
    public void setName(String name) {
        m_name = name;
    }

    /**
     * Accesses the name of this {@link AnimationInfo}.
     * @return the name.
     */
    public String getName() {
        return m_name;
    }

    /**
     * Sets the path to the {@link com.jme3.scene.Spatial} that can play the animation.
     * @param path the path to the {@link com.jme3.scene.Spatial}.
     */
    public void setPath(String path) {
        m_path = path;
    }

    /**
     * Accesses the path to the {@link com.jme3.scene.Spatial} that can play the animation.
     * @return the path to the {@link com.jme3.scene.Spatial}.
     */
    public String getPath() {
        return m_path;
    }
}
