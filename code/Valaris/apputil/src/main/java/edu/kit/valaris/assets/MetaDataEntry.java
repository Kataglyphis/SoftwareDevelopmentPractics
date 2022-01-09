package edu.kit.valaris.assets;

/**
 * Represents an entry in the MetaData of an {@link AssetInfo}.
 */
public class MetaDataEntry {

    /**
     * The name of the entry.
     */
    private String m_key;

    /**
     * The value of the entry.
     */
    private String m_value;

    /**
     * Sets the name of this {@link MetaDataEntry}.
     * @param key the name.
     */
    public void setKey(String key){
        m_key = key;
    }

    /**
     * Sets the Value of this {@link MetaDataEntry}.
     * @param value the value.
     */
    public void setValue(String value) {
        m_value = value;
    }

    /**
     * Accesses the name of the {@link MetaDataEntry}.
     * @return the name.
     */
    public String getKey() {
        return m_key;
    }

    /**
     * Accesses the value of the {@link MetaDataEntry}.
     * @return the value.
     */
    public String getValue() {
        return m_value;
    }
}
