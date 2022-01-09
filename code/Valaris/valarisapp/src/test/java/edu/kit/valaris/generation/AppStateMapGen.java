package edu.kit.valaris.generation;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AppStateManager;
import com.jme3.app.state.BaseAppState;
import com.jme3.asset.AssetManager;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.light.Light;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.util.SkyFactory;
import edu.kit.valaris.datastructure.IMapBody;
import edu.kit.valaris.generation.mapgeneration.MapGenerator;
import edu.kit.valaris.menu.gui.LoadingCallback;

/**
 * @author Lukas Schölch
 */

public class AppStateMapGen extends BaseAppState implements ActionListener {
    private SimpleApplication app;
    private Node rootNode;
    private Node sceneRootNode;
    private AssetManager assetManager;
    private AppStateManager stateManager;
    private InputManager inputManager;
    private ViewPort viewPort;
    private SceneCursor sceneCursor;
    private Camera cam;
    private float camHight;
    private int camMode;



    public static final Vector3f LEFT = new Vector3f(1, 0, 0);
    public static final Vector3f RIGHT = new Vector3f(-1, 0, 0);
    public static final Vector3f UP = new Vector3f(0, 1, 0);
    public static final Vector3f DOWN = new Vector3f(0, -1, 0);
    public static final Vector3f FRONT = new Vector3f(0, 0, 1);
    public static final Vector3f BACK = new Vector3f(0, 0, -1);





    @Override
    protected void initialize(Application app) {
        camMode = 0;
        // Init Stuff

        this.app = (SimpleApplication) app;
        this.rootNode = this.app.getRootNode();
        rootNode.detachAllChildren();
        for(Light light : rootNode.getWorldLightList()) {
            rootNode.removeLight(light);
        }
        this.assetManager = this.app.getAssetManager();
        this.stateManager = this.app.getStateManager();
        this.inputManager = this.app.getInputManager();
        this.viewPort = this.app.getViewPort();
        this.cam = this.app.getCamera();
        this.sceneRootNode = new Node("scene");
        rootNode.attachChild(sceneRootNode);

        // Add Light
        DirectionalLight dl = new DirectionalLight();
        dl.setDirection(new Vector3f(-0.1f, -50f, -1).normalizeLocal());
        dl.setColor(ColorRGBA.White.mult(0.4f));
        rootNode.addLight(dl);


        //rootNode.addLight(spotLight);


        AmbientLight al = new AmbientLight();
        al.setColor(ColorRGBA.White.mult(0.1f));
        //sceneRootNode.addLight(al);


        sceneCursor = new SceneCursor(rootNode, rootNode,cam);


        cam.lookAtDirection(FRONT.add(DOWN.mult(2)).normalize(), UP.mult(2).add(FRONT).normalize());




        rootNode.detachAllChildren();

        LoadingCallback generationCallback = new LoadingCallback() {
            @Override
            public void setProgress(double progress, String message) {
            }

            @Override
            public void finished(IMapBody body) {
                //do nothing here
            }
        };
        Node mapRootNode = new Node();
        int sphereRadius = 50;

        //DomeGen
        MapGenerator mapGen = new MapGenerator(this.app.getAssetManager(), this.app.getStateManager(), "smallMap", 10);
        mapGen.generate(66, mapRootNode, generationCallback);


        //Box für Koordinaten Tests
        /*Box b = new Box(30, 0, 30);
        b.setMode(Mesh.Mode.Lines);
        Geometry g = new Geometry("Box", b);

        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        //mat.setColor("Color", ColorRGBA.Blue);
        g.setMaterial(mat);
        g.setLocalTranslation(30,0,30);
        g.updateModelBound();
        rootNode.attachChild(g);*/



        //KUPPEL Struktur um deren Grenzen anzuzeigen
        /*Sphere sphere = new Sphere(100,100, sphereRadius);
        sphere.setMode(Mesh.Mode.Lines);
        Geometry geom = new Geometry("dome",sphere);
        geom.setLocalTranslation(0,0,sphereRadius);
        Material sampleMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        //sampleMat.setColor("blue",new ColorRGBA(1f,0f,0f,1f));
        geom.setMaterial(sampleMat);
        geom.updateModelBound();
        rootNode.attachChild(geom);*/


        //Node domeAssetNode = new Node();

        //DummyDomeAssetGenerator dummyDomeAssetGenerator = new DummyDomeAssetGenerator(assetManager);
        //dummyDomeAssetGenerator.generateDomeAssets(null, map.getRoad(), domeAssetNode);
        /**
        Spatial asset = assetManager.loadModel("edu.kit.edu.kit.edu.kit.valaris/models/scalingRoadA2/scalingRoadA2.j3o").deepClone();
        Vector3f assetDimesions = new Vector3f(6,1,4);
        int segmentsPerAsset = 4;


        Node roadAssetRootNode = AssetsUtility.generateRoadAsset(map.getRoad(), asset, assetDimesions, segmentsPerAsset, false);
         **/
        rootNode.attachChild(mapRootNode);

        //rootNode.attachChild(roadAssetRootNode);

        rootNode.attachChild(SkyFactory.createSky(assetManager, "edu.kit.valaris/skyboxTest.png", SkyFactory.EnvMapType.EquirectMap));

        setUpKeys();

    }

    private void setUpKeys() {
        inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("Up", new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("Down", new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping("Top", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addMapping("Bottom", new KeyTrigger(KeyInput.KEY_LSHIFT));
        inputManager.addMapping("Enter", new KeyTrigger(KeyInput.KEY_RETURN));
        inputManager.addListener(sceneCursor, "Left");
        inputManager.addListener(sceneCursor, "Right");
        inputManager.addListener(sceneCursor, "Up");
        inputManager.addListener(sceneCursor, "Down");
        inputManager.addListener(sceneCursor, "Top");
        inputManager.addListener(sceneCursor, "Bottom");
        inputManager.addListener(this, "Enter");
    }

    @Override
    public void update(float tpf) {
        sceneCursor.update(tpf);
        cam.setLocation(new Vector3f(sceneCursor.getPos().x, sceneCursor.getPos().y, sceneCursor.getPos().z).add(cam.getDirection().mult(-10f)));
    }

    @Override
    protected void cleanup(Application app) {
    }

    @Override
    protected void onEnable() {

    }

    @Override
    protected void onDisable() {

    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
    }

}


