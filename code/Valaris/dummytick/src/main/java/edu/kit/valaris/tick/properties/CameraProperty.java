package edu.kit.valaris.tick.properties;

import com.jme3.math.Vector3f;

/**
 * Represents the property that contains the m_position, m_direction axis and the m_left axis of the camera.
 */
public class CameraProperty implements IProperty {

    /**
     * The m_position of the camera coordinate system.
     **/
    private Vector3f m_position;

    /**
     * The m_direction axis of the camera coordinate system.
     **/
    private Vector3f m_direction;

    /**
     * The m_left axis of the camera coordinate system.
     **/
    private Vector3f m_left;

    /**
     * Creates a new camera property.
     *
     * @param position The m_position of the camera coordinate system.
     * @param direction The m_direction axis of the camera coordinate system.
     * @param left The m_left axis of the camera coordinate system.
     **/
    public CameraProperty(Vector3f position, Vector3f direction, Vector3f left) {
        this.m_position = position;
        this.m_direction = direction;
        this.m_left = left;
    }

    /**
     * Accesses the position.
     * @return The m_position of the camera.
     **/
    public Vector3f getPosition() {
        return this.m_position;
    }

    /**
     * Sets the specified m_position.
     *
     * @param position The m_position to set.
     **/
    public void setPosition(Vector3f position) {
        this.m_position = position;
    }

    /**
     * Accesses the direction.
     * @return The m_direction axis of the camera.
     **/
    public Vector3f getDirection() {
        return this.m_direction;
    }

    /**
     * Sets the specified m_direction axis.
     *
     * @param direction The m_direction axis to set.
     **/
    public void setDirection(Vector3f direction) {
        this.m_direction = direction;
    }

    /**
     * Accesses te left axis.
     * @return The m_left axis of the camera.
     **/
    public Vector3f getLeft() {
        return this.m_left;
    }

    /**
     * Sets the specified m_left axis.
     *
     * @param left The m_left axis to set.
     **/
    public void setLeft(Vector3f left) {
        this.m_left = left;
    }

    /**
     * Creates a deep copy of the property, copying all attributes.
     *
     * @return A deep copy of the property.
     */
    @Override
    public CameraProperty deepCopy() {
        return new CameraProperty(m_position.clone(), m_direction.clone(), m_left.clone());
    }

    /**
     * Checks, if the property can sync with the specified target.
     *
     * @param other The target property to sync with.
     * @return true, if this property can be synced with the specified target property.
     */
    @Override
    public boolean canSync(IProperty other) {
        return (other instanceof CameraProperty);
    }

    /**
     * Makes this property a deep copy of the specified target.
     *
     * @param other The target to sync with.
     */
    @Override
    public void sync(IProperty other) {
        if (canSync(other)) {
            CameraProperty otherProperty = (CameraProperty) other;
            this.m_position = otherProperty.m_position;
            this.m_direction = otherProperty.m_direction;
            this.m_left = otherProperty.m_left;
        } else {
            throw new IllegalStateException("Cannot sync with the given property.");
        }
    }
}
