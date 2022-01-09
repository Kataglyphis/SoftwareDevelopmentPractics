package edu.kit.valaris.rendering;

import com.jme3.bounding.BoundingSphere;
import com.jme3.math.Matrix4f;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.math.Vector4f;
import com.jme3.post.SceneProcessor;
import com.jme3.profile.AppProfiler;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.texture.FrameBuffer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 * Used to decide which nodes are visible for a given camera.
 *
 * @author Frederik Lingg
 */
public class CullingManager implements SceneProcessor {

    private boolean m_isInitialized;

    /**
     * A List of all cullable items in the scene.
     */
    private List<CullingControl> m_cullables;

    /**
     * A List of all cullables that contain the camera in their boundingbox.
     */
    private List<CullingControl> m_currentCamHosts;

    /**
     * The {@link Camera} to use as a view point for occlusion culling.
     */
    private Camera m_camera;

    private Vector2f[] m_tmpProjectionWindow = new Vector2f[] {new Vector2f(), new Vector2f(), new Vector2f(), new Vector2f()};

    private Vector2f[] m_tmpProjectionFace = new Vector2f[] {new Vector2f(), new Vector2f(), new Vector2f(), new Vector2f()};

    private Vector2f m_tmpDir = new Vector2f();
    private Vector3f m_tmpProjector = new Vector3f();

    /**
     * Creates a new {@link CullingManager} using the given camera and Nodes to provide a scene using occlusion-culling.
     * @param cullContents all seperately cullable items attached to a single node.
     */
    public CullingManager(Node cullContents) {
        m_isInitialized = false;

        //find cullables
        m_cullables = new ArrayList<>();
        for (Spatial spatial : cullContents.getChildren()) {
            CullingControl cullable = spatial.getControl(CullingControl.class);
            if (cullable != null) {
                m_cullables.add(cullable);
            }
        }

        //sort cullables in ascending order
        m_cullables.sort(Comparator.comparingInt(CullingControl::getIndex));

        //init camhosts and visible cullables as empty
        m_currentCamHosts = new LinkedList<>();
    }

    /**
     * Recalculates the boundingvolume with the given vertices.
     * @param bound the BoudningVolume to recalculate.
     * @param vertices the vertices to be contained by the bounding volume.
     */
    private void setBoudningVolume(BoundingSphere bound, Vector3f[] vertices) {
        //calculate center of mass
        Vector3f centerOfMass = new Vector3f();
        for (Vector3f vector : vertices) {
            centerOfMass.addLocal(vector);
        }
        centerOfMass.divideLocal(vertices.length);

        //calculate radius
        float radius = 0;
        for (Vector3f vector : vertices) {
            float dst = vector.distance(centerOfMass);
            radius = radius < dst ? dst : radius;
        }

        //apply calculated values
        bound.setCenter(centerOfMass);
        bound.setRadius(radius);
    }

    /**
     * Checks whether the 2 arbitrary polygons are overlapping.
     * @param window the window the camera can look through, projected on the camera plane.
     * @param face the face for wich to check visibility through the window. also projected on the camera plane.
     * @return whether the camera can see the face through the window.
     */
    private boolean checkOverlap(Vector2f[] window, Vector2f[] face) {
        //if they do not overlap, there is at least one direction equal to the direction of a side of the window,
        //one would be able to draw a line between the 2 polygons
        //so check for each edge direction of window

        //using vector3f for convenience essentially this calculation is 2D

        for (int i = 0; i < window.length; i++) {
            window[i].subtract(window[(i + 1) % window.length], m_tmpDir);
            m_tmpDir.normalizeLocal();

            //calc normal and store in same vector
            m_tmpDir.set(m_tmpDir.getY(), -m_tmpDir.getX());

            //now use projection on normal to see whether a line with the original direction can seperate the sets
            //project window
            float wd = window[0].dot(m_tmpDir);
            float minW = wd;
            float maxW = wd;
            for(int j = 1; j < window.length; j++) {
                wd = window[j].dot(m_tmpDir);
                minW = Math.min(minW, wd);
                maxW = Math.max(maxW, wd);
            }

            //project face
            float fd = face[0].dot(m_tmpDir);
            float minF = fd;
            float maxF = fd;
            for(int j = 1; j < face.length; j++) {
                fd = face[j].dot(m_tmpDir);
                minF = Math.min(minF, fd);
                maxF = Math.max(maxF, fd);
            }

            //check overlap of intervals
            if (minF > maxW || minW > maxF) {
                return false;
            }
        }

        return true;
    }

    /**
     * Projects the given src vertices on the camera viewPlane and stores them in the given target vertices.
     * @param src the source vertices.
     * @param target the target vertices.
     * @return the number of vertices left after clipping
     */
    private void project(Vector3f[] src, Vector2f[] target) {
        for(int i = 0; i < src.length; i++) {
            float w = m_camera.getViewProjectionMatrix().multProj(src[i], m_tmpProjector);
            m_tmpProjector.divideLocal(w);
            target[i].set(m_tmpProjector.x, m_tmpProjector.y);
        }
    }

