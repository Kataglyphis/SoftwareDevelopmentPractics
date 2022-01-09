package edu.kit.valaris.generation.roomgeneration;

import edu.kit.valaris.generation.ISceneItem;
import edu.kit.valaris.generation.roadgeneration.Road;

/**
 * Interface representing a Room, being the product
 * of the {@link IRoomGenerator}
 * @author Sidney Hansen
 */
public interface IRoom extends ISceneItem {

    /**
     * Return the road of this room.
     *
     * @return road of this room.
     */
    Road getRoad();

}
