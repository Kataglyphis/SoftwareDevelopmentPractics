package edu.kit.valaris.rendering.particleEffect;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.*;
import com.jme3.shader.VarType;

import static com.jme3.math.FastMath.HALF_PI;

/**
 * An effect for our jet emitting little triangles
 * @author Jonas Heinle
 * @version 1.2
 * @see ParticleStrategy
 */

public class JetParticleStrategy implements ParticleStrategy {

    private float lifeTime = 0.1f;

    private int numberOfParticles = 20;

    private Vector3f acceleration = new Vector3f(0f, -2f, 0f);

    private Vector4f[] colors = {new Vector4f(0f,0f,1f,0f), new Vector4f(1f,1f,1f,1f)};

    private float[] angles = {0, FastMath.QUARTER_PI, FastMath.HALF_PI, FastMath.QUARTER_PI * 3f,
                              FastMath.PI, FastMath.QUARTER_PI * 5f, FastMath.QUARTER_PI * 6,
                              FastMath.QUARTER_PI * 7};
    private float radius = 1;

    @Override
    public void emitParticle(Particle particle, float tpf) {

        float b = (particle.age - particle.lifetime) / particle.lifetime;
        //if dead;spawn new in given limits we want the particles to move
        if(particle.age >= lifeTime) {

            particle.age = MathUtil.getRandomArbitrary(0, this.lifeTime);

            int anglesIndex = FastMath.nextRandomInt(0, angles.length - 1);
            // mult(0.25f) --> 4 jets only maintaining a 4th of place
            Vector3f newTranslation = MathUtil.convertPolarToCartesian(this.radius, angles[anglesIndex]).mult(0.25f);

            particle.transform.setTranslation(newTranslation);

            float initialRotationalDisplacement = HALF_PI;
            float[] rotAngles = {initialRotationalDisplacement * FastMath.nextRandomFloat(),
                    initialRotationalDisplacement * FastMath.nextRandomFloat(),
                    initialRotationalDisplacement * FastMath.nextRandomFloat()};
            Quaternion rotation = new Quaternion(rotAngles);
            particle.transform.setRotation(rotation);
            particle.transform.setScale(b/4);
            return;
        }

        //make acceleration independent from fps
        particle.transform.setTranslation(particle.transform.getTranslation().add(acceleration.mult(b * tpf)));
        particle.transform.setScale(b/4 , b/4, b/4);
    }

    @Override
    public void updateParticle(Particle particle, float tpf) {

        particle.age += tpf;
    }

    @Override
    public int getNumParticles() {
        return this.numberOfParticles;
    }

    @Override
    public void initializeParticleData(Particle[] particles) {

        for(int i = 0; i < particles.length; i++) {

            particles[i] = new Particle();
            particles[i].lifetime = this.lifeTime;
            particles[i].age = MathUtil.getRandomArbitrary(0, this.lifeTime);

        }

    }

    @Override
    public Material getMaterial(AssetManager assetManager) {
        Material particleMat = new Material(assetManager,
                "/edu.kit.valaris/materials/PBRLighting.j3md");
        particleMat.setBoolean("UseColorAttribute", false);
        particleMat.setFloat("Metallic", 0f);
        particleMat.setFloat("Specular", 0.5f);
        particleMat.setBoolean("UseRampFactorAttribute", true);
        particleMat.setColor("BaseColor", ColorRGBA.Black);
        particleMat.setInt("NumEmissivePoints", this.colors.length);
        particleMat.setParam("EmissivePoints",VarType.Vector4Array, this.colors);
        particleMat.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Off);

        return particleMat;
    }

    @Override
    public Vector4f[] getColors() {
        return this.colors;
    }

    @Override
    public boolean isGlobal() {
        return false;
    }

}
