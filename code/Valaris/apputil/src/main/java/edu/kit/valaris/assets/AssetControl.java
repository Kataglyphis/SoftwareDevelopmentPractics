package edu.kit.valaris.assets;

import com.jme3.asset.AssetManager;
import com.jme3.asset.AssetNotFoundException;
import com.jme3.material.Material;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Used to provide asset specific data at runtime.
 *
 * @author Frederik Lingg
 */
public class AssetControl extends AbstractControl {

    /**
     * The {@link AssetInfo} this {@link AssetControl} is based on.
     */
    private AssetInfo m_info;

    /**
     * The {@link Node}s that need to be accessible at runtime.
     */
    private Map<String, Spatial> m_nodes;

    /**
     * The {@link Control}s that need to be accessible at runtime.
     */
    private Map<String, AbstractAssetControl> m_controls;

    /**
     * The Metadata of this {@link AssetControl}.
     */
    private Map<String, String> m_metadata;

    /**
     * The {@link AssetManager} used by this {@link AssetControl}.
     */
    private AssetManager m_assetManager;

    /**
     * Creates a new {@link AssetControl} based on the given {@link AssetInfo}.
     * @param info the {@link AssetInfo} to use.
     */
    public AssetControl(AssetInfo info, AssetManager assetManager) {
        m_info = info;
        m_nodes = new HashMap<>();
        m_controls = new HashMap<>();
        m_assetManager = assetManager;

        //init metadata
        m_metadata = new HashMap<>();
        if(m_info.getMetadata() != null) {
            for(MetaDataEntry entry : m_info.getMetadata()) {
                m_metadata.put(entry.getKey(), entry.getValue());
            }
        }
    }

    /**
     * Recursively searches for a child in the given {@link Node}.
     * @param path the path to the child.
     * @param parent the {@link Node} to search in.
     * @return the child or null if no child was found.
     */
    private Spatial findChild(String path, Node parent) {
        String[] spath = path.split("/");

        //rekursively search for child
        Spatial child = parent;
        for (String subnode : spath) {
            if (subnode != null) {
                child = ((Node) child).getChild(subnode);
            } else {
                return null;
            }
        }

        return child;
    }

    /**
     * Accesses the metadata with the given name.
     * @param key the name of the metadata.
     * @return the value of the metadata.
     */
    public String getMetadata(String key) {
        return m_metadata.get(key);
    }

    /**
     * Accesses the {@link Node} with the given name.
     * @param key the name of the {@link Node} to access.
     * @return the {@link Node} with the given name.
     */
    public Spatial getSubNode(String key) {
        return m_nodes.get(key);
    }

    /**
     * Accesses the {@link Control} with the given name.
     * @param key the name of the {@link Control} to access.
     * @return the {@link Control} with the given name.
     */
    public Control getActiveControl(String key) {
        return m_controls.get(key);
    }

    /**
     * Called when the DGO this asset has ben loaded for is removed.
     */
    public void cleanup() {
        //cleanup all controls
        for(Map.Entry<String, AbstractAssetControl> entry : m_controls.entrySet()) {
            entry.getValue().cleanup();
        }
    }

    @Override
    public void setSpatial(Spatial spatial) {
        super.setSpatial(spatial);

        if (spatial instanceof Node) {
            final Node asset = (Node) spatial;

            //find sub nodes
            Stream<NodeInfo> nodes = m_info.getNodes();
            nodes.forEachOrdered(info -> {
                //rekursively search for child
                Spatial child = findChild(info.getPath(), asset);
                if (child != null) {
                    m_nodes.put(info.getKey(), child);
                } else {
                    throw new IllegalStateException("No child found: " + info.getPath() + ", " + m_info.getKey());
                }
            });

            //set materials
            if (m_assetManager != null) {
                Stream<MeshInfo> meshes = m_info.getMeshes();
                meshes.forEachOrdered(mesh -> {
                    //find geometry
                    Spatial geom = findChild(mesh.getPath(), asset);

                    if (geom != null) {
                        //load material
                        Material mat = m_assetManager.loadMaterial(mesh.getMaterial());

                        if (mat != null) {
                            //set material
                            geom.setMaterial(mat);
                        } else {
                            throw new AssetNotFoundException("No Material with name " + mesh.getMaterial() + " found");
                        }
                    } else {
                        throw new IllegalStateException("No Mesh with name " + mesh.getPath() + " found");
                    }
                });

            } else {
                throw new IllegalStateException("No AssetManager set to AssetControl");
            }

            //find or create controls
            Stream<ControlInfo> controls = m_info.getControls();
            controls.forEachOrdered(info -> {
                //rekursively search for child
                Spatial child = findChild(info.getPath(), asset);
                if (child != null) {
                    //get type of controltype
                    Class controlType = null;
                    try {
                        controlType = Class.forName(info.getType());
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }

                    if (controlType != null) {
                        //get instance of control
                        AbstractAssetControl control = null;
                        if (info.isPreload()) {
                            control = (AbstractAssetControl) asset.getControl(controlType);
                        } else {
                            try {
                                //find constructor and create a new instance
                                if (info.getParams() != null) {
                                    Constructor<?> constructor = controlType.getConstructor(String.class, AssetManager.class);
                                    control = (AbstractAssetControl) constructor.newInstance(info.getParams(), m_assetManager);
                                } else {
                                    Constructor<?> constructor = controlType.getConstructor();
                                    control = (AbstractAssetControl) constructor.newInstance();
                                }

                                //add control to child
                                child.addControl(control);
                            } catch (NoSuchMethodException e) {
                                e.printStackTrace();
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            } catch (InstantiationException e) {
                                e.printStackTrace();
                            } catch (InvocationTargetException e) {
                                e.printStackTrace();
                            }
                        }

                        //if control exists, add it to the controls
                        if (control != null) {
                            m_controls.put(info.getKey(), control);
                        } else {
                            throw new IllegalStateException("No Control of type found: " + info.getType());
                        }
                    }
                } else {
                    throw new IllegalStateException("No child found: " + info.getPath() + ", " + m_info.getKey());
                }
            });
        } else {
            throw new IllegalStateException("Asset without Node as a root");
        }
    }

    @Override
    protected void controlUpdate(float tpf) {
        //do nothing
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        //do nothing
    }
}
