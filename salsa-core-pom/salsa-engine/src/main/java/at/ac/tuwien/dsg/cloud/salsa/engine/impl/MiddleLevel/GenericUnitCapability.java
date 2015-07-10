/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.salsa.engine.impl.MiddleLevel;

import at.ac.tuwien.dsg.cloud.salsa.engine.impl.base.AppCapability;
import at.ac.tuwien.dsg.cloud.salsa.engine.impl.base.VMCapability;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.CloudService;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.ServiceInstance;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.ServiceUnit;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.enums.SalsaEntityState;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.enums.SalsaEntityType;
import at.ac.tuwien.dsg.cloud.salsa.common.processing.SalsaCenterConnector;
import at.ac.tuwien.dsg.cloud.salsa.engine.exception.SalsaEngineException;
import at.ac.tuwien.dsg.cloud.salsa.engine.capabilityinterface.UnitCapabilityInterface;
import at.ac.tuwien.dsg.cloud.salsa.engine.utils.EngineLogger;
import at.ac.tuwien.dsg.cloud.salsa.engine.utils.SalsaConfiguration;
import java.util.List;

/**
 * This class generalize the capability of application and VM into service unit.
 * The states control is enabled here.
 * @author hungld
 */
public class GenericUnitCapability implements UnitCapabilityInterface {
    SalsaCenterConnector centerCon = new SalsaCenterConnector(SalsaConfiguration.getSalsaCenterEndpointLocalhost(), "/tmp", EngineLogger.logger);
    @Override
    public ServiceInstance deploy(String serviceId,  String nodeId, int instanceId) throws SalsaEngineException {
        CloudService newservice = centerCon.getUpdateCloudServiceRuntime(serviceId);
        String topologyId = newservice.getTopologyOfNode(nodeId).getId();
        ServiceUnit node = newservice.getComponentById(topologyId, nodeId);
        EngineLogger.logger.debug("Node type: " + node.getType() + ". String: " + SalsaEntityType.OPERATING_SYSTEM.getEntityTypeString());
        ServiceInstance repData = new ServiceInstance(instanceId, null);
        repData.setState(SalsaEntityState.ALLOCATING);
        repData.setHostedId_Integer(2147483647);
        
        java.util.Date date = new java.util.Date();
        EngineLogger.logger.debug("TIMESTAMP - Node: " + nodeId + "/" + instanceId + ", state: Allocating(manual)" + ", Time: " + date.getTime());

        if (node.getType().equals(SalsaEntityType.OPERATING_SYSTEM.getEntityTypeString())) {
            if (node.getInstanceNumber() >= node.getMax()) {
                EngineLogger.logger.error("Not enough cloud resource quota for the node: " + nodeId + ". Quit !");
                // out of quota
                throw new SalsaEngineException("Not enough cloud resource quota to deploy the node: " + nodeId, false);
            } else {
                centerCon.addInstanceUnitMetaData(serviceId, topologyId, nodeId, repData);
                VMCapability vmCap = new VMCapability();
                vmCap.deploy(serviceId, nodeId, instanceId);
                centerCon.updateNodeState(serviceId, topologyId, nodeId, instanceId, SalsaEntityState.CONFIGURING);
                EngineLogger.logger.debug("Updated VM state for node: " + nodeId + " to CONFIGURING !");
                return new ServiceInstance(instanceId);
            }
        } else {
            centerCon.addInstanceUnitMetaData(serviceId, topologyId, nodeId, repData);
            AppCapability appCapa = new AppCapability();
            appCapa.deploy(serviceId, nodeId, instanceId);
            centerCon.updateNodeState(serviceId, topologyId, nodeId, instanceId, SalsaEntityState.STAGING); // high level done, this line will trigger a pioneer to execute deployment
            return new ServiceInstance(instanceId);
        }
    }
    
