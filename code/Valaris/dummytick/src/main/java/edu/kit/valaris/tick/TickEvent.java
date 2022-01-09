package edu.kit.valaris.tick;

import java.util.List;

/**
 * An event that occured on a {@link Tick}.
 */
public class TickEvent {

    /**
     * Represents the action of adding a new {@link DynamicGameObject}.
     */
    public static final String EVENT_DYNAMIC_GAME_OBJECT_ADDED = "addDGO";

    /**
     * Represents the action of removing a {@link DynamicGameObject}.
     */
    public static final String EVENT_DYNAMIC_GAME_OBJECT_REMOVED = "removeDGO";

    /**
     * Represents the action of pausing the game.
     */
    public static final String EVENT_GAME_PAUSED = "pause";

    /**
     * Represents the action of the game ending.
     */
    public static final String EVENT_GAME_FINISHED = "finished";

    /**
     * the action that triggered this {@link TickEvent}.
     */
    private String m_action;

    /**
     * All parameters necessary to process this {@link TickEvent}.
     */
    private List<Object> m_params;

    /**
     * Creates a new {@link TickEvent} from the given parameters.
     * @param action the action that triggered this {@link TickEvent}
     * @param params all parameters necessary to process this {@link TickEvent}
     */
    public TickEvent(String action, List<Object> params) {
        m_action = action;
        m_params = params;
    }

    /**
     * Accesses the action that triggered this {@link TickEvent}.
     * @return a {@link String} representing the action.
     */
    public String getAction() {
        return m_action;
    }

    /**
     * Accesses the parameters of this {@link TickEvent}.
     * @return all parameters necessary to process this {@link TickEvent}.
     */
    public List<Object> getParams() {
        return m_params;
    }
}
