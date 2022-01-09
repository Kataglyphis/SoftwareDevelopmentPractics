package edu.kit.valaris.tick;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

/**
 * A Tick contains all {@link DynamicGameObject}s currently active and logs {@link TickEvent}s.
 */
public class Tick {

    /**
     * Contains all active {@link DynamicGameObject}s.
     */
    private List<DynamicGameObject> m_dgos;

    /**
     * Contains all {@link TickEvent}s that occurred since the last call of {@link #sync(Tick)}.
     */
    private List<TickEvent> m_events;

    /**
     * when {@link #sync(Tick)} was last called.
     */
    private long m_time;

    /**
     * Creates a new {@link Tick} with no {@link DynamicGameObject}s and no {@link TickEvent}s.
     */
    protected Tick() {
        m_dgos = new ArrayList<>();
        m_events = new LinkedList<>();

        m_time = System.currentTimeMillis();
    }

    /**
     * Adds a new {@link DynamicGameObject} to this {@link Tick}.
     * @param dgo the {@link DynamicGameObject} to add.
     * @param model the groupkey and key of the model to load ("groupkey/key").
     * @return the index of the added {@link DynamicGameObject}.
     */
    public int addDynamicGameObject(DynamicGameObject dgo, String model) {
        //search for unoccupied id
        int id = -1;
        for (int i = 0; i < m_dgos.size(); i++) {
            if (m_dgos.get(i) == null) {
                id = i;
                break;
            }
        }

        //add dgo to list
        if (id < 0) {
            id = m_dgos.size();
            m_dgos.add(dgo);
        } else {
            m_dgos.set(id, dgo);
        }

        //create event
        List<Object> params = new ArrayList<>();
        params.add(id);
        params.add(model);

        addEvent(new TickEvent(TickEvent.EVENT_DYNAMIC_GAME_OBJECT_ADDED, params));

        return id;
    }

    /**
     * Removes a {@link DynamicGameObject}.
     * @param id the id of the {@link DynamicGameObject} that shall be removed.
     */
    public void removeDynamicGameObject(int id) {
        //if there is no dgo with this id, do nothing
        if (getDynamicGameObject(id) == null) {
            return;
        }

        //set null at given index, so that other indices dont change
        m_dgos.set(id, null);

        //add event
        List<Object> params = new ArrayList<>();
        params.add(id);

        addEvent(new TickEvent(TickEvent.EVENT_DYNAMIC_GAME_OBJECT_REMOVED, params));
    }

    /**
     * Removes the given {@link DynamicGameObject} from this {@link Tick}.
     * Or does nothing if the {@link DynamicGameObject} is not contained in this {@link Tick}.
     * @param dgo the {@link DynamicGameObject} to be removed.
     */
    public void removeDynamicGameObject(DynamicGameObject dgo) {
        int id = m_dgos.indexOf(dgo);
        if (id >= 0) {
            removeDynamicGameObject(id);
        }
    }

    /**
     * Accesses a {@link DynamicGameObject}.
     * @param id the index of the {@link DynamicGameObject}.
     * @return the {@link DynamicGameObject} at the given index.
     */
    public DynamicGameObject getDynamicGameObject(int id) {
        //if id is too large, dont return anything
        if (id >= m_dgos.size()) {
            return null;
        }

        return m_dgos.get(id);
    }

    /**
     * Accesses all {@link DynamicGameObject}s held by this {@link Tick}.
     * @param clean whether empty ids should be included in the list or not
     * @return all {@link DynamicGameObject}s held by this {@link Tick}.
     */
    public List<DynamicGameObject> getDynamicGameObjects(boolean clean) {
        if (clean) {
            //use linked list since it should be faster with this operation
            List<DynamicGameObject> result = new LinkedList<>();
            for (DynamicGameObject dgo : m_dgos) {
                if (dgo != null) {
                    result.add(dgo);
                }
            }

            return result;
        } else {
            return m_dgos;
        }
    }

    /**
     * Adds a new {@link TickEvent} to the event queue.
     * @param event the {@link TickEvent} to be added.
     */
    public void addEvent(TickEvent event) {
        m_events.add(event);
    }

    /**
     * Removes all {@link TickEvent}s from this {@link Tick}.
     * @return the removed {@link TickEvent}s in order.
     */
    public Stream<TickEvent> getEventsAsStream() {
        //copy list to ensure the stream is not affected by later changes to m_events
        Stream<TickEvent> events = new LinkedList<>(m_events).stream();

        //clear event queue
        m_events.clear();
        return events;
    }

    /**
     * The timestamp of this {@link Tick}.
     * @return when {@link #sync(Tick)} was called the last time.
     */
    public long getTime() {
        return m_time;
    }

    /**
     * Synchronizes this {@link Tick} to the given {@link Tick}.
     * @param target the {@link Tick} to synchronize to.
     */
    public void sync(Tick target) {
        //queue events
        Stream<TickEvent> events = target.getEventsAsStream();
        events.forEachOrdered(event -> m_events.add(event));

        //sync dgos
        List<DynamicGameObject> newDgos = target.getDynamicGameObjects(false);
        int minSize = Math.min(newDgos.size(), m_dgos.size());

        for (int i = 0; i < minSize; i++) {
            DynamicGameObject ndgo = newDgos.get(i);
            if (ndgo != null) {
                if (m_dgos.get(i) == null) {
                    m_dgos.set(i, new DynamicGameObject(ndgo.getType()));
                }

                m_dgos.get(i).sync(ndgo);
            } else {
                m_dgos.set(i, null);
            }
        }

        //handle different counts of dgos
        if (newDgos.size() > m_dgos.size()) {
            //if new dgos were added, add them here aswell
            for (int i = minSize; i < newDgos.size(); i++) {
                if (newDgos.get(i) != null) {
                    //deepcopy dgo
                    DynamicGameObject dgo = new DynamicGameObject(newDgos.get(i).getType());
                    dgo.sync(newDgos.get(i));
                    m_dgos.add(dgo);
                } else {
                    m_dgos.add(null);
                }
            }
        } else if (m_dgos.size() > newDgos.size()) {
            //if less dgos in target, remove ones that are unnecessary
            while (m_dgos.size() > newDgos.size()) {
                m_dgos.remove(newDgos.size());
            }
        }

        //update timestamp
        m_time = System.currentTimeMillis();
    }
}
