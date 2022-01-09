package edu.kit.valaris.tick.properties;

import edu.kit.valaris.tick.DynamicGameObject;

/**
 * IPropertys are used to represent certain properties of {@link DynamicGameObject}s.
 */
public interface IProperty {

    /**
     * Creates a Deep-Copy ot this {@link IProperty}.
     * @return a Deep-Copy if this {@link IProperty}.
     */
    IProperty deepCopy();

    /**
     * Checks if this {@link IProperty} can be synced with the target {@link IProperty}.
     * @param target the {@link IProperty} to sync to.
     * @return whether this {@link IProperty} can sync with the given {@link IProperty}.
     */
    boolean canSync(IProperty target);


    /**
     * Synchronizes this {@link IProperty} with the given {@link IProperty}.
     * @param target the {@link IProperty} to synchronize to.
     * @throws IllegalStateException if this {@link IProperty} cannot sync with the given {@link IProperty}
     */
    void sync(IProperty target) throws IllegalStateException;
}
