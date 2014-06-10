package at.ac.tuwien.dsg.cloud.salsa.cloud_connector;

import java.util.List;


public interface CloudInterface {


		/**
	 * Retrieve all the relevant information from the Cloud about the instance
	 * with id instanceID
	 * 
	 * @param instanceID
	 * @return
	 */
	public InstanceDescription getInstanceDescriptionByID(String instanceID);

	
	public String launchInstance(String instanceName, String imageId, List<String> securityGroups,
			String sshKeyName, String userData, String instType,
			int minInst, int maxInst) throws ServiceDeployerException;
	
	/**
	 * Return the ID of the newly created instance
	 */
//	public String launchInstance(String imageId, List<String> securityGroups,
//			String sshKeyName, String userData, InstanceType instType,
//			int minInst, int maxInst);

	void removeInstance(String instanceToTerminateID) throws ServiceDeployerException;

	
}
