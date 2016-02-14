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

import generated.oasis.tosca.TArtifactReference;
import generated.oasis.tosca.TArtifactTemplate;
import generated.oasis.tosca.TArtifactTemplate.ArtifactReferences;
import generated.oasis.tosca.TCapability;
import generated.oasis.tosca.TDefinitions;
import generated.oasis.tosca.TDeploymentArtifact;
import generated.oasis.tosca.TDeploymentArtifacts;
import generated.oasis.tosca.TNodeTemplate;
import generated.oasis.tosca.TRelationshipTemplate;
import generated.oasis.tosca.TRelationshipTemplate.SourceElement;
import generated.oasis.tosca.TRelationshipTemplate.TargetElement;
import generated.oasis.tosca.TRequirement;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.CloudService;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.PrimitiveOperation;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.SalsaEntity;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.ServiceInstance;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.ServiceInstance.Capabilities;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.ServiceInstance.Properties;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.ServiceTopology;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.ServiceTopology.SalsaReplicaRelationships;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.ServiceUnit;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.ServiceUnitRelationship;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.enums.SalsaEntityState;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.enums.SalsaEntityType;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.rSYBL.deploymentDescription.AssociatedVM;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.rSYBL.deploymentDescription.DeploymentDescription;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.rSYBL.deploymentDescription.DeploymentUnit;
import at.ac.tuwien.dsg.cloud.salsa.engine.capabilityinterface.SalsaEngineServiceIntenal;
import at.ac.tuwien.dsg.cloud.salsa.engine.utils.SalsaCenterConnector;
import at.ac.tuwien.dsg.cloud.salsa.engine.dataprocessing.SalsaXmlDataProcess;
import at.ac.tuwien.dsg.cloud.salsa.engine.capabilityinterface.UnitCapabilityInterface;
import at.ac.tuwien.dsg.cloud.salsa.engine.capabilityinterface.WholeAppCapabilityInterface;
import at.ac.tuwien.dsg.cloud.salsa.common.interfaces.SalsaException;
import at.ac.tuwien.dsg.cloud.salsa.engine.exceptions.ServicedataProcessingException;
import at.ac.tuwien.dsg.cloud.salsa.engine.exceptions.IllegalConfigurationAPICallException;
import at.ac.tuwien.dsg.cloud.salsa.engine.impl.richInformationCapability.AsyncUnitCapability;
import at.ac.tuwien.dsg.cloud.salsa.engine.impl.genericCapability.InfoParser;
import at.ac.tuwien.dsg.cloud.salsa.engine.services.jsondata.ServiceJsonList;
import at.ac.tuwien.dsg.cloud.salsa.engine.utils.ActionIDManager;
import at.ac.tuwien.dsg.cloud.salsa.engine.utils.EngineLogger;
import at.ac.tuwien.dsg.cloud.salsa.engine.utils.MutualFileAccessControl;
import at.ac.tuwien.dsg.cloud.salsa.engine.utils.PioneerManager;
import at.ac.tuwien.dsg.cloud.salsa.engine.utils.SalsaConfiguration;
import at.ac.tuwien.dsg.cloud.salsa.engine.utils.SystemFunctions;
import at.ac.tuwien.dsg.cloud.salsa.messaging.messageInterface.MessagePublishInterface;
import at.ac.tuwien.dsg.cloud.salsa.messaging.protocol.SalsaMessage;
import at.ac.tuwien.dsg.cloud.salsa.messaging.protocol.SalsaMessageTopic;
import at.ac.tuwien.dsg.cloud.salsa.messaging.model.Salsa.SalsaMsgConfigureArtifact;
import at.ac.tuwien.dsg.cloud.salsa.domainmodels.types.SalsaArtifactType;
import at.ac.tuwien.dsg.cloud.salsa.domainmodels.types.ServiceCategory;
import at.ac.tuwien.dsg.cloud.salsa.engine.impl.richInformationCapability.RichInformationWholeAppCapability;
import at.ac.tuwien.dsg.cloud.salsa.engine.utils.EventPublisher;
import at.ac.tuwien.dsg.cloud.salsa.tosca.extension.SalsaCapaReqString;
import at.ac.tuwien.dsg.cloud.salsa.tosca.extension.SalsaInstanceDescription_Docker;
import at.ac.tuwien.dsg.cloud.salsa.tosca.extension.SalsaInstanceDescription_VM;
import at.ac.tuwien.dsg.cloud.salsa.engine.dataprocessing.ToscaStructureQuery;
import at.ac.tuwien.dsg.cloud.salsa.engine.dataprocessing.ToscaXmlProcess;
import at.ac.tuwien.dsg.cloud.salsa.engine.impl.base.DynamicPlacementHelper;
import static at.ac.tuwien.dsg.cloud.salsa.engine.services.ViewGenerator.logger;
import at.ac.tuwien.dsg.cloud.salsa.messaging.model.Salsa.SalsaMsgUpdateMetadata;
import at.ac.tuwien.dsg.cloud.salsa.tosca.extension.SalsaMappingProperties;
import java.net.UnknownHostException;
import java.util.logging.Level;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MalformedObjectNameException;
import javax.management.ReflectionException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Service
public class SalsaEngineImplAll implements SalsaEngineServiceIntenal {

    private static final Logger LOGGER;

    WholeAppCapabilityInterface wholeAppCapability = new RichInformationWholeAppCapability();
    UnitCapabilityInterface unitCapability = new AsyncUnitCapability();

    private final String dataFileExtension = ".data";

    static {
        LOGGER = Logger.getLogger("EngineLogger");
    }

    /*
     * MAIN SERVICES TO EXPOSE TO USERS
     */
    @Override
    public Response deployService(String serviceNameParam, InputStream uploadedInputStream) throws SalsaException {
        //EventPublisher.publishSALSAEvent(serviceNameParam, "Recieved a deployment request with service name: " + serviceNameParam);
        String tmpID = UUID.randomUUID().toString();
        String tmpFile = "/tmp/salsa_tmp_" + tmpID;
        String serviceName = serviceNameParam.replaceAll("\\s", "");
        if (!checkForServiceName(serviceName)) {
            return Response.status(404).entity("Error. Service Name is bad: " + serviceName).build();
        }
        try {
            MutualFileAccessControl.writeToFile(uploadedInputStream, tmpFile);
            TDefinitions def = ToscaXmlProcess.readToscaFile(tmpFile);

            CloudService service = wholeAppCapability.addService(serviceName, def);
            String output = "Deployed service. Id: " + service.getId();
            LOGGER.debug(output);

            // delete tmp file
            File file = new File(tmpFile);
            file.delete();

            // return 201: resource created
            return Response.status(201).entity(service.getId()).build();
        } catch (JAXBException e) {
            LOGGER.error("Error when parsing Tosca: " + e);
            // return 400: bad request, the XML is malformed and could not process 
            return Response.status(400).entity("Error. Unable to parse Tosca. Error: " + e).build();
        } catch (IOException e) {
            LOGGER.error("Error reading file: " + tmpFile + ". Error: " + e);
            //return 500: intenal server error. The server cannot create and process tmp Tosca file 
            return Response.status(500).entity("Error when process Tosca file. Error: " + e).build();
        }
    }

