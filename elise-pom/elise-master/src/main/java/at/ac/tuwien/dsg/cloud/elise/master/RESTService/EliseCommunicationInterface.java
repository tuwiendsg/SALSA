/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.elise.master.RESTService;

import at.ac.tuwien.dsg.cloud.salsa.messaging.model.Elise.EliseQuery;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author hungld
 */
@Path("/communication")
public interface EliseCommunicationInterface {

    @GET
    @Path("/count")
    public String count();

    @POST
    @Path("/queryUnitInstance")
    @Consumes(MediaType.APPLICATION_JSON)
    public String querySetOfInstance(EliseQuery query,
            @DefaultValue("false") @QueryParam("isUpdated") final boolean isUpdated,
            @DefaultValue("false") @QueryParam("notify") final boolean isNotified);
    
    @POST
    @Path("/queryUnitInstance_singleTime")
    @Consumes(MediaType.APPLICATION_JSON)
    public String querySetOfInstances(EliseQuery query);

    @GET
    @Path("/query/{queryUUID}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getQueryProcessStatus(String queryUUID);
}
