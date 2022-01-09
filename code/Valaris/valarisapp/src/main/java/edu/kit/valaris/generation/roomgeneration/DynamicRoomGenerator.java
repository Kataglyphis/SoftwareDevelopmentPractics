package edu.kit.valaris.generation.roomgeneration;

import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import edu.kit.valaris.assets.AssetProvider;
import edu.kit.valaris.generation.roadgeneration.*;
import edu.kit.valaris.generation.AssetsUtility;
import edu.kit.valaris.generation.GenerationConfig;
import edu.kit.valaris.generation.GeneratorSettings;
import edu.kit.valaris.generation.RandomNumberGenerator;

import java.util.ArrayList;
import java.util.Set;

/**
 * Implementation of {@link AbstractRoomGenerator}.
 * Factory for generating a {@link DynamicRoom} using a assembly
 * of assets, defined in the configuration file.
 *
 * @author Sidney Hansen
 */
public class DynamicRoomGenerator extends AbstractRoomGenerator {

    private GenerationConfig m_generationConfig;

    private AssetProvider m_assetProvider;

    private int m_vehicleCount;


    /**
     * Creates a new {@link DynamicRoomGenerator}.
     *
     * @param generationConfig the {@link GenerationConfig} to be used for this Generator.
     * @param assetProvider the {@link AssetProvider} to be used for this Generator
     * @param vehicleCount the number of vehicles.
     */
    DynamicRoomGenerator(GenerationConfig generationConfig, AssetProvider assetProvider, int vehicleCount) {
        this.m_generationConfig = generationConfig;
        this.m_assetProvider = assetProvider;
        this.m_vehicleCount = vehicleCount;
    }


    @Override
    public IRoom generateRoom(int seed, float boundingSphereRadius, Vector2f entryWidth, float exitAngle, Set<GeneratorSettings> flags) {

        RoadGenerator roadGenerator = new RoadGenerator(m_generationConfig);


        Vector3f cuboidDimensions;
        if (flags.contains(GeneratorSettings.STARTROOM)) {
            cuboidDimensions = calcDimensions(seed, 2 * boundingSphereRadius);

        } else {
            cuboidDimensions = calcDimensions(seed, boundingSphereRadius);
        }

        Road road = roadGenerator.generateCuboidRoad(seed, cuboidDimensions, entryWidth, exitAngle, "testsettings", flags);


        // If no valid road can be generated
        if (road == null) {
            return null;
        }
        Node roomRootNode = new Node("roomRootNode");

        Vector3f offset = new Vector3f(0,0,0);

        String assetBundle = selectAssetBundle(seed);

        // Generate Visual representation of room
        generateRoomWalls(seed, roomRootNode, road, flags, cuboidDimensions, offset, assetBundle);
        placeAssets(seed, roomRootNode, road, offset, cuboidDimensions, assetBundle);
        generateRoadAsset(seed, roomRootNode, road, assetBundle);


        // Handle Start-,Endroom
        if (flags.contains(GeneratorSettings.STARTROOM)) {
            placeStartPositions(road);
            generateDoor(seed, roomRootNode, road.getLastRoadCursor(), assetBundle);
        }
        if (flags.contains(GeneratorSettings.ENDROOM)) {
            road.getRoadCursors().get(1)
                    .addProperty(RoadCursorProperty.CHECKPOINT);
            generateDoor(seed, roomRootNode, road.getRoadCursors().get(1), assetBundle);
        }


        return new DynamicRoom(road, roomRootNode);
    }


    /**
     * Calculates dimensions of room.
     *
     * @param seed the Seed
     * @param radius distance between opposite corners
     * @return Dimensions of room
     */
    private Vector3f calcDimensions(int seed, float radius) {

        RandomNumberGenerator randomNumberGenerator = new RandomNumberGenerator(seed);

        float randomNumberAngle = 1 - 2f * randomNumberGenerator.random();
        float randomNumberHeight = randomNumberGenerator.random();

        float minHeight = 5;

        float maxAngle = (float) Math.PI / 32f;

        float angle = (float) Math.PI / 4f + randomNumberAngle * maxAngle;
        float height = minHeight + randomNumberHeight * radius / 4f;

        return new Vector3f(radius * (float)Math.cos(angle),height,radius * (float)Math.sin(angle));

    }

