package edu.kit.valaris.tick.properties;

/**
 * Represents one of the properties that contains an m_animation to perform and the m_weight of the m_animation.
 */
public class AnimatorProperty implements IProperty {

    /**
     * The m_weight of the m_animation.
     */
    private float m_weight;

    /**
     * The name of the m_animation to perform.
     */
    private String m_animation;

    /**
     * Creates a new animator property.
     *
     * @param animation The name of the m_animation to perform.
     * @param weight The m_weight of the m_animation.
     */
    public AnimatorProperty(String animation, float weight) {
        this.m_animation = animation;
        this.m_weight = weight;
    }

    /**
     * Accesses the m_weight.
     * @return The m_weight of the m_animation.
     */
    public float getWeight() {
        return m_weight;
    }

    /**
     * Sets the specified m_weight of the m_animation.
     *
     * @param weight The m_weight to set.
     */
    public void setWeight(float weight) {
        this.m_weight = weight;
    }

    /**
     * Accesses the name.
     * @return The name of the m_animation to perform.
     */
    public String getAnimation() {
        return m_animation;
    }

    /**
     * Sets the specified m_animation.
     *
     * @param animation The name of the m_animation to perform.
     */
    public void setAnimation(String animation) {
        this.m_animation = animation;
    }

    /**
     * Creates a deep copy of the property, copying all attributes.
     *
     * @return A deep copy of the property.
     */
    @Override
    public AnimatorProperty deepCopy() {
        return new AnimatorProperty(m_animation, m_weight);
    }

    /**
     * Checks, if the property can sync with the specified target.
     *
     * @param other The target property to sync with.
     * @return true, if this property can be synced with the specified target property.
     */
    @Override
    public boolean canSync(IProperty other) {
        return (other instanceof AnimatorProperty);
    }

    /**
     * Makes this property a deep copy of the specified target.
     *
     * @param other The target to sync with.
     */
    @Override
    public void sync(IProperty other) {
        if (canSync(other)) {
            AnimatorProperty otherProperty = (AnimatorProperty) other;
            this.m_animation = otherProperty.m_animation;
            this.m_weight = otherProperty.m_weight;
        } else {
            throw new IllegalStateException("Cannot sync with the given property.");
        }
    }
}
