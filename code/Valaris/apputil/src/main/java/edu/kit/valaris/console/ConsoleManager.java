package edu.kit.valaris.console;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.Console;
import de.lessvoid.nifty.controls.ConsoleCommands;
import de.lessvoid.nifty.tools.Color;
import edu.kit.valaris.BaseApplication;
import edu.kit.valaris.Metadata;
import edu.kit.valaris.rendering.ViewPortManager;

import javax.annotation.Nonnull;
import java.util.LinkedList;
import java.util.List;

/**
 * An {@link com.jme3.app.state.AppState} representing and displaying a console.
 * @author Frederik Lingg
 */
public class ConsoleManager extends AbstractAppState {

    /**
     * Used to tell other appstates that use input whether the console is visible.
     */
    public interface ConsoleStatusListener {

        /**
         * indicates whether the console is visible or not.
         * @param flag whether toe Console is visible.
         */
        public void consoleVisible(boolean flag);
    }

    /**
     * Commands are executed by the Console.
     */
    public static class Command implements ConsoleCommands.ConsoleCommand {
        /**
         * The name of this Command.
         */
        private String m_name;

        /**
         * The description of this command
         */
        private String m_desc;

        /**
         * The console used to execute this Command.
         */
        private Console m_console;

        /**
         * the action that is executed by this Command.
         */
        private Runnable m_action;

        /**
         * The current arguments of this Command.
         */
        private String[] m_args;

        /**
         * Sets the Console that calls this command.
         * @param console the console.
         */
        private void setConsole(Console console) {
            m_console = console;
        }

        /**
         * Sets the arguments for executing this Command.
         * @param args the arguments.
         */
        private void setArgs(String[] args) {
            m_args = args;
        }

        /**
         * Accesses the name of the Command.
         * @return the name.
         */
        private String getName() {
            return m_name;
        }

        /**
         * Accesses the description of the command.
         * @return the description.
         */
        private String getDescription() {
            return m_desc;
        }

        /**
         * Sets the name of this Command.
         * @param name the name.
         */
        protected void name(String name) {
            m_name = name;
        }

        /**
         * Sets the description of this command.
         * @param desc the description.
         */
        protected void description(String desc) {
            m_desc = desc;
        }

        /**
         * Sets the action of this Command.
         * @param action the action.
         */
        protected void action(Runnable action) {
            m_action = action;
        }

        /**
         * Accesses the Arguments for executing this command.
         * @return the arguments.
         */
        protected String[] args() {
            return m_args;
        }

        /**
         * Prints a message to the console.
         * @param message the message to print.
         */
        protected void out(String message) {
            m_console.output(message);
        }

        @Override
        public void execute(@Nonnull String... args) {
            if(args[0].equals(m_name)) {
                setArgs(args);
                m_action.run();
            }
        }
    }

    /**
     * The {@link AppStateManager} this ConsoleManager is attached to.
     */
    private AppStateManager m_stateManager;

    /**
     * The NiftyJmeDisplay currently used by the console.
     */
    private NiftyJmeDisplay m_display;

    /**
     * The Nifty currently used by the console.
     */
    private Nifty m_nifty;

    /**
     * the nifty Console used by the Console.
     */
    private Console m_console;

    /**
     * A List of all commands to use in the console
     */
    private List<Command> m_commandArchive;

    /**
     * The list of currently active commands.
     */
    private ConsoleCommands m_commands;

    /**
     * The root node of the guiscene.
     */
    private Node m_root;

    /**
     * The index of the viewport used by this console.
     */
    private int m_vpi;

    /**
     * The viewport of the console.
     */
    private ViewPort m_vp;

    /**
     * The ActionListener listening for the ToggleConsole event.
     */
    private ActionListener m_toggleConsoleListener = (name, pressed, tpf) -> {
        if (name.equals("ToggleConsole") && pressed) {
            //enqueue console toggle after current frame
            Metadata meta = m_stateManager.getState(Metadata.class);
            if(meta != null) {
                meta.getApp().enqueue(() -> {
                    //switch enabled flag
                    setEnabled(!isEnabled());
                });
            } else {
                throw new IllegalStateException("No Metadata found");
            }
        }
    };

    private List<ConsoleStatusListener> m_listeners;

