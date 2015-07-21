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

import org.springframework.stereotype.Service;

import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.CloudService;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.ServiceInstance;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.ServiceUnit;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.enums.SalsaEntityType;
import at.ac.tuwien.dsg.cloud.salsa.common.processing.SalsaXmlDataProcess;
import at.ac.tuwien.dsg.cloud.salsa.engine.utils.ActionIDManager;
import at.ac.tuwien.dsg.cloud.salsa.engine.utils.EngineLogger;
import at.ac.tuwien.dsg.cloud.salsa.engine.utils.PioneerManager;
import at.ac.tuwien.dsg.cloud.salsa.engine.utils.SalsaConfiguration;
import at.ac.tuwien.dsg.cloud.salsa.tosca.extension.SalsaInstanceDescription_VM;

@Service
@Path("/monitor")
public class SalsaMonitoringInfo {
    
    
    @GET
    @Path("/pioneers/cache")
    public String getPioneers(){
        EngineLogger.logger.debug("Getting pioneer");
        return PioneerManager.describe();        
    }
    
    @GET
    @Path("/actions/cache")
    public String getActions(){
        return ActionIDManager.describe();
    }
    
    

    @GET
    @Path("/services/{serviceId}/topologies/{topologyId}/nodes/{nodeId}/instances/{instanceId}")
    @Produces(MediaType.APPLICATION_XML)
    public Response getMonitorOfInstance(@PathParam("serviceId") String serviceId,
            @PathParam("topologyId") String topologyId,
            @PathParam("nodeId") String nodeId,
            @PathParam("instanceId") String instanceId) {
        try {
            String serviceFile = SalsaConfiguration.getServiceStorageDir() + "/" + serviceId + ".data";

            CloudService service = SalsaXmlDataProcess.readSalsaServiceFile(serviceFile);
            ServiceUnit node = service.getComponentById(topologyId, nodeId);
            ServiceInstance instance = service.getInstanceById(topologyId, nodeId, Integer.parseInt(instanceId));

            // in case of VM, fetch ganglia information and show up
            if (node.getType().equals(SalsaEntityType.OPERATING_SYSTEM.getEntityTypeString())) {
                SalsaInstanceDescription_VM pros = (SalsaInstanceDescription_VM) instance.getProperties().getAny();
                return Response.status(200).entity(getVMInformation(pros)).build();
            } else {

            }
        } catch (IOException e1) {
            EngineLogger.logger.debug(e1.toString());
            return Response.status(500).entity("<Error>Internal error. Cannot read the service file !</Error>").build();
        } catch (JAXBException e2) {
            EngineLogger.logger.debug(e2.toString());
            return Response.status(500).entity("<Error>Internal error. Cannot parse the service file !</Error>").build();
        }

        return null;
    }

    protected String getVMInformation(SalsaInstanceDescription_VM pros) {

        String ip = pros.getPrivateIp();
        EngineLogger.logger.debug("Querying ganglia information and return: " + ip);
        //ProcessBuilder pb = new ProcessBuilder("/usr/bin/telnet",ip,"8649");
        EngineLogger.logger.debug("Debug ganglia 1");
        try {
            //Process p = pb.start();
            Process p = Runtime.getRuntime().exec("/usr/bin/telnet " + ip + " 8649");
            EngineLogger.logger.debug("Debug ganglia 2");
            p.waitFor();
            EngineLogger.logger.debug("Debug ganglia 3");

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(p.getInputStream()));
            String output = "";
            boolean writing = false;

            String line = reader.readLine();
            EngineLogger.logger.debug("Debug ganglia 4");
            EngineLogger.logger.debug(line);
				//line = reader.readLine(); line = reader.readLine(); line = reader.readLine(); // 3 first line of telnet command
            // we write out the HOST information of GANGLIA_XML
            while (line != null) {
                if (line.contains("<HOST") && line.contains("IP=\"" + pros.getPrivateIp() + "\"")) {
                    writing = true;
                }
                if (writing) {
                    output += line;
                }
                if (writing && line.contains("</HOST>")) {
                    writing = false;
                }

                line = reader.readLine();
            }
            EngineLogger.logger.debug(output);
            return output;

        } catch (IOException e) {
            EngineLogger.logger.debug(e.toString());
            return "<Error>Error when query monitoring information !</Error>";

        } catch (InterruptedException e1) {
            EngineLogger.logger.debug(e1.toString());
            return "Error when execute command to query monitoring information !";
        }
    }

}
