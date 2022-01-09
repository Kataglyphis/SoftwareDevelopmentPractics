package edu.kit.valaris.generation.domegeneration;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import edu.kit.valaris.assets.AssetProvider;
import edu.kit.valaris.generation.GenerationConfig;
import edu.kit.valaris.generation.GeneratorSettings;
import edu.kit.valaris.generation.GridVertex;
import edu.kit.valaris.generation.GridVertexProperty;
import edu.kit.valaris.generation.RandomNumberGenerator;
import edu.kit.valaris.generation.roadgeneration.IRoadGenerator;
import edu.kit.valaris.generation.roadgeneration.Road;
import edu.kit.valaris.generation.roadgeneration.RoadCursor;
import edu.kit.valaris.generation.roadgeneration.RoadGenerator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

/**
 * Implements IDomeGenerator. Generates Dome - objects.
 *
 * @author Lukas Schölch
 */
public class DomeGenerator implements IDomeGenerator {

    private GenerationConfig m_generationConfig;

    private final String m_paramPath;

    private AssetManager m_assetManager;

    private IRoadGenerator m_roadGen;

    private AbstractNoiseGenerator m_noiseGen;

    private AbstractDomeAssetGenerator m_assetGen;

    private AssetProvider m_assetProvider;

    /**
     * Constructor.
     * @param generationConfig Data structure that holds parameters for generation.
     * @param assetManager To manage assets.
     */
    public DomeGenerator(GenerationConfig generationConfig, AssetManager assetManager, AssetProvider assetProvider) {
        this.m_generationConfig = generationConfig;
        this.m_paramPath = "domegeneration";
        this.m_assetManager = assetManager;
        this.m_assetProvider = assetProvider;

    }

    @Override
    public IDome generateDome(int seed, int sphereRadius, Vector2f entryWidth, float exitAngle, Set<GeneratorSettings> flags) {

        RandomNumberGenerator numberGen = new RandomNumberGenerator(seed);


        //determine biom
        String[] possibleBioms = m_generationConfig.getStringArray(m_paramPath + ".PossibleBioms");
        int randomNumber = (int) ((possibleBioms.length) * numberGen.random()) % possibleBioms.length;
        String biom = "." + possibleBioms[randomNumber];

        //get params from .json
        float maxSecondDerivationDeltaForLandscapeResolution = this.m_generationConfig.getNumber(this.m_paramPath
                + ".LandscapeParameters" + biom + ".maxSecondDerivationDeltaForLandscapeResolution").floatValue();
        String roadSettings = this.m_generationConfig.getString(this.m_paramPath + ".LandscapeParameters" + biom + ".RoadSetting");


        IColoring coloring = new BiomMeadowColoring(numberGen.getNewSeed(), m_generationConfig, m_paramPath, biom);

        this.m_roadGen = new RoadGenerator(this.m_generationConfig);
        this.m_noiseGen = new OpenSimplexNoise(numberGen.getNewSeed(), this.m_generationConfig);
        this.m_assetGen = new DomeAssetGenerator(m_generationConfig, m_assetProvider, coloring);



        //create grid, road and the landscape
        GridVertex[][] grid = generateGrid(sphereRadius);

        Road road = m_roadGen.generateSphereRoad(numberGen.getNewSeed(), sphereRadius, roadSettings, entryWidth, exitAngle, flags);
        if (road == null) {
            //should not be used, but safety first :)
            return null;
        }

        m_noiseGen.makeNoise(grid, biom);

        this.makeRoadGround(road, grid);

        this.setUnderground(grid, biom);


        //place assets
        Node domeAssetRootNode = new Node();
        int[] yMinMax = this.calcYminAndYmax(grid);

        Material mat = new Material(this.m_assetManager, "edu.kit.valaris/materials/PBRLighting.j3md");
        mat.setBoolean("UseColorAttribute", true);
        mat.setBoolean("UseRoughnessAttribute", true);
        mat.setBoolean("UseSpecularAttribute", true);
        mat.setFloat("Metallic", 0f);
        mat.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Back);


        m_assetGen.generateSphere(seed, sphereRadius, road.getFirstRoadCursor(), road.getLastRoadCursor(),
                yMinMax[0], yMinMax[1], biom, mat, domeAssetRootNode);
        m_assetGen.placeRoadAssets(seed, domeAssetRootNode, road, biom);
        m_assetGen.placeTrees(seed, grid, domeAssetRootNode, biom);


