/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.salsa.engine.impl.base;

import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.CloudService;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.ServiceInstance;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.ServiceUnit;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.enums.SalsaEntityType;
import at.ac.tuwien.dsg.cloud.salsa.common.processing.SalsaCenterConnector;
import at.ac.tuwien.dsg.cloud.salsa.common.processing.SalsaXmlDataProcess;
import at.ac.tuwien.dsg.cloud.salsa.engine.capabilityinterface.WholeAppCapabilityInterface;
import at.ac.tuwien.dsg.cloud.salsa.engine.exception.SalsaEngineException;
import at.ac.tuwien.dsg.cloud.salsa.engine.impl.MiddleLevel.AsyncUnitCapability;
import at.ac.tuwien.dsg.cloud.salsa.engine.impl.MiddleLevel.InfoManagement;
import at.ac.tuwien.dsg.cloud.salsa.engine.utils.EngineLogger;
import at.ac.tuwien.dsg.cloud.salsa.engine.utils.SalsaConfiguration;
import at.ac.tuwien.dsg.cloud.salsa.tosca.processing.ToscaXmlProcess;
import generated.oasis.tosca.TDefinitions;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author hungld
 */
public class WholeAppCapability implements WholeAppCapabilityInterface {

    SalsaCenterConnector centerCon = new SalsaCenterConnector(SalsaConfiguration.getSalsaCenterEndpointLocalhost(), "/tmp", EngineLogger.logger);

    File configFile;

    public WholeAppCapability() {
        this.configFile = SalsaConfiguration.getCloudUserParametersFile();
    }

    @Override
    public CloudService addService(String serviceName, TDefinitions def) throws SalsaEngineException {
        if (configFile == null) {
            EngineLogger.logger.error("No config file specified");
            throw new SalsaEngineException("There is no SALSA configuation file specific. Please check /etc/salsa.engine.properties", true);
        }

        String deployID = serviceName;
        EngineLogger.logger.info("Orchestrating service id: " + deployID);

        String ogininalToscaFile = SalsaConfiguration.getServiceStorageDir() + "/" + deployID + ".original";
        ToscaXmlProcess.writeToscaDefinitionToFile(def, ogininalToscaFile);

        // register service, all state is INITIAL
        String fullToscaFile = SalsaConfiguration.getServiceStorageDir() + "/" + deployID;

        ToscaXmlProcess.writeToscaDefinitionToFile(def, fullToscaFile);

        EngineLogger.logger.debug("debugggg Sep 8 - 1");

        // register service running data
        String fullSalsaDataFile = SalsaConfiguration.getServiceStorageDir() + "/" + deployID + ".data";
        EngineLogger.logger.debug("debugggg Sep 8 - 2");
        CloudService serviceData = InfoManagement.buildRuntimeDataFromTosca(def);
        EngineLogger.logger.debug("debugggg Sep 8 - 3");
        serviceData.setId(deployID);
        serviceData.setName(def.getId());
        SalsaXmlDataProcess.writeCloudServiceToFile(serviceData, fullSalsaDataFile);
        EngineLogger.logger.debug("debugggg Sep 8 - 4");

        return actualCreateNewService(serviceData);
    }

    @Override
    public boolean cleanService(String serviceId) throws SalsaEngineException {
        CloudService service = centerCon.getUpdateCloudServiceRuntime(serviceId);
        if (service == null) {
            EngineLogger.logger.error("Cannot clean service. Service description is not found.");
            throw new SalsaEngineException("Cannot clean service. Service description is not found for service: " + serviceId, false);
        }

        List<ServiceUnit> suList = service.getAllComponentByType(SalsaEntityType.OPERATING_SYSTEM);
        for (ServiceUnit su : suList) {
            if (su.getReference() == null) {
                List<ServiceInstance> repLst = su.getInstancesList();
                //List<ServiceInstance> repLst = service.getAllReplicaByType(SalsaEntityType.OPERATING_SYSTEM);
                for (ServiceInstance rep : repLst) {
                    if (rep.getProperties() != null) {
                        VMCapability vmCapa = new VMCapability();
                        vmCapa.remove(serviceId, su.getId(), rep.getInstanceId());
                    }
                }
            }
        }
        return true;
    }

    private CloudService actualCreateNewService(CloudService serviceData) throws SalsaEngineException {
        // here find all the TOP node
        List<ServiceUnit> nodes = serviceData.getAllComponent();
        List<ServiceUnit> topNodes = new ArrayList<>();
        for (ServiceUnit node : nodes) {
            boolean getIt = true;
            for (ServiceUnit t : nodes) {
                if (t.getHostedId().equals(node.getId())) {
                    getIt = false;
                    EngineLogger.logger.debug("Orchestating: Discard node: " + node.getId());
                    break;
                }
            }
            if (getIt) {
                EngineLogger.logger.debug("Orchestating: Get top node: " + node.getId());
                topNodes.add(node);
            }
        }

        InfoManagement.cloneDataForReferenceNodes(serviceData);

        // deploy new node by generate deployment threads for each service        
        for (ServiceUnit unit : topNodes) {
            ServiceUnit refUnit = InfoManagement.getReferenceServiceUnit(unit);
            if (refUnit == null && unit.getMin() > 0) {		// not a reference and min > 0
                EngineLogger.logger.debug("Orchestating: Creating top node: " + unit.getId());
                // try to create minimum instance of software
                for (int i = 1; i <= unit.getMin(); i++) {
                    AsyncUnitCapability asynCapa = new AsyncUnitCapability();
                    asynCapa.deploy(serviceData.getId(), unit.getId(), i);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        EngineLogger.logger.error("Thread interrupted !");
                    }
                }
            }
        }
        return serviceData;
    }

}