    /**
     * Places assets in the room in a grid structure.
     * @param seed the Seed
     * @param assetRootNode root Node of assets
     * @param road the road
     * @param offset of the room
     * @param dimensions of the room
     * @param assetBundle determines which assets to use
     */
    private void placeAssets(int seed, Node assetRootNode,
                             Road road, Vector3f offset,
                             Vector3f dimensions, String assetBundle) {

        final float targetGridWidth = m_generationConfig.getNumber("roomgeneration.TargetGridWidth").floatValue();
        final int assetHeight = 0;
        final int iterations = m_generationConfig.getNumber("roomgeneration.AssetPlacementIterations").intValue();

        assetBundle = "floorAssets." + assetBundle;

        if (!m_generationConfig.pathExists("roomgeneration.assetGroups." + assetBundle)) {
            assetBundle = "floorAssets.defaultBundle";
        }

        Vector3f standardOffset = new Vector3f(-dimensions.x / 2f, 0, 0);

        // Get new AssetGrid, containing road info
        AssetGrid assetGrid = getNewAssetGrid(road, offset.add(standardOffset), dimensions, targetGridWidth);

        RandomNumberGenerator randomNumberGenerator = new RandomNumberGenerator(seed);


        // Place Assets
        for (int i = 0; i < iterations; i++) {
            placeRandomAsset(randomNumberGenerator.getNewSeed(), assetRootNode,
                    assetGrid, assetHeight, assetBundle);
        }
    }


    private int[] getGridSegments(Vector3f dimensions, float targetGridWidth) {
        int segmentsX = (int)(dimensions.x / targetGridWidth);
        int segmentsY = (int)(dimensions.y / targetGridWidth);
        int segmentsZ = (int)(dimensions.z / targetGridWidth);

        return new int[] {
            segmentsX,
            segmentsY,
            segmentsZ,
        };
    }


    /**
     * Creates a new asset grid used for the placement of assets.
     *
     * @param road the road
     * @param offset of the room
     * @param dimensions of the room
     * @param targetGridWidth grid width which will be approximated
     * @return new {@link AssetGrid}
     */
    private AssetGrid getNewAssetGrid(Road road, Vector3f offset,
                                      Vector3f dimensions, float targetGridWidth) {
        float minX;
        float maxX;
        float minY;
        float maxY;
        int[] gridSegments = getGridSegments(dimensions, targetGridWidth);

        Vector3f scale = new Vector3f(
                dimensions.x / (float) gridSegments[0],
                dimensions.y / (float) gridSegments[1],
                dimensions.z / (float) gridSegments[2]);

        Vector3f[] quad;

        RoadCursor firstCursor;
        RoadCursor lastCursor;


        // Create grid, set all fields to height 0 => all fields free
        int [][] grid = new int [gridSegments[0]][gridSegments[2]];

        for (int x = 0; x < grid.length; x++) {
            for (int y = 0; y < grid[x].length; y++) {
                grid[x][y] = 0;
            }
        }

        // Set correct height for all fields intersecting the road
        for (int i = 0; i < road.getRoadCursors().size() - 1; i++) {

            firstCursor = road.getRoadCursors().get(i);
            lastCursor = road.getRoadCursors().get(i + 1);


            quad = new Vector3f[] {
                firstCursor.getRight().mult(firstCursor.getWidhtAndHightAndHight().x * (0.5f))
                        .add(firstCursor.getPosition()),
                lastCursor.getRight().mult(lastCursor.getWidhtAndHightAndHight().x * (0.5f))
                        .add(lastCursor.getPosition()),
                lastCursor.getRight().mult(lastCursor.getWidhtAndHightAndHight().x * (-0.5f))
                        .add(lastCursor.getPosition()),
                firstCursor.getRight().mult(firstCursor.getWidhtAndHightAndHight().x * (-0.5f))
                        .add(firstCursor.getPosition())
            };

            for (Vector3f vertex : quad) {
                vertex.subtractLocal(offset);
            }


            // Calculate all indices of fields intersecting whit road
            minX = dimensions.x;
            maxX = 0;
            minY = dimensions.z;
            maxY = 0;

            for (Vector3f vertex : quad) {
                minX = Math.min(minX, vertex.x);
                maxX = Math.max(maxX, vertex.x);

                minY = Math.min(minY, vertex.z);
                maxY = Math.max(maxY, vertex.z);
            }


            minX = minX / scale.x;
            maxX = maxX / scale.x;

            minY = minY / scale.z;
            maxY = maxY / scale.z;

            minX = Math.max(minX - 1, 0);
            maxX = Math.min(maxX + 1, gridSegments[0]);

            minY = Math.max(minY - 1, 0);
            maxY = Math.min(maxY + 1, gridSegments[2]);

            // Set indices to road height
            for (int x = (int) minX; x < (int) maxX; x++) {
                for (int y = (int) minY; y < (int) maxY; y++) {
                    grid[x][y] = 1 + (int)road.getFirstRoadCursor().getWidhtAndHightAndHight().y;
                }
            }
        }

        return new AssetGrid(gridSegments[0], gridSegments[1], gridSegments[2], scale, offset, grid);
    }


