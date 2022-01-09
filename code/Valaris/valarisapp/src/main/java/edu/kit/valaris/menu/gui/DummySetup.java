package edu.kit.valaris.menu.gui;

import com.jme3.app.state.AppStateManager;
import edu.kit.valaris.datastructure.IMapBody;
import edu.kit.valaris.dummysim.SimulationDummy;

/**
 * Sets up the dummy simulation
 */
public class DummySetup implements LoadingScreen.SimulationSetup {

    @Override
    public void setup(IMapBody mapbody, AppStateManager stateManager) {
        stateManager.attach(new SimulationDummy());
    }
}
