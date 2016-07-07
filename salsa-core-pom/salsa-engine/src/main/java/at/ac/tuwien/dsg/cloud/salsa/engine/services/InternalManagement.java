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
package at.ac.tuwien.dsg.cloud.salsa.engine.services;

import at.ac.tuwien.dsg.cloud.elise.collectorinterfaces.models.CollectorDescription;
import at.ac.tuwien.dsg.cloud.elise.collectorinterfaces.models.ConductorDescription;
import at.ac.tuwien.dsg.cloud.elise.master.RESTService.EliseManager;
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
import at.ac.tuwien.dsg.cloud.salsa.engine.dataprocessing.SalsaXmlDataProcess;
import at.ac.tuwien.dsg.cloud.salsa.engine.exceptions.EngineMisconfiguredException;
import at.ac.tuwien.dsg.cloud.salsa.common.interfaces.SalsaException;
import at.ac.tuwien.dsg.cloud.salsa.engine.services.jsondata.ServiceJsonList;
import at.ac.tuwien.dsg.cloud.salsa.engine.services.jsondata.ServiceJsonList.ServiceInfo;
import at.ac.tuwien.dsg.cloud.salsa.engine.utils.ActionIDManager;
import at.ac.tuwien.dsg.cloud.salsa.engine.utils.EngineLogger;
import at.ac.tuwien.dsg.cloud.salsa.engine.utils.PioneerManager;
import at.ac.tuwien.dsg.cloud.salsa.engine.utils.SalsaConfiguration;
import at.ac.tuwien.dsg.cloud.salsa.engine.utils.SystemFunctions;
import at.ac.tuwien.dsg.cloud.salsa.messaging.messageInterface.MessageClientFactory;
import at.ac.tuwien.dsg.cloud.salsa.messaging.messageInterface.MessagePublishInterface;
import at.ac.tuwien.dsg.cloud.salsa.messaging.protocol.SalsaMessage;
import at.ac.tuwien.dsg.cloud.salsa.messaging.protocol.SalsaMessageTopic;
import at.ac.tuwien.dsg.cloud.salsa.tosca.extension.SalsaInstanceDescription_VM;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import org.slf4j.Logger;


@Service
@Path("/manager")
public class InternalManagement {

    Logger LOGGER = EngineLogger.logger;

    /**
     * Get the list of all pioneers which are engaged with this salsa-engine
     *
     * @return The human-readable text of pioneers
     */
    @GET
    @Path("/pioneers/cache")
    public String getPioneers() {
        EngineLogger.logger.debug("Getting pioneer");
        return PioneerManager.describe();
    }

    @GET
    @Path("/pioneer/shutdown/{pioneerID}")
    public String shutdownPioneer(@PathParam("pioneerID") String pioneerID) {
        SalsaMessage msg = new SalsaMessage(SalsaMessage.MESSAGE_TYPE.salsa_shutdownPioneer, SalsaConfiguration.getSalsaCenterEndpoint(), SalsaMessageTopic.getPioneerTopicByID(pioneerID), "", "");
        MessageClientFactory factory = MessageClientFactory.getFactory(SalsaConfiguration.getBroker(), SalsaConfiguration.getBrokerType());
        MessagePublishInterface publish = factory.getMessagePublisher();
        publish.pushMessage(msg);
        return "Sent shutdown message to pioneer: " + pioneerID;
    }

    /**
     * Get the list of pending actions
     *
     * @return
     */
    @GET
    @Path("/actions/cache")
    public String getActions() {
        return ActionIDManager.describe();
    }

