package edu.kit.valaris.dummysim;

import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.*;
import edu.kit.valaris.Metadata;
import edu.kit.valaris.datastructure.Position;
import edu.kit.valaris.tick.DynamicGameObject;
import edu.kit.valaris.tick.DynamicGameObjectType;
import edu.kit.valaris.tick.TickEvent;
import edu.kit.valaris.tick.Ticker;
import edu.kit.valaris.tick.properties.CameraProperty;
import edu.kit.valaris.tick.properties.TransformProperty;
import edu.kit.valaris.tick.properties.VehicleProperty;
import edu.kit.valaris.tick.properties.VelocityProperty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Appstate that is a very basic simulation, just containing a flycam and some {@link DynamicGameObject}s.
 *
 * @author Frederik Lingg
 */
public class SimulationDummy extends AbstractAppState {

    /**
     * The {@link AppStateManager} this {@link SimulationDummy} is attached to.
     */
    private AppStateManager m_stateManager;

    /**
     * The Ticker used for the simulation.
     */
    private Ticker m_ticker;

    /**
     * whether the simulation is currently running or not.
     */
    private boolean m_running;

    /**
     * The {@link ActionListener} listening to the app exit button.
     */
    private ActionListener m_exitListener = (name, pressed, tpf) -> {
        if (name.equals("Exit") && pressed && isEnabled()) {
            enqueue(() -> m_ticker.getSimulationBuffer().addEvent(new TickEvent(TickEvent.EVENT_SIMULATION_EXCEPTION, new LinkedList<>(Arrays.asList(new IllegalStateException("Button was Pressed"))))));
        }
    };

    /**
     * Tasks that are queued for running in the simulation thread.
     */
    private LinkedList<Runnable> m_tasks;

    /**
     * The {@link Lock} used for synchronizing access to the task list.
     */
    private Lock m_tasksLock;

    /**
     * Creates a new {@link SimulationDummy}.
     */
    public SimulationDummy() {
        m_tasks = new LinkedList<>();
        m_tasksLock = new ReentrantLock();
        m_running = false;
    }

    private void startSimulation() {
        //only start simulation if not already running
        if (!m_running) {
            //register input events
            Metadata meta = m_stateManager.getState(Metadata.class);
            meta.getApp().getInputManager().addMapping("Exit", new KeyTrigger(KeyInput.KEY_ESCAPE));
            meta.getApp().getInputManager().addListener(m_exitListener, "Exit");

            Thread thread = new Thread(() -> {
                //init camera
                DynamicGameObject cam = new DynamicGameObject(DynamicGameObjectType.DUMMY);
//                int camProp = cam.addProperty(new TransformProperty(new Transform(new Vector3f(0, 0, -10))));
                int camProp = cam.addProperty(new TransformProperty(new Transform(new Vector3f(0, 0, -10)), new Position(0, new Vector2f()), 0, new Vector2f()));

                cam.addProperty(new CameraProperty(new Vector3f(), Vector3f.UNIT_X.negate(), Vector3f.UNIT_Z, 0));
                m_ticker.getSimulationBuffer().addDynamicGameObject(cam, null);
                FlyCam flyCam = new FlyCam((TransformProperty) cam.getProperty(camProp), meta.getApp().getInputManager());

                //init hovercart
                DynamicGameObject cart = new DynamicGameObject(DynamicGameObjectType.VEHICLE);
//                cart.addProperty(new TransformProperty(new Transform(new Vector3f(0.0f, 0.5f, 3.0f)), new Position(0, new Vector2f()), 0, new Vector2f()));
                cart.addProperty(new TransformProperty(new Transform(new Vector3f(0.0f, 0.5f, 3.0f)), new Position(0, new Vector2f()), 0, new Vector2f()));
                int velocity = cart.addProperty(new VelocityProperty(0.0f, 0.0f, 1.0f, new Vector2f()));
                int vehicle = cart.addProperty(new VehicleProperty(0, 0, 0, ColorRGBA.Black));
                m_ticker.getSimulationBuffer().addDynamicGameObject(cart, "Dynamics/HoverCart");

                DynamicGameObject missile = new DynamicGameObject(DynamicGameObjectType.HOMING_MISSILE);
                Transform missileTransform = new Transform();
                missileTransform.setTranslation(3.0f, 3.0f, 3.0f);
                missileTransform.setRotation(new Quaternion().fromAngleAxis((float) Math.PI / 2.0f, Vector3f.UNIT_X));
                missile.addProperty(new TransformProperty(missileTransform, new Position(0, new Vector2f()), 0, new Vector2f()));
                m_ticker.getSimulationBuffer().addDynamicGameObject(missile, "Dynamics/Trap");

                DynamicGameObject itembox = new DynamicGameObject(DynamicGameObjectType.ITEM_BOX);
                Transform itemboxTransform = new Transform();
                itemboxTransform.setTranslation(-3.0f, 3.0f, 3.0f);
                itembox.addProperty(new TransformProperty(itemboxTransform, new Position(0, new Vector2f()), 0, new Vector2f()));
                m_ticker.getSimulationBuffer().addDynamicGameObject(itembox, "Dynamics/Explosion");

                float accu = 0;

                LinkedList<Runnable> tmpTasks = new LinkedList<>();
                long lastMillis = System.currentTimeMillis();

                while (m_running && meta.isRunning()) {
                    float tps = (float) (System.currentTimeMillis() - lastMillis) / 1000f;
                    lastMillis = System.currentTimeMillis();

                    //update scene
                    flyCam.update(tps);
                    float currentVelocity = ((VelocityProperty) cart.getProperty(velocity)).getMomentum();
                    currentVelocity += tps / 4.0f;
                    currentVelocity %= 1;
                    ((VelocityProperty) cart.getProperty(velocity)).setMomentum(currentVelocity);

                    accu += tps;
                    accu %= 9;
//                    ((VehicleProperty) cart.getProperty(vehicle)).setEnergyLevel((int) accu);

                    //run tasks
                    m_tasksLock.lock();
                    LinkedList<Runnable> tmp = tmpTasks;
                    tmpTasks = m_tasks;
                    m_tasks = tmp;
                    m_tasksLock.unlock();
                    for (Runnable task : tmpTasks) {
                        task.run();
                    }
                    tmpTasks.clear();

                    //swap Tick
                    m_ticker.swapSimulationTick();

                    //wait at least a millisecond
                    while (((System.currentTimeMillis() - lastMillis < 1) || !isEnabled()) && m_running && meta.isRunning()) {
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            //e.printStackTrace();
                        }
                    }

                }
                flyCam.cleanup();
            });

            m_running = true;
            thread.start();
        }
    }

    @Override
    public void setEnabled(boolean flag) {
        super.setEnabled(flag);
    }

    /**
     * Enqueues the given {@link Runnable} for execution in the simulation thread.
     * @param task the task to be executed.
     */
    public void enqueue(Runnable task) {
        m_tasksLock.lock();
        m_tasks.add(task);
        m_tasksLock.unlock();
    }

    @Override
    public void stateAttached(AppStateManager stateManager) {
        m_stateManager = stateManager;
        m_ticker = m_stateManager.getState(Ticker.class);

        startSimulation();
    }

    @Override
    public void stateDetached(AppStateManager stateManager) {
        m_running = false;
    }
}
