package edu.kit.valaris.rendering.tick;

import com.jme3.app.SimpleApplication;
import com.jme3.light.AmbientLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Transform;
import edu.kit.valaris.assets.AssetProvider;
import edu.kit.valaris.assets.JsonInfoLoader;
import edu.kit.valaris.assets.LoadingCallback;
import edu.kit.valaris.rendering.tick.dynamics.properties.PropertyProcessorFactoryRegister;
import edu.kit.valaris.rendering.tick.dynamics.properties.TransformPropertyProcessorFactory;
import edu.kit.valaris.tick.DynamicGameObject;
import edu.kit.valaris.tick.DynamicGameObjectType;
import edu.kit.valaris.tick.TickEvent;
import edu.kit.valaris.tick.Ticker;
import edu.kit.valaris.tick.properties.TransformProperty;

public class TestTickProcessor extends SimpleApplication {

    public static void main(String[] args) {
        TestTickProcessor app = new TestTickProcessor();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        //load assets
        AssetProvider provider = new AssetProvider(getAssetManager());
        provider.setInfoLoader("json", new JsonInfoLoader());

        LoadingCallback callback = new LoadingCallback() {
            @Override
            public void setProgress(double progress, String message) {
                System.out.println("Loading " + message + ": " + progress + "%");
            }

            @Override
            public void finished() {
            }
        };

        provider.load("/edu/kit/valaris/assets/TestAssetPack.json", callback);
        getStateManager().attach(provider);

        //add necessary appstates
        getStateManager().attach(Ticker.getInstance());

        PropertyProcessorFactoryRegister register = new PropertyProcessorFactoryRegister();
        register.setPropertyProcessorFactory(TransformProperty.class, new TransformPropertyProcessorFactory());
        getStateManager().attach(register);

        TickProcessor tickProcessor = new TickProcessor();
        tickProcessor.setEventProcessor(TickEvent.EVENT_DYNAMIC_GAME_OBJECT_ADDED, new AddDynamicGameObjectEventProzessor());
        tickProcessor.setEventProcessor(TickEvent.EVENT_DYNAMIC_GAME_OBJECT_REMOVED, new RemoveDynamicGameObjectEventProcessor());

        getStateManager().attach(tickProcessor);

        //prepare scene
        rootNode.attachChild(tickProcessor.getDynamicsRoot());
        AmbientLight light = new AmbientLight();
        light.setColor(ColorRGBA.White.mult(0.5f));
        rootNode.addLight(light);

        //add test object
        DynamicGameObject object = new DynamicGameObject(DynamicGameObjectType.DUMMY);
        TransformProperty transform = new TransformProperty(new Transform(), null, 0, null);
        transform.getTransform().setTranslation(2, 1, 1);
        object.addProperty(transform);
        Ticker.getInstance().getSimulationBuffer().addDynamicGameObject(object, "TestGroup/TestAsset");

        DynamicGameObject object1 = new DynamicGameObject(DynamicGameObjectType.DUMMY);
        Ticker.getInstance().getSimulationBuffer().addDynamicGameObject(object1, "TestGroup/TestAsset");

        Ticker.getInstance().swapSimulationTick();
    }
}
