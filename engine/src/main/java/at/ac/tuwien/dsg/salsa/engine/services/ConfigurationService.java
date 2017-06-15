/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.salsa.engine.services;

import at.ac.tuwien.dsg.salsa.model.salsa.info.SalsaConfigureResult;
import at.ac.tuwien.dsg.salsa.model.salsa.info.SalsaException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Info;
import io.swagger.annotations.License;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
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
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Api(value = "Salsa Engine API")
@SwaggerDefinition(
        info = @Info(
                title = "Salsa Engine API",
                version = "3.0",
                description = "This API provides functions manage cloud services and salsa system.",
                license = @License(name = "Apache 2.0", url = "https://www.apache.org/licenses/LICENSE-2.0")),
        tags = @Tag(name = "Public", description = "This API is for public usage"),
        schemes = (SwaggerDefinition.Scheme.HTTP),
        consumes = {"application/json"},
        produces = {"application/json"}
)
public interface ConfigurationService {

    //////////////////////////////////////////
    // WHOLE CLOUD SERVICE API
    //////////////////////////////////////////
    /**
     * Submit a service description including a Salsafile.yml and capabilities.
     *
     * @param serviceName
     * @return The ID of the service
     * @throws SalsaException
     */
    @PUT
    @Path("/services/{serviceName}")
    @ApiOperation(value = "Initiate the service on Salsa by reading Salsafile and checking package.",
            notes = "This method assumes that service data is uploaded (Salsafile, capability).",
            response = Response.class,
            responseContainer = "String")
    public Response initServiceFiles(@PathParam("serviceName") String serviceName) throws SalsaException;

    /**
     * Create a cloud service from a list of pioneers. This will be used for
     * deployed other services.
     *
     * @param pioneerIDs list of PioneerID separated by space
     * @param serviceName name of the service to be created
     * @return service id
     * @throws SalsaException If pioneer is not registered
     */
    @PUT
    @Path("/services/pioneerfarm/{serviceName}")
    @Consumes(MediaType.TEXT_PLAIN)
    @ApiOperation(value = "Create a service from a list of pioneers.",
            notes = "A set of UUID is provided, separated by space.",
            response = Response.class,
            responseContainer = "String")
    public Response deployServiceFromPioneers(String pioneerIDs, @PathParam("serviceName") String serviceName) throws SalsaException;

    /**
     * Redeploy the service
     *
     * @param serviceId The ID of the service to be redeployed
     * @return The ID of the service if the redeployment is successful
     * @throws SalsaException
     */
    @POST
    @Path("/services/{serviceId}/redeploy")
    @ApiOperation(value = "Undeploy and deploy again a service.",
            notes = "The service must be deployed",
            response = Response.class,
            responseContainer = "String")
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
    @ApiOperation(value = "Undeploy a service.",
            notes = "The service will be deleted.",
            response = Response.class,
            responseContainer = "String")
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
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Get data of a service.",
            notes = "The data is in JSON format.",
            response = Response.class,
            responseContainer = "String")
    public Response getService(@PathParam("serviceId") String serviceID) throws SalsaException;

    @GET
    @Path("/services/list")
    @Produces(MediaType.TEXT_PLAIN)
    @ApiOperation(value = "Get list of service UUID.",
            notes = "Service UUIDs are separated by spaces",
            response = Response.class,
            responseContainer = "String")
    public String getServiceNames();

    //////////////////////////////////////////
    // SERVICE UNIT API
    //////////////////////////////////////////
    @PUT
    @Path("/services/{serviceId}/nodes/{nodeId}")
    @ApiOperation(value = "Update unit data.",
            notes = "Is used by SALSA Pioneer.",
            response = Response.class,
            responseContainer = "String")
    public Response updateUnitMeta(
            String metadata,
            @PathParam("serviceId") String serviceId,
            @PathParam("nodeId") String nodeId) throws SalsaException;

    /**
     * Deploy one instance. Deployment mean setup all artifacts and run init
     * capability if available.
     *
     * @param serviceId The ID of the service
     * @param nodeId The ID of the service unit
     * @param quantity
     * @return
     * @throws SalsaException
     */
    @POST
    @Path("/services/{serviceId}/nodes/{nodeId}")
    @ApiOperation(value = "Deploy new Service Instance of a Service Unit.",
            notes = "The service unit must be register first",
            response = Response.class,
            responseContainer = "String")
    public Response deployInstance(@PathParam("serviceId") String serviceId,
            @PathParam("nodeId") String nodeId) throws SalsaException;

    //////////////////////////////////////////
    // SERVICE INSTANCE API
    //////////////////////////////////////////
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
    @ApiOperation(value = "Delete a Service Instance.",
            notes = "The undeployment script will be executed.",
            response = Response.class,
            responseContainer = "String")
    public Response destroyInstance(@PathParam("serviceId") String serviceId,
            @PathParam("nodeId") String nodeId,
            @PathParam("instanceId") int instanceId) throws SalsaException;

    @PUT
    @Path("/services/{serviceId}/nodes/{nodeId}/instances/{instanceId}")
    @ApiOperation(value = "Change state of a Service Instance.",
            notes = "Is used by SALSA Pioneer",
            response = Response.class,
            responseContainer = "String")
    public Response updateInstanceState(String json,
            @PathParam("serviceId") String serviceId,
            @PathParam("nodeId") String nodeId,
            @PathParam("instanceId") int instanceId);

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
    @ApiOperation(value = "Request to execute an action on the Service Instance.",
            notes = "The action is sent to the queue of the pioneer.",
            response = Response.class,
            responseContainer = "String")
    public Response queueAction(
            @PathParam("serviceId") String serviceId,
            @PathParam("nodeId") String nodeId,
            @PathParam("instanceId") int instanceId,
            @PathParam("actionName") String actionName) throws SalsaException;

    // Note: the parameters are separated by ,    
    @POST
    @Path("/services/{serviceId}/nodes/{nodeId}/instances/{instanceId}/action_queue/{actionName}/parameters/{parameters}")
    @ApiOperation(value = "Request to execute an action on the Service Instance with parameters.",
            notes = "Is used by SALSA Pioneer.",
            response = Response.class,
            responseContainer = "String")
    public Response queueActionWithParameter(
            @PathParam("serviceId") String serviceId,
            @PathParam("nodeId") String nodeId,
            @PathParam("instanceId") int instanceId,
            @PathParam("actionName") String actionName,
            @PathParam("parameters") String parameters) throws SalsaException;

    @GET
    @Path("/healthcheck")
    @Produces(MediaType.TEXT_PLAIN)
    @ApiOperation(value = "Check if the SALSA Engine is running.",
            notes = "To check if the RESTful API is working properly.",
            response = Response.class,
            responseContainer = "String")
    public String health();
}
