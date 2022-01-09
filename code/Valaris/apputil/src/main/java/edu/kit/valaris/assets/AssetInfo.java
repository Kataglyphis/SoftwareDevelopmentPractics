package edu.kit.valaris.assets;

import edu.kit.valaris.Metadata;

import java.util.Arrays;
import java.util.stream.Stream;

/**
 * Contains information about a specific asset.
 *
 * @author Frederik Lingg
 */
public class AssetInfo {

    /**
     * The key used to access he asset at runtime.
     */
    private String m_key;

    /**
     * The path to the .j3o asset.
     */
    private String m_path;

    /**
     * The {@link NodeInfo}s.
     */
    private NodeInfo[] m_nodes;

    /**
     * The {@link ControlInfo}s.
     */
    private ControlInfo[] m_controls;

    /**
     * The {@link MeshInfo}s.
     */
    private MeshInfo[] m_meshes;

    /**
     * The {@link AnimationInfo}s.
     */
    private AnimationInfo[] m_animations;

    /**
     * The metadata.
     */
    private MetaDataEntry[] m_metadata;

    /**
     * Sets the key of this {@link AssetInfo}.
     * @param key the key.
     */
    public void setKey(String key) {
        m_key = key;
    }

    /**
     * Accesses the key used to access this asset at runtime.
     * @return the key.
     */
    public String getKey() {
        return m_key;
    }

    /**
     * Sets the path to the .j3o asset.
     * @param path the path.
     */
    public void setPath(String path) {
        m_path = path;
    }

    /**
     * The path of the .j3o asset to load.
     * @return the path to the .j3o asset to load.
     */
    public String getPath() {
        return m_path;
    }

    /**
     * Sets the {@link NodeInfo}s used by this {@link AssetInfo}.
     * @param nodes the {@link NodeInfo}s.
     */
    public void setNodes(NodeInfo[] nodes) {
        m_nodes = nodes;
    }

    /**
     * All {@link NodeInfo}s contained by this {@link AssetInfo}.
     * @return the {@link NodeInfo}s.
     */
    public Stream<NodeInfo> getNodes() {
        return Arrays.stream(m_nodes);
    }

    /**
     * Sets the {@link ControlInfo}s used in this {@link AssetInfo}.
     * @param controls the {@link ControlInfo}s.
     */
    public void setControls(ControlInfo[] controls) {
        m_controls = controls;
    }

    /**
     * All {@link ControlInfo}s contained by this {@link AssetInfo}.
     * @return the {@link ControlInfo}.
     */
    public Stream<ControlInfo> getControls() {
        return Arrays.stream(m_controls);
    }

    /**
     * Sets the {@link MeshInfo}s for this {@link AssetInfo}.
     * @param meshes the {@link MeshInfo}s.
     */
    public void setMeshes(MeshInfo[] meshes) {
        m_meshes = meshes;
    }

    /**
     * Accesses the {@link MeshInfo}s contained by this {@link AssetInfo}.
     * @return the {@link MeshInfo}s as a stream.
     */
    public Stream<MeshInfo> getMeshes() {
        return Arrays.stream(m_meshes);
    }

    /**
     * Sets the {@link AnimationInfo}s for this {@link AssetInfo}.
     * @param animations the {@link AnimationInfo}s.
     */
    public void setAnimations(AnimationInfo[] animations) {
        m_animations = animations;
    }

    /**
     * Accesses the {@link AnimationInfo}s contained by this {@link AssetInfo}.
     * @return the {@link AnimationInfo}s as a stream.
     */
    public Stream<AnimationInfo> getAnimations() {
        return Arrays.stream(m_animations);
    }

    /**
     * Checks whether this {@link AssetInfo} has animations or not.
     * @return whether this {@link AssetInfo} has animations.
     */
    public boolean hasAnimations() {
        return m_animations != null && m_animations.length > 0;
    }

    public void setMetadata(MetaDataEntry[] metadata) {
        m_metadata = metadata;
    }

    /**
     * Accesses the Metadata of this {@link AssetInfo}.
     * @return the {@link MetaDataEntry}s.
     */
    public MetaDataEntry[] getMetadata() {
        return m_metadata;
    }
}
