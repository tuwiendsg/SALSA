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
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.enums.SalsaEntityActions;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.enums.SalsaEntityState;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.enums.SalsaEntityType;
import at.ac.tuwien.dsg.cloud.salsa.engine.capabilityinterface.SalsaEngineServiceIntenal;
import at.ac.tuwien.dsg.cloud.salsa.engine.utils.SalsaCenterConnector;
import at.ac.tuwien.dsg.cloud.salsa.common.interfaces.SalsaException;
import at.ac.tuwien.dsg.cloud.salsa.engine.capabilityinterface.UnitCapabilityInterface;
import at.ac.tuwien.dsg.cloud.salsa.engine.exceptions.EngineConnectionException;
import at.ac.tuwien.dsg.cloud.salsa.engine.exceptions.DependencyConfigurationException;
import at.ac.tuwien.dsg.cloud.salsa.engine.exceptions.PioneerManagementException;
import at.ac.tuwien.dsg.cloud.salsa.engine.impl.genericCapability.InfoParser;
import at.ac.tuwien.dsg.cloud.salsa.engine.services.SalsaEngineImplAll;
import at.ac.tuwien.dsg.cloud.salsa.engine.utils.ActionIDManager;
import at.ac.tuwien.dsg.cloud.salsa.engine.utils.EngineLogger;
import at.ac.tuwien.dsg.cloud.salsa.engine.utils.PioneerManager;
import at.ac.tuwien.dsg.cloud.salsa.engine.utils.SalsaConfiguration;
import at.ac.tuwien.dsg.cloud.salsa.messaging.messageInterface.MessagePublishInterface;
import at.ac.tuwien.dsg.cloud.salsa.messaging.protocol.SalsaMessage;
import at.ac.tuwien.dsg.cloud.salsa.messaging.protocol.SalsaMessageTopic;
import at.ac.tuwien.dsg.cloud.salsa.messaging.model.Salsa.SalsaMsgConfigureArtifact;
import at.ac.tuwien.dsg.cloud.salsa.domainmodels.types.SalsaArtifactType;
import at.ac.tuwien.dsg.cloud.salsa.messaging.messageInterface.MessageClientFactory;
import at.ac.tuwien.dsg.cloud.salsa.engine.dataprocessing.ToscaStructureQuery;
import at.ac.tuwien.dsg.cloud.salsa.engine.utils.EventPublisher;
import at.ac.tuwien.dsg.cloud.salsa.messaging.model.Salsa.INFOMessage;
import at.ac.tuwien.dsg.cloud.salsa.tosca.extension.SalsaInstanceDescription_Docker;
import com.google.common.base.Joiner;
import generated.oasis.tosca.TDefinitions;
import generated.oasis.tosca.TRelationshipTemplate;
import java.util.List;
import java.util.UUID;
import javax.ws.rs.core.Response;
import org.apache.commons.io.FilenameUtils;

/**
 * The class contain functionalities for preparing the task at salsa center,
 * then request pioneer to execute it
 *
 * @author Duc-Hung Le
 */
public class AppCapabilityBase implements UnitCapabilityInterface {

    SalsaCenterConnector centerCon;

    {
        try {
            centerCon = new SalsaCenterConnector(SalsaConfiguration.getSalsaCenterEndpointLocalhost(), "/tmp", EngineLogger.logger);
        } catch (EngineConnectionException ex) {
            EngineLogger.logger.error("Cannot connect to SALSA service in localhost: " + SalsaConfiguration.getSalsaCenterEndpointLocalhost() + ". This is a fatal error !");
        }
    }

