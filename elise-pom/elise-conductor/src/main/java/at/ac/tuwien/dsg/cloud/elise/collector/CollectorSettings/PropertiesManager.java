/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.elise.collector.CollectorSettings;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;
import org.apache.log4j.Logger;

/**
 *
 * @author Duc-Hung Le
 */
public class PropertiesManager {

    protected static Logger logger = Logger.getLogger("EliseLogger");

    public static Properties getParameters(String configFile) {
        Properties configuration = new Properties();
        try {
            File f = new File(configFile);
            if (!f.exists()) {
                logger.debug("Configuration file not found: " + configFile + ". Return a black properties.");
                return new Properties();
            }
            configuration.load(new FileReader(f));
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
        return configuration;
    }

    public static String getParameter(String key, String configFile) {
        Properties configuration = getParameters(configFile);
        if (configuration != null) {
            logger.debug("Trying to get parameter: " + key + " in the configuration file:" + configFile + ", value: " + configuration.getProperty(key));
            return configuration.getProperty(key);
        }
        logger.debug("Cannot load the configuration file:" + configFile + " to load the key: " + key);

        return null;
    }

    public static void saveParameter(String key, String value, String configFile) {
        Properties configuration = getParameters(configFile);

        logger.debug("Trying to save parameter: " + key + "=" + value + ", in the configuration file:" + configFile);
        try {
            OutputStream output = new FileOutputStream(configFile);
            configuration.store(output, null);
        } catch (FileNotFoundException ex) {
            logger.error("Cannot find the configFile: " + configFile + ", to store properties ! Error: " + ex);
        } catch (IOException ex) {
            logger.error("Cannot write to configFile: " + configFile + ", to store properties ! Error: " + ex);
        }
    }

    public Logger getLogger() {
        return logger;
    }
}
