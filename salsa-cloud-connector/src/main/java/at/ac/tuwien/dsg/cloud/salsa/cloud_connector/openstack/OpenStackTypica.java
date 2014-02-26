package at.ac.tuwien.dsg.cloud.salsa.cloud_connector.openstack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;

import at.ac.tuwien.dsg.cloud.salsa.cloud_connector.CloudInterface;
import at.ac.tuwien.dsg.cloud.salsa.cloud_connector.InstanceDescription;
import at.ac.tuwien.dsg.cloud.salsa.cloud_connector.ServiceDeployerException;
import at.ac.tuwien.dsg.cloud.salsa.cloud_connector.VMStates;

import com.xerox.amazonws.ec2.EC2Exception;
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
	String sshName;

	public OpenStackTypica(
			// Resources
			Logger logger,
			// Services
			JEC2ClientFactory clientFactory,
			// Configuration Symbols
			Integer maxRetries, Long retryDelayMillis,
			Integer deployMaxRetries, Long deployWaitMillis, String sshName) {
		this.logger = logger;
		this.clientFactory = clientFactory;
		this.maxRetries = maxRetries;
		this.retryDelayMillis = retryDelayMillis;
		this.deployMaxRetries = deployMaxRetries;
		this.deployWaitMillis = deployWaitMillis;
		this.sshName = sshName;		

		// System.out.println("OpenStackTypica.OpenStackTypica() Logger "
		// + logger.getName());

		

	}
	

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


			InstanceDescription id = new InstanceDescription(
					instanceID, instance.getPrivateIpAddress(),
					instance.getIpAddress());
			id.setState(VMStates.fromString(instance.getState()));

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

		launchReq.setKeyName(this.sshName);
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
