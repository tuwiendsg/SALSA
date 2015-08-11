/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.elise.master.RESTInterface;

import at.ac.tuwien.dsg.cloud.elise.model.elasticunit.identification.GlobalIdentification;
import at.ac.tuwien.dsg.cloud.elise.model.elasticunit.identification.LocalIdentification;
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
 *
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

//  @POST
//  @Path("/data")
//  @Consumes({"application/json"})
//  public abstract String recievedCollectorData(CollectorData paramCollectorData);
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
