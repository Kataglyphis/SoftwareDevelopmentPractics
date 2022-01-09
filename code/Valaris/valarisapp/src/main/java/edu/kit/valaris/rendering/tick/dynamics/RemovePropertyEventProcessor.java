package edu.kit.valaris.rendering.tick.dynamics;

import edu.kit.valaris.tick.DynamicGameObject;
import edu.kit.valaris.tick.DynamicGameObjectEvent;
import edu.kit.valaris.tick.properties.IProperty;

/**
 * Used to process {@link DynamicGameObjectEvent}s triggered by removing an {@link IProperty}.
 *
 * @author Frederik Lingg
 */
public class RemovePropertyEventProcessor implements IDynamicGameObjectEventProcessor {

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
            throw new IllegalStateException("EventProcessor used without being set to a DynamicGameObjectProcessor");
        }

        m_dgoProcessor.removePropertyProcessor((int) event.getParams().get(0));
    }
}
