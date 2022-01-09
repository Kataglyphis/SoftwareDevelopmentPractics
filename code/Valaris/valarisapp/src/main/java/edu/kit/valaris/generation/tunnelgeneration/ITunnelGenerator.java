package edu.kit.valaris.generation.tunnelgeneration;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import edu.kit.valaris.generation.GeneratorSettings;

import java.util.Set;

/**
 * Interface defining a TunnelGenerator, which is the factory to generate a {@link ITunnel}.
 * @author Sidney Hansen
 */
public interface ITunnelGenerator {


    /**
     * Generates a tunnel. The end of the tunnel is defined by the relative position "target".
     * The tunnel starts at position (0,0,0) in direction (0,0,1). It can be attached to any Road using
     * "road.attach(tunnel.getRoad())".
     *
     * @param seed the seed to be used for the generation.
     * @param targetPosition the relative position (to the beginning of the tunnel) of the end of the tunnel.
     * @param horizontalAngle the horizontal angle between the entry and the exitPoint.
     * @param verticalAngle the vertical angle between the entry and the exitPoint.
     * @param entryWidth the width of the tunnel at the entry.
     * @param flags optional parameters to influence the generation process.
     * @return a tunnel.
     */
    ITunnel generate(int seed, Vector3f targetPosition, float horizontalAngle, float verticalAngle,
                            Vector2f entryWidth, Set<GeneratorSettings> flags);

}
