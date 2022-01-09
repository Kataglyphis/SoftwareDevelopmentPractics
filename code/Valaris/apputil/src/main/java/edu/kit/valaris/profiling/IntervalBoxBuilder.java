package edu.kit.valaris.profiling;

import de.lessvoid.nifty.builder.ControlBuilder;

/**
 * ControlBuilder used to build {@link IntervalBox}es
 */
public class IntervalBoxBuilder extends ControlBuilder {

    /**
     * Creates a new {@link IntervalBoxBuilder}.
     * @param id the id of the element to build.
     */
    public IntervalBoxBuilder(String id) {
        super(id, "intervalbox");
    }
}
