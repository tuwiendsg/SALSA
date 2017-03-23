/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.salsa.engine.services.algorithms;

import at.ac.tuwien.dsg.salsa.database.neo4j.repo.CloudServiceRepository;
import at.ac.tuwien.dsg.salsa.database.neo4j.repo.ServiceInstanceRepository;
import static at.ac.tuwien.dsg.salsa.engine.services.algorithms.OrchestrationProcess_RoundCheck.logger;
import at.ac.tuwien.dsg.salsa.engine.services.enabler.InfoGenerator;
import at.ac.tuwien.dsg.salsa.engine.services.enabler.PioneerManager;
import at.ac.tuwien.dsg.salsa.engine.utils.ActionIDManager;
import at.ac.tuwien.dsg.salsa.engine.utils.SalsaConfiguration;
import at.ac.tuwien.dsg.salsa.messaging.messageInterface.MessageClientFactory;
import at.ac.tuwien.dsg.salsa.messaging.messageInterface.MessagePublishInterface;
import at.ac.tuwien.dsg.salsa.messaging.protocol.SalsaMessage;
import at.ac.tuwien.dsg.salsa.messaging.protocol.SalsaMessageTopic;
import at.ac.tuwien.dsg.salsa.model.CloudService;
import at.ac.tuwien.dsg.salsa.model.ServiceInstance;
import at.ac.tuwien.dsg.salsa.model.ServiceTopology;
import at.ac.tuwien.dsg.salsa.model.ServiceUnit;
import at.ac.tuwien.dsg.salsa.model.enums.SalsaArtifactType;
import at.ac.tuwien.dsg.salsa.model.enums.SalsaCommonActions;
import at.ac.tuwien.dsg.salsa.model.enums.SalsaEntityType;
import at.ac.tuwien.dsg.salsa.model.properties.Artifact;
import at.ac.tuwien.dsg.salsa.model.salsa.confparameters.PlainMachineParameters;
import at.ac.tuwien.dsg.salsa.model.salsa.confparameters.ShellScriptParameters;
import at.ac.tuwien.dsg.salsa.model.salsa.info.PioneerInfo;
import at.ac.tuwien.dsg.salsa.model.salsa.info.SalsaConfigureTask;
import at.ac.tuwien.dsg.salsa.model.salsa.info.SalsaEvent;
import at.ac.tuwien.dsg.salsa.model.salsa.info.SalsaException;
import at.ac.tuwien.dsg.salsa.model.salsa.interfaces.ConfigurationModule;
import at.ac.tuwien.dsg.salsa.modules.plainmachine.LocalMachineConfigurator;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.apache.commons.io.FilenameUtils;

/**
 *
 * @author hungld
 */
public class BaseUnitCapability implements UnitCapabilityInterface {

    String cloudServiceName;
    CloudServiceRepository cloudRepo;
    ServiceInstanceRepository instanceRepo;

    public BaseUnitCapability(String cloudServiceName, CloudServiceRepository cloudRepo, ServiceInstanceRepository instanceRepo) {
        this.cloudServiceName = cloudServiceName;
        this.cloudRepo = cloudRepo;
        this.instanceRepo = instanceRepo;
    }

