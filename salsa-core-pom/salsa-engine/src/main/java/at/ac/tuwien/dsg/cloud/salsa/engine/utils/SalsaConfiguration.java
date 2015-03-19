package at.ac.tuwien.dsg.cloud.salsa.engine.utils;

import java.io.File;
import java.io.FileReader;
import java.util.Properties;



import at.ac.tuwien.dsg.cloud.salsa.cloudconnector.multiclouds.SalsaCloudProviders;
import java.io.BufferedOutputStream;
import java.io.PrintWriter;
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
    
    // default value of internal SASLA
    static final String DEFAULT_PIONEER_FILES="salsa-pioneer.jar";
    static final String DEFAULT_PIONEER_RUN="salsa-pioneer.jar";
    static final String DEFAULT_SALSA_PRIVATE_KEY="id_rsa";
    static final String DEFAULT_VARIABLE_FILE="salsa.variables";

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
            if (configuration.get(SALSA_CENTER_IP_KEY) == null){
                configuration.put(SALSA_CENTER_IP_KEY, SystemFunctions.getEth0IPAddress());                
            }            
            if (configuration.get(SALSA_CENTER_PORT_KEY) == null){
                configuration.put(SALSA_CENTER_PORT_KEY, SystemFunctions.getPort());
            }                        
            if (configuration.get(SALSA_CENTER_WORKING_DIR_KEY) == null){
                configuration.put(SALSA_CENTER_WORKING_DIR_KEY, "/tmp/salsa-engine");
            }            
            if (configuration.get(SALSA_PIONEER_WORKING_DIR_KEY) == null){
                configuration.put(SALSA_PIONEER_WORKING_DIR_KEY, "/tmp/salsa-pioneer");
            }
            if (configuration.get(SALSA_REPO_KEY) == null){
                configuration.put(SALSA_REPO_KEY, "http://localhost:8080/salsa/upload/files");
            }
            configuration.list(System.out);
            logger.debug("Center endpoint: " + getSalsaCenterEndpoint());            
            
            (new File(getWorkingDir())).mkdirs();
            (new File(getServiceStorageDir())).mkdirs();
        } catch (Exception ex) {
            logger.error("Error occured when configuring salsa engine: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    private static String getSALSA_CENTER_IP(){
        return configuration.getProperty(SALSA_CENTER_IP_KEY);
    }
    
    private static String getSALSA_CENTER_PORT(){
        return configuration.getProperty(SALSA_CENTER_PORT_KEY);
    }  

    public static String getRepoPrefix() {
        return configuration.getProperty("SALSA_REPO");
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
        return getSalsaCenterEndpoint()+"/rest/artifacts/pioneer";
    }

    public static String getPioneerLocalFile() {
        return CURRENT_DIR + "/" + getPioneerRun();
    }

    // working dir of the pioneer
    public static String getWorkingDir() {
        return configuration.getProperty(SALSA_PIONEER_WORKING_DIR_KEY);
    }

    // variable file should sit beside the pioneer artifact
    public static String getSalsaVariableFile() {
        return DEFAULT_VARIABLE_FILE;        
    }

    public static String getSalsaCenterEndpoint() {
        return "http://"+getSALSA_CENTER_IP()+":"+getSALSA_CENTER_PORT()+"/salsa-engine";
    }
    
    public static String getSalsaCenterEndpointLocalhost(){
        return "http://localhost:"+getSALSA_CENTER_PORT()+"/salsa-engine";
    }

    @Deprecated
    private static String getSalsaCenterEndpointForCloudProvider(SalsaCloudProviders provider) {
        if (provider == SalsaCloudProviders.DSG_OPENSTACK) {
            return configuration.getProperty("SALSA_CENTER_ENDPOINT_LOCAL");
        } else {
            return configuration.getProperty("SALSA_CENTER_ENDPOINT");
        }
    }

    public static String getServiceStorageDir() {
        return configuration.getProperty(SALSA_CENTER_WORKING_DIR_KEY)+"/services";
    }

    public static String getArtifactStorage() {
        return configuration.getProperty(SALSA_CENTER_WORKING_DIR_KEY)+"/artifacts";
    }

    public static String getToscaTemplateStorage() {
        return configuration.getProperty(SALSA_CENTER_WORKING_DIR_KEY)+"/tosca_templates";
    }

    public static String getCloudUserParameters() {
        return configuration.getProperty("CLOUD_USER_PARAMETERS");
    }


}
