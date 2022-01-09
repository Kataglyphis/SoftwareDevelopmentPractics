package edu.kit.valaris.generation.domegeneration;

import edu.kit.valaris.generation.GridVertex;


/**
 * Defines and implements the basic requirements for a noise function that generates a landscape adapted to the route.
 *
 * @author Lukas Schölch
 */
public abstract class AbstractNoiseGenerator {

    /**
     * Generates a nois function to create a landscape
     * @param grid The grid of an IDome Object
     * @param biom Indicates where in the JSON document the information about the current biome can be found.
     */
    protected abstract void makeNoise(GridVertex[][] grid, String biom);

}
