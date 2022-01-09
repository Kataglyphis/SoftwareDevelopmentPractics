package edu.kit.valaris.profiling;

import com.jme3.math.ColorRGBA;

import java.util.HashMap;
import java.util.Map;

/**
 * A section used by the {@link ProfilingAppState}.
 * A ProfilingSection represents a certain part of the workload and is used to track its duration.
 */
public class ProfilingSection {

    /**
     * Contains the result after ending a {@link ProfilingSection}
     */
    public static final class ProfilingResult {

        /**
         * Contains intervalls of the child profiling sections
         */
        private float[] m_edges;

        /**
         * The Colors of the sections
         */
        private ColorRGBA[] m_colors;

        /**
         * The names of the sections
         */
        private String[] m_names;

        /**
         * The duration of this ProfilingResult in milliseconds
         */
        private float m_duration;

        /**
         * Creates a new {@link ProfilingResult}
         * @param duration the duration of the {@link ProfilingSection}
         * @param color the {@link ColorRGBA} to use for representation of this profiling result
         * @param name the Name to use for this {@link ProfilingResult}
         * @param results the {@link ProfilingResult}s to use as children
         */
        public ProfilingResult(float duration, ColorRGBA color, String name, ProfilingResult... results) {
            m_duration = duration;

            //add counts for one intervall and color, name etc for this result
            int edgeCount = 2;
            int nameCount = 1;
            int colorCount = 1;

            for(ProfilingResult result : results) {
                edgeCount += result.getEdges().length - 1;
                nameCount += result.getNames().length;
                colorCount += result.getColors().length;
            }

            //create arrays
            m_edges = new float[edgeCount];
            m_colors = new ColorRGBA[colorCount];
            m_names = new String[nameCount];

            //merge results
            int edgePos = edgeCount - 1;
            int namePos = nameCount - 1;
            int colorPos = colorCount - 1;

            float intervallOffset = 1;

            //iterate in reverse order to garanty right proportions of intervals
            for(int i = results.length - 1; i >= 0; i--) {
                //merge intervalls, dont use last segment, it is included in the next profiling result
                float intervall = results[i].getDuration() / getDuration();
                for(int j = results[i].getEdges().length - 1; j > 0; j--) {
                    m_edges[edgePos] = intervallOffset - intervall * (1.0f - results[i].getEdges()[j]);
                    edgePos--;
                }
                intervallOffset -= intervall;

                //merge other information
                for(int j = results[i].getNames().length - 1; j >= 0; j++) {
                    m_names[namePos] = results[i].getNames()[j];
                    namePos--;
                }
                for(int j = results[i].getNames().length - 1; j >= 0; j++) {
                    m_colors[colorPos] = results[i].getColors()[j];
                    colorPos--;
                }
            }

            //add own info
            m_edges[0] = 0.0f;
            m_edges[1] = intervallOffset;

            m_names[0] = name;
            m_colors[0] = color;
        }

        /**
         * Accesses the borders of the intervalls.
         * @return the borders of the contained intervalls.
         */
        public float[] getEdges() {
            return m_edges;
        }

        /**
         * The colors of the intervalls.
         * @return the colors.
         */
        public ColorRGBA[] getColors() {
            return m_colors;
        }

        /**
         * The names of the intervalls.
         * @return the names.
         */
        public String[] getNames() {
            return m_names;
        }

        /**
         * The full duration of this ProfilingResult.
         * @return the duration in seconds.
         */
        public float getDuration() {
            return m_duration;
        }
    }

    /**
     * Map containing all {@link ProfilingSection}s that can be started during this {@link ProfilingSection}.
     */
    private Map<String, ProfilingSection> m_children;

    /**
     * The currently active child of this {@link ProfilingSection}.
     */
    private ProfilingSection m_activeChild;

    /**
     * Whether this {@link ProfilingSection} is currently active or not.
     */
    private boolean m_isActive;

    /**
     * The timestamp when this {@link ProfilingSection} was started.
     */
    private long m_startMillis;

    /**
     * The last measured duration of this {@link ProfilingSection}.
     */
    private long m_lastDuration;

    /**
     * The name of this {@link ProfilingSection}.
     */
    private String m_name;

    /**
     * The Color used to display this {@link ProfilingSection}.
     */
    private ColorRGBA m_color;

    /**
     * The last result of this {@link ProfilingSection}.
     */
    private ProfilingResult m_result;

    /**
     * Creates a new {@link ProfilingSection} with the given name.
     * @param name the name of the {@link ProfilingSection}.
     */
    public ProfilingSection(String name, ColorRGBA color) {
        m_name = name;
        m_color = color;

        m_children = new HashMap<>();
        m_activeChild = null;
        m_isActive = false;
        m_startMillis = 0;
    }

    /**
     * Accesses the name of this {@link ProfilingSection}.
     * @return the name.
     */
    public String getName() {
        return m_name;
    }

    /**
     * Checks whether this {@link ProfilingSection} is currently active.
     * @return whether the {@link ProfilingSection} is active.
     */
    public boolean isActive() {
        return m_isActive;
    }

    /**
     * Adds a child {@link ProfilingSection} to this {@link ProfilingSection}.
     * @param child the {@link ProfilingSection} to add.
     */
    public void addChild(ProfilingSection child) {
        m_children.put(child.getName(), child);
    }

    /**
     * Accesses the last result of this {@link ProfilingSection}.
     * @return the {@link ProfilingResult}.
     */
    public ProfilingResult getResult() {
        return m_result;
    }

    /**
     * Starts the section with the given name.
     * @param section the name of the section to start.
     */
    public void start(String section) {
        if(!isActive()) {
            //if this section is not active, it must be activated
            if(getName().equals(section)) {
                //if this is the section that should be activated, do it
                m_isActive = true;
                m_startMillis = System.currentTimeMillis();
            } else {
                //if this is not the section that should be activated, something went wrong
                throw new IllegalStateException("Section " + section + " does not exist or cannot be started from the current context.");
            }
        } else {
            //if this section is active, activate child
            if(m_activeChild == null) {
                m_activeChild = m_children.get(section);
                if(m_activeChild != null) {
                    //if a child was found, activate it
                    m_activeChild.start(section);
                } else {
                    //if no child was found, something went wrong
                    throw new IllegalStateException("Section " + section + " does not exist or cannot be started from the current context.");
                }
            } else {
                //if there is an active child, delegate the action to the child
                m_activeChild.start(section);
            }
        }
    }

    /**
     * Ends the section with the given name.
     * @param section the name of the section to end.
     */
    public void end(String section) {
        if(isActive()) {
            //if this section is active, find active child
            if(m_activeChild != null) {
                m_activeChild.end(section);
            } else {
                //if no active child is found, try to stop this section
                if(getName().equals(section)) {
                    m_lastDuration = System.currentTimeMillis() - m_startMillis;
                    m_isActive = false;

                    //collect results from children
                    ProfilingResult[] results = new ProfilingResult[m_children.size()];
                    int i = 0;
                    for(Map.Entry<String, ProfilingSection> child : m_children.entrySet()) {
                        results[i] = child.getValue().getResult();
                        i++;
                    }

                    //store profiling result
                    m_result = new ProfilingResult(m_lastDuration / 1000f, m_color, m_name, results);
                } else {
                    //if this is not the section to end, something went wrong
                    throw new IllegalStateException("Section " + section + " does not exist or cannot be started from the current context.");
                }
            }
        } else {
            //if this section is not active, something went wrong
            throw new IllegalStateException("Section " + section + " does not exist or cannot be started from the current context.");
        }
    }
}