package edu.kit.valaris.rendering.tick.dynamics.properties;

import com.jme3.scene.Node;
import edu.kit.valaris.assets.AssetProvider;

/**
 * {@link IPropertyProcessorFactory} to create {@link VehiclePropertyProcessor}s.
 *
 * @author Frederik Lingg
 */
public class VehiclePropertyProcessorFactory implements IPropertyProcessorFactory {

    /**
     * The {@link AssetProvider} to use.
     */
    private AssetProvider m_provider;

    /**
     * Creates a new {@link VehiclePropertyProcessorFactory} that uses the given {@link AssetProvider}.
     * @param provider the {@link AssetProvider} to use.
     */
    public VehiclePropertyProcessorFactory(AssetProvider provider) {
        m_provider = provider;
    }

    @Override
    public IPropertyProcessor newProcessor(Node target) {
        return new VehiclePropertyProcessor(target);
    }
}
