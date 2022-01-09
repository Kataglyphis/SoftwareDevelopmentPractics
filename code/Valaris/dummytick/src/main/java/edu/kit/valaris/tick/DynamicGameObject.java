package edu.kit.valaris.tick;

import edu.kit.valaris.tick.properties.IProperty;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Stream;

/**
 * Represents a dynamic object in the simulation.
 * This is done by adding appropriate {@link IProperty}s to this DynamicGameObject.
 *
 * @author Frederik Lingg, Tobias Knorr, Jonas Frey
 * @version 1.0
 */
public class DynamicGameObject {

    /**
     * Lock used to synchronize access of structure changing methods
     */
    private Lock m_syncLock;

    /**
     * All {@link IProperty}s currently active on this {@link DynamicGameObject}.
     */
    private List<IProperty> m_properties;

    /**
     * All {@link DynamicGameObjectEvent}s that occurred since the last call of {@link #getEventsAsStream()}.
     */
    private List<DynamicGameObjectEvent> m_events;

    /**
     * The {@link DynamicGameObjectType} of this {@link DynamicGameObject}.
     */
    private DynamicGameObjectType m_type;

    /**
     * Creates a new {@link DynamicGameObject} with no {@link IProperty}s and no {@link DynamicGameObjectEvent}s.
     */
    public DynamicGameObject(DynamicGameObjectType type) {
        this.m_type = type;
        this.m_properties = new ArrayList<>();
        this.m_events = new LinkedList<>();
        this.m_syncLock = new ReentrantLock();
    }

    /**
     * Access to the {@link DynamicGameObjectType} of this {@link DynamicGameObject}.
     * @return the {@link DynamicGameObjectType} of this {@link DynamicGameObject}.
     */
    public DynamicGameObjectType getType() {
        return m_type;
    }

    /**
     * Adds the given {@link IProperty} to this {@link DynamicGameObject}.
     * @param property the {@link IProperty} to add.
     * @param index the index where the linear search for the index of the {@link IProperty} shall begin.
     * @return the index of the added {@link IProperty}.
     */
    public int addProperty(IProperty property, int index) {
        m_syncLock.lock();

        //search for potential free space
        int id = -1;
        for (int i = index; i < m_properties.size(); i++) {
            if (m_properties.get(i) == null) {
                id = i;
                break;
            }
        }

        //add the property to the list
        if (id >= 0) {
            m_properties.set(id, property);
        } else {
            id = m_properties.size();
            m_properties.add(property);
        }

        //add new event if property exists
        if (property != null) {
            List<Object> params = new ArrayList<>();
            params.add(id);
            addEvent(new DynamicGameObjectEvent(DynamicGameObjectEvent.EVENT_ADD_PROPERTY, params));
        }

        m_syncLock.unlock();
        return id;
    }

    /**
     * Adds a new {@link IProperty} to this {@link DynamicGameObject}.
     * @param property the {@link IProperty} to add.
     * @return the index of the added {@link IProperty}.
     */
    public int addProperty(IProperty property) {
        return addProperty(property, m_properties.size());
    }

    /**
     * Sets the {@link IProperty} with the given id.
     * @param id the id to set the {@link IProperty}.
     * @param property the {@link IProperty} to set.
     * @return false if the id did not exist, true otherwise
     */
    public boolean setProperty(int id, IProperty property) {
        m_syncLock.lock();

        if (id >= m_properties.size()) {
            //dont forget to unlock before returning
            m_syncLock.unlock();
            return false;
        }

        //if old property exists check if it needs to be removed
        IProperty old = m_properties.get(id);
        boolean newProperty = (old == null) && (property != null);
        boolean differentProperty = (old != null && !old.canSync(property));

        if (differentProperty) {
            List<Object> params = new ArrayList<>();
            params.add(id);
            addEvent(new DynamicGameObjectEvent(DynamicGameObjectEvent.EVENT_REMOVE_PROPERTY, params));
        }

        //switch m_properties
        m_properties.set(id, property);

        //if new property is added, add event
        if (newProperty || differentProperty) {
            List<Object> params0 = new ArrayList<>();
            params0.add(id);
            addEvent(new DynamicGameObjectEvent(DynamicGameObjectEvent.EVENT_ADD_PROPERTY, params0));
        }

        m_syncLock.unlock();
        //everything worked out nicely
        return true;
    }

