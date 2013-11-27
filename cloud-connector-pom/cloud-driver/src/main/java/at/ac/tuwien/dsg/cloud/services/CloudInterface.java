package at.ac.tuwien.dsg.cloud.services;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import at.ac.tuwien.dsg.cloud.data.InstanceDescription;
import at.ac.tuwien.dsg.cloud.exceptions.ServiceDeployerException;
import ch.usi.cloud.controller.common.naming.FQN;

import com.xerox.amazonws.ec2.InstanceType;

public interface CloudInterface {

	// @Deprecated
	// public boolean isRegisteredClient(FQN serviceFQN)
	// throws ServiceDeployerException;

	// @Deprecated
	// public void registerClient(FQN serviceFQN) throws
	// ServiceDeployerException;

	// @Deprecated
	// public void deregisterClient(FQN serviceFQN)
	// throws ServiceDeployerException;

	// STILL IN USE
	@Deprecated
	public boolean isRegisteredService(FQN serviceFQN)
			throws ServiceDeployerException;

	// STILL IN USE
	@Deprecated
	public void registerService(FQN serviceFQN) throws ServiceDeployerException;

	// STILL IN USE
	@Deprecated
	public void deregisterService(FQN serviceFQN)
			throws ServiceDeployerException;

	/**
	 * Register a deployment given its ID with a serviceFQN.
	 * 
	 * @param deployID
	 * @param serviceFQN
	 * @throws ServiceDeployerException
	 */
	public void registerDeploy(FQN serviceFQN, UUID deployID)
			throws ServiceDeployerException;

	/**
	 * Deregister a deployment given its ID
	 * 
	 * @param deployID
	 * @throws ServiceDeployerException
	 */
	public void deregisterDeploy(FQN serviceFQN, UUID deployID)
			throws ServiceDeployerException;

	/**
	 * Retrieve the list of VM belonging to the set serviceFQN and DeployID.
	 * 
	 * @param serviceFQN
	 * @param deployID
	 * @return
	 * @throws ServiceDeployerException
	 */
	public Set<String> getServiceInstances(FQN serviceFQN, UUID deployID)
			throws ServiceDeployerException;

	/**
	 * Retrieve the list of deployments associated to a given services
	 * 
	 * @param serviceFQN
	 * @return
	 */
	public Set<UUID> getDeployIDs(FQN serviceFQN)
			throws ServiceDeployerException;

	/**
	 * Retrieve all the relevant information from the Cloud about the instance
	 * with id instanceID
	 * 
	 * @param instanceID
	 * @return
	 */
	public InstanceDescription getInstanceDescriptionByID(String instanceID);

	/**
	 * Create a new Tag for the replicaFQN via the SecurityGroup workaround
	 * 
	 * @param replicaFQN
	 * @throws ServiceDeployerException
	 */
	public void registerReplicaFQN(FQN replicaFQN)
			throws ServiceDeployerException;

	public void deregisterReplicaFQN(FQN replicaFQN); // throws
														// ServiceDeployerException;

	/**
	 * Return the ID of the newly created instance
	 */
	public String launchInstance(String imageId, List<String> securityGroups,
			String sshKeyName, String userData, InstanceType instType,
			int minInst, int maxInst) throws ServiceDeployerException;

	void removeInstance(String instanceToTerminateID)
			throws ServiceDeployerException;

	/**
	 * Return the instance id of the VEE
	 * 
	 * TODO Scoprire dove viene usato e a che serve ...
	 * 
	 * @param deployID
	 * @return
	 * @throws ServiceDeployerException
	 */
	public String getInstance(UUID deployID) throws ServiceDeployerException;

	/**
	 * Return the list of Sec Groups associated to an instance
	 * 
	 * @param instanceID
	 * @return
	 * @throws ServiceDeployerException
	 */
	public List<String> getSecurityGroups(String instanceID)
			throws ServiceDeployerException;
}
