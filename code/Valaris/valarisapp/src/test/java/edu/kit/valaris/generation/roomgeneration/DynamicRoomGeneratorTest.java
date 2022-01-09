package edu.kit.valaris.generation.roomgeneration;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.scene.Node;
import edu.kit.valaris.assets.AssetProvider;
import edu.kit.valaris.generation.GenerationConfig;
import edu.kit.valaris.generation.GeneratorSettings;
import edu.kit.valaris.generation.roadgeneration.RoadCursorProperty;
import edu.kit.valaris.generation.roadgeneration.RoadObject;
import edu.kit.valaris.generation.roadgeneration.RoadObjectProperty;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class DynamicRoomGeneratorTest extends SimpleApplication {

    private Vector2f width;
    private String settings;
    private GenerationConfig generationConfig;
    private DynamicRoomGenerator roomGenerator;
    private AssetProvider assetProvider;

    @Before
    public void setUp() {
        assetProvider = new MockAssetProvider(assetManager);
        width = new Vector2f(10, 5);
        generationConfig = new GenerationConfig();

    }

    @Test
    public void test0GenerateRoom() {
        int vehicleCount = 10;
        int seed = 666;
        float radius = 50;
        float exitAngle = 0;
        Set<GeneratorSettings> flags = new HashSet<>();
        roomGenerator = new DynamicRoomGenerator(generationConfig, assetProvider,vehicleCount);
        IRoom room = roomGenerator.generateRoom(seed, radius, width, exitAngle, flags);
        assertNotNull(room);
    }

    @Test
    public void test1GenerateRoom() {
        int vehicleCount = 10;
        int seed = 666;
        float radius = 40;
        float exitAngle = FastMath.HALF_PI;
        Set<GeneratorSettings> flags = new HashSet<>();
        roomGenerator = new DynamicRoomGenerator(generationConfig, assetProvider,vehicleCount);
        IRoom room = roomGenerator.generateRoom(seed, radius, width, exitAngle, flags);
        assertNotNull(room);
    }

    @Test
    public void test2GenerateRoom() {
        int vehicleCount = 10;
        int seed = 666;
        float radius = 40;
        float exitAngle = -FastMath.HALF_PI;
        Set<GeneratorSettings> flags = new HashSet<>();
        roomGenerator = new DynamicRoomGenerator(generationConfig, assetProvider,vehicleCount);
        IRoom room = roomGenerator.generateRoom(seed, radius, width, exitAngle, flags);
        assertNotNull(room);
    }

    @Test
    public void testGenerateStartRoom() {
        int vehicleCount = 10;
        int seed = 666;
        float radius = 50;
        float exitAngle = 0;
        Set<GeneratorSettings> flags = new HashSet<>();
        flags.add(GeneratorSettings.STARTROOM);
        roomGenerator = new DynamicRoomGenerator(generationConfig, assetProvider,vehicleCount);
        IRoom room = roomGenerator.generateRoom(seed, radius, width, exitAngle, flags);
        assertNotNull(room);

        int startPositionCounter = 0;
        for (RoadObject obj :
                room.getRoad().getRoadObjects()) {
            if(obj.hasProperty(RoadObjectProperty.START_POSITION)) {
                startPositionCounter++;
            }
        }

        assertEquals(startPositionCounter, vehicleCount);

    }

    @Test
    public void testGenerateEndRoom() {
        int vehicleCount = 10;
        int seed = 666;
        float radius = 50;
        float exitAngle = 0;
        Set<GeneratorSettings> flags = new HashSet<>();
        flags.add(GeneratorSettings.ENDROOM);
        roomGenerator = new DynamicRoomGenerator(generationConfig, assetProvider,vehicleCount);
        IRoom room = roomGenerator.generateRoom(seed, radius, width, exitAngle, flags);
        assertNotNull(room);
        assertTrue(room.getRoad().getRoadCursors().get(1).hasProperty(RoadCursorProperty.CHECKPOINT));

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