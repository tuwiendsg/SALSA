package at.ac.tuwien.dsg.cloud.salsa.common.interfaces;

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
import org.springframework.stereotype.Service;

import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.ServiceInstance;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.ServiceUnit;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.ServiceUnitRelationship;
import at.ac.tuwien.dsg.cloud.salsa.engine.exception.SalsaEngineException;
import at.ac.tuwien.dsg.cloud.salsa.tosca.extension.SalsaCapaReqString;

@Service
@Path("/")
public interface SalsaEngineServiceIntenal {
	
	// INTERFACES FOR SALSA USERS TO DEPLOY AND MANAGE THEIR APPLICATION
		
	/**
	 * This service deploys the whole Tosca file.
	 * The form which is posted to service must contain all parameters
	 * @param uploadedInputStream The file contents
	 * @param serviceName The ServiceName. This must be unique in whole system.
	 * @return The information
	 */
	@PUT
	@Path("/services/{serviceName}")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response deployService(@PathParam("serviceName") String serviceName,
			@Multipart("file") InputStream uploadedInputStream) throws SalsaEngineException;
	
	/**
	 * Deploy new service by submitting the XML directly in the data
	 * @param uploadedInputStream
	 * @return
	 */
	@PUT
	@Path("/services/xml")
	@Consumes(MediaType.APPLICATION_XML)	
	public Response deployServiceFromXML(String uploadedInputStream) throws SalsaEngineException;
	
	/**
	 * Remove the whole cloud service 
	 * @param serviceId
	 * @return
	 */
	@DELETE
	@Path("/services/{serviceId}")
	public Response undeployService(@PathParam("serviceId") String serviceId) throws SalsaEngineException;
	
	
	/**
	 * This service deploy a number of service units
	 * @param serviceId
	 * @param topologyId
	 * @param nodeId
	 * @param quantity
	 * @return
	 */
	@POST
    @Path("/services/{serviceId}/topologies/{topologyId}/nodes/{nodeId}/instance-count/{quantity}")
	//@Produces(MediaType.APPLICATION_JSON)
	public Response spawnInstance(@PathParam("serviceId")String serviceId, 
			@PathParam("topologyId")String topologyId,
			 @PathParam("nodeId") String nodeId, 
			 @PathParam("quantity") int quantity) throws SalsaEngineException;
	
	/**
	 * This method will destroy an instance, regardless it is a VM or a software
	 * @param serviceId
	 * @param topologyId
	 * @param nodeId
	 * @param instanceId
	 * @return
	 */
	@DELETE
	@Path("/services/{serviceId}/topologies/{topologyId}/nodes/{nodeId}/instances/{instanceId}")
	public Response destroyInstance(@PathParam("serviceId")String serviceId, 
			@PathParam("topologyId")String topologyId,
			@PathParam("nodeId") String nodeId, 
			@PathParam("instanceId")int instanceId) throws SalsaEngineException;
	
	/**
	 * Get service description in SALSA XML format
	 * 
	 * @param serviceName
	 *            The deployment ID of service
	 * @return XML document of service
	 */
	@GET
	@Path("/services/{serviceId}")
	@Produces(MediaType.TEXT_XML)
	public Response getService(@PathParam("serviceId")String serviceDeployId) throws SalsaEngineException;
	
	/**
	 * Get service description in TOSCA
	 * 
	 * @param serviceName
	 *            The deployment ID of service
	 * @return XML document of service
	 */
	@GET
	@Path("/services/tosca/{serviceId}")
	@Produces(MediaType.TEXT_XML)
	public Response getToscaService(@PathParam("serviceId")String serviceDeployId);
	
	
	// INTERFACES FOR rSYBL
	
	
	/**
	 * This method does scale out, is used by rSYBL
	 * @param serviceId
	 * @param nodeId
	 * @return
	 */
	@POST
	@Path("/services/{serviceId}/nodes/{nodeId}/scaleout")
	public Response scaleOutNode(@PathParam("serviceId")String serviceId, 
								 @PathParam("nodeId") String nodeId) throws SalsaEngineException;
	
