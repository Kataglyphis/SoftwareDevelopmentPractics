package edu.kit.valaris.generation;

import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AppStateManager;
import com.jme3.app.state.BaseAppState;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.Light;
import edu.kit.valaris.assets.AssetProvider;
import edu.kit.valaris.assets.JsonInfoLoader;
import edu.kit.valaris.assets.LoadingCallback;

import java.util.ArrayList;

public class TestMain extends SimpleApplication implements ActionListener {


    private AppStateManager appStateManager;
    private ArrayList<BaseAppState> appStates;
    private int currentAppStateIndex;
    public SceneCursor sceneCursor;



    public static void main(String[] args) {
        TestMain app = new TestMain();
        app.start();
    }

    @Override
    public void simpleInitApp() {

        appStateManager = new AppStateManager(this);
        appStates = new ArrayList<>();

        /*BaseAppState appStateRoadGeneration = new AppStateRoadTest();
        BaseAppState appStateMapBodyTest = new AppStateMapBodyTest();
        BaseAppState appStateTunnelRoadTest = new AppStateTunnelRoadTest();
        BaseAppState appStateRoomRoadTest = new AppStateRoomRoadTest();*/
        BaseAppState appStateDomeGen= new AppStateDomeGen();
        BaseAppState appStateMapGen= new AppStateMapGen();
        BaseAppState appStateMapBodyTest= new AppStateMapBodyTest();
        /*BaseAppState appStateTunnelTest= new AppStateTunnelTest();
        BaseAppState appStateRoomTest= new AppStateRoomTest();*/



        AssetProvider provider = new AssetProvider(getAssetManager());
        provider.setInfoLoader("json", new JsonInfoLoader());
        provider.load("/edu.kit.valaris/assets/AssetPack.json",
                new LoadingCallback() {
                    @Override
                    public void setProgress(double progress, String message) {
                        //Do Nothing
                    }

                    @Override
                    public void finished() {
                        //Do Nothing
                    }
                });
        getStateManager().attach(provider);

        appStates.add(appStateMapBodyTest);
        appStates.add(appStateDomeGen);
        appStates.add(appStateMapGen);
        //appStates.add(appStateMapBodyTest);
        //appStates.add(appStateRoomRoadTest);
        //appStates.add(appStateTunnelRoadTest);
        //appStates.add(appStateRoadGeneration);
        //appStates.add(appStateTunnelTest);
        currentAppStateIndex=0;

        appStateManager.attach(appStates.get(currentAppStateIndex));


        setUpKeys();
    }


    private void setUpKeys() {
        inputManager.addMapping("Togle", new KeyTrigger(KeyInput.KEY_TAB));
        inputManager.addListener(this, "Togle");
    }

    @Override
    public void simpleUpdate(float tpf) {
        appStateManager.update(tpf);

    }


    @Override
    public void onAction(String binding, boolean isPressed, float tpf) {
        if (binding.equals("Togle") && isPressed) {
            appStateManager.detach(appStates.get(currentAppStateIndex));
            currentAppStateIndex = (currentAppStateIndex + 1) % appStates.size();
            rootNode.detachAllChildren();
            for(Light light : rootNode.getWorldLightList()) {
                rootNode.removeLight(light);
            }
            appStateManager.attach(appStates.get(currentAppStateIndex));
        }
    }
}