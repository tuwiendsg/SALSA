package at.ac.tuwien.dsg.cloud.salsa.common.interfaces;

import java.io.InputStream;

import javax.ws.rs.core.Response;

public interface SalsaEngineApiInterface {

    Response deployService(String serviceName, InputStream uploadedInputStream);

    Response spawnInstance(String serviceId, String topologyId, String nodeId, int quantity);

    Response destroyInstance(String serviceId, String topologyId, String nodeId, String instanceId);

    Response undeployService(String serviceId);
    
    Response fetchStatus(String serviceId);
}