    @Override
    public ServiceInstance deploy(String serviceId, String nodeId) throws SalsaException {
        logger.debug("Start to deploy unit: {},{},{}" + serviceId, nodeId);
        CloudService cloudService = getUpdatedCloudService();
        ServiceUnit unit = cloudService.getUnitByName(nodeId);
        if (!needHostedInstance(unit)) {
            logger.debug("Unit: " + unit.getName() + " can be deployed without hoster");
            int instanceindex = unit.nextIdCounter();
            Date start = new Date();
            ServiceInstance newInstance = new ServiceInstance();
            newInstance.setUuid(UUID.randomUUID().toString());
            newInstance.setServiceUnitUuid(unit.getUuid());
            newInstance.setIndex(instanceindex);
            unit.hasInstance(newInstance);
            cloudRepo.save(cloudService);

            deployVM(unit, instanceindex);

            cloudService.hasEvent(new SalsaEvent(unit.getName() + "-" + newInstance.getIndex(), start, new Date(), "deploy"));
            cloudRepo.save(cloudService);

            logger.debug(" -- Deployed and now remove unit from queue: " + unit.getName());

            return newInstance;

        } else {
            String hostedUnitName = unit.getHostedUnitName();
            ServiceInstance hostedInstance = getHostInstance(cloudService, unit);

            if (hostedInstance != null) {
                logger.debug("To host: " + unit.getName() + ", an instance is available now: " + hostedUnitName + "/" + hostedInstance.getIndex());
                PioneerInfo pioneer = PioneerManager.getPioneer(SalsaConfiguration.getUserName(), cloudService.getName(), hostedUnitName, hostedInstance.getIndex());
                if (pioneer != null) {
                    logger.debug("Found pioneer: " + pioneer.toString() + " to deploy: " + cloudService.getName() + "/" + unit.getName());
                    int instanceindex = unit.nextIdCounter();
                    Date start = new Date();

                    logger.debug(" -- Deployed and now remove unit from queue: " + unit.getName());
                    ServiceInstance newInstance = new ServiceInstance();
                    newInstance.setUuid(UUID.randomUUID().toString());
                    newInstance.setIndex(instanceindex);
                    newInstance.setServiceUnitUuid(unit.getUuid());
                    newInstance.setHostedInstanceIndex(hostedInstance.getIndex());
                    unit.hasInstance(newInstance);
                    cloudRepo.save(cloudService);

                    deployArtifact(unit, pioneer, instanceindex);

                    cloudService.hasEvent(new SalsaEvent(unit.getName() + "-" + newInstance.getIndex(), start, new Date(), "deploy"));
                    cloudRepo.save(cloudService);
                    return newInstance;
                }
            } else {
                logger.debug("Unit: " + unit.getName() + " is waiting for the hoster: " + hostedUnitName);
                return null;
            }
        }

        return null;

    }

    @Override
    public void remove(String serviceId, String nodeId, int instanceId) throws SalsaException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    // = true with docker & artifacts, = false with VM
    private boolean needHostedInstance(ServiceUnit unit) {
        String hostedUnitName = unit.getHostedUnitName();
        if (hostedUnitName != null) {
            return true;
        }
        return false;
    }

    private CloudService getUpdatedCloudService() {
        return cloudRepo.findByName(cloudServiceName);
    }

    // try to find the instance to host
    private ServiceInstance getHostInstance(CloudService cloudService, ServiceUnit unit) {
        // in the case of host unit ready, check if pioneer is ready?
        // hostedUnitName should not be null here
        String hostedUnitName = unit.getHostedUnitName();
        ServiceUnit hostedUnit = cloudService.getUnitByName(hostedUnitName);
        Set<ServiceInstance> possibleHostedInstances = hostedUnit.getInstances();
        ServiceInstance hostedInstance = null;
        if (possibleHostedInstances != null) {
            for (ServiceInstance possibleHoster : possibleHostedInstances) {
                Integer numberInstAlreadyOn = unit.getInstanceHostOn(hostedUnitName, possibleHoster.getIndex()).size();
                logger.debug("Check if instance: " + unit.getName() + " can be hosted on: " + hostedUnit + "/" + possibleHoster.getIndex());
                if (numberInstAlreadyOn < hostedUnit.getMax()) {
                    logger.debug("  --> Yes, instance: " + hostedUnit + "/" + possibleHoster.getIndex());
                    hostedInstance = possibleHoster;
                    break;
                }
            }
        }
        // if there is no candidate of the hosted instance + task list has no candidate, put one task
//        if (hostedInstance == null && instancesNeeded.get(hostedUnit) == null) {
//            instancesNeeded.put(hostedUnit, 1);
//        }
        return hostedInstance;
    }

