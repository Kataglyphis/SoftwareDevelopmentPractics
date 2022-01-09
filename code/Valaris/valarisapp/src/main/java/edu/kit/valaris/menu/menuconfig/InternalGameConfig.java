package edu.kit.valaris.menu.menuconfig;

import java.util.Properties;

/**
 * This class manages the configuration file of the internal game settings. The file can be loaded and saved.
 * @author Artur Wesner
 */
public class InternalGameConfig {
    /**
     * Path of the InternalGameConfig configuration file.
     */
    private static final String m_IGCFilePath =
            "config/InternalGameConfig.properties";

    /**
     * Path of the default InternalGameConfig configuration file.
     */
    private static final String m_defaultConfigPath =
            "/edu.kit.valaris/config/Default/DefaultInternalGameConfig.properties";

    /**
     * Properties object containing the current settings.
     */
    private static Properties m_properties = null;

    /**
     * Getter for the m_properties object.
     * @return The m_properties object.
     */
    public static Properties getConfig() {
        if (m_properties == null) {
            m_properties = ConfigManager.getConfig(m_IGCFilePath, m_defaultConfigPath);
        }
        return m_properties;
    }

    /**
     * Saves the graphics config.
     * @return True, if saving was successful. False otherwise.
     */
    public static boolean saveConfig() {
        return ConfigManager.saveConfig(m_properties, m_IGCFilePath);
    }

    /**
     * Overwrites the InternalGameConfig config file with the default one.
     * @return True, if reset was successful. False otherwise.
     */
    public static boolean reset() {
        m_properties = ConfigManager.getConfig(null, m_defaultConfigPath);
        return InternalGameConfig.saveConfig();
    }
}