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
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.ServiceTopology;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.ServiceUnit;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.enums.SalsaEntityType;
import at.ac.tuwien.dsg.cloud.salsa.engine.utils.SalsaCenterConnector;
import at.ac.tuwien.dsg.cloud.salsa.engine.dataprocessing.SalsaXmlDataProcess;
import at.ac.tuwien.dsg.cloud.salsa.engine.capabilityinterface.WholeAppCapabilityInterface;
import at.ac.tuwien.dsg.cloud.salsa.engine.exceptions.EngineConnectionException;
import at.ac.tuwien.dsg.cloud.salsa.engine.exceptions.EngineMisconfiguredException;
import at.ac.tuwien.dsg.cloud.salsa.common.interfaces.SalsaException;
import at.ac.tuwien.dsg.cloud.salsa.engine.capabilityinterface.UnitCapabilityInterface;
import at.ac.tuwien.dsg.cloud.salsa.engine.exceptions.ServicedataProcessingException;
import at.ac.tuwien.dsg.cloud.salsa.engine.exceptions.AppDescriptionException;
import at.ac.tuwien.dsg.cloud.salsa.engine.impl.richInformationCapability.AsyncUnitCapability;
import at.ac.tuwien.dsg.cloud.salsa.engine.impl.genericCapability.InfoParser;
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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBException;

/**
 *
 * @author Duc-Hung Le
 */
public class WholeAppCapabilityBase implements WholeAppCapabilityInterface {

    SalsaCenterConnector centerCon;
    UnitCapabilityInterface asynCapa = new AsyncUnitCapability();

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
            serviceData = InfoParser.buildRuntimeDataFromTosca(def);
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
        centerCon = new SalsaCenterConnector(SalsaConfiguration.getSalsaCenterEndpointLocalhost(), "/tmp", EngineLogger.logger);
        CloudService service = centerCon.getUpdateCloudServiceRuntime(serviceId);
        if (service == null) {
            EngineLogger.logger.error("Cannot get the service information to delete its instances: {}", serviceId);
            throw new ServicedataProcessingException(serviceId);
        }

        List<ServiceUnit> suList = service.getAllComponentByType(SalsaEntityType.OPERATING_SYSTEM);
        if (suList == null){
            EngineLogger.logger.error("Cannot get the list ofthe VMs for service: {}", serviceId);
            return false;
        }
        EngineLogger.logger.debug("Trying to clean {} machine", suList.size());
        
        int numberOfReferenceNodes = 0;
        
        for (ServiceUnit su : suList) {
            EngineLogger.logger.debug("Checking to undeploy all instance of service unit: {}", su.getId());
            if (su.getReference() == null || su.getReference().isEmpty()) {
                List<ServiceInstance> repLst = su.getInstancesList();                
                EngineLogger.logger.debug("The service unit {} has {} instances", su.getId(), repLst.size());
                // Note: we must use this to ensure the dependency, so the higer stack will be removed before the VMs are removed
                for(ServiceInstance i: repLst){                    
                    EngineLogger.logger.debug("Calling API to remove instance: {}", i.getInstanceId());
                    asynCapa.remove(serviceId, su.getId(), i.getInstanceId());
//                    centerCon.removeOneInstance(serviceId, su.getId(), i.getInstanceId());
                }
            } else {
                EngineLogger.logger.debug("Node {} is a reference, to overpass it.", su.getId());
                numberOfReferenceNodes += su.getInstancesList().size();
            }
        }
        
        // wait until return true
        int count = 30;
        while (true){            
            service = centerCon.getUpdateCloudServiceRuntime(serviceId);            
            if (service == null){
                break;
            }
            List<ServiceInstance> instances = service.getAllReplicaByType(SalsaEntityType.OPERATING_SYSTEM);
            EngineLogger.logger.debug("Checking if all the instances of service {} are removed. There are {} left. Timeout in: {} times", serviceId, (instances.size() - numberOfReferenceNodes), count);
            if (instances.isEmpty() || count<1 || (instances.size() - numberOfReferenceNodes <=0)){
                break;
            }
            try {                
                // recheck every 3 second
                count = count - 1;
                Thread.sleep(3000);
            } catch (InterruptedException ex) {
                Logger.getLogger(WholeAppCapabilityBase.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        // unregister pioneers
        PioneerManager.removePioneerOfWholeService(SalsaConfiguration.getUserName(), serviceId);        
        EventPublisher.publishINFO("Clean service done: " + serviceId);
        return true;
    }

    private CloudService actualCreateNewService(CloudService serviceData) throws SalsaException {
        // here find all the TOP node
        if (serviceData == null){
            throw new ServicedataProcessingException("a new service");
        }
        EngineLogger.logger.debug("Start to process deployment with service data, id= {}", serviceData.getId());
        List<ServiceUnit> nodes = serviceData.getAllComponent();
         EngineLogger.logger.debug("Total number of nodes: {}", nodes.size());
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

        InfoParser.cloneDataForReferenceNodes(serviceData);

        // deploy new node by generate deployment threads for each service        
        for (ServiceUnit unit : topNodes) {
            ServiceUnit refUnit = InfoParser.getReferenceServiceUnit(unit);
            if (refUnit == null && unit.getMin() > 0) {		// not a reference and min > 0
                EngineLogger.logger.debug("Orchestating: Creating top node: " + unit.getId());
                // try to create minimum instance of software
                for (int i = 0; i < unit.getMin(); i++) {                    
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
