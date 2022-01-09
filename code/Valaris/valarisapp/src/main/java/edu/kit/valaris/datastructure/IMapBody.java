package edu.kit.valaris.datastructure;

import java.util.LinkedList;

import com.jme3.math.Transform;

/**
 * @author Tobias Knorr
 * @version 1.0
 *
 * data-structure represents a race-track. It contains although all Elements on the Road.
 */
public interface IMapBody {

    /**
     * returns the physical DividingRoadModel-data-structure.
     *
     *@return the data-structure
     */
    RoadModel getRoad();

    /**
     * returns the Objects to spawn on the road.
     *
     * @return the objects to spawn
     */
    LinkedList<RoadInitializationObject> getObjects();

    /**
     * calculates the Transform of the Position in (x, y, z).
     *
     * @param position on the DividingRoadModel in form of the Position-Object
     * @param angle of the DynamicGameObject relativ to the current Segment
     * @return the 3d-coordinates
     */
    Transform calc3DTransform(Position position, float angle);
}

