package edu.kit.valaris.tick.properties;

/**
 * Represents the vehicle property containing the energy level of the vehicle.
 */
public class VehicleProperty implements IProperty {

    /**
     * The energy level of the vehicle.
     */
    private int m_energyLevel;

    /**
     * The time in seconds of the last time, this vehicle tried to steal energy.
     * To get the current time in seconds, use {@code com.jme3.system.Timer.getTimeInSeconds()}.
     */
    private float m_energyTheftTimestamp;

    /**
     * The time in seconds of the last time, this vehicle lost energy, because another vehicle stole from him.
     * To get the current time in seconds, use {@code com.jme3.system.Timer.getTimeInSeconds()}.
     */
    private float m_energyStolenTimestamp;

    /**
     * Creates a new vehicle property.
     * @param energyLevel The energy level of the vehicle.
     * @param energyTheftTimestamp The time in seconds of the last time, this vehicle tried to steal energy.
     * @param energyStolenTimestamp The time in seconds of the last time, this vehicle lost energy,
     *                              because another vehicle stole from him.
     */
    public VehicleProperty(int energyLevel, float energyTheftTimestamp, float energyStolenTimestamp) {
        this.m_energyLevel = energyLevel;
        this.m_energyTheftTimestamp = energyTheftTimestamp;
        this.m_energyStolenTimestamp = energyStolenTimestamp;
    }

    /**
     * Accesses the EnergyLevel.
     * @return The energy level of the vehicle.
     */
    public int getEnergyLevel() {
        return m_energyLevel;
    }

    /**
     * Sets the specified energy level.
     *
     * @param energyLevel The energy level to set
     */
    public void setEnergyLevel(int energyLevel) {
        this.m_energyLevel = energyLevel;
    }

    /**
     * Accesses a timestamp.
     * @return The time in seconds of the last time, this vehicle tried to steal energy.
     */
    public float getEnergyTheftTimestamp() {
        return m_energyTheftTimestamp;
    }

    /**
     * Sets the time in seconds of the last time, this vehicle tried to steal energy.
     *
     * @param energyTheftTimestamp The time in seconds after game start.
     *                             To get the current time in seconds,
     *                             use {@code com.jme3.system.Timer.getTimeInSeconds()}.
     */
    public void setEnergyTheftTimestamp(float energyTheftTimestamp) {
        this.m_energyTheftTimestamp = energyTheftTimestamp;
    }

    /**
     * Accesses a timestamp.
     * @return The time in seconds of the last time, this vehicle lost energy, because another vehicle stole from him.
     */
    public float getEnergyStolenTimestamp() {
        return m_energyStolenTimestamp;
    }

    /**
     * Sets the time in seconds of the last time, this vehicle lost energy, because another vehicle stole from him.
     *
     * @param energyStolenTimestamp The time in seconds after game start.
     *                              To get the current time in seconds,
     *                              use {@code com.jme3.system.Timer.getTimeInSeconds()}.
     */
    public void setEnergyStolenTimestamp(float energyStolenTimestamp) {
        this.m_energyStolenTimestamp = energyStolenTimestamp;
    }

    /**
     * Creates a deep copy of the property, copying all attributes.
     *
     * @return A deep copy of the property.
     */
    @Override
    public VehicleProperty deepCopy() {
        return new VehicleProperty(m_energyLevel, m_energyTheftTimestamp, m_energyStolenTimestamp);
    }

    /**
     * Checks, if the property can sync with the specified target.
     *
     * @param other The target property to sync with.
     * @return true, if this property can be synced with the specified target property.
     */
    @Override
    public boolean canSync(IProperty other) {
        return (other instanceof VehicleProperty);
    }

    /**
     * Makes this property a deep copy of the specified target.
     *
     * @param other The target to sync with.
     */
    @Override
    public void sync(IProperty other) {
        if (canSync(other)) {
            VehicleProperty otherProperty = (VehicleProperty) other;
            this.m_energyLevel = otherProperty.m_energyLevel;
        } else {
            throw new IllegalStateException("Cannot sync with the given property.");
        }
    }
}
