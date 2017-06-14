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
import static at.ac.tuwien.dsg.salsa.engine.services.ConfigurationServiceImp.logger;
import at.ac.tuwien.dsg.salsa.model.ServiceInstance;
import at.ac.tuwien.dsg.salsa.model.ServiceTopology;
import at.ac.tuwien.dsg.salsa.model.ServiceUnit;
import at.ac.tuwien.dsg.salsa.model.enums.ConfigurationState;
import at.ac.tuwien.dsg.salsa.model.enums.SalsaEntityType;
import at.ac.tuwien.dsg.salsa.model.salsa.info.PioneerInfo;
import at.ac.tuwien.dsg.salsa.model.salsa.info.SalsaException;
import com.orientechnologies.orient.core.record.impl.ODocument;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.annotation.PostConstruct;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The implementation of central configuration service using OrientDB and DAO to
 * persist data
 *
 * @author hungld
 */
public class ConfigurationImplementation implements ConfigurationService {

    static Logger logger = LoggerFactory.getLogger("salsa");

    CloudServiceDAO cloudServiceDao = new CloudServiceDAO();
    AbstractDAO<ServiceUnit> unitDao = new AbstractDAO<>(ServiceUnit.class);

    @PostConstruct
    public void init() {
        logger.debug("Sending message to sync pioneers ...");
        PioneerManager.removeAllPioneerInfo();
        SalsaMessage msg = new SalsaMessage(SalsaMessage.MESSAGE_TYPE.discover, SalsaConfiguration.getSalsaCenterEndpoint(), SalsaMessageTopic.PIONEER_REGISTER_AND_HEARBEAT, "", "toDiscoverPioneer");
        MessagePublishInterface publish = SalsaConfiguration.getMessageClientFactory().getMessagePublisher();
        publish.pushMessage(msg);
    }

    @Override
    public Response deployServiceFromYML(String uploadedInputStream) throws SalsaException {
        // read YML to file
        logger.debug("Loading YAML file: \n" + uploadedInputStream);
        ServiceFile salsaFile = ServiceFile.fromYaml(uploadedInputStream);
        if (salsaFile == null) {
            return Response.status(401).entity("Cannot load the YAML data").build();
        }
        CloudService service = salsaFile.toCloudService();

        return Response.status(200).entity("Service is created and in deployment process: " + salsaFile.getName()).build();
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
        ODocument odoc = cloudServiceDao.save(service);
        logger.debug("Create a service of pioneerfarm done ! Service output is: " + odoc.toString());
        return Response.status(200).entity("Create a service of pioneerfarm done ! Service name:  " + serviceName).build();

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
        List<CloudService> services = cloudServiceDao.readAll();
        if (services != null && !services.isEmpty()) {
            for (CloudService s : services) {
                if (s.getName().equals(serviceName)) {
                    return Response.status(201).entity(s.getName()).build();
                }
            }
        }
        return Response.status(500).entity("Service not found: " + serviceName).build();
    }

    @Override
    public String getServiceNames() {
        List<CloudService> services = cloudServiceDao.readAll();

        StringBuilder sb = new StringBuilder();
        for (CloudService s : services) {
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
    public Response updateUnitMeta(String metadata, String serviceName, String nodeId) throws SalsaException {
        try {
            logger.debug("Updating unit metadata for {}/{}, data: {}", serviceName, nodeId, metadata);
            CloudService service = getCloudServiceByName(serviceName);
            ServiceUnit unit = getUnitByName(serviceName, nodeId);
            // todo: set metadata

            unitDao.save(unit);
            return Response.status(200).entity("Metadata of : " + serviceName + "/" + nodeId + " is set to: " + metadata).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    @Override
    public Response spawnInstance(String serviceName, String nodeId) throws SalsaException {
        logger.debug("Spawning new instance: {}/{}, quantity: {}" + serviceName, nodeId);
        CloudService service = getCloudServiceByName(serviceName);
        ServiceUnit node = service.getUnitByName(nodeId);
        if (node == null) {
            throw new IllegalConfigurationAPICallException("Cannot spawn instance for node: " + serviceName + "/" + nodeId + ". Invalid node name.");
        }
        node.setIdCounter(node.getIdCounter() + 1);
        return Response.status(200).entity("Request sent to pioneer to deploy instance: " + serviceName + "/" + nodeId + "/" + node.getIdCounter()).build();

    }

    @Override
    public Response destroyInstance(String serviceId, String nodeId, int instanceId) throws SalsaException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Response updateInstanceState(String json, String serviceId, String nodeId, int instanceId) {
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
    public String health() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
