package at.ac.tuwien.dsg.cloud.stratuslab.services.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;

import at.ac.tuwien.dsg.cloud.data.InstanceDescription;
import at.ac.tuwien.dsg.cloud.exceptions.ServiceDeployerException;
import at.ac.tuwien.dsg.cloud.services.CloudInterface;
import at.ac.tuwien.dsg.cloud.stratuslab.utils.ConfigurationStratuslab;
import at.ac.tuwien.dsg.cloud.stratuslab.utils.ProcessUtils;
import ch.usi.cloud.controller.common.naming.FQN;
import ch.usi.cloud.controller.common.naming.FQNType;

import com.xerox.amazonws.ec2.InstanceType;

public class StratusLabConnector implements CloudInterface{
	
	private Logger logger;
	private String VMName_As_Tag;	// format: [serviceFQN]_0_[deployID]
	private final String DIVIDE_STR="_0_";
	private int check_retry=20;
	private int check_inteval=1000;
	private String env[] = {"PYTHONPATH="+ConfigurationStratuslab.getPythonPath()};
	private String bindir = ConfigurationStratuslab.getBinDir().trim()+"/";	
	
	public StratusLabConnector(Logger logger){
		this.logger = logger;
		initialize();
	}
	
	/*
	 * Prepare ENDPOINT tag
	 */
	private void initialize(){
		// No tag mechanism having for stratus. Can use #Endpoint# in name
	}
	
	@Deprecated
	public boolean isRegisteredService(FQN serviceFQN)
			throws ServiceDeployerException {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Deprecated
	public void registerService(FQN serviceFQN) throws ServiceDeployerException {
		// Should create Sec group here, but not available. Use Name
		this.VMName_As_Tag = serviceFQN.toString();		
	}
	
	@Deprecated
	public void deregisterService(FQN serviceFQN)
			throws ServiceDeployerException {
		// Nothing to do here		
	}

	public void registerDeploy(FQN serviceFQN, UUID deployID)
			throws ServiceDeployerException {
		logger.info("Registering deploy " + deployID);
		String _deployID = deployID.toString();
		String _serviceFQN = serviceFQN.toString().replace(".", "_");
		// using name
		this.VMName_As_Tag = serviceFQN+DIVIDE_STR+deployID;	
		
	}

	public void deregisterDeploy(FQN serviceFQN, UUID deployID)
			throws ServiceDeployerException {
		// Nothing to do here
		
	}

	public Set<String> getServiceInstances(FQN serviceFQN, UUID deployID)
			throws ServiceDeployerException {
		logger.info("StratusLabConnector.getServiceInstances(" + serviceFQN + " , " + deployID
				+ ")");
		Set<String> result = new HashSet<String>();
		if (serviceFQN == null) {
			logger.warn("Null ServiceFQN");
			return result;
		}

		if (!deployID.toString().equals("")) {
			return getServiceInstanceInstances(deployID.toString());
		} else {
			logger.warn("Empty deployID");
			return result;
		}
	}
	
	// Find the deployID corresponding to _serviceFQN
	private Set<String> getServiceInstanceInstances(String _deployID)
			throws ServiceDeployerException {		
		// list all VM with name contain _deployID and add to SET
		String full=runStratusCommand("stratus-describe-instance | grep "+_deployID+" | awk '{print $1}' |awk '{if(NR>1)print}'");
		Set<String> serviceInstances = new HashSet<String>(Arrays.asList(full.split("\n")));		
		return serviceInstances;
	}
	
	private String runStratusCommand(String command){
		Process p;
		String re = "";
		try {
			p = Runtime.getRuntime().exec(bindir+command, env);
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
	

	public Set<UUID> getDeployIDs(FQN serviceFQN)
			throws ServiceDeployerException {
		logger.info("getDeployIDs(" + serviceFQN + ")");
		Set<UUID> result = new HashSet<UUID>();
		if (serviceFQN==null){
			logger.warn("Null ServiceFQN");
			return result;
		}
		String _serviceFQN = serviceFQN.toString().replace(".", "_");
		String full=runStratusCommand("stratus-describe-instance | grep "+_serviceFQN+" | awk '{print $7}' |awk '{if(NR>1)print}'");
		Set<String> serviceInstances = new HashSet<String>(Arrays.asList(full.split("\n")));
		for (String str : serviceInstances) {
			// extract serviceFQN from : [serviceFQN]_0_[deployID]
			result.add(UUID.fromString(str.substring(str.indexOf(DIVIDE_STR)-1)));
		}		
		return result;
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
			String instanceName = instanceDes.split(" ")[6];
			FQN serviceFQN = null;
			if (instanceName.contains(".")){
				String serviceFQN_str = instanceName.substring(
					instanceName.indexOf(DIVIDE_STR) - 1).replace("_", "."); // the first part of name			
				serviceFQN = new FQN(serviceFQN_str, FQNType.SERVICE);
			}
			String ip = instanceDes.split(" ")[5];

			InstanceDescription id = new InstanceDescription(serviceFQN,
					instanceID, ip, ip, "", "");
			id.setState(instanceDes.split(" ")[1]);
			logger.info("\n\nFound instance " + instanceID + " "
					+ id.getPrivateIp() + " " + id.getPublicIp());			
			return id;
		} catch (Exception e) {
			logger.error(e.toString());
			return null;
		}	
		
	}

	public void registerReplicaFQN(FQN replicaFQN)
			throws ServiceDeployerException {
		logger.warn("registerReplicaFQN: Not YET implemented!");		
	}

	public void deregisterReplicaFQN(FQN replicaFQN) {
		logger.warn("deregisterReplicaFQN: Not YET implemented!");
		
	}
	
	/*
	 * Note: sshKeyName is the file of publickey
	 */
	public String launchInstance(String imageId, List<String> securityGroups,
			String sshKeyName, String userData, InstanceType instType,
			int minInst, int maxInst) throws ServiceDeployerException {
		logger.debug("Launching instance with TAGs: \n" + securityGroups);
		try {
			// put userData to tmp file
			String tmpUserDataFile = "/tmp/user_data_stratus_"+UUID.randomUUID().toString();			
			FileUtils.writeStringToFile(new File(tmpUserDataFile), userData);
			String ppkFile= StratusLabConnector.class.getResource(ConfigurationStratuslab.getPublicKeyFile()).getFile();
			String cmd[] = {					
				ConfigurationStratuslab.getBinDir().trim()+"/stratus-run-instance",
				"--quiet",
				"-t", instType.getTypeId(),
				"--cloud-init",
				"ssh,"+ppkFile+"#x-shellscript,"+tmpUserDataFile,
				//"-c "+ConfigurationStratuslab.getConfigFile(),
				imageId
			};
		String env[] = {"PYTHONPATH="+ConfigurationStratuslab.getPythonPath()};
		// result should be: ID, IP (e.g. "323, 134.158.75.215")
		String result[] = ProcessUtils.execGetOutput(cmd,env).split(",");
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
