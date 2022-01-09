package edu.kit.valaris.generation;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


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
import com.jme3.math.*;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.*;
import com.jme3.util.SkyFactory;
import edu.kit.valaris.assets.AssetProvider;
import edu.kit.valaris.datastructure.IMapBody;
import edu.kit.valaris.datastructure.Position;
import edu.kit.valaris.datastructure.RoadModelSegment;
import edu.kit.valaris.generation.mapgeneration.MapGenerator;
import edu.kit.valaris.generation.roadgeneration.RoadCursor;
import edu.kit.valaris.menu.gui.LoadingCallback;

/**
 *
 * @author Sidney Hansen
 */
public class AppStateMapBodyTest extends BaseAppState implements ActionListener{
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

    private Spatial starship;
    private float xOffset;
    private int starshipSegmentIndex;
    private IMapBody mapBody;
    private float yOffset;

    private Vector2f vehicleDirection;
    private float currentTurnAngle;
    private float currentMove;
    private float angle;
    Spatial right;
    Spatial front;



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

        vehicleDirection = new Vector2f(0,1);
        currentTurnAngle=0;
        yOffset = 0;


        //rootNode.addLight(spotLight);


        AmbientLight al = new AmbientLight();
        al.setColor(ColorRGBA.White.mult(0.1f));
        //sceneRootNode.addLight(al);


        sceneCursor = new SceneCursor(rootNode, rootNode,cam);


        cam.lookAtDirection(FRONT.add(DOWN.mult(2)).normalize(), UP.mult(2).add(FRONT).normalize());


        rootNode.detachAllChildren();

        Node mapRootNode = new Node();
        int sphereRadius = 50;

        //DomeGen
        LoadingCallback generationCallback = new LoadingCallback() {
            @Override
            public void setProgress(double progress, String message) {
            }

            @Override
            public void finished(IMapBody body) {
                //do nothing here
            }
        };
        MapGenerator mapGen = new MapGenerator(this.app.getAssetManager(), this.app.getStateManager(), "normalMap", 10);
        mapBody = mapGen.generate(66, mapRootNode, generationCallback);

        rootNode.attachChild(mapRootNode);

        //rootNode.attachChild(roadAssetRootNode);

        rootNode.attachChild(SkyFactory.createSky(assetManager, "edu.kit.valaris/skyboxTest.png", SkyFactory.EnvMapType.EquirectMap));


        starship = this.stateManager.getState(AssetProvider.class).provide("Dynamics/HoverCart");

        front = starship.deepClone();
        front.setLocalScale(0.1f);
        right = starship.deepClone();
        right.setLocalScale(0.1f);
        Position position = new Position(10, new Vector2f(1,0));

        Transform transform = mapBody.calc3DTransform(position, 0f);


        starship.setLocalTransform(transform);
        starship.setLocalScale(0.1f);
        starshipSegmentIndex = 0;
        xOffset = 0;

        rootNode.attachChild(starship);
        rootNode.attachChild(front);
        rootNode.attachChild(right);

        int index = 10;

        Node domeRoof = this.stateManager.getState(AssetProvider.class).provide("DomeRoof/SteelRoof");

        rootNode.attachChild(domeRoof);

        AssetsUtility.setLightToRoot(rootNode, rootNode);

        Node mapBodyNode = new Node("mapBodyNode");
        mapBodyNode.setLocalTranslation(0,20f, 0f);

