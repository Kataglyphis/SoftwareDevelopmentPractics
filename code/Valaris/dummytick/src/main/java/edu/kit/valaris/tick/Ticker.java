package edu.kit.valaris.tick;

import com.jme3.app.state.AbstractAppState;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * {@link AbstractAppState} which is used to handle triple buffering between simulation- and renderthread.
 */
public class Ticker extends AbstractAppState {

    /**
     * Singleton instance.
     */
    private static Ticker s_ticker;

    /**
     * Accesses the singleton instance.
     * @return the singleton instance.
     */
    public static Ticker getInstance() {
        if (s_ticker == null) {
            s_ticker = new Ticker();
        }

        return s_ticker;
    }

    /**
     * Drops the singleton, forcing the creation of a new instance the next time {@link #getInstance()} is called.
     */
    public static void drop() {
        s_ticker = null;
    }

    /**
     * The {@link Tick} used by the simulationthread.
     */
    private Tick m_simulationBuffer;

    /**
     * The {@link Tick} used by the renderthread.
     */
    private Tick m_renderBuffer;

    /**
     * The {@link Tick} used as buffer between m_renderBuffer and m_simulationBuffer.
     */
    private Tick m_currentTick;

    /**
     * {@link Lock} used to synchronize access to m_currentTick.
     */
    private Lock m_tickLock;

    /**
     * Creates new {@link Ticker} and creates 3 {@link Tick}s as buffers, and a {@link Lock} to synchronize access.
     */
    public Ticker() {
        m_simulationBuffer = new Tick();
        m_renderBuffer = new Tick();
        m_currentTick = new Tick();

        m_tickLock = new ReentrantLock();
    }

    /**
     * Accesses the {@link Tick} used by the simulationthread.
     * @return the {@link Tick} used by the simulationthread.
     */
    public Tick getSimulationBuffer() {
        return m_simulationBuffer;
    }

    /**
     * Accesses the {@link Tick} used by the renderthread.
     * @return the {@link Tick} used by the renderthread.
     */
    public Tick getRenderBuffer() {
        return m_renderBuffer;
    }

    /**
     * Pushes the state of the the {@link Tick} accessed via {@link #getSimulationBuffer()}
     * to the buffer.
     */
    public void swapSimulationTick() {
        m_tickLock.lock();
        m_currentTick.sync(m_simulationBuffer);
        m_tickLock.unlock();
    }

    /**
     * Pushes the state of the buffer to the {@link Tick} accessed via {@link #getRenderBuffer()}.
     */
    public void swapRenderTick() {
        m_tickLock.lock();
        m_renderBuffer.sync(m_currentTick);
        m_tickLock.unlock();
    }
}
