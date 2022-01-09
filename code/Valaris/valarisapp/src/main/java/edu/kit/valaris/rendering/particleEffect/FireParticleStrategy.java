package edu.kit.valaris.rendering.particleEffect;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.*;
import com.jme3.scene.Spatial;
import com.jme3.shader.VarType;

import static com.jme3.math.FastMath.HALF_PI;

/**
 * symbolizing a low poly fire with changing colors
 * @author Jonas Heinle
 * @version 1.2
 * @see ParticleStrategy
 */
public class FireParticleStrategy implements  ParticleStrategy {

    private Vector3f gravity = new Vector3f(0f, 0.5f, 0);

    private float lifeTime = 2f;

    private int numberOfParticles = 100;

    //represents the color history of our fire
    private Vector4f[] colors = {new Vector4f(1f,0f,0f,0.25f),
                                 new Vector4f(1f,1f,0f,0.5f),
                                 new Vector4f(1f, 0f, 0f, 0.85f),
                                 new Vector4f(0f,0f,0f,1f)
                                 /*, new Vector4f(1f,1f,0f,0.75f), new Vector4f(1f,0f,0f,0.9f)*/};


    @Override
    public void emitParticle(Particle particle, float tpf) {

        if(particle.age >= lifeTime) {

            particle.transform.setTranslation(particle.transform.getTranslation().add(
                    MathUtil.getRandomArbitrary(-0.5f,0.5f),
                    MathUtil.getRandomArbitrary(0,0.5f),
                    MathUtil.getRandomArbitrary(-0.5f,0.5f)));

            particle.age = MathUtil.getRandomArbitrary(0, this.lifeTime);
            return;
        }

        float b = ( particle.lifetime - particle.age) / particle.lifetime;//(particle.age / particle.lifetime);

        float translationOfParticle =  (b * tpf);
        particle.transform.setScale(b, b, b);

        particle.transform.setTranslation(particle.transform.getTranslation().addLocal
                (particle.transform.getTranslation().mult(translationOfParticle)).
                addLocal(this.gravity.mult(tpf)));

    }

    @Override
    public void updateParticle(Particle particle, float tpf) {
        //aging
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
            Vector3f translation = new Vector3f(MathUtil.getRandomArbitrary(-1, 1),MathUtil.getRandomArbitrary(0,1)
                                                                         ,MathUtil.getRandomArbitrary(-1, 1));

            float rotAngle[] = {HALF_PI * MathUtil.getRandomArbitrary(0,1),HALF_PI * MathUtil.getRandomArbitrary(0,1),
                                                                  HALF_PI * MathUtil.getRandomArbitrary(0,1)};
            Quaternion rotation = new Quaternion(rotAngle);
            particles[i] = new Particle();
            particles[i].age = MathUtil.getRandomArbitrary(0, this.lifeTime);
            particles[i].lifetime = this.lifeTime;
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
