package at.ac.tuwien.dsg.cloud.salsa.pioneer.utils;

import java.io.File;
import java.io.FileReader;
import java.util.Properties;

import org.apache.log4j.Logger;

public class SalsaPioneerConfiguration {
	private static Properties configuration;
	static Logger logger;	
	private static final String CURRENT_DIR = System.getProperty("user.dir");
	private static final String CONFIG_FILE = CURRENT_DIR+"/salsa.variables";	
	
	private static String salsaCenterEndpoint;

	static {
		configuration = new Properties();
		logger = Logger.getLogger("PioneerLogger");
		logger.debug("Trying to load the configuration:" + CONFIG_FILE);
		try {			
			File f = new File(CONFIG_FILE);
			if (!f.exists()){
				logger.error("Configuration file not found: " + CONFIG_FILE);				
			} else {
				configuration.load(new FileReader(f));
				salsaCenterEndpoint = configuration.getProperty("SALSA_CENTER_ENDPOINT");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public static Properties getPioneerProperties(){
		return configuration;
	}

	public static String getWorkingDir() {
		return configuration.getProperty("SALSA_WORKING_DIR");
	}

	public static String getSalsaVariableFile() {		
		return CONFIG_FILE;
	}

	public static String getSalsaCenterEndpoint() {
		return salsaCenterEndpoint;
	}
        
        public static String getWorkingDirOfInstance(String nodeID, int instanceID){
            return getWorkingDir()+"/"+nodeID+"."+instanceID;
        }

}
