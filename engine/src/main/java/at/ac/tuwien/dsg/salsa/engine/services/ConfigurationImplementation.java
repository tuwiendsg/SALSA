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
import at.ac.tuwien.dsg.salsa.engine.exceptions.PioneerManagementException;
import static at.ac.tuwien.dsg.salsa.engine.services.ConfigurationServiceImp.logger;
import at.ac.tuwien.dsg.salsa.model.ServiceInstance;
import at.ac.tuwien.dsg.salsa.model.ServiceTopology;
import at.ac.tuwien.dsg.salsa.model.ServiceUnit;
import at.ac.tuwien.dsg.salsa.model.enums.SalsaEntityType;
import at.ac.tuwien.dsg.salsa.model.salsa.info.PioneerInfo;
import at.ac.tuwien.dsg.salsa.model.salsa.info.SalsaException;
import com.orientechnologies.orient.core.record.impl.ODocument;
import java.util.List;
import java.util.UUID;
import javax.annotation.PostConstruct;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
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
        if (salsaFile == null){
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
        List<CloudService> service = cloudServiceDao.readWithCondition("name="+serviceName);
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
    public Response getService(String serviceUUID) throws SalsaException {
        List<CloudService> services = cloudServiceDao.readAll();
        if (services != null && !services.isEmpty()){
            for (CloudService s:services){
                if (s.getName().equals(serviceUUID)){
                    return Response.status(201).entity(s.getName()).build();;
                }
            }
        }
        if (service != null) {
            return Response.status(201).entity(service).build();
        } else {
            return Response.status(500).entity("Service not found: " + serviceName).build();
        }
    }

    @Override
    public String getServiceNames() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Response updateUnitMeta(String metadata, String serviceId, String nodeId) throws SalsaException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Response spawnInstance(String serviceId, String nodeId) throws SalsaException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
