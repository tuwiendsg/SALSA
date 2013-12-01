package at.ac.tuwien.dsg.cloud.salsa.engine.utils;

import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

public class SalsaConfiguration {
	private static Properties configuration;
	static Logger logger;
	
	static {
		configuration = new Properties();
		try {
			InputStream is = SalsaConfiguration.class.getClassLoader()
					.getResourceAsStream("salsa.engine.properties");
			configuration.load(is);
			logger = Logger.getLogger("deploymentLogger");
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public static String getPioneerFiles(){
		return configuration.getProperty("PIONEER_FILES");
	}
	
	public static String getPioneerRun(){
		return configuration.getProperty("PIONEER_RUN");
	}
	
	
//	public static String getSalsaCenterStoragePath(){
//		return configuration.getProperty("SERVICE_STORAGE");
//	}
	
	public static String getSSHKeyForCenter(){
		return SalsaConfiguration.class.getResource(configuration.getProperty("SALSA_PRIVATE_KEY")).getFile();
	}
	
//	public static String getPioneerFolder(){
//		return configuration.getProperty("PIONEER_FOLDER");
//	}
	
	public static String getPioneerWeb(){
		return configuration.getProperty("PIONEER_WEB");
	}
	
//	public static String getServiceInstanceRepo(){
//		return configuration.getProperty("SERVICE_INSTANCE_REPOSITORY");
//	}
	
	public static String getWorkingDir(){
		return configuration.getProperty("WORKING_DIR");
	}
	
	public static String getSalsaVariableFile(){
		return configuration.getProperty("VARIABLE_FILE");
	}
	
//	public static String getSalsaCenterIP(){
//		return configuration.getProperty("SALSA_CENTER_IP");
//	}
//	
//	public static String getSalsaCenterPort(){
//		return configuration.getProperty("SALSA_CENTER_PORT");
//	}
//	
//	public static String getSalsaCenterPath(){
//		return configuration.getProperty("SALSA_CENTER_PATH");
//	}
	
	public static String getSalsaCenterEndpoint(){
		return configuration.getProperty("SALSA_CENTER_ENDPOINT");
	}

}