    // these functions are hard code at the moment only
    public void deployVM(ServiceUnit unit, int instanceindex) {
        CloudService cloudService = getUpdatedCloudService();
        ServiceTopology topo = cloudService.getTopologyOfNode(unit.getUuid());

        if (unit.getType().equals(SalsaEntityType.OPERATING_SYSTEM.getEntityTypeString())) {
            Map<String, String> properties = unit.getProperties();
            String providerName = properties.get("provider");
            logger.debug("Deploying machine, provider: " + providerName);
            if (providerName.endsWith("@openstack")) {
                // TODO: implement this: read parameters, etc
            }
            if (providerName.equals("localhost")) {
                String userData = InfoGenerator.prepareUserData(SalsaConfiguration.getUserName(), cloudService.getName(), topo.getName(), unit.getName(), instanceindex);
                properties.put(PlainMachineParameters.userData, userData);

                SalsaConfigureTask task = new SalsaConfigureTask();
                task.hasActionId(UUID.randomUUID().toString())
                        .hasActionName(SalsaCommonActions.deploy)
                        .hasServiceName(cloudService.getName())
                        .hasTopologyName(topo.getName())
                        .hasUnitName(unit.getName())
                        .hasInstanceIndex(instanceindex)
                        .hasUser(SalsaConfiguration.getUserName());

                // call the configuration module for localhost
                ConfigurationModule module = new LocalMachineConfigurator();
                module.configureArtifact(task, properties);
            }
        } else {
            logger.error("THe unit: " + unit.getName() + " is not a VM, but the deployVM function is call !");
        }

    }

    // send the configuration task to the Pioneer
    public void deployArtifact(ServiceUnit unit, PioneerInfo pioneer, int instanceindex) {
        CloudService cloudService = getUpdatedCloudService();
        ServiceTopology topo = cloudService.getTopologyOfNode(unit.getUuid());
        int instanceIndex = unit.getIdCounter();
        String actionID = UUID.randomUUID().toString();

        // task are shell script 
        SalsaConfigureTask task = new SalsaConfigureTask();
        task.hasActionId(actionID)
                .hasActionName(SalsaCommonActions.deploy)
                .hasServiceName(cloudService.getName())
                .hasTopologyName(topo.getName())
                .hasUnitName(unit.getName())
                .hasInstanceIndex(instanceIndex)
                .hasPioneerUUID(pioneer.getUuid())
                .hasUser(SalsaConfiguration.getUserName());
        for (Artifact a : unit.getArtifacts()) {
            task.hasArtifact(a.getName(), a.getArtifactType(), a.getReference());
            if (a.getArtifactType().equals(SalsaArtifactType.sh.getString()) || a.getArtifactType().equals(SalsaArtifactType.shcont.getString())) {
                String runByMe = "/bin/bash " + FilenameUtils.getName(a.getReference());
                task.hasParam(ShellScriptParameters.runByMe, runByMe);
                logger.debug(" -- Yes, the runByMe should be: " + runByMe);
            }
        }
        // refine runbyme if needed
        if (unit.getCapabilityByName(SalsaCommonActions.deploy) != null) {
            String runByMe = unit.getCapabilityByName(SalsaCommonActions.deploy).getCommand();
            task.hasParam(ShellScriptParameters.runByMe, runByMe);
        }

        // Register an action ID, send message to Pioneer
        ActionIDManager.addAction(actionID, task);
        SalsaMessage msg = new SalsaMessage(SalsaMessage.MESSAGE_TYPE.salsa_deploy, SalsaConfiguration.getSalsaCenterEndpoint(), SalsaMessageTopic.getPioneerTopicByID(pioneer.getUuid()), null, task.toJson());

        MessageClientFactory factory = MessageClientFactory.getFactory(SalsaConfiguration.getBroker(), SalsaConfiguration.getBrokerType());
        MessagePublishInterface publish = factory.getMessagePublisher();
        publish.pushMessage(msg);
    }

}
