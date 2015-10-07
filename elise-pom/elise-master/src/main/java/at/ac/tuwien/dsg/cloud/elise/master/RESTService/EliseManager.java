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

import at.ac.tuwien.dsg.cloud.elise.model.runtime.GlobalIdentification;
import at.ac.tuwien.dsg.cloud.elise.model.runtime.LocalIdentification;
import at.ac.tuwien.dsg.cloud.elise.collectorinterfaces.models.ConductorDescription;
import java.util.List;
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
 * For managing distributed components of ELISE
 * @author Duc-Hung Le
 */
@Path("/manager")
public interface EliseManager {

    // for collector
    @POST
    @Path("/conductor")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String registerConductor(ConductorDescription paramCollectorDescription);

    @PUT
    @Path("/conductor")
    @Consumes(MediaType.APPLICATION_JSON)
    public String updateConductor(ConductorDescription paramCollectorDescription);

    @GET
    @Path("/conductor/{conductorID}")
    @Produces(MediaType.APPLICATION_JSON)
    public ConductorDescription getConductor(@PathParam("conductorID") String paramString);

    @DELETE
    @Path("/conductor/{conductorID}")
    @Produces(MediaType.TEXT_PLAIN)
    public String removeConductor(@PathParam("conductorID") String paramString);

    @GET
    @Path("/conductor")
    @Produces(MediaType.APPLICATION_JSON)
    public List<ConductorDescription> getConductorList();    
        
    /**
     * To request the pioneer to run a conductor.
     * If the pioneerID=salsa, this will run conductor directly by ELISE here
     * @param pioneerID 
     */
    @POST
    @Path("/conductor/salsa/{pioneerID}")
    public void runConductorViaSalsa(@PathParam("conductorID") String pioneerID);    
    
    @POST
    @Path("/conductor/{conductorID}/collector/{collectorName}")
    @Consumes(MediaType.TEXT_PLAIN)
    public void pushCollectorToConductor(String configuration, @PathParam("conductorID") String conductorID, @PathParam("collectorName") String collectorName);
    
    
    @GET
    @Path("/collector/{collectorName}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getCollectorArtifact(@PathParam("collectorName") String collectorName);
    
    
    @POST
    @Path("/conductor/resyn")
    @Produces(MediaType.APPLICATION_JSON)
    public void ResynConductors();
    

    /**
     * Update a identification to the identification database
     * As the global ID is assign by SALSA, the possibleGlobalID is for the creation of new GlobalID
     * @param si
     * @param possibleGlobalID
     * @return 
     */    
    @POST
    @Path("/identification/{possibleGlobalID}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public GlobalIdentification updateComposedIdentification(LocalIdentification si, @PathParam("possibleGlobalID") String possibleGlobalID);

    @GET
    @Path("/query/{queryUUID}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getQueryInformation(@PathParam("queryUUID") String queryUUID);

    // health and generic stuff
    @GET
    @Path("/health")
    public String health();

    @POST
    @Path("/clean")
    public String cleanDB();

}
