package edu.kit.valaris.rendering;

import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import edu.kit.valaris.Metadata;

import java.util.ArrayList;
import java.util.List;

/**
 * Appstate for managing active {@link ViewPort}s.
 *
 * @author Frederik Lingg
 */
public class ViewPortManager extends AbstractAppState {

    public enum ViewPortType {
        /**
         * Viewport for consoles.
         */
        TYPE_CONSOLE,

        /**
         * Viewport for the actual scene.
         */
        TYPE_SCENE,

        /**
         * Viewport for gui.
         */
        TYPE_GUI
    }

    /**
     * Contains all active {@link ViewPort}s.
     */
    private List<ViewPort> m_viewports;

    /**
     * The {@link RenderManager} used by the current application.
     */
    private RenderManager m_renderManager;

    /**
     * The {@link AppStateManager} this {@link ViewPortManager} uses.
     */
    private AppStateManager m_stateManager;

    /**
     * Creates a new {@link ViewPortManager} with no {@link ViewPort}.
     * @param renderManager the {@link RenderManager} the application uses.
     */
    public ViewPortManager(RenderManager renderManager) {
        m_viewports = new ArrayList<>();
        m_renderManager = renderManager;
    }

    /**
     * Checks whether the id is valid.
     * @param id the id to validate.
     * @return whether the id is valid.
     */
    private boolean validateId(int id) {
        return m_viewports.size() > id && m_viewports.get(id) != null;
    }

    /**
     * Creates a new {@link ViewPort} and adds it to to this {@link ViewPortManager}.
     * @param name the name of the {@link ViewPort} to add.
     * @param type the {@link ViewPortType}.
     * @param scene the scene to render in the new {@link ViewPort}.
     * @param minX the minimum x value of the viewport in range 0 - 1.
     * @param minY the minimum y value of the viewport in range 0 - 1.
     * @param maxX the maximum x value of the viewport in range 0 - 1.
     * @param maxY the maximum y value of the viewport in range 0 - 1.
     * @return the id of the added {@link ViewPort} or -1 if something went wrong.
     */
    public int addViewPort(String name, ViewPortType type, Node scene, float minX, float minY, float maxX, float maxY) {
        //do nothing if viewport is actually null
        if (scene == null) {
            return -1;
        }

        //create camera
        float vpWidth = maxX - minX;
        float vpHeight = maxY - minY;

        Metadata meta = m_stateManager.getState(Metadata.class);
        Camera cam = new Camera(meta.getWidth(), meta.getHeight());
        cam.setViewPort(minX, maxX, minY, maxY);

        System.out.println("Creating Viewport with dims " + cam.getWidth() + ", " + cam.getHeight());

        //create new ViewPort
        ViewPort vp = null;
        switch (type) {
            case TYPE_SCENE:
                vp = m_renderManager.createPreView(name, cam);
                break;
            case TYPE_GUI:
                vp = m_renderManager.createMainView(name, cam);
                break;
            case TYPE_CONSOLE:
                vp = m_renderManager.createPostView(name, cam);
                break;
            default:
        }

        //if no viewport could be created, return
        if (vp == null) {
            return -1;
        }

        //set Basic Params for ViewPort and cam
        switch (type) {
            case TYPE_SCENE:
                vp.setBackgroundColor(ColorRGBA.Black);
                vp.setClearFlags(true, true, true);
                vp.attachScene(scene);

                //init camera with base values
                cam.setFrustumPerspective(60, ((float) meta.getWidth() * vpWidth) / ((float) meta.getHeight() * vpHeight), 1f, 1000f);
                break;
            case TYPE_GUI:
                vp.setBackgroundColor(ColorRGBA.Black);
                vp.setClearFlags(false, false, false);
                vp.attachScene(scene);
                break;
            case TYPE_CONSOLE:
                vp.setBackgroundColor(ColorRGBA.Black);
                vp.setClearFlags(false, false, false);
                vp.attachScene(scene);
                break;
            default:
        }

        //search the list for empty ids
        int id = -1;
        for (int i = 0; i < m_viewports.size(); i++) {
            if (m_viewports.get(i) == null) {
                id = i;
                break;
            }
        }

        //if no id was found, add a new one.
        if (id >= 0) {
            m_viewports.set(id, vp);
        } else {
            id = m_viewports.size();
            m_viewports.add(vp);
        }

        return id;
    }

    /**
     * Removes the viewport at the given id.
     * @param id the id of the {@link ViewPort} to be deleted.
     */
    public void removeViewPort(int id) {
        //if id does not exist, dont do anything
        if (!validateId(id)) {
            return;
        }

        //destroy viewport
        ViewPort vp = m_viewports.get(id);
        boolean success = !m_renderManager.removePreView(vp) ? !m_renderManager.removeMainView(vp) ? m_renderManager.removePostView(vp) : true : true;
        if (!success) {
            throw new IllegalStateException("ViewPort was destroyed without notify");
        }

        //set to null so that other ids dont change
        m_viewports.set(id, null);
    }

    /**
     * Accesses the {@link ViewPort} with the given id.
     * @param vp the id of the {@link ViewPort}.
     * @return the {@link ViewPort} with the given id or null if no {@link ViewPort} with the given id exists.
     */
    public ViewPort getViewPort(int vp) {
        if (!validateId(vp)) {
            return null;
        }

        return m_viewports.get(vp);
    }

    /**
     * Accesses a {@link ViewPort}. via its name.
     * @param name the name of the {@link ViewPort}.
     * @return the {@link ViewPort} with the given name or null if no such {@link ViewPort} exists.
     */
    public ViewPort getViewPort(String name) {
        //check all ViewPortTypes and use first one found
        ViewPort vp = m_renderManager.getPreView(name);
        vp = vp != null ? vp : m_renderManager.getMainView(name);
        vp = vp != null ? vp : m_renderManager.getPostView(name);

        return vp;
    }

    @Override
    public void stateAttached(AppStateManager stateManager) {
        super.stateAttached(stateManager);
        m_stateManager = stateManager;
    }

    @Override
    public void stateDetached(AppStateManager stateManager) {
        super.stateDetached(stateManager);
        m_stateManager = null;
    }
}
