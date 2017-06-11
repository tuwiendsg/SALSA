/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.salsa.engine.services;

import at.ac.tuwien.dsg.salsa.database.neo4j.repo.CloudServiceRepository;
import at.ac.tuwien.dsg.salsa.database.neo4j.repo.ServiceInstanceRepository;
import at.ac.tuwien.dsg.salsa.database.neo4j.repo.ServiceUnitRepository;
import at.ac.tuwien.dsg.salsa.engine.exceptions.AppDescriptionException;
import at.ac.tuwien.dsg.salsa.engine.exceptions.PioneerManagementException;
import at.ac.tuwien.dsg.salsa.engine.services.enabler.OrchestrationManager;
import at.ac.tuwien.dsg.salsa.engine.services.algorithms.OrchestrationProcess;
import at.ac.tuwien.dsg.salsa.engine.services.algorithms.OrchestrationProcess_RoundCheck;
import at.ac.tuwien.dsg.salsa.engine.services.enabler.PioneerManager;
import at.ac.tuwien.dsg.salsa.engine.utils.SalsaConfiguration;
import at.ac.tuwien.dsg.salsa.messaging.messageInterface.MessageClientFactory;
import at.ac.tuwien.dsg.salsa.messaging.messageInterface.MessagePublishInterface;
import at.ac.tuwien.dsg.salsa.messaging.protocol.SalsaMessage;
import at.ac.tuwien.dsg.salsa.messaging.protocol.SalsaMessageTopic;
import at.ac.tuwien.dsg.salsa.model.CloudService;
import at.ac.tuwien.dsg.salsa.model.ServiceInstance;
import at.ac.tuwien.dsg.salsa.model.ServiceTopology;
import at.ac.tuwien.dsg.salsa.model.ServiceUnit;
import at.ac.tuwien.dsg.salsa.model.enums.ConfigurationState;
import at.ac.tuwien.dsg.salsa.model.enums.SalsaEntityType;
import at.ac.tuwien.dsg.salsa.model.properties.Capability;
import at.ac.tuwien.dsg.salsa.model.salsa.info.PioneerInfo;
import at.ac.tuwien.dsg.salsa.model.salsa.info.SalsaConfigureResult;
import at.ac.tuwien.dsg.salsa.model.salsa.info.SalsaException;
import at.ac.tuwien.dsg.salsa.model.salsa.info.SalsaMsgUpdateMetadata;
import generated.oasis.tosca.TDefinitions;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javax.annotation.PostConstruct;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author hungld
 */
public class ConfigurationServiceImp implements ConfigurationService {

    static Logger logger = LoggerFactory.getLogger("salsa");

    @Autowired
    CloudServiceRepository cloudServiceRepo;

    @Autowired
    ServiceUnitRepository unitRepo;

    @Autowired
    ServiceInstanceRepository instanceRepo;
    
    @PostConstruct
    public void init() {
        logger.debug("Sending message to sync pioneers ...");
        PioneerManager.removeAllPioneerInfo();
        SalsaMessage msg = new SalsaMessage(SalsaMessage.MESSAGE_TYPE.discover, SalsaConfiguration.getSalsaCenterEndpoint(), SalsaMessageTopic.PIONEER_REGISTER_AND_HEARBEAT, "", "toDiscoverPioneer");
        MessagePublishInterface publish = SalsaConfiguration.getMessageClientFactory().getMessagePublisher();
        publish.pushMessage(msg);
    }
   

    @Override
    public Response deployServiceFromPioneers(String pioneerIDs, String serviceName) throws SalsaException {
        if (!checkForServiceNameOk(serviceName)) {
            return Response.status(404).entity("Error. Service Name is bad: " + serviceName).build();
        }
        CloudService service = new CloudService();
        service.setName(serviceName);
        service.setUuid(UUID.randomUUID().toString());

        ServiceTopology topo = new ServiceTopology();
        topo.setUuid(UUID.randomUUID().toString());
        topo.setName("pioneerfarm");
        service.hasTopology(topo);

        ServiceUnit unit = new ServiceUnit(UUID.randomUUID().toString(), SalsaEntityType.OPERATING_SYSTEM.toString());
        topo.hasUnit(unit);

        String[] pis = pioneerIDs.split(" ");
        for (String pi : pis) {
            PioneerInfo info = PioneerManager.getPioneerMap().get(pi);
            if (info == null) {
                logger.error("Cannot create cloud service from pioneer. Pioneer with ID: {} is not synchronized yet!", pi);
                throw new PioneerManagementException(PioneerManagementException.Reason.PIONEER_NOT_REGISTERED, "Cannot find pioneer to add to the service: " + serviceName);
            }

            ServiceInstance instance = new ServiceInstance();
            instance.setUuid(UUID.randomUUID().toString());
            instance.setIndex(unit.nextIdCounter());
            unit.hasInstance(instance);
        }
        CloudService serviceOut = cloudServiceRepo.save(service);
        logger.debug("Create a service of pioneerfarm done ! Service output is: " + serviceOut.toJson());
        return Response.status(200).entity("Create a service of pioneerfarm done ! Service name:  " + serviceName).build();
    }

