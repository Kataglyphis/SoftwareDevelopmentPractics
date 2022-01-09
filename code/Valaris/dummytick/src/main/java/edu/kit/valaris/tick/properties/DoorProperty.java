package edu.kit.valaris.tick.properties;

/**
 * Represents the property of the checkpoint doors that describe, if the door is opened or closed.
 */
public class DoorProperty implements IProperty {

    /**
     * The status of the door.
     */
    private boolean m_open;

    /**
     * The m_index of the door.
     */
    private int m_index;

    /**
     * Creates a new door property with the given status and RoadSegment m_index.
     *
     * @param open true, if the door is m_open.
     * @param index the m_index of the road segment.
     */
    public DoorProperty(boolean open, int index) {
        this.m_open = open;
        this.m_index = index;
    }

    /**
     * Checks whether the door is open or not.
     * @return true, if the door is m_open and false, if not.
     */
    public boolean isOpen() {
        return false;
    }

    /**
     * Sets the status of the door.
     *
     * @param open The new status of the door.
     */
    public void setOpen(boolean open) {
        this.m_open = open;
    }

    /**
     * Accesses the index.
     * @return The m_index of the door.
     */
    public int getIndex() {
        return m_index;
    }

    /**
     * Sets the m_index of the property.
     *
     * @param index The RoadSegment m_index.
     */
    public void setIndex(int index) {
        this.m_index = index;
    }

    /**
     * Creates a deep copy of the property, copying all attributes.
     *
     * @return A deep copy of the property.
     */
    @Override
    public DoorProperty deepCopy() {
        return new DoorProperty(m_open, m_index);
    }

    /**
     * Checks, if the property can sync with the specified other.
     *
     * @param other The other property to sync with.
     * @return true, if this property can be synced with the specified other property.
     */
    @Override
    public boolean canSync(IProperty other) {
        return (other instanceof DoorProperty);
    }

    /**
     * Makes this property a deep copy of the specified other.
     *
     * @param other The other instance to sync with.
     */
    @Override
    public void sync(IProperty other) {
        if (canSync(other)) {
            DoorProperty otherProperty = (DoorProperty) other;
            this.m_open = otherProperty.m_open;
            this.m_index = otherProperty.m_index;
        } else {
            throw new IllegalStateException("Cannot sync with the given property.");
        }
    }
}
