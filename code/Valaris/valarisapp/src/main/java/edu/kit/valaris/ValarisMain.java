package edu.kit.valaris;

import com.jme3.system.AppSettings;
import de.lessvoid.nifty.controls.ConsoleCommands;
import edu.kit.valaris.console.ConsoleManager;
import edu.kit.valaris.dummysim.SimulationDummy;
import edu.kit.valaris.menu.gui.*;
import edu.kit.valaris.menu.menuconfig.GraphicsConfig;
import edu.kit.valaris.menu.menuconfig.InternalGameConfig;
import edu.kit.valaris.menu.menuconfig.SeedConfig;
import edu.kit.valaris.menu.menudatastructures.SeedEntry;
import edu.kit.valaris.rendering.RenderUtil;
import edu.kit.valaris.tick.Ticker;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Main Class of the Valaris Game
 *
 * @author Frederik Lingg
 */
public class ValarisMain extends BaseApplication {

    public static void main(String[] args) {
        ValarisMain app = new ValarisMain();
        AppSettings settings = new AppSettings(true);
        settings.put("Title", "Valaris");
        Properties graphics = GraphicsConfig.getConfig();
        String[] resolution= graphics.getProperty("resolution").split(" x ");
        try {
            settings.setUseJoysticks(true);
            settings.put("Width", Integer.parseInt(resolution[0]));
            settings.put("Height", Integer.parseInt(resolution[1]));
            if (Integer.parseInt(graphics.getProperty("fullscreen")) == 1) {
                settings.put("Fullscreen", true);
            } else {
                settings.put("Fullscreen", false);
            }
            if (Integer.parseInt(graphics.getProperty("vsync")) == 1) {
                settings.put("VSync", true);
            } else {
                settings.put("VSync", false);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        GraphicsConfig.setIcons(settings);
        app.setSettings(settings);
        app.start();
    }

    @Override
    protected void init() {
        //init console commands
        ConsoleManager console = getStateManager().getState(ConsoleManager.class);
        if(console != null) {
            //add command for starting simulation dummy
            console.addCommand(new ConsoleManager.Command(){{
                name("start_dummy");
                description("Usage: start_dummy <seed>\n" +
                        "Starts the simulation-dummy with the given seed");
                action(() -> {
                    if(args().length != 2) {
                        out("The format of the Command is: start_dummy <seed>");
                        return;
                    }

                    //load seed if exists
                    String[] seed = getSeednumberforgivenName(args()[1]);
                    if(seed != null) {
                        SeedConfig.setCurrentSeed(new SeedEntry(args()[1], seed[0], seed[1]));
                    } else {
                        String[] inputSeedParts = (args()[1]).split("#");
                        if (inputSeedParts.length == 1) {
                            SeedConfig.setCurrentSeed(new SeedEntry(args()[1], args()[1],
                                    InternalGameConfig.getConfig().getProperty("RoadType")));
                        } else if (inputSeedParts.length == 2) {
                            switch (inputSeedParts[1]) {
                                case "small":
                                    SeedConfig.setCurrentSeed(new SeedEntry(args()[1], inputSeedParts[0], "small"));
                                    break;
                                case "normal":
                                    SeedConfig.setCurrentSeed(new SeedEntry(args()[1], inputSeedParts[0], "normal"));
                                    break;
                                case "large":
                                    SeedConfig.setCurrentSeed(new SeedEntry(args()[1], inputSeedParts[0], "large"));
                                    break;
                                default:
                                    out("Available road types: small, normal, large");
                                    return;
                            }
                        } else {
                            out("Only one # allowed");
                            return;
                        }

                    }

                    //start loadingscreen
                    LoadingScreen loader = new LoadingScreen(new DummySetup(), getStateManager(), getStateManager().getApplication());
                    LoadingUtil lu = new LoadingUtil((BaseApplication) getStateManager().getApplication(), getStateManager());
                    lu.shutDownAndSwitchScreen("Loading", loader);
                });
            }});
        }

        console.addCommand(new ConsoleManager.Command(){{
            name("main_menu");
            description("Usage: main_menu\n" +
                    "Switches back to the main-menu from any state of the application");
            action(() -> {
                if(args().length != 1) {
                    out("use main_menu");
                }

                //start main menu
                MainmenuScreen mainmenu = new MainmenuScreen(stateManager, stateManager.getApplication());
                SinglePlayerModeScreen single = new SinglePlayerModeScreen(stateManager, stateManager.getApplication());
                LoadingUtil lu = new LoadingUtil((BaseApplication) getStateManager().getApplication(), getStateManager());
                lu.shutDownAndSwitchScreen("MainnSingle", mainmenu, single);
            });
        }});

        //start main menu
        SplashScreen splash = new SplashScreen(stateManager, stateManager.getApplication());
        MainmenuScreen mainmenu = new MainmenuScreen(stateManager, stateManager.getApplication());
        SinglePlayerModeScreen single = new SinglePlayerModeScreen(stateManager, stateManager.getApplication());
        stateManager.attach(new NiftyAppState("MainnSingle", splash, mainmenu, single));
    }

    /**
     * Gets the corresponding number of a seed for a given name.
     * The first part of the string will be the actual number and the second part will be the road type.
     * @param seedName The selected name.
     * @return null if not found. If found then return the number.
     */
    private String[] getSeednumberforgivenName(String seedName) {
        String res = null;
        HashMap<String, String> help = transformConfigtoHashmap();
        for (Map.Entry<String, String> entry : help.entrySet()) {
            if (entry.getValue().equals(seedName)) {
                res = entry.getKey();
            }
        }
        if (res == null) {
            return null;
        }
        String[] parts = res.split("#");
        return parts;
    }

    /**
     * Transform the SeedConfig to a HashMap and saves it.
     */
    private HashMap<String, String> transformConfigtoHashmap() {
        HashMap<String, String> res = new HashMap<>();
        for (Map.Entry<Object, Object> entry : SeedConfig.getConfig().entrySet()) {
            res.put((String) entry.getKey(), (String) entry.getValue());
        }
        return res;
    }
}
