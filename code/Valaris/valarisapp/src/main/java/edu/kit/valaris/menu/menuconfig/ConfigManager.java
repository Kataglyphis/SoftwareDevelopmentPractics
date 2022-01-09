package edu.kit.valaris.menu.menuconfig;

import edu.kit.valaris.menu.gui.OwnScreenController;

import java.io.*;
import java.util.Properties;
import java.net.URL;

/**
 * This class provides functions to load and save files for a given path.
 * @author Artur Wesner
 */
public class ConfigManager {
    /**
     * Returns the to the file path corresponding properties object.
     * @param filepath The filepath.
     * @param defaultPath the path to the default configuration.
     * @return The corresponding properties object.
     */
    public static Properties getConfig(String filepath, String defaultPath) {
        Properties properties = new Properties();

        InputStream configStream = null;
        try {
            //try find actual file
            if(filepath != null) {
                File file = new File(filepath);
                if(file.exists()) {
                    configStream = new FileInputStream(file);
                }
            }

            //fall back to default if not found
            if(configStream == null) {
                configStream = OwnScreenController.class.getResourceAsStream(defaultPath);
            }

            //load properties
            properties.load(configStream);
            configStream.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return properties;
    }

    /**
     * Overrides the file variables with the given values in the properties object.
     * @param properties The properties object to be saved.
     * @param filepath The filepath to the file.
     * @return True, if saving was successful. False otherwise.
     */
    public static boolean saveConfig(Properties properties, String filepath) {
        File file = new File(filepath);
        if(!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try (OutputStream out = new FileOutputStream(file)) {
            properties.store(out, null);
            return true;
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
    }
}
