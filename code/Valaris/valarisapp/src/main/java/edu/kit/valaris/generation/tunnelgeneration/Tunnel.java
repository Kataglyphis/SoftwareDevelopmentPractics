package edu.kit.valaris.generation.tunnelgeneration;

import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import edu.kit.valaris.generation.roadgeneration.Road;
import jme3tools.optimize.GeometryBatchFactory;

/**
 * Implementation of the ITunnel
 * @author Sidney Hansen
 */
public class Tunnel implements ITunnel {

    private Road m_road;

    private Node m_tunnelAssetRootNode;


    Tunnel(Road road, Node tunnelAssetRootNode) {
        this.m_tunnelAssetRootNode = tunnelAssetRootNode;
        this.m_road = road;
    }

    @Override
    public Road getRoad() {
        return m_road;
    }

    @Override
    public Node generateSceneGraph() {

        Node tunnelRootNode = new Node("tunnelRootNode");

        m_tunnelAssetRootNode.setLocalTranslation(m_road.getFirstRoadCursor().getPosition());
        m_tunnelAssetRootNode.setLocalRotation(m_road.getFirstRoadCursor().getRotation());

        tunnelRootNode.attachChild(m_tunnelAssetRootNode);

        GeometryBatchFactory.optimize(tunnelRootNode);

        return tunnelRootNode;
    }

    @Override
    public Vector3f[] getEntryFrame() {
        return m_road.getFirstRoadCursor().getRoadFrame();
    }

    @Override
    public Vector3f[] getExitFrame() {
        return m_road.getLastRoadCursor().getRoadFrame();
    }
}
