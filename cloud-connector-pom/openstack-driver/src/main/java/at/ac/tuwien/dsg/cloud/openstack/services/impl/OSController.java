package at.ac.tuwien.dsg.cloud.openstack.services.impl;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.apache.tapestry5.ioc.services.SymbolSource;
import org.slf4j.Logger;

import at.ac.tuwien.dsg.cloud.data.DynamicProperty;
import at.ac.tuwien.dsg.cloud.data.DynamicServiceDescription;
import at.ac.tuwien.dsg.cloud.data.InstanceDescription;
import at.ac.tuwien.dsg.cloud.data.StaticProperty;
import at.ac.tuwien.dsg.cloud.data.StaticServiceDescription;
import at.ac.tuwien.dsg.cloud.data.VeeDescription;
import at.ac.tuwien.dsg.cloud.exceptions.ServiceDeployerException;
import at.ac.tuwien.dsg.cloud.services.CloudController;
import at.ac.tuwien.dsg.cloud.services.CloudInterface;
import at.ac.tuwien.dsg.cloud.services.InstanceService;
import at.ac.tuwien.dsg.cloud.services.UserDataService;
import ch.usi.cloud.controller.common.naming.FQN;
import ch.usi.cloud.controller.common.naming.FQNType;
import ch.usi.controlinterface.client.ControlInterfaceClient;

import com.xerox.amazonws.ec2.InstanceType;

/**
 * 
 * This class implements the methods to launch or remove a VEE instance running
 * on the OpenStack (OS) platform. We leverage Typica clients to connect to the
 * cloud.
 * 
 * @author Alessio Gambi
 * 
 */
public class OSController implements CloudController {

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

