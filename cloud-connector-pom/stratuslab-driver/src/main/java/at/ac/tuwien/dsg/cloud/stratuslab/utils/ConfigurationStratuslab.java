package at.ac.tuwien.dsg.cloud.stratuslab.utils;

import java.io.InputStream;
import java.util.Properties;

public class ConfigurationStratuslab {
	private static Properties configuration;
	static {
		configuration = new Properties();
		try {
			InputStream is = ConfigurationStratuslab.class.getClassLoader()
					.getResourceAsStream("stratuslab.properties");
			configuration.load(is);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public static String getBinDir()
    {
    	return configuration.getProperty("BINDIR");
    }
	
	public static String getPythonPath()
    {
    	return configuration.getProperty("PYTHONPATH");
    }
	
	public static String getConfigFile()
    {
    	return configuration.getProperty("CONFIGFILE");
    }
	
	public static String getPublicKeyFile(){
		return configuration.getProperty("PUBLIC_KEY");
	}
	
}