        setUpKeys();

    }

    private Vector3f calcNewVector(RoadCursor c, Vector3f relativeDirection) {
        return new Vector3f().add(c.getRight().mult(relativeDirection.x))
                .add(c.getNormal().mult(relativeDirection.y))
                .add(c.getDirection()).mult(relativeDirection.z)
                .add(c.getPosition());
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

        inputManager.addListener(this, "Left");
        inputManager.addListener(this, "Right");
        inputManager.addListener(this, "Up");
        inputManager.addListener(this, "Down");
        inputManager.addListener(this, "Top");
        inputManager.addListener(this, "Bottom");
        inputManager.addListener(this, "Enter");
    }

    @Override
    public void update(float tpf) {
        //float yOffset = -3f;
        float starshipSpeed = 20*tpf;

        //vehicleDirection.normalizeLocal();
        //vehicleDirection.rotateAroundOrigin(currentTurnAngle,false);



        RoadModelSegment segmentA = mapBody.getRoad().getPath().get(starshipSegmentIndex);
        RoadModelSegment segmentB = mapBody.getRoad().getPath().get(starshipSegmentIndex+1);


        Vector2f aDir = new Vector2f(0,1);
        Vector2f aRight = new Vector2f(1,0);

        //egal
        angle += currentTurnAngle*tpf;
        Vector2f movDir = new Vector2f(aDir).mult(currentMove*starshipSpeed);
        movDir.rotateAroundOrigin(angle,false);
        xOffset += movDir.y;
        yOffset += movDir.x;
        //


        Vector2f bPos = new Vector2f(0,segmentA.getLength());

        Vector2f p = new Vector2f(yOffset, xOffset);

        Vector2f bDir = new Vector2f(aDir);
        bDir.rotateAroundOrigin(segmentB.getAngle(),false);

        Vector2f bRight = new Vector2f(aRight);
        bRight.rotateAroundOrigin(segmentB.getAngle(),false);

        Vector2f pB = p.subtract(bPos);

        float xOffsetB = pB.dot(bDir);
        float yOffsetB = pB.dot(bRight);

        if (xOffsetB >= 0) {
            xOffset = xOffsetB;
            yOffset = yOffsetB;
            starshipSegmentIndex = (starshipSegmentIndex + 1) % (mapBody.getRoad().getPath().size()-1);
            angle = angle - segmentB.getAngle();
            System.out.println("Entering Segment: "+starshipSegmentIndex);
        }


        Position position = new Position(starshipSegmentIndex, new Vector2f(xOffset,yOffset));

        float vehicleDim = 2;

        Position frontPos = new Position(position.getSegmentIndex(),
                position.getOffset().add(new Vector2f((float)Math.cos(-angle),(float)Math.sin(-angle)).mult(vehicleDim)));
        Position rightPos = new Position(position.getSegmentIndex(),
                position.getOffset().add(new Vector2f((float)Math.sin(-angle),-(float)Math.cos(-angle)).mult(vehicleDim)));

//        Transform transformA = new Transform(mapBody.calc3DCoord(position));
//        Transform transformB = new Transform(mapBody.calc3DCoord(frontPos));
//        Transform transformC = new Transform(mapBody.calc3DCoord(rightPos));


        System.out.println(angle);

        Transform transform = mapBody.calc3DTransform(position, angle);
//        Transform transform = mapBody.calc3DTransform(position, -movDir.getAngle()+segmentA.getAngle());

        //sceneCursor.setPos(transform.getTranslation());
        starship.setLocalTransform(transform);
//        front.setLocalTransform(transformB);
//        right.setLocalTransform(transformC);

        starship.setLocalScale(1f);
        front.setLocalScale(0.5f);
        right.setLocalScale(0.5f);

        starship = starship.rotate(0,FastMath.PI,0);

        if(camMode == 1) {
            sceneCursor.setPos(transform.getTranslation());
            cam.setLocation(new Vector3f(sceneCursor.getPos().x, sceneCursor.getPos().y, sceneCursor.getPos().z).add(cam.getDirection().mult(-5f)));

            //cam.lookAt(transform.getTranslation().add(transform.getRotation().mult(new Vector3f(0,0,-1))).add(new Vector3f(0,-0.2f,0)),
            //        transform.getRotation().mult(new Vector3f(0,1f,0)));

            //camRail.mov(tpf);
            //cam.setLocation(camRail.getPos());
            //cam.lookAt(camRail.dir.add(camRail.getPos()), Vector3f.UNIT_Y);
            //sceneCursor.setPos(cam.getLocation().subtract(cam.getDirection().mult(-10f)));
        } else {

            sceneCursor.update(tpf);
            cam.setLocation(new Vector3f(sceneCursor.getPos().x, sceneCursor.getPos().y, sceneCursor.getPos().z).add(cam.getDirection().mult(-5f)));
        }
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

        float turnAngle = 2f;
        if(isPressed) {
            if (name.equals("Enter")) {
                camMode = (camMode+1)%2;
            }
            if (camMode==1) {
                if (name.equals("Left")) {
                    currentTurnAngle += turnAngle;
                }
                if (name.equals("Right")) {
                    currentTurnAngle -= turnAngle;
                }
                if (name.equals("Up")) {
                    currentMove += 1;
                }
                if (name.equals("Down")) {
                    currentMove -= 1;
                }

            }
        } else {
            if (camMode==1) {
                if (name.equals("Left")) {
                    currentTurnAngle -= turnAngle;
                }
                if (name.equals("Right")) {
                    currentTurnAngle += turnAngle;
                }
                if (name.equals("Up")) {
                    currentMove -= 1;
                }
                if (name.equals("Down")) {
                    currentMove += 1;
                }

            }
        }
    }

}