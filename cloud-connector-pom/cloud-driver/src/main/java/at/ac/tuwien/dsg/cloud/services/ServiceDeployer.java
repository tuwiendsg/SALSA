package at.ac.tuwien.dsg.cloud.services;

import java.util.UUID;

import at.ac.tuwien.dsg.cloud.data.StaticServiceDescription;
import at.ac.tuwien.dsg.cloud.exceptions.ServiceDeployerException;

public interface ServiceDeployer {

	public UUID deployService(String organizationName, String customerName,
			String serviceName, StaticServiceDescription serviceSpec)
			throws ServiceDeployerException;

	public void undeployService(String organizationName, String customerName,
			String serviceName, UUID deployID,
			StaticServiceDescription serviceSpec)
			throws ServiceDeployerException;

	public void undeployAllServices(String organizationName,
			String customerName, String serviceName,
			StaticServiceDescription serviceSpec)
			throws ServiceDeployerException;

}
