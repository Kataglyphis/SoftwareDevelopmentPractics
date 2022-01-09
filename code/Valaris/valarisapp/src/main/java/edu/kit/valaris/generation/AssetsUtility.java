package edu.kit.valaris.generation;

import com.jme3.light.Light;
import com.jme3.light.LightList;
import com.jme3.light.PointLight;
import com.jme3.light.SpotLight;
import com.jme3.math.Vector3f;
import com.jme3.scene.*;
import com.jme3.util.BufferUtils;
import edu.kit.valaris.generation.roadgeneration.Road;
import edu.kit.valaris.generation.roadgeneration.RoadCursor;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;


/**
 * Class providing useful operations on assets.
 * @author Sidney Hansen
 */
public class AssetsUtility {

    /**
     * Recursively puts all meshes of a spatial or its children in the given {@link Collection}
     *
     * @param spatial the spatial which meshes are to be extracted
     * @param meshes the {@link Collection} of meshes
     */
    private static void getMeshsFromSpatial(Spatial spatial, Collection<Mesh> meshes) {
        if (spatial instanceof Node) {
            for (Spatial child : ((Node) spatial).getChildren()) {
                getMeshsFromSpatial(child, meshes);
            }
        } else if (spatial instanceof Geometry) {
            meshes.add(((Geometry) spatial).getMesh());
        }
    }

    /**
     * Recursively puts all lights of a spatial or its children in the given {@link Collection}
     *
     * @param spatial the spatial which lights are to be extracted
     * @param lights the {@link Collection} of lights
     */
    public static void getLightFromSpatial(Spatial spatial, Collection<Light> lights) {

        LightList localLightList = spatial.getLocalLightList();
        for (int i = 0; i < localLightList.size(); i++) {
            lights.add(localLightList.get(i));
        }

        if (spatial instanceof Node) {
            for (Spatial child : ((Node) spatial).getChildren()) {
                getLightFromSpatial(child, lights);
            }
        }
    }

    /**
     * Recursively iterates over scene graph and attaches all Lights to the rootNode
     *
     * @param rootNode rootNode where Lights are to be attached.
     * @param parentNode node to start the recursive iteration on.
     */
    public static void setLightToRoot(Node rootNode, Node parentNode) {


        LightList lightList = parentNode.getLocalLightList();

        Light light;
        Vector3f newDirection;
        Vector3f newPosition;


        for (int i = 0; i < lightList.size(); i++) {

            light = lightList.get(i);

            if (light instanceof SpotLight) {
                newDirection = parentNode.getWorldRotation().mult(((SpotLight) light).getDirection());
                newPosition = parentNode.getWorldRotation().mult(((SpotLight) light).getPosition().mult(parentNode.getWorldScale()))
                        .add(parentNode.getWorldTranslation());

                ((SpotLight) light).setDirection(newDirection);
                ((SpotLight) light).setPosition(newPosition);


                parentNode.removeLight(light);
                i--;
                rootNode.addLight(light);

            }

            if (light instanceof PointLight) {
                newPosition = parentNode.getWorldRotation().mult(((PointLight) light).getPosition().mult(parentNode.getWorldScale()))
                        .add(parentNode.getWorldTranslation());

                ((PointLight) light).setPosition(newPosition);

                parentNode.removeLight(light);
                i--;
                rootNode.addLight(light);
            }
        }

        for (Spatial child : parentNode.getChildren()) {
            if (child instanceof Node) {
                setLightToRoot(rootNode, (Node) child);
            }
        }
    }

