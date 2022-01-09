package edu.kit.valaris.dummysim;

import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.math.Quaternion;
import com.jme3.math.Transform;
import com.jme3.math.Vector3f;
import edu.kit.valaris.tick.properties.IProperty;
import edu.kit.valaris.tick.properties.TransformProperty;

/**
 * A system controlling a {@link TransformProperty}
 * @author Frederik Lingg
 */
public class FlyCam {

    private TransformProperty m_target;

    private InputManager m_inputManager;

    private Vector3f m_currentSpeed;

    private Vector3f m_tmp;

    private Quaternion m_rotation;

    private float m_speed;

    private float m_sensibility;

    private float m_angleX = 0;

    private float m_angleY = 0;

    private ActionListener m_keyListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean isPressed, float tpf) {
            if (name.equals("MoveForward")) {
                if (isPressed) {
                    m_currentSpeed.addLocal(Vector3f.UNIT_Z);
                } else {
                    m_currentSpeed.subtractLocal(Vector3f.UNIT_Z);
                }
            } else if (name.equals("MoveBackward")) {
                if (isPressed) {
                    m_currentSpeed.subtractLocal(Vector3f.UNIT_Z);
                } else {
                    m_currentSpeed.addLocal(Vector3f.UNIT_Z);
                }
            } else if (name.equals("MoveLeft")) {
                if (isPressed) {
                    m_currentSpeed.addLocal(Vector3f.UNIT_X);
                } else {
                    m_currentSpeed.subtractLocal(Vector3f.UNIT_X);
                }
            } else if (name.equals("MoveRight")) {
                if (isPressed) {
                    m_currentSpeed.subtractLocal(Vector3f.UNIT_X);
                } else {
                    m_currentSpeed.addLocal(Vector3f.UNIT_X);
                }
            }
        }
    };

    private AnalogListener m_mouseListener = new AnalogListener() {
        @Override
        public void onAnalog(String name, float value, float tpf) {
            if (name.equals("RotateX")) {
                m_angleX -= value * m_sensibility;
            } else if (name.equals("RotateY")) {
                m_angleY -= value * m_sensibility;
            } else if (name.equals("RotateX_negative")) {
                m_angleX += value * m_sensibility;
            } else if (name.equals("RotateY_negative")) {
                m_angleY += value * m_sensibility;
            }
        }
    };

    /**
     * Creates a new FlyCam controlling the given {@link TransformProperty}.
     * @param target the target {@link IProperty}.
     * @param inputManager the input manager to use for input.
     */
    public FlyCam(TransformProperty target, InputManager inputManager) {
        m_target = target;
        m_inputManager = inputManager;
        m_currentSpeed = new Vector3f();
        m_tmp = new Vector3f();
        m_rotation = new Quaternion();
        m_speed = 20;
        m_sensibility = 1;

        //movement
        m_inputManager.addMapping("MoveForward", new KeyTrigger(KeyInput.KEY_W));
        m_inputManager.addMapping("MoveBackward", new KeyTrigger(KeyInput.KEY_S));
        m_inputManager.addMapping("MoveLeft", new KeyTrigger(KeyInput.KEY_A));
        m_inputManager.addMapping("MoveRight", new KeyTrigger(KeyInput.KEY_D));
        m_inputManager.addListener(m_keyListener, "MoveForward", "MoveBackward", "MoveLeft", "MoveRight");

        //rotation
        m_inputManager.addMapping("RotateX", new MouseAxisTrigger(MouseInput.AXIS_X, false));
        m_inputManager.addMapping("RotateX_negative", new MouseAxisTrigger(MouseInput.AXIS_X, true));
        m_inputManager.addMapping("RotateY", new MouseAxisTrigger(MouseInput.AXIS_Y, false));
        m_inputManager.addMapping("RotateY_negative", new MouseAxisTrigger(MouseInput.AXIS_Y, true));
        m_inputManager.addListener(m_mouseListener, "RotateX", "RotateY", "RotateX_negative", "RotateY_negative");

        m_inputManager.setCursorVisible(false);
    }

    /**
     * Updates the flycam.
     * @param tpf the time per frame.
     */
    public void update(float tpf) {
        //do translation
        Transform transform = m_target.getTransform();
        Vector3f delta = transform.getRotation().mult(m_currentSpeed, m_tmp).multLocal(tpf * m_speed);
        transform.setTranslation(delta.addLocal(transform.getTranslation()));
        transform.setRotation(m_rotation.fromAngles(m_angleY, m_angleX, 0));
    }

    /**
     * Deletes all keymappings that where created and makes the cursor visible.
     */
    public void cleanup() {
        m_inputManager.removeListener(m_keyListener);
        m_inputManager.deleteMapping("MoveForward");
        m_inputManager.deleteMapping("MoveBackward");
        m_inputManager.deleteMapping("MoveLeft");
        m_inputManager.deleteMapping("MoveRight");

        m_inputManager.removeListener(m_mouseListener);
        m_inputManager.deleteMapping("RotateX");
        m_inputManager.deleteMapping("RotateY");
        m_inputManager.deleteMapping("RotateX_negative");
        m_inputManager.deleteMapping("RotateY_negative");

        m_inputManager.setCursorVisible(true);
    }
}
