package edu.kit.valaris.rendering.tick.dynamics;

import com.jme3.scene.Node;
import edu.kit.valaris.rendering.tick.dynamics.properties.IPropertyProcessor;
import edu.kit.valaris.rendering.tick.dynamics.properties.IPropertyProcessorFactory;
import edu.kit.valaris.rendering.tick.dynamics.properties.PropertyProcessorFactoryRegister;
import edu.kit.valaris.tick.DynamicGameObject;
import edu.kit.valaris.tick.DynamicGameObjectEvent;
import edu.kit.valaris.tick.properties.IProperty;

/**
 * Used to process {@link DynamicGameObjectEvent}s triggered by adding a new {@link IProperty}.
 *
 * @author Frederik Lingg
 */
public class AddPropertyEventProcessor implements IDynamicGameObjectEventProcessor {

    /**
     * The {@link DynamicGameObjectProcessor} using this {@link IDynamicGameObjectEventProcessor}.
     */
    private DynamicGameObjectProcessor m_dgoProcessor;

    @Override
    public void setDynamicGameObjectProcessor(DynamicGameObjectProcessor processor) {
        m_dgoProcessor = processor;
    }

    @Override
    public void process(DynamicGameObjectEvent event, DynamicGameObject dgo) {
        if (m_dgoProcessor == null) {
            throw new IllegalStateException("EventProcessor used without being set to a DynamicGameObjectProcessor.");
        }

        if (m_dgoProcessor.getTickProcessor() == null) {
            throw new IllegalStateException("DynamicGameObjectProcessor not set to a TickProcessor.");
        }

        PropertyProcessorFactoryRegister factories = m_dgoProcessor.getTickProcessor()
                .getAppStateManager().getState(PropertyProcessorFactoryRegister.class);

        if (factories != null) {
            int id = (int) event.getParams().get(0);
            IProperty property = dgo.getProperty(id);

            if (property != null) {
                IPropertyProcessorFactory factory = factories.getPropertyProcessorFactory(property.getClass());

                //create property processor if possible
                if (factory != null) {
                    IPropertyProcessor processor = factory.newProcessor((Node) m_dgoProcessor.getSpatial());

                    m_dgoProcessor.addPropertyProcessor(id, processor);
                }
            }
        } else {
            throw new IllegalStateException("PropertyProcessorFactoryRegister not existing");
        }
    }
}
