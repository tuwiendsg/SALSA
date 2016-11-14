/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.salsa.engine.services;

import at.ac.tuwien.dsg.salsa.model.salsa.info.SalsaException;
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

/**
 *
 * @author hungld
 */
@Path("/")
public interface ConfigurationService {

    //////////////////////////////////////////
    // API for the whole cloud service
    //////////////////////////////////////////
    /**
     * Submit and deploy a service. The TOSCA is the data of the request.
     *
     * @param uploadedInputStream The XML String of the TOSCA
     * @return The ID of the service
     * @throws SalsaException
     */
    @PUT
    @Path("/services/xml")
    public Response deployServiceFromXML(String uploadedInputStream) throws SalsaException;

    /**
     * Redeploy the service
     *
     * @param serviceId The ID of the service to be redeployed
     * @return The ID of the service if the redeployment is successful
     * @throws SalsaException
     */
    @POST
    @Path("/services/{serviceId}/redeploy")
    public Response redeployService(@PathParam("serviceId") String serviceId) throws SalsaException;

    /**
     * Undeploy and remove all the instances of the services
     *
     * @param serviceId The ID of the service to be removed
     * @return The ID of the service if the remove is successful
     * @throws SalsaException
     */
    @DELETE
    @Path("/services/{serviceId}")
    public Response undeployService(@PathParam("serviceId") String serviceId) throws SalsaException;

    /**
     * Get the XML of the application structure and the configuration states
     *
     * @param serviceID The ID of the service
     * @return The XML that contain the information
     * @throws SalsaException
     */
    @GET
    @Path("/services/{serviceId}")
    @Produces(MediaType.TEXT_XML)
    public Response getService(@PathParam("serviceId") String serviceID) throws SalsaException;

    //////////////////////////////////////////
    // API for the whole service instance
    //////////////////////////////////////////
    /**
     * Deploy one or more instances
     *
     * @param serviceId The ID of the service
     * @param nodeId The ID of the service unit
     * @param quantity
     * @return
     * @throws SalsaException
     */
    @POST
    @Path("/services/{serviceId}/nodes/{nodeId}/instance-count/{quantity}")
    public Response spawnInstance(@PathParam("serviceId") String serviceId,
            @PathParam("nodeId") String nodeId,
            @PathParam("quantity") int quantity) throws SalsaException;

    /**
     * Undeploy one instance
     *
     * @param serviceId The ID of the service
     * @param nodeId The ID of the service unit
     * @param instanceId The ID of the instance, which is an integer
     * @return A message about the status of the undeployment
     * @throws SalsaException
     */
    @DELETE
    @Path("/services/{serviceId}/nodes/{nodeId}/instances/{instanceId}")
    public Response destroyInstance(@PathParam("serviceId") String serviceId,
            @PathParam("nodeId") String nodeId,
            @PathParam("instanceId") int instanceId) throws SalsaException;

    /**
     * Reconfigure an instance. The configuration action is put in the queue to
     * be execute sequentially.
     *
     * @param serviceId The ID of the service
     * @param nodeId The ID of the service unit
     * @param instanceId The ID of the instance, which is an integer
     * @param actionName The name of the action, which is defined in the TOSCA
     * @return A message to indicate the status of the action
     * @throws SalsaException
     */
    @POST
    @Path("/services/{serviceId}/nodes/{nodeId}/instances/{instanceId}/action_queue/{actionName}")
    public Response queueAction(
            @PathParam("serviceId") String serviceId,
            @PathParam("nodeId") String nodeId,
            @PathParam("instanceId") int instanceId,
            @PathParam("actionName") String actionName) throws SalsaException;

    // Note: the parameters are separated by ,    
    @POST
    @Path("/services/{serviceId}/nodes/{nodeId}/instances/{instanceId}/action_queue/{actionName}/parameters/{parameters}")
    public Response queueActionWithParameter(
            @PathParam("serviceId") String serviceId,
            @PathParam("nodeId") String nodeId,
            @PathParam("instanceId") int instanceId,
            @PathParam("actionName") String actionName,
            @PathParam("parameters") String parameters) throws SalsaException;

    @GET
    @Path("/health")
    public String health();
}
