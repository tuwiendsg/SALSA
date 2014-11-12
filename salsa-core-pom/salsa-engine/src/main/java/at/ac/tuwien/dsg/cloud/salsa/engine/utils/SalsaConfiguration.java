package at.ac.tuwien.dsg.cloud.salsa.engine.utils;

import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Properties;

import org.apache.log4j.Logger;

import at.ac.tuwien.dsg.cloud.salsa.cloudconnector.multiclouds.SalsaCloudProviders;

public class SalsaConfiguration {
	private static Properties configuration;
	static Logger logger;
	static String CONFIG_FILE_2="/etc/salsa.engine.properties";
	static String CURRENT_DIR;
	static String CONFIG_FILE_1="unknown";
	
	static {
		logger = Logger.getLogger("deploymentLogger");
		
		configuration = new Properties();
		if (ClassLoader.getSystemClassLoader().getResource(".") != null){
			//CURRENT_DIR = ClassLoader.getSystemClassLoader().getResource(".").getPath();
			CURRENT_DIR = System.getProperty("user.dir");
			CONFIG_FILE_1= CURRENT_DIR + "/salsa.engine.properties";
		}
		CURRENT_DIR = System.getProperty("user.dir");
		CONFIG_FILE_1= CURRENT_DIR + "/salsa.engine.properties";
		
		try {
			File f1 = new File(CONFIG_FILE_1);
			File f2 = new File(CONFIG_FILE_2);
			logger.info("Trying to load configuration from: " + CONFIG_FILE_1 +" and " + CONFIG_FILE_2);
			if (f1.exists()) {
				logger.info("Load configuration file: " + CONFIG_FILE_1);
				configuration.load(new FileReader(f1));
			} else if (f2.exists()) {
				logger.info("Load configuration file: " + CONFIG_FILE_2);
				configuration.load(new FileReader(f2));
			} else {
				logger.info("Load default configuration file");
				InputStream is = SalsaConfiguration.class.getClassLoader().getResourceAsStream("salsa.engine.properties");
				configuration.load(is);
			}
			(new File(getWorkingDir())).mkdirs();
			(new File(getServiceStorageDir())).mkdirs();
		} catch (Exception ex) {
			logger.error("Error occured when configuring salsa engine: " + ex.getMessage());
			ex.printStackTrace();
		}
	}
	
	public static String getRepoPrefix(){
		return configuration.getProperty("SALSA_REPO");
	}
	
	public static String getPioneerFiles(){
		return configuration.getProperty("PIONEER_FILES");
	}
	
	public static String getPioneerRun(){
		return configuration.getProperty("PIONEER_RUN");
	}
	
	public static String getSSHKeyForCenter(){
		return SalsaConfiguration.class.getResource(configuration.getProperty("SALSA_PRIVATE_KEY")).getFile();
	}
	
	public static String getPioneerWeb(){
		return configuration.getProperty("PIONEER_WEB");
	}
	
	public static String getPioneerLocalFile(){
		return CURRENT_DIR + "/" + getPioneerRun();
	}
	
	// working dir of the pioneer
	public static String getWorkingDir(){
		return configuration.getProperty("WORKING_DIR");
	}
	
	// variable file should sit beside the pioneer artifact
	public static String getSalsaVariableFile(){
		return configuration.getProperty("VARIABLE_FILE");
	}
	
	public static String getSalsaCenterEndpoint(){
		return configuration.getProperty("SALSA_CENTER_ENDPOINT_LOCAL");
	}
	
	public static String getSalsaCenterEndpointForCloudProvider(SalsaCloudProviders provider){
		if (provider==SalsaCloudProviders.DSG_OPENSTACK){
			return configuration.getProperty("SALSA_CENTER_ENDPOINT_LOCAL");
		} else {
			return configuration.getProperty("SALSA_CENTER_ENDPOINT");
		}
	}
	
	public static String getServiceStorageDir(){
		return configuration.getProperty("SERVICE_STORAGE");
	}
	
	public static String getArtifactStorage(){
		return configuration.getProperty("ARTIFACT_STORAGE");
	}
	
	public static String getToscaTemplateStorage(){
		return configuration.getProperty("TOSCA_TEMPLATE_STORAGE");
	}
	
	

}