    /**
     * Places a random asset at a random position.
     * @param seed the Seed
     * @param node asset root node
     * @param assetGrid the {@link AssetGrid} to be used
     * @param assetHeight height in which asset is to be placed
     * @param assetBundle determines which assets to use
     */
    private void placeRandomAsset(int seed, Node node, AssetGrid assetGrid,
                                     int assetHeight, String assetBundle) {
        int[] selectedFields = assetGrid.selectField(seed);
        placeAsset(seed, node, assetGrid, selectedFields[0], selectedFields[1], assetHeight, assetBundle);
    }


    /**
     * Places a asset at given location.
     * @param seed the Seed
     * @param node asset root node
     * @param assetGrid the {@link AssetGrid} to be used
     * @param indexX colum
     * @param indexZ row
     * @param assetHeight height in which asset is to be placed
     * @param assetBundle determines which assets to use
     */
    private void placeAsset(int seed, Node node, AssetGrid assetGrid, int indexX, int indexZ,
                            int assetHeight, String assetBundle) {

        if (!(0 <= indexX && indexX < assetGrid.m_segmentsX && 0 <= indexZ && indexZ < assetGrid.m_segmentsZ)) {
            return;
        }

        int localHeight = assetGrid.m_grid[indexX][indexZ];

        boolean onFloor = (assetHeight == -1);
        if (onFloor) {
            assetHeight = localHeight;
        } else if (localHeight > assetHeight) {
            return;
        }

        // Get possible sets of indices
        ArrayList<int[][]> possibleIndices = getPossibleIndices(assetGrid, indexX, indexZ, assetHeight, onFloor);

        if (possibleIndices.isEmpty()) {
            return;
        }

        // Select set of indices
        int[][] indices = selectIndices(seed, possibleIndices);

        addFittingAsset(seed, node, assetGrid, indices, assetHeight, assetBundle);

    }


