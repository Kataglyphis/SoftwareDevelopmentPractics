package edu.kit.valaris.rendering;

import com.jme3.light.Light;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;

import java.util.ArrayList;

/**
 * {@link com.jme3.scene.control.Control} that holds culling information about its {@link com.jme3.scene.Spatial}.
 *
 * @author Frederik Lingg
 */
public class CullingControl extends AbstractControl {

    /**
     * The index of the cullable.
     */
    private int m_index;

    /**
     * The Face covering the entry.
     */
    private Vector3f[] m_entryFrame;

    /**
     * The face covering the exit.
     */
    private Vector3f[] m_exitFrame;

    /**
     * Whether the door on the exit of this Cullable is open.
     */
    private boolean m_isDoorOpen;

    /**
     * List of all lights contained in scene Item
     */
    private ArrayList<Light> m_lights;

    /**
     * Whether the {@link CullingControl} was visible last frame.
     */
    private boolean m_lastVisible;

    private Node m_parent;

    /**
     * Creates a new {@link CullingControl} with the given index and frames.
     * @param index the index in order of the RaceTrack of the cullable object.
     * @param entryFrame the vertices of the face that covers the entry to this cullable.
     * @param exitFrame the vertices of the face that covers the exit of this cullable.
     */
    public CullingControl(int index, Vector3f[] entryFrame, Vector3f[] exitFrame, ArrayList<Light> lights) {
        m_index = index;
        m_entryFrame = entryFrame;
        m_exitFrame = exitFrame;
        m_isDoorOpen = true;
        m_lights = lights;
        m_lastVisible = true;
    }

    /**
     * Accesses the index of the cullable.
     * @return the index.
     */
    public int getIndex() {
        return m_index;
    }

    /**
     * Accesses the entry frame of the cullable.
     * @return the face covering the entry.
     */
    public Vector3f[] getEntryFrame() {
        return m_entryFrame;
    }

    /**
     * Accesses the exit frame of the cullable.
     * @return the face covering the exit.
     */
    public Vector3f[] getExitFrame() {
        return m_exitFrame;
    }

    /**
     * Sets whether this cullable is visible or not.
     * @param visible whether the spatial is visible or not.
     */
    public void setVisible(boolean visible) {
        if(m_lastVisible != visible) {
            getSpatial().setCullHint(visible ? Spatial.CullHint.Inherit : Spatial.CullHint.Always);

            for (Light light :
                    m_lights) {
                light.setEnabled(visible);
            }
            m_lastVisible = visible;
        }
    }

    /**
     * Sets whether the door at the exit of this cullable is open or not.
     * @param flag whether the door is open.
     */
    public void setDoorOpen(boolean flag) {
        m_isDoorOpen = flag;
    }

    /**
     * Accesses whether the door at the end of this cullable is open or not.
     * @return whether the door is open.
     */
    public boolean isDoorOpen() {
        return m_isDoorOpen;
    }

    @Override
    protected void controlUpdate(float tpf) {
        //do nothing here
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        //do nothing here
    }
}
