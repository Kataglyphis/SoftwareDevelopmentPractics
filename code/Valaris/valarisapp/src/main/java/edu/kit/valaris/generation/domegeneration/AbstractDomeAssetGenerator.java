package edu.kit.valaris.generation.domegeneration;

import com.jme3.light.Light;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.math.*;
import com.jme3.scene.*;
import com.jme3.util.BufferUtils;
import edu.kit.valaris.assets.AssetContainer;
import edu.kit.valaris.assets.AssetProvider;
import edu.kit.valaris.generation.AssetsUtility;
import edu.kit.valaris.generation.GenerationConfig;
import edu.kit.valaris.generation.GridVertex;
import edu.kit.valaris.generation.RandomNumberGenerator;
import edu.kit.valaris.generation.roadgeneration.Road;
import edu.kit.valaris.generation.roadgeneration.RoadCursor;

import java.util.ArrayList;
import java.util.Map;

/**
 * Defines and implements the minimum requirements of an AssetGenerator for a dome.
 * These are responsible for generating Object adapted to the landscape.
 *
 * @author Lukas Schölch
 */
public abstract class AbstractDomeAssetGenerator {

    private String m_paramPath;

    private GenerationConfig m_generationConfig;

    private AssetProvider m_assetProvider;


    /**
     * Constructor.
     * @param generationConfig Data structure that holds parameters for generation.
     */
    protected AbstractDomeAssetGenerator(GenerationConfig generationConfig, AssetProvider assetProvider) {
        m_paramPath = "domegeneration.AssetGenerator";
        m_generationConfig = generationConfig;
        m_assetProvider = assetProvider;

    }

