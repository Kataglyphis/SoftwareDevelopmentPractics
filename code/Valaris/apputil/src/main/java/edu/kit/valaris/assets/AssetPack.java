package edu.kit.valaris.assets;

import java.util.Arrays;
import java.util.stream.Stream;

/**
 * Contains multiple {@link GroupInfo}s to build a collection of {@link AssetInfo}s.
 *
 * @author Frederik Lingg
 */
public class AssetPack {

    /**
     * The {@link GroupInfo}s contained by this {@link AssetPack}.
     */
    private GroupInfo[] m_groups;

    /**
     * Sets the {@link GroupInfo}s in this {@link AssetPack}.
     * @param groups an array of {@link GroupInfo}s.
     */
    public void setGroups(GroupInfo[] groups) {
        m_groups = groups;
    }

    /**
     * Accesses the {@link GroupInfo}s contained by this {@link AssetPack}.
     * @return a {@link Stream} containing all {@link GroupInfo}s.
     */
    public Stream<GroupInfo> getGroups() {
        return Arrays.stream(m_groups);
    }
}