    @Override
    public Response deployServiceFromXML(String uploadedInputStream) throws SalsaException {
        String tmpID = UUID.randomUUID().toString();
        String tmpFile = "/tmp/salsa_tmp_" + tmpID;

        try {
            FileUtils.writeStringToFile(new File(tmpFile), uploadedInputStream);

            TDefinitions def = ToscaXmlProcess.readToscaFile(tmpFile);
            String serviceId = def.getId();
            if (!checkForServiceName(serviceId)) {
                return Response.status(404).entity("Error. Service Name is bad: " + serviceId).build();
            }
            //CloudService service = deployer.deployNewService(def, serviceId);
            CloudService service = this.wholeAppCapability.addService(serviceId, def);
            String output = "Deployed service. Id: " + service.getId();
            LOGGER.debug(output);
            // delete tmp file
            File file = new File(tmpFile);
            file.delete();

            // return 201: resource created
            return Response.status(201).entity(serviceId).build();
        } catch (JAXBException e) {
            LOGGER.error("Error when parsing Tosca: " + e);

            // return 400: bad request, the XML is malformed and could not process
            return Response.status(400).entity("Unable to parse the Tosca XML. Error: " + e).build();
        } catch (IOException e) {
            LOGGER.error("Error reading file: " + tmpFile + ". Error: " + e);
            return Response.status(500).entity("Error when process Tosca file. Error: " + e).build();
        }
    }

    @Override
    public Response redeployService(String serviceId) throws SalsaException {
        String ogininalToscaFile = SalsaConfiguration.getServiceStorageDir() + "/" + serviceId + ".original";
        try {
            String originalTosca = FileUtils.readFileToString(new File(ogininalToscaFile));
            undeployService(serviceId);
            Thread.sleep(3000);
            deployService(serviceId, new ByteArrayInputStream(originalTosca.getBytes("UTF-8")));
        } catch (IOException e) {
            LOGGER.error("Error when reading data file! Error: " + e);
            return Response.status(500).entity("Error when reading orgininal tosca file for service: " + serviceId + ". Error: " + e).build();
        } catch (InterruptedException ie) {
            LOGGER.error("Interrup error");
            return Response.status(500).entity("Error when reading orgininal tosca file for service: " + serviceId + ". Error: " + ie).build();
        }
        return Response.status(201).entity(serviceId).build();
    }

    /* (non-Javadoc)
     * @see at.ac.tuwien.dsg.cloud.salsa.engine.services.SalsaEngineIntenalInterface#undeployService(java.lang.String)
     */
    @Override
    public Response undeployService(String serviceId) throws SalsaException {
        LOGGER.debug("DELETING SERVICE: " + serviceId);
        boolean cleaned = wholeAppCapability.cleanService(serviceId);

        EngineLogger.logger.debug("The lower capability return: {}", cleaned);
        // wait and check if it is really cleaned

        String fileName = SalsaConfiguration.getServiceStorageDir() + "/" + serviceId;
        File file = new File(fileName);
        File datafile = new File(fileName.concat(this.dataFileExtension));
        File originalFile = new File(fileName.concat(".original"));

        if (originalFile.delete() && file.delete() && datafile.delete()) {
            if (cleaned) {// deregister service here
                LOGGER.debug("Deregister service done: " + serviceId);
                return Response.status(200).entity("Deregistered service: " + serviceId).build();
            } else {
                // return 404: not found the service to be undeployed
                LOGGER.error("Could not found service to deregister: " + serviceId);
                return Response.status(404).entity("An error occurs when cleaning service: " + serviceId).build();
            }
        } else {
            LOGGER.debug("Could not found service to deregister: " + serviceId);
            return Response.status(500).entity("Service not found to deregister: " + serviceId).build();
        }

    }

    /* (non-Javadoc)
     * @see at.ac.tuwien.dsg.cloud.salsa.engine.services.SalsaEngineIntenalInterface#spawnInstance(java.lang.String, java.lang.String, java.lang.String, int)
     */
    @Override
    public Response spawnInstance(String serviceId,
            String nodeId,
            int quantity) throws SalsaException {
        LOGGER.debug("SPAWNING MORE INSTANCE: " + serviceId + "/" + nodeId + ". Quantity:" + quantity);

        SalsaCenterConnector centerCon = new SalsaCenterConnector(SalsaConfiguration.getSalsaCenterEndpointLocalhost(), "/tmp", EngineLogger.logger);

        CloudService service = centerCon.getUpdateCloudServiceRuntime(serviceId);
        ServiceUnit node = service.getComponentById(nodeId);
        if (node == null) {
            throw new IllegalConfigurationAPICallException("Cannot find the node with id: " + serviceId + "/" + nodeId);
        }
        String correctTopologyID = service.getTopologyOfNode(node.getId()).getId();
        // update first the number + quantity		
        centerCon.updateNodeIdCounter(serviceId, correctTopologyID, nodeId, node.getIdCounter() + quantity);
        String returnVal = "";
        for (int i = node.getIdCounter() + 1; i < node.getIdCounter() + quantity + 1; i++) {
            unitCapability.deploy(serviceId, nodeId, i);
            returnVal += i + " ";
        }
        return Response.status(201).entity(returnVal).build();
    }

    @Override
    public Response scaleOutNode(String serviceId, String nodeId) throws SalsaException {
        SalsaCenterConnector centerCon = new SalsaCenterConnector(SalsaConfiguration.getSalsaCenterEndpointLocalhost(), "/tmp", EngineLogger.logger);
        CloudService service = centerCon.getUpdateCloudServiceRuntime(serviceId);
        ServiceTopology topo = service.getTopologyOfNode(nodeId);

        String instanceId = ((String) spawnInstance(serviceId, nodeId, 1).getEntity()).trim();
        LOGGER.debug("Generate instance id: " + instanceId);
        service = centerCon.getUpdateCloudServiceRuntime(serviceId);
        ServiceUnit unit = service.getComponentById(nodeId);
        ServiceInstance instance = unit.getInstanceById(Integer.parseInt(instanceId));

        int counter = 0;
        while (instance == null || !instance.getState().equals(SalsaEntityState.DEPLOYED)) {
            try {
                if (counter > 300) {
                    break;
                }
                Thread.sleep(3000);
                counter++;
            } catch (Exception e) {
                LOGGER.debug("Interrupt when waiting for the status of the node");
                return Response.status(500).entity("Could not get the IP of the scaled out node").build();
            }
            service = centerCon.getUpdateCloudServiceRuntime(serviceId);
            unit = service.getComponentById(nodeId);
            instance = unit.getInstanceById(Integer.parseInt(instanceId));
        }

        ServiceUnit hostedUnit = service.getComponentById(unit.getHostedId());
        LOGGER.debug("HostNode: " + hostedUnit.getId() + ". Prepare to get its instance: " + instance.getHostedId_Integer());
        ServiceInstance hostedInstance = hostedUnit.getInstanceById(instance.getHostedId_Integer());

        LOGGER.debug("HostNode/InstanceId: " + hostedUnit.getId() + "/" + hostedInstance.getInstanceId());
        while (!hostedUnit.getType().equals(SalsaEntityType.OPERATING_SYSTEM.getEntityTypeString())
                && !hostedUnit.getType().equals(SalsaEntityType.DOCKER.getEntityTypeString())) {
            hostedUnit = service.getComponentById(hostedUnit.getHostedId());
            hostedInstance = hostedUnit.getInstanceById(hostedInstance.getHostedId_Integer());
            LOGGER.debug("HostNode: " + hostedUnit.getId() + "/" + hostedInstance.getInstanceId());
        }
        SalsaInstanceDescription_VM vm = (SalsaInstanceDescription_VM) hostedInstance.getProperties().getAny();
        return Response.status(201).entity(vm.getPrivateIp()).build();
    }

    @Override
    public Response scaleInNode(String serviceId, String nodeId) throws SalsaException {
        SalsaCenterConnector centerCon = new SalsaCenterConnector(SalsaConfiguration.getSalsaCenterEndpointLocalhost(), "/tmp", EngineLogger.logger);
        CloudService service = centerCon.getUpdateCloudServiceRuntime(serviceId);
        ServiceTopology topo = service.getTopologyOfNode(nodeId);
        ServiceUnit unit = topo.getComponentById(nodeId);
        List<ServiceInstance> instances = unit.getInstancesList();
        if (instances.size() > 0) {
            return destroyInstance(serviceId, nodeId, instances.get(0).getInstanceId());
        }
        return Response.status(404).entity("Found no instance to remove").build();
    }

