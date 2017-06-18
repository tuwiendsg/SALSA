/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.salsa.engine.services;

import at.ac.tuwien.dsg.salsa.database.orientdb.DAO.AbstractDAO;
import at.ac.tuwien.dsg.salsa.database.orientdb.DAO.CloudServiceDAO;
import at.ac.tuwien.dsg.salsa.engine.services.enabler.PioneerManager;
import at.ac.tuwien.dsg.salsa.engine.utils.SalsaConfiguration;
import at.ac.tuwien.dsg.salsa.messaging.messageInterface.MessagePublishInterface;
import at.ac.tuwien.dsg.salsa.messaging.protocol.SalsaMessage;
import at.ac.tuwien.dsg.salsa.messaging.protocol.SalsaMessageTopic;
import at.ac.tuwien.dsg.salsa.model.CloudService;
import at.ac.tuwien.dsg.salsa.description.ServiceFile;
import at.ac.tuwien.dsg.salsa.engine.exceptions.IllegalConfigurationAPICallException;
import at.ac.tuwien.dsg.salsa.engine.exceptions.PioneerManagementException;
import at.ac.tuwien.dsg.salsa.messaging.messageInterface.MessageClientFactory;
import at.ac.tuwien.dsg.salsa.model.ServiceInstance;
import at.ac.tuwien.dsg.salsa.model.ServiceTopology;
import at.ac.tuwien.dsg.salsa.model.ServiceUnit;
import at.ac.tuwien.dsg.salsa.model.enums.SalsaArtifactType;
import at.ac.tuwien.dsg.salsa.model.enums.SalsaEntityType;
import at.ac.tuwien.dsg.salsa.model.salsa.info.PioneerInfo;
import at.ac.tuwien.dsg.salsa.model.salsa.info.SalsaConfigureResult;
import at.ac.tuwien.dsg.salsa.model.salsa.info.SalsaConfigureTask;
import at.ac.tuwien.dsg.salsa.model.salsa.info.SalsaException;
import com.orientechnologies.orient.core.record.impl.ODocument;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import javax.annotation.PostConstruct;
import javax.ws.rs.core.Response;
import org.apache.commons.io.FileUtils;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.ext.multipart.MultipartBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * The implementation of central configuration service using OrientDB and DAO to
 * persist data
 *
 * @author hungld
 */
@Service
public class ConfigurationImplementation implements ConfigurationService {

    static Logger logger = LoggerFactory.getLogger("salsa");

    CloudServiceDAO cloudServiceDao = new CloudServiceDAO();
    AbstractDAO<ServiceInstance> instanceDao = new AbstractDAO<>(ServiceInstance.class);
    AbstractDAO<ServiceUnit> unitDao = new AbstractDAO<>(ServiceUnit.class);
    MessageClientFactory factory = new MessageClientFactory(SalsaConfiguration.getBroker(), SalsaConfiguration.getBrokerType());

    @PostConstruct
    public void init() {
        logger.debug("Sending message to sync pioneers ...");
        PioneerManager.removeAllPioneerInfo();
        SalsaMessage msg = new SalsaMessage(SalsaMessage.MESSAGE_TYPE.discover, SalsaConfiguration.getSalsaCenterEndpoint(), SalsaMessageTopic.PIONEER_REGISTER_AND_HEARBEAT, "", "toDiscoverPioneer");
        MessagePublishInterface publish = SalsaConfiguration.getMessageClientFactory().getMessagePublisher();
        publish.pushMessage(msg);
    }