	public OSController(Logger logger, SymbolSource symbolSource,
			CloudInterface cloud, UserDataService userDataService,
			InstanceService instanceService) {
		this.logger = logger;
		this.symbolSource = symbolSource;
		this.cloud = cloud;
		this.userDataService = userDataService;
		this.instanceService = instanceService;

		// System.out.println("OSController.OSController() Logger "
		// + logger.getName());
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

	// TODO THis is a good candidate for a pipeline builder service: we build a
	// pipeline of userdata injectors (aka Decorators) that we can contribute
	// via the T5 registry.
	public String prepareUserData(DynamicServiceDescription service,
			VeeDescription vee, Integer replicaNum, FQN replicaFQN,
			InstanceType type, UUID deployID) {

		// Check if this is what CloudInit starts at the beginning
		StringBuffer userdataBuffer = new StringBuffer();

		/*
		 * TODO Here we adopt a naive approach to create the customization
		 * script. For the moment this is fine, in the future this may be
		 * enhanced
		 */

		// Declare that this is a script file

		userdataBuffer.append("#!/bin/bash\n");
		userdataBuffer.append("echo \" Running the customization scripts\"\n");
		// The first part consists in creating the file containing the env
		// variable.

		// Write the port check script to a file
		userdataBuffer.append(WAIT_PORT_CHECK);

		// Open the CAT
		userdataBuffer.append("echo \"Prepare the evn data file \"\n");
		userdataBuffer.append("cat > /tmp/userdata << !\n");

		// Build the user-data string

		String[] roles = null;

		for (Entry<String, String> prop : vee.getProperties().entrySet()) {

			if (DynamicProperty.isDynamicProperty(prop.getValue())) {

				// Create a new DynamicProperty object
				DynamicProperty dynProp = new DynamicProperty(prop.getValue());

				logger.debug("DynamicProperty " + dynProp.toString()
						+ " has been found for VEE " + vee.getName());

				String value = "";

				// TODO change this method,
				// It is not possible to hardcode the names of all the possible
				// dynamic properties

				if (dynProp.getPropertyName().equals("privateIp")) {
					try {
						value = service.getPrivateIp(dynProp.getVeeName(),
								dynProp.getReplicaNum());
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else if (dynProp.getPropertyName().equals("publicIp")) {
					try {
						value = service.getPublicIp(dynProp.getVeeName(),
								dynProp.getReplicaNum());
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {

				}

				logger.debug("The value of DynamicProperty "
						+ dynProp.toString() + " is " + value);

				// TODO Cannot use @privateIP along with other values inside of
				// a give prop
				// For example cannot do -JprivateIP=@(frontend, 0, privateIp)
				// -Jfoo=bar
				// FIXME Combined properties

				userdataBuffer.append("export "
						+ prop.getKey().replace("USERDATA:", "")
								.replaceAll("\\.", "_") + "=" + value + "\n");

				// try {
				// userdata.append(prop.getKey()+"="+dynProp.getValue()+"\n");
				// } catch (IllegalArgumentException e) {
				// errorMessage="An erro has occured while retrieving the value of DynamicProperty "+dynProp.toString();
				// logger.error(errorMessage);
				// throw new EucalyptusActuatorException(errorMessage, e);
				// }

				// Static props are the one to setup the deploy of the VM, like
				// ssh keychain etc...?
			} else if (StaticProperty.isStaticProperty(prop.getValue())) {

				// Create a new StaticProperty object
				StaticProperty stProp = new StaticProperty(prop.getValue());

				logger.debug("StaticProperty " + stProp.toString()
						+ " has been found for VEE " + vee.getName());

				try {
					if (prop.getKey().startsWith("USERDATA")) {
						// USERDATA props become env variables. Not we need to
						// sanitize them somehow !
						userdataBuffer.append("export "
								+ prop.getKey().replace("USERDATA:", "")
										.replaceAll("\\.", "_") + "="
								+ stProp.getValue() + "\n");

					} else if (prop.getKey().startsWith("STARTUP")) {
						roles = stProp.getValue().split(",");
						logger.debug("Found " + roles.length + " roles");
					} else {
						logger.warn("Unknown prefix. Skip property "
								+ prop.getKey() + "==" + stProp.getValue());
					}

				} catch (IllegalArgumentException e) {
					String errorMessage = "An error has occured while retrieving the value of StaticProperty "
							+ prop.getKey() + " of VEE " + vee.getName();
					logger.error(errorMessage);
					throw e;
				}

			} else {

				logger.debug("NormalProperty " + prop.toString()
						+ " has been found for VEE " + vee.getName());

				if (prop.getKey().startsWith("USERDATA")) {
					userdataBuffer.append("export "
							+ prop.getKey().replace("USERDATA:", "")
									.replaceAll("\\.", "_") + "="
							+ prop.getValue() + "\n");

				} else if (prop.getKey().startsWith("STARTUP")) {
					roles = prop.getValue().split(",");
					logger.debug("Found " + roles.length + " roles");
				} else {
					logger.warn("Unknown prefix. Skip property "
							+ prop.getKey() + "==" + prop.getValue());
				}
			}

		}

		logger.debug("The VEE information retrieved from service description are \n"
				+ vee);

		// Add the replica FQN to the user data
		userdataBuffer.append("export REPLICA_FQN" + "=" + replicaFQN + "\n");

		// CLOSE THE CAT
		userdataBuffer.append("!\n");

		// Prepare the role startup scripts. Convention over configuration
		userdataBuffer.append("echo \"Prepare the startup file \"\n");
		userdataBuffer.append("cat > /tmp/startup.sh << !\n");

		if (roles != null) {
			for (int i = 0; i < roles.length; i++) {
				userdataBuffer.append("echo \"Starting " + roles[i] + " \"\n");
				userdataBuffer.append("chmod +x /opt/" + roles[i] + "/"
						+ roles[i] + ".sh\n");
				userdataBuffer.append("/opt/" + roles[i] + "/" + roles[i]
						+ ".sh start\n");

				if ("doodleas".equalsIgnoreCase(roles[i])) {
					System.out
							.println("\n\tOSController.prepareUserData() FORCING PORT CHECK for doodle as (this is beta !!!) \n");
					userdataBuffer.append("\n");
					userdataBuffer.append("/tmp/waitportstobeopen.sh\n");
					userdataBuffer.append("\n");
				}
			}
		}

		userdataBuffer.append("!\n");

		userdataBuffer.append("chmod +x /tmp/startup.sh\n");
		// Run the script with the ubuntu user and redirect to a log file !

		// Add some logging
		userdataBuffer.append("echo \"Start components at\"`date`\n");
		userdataBuffer
				.append("sudo -u ubuntu /tmp/startup.sh 2>&1 | tee -a /var/log/startup-components.log\n\n\n");
		userdataBuffer.append("echo \"Component started at\"`date`\n");

		// Prepare the role startup scripts. Convention over configuration
		userdataBuffer.append("echo \"Prepare the registration file \"\n");
		userdataBuffer.append("cat > /tmp/registration.sh << !\n");

		if (roles != null) {
			for (int i = 0; i < roles.length; i++) {
				userdataBuffer.append("echo \"Starting Registration of "
						+ roles[i] + " \"\n");
				userdataBuffer.append("chmod +x /opt/" + roles[i] + "/"
						+ roles[i] + ".sh\n");
				userdataBuffer.append("/opt/" + roles[i] + "/" + roles[i]
						+ ".sh register\n");
			}
		}
		userdataBuffer.append("!\n");

		userdataBuffer.append("chmod +x /tmp/registration.sh\n");

		userdataBuffer
				.append("sudo -u ubuntu /tmp/registration.sh  2>&1 | tee -a /var/log/register-components.log\n\n\n");

		// Prepare the role deregistration script but don't run it
		userdataBuffer.append("echo \"Prepare the deregistration file \"\n");
		userdataBuffer.append("cat > /tmp/deregistration.sh << !\n");

		if (roles != null) {
			for (int i = 0; i < roles.length; i++) {
				userdataBuffer.append("echo \"Starting Deregistration of "
						+ roles[i] + " \"\n");
				userdataBuffer.append("chmod +x /opt/" + roles[i] + "/"
						+ roles[i] + ".sh\n");
				userdataBuffer.append("/opt/" + roles[i] + "/" + roles[i]
						+ ".sh deregister\n");
			}
		}

		userdataBuffer.append("!\n");
		userdataBuffer.append("chmod +x /tmp/deregistration.sh\n");

		userdataBuffer.append("echo \"End of user provided file!\"\n");

		// Accumulate the instance runtime values

		// This should be like an environmental service !
		// TODO Check how to actually implement such a thing
		instanceService.put("@replicaFQN", replicaFQN);
		instanceService.put("@replicaNum", replicaNum);
		instanceService.put(
				"@serviceId",
				new FQN(replicaFQN.getOrganizationName(), replicaFQN
						.getCustomerName(), replicaFQN.getServiceName()));
		instanceService.put(
				"@serviceFQN",
				new FQN(replicaFQN.getOrganizationName(), replicaFQN
						.getCustomerName(), replicaFQN.getServiceName()));
		instanceService.put("@maxInstances", vee.getMaxInstances());
		instanceService.put("@minInstances", vee.getMinInstances());
		instanceService.put("@@UUID", "@@" + deployID);
		instanceService.put("@UUID", deployID);

		// This is a pipeline each service in the pipeline read and write to the
		// instanceService

		String theuserdata = userDataService.getUserData(
				userdataBuffer.toString(), instanceService);

		logger.debug("The Userdata file is:\n " + theuserdata);

		return theuserdata;

		// userdataBuffer = new
		// StringBuffer(OSUtils.injectRuntimeValues(symbolSource,
		// userdataBuffer.toString(), vee, service.getStaticServiceDescription()
		// .getServiceFQN(), replicaFQN, replicaNum, service,
		// deployID));
		//
		// // This requires the cloud interface to retrieve the values
		// // NOTE THAT replicaFQN is simply ignored
		// userdataBuffer = new StringBuffer(OSUtils.injectRemoteValues(
		// userdataBuffer.toString(), cloud, replicaFQN));
		//
		// return userdataBuffer.toString();
	}

	// If we do not pass the replicaFQN then we must compute the replicaNumber
	@Override
	public void launchVEE(FQN veeFQN, DynamicServiceDescription service,
			UUID deployID) throws ServiceDeployerException {

		logger.debug("Thread " + Thread.currentThread().getName()
				+ " launch veeFQN " + veeFQN);

		// FIXME TODO NOTE THIS MAY BE BUGGY. TEST WITH MULTI DEPLOY !!
		int replicaNum = service.getFirstNullReplicaNum(veeFQN.getVeeName());

		FQN replicaFQN = new FQN(FQN.getRootNamespace(veeFQN.toString()),
				FQN.getCustomerName(veeFQN.toString()),
				FQN.getServiceName(veeFQN.toString()), "",
				FQN.getVeeName(veeFQN.toString()), replicaNum);

		// Actually configure and start the instance
		launchVEEwithReplicaFQN(replicaFQN, service, deployID);

	}

	// Assumption we pass the replica FQN
	@Override
	public void launchVEEwithReplicaFQN(FQN replicaFQN,
			DynamicServiceDescription service, UUID deployID)
			throws ServiceDeployerException {

		// Try to create a tag for the replicaFQN
		cloud.registerReplicaFQN(replicaFQN);

		logger.debug("Thread " + Thread.currentThread().getName()
				+ " launch replicaFQN " + replicaFQN);

		// Prepare all the configuration elements
		String errorMessage;

		ArrayList<String> secGroups = new ArrayList<String>();

		// Get the static (launch parameters and user-data) information of the
		// VEE we want to deploy

		VeeDescription vee = null;
		try {
			vee = service.getVeeDescription(new FQN(replicaFQN.toString(),
					FQNType.VEE));
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceDeployerException("", e);
		}

		// Instance types defined via distributed configuration ?
		logger.debug("Vee Description " + vee);
		InstanceType type = InstanceType.getTypeFromString(vee
				.getInstanceType());

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

		// Add Sec Groups (from manifest)
		secGroups.addAll(vee.getSecurityGroups());

		// Here tag the service if any
		if (service.getDeployID() != null
				&& !secGroups.contains(service.getDeployID().toString())) {
			secGroups.add(service.getDeployID().toString());
		}

		// Inject here the replicaFQN one
		secGroups.add(replicaFQN.toString().replace(".", "_"));

		// Inject ENTRY_POINT TAG here if any
		if (vee.getName().equalsIgnoreCase(
				service.getStaticServiceDescription().getEntryPoint())) {
			secGroups.add("ENTRY_POINT");
		}

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

			logger.info("sec groups : " + secGroups);
			/*
			 * Original code assumed a newly generate ssh key to be injected
			 * somehow. We instead use only the name provided with the manifest
			 */
			// result = launchInstance(vee.getBaseImageId(), secGroups,
			// service.getServiceKey().getKeyName(), userdata, type, 1, 1);

			// TODO NOTE 1,1 means sequencial deployment... maybe we can also
			// change
			// this leveraging the value of initial for instances !
			launchedInstanceId = cloud.launchInstance(vee.getBaseImageId(),
					secGroups, vee.getSshKeyName(), userdata, type, 1, 1);
			
			System.out.println("LAUNCHED INSTANCE ID = " + launchedInstanceId);
			// Update the Shared structure
			service.addVeeInstance(vee,
					cloud.getInstanceDescriptionByID(launchedInstanceId));

		} catch (Exception e) {
			e.printStackTrace();
			errorMessage = "The EC2/Eucalyptus client has throwed an exception while trying to execute the runInstances command";
			throw new ServiceDeployerException(errorMessage, e);
		}
	}

	// Scan the port, if is not open just go on, otherwise send the command and
	// wait
	private void sendShutdownCommand(VeeDescription veeDescription,
			InstanceDescription instanceDescription) {
		String errorMessage = null;
		try {
			logger.info("\n\nSend shutdown command to " + veeDescription + " "
					+ instanceDescription + "\n\n");

			ControlInterfaceClient controlIClient;

			Map<String, String> userdata = (veeDescription != null) ? veeDescription
					.getProperties() : new HashMap<String, String>();

			String controlInterfacePort = userdata.get("controlinterface_port");
			Integer controlInterfaceIntegerPort = null;
			String controlInterfacePassword = userdata
					.get("controlinterface_password");

			if (controlInterfacePort == null) {

				controlInterfacePort = userdata.get("controlinterface.port");

				if (controlInterfacePort == null) {
					controlInterfacePort = "55555";
				}

				try {
					controlInterfaceIntegerPort = Integer
							.parseInt(controlInterfacePort);
				} catch (NumberFormatException e) {
					errorMessage = "The controlinterface port parameter (with value "
							+ controlInterfacePort
							+ ") retrieved from "
							+ "manifest doesn't represent a valid integer value, "
							+ "aborting control interface shutdown procedure";
					logger.error(errorMessage, e);
				}
			}

			if (controlInterfacePassword == null) {

				controlInterfacePassword = userdata
						.get("controlinterface.password");

				if (controlInterfacePassword == null) {
					controlInterfacePassword = "Controller";
				}
			}

			InetAddress instancePublicIp = instanceDescription.getPublicIp();

			// We use a small timeout for the socket to avoid spending 30+ sec
			// waiting
			try {
				Socket socket = new Socket();
				socket.connect(new InetSocketAddress(instancePublicIp,
						controlInterfaceIntegerPort), 5000);
				socket.close();
			} catch (IOException ex) {
				logger.warn(
						"ControlInterface is not running, terminate VM without shutdown command",
						ex);
				return;
			}

			controlIClient = new ControlInterfaceClient(instancePublicIp,
					controlInterfaceIntegerPort, controlInterfacePassword);

			try {
				// Send the SHUTDOWN events to ALL components
				logger.debug("OSController.sendShutdownCommand() Sending the command ");
				controlIClient.sendCommandToComponent("shutdown", "ALL");
			} catch (IOException e) {
				errorMessage = "An error has occured while sending to the control interface at "
						+ ""
						+ instancePublicIp
						+ ":"
						+ controlInterfacePort
						+ " the command to stop and"
						+ "deregister all components";
				logger.error(errorMessage, e);

				if (e instanceof ConnectException) {
					logger.warn(
							"Be sure you can connect the VM. Double check if you are outside the cloud network",
							e);
				}

			} catch (Exception e) {
				errorMessage = "An error has occured while sending to the control interface at "
						+ ""
						+ instancePublicIp
						+ ":"
						+ controlInterfacePort
						+ " the command to stop and"
						+ "deregister all components";
				logger.error(errorMessage, e);
			}

		} catch (Throwable e) {
			logger.warn(
					"The control interface mechanism for shutdown failed, but we continute the shutdown",
					e);
			e.printStackTrace();
		}
	}

	@Override
	public void removeVEEbyInstanceID(String instanceToTerminateID,
			StaticServiceDescription service) {

		logger.debug("\n\n\nOSController.removeVEEbyInstanceID(String instanceToTerminateID, StaticServiceDescription service)");
		logger.debug("service : " + service);

		String errorMessage;
		// Retrieve the VEE type from describe Instance with ID
		InstanceDescription instanceDescription = cloud
				.getInstanceDescriptionByID(instanceToTerminateID);

		// logger.debug("OSController.removeVEEbyInstanceID() "
		// + instanceDescription);

		if (service != null) {
			String veeName = instanceDescription.getReplicaFQN().getVeeName();
			logger.debug("OSController.removeVEEbyInstanceID() " + veeName);
			// Retrieve STATIC userdata from service
			for (VeeDescription vee : service.getOrderedVees()) {
				if (vee.getName().equalsIgnoreCase(veeName)) {
					try {
						sendShutdownCommand(vee, instanceDescription);
					} catch (Exception e) {
						logger.warn(
								"Error while sending shutdown command to controlinterface",
								e);
					}
				} else {

				}
			}
		} else {
			logger.warn("Remove replica with null service !");
		}

		try {
			cloud.removeInstance(instanceToTerminateID);
		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
			errorMessage = "An exception has occured while launching the terminteInstances signal from the EC2/Euca client";
			logger.error(errorMessage, e);
			throw new IllegalArgumentException(errorMessage, e);
		}
	}

	/**
	 * This method remove a virtual machine by its instance ID.
	 * 
	 * Note that by using this method we cannot (yet) access the vm userdata
	 * therefore we can only use an heuristic for contacting the
	 * controlinterface.
	 * 
	 * Note we work under the assumption that this class is thread scoped !
	 * 
	 * @param instanceToTerminateID
	 */
	@Override
	@Deprecated
	public void removeVEEbyInstanceID(String instanceToTerminateID) {

		logger.info("Terminating VEE by InstanceID " + instanceToTerminateID);
		removeVEEbyInstanceID(instanceToTerminateID, null);

	}

	public void removeVEEsbyInstanceID(DynamicServiceDescription service,
			List<String> instancesToTerminateID) {

		logger.debug("Terminating Parallel VEEs by InstanceID "
				+ instancesToTerminateID);
		// Since the terminateInstances method take as input
		// a list of ids of the instances to teminate
		// we create a List containing only the instanceId
		// of the single replica we want to remove
		ArrayList<String> instancesToTerminate = new ArrayList<String>();
		instancesToTerminate.addAll(instancesToTerminateID);

		for (String instanceToTerminateID : instancesToTerminate) {
			// Here is where the termination command is really launched
			try {
				removeVEEbyInstanceID(instanceToTerminateID,
						service.getStaticServiceDescription());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
