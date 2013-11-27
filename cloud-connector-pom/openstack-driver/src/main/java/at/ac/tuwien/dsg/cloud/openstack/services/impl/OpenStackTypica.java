package at.ac.tuwien.dsg.cloud.openstack.services.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;

import at.ac.tuwien.dsg.cloud.data.InstanceDescription;
import at.ac.tuwien.dsg.cloud.exceptions.ServiceDeployerException;
import at.ac.tuwien.dsg.cloud.services.CloudInterface;
import ch.usi.cloud.controller.common.naming.FQN;

import com.xerox.amazonws.ec2.EC2Exception;
import com.xerox.amazonws.ec2.GroupDescription;
import com.xerox.amazonws.ec2.InstanceType;
import com.xerox.amazonws.ec2.Jec2;
import com.xerox.amazonws.ec2.LaunchConfiguration;
import com.xerox.amazonws.ec2.ReservationDescription;
import com.xerox.amazonws.ec2.ReservationDescription.Instance;

public class OpenStackTypica implements CloudInterface {

	private Logger logger;

	private JEC2ClientFactory clientFactory;

	private int maxRetries;
	private long retryDelayMillis;
	private int deployMaxRetries;
	private long deployWaitMillis;

	public OpenStackTypica(
			// Resources
			Logger logger,
			// Services
			JEC2ClientFactory clientFactory,
			// Configuration Symbols
			Integer maxRetries, Long retryDelayMillis,
			Integer deployMaxRetries, Long deployWaitMillis) {
		this.logger = logger;
		this.clientFactory = clientFactory;
		this.maxRetries = maxRetries;
		this.retryDelayMillis = retryDelayMillis;
		this.deployMaxRetries = deployMaxRetries;
		this.deployWaitMillis = deployWaitMillis;

		// System.out.println("OpenStackTypica.OpenStackTypica() Logger "
		// + logger.getName());

		initialize();

	}

	private void initialize() {
		// Check if ENTRY_POINT IS THERE
		String tag = "ENTRY_POINT";
		logger.info("Initialize");
		int c = 0;
		do {
			try {
				//
				// This calls always generate an exception with empty cause !
				//
				clientFactory.getNewClient().createSecurityGroup(tag,
						"Tag supporting entry point identification");
			} catch (EC2Exception e) {
				logger.info("" + e.getCause());
			}

			try {
				// We use this to understand if the group was actually created
				// This will fail if the group is not there yet !
				// If this fails then there is a real problem !
				clientFactory.getNewClient().describeSecurityGroups(
						new String[] { tag });

				return;
			} catch (EC2Exception e) {
				logger.warn("Error while initialing ", e);
				e.printStackTrace();
			} catch (Exception e) {
				logger.warn("Error while initialing ", e);
				e.printStackTrace();
			}

			c++;

			// Wait for it
			try {
				Thread.sleep(retryDelayMillis);
			} catch (InterruptedException e) {
				logger.warn("Forced wakeup. Abort!");
				break;
			} catch (Exception e) {
				logger.warn("", e);
			}

		} while (c <= maxRetries);

		String errorMessage = "(INIT) After " + c + "(" + maxRetries + ")"
				+ ". The security group for " + tag + " was not created!!";
		logger.error(errorMessage);
		throw new RuntimeException(errorMessage);

	}

	// @Override
	// public void registerClient(FQN clientFQN) {
	// logger.warn("Not implemented and maybe not necessary");
	// }

	// @Override
	// public boolean isRegisteredClient(FQN serviceFQN)
	// throws ServiceDeployerException {
	// logger.warn("Not implemented and maybe not necessary");
	// return false;
	// }

	// @Override
	// public void deregisterClient(FQN clientFQN) {
	// logger.warn("Not implemented and maybe not necessary");
	// }

