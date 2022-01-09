package edu.kit.valaris.generation.domegeneration;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import edu.kit.valaris.assets.AssetContainer;
import edu.kit.valaris.assets.AssetControl;
import edu.kit.valaris.assets.AssetProvider;
import edu.kit.valaris.generation.GenerationConfig;
import edu.kit.valaris.generation.GridVertex;
import edu.kit.valaris.generation.GridVertexProperty;
import edu.kit.valaris.generation.RandomNumberGenerator;
import jme3tools.optimize.GeometryBatchFactory;
import umontreal.ssj.hups.HammersleyPointSet;

import java.util.ArrayList;
import java.util.Map;

/**
 * Extends AbstractDomeAssetGenerator in which the creation of objects is implemented here in a concrete strategy.
 *
 * @author Lukas Schölch
 */
public class DomeAssetGenerator extends AbstractDomeAssetGenerator {

    private String m_paramPath;

    private GenerationConfig m_generationConfig;

    private AssetProvider m_assetProvider;

    private IColoring m_coloring;


    /**
     * Constructor.
     * @param generationConfig Data structure that holds parameters for generation.
     */
    public DomeAssetGenerator(GenerationConfig generationConfig, AssetProvider assetProvider, IColoring coloring) {
        super(generationConfig, assetProvider);
        m_paramPath = "domegeneration.AssetGenerator";
        m_generationConfig = generationConfig;
        m_assetProvider = assetProvider;
        this.m_coloring = coloring;

    }