    /**
     * Creates and places a roof (sphere) for a dome.
     * @param seed To generate pseudorandom values.
     * @param radius the radius of the dome (and sphere).
     * @param entrance the first roadCursor of the dome.
     * @param exit the last roadCursor of the dome.
     * @param yMin the lowest point in the dome.
     * @param yMax the highest point in the dome.
     * @param biom Indicates where in the JSON document the information about the current biome is available.
     * @param assetRootNode Node as used in JMonkey, where all objects are attached to the dome.
     */
    protected void generateSphere(int seed, int radius, RoadCursor entrance, RoadCursor exit, int yMin, int yMax, String biom, Material mat, Node assetRootNode) {

        RandomNumberGenerator numberGen = new RandomNumberGenerator(seed);
        Vector3f midPoint = new Vector3f(0, 0, radius);
        float angleBetweenPoints = m_generationConfig.getNumber(this.m_paramPath + biom + ".AngleBetweenWallPoints").floatValue();

        //get needed parameters
        Vector2f entranceWidthAndHight = entrance.getWidhtAndHightAndHight();

        Vector3f entranceLeftPoint = entrance.getPosition().subtract(entrance.getRight().mult(entranceWidthAndHight.x / 2));
        entranceLeftPoint.y = 0;
        Vector3f entranceRightPoint =  entrance.getPosition().add(entrance.getRight().mult(entranceWidthAndHight.x / 2));
        entranceRightPoint.y = 0;


        Vector2f exitWidthAndHight = exit.getWidhtAndHightAndHight();

        Vector3f exitLeftPoint = exit.getPosition().subtract(exit.getRight().mult(exitWidthAndHight.x / 2));
        exitLeftPoint.y = 0;
        Vector3f exitRightPoint = exit.getPosition().add(exit.getRight().mult(exitWidthAndHight.x / 2));
        exitRightPoint.y = 0;

        float entranceTop = entrance.getPosition().y + entrance.getWidhtAndHightAndHight().y;
        float exitTop = exit.getPosition().y + exit.getWidhtAndHightAndHight().y;

        yMax = (int) Math.max(yMax, Math.max(entranceTop, exitTop)) + 1;
        yMin = (int) Math.min(yMin, Math.min(entrance.getPosition().y, exit.getPosition().y)) - 1;




        //Calc angle between entrance and exit
        Vector2f entrancePointForAngle = new Vector2f(entranceLeftPoint.x, entranceLeftPoint.z);
        Vector2f exitPointForAngle = new Vector2f(exitLeftPoint.x, exitLeftPoint.z);
        entrancePointForAngle = entrancePointForAngle.subtract(new Vector2f(midPoint.x, midPoint.z)).normalizeLocal();
        exitPointForAngle = exitPointForAngle.subtract(new Vector2f(midPoint.x, midPoint.z)).normalizeLocal();


        float angleEntranceExit = entrancePointForAngle.normalize().smallestAngleBetween(exitPointForAngle.normalize());

        //check if exit right from entrance
        if (!this.checkAngle(new Vector3f(entrancePointForAngle.x, 0, entrancePointForAngle.y),
                new Vector3f(exitPointForAngle.x, 0, exitPointForAngle.y), angleEntranceExit)) {

            angleEntranceExit = (float) Math.PI * 2 - (angleEntranceExit);
        }

        int numberOfVertsHorizontal = Math.abs((int) (angleEntranceExit / angleBetweenPoints));

        //segment on entrance left
        float aktAngleBetweenPoints = angleEntranceExit / numberOfVertsHorizontal;
        Matrix3f m = new Matrix3f();
        m.fromAngleAxis(-aktAngleBetweenPoints, Vector3f.UNIT_Y);

        ArrayList<Vector3f> verts = new ArrayList<>();

        Vector3f pointToSet = new Vector3f(entrancePointForAngle.x, 0, entrancePointForAngle.y).normalizeLocal().multLocal(radius);
        this.placeVecs(pointToSet, m, yMin, yMax, numberOfVertsHorizontal, midPoint, verts);



        //segment on entrance right
        entrancePointForAngle = new Vector2f(entranceRightPoint.x, entranceRightPoint.z);
        entrancePointForAngle = entrancePointForAngle.subtract(new Vector2f(midPoint.x, midPoint.z)).normalizeLocal();
        exitPointForAngle = new Vector2f(exitRightPoint.x, exitRightPoint.z);
        exitPointForAngle = exitPointForAngle.subtract(new Vector2f(midPoint.x, midPoint.z)).normalizeLocal();


        angleEntranceExit = entrancePointForAngle.normalize().smallestAngleBetween(exitPointForAngle.normalize());

        System.out.println(entranceRightPoint + "------" + exitRightPoint);

        //check if exit left from entrance
        if (this.checkAngle(new Vector3f(entrancePointForAngle.x, 0, entrancePointForAngle.y),
                new Vector3f(exitPointForAngle.x, 0, exitPointForAngle.y), angleEntranceExit)) {

            angleEntranceExit = (float) Math.PI * 2 - (angleEntranceExit);
        }


        numberOfVertsHorizontal = Math.abs((int) (angleEntranceExit / angleBetweenPoints));
        aktAngleBetweenPoints = angleEntranceExit / numberOfVertsHorizontal;

        m = new Matrix3f();
        m.fromAngleAxis(aktAngleBetweenPoints, Vector3f.UNIT_Y);


        pointToSet = new Vector3f(entrancePointForAngle.x, 0, entrancePointForAngle.y).normalizeLocal().multLocal(radius);
        this.placeVecs(pointToSet, m, yMin, yMax, numberOfVertsHorizontal, midPoint, verts);






        //close the door hole OVER the entrance doorHeight
        float entranceAngle = entrancePointForAngle.smallestAngleBetween(new Vector2f(entranceLeftPoint.x, entranceLeftPoint.z)
                .subtract(new Vector2f(midPoint.x, midPoint.z)).normalizeLocal());
        numberOfVertsHorizontal = Math.abs((int) (entranceAngle / angleBetweenPoints));
        aktAngleBetweenPoints = entranceAngle / numberOfVertsHorizontal;

        m = new Matrix3f();
        m.fromAngleAxis(-aktAngleBetweenPoints, Vector3f.UNIT_Y);

        this.placeVecs(pointToSet, m, entranceTop, yMax, numberOfVertsHorizontal, midPoint, verts);


        //close the door hole UNDER the entrance doorHeight
        numberOfVertsHorizontal = Math.abs((int) (entranceAngle / angleBetweenPoints));
        aktAngleBetweenPoints = entranceAngle / numberOfVertsHorizontal;

        m = new Matrix3f();
        m.fromAngleAxis(-aktAngleBetweenPoints, Vector3f.UNIT_Y);

        this.placeVecs(pointToSet, m, entrance.getPosition().y, yMin, numberOfVertsHorizontal, midPoint, verts);


        //close the door hole OVER the exit doorHeight
        pointToSet = new Vector3f(exitPointForAngle.x, 0, exitPointForAngle.y).normalizeLocal().multLocal(radius);
        float exitAngle = exitPointForAngle.smallestAngleBetween(new Vector2f(exitLeftPoint.x, exitLeftPoint.z)
                .subtract(new Vector2f(midPoint.x, midPoint.z)).normalizeLocal());
        numberOfVertsHorizontal = Math.abs((int) (exitAngle / angleBetweenPoints));
        aktAngleBetweenPoints = -exitAngle / numberOfVertsHorizontal;

        m = new Matrix3f();
        m.fromAngleAxis(-aktAngleBetweenPoints, Vector3f.UNIT_Y);

        this.placeVecs(pointToSet, m, exitTop, yMax, numberOfVertsHorizontal, midPoint, verts);


        //close the door hole UNDER the exit doorHeight
        numberOfVertsHorizontal = Math.abs((int) (exitAngle / angleBetweenPoints));
        aktAngleBetweenPoints = -exitAngle / numberOfVertsHorizontal;

        m = new Matrix3f();
        m.fromAngleAxis(-aktAngleBetweenPoints, Vector3f.UNIT_Y);

        this.placeVecs(pointToSet, m, exit.getPosition().y, yMin, numberOfVertsHorizontal, midPoint, verts);




        //create Mesh for domeWall
        Vector3f[] vertices = new Vector3f[verts.size()];
        Vector3f[] normals = new Vector3f[verts.size()];
        float[] colorArray = this.calcColorArray(verts.size(), biom);
        vertices = verts.toArray(vertices);

        for (int i = 0; i < vertices.length; i += 3) {
            Vector3f normal = vertices[i].subtract(vertices[i + 1]).cross(vertices[i + 1].subtract(vertices[i + 2])).normalize();
            if (normal.dot(midPoint.subtract(vertices[i])) < 0) {
                normal.negateLocal();
                Vector3f puffVert = vertices[i + 1];
                vertices[i + 1] = vertices[i + 2];
                vertices[i + 2] = puffVert;
            }
            normals[i] = normal;
            normals[i + 1] = normal;
            normals[i + 2] = normal;
        }


        Mesh mesh = new Mesh();

        mesh.setBuffer(VertexBuffer.Type.Position, 3, BufferUtils.createFloatBuffer(vertices));
        mesh.setBuffer(VertexBuffer.Type.Normal, 3, BufferUtils.createFloatBuffer(normals));
        mesh.setBuffer(VertexBuffer.Type.Color, 4, BufferUtils.createFloatBuffer(colorArray));
        mesh.updateBound();

        Geometry geomWall = new Geometry("domeWall", mesh);
        geomWall.setMaterial(mat);


        //set roof
        Map<String, AssetContainer> possibleRoofs = m_assetProvider.provideGroup("DomeRoof");

        if (possibleRoofs == null) {
            System.out.println("did not find roof assets");
            return;
        }

        ArrayList<String> roofNames = new ArrayList<>();
        for (Map.Entry entry: possibleRoofs.entrySet()) {
            roofNames.add((String) entry.getKey());
        }
        int randomNumber = (int) ((roofNames.size()) * numberGen.random()) % roofNames.size();
        Spatial roof = possibleRoofs.get(roofNames.get(randomNumber)).newUsableInstance();
        roof.setLocalScale((float) radius / 10f);
        roof.setLocalTranslation(new Vector3f(0, yMax, radius));

        ArrayList<Light> lights = new ArrayList<>();
        AssetsUtility.getLightFromSpatial(roof, lights);

        for (Light light: lights) {
            if (light instanceof PointLight) {
                light.setColor(ColorRGBA.White.mult(12f));
                if (((PointLight) light).getPosition().x == 0 && ((PointLight) light).getPosition().z == 0) {
                    ((PointLight) light).setRadius(radius * 1.5f);

                }
            }
        }
        PointLight pointLight = new PointLight(new Vector3f(0, radius, radius),ColorRGBA.White.mult(16f), radius * 1.35f);
        assetRootNode.addLight(pointLight);


        assetRootNode.attachChild(geomWall);
        assetRootNode.attachChild(roof);

    }