    /**
     * Get meta information of salsa
     *
     * @return
     */
    @GET
    @Path("/meta")
    public String getMetadata() {
        Map<String, Object> map = new HashMap<>();
        map.put("endpoint", SalsaConfiguration.getSalsaCenterEndpoint());
        map.put("version", SalsaConfiguration.getSalsaVersion());
        map.put("build_time", SalsaConfiguration.getBuildTime());
        map.put("broker", SalsaConfiguration.getBroker());
        map.put("broker_type", SalsaConfiguration.getBrokerType());
        map.put("user", SalsaConfiguration.getUserName());
        map.put("pioneer_number", PioneerManager.count());
        map.put("pioneer_description", PioneerManager.describeShort());

        EliseManager eliseManager = ((EliseManager) JAXRSClientFactory.create(SalsaConfiguration.getSalsaCenterEndpointLocalhost() + "/rest/elise", EliseManager.class, Collections.singletonList(new JacksonJaxbJsonProvider())));
        List<ConductorDescription> conductors = eliseManager.getConductorList();
        map.put("conductor_number", conductors.size());
        if (conductors.size() > 0) {
            Map<String, String> conductorMap = new HashMap<>();
            for (ConductorDescription c : conductors) {
                String s = "";
                for (CollectorDescription cl : c.getCollectors()) {
                    s += cl.getName() + " ";
                }
                conductorMap.put(c.getId(), c.getIp() + "," + s.trim());
            }
            map.put("conductor_description", conductorMap);
        }
        ServiceJsonList serviceList = new ServiceJsonList(SalsaConfiguration.getServiceStorageDir());
        map.put("managed_cloudservices", serviceList.getServicesList().size());
        if (serviceList.getServicesList().size() > 0) {
            Map<String, String> serviceMap = new HashMap<>();
            for (ServiceInfo sv : serviceList.getServicesList()) {
                serviceMap.put(sv.getServiceId(), "deployed:" + sv.getDeployTime());
            }
            map.put("managed_cloudservices_description", serviceMap);
        }

        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writer().withDefaultPrettyPrinter().writeValueAsString(map);
        } catch (IOException ex) {
            return null;
        }
    }

    @GET
    @Path("/syn")
    public String synPioneer() throws SalsaException {
        PioneerManager.removeAllPioneerInfo();
        SalsaMessage msg = new SalsaMessage(SalsaMessage.MESSAGE_TYPE.discover, SalsaConfiguration.getSalsaCenterEndpoint(), SalsaMessageTopic.PIONEER_REGISTER_AND_HEARBEAT, "", "toDiscoverPioneer");
        MessagePublishInterface publish = SalsaConfiguration.getMessageClientFactory().getMessagePublisher();
        publish.pushMessage(msg);
        return "Syn message published to the queue.";
    }

    /**
     * @return @throws SalsaException
     */
    @GET
    @Path("/artifacts/pioneer")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getPioneerArtifact() throws SalsaException {
        LOGGER.debug("Getting pioneer artifact and return: " + SalsaConfiguration.getPioneerLocalFile());
        File file = new File(SalsaConfiguration.getPioneerLocalFile());
        String fileName = file.getName();
        if (file.exists()) {
            return Response.ok(file, MediaType.APPLICATION_OCTET_STREAM)
                    .header("Content-Disposition", "attachment; filename=\"" + fileName + "\"")
                    .build();
        }
        throw new EngineMisconfiguredException(fileName, "Not found the pioneer.jar artifact: " + SalsaConfiguration.getPioneerLocalFile());
    }

    /**
     *
     * @return @throws SalsaException
     */
    @GET
    @Path("/artifacts/conductor")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getConductorArtifact() throws SalsaException {
        LOGGER.debug("Getting conductor artifact and return: " + SalsaConfiguration.getConductorLocalFile());
        File file = new File(SalsaConfiguration.getConductorLocalFile());
        String fileName = file.getName();
        if (file.exists()) {
            return Response.ok(file, MediaType.APPLICATION_OCTET_STREAM)
                    .header("Content-Disposition", "attachment; filename=\"" + fileName + "\"")
                    .build();
        }
        throw new EngineMisconfiguredException(fileName, "Not found the conductor.jar artifact at: " + SalsaConfiguration.getConductorLocalFile());
    }

    /**
     * This return the bootstrap script for the piooner. The script usually contains environment preparation, e.g. install suitable JRE
     *
     * @return The content of the script file
     * @throws SalsaException
     */
    @GET
    @Path("/artifacts/pioneerbootstrap")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getPioneerBootstrapScript() throws SalsaException {
        LOGGER.debug("Getting conductor artifact and return: " + SalsaConfiguration.getConductorLocalFile());
        File file = new File(SalsaConfiguration.getPioneerBootstrapScriptLocalFile());
        String fileName = file.getName();
        if (file.exists()) {
            return Response.ok(file, MediaType.APPLICATION_OCTET_STREAM)
                    .header("Content-Disposition", "attachment; filename=\"" + fileName + "\"")
                    .build();
        }
        throw new EngineMisconfiguredException(fileName, "Not found the bootstrap script artifact at: " + SalsaConfiguration.getPioneerBootstrapScript());
    }

    // this should be removed because ELISE is now involved
    @Deprecated
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

    //run and manage a single conductor on the main service
    static Process myConductor;

    boolean isRunning(Process process) {
        try {
            process.exitValue();
            return false;
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * Run a conductor. By default just copy all to /tmp and run
     *
     * @return
     */
    @GET
    @Path("/conductor/start")
    public boolean startConductor() {
        if (myConductor != null && isRunning(myConductor)) {
            EngineLogger.logger.debug("Tend to start a conductor, but it is already running... Salsa will do nothing!");
            return false;
        }
        String fileName = "conductor.jar";
        String workingFolderName = "/tmp/conductor/";
        String extensionsFolderName = workingFolderName + "extensions/";

        (new File(extensionsFolderName)).mkdirs();
        File localfile = new File(SalsaConfiguration.getConductorLocalFile());
        File configFile = new File(SalsaConfiguration.getConfigurationFile());
        File runFile = new File(workingFolderName + fileName);
        try {
            FileUtils.copyFile(localfile, runFile);
            EngineLogger.logger.debug("Copying configuration file from: {} to {}", configFile.getPath(), workingFolderName + "salsa.engine.properties");
            FileUtils.copyFile(configFile, new File(workingFolderName + "salsa.engine.properties"));

            EngineLogger.logger.debug("Starting a conductor ...");
            myConductor = SystemFunctions.executeCommandAndForget("java -jar " + fileName, workingFolderName, SalsaConfiguration.getSalsaCenterEndpoint());
        } catch (IOException ex) {
            LOGGER.error("Error when starting conductor: {}", ex.getMessage(), ex);
            return false;
        }
        return true;
    }

}
