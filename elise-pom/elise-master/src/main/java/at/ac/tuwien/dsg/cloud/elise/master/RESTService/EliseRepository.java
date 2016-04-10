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
     * Get the instance. The DomainInfo is parsing based on the category of the service
     *
     * @param uniqueID
     * @return an unit instance
     */
    @GET
    @Path("/instance/{uniqueID}")
    @Produces(MediaType.APPLICATION_JSON)
    UnitInstance readUnitInstance(@PathParam("uniqueID") String uniqueID);

    /**
     * Get the instance base on some basic metadata, managed by ELISE The metadata can be null
     *
     * @param name
     * @param category
     * @param state
     * @param hostedOnID
     * @return
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
     *
     * @param extra
     * @return
     */
    @POST
    @Path("/instance/querymeta")
    @Produces(MediaType.APPLICATION_JSON)
    Set<UnitInstance> readUnitInstanceByExtension(List<ExtensibleModel> extra);

    /**
     * Add new or update an instance
     *
     * @param unitInstance
     * @return the ID of the added instance, or null if failed
     */
    @POST
    @Path("/instance")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    UnitInstance saveUnitInstance(UnitInstance unitInstance);

    @POST
    @Path("/instance/relationship/hoston")
    @Consumes(MediaType.APPLICATION_JSON)
    void saveRelationshipHostOn(HostOnRelationshipInstance hostOnRela);

    @POST
    @Path("/instance/relationship/connectto")
    @Consumes(MediaType.APPLICATION_JSON)
    void saveRelationshipConnectTo(ConnectToRelationshipInstance connectToRela);

    /**
     * Delete unit by ID
     *
     * @param uniqueID
     */
    @DELETE
    @Path("/instance/{uniqueID}")
    void deleteUnitInstance(@PathParam("uniqueID") String uniqueID);

    /**
     * Filter unit instance by an ID
     *
     * @param query A filter to query the result
     * @return a set of unit instancess
     */
    @POST
    @Path("/instance/query")
    @Consumes(MediaType.APPLICATION_JSON)
    public Set<UnitInstance> query(EliseQuery query);

    /**
     * CRUD for providers
     */
    @GET
    @Path("/provider/{uniqueID}")
    @Produces(MediaType.APPLICATION_JSON)
    Provider readProvider(@PathParam("uniqueID") String uniqueID);

    @GET
    @Path("/provider")
    @Produces(MediaType.APPLICATION_JSON)
    Set<Provider> readAllProviders();

    @POST
    @Path("/provider")
    @Consumes(MediaType.APPLICATION_JSON)
    String saveProvider(Provider provider);

    @DELETE
    @Path("/provider/{uniqueID}")
    @Produces(MediaType.APPLICATION_JSON)
    void deleteProvider(@PathParam("uniqueID") String uniqueID);

    /**
     * CRUD for service template
     */
    @GET
    @Path("/servicetemplate/{uniqueID}")
    @Produces(MediaType.APPLICATION_JSON)
    ServiceTemplate readServiceTemplate(@PathParam("uniqueID") String uniqueID);

    @GET
    @Path("/servicetemplate")
    @Produces(MediaType.APPLICATION_JSON)
    Set<ServiceTemplate> readAllServiceTemplates();

    @POST
    @Path("/servicetemplate")
    @Consumes(MediaType.APPLICATION_JSON)
    String saveServiceTemplate(ServiceTemplate provider);

    @DELETE
    @Path("/servicetemplate/{uniqueID}")
    @Produces(MediaType.APPLICATION_JSON)
    String deleteServiceTemplate(@PathParam("uniqueID") String uniqueID);

    /**
     * ARTIFACT REPOSITORY
     */
    @GET
    @Path("/artifact/")
    @Produces(MediaType.APPLICATION_JSON)
    Set<Artifact> readArtifact(
            @QueryParam("name") String name,
            @QueryParam("version") String version,
            @QueryParam("type") String type);

    @POST
    @Path("/artifact")
    @Consumes(MediaType.APPLICATION_JSON)
    Artifact saveArtifact(Artifact artifact);

    @DELETE
    @Path("/artifact")
    @Consumes(MediaType.APPLICATION_JSON)
    void deleteArtifact(Artifact artifact);

    /**
     * SOME STATIC INFORMATION *
     */
    /**
     * Return a list of supported service unit types.
     *
     * @return A list of categories of service units
     */
    @GET
    @Path("/categories")
    public Set<String> getUnitCategory();

    @GET
    @Path("/health")
    public String health();

}
