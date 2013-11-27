package at.ac.tuwien.dsg.cloud.services;

import java.util.List;
import java.util.UUID;

import at.ac.tuwien.dsg.cloud.data.DynamicServiceDescription;
import at.ac.tuwien.dsg.cloud.data.StaticServiceDescription;
import at.ac.tuwien.dsg.cloud.exceptions.ServiceDeployerException;
import ch.usi.cloud.controller.common.naming.FQN;

public interface CloudController {

	void launchVEE(FQN veeFQN, DynamicServiceDescription service, UUID deployID)
			throws ServiceDeployerException;

	void launchVEEwithReplicaFQN(FQN replicaFQN,
			DynamicServiceDescription service, UUID deployID)
			throws ServiceDeployerException;

	void removeVEEbyInstanceID(String instanceToTerminateID,
			StaticServiceDescription service);

	void removeVEEbyInstanceID(String instanceToTerminateID);

	void removeVEEsbyInstanceID(DynamicServiceDescription service,
			List<String> instancesToTerminateID);

}
