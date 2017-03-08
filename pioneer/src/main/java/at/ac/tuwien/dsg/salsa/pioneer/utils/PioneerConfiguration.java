/*
 * Copyright (c) 2013 Technische Universitat Wien (TUW), Distributed Systems Group. http://dsg.tuwien.ac.at
 *
 * This work was partially supported by the European Commission in terms of the CELAR FP7 project (FP7-ICT-2011-8 #317790), http://www.celarcloud.eu/
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package at.ac.tuwien.dsg.salsa.pioneer.utils;

import at.ac.tuwien.dsg.salsa.model.salsa.info.PioneerInfo;
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
 * @author Duc-Hung Le
 */
public class PioneerConfiguration {

    private static final String CURRENT_DIR = System.getProperty("user.dir");
    private static final String CONFIG_FILE = CURRENT_DIR + "/salsa.variables";

    // if the pioneer is restarted, this will generate another ID
    private static final String PIONEER_ID = UUID.randomUUID().toString();

    public static String getPioneerID() {
        return PIONEER_ID;
    }

    public static String getBroker() {
        return getGenericParameter("BROKER", "tcp://iot.eclipse.org:1883");
    }

    public static String getBrokerType() {
        return getGenericParameter("BROKER_TYPE", "mqtt");
    }

    public static String getWorkingDir() {
        return getGenericParameter("SALSA_WORKING_DIR", "/tmp");
    }

    public static String getWorkingDirOfInstance(String nodeID, int instanceID) {
        return getWorkingDir() + "/" + nodeID + "." + instanceID;
    }

    public static String getEliseConductorURL() {
        return getGenericParameter("ELISE_CONDUCTOR_URL", null);
    }

    public static String getConductorFilePath() {
        return getWorkingDir() + "/conductor.jar";
    }

    public static PioneerInfo getPioneerInfo() {
        PioneerInfo info = new PioneerInfo(getGenericParameter("SALSA_USER_NAME", "salsa-default"),
                PIONEER_ID, SystemFunctions.getIPAdressLocalhost(),
                getGenericParameter("SALSA_SERVICE_ID", null),
                getGenericParameter("SALSA_TOPOLOGY_ID", null),
                getGenericParameter("SALSA_NODE_ID", null),
                Integer.parseInt(getGenericParameter("SALSA_REPLICA", "0")));
        info.setHostname(SystemFunctions.getHostNameLocalhost());
        return info;
    }

    public static String getPioneerID_Structure() {
        PioneerInfo pi = getPioneerInfo();
        return pi.getUserName() + "_" + pi.getService() + "_" + pi.getUnit() + "_" + pi.getInstance();
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
            if (param != null && !param.isEmpty()) {   // just return default MQTT
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
