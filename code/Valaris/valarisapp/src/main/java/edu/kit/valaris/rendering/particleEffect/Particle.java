package edu.kit.valaris.rendering.particleEffect;

import com.jme3.math.Quaternion;
import com.jme3.math.Transform;
import com.jme3.math.Vector3f;

import java.util.ArrayList;
import java.util.List;

/**
 * @version 1.2
 * @author Jonas Heinle
 * representing a single particle from a particle effect
 * contains all necessary information for rendering this
 * particle to a scene
 */
public class Particle {
    /**
     * representing the maximum age
     */
    public float lifetime = 1;

    /**
     * is the current age of the particle
     */
    public float age = 0;

    /**
     * the transform of our particle;
     * combining rotation, scale, translation
     */
    public Transform transform = new Transform();
}
