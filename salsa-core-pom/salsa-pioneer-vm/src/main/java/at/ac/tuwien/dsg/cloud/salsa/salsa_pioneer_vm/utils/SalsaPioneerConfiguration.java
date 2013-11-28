package at.ac.tuwien.dsg.cloud.salsa.salsa_pioneer_vm.utils;

import java.util.Properties;

import org.apache.log4j.Logger;

public class SalsaPioneerConfiguration {
	private static Properties configuration;
	static Logger logger;
	private static final String CONFIG_FILE = "/etc/salsa.variables";

	static {
		configuration = new Properties();
		try {
//			File f = new File(CONFIG_FILE);
//			if (f.exists()) {
//				configuration.load(new FileReader(f));
//			} else {
			configuration.load(SalsaPioneerConfiguration.class.getClassLoader()
					.getResourceAsStream("salsa.pioneer.properties"));
//			}
			logger = Logger.getLogger("PioneerLogger");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	//
	// public static String getPioneerFile(){
	// return configuration.getProperty("PIONEER_FILE");
	// }
	//
	// public static String getSalsaCenterStoragePath(){
	// return configuration.getProperty("SERVICE_STORAGE");
	// }

	public static String getSSHKeyForCenter() {
		return SalsaPioneerConfiguration.class.getResource(
				configuration.getProperty("SALSA_PRIVATE_KEY")).getFile();
	}

	// public static String getPioneerFolder(){
	// return configuration.getProperty("PIONEER_FOLDER");
	// }
	//
	// public static String getPioneerWeb(){
	// return configuration.getProperty("PIONEER_WEB");
	// }
	//
	// public static String getServiceInstanceRepo(){
	// return configuration.getProperty("SERVICE_INSTANCE_REPOSITORY");
	// }

	public static String getWorkingDir() {
		return configuration.getProperty("WORKING_DIR");
	}

	public static String getSalsaVariableFile() {
		return configuration.getProperty("VARIABLE_FILE");
	}

	// public static String getSalsaCenterIP(){
	// return configuration.getProperty("SALSA_CENTER_IP");
	// }
	//
	// public static String getSalsaCenterPort(){
	// return configuration.getProperty("SALSA_CENTER_PORT");
	// }
	//
	// public static String getSalsaCenterPath(){
	// return configuration.getProperty("SALSA_CENTER_PATH");
	// }

	public static String getSalsaCenterEndpoint() {
		return configuration.getProperty("SALSA_CENTER_ENDPOINT");
	}

}