	/**
	 * This method does scale in, is used by rSYBL
	 * @param serviceId
	 * @param nodeId
	 * @return
	 */
	@POST
	@Path("/services/{serviceId}/nodes/{nodeId}/scalein")
	public Response scaleInNode(@PathParam("serviceId")String serviceId, 
								 @PathParam("nodeId") String nodeId) throws SalsaEngineException;
	
	/**
	 * This method do scale in at the VM level, by taking the VM's IP
	 * @param serviceId
	 * @param vmIp
	 * @return
	 */
	@POST
	@Path("/services/{serviceId}/vmnodes/{ip}/scalein")
	public Response scaleInVM(@PathParam("serviceId")String serviceId, 
								 @PathParam("ip") String vmIp) throws SalsaEngineException;
	
	
	@GET
	@Path("/services/tosca/{serviceId}/syblapp")
	@Produces(MediaType.TEXT_XML)
	public Response getServiceSYBL_APP_DESP(@PathParam("serviceId")String serviceDeployId);
	
	
	@GET
	@Path("/services/tosca/{serviceId}/sybl")
	@Produces(MediaType.TEXT_XML)
	public Response getServiceSYBL_DEP_DESP(@PathParam("serviceId")String serviceDeployId);
	
	
	// INTERFACE FOR THE CLOUD CONNECTOR AND THE PIONEER TO CONNECT TO
	
	@GET
	@Path("/health")
	public String health();
		
	
	@POST
    @Path("/services/{serviceId}/topologies/{topologyId}")
	public Response addServiceUnitMetaData(ServiceUnit unit,
			@PathParam("serviceId")String serviceId, 
			@PathParam("topologyId")String topologyId);
	
	@POST
    @Path("/services/{serviceId}/topologies/{topologyId}/tosca")
	public Response addServiceUnitMetaData(String toscaXML,
			@PathParam("serviceId")String serviceId, 
			@PathParam("topologyId")String topologyId);
	
	/**
	 * This method add new instance deployment and metadata
	 * @param serviceId The exist service
	 * @param topologyId Not require at this time, but need to be presented
	 * @param nodeId Id of node to be deployed more
	 * @param instanceId The defined ID of the instance.
	 * 		  if the instanceID existed, update and redeploy instance (not implemented)
	 * @return
	 * 
	 */
	@PUT
	@Path("/services/{serviceId}/topologies/{topologyId}/nodes/{nodeId}/instances/{instanceId}")
	public Response deployInstance(@PathParam("serviceId") String serviceId, 
			@PathParam("topologyId")String topologyId,
			@PathParam("nodeId")String nodeId, 
			@PathParam("instanceId")int instanceId) throws SalsaEngineException;
	
	
	/**
	 * Add a relationship on the topology
	 * @param data
	 * @param serviceId
	 * @param topologyId
	 * @return
	 */
	@POST
	@Path("/services/{serviceId}/topologies/{topologyId}/relationship")
	//@Consumes(MediaType.APPLICATION_XML)
	public Response addRelationship(ServiceUnitRelationship data,
			@PathParam("serviceId")String serviceId,
			@PathParam("topologyId")String topologyId);

	@POST
	@Path("/services/{serviceId}/topologies/{topologyId}/nodes/{nodeId}/instance-counter/{value}")
	public Response updateNodeIdCounter(
			@PathParam("serviceId")String serviceId,
			@PathParam("topologyId")String topologyId,
			@PathParam("nodeId")String nodeId,
			@PathParam("value")int value);

