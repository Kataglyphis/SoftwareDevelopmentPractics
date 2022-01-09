package edu.kit.valaris.generation.domegeneration;

import com.jme3.math.Vector2f;
import edu.kit.valaris.generation.GeneratorSettings;

import java.util.Set;

/**
 * Defines the necessary functionality to start a dome generation.
 *
 * @author Lukas Schölch
 */
public interface IDomeGenerator {

    /**
     * Starts generation for a DomeObject.
     * @param seed To generate a pseudo random value.
     * @param sphereRadius Max. Radius of the Dome.
     * @param entryWidth Dimension of the entry.
     * @param exitAngle Angle between entry and exit.
     * @param flags Preferences for the Generation.
     * @return IDome Object, that represents a Dome.
     */
    IDome generateDome(int seed, int sphereRadius, Vector2f entryWidth, float exitAngle, Set<GeneratorSettings> flags);

}
