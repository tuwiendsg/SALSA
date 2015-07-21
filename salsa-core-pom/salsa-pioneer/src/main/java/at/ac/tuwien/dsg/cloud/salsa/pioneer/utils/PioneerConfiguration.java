/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.salsa.pioneer.utils;

import at.ac.tuwien.dsg.cloud.salsa.messaging.model.PioneerInfo;
import at.ac.tuwien.dsg.cloud.salsa.pioneer.queueLogger.QueueAppender;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.UUID;
import org.apache.log4j.PatternLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author hungld
 */
public class PioneerConfiguration {

    public static final Logger logger;

    private static final String CURRENT_DIR = System.getProperty("user.dir");
    private static final String CONFIG_FILE = CURRENT_DIR + "/salsa.variables";

    // if the pioneer is restarted, this will generate another ID
    private static final String PIONEER_ID = UUID.randomUUID().toString();

    static {
        
        // add new LogAppender
//        System.out.println("logger adding 1");
//        QueueAppender queueAppender = new QueueAppender(PioneerConfiguration.getBroker());
//        System.out.println("logger adding 2");
//        queueAppender.setLayout(new PatternLayout("%5p [%t] (%F:%L) - %m%n"));        
//        System.out.println("logger adding 3");
//        org.apache.log4j.Logger.getLogger("SalsaPioneerLog").addAppender(queueAppender);
//        logger.debug("The Pioneer {} has inited log...", PioneerConfiguration.PIONEER_ID);
        
        
        logger =  LoggerFactory.getLogger("SalsaPioneer");
    }

    public static String getPioneerID() {
        return PIONEER_ID;
    }

    public static String getBroker() {
        return getGenericParameter("BROKER", "tcp://iot.eclipse.org:1883");
    }

    public static String getWorkingDir() {
        return getGenericParameter("SALSA_WORKING_DIR", "/tmp");
    }

    public static String getWorkingDirOfInstance(String nodeID, int instanceID) {
        return getWorkingDir() + "/" + nodeID + "." + instanceID;
    }

    public static PioneerInfo getPioneerInfo() {
        return new PioneerInfo(getGenericParameter("SALSA_USER_NAME", "salsa-default"),
                PIONEER_ID, SystemFunctions.getEth0IPAddress(),
                getGenericParameter("SALSA_SERVICE_ID", null),
                getGenericParameter("SALSA_TOPOLOGY_ID", null),
                getGenericParameter("SALSA_NODE_ID", null),
                Integer.parseInt(getGenericParameter("SALSA_REPLICA", null)));
    }
    
    public static String getPioneerID_Structure(){
        PioneerInfo pi = getPioneerInfo();
        return pi.getUserName()+"_"+pi.getService()+"_"+pi.getUnit()+"_"+pi.getInstance();
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
