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
package at.ac.tuwien.dsg.cloud.salsa.pioneer;

import static at.ac.tuwien.dsg.cloud.salsa.domainmodels.types.SalsaArtifactType.apt;
import static at.ac.tuwien.dsg.cloud.salsa.domainmodels.types.SalsaArtifactType.chef;
import static at.ac.tuwien.dsg.cloud.salsa.domainmodels.types.SalsaArtifactType.chefSolo;
import static at.ac.tuwien.dsg.cloud.salsa.domainmodels.types.SalsaArtifactType.dockerfile;
import static at.ac.tuwien.dsg.cloud.salsa.domainmodels.types.SalsaArtifactType.sh;
import static at.ac.tuwien.dsg.cloud.salsa.domainmodels.types.SalsaArtifactType.shcont;
import static at.ac.tuwien.dsg.cloud.salsa.domainmodels.types.SalsaArtifactType.war;
import at.ac.tuwien.dsg.cloud.salsa.messaging.messageInterface.MessagePublishInterface;
import at.ac.tuwien.dsg.cloud.salsa.messaging.messageInterface.MessageSubscribeInterface;
import at.ac.tuwien.dsg.cloud.salsa.domainmodels.types.ServiceCategory;
import at.ac.tuwien.dsg.cloud.salsa.messaging.messageInterface.MessageClientFactory;
import at.ac.tuwien.dsg.cloud.salsa.messaging.messageInterface.SalsaMessageHandling;
import at.ac.tuwien.dsg.cloud.salsa.messaging.model.Salsa.SalsaMsgConfigureArtifact;
import at.ac.tuwien.dsg.cloud.salsa.messaging.model.Salsa.SalsaMsgConfigureState;
import at.ac.tuwien.dsg.cloud.salsa.messaging.protocol.SalsaMessage;
import at.ac.tuwien.dsg.cloud.salsa.messaging.protocol.SalsaMessageTopic;
import at.ac.tuwien.dsg.cloud.salsa.pioneer.elise.EliseConductorManager;
import at.ac.tuwien.dsg.cloud.salsa.pioneer.instruments.AptGetInstrument;
import at.ac.tuwien.dsg.cloud.salsa.pioneer.instruments.ArtifactConfigurationInterface;
import at.ac.tuwien.dsg.cloud.salsa.pioneer.instruments.BashContinuousInstrument;
import at.ac.tuwien.dsg.cloud.salsa.pioneer.instruments.BashContinuousManagement;
import at.ac.tuwien.dsg.cloud.salsa.pioneer.instruments.BashInstrument;
import at.ac.tuwien.dsg.cloud.salsa.pioneer.instruments.BinaryExecutionInstrument;
import at.ac.tuwien.dsg.cloud.salsa.pioneer.instruments.ChefSoloInstrument;
import at.ac.tuwien.dsg.cloud.salsa.pioneer.instruments.DockerConfigurator;
import at.ac.tuwien.dsg.cloud.salsa.pioneer.instruments.WarInstrument;
import at.ac.tuwien.dsg.cloud.salsa.pioneer.utils.PioneerConfiguration;
import at.ac.tuwien.dsg.cloud.salsa.pioneer.utils.SystemFunctions;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Level;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;

/**
 *
 * @author Duc-Hung Le
 */
public class Main {

    static Logger logger = PioneerConfiguration.logger;
    static Queue<SalsaMsgConfigureArtifact> taskQueue = new LinkedList<>();
    static final MessageClientFactory factory = new MessageClientFactory(PioneerConfiguration.getBroker(), PioneerConfiguration.getBrokerType());

