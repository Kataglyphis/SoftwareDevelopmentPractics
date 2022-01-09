package edu.kit.valaris.generation.domegeneration;

import edu.kit.valaris.generation.ISceneItem;
import edu.kit.valaris.generation.roadgeneration.Road;

/**
 * Implements ISceneItem. Defines the basic functionality of the data structure for a dome.
 *
 * @author Lukas Schölch
 */
public interface IDome extends ISceneItem {

    /**
     * Returns the road that passes through the dome.
     * @return Road of the Dome Object
     */
    Road getRoad();

}
