package edu.kit.valaris.rendering.particleEffect;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.*;
import com.jme3.scene.Spatial;
import com.jme3.shader.VarType;

import static com.jme3.math.FastMath.HALF_PI;

/**
 * a gravity trap for the vehicles
 * @author Jonas Heinle
 * @version  1.2
 * @see ParticleStrategy
 */
public class GravityTrap implements ParticleStrategy {

    private float lifeTime = 3f;

    private int numberOfParticles = 50;

    private Vector4f[] colors = {new Vector4f(0f,1f,0f,0f), new Vector4f(0f,0f,0f,1f)};

    @Override
    public void emitParticle(Particle particle, float tpf) {

        if((particle.age >= lifeTime)) {

            particle.lifetime = this.lifeTime;
            particle.age = 0;//this.getRandomArbitrary(0, this.lifeTime);
            Vector3f localTranslation = new Vector3f();
            Vector3f computedLocal = localTranslation.add(MathUtil.getRandomArbitrary(-1, 1),
                    MathUtil.getRandomArbitrary(-1, 1),
                    MathUtil.getRandomArbitrary(-1, 1));
            particle.transform.setTranslation(computedLocal);

            return;
        }


        float b = ( particle.lifetime - particle.age ) / particle.lifetime;

        particle.transform.setScale(b, b, b);

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

            Vector3f scale = new Vector3f(1f, 1f, 1f);
            Vector3f translation = new Vector3f(MathUtil.getRandomArbitrary(-1,1), MathUtil.getRandomArbitrary(-1, 1),
                    MathUtil.getRandomArbitrary(-1,1));
            float initialRotationalDisplacement = HALF_PI;
            float[] rotAngles = {initialRotationalDisplacement * FastMath.nextRandomFloat(),
                                 initialRotationalDisplacement * FastMath.nextRandomFloat(),
                                 initialRotationalDisplacement * FastMath.nextRandomFloat()};
            Quaternion rotation = new Quaternion(rotAngles);
            particles[i] = new Particle();
            particles[i].lifetime = this.lifeTime;
            particles[i].age = 0;//this.getRandomArbitrary(0, this.lifeTime);
            particles[i].transform = new Transform(translation, rotation, scale);
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
