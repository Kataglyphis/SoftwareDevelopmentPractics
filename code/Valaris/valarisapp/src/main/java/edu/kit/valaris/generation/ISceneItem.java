package edu.kit.valaris.generation;

import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

/**
 * Parent interface of the interfaces of all subgenerator products.
 * It defines the functionality to be able to generate a SceneGraph from an object.
 *
 * @author Lukas Schölch
 */
public interface ISceneItem {

    /**
     * Generates scenegraph of the SceneItem.
     * @return rootNode  of the SceneItem
     */
    Node generateSceneGraph();

    /**
     * Returns the face covering the entry of this sceneItem.
     * @return face covering the entry
     */
    Vector3f[] getEntryFrame();

    /**
     * Returns the face covering the exit of this sceneItem.
     * @return face covering the exit
     */
    Vector3f[] getExitFrame();
}

