/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.salsa.collector;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

/**
 *
 * @author Duc-Hung Le
 */
public class PropertiesManager {

    public static Properties getParameters(String configFile) {
        Properties configuration = new Properties();
        try {
            File f = new File(configFile);
            if (!f.exists()) {
                System.out.println("Configuration file not found: " + configFile + ". Return a black properties.");
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
            System.out.println("Trying to get parameter: " + key + " in the configuration file:" + configFile + ", value: " + configuration.getProperty(key));
            return configuration.getProperty(key);
        }
        System.out.println("Cannot load the configuration file:" + configFile + " to load the key: " + key);

        return null;
    }

    public static void saveParameter(String key, String value, String configFile) {
        Properties configuration = getParameters(configFile);

        System.out.println("Trying to save parameter: " + key + "=" + value + ", in the configuration file:" + configFile);
        try {
            OutputStream output = new FileOutputStream(configFile);
            configuration.store(output, null);
        } catch (FileNotFoundException ex) {
            System.out.println("Cannot find the configFile: " + configFile + ", to store properties ! Error: " + ex);
        } catch (IOException ex) {
            System.out.println("Cannot write to configFile: " + configFile + ", to store properties ! Error: " + ex);
        }
    }
    

}
