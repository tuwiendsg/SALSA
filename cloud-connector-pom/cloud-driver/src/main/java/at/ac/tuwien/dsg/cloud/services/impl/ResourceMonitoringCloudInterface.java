package at.ac.tuwien.dsg.cloud.services.impl;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import at.ac.tuwien.dsg.cloud.data.InstanceDescription;
import at.ac.tuwien.dsg.cloud.data.ResourceRequest;
import at.ac.tuwien.dsg.cloud.exceptions.ServiceDeployerException;
import at.ac.tuwien.dsg.cloud.services.CloudInterface;
import at.ac.tuwien.dsg.cloud.services.ResourceMonitor;
import ch.usi.cloud.controller.common.naming.FQN;

import com.xerox.amazonws.ec2.InstanceType;

public class ResourceMonitoringCloudInterface implements CloudInterface {

	private CloudInterface delegate;
	private ResourceMonitor resourceMonitor;

	public ResourceMonitoringCloudInterface(CloudInterface delegate,
			ResourceMonitor resourceMonitor) {
		this.delegate = delegate;
		this.resourceMonitor = resourceMonitor;
	}

	@Override
	public boolean isRegisteredService(FQN serviceFQN)
			throws ServiceDeployerException {
		return delegate.isRegisteredService(serviceFQN);
	}

	@Override
	public void registerService(FQN serviceFQN) throws ServiceDeployerException {
		delegate.registerService(serviceFQN);
	}

	@Override
	public void deregisterService(FQN serviceFQN)
			throws ServiceDeployerException {
		delegate.deregisterService(serviceFQN);

	}

	@Override
	public void registerDeploy(FQN serviceFQN, UUID deployID)
			throws ServiceDeployerException {
		delegate.registerDeploy(serviceFQN, deployID);
	}

	@Override
	public void deregisterDeploy(FQN serviceFQN, UUID deployID)
			throws ServiceDeployerException {
		delegate.deregisterDeploy(serviceFQN, deployID);
	}

	@Override
	public Set<String> getServiceInstances(FQN serviceFQN, UUID deployID)
			throws ServiceDeployerException {
		return delegate.getServiceInstances(serviceFQN, deployID);
	}

	@Override
	public Set<UUID> getDeployIDs(FQN serviceFQN)
			throws ServiceDeployerException {
		return delegate.getDeployIDs(serviceFQN);
	}

	@Override
	public InstanceDescription getInstanceDescriptionByID(String instanceID) {
		return delegate.getInstanceDescriptionByID(instanceID);
	}

	@Override
	public void registerReplicaFQN(FQN replicaFQN)
			throws ServiceDeployerException {
		delegate.registerReplicaFQN(replicaFQN);
	}

	@Override
	public void deregisterReplicaFQN(FQN replicaFQN) {
		delegate.deregisterReplicaFQN(replicaFQN);
	}

	@Override
	public String launchInstance(String imageId, List<String> securityGroups,
			String sshKeyName, String userData, InstanceType instType,
			int minInst, int maxInst) throws ServiceDeployerException {

		String instanceID = null;

		ResourceRequest request = ResourceRequest.newDeploy();
		request.setImageID(imageId);
		request.setSecurityGroups(securityGroups);
		request.setInstanceType(instType.name());

		try {
			// Collect some useful data for monitoring
			instanceID = delegate.launchInstance(imageId, securityGroups,
					sshKeyName, userData, instType, minInst, maxInst);
		} catch (ServiceDeployerException e) {
			// Here there's an error
			request.done();
			request.setError();
			resourceMonitor.record(request);
			throw e;
		}
		request.done();
		request.setInstanceID(instanceID);
		// Here there's we are just fine
		resourceMonitor.record(request);
		return instanceID;
	}

	@Override
	public void removeInstance(String instanceToTerminateID)
			throws ServiceDeployerException {

		ResourceRequest request = ResourceRequest.newUndeploy();
		request.setInstanceID(instanceToTerminateID);

		try {
			delegate.removeInstance(instanceToTerminateID);
		} catch (ServiceDeployerException e) {
			// Here there's an error
			request.done();
			request.setError();
			resourceMonitor.record(request);
			throw e;
		}
		request.done();
		// Here there's we are just fine
		resourceMonitor.record(request);
	}

	@Override
	public String getInstance(UUID deployID) throws ServiceDeployerException {
		return delegate.getInstance(deployID);
	}

	@Override
	public List<String> getSecurityGroups(String instanceID)
			throws ServiceDeployerException {
		return delegate.getSecurityGroups(instanceID);
	}

}