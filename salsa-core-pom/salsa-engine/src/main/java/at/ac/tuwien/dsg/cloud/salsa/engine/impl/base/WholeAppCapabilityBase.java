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
package at.ac.tuwien.dsg.cloud.salsa.engine.impl.base;

import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.CloudService;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.ServiceInstance;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.ServiceUnit;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.enums.SalsaEntityType;
import at.ac.tuwien.dsg.cloud.salsa.engine.utils.SalsaCenterConnector;
import at.ac.tuwien.dsg.cloud.salsa.engine.dataprocessing.SalsaXmlDataProcess;
import at.ac.tuwien.dsg.cloud.salsa.engine.capabilityinterface.WholeAppCapabilityInterface;
import at.ac.tuwien.dsg.cloud.salsa.engine.exceptions.EngineConnectionException;
import at.ac.tuwien.dsg.cloud.salsa.engine.exceptions.EngineMisconfiguredException;
import at.ac.tuwien.dsg.cloud.salsa.engine.exceptions.SalsaException;
import at.ac.tuwien.dsg.cloud.salsa.engine.exceptions.ServicedataProcessingException;
import at.ac.tuwien.dsg.cloud.salsa.engine.exceptions.AppDescriptionException;
import at.ac.tuwien.dsg.cloud.salsa.engine.impl.richInformationCapability.AsyncUnitCapability;
import at.ac.tuwien.dsg.cloud.salsa.engine.impl.genericCapability.InfoManagement;
import at.ac.tuwien.dsg.cloud.salsa.engine.utils.EngineLogger;
import at.ac.tuwien.dsg.cloud.salsa.engine.utils.PioneerManager;
import at.ac.tuwien.dsg.cloud.salsa.engine.utils.SalsaConfiguration;
import at.ac.tuwien.dsg.cloud.salsa.engine.dataprocessing.ToscaXmlProcess;
import at.ac.tuwien.dsg.cloud.salsa.engine.utils.EventPublisher;
import generated.oasis.tosca.TDefinitions;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBException;

/**
 *
 * @author Duc-Hung Le
 */
public class WholeAppCapabilityBase implements WholeAppCapabilityInterface {

    SalsaCenterConnector centerCon;

    {
        try {
            centerCon = new SalsaCenterConnector(SalsaConfiguration.getSalsaCenterEndpointLocalhost(), "/tmp", EngineLogger.logger);
        } catch (EngineConnectionException ex) {
            EngineLogger.logger.error("Cannot connect to SALSA service in localhost: " + SalsaConfiguration.getSalsaCenterEndpointLocalhost() + ". This is a fatal error !");
        }
    }

    File configFile;

    public WholeAppCapabilityBase() {
        this.configFile = SalsaConfiguration.getCloudUserParametersFile();
    }

    @Override
    public CloudService addService(String serviceName, TDefinitions def) throws SalsaException {        
        EventPublisher.publishINFO("Start to add a new cloud service with ID " + serviceName);
        if (configFile == null) {
            throw new EngineMisconfiguredException("./salsa.engine.properties", "The file is missing");
        }
        String deployID = serviceName;

        String ogininalToscaFile = SalsaConfiguration.getServiceStorageDir() + "/" + deployID + ".original";
        try {
            ToscaXmlProcess.writeToscaDefinitionToFile(def, ogininalToscaFile);
        } catch (JAXBException | IOException ex) {
            throw new ServicedataProcessingException(serviceName, ex);
        }

        // register service, all state is INITIAL
        String fullToscaFile = SalsaConfiguration.getServiceStorageDir() + "/" + deployID;

        try {
            ToscaXmlProcess.writeToscaDefinitionToFile(def, fullToscaFile);
        } catch (JAXBException | IOException ex) {
            throw new ServicedataProcessingException(serviceName, ex);
        }

        EngineLogger.logger.debug("debugggg Sep 8 - 1");

        // register service running data
        String fullSalsaDataFile = SalsaConfiguration.getServiceStorageDir() + "/" + deployID + ".data";
        EngineLogger.logger.debug("debugggg Sep 8 - 2");
        CloudService serviceData = null;
        try {
            serviceData = InfoManagement.buildRuntimeDataFromTosca(def);
        } catch (Exception e) {
            throw new AppDescriptionException("TOSCA description", "Cannot build the cloud service model from input TOSCA. Please check: ID consistency, relationship orders.", e);
        }
        EngineLogger.logger.debug("debugggg Sep 8 - 3");
        serviceData.setId(deployID);
        serviceData.setName(def.getId());
        SalsaXmlDataProcess.writeCloudServiceToFile(serviceData, fullSalsaDataFile);
        EngineLogger.logger.debug("debugggg Sep 8 - 4");

        EventPublisher.publishINFO("creating to add a new cloud service with ID " + serviceName);
        return actualCreateNewService(serviceData);
    }

    @Override
    public boolean cleanService(String serviceId) throws SalsaException {        
        EventPublisher.publishINFO("Start to clean service ID " + serviceId);
        CloudService service = centerCon.getUpdateCloudServiceRuntime(serviceId);
        if (service == null) {
            throw new ServicedataProcessingException(serviceId);
        }

        List<ServiceUnit> suList = service.getAllComponentByType(SalsaEntityType.OPERATING_SYSTEM);
        for (ServiceUnit su : suList) {
            if (su.getReference() == null) {
                List<ServiceInstance> repLst = su.getInstancesList();
                //List<ServiceInstance> repLst = service.getAllReplicaByType(SalsaEntityType.OPERATING_SYSTEM);
                for (ServiceInstance rep : repLst) {
                    if (rep.getProperties() != null) {
                        VMCapabilityBase vmCapa = new VMCapabilityBase();
                        vmCapa.remove(serviceId, su.getId(), rep.getInstanceId());
                    }
                }
            }
        }
        // unregister pioneers
        PioneerManager.removePioneerOfWholeService(SalsaConfiguration.getUserName(), serviceId);        
        EventPublisher.publishINFO("Clean service done: " + serviceId);
        return true;
    }

    private CloudService actualCreateNewService(CloudService serviceData) throws SalsaException {
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
                for (int i = 0; i < unit.getMin(); i++) {
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