    @Override
    public ServiceInstance deploy(String serviceId, String nodeId, int instanceId) throws SalsaException {
        EventPublisher.publishInstanceEvent(Joiner.on("/").join(serviceId, nodeId, instanceId), INFOMessage.ACTION_TYPE.DEPLOY, INFOMessage.ACTION_STATUS.STARTED, "AppCapabilityBase", "Start the based deployment of software stacks");

        setLock(nodeId + "/" + instanceId);
        CloudService service = centerCon.getUpdateCloudServiceRuntime(serviceId);
        // find the hosted node of this node              

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

            //Doing: check the hosteOn instance by : static: via max instance definition, dynamic: via a resource
            boolean dynamicPlacementOn = SalsaConfiguration.getDynamicPlacementOn();
            boolean placeOnMe = false;
            if (dynamicPlacementOn && unit.getMax() <= 0) {
                float threadhold = SalsaConfiguration.getPlacementThreadhold();
                EngineLogger.logger.debug("Dynamic placement. Threadhold: {}, instance:: {}/{}/{} ", threadhold, service.getId(), nodeId, instanceId);
                if (DynamicPlacementHelper.checkMemoryUsageIsBelowThreadholdByInstanceID_NoDockerConcern(threadhold, service, hostedUnit.getId(), hostedInst.getInstanceId())) {
                    placeOnMe = true;
                }
            } else {
                EngineLogger.logger.debug("Static placement, unitMax: {}, sice: {}", unit.getMax(), unit.getInstanceHostOn(hostedInst.getInstanceId()).size());
                if (unit.getInstanceHostOn(hostedInst.getInstanceId()).size() < unit.getMax()) {
                    placeOnMe = true;
                }
            }

            if (placeOnMe) {
                suitableHostedInstance = hostedInst;
                hostInstanceId = hostedInst.getInstanceId();
                EngineLogger.logger.debug("DEPLOY MORE INSTANCE. FOUND EXISTED HOST: " + hostedUnit.getId() + "/" + hostInstanceId);
                break;
            }
        }
        CloudService newService;
        // if there is no suitable host, create new one:
        if (suitableHostedInstance == null) {
            EngineLogger.logger.debug("DEPLOY MORE INSTANCE. No existing host node, create new node: "
                    + hostedUnit.getId() + " to deploy: " + nodeId);
            SalsaEngineServiceIntenal serviceLayerDeployer = new SalsaEngineImplAll();
            //setLock("Lock until adding more VM node data: " + service.getId() + "/" + hostedUnit.getId() +", in order to host node:" + nodeId +"/" + instanceId);
            EngineLogger.logger.debug("Starting to invoke the spawnInstance to deploy hosted node: {}/{}/{}", service.getId(), topologyId, hostedUnit.getId());
            Response res = serviceLayerDeployer.spawnInstance(service.getId(), hostedUnit.getId(), 1);
            EngineLogger.logger.debug("The invocation is done to deploy hosted node: {}/{}/{}", service.getId(), topologyId, hostedUnit.getId() + ", it return the code: " + res.getStatus());
            //GenericUnitCapability geneCapa = new GenericUnitCapability();
            //ServiceInstance hostedInstance = geneCapa.deploy(service.getId(), hostedUnit.getId(), 1);
            if (res.getStatus() == 201) {
                //hostInstanceId = hostedInstance.getInstanceId();                
                hostInstanceId = Integer.parseInt(((String) res.getEntity()).trim());
                EngineLogger.logger.debug("Hosted node {}/{}/{}/{} metadata is created, but node is deploying ... We can process the orchestration!", service.getId(), topologyId, hostedUnit.getId(), hostInstanceId);
                ServiceInstance hostInstance = null;
                int countToWaitHostInstanceUp = 0;
                while (hostInstance == null) {	// wait for host instance
                    newService = centerCon.getUpdateCloudServiceRuntime(service.getId());
                    hostInstance = newService.getInstanceById(hostedUnit.getId(), hostInstanceId);
                    try {
                        Thread.sleep(2000);
                        countToWaitHostInstanceUp = countToWaitHostInstanceUp + 1;
                        if (countToWaitHostInstanceUp > 300) {    // wait 10 minutes
                            EngineLogger.logger.warn("Waiting for host metadata of {}/{} is add too long, timeout, still try to process!", hostedUnit.getId(), hostInstanceId);
                            break;
                        } else {
                            EngineLogger.logger.debug("Waiting for host metadata of {}/{} to be added", hostedUnit.getId(), hostInstanceId);
                        }
                    } catch (Exception e) {
                        break;
                    }
                }
            } else {
                // not release here : releaseLock();
                EngineLogger.logger.error("More log. Failed to deploy dependency for node: " + serviceId + "/" + nodeId + "/" + instanceId, hostedUnit.getId());
                throw new DependencyConfigurationException(serviceId + "/" + nodeId + "/" + instanceId, hostedUnit.getId(), "The hosted node cannot be created " + hostedUnit.getId());
            }
        }

