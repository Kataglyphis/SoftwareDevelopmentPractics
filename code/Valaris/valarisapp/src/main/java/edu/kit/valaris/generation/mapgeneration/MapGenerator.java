package edu.kit.valaris.generation.mapgeneration;

import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import edu.kit.valaris.datastructure.IMapBody;
import edu.kit.valaris.assets.AssetProvider;
/*import edu.kit.valaris.debugutil.GraphicsObject;
import edu.kit.valaris.debugutil.VectorGraphicsDrawer;
import edu.kit.valaris.debugutil.VectorGraphicsExporter;*/
import edu.kit.valaris.generation.*;
import edu.kit.valaris.generation.domegeneration.DomeGenerator;
import edu.kit.valaris.generation.domegeneration.IDome;
import edu.kit.valaris.generation.domegeneration.IDomeGenerator;
import edu.kit.valaris.generation.roadgeneration.Road;
import edu.kit.valaris.generation.roadgeneration.RoadCursor;
import edu.kit.valaris.generation.roadgeneration.RoadObject;
import edu.kit.valaris.generation.roadgeneration.RoadObjectProperty;
import edu.kit.valaris.generation.roomgeneration.IRoom;
import edu.kit.valaris.generation.roomgeneration.IRoomGenerator;
import edu.kit.valaris.generation.roomgeneration.RoomSelector;
import edu.kit.valaris.generation.tunnelgeneration.ITunnel;
import edu.kit.valaris.generation.tunnelgeneration.ITunnelGenerator;
import edu.kit.valaris.generation.tunnelgeneration.TunnelGenerator;
import edu.kit.valaris.menu.gui.LoadingCallback;

import java.util.*;


/**
 * Implements IMapGenerator and defines a concrete implementation for the generation.
 *
 * @author Lukas Schölch
 */
public class MapGenerator implements IMapGenerator {

    private IDomeGenerator m_domeGen;

    private GenerationConfig m_generationConfig;

    private String m_paramPath;

    private AppStateManager m_appStateManager;

    private AssetManager m_assetManager;

    private ITunnelGenerator m_tunnelGen;

    private IRoomGenerator m_roomGen;

    /**
     * Constructor.
     * @param assetManager Classified by JMonkey for managing assets.
     * @param appStateManager Adopted by JMonkey to manage the appstates.
     * @param vehicleCount Number of vehicles to be set up at the start.
     */
    public MapGenerator(AssetManager assetManager, AppStateManager appStateManager, String kindOfMap, int vehicleCount) {

        this.m_generationConfig = new GenerationConfig();
        this.m_paramPath = "mapgeneration" + "." + kindOfMap;

        this.m_appStateManager = appStateManager;
        this.m_assetManager = assetManager;

        //create Generators

        if (appStateManager.getState(AssetProvider.class) == null) {
            throw new IllegalStateException("No AssetProvider atached!");
        }

        this.m_domeGen = new DomeGenerator(m_generationConfig, m_assetManager,
                appStateManager.getState(AssetProvider.class));

        this.m_roomGen = new RoomSelector(m_generationConfig,
                appStateManager.getState(AssetProvider.class),
                vehicleCount);

        this.m_tunnelGen = new TunnelGenerator(m_generationConfig,
                appStateManager.getState(AssetProvider.class));

    }

