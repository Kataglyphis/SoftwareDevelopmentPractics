package edu.kit.valaris.generation.roomgeneration;

import com.jme3.math.Vector2f;
import edu.kit.valaris.generation.GeneratorSettings;
import edu.kit.valaris.generation.RandomNumberGenerator;

import java.util.Set;


/**
 * Represent a room generator, which may be used by the {@link RoomSelector}
 * @author Sidney Hansen
 */
public abstract class AbstractRoomGenerator {


    /**
     *  Generates an instance of IRoom.
     *
     * @param seed seed used for {@link RandomNumberGenerator}.
     * @param boundingSphereRadius radius of sphere which is to contain the entire room.
     * @param entryWidth width of road at entry.
     * @param exitAngle angle in which approximate direction the road shoud exit the room.
     * @param flags optional flags to control parts of the generation process.
     * @return a Instance of IRoom.
     */
    public abstract IRoom generateRoom(
            int seed, float boundingSphereRadius, Vector2f entryWidth, float exitAngle, Set<GeneratorSettings> flags);

}
