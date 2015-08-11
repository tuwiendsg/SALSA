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
package at.ac.tuwien.dsg.cloud.salsa.engine.utils;

import java.io.File;
import java.io.FileReader;
import java.util.Properties;

import at.ac.tuwien.dsg.cloud.salsa.engine.services.SalsaEngineImplAll;
import at.ac.tuwien.dsg.cloud.salsa.messaging.messageInterface.MessageClientFactory;
import org.slf4j.Logger;

public class SalsaConfiguration {

    static final Properties configuration = new Properties();
    static final Logger logger = EngineLogger.logger;
    static final String CONFIG_FILE_2 = "/etc/salsa.engine.properties";
    static final String CURRENT_DIR = System.getProperty("user.dir");
    static final String CONFIG_FILE_1 = CURRENT_DIR + "/salsa.engine.properties";

    static final String SALSA_CENTER_IP_KEY = "SALSA_CENTER_IP";
    static final String SALSA_CENTER_PORT_KEY = "SALSA_CENTER_PORT";
    static final String SALSA_CENTER_WORKING_DIR_KEY = "SALSA_CENTER_WORKING_DIR";
    static final String SALSA_PIONEER_WORKING_DIR_KEY = "SALSA_PIONEER_WORKING_DIR";
    static final String SALSA_REPO_KEY = "SALSA_REPO";
    static final String SALSA_USERNAME_KEY = "USERNAME";
    static final String SALSA_BROKER_KEY = "BROKER";
    static final String SALSA_BROKER_TYPE_KEY = "BROKER_TYPE";

    // default value of internal SASLA
    static final String DEFAULT_PIONEER_FILES = "salsa-pioneer.jar";
    static final String DEFAULT_PIONEER_RUN = "salsa-pioneer.jar";
    static final String DEFAULT_SALSA_PRIVATE_KEY = "id_rsa";
    static final String DEFAULT_VARIABLE_FILE = "salsa.variables";

    static final String USERNAME = "salsa-default";
    static final String BROKER = "";
    

    static File configFile;

    static {
        // try to load property files at current folder, then /etc/
        try {
            File f1 = new File(CONFIG_FILE_1);
            File f2 = new File(CONFIG_FILE_2);
            logger.info("Trying to load configuration from: " + CONFIG_FILE_1 + " and " + CONFIG_FILE_2);
            if (f1.exists()) {
                logger.info("Load configuration file: " + CONFIG_FILE_1);
                configuration.load(new FileReader(f1));
            } else if (f2.exists()) {
                logger.info("Load configuration file: " + CONFIG_FILE_2);
                configuration.load(new FileReader(f2));
            }

            // check if user set any thing, other wise detect one
            if (configuration.get(SALSA_CENTER_IP_KEY) == null) {
                configuration.put(SALSA_CENTER_IP_KEY, SystemFunctions.getEth0IPAddress());
            }
            if (configuration.get(SALSA_CENTER_PORT_KEY) == null) {
                configuration.put(SALSA_CENTER_PORT_KEY, SystemFunctions.getPort());
            }
            if (configuration.get(SALSA_CENTER_WORKING_DIR_KEY) == null) {
                configuration.put(SALSA_CENTER_WORKING_DIR_KEY, "/tmp/salsa-engine");
            }
            if (configuration.get(SALSA_PIONEER_WORKING_DIR_KEY) == null) {
                configuration.put(SALSA_PIONEER_WORKING_DIR_KEY, "/tmp/salsa-pioneer");
            }
            if (configuration.get(SALSA_REPO_KEY) == null) {
                configuration.put(SALSA_REPO_KEY, "http://localhost:8080/salsa/upload/files");
            }
            if (configuration.get(SALSA_USERNAME_KEY) == null) {
                configuration.put(SALSA_USERNAME_KEY, "salsa-default");
            }
            if (configuration.get(SALSA_BROKER_KEY) == null) {
                configuration.put(SALSA_BROKER_KEY, "tcp://iot.eclipse.org:1883");
            }
            if (configuration.get(SALSA_BROKER_TYPE_KEY) == null) {
                configuration.put(SALSA_BROKER_TYPE_KEY, "mqtt");
            }
            configuration.list(System.out);
            logger.debug("Center endpoint: " + getSalsaCenterEndpoint());

            (new File(getPioneerWorkingDir())).mkdirs();
            (new File(getServiceStorageDir())).mkdirs();
        } catch (Exception ex) {
            logger.error("Error occured when configuring salsa engine: " + ex.getMessage());
            ex.printStackTrace();
        }

        // load configure file
        String userFile = SalsaConfiguration.getCloudUserParameters();
        if (userFile != null && !userFile.equals("")) {
            logger.debug("Found the user file in the main engine configuration. Load cloud configuration at: " + userFile);
            configFile = new File(userFile);
        } else {            
            File file1 = new File(CURRENT_DIR + "/cloudUserParameters.ini");
            File tmpFile = new File("/etc/cloudUserParameters.ini");
            if (file1.exists()) {
                logger.debug("Load cloud configuration at: " + file1.getAbsolutePath());
                configFile = file1;
            } else if (tmpFile.exists()) {
                logger.debug("Load cloud configuration at: " + tmpFile.getAbsolutePath());
                configFile = tmpFile;
            } else {
                logger.debug("Load cloud configuration at: default resource folder");
                configFile = new File(SalsaEngineImplAll.class.getResource("/cloudUserParameters.ini").getFile());
            }
        }
    }

