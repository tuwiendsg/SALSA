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

import at.ac.tuwien.dsg.cloud.salsa.messaging.messageInterface.MessageClientFactory;
import java.io.IOException;
import org.slf4j.Logger;

public class SalsaConfiguration {

    static final Logger logger = EngineLogger.logger;
    static final String CURRENT_DIR = System.getProperty("user.dir");

    static {
        // try to create working folder
        try {
            (new File(getPioneerWorkingDir())).mkdirs();
            (new File(getServiceStorageDir())).mkdirs();
        } catch (Exception ex) {
            logger.error("Error occured when configuring salsa engine: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public static String getBroker() {
        // tcp://iot.eclipse.org:1883
        // amqp://128.130.172.215
        return getGenericParameter("BROKER", "tcp://iot.eclipse.org:1883");  
    }

    public static String getBrokerType() {
        return getGenericParameter("BROKER_TYPE", "mqtt"); //mqtt, amqp
    }

    public static String getBrokerExport() {
        return getGenericParameter("EXPORT_BROKER", getBroker());
    }

    public static String getBrokerTypeExport() {
        return getGenericParameter("EXPORT_BROKER_TYPE", getBrokerType());
    }

    private static String getSALSA_CENTER_IP() {
        return getGenericParameter("SALSA_CENTER_IP", SystemFunctions.getEth0IPAddress());
    }

    private static String getSALSA_CENTER_PORT() {
        return getGenericParameter("SALSA_CENTER_PORT", SystemFunctions.getPort());
    }

    public static String getRepoPrefix() {
        return getGenericParameter("SALSA_REPO", "http://localhost:8080/salsa/upload/files");
    }

    public static String getUserName() {
        return getGenericParameter("USERNAME", "salsa-default");
    }

    public static String getPioneerFiles() {
        return "salsa-pioneer.jar";
    }

    public static String getPioneerRun() {
        return "salsa-pioneer.jar";
    }

    public static String getSSHKeyForCenter() {
        return SalsaConfiguration.class.getResource("id_rsa").getFile();
    }

    public static String getPioneerArtifact() {
        return getSalsaCenterEndpoint() + "/rest/manager/artifacts/pioneer";
    }

    public static String getConductorWeb() {
        return getSalsaCenterEndpoint() + "/rest/manager/artifacts/conductor";
    }

    public static String getPioneerBootstrapScript() {
        return getSalsaCenterEndpoint() + "/rest/manager/artifacts/";
    }

    public static String getPioneerLocalFile() {
        return CURRENT_DIR + "/" + getPioneerRun();
    }

    public static String getConductorLocalFile() {
        return CURRENT_DIR + "/conductor.jar";
    }

    public static String getPioneerBootstrapScriptLocalFile() {
        return SalsaConfiguration.class.getResource("/scripts/java1.8_update.sh").getFile();
    }

    // working dir of the pioneer
    public static String getPioneerWorkingDir() {
        return getGenericParameter("SALSA_PIONEER_WORKING_DIR", "/tmp/pioneer-workspace");
    }

    // variable file should sit beside the pioneer artifact
    public static String getSalsaVariableFile() {
        return "salsa.variables";
    }

    public static String getSalsaCenterEndpoint() {
        return "http://" + getSALSA_CENTER_IP() + ":" + getSALSA_CENTER_PORT() + "/salsa-engine";
    }

    public static String getSalsaCenterEndpointLocalhost() {
        return "http://localhost:" + getSALSA_CENTER_PORT() + "/salsa-engine";
    }

    public static String getServiceStorageDir() {
        return createFolderIfNotExisted(CURRENT_DIR + "/services");
    }

    public static String getArtifactStorage() {
        return createFolderIfNotExisted(CURRENT_DIR + "/artifacts");
    }

    public static String getToscaTemplateStorage() {
        return createFolderIfNotExisted(CURRENT_DIR + "/tosca_templates");
    }

    public static String getCloudProviderDescriptionDir() {
        return createFolderIfNotExisted(CURRENT_DIR + "/cloudDescriptions");
    }

    public static String getSalsaVersion() {
        String[] versionFile = {SalsaConfiguration.class.getResource("/version.txt").getFile()};
        return getGenericParameterFromFile(versionFile, "version", "unknown");
    }

    public static String getBuildTime() {
        String[] versionFile = {SalsaConfiguration.class.getResource("/version.txt").getFile()};
        return getGenericParameterFromFile(versionFile, "build.date", "unknown") + " UTC";
    }
    
    
    // for placement ADHOC implementation
    public static boolean getDynamicPlacementOn(){
        String placementOn = getGenericParameter("DYNAMIC_PLACEMENT", "off");
        if (placementOn.equals("on")){
            return true;
        } 
        return false;
    }
    
    public static String getPlacementThreadholdMetric(){
        return getGenericParameter("METRIC", "mem");
    }
    
    public static float getPlacementThreadhold(){
        return Float.parseFloat(getGenericParameter("THREADHOLD", "0.8"));
    }
    
    
    

    public static File getCloudUserParametersFile() {
        String fileNames[] = {CURRENT_DIR + "/cloudUserParameters.ini", "/etc/cloudUserParameters.ini"};
        String f = null;
        for (String fn : fileNames) {
            File file = new File(fn);
            if (file.exists()) {
                logger.debug("Load cloud configuration at: " + file.getAbsolutePath());
                return file;
            }
        }
        try {
            (new File(fileNames[0])).createNewFile();
            return new File(fileNames[0]);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    // return folderName
    private static String createFolderIfNotExisted(String folder) {
        File f = new File(folder);
        f.mkdirs();
        if (f.exists()) {
            return folder;
        }
        return "/tmp";
    }

    public static MessageClientFactory getMessageClientFactory() {
        return MessageClientFactory.getFactory(getBroker(), getBrokerType());
    }

    public static String getGenericParameter(String key, String theDefault) {
        String fileNames[] = {CURRENT_DIR + "/salsa.engine.properties", "/etc/salsa.engine.properties"};
        return getGenericParameterFromFile(fileNames, key, theDefault);
    }

    public static String getConfigurationFile() {
        return CURRENT_DIR + "/salsa.engine.properties";
    }
    
    
    public static String getEventLogFile(){
        return "./logs/salsa.messages.log";
    }
    

    public static String getGenericParameterFromFile(String[] fileNames, String key, String theDefault) {
        Properties prop = new Properties();
        for (String file : fileNames) {
            File f = new File(file);
            try {
                if (!f.exists()) {
                    f.createNewFile();
                }
                prop.load(new FileReader(f));
                String param = prop.getProperty(key);
                if (param != null) {
                    logger.debug("Read a property in {} and use {}={}", file, key, param);
                    return param;
                }

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        logger.debug("Read a property and use the default {}={}", key, theDefault);
        return theDefault;
    }

}