    /**
     * Places a asset in the groom using only the given fields.
     * @param seed the Seed
     * @param node asset root node
     * @param assetGrid the {@link AssetGrid} to be used
     * @param indices list holding pairs of indices, describing free fields in the grid where the asset may be placed.
     * @param assetHeight height in which asset is to be placed
     * @param assetBundle determines which assets to use
     */
    private void addFittingAsset(
            int seed, Node node, AssetGrid assetGrid,
            int[][] indices, int assetHeight, String assetBundle) {

        // Get names of all possible assets
        String[] assets = m_generationConfig.getChildren("roomgeneration.assetGroups." + assetBundle);

        int minXIndex = assetGrid.m_segmentsX;
        int maxXIndex = 0;
        int minZIndex = assetGrid.m_segmentsZ;
        int maxZIndex = 0;

        for (int[] indice:
                indices) {
            minXIndex = Math.min(minXIndex, indice[0]);
            maxXIndex = Math.max(maxXIndex, indice[0]);
            minZIndex = Math.min(minZIndex, indice[1]);
            maxZIndex = Math.max(maxZIndex, indice[1]);
        }

        // Calculate maximum dimensions of asset, in indices
        int sizeX = (maxXIndex - minXIndex);
        int sizeY = assetGrid.m_segmentsY - assetHeight;
        int sizeZ = maxZIndex - minZIndex;


        String asset;
        float assetSizeX;
        float assetSizeY;
        float assetSizeZ;

        Vector3f boundingBoxDimensions;

        int rotationIndex;

        rotationIndex = Math.abs(seed) % 4;

        float scaleCoefficient;

        ArrayList<String> fittingAssets = new ArrayList<>();

        final float minimalScale = 0.2f;

        // Calculate for every asset, if it fits
        for(int i = 0; i < assets.length; i++) {
            asset = assets[i];
            assetSizeX = m_generationConfig.getNumber("roomgeneration.assetGroups." + assetBundle + "." + asset + ".SizeX").floatValue();
            assetSizeY = m_generationConfig.getNumber("roomgeneration.assetGroups." + assetBundle + "." + asset + ".SizeY").floatValue();
            assetSizeZ = m_generationConfig.getNumber("roomgeneration.assetGroups." + assetBundle + "." + asset + ".SizeZ").floatValue();

            boundingBoxDimensions = new Vector3f(
                    new float[] {assetSizeX, assetSizeZ}[rotationIndex % 2],
                    assetSizeY,
                    new float[] {assetSizeZ, assetSizeX}[rotationIndex % 2]);


            scaleCoefficient = Math.min(sizeX * assetGrid.m_scale.x / boundingBoxDimensions.x,
                    Math.min(sizeY * assetGrid.m_scale.y / assetSizeY, sizeZ * assetGrid.m_scale.z / boundingBoxDimensions.z));

            if (scaleCoefficient >= minimalScale) {
                fittingAssets.add(asset);
            }
        }

        if (fittingAssets.isEmpty()) {
            return;
        }

        // Select asset
        RandomNumberGenerator randomNumberGenerator = new RandomNumberGenerator(seed);
        int selectionIndex = Math.abs(randomNumberGenerator.getNewSeed()) % fittingAssets.size();


        asset = fittingAssets.get(selectionIndex);

        assetSizeX = m_generationConfig.getNumber("roomgeneration.assetGroups." + assetBundle + "." + asset + ".SizeX").floatValue();
        assetSizeY = m_generationConfig.getNumber("roomgeneration.assetGroups." + assetBundle + "." + asset + ".SizeY").floatValue();
        assetSizeZ = m_generationConfig.getNumber("roomgeneration.assetGroups." + assetBundle + "." + asset + ".SizeZ").floatValue();




        boundingBoxDimensions = new Vector3f(
                new float[] {assetSizeX, assetSizeZ}[rotationIndex % 2],
                assetSizeY,
                new float[] {assetSizeZ, assetSizeX}[rotationIndex % 2]);


        // Calculate asset scale
        scaleCoefficient = Math.min(sizeX * assetGrid.m_scale.x / boundingBoxDimensions.x,
                Math.min(sizeY * assetGrid.m_scale.y / assetSizeY, sizeZ * assetGrid.m_scale.z / boundingBoxDimensions.z));


        // Calculate asset position
        Vector3f relativePosition = new Vector3f(
                (minXIndex + maxXIndex) / 2f,
                assetHeight,
                (minZIndex + maxZIndex) / 2f)
                .mult(assetGrid.m_scale);

        relativePosition.addLocal(assetGrid.m_offset);

        // Update assetGrid
        assetGrid.setFieldsToHeight(indices, assetHeight + (int)boundingBoxDimensions.y + 1);

        float rotationAngle = new float[] {0, FastMath.HALF_PI, FastMath.PI, -FastMath.PI}[rotationIndex];

        String assetJsonPath = "roomgeneration.assetGroups." + assetBundle + "." + asset;

        attachAsset(node, relativePosition, rotationAngle, assetGrid.m_scale.mult(scaleCoefficient), assetJsonPath);

    }

    /**
     * Attaches asset at given position to parent Node.
     * @param parentNode the Node where the asset is to be attached
     * @param relativePosition the relative position of the asset
     * @param rotationAngle the anlge of rotation of the asset around the y-Axis
     * @param scale the Scale of the asset
     * @param assetJsonPath the Path describing the asset in the GenerationConfig.json
     */
    private void attachAsset(Node parentNode, Vector3f relativePosition, float rotationAngle,Vector3f scale, String assetJsonPath) {
        String assetID = m_generationConfig.getString(assetJsonPath + ".Id");

        Spatial model = m_assetProvider.provide(assetID);

        model.setLocalScale(scale);
        model.setLocalTranslation(relativePosition);
        model.rotate(0, rotationAngle, 0);

        parentNode.attachChild(model);
    }


