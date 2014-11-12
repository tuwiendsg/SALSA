package at.ac.tuwien.dsg.cloud.salsa.pioneer.utils;

import java.io.File;
import java.io.FileReader;
import java.util.Properties;

import org.apache.log4j.Logger;

public class SalsaPioneerConfiguration {
	private static Properties configuration;
	static Logger logger;	
	private static final String CONFIG_FILE = ClassLoader.getSystemClassLoader().getResource(".").getPath()+"/salsa.variables";	
	
	private static String salsaCenterEndpoint;

	static {
		configuration = new Properties();
		logger = Logger.getLogger("PioneerLogger");
		try {			
			File f = new File(CONFIG_FILE);
			if (f.exists()){
				logger.error("Configuration file not found: " + CONFIG_FILE);				
			} else {
				configuration.load(new FileReader(f));
				salsaCenterEndpoint = configuration.getProperty("SALSA_CENTER_ENDPOINT");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}


	public static String getSSHKeyForCenter() {
		return SalsaPioneerConfiguration.class.getResource(
				configuration.getProperty("SALSA_PRIVATE_KEY")).getFile();
	}

	public static String getWorkingDir() {
		return configuration.getProperty("WORKING_DIR");
	}

	public static String getSalsaVariableFile() {		
		return configuration.getProperty("VARIABLE_FILE");
	}

	public static String getSalsaCenterEndpoint() {
		return salsaCenterEndpoint;
	}

}
