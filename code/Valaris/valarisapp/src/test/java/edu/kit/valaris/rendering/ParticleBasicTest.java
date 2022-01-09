package edu.kit.valaris.rendering;

import com.jme3.asset.*;
import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import edu.kit.valaris.rendering.particleEffect.ParticleEffect;
import edu.kit.valaris.rendering.particleEffect.ParticleMesh;
import org.junit.Assert;
import edu.kit.valaris.rendering.particleEffect.Particle;
import org.junit.BeforeClass;
import org.junit.Test;

import java.nio.FloatBuffer;

import static org.junit.Assert.assertEquals;


/**
 * testing the basic funcionality of
 * Testing {@link edu.kit.valaris.rendering.particleEffect.ParticleMesh}
 *
 * @author Jonas Heinle
 * @version 1.00
 */
public class ParticleBasicTest {

    static ParticleEffect particleEffectExplosion;
    static ParticleEffect particleEffectJet;
    static ParticleEffect particleEffectGravity;
    static ParticleEffect particleEffectShield;
    static ParticleEffect particleEffectFire;

    static AssetManager assetManager;

    static ParticleMesh particleMesh;

    static Particle[] particles;

    static Vector3f[] m_particleModel = new Vector3f[] {
            new Vector3f(0f, 0f, 0.5f),
            new Vector3f(-0.5f, 0.0f, -0.5f).normalizeLocal(),
            new Vector3f(0.5f, 0.0f, -0.5f).normalizeLocal()
    };

    @BeforeClass
    public static void init() {

        particleEffectExplosion = new ParticleEffect("Explosion", assetManager);
        particleEffectJet = new ParticleEffect("Jet", assetManager);
        particleEffectGravity = new ParticleEffect("GravityTrap", assetManager);
        particleEffectShield = new ParticleEffect("Shield", assetManager);
        particleEffectFire = new ParticleEffect("Fire", assetManager);

        particleMesh = new ParticleMesh();
        particles = new Particle[40];
        for(int i = 0; i < particles.length; i++) {
            particles[i] = new Particle();
            particles[i].lifetime = 3;
            particles[i].age = 1;
        }
    }
    @Test
    public void initializeMeshAndBuffers() {

        Assert.assertNotNull("Particle effect explosion instancing went horribly wrong", particleEffectExplosion);
        Assert.assertNotNull("Particle effect jet instancing went horribly wrong", particleEffectJet);
        Assert.assertNotNull("Particle effect gravity instancing went horribly wrong", particleEffectGravity);
        Assert.assertNotNull("Particle effect shield instancing went horribly wrong", particleEffectShield);
        Assert.assertNotNull("Particle effect fire instancing went horribly wrong", particleEffectFire);

        //explosion strategy proven
        particleMesh.initParticleData(particleEffectExplosion.numberOfParticles());
        assertEquals("Wrong number of particles!", 160, particleEffectExplosion.numberOfParticles());
        assertEquals("Wrong mesh mode", particleMesh.getMode(), Mesh.Mode.Triangles);
        VertexBuffer pb = particleMesh.getBuffer(VertexBuffer.Type.Position);
        VertexBuffer nb = particleMesh.getBuffer(VertexBuffer.Type.Normal);
        VertexBuffer tb = particleMesh.getBuffer(VertexBuffer.Type.TexCoord6);
        assertEquals("position buffer from explosion instancing went horribly wrong", 160 * 3, pb.getNumElements());
        assertEquals("normal buffer from explosion instancing went horribly wrong", 160 * 3, nb.getNumElements());
        assertEquals("ramp buffer from explosion instancing went horribly wrong", 160 , tb.getNumElements());

        //jet strategy proven
        particleMesh.initParticleData(particleEffectJet.numberOfParticles());
        assertEquals("Wrong number of particles!", 20, particleEffectJet.numberOfParticles());
        pb = particleMesh.getBuffer(VertexBuffer.Type.Position);
        nb = particleMesh.getBuffer(VertexBuffer.Type.Normal);
        tb = particleMesh.getBuffer(VertexBuffer.Type.TexCoord6);
        assertEquals("position buffer from explosion instancing went horribly wrong", 20 * 3, pb.getNumElements());
        assertEquals("normal buffer from explosion instancing went horribly wrong", 20 * 3, nb.getNumElements());
        assertEquals("ramp buffer from explosion instancing went horribly wrong", 20 , tb.getNumElements());

        //fire strategy proven
        particleMesh.initParticleData(particleEffectFire.numberOfParticles());
        assertEquals("Wrong number of particles!", particleEffectFire.numberOfParticles(), 100);
        pb = particleMesh.getBuffer(VertexBuffer.Type.Position);
        nb = particleMesh.getBuffer(VertexBuffer.Type.Normal);
        tb = particleMesh.getBuffer(VertexBuffer.Type.TexCoord6);
        assertEquals("position buffer from explosion instancing went horribly wrong", 100 * 3, pb.getNumElements());
        assertEquals("normal buffer from explosion instancing went horribly wrong", 100 * 3, nb.getNumElements());
        assertEquals("ramp buffer from explosion instancing went horribly wrong", 100 , tb.getNumElements());

        //gravity strategy proven
        particleMesh.initParticleData(particleEffectGravity.numberOfParticles());
        assertEquals("Wrong number of particles!", particleEffectGravity.numberOfParticles(), 50);
        pb = particleMesh.getBuffer(VertexBuffer.Type.Position);
        nb = particleMesh.getBuffer(VertexBuffer.Type.Normal);
        tb = particleMesh.getBuffer(VertexBuffer.Type.TexCoord6);
        assertEquals("position buffer from explosion instancing went horribly wrong", 50 * 3, pb.getNumElements());
        assertEquals("normal buffer from explosion instancing went horribly wrong", 50 * 3, nb.getNumElements());
        assertEquals("ramp buffer from explosion instancing went horribly wrong", 50 , tb.getNumElements());

        //shield strategy proven
        particleMesh.initParticleData(particleEffectShield.numberOfParticles());
        assertEquals("Wrong number of particles!", 40, particleEffectShield.numberOfParticles());
        pb = particleMesh.getBuffer(VertexBuffer.Type.Position);
        nb = particleMesh.getBuffer(VertexBuffer.Type.Normal);
        tb = particleMesh.getBuffer(VertexBuffer.Type.TexCoord6);
        assertEquals("position buffer from explosion instancing went horribly wrong", 40 * 3, pb.getNumElements());
        assertEquals("normal buffer from explosion instancing went horribly wrong", 40 * 3, nb.getNumElements());
        assertEquals("ramp buffer from explosion instancing went horribly wrong", 40 , tb.getNumElements());

    }

