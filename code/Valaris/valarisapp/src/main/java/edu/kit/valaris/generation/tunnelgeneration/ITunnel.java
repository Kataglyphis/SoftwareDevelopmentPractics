package edu.kit.valaris.generation.tunnelgeneration;

import edu.kit.valaris.generation.ISceneItem;
import edu.kit.valaris.generation.roadgeneration.Road;

/**
 * Interface defining a Tunnel, which is a {@link ISceneItem} connecting two points with a road.
 * @author Sidne Hansen
 */
public interface ITunnel extends ISceneItem {

    /**
     * Returns the Road of this tunnel.
     *
     * @return the Road.
     */
    Road getRoad();


}
