package edu.kit.valaris.rendering.particleEffect;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.*;
import com.jme3.scene.Spatial;
import com.jme3.shader.VarType;

import static com.jme3.math.FastMath.HALF_PI;

/**
 * creates an explosion on the screen in low poly style with triangles
 * @author Jonas Heinle
 * @version 1.2
 * @see ParticleStrategy
 */
public class ExplosionParticleStrategy implements ParticleStrategy {

    private Vector3f gravity = new Vector3f(0f, 1.5f, 0f);

    private float lifeTime = 1f;

    private float duration = 100f;

    private int numParticles = 160;

    private float accelerationFac = 2;

    private float scaleFac = (float) (1f/3f);

    private Vector4f[] colors = {new Vector4f(1f,1f,1f,0.00f),new Vector4f(1f,1f,0f,0.15f),
                                 new Vector4f(1f,0f,0f,0.5f), new Vector4f(0f,0f,0f,0.65f)};

    private Vector3f[] explosionDirection = {new Vector3f(1f,1f,1f),
                                             new Vector3f(1f,1f,-1f),
                                             new Vector3f(1f,1f,0f),
                                             new Vector3f(0f,1f,1f),
                                             new Vector3f(0f,1f,-1f),
                                             new Vector3f(-1f,1f,0f),
                                             new Vector3f(-1f, 1f, 1f),
                                             new Vector3f(-1f, 1f,-1f)};

    @Override
    public void emitParticle(Particle particle, float tpf) {
//        System.out.println("tpf = " + tpf);
//        System.out.println("duration = " + duration);
        //one big explosion.
        //scale for the computation depending on the max. lifetime/ current age ratio
        float ageLifetimeRatio = ( particle.lifetime - particle.age) / particle.lifetime;

        if(((particle.age >= lifeTime) && (duration > 0))) {

            particle.age = MathUtil.getRandomArbitrary(0, this.lifeTime);
            particle.lifetime = this.lifeTime;

            float initialRotationalDisplacement = HALF_PI;
            //getting different starting rotations
            //optical impression of chaotic explosion
            float[] rotAngles = {initialRotationalDisplacement * FastMath.nextRandomFloat(),
                    initialRotationalDisplacement * FastMath.nextRandomFloat(),
                    initialRotationalDisplacement * FastMath.nextRandomFloat()};
            Quaternion rotation = new Quaternion(rotAngles);

            int i = FastMath.nextRandomInt(0, explosionDirection.length - 1);
            Vector3f newTranslation = this.explosionDirection[i];
            particle.transform.setTranslation(newTranslation);
            particle.transform.setRotation(rotation);
            particle.transform.setScale(ageLifetimeRatio * scaleFac, ageLifetimeRatio * scaleFac,
                                       ageLifetimeRatio * scaleFac);

        } else if ((particle.age < this.lifeTime) && (duration <= 0)) {
//
            float invRatio = 1 - ageLifetimeRatio;
            particle.transform.setTranslation(particle.transform.getTranslation().addLocal
                    (particle.transform.getTranslation().mult((ageLifetimeRatio * tpf * accelerationFac)))
                    .subtractLocal(this.gravity.mult(invRatio * tpf * accelerationFac)));

            //scaling depend on ramp fac
            particle.transform.setScale(ageLifetimeRatio * scaleFac , ageLifetimeRatio * scaleFac,
                    ageLifetimeRatio * scaleFac);
            return;

        } else if ((particle.age > this.lifeTime) && (duration <= 0)){

            particle.transform.setScale(0f);
            return;
        }

        float invRatio = 1 - ageLifetimeRatio;
        //1.)making it independent of frame rate and of our ratio above; leads to fast acceleration, speed fades with
        // time
        //2.) gravity plays a role on explosions so apply it

        particle.transform.setTranslation(particle.transform.getTranslation().addLocal
                (particle.transform.getTranslation().mult((ageLifetimeRatio * tpf * accelerationFac)))
                .subtractLocal(this.gravity.mult(invRatio * tpf * accelerationFac)));

        //scaling depend on ramp fac
        particle.transform.setScale(ageLifetimeRatio * scaleFac , ageLifetimeRatio * scaleFac,
                                    ageLifetimeRatio * scaleFac);

    }

    @Override
    public void updateParticle(Particle particle, float tpf) {

        //particle is altering
        particle.age += tpf;
        duration -= tpf;
    }

    @Override
    public int getNumParticles() {
        return this.numParticles;
    }

    @Override
    public void initializeParticleData(Particle[] particles) {

        for(int i = 0; i < explosionDirection.length; i++) {

            for (int j = 0; j < numParticles/explosionDirection.length; j++) {

                particles[j+(i * numParticles/explosionDirection.length)] = new Particle();
                particles[j+(i * numParticles/explosionDirection.length)].age = this.lifeTime;
                particles[j+(i * numParticles/explosionDirection.length)].lifetime = this.lifeTime;
                particles[j+(i * numParticles/explosionDirection.length)].transform.setScale(0f);

            }
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
