/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.salsa.engine.impl.base;

import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.CloudService;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.ServiceInstance;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.ServiceUnit;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.enums.SalsaEntityActions;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.enums.SalsaEntityState;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.enums.SalsaEntityType;
import at.ac.tuwien.dsg.cloud.salsa.common.interfaces.SalsaEngineServiceIntenal;
import at.ac.tuwien.dsg.cloud.salsa.common.processing.SalsaCenterConnector;
import at.ac.tuwien.dsg.cloud.salsa.engine.exception.SalsaEngineException;
import at.ac.tuwien.dsg.cloud.salsa.engine.capabilityinterface.UnitCapabilityInterface;
import at.ac.tuwien.dsg.cloud.salsa.engine.services.SalsaEngineImplAll;
import at.ac.tuwien.dsg.cloud.salsa.engine.utils.EngineLogger;
import at.ac.tuwien.dsg.cloud.salsa.engine.utils.SalsaConfiguration;
import java.util.List;
import javax.ws.rs.core.Response;

/**
 * The class contain functionalities for preparing the task at salsa center, then request pioneer to execute it
 *
 * @author hungld
 */
public class AppCapability implements UnitCapabilityInterface {

    SalsaCenterConnector centerCon = new SalsaCenterConnector(SalsaConfiguration.getSalsaCenterEndpointLocalhost(), "/tmp", EngineLogger.logger);

    @Override
    public ServiceInstance deploy(String serviceId, String nodeId, int instanceId) throws SalsaEngineException {
        setLock(nodeId + "/" + instanceId);
        CloudService service = centerCon.getUpdateCloudServiceRuntime(serviceId);
        // find the hosted node of this node
        EngineLogger.logger.debug("Start the deployment of software stacks. Node id: " + nodeId);

        String topologyId = service.getTopologyOfNode(nodeId).getId();

        ServiceUnit unit = service.getComponentById(topologyId, nodeId);
        EngineLogger.logger.debug("NodeId: " + unit.getId());
        ServiceUnit hostedUnit = service.getComponentById(topologyId, unit.getHostedId());
        EngineLogger.logger.debug("Hosted id:  " + hostedUnit.getId());
        // decide which hostedUnit will be used, or create another one
        List<ServiceInstance> hostedInstances = hostedUnit.getInstancesList();
        ServiceInstance suitableHostedInstance = null;
        int hostInstanceId = 0;
        for (ServiceInstance hostedInst : hostedInstances) {
            EngineLogger.logger.debug("There are " + hostedInstances.size() + " instance(s) for " + hostedUnit.getId());
            EngineLogger.logger.debug("On node: " + hostedUnit.getId() + "/"
                    + hostedInst.getInstanceId() + " currently has "
                    + unit.getInstanceHostOn(hostedInst.getInstanceId()).size()
                    + " node " + unit.getId());
            String ids = "->";
            for (ServiceInstance instanceTmp : unit.getInstanceHostOn(hostedInst.getInstanceId())) {
                ids += instanceTmp.getInstanceId() + ", ";
            }
            EngineLogger.logger.debug("And their IDs are: " + ids);
            if (unit.getInstanceHostOn(hostedInst.getInstanceId()).size() < unit.getMax()) {
                suitableHostedInstance = hostedInst;
                hostInstanceId = hostedInst.getInstanceId();
                EngineLogger.logger.debug("DEPLOY MORE INSTANCE. FOUND EXISTED HOST: " + hostedUnit.getId() + "/" + hostInstanceId);
                break;
            }
        }
        CloudService newService = null;
        // if there is no suitable host, create new one:
        if (suitableHostedInstance == null) {
            EngineLogger.logger.debug("DEPLOY MORE INSTANCE. No existing host node, create new node: "
                    + hostedUnit.getId() + " to deploy: " + nodeId);
            SalsaEngineServiceIntenal serviceLayerDeployer = new SalsaEngineImplAll();
            //setLock("Lock until adding more VM node data: " + service.getId() + "/" + hostedUnit.getId() +", in order to host node:" + nodeId +"/" + instanceId);
            Response res = serviceLayerDeployer.spawnInstance(service.getId(), topologyId, hostedUnit.getId(), 1);
            if (res.getStatus() == 201) {
                hostInstanceId = Integer.parseInt(((String) res.getEntity()).trim());
                EngineLogger.logger.debug("The hosting node is being add new data: " + hostedUnit.getId() + "/" + hostInstanceId);
                ServiceInstance hostInstance = null;
                while (hostInstance == null) {	// wait for host instance
                    newService = centerCon.getUpdateCloudServiceRuntime(service.getId());
                    hostInstance = newService.getInstanceById(hostedUnit.getId(), hostInstanceId);
                    try {
                        Thread.sleep(2000);
                    } catch (Exception e) {
                    }
                }
            } else {
                EngineLogger.logger.debug("Could not create host node "
                        + hostedUnit.getId() + "/" + hostInstanceId
                        + " for deploying node: " + nodeId);
                // not release here : releaseLock(); 
                throw new SalsaEngineException("Could not create host node " + hostedUnit.getId() + "/" + hostInstanceId + " for deploying node: " + nodeId, true);
            }
        }

        // for testing, get the first OSNode:
        EngineLogger.logger.debug("DEPLOY MORE INSTANCE. FOUND EXISTED HOST (2nd time): " + hostInstanceId);
        newService = centerCon.getUpdateCloudServiceRuntime(service.getId());
        suitableHostedInstance = newService.getInstanceById(topologyId,
                hostedUnit.getId(), hostInstanceId);

        if (suitableHostedInstance == null) {
            EngineLogger.logger.debug("Hosted node is null");
            releaseLock();
            throw new SalsaEngineException("Couldn't find a node (null) to host node: " + nodeId, true);
        }

        EngineLogger.logger.debug("Hosted node: " + hostedUnit.getId() + "/"
                + suitableHostedInstance.getInstanceId() + " type: "
                + hostedUnit.getType());
		// not release here : releaseLock();

        // if host in OS or DOCKER, set the status to STAGING. a Pioneer will take it
        if (hostedUnit.getType().equals(SalsaEntityType.OPERATING_SYSTEM.getEntityTypeString())
                || hostedUnit.getType().equals(SalsaEntityType.DOCKER.getEntityTypeString())
                || hostedUnit.getType().equals(SalsaEntityType.TOMCAT.getEntityTypeString())) {
            newService = centerCon.getUpdateCloudServiceRuntime(service.getId());
            ServiceInstance data = newService.getInstanceById(topologyId, nodeId, instanceId);
            data.setHostedId_Integer(hostInstanceId);
            data.setState(SalsaEntityState.ALLOCATING); // Hung-18062015: this line can be removed?
            centerCon.addInstanceUnitMetaData(service.getId(), topologyId, nodeId, data);
            // only release lock when we add the data to inform other node that this is hosted.
            EngineLogger.logger.debug("Lock should be released here. Current Lock: " + currentLock + ". Node:" + nodeId + "/" + data.getInstanceId());
            releaseLock();
            // waiting for hostInstance become RUNNING or FINISH
            while (!suitableHostedInstance.getState().equals(SalsaEntityState.INSTALLING)
                    && !suitableHostedInstance.getState().equals(SalsaEntityState.DEPLOYED)) {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                }
                CloudService updateService = centerCon.getUpdateCloudServiceRuntime(service.getId());
                suitableHostedInstance = updateService.getInstanceById(topologyId, hostedUnit.getId(),
                        suitableHostedInstance.getInstanceId());
            }

            // wait for CONNECTTO relationship
            boolean fullfilled;
            do {
                fullfilled = true;
                CloudService updateService = centerCon.getUpdateCloudServiceRuntime(service.getId());
                for (String connectNode : unit.getConnecttoId()) {
                    ServiceUnit u = updateService.getComponentById(connectNode);
                    EngineLogger.logger.debug("Node: " + unit.getId() + "/" + instanceId + " is waiting for connectto node: " + u.getId());
                    if (u.getInstancesList() == null || u.getInstancesList().isEmpty()) {
                        fullfilled = false;
                        break;
                    }
                    if (!u.getInstancesList().get(0).getState().equals(SalsaEntityState.DEPLOYED)) {
                        EngineLogger.logger.debug("Node: " + unit.getId() + "/" + instanceId + " is waiting for connectto node: " + u.getId() + " but the state is not DEPLOYED, it is: " + u.getInstancesList().get(0).getState());
                        fullfilled = false;
                        break;
                    }
                }
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                }
            } while (fullfilled == false);

