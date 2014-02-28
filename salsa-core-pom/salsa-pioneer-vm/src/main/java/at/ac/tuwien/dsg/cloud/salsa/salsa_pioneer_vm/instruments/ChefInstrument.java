package at.ac.tuwien.dsg.cloud.salsa.salsa_pioneer_vm.instruments;

import generated.oasis.tosca.TNodeTemplate;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

import at.ac.tuwien.dsg.cloud.salsa.salsa_pioneer_vm.utils.PioneerLogger;

public class ChefInstrument implements InstrumentInterface {
	
	Logger logger = PioneerLogger.logger;

	@Override
	public void initiate(TNodeTemplate node) {
		PioneerLogger.logger.debug("INSTRUMENT INITIATE: CHEF");
		// install chef if not ready
		File chefFile = new File("/usr/bin/chef-client");
		if (!chefFile.exists()){
			try {				
				// install chef-client
				PioneerLogger.logger.debug("Prepare installing CHEF...");
				Process p = Runtime.getRuntime().exec("curl -L https://www.opscode.com/chef/install.sh");				
				p.waitFor();
				
				PioneerLogger.logger.debug("Writing installation file...");
				StringBuffer sb = new StringBuffer();
				BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));				 
			    String line = "";			
			    while ((line = reader.readLine())!= null) {
			    	sb.append(line + "\n");
			    }
			    
			    BufferedWriter out = new BufferedWriter(new FileWriter("install.sh"));  
		        out.write(sb.toString());  
		        out.flush();  
		        out.close();
		        
		        // RUN THE INSTALLTION
		        PioneerLogger.logger.debug("RUN THE INSTALATION...");
		        Process p1 = Runtime.getRuntime().exec("bash install.sh");
				p1.waitFor();
			    // SEE THE OUTPUT
				sb = new StringBuffer();
				reader = new BufferedReader(new InputStreamReader(p1.getInputStream()));		 
			    while ((line = reader.readLine())!= null) {
			    	sb.append(line + "\n");
			    }
			    System.out.println(sb.toString());			    
				
				
				File chefDir=new File("/etc/chef");
				chefDir.mkdirs();
				// write validation.pem
				
				InputStream is=ChefInstrument.class.getResourceAsStream("/chef/validation.pem");
				OutputStream os=new FileOutputStream("/etc/chef/validation.pem");
				int read = 0;
				byte[] bytes = new byte[1024];
		 
				while ((read = is.read(bytes)) != -1) {
					os.write(bytes, 0, read);
				}
				is.close();
				os.close();
				
			} catch (Exception e){
				logger.debug("Error when installing chef. Error: " + e);
			}
		}
	}
	
	/**
	 * put the client.rc with the chef name on it
	 */
	@Override
	public String deployArtifact(String uri) {
		try {
			
			StringBuffer sb = new StringBuffer();

			sb.append("log_level 		:auto \n");
			sb.append("log_location     STDOUT \n");
			sb.append("log_location     STDOUT \n");
			sb.append("chef_server_url  \"https://api.opscode.com/organizations/tuwien\" \n");
			sb.append("validation_client_name \"tuwien-validator\" \n");
			sb.append("node_name \"" + uri.trim() + "\" \n");
			
			BufferedWriter out = new BufferedWriter(new FileWriter("/etc/chef/client.rb"));  
	        out.write(sb.toString());  
	        out.flush();  
	        out.close();
			
			Process p = Runtime.getRuntime().exec("chef-client");
		    p.waitFor();
		 
		    BufferedReader reader = 
		         new BufferedReader(new InputStreamReader(p.getInputStream()));
		    StringBuffer sb2 = new StringBuffer();
		    String line = "";			
		    while ((line = reader.readLine())!= null) {
		    	sb2.append(line + "\n");
		    }
		    
		    PioneerLogger.logger.debug(sb2.toString());	        		    
			return "Done";
			
		} catch (Exception e){
			PioneerLogger.logger.debug(e.toString());
			return null;
		}
	}

	@Override
	public String getStatus(String nodeId, String instanceId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String executeArtifactAction(String action, String nodeId,
			String instanceId) {
		// TODO Auto-generated method stub
		return null;
	}

	
	
}
