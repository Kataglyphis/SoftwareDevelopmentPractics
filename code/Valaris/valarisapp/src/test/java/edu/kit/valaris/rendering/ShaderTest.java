package edu.kit.valaris.rendering;

import com.jme3.app.SimpleApplication;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.system.AppSettings;
import com.jme3.util.SkyFactory;
import com.jme3.util.TangentBinormalGenerator;

public class ShaderTest extends SimpleApplication {

    public static void main(String[] args) {
        AppSettings settings = new AppSettings(true);
        ShaderTest app = new ShaderTest();
        app.setSettings(settings);
        app.start();
    }

    @Override
    public void simpleInitApp() {
        flyCam.setMoveSpeed(10);

        Spatial sky = SkyFactory.createSky(assetManager, "/edu.kit.valaris/Textures/environment.jpg", SkyFactory.EnvMapType.EquirectMap);
        rootNode.attachChild(sky);

        Box cube1Mesh = new Box(10f,1f,10f);
        Geometry cube1Geo = new Geometry("My Textured Box", cube1Mesh);
        TangentBinormalGenerator.generate(cube1Geo);

        cube1Geo.setLocalTranslation(new Vector3f(0, -1, 0));
        Material cube1Mat = new Material(assetManager,
                "/edu.kit.valaris/materials/PBRLighting.j3md");
//        cube1Mat.setColor("BaseColor", ColorRGBA.Black);
        cube1Mat.setFloat("Specular", 0.5f);
        cube1Mat.setTexture("MetallicTexture", assetManager.loadTexture("edu.kit.valaris/Textures/cavefloor1_Metallic.png"));
        cube1Mat.setTexture("RoughnessTexture", assetManager.loadTexture("edu.kit.valaris/Textures/cavefloor1_Roughness.png"));
        cube1Mat.setTexture("ColorTexture", assetManager.loadTexture("edu.kit.valaris/Textures/cavefloor1_Base_Color.png"));
        cube1Mat.setTexture("NormalTexture", assetManager.loadTexture("edu.kit.valaris/Textures/cavefloor1_Normal.png"));
//        cube1Mat.setTexture("EmissiveTexture", assetManager.loadTexture("edu.kit.edu.kit.edu.kit.valaris/Textures/cavefloor1_Base_Color.png"));
//        cube1Mat.setBoolean("IsEquirectMap", true);
        cube1Geo.setMaterial(cube1Mat);
        cube1Geo.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
        rootNode.attachChild(cube1Geo);

        /* Test Sphere.*/
//        for(int x = 0; x < 10; x++) {
//            for(int y = 0; y < 10; y++) {
//                Spatial model = assetManager.loadModel("edu.kit.valaris/showcaseMesh.obj");
//                TangentBinormalGenerator.generate(model);
//                Material sphereMat = new Material(assetManager, "/edu.kit.valaris/materials/PBRLighting.j3md");
////                sphereMat.setFloat("Metallic", 1.0f);
////                sphereMat.setFloat("Roughness", (float) x / 10.0f + 0.1f);
////                sphereMat.setFloat("Specular", (float) y / 9.0f);
//                sphereMat.setTexture("NormalTexture", assetManager.loadTexture("edu.kit.valaris/Textures/rustediron2_normal.png"));
//                sphereMat.setTexture("MetallicTexture", assetManager.loadTexture("edu.kit.valaris/Textures/rustediron2_metallic.png"));
//                sphereMat.setTexture("RoughnessTexture", assetManager.loadTexture("edu.kit.valaris/Textures/rustediron2_roughness.png"));
//                sphereMat.setTexture("ColorTexture", assetManager.loadTexture("edu.kit.valaris/Textures/rustediron2_basecolor.png"));
////                sphereMat.setTexture("EnvMap", assetManager.loadTexture("edu.kit.valaris/Textures/environment.jpg"));
//                sphereMat.setBoolean("IsEquirectMap", true);
//                model.setMaterial(sphereMat);
//                model.setLocalTranslation(-13.5f + x * 3,0,-13.5f + y * 3); // Move it a bit
//                model.rotate(0, (float) Math.PI / 2f, 0);
//                model.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
//                rootNode.attachChild(model);
//            }
//        }

//        Node model = (Node) assetManager.loadModel("edu.kit.valaris/Models/Cart/Cart.j3o");
//        model.getChild("Chassis").setMaterial(assetManager.loadMaterial("edu.kit.valaris/Models/Cart/Chassis.j3m"));
//        model.getChild("Seat").setMaterial(assetManager.loadMaterial("edu.kit.valaris/Models/Cart/Seat.j3m"));
//        model.getChild("BreakLight").setMaterial(assetManager.loadMaterial("edu.kit.valaris/Models/Cart/BreakLight.j3m"));
//        model.getChild("Engines").setMaterial(assetManager.loadMaterial("edu.kit.valaris/Models/Cart/Engines.j3m"));
//        model.getChild("Sockets").setMaterial(assetManager.loadMaterial("edu.kit.valaris/Models/Cart/Sockets.j3m"));
//        model.getChild("Thrust").setMaterial(assetManager.loadMaterial("edu.kit.valaris/Models/Cart/Thrust.j3m"));
//        model.getChild("Strip").setMaterial(assetManager.loadMaterial("edu.kit.valaris/Models/Cart/Strip.j3m"));
//        TangentBinormalGenerator.generate(model);
//        model.move(0.0f, 2.0f, 0.0f);
//        model.scale(5);
//        model.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
//        rootNode.attachChild(model);

        /* Must add a light to make the lit object visible */
        DirectionalLight light0 = new DirectionalLight();
        light0.setColor(ColorRGBA.White);
        light0.setDirection(new Vector3f(.5f, -.5f, -.5f).normalize());
        rootNode.addLight(light0);

        DirectionalLight light1 = new DirectionalLight();
        light1.setColor(ColorRGBA.White);
        light1.setDirection(new Vector3f(-.5f, -.5f, -.5f).normalize());
        rootNode.addLight(light1);

        DirectionalLight light2 = new DirectionalLight();
        light2.setColor(ColorRGBA.White);
        light2.setDirection(new Vector3f(.5f, -.5f, .5f).normalize());
        rootNode.addLight(light2);


        //add HDR
//        ToneMapFilter hdr = new ToneMapFilter();
//        hdr.setWhitePoint(new Vector3f(4, 4, 4));

//        DirectionalLightShadowFilter shadow = new DirectionalLightShadowFilter(assetManager, 1024, 1);
//        shadow.setLight(light0);
//
//        FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
//        fpp.addFilter(shadow);
//        getViewPort().addProcessor(fpp);
    }
}