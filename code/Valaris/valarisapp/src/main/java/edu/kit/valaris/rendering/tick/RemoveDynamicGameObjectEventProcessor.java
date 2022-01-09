package edu.kit.valaris.rendering.tick;

import edu.kit.valaris.tick.TickEvent;
import edu.kit.valaris.tick.DynamicGameObject;

/**
 * Used to process {@link TickEvent}s triggered by removing a {@link DynamicGameObject}.
 *
 * @author Frederik Lingg
 */
public class RemoveDynamicGameObjectEventProcessor implements ITickEventProcessor {

    /**
     * The {@link TickProcessor} that uses this {@link ITickEventProcessor}.
     */
    private TickProcessor m_tickProcessor;

    @Override
    public void setTickProcessor(TickProcessor processor) {
        m_tickProcessor = processor;
    }

    @Override
    public void process(TickEvent event) {
        if (m_tickProcessor == null) {
            throw new IllegalStateException("EventProcessor is being used without being set to a TickProcessor");
        }

        m_tickProcessor.removeDynamicGameObjectProcessor((int) event.getParams().get(0));
    }
}
