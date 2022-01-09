package edu.kit.valaris.rendering.tick.dynamics;

import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;
import edu.kit.valaris.rendering.tick.TickProcessor;
import edu.kit.valaris.rendering.tick.dynamics.properties.IPropertyProcessor;
import edu.kit.valaris.tick.DynamicGameObject;
import edu.kit.valaris.tick.DynamicGameObjectEvent;
import edu.kit.valaris.tick.properties.IProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Used to process a {@link DynamicGameObject}.
 * Note that one DynamicGameObjectProcessor should only be used for one specific {@link DynamicGameObject}
 *
 * @author Frederik Lingg
 */
public class DynamicGameObjectProcessor extends AbstractControl {

    /**
     * The {@link TickProcessor} that uses this {@link DynamicGameObjectProcessor}.
     */
    private TickProcessor m_tickProcessor;

    /**
     * Contains an {@link IPropertyProcessor} for each {@link IProperty}.
     * of the {@link DynamicGameObject} that is processed
     */
    private List<IPropertyProcessor> m_propertyProcessors;

    /**
     * Contains an {@link IDynamicGameObjectEventProcessor} for each action that triggers a
     * {@link DynamicGameObjectEvent}.
     */
    private Map<String, IDynamicGameObjectEventProcessor> m_eventProcessors;

    /**
     * Creates a new {@link DynamicGameObjectProcessor} with no {@link IPropertyProcessor}s and no {@link IDynamicGameObjectEventProcessor}s.
     */
    public DynamicGameObjectProcessor() {
        m_propertyProcessors = new ArrayList<>();

        m_eventProcessors = new HashMap<>();
    }

    /**
     * Sets the {@link TickProcessor} that uses this {@link DynamicGameObjectProcessor}.
     * @param tickProcessor the {@link TickProcessor}
     */
    public void setTickProcessor(TickProcessor tickProcessor) {
        m_tickProcessor = tickProcessor;
    }

    /**
     * Accesses the {@link TickProcessor}.
     * @return the {@link TickProcessor} that uses this {@link DynamicGameObjectProcessor}.
     */
    public TickProcessor getTickProcessor() {
        return m_tickProcessor;
    }

    /**
     * Sets the {@link IPropertyProcessor} used for the {@link IProperty} with the given index.
     * @param property the index of the {@link IProperty}.
     * @param processor the {@link IPropertyProcessor} to use.
     */
    public void addPropertyProcessor(int property, IPropertyProcessor processor) {
        //ensure the id exists
        while (property >= m_propertyProcessors.size()) {
            m_propertyProcessors.add(null);
        }

        //set processor
        m_propertyProcessors.set(property, processor);
    }

    /**
     * Removes the {@link IPropertyProcessor} at the given index.
     * @param index the index of the {@link IPropertyProcessor} to remove.
     */
    public void removePropertyProcessor(int index) {
        //if index does not exist do nothing
        if (index >= m_propertyProcessors.size()) {
            return;
        }

        //set as null to not change other indices
        m_propertyProcessors.set(index, null);
    }

    /**
     * Sets the {@link IDynamicGameObjectEventProcessor} for the given action.
     * @param action the action.
     * @param processor the {@link IDynamicGameObjectEventProcessor}.
     */
    public void setEventProcessor(String action, IDynamicGameObjectEventProcessor processor) {
        processor.setDynamicGameObjectProcessor(this);
        m_eventProcessors.put(action, processor);
    }

    /**
     * Processes the {@link DynamicGameObject} by first processing its events, and then its {@link IProperty}s.
     * @param dgo the {@link DynamicGameObject} to process.
     */
    public void processDynamicGameObject(DynamicGameObject dgo) {
        //process events first
        Stream<DynamicGameObjectEvent> events = dgo.getEventsAsStream();
        events.forEachOrdered(event -> m_eventProcessors.get(event.getAction()).process(event, dgo));

        //process IProperties
        //m_propertyProcessors is always at least as long as there are properties in the dgo
        List<IProperty> properties = dgo.getProperties();
        for (int i = 0; i < m_propertyProcessors.size(); i++) {
            if (m_propertyProcessors.get(i) != null) {
                if (properties.get(i) != null) {
                    m_propertyProcessors.get(i).process(properties.get(i), dgo,this);
                } else {
                    //this should never happen
                    throw new IllegalStateException("IPropertyProcessor without IProperty at index " + i);
                }
            }
        }
    }

    @Override
    protected void controlUpdate(float tpf) {
        //no need to update anything
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        //no need to render anything
    }
}
