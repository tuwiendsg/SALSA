package at.ac.tuwien.dsg.cloud.salsa.salsa_pioneer_vm;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;

import at.ac.tuwien.dsg.cloud.salsa.salsa_pioneer_vm.utils.PioneerLogger;

public class SystemFunctions {
	
	private final static File configFile= new File("/etc/profile.d/salsa-relationship-values.sh");
	private final static File envFileGlobal= new File("/etc/environment");
	
	static {
		if (!configFile.exists()){
			try{
				configFile.createNewFile();
			} catch (IOException e){
				PioneerLogger.logger.error("Could not create config file. Error: " + e);				
			}
		}
	}
	
	public static void writeSystemVariable(String key, String value){
		try {
			PioneerLogger.logger.debug("WRITE ENV: " + key+" -- " + value);
//			FileWriter fileWritter = new FileWriter(configFile.getName(),true);
//	        BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
	        String data="export "+key + "=" + value + "\n";
	        
//	        bufferWritter.write(data);
//	        bufferWritter.flush();
//	        bufferWritter.close();
	        
	        PioneerLogger.logger.debug(data);
	        PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(configFile.getPath(), true)));
	        out.println(data);
	        out.flush();
	        out.close();
	        
	        String data1=key + "=" + value + "\n";
	        out = new PrintWriter(new BufferedWriter(new FileWriter(envFileGlobal.getPath(), true)));
	        out.println(data1);
	        out.flush();
	        out.close();
	        
	        
	        // write to both
//	        fileWritter = new FileWriter(envFileGlobal.getName(),true);
//	        bufferWritter = new BufferedWriter(fileWritter);
//	        String data1=key + "=" + value + "\n";
//	        bufferWritter.write(data1);
//	        bufferWritter.flush();
//	        bufferWritter.close(); 
	        
	        // do the export	        
	        
		} catch (IOException e){
			PioneerLogger.logger.error("Could not write in config file. Error: " + e);
		}

	}
	
	public static String getLocalIpAddress(){
		try {
			String ip = InetAddress.getLocalHost().getHostAddress();
			return ip;
		} catch(Exception e){
			PioneerLogger.logger.error("Cannot get the local IP adress");			
		}		
		return "";
	}
	
}
