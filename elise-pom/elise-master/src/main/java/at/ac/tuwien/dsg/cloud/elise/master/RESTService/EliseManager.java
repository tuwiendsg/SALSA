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
import at.ac.tuwien.dsg.cloud.salsa.messaging.model.Elise.ConductorDescription;
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

/**
 * For managing distributed components of ELISE
 * @author Duc-Hung Le
 */
@Path("/manager")
public interface EliseManager {

    // for collector
    @POST
    @Path("/collector")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String registerConductor(ConductorDescription paramCollectorDescription);

    @PUT
    @Path("/collector")
    @Consumes(MediaType.APPLICATION_JSON)
    public String updateConductor(ConductorDescription paramCollectorDescription);

    @GET
    @Path("/collector{collectorID}")
    @Produces(MediaType.APPLICATION_JSON)
    public ConductorDescription getConductor(@PathParam("collectorID") String paramString);

    @DELETE
    @Path("/collector{collectorID}")
    @Produces(MediaType.TEXT_PLAIN)
    public String removeConductor(@PathParam("collectorID") String paramString);

    @GET
    @Path("/collector")
    @Produces(MediaType.APPLICATION_JSON)
    public List<ConductorDescription> getCollectorList();

    // for identification
    @POST
    @Path("/identification")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    GlobalIdentification updateComposedIdentification(LocalIdentification si);

    @GET
    @Path("/query/{queryUUID}")
    @Produces(MediaType.APPLICATION_JSON)
    String getQueryInformation(@PathParam("queryUUID") String queryUUID);

    // health and generic stuff
    @GET
    @Path("/health")
    String health();

    @POST
    @Path("/clean")
    String cleanDB();

}