    public static void main(String[] args) {

        logger.debug("Starting pioneer ...");
        String command = "startserver";
        if (args.length > 0) {
            command = args[0];
        }

        MessageSubscribeInterface subscribeSalsaEngineRequests = factory.getMessageSubscriber(new SalsaMessageHandling() {
            @Override
            public void handleMessage(SalsaMessage msg) {
                switch (msg.getMsgType()) {
                    case salsa_deploy: {
                        SalsaMsgConfigureArtifact confInfo = SalsaMsgConfigureArtifact.fromJson(msg.getPayload());
                        if (!confInfo.getPioneerID().equals(PioneerConfiguration.getPioneerID())) {
                            break;
                        }
                        taskQueue.offer(confInfo);
                        logger.debug("Received message for DEPLOYMENT: " + msg.toJson() + ", put in queue. QueueSize: " + taskQueue.size());
                        break;
                    }
                    case salsa_reconfigure: {
                        SalsaMsgConfigureArtifact cmd = SalsaMsgConfigureArtifact.fromJson(msg.getPayload());
                        logger.debug("Received a reconfiguration command for: " + cmd.getUser() + "/" + cmd.getService() + "/" + cmd.getUnit() + "/" + cmd.getInstance() + ". ActionName: " + cmd.getActionName() + ", ActionID: " + cmd.getActionID());
                        // send back message that the pioneer received the command
                        SalsaMsgConfigureState confState = new SalsaMsgConfigureState(cmd.getActionID(), SalsaMsgConfigureState.CONFIGURATION_STATE.PROCESSING, 0, "Pioneer received the request and is processing. Action ID: " + cmd.getActionID());
                        SalsaMessage notifyMsg = new SalsaMessage(SalsaMessage.MESSAGE_TYPE.salsa_messageReceived, PioneerConfiguration.getPioneerID(), SalsaMessageTopic.PIONEER_UPDATE_CONFIGURATION_STATE, "", confState.toJson());
                        MessagePublishInterface publish = factory.getMessagePublisher();
                        publish.pushMessage(notifyMsg);
                        // now reconfigure: only support script now
                        int returnCode = SystemFunctions.executeCommandGetReturnCode(cmd.getRunByMe(), PioneerConfiguration.getWorkingDirOfInstance(cmd.getUnit(), cmd.getInstance()), PioneerConfiguration.getPioneerID());
                        // And update the configuration result
                        SalsaMsgConfigureState confResult;
                        if (returnCode == 0) {
                            confResult = new SalsaMsgConfigureState(cmd.getActionID(), SalsaMsgConfigureState.CONFIGURATION_STATE.SUCCESSFUL, 0, "Action is successful: " + cmd.getRunByMe());
                        } else {
                            confResult = new SalsaMsgConfigureState(cmd.getActionID(), SalsaMsgConfigureState.CONFIGURATION_STATE.SUCCESSFUL, 1, "Action is failed: " + cmd.getRunByMe() + ". Return code: " + returnCode);
                        }
                        MessagePublishInterface publish_conf = factory.getMessagePublisher();
                        SalsaMessage reply = new SalsaMessage(SalsaMessage.MESSAGE_TYPE.salsa_configurationStateUpdate, PioneerConfiguration.getPioneerID(), SalsaMessageTopic.PIONEER_UPDATE_CONFIGURATION_STATE, null, confResult.toJson());
                        publish_conf.pushMessage(reply);
                        logger.debug("Result is published !");
                        break;
                    }
                    case salsa_configurationStateUpdate: {
                        break;
                    }
                    case salsa_messageReceived: {
                        break;
                    }
                    default: {
                        logger.error("Message type is not support !" + msg.getMsgType());
                        break;
                    }
                }
            }
        });
        if (subscribeSalsaEngineRequests == null) {
            logger.error("Cannot subscribe to the message queue: {}, type: {}. Pioneer QUIT !", PioneerConfiguration.getBroker(), PioneerConfiguration.getBrokerType());
            return;
        }
        subscribeSalsaEngineRequests.subscribe(SalsaMessageTopic.getPioneerTopicByID(PioneerConfiguration.getPioneerID()));

        // send information of this pioneer
        MessagePublishInterface publish = factory.getMessagePublisher();
        publish.pushMessage(new SalsaMessage(SalsaMessage.MESSAGE_TYPE.salsa_pioneerActivated, PioneerConfiguration.getPioneerID(), SalsaMessageTopic.PIONEER_REGISTER_AND_HEARBEAT, null, PioneerConfiguration.getPioneerInfo().toJson()));

        new Thread(new TaskHandleThread()).start();
        // also start an elise conductor
        //new Thread(new EliseConductorThread()).start();
    }

    private static class TaskHandleThread implements Runnable {

