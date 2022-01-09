package edu.kit.valaris.tick.properties;

import com.jme3.math.Transform;

/**
 * Represents the property that contains the position, rotation, scale and bounding box.
 */
public class TransformProperty implements IProperty {

    /**
     * The m_transform of the game object, containing the position, rotation and scale.
     */
    private Transform m_transform;

    /**
     * Creates a new m_transform property.
     *
     * @param transform The m_transform of the game object in the 3d coordinate system.
     */
    public TransformProperty(Transform transform) {
        this.m_transform = transform;
    }

    /**
     * Accesses the m_transform.
     * @return The m_transform of the game object, containing the position, rotation and scale.
     */
    public Transform getTransform() {
        return m_transform;
    }

    /**
     * Sets the m_transform of the game object.
     *
     * @param transform The m_transform.
     */
    public void setTransform(Transform transform) {
        this.m_transform = transform;
    }

    /**
     * Creates a deep copy of the property, copying all attributes.
     *
     * @return A deep copy of the property.
     */
    @Override
    public TransformProperty deepCopy() {
        return new TransformProperty(m_transform.clone());
    }

    /**
     * Checks, if the property can sync with the specified target.
     *
     * @param other The target property to sync with.
     * @return true, if this property can be synced with the specified target property.
     */
    @Override
    public boolean canSync(IProperty other) {
        return (other instanceof TransformProperty);
    }

    /**
     * Makes this property a deep copy of the specified target.
     *
     * @param other The target to sync with.
     */
    @Override
    public void sync(IProperty other) {
        if (canSync(other)) {
            TransformProperty otherProperty = (TransformProperty) other;
            this.m_transform = otherProperty.m_transform.clone();
        } else {
            throw new IllegalStateException("Cannot sync with the given property.");
        }
    }
}
