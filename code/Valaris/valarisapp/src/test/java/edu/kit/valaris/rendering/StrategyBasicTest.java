package edu.kit.valaris.rendering;

import com.jme3.asset.AssetManager;
import com.jme3.math.Vector3f;
import edu.kit.valaris.rendering.particleEffect.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


/**
 * testing the basic funcionality of
 * Testing {@link edu.kit.valaris.rendering.particleEffect}
 * @see FireParticleStrategy
 * @see JetParticleStrategy
 * @see GravityTrap
 * @see ExplosionParticleStrategy
 * @author Jonas Heinle
 * @version 1.00
 */
public class StrategyBasicTest {

    static ParticleEffect particleEffectExplosion;
    static ParticleEffect particleEffectJet;
    static ParticleEffect particleEffectGravity;
    static ParticleEffect particleEffectShield;
    static ParticleEffect particleEffectFire;

    static AssetManager assetManager;

    static Particle[] particles;

    @BeforeClass
    public static void init() {

        particleEffectExplosion = new ParticleEffect("Explosion", assetManager);
        particleEffectJet = new ParticleEffect("Jet", assetManager);
        particleEffectGravity = new ParticleEffect("GravityTrap", assetManager);
        particleEffectShield = new ParticleEffect("Shield", assetManager);
        particleEffectFire = new ParticleEffect("Fire", assetManager);

    }

    @Test
    public void numberOfParticles() {
        assertEquals("Wrong number of Particles", 160, particleEffectExplosion.getStrategy().getNumParticles());
        assertEquals("Wrong number of Particles", 20, particleEffectJet.getStrategy().getNumParticles());
        assertEquals("Wrong number of Particles", 50, particleEffectGravity.getStrategy().getNumParticles());
        assertEquals("Wrong number of Particles", 40, particleEffectShield.getStrategy().getNumParticles());
        assertEquals("Wrong number of Particles", 100, particleEffectFire.getStrategy().getNumParticles());
    }

    @Test
    public void explosionStrategy() {
        particles = new Particle[particleEffectExplosion.getStrategy().getNumParticles()];

        for(int i = 0; i < particleEffectExplosion.getStrategy().getNumParticles(); i++) {
            particles[i] = new Particle();
        }
        particleEffectExplosion.getStrategy().initializeParticleData(particles);
        for(Particle particle : particles) {
            assertNotNull(particle.transform);
            assertEquals(1, particle.lifetime, 1e-6);
            boolean correctAge = (particle.age >= 0 && (particle.age <= 1)) ? true : false;
            assertTrue("Particle age has been false initialized!", correctAge);
        }

    }
    @Test
    public void jetStrategy() {
        particles = new Particle[particleEffectJet.getStrategy().getNumParticles()];

        for(int i = 0; i < particleEffectJet.getStrategy().getNumParticles(); i++) {
            particles[i] = new Particle();
        }
        particleEffectJet.getStrategy().initializeParticleData(particles);

        for(Particle particle : particles) {
            assertNotNull(particle.transform);
            assertEquals(0.1, particle.lifetime, 1e-6);
            boolean correctAge = (particle.age >= 0 && (particle.age <= 0.1)) ? true : false;
            assertTrue("Particle age has been false initialized!", correctAge);
            assertEquals(new Vector3f(1f, 1f, 1f), particle.transform.getScale());
        }
    }
    @Test
    public void gravityStrategy() {
        particles = new Particle[particleEffectGravity.getStrategy().getNumParticles()];

        for(int i = 0; i < particleEffectGravity.getStrategy().getNumParticles(); i++) {
            particles[i] = new Particle();
        }
        particleEffectGravity.getStrategy().initializeParticleData(particles);

        for(Particle particle : particles) {
            assertNotNull(particle.transform);
            assertEquals(3, particle.lifetime, 1e-6);
            boolean correctAge = (particle.age >= 0 && (particle.age <= 3)) ? true : false;
            assertTrue("Particle age has been false initialized!", correctAge);
            assertEquals(new Vector3f(1f, 1f, 1f), particle.transform.getScale());
        }
    }
    @Test
    public void fireStrategy() {
        particles = new Particle[particleEffectFire.getStrategy().getNumParticles()];

        for(int i = 0; i < particleEffectFire.getStrategy().getNumParticles(); i++) {
            particles[i] = new Particle();
        }
        particleEffectFire.getStrategy().initializeParticleData(particles);

        for(Particle particle : particles) {
            assertNotNull(particle.transform);
            assertEquals(2, particle.lifetime, 1e-6);
            boolean correctAge = (particle.age >= 0 && (particle.age <= 2)) ? true : false;
            assertTrue("Particle age has been false initialized!", correctAge);
            assertEquals(new Vector3f(1f, 1f, 1f), particle.transform.getScale());
        }
    }
    @Test
    public void shieldStrategy() {
        particles = new Particle[particleEffectShield.getStrategy().getNumParticles()];

        for(int i = 0; i < particleEffectShield.getStrategy().getNumParticles(); i++) {
            particles[i] = new Particle();
        }
        particleEffectShield.getStrategy().initializeParticleData(particles);

        for(Particle particle : particles) {
            assertNotNull(particle.transform);
            assertEquals(3, particle.lifetime, 1e-6);
            boolean correctAge = (particle.age >= 0 && (particle.age <= 3)) ? true : false;
            assertTrue("Particle age has been false initialized!", correctAge);
        }
    }


}