    /**
     * Transforms the given {@link Spatial} along all {@link RoadCursor} in the given {@link Road}
     * between the startIndex and endIndex.
     *
     * @param road the road on which to be scaled along
     * @param startIndex the start index
     * @param endIndex the end index
     * @param asset the asset to be scaled
     * @param assetDimensions the dimensions off the asset
     * @param scaleOnY if the asset is to be scaled on its Y-Axis
     */
    private static void tranformAssetAlongRoadCursors(Road road, int startIndex,
                                                 int endIndex, Spatial asset, Vector3f assetDimensions, boolean scaleOnY) {
        int cursorCount = endIndex - startIndex;

        float assetLength = assetDimensions.z;

        Collection<Mesh> meshes = new LinkedList<>();
        Collection<Light> lights = new LinkedList<>();

        getMeshsFromSpatial(asset, meshes);

        getLightFromSpatial(asset, lights);


        float coef;
        RoadCursor roadCursorA;
        RoadCursor roadCursorB;
        int cursorIndex;
        Vector3f[] vertices;
        Vector3f lightPosition;
        Vector3f newLightPosition;

        for (Light light : lights) {
            if (light instanceof PointLight) {
                lightPosition = ((PointLight) light).getPosition();

                cursorIndex = (int)(Math.min(cursorCount-1, Math.abs(lightPosition.z / assetLength) * cursorCount));
                coef = ((lightPosition.z / assetLength) * cursorCount) - cursorIndex;
                roadCursorA = road.getRoadCursors().get(startIndex + cursorIndex);
                roadCursorB = road.getRoadCursors().get(Math.min(endIndex, startIndex + cursorIndex + 1));

                newLightPosition =
                        calcVertexFromRoadCursors(roadCursorA, roadCursorB, coef, lightPosition, assetDimensions, scaleOnY);

                ((PointLight) light).setPosition(newLightPosition);

            } else if (light instanceof SpotLight) {
                lightPosition = ((SpotLight) light).getPosition();

                cursorIndex = (int)(Math.min(cursorCount-1, Math.abs(lightPosition.z / assetLength) * cursorCount));
                coef = ((lightPosition.z / assetLength) * cursorCount) - cursorIndex;
                roadCursorA = road.getRoadCursors().get(startIndex + cursorIndex);
                roadCursorB = road.getRoadCursors().get(Math.min(endIndex, startIndex + cursorIndex + 1));

                newLightPosition =
                        calcVertexFromRoadCursors(roadCursorA, roadCursorB, coef, lightPosition, assetDimensions, scaleOnY);

                ((SpotLight) light).setPosition(newLightPosition);
            }


        }

        for (Mesh mesh :
                meshes) {

            vertices = getVerticesFromMesh(mesh);

            Vector3f vTemp;
            Vector3f newVertex;


            for (int i = 0; i < vertices.length; i++) {
                vTemp = new Vector3f(vertices[i]);

                cursorIndex = (int)(Math.min(cursorCount-1, Math.abs(vTemp.z / assetLength) * cursorCount));
                coef = (Math.abs(vTemp.z / assetLength) * cursorCount)-cursorIndex;


                roadCursorA = road.getRoadCursors().get(startIndex + cursorIndex);
                roadCursorB = road.getRoadCursors().get(Math.min(endIndex, startIndex + cursorIndex + 1));

                newVertex = calcVertexFromRoadCursors(roadCursorA, roadCursorB, coef, vTemp, assetDimensions, scaleOnY);

                vertices[i] = newVertex;
            }

            mesh.setBuffer(VertexBuffer.Type.Position, 3, BufferUtils.createFloatBuffer(vertices));
            mesh.updateBound();
        }
    }


    /**
     * Returns a array of vertices of given {@link Mesh},
     *
     * @param mesh the mesh
     * @return array of vertices
     */
    private static Vector3f[] getVerticesFromMesh(Mesh mesh) {
        VertexBuffer vB = mesh.getBuffer(VertexBuffer.Type.Position);
        Vector3f[] vertices = new Vector3f[vB.getNumElements()];

        if (vB.getNumComponents() != 3) {
            throw new RuntimeException("Position Buffer doesn't have 3-component Vectors but " + vB.getNumComponents());
        }

        FloatBuffer buf = (FloatBuffer) vB.getData();
        Vector3f vTemp = new Vector3f();

        for (int i = 0; i < vB.getNumElements(); i++) {
            vTemp.set(buf.get(i * 3), buf.get(i * 3 + 1), buf.get(i * 3 + 2));
            vertices[i] = new Vector3f(vTemp);
        }
        return vertices;
    }


    /**
     * Interpolates the vertex, using 2 {@link RoadCursor} and a coefficient.
     *
     * @param roadCursorA first RoadCursor
     * @param roadCursorB second RoadCursor
     * @param coef the coefficient
     * @param vertex the original vertexPosition
     * @param assetDimensions the dimensions of the model this vertex belongs to.
     * @param scaleOnY if the vertex is to be scaled on Y taking to account the hight of
     *                 the road in the given {@link RoadCursor}s
     * @return the new vertex
     */
    private static Vector3f calcVertexFromRoadCursors(RoadCursor roadCursorA, RoadCursor roadCursorB,
                                                      float coef, Vector3f vertex, Vector3f assetDimensions, boolean scaleOnY) {

        float distance = roadCursorB.getPosition().subtract(roadCursorA.getPosition()).length();
        Vector3f vertexA = roadCursorA.getPosition()
                .add(roadCursorA.getDirection().mult(distance * (coef)/4))
                .add(roadCursorA.getRight().mult((-roadCursorA.getWidhtAndHightAndHight().x / assetDimensions.x) * vertex.x));
        if (scaleOnY) {
            vertexA.addLocal(roadCursorA.getNormal().mult((roadCursorA.getWidhtAndHightAndHight().y / assetDimensions.y) * vertex.y));
        } else {
            vertexA.addLocal(roadCursorA.getNormal().mult(vertex.y));
        }

        Vector3f vertexB = roadCursorB.getPosition()
                .add(roadCursorB.getDirection().mult(-distance * (1 - coef)/4))
                .add(roadCursorB.getRight().mult((-roadCursorB.getWidhtAndHightAndHight().x / assetDimensions.x) * vertex.x));
        if (scaleOnY) {
            vertexB.addLocal(roadCursorB.getNormal().mult((roadCursorB.getWidhtAndHightAndHight().y / assetDimensions.y) * vertex.y));
        } else {
            vertexB.addLocal(roadCursorB.getNormal().mult(vertex.y));
        }

        Vector3f newVertex = new Vector3f(vertexA);
        float interpolationPower = 2;
        float interpolationValue = (float) ((coef <= 0.5)
                ?( Math.pow(2*coef,interpolationPower)/2f)
                : 1-( Math.pow(2*(1-coef),interpolationPower)/2f));
        newVertex.interpolateLocal(vertexB, interpolationValue);
        return newVertex;
    }


