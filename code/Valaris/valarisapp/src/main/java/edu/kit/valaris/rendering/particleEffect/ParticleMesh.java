package edu.kit.valaris.rendering.particleEffect;

import com.jme3.math.Transform;
import com.jme3.math.Vector3f;
import com.jme3.math.Vector4f;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import com.jme3.util.BufferUtils;

import java.nio.Buffer;
import java.nio.FloatBuffer;

/**
 * one particle Mesh for all our particle effects;
 * @author Jonas Heinle
 * @version 1.3
 * @see Particle
 * @see ParticleEffect
 */
public class ParticleMesh extends Mesh {

    /**
     * Particle model
     */
    private Vector3f[] m_particleModel = new Vector3f[] {
            new Vector3f(0f, 0f, 0.5f),
            new Vector3f(-0.5f, 0.0f, -0.5f).normalizeLocal(),
            new Vector3f(0.5f, 0.0f, -0.5f).normalizeLocal()
    };

    public ParticleMesh() {
        super();
    }

    public void initParticleData(int numParticles) {

        //always set up on triangles
        setMode(com.jme3.scene.Mesh.Mode.Triangles);

        //initialize all necessary vertex buffers for our mesh on given information
        FloatBuffer pb = BufferUtils.createVector3Buffer(numParticles * 3);

        VertexBuffer buf = getBuffer(VertexBuffer.Type.Position);
        if(buf != null) {
            buf.updateData(pb);
        } else {
            VertexBuffer vertexBuffer = new VertexBuffer(VertexBuffer.Type.Position);
            vertexBuffer.setupData(VertexBuffer.Usage.Stream, 3, VertexBuffer.Format.Float, pb);
            setBuffer(vertexBuffer);
        }
        /*
        //we no longer use color as vertex attribut for storing computaion in shaders for relieve cpu
        // set colors, for each particlevertex we got 4 channels (RGBA) therefore
        // we need for each again 4 values,
        // ByteBuffer for having high enough color depth
        ByteBuffer cb = BufferUtils.createByteBuffer(numParticles * 12);
        buf = getBuffer(VertexBuffer.Type.Color);
        if(buf != null) {
            buf.updateData(cb);
        } else {
            VertexBuffer vertexBuffer = new VertexBuffer(VertexBuffer.Type.Color);
            vertexBuffer.setupData(VertexBuffer.Usage.Stream, 4, VertexBuffer.Format.UnsignedByte, cb);
            vertexBuffer.setNormalized(true);
            setBuffer(vertexBuffer);
        }*/

        FloatBuffer nb = BufferUtils.createVector3Buffer(numParticles * 3);
        buf = getBuffer(VertexBuffer.Type.Normal);
        if(buf != null) {
            buf.updateData(nb);
        } else {
            VertexBuffer vertexBuffer = new VertexBuffer(VertexBuffer.Type.Normal);
            vertexBuffer.setupData(VertexBuffer.Usage.Stream, 3, VertexBuffer.Format.Float, nb);
            setBuffer(vertexBuffer);
        }


        FloatBuffer rampFactor = BufferUtils.createFloatBuffer(numParticles * 3);
        buf = getBuffer(VertexBuffer.Type.TexCoord6);
        if(buf != null) {
            buf.updateData(rampFactor);
        } else {
            VertexBuffer vertexBuffer = new VertexBuffer(VertexBuffer.Type.TexCoord6);
            vertexBuffer.setupData(VertexBuffer.Usage.Stream, 3, VertexBuffer.Format.Float, rampFactor);
            setBuffer(vertexBuffer);
        }

        //due to element size is limited from 1-4 each element contains one row of a matrix

        updateCounts();
    }

    public void updateParticleData(Particle[] particles) {

        VertexBuffer pvb = getBuffer(VertexBuffer.Type.Position);
        FloatBuffer positions = (FloatBuffer) pvb.getData();

        // no longer needed as described above
        /*VertexBuffer cvb = getBuffer(VertexBuffer.Type.Color);
        ByteBuffer colors = (ByteBuffer) cvb.getData();*/

        VertexBuffer nvb = getBuffer(VertexBuffer.Type.Normal);
        FloatBuffer normals = (FloatBuffer) nvb.getData();

        VertexBuffer tcb = getBuffer(VertexBuffer.Type.TexCoord6);
        FloatBuffer rampFactor = (FloatBuffer) tcb.getData();

        // update data in vertex buffer, position set to zero,limit is set to capacity
        positions.clear();
        //colors.clear();
        normals.clear();
        rampFactor.clear();

        //transform for our particle; one for each particle
        Transform particleTransform;

        Vector3f out = new Vector3f();

        for (Particle particle : particles){

            particleTransform = particle.transform;

            out = particleTransform.transformVector(m_particleModel[0], out);
            //for normal computation
            //System.out.println(out);

            positions.put(out.x )
                     .put(out.y)
                     .put(out.z);

            particleTransform.transformVector(m_particleModel[1], out);

            positions.put(out.x)
                     .put(out.y)
                     .put(out.z);


            out = particleTransform.transformVector(m_particleModel[2], out);

            positions.put(out.x)
                     .put(out.y)
                     .put(out.z);

            // no longer needed as described above
            /*int abgr = particle.color.asIntABGR();
            colors.putInt(abgr);
            colors.putInt(abgr);
            colors.putInt(abgr);
            */
            //calc normals

            //calculate worldspace normal
            //for we having static particle model with transformation applied every time
            //normal computation is simplified
            Vector4f normal = new Vector4f(0.0f, 1.0f, 0.0f, 0.0f);
            particleTransform.toTransformMatrix().mult(normal, normal);

            //end calc
            normals.put(normal.x)
                    .put(normal.y)
                    .put(normal.z);
            normals.put(normal.x)
                    .put(normal.y)
                    .put(normal.z);
            normals.put(normal.x)
                    .put(normal.y)
                    .put(normal.z);


            //calc ramp factor for each vertex
            float rampFac = ( particle.lifetime - particle.age) / particle.lifetime;
            rampFactor.put(rampFac)
                      .put(rampFac)
                      .put(rampFac);

        }

        positions.clear();
        //colors.clear();
        normals.clear();
        rampFactor.clear();

        // force renderer to re-send data to GPU
        pvb.updateData(positions);
        //cvb.updateData(colors);
        nvb.updateData(normals);
        tcb.updateData(rampFactor);

    }
}
