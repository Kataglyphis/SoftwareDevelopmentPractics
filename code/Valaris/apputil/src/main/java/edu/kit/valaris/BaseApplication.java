package edu.kit.valaris;

import com.jme3.app.LegacyApplication;
import com.jme3.asset.ThreadingManager;
import com.jme3.profile.AppStep;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeSystem;
import edu.kit.valaris.assets.AssetProvider;
import edu.kit.valaris.assets.JsonInfoLoader;
import edu.kit.valaris.console.ConsoleManager;
import edu.kit.valaris.profiling.ProfilingAppState;
import edu.kit.valaris.rendering.ViewPortManager;
import edu.kit.valaris.threading.JobManager;

/**
 * {@link com.jme3.app.Application} that is expected to manage everything using {@link com.jme3.app.state.AppState}s
 * and the {@link ViewPortManager}.
 *
 * @author Frederik Lingg
 */
public abstract class BaseApplication extends LegacyApplication {

    /**
     * Creates a new {@link BaseApplication}.
     */
    public BaseApplication() {
        super();
    }

    /**
     * Attaches base {@link com.jme3.app.state.AppState}s.
     */
    protected abstract void init();

    @Override
    public void start() {
        // set some default settings in-case
        // settings dialog is not shown
        if (settings == null) {
            setSettings(new AppSettings(true));

            // show settings dialog
            if (!JmeSystem.showSettingsDialog(settings, true)) {
                return;
            }
        }

        //re-setting settings they can have been merged from the registry.
        setSettings(settings);
        super.start();
    }

    @Override
    public void initialize() {
        super.initialize();

        //disable standard viewports
        getGuiViewPort().setEnabled(false);
        getViewPort().setEnabled(false);

        //initialize base appstates
        //add metadata
        getStateManager().attach(new Metadata(this, settings.getWidth(), settings.getHeight()));

        //add ViewPortManager
        getStateManager().attach(new ViewPortManager(getRenderManager()));

        //add profiler
        getStateManager().attach(new ProfilingAppState());

        //add Console
        getStateManager().attach(new ConsoleManager());

        //add AssetProvider and base loaders
        AssetProvider provider = new AssetProvider(getAssetManager());
        provider.setInfoLoader("json", new JsonInfoLoader());
        getStateManager().attach(provider);

        //add basic commands
        getStateManager().getState(ConsoleManager.class).addCommand(new ConsoleManager.Command(){{
            name("show_profiler");
            description("Usage: show_profiler <boolean>\n" +
                    "Shows/Hides the profiler based on the given flag");
            action(() -> {
                if(args().length != 2) {
                    out("use show_profiler <boolean>");
                    return;
                }

                try {
                    boolean flag = Boolean.parseBoolean(args()[1]);
                    getStateManager().getState(ProfilingAppState.class).setEnabled(flag);
                } catch (NumberFormatException e) {
                    out("use show_profiler <boolean>");
                    return;
                }
            });
        }});

        //init app
        init();
    }

    @Override
    public void update() {
        //update app
        if (prof != null) {
            prof.appStep(AppStep.BeginFrame);
        }

        super.update();

        if (paused) {
            return;
        }

        float tpf = timer.getTimePerFrame();

        //update statemanager
        if (prof != null) {
            prof.appStep(AppStep.StateManagerUpdate);
        }
        stateManager.update(tpf);

        //render statemanager
        if (prof != null) {
            prof.appStep(AppStep.StateManagerRender);
        }
        stateManager.render(renderManager);

        //render frame
        if (prof != null) {
            prof.appStep(AppStep.RenderFrame);
        }
        renderManager.render(tpf, context.isRenderable());
        stateManager.postRender();

        //end frame
        if (prof != null) {
            prof.appStep(AppStep.EndFrame);
        }
    }

    @Override
    public void reshape(int w, int h) {
        super.reshape(w, h);
        //update metadata
        getStateManager().getState(Metadata.class).setWidth(w);
        getStateManager().getState(Metadata.class).setHeight(h);
    }

    @Override
    public void stop() {
        //update metadata
        getStateManager().getState(Metadata.class).setRunning(false);
        super.stop();
    }

    @Override
    public void requestClose(boolean esc) {
        //update metadata
        getStateManager().getState(Metadata.class).setRunning(false);
        super.requestClose(esc);
    }

    @Override
    public void loseFocus() {
        super.loseFocus();
        getStateManager().getState(Metadata.class).setFocus(false);
    }

    @Override
    public void gainFocus() {
        super.gainFocus();
        getStateManager().getState(Metadata.class).setFocus(true);
    }
}
