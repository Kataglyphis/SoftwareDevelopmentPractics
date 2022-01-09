package edu.kit.valaris.generation.roadgeneration;

import com.jme3.math.Vector3f;

import java.util.HashSet;
import java.util.Set;


/**
 * Represents an object which can be placed on to of the road (Eg. Items).
 * @author Sidney Hansen
 */
public class RoadObject {


    private Set<RoadObjectProperty> m_roadObjectProperties;

    private Vector3f m_position;

    private int m_index;


    /**
     * Creates new {@link RoadObject}, at given Relative position.
     *
     * @param position - Relative Position
     */
    public RoadObject(Vector3f position, int index) {
        m_position = new Vector3f(position);
        m_index = index;
        m_roadObjectProperties = new HashSet<>();
    }

    /**
     * Returns position.
     *
     * @return position
     */
    public Vector3f getPosition() {
        return m_position;
    }

    public int getIndex() {
        return m_index;
    }

    public void setIndex(int index) {
        this.m_index = index;
    }

    /**
     * Sets position.
     *
     * @param position - Relative Position
     */
    public void setPosition(Vector3f position) {
        this.m_position = position;

    }

    /**
     * Adds {@link RoadObjectProperty} to {@link RoadObject}.
     *
     * @param roadObjectProperty to be added.
     */
    public void addProperty(RoadObjectProperty roadObjectProperty) {
        m_roadObjectProperties.add(roadObjectProperty);
    }

    /**
     * Returns if {@link RoadObject} has the give {@link RoadObjectProperty}.
     *
     * @param roadObjectProperty - to be checked for
     * @return true if this {@link RoadObject} has the give {@link RoadObjectProperty}, false otherwise.
     */
    public boolean hasProperty(RoadObjectProperty roadObjectProperty) {
        return m_roadObjectProperties.contains(roadObjectProperty);
    }


}
