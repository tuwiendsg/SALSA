/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.elise.master.RESTInterface;

import at.ac.tuwien.dsg.cloud.elise.model.elasticunit.runtime.UnitInstance;
import at.ac.tuwien.dsg.cloud.salsa.messaging.model.Elise.EliseQuery;
import java.util.Set;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author Duc-Hung Le
 */
@Path("/unitinstance")
public interface UnitInstanceDAO {
    /* Service instance management */

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    Set<UnitInstance> getUnitInstanceList();
    
    @GET
    @Path("/{uniqueID}")
    @Produces(MediaType.APPLICATION_JSON)
    UnitInstance getUnitInstanceByID(@PathParam("uniqueID")String uniqueID);

    @POST
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    String addUnitInstance(UnitInstance unitInstance);

    /**
     * This for querying instances by running collector
     * @param query
     * @return 
     */
    @POST
    @Path("/query")
    @Consumes(MediaType.APPLICATION_JSON)
    public Set<UnitInstance> queryUnitInstance(EliseQuery query);
    
    /**
     * This for querying instances without running collector, get existing data
     * @param query
     * @return 
     */
    @POST
    @Path("/queryDB")
    @Consumes(MediaType.APPLICATION_JSON)
    public Set<UnitInstance> queryUnitInstanceDB(EliseQuery query);
    
    
    @POST
    @Path("/categories")
    @Consumes(MediaType.APPLICATION_JSON)
    public Set<String> getUnitCategory();
    
}