    @Override
    protected void placeTrees(int seed, GridVertex[][] grid, Node assetRootNode, String biom) {

        RandomNumberGenerator numberGen = new RandomNumberGenerator(seed);
        Node treeNode = new Node();
        //Get all possible Trees for the biome
        Map<String, AssetContainer> possibleTrees = m_assetProvider.provideGroup(biom + "Trees");
        if (possibleTrees == null) {
            return;
        }
        //get parameters from the GenerationConfig
        float treesPerSquareMeter = m_generationConfig.getNumber(m_paramPath + biom + ".treesPerSquareMeter").floatValue();
        int maxNumberOfTrees = Math.round(treesPerSquareMeter * grid.length * grid.length);
        int minDistance = m_generationConfig.getNumber(m_paramPath + biom + ".minDistanceToTree").intValue();
        float degreeOfClustering = m_generationConfig.getNumber(m_paramPath + biom + ".degreeOfClustering").floatValue();
        float maxAdditionalTreeScale = m_generationConfig.getNumber(m_paramPath + biom + ".maxAdditionalTreeScale").floatValue();
        float minAdditionalScale = m_generationConfig.getNumber(m_paramPath + biom + ".minAdditionalScale").floatValue();

        if (possibleTrees == null) {
            System.out.println("did not find tree assets");
            return;
        }

        ArrayList<String> treeNames = new ArrayList<>();
        for (Map.Entry entry: possibleTrees.entrySet()) {
            treeNames.add((String) entry.getKey());
        }

        //HammersleySet for a even distribution of the trees
        HammersleyPointSet pointSet = new HammersleyPointSet(maxNumberOfTrees, 2);
        float[][] density = new float[grid.length][grid.length];

        for (int x = 1; x < grid.length - 1; x++) {
            for (int z = 1; z < grid.length - 1; z++) {


                //Calculate density (for trees) based on slope
                if (grid[x + 1][z] != null && grid[x][z + 1] != null && grid[x + 1][z + 1] != null
                        && grid[x - 1][z] != null && grid[x][z - 1] != null && grid[x - 1][z - 1] != null
                        && grid[x - 1][z + 1] != null && grid[x + 1][z - 1] != null && grid[x][z] != null) {

                    Vector3f vec1 = new Vector3f(0, grid[x][z + 1].getPosition().y - grid[x][z].getPosition().y, 1);
                    Vector3f vec2 = new Vector3f(-1, grid[x - 1][z].getPosition().y - grid[x][z].getPosition().y, 0);
                    Vector3f vec3 = new Vector3f(0, grid[x][z - 1].getPosition().y - grid[x][z].getPosition().y, -1);
                    Vector3f vec4 = new Vector3f(1, grid[x + 1][z].getPosition().y - grid[x][z].getPosition().y, 0);

                    Vector3f ergVec1 = vec1.cross(vec2).normalize();
                    Vector3f ergVec2 = vec2.cross(vec3).normalize();
                    Vector3f ergVec3 = vec3.cross(vec4).normalize();
                    Vector3f ergVec4 = vec4.cross(vec1).normalize();

                    Vector3f normal = ergVec1.add(ergVec2).add(ergVec3).add(ergVec4);
                    normal = normal.divide(4);
                    density[x][z] = normal.dot(Vector3f.UNIT_Y);
                }
            }
        }


        //get all maxDensity positions
        ArrayList<Vector3f> vecsOfDensityMaxs = new ArrayList<>();

        for (int x = 1; x < density.length - 1; x++) {
            for (int z = 1; z < density.length - 1; z++) {
                if (grid[x][z] != null && grid[x + 1][z] != null && grid[x][z + 1] != null && grid[x + 1][z + 1] != null
                        && grid[x - 1][z] != null && grid[x][z - 1] != null && grid[x - 1][z - 1] != null
                        && grid[x - 1][z + 1] != null && grid[x + 1][z - 1] != null) {
                    if (density[x][z] >= density[x + 1][z]
                            && density[x][z] >= density[x][z + 1]
                            && density[x][z] >= density[x - 1][z]
                            && density[x][z] >= density[x][z - 1]
                            && grid[x][z] != null
                            && !this.isToCloseToRoad(grid, x, z, minDistance)) {
                        vecsOfDensityMaxs.add(new Vector3f(x, 0, z));
                    }
                }
            }
        }

        ArrayList<Vector3f> treePositions = new ArrayList<>();


        for (int i = 0; i < maxNumberOfTrees; i++) {

            float xCoord = (float) pointSet.getCoordinate(i, 0) * grid.length;
            float zCoord = (float) pointSet.getCoordinate(i, 1) * grid.length;

            if (grid[(int) xCoord][(int) zCoord] != null) {

                Vector3f pos = new Vector3f(xCoord, 0, zCoord);
                Vector3f toAdd = new Vector3f(0, 0, 0);


                if (vecsOfDensityMaxs.size() > 0) {

                    //Compute displacement for current tree
                    float shortestRange = vecsOfDensityMaxs.get(0).subtract(pos).length();
                    for (Vector3f max: vecsOfDensityMaxs) {
                        Vector3f newAdd = max.subtract(pos);
                        if (newAdd.length() < shortestRange) {
                            shortestRange = newAdd.length();
                        }
                        newAdd = newAdd.normalize().divide(1 + newAdd.length());
                        toAdd.addLocal(newAdd);
                    }

                    pos = pos.add(toAdd.mult(shortestRange * degreeOfClustering));


                } else {
                    System.out.println("No densityMax");
                }

                //check if treePos is valid
                if ((int) pos.x >= 0 && (int) pos.z >= 0 && (int) pos.x < grid.length && (int) pos.z < grid.length
                        && grid[(int) pos.x][(int) pos.z] != null
                        && !this.isToCloseToRoad(grid, (int) pos.x, (int) pos.z, minDistance)
                        && !this.isToCloseToWall(grid, (int) pos.x, (int) pos.z, minDistance)) {

                    pos.y = grid[(int) pos.x][(int) pos.z].getPosition().y;
                    pos.x -= (float) grid.length / 2;
                    treePositions.add(pos);
                }
            }
        }

        for (Vector3f treePos: treePositions) {

            if (!this.isToCloseToTree(treePositions, treePos, minDistance)) {
                //choose one tree for akt treePos
                int randomNumber = (int) ((treeNames.size()) * numberGen.random()) % treeNames.size();
                Spatial tree = possibleTrees.get(treeNames.get(randomNumber)).newUsableInstance();

                //scale all the tree to the right dimensions
                float additionalScale = minAdditionalScale + numberGen.random() * maxAdditionalTreeScale;
                AssetControl treeControl = tree.getControl(AssetControl.class);
                float scale = Float.parseFloat(treeControl.getMetadata("scale")) + additionalScale;
                tree.scale(scale);

                //set leaveColors
                Material leaveMat = ((Geometry)treeControl.getSubNode("Leaves")).getMaterial();
                float[] treeColors = m_coloring.makeTreeColor(numberGen.getNewSeed());
                leaveMat.setColor("BaseColor", new ColorRGBA(treeColors[0], treeColors[1], treeColors[2], treeColors[3]));

                tree.setLocalTranslation(treePos);
                treeNode.attachChild(tree);
            }
        }

//        treeNode.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
        GeometryBatchFactory.optimize(treeNode);
        assetRootNode.attachChild(treeNode);
    }



    private boolean isToCloseToRoad(GridVertex[][] grid, int x, int z, int minDistance) {

        for (int a = -minDistance; a <= minDistance; a++) {
            for (int b = -minDistance; b <= minDistance; b++) {
                if (x + a >= 0 && x + a < grid.length && z + b >= 0 && z + b < grid.length) {

                    if (grid[x + a][z + b] != null &&
                            grid[x + a][z + b].hasProperty(GridVertexProperty.road)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean isToCloseToTree(ArrayList<Vector3f> treePositions, Vector3f aktTreePos, int minDistance) {

        for (int a = -minDistance * 2; a <= minDistance * 2; a++) {
            for (int b = -minDistance * 2; b <= minDistance * 2; b++) {

                if (a != 0 && b != 0) {
                    for (Vector3f otherTreePos: treePositions) {

                        if ((int) aktTreePos.x + a == (int) otherTreePos.x && (int) aktTreePos.z + b == (int) otherTreePos.z) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean isToCloseToWall(GridVertex[][] grid, int x, int z, int minDistance) {

        for (int a = -minDistance; a <= minDistance; a++) {
            for (int b = -minDistance; b <= minDistance; b++) {
                if (x + a < 0 || x + a >= grid.length || z + b < 0 || z + b >= grid.length) {
                    return true;
                }
            }
        }
        return false;
    }
}