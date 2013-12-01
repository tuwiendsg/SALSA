package at.ac.tuwien.dsg.cloud.salsa.engine.utils;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EngineLogger {
	public static Logger logger ;
	
	static{		
       // PropertyConfigurator.configure(EngineLogger.class.getResourceAsStream("/log4j.properties"));
        logger = LoggerFactory.getLogger("EngineLogger");		
	}
}