            EngineLogger.logger.debug("Set state to STAGING for node: " + nodeId + "/" + instanceId + " which will be hosted on " + hostedUnit.getId() + "/" + hostInstanceId);

        }

        EngineLogger.logger.debug("Deploy more instance artifact is done !");
        return new ServiceInstance(instanceId);
    }

    @Override
    public void remove(String serviceId, String nodeId, int instanceId) throws SalsaEngineException {
        EngineLogger.logger.debug("Removing a software node somewhere: " + nodeId + "/" + instanceId);
        //set the state=STAGING and stagingAction=undeploy, the pioneer handle the rest			
        CloudService service = centerCon.getUpdateCloudServiceRuntime(serviceId);
        String topologyId = service.getTopologyOfNode(nodeId).getId();
        centerCon.updateNodeState(serviceId, topologyId, nodeId, instanceId, SalsaEntityState.STAGING_ACTION);
        centerCon.queueActions(serviceId, nodeId, instanceId, SalsaEntityActions.UNDEPLOY.getActionString());
        SalsaEntityState state = SalsaEntityState.STAGING_ACTION;
        int count = 0;
        while (state != SalsaEntityState.UNDEPLOYED && count < 100) {	// wait until pioneer finish its job and inform undeployed or just wait 5 mins
            try {
                state = SalsaEntityState.fromString(centerCon.getInstanceState(serviceId, nodeId, instanceId));
            } catch (SalsaEngineException e1) {
                e1.printStackTrace();
            }
            EngineLogger.logger.debug("Wating for pioneer to undeploy node: " + serviceId + "/" + nodeId + "/" + instanceId);
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            count += 1;
        }
        EngineLogger.logger.debug("Pioneer seems to response that undeploying node done: " + serviceId + "/" + nodeId + "/" + instanceId);
        // remove complete, delete metadata
        try {
            centerCon.removeInstanceMetadata(serviceId, nodeId, instanceId);
        } catch (SalsaEngineException e) {
            e.printStackTrace();
        }

    }

    static boolean orchestating = false;
    static String currentLock = "";

    private static synchronized void setLock(String log) {
        int count = 0;
        while (orchestating) {
            try {
                EngineLogger.logger.debug("The node:" + log + " is waiting for lock: " + currentLock + ". Count: " + count);
                Thread.sleep(500);
                count++;
                if (count > 100) {
                    releaseLock();
                }
            } catch (Exception e) {
                EngineLogger.logger.warn("Not found");
            }
        }
        currentLock = log;
        orchestating = true;
    }

    private static void releaseLock() {
        if (orchestating) {
            EngineLogger.logger.debug("Release current lock: " + currentLock);
        } else {
            EngineLogger.logger.debug("Release lock but it is not locked: " + currentLock);
        }
        orchestating = false;
    }

}
