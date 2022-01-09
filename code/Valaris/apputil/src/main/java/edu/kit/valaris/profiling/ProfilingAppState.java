package edu.kit.valaris.profiling;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.TextRenderer;
import edu.kit.valaris.rendering.ViewPortManager;

import java.util.HashMap;
import java.util.Map;

/**
 * An {@link com.jme3.app.state.AppState} used to collect and display information about the performance of an application.
 */
public class ProfilingAppState extends AbstractAppState {

    /**
     * A Map containing the root sections of all threads that use the profiler
     */
    private Map<Thread, ProfilingSection> m_profiles;

    /**
     * The id of the ViewPort used for rendering.
     */
    private int m_viewPort;

    private ViewPort m_vp;

    private NiftyJmeDisplay m_display;

    private Nifty m_nifty;

    private TextRenderer m_fps;

    private float m_timeAccu;

    private int m_frameAccu;

    /**
     * The Root of the scene containing information about the Profiler.
     */
    private Node m_root;

    public ProfilingAppState() {
        m_profiles = new HashMap<>();
        m_viewPort = -1;
        m_root = new Node("Profiling Root");
    }

    /**
     * Sets the root {@link ProfilingSection} to use on the thread this method is called on.
     * @param profile the root {@link ProfilingSection}.
     */
    public void setProfile(ProfilingSection profile) {
        //use current Thread
        setProfile(Thread.currentThread(), profile);
    }

    /**
     * Sets the root {@link ProfilingSection} for the given thread
     * @param thread the thread that uses the given root
     * @param profile the root {@link ProfilingSection}
     */
    public void setProfile(Thread thread, ProfilingSection profile) {
        m_profiles.put(thread, profile);
    }

    /**
     * Starts the {@link ProfilingSection} with the given name.
     * @param section the name of the profiling section
     */
    public void start(String section) {
        ProfilingSection root = m_profiles.get(Thread.currentThread());
        if(root != null) {
            root.start(section);
        } else {
            throw new IllegalStateException("No Profile for thread " + Thread.currentThread().getName() + " found.");
        }
    }

    /**
     * Ends the {@link ProfilingSection} with the given name.
     * @param section the name of the profiling section
     */
    public void end(String section) {
        ProfilingSection root = m_profiles.get(Thread.currentThread());
        if(root != null) {
            root.end(section);
        } else {
            throw new IllegalStateException("No Profile for thread " + Thread.currentThread().getName() + " found.");
        }
    }

    @Override
    public void setEnabled(boolean flag) {
        if(m_vp != null) {
            m_vp.setEnabled(flag);
        }
    }

    @Override
    public void update(float tpf) {
        m_root.updateLogicalState(tpf);
        m_root.updateGeometricState();

        m_timeAccu += tpf;
        m_frameAccu++;
        if(m_timeAccu >= 1.0f) {
            m_fps.setText("fps: " + (int) ((float) m_frameAccu / m_timeAccu));
            m_timeAccu = 0;
            m_frameAccu = 0;
        }
    }

    @Override
    public void stateAttached(AppStateManager stateManager) {
        super.stateAttached(stateManager);

        ViewPortManager viewPortManager = stateManager.getState(ViewPortManager.class);
        if(viewPortManager != null) {
            Application app = stateManager.getApplication();

            m_viewPort = viewPortManager.addViewPort("Profiler", ViewPortManager.ViewPortType.TYPE_CONSOLE, m_root, 0.0f, 0.0f, 1.0f, 1.0f);
            m_vp = viewPortManager.getViewPort(m_viewPort);

            m_display = NiftyJmeDisplay.newNiftyJmeDisplay(app.getAssetManager(), app.getInputManager(), app.getAudioRenderer(), m_vp);
            m_nifty = m_display.getNifty();

            m_vp.addProcessor(m_display);
            m_nifty.fromXml("edu/kit/valaris/profiling/profiling-screen.xml", "profiling-screen");

            m_fps = m_nifty.getCurrentScreen().findElementById("fpsLabel").getRenderer(TextRenderer.class);
        }

        setEnabled(false);
    }

    @Override
    public void stateDetached(AppStateManager stateManager) {
        super.stateDetached(stateManager);

        if(m_viewPort >= 0) {
            ViewPortManager viewPortManager = stateManager.getState(ViewPortManager.class);
            if(viewPortManager != null) {
                m_nifty.exit();
                m_display.cleanup();

                viewPortManager.removeViewPort(m_viewPort);
            }
        }
    }
}
