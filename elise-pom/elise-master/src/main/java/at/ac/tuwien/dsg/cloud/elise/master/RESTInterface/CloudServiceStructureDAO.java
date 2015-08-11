/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.elise.master.RESTInterface;

import at.ac.tuwien.dsg.cloud.elise.model.elasticunit.structure.CloudService;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author Duc-Hung Le
 */
@Path("/cloudservice")
public interface CloudServiceStructureDAO{
    /* Cloud service structure management */
    @POST
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    String addCloudServiceStructure(CloudService serviceStructure);

    @GET
    @Path("/{uniqueID}")
    String getCloudServiceStructureByID(@PathParam("uniqueID") String id);
}
