package edu.kit.valaris.generation;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Transform;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.*;
import com.jme3.scene.shape.Box;
import com.jme3.util.BufferUtils;
import edu.kit.valaris.datastructure.IMapBody;
import edu.kit.valaris.datastructure.RoadModelSegment;
import jme3tools.optimize.GeometryBatchFactory;
import java.util.Iterator;

public class RoadModelRenderer {

    private Node rootNode;
    private AssetManager assetManager;
    private Material material;

    public RoadModelRenderer(Node rootNode, AssetManager assetManager) {
        this.rootNode = rootNode;
        this.assetManager = assetManager;
        this.material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        material.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Off);
    }


    public void attachRoad(IMapBody mapBody) {
        Mesh mesh;

        Iterator<RoadModelSegment> it = mapBody.getRoad().iterator();

        RoadModelSegment lastRoadModelSegment;
        RoadModelSegment nextRoadModelSegment = it.next();


        Vector2f lastPosition = new Vector2f(0,0);
        Vector2f nextPosition = new Vector2f(0,0);

        float lenght;


        Vector2f xAxisLast = new Vector2f(1,0);
        Vector2f xAxisNext;

        Vector2f yAxisLast;
        Vector2f yAxisNext;

        float widthLast;
        float widthNext;


        Vector3f[] vertices;

        while(it.hasNext()) {

            lastRoadModelSegment = nextRoadModelSegment.clone();
            nextRoadModelSegment = it.next();

            lenght = lastRoadModelSegment.getLength();

            xAxisNext = new Vector2f(xAxisLast);
            xAxisNext.rotateAroundOrigin(nextRoadModelSegment.getAngle(),true);

            nextPosition.addLocal(xAxisLast.mult(lenght));

            yAxisLast = new Vector2f(xAxisLast);
            yAxisLast.rotateAroundOrigin((float)Math.PI/2f, false);
            yAxisLast.normalizeLocal();


            yAxisNext = new Vector2f(xAxisNext);
            yAxisNext.rotateAroundOrigin((float)Math.PI/2f, false);
            yAxisNext.normalizeLocal();

            widthLast = lastRoadModelSegment.getWidth();
            widthNext= nextRoadModelSegment.getWidth();

            mesh = new Mesh();

            int vertex_count = 4;
            int [] indexes = new int [6];


            indexes[0] = 0;
            indexes[1] = 1;
            indexes[2] = 2;
            indexes[3] = 2;
            indexes[4] = 3;
            indexes[5] = 0;

            float[] colorArray = new float[4*4];

            int colorIndex = 0;

            for (int i = 0; i < vertex_count; i++) {
                colorArray[colorIndex++] = 0.1f + (.05f * i);
                colorArray[colorIndex++] = 0.1f + (0.1f * i);
                colorArray[colorIndex++] = 1f;
                colorArray[colorIndex++] = 1.0f;
            }

            vertices = new Vector3f[]{
                    toVec3D(yAxisLast.mult(widthLast*(0.5f))),
                    toVec3D(yAxisNext.mult(widthNext*(0.5f)).add(nextPosition.subtract(lastPosition))),
                    toVec3D(yAxisNext.mult(widthNext*(-0.5f)).add(nextPosition.subtract(lastPosition))),
                    toVec3D(yAxisLast.mult(widthLast*(-0.5f)))
            };

            mesh.setBuffer(VertexBuffer.Type.Position, 3, BufferUtils.createFloatBuffer(vertices));
            mesh.setBuffer(VertexBuffer.Type.Index,    3, BufferUtils.createIntBuffer(indexes));
            mesh.setBuffer(VertexBuffer.Type.Color, 4, colorArray);



            Geometry geom = new Geometry("grid", mesh);

            geom.setLocalTranslation(toVec3D(lastPosition));
            material.setBoolean("VertexColor", true);
            mesh.updateBound();
            geom.setMaterial(material);

            rootNode.attachChild(geom);

            lastPosition = new Vector2f(nextPosition);
            xAxisLast = new Vector2f(xAxisNext);
        }

        GeometryBatchFactory.optimize(rootNode);
    }


    private Vector3f toVec3D(Vector2f vector2f) {
        return new Vector3f(vector2f.x, 0, -vector2f.y);
    }
}