    /**
     * Select random list of indices for the {@link AssetGrid} from a Array of lists of possible indices.
     * @param seed the Seed
     * @param possibleIndices list of possible indices
     * @return selected list of indices
     */
    private int[][] selectIndices(int seed, ArrayList<int[][]> possibleIndices) {
        RandomNumberGenerator randomNumberGenerator = new RandomNumberGenerator(seed);

        return possibleIndices.get(((int)(Math.pow(randomNumberGenerator.random(),0.2)
                * possibleIndices.size()) + 1) % possibleIndices.size());
    }

    /**
     * Calculate lists of possible indices on which a asset may be placed.
     * @param assetGrid the {@link AssetGrid} to be used
     * @param indexX colunm
     * @param indexZ row
     * @param height on which the asset is to be placed
     * @param groundBound if the asset must be placed on a surface
     * @return lists of possible indices
     */
    private ArrayList<int[][]> getPossibleIndices(AssetGrid assetGrid, int indexX,
                                                  int indexZ, int height, boolean groundBound) {

        ArrayList<int[][]> indicesList = new ArrayList<>();
        ArrayList<int[]> fieldIndices = new ArrayList<>();


        if (groundBound && height != assetGrid.m_grid[indexX][indexZ]) {
            return indicesList;
        }

        fieldIndices.add(new int[]{indexX,indexZ});

        int minXIndex = indexX;
        int maxXIndex = indexX;
        int minYIndex = indexZ;
        int maxYIndex = indexZ;

        int [] outherIndices;
        ArrayList<int[]> newIndices;

        int currentHeight;

        int[][] deltas;

        boolean validLine;

        final int size = m_generationConfig.getNumber("roomgeneration.AssetPlacementSize").intValue();


        // Calculate indices of adjacent, free fields around given field, creating in effect
        // a larger rectangle composed of several fields
        for (int i = 0; i < size; i++) {
            for (int[] fieldIndice:
                    fieldIndices) {
                minXIndex = Math.min(minXIndex, fieldIndice[0]);
                maxXIndex = Math.max(maxXIndex, fieldIndice[0]);
                minYIndex = Math.min(minYIndex, fieldIndice[1]);
                maxYIndex = Math.max(maxYIndex, fieldIndice[1]);
            }

            outherIndices = new int[]{
                maxXIndex,
                maxYIndex,
                minXIndex,
                minYIndex
            };

            newIndices = new ArrayList<>();
            deltas = new int[][] {
                {1,0},
                {0,1},
                {-1,0},
                {0,-1}
            };

            for (int j = 0; j < 4; j++) {
                validLine = true;

                for (int[] fieldIndex:
                        fieldIndices) {
                    if (fieldIndex[j % 2] == outherIndices[j]) {
                        if (fieldIndex[0] + deltas[j][0] < 0
                                || fieldIndex[0] + deltas[j][0] >= assetGrid.m_segmentsX
                                || fieldIndex[1] + deltas[j][1] < 0
                                || fieldIndex[1] + deltas[j][1] >= assetGrid.m_segmentsZ) {
                            break;
                        }
                        currentHeight = assetGrid.m_grid[fieldIndex[0] + deltas[j][0]][fieldIndex[1] + deltas[j][1]];

                        if (currentHeight <= height && (!groundBound || currentHeight == height)) {

                            newIndices.add(new int[] {fieldIndex[0] + deltas[j][0], fieldIndex[1] + deltas[j][1]});
                        } else {
                            validLine = false;
                            break;
                        }
                    }
                }
                if (validLine) {
                    fieldIndices.addAll(newIndices);
                    indicesList.add(fieldIndices.toArray(new int[fieldIndices.size()][2]));
                }
            }
        }
        return indicesList;

    }

    /**
     * AssetGrid, used for the placement of assets in the room.
     */
    private class AssetGrid {
        private int m_segmentsX;

        private int m_segmentsY;

        private int m_segmentsZ;

        private Vector3f m_scale;

        private Vector3f m_offset;

        private int[][] m_grid;

