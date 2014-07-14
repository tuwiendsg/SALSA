package at.ac.tuwien.dsg.cloud.salsa.common.interfaces;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@Path("/")
public interface SalsaPioneerInterface {
	
	@POST
	@Path("/nodes/{nodeID}/instances/{instanceId}")
	String deployNode(@PathParam("nodeID") String nodeID, @PathParam("instanceId") int instanceId);
	
	@DELETE
	@Path("/nodes/{nodeID}/instances/{instanceId}")
	String removeNodeInstance(@PathParam("nodeID") String nodeID, @PathParam("instanceId") int instanceId);
	
	@GET
	@Path("/health")
	String health();
	
	@GET
	@Path("/info")
	String info();
}
