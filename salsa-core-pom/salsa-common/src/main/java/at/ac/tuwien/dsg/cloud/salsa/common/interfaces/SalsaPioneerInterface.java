package at.ac.tuwien.dsg.cloud.salsa.common.interfaces;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.ws.rs.PathParam;

@WebService
public interface SalsaPioneerInterface {
	
	@WebMethod
	String deployNode(@PathParam("nodeID") String nodeID, @PathParam("instanceId") int instanceId);
	
	@WebMethod
	String removeNodeInstance(@PathParam("nodeID") String nodeID, @PathParam("instanceId") int instanceId);
	
	@WebMethod
	String health();
}
