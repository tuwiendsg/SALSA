package at.ac.tuwien.dsg.cloud.salsa.cloudconnector.localhost;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

import org.slf4j.Logger;

import at.ac.tuwien.dsg.cloud.salsa.cloudconnector.CloudInterface;
import at.ac.tuwien.dsg.cloud.salsa.cloudconnector.InstanceDescription;
import at.ac.tuwien.dsg.cloud.salsa.cloudconnector.ServiceDeployerException;
import at.ac.tuwien.dsg.cloud.salsa.cloudconnector.VMStates;

/**
 * This connector enable deployment on localhost machine
 * In the future should be extended to further mechanism
 * 
 * @author hungld
 *
 */
public class LocalhostConnector implements CloudInterface {
	Logger logger;
	
	public LocalhostConnector(Logger logger) {
		this.logger = logger;
	}

	@Override
	public InstanceDescription getInstanceDescriptionByID(String instanceID) {
		logger.debug("Get information for localhost");
		String hostName = "localhost";
		try {
			hostName = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			logger.debug("Warning: cannot get hostname of the localhost");
		}
		String ip = "127.0.0.1";
		InstanceDescription inst = new InstanceDescription(hostName,ip,ip);
		inst.setState(VMStates.Running);
		
		logger.debug("Get information for localhost: DONE");
		return inst;
	}

	@Override
	public String launchInstance(String instanceName, String imageId, List<String> securityGroups, String sshKeyName, String userData, String instType, int minInst, int maxInst) throws ServiceDeployerException {
		// run the userdata
		logger.debug("Trying to run command");
		try {			
			String tmp_file = "/tmp/" + UUID.randomUUID().toString();
			logger.debug("Command file name: " + tmp_file);
			File f = new File(tmp_file);
			PrintWriter out = new PrintWriter(f);
			out.print(userData);
			out.flush();
			out.close();
			
			runLocalhostCommandNoWaitingFor("/bin/bash " + tmp_file);
			
			//f.delete();
		} catch (FileNotFoundException e) {
			logger.debug("Error when configuring pioneer on localhost");
		}
		return "localhost";
	}

	@Override
	public void removeInstance(String instanceToTerminateID) throws ServiceDeployerException {
		// we do not remove the localhost but clean all pioneer on it, also remove all the docker
		runLocalhostCommandNoWaitingFor("pkill -f salsa-pioneer");
		
		String listOfContainer = runLocalhostCommand("sudo docker ps -a -q");
		Scanner scanner = new Scanner(listOfContainer);
		while (scanner.hasNextLine()) {
		  String line = scanner.nextLine();
		  runLocalhostCommandNoWaitingFor("sudo docker rm " + line);
		}
		scanner.close();				
	}
	
	
	private String runLocalhostCommand(String command){
		Process p;
		String re = "";
		try {			
			logger.debug("Executing command on localhost !");
			p = Runtime.getRuntime().exec(command);

			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line = reader.readLine();
			while (line != null) {
				line = reader.readLine();	
				re += line + "\n";
			}
			p.waitFor();			
			logger.debug("Exit value : "+p.exitValue());
			
		} catch (Exception e) {
			logger.error(e.toString());			
		}
		return re;
	}
	
	private void runLocalhostCommandNoWaitingFor(String command){
		try {			
			logger.debug("Executing command on localhost without wating !");
			Runtime.getRuntime().exec(command);
			try{
				Thread.sleep(2);
			} catch (InterruptedException e){
				logger.error(e.getMessage());
			}
		} catch (Exception e) {
			logger.error(e.toString());			
		}

	}

}
