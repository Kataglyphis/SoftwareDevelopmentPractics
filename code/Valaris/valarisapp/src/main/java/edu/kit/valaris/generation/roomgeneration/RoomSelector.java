package edu.kit.valaris.generation.roomgeneration;

import com.jme3.math.Vector2f;
import edu.kit.valaris.assets.AssetProvider;
import edu.kit.valaris.generation.GenerationConfig;
import edu.kit.valaris.generation.GeneratorSettings;

import java.util.Set;

/**
 * Implementation of {@link IRoomGenerator}. Selects a random {@link AbstractRoomGenerator} and
 * for generating a room.
 * @author Sidney Hansen
 */
public class RoomSelector implements IRoomGenerator {

    private GenerationConfig m_generationConfig;

    private AssetProvider m_assetProvider;

    private int m_vehicleCount;

    /**
     *
     * Creates a new {@link RoomSelector}.
     *
     * @param generationConfig {@link GenerationConfig} used the generation.
     * @param assetProvider {@link AssetProvider} used to obtain assets.
     * @param vehicleCount number of vehicles.
     */
    public RoomSelector(GenerationConfig generationConfig, AssetProvider assetProvider, int vehicleCount) {
        this.m_generationConfig = generationConfig;
        this.m_assetProvider = assetProvider;
        this.m_vehicleCount = vehicleCount;
    }

    @Override
    public IRoom generate(int seed, float boundingSphereRadius, Vector2f entryWidth, float exitAngle, Set<GeneratorSettings> flags) {

        // More Room Generator Types may be added.
        AbstractRoomGenerator roomGenerator = new DynamicRoomGenerator(m_generationConfig, m_assetProvider, m_vehicleCount);

        return roomGenerator.generateRoom(seed, boundingSphereRadius, entryWidth, exitAngle, flags);
    }
}
