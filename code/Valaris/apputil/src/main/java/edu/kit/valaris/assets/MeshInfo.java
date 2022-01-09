package edu.kit.valaris.assets;

/**
 * Used to set materials to meshes in assets.
 *
 * @author Frederik Lingg
 */
public class MeshInfo {

    /**
     * The path to the mesh in the asset.
     */
    private String m_path;

    /**
     * The path to the .j3m file to use as a material.
     */
    private String m_material;

    /**
     * Sets the path to the mesh inside the asset.
     * @param path the path.
     */
    public void setPath(String path) {
        m_path = path;
    }

    /**
     * Accesses the path to the mesh.
     * @return the path.
     */
    public String getPath() {
        return m_path;
    }

    /**
     * Sets the path to the .j3m asset to use as material.
     * @param material the material.
     */
    public void setMaterial(String material) {
        m_material = material;
    }

    /**
     * Accesses the path to the material asset.
     * @return the path to the .j3m file to use as a material.
     */
    public String getMaterial() {
        return m_material;
    }
}
