package edu.kit.valaris.generation.mapgeneration;

import com.jme3.scene.Node;
import edu.kit.valaris.datastructure.IMapBody;
import edu.kit.valaris.menu.gui.LoadingCallback;

public interface IMapGenerator {

    /**
     * Method that initiates the generation of a map, depending on the seed.
     * @param seed To generate pseudo-random values.
     * @param rootNode All elements should be attached here.
     * @return data struct that represents a map for the physics
     */
    IMapBody generate(int seed, Node rootNode, LoadingCallback callback);
}
