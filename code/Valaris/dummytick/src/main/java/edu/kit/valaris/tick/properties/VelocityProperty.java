package edu.kit.valaris.tick.properties;

import com.jme3.math.Vector2f;

/**
 * Represents the property that contains the velocity of the game object.
 */
public class VelocityProperty implements IProperty {

    /**
     * The m_momentum of the game object.
     */
    private float m_momentum;

    /**
     * The angular m_momentum of the game object.
     */
    private float m_angularMomentum;

    /**
     * The m_mass of the game object.
     */
    private float m_mass;

    /**
     * The direction of the m_momentum.
     */
    private Vector2f m_momentumDirection;

    /**
     * Creates a new velocity property.
     *
     * @param momentum The m_momentum of the game object.
     * @param angularMomentum The angular m_momentum of the game object.
     * @param mass The m_mass of the game object.
     * @param momentumDirection The direction of the m_momentum.
     */
    public VelocityProperty(float momentum, float angularMomentum, float mass, Vector2f momentumDirection) {
        this.m_momentum = momentum;
        this.m_angularMomentum = angularMomentum;
        this.m_mass = mass;
        this.m_momentumDirection = momentumDirection;
    }

    /**
     * Accesses the m_momentum.
     * @return The m_momentum of the game object.
     */
    public float getMomentum() {
        return m_momentum;
    }

    /**
     * Sets the m_momentum of the game object.
     *
     * @param momentum The m_momentum.
     */
    public void setMomentum(float momentum) {
        this.m_momentum = momentum;
    }

    /**
     * Accesses the the direction of m_momentum.
     * @return The direction of the m_momentum.
     */
    public Vector2f getMomentumDirection() {
        return m_momentumDirection;
    }

    /**
     * Sets the direction of the m_momentum.
     *
     * @param momentumDirection The direction of the m_momentum.
     */
    public void setMomentumDirection(Vector2f momentumDirection) {
        this.m_momentumDirection = momentumDirection;
    }

    /**
     * Accesses the angular m_momentum.
     * @return The angular m_momentum of the game object.
     */
    public float getAngularMomentum() {
        return m_angularMomentum;
    }

    /**
     * Sets the angular m_momentum of the game object.
     *
     * @param angularMomentum The angular m_momentum
     */
    public void setAngularMomentum(float angularMomentum) {
        this.m_angularMomentum = angularMomentum;
    }

    /**
     * Accesses the m_mass.
     * @return The m_mass of the game object.
     */
    public float getMass() {
        return m_mass;
    }

    /**
     * Sets the m_mass of the game object.
     *
     * @param mass The m_mass of the game object.
     */
    public void setMass(float mass) {
        this.m_mass = mass;
    }

    /**
     * Creates a deep copy of the property, copying all attributes.
     *
     * @return A deep copy of the property.
     */
    @Override
    public VelocityProperty deepCopy() {
        return new VelocityProperty(m_momentum, m_angularMomentum, m_mass, m_momentumDirection.clone());
    }

    /**
     * Checks, if the property can sync with the specified target.
     *
     * @param other The target property to sync with.
     * @return true, if this property can be synced with the specified target property.
     */
    @Override
    public boolean canSync(IProperty other) {
        return (other instanceof VelocityProperty);
    }

    /**
     * Makes this property a deep copy of the specified target.
     *
     * @param other The target to sync with.
     */
    @Override
    public void sync(IProperty other) {
        if (canSync(other)) {
            VelocityProperty otherProperty = (VelocityProperty) other;
            this.m_momentum = otherProperty.m_momentum;
            this.m_momentumDirection = otherProperty.m_momentumDirection.clone();
            this.m_angularMomentum = otherProperty.m_angularMomentum;
            this.m_mass = otherProperty.m_mass;
        } else {
            throw new IllegalStateException("Cannot sync with the given property.");
        }
    }
}
