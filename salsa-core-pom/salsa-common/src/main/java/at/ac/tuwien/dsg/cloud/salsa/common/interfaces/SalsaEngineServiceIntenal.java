/*
 * Copyright (c) 2013 Technische Universitat Wien (TUW), Distributed Systems Group. http://dsg.tuwien.ac.at
 *
 * This work was partially supported by the European Commission in terms of the CELAR FP7 project (FP7-ICT-2011-8 #317790), http://www.celarcloud.eu/
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
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
//import org.springframework.stereotype.Service;

import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.ServiceInstance;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.ServiceUnit;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.ServiceUnitRelationship;
import at.ac.tuwien.dsg.cloud.salsa.engine.exception.SalsaException;
import at.ac.tuwien.dsg.cloud.salsa.tosca.extension.SalsaCapaReqString;
import javax.ws.rs.QueryParam;

//@Service
@Path("/")
public interface SalsaEngineServiceIntenal {

    // INTERFACES FOR SALSA USERS TO DEPLOY AND MANAGE THEIR APPLICATION
    /**
     * This service deploys the whole Tosca file. The form which is posted to service must contain all parameters
     *
     * @param uploadedInputStream The file contents
     * @param serviceName The ServiceName. This must be unique in whole system.
     * @return The information
     * @throws at.ac.tuwien.dsg.cloud.salsa.engine.exception.SalsaException
     */
    @PUT
    @Path("/services/{serviceName}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response deployService(@PathParam("serviceName") String serviceName,
            @Multipart("file") InputStream uploadedInputStream) throws SalsaException;

    /**
     * Deploy new service by submitting the XML directly in the data
     *
     * @param uploadedInputStream
     * @return
     * @throws at.ac.tuwien.dsg.cloud.salsa.engine.exception.SalsaException
     */
    @PUT
    @Path("/services/xml")
    @Consumes(MediaType.APPLICATION_XML)
    public Response deployServiceFromXML(String uploadedInputStream) throws SalsaException;

    @POST
    @Path("/services/{serviceId}/redeploy")
    public Response redeployService(@PathParam("serviceId") String serviceId) throws SalsaException;

    /**
     * Remove the whole cloud service
     *
     * @param serviceId
     * @return
     * @throws at.ac.tuwien.dsg.cloud.salsa.engine.exception.SalsaException
     */
    @DELETE
    @Path("/services/{serviceId}")
    public Response undeployService(@PathParam("serviceId") String serviceId) throws SalsaException;

    /**
     * This service deploy a number of service units
     *
     * @param serviceId
     * @param topologyId
     * @param nodeId
     * @param quantity
     * @return
     * @throws at.ac.tuwien.dsg.cloud.salsa.engine.exception.SalsaException
     */
    @POST
    @Path("/services/{serviceId}/topologies/{topologyId}/nodes/{nodeId}/instance-count/{quantity}")
    //@Produces(MediaType.APPLICATION_JSON)
    public Response spawnInstance(@PathParam("serviceId") String serviceId,
            @PathParam("topologyId") String topologyId,
            @PathParam("nodeId") String nodeId,
            @PathParam("quantity") int quantity) throws SalsaException;

    /**
     * This method will destroy an instance, regardless it is a VM or a software
     *
     * @param serviceId
     * @param topologyId
     * @param nodeId
     * @param instanceId
     * @return
     * @throws at.ac.tuwien.dsg.cloud.salsa.engine.exception.SalsaException
     */
    @DELETE
    @Path("/services/{serviceId}/topologies/{topologyId}/nodes/{nodeId}/instances/{instanceId}")
    public Response destroyInstance(@PathParam("serviceId") String serviceId,
            @PathParam("topologyId") String topologyId,
            @PathParam("nodeId") String nodeId,
            @PathParam("instanceId") int instanceId) throws SalsaException;

    @DELETE
    @Path("/services/{serviceId}/nodes/{nodeId}/instances/{instanceId}/metadata")
    public Response removeInstanceMetadata(
            @PathParam("serviceId") String serviceId,
            @PathParam("nodeId") String nodeId,
            @PathParam("instanceId") int instanceId) throws SalsaException;

    /**
     * Get service description in SALSA XML format
     *
     * @param serviceDeployId
     * @return XML document of service
     * @throws at.ac.tuwien.dsg.cloud.salsa.engine.exception.SalsaException
     */
    @GET
    @Path("/services/{serviceId}")
    @Produces(MediaType.TEXT_XML)
    public Response getService(@PathParam("serviceId") String serviceDeployId) throws SalsaException;

    /**
     * Get service description in TOSCA
     *
     * @param serviceDeployId
     * @return XML document of service
     * @throws at.ac.tuwien.dsg.cloud.salsa.engine.exception.SalsaException
     */
    @GET
    @Path("/services/tosca/{serviceId}")
    @Produces(MediaType.TEXT_XML)
    public Response getToscaService(@PathParam("serviceId") String serviceDeployId) throws SalsaException;

    /**
     * Batch deploy all the services in the topology
     *
     * @param serviceId
     * @param topologyId
     * @return
     * @throws SalsaException
     */
    @POST
    @Path("/services/{serviceId}/topologies/{topologyId}")
    //@Produces(MediaType.APPLICATION_JSON)
    public Response deployTopology(@PathParam("serviceId") String serviceId,
            @PathParam("topologyId") String topologyId) throws SalsaException;

    /**
     * Batch undeploy all the services in the topology
     *
     * @param serviceId
     * @param topologyId
     * @return
     * @throws SalsaException
     */
    @DELETE
    @Path("/services/{serviceId}/topologies/{topologyId}")
    //@Produces(MediaType.APPLICATION_JSON)
    public Response undeployTopology(@PathParam("serviceId") String serviceId,
            @PathParam("topologyId") String topologyId) throws SalsaException;

    /**
     * Remove all the instance of a service unit
     *
     * @param serviceId
     * @param topologyId
     * @param nodeId
     * @return
     * @throws SalsaException
     */
    @DELETE
    @Path("/services/{serviceId}/topologies/{topologyId}/nodes/{nodeId}")
    public Response destroyInstanceOfNodeType(@PathParam("serviceId") String serviceId,
            @PathParam("topologyId") String topologyId, @PathParam("nodeId") String nodeId) throws SalsaException;

    // INTERFACES FOR rSYBL
    /**
     * This method does scale out, is used by rSYBL
     *
     * @param serviceId
     * @param nodeId
     * @return
     * @throws at.ac.tuwien.dsg.cloud.salsa.engine.exception.SalsaException
     */
    @POST
    @Path("/services/{serviceId}/nodes/{nodeId}/scaleout")
    public Response scaleOutNode(@PathParam("serviceId") String serviceId,
            @PathParam("nodeId") String nodeId) throws SalsaException;

    /**
     * This method does scale in, is used by rSYBL
     *
     * @param serviceId
     * @param nodeId
     * @return
     * @throws at.ac.tuwien.dsg.cloud.salsa.engine.exception.SalsaException
     */
    @POST
    @Path("/services/{serviceId}/nodes/{nodeId}/scalein")
    public Response scaleInNode(@PathParam("serviceId") String serviceId,
            @PathParam("nodeId") String nodeId) throws SalsaException;

    /**
     * This method do scale in at the VM level, by taking the VM's IP
     *
     * @param serviceId
     * @param vmIp
     * @return
     * @throws at.ac.tuwien.dsg.cloud.salsa.engine.exception.SalsaException
     */
    @POST
    @Path("/services/{serviceId}/vmnodes/{ip}/scalein")
    public Response scaleInVM(@PathParam("serviceId") String serviceId,
            @PathParam("ip") String vmIp) throws SalsaException;

    /**
     * 
     * @param serviceId
     * @param vmIp
     * @return
     * @throws SalsaException 
     */
    @POST
    @Path("/services/{serviceId}/vmnodes/{ip}/scaleout")
    public Response scaleOutVM(@PathParam("serviceId") String serviceId,
            @PathParam("ip") String vmIp) throws SalsaException;

    /**
     * 
     * @param serviceDeployId
     * @return
     * @throws SalsaException 
     */
    @GET
    @Path("/services/tosca/{serviceId}/sybl")
    @Produces(MediaType.TEXT_XML)
    public Response getServiceSYBL_DEP_DESP(@PathParam("serviceId") String serviceDeployId) throws SalsaException;

    // INTERFACE FOR THE CLOUD CONNECTOR AND THE PIONEER TO CONNECT TO
    /**
     * 
     * @return status of the service
     */
    @GET
    @Path("/health")
    public String health();

    /**
     * 
     * @param unit
     * @param serviceId
     * @param topologyId
     * @return
     * @throws SalsaException 
     */
    @POST
    @Path("/services/{serviceId}/topologies/{topologyId}")
    public Response addServiceUnitMetaData(ServiceUnit unit,
            @PathParam("serviceId") String serviceId,
            @PathParam("topologyId") String topologyId) throws SalsaException;

    /**
     * 
     * @param toscaXML
     * @param serviceId
     * @param topologyId
     * @return
     * @throws SalsaException 
     */
    @POST
    @Path("/services/{serviceId}/topologies/{topologyId}/tosca")
    public Response addServiceUnitMetaData(String toscaXML,
            @PathParam("serviceId") String serviceId,
            @PathParam("topologyId") String topologyId) throws SalsaException;

    /**
     * This method add new instance deployment and metadata
     *
     * @param serviceId The exist service
     * @param topologyId Not require at this time, but need to be presented
     * @param nodeId Id of node to be deployed more
     * @param instanceId The defined ID of the instance. if the instanceID existed, update and redeploy instance (not implemented)
     * @return
     * @throws at.ac.tuwien.dsg.cloud.salsa.engine.exception.SalsaException
     *
     */
    @PUT
    @Path("/services/{serviceId}/topologies/{topologyId}/nodes/{nodeId}/instances/{instanceId}")
    public Response deployInstance(@PathParam("serviceId") String serviceId,
            @PathParam("topologyId") String topologyId,
            @PathParam("nodeId") String nodeId,
            @PathParam("instanceId") int instanceId) throws SalsaException;

    /**
     * Add a relationship on the topology
     *
     * @param data
     * @param serviceId
     * @param topologyId
     * @return
     * @throws at.ac.tuwien.dsg.cloud.salsa.engine.exception.SalsaException
     */
    @POST
    @Path("/services/{serviceId}/topologies/{topologyId}/relationship")
    //@Consumes(MediaType.APPLICATION_XML)
    public Response addRelationship(ServiceUnitRelationship data,
            @PathParam("serviceId") String serviceId,
            @PathParam("topologyId") String topologyId) throws SalsaException;

    /**
     * 
     * @param serviceId
     * @param topologyId
     * @param nodeId
     * @param value
     * @return
     * @throws SalsaException 
     */
    @POST
    @Path("/services/{serviceId}/topologies/{topologyId}/nodes/{nodeId}/instance-counter/{value}")
    public Response updateNodeIdCounter(
            @PathParam("serviceId") String serviceId,
            @PathParam("topologyId") String topologyId,
            @PathParam("nodeId") String nodeId,
            @PathParam("value") int value) throws SalsaException;

    /**
     * This PUT the metadata only. It is called by Pioneer which already do deployment.
     *
     * @param data
     * @param serviceId The exist service
     * @param topologyId Not require at this time, but need to be presented
     * @param nodeId Id of node to be deployed more
     * @return
     * @throws at.ac.tuwien.dsg.cloud.salsa.engine.exception.SalsaException
     *
     */
    @POST
    @Path("/services/{serviceId}/topologies/{topologyId}/nodes/{nodeId}/instance-metadata")
    //@Consumes(MediaType.APPLICATION_XML)
    public Response addInstanceUnitMetaData(ServiceInstance data,
            @PathParam("serviceId") String serviceId,
            @PathParam("topologyId") String topologyId,
            @PathParam("nodeId") String nodeId) throws SalsaException;

    /**
     * Update a replica capability.
     *
     * @param data
     * @param serviceId
     * @param topologyId
     * @param nodeId
     * @param instanceId
     * @return
     * @throws at.ac.tuwien.dsg.cloud.salsa.engine.exception.SalsaException
     */
    @POST
    @Path("/services/{serviceId}/topologies/{topologyId}/nodes/{nodeId}/instances/{instanceId}/capability")
    //@Consumes(MediaType.APPLICATION_XML)
    public Response updateInstanceUnitCapability(
            SalsaCapaReqString data,
            @PathParam("serviceId") String serviceId,
            @PathParam("topologyId") String topologyId,
            @PathParam("nodeId") String nodeId,
            @PathParam("instanceId") int instanceId) throws SalsaException;

    /**
     * Update the properties for a replica node instance. Properties is an AnyType Xml object and will be parsed if possible, and add all to the replica.
     *
     * @param data
     * @param serviceId
     * @param topologyId
     * @param nodeId
     * @param instanceId
     * @return
     * @throws at.ac.tuwien.dsg.cloud.salsa.engine.exception.SalsaException
     */
    @POST
    @Path("/services/{serviceId}/topologies/{topologyId}/nodes/{nodeId}/instances/{instanceId}/properties")
    @Consumes(MediaType.TEXT_PLAIN)
    public Response updateInstanceUnitProperties(
            //JAXBElement<Object> data,
            String data,
            @PathParam("serviceId") String serviceId,
            @PathParam("topologyId") String topologyId,
            @PathParam("nodeId") String nodeId,
            @PathParam("instanceId") int instanceId) throws SalsaException;

    /**
     * Update a deployment's state.
     *
     * @param serviceId
     * @param topologyId
     * @param nodeId
     * @param instanceId
     * @param value
     * @param extra
     * @return
     * @throws at.ac.tuwien.dsg.cloud.salsa.engine.exception.SalsaException
     */
    @POST
    @Path("/services/{serviceId}/topologies/{topologyId}/nodes/{nodeId}/instances/{instanceId}/state/{value}")
    public Response updateNodeState(
            @PathParam("serviceId") String serviceId,
            @PathParam("topologyId") String topologyId,
            @PathParam("nodeId") String nodeId,
            @PathParam("instanceId") int instanceId,
            @PathParam("value") String value,
            @QueryParam("extra") String extra) throws SalsaException;

    /**
     * 
     * @param serviceId
     * @param topologyId
     * @param nodeId
     * @param instanceId
     * @param reqId
     * @return
     * @throws SalsaException 
     */
    @GET
    @Path("/services/{serviceId}/topologies/{topologyId}/nodes/{nodeId}/instances/{instanceId}/requirement/{reqId}")
    public Response getRequirementValue(
            @PathParam("serviceId") String serviceId,
            @PathParam("topologyId") String topologyId,
            @PathParam("nodeId") String nodeId,
            @PathParam("instanceId") int instanceId,
            @PathParam("reqId") String reqId) throws SalsaException;

    /**
     * 
     * @param serviceId
     * @param nodeId
     * @param instanceId
     * @param actionName
     * @return
     * @throws SalsaException 
     */
    @POST
    @Path("/services/{serviceId}/nodes/{nodeId}/instances/{instanceId}/action_queue/{actionName}")
    public Response queueAction(
            @PathParam("serviceId") String serviceId,
            @PathParam("nodeId") String nodeId,
            @PathParam("instanceId") int instanceId,
            @PathParam("actionName") String actionName) throws SalsaException;

    
    /**
     * 
     * @param serviceId
     * @param nodeId
     * @param instanceId
     * @return
     * @throws SalsaException 
     */
    @POST
    @Path("/services/{serviceId}/nodes/{nodeId}/instances/{instanceId}/action_unqueue")
    public Response unqueueAction(
            @PathParam("serviceId") String serviceId,
            @PathParam("nodeId") String nodeId,
            @PathParam("instanceId") int instanceId) throws SalsaException;

    /**
     * 
     * @param data
     * @return 
     */
    @POST
    @Path("/log")
    public Response logMessage(String data);

    /**
     * 
     * @param fileName
     * @return
     * @throws SalsaException 
     */
    @GET
    @Path("/artifacts/pioneer/{fileName}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getPioneerArtifact(@PathParam("fileName") String fileName) throws SalsaException;

}
