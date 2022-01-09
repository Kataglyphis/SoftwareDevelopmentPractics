package edu.kit.valaris.generation.tunnelgeneration;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import edu.kit.valaris.assets.AssetProvider;
import edu.kit.valaris.generation.GenerationConfig;
import edu.kit.valaris.generation.GeneratorSettings;
import edu.kit.valaris.generation.roadgeneration.RoadCursor;
import edu.kit.valaris.generation.roomgeneration.DynamicRoomGenerator;
import edu.kit.valaris.generation.roomgeneration.IRoom;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class TunnelGeneratorTest extends SimpleApplication {

    private Vector2f width;
    private GenerationConfig generationConfig;
    private TunnelGenerator tunnelGenerator;
    private AssetProvider assetProvider;

    @Before
    public void setUp() {
        assetProvider = new MockAssetProvider(assetManager);
        width = new Vector2f(10, 5);
        generationConfig = new GenerationConfig();


    }

    @Test
    public void testGenerate() {
        int seed = 666;
        Set<GeneratorSettings> flags = new HashSet<>();
        tunnelGenerator = new TunnelGenerator(generationConfig, assetProvider);
        Vector3f[] targetPositions = {
                new Vector3f(0, 0, 50),
                new Vector3f(50, 0, 50),
                new Vector3f(-50, 0, 50),
                new Vector3f(0, 0, 100),
                new Vector3f(0, 50, 100),
                new Vector3f(0, 50,50),
                new Vector3f(0, -50,50),
        };
        float[][] angles = {
                {0f,0f},
                {FastMath.HALF_PI,0},
                {-FastMath.HALF_PI,0},
                {FastMath.HALF_PI*(45f/90f),0},
                {-FastMath.HALF_PI*(45f/90f),0},
                {0,FastMath.HALF_PI*(10f/90f)},
                {0,-FastMath.HALF_PI*(10f/90f)}
        };
        for (int i = 0; i < targetPositions.length; i++) {
            ITunnel tunnel = tunnelGenerator.generate(
                    seed, targetPositions[i], angles[i][0], angles[i][1], width, flags);

            System.out.println("Tunnel with angle: "+angles[i][0]+", "+angles[i][1]
                    +" and target position: "+targetPositions[i].toString()+
                    " is "+((tunnel==null)?"null.":"not null."));
            assertNotNull(tunnel);
            RoadCursor lastRoadCursor = tunnel.getRoad().getLastRoadCursor();
            Vector3f actualPosition = lastRoadCursor.getPosition();

            // Must be (very close to) zero
            System.out.println("Position offset: "+actualPosition.subtract(targetPositions[i]).length());

            assertTrue(actualPosition.subtract(targetPositions[i]).length() <= 0.01);

            float xzAngleOffset = (float) Math.min(Math.abs(lastRoadCursor.getXZAngle() - angles[i][0]),
                    Math.abs(Math.abs(lastRoadCursor.getXZAngle() - angles[i][0])-2*Math.PI));

            float yAngleOffset = (float) Math.min(Math.abs(lastRoadCursor.getYAngle() - angles[i][1]),
                    Math.abs(Math.abs(lastRoadCursor.getYAngle() - angles[i][1])-2*Math.PI));

            System.out.println("XZAngle offset: "+xzAngleOffset);

            System.out.println("YAngle offset: "+yAngleOffset);


            assertTrue(xzAngleOffset <= 0.01);

            assertTrue(yAngleOffset <= 0.01);
        }

    }

    @Override
    public void simpleInitApp() {
        // Do nothing
    }


    private class MockAssetProvider extends AssetProvider {
        /**
         * Creates a new {@link AssetProvider} using the given {@link AssetManager}.
         *
         * @param assetManager the {@link AssetManager} to use for loading resources.
         */
        public MockAssetProvider(AssetManager assetManager) {
            super(assetManager);
        }
        @Override
        public Node provide(String key) {
            // Dummy Implementation
            return new Node("DummyNode");
        }
    }

}