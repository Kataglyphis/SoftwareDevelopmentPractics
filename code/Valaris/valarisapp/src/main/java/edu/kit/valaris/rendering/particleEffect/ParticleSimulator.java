package edu.kit.valaris.rendering.particleEffect;

import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import com.jme3.util.BufferUtils;
import edu.kit.valaris.threading.Job;

import java.nio.FloatBuffer;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Used to operate a {@link ParticleStrategy} in a seperate thread.
 *
 * @author Frederik Lingg
 */
public class ParticleSimulator extends Job {

    /**
     * buffer containing consistent position data for the mesh.
     */
    private FloatBuffer m_renderPositionBuffer;

    /**
     * buffer containing consistent normal data for the mesh.
     */
    private FloatBuffer m_renderNormalBuffer;

    /**
     * buffer containing consistent ramp data for the mesh.
     */
    private FloatBuffer m_renderRampBuffer;

    /**
     * buffer containing consistent position data after step of simulation is finished.
     */
    private float[] m_mediatorPositionBuffer;

    /**
     * buffer containing consistent normal data after step of simulation is finished.
     */
    private float[] m_mediatorNormalBuffer;

    /**
     * buffer containing consistent ramp data after step of simulation is finished.
     */
    private float[] m_mediatorRampBuffer;

    /**
     * buffer used by simulation to write position data.
     */
    private float[] m_simulationPositionBuffer;

    /**
     * buffer used by simulation to write position data.
     */
    private float[] m_simulationNormalBuffer;

    /**
     * buffer used by simulation to write position data.
     */
    private float[] m_simulationRampBuffer;

    /**
     * Whether the renderBuffers are outdated.
     */
    private boolean m_dirty;

    /**
     * Lock used to synchronize access to mediatorBuffers.
     */
    private Lock m_bufferLock;

    /**
     * The {@link ParticleStrategy} used for simulation.
     */
    private ParticleStrategy m_strategy;

    /**
     * whether the simulation should still be running or not.
     */
    private boolean m_running;

    //------ particle model ------//
    private Vector3f m_v0;
    private Vector3f m_v1;
    private Vector3f m_v2;
    private Vector3f m_n;
    private Particle[] m_particles;

    //------ variables made to avoid creating unnecessary instances at runtime ------//
    private Vector3f m_tmpV;

    /**
     * Creates a new {@link ParticleSimulator} using the given {@link ParticleStrategy}.
     * @param strategy the strategy to use.
     */
    public ParticleSimulator(ParticleStrategy strategy) {
        m_strategy = strategy;
        m_dirty = false;
        m_running = true;

        //init lock
        m_bufferLock = new ReentrantLock();

        //init buffers
        int numVertices = strategy.getNumParticles() * 3;

        m_renderPositionBuffer = BufferUtils.createVector3Buffer(numVertices);
        m_renderNormalBuffer = BufferUtils.createVector3Buffer(numVertices);
        m_renderRampBuffer = BufferUtils.createFloatBuffer(numVertices);
        m_renderPositionBuffer.clear();
        m_renderNormalBuffer.clear();
        m_renderRampBuffer.clear();

        m_mediatorPositionBuffer = new float[numVertices * 3];
        m_mediatorNormalBuffer = new float[numVertices * 3];
        m_mediatorRampBuffer = new float[numVertices];

        m_simulationPositionBuffer = new float[numVertices * 3];
        m_simulationNormalBuffer = new float[numVertices * 3];
        m_simulationRampBuffer = new float[numVertices];

        //init particle model
        m_v0 = new Vector3f(0f, 0f, 0.5f);
        m_v1 = new Vector3f(-0.5f, 0.0f, -0.5f);
        m_v2 = new Vector3f(0.5f, 0.0f, -0.5f);
        m_n = new Vector3f(Vector3f.UNIT_Y);

        //init tmp variables
        m_tmpV = new Vector3f();
    }

    /**
     * Prepares the given {@link Mesh} for use with this {@link ParticleSimulator}.
     * @param mesh the {@link Mesh} to prepare.
     */
    public void prepareMesh(Mesh mesh) {
        //set rendering mode
        mesh.setMode(Mesh.Mode.Triangles);

        //check for positionbuffer
        VertexBuffer pbuf = mesh.getBuffer(VertexBuffer.Type.Position);
        if(pbuf == null) {
            pbuf = new VertexBuffer(VertexBuffer.Type.Position);
            pbuf.setupData(VertexBuffer.Usage.Dynamic, 3, VertexBuffer.Format.Float, m_renderPositionBuffer);
            mesh.setBuffer(pbuf);
        } else {
            pbuf.updateData(m_renderPositionBuffer);
        }

        //check for normalbuffer
        VertexBuffer nbuf = mesh.getBuffer(VertexBuffer.Type.Normal);
        if(nbuf == null) {
            nbuf = new VertexBuffer(VertexBuffer.Type.Normal);
            nbuf.setupData(VertexBuffer.Usage.Dynamic, 3, VertexBuffer.Format.Float, m_renderNormalBuffer);
            mesh.setBuffer(nbuf);
        } else {
            nbuf.updateData(m_renderNormalBuffer);
        }

        //check for rampbuffer
        VertexBuffer rbuf = mesh.getBuffer(VertexBuffer.Type.TexCoord6);
        if(rbuf == null) {
            rbuf = new VertexBuffer(VertexBuffer.Type.TexCoord6);
            rbuf.setupData(VertexBuffer.Usage.Dynamic, 1, VertexBuffer.Format.Float, m_renderRampBuffer);
            mesh.setBuffer(rbuf);
        } else {
            rbuf.updateData(m_renderRampBuffer);
        }

        //notify mesh
        mesh.updateCounts();
    }

