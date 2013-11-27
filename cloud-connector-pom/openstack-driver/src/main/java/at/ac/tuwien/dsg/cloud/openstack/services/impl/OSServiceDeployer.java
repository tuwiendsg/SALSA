package at.ac.tuwien.dsg.cloud.openstack.services.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;

import at.ac.tuwien.dsg.cloud.data.DynamicServiceDescription;
import at.ac.tuwien.dsg.cloud.data.InstanceDescription;
import at.ac.tuwien.dsg.cloud.data.StaticServiceDescription;
import at.ac.tuwien.dsg.cloud.data.VeeDescription;
import at.ac.tuwien.dsg.cloud.exceptions.ServiceDeployerException;
import at.ac.tuwien.dsg.cloud.services.CloudController;
import at.ac.tuwien.dsg.cloud.services.CloudInterface;
import at.ac.tuwien.dsg.cloud.services.ServiceDeployer;
import ch.usi.cloud.controller.common.naming.FQN;

/**
 * This class implements a PER-THREAD scoped service that either executes a
 * deployment of a given service, or its complete un-deployment.
 * 
 * @author Alessio Gambi (alessio.gambi@usi.ch)
 */

public class OSServiceDeployer implements ServiceDeployer {

	private Logger logger;
	private CloudController controller;
	private CloudInterface cloud;

	public OSServiceDeployer(Logger logger, CloudController controller,
			CloudInterface cloud) {

		this.logger = logger;
		this.cloud = cloud;
		this.controller = controller;

		// System.out.println("OSServiceDeployer.OSServiceDeployer() Logger "
		// + logger.getName());

	}

	// Note that this is STATELESS, with use the Deployment ID as key to
	// retrieve info from the Cloud !
	@Override
	public UUID deployService(String organizationName, String customerName,
			String serviceName, StaticServiceDescription _serviceSpec)
			throws ServiceDeployerException {

		// Generate Unique DeployID. This will be used with serviceFQN
		UUID deployID = UUID.randomUUID();
		logger.debug("Starting Deployment " + deployID);

		// Generate ServiceID
		FQN serviceFQN = new FQN(organizationName, customerName, serviceName);

		// Register the DeployID with the serviceFQN
		// This TAG all the virtual machines that belong to this service
		// instance.
		// Later, just before deployment, other TAGs will be added to identify
		// the replicaFQN of each single instance
		cloud.registerDeploy(serviceFQN, deployID);

		String errorMessage;

		if (logger.isDebugEnabled()) {
			try {
				logger.debug("Deploying a new service on the OPENSTACK platform.\n"
						+ "\t\t"
						+ "OrganizationName "
						+ organizationName
						+ "\n"
						+ "\t\t"
						+ "CustomerName "
						+ customerName
						+ "\n"
						+ "\t\t"
						+ "ServiceName "
						+ serviceName
						+ "\n\n"
						+ "ServiceSpec " + _serviceSpec + "\n");
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
				throw new ServiceDeployerException("Parameters exceptions ", e);
			}
		}

		StaticServiceDescription serviceSpec = new StaticServiceDescription(
				serviceFQN, _serviceSpec.getOrderedVees(),
				_serviceSpec.getEntryPoint());

		DynamicServiceDescription service = new DynamicServiceDescription(
				serviceSpec);

		for (VeeDescription vee : service.getStaticServiceDescription()
				.getOrderedVees()) {
			String veeName = vee.getName();

			// Inject the TAG as sec group to all the VEE types
			service.getVeeDescription(veeName).getSecurityGroups()
					.add(deployID.toString());
		}

		try {
			launchService(service, deployID);
			logger.info("\n=======================================\n"
					+ "\t Deployment done for service " + serviceFQN + "\n"
					+ "=======================================\n");
			return deployID;
		} catch (Throwable e) {
			errorMessage = "A problem has occured while launching the service "
					+ serviceFQN + " under deployment " + deployID;
			logger.error(errorMessage, e);
			throw new ServiceDeployerException(errorMessage, e);
		}

	}

	@Override
	public void undeployAllServices(String organizationName,
			String customerName, String serviceName,
			StaticServiceDescription service) throws ServiceDeployerException {

		FQN serviceFQN = new FQN(organizationName, customerName, serviceName);
		logger.warn("Wildcard DeployID Undeploy ALL the instances of service "
				+ serviceFQN);

		undeployAll(serviceFQN, service);

	}

	private void undeployAll(FQN serviceFQN, StaticServiceDescription service)
			throws ServiceDeployerException {
		// Get the list of all the DeployID
		Set<UUID> deployIDs = cloud.getDeployIDs(serviceFQN);
		for (UUID deployID : deployIDs) {
			try {
				undeploy(serviceFQN, deployID, service);
			} catch (Exception e) {
				e.printStackTrace();
				logger.warn("", e);
			}
		}

	}

