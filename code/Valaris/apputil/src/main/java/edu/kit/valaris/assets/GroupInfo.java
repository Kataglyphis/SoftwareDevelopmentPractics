package edu.kit.valaris.assets;

import java.util.Arrays;
import java.util.stream.Stream;

/**
 * Holds references to the {@link AssetInfo}s.
 *
 * @author Frederik Lingg
 */
public class GroupInfo {

    /**
     * The name of this {@link GroupInfo}.
     */
    private String m_key;

    /**
     * The {@link AssetInfo}s contained by this {@link GroupInfo}.
     */
    private String[] m_assets;

    /**
     * Sets the key of this {@link GroupInfo}.
     * @param key the key.
     */
    public void setKey(String key) {
        m_key = key;
    }

    /**
     * Accesses the name of this {@link GroupInfo}.
     * @return the name of this {@link GroupInfo}.
     */
    public String getKey() {
        return m_key;
    }

    /**
     * Sets the assets referenced by this {@link GroupInfo}.
     * @param assets the assets.
     */
    public void setAssets(String[] assets) {
        m_assets = assets;
    }

    /**
     * All references to the {@link AssetInfo}s in this {@link GroupInfo}.
     * @return a {@link Stream} containing the references.
     */
    public Stream<String> getAssets() {
        return Arrays.stream(m_assets);
    }
}
