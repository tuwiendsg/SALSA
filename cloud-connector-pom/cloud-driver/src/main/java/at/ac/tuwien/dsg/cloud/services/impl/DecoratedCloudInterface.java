package at.ac.tuwien.dsg.cloud.services.impl;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import at.ac.tuwien.dsg.cloud.data.InstanceDescription;
import at.ac.tuwien.dsg.cloud.exceptions.ServiceDeployerException;
import at.ac.tuwien.dsg.cloud.services.CloudInterface;
import ch.usi.cloud.controller.common.naming.FQN;

import com.xerox.amazonws.ec2.InstanceType;

public class DecoratedCloudInterface implements CloudInterface {

	private CloudInterface delegate;

	public DecoratedCloudInterface(CloudInterface delegate) {
		this.delegate = delegate;
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
		return delegate.launchInstance(imageId, securityGroups, sshKeyName,
				userData, instType, minInst, maxInst);
	}

	@Override
	public void removeInstance(String instanceToTerminateID)
			throws ServiceDeployerException {
		delegate.removeInstance(instanceToTerminateID);
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