    /**
     * Creats and places objects in a dome, for a specific biom.
     * @param seed To generate pseudorandom values.
     * @param grid The grid of an IDome Object.
     * @param assetRootNode Node as used in JMonkey, where all objects are attached to the dome.
     * @param biom Indicates where in the JSON document the information about the current biome is available.
     */
    protected abstract void placeTrees(int seed, GridVertex[][] grid, Node assetRootNode, String biom);

    /**
     * Places Road assets.
     * @param seed To generate pseudorandom values.
     * @param domeAssetRootNode Node as used in JMonkey, where all objects are attached to the dome.
     * @param road The Road that have to be visualized.
     * @param biom Indicates where in the JSON document the information about the current biome is available.
     */
    protected void placeRoadAssets(int seed, Node domeAssetRootNode, Road road, String biom) {
        String parentPath = "domegeneration.AssetGenerator" + biom + ".roadAsset";
        String roadAssetName = m_generationConfig.getChildren(parentPath)
                [seed % m_generationConfig.getChildren(parentPath).length];
        String roadAssetPath =  parentPath + "." + roadAssetName;
        String assetId = m_generationConfig.getString(roadAssetPath + ".Id");
        Spatial asset = m_assetProvider.provide(assetId);

        Vector3f assetDimensions = new Vector3f(
                m_generationConfig.getNumber(roadAssetPath + ".SizeX").floatValue(),
                m_generationConfig.getNumber(roadAssetPath + ".SizeY").floatValue(),
                m_generationConfig.getNumber(roadAssetPath + ".SizeZ").floatValue());
        int segmentsPerAsset = m_generationConfig.getNumber(roadAssetPath + ".Segments").intValue();
        domeAssetRootNode.attachChild(AssetsUtility.generateRoadAsset(road, asset, assetDimensions, segmentsPerAsset, false));
    }


