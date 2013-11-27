package at.ac.tuwien.dsg.cloud.services.impl;

import java.util.List;

import at.ac.tuwien.dsg.cloud.exceptions.ServiceDeployerException;
import at.ac.tuwien.dsg.cloud.services.CloudInterface;
import at.ac.tuwien.dsg.cloud.services.DelayInjectionService;

import com.xerox.amazonws.ec2.InstanceType;

public class PreDelayedCloudInterface extends DecoratedCloudInterface {

	private DelayInjectionService delayInjector;

	public PreDelayedCloudInterface(CloudInterface delegate) {
		super(delegate);
	}

	public PreDelayedCloudInterface(CloudInterface delegate,
			DelayInjectionService delayInjection) {
		super(delegate);
		this.delayInjector = delayInjection;
	}

	@Override
	public String launchInstance(String imageId, List<String> securityGroups,
			String sshKeyName, String userData, InstanceType instType,
			int minInst, int maxInst) throws ServiceDeployerException {
		String instanceID = null;
		try {
			delayInjector.injectDelay();
			instanceID = super.launchInstance(imageId, securityGroups,
					sshKeyName, userData, instType, minInst, maxInst);
		} catch (ServiceDeployerException e) {
		}
		return instanceID;
	}

	@Override
	public void removeInstance(String instanceToTerminateID)
			throws ServiceDeployerException {

		try {
			delayInjector.injectDelay();
			super.removeInstance(instanceToTerminateID);
		} catch (ServiceDeployerException e) {
			throw e;
		}
	}
}