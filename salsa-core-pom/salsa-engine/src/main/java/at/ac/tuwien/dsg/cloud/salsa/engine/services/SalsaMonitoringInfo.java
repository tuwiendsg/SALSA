package at.ac.tuwien.dsg.cloud.salsa.engine.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBException;

import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.CloudService;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.ServiceInstance;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.ServiceUnit;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.enums.SalsaEntityType;
import at.ac.tuwien.dsg.cloud.salsa.common.processing.SalsaXmlDataProcess;
import at.ac.tuwien.dsg.cloud.salsa.engine.utils.EngineLogger;
import at.ac.tuwien.dsg.cloud.salsa.engine.utils.SalsaConfiguration;
import at.ac.tuwien.dsg.cloud.salsa.tosca.extension.SalsaInstanceDescription_Service;
import at.ac.tuwien.dsg.cloud.salsa.tosca.extension.SalsaInstanceDescription_VM;


@Path("/monitor")
public class SalsaMonitoringInfo {
	
	@GET
	@Path("/services/{serviceId}/topologies/{topologyId}/nodes/{nodeId}/instances/{instanceId}")
	@Produces(MediaType.APPLICATION_XML)
	public Response getMonitorOfInstance(@PathParam("serviceId") String serviceId,
			@PathParam("topologyId") String topologyId,
			@PathParam("nodeId") String nodeId,
			@PathParam("instanceId") String instanceId){
		try{
			String serviceFile = SalsaConfiguration.getServiceStorageDir()+"/"+serviceId + ".data";

			CloudService service = SalsaXmlDataProcess.readSalsaServiceFile(serviceFile);
			ServiceUnit node = service.getComponentById(topologyId, nodeId);
			ServiceInstance instance = service.getInstanceById(topologyId, nodeId, Integer.parseInt(instanceId));
			
			
			// in case of VM, fetch ganglia information and show up
			if (node.getType().equals(SalsaEntityType.OPERATING_SYSTEM.getEntityTypeString())){
				SalsaInstanceDescription_VM pros = (SalsaInstanceDescription_VM) instance.getProperties().getAny();
				return getVMInformation(pros);
			} else {
				
			}
		} catch (IOException e1){
			EngineLogger.logger.debug(e1.toString());
			return Response.status(500).entity("<Error>Internal error. Cannot read the service file !</Error>").build();
		} catch (JAXBException e2) {
			EngineLogger.logger.debug(e2.toString());
			return Response.status(500).entity("<Error>Internal error. Cannot parse the service file !</Error>").build();
		}

				
		return null;
	}
	
	
	
	private Response getVMInformation(SalsaInstanceDescription_VM pros){
		
			String ip = pros.getPrivateIp();
			EngineLogger.logger.debug("Querying ganglia information and return");
			ProcessBuilder pb = new ProcessBuilder("/usr/bin/telnet",ip,"8649");
			try {
				Process p = pb.start();
				p.waitFor();
	
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(p.getInputStream()));
				String output = "";
				boolean writing = false;
				
				String line = reader.readLine();
				//line = reader.readLine(); line = reader.readLine(); line = reader.readLine(); // 3 first line of telnet command
				// we write out the HOST information of GANGLIA_XML
				while (line != null) {
					if (line.contains("<HOST") && line.contains("IP=\"" + pros.getPrivateIp() +"\"")){
						writing = true;
					}
					if (writing){
						output += line;
					}
					if (writing && line.contains("</HOST>")){
						writing = false;
					}
									
					line = reader.readLine();						
				}
				EngineLogger.logger.debug(output);
				
				return Response.status(200).entity(output).build();
		} catch (IOException e) {
			EngineLogger.logger.debug(e.toString());
			return Response.status(500).entity("<Error>Error when query monitoring information !</Error>").build();				
		
		} catch (InterruptedException e1){
			EngineLogger.logger.debug(e1.toString());
			return Response.status(500).entity("Error when execute command !").build();
		}
	}
	
	
}