	@Override
	public void registerService(FQN serviceFQN) throws ServiceDeployerException {
		logger.info("Registering service  " + serviceFQN);
		String _serviceFQN = serviceFQN.toString().replace(".", "_");

		int c = 0;
		do {
			try {
				//
				// This calls always generate an exception with empty cause !
				//
				clientFactory.getNewClient().createSecurityGroup(
						serviceFQN.toString().replace(".", "_"),
						"A security group");
			} catch (EC2Exception e) {
				logger.info("" + e.getCause());
			}

			try {
				// We use this to understand if the group was actually created
				// This will fail if the group is not there yet !
				// If this fails then there is a real problem !
				clientFactory.getNewClient().describeSecurityGroups(
						new String[] { _serviceFQN });

				return;
			} catch (EC2Exception e) {
				logger.warn("Error while registering service " + serviceFQN, e);
				e.printStackTrace();
			} catch (Exception e) {
				logger.warn("Error while registering service " + serviceFQN, e);
				e.printStackTrace();
			}

			c++;

			// Wait for it
			try {
				Thread.sleep(retryDelayMillis);
			} catch (InterruptedException e) {
				logger.warn("Forced wakeup. Abort!");
				break;
			} catch (Exception e) {
				logger.warn("", e);
			}

		} while (c <= maxRetries);

		String errorMessage = "(DEPLOY) After " + c + "(" + maxRetries + ")"
				+ ". The security group for " + serviceFQN
				+ " was not created!!";
		logger.error(errorMessage);
		throw new ServiceDeployerException(errorMessage);

	}

	/**
	 * In the current implementation deregistering a service accounts for the
	 * removal of a service group named after it.
	 * 
	 * Not really sure this is thread safe...
	 */
	@Override
	public void deregisterService(FQN serviceFQN)
			throws ServiceDeployerException {
		logger.info("Deregistering service  " + serviceFQN);
		String _serviceFQN = serviceFQN.toString().replace(".", "_");

		int c = 0;
		do {
			try {
				clientFactory.getNewClient().deleteSecurityGroup(_serviceFQN);
			} catch (EC2Exception e) {
				// Ignore this and check later
			}

			// Check if actually removed
			try {
				clientFactory.getNewClient()
						.describeSecurityGroups(
								new String[] { serviceFQN.toString().replace(
										".", "_") });
			} catch (EC2Exception e) {
				if (e.getMessage().contains("Security group")
						&& e.getMessage().contains("not found")) {
					// This is ok !
					return;
				} else {
					logger.warn("Error while deregistering service "
							+ serviceFQN, e);
					e.printStackTrace();
				}
			} catch (Exception e) {
				logger.warn("Error while deregistering service " + serviceFQN,
						e);
				// String errorMessage =
				// "(UNDEPLOY) An error has occured while undeplioying service";
				// logger.error(errorMessage, e);
				// throw new ServiceDeployerException(errorMessage, e);
			}

			c++;

			// Wait for it
			try {
				Thread.sleep(retryDelayMillis);
			} catch (InterruptedException e) {
				logger.warn("Forced wakeup. Abort!");
				break;
			} catch (Exception e) {
				logger.warn("", e);
			}

		} while (c <= maxRetries);

		String errorMessage = "(UNDEPLOY) After " + c + "(" + maxRetries + ")"
				+ ". The security group for " + serviceFQN
				+ " was not removed!!";
		logger.error(errorMessage);
		throw new ServiceDeployerException(errorMessage);

	}

	@Override
	public boolean isRegisteredService(FQN serviceFQN)
			throws ServiceDeployerException {
		logger.info("isRegisteredService service  " + serviceFQN);
		String _serviceFQN = serviceFQN.toString().replace(".", "_");

		int c = 0;
		do {
			try {
				clientFactory.getNewClient().describeSecurityGroups(
						new String[] { _serviceFQN });
				return true;
			} catch (EC2Exception e) {
				if (e.getMessage().contains("Security group")
						&& e.getMessage().contains("not found")) {
					// This is ok !
					return false;
				} else {
					logger.warn("Error while isRegisteredService service "
							+ serviceFQN, e);
					e.printStackTrace();
				}
			} catch (Exception e) {
				logger.warn("Error while isRegisteredService service "
						+ serviceFQN, e);
			}

			c++;

			// Wait for it
			try {
				Thread.sleep(retryDelayMillis);
			} catch (InterruptedException e) {
				logger.warn("Forced wakeup. Abort!");
				break;
			} catch (Exception e) {
				logger.warn("", e);
			}

		} while (c <= maxRetries);

		String errorMessage = "After " + c + "(" + maxRetries + ")"
				+ "isRegisteredService was not completed ";
		logger.error(errorMessage);
		throw new ServiceDeployerException(errorMessage);

	}