    @Override
    public Response initServiceFiles(String serviceName) throws SalsaException {
        // read YML to file
        String serviceFolder = SalsaConfiguration.getServiceStorageDir(serviceName);
        logger.debug("Initiate service in the folder: {} \n", serviceFolder);
        String salsafile_str = "";
        try {
            salsafile_str = FileUtils.readFileToString(new File(serviceFolder + "/Salsafile.yml"), "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            logger.debug("Cannot read Salsafile at: {}", serviceFolder + "/Salsafile.yml");
            return Response.status(401).entity("Cannot read Salsafile at: " + serviceFolder + "/Salsafile.yml").build();
        }
        ServiceFile salsaFile = ServiceFile.fromYaml(salsafile_str);
        if (salsaFile == null) {
            return Response.status(401).entity("Cannot load the YAML data.").build();
        }
        logger.debug("Load service file: " + salsaFile.toJson());
        CloudService service = salsaFile.toCloudService();
        service.updateMissingUUID();
        if (serviceName != null && !serviceName.isEmpty()) {
            service.setName(serviceName);
        }
        logger.debug("Loaded service: " + service.toJson());

        cloudServiceDao.save(service);

        return Response.status(200).entity("Service is created and in deployment process: " + salsaFile.getName()).build();
    }

    @Override
    public Response uploadServiceFile(String serviceName, MultipartBody body) throws SalsaException {
        try {
            String serviceFolder = SalsaConfiguration.getServiceStorageDir(serviceName);
            for (Attachment attachment : body.getAllAttachments()) {
                logger.debug("Uploaded file: " + attachment);
                attachment.transferTo(new File(serviceFolder + attachment.toString()));
            }
            return Response.ok("File upload done!").build();
        } catch (Exception ex) {
            logger.error("uploadFile.error():", ex);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    private boolean checkForServiceNameOk(String serviceName) {
        if (serviceName.equals("")) {
            logger.debug("service name is empty");
            return false;
        }
        List<CloudService> service = cloudServiceDao.readWithCondition("name=" + serviceName);
        return (service == null || service.isEmpty());
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

        ServiceUnit unit = new ServiceUnit(SalsaEntityType.OPERATING_SYSTEM.toString());
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
        service.updateMissingUUID();
        ODocument odoc = cloudServiceDao.save(service);
        logger.debug("Create a service of pioneerfarm done ! Service output is: " + odoc.toString());
        return Response.status(200).entity("Create a service of pioneerfarm done ! Service name:  " + serviceName).build();

    }

    @Override
    public Response redeployService(String serviceName) throws SalsaException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Response undeployService(String serviceName) throws SalsaException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Response getService(String serviceName) throws SalsaException {
        CloudService service = cloudServiceDao.readByName(serviceName);
        if (service != null) {
            return Response.status(201).entity(service.toJson()).build();
        } else {
            return Response.status(500).entity("Service not found: " + serviceName).build();
        }
//        
//        List<CloudService> services = cloudServiceDao.readAll();
//        if (services != null && !services.isEmpty()) {
//            for (CloudService s : services) {
//                if (s.getName().equals(serviceName)) {
//                    return Response.status(201).entity(s.toJson()).build();
//                }
//            }
//        }
//        return Response.status(500).entity("Service not found: " + serviceName).build();
    }

    @Override
    public String getServiceNames() {
        List<CloudService> services = cloudServiceDao.readAll();
        logger.debug("Get list of cloud service name: " + services.size());
        StringBuilder sb = new StringBuilder();
        for (CloudService s : services) {
            logger.debug("Reading service: " + s.getName() + "/" + s.getUuid());
            sb.append(s.getName()).append(",");
        }
        logger.debug("All available services: " + sb.toString());
        return sb.toString();
    }

    private CloudService getCloudServiceByName(String serviceName) {
        List<CloudService> services = cloudServiceDao.readAll();
        CloudService service = null;
        for (CloudService s : services) {
            if (s.getName().trim().equals(serviceName.trim())) {
                service = s;
                break;
            }
        }
        return service;
    }

    private ServiceUnit getUnitByName(String serviceName, String unitName) {
        CloudService service = getCloudServiceByName(serviceName);
        if (service != null) {
            return service.getUnitByName(unitName);
        }
        return null;
    }

    @Override
    public Response updateUnitMeta(String metadata, String serviceName, String nodeName) throws SalsaException {
        try {
            logger.debug("Updating unit metadata for {}/{}, data: {}", serviceName, nodeName, metadata);
            ServiceUnit unit = getUnitByName(serviceName, nodeName);
            // todo: set metadata

            unitDao.save(unit);
            return Response.status(200).entity("Metadata of : " + serviceName + "/" + nodeName + " is set to: " + metadata).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    @Override
    public Response deployInstance(String serviceName, String nodeName, String pioneerUUID) throws SalsaException {
        logger.debug("Spawning new instance: {}/{}, quantity: {}" + serviceName, nodeName);
        CloudService service = getCloudServiceByName(serviceName);
        ServiceTopology topo = service.getTopologyOfNode(nodeName);
        ServiceUnit node = service.getUnitByName(nodeName);
        if (node == null) {
            throw new IllegalConfigurationAPICallException("Cannot spawn instance for node: " + serviceName + "/" + nodeName + ". Invalid node name.");
        }
        MessagePublishInterface publish = factory.getMessagePublisher();
        String pioneerID_to_deploy;
        if (pioneerUUID == null || pioneerUUID.isEmpty()) {
            if (node.getPioneerIds() == null || node.getPioneerIds().isEmpty()) {
                return Response.status(401).entity("No pioneer found to handle deployment of: " + serviceName + "/" + nodeName).build();
            } else {
                pioneerID_to_deploy = node.getPioneerIds().get(0);
            }
        } else {
            pioneerID_to_deploy = pioneerUUID;
        }

        node.setIdCounter(node.getIdCounter() + 1);
        SalsaConfigureTask confTask = new SalsaConfigureTask("deploy", pioneerID_to_deploy, SalsaConfiguration.getUserName(), serviceName, topo.getName(), nodeName, 0, SalsaArtifactType.salsa_internal_deploy);

        publish.pushMessage(
                new SalsaMessage(SalsaMessage.MESSAGE_TYPE.salsa_deploy_instance, pioneerID_to_deploy, SalsaMessageTopic.CENTER_REQUEST_PIONEER, "", confTask.toJson()));
        return Response.status(
                200).entity("Request sent to pioneer to deploy instance: " + serviceName + "/" + nodeName + "/" + node.getIdCounter()).build();
    }

    @Override
    public Response destroyInstance(String serviceName, String nodeName, int instanceId) throws SalsaException {
        logger.debug("Spawning new instance: {}/{}, quantity: {}" + serviceName, nodeName);
        CloudService service = getCloudServiceByName(serviceName);
        ServiceTopology topo = service.getTopologyOfNode(nodeName);
        ServiceUnit node = service.getUnitByName(nodeName);
        if (node == null) {
            throw new IllegalConfigurationAPICallException("Cannot spawn instance for node: " + serviceName + "/" + nodeName + ". Invalid node name.");
        }
        MessagePublishInterface publish = factory.getMessagePublisher();
        String pioneerID_to_deploy = node.getPioneerIds().get(0);
        node.setIdCounter(node.getIdCounter() + 1);
        SalsaConfigureTask confTask = new SalsaConfigureTask("undeploy", pioneerID_to_deploy, SalsaConfiguration.getUserName(), serviceName, topo.getName(), nodeName, 0, SalsaArtifactType.salsa_internal_deploy);

        publish.pushMessage(new SalsaMessage(SalsaMessage.MESSAGE_TYPE.salsa_reconfigure_instance, SalsaConfiguration.getSalsaCenterEndpoint(), SalsaMessageTopic.CENTER_REQUEST_PIONEER, "", confTask.toJson()));
        return Response.status(200).entity("Request sent to pioneer to deploy instance: " + serviceName + "/" + nodeName + "/" + node.getIdCounter()).build();
    }

    @Override
    public Response updateInstanceState(String json, String serviceName, String nodeName, int instanceId) {
        try {
            logger.debug("Updating instance state: {}/{}/{}", serviceName, nodeName, instanceId);
            CloudService service = getCloudServiceByName(serviceName);
            SalsaConfigureResult confResult = SalsaConfigureResult.fromJson(json);
            logger.debug("Updating state: " + confResult.toJson());
            ServiceInstance instance = service.getUnitByName(nodeName).getInstanceByIndex(instanceId);
            instance.updateState(confResult);
            instanceDao.save(instance);

            return Response.status(200).entity("State of : " + serviceName + "/" + nodeName + "/" + instanceId + " is set to: " + confResult.toJson()).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    @Override
    public Response queueAction(String serviceName, String nodeName, int instanceId, String actionName) throws SalsaException {
        logger.debug("Sending an action to pioneer to queue up: {}/{}/{}/{}", serviceName, nodeName, instanceId, actionName);
        CloudService service = getCloudServiceByName(serviceName);
        ServiceTopology topo = service.getTopologyOfNode(nodeName);
        ServiceUnit unit = service.getUnitByName(nodeName);
        ServiceInstance instance = unit.getInstanceByIndex(instanceId);
        SalsaConfigureTask confTask = new SalsaConfigureTask(actionName, instance.getPioneer(), SalsaConfiguration.getUserName(), serviceName, topo.getName(), nodeName, instanceId, SalsaArtifactType.sh);
        MessagePublishInterface publish = factory.getMessagePublisher();
        publish.pushMessage(new SalsaMessage(SalsaMessage.MESSAGE_TYPE.salsa_reconfigure_instance, SalsaConfiguration.getSalsaCenterEndpoint(), SalsaMessageTopic.CENTER_REQUEST_PIONEER, "", confTask.toJson()));
        logger.debug("Queue an action done: {}/{}/{}/{} to Pioneer: {}", serviceName, nodeName, instanceId, actionName, instance.getPioneer());
        return Response.status(200).entity("Queue action is done for pioneer: " + instance.getId()).build();
    }

    @Override
    public Response queueActionWithParameter(String serviceName, String nodeName, int instanceId, String actionName, String parameters) throws SalsaException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String health() {
        return "healthy";
    }

}
