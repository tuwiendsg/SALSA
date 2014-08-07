package at.ac.tuwien.dsg.cloud.salsa.common.interfaces;

import java.io.InputStream;

import javax.ws.rs.core.Response;

import at.ac.tuwien.dsg.cloud.salsa.engine.exception.SalsaEngineException;

public interface SalsaEngineApiInterface {

    Response deployService(String serviceName, InputStream uploadedInputStream) throws SalsaEngineException;

    Response spawnInstance(String serviceId, String topologyId, String nodeId, int quantity) throws SalsaEngineException;

    Response destroyInstance(String serviceId, String topologyId, String nodeId, String instanceId) throws SalsaEngineException;

    Response undeployService(String serviceId) throws SalsaEngineException;
    
    Response fetchStatus(String serviceId) throws SalsaEngineException;
}