	@Override
	public String getInstance(UUID deployID) throws ServiceDeployerException {
		Set<String> serviceInstances = new HashSet<String>();

		String _deployID = deployID.toString();
		// Describe all instances, also the ones in ERROR state
		List<ReservationDescription> instances = new ArrayList<ReservationDescription>();
		try {
			instances
					.addAll(describeInstancesAvoidSignatureError(new ArrayList<String>()));

			// Getting all the Instance-ID that match _deployID
			for (ReservationDescription res : instances) {
				if (res.getGroups().contains(_deployID)) {
					for (Instance inst : res.getInstances()) {
						serviceInstances.add(inst.getInstanceId());
					}
				}
			}

		} catch (Throwable e) {
			throw new ServiceDeployerException(
					"Exception while getting Instance ids for " + _deployID, e);
		}

		if (serviceInstances.size() > 1) {
			String errorMsg = "Single result expected for " + deployID + " !!!";
			logger.error(errorMsg);
			throw new ServiceDeployerException(errorMsg);
		}

		if (serviceInstances.size() > 0) {
			return serviceInstances.iterator().next();
		}

		return null;
	}

	// Find the deployID corresponding to _serviceFQN
	private List<String> getServiceInstances(String _serviceFQN)
			throws ServiceDeployerException {
		List<String> serviceInstances = new ArrayList<String>();

		// Get all security groups
		try {
			List<GroupDescription> groups = clientFactory.getNewClient()
					.describeSecurityGroups(new String[] {});
			for (GroupDescription group : groups) {
				if (_serviceFQN.equalsIgnoreCase(group.getDescription())) {
					String _deployID = group.getName();

					serviceInstances
							.addAll(getServiceInstanceInstances(_deployID));
				}
			}
		} catch (EC2Exception e) {
			e.printStackTrace();
			throw new ServiceDeployerException(
					"Error while getting instances for " + _serviceFQN, e);
		}

		return serviceInstances;
	}

	// Find the deployID corresponding to _serviceFQN
	private Set<String> getServiceInstanceInstances(String _deployID)
			throws ServiceDeployerException {
		Set<String> serviceInstances = new HashSet<String>();

		// Describe all instances, also the ones in ERROR state
		List<ReservationDescription> instances = new ArrayList<ReservationDescription>();
		try {
			instances
					.addAll(describeInstancesAvoidSignatureError(new ArrayList<String>()));

			// Getting all the Instance-ID that match _deployID
			for (ReservationDescription res : instances) {
				if (res.getGroups().contains(_deployID)) {
					for (Instance inst : res.getInstances()) {
						serviceInstances.add(inst.getInstanceId());
					}
				}
			}

		} catch (Throwable e) {
			throw new ServiceDeployerException(
					"Exception while getting Instance ids for " + _deployID, e);
		}

		return serviceInstances;
	}

	public Set<UUID> getDeployIDs(FQN serviceFQN)
			throws ServiceDeployerException {
		logger.info("getDeployIDs(" + serviceFQN + ")");

		Set<UUID> result = new HashSet<UUID>();

		if (serviceFQN == null) {
			logger.warn("Null ServiceFQN");
			return result;
		}

		String _serviceFQN = serviceFQN.toString().replace(".", "_");

		// Get all security groups
		try {
			List<GroupDescription> groups = clientFactory.getNewClient()
					.describeSecurityGroups(new String[] {});

			for (GroupDescription group : groups) {
				try {
					if (_serviceFQN.equalsIgnoreCase(group.getDescription())) {
						result.add(UUID.fromString(group.getName()));
					}
				} catch (IllegalArgumentException e) {
					// logger.debug("", e);
				}
			}
		} catch (EC2Exception e) {
			e.printStackTrace();
			throw new ServiceDeployerException(
					"Error while getting instances for " + _serviceFQN, e);
		}

		return result;
	}