    public static File getCloudUserParametersFile() {
        return configFile;
    }

    public static String getBroker() {
        return configuration.getProperty(SALSA_BROKER_KEY);
    }

    public static String getBrokerType() {
        return configuration.getProperty(SALSA_BROKER_TYPE_KEY);
    }

    private static String getSALSA_CENTER_IP() {
        return configuration.getProperty(SALSA_CENTER_IP_KEY);
    }

    private static String getSALSA_CENTER_PORT() {
        return configuration.getProperty(SALSA_CENTER_PORT_KEY);
    }

    public static String getRepoPrefix() {
        return configuration.getProperty("SALSA_REPO");
    }

    public static String getUserName() {
        return configuration.getProperty(SALSA_USERNAME_KEY);
    }

    public static String getPioneerFiles() {
        return DEFAULT_PIONEER_FILES;
    }

    public static String getPioneerRun() {
        return DEFAULT_PIONEER_RUN;
    }

    public static String getSSHKeyForCenter() {
        return SalsaConfiguration.class.getResource(DEFAULT_SALSA_PRIVATE_KEY).getFile();
    }

    public static String getPioneerWeb() {
        return getSalsaCenterEndpoint() + "/rest/artifacts/pioneer";
    }

    public static String getPioneerLocalFile() {
        return CURRENT_DIR + "/" + getPioneerRun();
    }

    // working dir of the pioneer
    public static String getPioneerWorkingDir() {
        return configuration.getProperty(SALSA_PIONEER_WORKING_DIR_KEY);
    }

    // variable file should sit beside the pioneer artifact
    public static String getSalsaVariableFile() {
        return DEFAULT_VARIABLE_FILE;
    }

    public static String getSalsaCenterEndpoint() {
        return "http://" + getSALSA_CENTER_IP() + ":" + getSALSA_CENTER_PORT() + "/salsa-engine";
    }

    public static String getSalsaCenterEndpointLocalhost() {
        return "http://localhost:" + getSALSA_CENTER_PORT() + "/salsa-engine";
    }

    public static String getServiceStorageDir() {
        return configuration.getProperty(SALSA_CENTER_WORKING_DIR_KEY) + "/services";
    }

    public static String getArtifactStorage() {
        return configuration.getProperty(SALSA_CENTER_WORKING_DIR_KEY) + "/artifacts";
    }

    public static String getToscaTemplateStorage() {
        return configuration.getProperty(SALSA_CENTER_WORKING_DIR_KEY) + "/tosca_templates";
    }

    public static String getCloudProviderDescriptionDir() {
        return configuration.getProperty(SALSA_CENTER_WORKING_DIR_KEY) + "/cloudDescriptions";
    }

    public static String getCloudUserParameters() {
        return configuration.getProperty("CLOUD_USER_PARAMETERS");
    }
    
    public static MessageClientFactory getMessageClientFactory(){
        return MessageClientFactory.getFactory(getBroker(), getBrokerType());
    }

    

}
