package at.ac.tuwien.dsg.cloud.salsa.engine.services;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;

import at.ac.tuwien.dsg.cloud.salsa.common.processing.SalsaCenterConnector;
import at.ac.tuwien.dsg.cloud.salsa.engine.utils.EngineLogger;
import at.ac.tuwien.dsg.cloud.salsa.engine.utils.SalsaConfiguration;

/**
 * This code is a proxy for some service calls from SalsaEngine to SalsaCenter
 * SalsaCenterConnector class provides enough calls to the center, but JavaScript
 * on this webapp cannot due to Cross-Domain Request.
 * @author Le Duc Hung
 *
 */
@Path("/proxy")
public class ProxySalsaCenter  {
	
	SalsaCenterConnector connector;
	String endPoint = SalsaConfiguration.getSalsaCenterEndpoint();
	Logger logger = EngineLogger.logger;
	

	@GET
	@Path("/test")
	@Produces(MediaType.TEXT_PLAIN)
	public String test() {		
		return "This is a test from Proxy !";		
	}
	
	@GET
	@Path("/getserviceruntimexml/{id}")
	@Produces(MediaType.TEXT_PLAIN)
	public String getServiceRuntimeXml(@PathParam("id") String serviceDeployId) {
		connector = new SalsaCenterConnector(endPoint, serviceDeployId, "", logger);
		return connector.getUpdateCloudServiceRuntimeXML();		
	}
	
	@GET
	@Path("/getservicejsonlist")
	@Produces(MediaType.TEXT_PLAIN)
	public String getServiceRuntimeJson() {
		connector = new SalsaCenterConnector(endPoint, "", "", logger);
		return connector.getServiceListJson();
	}
	
	@GET
	@Path("/deregister/{serviceId}")
	@Produces(MediaType.TEXT_PLAIN)
	public String deregisterService(@PathParam("serviceId") String serviceDeployId) {
		if (serviceDeployId == null){
			return "Undefined service ID !";
		}
		connector = new SalsaCenterConnector(endPoint, serviceDeployId, "", logger);
		EngineLogger.logger.debug("Proxy call deregister: " + serviceDeployId);
		return connector.deregisterService();		
	}
	
}
