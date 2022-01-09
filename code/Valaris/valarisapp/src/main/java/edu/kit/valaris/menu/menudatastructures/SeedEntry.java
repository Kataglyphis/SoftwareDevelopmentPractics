package edu.kit.valaris.menu.menudatastructures;

import edu.kit.valaris.menu.menuconfig.InternalGameConfig;

/**
 *  This class provides a data structure for m_seeds. A SeedEntry has a m_name and a unique m_number.
 *  It implements the interface Comparable to sort seed entries later on.
 * @author Artur Wesner
 */
public class SeedEntry implements Comparable<SeedEntry> {
    /**
     * Contains the m_name.
     */
    private String m_name;

    /**
     * Contains the m_number.
     */
    private String m_number;

    /**
     * Saves the road type.
     */
    private String m_roadType;

    /**
     * Constructor setting the given parameters.
     * @param name The m_name to be set.
     * @param num The m_number to be set.
     * @param roadType The road type to be set.
     */
    public SeedEntry(String name, String num, String roadType) {
        m_name = name;
        m_number = num;
        m_roadType = roadType;
    }

    /**
     * Constructor setting the given parameters. Road type will be the default one set in the config.
     * @param name The m_name to be set.
     * @param num The m_number to be set.
     */
    public SeedEntry(String name, String num) {
        m_name = name;
        m_number = num;
        m_roadType = InternalGameConfig.getConfig().getProperty("RoadType");
    }

    /**
     * Constructor setting the m_name and m_number to the given parameter. Road type will be the default one set
     * in the config.
     * @param num The m_number to be set.
     */
    public SeedEntry(String num) {
        m_name = num;
        m_number = num;
        m_roadType = InternalGameConfig.getConfig().getProperty("RoadType");
    }

    /**
     * Getter for the m_number.
     * @return The m_number.
     */
    public String getNumber() {
        return m_number;
    }

    /**
     * Setter for the m_number.
     * @param num The m_number to be set.
     */
    public void setNumber(String num) {
        m_number = num;
    }

    /**
     * Getter for the m_name.
     * @return The m_name.
     */
    public String getName() {
        return m_name;
    }

    /**
     * Setter for the m_name.
     * @param name The m_name to be set.
     */
    public void setName(String name) {
        this.m_name = name;
    }

    /**
     * Getter for road type.
     * @return The road type.
     */
    public String getRoadType() {
        return m_roadType;
    }

    /**
     * Setter for the road type.
     * @param roadType The road type to set.
     */
    public void setRoadType(String roadType) {
        this.m_roadType = roadType;
    }

    /*(nicht-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(SeedEntry o) {
        return this.m_name.compareTo(o.getName());
    }
}
