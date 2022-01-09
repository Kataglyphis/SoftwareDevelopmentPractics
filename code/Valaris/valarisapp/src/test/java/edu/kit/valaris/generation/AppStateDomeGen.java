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
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import edu.kit.valaris.assets.AssetProvider;
import edu.kit.valaris.generation.domegeneration.DomeGenerator;
import edu.kit.valaris.generation.domegeneration.IDome;
import edu.kit.valaris.generation.domegeneration.IDomeGenerator;

/**
 * @author Lukas Schölch
 */

        /*
         * To change this license header, choose License Headers in Project Properties.
         * To change this template file, choose Tools | Templates
         * and open the template in the editor.
         */


import java.util.HashSet;

/**
 *
 * @author Sidney Hansen
 */
public class AppStateDomeGen extends BaseAppState implements ActionListener {
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
        this.assetManager = this.app.getAssetManager();
        this.stateManager = this.app.getStateManager();
        this.inputManager = this.app.getInputManager();
        this.viewPort = this.app.getViewPort();
        this.cam = this.app.getCamera();
        this.sceneRootNode = new Node("scene");
        rootNode.attachChild(sceneRootNode);

        // Add Light
        DirectionalLight dl = new DirectionalLight();
        dl.setDirection(new Vector3f(-30f, 50f, -0.5f).normalizeLocal());
        dl.setColor(ColorRGBA.White.mult(4f));
        //rootNode.addLight(dl);

        AmbientLight al = new AmbientLight();
        al.setColor(ColorRGBA.White.mult(0.2f));
        //sceneRootNode.addLight(al);


        sceneCursor = new SceneCursor(rootNode, rootNode,cam);


        cam.lookAtDirection(FRONT.add(DOWN.mult(2)).normalize(), UP.mult(2).add(FRONT).normalize());




        rootNode.detachAllChildren();

        int sphereRadius = 100;

        //DomeGen
        IDomeGenerator domeGen = new DomeGenerator(new GenerationConfig(), this.app.getAssetManager(),
                getStateManager().getState(AssetProvider.class));
        IDome dome = domeGen.generateDome(8, sphereRadius, new Vector2f(4, 4), -1.4f, new HashSet<>());



        /*//KUPPEL Struktur um deren Grenzen anzuzeigen
        Sphere sphere = new Sphere(100,100, sphereRadius);
        sphere.setMode(Mesh.Mode.Lines);
        Geometry geom = new Geometry("dome",sphere);
        geom.setLocalTranslation(0,0,sphereRadius);
        Material sampleMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        //sampleMat.setColor("blue",new ColorRGBA(1f,0f,0f,1f));
        geom.setMaterial(sampleMat);
        geom.updateModelBound();
        rootNode.attachChild(geom);*/



        rootNode.attachChild(dome.generateSceneGraph());



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


