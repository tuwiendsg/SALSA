/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.salsa.common.interfaces;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author Duc-Hung LE
 */
@Path("/viewgenerator")
public interface ApplicationInfoAPI {

    @GET
    @Path("/cloudservice/json/compact/{serviceId}")
    @Produces(MediaType.TEXT_PLAIN)
    public String getServiceRuntimeJsonTreeCompact(@PathParam("serviceId") String serviceDeployId);

    @GET
    @Path("/cloudservice/json/list")
    @Produces(MediaType.TEXT_PLAIN)
    public String getServiceJsonList();
}