    /**
     * Creates a new ConsoleManager.
     */
    public ConsoleManager() {
        m_listeners = new LinkedList<>();
        m_commandArchive = new LinkedList<>();

        addCommand(new Command() {{
            name("help");
            description("Usage: help\n" +
                    "Prints all available commands");
            action(() -> {
                if(args().length != 1) {
                    out("use help");
                }

                //print command names
                for(Command c : m_commandArchive) {
                    out(c.getDescription());
                    out("\n");
                }
            });
        }});
    }

    /**
     * Adds a {@link ConsoleStatusListener} to this {@link ConsoleManager}.
     * @param listener the listener.
     */
    public void addStatusListener(ConsoleStatusListener listener) {
        m_listeners.add(listener);
    }

    /**
     * Removes the a {@link ConsoleStatusListener} from this {@link ConsoleManager}.
     * @param listener the listener to remove.
     */
    public void removeStatusListener(ConsoleStatusListener listener) {
        m_listeners.remove(listener);
    }

    /**
     * Adds a {@link de.lessvoid.nifty.controls.ConsoleCommands.ConsoleCommand} to this {@link ConsoleManager}.
     * @param command the {@link de.lessvoid.nifty.controls.ConsoleCommands.ConsoleCommand}.
     */
    public void addCommand(Command command) {
        m_commandArchive.add(command);

        if(m_commands != null) {
            command.setConsole(m_console);
            m_commands.registerCommand(command.getName(), command);
        }
    }

    /**
     * Shows the ui containing the console.
     */
    private void showConsole() {
        m_vp.setEnabled(true);
        m_nifty.setIgnoreKeyboardEvents(false);
    }

    /**
     * Hides the ui containing the console.
     */
    private void hideConsole() {
        m_vp.setEnabled(false);
        m_nifty.setIgnoreKeyboardEvents(true);
    }

    @Override
    public void setEnabled(boolean flag) {
        super.setEnabled(flag);

        //notify listeners
        for(ConsoleStatusListener l : m_listeners) {
            l.consoleVisible(flag);
        }

        //show/hide console accordingly
        if(flag) {
            showConsole();
        } else {
            hideConsole();
        }
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        //init parent
        super.initialize(stateManager, app);

        //cache statemanager
        m_stateManager = stateManager;

        //add inputs
        app.getInputManager().addMapping("ToggleConsole", new KeyTrigger(KeyInput.KEY_F5));
        app.getInputManager().addListener(m_toggleConsoleListener, "ToggleConsole");

        //init node
        m_root = new Node("Console-Root");
        m_root.updateLogicalState(0);
        m_root.updateGeometricState();

        //create viewport
        ViewPortManager vpm = m_stateManager.getState(ViewPortManager.class);
        if(vpm == null) {
            throw new IllegalStateException("No ViewPortManager found");
        }
        m_vpi = vpm.addViewPort("ConsolePort", ViewPortManager.ViewPortType.TYPE_CONSOLE, m_root, 0f, 0f, 1f, 1f);
        m_vp = vpm.getViewPort(m_vpi);

        //create nifty
        m_display = NiftyJmeDisplay.newNiftyJmeDisplay(app.getAssetManager(), app.getInputManager(), app.getAudioRenderer(), m_vp);
        m_nifty = m_display.getNifty();
        m_vp.addProcessor(m_display);

        //load ui
        m_nifty.fromXml("edu/kit/valaris/console/console-manager.xml", "console-manager");

        //get console object and init commands
        m_console = m_nifty.getCurrentScreen().findNiftyControl("console", Console.class);
        m_console.changeColors(new Color("#fff"), new Color("#ff0"));
        m_commands = new ConsoleCommands(m_nifty, m_console);
        for(int i = 0; i < m_commandArchive.size(); i++) {
            m_commandArchive.get(i).setConsole(m_console);
            m_commands.registerCommand(m_commandArchive.get(i).getName(), m_commandArchive.get(i));
        }
        m_commands.enableCommandCompletion(true);

        //console should be disabled by default
        setEnabled(false);
    }

    @Override
    public void cleanup() {
        //destroy nifty
        m_nifty.exit();
        m_display.cleanup();

        //remove viewport
        ViewPortManager vpm = m_stateManager.getState(ViewPortManager.class);
        if(vpm == null) {
            throw new IllegalStateException("No ViewPortManager found");
        }
        vpm.removeViewPort(m_vpi);

        m_commands = null;
        m_console = null;
        m_display = null;
        m_nifty = null;
        m_vp = null;
        m_vpi = -1;
    }
}
