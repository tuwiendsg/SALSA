package at.ac.tuwien.dsg.cloud.openstack.services.impl;

import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;

import at.ac.tuwien.dsg.cloud.data.InstanceDescription;
import at.ac.tuwien.dsg.cloud.exceptions.ServiceDeployerException;
import at.ac.tuwien.dsg.cloud.services.CloudInterface;
import at.ac.tuwien.dsg.cloud.services.InstanceService;
import at.ac.tuwien.dsg.cloud.services.UserDataService;
import at.ac.tuwien.dsg.cloud.services.UserDataServiceFilter;
import ch.usi.cloud.controller.common.naming.FQN;

public class RemoteValuesUserDataServiceFilter implements UserDataServiceFilter {

	private Logger logger;
	private CloudInterface cloud;

	public RemoteValuesUserDataServiceFilter(Logger logger, CloudInterface cloud) {
		this.logger = logger;
		this.cloud = cloud;

		// System.out
		// .println("RemoteValuesUserDataServiceFilter.RemoteValuesUserDataServiceFilter() Logger "
		// + logger.getName());
	}

	@Override
	public String getUserData(String inputUserData,
			InstanceService instanceService, UserDataService delegate) {

		FQN serviceFQN = (FQN) instanceService.get("@serviceFQN");
		// Post Processing
		return injectRemoteValues(
				delegate.getUserData(inputUserData, instanceService),
				serviceFQN);
	}

	private String injectRemoteValue(String propertyName, FQN serviceFQN,
			String _deployID) {

		if ("entrypoint-ip".equals(propertyName)
				|| "entrypoint-private-ip".equals(propertyName)) {

			try {
				Set<String> serviceInstances = cloud.getServiceInstances(
						serviceFQN, UUID.fromString(_deployID));

				for (String instanceID : serviceInstances) {

					if (cloud.getSecurityGroups(instanceID).contains(
							"ENTRY_POINT")) {
						try {
							InstanceDescription entryPoint = cloud
									.getInstanceDescriptionByID(instanceID);

							logger.info("OSUtils.injectRemoteValue() Entry point "
									+ entryPoint.getPublicIp()
									+ " "
									+ entryPoint.getPrivateIp());

							if ("entrypoint-private-ip".equals(propertyName)) {
								logger.info("Returning the value of Private IP");
								return entryPoint.getPrivateIp()
										.getHostAddress();
							} else {
								logger.info("Returning the value of DEFAULT IP");
								return (entryPoint.getPublicIp() != null) ? entryPoint
										.getPublicIp().getHostAddress()
										: entryPoint.getPrivateIp()
												.getHostAddress();
							}
						} catch (Exception e) {
							e.printStackTrace();
							return null;
						}
					}
				}

			} catch (ServiceDeployerException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		} else {
			logger.warn("Not a valid remote property " + propertyName);
		}
		return null;

	}

	public String injectRemoteValues(String original, FQN serviceFQN) {

		logger.debug("injectRemoteValues() " + serviceFQN);

		String replacement = original;

		// Extract the string from the propertis that start with @@<UUID>
		String regex = "@@[0-9a-fA-F]{8}(?:-[0-9a-fA-F]{4}){3}-[0-9a-fA-F]{12}@.+";
		Matcher m = Pattern.compile("(?=(" + regex + "))").matcher(original);
		while (m.find()) {
			// "@@UUID@entrypoint-ip",

			String group = m.group(1);
			String _deployID = group.replace("@@", "").split("@")[0];
			String propertyName = group.replace("@@", "").split("@")[1];

			System.out.println("injectRemoteValues() Remote of " + propertyName
					+ " from " + _deployID + " sevice " + serviceFQN);

			String remoteValue = null;
			try {
				remoteValue = injectRemoteValue(propertyName, serviceFQN,
						_deployID);
			} catch (Exception e) {
				logger.warn("Problem in getting remote value " + propertyName);
			}
			if (remoteValue != null) {
				replacement = replacement.replaceAll(group, remoteValue);
			}
		}
		return replacement;

	}
}
