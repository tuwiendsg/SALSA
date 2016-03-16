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

import at.ac.tuwien.dsg.cloud.elise.model.relationships.ConnectToRelationshipInstance;
import at.ac.tuwien.dsg.cloud.elise.model.relationships.HostOnRelationshipInstance;
import at.ac.tuwien.dsg.cloud.elise.model.runtime.UnitInstance;
import at.ac.tuwien.dsg.cloud.salsa.messaging.model.Elise.EliseQuery;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import java.util.Set;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.codehaus.jackson.annotate.JsonSubTypes.Type;

/**
 * The APIs enables to read/write/search unit instances in ELISE. These APIs only query information in the database without any execution for collecting real
 * information. For the information collecting, please use communication service APIs.
 *
 * @author Duc-Hung Le
 */
@Path("/unitinstance")
@JsonSubTypes({
    @JsonSubTypes.Type(value = EliseQuery.class, name = "_EliseQuery"),
    @JsonSubTypes.Type(value = Set.class, name = "_Set")
})
public interface UnitInstanceInfoManagement {

    /* Service instance management */

    /**
     * Get all the instance in database. Note: the number of instance could be very large.
     *
     * @return a set of unit instances
     */
    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    Set<UnitInstance> getUnitInstanceList();

    /**
     * Get the instance. The DomainInfo is parsing based on the category of the service
     *
     * @param uniqueID
     * @return an unit instance
     */
    @GET
    @Path("/{uniqueID}")
    @Produces(MediaType.APPLICATION_JSON)
    UnitInstance getUnitInstanceByID(@PathParam("uniqueID") String uniqueID);

    /**
     * Get the instance which the DomainInfo contains full software stack
     *
     * @param uniqueID
     * @return an unit instance with DomainInfoFullstack
     */
    @GET
    @Path("/{uniqueID}/fullstack")
    @Produces(MediaType.APPLICATION_JSON)
    UnitInstance getUnitInstanceByIDFullStack(@PathParam("uniqueID") String uniqueID);

    /**
     * Get the instance. The DomainInfo is parsing based on the category of the service
     *
     * @param unitName
     * @return an unit instance
     */
    @GET
    @Path("/name/{unitName}")
    @Produces(MediaType.APPLICATION_JSON)
    UnitInstance getUnitInstanceFirstByName(@PathParam("unitName") String unitName);

    /**
     * Get the instance which the DomainInfo contains full software stack
     *
     * @param unitName
     * @return an unit instance with DomainInfoFullstack
     */
    @GET
    @Path("/name/{unitName}/fullstack")
    @Produces(MediaType.APPLICATION_JSON)
    UnitInstance getUnitInstanceFirstByNameFullStack(@PathParam("unitName") String unitName);

    /**
     * Add an instance
     *
     * @param unitInstance
     * @return the ID of the added instance, or null if failed
     */
    @POST
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    String addUnitInstance(UnitInstance unitInstance);
    
    @POST
    @Path("/relationship/hoston")
    @Consumes(MediaType.APPLICATION_JSON)
    void addRelationshipHostOn(HostOnRelationshipInstance hostOnRela);
    
    
    @POST
    @Path("/relationship/connectto")
    @Consumes(MediaType.APPLICATION_JSON)
    void addRelationshipConnectTo(ConnectToRelationshipInstance connectToRela);

    /**
     * Delete unit by ID
     *
     * @param uniqueID
     */
    @DELETE
    @Path("/{uniqueID}")
    void deleteUnitInstanceByID(@PathParam("uniqueID") String uniqueID);

    /**
     * Filter unit instance by an ID
     *
     * @param query A filter to query the result
     * @return a set of unit instancess
     */
    @POST
    @Path("/query")
    @Consumes(MediaType.APPLICATION_JSON)
    public Set<UnitInstance> queryUnitInstance(EliseQuery query);

    /**
     * Return a list of supported service unit types.
     *
     * @return A list of categories of service units
     */
    @POST
    @Path("/categories")
    @Consumes(MediaType.APPLICATION_JSON)
    public Set<String> getUnitCategory();

}
