package edu.kit.valaris.generation.mapgeneration;

import com.jme3.asset.AssetManager;
import com.jme3.light.Light;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import edu.kit.valaris.datastructure.*;
//import edu.kit.valaris.debugutil.GraphicsObject;
//import edu.kit.valaris.debugutil.VectorGraphicsDrawer;
//import edu.kit.valaris.debugutil.VectorGraphicsExporter;
import edu.kit.valaris.generation.AssetsUtility;
import edu.kit.valaris.generation.ISceneItem;
import edu.kit.valaris.generation.domegeneration.IDome;
import edu.kit.valaris.generation.roadgeneration.*;
import edu.kit.valaris.menu.gui.LoadingCallback;
import edu.kit.valaris.rendering.CullingControl;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Map {

    private Road m_road;

    private ArrayList<ISceneItem> m_sceneItems;

    private AssetManager m_assetManager;

    private int m_cnt = 0;


    /**
     * Constructor.
     * @param assetManager Classified by JMonkey for managing assets.
     */
    public Map(AssetManager assetManager) {
        this.m_assetManager = assetManager;

        this.m_sceneItems = new ArrayList<>();

        this.m_road = new Road();
    }


    /**
     * Adds a ISceneItem to the map to expand it.
     * @param sceneItem The ISceneItem.
     */
    protected void addSceneItem(ISceneItem sceneItem) {
        m_sceneItems.add(sceneItem);
    }

    /**
     * generates scenegraph of the entire map.
     * @param rootNode The node to attach all sceneitems
     * @param callback For LoadingScreen
     */
    public void generateSceneGraph(Node rootNode, LoadingCallback callback) {

        long startTime = System.currentTimeMillis();

        System.out.println("Now: generateScenegraph()");

        //for loadingScreen
        float progress = 0;
        callback.setProgress(progress, "draw Landscape");


        ThreadingClass[] threads = new ThreadingClass[m_sceneItems.size()];
        ExecutorService exec = Executors.newCachedThreadPool();

        System.out.println("numberOfProcessors: " + Runtime.getRuntime().availableProcessors());

        for (int i = 0; i < m_sceneItems.size(); i++) {
            ISceneItem sceneItem = m_sceneItems.get(i);
            ThreadingClass tc = new ThreadingClass(sceneItem);
            exec.execute(tc);
            threads[i] = tc;
        }

        exec.shutdown();

        //vars to update the loadingScreen
        int domeCnt = 0;
        int toBreak = -1;
        for (ThreadingClass tc: threads) {
            if (tc.m_item instanceof IDome) {
                domeCnt++;
            }
        }

        while (!exec.isTerminated()) {
            for (int i = domeCnt - 1; i >= 0; i--) {
                float check = 1f - (domeCnt - i) / (float) threads.length;
                //"if-struct" for callback is optimized for the chosen executor service
                if (i == toBreak) {
                    break;
                }
                if ((float) m_cnt / (float) threads.length >= check) {
                    progress += 1 / (float) (domeCnt + 1);
                    callback.setProgress(progress, "draw Landscape");
                    toBreak = i;
                    break;
                }
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException e)  {
                e.printStackTrace();
            }
        }

        System.out.println(m_cnt);
        for (int i = 0; i < m_sceneItems.size(); i++) {
            Node sceneItemNode = threads[i].m_sceneItemNode;
            ArrayList<Light> lights = new ArrayList<>();
            AssetsUtility.getLightFromSpatial(sceneItemNode,lights);
            sceneItemNode.addControl(new CullingControl(
                    i, threads[i].m_item.getEntryFrame(),
                    threads[i].m_item.getExitFrame(),lights));
            rootNode.attachChild(sceneItemNode);
        }

        callback.setProgress(1, "draw Landscape");

        System.out.println("Fin: generateScenegraph() -- time:" + (System.currentTimeMillis() - startTime) + "ms");

    }

    /**
     *
     * @return The Road of the entire Map.
     */
    public Road getRoad() {
        return this.m_road;
    }

    /**
     *
     * @return MapBody-object to represent the track of the Map.
     */
    IMapBody generateMapBody() {

        RoadModel roadModel = new SimpleRoadModel();


        LinkedList<RoadInitializationObject> roadObjects
                = new LinkedList<>();



        ArrayList<RoadCursor> roadCursors = m_road.getRoadCursors();
        RoadCursor roadCursor;
        RoadCursor nextRoadCursor;
        RoadModelSegment roadModelSegment;
        Vector2f currentDirection;
        RoadCursor lastRoadCursor = m_road.getFirstRoadCursor();

        float relativeAngle;
        Vector3f deltaPos3D;

        // calculate roadModelSegments
        for (int i = 0; i < roadCursors.size() - 1; i++) {

            roadCursor = roadCursors.get(i);
            nextRoadCursor = roadCursors.get(i + 1);


            currentDirection = new Vector2f(roadCursor.getRight().dot(lastRoadCursor.getRight()),
                    roadCursor.getRight().dot(lastRoadCursor.getDirection())).normalize();


            lastRoadCursor = new RoadCursor(roadCursor);

            relativeAngle = currentDirection.angleBetween(new Vector2f(1,0));

            deltaPos3D = new Vector3f(nextRoadCursor.getPosition()
                    .subtract(roadCursor.getPosition()));


            roadModelSegment = new RoadModelSegment(
                    i, relativeAngle, deltaPos3D.length(),roadCursor.getWidhtAndHightAndHight().x);

            if (roadCursor.hasProperty(RoadCursorProperty.CHECKPOINT)) {
                roadModel.addCheckpointSegment(roadModelSegment);
            } else {
                roadModel.addRoadSegment(roadModelSegment);
            }
        }

        // add items and start positions to mapbody
        for (RoadObject roadObject :
                m_road.getRoadObjects()) {

            if (roadObject.hasProperty(RoadObjectProperty.ITEM)) {

                roadObjects.add(new RoadInitializationObject(InitializationObjectType.ITEMBOX,
                        new Position(roadObject.getIndex(), new Vector2f(roadObject.getPosition().z, roadObject.getPosition().x)), 0));
            }
            if (roadObject.hasProperty(RoadObjectProperty.START_POSITION)) {

                roadObjects.add(new RoadInitializationObject(InitializationObjectType.VEHICLE,
                        new Position(roadObject.getIndex(), new Vector2f(roadObject.getPosition().z, roadObject.getPosition().x)), 0));
            }
        }

        return new MapBody(roadModel, roadObjects, m_road);
    }

    private synchronized void count() {
        m_cnt++;
    }



    private class ThreadingClass implements Runnable {

        private Node m_sceneItemNode = new Node();

        private ISceneItem m_item;

        private ThreadingClass(ISceneItem item) {
            m_item = item;
        }

        @Override
        public void run() {
            m_sceneItemNode = m_item.generateSceneGraph();
            count();
        }

    }


}
