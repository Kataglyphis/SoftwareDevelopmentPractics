package edu.kit.valaris.rendering.tick.dynamics.properties;

import com.jme3.app.state.AbstractAppState;
import edu.kit.valaris.tick.properties.IProperty;

import java.util.HashMap;
import java.util.Map;

/**
 * Keeps a reference on every known {@link IPropertyProcessorFactory}.
 *
 * @author Frederik Lingg
 */
public class PropertyProcessorFactoryRegister extends AbstractAppState {

    /**
     * Contains all known {@link IPropertyProcessorFactory}s.
     */
    private Map<Class<? extends IProperty>, IPropertyProcessorFactory> m_factories;

    /**
     * Creates a new {@link PropertyProcessorFactoryRegister} with no {@link IPropertyProcessorFactory}s contained.
     */
    public PropertyProcessorFactoryRegister() {
        m_factories = new HashMap<>();
    }

    /**
     * Sets the {@link IPropertyProcessorFactory} to use for a certain {@link IProperty}.
     * @param property the {@link IProperty}.
     * @param factory the {@link IPropertyProcessorFactory}.
     */
    public void setPropertyProcessorFactory(Class<? extends IProperty> property, IPropertyProcessorFactory factory) {
        m_factories.put(property, factory);
    }

    /**
     * Searches an {@link IPropertyProcessorFactory} to use with the given {@link IProperty}.
     * @param property the {@link IProperty}.
     * @return the {@link IPropertyProcessorFactory} to use.
     */
    public IPropertyProcessorFactory getPropertyProcessorFactory(Class<? extends IProperty> property) {
        return m_factories.get(property);
    }
}