    /**
     * Interpolates the vertex, using 2 {@link RoadCursor} and a coefficient.
     *
     * @param roadCursor the {@link RoadCursor}
     * @param vertex the original vertexPosition
     * @param assetDimensions the dimensions of the model this vertex belongs to.
     * @param scaleOnY if the vertex is to be scaled on Y taking to account the hight of
     *                 the road in the given {@link RoadCursor}s
     * @return the new vertex
     */
    private static Vector3f calcVertexFromRoadCursor(RoadCursor roadCursor, Vector3f vertex,
                                                     Vector3f assetDimensions, boolean scaleOnY) {
        Vector3f newVertex = roadCursor.getPosition()
                .add(roadCursor.getDirection().mult(vertex.z / assetDimensions.z))
                .add(roadCursor.getRight().mult((-roadCursor.getWidhtAndHightAndHight().x / assetDimensions.x) * vertex.x));
        if (scaleOnY) {
            newVertex.addLocal(roadCursor.getNormal().mult((roadCursor.getWidhtAndHightAndHight().y / assetDimensions.y) * vertex.y));
        } else {
            newVertex.addLocal(roadCursor.getNormal().mult(vertex.y));
        }
        return new Vector3f(newVertex);
    }

    /**
     * Transforms the given {@link Spatial} along all {@link RoadCursor} in the given {@link Road}
     * between the startIndex and endIndex.
     * @param roadCursor the {@link RoadCursor}
     * @param asset the asset to be scaled
     * @param assetDimensions the dimensions off the asset
     * @param scaleOnY if the asset is to be scaled on its Y-Axis
     */
    public static Node tranformAssetAlongRoadCursor(RoadCursor roadCursor, Spatial asset,
                                                     Vector3f assetDimensions, boolean scaleOnY) {
        Spatial assetClone = asset.deepClone();
        Node doorRootNode = new Node("doorRootNode");
        Collection<Mesh> meshes = new LinkedList<>();
        Collection<Light> lights = new LinkedList<>();
        getMeshsFromSpatial(assetClone, meshes);
        getLightFromSpatial(assetClone, lights);
        Vector3f[] vertices;
        Vector3f lightPosition;
        Vector3f newLightPosition;

        for (Light light : lights) {
            if (light instanceof PointLight) {
                lightPosition = ((PointLight) light).getPosition();

                newLightPosition =
                        calcVertexFromRoadCursor(roadCursor, lightPosition, assetDimensions, scaleOnY);
                ((PointLight) light).setPosition(newLightPosition);

            } else if (light instanceof SpotLight) {
                lightPosition = ((SpotLight) light).getPosition();
                newLightPosition =
                        calcVertexFromRoadCursor(roadCursor, lightPosition, assetDimensions, scaleOnY);
                ((SpotLight) light).setPosition(newLightPosition);
            }
        }
        for (Mesh mesh :
                meshes) {
            vertices = getVerticesFromMesh(mesh);
            Vector3f vTemp;
            Vector3f newVertex;
            for (int i = 0; i < vertices.length; i++) {
                vTemp = new Vector3f(vertices[i]);
                newVertex = calcVertexFromRoadCursor(roadCursor, vTemp, assetDimensions, scaleOnY);
                vertices[i] = newVertex;
            }
            mesh.setBuffer(VertexBuffer.Type.Position, 3, BufferUtils.createFloatBuffer(vertices));
            mesh.updateBound();
        }
        doorRootNode.attachChild(assetClone);
        return doorRootNode;
    }




