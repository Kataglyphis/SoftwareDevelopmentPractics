package edu.kit.valaris.tick;

import edu.kit.valaris.tick.properties.IProperty;

import java.util.List;

/**
 * Used to represent events that occur on a {@link DynamicGameObject}.
 */
public class DynamicGameObjectEvent {

    /**
     * String to represent the action of adding an {@link IProperty}.
     */
    public static final String EVENT_ADD_PROPERTY = "addProperty";

    /**
     * String to represent the action of removing an {@link IProperty}.
     */
    public static final String EVENT_REMOVE_PROPERTY = "removeProperty";

    /**
     * The action that triggered this {@link DynamicGameObjectEvent}.
     */
    private String m_action;

    /**
     * Contains all parameters necessary to process this {@link DynamicGameObjectEvent}.
     */
    private List<Object> m_params;

    /**
     * Creates a new {@link DynamicGameObjectEvent}.
     * @param action the action that triggered this event.
     * @param params all parameters necessary to process this event.
     */
    public DynamicGameObjectEvent(String action, List<Object> params) {
        m_action = action;
        m_params = params;
    }

    /**
     * Access to the Action of this {@link DynamicGameObjectEvent}.
     * @return the action that triggered this {@link DynamicGameObjectEvent}.
     */
    public String getAction() {
        return m_action;
    }

    /**
     * Access to the parameters of this {@link DynamicGameObjectEvent}.
     * @return all parameters necessary to process this {@link DynamicGameObjectEvent}.
     */
    public List<Object> getParams() {
        return m_params;
    }
}