    @Override
    public void initialize(RenderManager rm, ViewPort vp) {
        m_camera = vp.getCamera();
        m_isInitialized = true;
    }

    @Override
    public void reshape(ViewPort vp, int w, int h) {
        //do nothing here
    }

    @Override
    public boolean isInitialized() {
        return m_isInitialized;
    }

    @Override
    public void preFrame(float tpf) {
        //find cullables the camera could be in and make all cullables invisible
        //visible cullables will be made visible again later
        m_currentCamHosts.clear();
        for (int i = 0; i < m_cullables.size(); i++) {
            CullingControl cullable = m_cullables.get(i);
            cullable.setVisible(false);

            if (cullable.getSpatial().getWorldBound().contains(m_camera.getLocation())) {
                m_currentCamHosts.add(cullable);
            }
        }

        //use this as bound for frustrum intersect, to save instances
        BoundingSphere tmpBound = new BoundingSphere();

        //do visibility calculations from each host spatial
        for (CullingControl host : m_currentCamHosts) {
            //host is always visible
            host.setVisible(true);

            //check in race direction
            //first check whether the exit is visible
            setBoudningVolume(tmpBound, host.getExitFrame());
            if (m_camera.contains(tmpBound) != Camera.FrustumIntersect.Outside) {

                //compare entry faces of following cullables to see if they can be seen
                //directly following cullable does not need to be checked, since its entryface is equal to the hosts exit face.
                if (host.getIndex() + 1 < m_cullables.size()) {
                    m_cullables.get(host.getIndex() + 1).setVisible(true);
                }

                if (host.getIndex() + 2 < m_cullables.size()) {
                    m_cullables.get(host.getIndex() + 2).setVisible(true);
                    project(m_cullables.get(host.getIndex() + 2).getEntryFrame(), m_tmpProjectionWindow);
                }

                //all other cullables need to be checked
                int index = host.getIndex() + 3;
                boolean lastVisible = true;
                while (lastVisible && index < m_cullables.size()) {
                    CullingControl cullable = m_cullables.get(index);

                    //project entry to viewing plane
                    project(cullable.getEntryFrame(), m_tmpProjectionFace);

                    //if exitframe of host and entryframe of this cullable overlap, this cullable can be seen
                    if (checkOverlap(m_tmpProjectionWindow, m_tmpProjectionFace)) {
                        cullable.setVisible(true);
                        m_tmpProjectionWindow[0].set(m_tmpProjectionFace[0]);
                        m_tmpProjectionWindow[1].set(m_tmpProjectionFace[1]);
                        m_tmpProjectionWindow[2].set(m_tmpProjectionFace[2]);
                        m_tmpProjectionWindow[3].set(m_tmpProjectionFace[3]);

                        lastVisible = true;
                    } else {
                        lastVisible = false;
                    }

                    //increment index
                    index++;
                }
            }

            //check in oposite direction
            //first check whether the exit is visible
            setBoudningVolume(tmpBound, host.getEntryFrame());
            if (m_camera.contains(tmpBound) != Camera.FrustumIntersect.Outside) {
                //project exit to viewing plane
                project(host.getEntryFrame(), m_tmpProjectionWindow);

                //compare entry faces of preceeding cullables to see if they can be seen
                //directly preceeding cullable does not need to be checked, since its exitface is equal to the hosts entry face.
                if (host.getIndex() - 1 >= 0) {
                    m_cullables.get(host.getIndex() - 1).setVisible(true);
                }

                //all other cullables need to be checked
                int index = host.getIndex() - 2;
                boolean lastVisible = true;
                while (lastVisible && index >= 0) {
                    CullingControl cullable = m_cullables.get(index);

                    //project entry to viewing plane
                    project(cullable.getExitFrame(), m_tmpProjectionFace);

                    //if exitframe of host and entryframe of this cullable overlap, this cullable can be seen
                    if (checkOverlap(m_tmpProjectionWindow, m_tmpProjectionFace)) {
                        cullable.setVisible(true);
                        m_tmpProjectionWindow[0].set(m_tmpProjectionFace[0]);
                        m_tmpProjectionWindow[1].set(m_tmpProjectionFace[1]);
                        m_tmpProjectionWindow[2].set(m_tmpProjectionFace[2]);
                        m_tmpProjectionWindow[3].set(m_tmpProjectionFace[3]);

                        lastVisible = true;
                    } else {
                        lastVisible = false;
                    }

                    //increment index
                    index--;
                }
            }
        }
    }

    @Override
    public void postQueue(RenderQueue rq) {
        //nothing to do here
    }

    @Override
    public void postFrame(FrameBuffer out) {
        //nothing to do here
    }

    @Override
    public void cleanup() {
        //nothing to do here
    }

    @Override
    public void setProfiler(AppProfiler profiler) {
        //dont use the profiler right now
    }
}