    /**
     * Generates a representation of the road using the given asset
     * and scaling it over a given number of segments, repeating this for the entire road.
     *
     * @param road the given road
     * @param asset the asset to be used
     * @param assetDimensions dimension of the asset
     * @param segmentsPerAsset number of segments every asset is to be stretched
     * @param scaleOnY if asset is to be scaled on the Y-Axis
     * @return
     */
    public static Node generateRoadAsset(Road road, Spatial asset, Vector3f assetDimensions, int segmentsPerAsset, boolean scaleOnY) {


        ArrayList<RoadCursor> roadCursors = road.getRoadCursors();

        Node roadAssetRootNode = new Node("roadAssetRootNode");

        int currentIndex = 0;
        int nextIndex;
        Spatial assetCopy;


        while (currentIndex < roadCursors.size() - 1) {

            nextIndex = Math.min(currentIndex + segmentsPerAsset,roadCursors.size() - 1);
            assetCopy = asset.deepClone();
            tranformAssetAlongRoadCursors(road, currentIndex, nextIndex, assetCopy, assetDimensions, scaleOnY);

            roadAssetRootNode.attachChild(assetCopy);

            currentIndex = nextIndex;
        }
        return roadAssetRootNode;
    }


    /**
     * Transforms a rectangular asset of given dimension, to match the quad,
     * defined by the given points.
     *
     * @param pointA first point of the quad
     * @param pointB second point of the quad
     * @param pointC third point of the quad
     * @param pointD fourth point of the quad
     * @param asset the asset to be used
     * @param assetDimensions the dimensions of the asset
     * @return the transformed asset.
     */
    public static Spatial transformBetweenPoints(Vector3f pointA, Vector3f pointB,
                                                 Vector3f pointC, Vector3f pointD,
                                                 Spatial asset, Vector3f assetDimensions) {

        Collection<Mesh> meshes = new ArrayList<>();
        Collection<Light> lights = new LinkedList<>();

        Spatial transformedAsset = asset.deepClone();

        getLightFromSpatial(transformedAsset, lights);
        Vector3f lightPosition;
        Vector3f newLightPosition;

        for (Light light : lights) {
            if (light instanceof PointLight) {
                lightPosition = ((PointLight) light).getPosition();

                newLightPosition =
                        calcVertexFromPoints(lightPosition, pointA, pointB,
                                pointC, pointD, assetDimensions);
                ((PointLight) light).setPosition(newLightPosition);

            } else if (light instanceof SpotLight) {
                lightPosition = ((SpotLight) light).getPosition();
                newLightPosition =
                        calcVertexFromPoints(lightPosition, pointA, pointB,
                                pointC, pointD, assetDimensions);
                ((SpotLight) light).setPosition(newLightPosition);
            }
        }

        getMeshsFromSpatial(transformedAsset, meshes);

        Vector3f[] vertices;

        for (Mesh mesh :
                meshes) {
            vertices = getVerticesFromMesh(mesh);


            for (int i = 0; i < vertices.length; i++) {
                vertices[i] = calcVertexFromPoints(vertices[i], pointA, pointB,
                        pointC, pointD, assetDimensions);
            }

            mesh.setBuffer(VertexBuffer.Type.Position, 3, BufferUtils.createFloatBuffer(vertices));
            mesh.updateBound();
        }

        return transformedAsset;
    }

    /**
     *
     * Interpolates the vertex using 4 points in 3D space.
     *
     * @param vertex the vertex
     * @param pointA the firstPoint
     * @param pointB the secondPoint
     * @param pointC the thirdPoint
     * @param pointD the fourthPoint
     * @param assetDimensions the dimensions of the model this vertex belongs to
     * @return the transformed vertex
     */
    private static Vector3f calcVertexFromPoints(Vector3f vertex, Vector3f pointA, Vector3f pointB,
                                          Vector3f pointC, Vector3f pointD, Vector3f assetDimensions) {

        float relativeX = (vertex.x + (assetDimensions.x / 2f)) / assetDimensions.x;
        float relativeZ = (vertex.z + (assetDimensions.z / 2f)) / assetDimensions.z;

        float reverseRelativeX = 1 - relativeX;
        float reverseRelativeZ = 1 - relativeZ;


        Vector3f normal = pointA.subtract(pointD).normalize()
                .cross(pointC.subtract(pointD).normalize()).normalize();

        /**
         *
         *      Z +
         *      |
         *      |
         *      O-----X +
         *
         *    (-1, 1)       (1, 1)
         *    Point C ----- Point B
         *      |             |
         *      |             |
         *      |     (0,0)   |
         *      |             |
         *      |             |
         *   Point D ------ Point A
         *   (-1, -1)       (1, -1)
         *
         */


        Vector3f newVertex = new Vector3f()
                .add(pointA.mult(relativeX * reverseRelativeZ))
                .add(pointB.mult(relativeX * relativeZ))
                .add(pointC.mult(reverseRelativeX * relativeZ))
                .add(pointD.mult(reverseRelativeX * reverseRelativeZ));

        newVertex.addLocal(normal.mult(-vertex.y * assetDimensions.y));

        return newVertex;

    }

}
