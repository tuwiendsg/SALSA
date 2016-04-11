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
package at.ac.tuwien.dsg.cloud.elise.master.RESTService;

import at.ac.tuwien.dsg.cloud.elise.model.provider.Artifact;
import at.ac.tuwien.dsg.cloud.elise.model.provider.Provider;
import at.ac.tuwien.dsg.cloud.elise.model.provider.ServiceTemplate;
import at.ac.tuwien.dsg.cloud.elise.model.relationships.ConnectToRelationshipInstance;
import at.ac.tuwien.dsg.cloud.elise.model.relationships.HostOnRelationshipInstance;
import at.ac.tuwien.dsg.cloud.elise.model.runtime.UnitInstance;
import at.ac.tuwien.dsg.cloud.salsa.domainmodels.ExtensibleModel;
import at.ac.tuwien.dsg.cloud.salsa.domainmodels.IaaS.VirtualMachineInfo;
import at.ac.tuwien.dsg.cloud.salsa.messaging.model.Elise.EliseQuery;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * The APIs enables to read/write/search unit instances in ELISE. These APIs only query information in the database without any execution for collecting real
 * information. For the information collecting, please use communication service APIs.
 *
 * @author Duc-Hung Le
 */
@Path("/")
@JsonSubTypes({
    @JsonSubTypes.Type(value = EliseQuery.class, name = "_EliseQuery"),
    @JsonSubTypes.Type(value = UnitInstance.class, name = "_UnitInstance"),
    @JsonSubTypes.Type(value = Set.class, name = "_Set"),
    @JsonSubTypes.Type(value = Map.class, name = "_Map"),
    @JsonSubTypes.Type(value = VirtualMachineInfo.class, name = "_VirtualMachineInfo"),
    @JsonSubTypes.Type(value = ExtensibleModel.class, name = "_ExtensibleModel")

})
@Configurable
public interface EliseRepository {

    /* UNIT INSTANCE MANAGEMENT */
    /**
     * Read an unit instance from the database and all its extensions
     *
     * @param uniqueID The uuid of the unit
     * @return An unit instance object
     */
    @GET
    @Path("/instance/{uniqueID}")
    @Produces(MediaType.APPLICATION_JSON)
    UnitInstance readUnitInstance(@PathParam("uniqueID") String uniqueID);

    /**
     * Get the unit instance base on some basic metadata, managed by ELISE.
     * The metadata can be null if not available.
     *
     * @param name Name of the service instance, e.g. LoadBalancerUnit
     * @param category The category, e.g. docker
     * @param state The state, e.g. to get all "error" instance
     * @param hostedOnID To find all instances which is hosted by an instance
     * @return A list of unit instances.
     */
    @GET
    @Path("/instance/")
    @Produces(MediaType.APPLICATION_JSON)
    Set<UnitInstance> readAllUnitInstances(
            @QueryParam("name") String name,
            @QueryParam("category") String category,
            @QueryParam("state") String state,
            @QueryParam("hostedOnID") String hostedOnID);

    /**
     * Get the list of instance with external model info, e.g. location at xyz
     * The matching of extended info is similar search.
     * @param extra The template of extra information
     * @return A list of unit instances with similar extra info.
     */
    @POST
    @Path("/instance/querymeta")
    @Produces(MediaType.APPLICATION_JSON)
    Set<UnitInstance> readUnitInstanceByExtension(List<ExtensibleModel> extra);

    /**
     * Add new or update an unit instance.
     *
     * @param unitInstance The instance to be saved
     * @return The object of the instance include DB id, or null if failed
     */
    @POST
    @Path("/instance")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    UnitInstance saveUnitInstance(UnitInstance unitInstance);

    /**
     * To save a relationship between two instances
     * @param hostOnRela The relationship type
     */
    @POST
    @Path("/instance/relationship/hoston")
    @Consumes(MediaType.APPLICATION_JSON)
    void saveRelationshipHostOn(HostOnRelationshipInstance hostOnRela);

    /**
     * To save a relationship between two instances
     * @param connectToRela The relationship type
     */
    @POST
    @Path("/instance/relationship/connectto")
    @Consumes(MediaType.APPLICATION_JSON)
    void saveRelationshipConnectTo(ConnectToRelationshipInstance connectToRela);

