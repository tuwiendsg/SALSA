/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.salsa.engine.services.algorithms;

import at.ac.tuwien.dsg.salsa.engine.services.enabler.InfoGenerator;
import at.ac.tuwien.dsg.salsa.engine.services.enabler.PioneerManager;
import at.ac.tuwien.dsg.salsa.engine.utils.ActionIDManager;
import at.ac.tuwien.dsg.salsa.engine.utils.SalsaConfiguration;
import at.ac.tuwien.dsg.salsa.messaging.messageInterface.MessageClientFactory;
import at.ac.tuwien.dsg.salsa.messaging.messageInterface.MessagePublishInterface;
import at.ac.tuwien.dsg.salsa.messaging.model.Salsa.PioneerInfo;
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
import at.ac.tuwien.dsg.salsa.model.salsa.info.SalsaConfigureTask;
import at.ac.tuwien.dsg.salsa.model.salsa.interfaces.ConfigurationModule;
import at.ac.tuwien.dsg.salsa.modules.plainmachine.LocalMachineConfigurator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This algorithm is traditional process of Salsa.
 *
 * - All the units of the cloudservice are put into a list. - The process check
 * the unit one by one, and repeat. - If all the dependencies are fulfilled, the
 * deployment is call.
 *
 * @author hungld
 */
public class OrchestrationProcess_RoundCheck implements OrchestrationProcess {

    static Logger logger = LoggerFactory.getLogger("salsa");
    CloudService cloudService;

    // Map store service unit uuid --> number of instances to be deployed
    Map<ServiceUnit, Integer> instancesNeeded = new HashMap<>();

    @Override
    public void deployCloudservice(CloudService service) {
        logger.debug("Start round check configuration");
        this.cloudService = service;
        List<ServiceUnit> allUnits = service.getAllComponent();

        // build the list of needed instances
        for (ServiceUnit unit : allUnits) {
            instancesNeeded.put(unit, unit.getMin());
        }

        while (!instancesNeeded.isEmpty()) {
            for (ServiceUnit unit : instancesNeeded.keySet()) {
                if (!needHostedInstance(unit)) {
                    int instanceindex = unit.nextIdCounter();
                    deployVM(unit, instanceindex);
                } else {
                    ServiceInstance hostedInstance = getHostInstance(unit);
                    String hostedUnitName = unit.getHostedUnitName();
                    PioneerInfo pioneer = PioneerManager.getPioneer(SalsaConfiguration.getUserName(), cloudService.getName(), hostedUnitName, hostedInstance.getIndex());
                    if (pioneer != null) {
                        logger.debug("Found pioneer: " + pioneer.toString() + " to deploy: " + cloudService.getName() + "/" + unit.getName());
                        int instanceindex = unit.nextIdCounter();
                        deployArtifact(unit, pioneer, instanceindex);
                    }
                }
            }
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }

    // = true with docker & artifacts, = false with VM
    private boolean needHostedInstance(ServiceUnit unit) {
        String hostedUnitName = unit.getHostedUnitName();
        if (hostedUnitName != null) {
            return true;
        }
        return false;
    }

    // try to find the instance to host
    private ServiceInstance getHostInstance(ServiceUnit unit) {
        // in the case of host unit ready, check if pioneer is ready?
        // hostedUnitName should not be null here
        String hostedUnitName = unit.getHostedUnitName();
        ServiceUnit hostedUnit = cloudService.getComponentByName(hostedUnitName);
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
        if (hostedInstance == null && instancesNeeded.get(hostedUnit) == null) {
            instancesNeeded.put(hostedUnit, 1);
        }
        return hostedInstance;
    }

    // these functions are hard code at the moment only
    public void deployVM(ServiceUnit unit, int instanceindex) {
        ServiceTopology topo = cloudService.getTopologyOfNode(unit.getUuid());
        int instanceIndex = unit.getIdCounter();

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
                        .hasInstanceIndex(instanceIndex)
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
                .hasUser(SalsaConfiguration.getUserName());
        for (Artifact a : unit.getArtifacts()) {
            task.hasArtifact(a.getName(), a.getArtifactType(), a.getReference());
            if (a.getArtifactType().equals(SalsaArtifactType.sh.getString()) || a.getArtifactType().equals(SalsaArtifactType.shcont.getString())) {
                String runByMe = FilenameUtils.getName(a.getReference());
                task.hasParam(ShellScriptParameters.runByMe, a.getName());
                logger.debug(" -- Yes, the runByMe should be: " + runByMe);
            }
        }
        // refine runbyme if needed
        if (unit.getCapabilityByName(SalsaCommonActions.deploy) != null) {
            String runByMe = unit.getCapabilityByName(SalsaCommonActions.deploy).getExecutionREF();
            task.hasParam(ShellScriptParameters.runByMe, runByMe);
        }

        // Register an action ID, send message to Pioneer
        ActionIDManager.addAction(actionID, task);
        SalsaMessage msg = new SalsaMessage(SalsaMessage.MESSAGE_TYPE.salsa_deploy, SalsaConfiguration.getSalsaCenterEndpoint(), SalsaMessageTopic.getPioneerTopicByID(pioneer.getId()), null, task.toJson());

        MessageClientFactory factory = MessageClientFactory.getFactory(SalsaConfiguration.getBroker(), SalsaConfiguration.getBrokerType());
        MessagePublishInterface publish = factory.getMessagePublisher();
        publish.pushMessage(msg);
    }

}
