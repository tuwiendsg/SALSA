/*
 * Copyright (c) 2013 Technische Universitat Wien (TUW), Distributed Systems Group. http://dsg.tuwien.ac.at
 *
 * This work was partially supported by the European Commission in terms of the CELAR FP7 project (FP7-ICT-2011-8 #317790), http://www.celarcloud.eu/
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package at.ac.tuwien.dsg.cloud.salsa.cloudconnector.stratuslab;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;

import at.ac.tuwien.dsg.cloud.salsa.cloudconnector.CloudInterface;
import at.ac.tuwien.dsg.cloud.salsa.cloudconnector.InstanceDescription;
import at.ac.tuwien.dsg.cloud.salsa.cloudconnector.ServiceDeployerException;
import at.ac.tuwien.dsg.cloud.salsa.cloudconnector.VMStates;

public class StratusLabConnector implements CloudInterface{
	
	private Logger logger;
	private final String DIVIDE_STR="_0_";
	private int check_retry=20;
	private int check_inteval=1000;
	private ArrayList<String> env = new ArrayList<String>();
	private String bindir;
	private String config_file = "/tmp/stratus-user.cnf";
	private String public_key_file;
	
	
	public static void main(String[] args) {
		String resource = StratusLabConnector.class.getResource("stratuslab-client").getFile();
		String bin = resource + "/bin";
		String python = resource + "/lib/stratuslab/python";
		System.out.println(bin);
		System.out.println(python);
	}
	
	public StratusLabConnector(Logger logger, String endpoint,
			String pdisk_endpoint, String username, String password,
			String user_public_key_file, String client_path) {
		//String resource = StratusLabConnector.class.getResource("/stratuslab-client").getFile();
		this.logger = logger;		
		this.env.add("PYTHONPATH=" + client_path + "/lib/stratuslab/python");
		this.bindir = client_path + "/bin";
		this.public_key_file = user_public_key_file;
		
		File f = new File(config_file);		
		if (!f.exists()) {
			try {				 
				String content = "[default] \n";
				content += "endpoint = " + endpoint + "\n";
				content += "pdisk_endpoint = " + pdisk_endpoint + "\n";
				content += "username = " + username + "\n";
				content += "password = " + password + "\n";
				content += "user_public_key_file = " + user_public_key_file + "\n";
	 
				f.createNewFile();
				
				FileWriter fw = new FileWriter(f.getAbsoluteFile());
				BufferedWriter bw = new BufferedWriter(fw);
				bw.write(content);
				bw.close();
	 
				System.out.println("Wrote stratuslab config file: " + config_file);
	 
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
	
	
		
	private String runStratusCommand(String command){
		Process p;
		String re = "";
		try {
			String cmdTotal = bindir+"/"+command + " -c "+ config_file;
			p = Runtime.getRuntime().exec( cmdTotal, env.toArray(new String[env.size()]));
			//p = Runtime.getRuntime().exec("sh -c "+ bindir+command+"", env);
			//String[]cmd = {"sh","-c", bindir+command};
			//p = Runtime.getRuntime().exec(cmd, env);
			
			p.waitFor();

			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line = reader.readLine();
			while (line != null) {
				line = reader.readLine();				
				re += line + "\n";
			}
			logger.debug("Execute cmd: "+command);
			logger.debug("Exit value : "+p.exitValue());
			
		} catch (Exception e) {
			logger.error(e.toString());			
		}
		return re;
	}
	

	
	public InstanceDescription getInstanceDescriptionByID(String instanceID) {
		// This command will return one and only one line, if instance is existed
		// e.g: 799 Running 1 1572864 0 134.158.75.104 service123_0_deployID
		int step = 0;
		String instanceDes="";
		String instanceDesRaw = "";
		while (step <check_retry && instanceDes.equals("")){
			instanceDesRaw=runStratusCommand("stratus-describe-instance -n");			
			Scanner scanner = new Scanner(instanceDesRaw);
			while (scanner.hasNextLine()) {
			  String line = scanner.nextLine();
			  if (!line.trim().equals("") && line.replaceAll("\\s+", " ").split(" ")[0].equals(instanceID)){
				  instanceDes = line.replaceAll("\\s+", " ");
				  System.out.println("FOUND: "+instanceDes);
				  break;
			  }
			}
			scanner.close();
			if (instanceDes.trim().equals("")){	// not update yet, retry
				try {
					System.out.println("Sleep: "+instanceDes);
					Thread.sleep(check_inteval);
				} catch (Exception e) {}				
			}
			step++;
		}
		if (instanceDes.trim().equals("")) {
			logger.warn("Not fould Instance: " + instanceID);
			return null;
		}
		try {			
			String ip = instanceDes.split(" ")[5];

			InstanceDescription id = new InstanceDescription(instanceID, ip, ip);
			String stateStr =instanceDes.split(" ")[1];
			id.setState(VMStates.fromString(stateStr));			
			logger.info("\n\nFound instance " + instanceID + " "
					+ id.getPrivateIp() + " " + id.getPublicIp());			
			return id;
		} catch (Exception e) {
			logger.error(e.toString());
			return null;
		}	
		
	}

	
	
	/*
	 * Note: sshKeyName is the file of publickey
	 */
	public String launchInstance(String instanceName, String imageId, List<String> securityGroups,
			String sshKeyName, String userData, String instType,
			int minInst, int maxInst) throws ServiceDeployerException {
		logger.debug("Launching instance with TAGs: \n" + securityGroups);
		try {
			// put userData to tmp file
			String tmpUserDataFile = "/tmp/user_data_stratus_"+UUID.randomUUID().toString();			
			FileUtils.writeStringToFile(new File(tmpUserDataFile), userData);			
			String cmd[] = {					
				this.bindir+"/stratus-run-instance",
				"--quiet",
				"-t", instType,
				"--cloud-init",
				"ssh,"+this.public_key_file+"#x-shellscript,"+tmpUserDataFile,
				"-c", this.config_file,
				imageId
			};		
		// result should be: ID, IP (e.g. "323, 134.158.75.215")
			StringBuilder builder = new StringBuilder();
			for(String s : cmd) {
			    builder.append(s+" ");
			}
			
			logger.debug("Stratus cmd: " + builder.toString());
		String result[] = ProcessUtils.execGetOutput(cmd,this.env.toArray(new String[env.size()])).split(",");
		if (result.length > 1){
			return result[0].trim();	// the Stratuslab ID of new VM
		} else {
			logger.error("Cannot create Stratuslab instance");
			return "";
		}
		} catch (Exception e){
			logger.error(e.toString());
		}
		return null;
	}

	public void removeInstance(String instanceToTerminateID)
			throws ServiceDeployerException {
		runStratusCommand("stratus-kill-instance " + instanceToTerminateID);		
	}

	public String getInstance(UUID deployID) throws ServiceDeployerException {
		String instanceDes=runStratusCommand("stratus-describe-instance -n | grep "+deployID.toString()).replaceAll("\\s+", " ");
		return instanceDes.split(" ")[0];		
	}

	public List<String> getSecurityGroups(String instanceID)
			throws ServiceDeployerException {
		String instanceDes=runStratusCommand("stratus-describe-instance "+instanceID).replaceAll("\\s+", " ");
		List<String> lst = new ArrayList<String>();
		lst.add(instanceDes.split(" ")[7]);
		// attract serviceID		
		return lst;
	}
	

}
