package at.ac.tuwien.dsg.cloud.salsa.pioneer.utils;


import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PioneerLogger {
	public static Logger logger ;
	
	static{		
        PropertyConfigurator.configure(PioneerLogger.class.getResourceAsStream("/log4j.properties"));
        logger = LoggerFactory.getLogger("PioneerLogger");		
	}
}