        /**
         * Creates a new asset grid using a 2D Array of field heights.
         *
         * @param segmentsX Row count of grid
         * @param segmentsY Column count of grid
         * @param segmentsZ Segment count matching the height of the room
         * @param scale Scale of grid segments
         * @param offset Offset of grid to (0,0,0)
         * @param grid 2D Array of field heights
         */
        private AssetGrid(int segmentsX, int segmentsY, int segmentsZ,
                          Vector3f scale, Vector3f offset, int[][] grid) {
            m_segmentsX = segmentsX;
            m_segmentsY = segmentsY;
            m_segmentsZ = segmentsZ;
            m_scale = scale;
            m_offset = offset;
            m_grid = grid;
        }

        /**
         * Sets field value of given fields to given height.
         *
         * @param fields list of indices in the grid
         * @param height height to be set
         */
        private void setFieldsToHeight(int[][] fields, int height) {
            for (int[] field:
                    fields) {
                m_grid[field[0]][field[1]] = height;
            }
        }

        /**
         * Select random field.
         *
         * @param seed the Seed
         * @return random field
         */
        private int[] selectField(int seed) {
            RandomNumberGenerator randomNumberGenerator = new RandomNumberGenerator(seed);
            return new int[] {
                ((int) (randomNumberGenerator.random() * m_segmentsX) + 1) % m_segmentsX,
                ((int) (randomNumberGenerator.random() * m_segmentsZ) + 1) % m_segmentsZ
            };
        }

    }

    /**
     * Generate room wall, by using and transforming different wall assets.
     *
     * @param seed the Seed
     * @param assetRootNode root Node of assets
     * @param road the road
     * @param flags generation Settings
     * @param dimensions of the room
     * @param offset of the room
     * @param assetBundle determines which assets to use
     */
    private void generateRoomWalls(int seed, Node assetRootNode, Road road, Set<GeneratorSettings> flags,
                                   Vector3f dimensions, Vector3f offset, String assetBundle) {

        float dot = road.getFirstRoadCursor().getRight().normalize()
                .dot(road.getLastRoadCursor().getDirection().normalize());

        int exitIndex;
        if (dot > 0.5f) {
            exitIndex = 3;
        } else if (dot < -0.5f) {
            exitIndex = 2;
        } else {
            exitIndex = 1;
        }


        RoadCursor entryRoadCursor = road.getFirstRoadCursor();
        RoadCursor exitRoadCursor = road.getLastRoadCursor();

        Vector3f [] roomVertices = {
                new Vector3f(dimensions.x / 2f,0,0).add(offset),
                new Vector3f(dimensions.x / 2f,dimensions.y,0).add(offset),
                new Vector3f(-dimensions.x / 2f,dimensions.y,0).add(offset),
                new Vector3f(-dimensions.x / 2f,0,0).add(offset),

                new Vector3f(dimensions.x / 2f,0,dimensions.z).add(offset),
                new Vector3f(dimensions.x / 2f,dimensions.y,dimensions.z).add(offset),
                new Vector3f(-dimensions.x / 2f,dimensions.y,dimensions.z).add(offset),
                new Vector3f(-dimensions.x / 2f,0,dimensions.z).add(offset)
        };

        Vector3f currentRight = entryRoadCursor.getRight();
        Vector3f currentNormal = entryRoadCursor.getNormal();
        Vector2f currentWidth = entryRoadCursor.getWidhtAndHightAndHight();


        Vector3f [] entryVertices = {
            new Vector3f(offset.add(currentRight.mult(currentWidth.x / 2f))),
            new Vector3f(offset.add(currentRight.mult(currentWidth.x / 2f))
                    .add(currentNormal.mult(currentWidth.y))),
            new Vector3f(offset.add(currentRight.mult(-currentWidth.x / 2f))
                    .add(currentNormal.mult(currentWidth.y))),
            new Vector3f(offset.add(currentRight.mult(-currentWidth.x / 2f)))
        };

        currentRight = exitRoadCursor.getRight();
        currentNormal = exitRoadCursor.getNormal();
        currentWidth = exitRoadCursor.getWidhtAndHightAndHight();
        Vector3f currentPosition = exitRoadCursor.getPosition();

        Vector3f [] exitVertices = {
            new Vector3f(offset.add(currentPosition.add(currentRight.mult(-currentWidth.x / 2f)))),
            new Vector3f(offset.add(currentPosition.add(currentRight.mult(-currentWidth.x / 2f))
                    .add(currentNormal.mult(currentWidth.y)))),
            new Vector3f(offset.add(currentPosition.add(currentRight.mult(currentWidth.x / 2f))
                    .add(currentNormal.mult(currentWidth.y)))),
            new Vector3f(offset.add(currentPosition.add(currentRight.mult(currentWidth.x / 2f))))
        };

        int[][] cuboidQuadIndices = {
                //Front
                {3,2,1,0},
                //Back
                {4,5,6,7},
                //Right
                {0,1,5,4},
                //Left
                {7,6,2,3},
                //Top
                //{2,6,5,1},
                {1,5,6,2},
                //Bottom
                {0,4,7,3}
        };

        Vector3f lineAStart;
        Vector3f lineAEnd;
        Vector3f lineBStart;
        Vector3f lineBEnd;
        String assetGroup;

        for (int i = 0; i < 6; i++) {

            if ((i == 0) && !flags.contains(GeneratorSettings.STARTROOM)) {
                for (int j = 0; j < 3; j++) {

                    assetGroup = "passageWall";
                    lineAStart = roomVertices[cuboidQuadIndices[i][j]];
                    lineAEnd = roomVertices[cuboidQuadIndices[i][j + 1]];
                    lineBStart = entryVertices[j];
                    lineBEnd = entryVertices[j + 1];

                    placeWallAsset(seed, assetRootNode, lineAStart, lineAEnd, lineBEnd, lineBStart,
                            assetGroup, assetBundle);
                }

            } else if ((i == exitIndex) && !flags.contains(GeneratorSettings.ENDROOM)) {
                for (int j = 0; j < 3; j++) {

                    assetGroup = "passageWall";
                    lineAStart = roomVertices[cuboidQuadIndices[i][j]];
                    lineAEnd = roomVertices[cuboidQuadIndices[i][j + 1]];
                    lineBStart = exitVertices[j];
                    lineBEnd = exitVertices[j + 1];

                    placeWallAsset(seed, assetRootNode, lineAStart, lineAEnd, lineBEnd, lineBStart,
                             assetGroup, assetBundle);
                }

            } else {
                if (i == 4) {
                    assetGroup = "roof";
                } else if (i == 5) {
                    assetGroup = "floor";
                } else {
                    assetGroup = "wall";
                }
                lineAStart = roomVertices[cuboidQuadIndices[i][0]];
                lineAEnd = roomVertices[cuboidQuadIndices[i][1]];
                lineBStart = roomVertices[cuboidQuadIndices[i][3]];
                lineBEnd = roomVertices[cuboidQuadIndices[i][2]];

                placeWallAsset(seed, assetRootNode, lineAStart, lineAEnd, lineBEnd, lineBStart,
                        assetGroup, assetBundle);

            }


        }

    }

