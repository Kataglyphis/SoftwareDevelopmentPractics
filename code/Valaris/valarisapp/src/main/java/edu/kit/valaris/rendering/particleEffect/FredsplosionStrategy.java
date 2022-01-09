package edu.kit.valaris.rendering.particleEffect;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.math.Vector4f;
import com.jme3.shader.VarType;
import umontreal.ssj.hups.HammersleyPointSet;
import umontreal.ssj.hups.PointSet;

import java.util.Random;

/**
 * Alternative Explosion by Frederik Lingg
 */
public class FredsplosionStrategy implements ParticleStrategy {

    /**
     * The gravity used for the simulation of the trajectory of the trails
     */
    public static final Vector3f FREDSPLOSION_GRAVITY = new Vector3f(0, -9.81f, 0);

    /**
     * The ColorRamp to use for emission
     */
    public static final Vector4f[] FREDSPLOSION_EMISSION = new Vector4f[]{
            new Vector4f(0, 0, 0, .00f),
            new Vector4f(0, 0, 0, .40f),
            new Vector4f(.6f, .2f, .0f, .5f),
            new Vector4f(.8f, .7f, .5f, .7f),
            new Vector4f(0, 0, 0, .8f),
            new Vector4f(0, 0, 0, 1.0f)
    };

    /**
     * The ColorRamp to use for albedo
     */
    public static final Vector4f[] FREDSPLOSION_ALBEDO = new Vector4f[]{
            new Vector4f(.25f, .16f, .12f, .00f),
            new Vector4f(.25f, .16f, .12f, .40f),
            new Vector4f(0, 0, 0, .5f),
            new Vector4f(0, 0, 0, .7f),
            new Vector4f(.04f, .04f, .03f, .8f),
            new Vector4f(.04f, .04f, .03f, 1.0f)
    };

    /**
     * More data is needed to accuratly simulate the effect
     */
    private class FredsplosionParticle extends Particle {
        /**
         * The speed of a particle
         */
        public Vector3f speed = new Vector3f();

        /**
         * The rotation of the particle
         */
        public float angularMomentum = 0;

        /**
         * Whether the particle is alive or not
         */
        public boolean alive = false;

        public float trailAge = 0;

        /**
         * The index of the particle (this is used to assign it to the correct trail
         */
        public int index = 0;
    }

    /**
     * The speed of the trail emitters
     */
    private Vector3f[] m_trailSpeed;

    /**
     * The maximum lifetime of the trail emitters
     */
    private float[] m_trailLifeTime;

    /**
     * How many particles can be emitted per trail
     */
    private int m_numPerTrail;

    /**
     * How many particles are emitted from the base
     */
    private int m_numBase;

    /**
     * The total number of particles this effect will simulate
     */
    private int m_numParticles;

    /**
     * Used for all random operations
     */
    private Random m_random;

    /**
     * Temporary values to prevent creating new instances
     */
    private Vector3f m_tmpVec0;
    private Vector3f m_tmpVec1;
    private Vector3f m_tmpVec2;

    /**
     * Creates a new {@link FredsplosionStrategy} with the given number of particles and trails
     * @param numParticles the maximum number of particles
     * @param numTrails the number of trails
     */
    public FredsplosionStrategy(int numParticles, int numTrails) {
        m_trailSpeed = new Vector3f[numTrails];
        m_trailLifeTime = new float[numTrails];

        m_numParticles = numParticles;
        m_numPerTrail = m_numParticles / (numTrails + 1);
        m_numBase = m_numParticles - (m_trailSpeed.length * m_numPerTrail);

        //init m_random
        m_random = new Random();

        //initialize tmp variables
        m_tmpVec0 = new Vector3f();
        m_tmpVec1 = new Vector3f();
        m_tmpVec2 = new Vector3f();
    }

    @Override
    public void emitParticle(Particle particle, float tpf) {
        FredsplosionParticle fp = (FredsplosionParticle) particle;

        //if the particle was not dead before, it is possible to spawn it
        if(!fp.alive && !(fp.age > fp.lifetime)) {
            //check for which trail the particle belongs to
            int trail = (fp.index - m_numBase) / m_numPerTrail;

            //check whether it should spawn
            int innerIndex = (fp.index - m_numBase) % m_numPerTrail;
            float spawnTime = (float) (innerIndex + 1) / (float) m_numPerTrail;
            if((trail >= 0) && (spawnTime * m_trailLifeTime[trail]) <= (fp.trailAge)) {
                //spawn particle
                fp.age = 0;
                fp.alive = true;

                //random lifetime
                fp.lifetime = m_random.nextFloat() + 1.0f;

                //calculate trail position
                m_tmpVec0.set(FREDSPLOSION_GRAVITY).multLocal(fp.trailAge / 2.0f);
                m_tmpVec0.addLocal(m_trailSpeed[trail]);
                m_tmpVec0.multLocal(fp.trailAge);
                fp.transform.setTranslation(m_tmpVec0);


                //calculate trail speed
                m_tmpVec0.set(FREDSPLOSION_GRAVITY).multLocal(fp.trailAge);
                m_tmpVec0.addLocal(m_trailSpeed[trail]);

                //calculate tangent and binormal for random drift perpendicular to trail
                m_tmpVec0.cross(Vector3f.UNIT_Y, m_tmpVec1);
                m_tmpVec0.cross(m_tmpVec1, m_tmpVec2);

                //calculate drift
                float x = m_random.nextFloat() * 2.0f - 1.0f;
                float z = m_random.nextFloat() * 2.0f - 1.0f;
                m_tmpVec1.normalizeLocal().multLocal(x * 0.2f);
                m_tmpVec2.normalizeLocal().multLocal(z * 0.2f);

                //set speed
                fp.speed.set(m_tmpVec1);
                fp.speed.addLocal(m_tmpVec2);

                //set random lorotation and angular momentum
                float amx = m_random.nextFloat() * 2.0f - 1.0f;
                float amy = m_random.nextFloat() * 2.0f - 1.0f;
                float amz = m_random.nextFloat() * 2.0f - 1.0f;
//                fp.transform.getRotation().fromAngles(amx, amy, amz);
                fp.angularMomentum = m_random.nextFloat() * (float) Math.PI * 2.0f;
            }
        }
    }

