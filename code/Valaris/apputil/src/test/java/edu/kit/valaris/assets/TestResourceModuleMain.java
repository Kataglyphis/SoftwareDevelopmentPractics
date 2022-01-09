package edu.kit.valaris.assets;

import com.jme3.app.SimpleApplication;
import com.jme3.light.AmbientLight;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Node;
import com.jme3.scene.control.Control;

public class TestResourceModuleMain extends SimpleApplication {

    public static void main(String[] args) {
        SimpleApplication app = new TestResourceModuleMain();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        AssetProvider assetProvider = new AssetProvider(assetManager);
        assetProvider.setInfoLoader("json", new JsonInfoLoader());

        LoadingCallback callback = new LoadingCallback() {
            @Override
            public void setProgress(double progress, String message) {
                System.out.println("Loading " + message + " " + progress + "%");
            }

            @Override
            public void finished() {
                Node asset = assetProvider.provide("TestGroup/TestAsset");
                Control control = asset.getControl(AssetControl.class).getActiveControl("RotateControl");
                ((RotateControl) control).setSpeed(3);

                rootNode.attachChild(asset);
            }
        };

        AmbientLight light = new AmbientLight();
        light.setColor(ColorRGBA.White.mult(0.5f));
        rootNode.addLight(light);

        assetProvider.load("/edu.kit.valaris/assets/TestAssetPack.json", callback);
    }
}
