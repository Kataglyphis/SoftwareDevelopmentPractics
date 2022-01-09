package edu.kit.valaris.rendering;

import com.jme3.app.state.AbstractAppState;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Appstate that holds all cullable nodes in their order.
 *
 * @author Frederik Lingg
 */
public class CullableRegister extends AbstractAppState {

    /**
     * A List containing all cullables.
     */
    private List<CullingControl> m_cullables;

    /**
     * The node containing all cullables.
     */
    private Node m_cullableRoot;

    /**
     * Creates a new CullableRegister, containing the given cullables.
     */
    public CullableRegister(Node cullables) {
        m_cullableRoot = cullables;

        //find cullables
        m_cullables = new ArrayList<>();
        for (Spatial spatial : cullables.getChildren()) {
            CullingControl cullable = spatial.getControl(CullingControl.class);
            if (cullable != null) {
                m_cullables.add(cullable);
            }
        }

        //sort cullables
        m_cullables.sort(Comparator.comparingInt(CullingControl::getIndex));
    }

    /**
     * Accesses the {@link Node} containing all cullables.
     * @return the Node.
     */
    public Node getCullRoot() {
        return m_cullableRoot;
    }

    /**
     * Accesses the count of the cullable items.
     * @return the count.
     */
    public int cullableCount() {
        return m_cullables.size();
    }

    /**
     * Accesses the cullable with the given index.
     * @param index the index.
     * @return the cullable.
     */
    public CullingControl getCullable(int index) {
        return m_cullables.get(index);
    }
}