    @Override
    public void updateParticle(Particle p, float tpf) {
        FredsplosionParticle fp = (FredsplosionParticle) p;

        //always update trailage
        fp.trailAge += tpf;

        //update the particle
        if(fp.alive) {
            //update age
            fp.age += tpf;

            //update position
            fp.transform.getTranslation().addLocal(fp.speed.mult(tpf, m_tmpVec0));

            //update rotation
//            Quaternion rotation = fp.transform.getRotation();
//            rotation.set(rotation.getX(), rotation.getY(), rotation.getZ(), rotation.getW() + fp.angularMomentum * tpf);

            //update scale
            m_tmpVec0.set(1f, 1f, 1f);
            //TODO use more sophisticated scaling
            fp.transform.setScale(m_tmpVec0);

            //check if particle is dead and if so, set scale to 0
            if(fp.age > fp.lifetime) {
                fp.alive = false;
                fp.transform.getScale().set(0, 0, 0);
            }

            //System.out.println(fp.transform);
        }
    }

    @Override
    public int getNumParticles() {
        return m_numParticles;
    }

    @Override
    public void initializeParticleData(Particle[] particles) {
        //initialize the particles
        //the velocitydistribution of the base particles
        PointSet baseDist = new HammersleyPointSet(m_numBase, 2);

        //initialize particles and emit base particles
        for(int i = 0; i < particles.length; i++) {
            FredsplosionParticle particle = new FredsplosionParticle();
            particle.index = i;
            particle.age = 0;
            particle.trailAge = 0;

            //spawn base and disable other particles
            if(i < m_numBase) {
                //calc velocity
                float x = m_random.nextFloat() * 2.0f - 1.0f;
                float y = 1.0f;
                float z = m_random.nextFloat() * 2.0f - 1.0f;
                particle.speed.set(x, y, z).normalizeLocal();

                //calc initial rotation and angular momentum
                float amx = m_random.nextFloat() * 2.0f - 1.0f;
                float amy = m_random.nextFloat() * 2.0f - 1.0f;
                float amz = m_random.nextFloat() * 2.0f - 1.0f;
                particle.transform.getRotation().fromAngles(amx, amy, amz);
                particle.angularMomentum = m_random.nextFloat() * (float) Math.PI * 2.0f;

                //base particles live 2 - 3 seconds
                particle.lifetime = 2f + m_random.nextFloat();

                //remember this particle is ready to simulate
                particle.alive = true;
            } else {
                //if this particle does not belong to the base, it will be spawned later
                particle.alive = false;
                particle.transform.getScale().set(new Vector3f(0, 0, 0));
            }

            //put particle into the array of all particles
            particles[i] = particle;
        }

        //initialize the trails
        for(int i = 0; i < m_trailSpeed.length; i++) {
            //trails have a m_random speed and direction
            float x = m_random.nextFloat() * 2.0f - 1.0f;
            float z = m_random.nextFloat() * 2.0f - 1.0f;
            float y = 1.0f;
            m_trailSpeed[i] = new Vector3f(x, y, z).normalizeLocal().multLocal(m_random.nextFloat() * 2.0f + 3.0f);

            //trails have a m_random lifetime
            m_trailLifeTime[i] = m_random.nextFloat() / 4.0f + 0.5f;
        }
    }

    @Override
    public Material getMaterial(AssetManager assetManager) {
        Material mat = new Material(assetManager, "/edu.kit.valaris/materials/PBRLighting.j3md");

        //adjust base values
        mat.setFloat("Metallic", 0.0f);
        mat.setFloat("Specular", 0.5f);

        //set colorramps and interpolator attribute
        mat.setBoolean("UseRampFactorAttribute", true);

        //albedo
        mat.setInt("NumColorPoints", FREDSPLOSION_ALBEDO.length);
        mat.setParam("ColorPoints", VarType.Vector4Array, FREDSPLOSION_ALBEDO);

        //emission
        mat.setInt("NumColorPoints", FREDSPLOSION_EMISSION.length);
        mat.setParam("ColorPoints", VarType.Vector4Array, FREDSPLOSION_EMISSION);
        mat.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Off);

        return mat;
    }

    @Override
    public Vector4f[] getColors() {
        return FREDSPLOSION_ALBEDO;
    }

    @Override
    public boolean isGlobal() {
        return false;
    }
}