    @Override
    public IMapBody generate(int seed, Node rootNode, LoadingCallback callback) {

        //for LoadingScreen
        float progress = 0;

        long startTime = System.currentTimeMillis();
        System.out.println("Now: generateMap()");

        int fieldX = this.m_generationConfig.getNumber(this.m_paramPath + ".fieldX").intValue();
        int fieldZ = this.m_generationConfig.getNumber(this.m_paramPath + ".fieldZ").intValue();
        int startPos = m_generationConfig.getNumber(this.m_paramPath + ".StartPosition").intValue();
        if (startPos < 0 || startPos > fieldX) {
            throw new IllegalArgumentException("The start position is not in the field!");
        }

        int minNumberOfDomes = this.m_generationConfig.getNumber(this.m_paramPath + ".minNumberOfDomes").intValue();
        int maxNumberOfDomes = fieldZ;
        int minDomeRadius = this.m_generationConfig.getNumber(this.m_paramPath + ".MinDomeRadius").intValue();
        int maxDomeRadius = this.m_generationConfig.getNumber(this.m_paramPath + ".MaxDomeRadius").intValue();

        int minRoomRadius = this.m_generationConfig.getNumber(this.m_paramPath + ".MinRoomRadius").intValue();
        int maxRoomRadius = this.m_generationConfig.getNumber(this.m_paramPath + ".MaxRoomRadius").intValue();
        if (minNumberOfDomes > maxNumberOfDomes) {
            throw new IllegalArgumentException("minNumberOfDomes > maxNumberOfDomes(<=> fieldZ)!");
        }

        int minTunnelLength = this.m_generationConfig.getNumber(this.m_paramPath + ".MinTunnelLength").intValue();
        int maxTunnelLength = this.m_generationConfig.getNumber(this.m_paramPath + ".MaxTunnelLength").intValue();


        RandomNumberGenerator numberGen = new RandomNumberGenerator(seed);
        Map map = new Map(m_assetManager);
        Set<GeneratorSettings> flags = new HashSet<>();


        //determine domePosition
        ArrayList<FieldPosition> domePositions = new ArrayList<>();

        int numberOfDomes = minNumberOfDomes + Math.round((maxNumberOfDomes - minNumberOfDomes) * numberGen.random());
        int nextRow = 0;
        for (int i = 0; i < numberOfDomes; i++) {
            int numberOfPossibleRows = (fieldZ - nextRow) - (numberOfDomes - i);
            domePositions.add(new FieldPosition(Math.round(fieldX * numberGen.random()), Math.round(nextRow + (numberOfPossibleRows * numberGen.random()))));
            nextRow += domePositions.get(i).z - nextRow + 1;
        }

        //determine start and finish positions
        FieldPosition finishPosition = new FieldPosition(fieldX, fieldZ + 1);
        domePositions.add(finishPosition);

        FieldPosition aktFieldPos = new FieldPosition(startPos, 0);
        FieldPosition lastFieldPos = new FieldPosition(0, -1);

        //get street Width and Height for the start
        int[] forFirstEntryWidth = m_generationConfig.getIntArray(m_paramPath + ".StartEntryWidthAndHeight");
        Vector2f firstEntryWidth = new Vector2f(forFirstEntryWidth[0], forFirstEntryWidth[1]);
        map.getRoad().addRoadCursor(new RoadCursor(0, 0, 0, new Vector3f(0, 0, 0), firstEntryWidth));

        //startRoom
        flags.add(GeneratorSettings.STARTROOM);
        addNewRoom(map, numberGen.getNewSeed(), maxRoomRadius, 0, flags);
        flags.clear();

        boolean domeWasReached = false;
        for (int cnt = 0; cnt < domePositions.size(); cnt++) {
            boolean domeReached = false;
            while (!domeReached) {


                domeReached = aktFieldPos.x == domePositions.get(cnt).x && aktFieldPos.z == domePositions.get(cnt).z;

                FieldPosition nextFieldPos = this.getNextFieldPos(aktFieldPos, domePositions.get(cnt), numberGen);

                //createTunnel to the segment
                int tunnelLength = Math.round(minTunnelLength + ((maxTunnelLength - minTunnelLength) * numberGen.random()));
                Vector3f tunnelTarget = new Vector3f(0, 0, tunnelLength);
                if (domeReached) {
                    flags.add(GeneratorSettings.CHECKPOINT_EXIT);
                    flags.add(GeneratorSettings.DOOR_EXIT);
                }
                if (domeWasReached) {
                    flags.add(GeneratorSettings.CHECKPOINT_ENTRY);
                    flags.add(GeneratorSettings.DOOR_ENTRY);
                    domeWasReached = false;
                }
                addNewTunnel(map, numberGen.getNewSeed(), tunnelTarget, 0, flags);
                flags.clear();

                //check if next dome reached
                if (domeReached) {
                    //for LoadingScreen
                    progress = ((20 / (float)domePositions.size()) / 100) * (cnt + 1);
                    callback.setProgress(progress, "place Domes");

                    //test if hit is the finish line
                    if (cnt + 1 >= domePositions.size()) {
                        break;
                    }
                    domeWasReached = true;
                    float angle = this.calcAngle(lastFieldPos, aktFieldPos, nextFieldPos);
                    int radius = Math.round(minDomeRadius + (maxDomeRadius - minDomeRadius) * numberGen.random());
                    IDome dome = addNewDome(map, numberGen.getNewSeed(), radius, angle, flags);
                    if (dome == null) {
                        //should not be used
                        tunnelLength = Math.round(minTunnelLength + ((maxTunnelLength - minTunnelLength) * numberGen.random()));
                        tunnelTarget = this.calcSpecialTunnelTarget(lastFieldPos, aktFieldPos, nextFieldPos, tunnelLength);
                        addNewTunnel(map, numberGen.getNewSeed(), tunnelTarget, angle, flags);
                    }


                } else {

                    //add room
                    int radius = Math.round(minRoomRadius + (maxRoomRadius - minRoomRadius) * numberGen.random());
                    float angle = this.calcAngle(lastFieldPos, aktFieldPos, nextFieldPos);
                    IRoom room = addNewRoom(map, numberGen.getNewSeed(), radius, angle, flags);
                    if (room == null) {
                        //create tunnel instead of room
                        tunnelLength = Math.round(minTunnelLength + ((maxTunnelLength - minTunnelLength) * numberGen.random()));
                        tunnelTarget = this.calcSpecialTunnelTarget(lastFieldPos, aktFieldPos, nextFieldPos, tunnelLength);
                        addNewTunnel(map, numberGen.getNewSeed(), tunnelTarget, angle, flags);
                    }
                }

                lastFieldPos = aktFieldPos;
                aktFieldPos = nextFieldPos;

            }
        }

        //endRoom
        flags.add(GeneratorSettings.ENDROOM);
        addNewRoom(map, numberGen.getNewSeed(), maxRoomRadius, 0, flags);
        flags.clear();

        System.out.println("Now: Map created -- time: " + (System.currentTimeMillis() - startTime) + "ms");

        //for LoadingScreen
        final float minMapProgress = progress;
        LoadingCallback sceneGraphCallback = new LoadingCallback() {
            @Override
            public void setProgress(double progress, String message) {
                callback.setProgress(minMapProgress + progress * (1 - minMapProgress), message);
            }

            @Override
            public void finished(IMapBody body) {
                //do nothing here
            }
        };

        map.generateSceneGraph(rootNode, sceneGraphCallback);

        AssetsUtility.setLightToRoot(rootNode, rootNode);
        placeItems(seed, map.getRoad());


        System.out.println("Fin: Entire generation -- time: " + (System.currentTimeMillis() - startTime) + "ms");


        //Debug
        /*GraphicsObject graphicsObject = new GraphicsObject();
        VectorGraphicsDrawer vectorGraphicsDrawer = new VectorGraphicsDrawer();
        VectorGraphicsExporter vectorGraphicsExporter = VectorGraphicsExporter.getInstance();
        vectorGraphicsDrawer.addRoad(graphicsObject, map.generateMapBody().getRoad());
        vectorGraphicsExporter.exportVectorGraphics(graphicsObject,"C:\\Users\\Megaport\\Desktop\\PSE_VectorGraph\\map.eps");*/

        return map.generateMapBody();
    }



