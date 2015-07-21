package at.ac.tuwien.dsg.cloud.salsa.common.interfaces;

import java.io.InputStream;

import javax.ws.rs.core.Response;

import at.ac.tuwien.dsg.cloud.salsa.engine.exception.SalsaException;

public interface SalsaEngineApiInterface {

    Response deployService(String serviceName, InputStream uploadedInputStream) throws SalsaException;

    Response spawnInstance(String serviceId, String topologyId, String nodeId, int quantity) throws SalsaException;

    Response destroyInstance(String serviceId, String topologyId, String nodeId, String instanceId) throws SalsaException;

    Response undeployService(String serviceId) throws SalsaException;
    
    Response fetchStatus(String serviceId) throws SalsaException;
}