    /**
     * Updates vertexdata of the given {@link Mesh}. the {@link Mesh} has to be prepared using {@link #prepareMesh(Mesh)}.
     * @param mesh the mesh to fetch the buffers to.
     */
    public void fetchBuffers(Mesh mesh) {
        //clear position of buffers
        m_renderPositionBuffer.clear();
        m_renderNormalBuffer.clear();
        m_renderRampBuffer.clear();

        //copy data
        m_bufferLock.lock();
        if(m_dirty) {
            m_renderPositionBuffer.put(m_mediatorPositionBuffer);
            m_renderNormalBuffer.put(m_mediatorNormalBuffer);
            m_renderRampBuffer.put(m_mediatorRampBuffer);

            //remember buffers were fetched
            m_dirty = false;
        }
        m_bufferLock.unlock();

        //clear position of buffers
        m_renderPositionBuffer.clear();
        m_renderNormalBuffer.clear();
        m_renderRampBuffer.clear();

        //update mesh data
        mesh.getBuffer(VertexBuffer.Type.Position).updateData(m_renderPositionBuffer);
        mesh.getBuffer(VertexBuffer.Type.Normal).updateData(m_renderNormalBuffer);
        mesh.getBuffer(VertexBuffer.Type.TexCoord6).updateData(m_renderRampBuffer);

        //notify the mesh
        //mesh.updateBound();
    }

    /**
     * Stops the simulation and deletes all created buffers.
     * The {@link ParticleSimulator} should not be used further after calling this method.
     */
    public void cleanup() {
        m_running = false;
    }

    @Override
    protected void jobInit() {
        m_particles = new Particle[m_strategy.getNumParticles()];
        m_strategy.initializeParticleData(m_particles);
    }

    @Override
    protected void jobUpdate(float tpf) {
        //simulate step
        for(Particle particle : m_particles) {
            m_strategy.emitParticle(particle, tpf);
            m_strategy.updateParticle(particle, tpf);
        }

        //update buffers
        for(int i = 0; i < m_particles.length; i++) {
            Particle particle = m_particles[i];
            int ri = i * 3;
            int pni = i * 3 * 3;

            //update positions
            particle.transform.transformVector(m_v0, m_tmpV);
            m_simulationPositionBuffer[pni + 0] = m_tmpV.x;
            m_simulationPositionBuffer[pni + 1] = m_tmpV.y;
            m_simulationPositionBuffer[pni + 2] = m_tmpV.z;

            particle.transform.transformVector(m_v1, m_tmpV);
            m_simulationPositionBuffer[pni + 3] = m_tmpV.x;
            m_simulationPositionBuffer[pni + 4] = m_tmpV.y;
            m_simulationPositionBuffer[pni + 5] = m_tmpV.z;

            particle.transform.transformVector(m_v2, m_tmpV);
            m_simulationPositionBuffer[pni + 6] = m_tmpV.x;
            m_simulationPositionBuffer[pni + 7] = m_tmpV.y;
            m_simulationPositionBuffer[pni + 8] = m_tmpV.z;

            //update normals
            //3 times the same normal
            particle.transform.getRotation().mult(m_n, m_tmpV);
            m_simulationNormalBuffer[pni + 0] = m_tmpV.x;
            m_simulationNormalBuffer[pni + 1] = m_tmpV.y;
            m_simulationNormalBuffer[pni + 2] = m_tmpV.z;
            m_simulationNormalBuffer[pni + 3] = m_tmpV.x;
            m_simulationNormalBuffer[pni + 4] = m_tmpV.y;
            m_simulationNormalBuffer[pni + 5] = m_tmpV.z;
            m_simulationNormalBuffer[pni + 6] = m_tmpV.x;
            m_simulationNormalBuffer[pni + 7] = m_tmpV.y;
            m_simulationNormalBuffer[pni + 8] = m_tmpV.z;

            //update ramps
            //3 times the same factor
            float ramp = particle.age / particle.lifetime;
            m_simulationRampBuffer[ri + 0] = ramp;
            m_simulationRampBuffer[ri + 1] = ramp;
            m_simulationRampBuffer[ri + 2] = ramp;
        }

        //swap mediator buffers
        m_bufferLock.lock();
        float[] tmp = m_simulationPositionBuffer;
        m_simulationPositionBuffer = m_mediatorPositionBuffer;
        m_mediatorPositionBuffer = tmp;

        tmp = m_simulationNormalBuffer;
        m_simulationNormalBuffer = m_mediatorNormalBuffer;
        m_mediatorNormalBuffer = tmp;

        tmp = m_simulationRampBuffer;
        m_simulationRampBuffer = m_mediatorRampBuffer;
        m_mediatorRampBuffer = tmp;

        //remember buffers were updated
        m_dirty = true;
        m_bufferLock.unlock();
    }

    @Override
    protected void jobCleanup() {
        //do cleanup
        BufferUtils.destroyDirectBuffer(m_renderPositionBuffer);
        BufferUtils.destroyDirectBuffer(m_renderNormalBuffer);
        BufferUtils.destroyDirectBuffer(m_renderRampBuffer);
    }

    @Override
    public boolean isDead() {
        return !m_running;
    }
}
