package edu.kit.valaris.rendering.tick;

import edu.kit.valaris.tick.TickEvent;

/**
 * Used to process {@link TickEvent}s.
 *
 * @author Frederik Lingg
 */
public interface ITickEventProcessor {

    /**
     * Sets the {@link TickProcessor} that uses this {@link ITickEventProcessor}.
     * @param processor the {@link TickProcessor}.
     */
    public void setTickProcessor(TickProcessor processor);

    /**
     * Processes the given {@link TickEvent}.
     * @param event the {@link TickEvent} to process.
     */
    public void process(TickEvent event);
}
