package at.ac.tuwien.dsg.cloud.salsa.engine.utils;

import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

import at.ac.tuwien.dsg.cloud.salsa.cloud_connector.multiclouds.SalsaCloudProviders;

public class SalsaConfiguration {
	private static Properties configuration;
	static Logger logger;
	static String CONFIG_FILE="/etc/salsa.engine.properties";
	
	static {
		configuration = new Properties();
		try {
			
			File f = new File(CONFIG_FILE);
			if (f.exists()) {
				configuration.load(new FileReader(f));
			} else {
				InputStream is = SalsaConfiguration.class.getClassLoader().getResourceAsStream("salsa.engine.properties");
				configuration.load(is);
			}			
			logger = Logger.getLogger("deploymentLogger");
			
			//(new File(getWorkingDir())).mkdirs();
			
		} catch (Exception ex) {
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
	
	
	public static String getWorkingDir(){
		return configuration.getProperty("WORKING_DIR");
	}
	
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
//		String configKey = "SALSA_CENTER_ENDPOINT_@_"+provider.getCloudProviderString();
//		System.out.println(configKey);
//		return configuration.getProperty(configKey);
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