	public Set<String> getServiceInstances(FQN serviceFQN, UUID deployID)
			throws ServiceDeployerException {

		logger.info("getServiceInstances(" + serviceFQN + " , " + deployID
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

	@Override
	public void registerDeploy(FQN serviceFQN, UUID deployID)
			throws ServiceDeployerException {

		logger.info("Registering deploy " + deployID);
		String _deployID = deployID.toString();
		String _serviceFQN = serviceFQN.toString().replace(".", "_");

		int c = 0;
		do {
			try {
				//
				// This calls always generate an exception with empty cause !
				//
				// Apparently long descriptions result in a runtime error...
				clientFactory.getNewClient().createSecurityGroup(_deployID,
						_serviceFQN);

			} catch (EC2Exception e) {
				if (!e.getMessage().contains("No reason given.")) {
					e.printStackTrace();
				}
			}

			try {
				// We use this to understand if the group was actually created
				// This will fail if the group is not there yet !
				// If this fails then there is a real problem !
				clientFactory.getNewClient().describeSecurityGroups(
						new String[] { _deployID });
				return;
			} catch (EC2Exception e) {
				logger.warn("Error while registering deployID " + _deployID, e);
				e.printStackTrace();
			} catch (Exception e) {
				logger.warn("Error while registering deployID  " + _deployID, e);
				e.printStackTrace();
			}

			c++;

			// Wait for it
			try {
				Thread.sleep(retryDelayMillis);
			} catch (InterruptedException e) {
				logger.warn("Forced wakeup. Abort!");
				break;
			} catch (Exception e) {
				logger.warn("", e);
			}

		} while (c <= maxRetries);

		String errorMessage = "(UNDEPLOY) After " + c + "(" + maxRetries + ")"
				+ ". The security group for " + _deployID
				+ " was not removed!!";
		logger.error(errorMessage);
		throw new ServiceDeployerException(errorMessage);
	}

	@Override
	public void deregisterDeploy(FQN serviceFQN, UUID deployID)
			throws ServiceDeployerException {
		logger.info("Deregistering deploy " + deployID + " service "
				+ serviceFQN);
		String _deployID = deployID.toString();
		String _serviceFQN = serviceFQN.toString().replace(".", "_");

		List<String> toRemove = new ArrayList<String>();
		if ("*".equals(_deployID)) {
			// Get all security groups
			try {
				List<GroupDescription> groups = clientFactory.getNewClient()
						.describeSecurityGroups(new String[] {});
				for (GroupDescription group : groups) {
					if (_serviceFQN.equalsIgnoreCase(group.getDescription())) {
						logger.info("Mark " + group.getName() + " to remove");
						toRemove.add(group.getName());
					}
				}
			} catch (EC2Exception e) {
				e.printStackTrace();
			}
		} else {
			// Shall we check if the group is there ?
			toRemove.add(_deployID);
		}

		// NOTE THAT THIS WILL ABORT AS SOON AS ONE DEREG FAILS !
		for (String id : toRemove) {
			deregisterDeploy(id);
		}
	}

	private void deregisterDeploy(String _deployID)
			throws ServiceDeployerException {

		logger.info("Deregistering deploy " + _deployID);

		int c = 0;
		do {
			try {
				clientFactory.getNewClient().deleteSecurityGroup(_deployID);
			} catch (EC2Exception e) {
				// Ignore this
				// logger.debug("", e);
			}

			// Check if actually removed
			try {
				clientFactory.getNewClient().describeSecurityGroups(
						new String[] { _deployID });

				logger.warn("Error while deregistering deployID. " + _deployID);

			} catch (EC2Exception e) {
				// Those are specific messages from each cloud
				if (e.getMessage().contains("Security group")
						&& e.getMessage().contains("not found")) {
					// This is ok !
					return;
				} else {
					logger.warn("Error while deregistering deployID"
							+ _deployID, e);
					e.printStackTrace();
				}
			} catch (Exception e) {
				logger.warn("Error while deregistering deployID " + _deployID,
						e);
			}

			c++;

			// Wait for it
			try {
				Thread.sleep(retryDelayMillis);
			} catch (InterruptedException e) {
				logger.warn("Forced wakeup. Abort!");
				break;
			} catch (Exception e) {
				logger.warn("", e);
			}

		} while (c < maxRetries);

		String errorMessage = "(UNDEPLOY) After " + c + " (" + maxRetries + ")"
				+ ". The security group for " + _deployID
				+ " was not removed!!";
		logger.error(errorMessage);
		throw new ServiceDeployerException(errorMessage);

	}

	private String getServiceFQN(List<String> instanceGroupNames) {
		// Get the Sec Group that match UUID
		for (String securityGroup : instanceGroupNames) {
			try {
				// Check if valid UUID
				UUID.fromString(securityGroup);

				// Retrieve the corresponding ReplicaFQN
				List<GroupDescription> groups = clientFactory.getNewClient()
						.describeSecurityGroups(new String[] { securityGroup });

				return groups.get(0).getDescription();
			} catch (IllegalArgumentException e) {
				if (!e.getMessage().contains("Invalid UUID string")) {
					// Invalid UUID string: default e
					logger.warn(" Problem in getServiceFQN. ", e);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	private FQN getReplicaFQN(List<String> instanceGroupNames) {
		String _serviceFQN = getServiceFQN(instanceGroupNames);

		if (_serviceFQN == null) {
			return null;
		}

		for (String securityGroup : instanceGroupNames) {
			try {
				// Check for NON-valid UUID
				UUID.fromString(securityGroup);
			} catch (IllegalArgumentException e1) {
				// Invalid UUID string: default e
				// logger.debug("", e1);

				try {
					// Retrieve description
					List<GroupDescription> groups = clientFactory
							.getNewClient().describeSecurityGroups(
									new String[] { securityGroup });

					if (groups.get(0).getDescription().startsWith(_serviceFQN)) {

						return new FQN(FQN.getRootNamespace(securityGroup),
								FQN.getCustomerName(securityGroup),
								FQN.getServiceName(securityGroup), "",
								FQN.getVeeName(securityGroup), new Integer(
										FQN.getVeeReplicaNumber(securityGroup)));
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	@Override
	public InstanceDescription getInstanceDescriptionByID(String instanceID) {

		logger.info("getInstanceDescriptionByID " + instanceID);
		try {

			List<ReservationDescription> rds = clientFactory.getNewClient()
					.describeInstances(new String[] { instanceID });

			logger.info("Instances : " + rds);
			// Must be one and only one !?!
			ReservationDescription rd = rds.get(0);

			Instance instance = rd.getInstances().get(0);
			List<String> secGroupNames = rd.getGroups();

			FQN replicaFQN = getReplicaFQN(secGroupNames);

			InstanceDescription id = new InstanceDescription(replicaFQN,
					instanceID, instance.getPrivateIpAddress(),
					instance.getIpAddress(), instance.getPrivateDnsName(),
					instance.getDnsName());
			id.setState(instance.getState());

			logger.info("\n\nFound instance " + instanceID + " "
					+ id.getPrivateIp() + " " + id.getPublicIp());

			return id;
		} catch (EC2Exception e) {
			logger.warn("Cannot find the instance " + instanceID, e);
			e.printStackTrace();
		} catch (Throwable e) {
			logger.warn("Error while find the instance " + instanceID, e);
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public void deregisterReplicaFQN(FQN replicaFQN) {
		/*
		 * This is tricky because we need to be sure that no other instances are
		 * using the same TAG which is not provided by the platform !
		 */
		logger.warn("Not YET implemented!");
	}

	/**
	 * Note: Apparently inside the description field of the sec group, using
	 * this lib, cannot contains . symbols
	 */
	@Override
	public void registerReplicaFQN(FQN replicaFQN)
			throws ServiceDeployerException {
		logger.info("Registering replicaFQN  " + replicaFQN);

		String _replicaFQN = replicaFQN.toString().replace(".", "_");
		String _serviceFQN = FQN.getServiceFQN(replicaFQN.toString())
				.toString().replace(".", "_");
		;

		// System.out.println("OpenStackTypica.registerReplicaFQN() _replicaFQN "
		// + _replicaFQN);
		// System.out.println("OpenStackTypica.registerReplicaFQN() _serviceFQN "
		// + _serviceFQN);

		int c = 0;
		do {
			try {
				//
				// This calls always generate an exception with empty cause !
				//
				clientFactory.getNewClient().createSecurityGroup(_replicaFQN,
						_serviceFQN);
			} catch (EC2Exception e) {
				if (e.getCause() != null) {
					logger.warn("Registering ReplicaFQN ", e);
				} else {
					if (!e.getMessage().contains("already exists")) {
						logger.error("registerReplicaFQN", e);
					} else {
						logger.info("Security group " + _replicaFQN
								+ " already registered ");
					}
				}
			} catch (Throwable e) {
				logger.error("registerReplicaFQN", e);
			}

			try {
				// We use this to understand if the group was actually created
				// This will fail if the group is not there yet !
				// If this fails then there is a real problem !
				List<GroupDescription> groups = clientFactory.getNewClient()
						.describeSecurityGroups(new String[] { _replicaFQN });

				logger.debug("OpenStackTypica.registerReplicaFQN() groups : "
						+ groups);

				return;
			} catch (EC2Exception e) {
				logger.warn("Error while registering ReplicaFQN " + replicaFQN,
						e);
				// e.printStackTrace();
			} catch (Throwable e) {
				logger.warn("Error while registering service " + replicaFQN, e);
				// e.printStackTrace();
				// TODO Should we retry here ?
			}

			c++;

			// Wait for it
			try {
				Thread.sleep(retryDelayMillis);
			} catch (InterruptedException e) {
				logger.warn("Forced wakeup. Abort!");
				break;
			} catch (Exception e) {
				logger.warn("", e);
			}

			logger.info("Repeat Registering FQN " + replicaFQN);
		} while (c <= maxRetries);

		String errorMessage = "After " + c + " retries the security group for "
				+ replicaFQN + " was not created!!";
		logger.error(errorMessage);
		throw new ServiceDeployerException(errorMessage);
	}

	public List<String> getSecurityGroups(String instanceID)
			throws ServiceDeployerException {
		List<String> groups = new ArrayList<String>();

		try {
			List<ReservationDescription> instances = describeInstancesAvoidSignatureError(Arrays
					.asList(new String[] { instanceID }));

			// We expect 1 or 0 results !
			if (instances.size() > 0) {
				groups.addAll(instances.get(0).getGroups());
			}

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return groups;
	}

	/**
	 * Utility method that wraps the Jec2 descibeInstancesMethod and avoid the
	 * error "/Same signature was used within the last 5 minute" in the case
	 * this error is throwed it recursively try to create a new client with a
	 * different signature
	 * 
	 * @return
	 * @throws InterruptedException
	 */
	private List<ReservationDescription> describeInstancesAvoidSignatureError(
			List<String> instancesToDescribe) throws InterruptedException {

		try {
			logger.debug(" describeInstancesAvoidSignatureError "
					+ instancesToDescribe);
			List<ReservationDescription> result = clientFactory.getNewClient()
					.describeInstances(instancesToDescribe);
			logger.debug(" describeInstancesAvoidSignatureError Client queried the cloud"
					+ instancesToDescribe);
			return result;
		} catch (EC2Exception e) {
			e.printStackTrace();

			logger.warn("describeInstancesAvoidSignatureError() reinstantiating eucaClient, "
					+ "singature exception was catched, sleeping 1 seconds");

			Thread.sleep(1000);

			// TODO here we must retrieve the errorMessage
			// and check if it is
			// "Same signature was used within the last 5 minutes"
			// only in this specific case we must recursively call the method
			// in other cases we must throw an exception

			return describeInstancesAvoidSignatureError(instancesToDescribe);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			logger.debug("describeInstancesAvoidSignatureError end "
					+ instancesToDescribe);
		}
	}

	private void waitInstanceStop(String instancesToTerminate)
			throws ServiceDeployerException {

		// Prepare all the configuration elements
		String errorMessage;

		List<ReservationDescription> instancesState = null;

		// The describInstances method of the client takes as input an array of
		// ids
		// in this case we want to retrieve the state of only one instance
		// so we create a list containing the id of the just launched instance
		Integer waitForTerminationTries = 0;

		while (true) {

			try {
				List<String> instances = new ArrayList<String>();
				instances.add(instancesToTerminate);

				instancesState = describeInstancesAvoidSignatureError(instances);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}

			String instanceStateString = null;

			try {
				instanceStateString = instancesState.get(0).getInstances()
						.get(0).getState();
			} catch (IndexOutOfBoundsException e) {
				// Here there is no entry for the instance
				// e.printStackTrace();
				instanceStateString = "terminated";
			}

			logger.debug("Instance " + instancesToTerminate + " is in state "
					+ instanceStateString);

			if (!instanceStateString.equals("terminated")) {
				logger.debug("Instance " + instancesToTerminate
						+ " has not reached the TERMINATED state, waiting "
						+ deployWaitMillis / 1000 + " seconds...");

				try {
					Thread.sleep(deployWaitMillis);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} else {
				logger.debug("Instance "
						+ instancesToTerminate
						+ " has reached the TERMINATED state, exiting from the loop...");
				// The instance has reached the TERMINATED state we can exit
				// from the loop
				break;
			}

			waitForTerminationTries++;

			if (waitForTerminationTries > deployMaxRetries) {
				errorMessage = "The instance "
						+ instancesToTerminate
						+ " has not reached the TERMINATED state after "
						+ (deployMaxRetries * deployWaitMillis)
						+ " milliseconds, "
						+ "the waiting process has been stopped and the removal process has been aborted";
				logger.error(errorMessage);
				throw new ServiceDeployerException(errorMessage);
			}
		}
	}

	private void waitInstanceStart(String launchedInstanceId)
			throws ServiceDeployerException {

		ArrayList<String> instancesToDescribe = new ArrayList<String>();

		List<ReservationDescription> instancesState = null;

		int waitForRunningTries = 0;

		// The describInstances method of the client takes as input an array of
		// ids
		// in this case we want to retrieve the state of only one instance
		// so we create a list containing the id of the just launched instance
		instancesToDescribe.add(launchedInstanceId);

		// Wait until the just launched instance reach the running state
		while (true) {

			try {
				instancesState = describeInstancesAvoidSignatureError(instancesToDescribe);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			String instanceStateString = instancesState.get(0).getInstances()
					.get(0).getState();

			String instancePrivateDNS = instancesState.get(0).getInstances()
					.get(0).getPrivateIpAddress();

			logger.info("Instance " + launchedInstanceId + " is in state "
					+ instanceStateString);

			if ("error".equalsIgnoreCase(instanceStateString)) {
				logger.warn("The instance is in ERROR state");
				// Exit the loop, leave the add action in the queue, but
				// schedule a remove action for the faulty machine

				try {
					logger.debug("Try to remove the ERROR VM");
					removeInstance(launchedInstanceId);
				} catch (Exception e) {
					e.printStackTrace();
					logger.error(
							"Failed to remove the VM in error state. Continue with the other machines.",
							e);
					break;
				}

			} else if ("running".equalsIgnoreCase(instanceStateString)) {
				logger.debug("Instance " + launchedInstanceId
						+ " has reached the running state");

				if (!instancePrivateDNS.equals("0.0.0.0")) {
					logger.debug("The just launched instance with id "
							+ launchedInstanceId
							+ " has received a privateIp address "
							+ "with value " + instancePrivateDNS
							+ ", exiting from the waiting loop");

					break;
				} else {
					logger.debug("The instance "
							+ launchedInstanceId
							+ " has reached the running state but has "
							+ "not already received a valid ip address, we must wait for the instance to receive a valid network configuration,"
							+ " re-entering in the waiting loop");
				}
			} else {
				logger.debug("Instance " + launchedInstanceId
						+ " has not reached the running state, waiting "
						+ deployWaitMillis / 1000 + " seconds...");

			}

			// DO another round of wait
			waitForRunningTries++;

			try {
				Thread.sleep(deployWaitMillis);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			if (waitForRunningTries > deployMaxRetries) {
				logger.warn("The instance "
						+ launchedInstanceId
						+ " has not reached the running state after "
						+ (deployMaxRetries * deployWaitMillis)
						+ " milliseconds, "
						+ "the waiting process has been stopped and the delpoyment process has been aborted");

				throw new ServiceDeployerException(
						"The instance "
								+ launchedInstanceId
								+ " has not reached the running state after "
								+ (deployMaxRetries * deployWaitMillis)
								+ " milliseconds, "
								+ "the waiting process has been stopped and the delpoyment process has been aborted");

			}
		}
	}

	@Override
	public String launchInstance(String imageId, List<String> securityGroups,
			String sshKeyName, String userData, InstanceType instType,
			int minInst, int maxInst) throws ServiceDeployerException {

		logger.debug("Secutiry Groups: \n" + securityGroups);

		LaunchConfiguration launchReq = new LaunchConfiguration(imageId);
		launchReq.setImageId(imageId);

		// Here we pass the parameter to configure the machine at boot time
		// Cloud init cannot run this... dunno why
		launchReq.setUserData(userData.getBytes());
		launchReq.setInstanceType(instType);

		// The launch instance method of launch only one instance
		launchReq.setMinCount(minInst);
		launchReq.setMaxCount(maxInst);

		launchReq.setKeyName(sshKeyName);
		launchReq.setSecurityGroup(securityGroups);

		ReservationDescription result;
		try {
			result = clientFactory.getNewClient().runInstances(launchReq);
		} catch (EC2Exception e) {
			e.printStackTrace();
			throw new ServiceDeployerException(e);
		} catch (Throwable e) {
			e.printStackTrace();
			throw new ServiceDeployerException(e);
		}

		String launchedInstanceId = result.getInstances().get(0)
				.getInstanceId();

		logger.debug("Waiting until instance " + launchedInstanceId
				+ " reaches the running (or error) state");

		waitInstanceStart(launchedInstanceId);

		return launchedInstanceId;

	}

	@Override
	public void removeInstance(String instanceToTerminateID)
			throws ServiceDeployerException {

		String errorMessage = "";

		// Shall we move all this code inside the Cloud Interface ?!
		Jec2 client = clientFactory.getNewClient();

		// Since the terminateInstances method take as input
		// a list of ids of the instances to teminate
		// we create a List containing only the instanceId
		// of the single replica we want to remove
		ArrayList<String> instancesToTerminate = new ArrayList<String>();
		instancesToTerminate.add(instanceToTerminateID);
		// Here is where the termination command is really launched
		try {
			client.terminateInstances(instancesToTerminate);
		} catch (EC2Exception e) {
			errorMessage = "An exception has occured while launching the terminteInstances signal from the EC2/Euca client";
			logger.debug(errorMessage);
			throw new ServiceDeployerException(errorMessage, e);
		} catch (NullPointerException e) {
			// the call to terminate the instance goes through but it looks like
			// Eucalyptus
			// returns a null value and typica chokes on it, ignore it
			// for more information see
			// http://code.google.com/p/typica/issues/detail?id=105
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		waitInstanceStop(instanceToTerminateID);
	}
}