    /**
     * Transform and place wall asset of given group.
     *
     * @param seed the Seed
     * @param assetRootNode root Node of assets
     * @param pointA corner of Wall
     * @param pointB corner of Wall
     * @param pointC corner of Wall
     * @param pointD corner of Wall
     * @param assetGroup group of which to select wall asset
     * @param assetBundle determines which type of assets of given group to use
     */
    private void placeWallAsset(int seed, Node assetRootNode,
                                 Vector3f pointA, Vector3f pointB,
                                 Vector3f pointC, Vector3f pointD,
                                 String assetGroup, String assetBundle) {

        String assetPath = selectAssetFromBundle(seed, assetGroup, assetBundle);
        String assetId = m_generationConfig.getString(assetPath + ".Id");
        Spatial asset = m_assetProvider.provide(assetId);

        Vector3f assetDimensions = new Vector3f(
                m_generationConfig.getNumber(assetPath + ".SizeX").floatValue(),
                m_generationConfig.getNumber(assetPath + ".SizeY").floatValue(),
                m_generationConfig.getNumber(assetPath + ".SizeZ").floatValue());


        Spatial transformedAsset = AssetsUtility.transformBetweenPoints(
                pointA, pointB, pointC, pointD, asset, assetDimensions);

        assetRootNode.attachChild(transformedAsset);
    }