    @Test
    /**
     * checking last created buffer positions; other tests would look exact the same
     */
    public void checkPositions() {
        particleMesh.updateParticleData(particles);

        VertexBuffer pb = particleMesh.getBuffer(VertexBuffer.Type.Position);
        FloatBuffer positions = (FloatBuffer) pb.getData();

        for(int i = 0; i < pb.getNumElements(); i++) {

            assertEquals("Position in buffer is not correct!" , m_particleModel[i%3].x, positions.get(), 1e-8);
            assertEquals("Position in buffer is not correct!" , m_particleModel[i%3].y, positions.get(), 1e-8);
            assertEquals("Position in buffer is not correct!" , m_particleModel[i%3].z, positions.get(), 1e-8);

        }
    }
    @Test
    public void checkNormals() {
        particleMesh.updateParticleData(particles);

        VertexBuffer nvb = particleMesh.getBuffer(VertexBuffer.Type.Normal);
        FloatBuffer normals = (FloatBuffer) nvb.getData();
        for(int i = 0; i < nvb.getNumComponents(); i++) {
            assertEquals("Normal in buffer is not correct!" , 0, normals.get(),  1e-8);
            assertEquals("Normal in buffer is not correct!" , 1, normals.get(),  1e-8);
            assertEquals("Normal in buffer is not correct!" , 0, normals.get(),  1e-8);
        }
    }
    @Test
    public void checkRamp() {

        VertexBuffer tcb = particleMesh.getBuffer(VertexBuffer.Type.TexCoord6);
        FloatBuffer rampFactor = (FloatBuffer) tcb.getData();
        for(int i = 0; i < tcb.getNumComponents(); i++) {
            assertEquals("Ramp factor in buffer is not correct!" ,(2f/3f), rampFactor.get(),  1e-6);
            assertEquals("Ramp factor in buffer is not correct!" , (2f/3f), rampFactor.get(),  1e-6);
            assertEquals("Ramp factor in buffer is not correct!" , (2f/3f), rampFactor.get(),  1e-6);
        }
    }
}