    @Override
    public void remove(String serviceId, String nodeId, int instanceId) throws SalsaEngineException {
        CloudService service = centerCon.getUpdateCloudServiceRuntime(serviceId);
        ServiceUnit node = service.getComponentById(nodeId);        
        ServiceInstance instance = node.getInstanceById(instanceId);
        if (instance.getState().equals(SalsaEntityState.ALLOCATING)) {
            EngineLogger.logger.debug("Just remove metadata");
            //TODO: REMOVE METADATA HERE !
            return;
        }
        List<ServiceUnit> listUnit = service.getAllComponent();

        // undeploy dependency chain first. It is recursive.
        for (ServiceUnit u : listUnit) {  // all the unit of the service
            EngineLogger.logger.debug("Checking if this unit: " + u.getId() + " is HOSTED ON current removing node: " + nodeId);
            if (u.getHostedId().equals(nodeId)) {    // this is hosted on the node we want to remove. remove it first                        
                EngineLogger.logger.debug("YES! Now check instance of node: " + u.getId() + " is HOSTED ON current removing node: " + nodeId);
                for (ServiceInstance i : u.getInstanceHostOn(instanceId)) {   // the instance of above unit and hosted on current instance
                    EngineLogger.logger.debug("Found instance need to be remove first: " + u.getId() + "/" + i.getInstanceId());
                    GenericUnitCapability geneCapa = new GenericUnitCapability();
                    geneCapa.remove(serviceId, u.getId(), i.getInstanceId());                    
                }
            }
            for (String connectoId : u.getConnecttoId()) {    // the unit u can connect to something
                EngineLogger.logger.debug("Checking if this unit: " + u.getId() + " is CONNECT TO current removing node: " + nodeId);
                if (connectoId.equals(nodeId)) {             // which can be this node
                    EngineLogger.logger.debug("YES! Now checking instance of node: " + u.getId() + " is CONNECT TO current removing node: " + nodeId);
                    for (ServiceInstance i : u.getInstancesList()) {   // remove all its instance
                        EngineLogger.logger.debug("Found instance need to be remove first: " + u.getId() + "/" + i.getInstanceId());
                        GenericUnitCapability geneCapa = new GenericUnitCapability();
                        geneCapa.remove(serviceId, u.getId(), i.getInstanceId());
                    }
                }
            }
        }
        EngineLogger.logger.debug("The dependency should be cleaned for the node: " + nodeId + "/" + instanceId);

                // check all instance of hoston and connect to are undeployed
        // do similar things and check if all the list are empty
        boolean cleaned;
        do {
            service = centerCon.getUpdateCloudServiceRuntime(serviceId);
            node = service.getComponentById(nodeId);            
            listUnit = service.getAllComponent();
            cleaned = true;
            EngineLogger.logger.debug("Checking if dependency is really clean for node: " + nodeId + "/" + instanceId);
            for (ServiceUnit u : listUnit) {
                if (u.getHostedId().equals(nodeId)) {
                    EngineLogger.logger.debug("Checking if dependency is really clean for node: " + nodeId + "/" + instanceId + ". Checking unit: " + u.getId() + " which number of hosted on inst: " + u.getInstanceHostOn(instanceId).size());
                    if (!u.getInstanceHostOn(instanceId).isEmpty()) {
                        EngineLogger.logger.debug("Waiting for cleaning HOST ON nodes of: " + nodeId + "/" + instanceId + ". Nodes left: " + u.getInstanceHostOn(instanceId).size());
                        for (ServiceInstance debugI : u.getInstanceHostOn(instanceId)) {
                            EngineLogger.logger.debug("They are: " + u.getId() + "/" + debugI.getInstanceId());
                        }
                        cleaned = false;
                    }
                }
                for (String connectoId : u.getConnecttoId()) {
                    if (connectoId.equals(nodeId)) {
                        if (!u.getInstancesList().isEmpty()) {
                            EngineLogger.logger.debug("Waiting for cleaning CONNECT TO nodes of: " + nodeId + "/" + instanceId + ". Nodes left: " + u.getInstancesList().size());
                            cleaned = false;
                        }
                    }
                }
            }
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
            }
        } while (cleaned == false);

        EngineLogger.logger.debug("It is TRUE, the dependency is now cleaned for the node: " + nodeId + "/" + instanceId);

        // Call appropriate catapbility based on type
        if (node.getType().equals(SalsaEntityType.OPERATING_SYSTEM.getEntityTypeString())) {
            VMCapability vmCapability = new VMCapability();
            vmCapability.remove(serviceId, nodeId, instanceId);
        } else {
            AppCapability appCapa = new AppCapability();
            appCapa.remove(serviceId, nodeId, instanceId);
        }
    }
}
