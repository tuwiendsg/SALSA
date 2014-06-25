package at.ac.tuwien.dsg.cloud.salsa.engine.services;

import java.io.File;
import java.io.InputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import at.ac.tuwien.dsg.cloud.salsa.common.interfaces.SalsaEngineApiInterface;
import at.ac.tuwien.dsg.cloud.salsa.common.interfaces.SalsaEngineIntenalInterface;

@Service
@Path("/comot")
public class SalsaEngineApiComot implements SalsaEngineApiInterface {
	static Logger logger;
	static File configFile;

	static {
		File tmpFile=new File("/etc/cloudUserParameters.ini");
		if (tmpFile.exists()) {
			configFile = tmpFile;
		} else {
			configFile = new File(SalsaEngineApiComot.class.getResource("/cloudUserParameters.ini").getFile());
		}
		logger = Logger.getLogger("EngineLogger");
	}
	
	SalsaEngineIntenalInterface internalEngine = new SalsaEngineInternal();
	
	@Override
	@PUT
	@Path("/services/{serviceName}")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response deployService(
			@PathParam("serviceName") String serviceName,
			@Multipart("file") InputStream uploadedInputStream) {		
		return internalEngine.deployService(serviceName, uploadedInputStream);
	}
	
	@PUT
	@Path("/services/xml")
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.APPLICATION_XML)
	public String deployServiceFromXML(String uploadedInputStream){
		return internalEngine.deployServiceFromXML(uploadedInputStream);
	}
	
	
	@POST
	@Path("/services/{serviceId}/nodes/{nodeId}/scaleout")
	public Response scaleOutNode(@PathParam("serviceId")String serviceId, 
								 @PathParam("nodeId") String nodeId){
		return internalEngine.scaleOutNode(serviceId, nodeId);
	}
	
	@POST
	@Path("/services/{serviceId}/nodes/{nodeId}/scalein")
	public Response scaleInNode(@PathParam("serviceId")String serviceId, 
								 @PathParam("nodeId") String nodeId){
		return internalEngine.scaleInNode(serviceId, nodeId);
	}
	
	
	@Override
	@DELETE
	@Path("/services/{serviceId}")
	public Response undeployService(@PathParam("serviceId")String serviceId){
		return internalEngine.undeployService(serviceId);
	}
	
	@Override
	@POST
    @Path("/services/{serviceId}/topologies/{topologyId}/nodes/{nodeId}/instance-count/{quantity}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response spawnInstance(@PathParam("serviceId") String serviceId,
                                  @PathParam("topologyId") String topologyId,
                                  @PathParam("nodeId") String nodeId,
                                  @PathParam("quantity") int quantity) {
		return internalEngine.spawnInstance(serviceId, topologyId, nodeId, quantity);
	}
	
	/**
	 * Undeploy an instance
	 * @param serviceId
	 * @param topologyId
	 * @param nodeId
	 * @param instanceId
	 * @return
	 */
	@Override
	@DELETE
	@Path("/services/{serviceId}/topologies/{topologyId}/nodes/{nodeId}/instances/{instanceId}")
	public Response destroyInstance(
			@PathParam("serviceId") String serviceId,
			@PathParam("topologyId") String topologyId,
			@PathParam("nodeId") String nodeId,
			@PathParam("instanceId") String instanceId){
		return destroyInstance(serviceId, topologyId, nodeId, instanceId);
	}

	@Override
	@GET
	@Path("/services/{serviceId}")
	public Response fetchStatus(@PathParam("serviceId") String serviceId) {		
		return internalEngine.getService(serviceId);
	}
	
	
	
	
}