    private FieldPosition getNextFieldPos(FieldPosition aktFieldPos, FieldPosition targetFieldPos, RandomNumberGenerator numGen) {

        int zDistance = targetFieldPos.z - aktFieldPos.z;
        int xDistance = targetFieldPos.x - aktFieldPos.x;
        FieldPosition nextFieldPosition = new FieldPosition(aktFieldPos.x, aktFieldPos.z);

        if (xDistance == 0) {
            nextFieldPosition.z++;
            return nextFieldPosition;
        } else if (zDistance == 0) {
            if (xDistance > 0) {
                nextFieldPosition.x++;
            } else {
                nextFieldPosition.x--;
            }
            return nextFieldPosition;
        } else {
            if (numGen.random() <= 0.5f) {
                nextFieldPosition.z++;
                return nextFieldPosition;
            }


            if (xDistance > 0) {
                nextFieldPosition.x++;
            } else {
                nextFieldPosition.x--;
            }
            return nextFieldPosition;
        }
    }

    private float calcAngle(FieldPosition lastFieldPos, FieldPosition aktFieldPos, FieldPosition nextFieldPos) {
        if (lastFieldPos.z < aktFieldPos.z) {
            //from bottom
            if (nextFieldPos.z > aktFieldPos.z) {
                return 0;
            } else if (nextFieldPos.x > aktFieldPos.x) {
                return (float) Math.PI / 2;
            } else {
                return (float) -Math.PI / 2;
            }
        } else if (lastFieldPos.x < aktFieldPos.x) {

            //from right
            if (nextFieldPos.x > aktFieldPos.x) {
                return 0;
            }  else {
                return (float) -Math.PI / 2;
            }
        } else {

            //from left
            if (nextFieldPos.x < aktFieldPos.x) {
                return 0;
            }  else {
                return (float) Math.PI / 2;
            }
        }
    }

    private Vector3f calcSpecialTunnelTarget(FieldPosition lastFieldPos, FieldPosition aktFieldPos, FieldPosition nextFieldPos, int tunnelLength) {
        float angle = this.calcAngle(lastFieldPos, aktFieldPos, nextFieldPos);
        if (angle == (float) Math.PI / 2) {
            return new Vector3f(tunnelLength, 0, tunnelLength);
        }
        if (angle == (float) -Math.PI / 2) {
            return new Vector3f(-tunnelLength, 0, tunnelLength);
        }
        return new Vector3f(0, 0, tunnelLength);
    }

