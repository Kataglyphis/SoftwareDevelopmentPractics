package edu.kit.valaris.menu.gui;

import com.jme3.app.state.AppStateManager;
import edu.kit.valaris.InputHandler;
import edu.kit.valaris.RaceAppState;
import edu.kit.valaris.menu.menuconfig.InternalGameConfig;
import edu.kit.valaris.util.TickBuilder;
import edu.kit.valaris.config.Config;
import edu.kit.valaris.config.ConfigKey;
import edu.kit.valaris.datastructure.IMapBody;
import edu.kit.valaris.tick.PropertyIndex;
import edu.kit.valaris.tick.properties.GameProperty;
import edu.kit.valaris.util.VehicleSniffer;

/**
 * Sets up the race simulation
 */
public class RaceSetup implements LoadingScreen.SimulationSetup {
    @Override
    public void setup(IMapBody mapbody, AppStateManager stateManager) {
        TickBuilder builder = new TickBuilder(mapbody);
        try {
            builder.buildSimulationTick(Integer.parseInt(InternalGameConfig.getConfig().getProperty("PlayerCount")));
        } catch (Exception ex) {
            builder.buildSimulationTick(1);
        }


        // INITIALIZES TIMER IN GAME DGO. AND STARTS COUNTDOWN
        GameProperty gameProperty =
                (GameProperty) VehicleSniffer.getGameDummy().getProperty(PropertyIndex.GAME_PROPERTY.getIndex());
        gameProperty.startCountdown(Config.getInt(ConfigKey.GAMELOGIC_COUNTDOWN_LENGTH));

        InputHandler input = new InputHandler(builder.getPlayers());
        RaceAppState race = new RaceAppState(mapbody, builder.getPlayers(), InternalGameConfig.getConfig().getProperty("KIDifficulty"));

        stateManager.attach(input);
        stateManager.attach(race);

    }
}