	private void undeploy(FQN serviceFQN, UUID deployID,
			StaticServiceDescription service) throws ServiceDeployerException {
		try {

			Set<String> instanceIds = new HashSet<String>();
			if (service != null) {
				if (service.isSingleInstanceDeployment()) {
					logger.info("Single Instance Undeploy ");
					instanceIds.add(cloud.getInstance(deployID));
				} else {
					instanceIds = cloud.getServiceInstances(serviceFQN,
							deployID);
				}
			} else {
				instanceIds = cloud.getServiceInstances(serviceFQN, deployID);
			}

			for (String instanceID : instanceIds) {
				try {
					controller.removeVEEbyInstanceID(instanceID, service);
				} catch (IllegalArgumentException e) {
					String errorMessage = "(UNDEPLOY) An error has occured while terminating virtual machine "
							+ instanceID;
					logger.error(errorMessage, e);
					// TODO Not sure about raising this exception here...
					// throw new ServiceDeployerException(errorMessage, e);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			String errorMessage = "(UNDEPLOY) An error has occured while undeploying service "
					+ serviceFQN;
			logger.error(errorMessage, e);
			throw new ServiceDeployerException(errorMessage, e);
		}

		try {
			// If the undeploy is already started and someone else, i.e.,
			// elastic controller, deploys a new virtual machine it's a race
			// conditions.
			// The cloud does not allows to remove the deployID as that is in
			// use by the newly launched virtual machines. So we need to synch
			// the state again a eventually repeat the undeploy.
			cloud.deregisterDeploy(serviceFQN, deployID);
		} catch (ServiceDeployerException e) {
			logger.error("Possible race condition", e);
			if (e.getMessage().contains("The security group for")
					&& e.getMessage().contains(deployID.toString())) {

				// try again to undeploy the thing
				logger.warn("Try to undeploy the service (recursively) !");
				undeploy(serviceFQN, deployID, service);
			}
		} catch (Exception e) {

			e.printStackTrace();
			String errorMessage = "(UNDEPLOY) An error has occured while undeploying service "
					+ serviceFQN;
			logger.error(errorMessage, e);
			throw new ServiceDeployerException(errorMessage, e);
		}

	}

	@Override
	public void undeployService(String organizationName, String customerName,
			String serviceName, UUID deployID, StaticServiceDescription service)
			throws ServiceDeployerException {

		if (deployID == null) {
			logger.warn("DeployID was not set, skip the service undeploy.");
			return;
		}

		FQN serviceFQN = new FQN(organizationName, customerName, serviceName);
		if (deployID.toString().equals("*")) {
			logger.warn("Wildcard DeployID : Undeploy ALL the instances of service "
					+ serviceFQN);

			undeployAll(serviceFQN, service);
		} else {
			undeploy(serviceFQN, deployID, service);
		}

		// This must be done as last step because id there are instances using
		// the sec group, the platform will not let us to remove it

		//
		logger.info("\n=======================================\n"
				+ "\t Undeployment done for service " + serviceFQN + "  "
				+ deployID + "\n" + "=======================================\n");
	}

	private void launchService(DynamicServiceDescription service, UUID deployID)
			throws ServiceDeployerException {

		// We are under the assumption that every deployment is UNIQUE !
		logger.info("launchService "
				+ service.getStaticServiceDescription().getServiceFQN()
				+ " under deploy " + deployID);

		logger.info("EntryPoint "
				+ service.getStaticServiceDescription().getEntryPoint());

		String errorMessage;

		// THis will return the ordered list of machines to run. It is build
		// considered the orderedVee constraints
		List<InstanceDescription> initialConfiguration = service
				.getStaticServiceDescription().getInitialConfiguration();
		// TODO Deployment plan.
		logger.info("Deployment plan " + initialConfiguration);

		// TODO Given a deployment plan we follow it. For the moment everything
		// is sequential but in the future we may decide to mix sequential and
		// parallel deployments as an optimization

		// loop over all the instances of the service and deploy them !
		for (InstanceDescription instanceDescription : initialConfiguration) {
			FQN replicaFQN = instanceDescription.getReplicaFQN();
			try {
				logger.info("Launching replica " + replicaFQN
						+ " under deploy " + deployID);

				cloud.registerReplicaFQN(replicaFQN);

				controller.launchVEEwithReplicaFQN(replicaFQN, service,
						deployID);

				logger.info("Service status after deployment " + service);

			} catch (ServiceDeployerException e) {
				errorMessage = "(DEPLOY) An error has occured while launching  "
						+ replicaFQN;
				throw new ServiceDeployerException(errorMessage, e);
			} catch (Exception e) {
				errorMessage = "(DEPLOY) An error has occured while launching an instance of VEE "
						+ instanceDescription.getReplicaFQN().toString();
				throw new ServiceDeployerException(errorMessage, e);
			}
		}

	}
}
