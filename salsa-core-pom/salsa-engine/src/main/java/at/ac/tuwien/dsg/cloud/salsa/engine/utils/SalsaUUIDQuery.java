package at.ac.tuwien.dsg.cloud.salsa.engine.utils;

import java.util.UUID;

import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.CloudService;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.ServiceInstance;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.ServiceTopology;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.ServiceUnit;

public class SalsaUUIDQuery {
	public static ServiceInstance query(CloudService service, UUID uuid){
		for (ServiceTopology topo : service.getComponentTopologyList()) {
			for (ServiceUnit unit : topo.getComponents()) {
				for (ServiceInstance instance : unit.getInstancesList()) {
					if (instance.getUuid().equals(uuid)){
						return instance;
					}
				}
				
			}
		}
		return null;
	}
}
