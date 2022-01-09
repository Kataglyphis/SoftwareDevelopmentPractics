package edu.kit.valaris.rendering;

import com.jme3.app.SimpleApplication;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.*;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import com.jme3.shader.VarType;
import com.jme3.util.SkyFactory;
import com.jme3.util.TangentBinormalGenerator;
import edu.kit.valaris.Metadata;
import edu.kit.valaris.rendering.particleEffect.ParticleEffect;
import edu.kit.valaris.threading.JobManager;

import static com.jme3.scene.plugins.gltf.TrackData.Type.Translation;

public class ParticleTest extends SimpleApplication {

    public static void main(String[] args) {
        ParticleTest app = new ParticleTest();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        //setup metadata
        Metadata meta = new Metadata(this, 10, 10);
        getStateManager().attach(meta);

        //setup jobmanager
        JobManager.initInstance(Math.max(Runtime.getRuntime().availableProcessors() - 1, 1), 100);
        getStateManager().attach(JobManager.getInstance());

        //setup for camera
        flyCam.setMoveSpeed(30);
        this.cam.setLocation(new Vector3f(-3,2,-10));
        this.cam.lookAt(new Vector3f(0,0,0), new Vector3f(0f,1f,0));
        viewPort.setBackgroundColor(ColorRGBA.LightGray);

        //initialize skyBox
        getRootNode().attachChild(SkyFactory.createSky(getAssetManager(),
                "edu.kit.valaris/Textures/EmptySpace.dds", SkyFactory.EnvMapType.CubeMap));

        //set up environment
        Box cube1Mesh = new Box(10f,1f,10f);
        Geometry cube1Geo = new Geometry("My Textured Box", cube1Mesh);
        TangentBinormalGenerator.generate(cube1Geo);
        cube1Geo.setLocalTranslation(new Vector3f(0, 3, 0));
        Material cube1Mat = new Material(assetManager,
                "/edu.kit.valaris/materials/PBRLighting.j3md");
        cube1Mat.setFloat("Specular", 0.5f);
        cube1Mat.setTexture("MetallicTexture",
                         assetManager.loadTexture("edu.kit.valaris/Textures/cavefloor1_Metallic.png"));
        cube1Mat.setTexture("RoughnessTexture",
                         assetManager.loadTexture("edu.kit.valaris/Textures/cavefloor1_Roughness.png"));
        cube1Mat.setTexture("ColorTexture",
                         assetManager.loadTexture("edu.kit.valaris/Textures/cavefloor1_Base_Color.png"));
        cube1Geo.setMaterial(cube1Mat);
//        rootNode.attachChild(cube1Geo);

        Box box1Mesh = new Box(1f,1f,1f);
        Geometry box1 = new Geometry("Box", box1Mesh );
        Material box1Mat = new Material(
                assetManager, "/edu.kit.valaris/materials/PBRLighting.j3md");
        box1Mat.setFloat("Metallic", 0);
        box1Mat.setFloat("Roughness", .8f);
        box1.setMaterial(box1Mat);
        box1.setLocalTranslation(4.0f,2f,3.0f);
        rootNode.attachChild(box1);

        Box box2Mesh = new Box(2f,2f,2f);
        Spatial box2 = new Geometry("Box", box2Mesh);
        Material box2Mat = new Material(
                assetManager, "/edu.kit.valaris/materials/PBRLighting.j3md");
        box2.setMaterial(box2Mat);
        box2Mat.setFloat("Metallic", 0);
        box2Mat.setFloat("Roughness", .6f);
        box2Mat.setColor("BaseColor", ColorRGBA.Blue);
        box2.setLocalTranslation(-5.0f,3f,-5.0f);
        rootNode.attachChild(box2);

        // A bumpy rock with a shiny light effect.
        Sphere sphereMesh = new Sphere(32,32, 2f);
        Geometry sphereGeo = new Geometry("Shiny rock", sphereMesh);
        sphereMesh.setTextureMode(Sphere.TextureMode.Projected);
        TangentBinormalGenerator.generate(sphereMesh);
        Material sphereMat = new Material(assetManager,
                "/edu.kit.valaris/materials/PBRLighting.j3md");
        sphereGeo.setMaterial(sphereMat);
        sphereGeo.setLocalTranslation(0,10,-5);
        sphereGeo.rotate(1.6f, 0, 0);
        rootNode.attachChild(sphereGeo);

        ParticleEffect particleEffect = new ParticleEffect("Explosion", this.assetManager);
        Box box3Mesh = new Box(1f,1f,1f);
        Spatial box3 = new Geometry("Box", box3Mesh);
        box3.setMaterial(particleEffect.getStrategy().getMaterial(this.assetManager));
        Vector3f translation = new Vector3f(0f, 0f, 0f);
        float[] angles = {0, 0, 0};
        Quaternion quaternion = new Quaternion(angles);
        Vector3f scale = new Vector3f(1f, 1f, 1f);
        Transform transform = new Transform(translation, quaternion, scale);

        rootNode.attachChild(box3);

        box3.addControl(particleEffect);
        box3.setLocalTransform(transform);

        // Must add a light to make the lit object visible!
        DirectionalLight dl = new DirectionalLight();
        dl.setColor(ColorRGBA.White);
        dl.setDirection(new Vector3f(-0.1f, -1f, 1).normalizeLocal());
        rootNode.addLight(dl);

        DirectionalLight sun2 = new DirectionalLight();
        sun2.setColor(ColorRGBA.White);
        sun2.setDirection(new Vector3f(-.5f,-.5f,-.5f).normalizeLocal());
        rootNode.addLight(sun2);

        DirectionalLight sun3 = new DirectionalLight();
        sun3.setColor(ColorRGBA.White);
        sun3.setDirection(new Vector3f(0f, 0f, -1).normalizeLocal());
        rootNode.addLight(sun3);

        DirectionalLight sun4 = new DirectionalLight();
        sun4.setColor(ColorRGBA.White);
        sun4.setDirection(new Vector3f(0f, 1f, 0).normalizeLocal());
        rootNode.addLight(sun4);

        DirectionalLight sun5 = new DirectionalLight();
        sun5.setColor(ColorRGBA.White);
        sun5.setDirection(new Vector3f(0f, 0f, -1).normalizeLocal());
        rootNode.addLight(sun5);

        AmbientLight al = new AmbientLight();
        al.setColor(ColorRGBA.White);
        rootNode.addLight(al);

    }
}
