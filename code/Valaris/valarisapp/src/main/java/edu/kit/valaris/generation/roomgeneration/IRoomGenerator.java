package edu.kit.valaris.generation.roomgeneration;

import com.jme3.math.Vector2f;
import edu.kit.valaris.generation.GeneratorSettings;

import java.util.Set;

/**
 * Interface of the entire roomgeneration.
 * @author Sidney Hansen
 */
public interface IRoomGenerator {

    /**
     * Generates a room using the given parameters.
     *
     * @param seed used to generate random numbers.
     * @param boundingSphereRadius radius of the sphere which must contain the generated room.
     * @param entryWidth width and height of the road at the entrance.
     * @param exitAngle angle in which approximate direction the road shoud exit the room.
     * @param flags optional flags to control parts of the generation process.
     * @return a Instance of IRoom.
     */
    IRoom generate(int seed, float boundingSphereRadius, Vector2f entryWidth, float exitAngle, Set<GeneratorSettings> flags);


}