    @Override
    public Response scaleInVM(String serviceId, String vmIp) throws SalsaException {
        SalsaCenterConnector centerCon = new SalsaCenterConnector(SalsaConfiguration.getSalsaCenterEndpointLocalhost(), "/tmp", EngineLogger.logger);
        CloudService service = centerCon.getUpdateCloudServiceRuntime(serviceId);
        for (ServiceTopology topo : service.getComponentTopologyList()) {
            for (ServiceUnit unit : topo.getComponentsByType(SalsaEntityType.OPERATING_SYSTEM)) {
                EngineLogger.logger.debug("Scaling in VM. Checking OS unit: " + unit.getId());
                for (ServiceInstance vm : unit.getInstancesList()) {
                    SalsaInstanceDescription_VM vmProp = (SalsaInstanceDescription_VM) vm.getProperties().getAny();
                    if (vmProp.getPrivateIp().equals(vmIp)) {
                        return destroyInstance(serviceId, unit.getId(), vm.getInstanceId());
                    }
                }
            }
            for (ServiceUnit unit : topo.getComponentsByType(SalsaEntityType.DOCKER)) {
                EngineLogger.logger.debug("Scaling in VM/docker. Checking docker unit: " + unit.getId());
                for (ServiceInstance vm : unit.getInstancesList()) {
                    EngineLogger.logger.debug("Scaling in VM/docker. Checking docker instance: " + vm.getInstanceId());
                    SalsaInstanceDescription_VM vmProp = (SalsaInstanceDescription_VM) vm.getProperties().getAny();
                    if (vmProp.getPrivateIp().equals(vmIp)) {
                        EngineLogger.logger.debug("Scaling in VM. GOT A DOCKER NODE TO SCALE-IN: " + vm.getInstanceId() + "/" + vmProp.getPrivateIp());
                        return destroyInstance(serviceId, unit.getId(), vm.getInstanceId());
                    }
                }
            }
        }
        return Response.status(404).entity("Not found a VM nodes of IP: " + vmIp).build();
    }

    @Override
    public Response scaleOutVM(String serviceId, String vmIp) throws SalsaException {
        SalsaCenterConnector centerCon = new SalsaCenterConnector(SalsaConfiguration.getSalsaCenterEndpointLocalhost(), "/tmp", EngineLogger.logger);
        centerCon.getUpdateCloudServiceRuntime(serviceId);
        return null;
    }

    @Override
    public Response deployInstance(
            String serviceId,
            String topologyId,
            String nodeId,
            int instanceId) throws SalsaException {
        LOGGER.debug("Deployment request for this node: " + serviceId + " - " + nodeId + " - " + instanceId);
        LOGGER.debug("PUT 1 MORE INSTANCE: " + serviceId + "/" + topologyId + "/" + nodeId);

        SalsaCenterConnector centerCon = new SalsaCenterConnector(SalsaConfiguration.getSalsaCenterEndpointLocalhost(), "/tmp", EngineLogger.logger);
        TDefinitions def = centerCon.getToscaDescription(serviceId);
        CloudService service = centerCon.getUpdateCloudServiceRuntime(serviceId);
        ServiceUnit node = service.getComponentById(nodeId);
        if (node == null) {
            EngineLogger.logger.error("May be the id of node is invalided");
            return Response.status(500).entity("Error: Node ID is not found.").build();
        }
        unitCapability.deploy(serviceId, nodeId, instanceId);
        return Response.status(201).entity(instanceId).build();
        //TODO: What happen if it is fail to spawn a VM ? 
    }

    @Override
    public Response destroyInstance(
            String serviceId,
            String nodeId,
            int instanceId) throws SalsaException {
        String salsaFile = SalsaConfiguration.getServiceStorageDir() + File.separator + serviceId + this.dataFileExtension;
        CloudService service;
        try {
            service = SalsaXmlDataProcess.readSalsaServiceFile(salsaFile);
            ServiceUnit unit = service.getComponentById(nodeId);
            if (unit.getReference() != null && !unit.getReference().isEmpty()) {
                return Response.status(200).entity("Do not destroy reference instance: " + serviceId + "/" + nodeId + "/" + instanceId).build();
            }
        } catch (JAXBException | IOException ex) {
            return Response.status(500).entity("Cannot get instance information: " + serviceId + "/" + nodeId + "/" + instanceId).build();
        }

        LOGGER.debug("Removing service instance: " + serviceId + "/" + nodeId + "/" + instanceId);
        unitCapability.remove(serviceId, nodeId, instanceId);
        LOGGER.debug("Removing service instance: " + serviceId + "/" + nodeId + "/" + instanceId + " - DONE!");
        return Response.status(200).entity("Undeployed instance: " + serviceId + "/" + nodeId + "/" + instanceId).build();
    }

    @Override
    public Response getInstanceStatus(String serviceId, String nodeId, int instanceId) throws SalsaException {
        try {
            String salsaFile = SalsaConfiguration.getServiceStorageDir() + File.separator + serviceId + this.dataFileExtension;
            CloudService service = SalsaXmlDataProcess.readSalsaServiceFile(salsaFile);
            ServiceUnit nodeData = service.getComponentById(nodeId);
            ServiceInstance instanceData = nodeData.getInstanceById(instanceId);
            return Response.status(200).entity(instanceData.getState().getNodeStateString()).build();
        } catch (JAXBException | IOException | NullPointerException e) {
            return Response.status(404).entity("Cannot get configuration status of instance: " + instanceId + ", on node: " + nodeId + ", on service " + serviceId).build();
        }
    }

    @Override
    public Response removeInstanceMetadata(
            String serviceId,
            String nodeId,
            int instanceId) throws SalsaException {
        // remove metadata
        try {
            MutualFileAccessControl.lockFile();
            String salsaFile = SalsaConfiguration.getServiceStorageDir() + File.separator + serviceId + this.dataFileExtension;
            CloudService service = SalsaXmlDataProcess.readSalsaServiceFile(salsaFile);
            ServiceUnit nodeData = service.getComponentById(nodeId);
            ServiceInstance instanceData = nodeData.getInstanceById(instanceId);
            if (nodeData.getReference() != null) {
                return Response.status(404).entity("Cannot remove instance because it is a reference: " + instanceId + ", on node: " + nodeId + ", on service " + serviceId).build();
            }
            // remove hosted on node
            for (ServiceUnit unit : service.getAllComponent()) {
                if (unit.getHostedId().equals(nodeData.getId())) {
                    for (ServiceInstance instance : unit.getInstancesList()) {
                        if (instance.getHostedId_Integer() == instanceData.getInstanceId()) {
                            unit.getInstancesList().remove(instance);
                        }
                    }
                }
            }
            nodeData.getInstancesList().remove(instanceData);
            updateComponentStateBasedOnInstance(service);
            SalsaXmlDataProcess.writeCloudServiceToFile(service, salsaFile);

        } catch (JAXBException | IOException e) {
            LOGGER.error(e.getMessage());
            return Response.status(404).entity("Cannot remove instance: " + instanceId + ", on node: " + nodeId + ", on service " + serviceId).build();
        } finally {
            MutualFileAccessControl.releaseFile();
        }
        return Response.status(200).entity("Undeployed instance: " + serviceId + "/" + nodeId + "/" + instanceId).build();
    }