    private IDome addNewDome(Map map, int seed, int radius, float angle, Set<GeneratorSettings> flags) {

        IDome dome = this.m_domeGen.generateDome(seed, radius, map.getRoad().getLastRoadCursor().getWidhtAndHightAndHight(), angle, flags);

        if (dome == null) {
            return null;
        }

        map.getRoad().attach(dome.getRoad());
        map.addSceneItem(dome);

        return dome;
    }

    private ITunnel addNewTunnel(Map map, int seed, Vector3f targetVec, float angle, Set<GeneratorSettings> flags) {

        int maxTunnelDeviation = this.m_generationConfig.getNumber(this.m_paramPath + ".maxTunnelDeviation").intValue();
        RandomNumberGenerator numGen = new RandomNumberGenerator(seed);

        //calc curve
        targetVec.x += maxTunnelDeviation * numGen.random();

        ITunnel tunnel = this.m_tunnelGen.generate(seed, targetVec, angle, -map.getRoad().getLastRoadCursor().getYAngle(),
                map.getRoad().getLastRoadCursor().getWidhtAndHightAndHight(), flags);
        if (tunnel == null) {
            return null;
        }

        map.addSceneItem(tunnel);
        map.getRoad().attach(tunnel.getRoad());

        return tunnel;
    }

    private IRoom addNewRoom(Map map, int seed, int radius, float angle, Set<GeneratorSettings> flags) {

        float probabilityForTunnelInsteadOfRoom =  this.m_generationConfig.getNumber(this.m_paramPath + ".probabilityForTunnelInsteadOfRoom").floatValue();

        RandomNumberGenerator numGen = new RandomNumberGenerator(seed);
        if (!flags.contains(GeneratorSettings.STARTROOM) && !flags.contains(GeneratorSettings.ENDROOM) &&
                numGen.random() < probabilityForTunnelInsteadOfRoom) {
            return null;
        }

        IRoom room = m_roomGen.generate(numGen.getNewSeed(), radius, map.getRoad().getLastRoadCursor().getWidhtAndHightAndHight(), angle, flags);
        if (room == null) {
            return null;
        }
        map.addSceneItem(room);
        map.getRoad().attach(room.getRoad());

        return room;
    }



    private void placeItems(int seed, Road road) {

        RandomNumberGenerator randomNumberGenerator = new RandomNumberGenerator(seed);

        final float itemFrequency = m_generationConfig.getNumber("mapgeneration.items.ItemFrequency").floatValue();


        float itemPropabillity = 0;

        float randomNumber;

        Iterator<RoadCursor> iterator = road.getRoadCursorIterator();
        RoadCursor currentRoadCursor;

        int distanceToBeginningOfRoad = 20;
        int distanceToEndOfRoad = 20;

        for (int i = distanceToBeginningOfRoad; i < road.getRoadCursors().size() - distanceToEndOfRoad; i++) {
            currentRoadCursor = road.getRoadCursors().get(i);

            randomNumber = randomNumberGenerator.random();

            if (randomNumber < itemPropabillity) {
                road.addRoadObjects(placeItemsAtIndex(currentRoadCursor, i));
                itemPropabillity = 0;
            } else {
                itemPropabillity += itemFrequency;
            }
        }

    }

    private Collection<RoadObject> placeItemsAtIndex(RoadCursor roadCursor, int index) {

        final float itemSpace = m_generationConfig.getNumber("mapgeneration.items.ItemSpace").floatValue();
        final float itemHeight = m_generationConfig.getNumber("mapgeneration.items.ItemHeight").floatValue();
        Collection<RoadObject> roadObjects = new LinkedList<>();


        int itemCount = (int) (roadCursor.getWidhtAndHightAndHight().x/itemSpace) - 1;

        Vector3f itemPosition;
        RoadObject roadObject;

        for (int i = 0; i < itemCount; i++) {
            itemPosition = new Vector3f((i - itemCount/2f)*itemSpace, itemHeight, 0);

            roadObject = new RoadObject(itemPosition, index);
            roadObject.addProperty(RoadObjectProperty.ITEM);
            roadObjects.add(roadObject);
        }

        return roadObjects;

    }

    private class FieldPosition {
        private int x;
        private int z;

        private FieldPosition(int x, int z) {
            this.x = x;
            this.z = z;
        }
    }
}