    /**
     * Delete unit instance by ID
     *
     * @param uniqueID The ID of the unit
     */
    @DELETE
    @Path("/instance/{uniqueID}")
    void deleteUnitInstance(@PathParam("uniqueID") String uniqueID);

    /**
     * Filter unit instance by an ID
     *
     * @param query A filter to query the result.
     * This functions will be replated by the readUnitInstanceByExtension function
     * @return A set of unit instancess
     */
    @POST
    @Path("/instance/query")
    @Consumes(MediaType.APPLICATION_JSON)
    public Set<UnitInstance> query(EliseQuery query);

    /** CRUD for providers **/
    
    /**
     * Read information about provider
     * @param uniqueID the uuid of provider
     * @return An provider object
     */
    @GET
    @Path("/provider/{uniqueID}")
    @Produces(MediaType.APPLICATION_JSON)
    Provider readProvider(@PathParam("uniqueID") String uniqueID);

    /**
     * Get a list of all available provider
     * @return A list of providers
     */
    @GET
    @Path("/provider")
    @Produces(MediaType.APPLICATION_JSON)
    Set<Provider> readAllProviders();

    /**
     * To save a provider
     * @param provider the information
     * @return the assigned id of provider 
     */
    @POST
    @Path("/provider")
    @Consumes(MediaType.APPLICATION_JSON)
    String saveProvider(Provider provider);

    /**
     * Delete information of provider from DB
     * @param uniqueID The id of the provider to be deleted
     */
    @DELETE
    @Path("/provider/{uniqueID}")
    @Produces(MediaType.APPLICATION_JSON)
    void deleteProvider(@PathParam("uniqueID") String uniqueID);

    /** CRUD for service template  **/
    
    /**
     * To read service template     
     * @param uniqueID The uuid of the template
     * @return an service template object
     */
    @GET
    @Path("/servicetemplate/{uniqueID}")
    @Produces(MediaType.APPLICATION_JSON)
    ServiceTemplate readServiceTemplate(@PathParam("uniqueID") String uniqueID);

    /**
     * Get all the list of service template
     * @return A set of service templates
     */
    @GET
    @Path("/servicetemplate")
    @Produces(MediaType.APPLICATION_JSON)
    Set<ServiceTemplate> readAllServiceTemplates();

    /**
     * Create new or update a service template
     * @param serviceTemplate the service template to be saved
     * @return The object with ID from the DB
     */
    @POST
    @Path("/servicetemplate")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    ServiceTemplate saveServiceTemplate(ServiceTemplate serviceTemplate);

    /**
     * Delete the service template from DB
     * @param uniqueID The uuid of the template
     */
    @DELETE
    @Path("/servicetemplate/{uniqueID}")
    void deleteServiceTemplate(@PathParam("uniqueID") String uniqueID);

    /**  ARTIFACT REPOSITORY **/
    /**
     * Read the artifact information. The parameters are optional.
     * @param name E.g. haproxy.sh
     * @param version E.g. v1.0
     * @param type E.g. shellscript
     * @return The list of the artifacts
     */
    @GET
    @Path("/artifact/")
    @Produces(MediaType.APPLICATION_JSON)
    Set<Artifact> readArtifact(
            @QueryParam("name") String name,
            @QueryParam("version") String version,
            @QueryParam("type") String type);

    /**
     * To create new or update artifacts
     * @param artifact the information
     * @return the object from DB
     */
    @POST
    @Path("/artifact")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    Artifact saveArtifact(Artifact artifact);

    /**
     * To delete the artifact
     * @param artifact The artifact object
     */
    @DELETE
    @Path("/artifact")
    @Consumes(MediaType.APPLICATION_JSON)
    void deleteArtifact(Artifact artifact);

    /**
     * SOME STATIC INFORMATION *
     */
    
    /**
     * Return a list of supported service unit types. 
     * This information is static in SALS
     * @return A list of categories of service units
     */
    @GET
    @Path("/categories")
    public Set<String> getUnitCategory();

    /**
     * To check the availability of this RESTful API. Use for testing.
     * @return A quick message
     */
    @GET
    @Path("/health")
    public String health();

}
