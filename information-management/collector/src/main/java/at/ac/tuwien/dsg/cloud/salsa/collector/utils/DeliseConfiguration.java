/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.salsa.collector.utils;

import at.ac.tuwien.dsg.cloud.salsa.informationmanagement.communication.Utils.DeliseUtils;
import at.ac.tuwien.dsg.cloud.salsa.informationmanagement.communication.messagePayloads.DeliseMeta;
import at.ac.tuwien.dsg.cloud.salsa.informationmanagement.communication.protocol.DeliseMessageTopic;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author hungld
 */
public class DeliseConfiguration {

    static Logger logger = LoggerFactory.getLogger("DELISE");

    private static final String CURRENT_DIR = System.getProperty("user.dir");
    private static final String CONFIG_FILE = CURRENT_DIR + "/salsa.variables";
    private static final String myUUID = UUID.randomUUID().toString();

    public static DeliseMeta getMeta() {
        return new DeliseMeta(myUUID, DeliseUtils.getEth0Address(), DeliseMessageTopic.getCollectorTopicByID(myUUID));
    }

    public static String getBroker() {
        return getGenericParameter("BROKER", "tcp://iot.eclipse.org:1883");
    }

    public static String getBrokerType() {
        return getGenericParameter("BROKER_TYPE", "mqtt");
    }

    public static String getCURRENT_DIR() {
        return CURRENT_DIR;
    }

    public static String getMyUUID() {
        return myUUID;
    }

    public static Logger getLogger() {
        return logger;
    }

    private static String getGenericParameter(String key, String theDefault) {
        Properties prop = new Properties();
        InputStream input;
        File myFile = new File(CONFIG_FILE);

        try {
            if (!myFile.exists()) {
                myFile.createNewFile();
            }
            input = new FileInputStream(CONFIG_FILE);
            // load a properties file
            prop.load(input);
            String param = prop.getProperty(key);
            if (param != null) {   // just return default MQTT
                return param;
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return theDefault;
    }
}