    /**
     * Removes an {@link IProperty} from this {@link DynamicGameObject}.
     * @param id the index of the {@link IProperty} to remove.
     */
    public void removeProperty(int id) {
        m_syncLock.lock();
        //if no property exists on this id, do nothing
        if (getProperty(id) == null) {
            m_syncLock.unlock();
            return;
        }

        //dont actually remove from list, so that other indices do not change
        m_properties.set(id, null);

        //add event
        List<Object> params = new ArrayList<>();
        params.add(id);
        addEvent(new DynamicGameObjectEvent(DynamicGameObjectEvent.EVENT_REMOVE_PROPERTY, params));

        m_syncLock.unlock();
    }

    /**
     * Accesses a certain {@link IProperty} of this {@link DynamicGameObject}.
     * @param id the id of the {@link IProperty}.
     * @return the {@link IProperty} with the given id or null if no {@link IProperty} with the given id exists.
     */
    public IProperty getProperty(int id) {
        if (id < m_properties.size()) {
            return m_properties.get(id);
        }

        return null;
    }

    /**
     * Checks whether there is a {@link IProperty} at the given index or not.
     * @param id the index to check.
     * @return whether there is a {@link IProperty} at the index.
     */
    public boolean hasProperty(int id) {
        return getProperty(id) != null;
    }

    /**
     * Access to all {@link IProperty}s.
     * @return all {@link IProperty}s that are active on this {@link DynamicGameObject}.
     */
    public List<IProperty> getProperties() {
        return m_properties;
    }

    /**
     * Adds a new {@link DynamicGameObjectEvent} to this {@link DynamicGameObject}.
     * @param event the {@link DynamicGameObjectEvent} to add.
     */
    public void addEvent(DynamicGameObjectEvent event) {
        m_syncLock.lock();
        m_events.add(event);
        m_syncLock.unlock();
    }

    /**
     * Removes all {@link DynamicGameObjectEvent}s of this {@link DynamicGameObject}.
     * @return a {@link Stream} containing all {@link DynamicGameObjectEvent}s of this {@link DynamicGameObject}.
     */
    public Stream<DynamicGameObjectEvent> getEventsAsStream() {
        m_syncLock.lock();
        //save m_events in stream.
        // duplicate list so the stream does still contain the elements of the list after it is cleared
        Stream<DynamicGameObjectEvent> eventStream = new LinkedList<>(m_events.subList(0, m_events.size())).stream();

        //clear event list
        m_events.clear();

        m_syncLock.unlock();
        return eventStream;
    }

    /**
     * Synchronizes this {@link DynamicGameObject} with the given {@link DynamicGameObject} in 2 steps:.
     * 1. get m_events and add them to this {@link DynamicGameObject}.
     * 2. synchronize all {@link IProperty}s, new {@link IProperty}s are copied.
     * @param target the {@link DynamicGameObject} to synchronize with.
     */
    public void sync(DynamicGameObject target) {
        m_syncLock.lock();

        //queue new m_events
        Stream<DynamicGameObjectEvent> newEvents = target.getEventsAsStream();
        newEvents.forEachOrdered(event -> m_events.add(event));

        //sync m_properties
        List<IProperty> newProperties = target.getProperties();

        int minSize = Math.min(newProperties.size(), m_properties.size());
        for (int i = 0; i < minSize; i++) {
            IProperty property = newProperties.get(i);
            if (property != null) {
                //if property can sync do it
                if ((m_properties.get(i) != null) && (m_properties.get(i).canSync(property))) {
                    m_properties.get(i).sync(property);
                } else {
                    //if property cannot be synced, use deepcopy
                    m_properties.set(i, property.deepCopy());
                }
            } else {
                //if property is not existent, set given index as null
                m_properties.set(i, null);
            }
        }

        //handle size differences
        if (newProperties.size() < m_properties.size()) {
            while (newProperties.size() < m_properties.size()) {
                m_properties.remove(newProperties.size());
            }
        } else {
            for (int i = minSize; i < newProperties.size(); i++) {
                if (newProperties.get(i) != null) {
                    m_properties.add(newProperties.get(i).deepCopy());
                } else {
                    m_properties.add(null);
                }
            }
        }

        //copy m_type
        m_type = target.getType();

        m_syncLock.unlock();
    }
}
