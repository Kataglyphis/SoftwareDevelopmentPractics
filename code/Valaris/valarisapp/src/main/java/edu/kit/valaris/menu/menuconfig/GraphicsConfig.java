package edu.kit.valaris.menu.menuconfig;

import com.jme3.system.AppSettings;
import edu.kit.valaris.menu.gui.OwnScreenController;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;
import javax.imageio.ImageIO;

/**
 * This class manages the configuration file of the graphics settings. The file can be loaded and saved.
 * @author Artur Wesner
 */
public class GraphicsConfig {
    /**
     * Path of the graphics configuration file.
     */
    private static final String m_GraphicsFilePath =
            "config/GraphicsConfig.properties";

    /**
     * Path of the default graphics configuration file.
     */
    private static final String m_defaultConfigPath =
            "/edu.kit.valaris/config/Default/DefaultGraphicsConfig.properties";

    /**
     * Properties object containing the current settings.
     */
    private static Properties m_properties;

    /**
     * Getter for the m_properties object.
     * @return The m_properties object.
     */
    public static Properties getConfig() {
        if (m_properties == null) {
            m_properties = ConfigManager.getConfig(m_GraphicsFilePath, m_defaultConfigPath);
        }
        return m_properties;
    }

    /**
     * Saves the graphics config.
     * @return True, if saving was successful. False otherwise.
     */
    public static boolean saveConfig() {
        return ConfigManager.saveConfig(m_properties, m_GraphicsFilePath);
    }

    /**
     * Overwrites the graphics config file with the default one.
     * @return True, if reset was successful. False otherwise.
     */
    public static boolean reset() {
        m_properties = ConfigManager.getConfig(null, m_defaultConfigPath);
        return GraphicsConfig.saveConfig();
    }

    /**
     * Sets the window icons.
     * @param settings Reference to the AppSettings.
     */
    public static void setIcons(AppSettings settings) {
        URL f1 = OwnScreenController.class.getResource("/edu/kit/valaris/menuSkin/Bilder/Icons/valaris16.png");
        URL f2 = OwnScreenController.class.getResource("/edu/kit/valaris/menuSkin/Bilder/Icons/valaris32.png");
        URL f3 = OwnScreenController.class.getResource("/edu/kit/valaris/menuSkin/Bilder/Icons/valaris128.png");
        try {
            BufferedImage[] bi = {ImageIO.read(f1), ImageIO.read(f2), ImageIO.read(f3)};
            settings.setIcons(bi);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}