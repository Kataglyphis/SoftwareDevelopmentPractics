package edu.kit.valaris.generation.domegeneration;

import edu.kit.valaris.generation.GridVertex;

/**
 * Defines the functions to color landscapes.
 *
 * @author Lukas Schölch
 */
public interface IColoring {

    /**
     * Calculates the color for the given vertices.
     * @param vertices The array of vertices that will be colored.
     * @return A colorArray for a mesh as vertexAttribute
     */
    float[] makeColor(GridVertex[] vertices);

    /**
     * Calculates the roughness for the given vertices.
     * @param vertices The array of vertices for the calculation.
     * @return An array for mesh as vertexAttribute.
     */
    float[] makeGloss(GridVertex[] vertices);

    /**
     * Claculates the specular for the given vertices.
     * @param vertices The array of vertices for the calculation.
     * @return An array for mesh as vertexAttribute.
     */
    float[] makeSpecular(GridVertex[] vertices);

    /**
     * Calculates the color for a specific tree.
     * @param seed To generate a pseudo random value.
     * @return An array that represents the color.
     */
    float[] makeTreeColor(int seed);

}