    private boolean checkForServiceNameOk(String serviceName) {
        if (serviceName.equals("")) {
            logger.debug("service name is exisited");
            return false;
        }
        CloudService service = cloudServiceRepo.findByName(serviceName);
        return service == null;
    }

    @Override
    public Response redeployService(String serviceId) throws SalsaException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Response undeployService(String serviceId) throws SalsaException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Response getService(String serviceName) throws SalsaException {
        CloudService service = cloudServiceRepo.findByName(serviceName);
        if (service != null) {
            return Response.status(201).entity(service).build();
        } else {
            return Response.status(500).entity("Service not found: " + serviceName).build();
        }
    }

    @Override
    public String getServiceNames() {
        Set<String> theSet = cloudServiceRepo.findAllServiceName();
        StringBuilder sb = new StringBuilder();
        for (String s : theSet) {
            sb.append(s).append(",");
        }
//        logger.debug("All available services: " + sb.toString());
        return sb.toString();
    }

    @Override
    public Response spawnInstance(String serviceId, String nodeId, int quantity) throws SalsaException {
//        logger.debug("Spawning new instance: {}/{}, quantity: {}" + serviceId, nodeId, quantity);
//        CloudService service = cloudServiceRepo.findByName(serviceId);
//        ServiceUnit node = service.getUnitByName(nodeId);
//        if (node == null) {
//            throw new IllegalConfigurationAPICallException("Cannot find the node with id: " + serviceId + "/" + nodeId);
//        }
//        int startCounter = node.getIdCounter() + 1;
//        node.setIdCounter(node.getIdCounter() + quantity);
//        unitRepo.save(node);

        return null;

    }

    @Override
    public Response updateInstanceState(String serviceId, String nodeId, int instanceId, String state) {
        try {
            logger.debug("Updating instance state: {}/{}/{} to: {}", serviceId, nodeId, instanceId, state);
            CloudService service = cloudServiceRepo.findByName(serviceId);
            ServiceInstance instance = service.getUnitByName(nodeId).getInstanceByIndex(instanceId);
            instance.setState(ConfigurationState.valueOf(state));
            cloudServiceRepo.save(service);
            return Response.status(200).entity("State of : " + serviceId + "/" + nodeId + "/" + instanceId + " is set to: " + state).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    @Override
    public Response updateInstanceProperties(SalsaConfigureResult data, String serviceId, String nodeId, int instanceId) {
        try {
            CloudService service = cloudServiceRepo.findByName(serviceId);
            ServiceInstance instance = service.getUnitByName(nodeId).getInstanceByIndex(instanceId);
            instance.setContext(data.getEffects());
            cloudServiceRepo.save(service);
            return Response.status(200).entity("Properties of : " + serviceId + "/" + nodeId + "/" + instanceId + " is set with: " + data.toJson()).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    @Override
    public Response destroyInstance(String serviceId, String nodeId, int instanceId) throws SalsaException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Response queueAction(String serviceId, String nodeId, int instanceId, String actionName) throws SalsaException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Response queueActionWithParameter(String serviceId, String nodeId, int instanceId, String actionName, String parameters) throws SalsaException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Response updateUnitMeta(String metadata, String serviceName, String nodeName) throws SalsaException {
        SalsaMsgUpdateMetadata metadataInfo = SalsaMsgUpdateMetadata.fromJson(metadata);
        CloudService service = cloudServiceRepo.findByName(serviceName);
        ServiceUnit unit = service.getUnitByName(nodeName);
        for (Map.Entry<String, String> entry : metadataInfo.getActions().entrySet()) {
            unit.hasCapability(new Capability(entry.getKey(), entry.getValue()));
        }
        cloudServiceRepo.save(service);
        return Response.accepted().entity("Update unit metadata done: " + nodeName).build();
    }

    @Override
    public String health() {
        return "healthy";
    }

}
