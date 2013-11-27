package at.ac.tuwien.dsg.cloud.stratuslab.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.tapestry5.ioc.services.SymbolSource;
import org.slf4j.Logger;

import com.xerox.amazonws.ec2.InstanceType;

import ch.usi.cloud.controller.common.naming.FQN;
import ch.usi.cloud.controller.common.naming.FQNType;
import at.ac.tuwien.dsg.cloud.data.DynamicServiceDescription;
import at.ac.tuwien.dsg.cloud.data.StaticServiceDescription;
import at.ac.tuwien.dsg.cloud.data.VeeDescription;
import at.ac.tuwien.dsg.cloud.exceptions.ServiceDeployerException;
import at.ac.tuwien.dsg.cloud.services.CloudController;
import at.ac.tuwien.dsg.cloud.services.CloudInterface;
import at.ac.tuwien.dsg.cloud.services.InstanceService;
import at.ac.tuwien.dsg.cloud.services.UserDataService;

public class SLController implements CloudController{
	
	// Dependencies injected at runtime by Tapestry DI-IoC
	private Logger logger;
	// Object to retrieve configuration parameters
	private SymbolSource symbolSource;
	// Object that implements low-level cloud functionalities
	private CloudInterface cloud;
	// Private Object that injects all the user data
	private UserDataService userDataService;
	// This should be like an environmental parameter or something
	private InstanceService instanceService;
	
	public SLController(Logger logger, SymbolSource symbolSource,
			CloudInterface cloud, UserDataService userDataService,
			InstanceService instanceService) {
		this.logger = logger;
		this.symbolSource = symbolSource;
		this.cloud = cloud;
		this.userDataService = userDataService;
		this.instanceService = instanceService;
	}
	
	private final String WAIT_PORT_CHECK = ""
			+ "cat > /tmp/waitportstobeopen.sh << !\n"
			+ "#!/bin/bash\n"
			+ "echo \"Waiting the 8081 port to be open !\"\n"
			+ "r=1; while [ \"\\$r\" != \"0\" ]; do echo -e \"HEAD / HTTP/1.0\\n\\r\" | nc localhost 8081 > /dev/null; "
			+ "r=\\$?; "
			+ "if [ \"\\$r\" == \"0\" ]; then echo \\`date\\`\" PORT READY\"; else echo \\`date\\`\" PORT NOT READY\"; "
			+ "sleep 1; " + "fi; " + "done\n" + "!\n\n"
			+ "chmod +x /tmp/waitportstobeopen.sh\n\n\n";

	// compute relica number and launch
	public void launchVEE(FQN veeFQN, DynamicServiceDescription service,
			UUID deployID) throws ServiceDeployerException {
		logger.debug("Thread " + Thread.currentThread().getName()
				+ " launch veeFQN " + veeFQN);
		int replicaNum = service.getFirstNullReplicaNum(veeFQN.getVeeName());

		FQN replicaFQN = new FQN(FQN.getRootNamespace(veeFQN.toString()),
				FQN.getCustomerName(veeFQN.toString()),
				FQN.getServiceName(veeFQN.toString()), "",
				FQN.getVeeName(veeFQN.toString()), replicaNum);
		launchVEEwithReplicaFQN(replicaFQN, service, deployID);		
	}

	public void launchVEEwithReplicaFQN(FQN replicaFQN,
			DynamicServiceDescription service, UUID deployID)
			throws ServiceDeployerException {
		cloud.registerReplicaFQN(replicaFQN);
		logger.debug("Thread " + Thread.currentThread().getName()
				+ " launch replicaFQN " + replicaFQN);
		String errorMessage;
		ArrayList<String> secGroups = new ArrayList<String>();
		VeeDescription vee = null;
		try {
			vee = service.getVeeDescription(new FQN(replicaFQN.toString(),
					FQNType.VEE));
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceDeployerException("", e);
		}
		// check instance type
		logger.debug("Vee Description " + vee);
		InstanceType type = InstanceType.getTypeFromString(vee.getInstanceType());
		if (type == null) {
			logger.warn("InstanceType "
					+ vee.getInstanceType()
					+ " is not defined as a field of com.xerox.amazonws.ec2.InstanceType, "
					+ "using default instanceType " + InstanceType.DEFAULT
					+ " instead");
			type = InstanceType.DEFAULT;
		}
		logger.debug(" type " + type + " " + vee.getInstanceType());
		String userdata = "";
		try {
			userdata = prepareUserData(service, vee, Integer.parseInt(FQN
					.getVeeReplicaNumber(replicaFQN.toString())), replicaFQN,
					type, deployID);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Problems in preparing the user data");
		}
		
		secGroups.addAll(vee.getSecurityGroups());
		
		logger.debug("\t Launching new instance with parameters :" + "\n\n"
				+ "\t\t" + "baseImageID = "
				+ vee.getBaseImageId()
				+ "\n"
				+ "\t\t"
				+ "securityGroups = "
				+ vee.getSecurityGroups()
				+ "\n"
				+ "\t\t"
				+ "TAGS = "
				+ secGroups
				+ "\n"
				+ "\t\t"
				+ "keyName = "
				+ vee.getSshKeyName()
				+ "\n"
				+ "\t\t"
				+ "userdata size = \n"
				+ userdata.length()
				+ "\n"
				+ "\t\t"
				+ "type = "
				+ type
				+ "\n"
				+ "\t\t" + "replicaFQN = " + replicaFQN);
		String launchedInstanceId;
		try {
			launchedInstanceId = cloud.launchInstance(vee.getBaseImageId(), secGroups, vee.getSshKeyName(), userdata, type, 1, 1);
			System.out.println("LAUNCHED INSTANCE ID = " + launchedInstanceId);
			// Update the Shared structure
			service.addVeeInstance(vee,
					cloud.getInstanceDescriptionByID(launchedInstanceId));
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceDeployerException("Error when launching Stratuslab instance", e);
		}

	}
	
	public String prepareUserData(DynamicServiceDescription service,
			VeeDescription vee, Integer replicaNum, FQN replicaFQN,
			InstanceType type, UUID deployID) {
		return "";
	}

	public void removeVEEbyInstanceID(String instanceToTerminateID,
			StaticServiceDescription service) {
		// TODO Auto-generated method stub
		
	}

	public void removeVEEbyInstanceID(String instanceToTerminateID) {
		// TODO Auto-generated method stub
		
	}

	public void removeVEEsbyInstanceID(DynamicServiceDescription service,
			List<String> instancesToTerminateID) {
		// TODO Auto-generated method stub
		
	}
	

}