    private float[] calcColorArray(int length, String biom) {
        float[] colorArray = new float[length * 4 * 3];

        float[] rgba = this.m_generationConfig.getFloatArray(this.m_paramPath + biom + ".RoofColor");

        int colorIndex = 0;
        for (int i = 0; i < length; i++) {
            for (int j = 0; j < 3; j++) {
                colorArray[colorIndex++] = rgba[0];
                colorArray[colorIndex++] = rgba[1];
                colorArray[colorIndex++] = rgba[2];
                colorArray[colorIndex++] = rgba[3];
            }
        }
        return colorArray;
    }

    private boolean checkAngle(Vector3f entracneVec, Vector3f exitVec, float angle) {

        Quaternion q = new Quaternion();
        q.fromAngleAxis(angle, Vector3f.UNIT_Y);

        Vector3f checkVec = q.mult(exitVec);

        return ((Math.abs(checkVec.x - entracneVec.x) < 0.01) && (Math.abs(checkVec.z - entracneVec.z) < 0.01));
    }

    private void placeVecs(Vector3f pointToSet, Matrix3f m, float yMin, float yMax, int numberOfVertsHorizontal,
                            Vector3f midPoint, ArrayList<Vector3f> verts) {

        Vector3f nextPointToSet = m.mult(pointToSet);
        for (int i = 0; i < numberOfVertsHorizontal; i++) {

            verts.add(new Vector3f(pointToSet.x, yMin, pointToSet.z).add(midPoint));
            verts.add(new Vector3f(nextPointToSet.x, yMin, nextPointToSet.z).add(midPoint));
            verts.add(new Vector3f(pointToSet.x, yMax, pointToSet.z).add(midPoint));

            verts.add(new Vector3f(nextPointToSet.x, yMin, nextPointToSet.z).add(midPoint));
            verts.add(new Vector3f(pointToSet.x, yMax, pointToSet.z).add(midPoint));
            verts.add(new Vector3f(nextPointToSet.x, yMax, nextPointToSet.z).add(midPoint));

            pointToSet = nextPointToSet;
            nextPointToSet = m.mult(nextPointToSet);
        }
    }

}
