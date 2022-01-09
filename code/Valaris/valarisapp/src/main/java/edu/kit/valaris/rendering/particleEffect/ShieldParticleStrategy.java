package edu.kit.valaris.rendering.particleEffect;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.*;
import com.jme3.shader.VarType;

import static com.jme3.math.FastMath.HALF_PI;

public class ShieldParticleStrategy implements ParticleStrategy {

    private Vector3f gravity = new Vector3f(0f, -0.5f, 0f);

    private float lifeTime = 3f;

    private int numParticles = 160;

    private Vector4f[] colors = {ColorRGBA.Orange.toVector4f(),
                                 };

    float scaleFactor = 0.25f;

    private float radius = 1;

    private float[] theta = {0, FastMath.PI * 0.125f, FastMath.QUARTER_PI, FastMath.PI * 0.375f, FastMath.HALF_PI,
                             FastMath.PI * (0.625f), FastMath.PI * (0.75f), FastMath.PI * (0.875f), FastMath.PI};

    private float[] phi = {0, FastMath.QUARTER_PI, FastMath.HALF_PI, FastMath.PI * (0.75f), FastMath.PI,
                           FastMath.PI* (1.25f), FastMath.PI* (1.5f), FastMath.PI * (1.75f)};

    @Override
    public void emitParticle(Particle particle, float tpf) {

        float ageLifetimeRatio = (particle.age - particle.lifetime) / particle.lifetime;

        if((particle.age >= lifeTime)) {

            particle.age = 0;

            float initialRotationalDisplacement = HALF_PI;

            //getting different starting rotations
            float[] rotAngles = {initialRotationalDisplacement * FastMath.nextRandomFloat(),
                    initialRotationalDisplacement * FastMath.nextRandomFloat(),
                    initialRotationalDisplacement * FastMath.nextRandomFloat()};

            Quaternion rotation = new Quaternion(rotAngles);

            int phiIndex = FastMath.nextRandomInt(0, phi.length - 1);
            int thetaIndex = FastMath.nextRandomInt(0, theta.length - 1);
            Vector3f translation = MathUtil.convertSpericalToCartesian(radius, theta[thetaIndex], phi[phiIndex]);

            particle.transform.setScale(0.25f);
            particle.transform.setTranslation(translation.mult(0.05f));
            particle.transform.setRotation(rotation);

            return;
        }
//        1.)making it independent of frame rate and of our ratio above; leads to fast acceleration, speed fades with
//         time
//        particle.transform.setTranslation(particle.transform.getTranslation().addLocal
//                (gravity.mult((ageLifetimeRatio * tpf))));
        particle.transform.setTranslation(particle.transform.getTranslation()
                                                .add(particle.transform.getTranslation().mult(tpf)));
        //System.out.println(particle.transform.getTranslation());
//        particle.transform.setScale(scaleFactor * ageLifetimeRatio, scaleFactor * ageLifetimeRatio,
//                            scaleFactor * ageLifetimeRatio);
    }

    @Override
    public void updateParticle(Particle particle, float tpf) {

        //particle is altering
        particle.age += tpf;

    }

    @Override
    public int getNumParticles() {
        return this.numParticles;
    }

    @Override
    public void initializeParticleData(Particle[] particles) {

        for(int i = 0; i < particles.length; i++) {
            particles[i] = new Particle();
            particles[i].lifetime = this.lifeTime;
            particles[i].age = this.lifeTime;
            particles[i].transform = new Transform();
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
