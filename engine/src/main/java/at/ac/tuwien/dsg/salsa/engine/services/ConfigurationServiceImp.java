/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.salsa.engine.services;

import at.ac.tuwien.dsg.salsa.database.neo4j.repo.CloudServiceRepository;
import at.ac.tuwien.dsg.salsa.engine.dataprocessing.ToscaXmlProcess;
import at.ac.tuwien.dsg.salsa.engine.exceptions.AppDescriptionException;
import at.ac.tuwien.dsg.salsa.engine.services.enabler.OrchestrationManager;
import at.ac.tuwien.dsg.salsa.engine.services.algorithms.OrchestrationProcess;
import at.ac.tuwien.dsg.salsa.engine.services.algorithms.OrchestrationProcess_Dummy;
import at.ac.tuwien.dsg.salsa.model.CloudService;
import at.ac.tuwien.dsg.salsa.model.salsa.info.SalsaException;
import generated.oasis.tosca.TDefinitions;
import java.io.File;
import java.io.IOException;
import java.util.UUID;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBException;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author hungld
 */
@Service
public class ConfigurationServiceImp implements ConfigurationService {

    static Logger logger = LoggerFactory.getLogger("salsa");

    @Autowired
    CloudServiceRepository cloudServiceRepo;

    @Override
    public Response deployServiceFromXML(String uploadedInputStream) throws SalsaException {
        String tmpID = UUID.randomUUID().toString();
        String tmpFile = "/tmp/salsa_tmp_" + tmpID;

        try {
            FileUtils.writeStringToFile(new File(tmpFile), uploadedInputStream);

            TDefinitions def = ToscaXmlProcess.readToscaFile(tmpFile);
            String serviceId = def.getId();
            if (!checkForServiceNameOk(serviceId)) {
                return Response.status(404).entity("Error. Service Name is bad: " + serviceId).build();
            }

            // convert from Tosca
            try {
                CloudService serviceData = InfoParser.buildRuntimeDataFromTosca(def);
                serviceData.setUuid(UUID.randomUUID().toString());
                serviceData.setName(def.getId());

                CloudService service = cloudServiceRepo.save(serviceData);
                logger.debug("Saved cloud service: \n " + service.toJson());

                OrchestrationManager.startDeployment(serviceData, new OrchestrationProcess_Dummy());

                // here run the orchestration algorithm
            } catch (Exception e) {
                throw new AppDescriptionException("TOSCA description", "Cannot build the cloud service model from input TOSCA. Please check: ID consistency, relationship orders.", e);
            }

            // return 201: resource created
            return Response.status(201).entity(serviceId).build();
        } catch (JAXBException e) {
            logger.error("Error when parsing Tosca: " + e);

            // return 400: bad request, the XML is malformed and could not process
            return Response.status(400).entity("Unable to parse the Tosca XML. Error: " + e).build();
        } catch (IOException e) {
            logger.error("Error reading file: " + tmpFile + ". Error: " + e);
            return Response.status(500).entity("Error when process Tosca file. Error: " + e).build();
        }
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
    public Response spawnInstance(String serviceId, String nodeId, int quantity) throws SalsaException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
    public String health() {
        return "healthy";
    }

}