    /**
     * Place start positions on road.
     *
     * @param road the road
     */
    private void placeStartPositions(Road road) {

        final float vehicleWidth = m_generationConfig.getNumber("roomgeneration.VehicleWidth").floatValue();
        final int playersPerCursor = (int) (road.getFirstRoadCursor().getWidhtAndHightAndHight().x / (2f * vehicleWidth));
        final int startCursorIndex = 3;
        System.out.println("playersPerCursor: " + playersPerCursor);

        for (int i = 0; i < m_vehicleCount; i++) {
            RoadCursor roadCursor = road.getRoadCursors().get((i / playersPerCursor) + startCursorIndex);

            float distortion = (0.45f - 0.9f * (((1f / 2f) + (i % playersPerCursor)) / (float)playersPerCursor));

            Vector3f position = roadCursor.getPosition().add(roadCursor.getRight().mult(
                    roadCursor.getWidhtAndHightAndHight().x * distortion));



            RoadObject startPosition = new RoadObject(position, startCursorIndex + i / playersPerCursor);
            startPosition.addProperty(RoadObjectProperty.START_POSITION);
            road.addRoadObject(startPosition);
        }

        road.getRoadCursors().get(m_vehicleCount / playersPerCursor + startCursorIndex + 1)
                .addProperty(RoadCursorProperty.CHECKPOINT);
    }

    /**
     * Select asset bundle.
     *
     * @param seed the Seed
     * @return the name of the selected assetBundle
     */
    private String selectAssetBundle(int seed) {

        final String parentPath = "roomgeneration.AssetBundles";

        String[] assetBundles =  m_generationConfig.getStringArray(parentPath);

        return assetBundles[Math.abs(seed) % assetBundles.length];
    }

    /**
     * Select asset of certain group from given bundle.
     * If no asset of that group exists in the bundle, select
     * asset of that group from defaultBundle.
     *
     * @param seed the Seed
     * @param assetGroup group of which to select asset from
     * @param assetBundle the bundle which the assets is preferably selected from
     * @return the selected assets json path
     */
    private String selectAssetFromBundle(int seed, String assetGroup, String assetBundle) {
        String parentPath = "roomgeneration.assetGroups." + assetGroup + "." + assetBundle;
        if (!m_generationConfig.pathExists(parentPath)) {
            parentPath = "roomgeneration.assetGroups." + assetGroup + "." + "defaultBundle";
        }
        String[] assetNames =  m_generationConfig.getChildren(parentPath);
        return parentPath + "." + assetNames[Math.abs(seed) % assetNames.length];

    }

    /**
     * Transform and place road asset.
     *
     * @param seed the Seed
     * @param assetRootNode root Node of assets
     * @param road the road
     * @param assetBundle determines which assets to use
     */
    private void generateRoadAsset(int seed, Node assetRootNode, Road road, String assetBundle) {
        String assetPath = selectAssetFromBundle(seed, "roadAsset", assetBundle);
        String assetId = m_generationConfig.getString(assetPath + ".Id");
        Spatial asset = m_assetProvider.provide(assetId);

        Vector3f assetDimensions = new Vector3f(
                m_generationConfig.getNumber(assetPath + ".SizeX").floatValue(),
                m_generationConfig.getNumber(assetPath + ".SizeY").floatValue(),
                m_generationConfig.getNumber(assetPath + ".SizeZ").floatValue());

        int segmentsPerAsset = m_generationConfig.getNumber(assetPath + ".Segments").intValue();
        assetRootNode.attachChild(AssetsUtility.generateRoadAsset(road, asset, assetDimensions, segmentsPerAsset, false));
    }

    /**
     * Transform and place door asset.
     *
     * @param seed the Seed
     * @param assetRootNode root Node of assets
     * @param roadCursor {@link RoadCursor} at which the door is to be placed
     * @param assetBundle determines which assets to use
     */
    private void generateDoor(int seed, Node assetRootNode, RoadCursor roadCursor, String assetBundle) {
        String assetPath = selectAssetFromBundle(seed, "doorAsset", assetBundle);
        String assetId = m_generationConfig.getString(assetPath + ".Id");
        Spatial asset = m_assetProvider.provide(assetId);

        Vector3f assetDimensions = new Vector3f(
                m_generationConfig.getNumber(assetPath + ".SizeX").floatValue(),
                m_generationConfig.getNumber(assetPath + ".SizeY").floatValue(),
                m_generationConfig.getNumber(assetPath + ".SizeZ").floatValue());
        assetRootNode.attachChild(AssetsUtility.tranformAssetAlongRoadCursor(
                roadCursor, asset, assetDimensions, true));
    }


}
