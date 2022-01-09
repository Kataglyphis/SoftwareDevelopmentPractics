package edu.kit.valaris.rendering.particleEffect;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.Vector4f;
import com.jme3.scene.Spatial;

/**
 * Interface for the actual particle strategy. For different kind of particles we also
 * need different classes which all have to implement those interface
 * @author Jonas Heinle
 * @version 1.2
 * @see FireParticleStrategy
 * @see JetParticleStrategy
 * @see GravityTrap
 * @see ExplosionParticleStrategy
 */
public interface ParticleStrategy {

    /**
     * is emiting the particle on the given strategy
     * @param particle particle we want to emit
     * @param tpf time per frame for independent computation
     */
     void emitParticle(Particle particle, float tpf);

     /**
     * updates the particle information depending on the tpf
     * @param p represents Particle that has to be updated
     * @param tpf time per frame for updating particle in dependency of frame rate
     */
     void updateParticle(Particle p, float tpf);

     /**
     * gives us the number of particles
     * @return the number of particles to be alive concurrently on the screen
     */
     int getNumParticles();

     /**
     * method to initialize all necessary information of our particles
      * @param  particles particles we want to use for this effect
     */
     void initializeParticleData(Particle[] particles);

    /**
     * is returning the material for the effect of this particle
     * @param assetManager assetManager for loading our material
     * @return material for this particle strategy
     */
      Material getMaterial(AssetManager assetManager);

    /**
     * returns an array of the used colors
     * @return used colors
     */
    Vector4f[] getColors();

    /**
     * whether particle positions are given in global or local-space.
     */
    boolean isGlobal();
}