        return new Dome(grid, maxSecondDerivationDeltaForLandscapeResolution, road, domeAssetRootNode, mat, coloring);
    }




    private GridVertex[][] generateGrid(int sphereRadius) {
        GridVertex[][] grid = new GridVertex[2 * sphereRadius][2 * sphereRadius];
        GridVertex vertex;
        Vector3f midVec = new Vector3f(sphereRadius, 0, sphereRadius);

        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid.length; j++) {
                Vector3f vec = new Vector3f(i - grid.length / 2, 0, j);
                if ((new Vector3f(i, 0, j).subtract(midVec).length()) <= sphereRadius) {
                    vertex = new GridVertex(vec, i, 0, j);
                    grid[i][j] = vertex;
                }
            }
        }
        return grid;
    }


    private void setUnderground(GridVertex[][] grid, String biom) {

        float minPitchForRock = m_generationConfig.getNumber(m_paramPath + ".LandscapeParameters" + biom + ".MinPitchForRock").floatValue();
        float minHeightForSnow = m_generationConfig.getNumber(m_paramPath + ".LandscapeParameters" + biom + ".MinHeightForSnow").floatValue();

        for (int x = 0; x < grid.length - 1; x++) {
            for (int z = 0; z < grid[x].length - 1; z++) {

                if (grid[x][z] != null) {
                    grid[x][z].setProperty(GridVertexProperty.rock);
                }

                if (grid[x + 1][z] != null && grid[x + 1][z + 1] != null && grid[x][z] != null && grid[x][z + 1] != null
                        && grid[x - 1][z] != null && grid[x - 1][z - 1] != null && grid[x][z] != null && grid[x][z - 1] != null
                        && grid[x + 1][z - 1] != null && grid[x - 1][z + 1] != null) {


                    //check 1. derivation in all directions
                    if (Math.abs(grid[x][z].getPosition().y - (grid[x + 1][z + 1].getPosition().y)) < minPitchForRock
                            && Math.abs(grid[x][z].getPosition().y - (grid[x + 1][z].getPosition().y)) < minPitchForRock
                            && Math.abs(grid[x][z].getPosition().y - (grid[x][z + 1].getPosition().y)) < minPitchForRock
                            && Math.abs(grid[x][z].getPosition().y - (grid[x - 1][z - 1].getPosition().y)) < minPitchForRock
                            && Math.abs(grid[x][z].getPosition().y - (grid[x - 1][z].getPosition().y)) < minPitchForRock
                            && Math.abs(grid[x][z].getPosition().y - (grid[x][z - 1].getPosition().y)) < minPitchForRock
                            && Math.abs(grid[x][z].getPosition().y - (grid[x + 1][z - 1].getPosition().y)) < minPitchForRock
                            && Math.abs(grid[x][z].getPosition().y - (grid[x - 1][z + 1].getPosition().y)) < minPitchForRock) {

                        grid[x][z].setProperty(GridVertexProperty.meadow);

                        if (grid[x][z].getPosition().y >= minHeightForSnow) {
                            grid[x][z].setProperty(GridVertexProperty.snow);
                        }
                    }
                }
            }
        }
    }


    private void makeRoadGround(Road road, GridVertex[][] grid) {

        Iterator<RoadCursor> it = road.getRoadCursorIterator();
        ArrayList<BoundingBox> boundingBoxes = new ArrayList<>();

        //create a boundingBox for each RoadSegment
        RoadCursor cursor = it.next();
        while (it.hasNext()) {

            RoadCursor nextCursor = it.next();
            boundingBoxes.add(new BoundingBox(cursor, nextCursor));
            cursor = nextCursor;

        }

        //Set the height for each GridVertex (in boundingBox) at the height of the street above
        for (int i = 0; i < boundingBoxes.size(); i++) {
            BoundingBox bb = boundingBoxes.get(i);

            //+grid.length/2 becuase of the displacement of the x axis
            for (int x = bb.x_min + grid.length / 2; x <= bb.x_max + grid.length / 2; x++) {
                for (int z = bb.z_min; z <= bb.z_max; z++) {
                    if (x < grid.length && z < grid[0].length && x >= 0 && z >= 0 && grid[x][z] != null) {

                        grid[x][z].setProperty(GridVertexProperty.road);

                        boolean doCalc = true;
                        Vector3f vec = grid[x][z].getPosition();

                        //test if grid[x][y] lies in further bb
                        for (BoundingBox box: boundingBoxes) {
                            //-grid.length/2 becuase of the displacement of the x axis
                            if (box.contains(x - grid.length / 2, z)) {
                                if (this.calcRoadHeight(box, vec) < this.calcRoadHeight(bb, vec)) {
                                    doCalc = false;
                                    break;
                                }
                            }
                        }

                        if (doCalc) {
                            vec.setY(this.calcRoadHeight(bb, vec) - 0.2f);
                            grid[x][z].setPosition(vec);
                        }
                    }
                }
            }
        }
    }


    private float calcRoadHeight(BoundingBox bb, Vector3f vec) {
        Vector2f firstVec = new Vector2f(bb.firstCursor.getPosition().x, bb.firstCursor.getPosition().z);
        Vector2f lastVec = new Vector2f(bb.lastCursor.getPosition().x, bb.lastCursor.getPosition().z);
        Vector2f xVec = new Vector2f(vec.x, vec.z);


        float dotProd = ((lastVec.subtract(firstVec)).normalize()).dot(xVec.subtract(firstVec));
        float denominator = (lastVec.subtract(firstVec)).length();

        Vector3f prod = (bb.lastCursor.getPosition().subtract(bb.firstCursor.getPosition())).mult(dotProd / denominator);

        return (bb.firstCursor.getPosition().add(prod)).y;

    }

    private int[] calcYminAndYmax(GridVertex[][] grid) {
        int max = 0;
        int min = 0;

        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                if (grid[i][j] != null) {
                    int y = (int) grid[i][j].getPosition().y + 1;
                    if (y < min) {
                        min = y;
                    } else if (y > max) {
                        max = y;
                    }
                }
            }
        }

        int[] minMax = {min, max};
        return minMax;
    }


    private class BoundingBox {
        private int x_min;
        private int x_max;
        private int z_min;
        private int z_max;
        private RoadCursor firstCursor;
        private RoadCursor lastCursor;

        private BoundingBox(RoadCursor firstCursor, RoadCursor lastCursor) {
            this.firstCursor = firstCursor;
            this.lastCursor = lastCursor;
            float width = firstCursor.getWidhtAndHightAndHight().x;
            float nextWidth = lastCursor.getWidhtAndHightAndHight().x;

            Vector3f[] vecs = new Vector3f[] {
                    firstCursor.getPosition().add(firstCursor.getRight().mult(-width / 2)),
                    firstCursor.getPosition().add(firstCursor.getRight().mult(width / 2)),
                    lastCursor.getPosition().add(lastCursor.getRight().mult(-nextWidth / 2)),
                    lastCursor.getPosition().add(lastCursor.getRight().mult(nextWidth / 2)),
            };

            vecs[2] = vecs[2].add(lastCursor.getDirection().mult(2));
            vecs[3] = vecs[3].add(lastCursor.getDirection().mult(2));

            this.x_min = (int) Math.min(Math.min(vecs[0].x, vecs[2].x), Math.min(vecs[1].x, vecs[3].x) - 1);
            this.x_max = Math.round(Math.max(Math.max(vecs[0].x, vecs[2].x), Math.max(vecs[1].x, vecs[3].x))) + 1;
            this.z_max = Math.round(Math.max(Math.max(vecs[0].z, vecs[2].z), Math.max(vecs[1].z, vecs[3].z))) + 1;
            this.z_min = (int) Math.min(Math.min(vecs[0].z, vecs[2].z), Math.min(vecs[1].z, vecs[3].z)) - 1;
        }

        private boolean contains(int x, int z) {
            return  (x <= x_max && x >= x_min && z <= z_max && z >= z_min);
        }

        public boolean equals(Object box) {
            if (box.getClass() != this.getClass()) {
                return false;
            }
            BoundingBox b = (BoundingBox) box;
            return this.firstCursor.getPosition().equals(b.firstCursor.getPosition()) && this.lastCursor.getPosition().equals(b.lastCursor.getPosition());
        }
    }
}