    private boolean checkForServiceName(String serviceName) {
        if (serviceName.equals("")) {
            return false;
        }
        String pathName = SalsaConfiguration.getServiceStorageDir();

        ServiceJsonList serviceList = new ServiceJsonList(pathName);
        for (ServiceJsonList.ServiceInfo serv : serviceList.getServicesList()) {
            if (serviceName.equals(serv.getServiceId())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Response deployTopology(String serviceId, String topologyId) throws SalsaException {
        String salsaFile = SalsaConfiguration.getServiceStorageDir() + File.separator + serviceId + this.dataFileExtension;
        try {
            CloudService service = SalsaXmlDataProcess.readSalsaServiceFile(salsaFile);
            ServiceTopology topo = service.getComponentTopologyById(topologyId);
            List<ServiceUnit> units = topo.getComponentsByType(SalsaEntityType.SOFTWARE);
            for (ServiceUnit u : units) {
                spawnInstance(serviceId, u.getId(), 1);
            }
            return Response.status(200).entity("Deploy the whole topology").build();
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            return Response.status(404).entity("Cannot read the service data file").build();
        } catch (JAXBException e1) {
            LOGGER.error(e1.getMessage());
            return Response.status(500).entity("Cannot parse the service data file").build();
        }
    }

    @Override
    public Response undeployTopology(String serviceId, String topologyId) throws SalsaException {
        String salsaFile = SalsaConfiguration.getServiceStorageDir() + File.separator + serviceId + this.dataFileExtension;
        try {
            CloudService service = SalsaXmlDataProcess.readSalsaServiceFile(salsaFile);
            ServiceTopology topo = service.getComponentTopologyById(topologyId);
            List<ServiceUnit> units = topo.getComponentsByType(SalsaEntityType.OPERATING_SYSTEM);
            for (ServiceUnit u : units) {
                for (ServiceInstance instance : u.getInstancesList()) {
                    destroyInstance(serviceId, u.getId(), instance.getInstanceId());
                }
            }
            return Response.status(200).entity("Deploy the whole topology").build();
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            return Response.status(404).entity("Cannot read the service data file").build();
        } catch (JAXBException e1) {
            LOGGER.error(e1.getMessage());
            return Response.status(500).entity("Cannot parse the service data file").build();
        }
    }

    @Override
    public Response destroyInstanceOfNodeType(String serviceId, String topologyId, String nodeId) throws SalsaException {
        String salsaFile = SalsaConfiguration.getServiceStorageDir() + File.separator + serviceId + this.dataFileExtension;
        LOGGER.debug("Remove all instances of node: " + nodeId);
        try {
            CloudService service = SalsaXmlDataProcess.readSalsaServiceFile(salsaFile);
            ServiceUnit unit = service.getComponentById(nodeId);
            for (ServiceInstance instance : unit.getInstancesList()) {
                destroyInstance(serviceId, nodeId, instance.getInstanceId());
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            return Response.status(404).entity("Cannot read the service data file").build();
        } catch (JAXBException e1) {
            LOGGER.error(e1.getMessage());
            return Response.status(500).entity("Cannot parse the service data file").build();
        }

        return Response.status(200).entity("Undeploy nodes").build();
    }

    /*
     * INTERNAL SERVICES FOR CLOUD SERVICES
     */
    @Override
    public Response getService(String serviceDeployId) throws SalsaException {
        if (serviceDeployId.equals("")) {
            throw new IllegalConfigurationAPICallException("Query service data, but the service ID is empty !");
        }
        String fileName = SalsaConfiguration.getServiceStorageDir() + "/" + serviceDeployId + this.dataFileExtension;
        try {
            String xml = FileUtils.readFileToString(new File(fileName));
            return Response.status(200).entity(xml).build();
        } catch (Exception e) {
            LOGGER.error("Could not find service: " + serviceDeployId + ". Data did not be sent. Error: " + e.toString());
            LOGGER.debug("THROWING AN EXCEPTION OF FAILURE TO GET SERVICE !");
            throw new ServicedataProcessingException("Cannot read the service data for service ID: " + serviceDeployId);
        }
    }

    @Override
    public Response getToscaService(String serviceDeployId) throws SalsaException {
        LOGGER.debug("Read Tosca file and return !");
        if (serviceDeployId.equals("")) {
            throw new IllegalConfigurationAPICallException("Query TOSCA, but the service ID is empty !");
        }
        String fileName = SalsaConfiguration.getServiceStorageDir() + "/" + serviceDeployId;
        try {
            String xml = FileUtils.readFileToString(new File(fileName));
            return Response.status(200).entity(xml).build();
        } catch (Exception e) {
            throw new ServicedataProcessingException("Cannot read the TOSCA for service ID: " + serviceDeployId);
        }
    }

    @Override
    public Response getServiceSYBL_DEP_DESP(String serviceId) throws SalsaException {
        String salsaFile = SalsaConfiguration.getServiceStorageDir() + File.separator + serviceId + this.dataFileExtension;
        LOGGER.debug("Generating deployment desp for SYBL");
        try {
            CloudService service = SalsaXmlDataProcess.readSalsaServiceFile(salsaFile);
            LOGGER.debug("Service id: " + service.getId());
            DeploymentDescription sybl = new DeploymentDescription();
            sybl.setAccessIP("localhost");
            for (ServiceTopology topo : service.getComponentTopologyList()) {
                LOGGER.debug("Topo ID: " + topo.getId());
                List<ServiceUnit> units = topo.getComponentsByType(SalsaEntityType.SOFTWARE);
                units.addAll(topo.getComponentsByType(SalsaEntityType.WAR));
                sybl.setCloudServiceID(serviceId);
                for (ServiceUnit unit : units) {
                    LOGGER.debug("NodeID: " + unit.getId());
                    DeploymentUnit syblDepUnit = new DeploymentUnit();
                    syblDepUnit.setServiceUnitID(unit.getId());

                    for (ServiceInstance instance : unit.getInstancesList()) {
                        ServiceUnit hostedUnit = topo.getComponentById(unit.getHostedId());
                        ServiceInstance hostedInstance = hostedUnit.getInstanceById(instance.getHostedId_Integer());
                        // in the case we have more than one software stack
                        while (!hostedUnit.getType().equals(SalsaEntityType.OPERATING_SYSTEM.getEntityTypeString())
                                && !hostedUnit.getType().equals(SalsaEntityType.DOCKER.getEntityTypeString())) {
                            hostedUnit = topo.getComponentById(hostedUnit.getHostedId());
                            hostedInstance = hostedUnit.getInstanceById(hostedInstance.getHostedId_Integer());
                        }
                        LOGGER.debug("Host instance: " + hostedUnit.getId() + "/" + hostedInstance.getInstanceId());
                        SalsaInstanceDescription_VM vm = (SalsaInstanceDescription_VM) hostedInstance.getProperties().getAny();
                        AssociatedVM assVM = new AssociatedVM();
                        assVM.setIp(vm.getPrivateIp().trim());
                        assVM.setUuid(vm.getInstanceId());
                        syblDepUnit.addAssociatedVM(assVM);
                    }
                    sybl.getDeployments().add(syblDepUnit);
                }
            }
            JAXBContext a = JAXBContext.newInstance(DeploymentDescription.class);
            Marshaller mar = a.createMarshaller();
            mar.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            StringWriter xmlWriter = new StringWriter();
            mar.marshal(sybl, xmlWriter);
            return Response.status(200).entity(xmlWriter.toString()).build();
        } catch (JAXBException e1) {
            throw new ServicedataProcessingException("Cannot parse the data using JAXB for service ID: " + serviceId);
        } catch (IOException e2) {
            throw new ServicedataProcessingException("Cannot read the data for service ID: " + serviceId);
        }
    }

    private String getIpOfServiceInstance(String serviceId, String nodeId, int instanceId) {
        String salsaFile = SalsaConfiguration.getServiceStorageDir() + File.separator + serviceId + this.dataFileExtension;
        try {
            LOGGER.debug("Searching IP of instance");
            CloudService service = SalsaXmlDataProcess.readSalsaServiceFile(salsaFile);
            ServiceUnit unit = service.getComponentById(nodeId);
            ServiceInstance instance = unit.getInstanceById(instanceId);
            LOGGER.debug("Searching IP of instance 1");
            ServiceUnit hostedUnit = service.getComponentById(unit.getHostedId());
            LOGGER.debug("Searching IP of instance 2");
            ServiceInstance hostedInstance = hostedUnit.getInstanceById(instance.getHostedId_Integer());
            LOGGER.debug("Searching IP of instance 3");
            while (!hostedUnit.getType().equals(SalsaEntityType.OPERATING_SYSTEM.getEntityTypeString())
                    && !hostedUnit.getType().equals(SalsaEntityType.DOCKER.getEntityTypeString())) {
                LOGGER.debug("Searching IP of instance 4");
                hostedUnit = service.getComponentById(hostedUnit.getHostedId());
                hostedInstance = hostedUnit.getInstanceById(hostedInstance.getHostedId_Integer());
            }
            LOGGER.debug("Searching IP of instance 5");
            if (hostedInstance.getProperties() == null) {
                return "";
            }
            SalsaInstanceDescription_VM vm = (SalsaInstanceDescription_VM) hostedInstance.getProperties().getAny();
            LOGGER.debug("Searching IP of instance 6. VMID=" + vm.getInstanceId());
            LOGGER.debug("Searching IP of instance 6. IP=" + vm.getPrivateIp());
            return vm.getPrivateIp().trim();
        } catch (IOException | JAXBException e) {
            LOGGER.debug(e.getMessage());
            return "";
        }
    }

    /*
     * INTERFNAL SERVICES FOR TOPOLOGY
     */
 /* (non-Javadoc)
     * @see at.ac.tuwien.dsg.cloud.salsa.engine.services.SalsaEngineIntenalInterface#addRelationship(javax.xml.bind.JAXBElement, java.lang.String, java.lang.String)
     */
    @Override
    public Response addRelationship(
            ServiceUnitRelationship data,
            String serviceId,
            String topologyId) throws SalsaException {
        String salsaFile = SalsaConfiguration.getServiceStorageDir()
                + File.separator + serviceId + this.dataFileExtension;
        try {
            MutualFileAccessControl.lockFile();
            CloudService service = SalsaXmlDataProcess.readSalsaServiceFile(salsaFile);
            ServiceTopology topo = service.getComponentTopologyById(topologyId);
            SalsaReplicaRelationships rels = topo.getRelationships();
            if (rels == null) {
                rels = new SalsaReplicaRelationships();
                topo.setRelationships(rels);
            }
            //rels.addRelationship(data.getValue());
            rels.addRelationship(data);
            SalsaXmlDataProcess.writeCloudServiceToFile(service, salsaFile);
        } catch (JAXBException | IOException ex) {
            throw new ServicedataProcessingException(serviceId, ex);
        } finally {
            MutualFileAccessControl.releaseFile();
        }
        return Response.status(200).entity("Updated relationship ").build();

    }

    /* (non-Javadoc)
     * @see at.ac.tuwien.dsg.cloud.salsa.engine.services.SalsaEngineIntenalInterface#updateNodeIdCounter(java.lang.String, java.lang.String, java.lang.String, int)
     */
    @Override
    public Response updateNodeIdCounter(
            String serviceId,
            String topologyId,
            String nodeId,
            int value) throws SalsaException {
        try {
            MutualFileAccessControl.lockFile();
            String salsaFile = SalsaConfiguration.getServiceStorageDir()
                    + File.separator + serviceId + this.dataFileExtension;
            CloudService service = SalsaXmlDataProcess
                    .readSalsaServiceFile(salsaFile);
            ServiceTopology topo = service
                    .getComponentTopologyById(topologyId);
            ServiceUnit nodeData = topo.getComponentById(nodeId);
            nodeData.setIdCounter(value);
            SalsaXmlDataProcess.writeCloudServiceToFile(service, salsaFile);
        } catch (JAXBException | IOException ex) {
            throw new ServicedataProcessingException(serviceId, ex);
        } finally {
            MutualFileAccessControl.releaseFile();
        }
        return Response.status(200).entity("Updated node " + nodeId + " on service " + serviceId
                + " node ID counter: " + value).build();
    }

    /*
     * INTERNAL SERVICES FOR INSTANCE UNITS
     */
    @Override
    public Response addInstanceUnitMetaData(ServiceInstance data,
            String serviceId,
            String topologyId,
            String nodeId) throws SalsaException {
        EngineLogger.logger.debug("addInstanceUnitMetaData1. STARTING: " + serviceId + "/" + topologyId + "/" + nodeId + "/" + data.getInstanceId());
        String fileName = SalsaConfiguration.getServiceStorageDir() + File.separator + serviceId + this.dataFileExtension;
        try {
            MutualFileAccessControl.lockFile();
            EngineLogger.logger.debug("addInstanceUnitMetaData2. STARTING TO TRY BLOCK");
            CloudService service = SalsaXmlDataProcess.readSalsaServiceFile(fileName);
            EngineLogger.logger.debug("addInstanceUnitMetaData3. Compo.id" + service.getId());
            ServiceTopology topo = service.getComponentTopologyById(topologyId);
            EngineLogger.logger.debug("addInstanceUnitMetaData4. Compo.id" + topo.getId());
            ServiceUnit compo = topo.getComponentById(nodeId);
            EngineLogger.logger.debug("addInstanceUnitMetaData5. Compo.id" + compo.getId());
            ServiceInstance replicaData = data;//.getValue();
            EngineLogger.logger.debug("addInstanceUnitMetaData6. Instance id: " + replicaData.getInstanceId());
            int id = replicaData.getInstanceId();
            ServiceInstance existedInstance = compo.getInstanceById(id);
            if (existedInstance == null) {
                EngineLogger.logger.debug("Create new instance meta-data");
                compo.addInstance(replicaData);
            } else {
                EngineLogger.logger.debug("Update old instance meta-data");
                compo.getInstanceById(id).setHostedId_Integer(replicaData.getHostedId_Integer());
            }
            EngineLogger.logger.debug("addInstanceUnitMetaData. writing down service id: " + service.getId());
            SalsaXmlDataProcess.writeCloudServiceToFile(service, fileName);
        } catch (JAXBException | IOException ex) {
            throw new ServicedataProcessingException(serviceId, ex);
        } finally {
            MutualFileAccessControl.releaseFile();
        }
        MutualFileAccessControl.releaseFile();
        return Response.status(201).entity("Spawned VM").build();
        //TODO: What happen if it is fail to spawn a VM ? 
    }

    @Override
    public Response addServiceUnitMetaData(ServiceUnit data,
            String serviceId, String topologyId) throws SalsaException {
        String fileName = SalsaConfiguration.getServiceStorageDir() + File.separator + serviceId + this.dataFileExtension;
        String toscaFileName = SalsaConfiguration.getServiceStorageDir() + File.separator + serviceId;
        try {
            MutualFileAccessControl.lockFile();
            CloudService service = SalsaXmlDataProcess.readSalsaServiceFile(fileName);
            ServiceTopology topo = service.getComponentTopologyById(topologyId);
            topo.addComponent(data);
            SalsaXmlDataProcess.writeCloudServiceToFile(service, fileName);

            // update the Tosca
            TDefinitions def = ToscaXmlProcess.readToscaFile(toscaFileName);
            TNodeTemplate toscaNode = new TNodeTemplate();
            toscaNode.setId(data.getId());
            toscaNode.setMinInstances(data.getMin());
            toscaNode.setMaxInstances(Integer.toString(data.getMax()));
            toscaNode.setType(new QName(data.getType()));
            TDeploymentArtifact dA = new TDeploymentArtifact();
            dA.setArtifactRef(new QName(toscaNode.getId() + "_artifact"));
            dA.setArtifactType(new QName(data.getArtifactType()));
            TDeploymentArtifacts dAs = new TDeploymentArtifacts();
            dAs.getDeploymentArtifact().add(dA);
            toscaNode.setDeploymentArtifacts(dAs);
            //ToscaStructureQuery.getFirstServiceTemplate(def).getTopologyTemplate().getNodeTemplateOrRelationshipTemplate().add(toscaNode);
            ToscaStructureQuery.getTopologyTemplate(topologyId, def).getNodeTemplateOrRelationshipTemplate().add(toscaNode);

            TArtifactTemplate artTemp = new TArtifactTemplate();
            ArtifactReferences artRefs = new ArtifactReferences();
            TArtifactReference artRef = new TArtifactReference();

            artRefs.getArtifactReference().add(artRef);
            artTemp.setArtifactReferences(artRefs);
            artTemp.setId(toscaNode.getId() + "_artifact");
            artTemp.setType(new QName(data.getArtifactType()));
            def.getServiceTemplateOrNodeTypeOrNodeTypeImplementation().add(artTemp);

            ServiceUnit hostNode = service.getComponentById(topologyId, data.getHostedId());
            if (hostNode == null) {
                LOGGER.error("Cannot find the host node for this node.");
                return Response.status(401).entity("Cannot find the host node for this node.").build();
            }
            TNodeTemplate toscaHostNode = ToscaStructureQuery.getNodetemplateById(hostNode.getId(), def);
            TRelationshipTemplate rela = new TRelationshipTemplate();
            SourceElement source = new SourceElement();
            source.setRef(toscaNode);
            TargetElement target = new TargetElement();
            target.setRef(toscaHostNode);
            rela.setSourceElement(source);
            rela.setTargetElement(target);
            //ToscaStructureQuery.getFirstServiceTemplate(def).getTopologyTemplate().getNodeTemplateOrRelationshipTemplate().add(rela);
            ToscaStructureQuery.getTopologyTemplate(topologyId, def).getNodeTemplateOrRelationshipTemplate().add(rela);

            ToscaXmlProcess.writeToscaDefinitionToFile(def, toscaFileName);
        } catch (JAXBException | IOException ex) {
            throw new ServicedataProcessingException(serviceId, ex);
        } finally {
            MutualFileAccessControl.releaseFile();
        }
        MutualFileAccessControl.releaseFile();
        return Response.status(201).entity("Added service unit").build();
    }

    @Override
    public Response addServiceUnitMetaData(String toscaXML,
            String serviceId, String topologyId) throws SalsaException {
        String fileName = SalsaConfiguration.getServiceStorageDir() + File.separator + serviceId + this.dataFileExtension;
        try {
            MutualFileAccessControl.lockFile();
            TNodeTemplate toscaNode = ToscaXmlProcess.readToscaNodeTemplateFromString(toscaXML);
            if (toscaNode == null || toscaNode.getId() == null || toscaNode.getType() == null) {
                LOGGER.error("Wrong format of ToscaXML !");
                return Response.status(404).entity("Wrong format of ToscaXML !").build();
            }
            String id = toscaNode.getId();
            String type = toscaNode.getType().getLocalPart();
            ServiceUnit data = new ServiceUnit(id, type);
            CloudService service = SalsaXmlDataProcess.readSalsaServiceFile(fileName);
            ServiceTopology topo = service.getComponentTopologyById(topologyId);
            topo.addComponent(data);
        } catch (JAXBException | IOException ex) {
            throw new ServicedataProcessingException(serviceId, ex);
        } finally {
            MutualFileAccessControl.releaseFile();
        }
        MutualFileAccessControl.releaseFile();
        return Response.status(201).entity("Added service unit").build();
    }

    @Override
    public Response updateInstanceUnitCapability(SalsaCapaReqString data,
            String serviceId,
            String topologyId,
            String nodeId,
            int instanceId) throws SalsaException {
        MutualFileAccessControl.lockFile();
        try {
            String serviceFile = SalsaConfiguration.getServiceStorageDir() + File.separator + serviceId + this.dataFileExtension;
            CloudService service = SalsaXmlDataProcess.readSalsaServiceFile(serviceFile);
            ServiceInstance rep = service.getInstanceById(nodeId, instanceId);
            Capabilities capas = rep.getCapabilities();
            if (capas == null) { // there is no capability list before, create a new
                capas = new Capabilities();
                rep.setCapabilities(capas);
            }
            List<SalsaCapaReqString> capaLst = capas.getCapability();
            //capaLst.add(data.getValue());
            // replace data if the value is "salsa:ip"
            if (data.getValue().equals("salsa:localIP")) {
                LOGGER.debug("Update instance unit capability - Get string => salsa:localIP");
                data.setValue(getIpOfServiceInstance(serviceId, nodeId, instanceId));
            }
            capaLst.add(data);
            SalsaXmlDataProcess.writeCloudServiceToFile(service, serviceFile);
        } catch (JAXBException | IOException ex) {
            throw new ServicedataProcessingException(serviceId, ex);
        } finally {
            MutualFileAccessControl.releaseFile();
        }

        return Response.status(200).entity("Updated capability for node: " + nodeId + " on service "
                + serviceId).build();
    }

    @Override
    public Response updateInstanceUnitProperties(
            //JAXBElement<Object> data,
            String data,
            String serviceId,
            String topologyId,
            String nodeId,
            int instanceId) throws SalsaException {
        LOGGER.debug("update instance unit prop 1. Raw string data: " + data);
        MutualFileAccessControl.lockFile();
        try {
            LOGGER.debug("update instance unit prop 2");
            String serviceFile = SalsaConfiguration.getServiceStorageDir() + File.separator + serviceId + this.dataFileExtension;
            LOGGER.debug("Setting property. Read service file: " + serviceFile);
            CloudService service = SalsaXmlDataProcess.readSalsaServiceFile(serviceFile);
            ServiceUnit unit = service.getComponentById(nodeId);
            LOGGER.debug("update instance unit prop 3: " + service.getId());
            ServiceInstance rep = service.getInstanceById(nodeId, instanceId);

            Properties props = rep.getProperties();
            if (props == null) {
                props = new Properties();
            }
            // marshall data and add to props. Currently only can receive VM and Docker properties
            JAXBContext context;

            if (unit.getType().equals(SalsaEntityType.OPERATING_SYSTEM.getEntityTypeString())) {
                LOGGER.debug("update instance unit: marshall VM description ");
                context = JAXBContext.newInstance(SalsaInstanceDescription_VM.class);
                Unmarshaller um = context.createUnmarshaller();
                SalsaInstanceDescription_VM propData = (SalsaInstanceDescription_VM) um.unmarshal(new StringReader(data));
                LOGGER.debug("VM properties captured: " + propData.getInstanceId() + ", ip: " + propData.getPrivateIp());
                props.setAny(propData);
            } else if (unit.getType().equals(SalsaEntityType.DOCKER.getEntityTypeString())) {
                LOGGER.debug("update instance unit: marshall DOCKER description ");
                context = JAXBContext.newInstance(SalsaInstanceDescription_Docker.class);
                Unmarshaller um = context.createUnmarshaller();
                SalsaInstanceDescription_Docker propData = (SalsaInstanceDescription_Docker) um.unmarshal(new StringReader(data));
                LOGGER.debug("Docker properties captured: " + propData.getDockername() + ", portmap: " + propData.getPortmap());
                props.setAny(propData);
            }

            rep.setProperties(props);
            SalsaXmlDataProcess.writeCloudServiceToFile(service, serviceFile);
            LOGGER.debug("update instance unit prop END: " + service.getId());

        } catch (JAXBException | IOException ex) {
            throw new ServicedataProcessingException(serviceId, ex);
        } finally {
            MutualFileAccessControl.releaseFile();
        }
        return Response.status(200).entity("Updated capability for node: " + nodeId + ", replica: " + instanceId + " on service "
                + serviceId).build();
    }

    @Override
    public Response updateNodeMetadata(
            String metadata,
            String serviceId,
            String topologyId,
            String nodeId) throws SalsaException {
        LOGGER.debug("update metadata: " + metadata);
        MutualFileAccessControl.lockFile();

        try {
            SalsaMsgUpdateMetadata data = SalsaMsgUpdateMetadata.fromJson(metadata);
            String serviceFile = SalsaConfiguration.getServiceStorageDir() + File.separator + serviceId + this.dataFileExtension;

            CloudService service = SalsaXmlDataProcess.readSalsaServiceFile(serviceFile);
            ServiceUnit node = service.getComponentById(nodeId);

            for (Map.Entry<String, String> entry : data.getActions().entrySet()) {
                node.addPrimitiveOperation(PrimitiveOperation.newCommandType(entry.getKey(), entry.getValue()));
            }            
            SalsaXmlDataProcess.writeCloudServiceToFile(service, serviceFile);

        } catch (JAXBException | IOException ex) {
            EngineLogger.logger.error(ex.getMessage());
        } finally {
            MutualFileAccessControl.releaseFile();
        }

        return Response.status(200).entity("Updated metadata for node: " + nodeId + ", on service " + serviceId).build();
    }

    @Override
    public Response updateNodeState(String serviceId,
            // topology is not need actually
            String topologyId,
            String nodeId,
            int instanceId,
            String value,
            String extra) throws SalsaException {
        try {
            LOGGER.debug("UPDATE NODE STATE: " + nodeId + ", instance: " + instanceId + ", state: " + value);
            MutualFileAccessControl.lockFile();
            String salsaFile = SalsaConfiguration.getServiceStorageDir() + File.separator + serviceId + this.dataFileExtension;
            CloudService service = SalsaXmlDataProcess.readSalsaServiceFile(salsaFile);
            ServiceUnit nodeData = service.getComponentById(nodeId);

            if (instanceId == -1) {
                nodeData.setState(SalsaEntityState.fromString(value));
                updateComponentStateBasedOnInstance(service);
                SalsaXmlDataProcess.writeCloudServiceToFile(service, salsaFile);
            } else { // update for instance
                ServiceInstance replicaInst = nodeData.getInstanceById(instanceId);
                if (SalsaEntityState.fromString(value) != null) {
                    replicaInst.setState(SalsaEntityState.fromString(value));
                    replicaInst.setExtra(extra);
                    updateComponentStateBasedOnInstance(service);	// update the Tosca node
                    SalsaXmlDataProcess.writeCloudServiceToFile(service, salsaFile);
                } else {
                    throw new ServicedataProcessingException(serviceId + "/" + nodeId + "/" + instanceId);
                }
            }

        } catch (JAXBException | IOException ex) {
            LOGGER.error("Failed to update node state of node: " + nodeId + ", instance: " + instanceId + ", state: " + value);
            throw new ServicedataProcessingException(serviceId, ex);
        } finally {
            MutualFileAccessControl.releaseFile();
        }
        LOGGER.debug("UPDATE NODE STATE: " + nodeId + ", instance: " + instanceId + ", state: " + value + " - DONE !");
        return Response.status(200).entity("Updated node " + nodeId + " on service " + serviceId + " deployed status: " + value).build();
    }

    @Override
    public Response getRequirementValue(
            String serviceId,
            String topologyId,
            String nodeId,
            int instanceId,
            String reqId) throws SalsaException {
        try {
            // read current TOSCA and SalsaCloudService
            String salsaFile = SalsaConfiguration.getServiceStorageDir() + File.separator + serviceId + this.dataFileExtension;
            CloudService service = SalsaXmlDataProcess.readSalsaServiceFile(salsaFile);
            ServiceTopology topo = service.getComponentTopologyById(topologyId);

            String toscaFile = SalsaConfiguration.getServiceStorageDir() + File.separator + serviceId;
            TDefinitions def = ToscaXmlProcess.readToscaFile(toscaFile);
            TRequirement req = (TRequirement) ToscaStructureQuery.getRequirementOrCapabilityById(reqId, def);
            TCapability capa = ToscaStructureQuery.getCapabilitySuitsRequirement(req, def);
            String capaid = capa.getId();
            TNodeTemplate toscanode = ToscaStructureQuery.getNodetemplateOfRequirementOrCapability(capa, def);
            // get the capability of the first instance of the node which have id
            ServiceUnit nodeData = topo.getComponentById(toscanode.getId());
            if (nodeData.getInstancesList().isEmpty()) {
                return Response.status(201).entity("").build();
            }
            ServiceInstance nodeInstanceOfCapa = nodeData.getInstanceById(0);
            String reqAndCapaValue = nodeInstanceOfCapa.getCapabilityValue(capaid);
            return Response.status(200).entity(reqAndCapaValue).build();
        } catch (JAXBException | IOException ex) {
            throw new ServicedataProcessingException(serviceId, ex);
        }

    }

    @Override
    public Response queueAction(String serviceId, String nodeId, int instanceId, String actionName) throws SalsaException {
        return queueActionWithParameter(serviceId, nodeId, instanceId, actionName, "");
    }

    @Override
    public Response queueActionWithParameter(
            String serviceId,
            String nodeId,
            int instanceId,
            String actionName, String parameters) throws SalsaException {
        try {
            EngineLogger.logger.debug("Queueing action: " + serviceId + "/" + nodeId + "/" + instanceId + "/" + actionName);
            MutualFileAccessControl.lockFile();
            String salsaFile = SalsaConfiguration.getServiceStorageDir() + File.separator + serviceId + this.dataFileExtension;
            CloudService service = SalsaXmlDataProcess.readSalsaServiceFile(salsaFile);
            ServiceUnit nodeData = service.getComponentById(nodeId);
            String topoID = service.getTopologyOfNode(nodeData.getId()).getId();

            ServiceInstance instance = nodeData.getInstanceById(instanceId);
            instance.queueAction(actionName);
            instance.setState(SalsaEntityState.STAGING_ACTION);

            // update to data file
            SalsaXmlDataProcess.writeCloudServiceToFile(service, salsaFile);
            MutualFileAccessControl.releaseFile();

            String runByMe = "";
            if (nodeData.getPrimitiveByName(actionName) != null) {
                runByMe = nodeData.getPrimitiveByName(actionName).getExecutionREF() + " " + parameters.replace(",", " ");
            } else {
                EngineLogger.logger.debug("There is no description for action: " + actionName + ". Pioneer will use its default operation.");
            }
            String preRunByMe = "";

            String newActionID = UUID.randomUUID().toString();
            EngineLogger.logger.debug("Converting SALSA node type to category. nodetype: {}", nodeData.getType());
            ServiceCategory theCategory = InfoParser.mapOldAndNewCategory(SalsaEntityType.fromString(nodeData.getType()));
            EngineLogger.logger.debug("Converted  SALSA node type to category. nodetype: {} == {}", nodeData.getType(), theCategory);
            // if the action name is undeploy, so undeploy. Only support artifact type of script. E.g., do not support stop docker
            if (actionName.equals("undeploy")) {
                PrimitiveOperation stopAction = nodeData.getPrimitiveByName("stop");
                if (stopAction != null) {  // there is a stop action, add it before the runByMe
                    LOGGER.debug("Found a stop action to  run before undeploy action");
                    preRunByMe = stopAction.getExecutionREF();
                    LOGGER.debug("The command will be sent is: " + preRunByMe + " and undeploy following: " + runByMe + ". Undeploy apply for node type: " + InfoParser.mapOldAndNewCategory(SalsaEntityType.fromString(nodeData.getType())));
                }
                // to undeploy Docker, we send the DockerID into runByMe
                if (theCategory == ServiceCategory.Docker) {
                    EngineLogger.logger.debug("The instance {}/{}/{} is Docker, checking docker property", serviceId, nodeId, instanceId);
                    if (instance.getProperties() != null) {
                        SalsaInstanceDescription_Docker vm = (SalsaInstanceDescription_Docker) instance.getProperties().getAny();
                        if (vm != null && vm.getInstanceId() != null) {
                            runByMe = vm.getInstanceId();
                            EngineLogger.logger.debug("The instance {}/{}/{} is Docker, send runByMe as docker id: {}", serviceId, nodeId, instanceId, runByMe);
                        }
                    } else {
                        EngineLogger.logger.error("Cannot get the property of docker node: {}/{}/{}", serviceId, nodeId, instanceId);
                    }
                }
                // TODO: maybe support more things
            }
            // search for the first artifact type which is not misc, which is used to install the service
            SalsaArtifactType artifactTypeOfDeployment = null;
            for (ServiceUnit.Artifacts art : nodeData.getArtifacts()) {
                if (!art.getType().equals(SalsaArtifactType.misc.getString())) {
                    artifactTypeOfDeployment = SalsaArtifactType.fromString(art.getType());
                    break;
                }
            }
            String pioneerID = PioneerManager.getPioneerIDForNode(SalsaConfiguration.getUserName(), serviceId, nodeId, instanceId, service);
            if (pioneerID == null) {
                EngineLogger.logger.error("The pioneer on node {}/{}/{}/{} is not registered to SalsaEngine, deployment aborted !", SalsaConfiguration.getUserName(), serviceId, nodeId, instanceId);
                return null;
            }
            EngineLogger.logger.debug("Found a pioneer to execute this action: {}", pioneerID);
            SalsaMsgConfigureArtifact configCommand = new SalsaMsgConfigureArtifact(newActionID, actionName, pioneerID, SalsaConfiguration.getUserName(), serviceId, topoID, nodeId, instanceId, theCategory, preRunByMe, runByMe, artifactTypeOfDeployment, "");
            ActionIDManager.addAction(newActionID, configCommand);
            SalsaMessage msg = new SalsaMessage(SalsaMessage.MESSAGE_TYPE.salsa_reconfigure, SalsaConfiguration.getSalsaCenterEndpoint(), SalsaMessageTopic.getPioneerTopicByID(pioneerID), "", configCommand.toJson());
            MessagePublishInterface publish = SalsaConfiguration.getMessageClientFactory().getMessagePublisher();
            publish.pushMessage(msg);
        } catch (JAXBException | IOException ex) {
            EngineLogger.logger.error("Unable to queue action. " + ex.getMessage());
            ex.printStackTrace();
            throw new ServicedataProcessingException(serviceId, ex);
        } finally {
            MutualFileAccessControl.releaseFile();
        }

        return Response.status(201).entity("ok").build();
    }

    @Override
    public Response unqueueAction(
            String serviceId,
            String nodeId,
            int instanceId) throws SalsaException {
        try {
            EngineLogger.logger.debug("Unqueueing action: " + serviceId + "/" + nodeId + "/" + instanceId);
            MutualFileAccessControl.lockFile();
            String salsaFile = SalsaConfiguration.getServiceStorageDir() + File.separator + serviceId + this.dataFileExtension;
            EngineLogger.logger.debug("Debug - Unqueueing action 1");
            CloudService service = SalsaXmlDataProcess.readSalsaServiceFile(salsaFile);
            EngineLogger.logger.debug("Debug - Unqueueing action 2");
            ServiceUnit nodeData = service.getComponentById(nodeId);
            EngineLogger.logger.debug("Debug - Unqueueing action 3. Node: " + nodeData.getId());
            ServiceInstance instance = nodeData.getInstanceById(instanceId);
            EngineLogger.logger.debug("Debug - Unqueueing action 4");
            EngineLogger.logger.debug("Debug - Instance: " + instance.getInstanceId() + "/" + instance.getState());
            instance.unqueueAction();
            EngineLogger.logger.debug("Debug - Unqueueing action 5");

            SalsaXmlDataProcess.writeCloudServiceToFile(service, salsaFile);
        } catch (JAXBException | IOException ex) {
            throw new ServicedataProcessingException(serviceId, ex);
        } finally {
            MutualFileAccessControl.releaseFile();
        }
        return Response.status(201).entity("ok").build();
    }

    protected void updateComponentStateBasedOnInstance(SalsaEntity nodeData) {
        Map<SalsaEntityState, Integer> rankState = new HashMap<>();
        rankState.put(SalsaEntityState.UNDEPLOYED, 0);
        rankState.put(SalsaEntityState.ERROR, 1);
        rankState.put(SalsaEntityState.ALLOCATING, 2);
        rankState.put(SalsaEntityState.STAGING, 3);
        rankState.put(SalsaEntityState.STAGING_ACTION, 4);
        rankState.put(SalsaEntityState.CONFIGURING, 5);
        rankState.put(SalsaEntityState.DEPLOYED, 7);
        rankState.put(SalsaEntityState.INSTALLING, 8);
        if (nodeData.getClass().equals(CloudService.class)) {
            LOGGER.debug("Updating the state of the whole service !");
        }

        List<SalsaEntity> insts = new ArrayList<>();
        if (nodeData.getClass().equals(ServiceUnit.class)) {
            List<ServiceInstance> serviceInst = ((ServiceUnit) nodeData).getInstancesList();
            for (ServiceInstance instance : serviceInst) {
                insts.add(instance);
            }
        } else if (nodeData.getClass().equals(ServiceTopology.class)) {
            List<ServiceUnit> serviceUnits = ((ServiceTopology) nodeData).getComponents();
            for (ServiceUnit unit : serviceUnits) {
                updateComponentStateBasedOnInstance(unit);
                insts.add(unit);
            }
        } else if (nodeData.getClass().equals(CloudService.class)) {
            List<ServiceTopology> serviceTopos = ((CloudService) nodeData).getComponentTopologyList();
            for (ServiceTopology topo : serviceTopos) {
                updateComponentStateBasedOnInstance(topo);
                insts.add(topo);
            }
        }

        int minState = 8;
        if (insts.isEmpty()) {
            nodeData.setState(SalsaEntityState.UNDEPLOYED);
            return;
        }
        for (SalsaEntity inst : insts) {
            if (minState >= rankState.get(inst.getState())) {
                nodeData.setState(inst.getState());
                minState = rankState.get(inst.getState());
            }
        }
    }

    @Override
    public String health() {
        try {
            List<String> str = SystemFunctions.getEndPoints();
            String rs = "";
            for (String s : str) {
                rs += s + ",";
            }
            return rs;
        } catch (MalformedObjectNameException | NullPointerException | UnknownHostException | AttributeNotFoundException | InstanceNotFoundException | MBeanException | ReflectionException e) {
            return "Working but cannot get the endpoint";
        }
    }

    // note: this function is not care about docker, just the VM
    @Override
    public String getGangliaHostInfo(String serviceID, String nodeID, int instanceID) {
        String salsaFile = SalsaConfiguration.getServiceStorageDir() + "/" + serviceID + ".data";
        CloudService service;
        try {
            service = SalsaXmlDataProcess.readSalsaServiceFile(salsaFile);
            return DynamicPlacementHelper.getGangliaVMInfo(service, nodeID, instanceID);
        } catch (JAXBException | IOException ex) {
            logger.error("Failed to get the Ganglia information for the service");
        }
        return null;
    }

    @Override
    public Response logMessage(String data) {
        EngineLogger.logger.debug("Receive a log message from pioneer: " + data);
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("/tmp/salsa.pioneer.message.log", true)))) {
            out.println(data);
            out.close();
        } catch (IOException e) {
            EngineLogger.logger.error(e.toString());
        }
        return Response.status(201).entity("saved message: " + data).build();
    }

}