	/**
	 * This PUT the metadata only. It is called by Pioneer which already do deployment.
	 * @param serviceId The exist service
	 * @param topologyId Not require at this time, but need to be presented
	 * @param nodeId Id of node to be deployed more
	 * @param quantity Number of instances will be deployed
	 * @return
	 * 
	 */
	@POST
	@Path("/services/{serviceId}/topologies/{topologyId}/nodes/{nodeId}/instance-metadata")
	//@Consumes(MediaType.APPLICATION_XML)
	public Response addInstanceUnitMetaData(ServiceInstance data,
			@PathParam("serviceId") String serviceId,
			@PathParam("topologyId")String topologyId,
			@PathParam("nodeId")String nodeId);

	/**
	 * Update a replica capability.
	 * 
	 * @param serviceId
	 * @param instanceId
	 * @param capaId
	 * @param value
	 * @return
	 */
	@POST
	@Path("/services/{serviceId}/topologies/{topologyId}/nodes/{nodeId}/instances/{instanceId}/capability")
	//@Consumes(MediaType.APPLICATION_XML)
	public Response updateInstanceUnitCapability(
			SalsaCapaReqString data,
			@PathParam("serviceId") String serviceId,
			@PathParam("topologyId")String topologyId,
			@PathParam("nodeId")String nodeId,
			@PathParam("instanceId") int instanceId);

	/**
	 * Update the properties for a replica node instance. Properties is an AnyType Xml object
	 * and will be parsed if possible, and add all to the replica.
	 * @param data
	 * @param serviceId
	 * @param topologyId
	 * @param nodeId
	 * @param instanceId
	 * @return
	 */

	@POST
	@Path("/services/{serviceId}/topologies/{topologyId}/nodes/{nodeId}/instances/{instanceId}/properties")
	//@Consumes(MediaType.TEXT_PLAIN)
	public Response updateInstanceUnitProperties(
			//JAXBElement<Object> data,
			String data,
			@PathParam("serviceId") String serviceId,
			@PathParam("topologyId") String topologyId, 
			@PathParam("nodeId") String nodeId,
			@PathParam("instanceId") int instanceId);

	/**
	 * Update a replica's state.
	 * 
	 * @param serviceId
	 * @param topologyId
	 * @param nodeId
	 * @param instanceId
	 * @param value
	 * @return
	 */
	@POST
	@Path("/services/{serviceId}/topologies/{topologyId}/nodes/{nodeId}/instances/{instanceId}/state/{value}")
	public Response updateNodeState(
			@PathParam("serviceId")String serviceId,
			@PathParam("topologyId") String topologyId,
			@PathParam("nodeId")String nodeId,
			@PathParam("instanceId")int instanceId,
			@PathParam("value") String value);

	@GET
	@Path("/services/{serviceId}/topologies/{topologyId}/nodes/{nodeId}/instances/{instanceId}/requirement/{reqId}")
	public Response getRequirementValue(
			@PathParam("serviceId") String serviceId,
			@PathParam("topologyId") String topologyId,
			@PathParam("nodeId") String nodeId,
			@PathParam("instanceId") int instanceId,
			@PathParam("reqId") String reqId);

	@POST
	@Path("/services/{serviceId}/topologies/{topologyId}/nodes/{nodeId}/instances/{instanceId}/action_queue/{actionName}")
	public Response queueAction(			
			@PathParam("serviceId") String serviceId,
			@PathParam("topologyId") String topologyId,
			@PathParam("nodeId") String nodeId,
			@PathParam("instanceId") int instanceId,
			@PathParam("actionName") String actionName);
	
	@POST
	@Path("/services/{serviceId}/topologies/{topologyId}/nodes/{nodeId}/instances/{instanceId}/action_unqueue/{actionName}")
	public Response unqueueAction(
			@PathParam("serviceId") String serviceId,
			@PathParam("topologyId") String topologyId,
			@PathParam("nodeId") String nodeId,
			@PathParam("instanceId") int instanceId,
			@PathParam("actionName") String actionName);
	
	
	@POST
	@Path("/log")
	public Response logMessage(String data);

}