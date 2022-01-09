package edu.kit.valaris.menu.menuconfig;

import edu.kit.valaris.menu.menudatastructures.SeedEntry;

import java.util.Properties;

/**
 * This class manages the configuration file of the graphics settings. The file can be loaded and saved.
 * @author Artur Wesner
 */
public class SeedConfig {
    /**
     * Path of the SeedConfig configuration file.
     */
    private static final String m_SeedFilePath =
            "config/SavedSeeds.properties";

    /**
     * Path of the default InternalGameConfig configuration file.
     */
    private static final String m_defaultSeedFilePath =
            "/edu.kit.valaris/config/Default/DefaultSavedSeeds.properties";

    /**
     * Properties object containing the current settings.
     */
    private static Properties m_properties;

    /**
     * The currently selected Seed.
     */
    private static SeedEntry m_currentSeedEntry = null;

    /**
     * Getter for the currently selected Seed.
     * @return The currently selected Seed.
     */
    public static SeedEntry getCurrentSeed() {
        return m_currentSeedEntry;
    }

    /**
     * Setter for the currently selected Seed.
     * @param currentSeedEntry The seed to be saved.
     */
    public static void setCurrentSeed(SeedEntry currentSeedEntry) {
        SeedConfig.m_currentSeedEntry = currentSeedEntry;
    }

    /**
     * Saves the SeedConfig config.
     * @return True, if saving was successful. False otherwise.
     */
    public static boolean saveConfig() {
        return ConfigManager.saveConfig(m_properties, m_SeedFilePath);
    }

    /**
     * Getter for the m_properties object.
     * @return The m_properties object.
     */
    public static Properties getConfig() {
        if (m_properties == null) {
            m_properties = ConfigManager.getConfig(m_SeedFilePath, m_defaultSeedFilePath);
        }
        return m_properties;
    }

    /**
     * Adds a new seed entry to the file.
     * @param seed The seed to be added.
     * @return True, if adding was successful. False otherwise.
     */
    public static boolean addSeed(SeedEntry seed) {
        if (m_properties == null) {
            m_properties = ConfigManager.getConfig(m_SeedFilePath, m_defaultSeedFilePath);
        }
        m_properties.setProperty(seed.getNumber() + "#" + seed.getRoadType(), seed.getName() + "#" + seed.getRoadType());
        return SeedConfig.saveConfig();
    }

    /**
     * Overwrites the SeedConfig config file with the default one.
     * @return True, if reset was successful. False otherwise.
     */
    public static boolean reset() {
        m_properties = ConfigManager.getConfig(null, m_defaultSeedFilePath);
        return SeedConfig.saveConfig();
    }
}