        // for testing, get the first OSNode:
        EngineLogger.logger.debug("DEPLOY MORE INSTANCE. FOUND EXISTED HOST (2nd time): " + hostInstanceId);
        newService = centerCon.getUpdateCloudServiceRuntime(service.getId());
        suitableHostedInstance = newService.getInstanceById(topologyId, hostedUnit.getId(), hostInstanceId);

        if (suitableHostedInstance == null) {
            EngineLogger.logger.debug("Hosted node is null");
            releaseLock();
            throw new DependencyConfigurationException(serviceId + "/" + nodeId + "/" + instanceId, hostedUnit.getId(), "No instance of node " + hostedUnit.getId() + " is found !");
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
                    break;
                }
                CloudService updateService = centerCon.getUpdateCloudServiceRuntime(service.getId());
                suitableHostedInstance = updateService.getInstanceById(topologyId, hostedUnit.getId(), suitableHostedInstance.getInstanceId());
            }

            // wait for CONNECTTO relationship and build the environment variable
            String environment = "";
            boolean fullfilled;
            do {
                fullfilled = true;
                CloudService updateService = centerCon.getUpdateCloudServiceRuntime(service.getId());
                for (String connectNodeID : unit.getConnecttoId()) {
                    ServiceUnit masterNode = updateService.getComponentById(connectNodeID);
                    EngineLogger.logger.debug("Node: " + unit.getId() + "/" + instanceId + " is waiting for connectto node: " + masterNode.getId());
                    if (masterNode.getInstancesList() == null || masterNode.getInstancesList().isEmpty()) {
                        fullfilled = false;
                        break;
                    }
                    if (!masterNode.getInstancesList().get(0).getState().equals(SalsaEntityState.DEPLOYED)) {
                        //EngineLogger.logger.debug("Node: " + unit.getId() + "/" + instanceId + " is waiting for connectto node: " + masterNode.getId() + " but the state is not DEPLOYED, it is: " + masterNode.getInstancesList().get(0).getState());
                        fullfilled = false;
                        if (masterNode.getInstancesList().get(0).getState().equals(SalsaEntityState.ERROR)) {
                            centerCon.updateNodeState(serviceId, topologyId, nodeId, instanceId, SalsaEntityState.ERROR, "Error due to a configuration failed from node: " + masterNode.getId() + "/" + masterNode.getInstancesList().get(0).getInstanceId());
                            throw new DependencyConfigurationException(serviceId + "/" + nodeId + "/" + instanceId, serviceId + "/" + masterNode.getId() + "/" + masterNode.getInstancesList().get(0).getInstanceId(), "Domino effect: configuration failed due to an error of a dependency");
                        }
                        break;
                    }

                    // now connectNode is available, get the first instance
                    if (masterNode.getInstancesList().isEmpty()) {
                        throw new DependencyConfigurationException(serviceId + "/" + nodeId + "/" + instanceId, masterNode.getId(), "There is no instance of node: " + masterNode.getId() + ", cannot get the capability.");
                    }
                    ServiceInstance masterInstance = masterNode.getInstancesList().get(0);
                    // TODO: Fix this hack. This get the capabilityVar[0], which assume that a unit only expose 1 capability, which is its ID
                    // This will be extended to support multiple capability.                    
                    EngineLogger.logger.debug("Querying capability of node: {}/{}/{}/{} " + serviceId, topologyId, nodeId, instanceId, masterNode.getCapabilityVars().get(0));

                    String env = centerCon.getCapabilityValue(serviceId, topologyId, masterNode.getId(), masterInstance.getInstanceId(), masterNode.getCapabilityVars().get(0));
                    // Here export the env (the capability) into different variable names. Currently, only apply for the FIRST capability value
                    // export to masternode_IP
                    environment += masterNode.getId() + "_IP=" + env + ";";
                    // export also to the capaID
                    if (masterInstance.getCapabilities() != null && !masterInstance.getCapabilities().getCapability().isEmpty()) {
                        String capaID = masterInstance.getCapabilities().getCapability().get(0).getId();
                        environment += capaID + "=" + env + ";";
                    }
                    //export even to the connectto relationship ID between master and node?
                    TDefinitions def = centerCon.getToscaDescription(serviceId);
                    TRelationshipTemplate rela = ToscaStructureQuery.getRelationshipBetweenTwoNode(ToscaStructureQuery.getNodetemplateById(masterNode.getId(), def), ToscaStructureQuery.getNodetemplateById(unit.getId(), def), def);
                    if (rela != null) {
                        environment += rela.getId() + "_IP=" + env + ";";
                    }
                }
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    break;
                }
            } while (fullfilled == false);

            EngineLogger.logger.debug("Set state to STAGING for node: " + nodeId + "/" + instanceId + " which will be hosted on " + hostedUnit.getId() + "/" + hostInstanceId);

            // publish a message to pioneer
            String actionID = UUID.randomUUID().toString();

            centerCon.updateNodeState(serviceId, topologyId, nodeId, instanceId, SalsaEntityState.STAGING, "Configuration request is sending to Pioneer. Action ID: " + actionID);

            // note: with the capability "deploy", the runByMe parameter is null, pioneer will check this         
            String pioneerID = PioneerManager.getPioneerIDForNode(SalsaConfiguration.getUserName(), serviceId, nodeId, instanceId, newService);
            if (pioneerID == null) {
                throw new PioneerManagementException(PioneerManagementException.Reason.PIONEER_NOT_REGISTERED, "The pioneer on node " + SalsaConfiguration.getUserName() + "/" + serviceId + "/" + nodeId + "/" + instanceId + " is not registered to SalsaEngine, deployment aborted !");
            }

            SalsaMsgConfigureArtifact command = new SalsaMsgConfigureArtifact(actionID, "deploy", pioneerID, SalsaConfiguration.getUserName(), serviceId, topologyId, nodeId, instanceId, InfoParser.mapOldAndNewCategory(SalsaEntityType.fromString(unit.getType())), "", "", SalsaArtifactType.fromString(unit.getArtifactType()), environment);
            String runByMe = "";

            // add needed artifacts for the deployment
            for (ServiceUnit.Artifacts art : unit.getArtifacts()) {
                EngineLogger.logger.debug("Debug1605: artifact reference is: " + art.getReference());
                command.hasArtifact(art.getName(), art.getType(), art.getReference());
            }

            // if the deploy action is explicit define, RUN IT
            if (unit.getPrimitiveByName("deploy") != null) {
                // TODO: consider to expand this part, this supports only SCRIPT type now
                runByMe = unit.getPrimitiveByName("deploy").getExecutionREF();
            } // otherwise SALSA tries to detect via Artifact type
            else {
                for (ServiceUnit.Artifacts art : unit.getArtifacts()) {                    
                    EngineLogger.logger.debug("Comparing artifact type (" + art.getType() + ") and unit artifact type (" + unit.getArtifactType() + ")");
                    if (art.getType().equals(unit.getArtifactType()) && runByMe.isEmpty()) {
                        runByMe = FilenameUtils.getName(art.getReference());
                        EngineLogger.logger.debug(" -- Yes, the runByMe should be: " + runByMe);
                    }
                }
            }
            command.setRunByMe(runByMe);

            // here with the docker, add preRunByMe to install need packages
            if (unit.getType().equals(SalsaEntityType.DOCKER.getEntityTypeString()) && unit.getProperties() != null) {
                EngineLogger.logger.debug("Docker unit have property");
                SalsaInstanceDescription_Docker dockerProp = (SalsaInstanceDescription_Docker) unit.getProperties().getAny();
                if (dockerProp != null && dockerProp.getPackagesDependenciesList() != null && !dockerProp.getPackagesDependenciesList().getPackageDependency().isEmpty()) {
                    String preRunByMe = "apt-get install -y ";
                    for (String p : dockerProp.getPackagesDependenciesList().getPackageDependency()) {
                        preRunByMe += p + " ";
                    }
                    command.setPreRunByMe(preRunByMe);
                }
            }

            // add an action
            ActionIDManager.addAction(actionID, command);
            SalsaMessage msg = new SalsaMessage(SalsaMessage.MESSAGE_TYPE.salsa_deploy, SalsaConfiguration.getSalsaCenterEndpoint(), SalsaMessageTopic.getPioneerTopicByID(pioneerID), null, command.toJson());

            MessageClientFactory factory = MessageClientFactory.getFactory(SalsaConfiguration.getBroker(), SalsaConfiguration.getBrokerType());
            MessagePublishInterface publish = factory.getMessagePublisher();
            publish.pushMessage(msg);
        }

        EventPublisher.publishInstanceEvent(Joiner.on("/").join(serviceId, nodeId, instanceId), INFOMessage.ACTION_TYPE.DEPLOY, INFOMessage.ACTION_STATUS.PROCESSING, "AppCapabilityBase", "Sending request to deploy more instance artifacts is done");
        return new ServiceInstance(instanceId);
    }

    @Override
    public void remove(String serviceId, String nodeId, int instanceId) throws SalsaException {
        EventPublisher.publishInstanceEvent(Joiner.on("/").join(serviceId, nodeId, instanceId), INFOMessage.ACTION_TYPE.REMOVE, INFOMessage.ACTION_STATUS.STARTED, "AppCapabilityBase", "Removing a software node somewhere");
        //set the state=STAGING and stagingAction=undeploy, the pioneer handle the rest			
        CloudService service = centerCon.getUpdateCloudServiceRuntime(serviceId);
        String topologyId = service.getTopologyOfNode(nodeId).getId();
        ServiceUnit unit = service.getComponentById(nodeId);
        centerCon.updateNodeState(serviceId, topologyId, nodeId, instanceId, SalsaEntityState.STAGING_ACTION, "Undeployment action is queued");
        centerCon.queueActions(serviceId, nodeId, instanceId, SalsaEntityActions.UNDEPLOY.getActionString());

        SalsaEntityState state = SalsaEntityState.STAGING_ACTION;
        int count = 0;
        while (state != SalsaEntityState.UNDEPLOYED && count < 100) {	// wait until pioneer finish its job and inform undeployed or just wait 5 mins
            try {
                state = SalsaEntityState.fromString(centerCon.getInstanceState(serviceId, nodeId, instanceId));
            } catch (SalsaException e1) {
                e1.printStackTrace();
                throw e1;
            }
            EngineLogger.logger.debug("Wating for pioneer to undeploy node: " + serviceId + "/" + nodeId + "/" + instanceId);
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            count += 1;
        }
        EngineLogger.logger.debug("Pioneer seems to response that it undeployed node, task is done: " + serviceId + "/" + nodeId + "/" + instanceId);
        // remove complete, delete metadata
        try {
            centerCon.removeInstanceMetadata(serviceId, nodeId, instanceId);
//            ActionIDManager.removeAction(undeployActionID);
        } catch (SalsaException e) {
            throw e;
        }
        EventPublisher.publishInstanceEvent(Joiner.on("/").join(serviceId, nodeId, instanceId), INFOMessage.ACTION_TYPE.REMOVE, INFOMessage.ACTION_STATUS.DONE, "AppCapabilityBase", "Removed a software node");
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
            } catch (InterruptedException e) {
                EngineLogger.logger.warn("Interrupted", e);
                break;
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