        @Override
        public void run() {
            int count = 0;
            while (true) {
                SalsaMsgConfigureArtifact confInfo = taskQueue.poll();
                if (confInfo != null) {
                    logger.debug("FULLED A TASK FROM TASKQUEUE. The queue how have: " + taskQueue.size());
                    handleConfigurationTask(confInfo);
                    count =0;
                } else {
                    try {
                        if (count <= 2) {
                            count +=1;
                            logger.debug("TASKQUEUE is empty (show {}/3).", count);                            
                        }
                        Thread.sleep(2000);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
    }

    private static void handleConfigurationTask(SalsaMsgConfigureArtifact confInfo) {
        // feedback that Pioneer is PROCESSING the request
        SalsaMsgConfigureState confState = new SalsaMsgConfigureState(confInfo.getActionID(), SalsaMsgConfigureState.CONFIGURATION_STATE.PROCESSING, 0, "Pioneer received the request and is processing. Action ID: " + confInfo.getActionID());
        SalsaMessage notifyMsg = new SalsaMessage(SalsaMessage.MESSAGE_TYPE.salsa_messageReceived, PioneerConfiguration.getPioneerID(), SalsaMessageTopic.PIONEER_UPDATE_CONFIGURATION_STATE, "", confState.toJson());
        MessagePublishInterface publish_deploy = factory.getMessagePublisher();
        publish_deploy.pushMessage(notifyMsg);

        logger.debug("The command is attracted: Target to pioneer {}, and mine is {}!", confInfo.getPioneerID(), PioneerConfiguration.getPioneerID());
        if (confInfo.getPioneerID().equals(PioneerConfiguration.getPioneerID())) {
            SystemFunctions.writeSystemVariable(confInfo.getEnvironment());
            logger.debug("Executing the first deployment ! ConfInfo: " + confInfo.toJson());

            if (confInfo.getActionName().equals(CommonLifecycle.DEPLOY)) {
                logger.debug("Yes, the action name is: deploy");
                SalsaMsgConfigureState downloadResult = downloadArtifact(confInfo);
                if (downloadResult.getState().equals(SalsaMsgConfigureState.CONFIGURATION_STATE.ERROR)) {
                    MessagePublishInterface publish_conf = factory.getMessagePublisher();
                    SalsaMessage reply = new SalsaMessage(SalsaMessage.MESSAGE_TYPE.salsa_configurationStateUpdate, PioneerConfiguration.getPioneerID(), SalsaMessageTopic.PIONEER_UPDATE_CONFIGURATION_STATE, null, downloadResult.toJson());
                    publish_conf.pushMessage(reply);
                    logger.error("Artifact download failed for unit: {}/{}/{}. The result is sent back to center." + confInfo.getService(), confInfo.getUnit(), confInfo.getInstance());
                } else {
                    logger.debug("Finished download artifacts !");
                    ArtifactConfigurationInterface confModule = selectConfigurationModule(confInfo);
                    logger.debug("Select module done !");
                    if (confModule != null) {
                        SalsaMsgConfigureState confResult = confModule.configureArtifact(confInfo);
                        logger.debug("Configuration done ! Result: " + confResult.getState() + ", Info: " + confResult.getDomainID());

                        MessagePublishInterface publish_conf = factory.getMessagePublisher();
                        SalsaMessage reply = new SalsaMessage(SalsaMessage.MESSAGE_TYPE.salsa_configurationStateUpdate, PioneerConfiguration.getPioneerID(), SalsaMessageTopic.PIONEER_UPDATE_CONFIGURATION_STATE, null, confResult.toJson());
                        publish_conf.pushMessage(reply);
                        logger.debug("Result is published !");
                    } else {
                        SalsaMsgConfigureState confResult = new SalsaMsgConfigureState(confInfo.getActionID(), SalsaMsgConfigureState.CONFIGURATION_STATE.ERROR, 101, "Cannot find configuration module to execute action!");
                        MessagePublishInterface publish_conf = factory.getMessagePublisher();
                        SalsaMessage reply = new SalsaMessage(SalsaMessage.MESSAGE_TYPE.salsa_configurationStateUpdate, PioneerConfiguration.getPioneerID(), SalsaMessageTopic.PIONEER_UPDATE_CONFIGURATION_STATE, null, confResult.toJson());
                        publish_conf.pushMessage(reply);
                        logger.debug("Result is published !");
                    }
                }
            }
        }
    }

    private static class EliseConductorThread implements Runnable {

        @Override
        public void run() {
            EliseConductorManager.runConductor();
        }
    }

    private static SalsaMsgConfigureState downloadArtifact(SalsaMsgConfigureArtifact confInfo) {
        logger.debug("Inside downloadArtifact method");
        logger.debug("Preparing artifact for node: " + confInfo.getUnit());
        if (confInfo.getArtifacts() == null) {
            new File(PioneerConfiguration.getWorkingDirOfInstance(confInfo.getUnit(), confInfo.getInstance())).mkdirs();
            return new SalsaMsgConfigureState(confInfo.getActionID(), SalsaMsgConfigureState.CONFIGURATION_STATE.SUCCESSFUL, 0, "No need to download artifact");
        }
        logger.debug("Number of artifact: " + confInfo.getArtifacts().size());
        for (SalsaMsgConfigureArtifact.DeploymentArtifact art : confInfo.getArtifacts()) {
            try {
                logger.debug("Downloading artifact for: " + confInfo.getUnit() + ". URL:" + art);
                URL url = new URL(art.getReference());
                String filePath = PioneerConfiguration.getWorkingDirOfInstance(confInfo.getUnit(), confInfo.getInstance())
                        + File.separator + FilenameUtils.getName(url.getFile());
                logger.debug("Download file from:" + url.toString() + "\nSave to file:" + filePath);
                FileUtils.copyURLToFile(url, new File(filePath));
                (new File(filePath)).setExecutable(true);

                // if the artifact is an archieve, try to extract it
                extractFile(filePath, PioneerConfiguration.getWorkingDirOfInstance(confInfo.getUnit(), confInfo.getInstance()));
            } catch (IOException e) {   // in the case cannot create artifact
                logger.error("Error while downloading artifact for: " + confInfo.getUnit() + ". URL:" + art);
                return new SalsaMsgConfigureState(confInfo.getActionID(), SalsaMsgConfigureState.CONFIGURATION_STATE.ERROR, 0, "Failed to download artifact at: " + art.getReference());
            }
        }
        return new SalsaMsgConfigureState(confInfo.getActionID(), SalsaMsgConfigureState.CONFIGURATION_STATE.SUCCESSFUL, 0, "All artifacts are download successfully.");
    }

    // support only .tar.gz at this time
    private static void extractFile(String filePath, String workingDir) {
        if (filePath.endsWith("tar.gz")) {
            SystemFunctions.executeCommandGetReturnCode("tar -xvzf " + filePath, workingDir, "ExtractFileModule");
        }
    }

    private static ArtifactConfigurationInterface selectConfigurationModule(SalsaMsgConfigureArtifact confInfo) {
        logger.debug("Selecting configuration module for: " + confInfo.getActionID() + ", " + confInfo.getRunByMe());
        if (confInfo.getArtifactType() == null) {
            // in this case, the runByMe contain a binary command
            if (!confInfo.getRunByMe().isEmpty()) {
                return new BinaryExecutionInstrument();
            } else {
                return null;
            }
        }
        switch (confInfo.getArtifactType()) {
            case apt:
                logger.debug("Selected module apt !");
                return new AptGetInstrument();
            case chef:
                logger.debug("Selected module chef !");
                return new ChefSoloInstrument();
            case chefSolo:
                logger.debug("Selected module chef solo !");
                return new ChefSoloInstrument();
            case dockerfile:
                logger.debug("Selected module docker !");
                return new DockerConfigurator();
            case sh:
                logger.debug("Selected module sh !");
                return new BashInstrument();
            case shcont:
                logger.debug("Selected module sh cont !");
                return new BashContinuousInstrument();
            case war:
                logger.debug("Selected module war !");
                return new WarInstrument();
            default:
                logger.debug("Cannot select any module !");
                return null;
        }
    }

    public int executeLifecycleAction(SalsaMsgConfigureArtifact cmd) {
        logger.debug("Recieve command to executing action: " + cmd.getUnit() + "/" + cmd.getInstance() + "/" + cmd.getActionName());
        String actionName = cmd.getActionName();
        logger.debug("Found a custom action: " + actionName);

        if (cmd.getPreRunByMe() == null || !cmd.getPreRunByMe().trim().isEmpty()) { // something need to to be run first
            int code = SystemFunctions.executeCommandGetReturnCode(cmd.getPreRunByMe(), PioneerConfiguration.getWorkingDirOfInstance(cmd.getUnit(), cmd.getInstance()), PioneerConfiguration.getPioneerID());
            if (code != 0) {
                logger.error("Error when execute the pre-action: " + cmd.getPreRunByMe() + ". The process still will be continued !");
            }
        }

        // if this is an undeployment, execute it
        if (actionName.equals("undeploy")) { // if this is an undeploy action, remove node
            logger.debug("Recieve command to remove node: " + cmd.getUnit() + "/" + cmd.getInstance());

            // TODO: more detail model for specific DOCKER. Here we assume that AppContainer is DOCKER
            if (cmd.getUnitType() == ServiceCategory.AppContainer) {
                DockerConfigurator docker = new DockerConfigurator();
                docker.removeDockerContainer(cmd.getRunByMe().trim());
            } else {
                BashContinuousManagement.killProcessInstance(cmd.getActionID());
            }
            return 0;
        } else {      // other actions
            if (cmd.getRunByMe().trim().length() == 0) {
                logger.debug("Do not find any running command for the action: " + actionName);
            }
            SystemFunctions.executeCommandGetReturnCode(cmd.getRunByMe(), PioneerConfiguration.getWorkingDirOfInstance(cmd.getUnit(), cmd.getInstance()), PioneerConfiguration.getPioneerID());
        }

        return 0;
    }

}
