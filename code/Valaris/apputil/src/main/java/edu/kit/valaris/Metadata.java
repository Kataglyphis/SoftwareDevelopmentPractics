package edu.kit.valaris;

import com.jme3.app.Application;
import com.jme3.app.LegacyApplication;
import com.jme3.app.state.AbstractAppState;
import org.lwjgl.opengl.Display;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * {@link com.jme3.app.state.AppState} that holds all metadata of the application.
 *
 * @author Frederik Lingg
 */
public class Metadata extends AbstractAppState {

    /**
     * if the app is currently running.
     */
    private boolean m_running;

    /**
     * Whether the app currently has focus.
     */
    private boolean m_isFocus;

    /**
     * The screen width.
     */
    private int m_width;

    /**
     * the screen height.
     */
    private int m_height;

    /**
     * The app currently running.
     */
    private LegacyApplication m_app;

    /**
     * Lock for synchronizing Access to dimensions.
     */
    private Lock m_dimensionsLock;

    /**
     * Lock for synchronizing Access to status variables.
     */
    private Lock m_statusLock;

    /**
     * Creates new {@link Metadata} for a running app.
     * @param width the screen width.
     * @param  height the screen height.
     */
    public Metadata(LegacyApplication app, int width, int height) {
        m_app = app;

        m_width = width;
        m_height = height;

        //when this is created, app is probably running and in focus
        m_running = true;
        m_isFocus = true;

        m_dimensionsLock = new ReentrantLock();
        m_statusLock = new ReentrantLock();
    }

    /**
     * Sets the Screen width of the application.
     * @param width the width.
     */
    public void setWidth(int width) {
        m_dimensionsLock.lock();
        m_width = width;
        m_dimensionsLock.unlock();
    }

    /**
     * Accesses the Screen width.
     * @return the screen width.
     */
    public int getWidth() {
        m_dimensionsLock.lock();
        try {
            return m_width;
        } finally {
            m_dimensionsLock.unlock();
        }
    }

    /**
     * Sets the Screen height of the application.
     * @param height the height.
     */
    public void setHeight(int height) {
        m_dimensionsLock.lock();
        m_height = height;
        m_dimensionsLock.unlock();
    }

    /**
     * Accesses the Screen height.
     * @return the height.
     */
    public int getHeight() {
        m_dimensionsLock.lock();
        try {
            return m_height;
        } finally {
            m_dimensionsLock.unlock();
        }
    }

    /**
     * Sets whether the app is running.
     * @param isRunning whether the app is running.
     */
    public void setRunning(boolean isRunning) {
        m_statusLock.lock();
        m_running = isRunning;
        m_statusLock.unlock();
    }

    /**
     * Accesses a status of the application.
     * @return whether the app is running.
     */
    public boolean isRunning() {
        m_statusLock.lock();
        try {
            return m_running;
        } finally {
            m_statusLock.unlock();
        }
    }

    /**
     * Sets whether the app is in focus.
     * @param isFocus whether the app is in focus.
     */
    public void setFocus(boolean isFocus) {
        m_statusLock.lock();
        m_isFocus = isFocus;
        m_statusLock.unlock();
    }

    /**
     * Accesses a status of the application.
     * @return whether the app is in focus.
     */
    public boolean isFocus() {
        m_statusLock.lock();
        try {
            return m_isFocus;
        } finally {
            m_statusLock.unlock();
        }
    }

    /**
     * Accesses the {@link BaseApplication} currently running.
     * @return the {@link BaseApplication} currently running.
     */
    public BaseApplication getApp() {
        return (BaseApplication) m_app;
    }

    @Override
    public void update(float tpf) {
        //listen to size changes and notify app
        if(m_width != Display.getWidth() || m_height != Display.getHeight()) {
            m_app.reshape(Display.getWidth(), Display.getHeight());
        }
    }
}
