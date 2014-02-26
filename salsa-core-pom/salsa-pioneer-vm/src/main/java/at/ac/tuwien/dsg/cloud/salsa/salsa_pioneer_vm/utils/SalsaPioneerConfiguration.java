package at.ac.tuwien.dsg.cloud.salsa.salsa_pioneer_vm.utils;

import java.io.File;
import java.io.FileReader;
import java.util.Properties;

import org.apache.log4j.Logger;

public class SalsaPioneerConfiguration {
	private static Properties configuration_local;
	private static Properties configuration_setting;
	static Logger logger;
	private static final String CONFIG_FILE = "/etc/salsa.variables";
	
	private static String salsaCenterEndpoint;

	static {
		configuration_local = new Properties();
		configuration_setting = new Properties();
		try {			
			configuration_local.load(SalsaPioneerConfiguration.class.getClassLoader()
					.getResourceAsStream("salsa.pioneer.properties"));
			
			File f = new File(CONFIG_FILE);
			if (f.exists()) {
				configuration_setting.load(new FileReader(f));
				salsaCenterEndpoint = configuration_setting.getProperty("SALSA_CENTER_ENDPOINT");
			} else {
				salsaCenterEndpoint = configuration_local.getProperty("SALSA_CENTER_ENDPOINT");
			}
			logger = Logger.getLogger("PioneerLogger");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}


	public static String getSSHKeyForCenter() {
		return SalsaPioneerConfiguration.class.getResource(
				configuration_local.getProperty("SALSA_PRIVATE_KEY")).getFile();
	}

	public static String getWorkingDir() {
		return configuration_local.getProperty("WORKING_DIR");
	}

	public static String getSalsaVariableFile() {
		return configuration_local.getProperty("VARIABLE_FILE");	}


	public static String getSalsaCenterEndpoint() {
		return salsaCenterEndpoint;
	}

}
