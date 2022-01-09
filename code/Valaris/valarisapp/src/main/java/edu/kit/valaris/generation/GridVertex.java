package edu.kit.valaris.generation;

import com.jme3.math.Vector3f;

import java.util.HashSet;

/**
 * Data structure for a point in a 3-dimensional grid.
 *
 * @author Lukas Schölch
 */
public class GridVertex {
    private Vector3f m_position;

    private final int[] m_index = new int[3];

    private HashSet<GridVertexProperty> m_properties;

    /**
     * Constructor.
     * @param position The position of the GridVertex.
     * @param indexX The x - index of the GridVertex.
     * @param indexY The y - index of the GridVertex.
     * @param indexZ The z - index of the GridVertex.
     */
    public GridVertex(Vector3f position, int indexX, int indexY, int indexZ) {
        this.m_position = position;
        this.m_index[0] = indexX;
        this.m_index[1] = indexY;
        this.m_index[2] = indexZ;

        m_properties = new HashSet<>();
    }


    /**
     * Implements the equals method.
     * @param obj The object to compare.
     * @return True, if obj equals this GridVertex. Otherwise returns false.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj.getClass() == this.getClass()) {
            GridVertex gridVertex = (GridVertex) obj;
            for (int i = 0; i < m_index.length; i++) {
                if (this.m_index[i] != gridVertex.getIndex()[i]) {
                    return false;
                }
            }
            return true;
        }

        return false;
    }

    /**
     *
     * @return The Index of this GridVertex.
     */
    public int[] getIndex() {
        return this.m_index;
    }

    /**
     * Sets Position.
     * @param position New position.
     */
    public void setPosition(Vector3f position) {
        this.m_position = new Vector3f(position);
    }

    public Vector3f getPosition() {
        return this.m_position;
    }

    /**
     * Checks whether the GridVertex contains the given GridVertexProperty.
     * @param property The property to check.
     * @return True, if the GridVertex contains the property. Otherwise returns false.
     */
    public boolean hasProperty(GridVertexProperty property) {
        return this.m_properties.contains(property);
    }

    /**
     * Adds a property to the GridVertex.
     * @param property The new property.
     */
    public void setProperty(GridVertexProperty property) {
        this.m_properties.add(property);
    }

}
