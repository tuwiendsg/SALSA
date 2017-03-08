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
package at.ac.tuwien.dsg.salsa.pioneer;

import at.ac.tuwien.dsg.salsa.docker.DockerConfigurator;
import at.ac.tuwien.dsg.salsa.model.salsa.interfaces.ConfigurationModule;
import at.ac.tuwien.dsg.salsa.messaging.messageInterface.MessagePublishInterface;
import at.ac.tuwien.dsg.salsa.messaging.messageInterface.MessageSubscribeInterface;

import at.ac.tuwien.dsg.salsa.messaging.messageInterface.MessageClientFactory;
import at.ac.tuwien.dsg.salsa.messaging.messageInterface.SalsaMessageHandling;
import at.ac.tuwien.dsg.salsa.model.salsa.info.SalsaConfigureTask;
import at.ac.tuwien.dsg.salsa.model.salsa.info.SalsaConfigureResult;

import at.ac.tuwien.dsg.salsa.messaging.protocol.SalsaMessage;
import at.ac.tuwien.dsg.salsa.messaging.protocol.SalsaMessageTopic;
import at.ac.tuwien.dsg.salsa.model.enums.SalsaArtifactType;
import at.ac.tuwien.dsg.salsa.model.salsa.confparameters.ShellScriptParameters;
import at.ac.tuwien.dsg.salsa.model.salsa.info.SalsaMsgUpdateMetadata;
import at.ac.tuwien.dsg.salsa.pioneer.utils.PioneerConfiguration;
import at.ac.tuwien.dsg.salsa.pioneer.utils.SystemFunctions;
import at.ac.tuwien.dsg.salsa.shellscript.BashContinuousInstrument;
import at.ac.tuwien.dsg.salsa.shellscript.BashContinuousManagement;
import at.ac.tuwien.dsg.salsa.shellscript.BashInstrument;
import at.ac.tuwien.dsg.salsa.shellscript.BinaryExecutionInstrument;
import at.ac.tuwien.dsg.salsa.webapp.WarInstrument;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Duc-Hung Le
 */
public class Main {

    static Logger logger = LoggerFactory.getLogger("salsa");
    static Queue<SalsaConfigureTask> deploymentQueue = new LinkedList<>();
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
                logger.debug("A private message arrive: " + msg.toJson() + ", message type is: " + msg.getMsgType());
                switch (msg.getMsgType()) {
                    case salsa_deploy: {
                        logger.debug("Processing deployment request ...");
                        SalsaConfigureTask confInfo = SalsaConfigureTask.fromJson(msg.getPayload());
                        logger.debug("Configuration info: actionID : {}, parameters: {}", confInfo.getActionID(), confInfo.getParameters().toString());
                        if (confInfo.getPioneerID() == null) {
                            logger.error("The request does not specify pioneer ID, not I am not sure if it is for me !");
                            break;
                        }
                        if (!confInfo.getPioneerID().equals(PioneerConfiguration.getPioneerID())) {
                            logger.debug("Received a message but not for me, it is for pioneer: " + confInfo.getPioneerID());
                            break;
                        }
                        logger.debug("Adding a configuration task to the queue. Current queue size: " + deploymentQueue.size());
                        deploymentQueue.add(confInfo);
                        logger.debug("Received message for DEPLOYMENT: " + msg.toJson() + ", put in queue. QueueSize: " + deploymentQueue.size());
                        break;
                    }
                    case salsa_reconfigure: {
                        logger.debug("Processing reconfiguration request ...");
                        SalsaConfigureTask cmd = SalsaConfigureTask.fromJson(msg.getPayload());
                        logger.debug("Received a reconfiguration command for: " + cmd.getUser() + "/" + cmd.getService() + "/" + cmd.getUnit() + "/" + cmd.getInstance() + ". ActionName: " + cmd.getActionName() + ", ActionID: " + cmd.getActionID());
                        // send back message that the pioneer received the command
                        SalsaConfigureResult confState = new SalsaConfigureResult(cmd.getActionID(), SalsaConfigureResult.CONFIGURATION_STATE.PROCESSING, 0, "Pioneer received the request and is processing. Action ID: " + cmd.getActionID());
                        SalsaMessage notifyMsg = new SalsaMessage(SalsaMessage.MESSAGE_TYPE.salsa_messageReceived, PioneerConfiguration.getPioneerID(), SalsaMessageTopic.PIONEER_UPDATE_CONFIGURATION_STATE, "", confState.toJson());
                        MessagePublishInterface publish = factory.getMessagePublisher();
                        publish.pushMessage(notifyMsg);
                        // now reconfigure: only support script now                        
                        int returnCode = executeLifecycleAction(cmd);
                        // And update the configuration result
                        SalsaConfigureResult confResult;
                        if (returnCode == 0) {
                            confResult = new SalsaConfigureResult(cmd.getActionID(), SalsaConfigureResult.CONFIGURATION_STATE.SUCCESSFUL, 0, "Action is successful: " + cmd.getParameters(ShellScriptParameters.runByMe));
                        } else {
                            confResult = new SalsaConfigureResult(cmd.getActionID(), SalsaConfigureResult.CONFIGURATION_STATE.SUCCESSFUL, 1, "Action is failed: " + cmd.getParameters(ShellScriptParameters.runByMe) + ". Return code: " + returnCode);
                        }
                        MessagePublishInterface publish_conf = factory.getMessagePublisher();
                        SalsaMessage reply = new SalsaMessage(SalsaMessage.MESSAGE_TYPE.salsa_configurationStateUpdate, PioneerConfiguration.getPioneerID(), SalsaMessageTopic.PIONEER_UPDATE_CONFIGURATION_STATE, null, confResult.toJson());
                        publish_conf.pushMessage(reply);
                        logger.debug("Result is published !");
                        break;
                    }
                    case salsa_shutdownPioneer: {
                        logger.debug("Received a message to shutdown this pioneer. Bye!");
                        System.exit(0);
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

        MessageSubscribeInterface subscribePublicChannel = factory.getMessageSubscriber(new SalsaMessageHandling() {
            @Override
            public void handleMessage(SalsaMessage salsaMessage) {
                logger.debug("A public message arrive: " + salsaMessage.toJson());
                // send information of this pioneer when get Discover command
                if (salsaMessage.getMsgType().equals(SalsaMessage.MESSAGE_TYPE.discover)) {
                    MessagePublishInterface publish = factory.getMessagePublisher();
                    publish.pushMessage(new SalsaMessage(SalsaMessage.MESSAGE_TYPE.salsa_pioneerActivated, PioneerConfiguration.getPioneerID(), SalsaMessageTopic.PIONEER_REGISTER_AND_HEARBEAT, null, PioneerConfiguration.getPioneerInfo().toJson()));
                }
            }
        });
        subscribePublicChannel.subscribe(SalsaMessageTopic.PIONEER_REGISTER_AND_HEARBEAT);

        // send information of this pioneer
        MessagePublishInterface publish = factory.getMessagePublisher();
        String pioneerJson = PioneerConfiguration.getPioneerInfo().toJson();
        publish.pushMessage(new SalsaMessage(SalsaMessage.MESSAGE_TYPE.salsa_pioneerActivated, PioneerConfiguration.getPioneerID(), SalsaMessageTopic.PIONEER_REGISTER_AND_HEARBEAT, null, pioneerJson));

        logger.debug("Pioneer start \n: " + pioneerJson);
        
        new Thread(new TaskHandleThread()).start();

        

        // also start an elise conductor
        //new Thread(new EliseConductorThread()).start();
    }

    // This thread check the Task Queue, pick up a task, and handle it
    private static class TaskHandleThread implements Runnable {

        @Override
        public void run() {
            int count = 0;
            while (true) {
                SalsaConfigureTask confInfo = deploymentQueue.poll();
                if (confInfo != null) {
                    logger.debug("FULLED A TASK FROM TASKQUEUE. The queue how have: " + deploymentQueue.size());
                    handleConfigurationTask(confInfo);
                    count = 0;
                } else {
                    try {
                        if (count <= 3) {
                            count += 1;
                            logger.debug("TASKQUEUE is empty (show {}/3).", count);
                        }
                        Thread.sleep(2000);
                    } catch (InterruptedException ex) {
                        logger.error("Task handling thread is interrupted, pioneer is no longer to process request!");
                        ex.printStackTrace();
                    }
                }
            }
        }
    }

    private static void handleConfigurationTask(SalsaConfigureTask confInfo) {
        // feedback that Pioneer is PROCESSING the request
        logger.debug("Start to handle the task: {} of {}/{}", confInfo.getActionName(), confInfo.getUnit(), confInfo.getInstance());
        SalsaConfigureResult confState = new SalsaConfigureResult(confInfo.getActionID(), SalsaConfigureResult.CONFIGURATION_STATE.PROCESSING, 0, "Pioneer received the request and is processing. Action ID: " + confInfo.getActionID());
        SalsaMessage notifyMsg = new SalsaMessage(SalsaMessage.MESSAGE_TYPE.salsa_messageReceived, PioneerConfiguration.getPioneerID(), SalsaMessageTopic.PIONEER_UPDATE_CONFIGURATION_STATE, "", confState.toJson());
        logger.debug("Start to publish notification message ...");
        MessagePublishInterface publish_deploy = factory.getMessagePublisher();
        publish_deploy.pushMessage(notifyMsg);

        logger.debug("The command is attracted: Target to pioneer {}, and mine is {}!", confInfo.getPioneerID(), PioneerConfiguration.getPioneerID());

        if (confInfo.getPioneerID().equals(PioneerConfiguration.getPioneerID())) {
            logger.debug("Now write system variable");
            SystemFunctions.writeSystemVariable(confInfo.getEnvironment());
            logger.debug("Executing the first deployment ! ConfInfo: " + confInfo.toJson());

            if (confInfo.getActionName().equals(CommonLifecycle.DEPLOY)) {
                logger.debug("Yes, the action name is: deploy");
                SalsaConfigureResult downloadResult = downloadArtifact(confInfo);
                if (downloadResult.getState().equals(SalsaConfigureResult.CONFIGURATION_STATE.ERROR)) {
                    MessagePublishInterface publish_conf = factory.getMessagePublisher();
                    SalsaMessage reply = new SalsaMessage(SalsaMessage.MESSAGE_TYPE.salsa_configurationStateUpdate, PioneerConfiguration.getPioneerID(), SalsaMessageTopic.PIONEER_UPDATE_CONFIGURATION_STATE, null, downloadResult.toJson());
                    publish_conf.pushMessage(reply);
                    logger.error("Artifact download failed for unit: {}/{}/{}. The result is sent back to center." + confInfo.getService(), confInfo.getUnit(), confInfo.getInstance());
                } else {
                    logger.debug("Finished download artifacts !");
                    ConfigurationModule confModule = selectConfigurationModule(confInfo);
                    logger.debug("Select module done !");
                    if (confModule != null) {

                        Map<String, String> params = new HashMap();
                        params.put("workingdir", PioneerConfiguration.getWorkingDirOfInstance(confInfo.getUnit(), confInfo.getInstance()));

                        SalsaConfigureResult confResult = confModule.configureArtifact(confInfo, params);
                        logger.debug("Configuration done ! Result: " + confResult.getState() + ", Info: " + confResult.getDomainID());

                        MessagePublishInterface publish_conf = factory.getMessagePublisher();
                        SalsaMessage reply = new SalsaMessage(SalsaMessage.MESSAGE_TYPE.salsa_configurationStateUpdate, PioneerConfiguration.getPioneerID(), SalsaMessageTopic.PIONEER_UPDATE_CONFIGURATION_STATE, null, confResult.toJson());
                        publish_conf.pushMessage(reply);
                        logger.debug("Result is published !");
                    } else {
                        SalsaConfigureResult confResult = new SalsaConfigureResult(confInfo.getActionID(), SalsaConfigureResult.CONFIGURATION_STATE.ERROR, 101, "Cannot find configuration module to execute action!");
                        MessagePublishInterface publish_conf = factory.getMessagePublisher();
                        SalsaMessage reply = new SalsaMessage(SalsaMessage.MESSAGE_TYPE.salsa_configurationStateUpdate, PioneerConfiguration.getPioneerID(), SalsaMessageTopic.PIONEER_UPDATE_CONFIGURATION_STATE, null, confResult.toJson());
                        publish_conf.pushMessage(reply);
                        logger.debug("Result is published !");
                    }
                }
            }
        }
    }

    private static SalsaConfigureResult downloadArtifact(SalsaConfigureTask confInfo) {
        logger.debug("Inside downloadArtifact method");
        logger.debug("Preparing artifact for node: " + confInfo.getUnit());
        if (confInfo.getArtifacts() == null) {
            new File(PioneerConfiguration.getWorkingDirOfInstance(confInfo.getUnit(), confInfo.getInstance())).mkdirs();
            return new SalsaConfigureResult(confInfo.getActionID(), SalsaConfigureResult.CONFIGURATION_STATE.SUCCESSFUL, 0, "No need to download artifact");
        }
        logger.debug("Number of artifact: " + confInfo.getArtifacts().size());
        for (SalsaConfigureTask.DeploymentArtifact art : confInfo.getArtifacts()) {
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

                // if artifact is META, parse new capabilities and publish a message to update capabilities
                if (art.getType().equals(SalsaArtifactType.metadata.getString())) {
                    logger.debug("Found a meta artifact: " + filePath);

                    HashMap<String, String> metadataActions = new HashMap<>();
                    try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                        logger.debug("Ok, now reading meta data file");
                        while (true) {
                            String inputLine = reader.readLine();
                            logger.debug("Read a meta data line: " + inputLine);
                            if (inputLine == null) {
                                logger.debug("Reading meta data file completed");
                                break;
                            } else if (inputLine.startsWith("action.")) {
                                logger.debug("  -> The line starts with action. , parsing the action");
                                String newActionName = inputLine.substring(inputLine.indexOf(".") + 1, inputLine.indexOf("="));
                                logger.debug(" --> action name: " + newActionName);
                                String newActionRef = inputLine.substring(inputLine.indexOf("=") + 1);
                                logger.debug(" --> action ref: " + newActionRef);
                                if (newActionName != null && newActionRef != null && !newActionName.trim().isEmpty() && !newActionRef.trim().isEmpty()) {
                                    metadataActions.put(newActionName, newActionRef);
                                    logger.debug("Parsing metadata action for {}/{}, action: {}, ref: {}", confInfo.getUnit(), confInfo.getInstance(), newActionName, newActionRef);
                                } else {
                                    logger.debug("Parsing error: either action or action reference is empty");
                                }
                            } else if (!inputLine.trim().isEmpty()) {
                                logger.debug("Do not parse custom attributes yet, pass this: {}", inputLine);
                            }
                        }
                    }

                    MessagePublishInterface publish_deploy = factory.getMessagePublisher();
                    SalsaMsgUpdateMetadata updateMsg = new SalsaMsgUpdateMetadata(confInfo, metadataActions);
                    SalsaMessage salsaMsg = new SalsaMessage(SalsaMessage.MESSAGE_TYPE.salsa_updateNodeMetadata, PioneerConfiguration.getPioneerID(), SalsaMessageTopic.PIONEER_UPDATE_CONFIGURATION_STATE, "", updateMsg.toJson());
                    publish_deploy.pushMessage(salsaMsg);
                }

            } catch (IOException e) {   // in the case cannot create artifact
                logger.error("Error while downloading artifact for: " + confInfo.getUnit() + ". URL:" + art);
                return new SalsaConfigureResult(confInfo.getActionID(), SalsaConfigureResult.CONFIGURATION_STATE.ERROR, 0, "Failed to download artifact at: " + art.getReference());
            }
        }
        return new SalsaConfigureResult(confInfo.getActionID(), SalsaConfigureResult.CONFIGURATION_STATE.SUCCESSFUL, 0, "All artifacts are download successfully.");
    }

    // support only .tar.gz at this time
    private static void extractFile(String filePath, String workingDir) {
        if (filePath.endsWith("tar.gz")) {
            SystemFunctions.executeCommandGetReturnCode("tar -xvzf " + filePath, workingDir, "ExtractFileModule");
        }
    }

    private static ConfigurationModule selectConfigurationModule(SalsaConfigureTask confInfo) {
        logger.debug("Selecting configuration module for: " + confInfo.getActionID() + ", " + confInfo.getParameters(ShellScriptParameters.runByMe));
        if (confInfo.getArtifactType() == null) {
            // in this case, the runByMe contain a binary command
            if (!confInfo.getParameters(ShellScriptParameters.runByMe).isEmpty()) {
                return new BinaryExecutionInstrument();
            } else {
                return null;
            }
        }
        switch (confInfo.getArtifactType()) {
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

    public static int executeLifecycleAction(SalsaConfigureTask cmd) {
        logger.debug("Recieve command to executing action: " + cmd.getUnit() + "/" + cmd.getInstance() + "/" + cmd.getActionName());
        String actionName = cmd.getActionName();
        logger.debug("Found a custom action: " + actionName);

        if (cmd.getParameters(ShellScriptParameters.preRunByMe) == null || !cmd.getParameters(ShellScriptParameters.preRunByMe).trim().isEmpty()) { // something need to to be run first
            int code = SystemFunctions.executeCommandGetReturnCode(cmd.getParameters(ShellScriptParameters.preRunByMe), PioneerConfiguration.getWorkingDirOfInstance(cmd.getUnit(), cmd.getInstance()), PioneerConfiguration.getPioneerID());
            if (code != 0) {
                logger.error("Error when execute the pre-action: " + cmd.getParameters(ShellScriptParameters.preRunByMe) + ". The process still will be continued !");
            }
        }

        // if this is an undeployment, execute it
        if (actionName.equals("undeploy")) { // if this is an undeploy action, remove node
            logger.debug("Recieve command to remove node: " + cmd.getUnit() + "/" + cmd.getInstance());

            // TODO: more detail model for specific DOCKER. Here we assume that AppContainer is DOCKER
            if (cmd.getUnitType().equals("docker")) {// == ServiceCategory.docker) {
                logger.debug("This node is a docker container, start to remove it !");
                DockerConfigurator docker = new DockerConfigurator();
                docker.removeDockerContainer(cmd.getParameters(ShellScriptParameters.runByMe).trim());
            } else {
                BashContinuousManagement.killProcessInstance(cmd.getActionID());
            }
            return 0;
        } else {      // other actions
            if (cmd.getParameters(ShellScriptParameters.runByMe).trim().length() == 0) {
                logger.debug("Do not find any running command for the action: " + actionName);
            }
            SystemFunctions.executeCommandGetReturnCode(cmd.getParameters(ShellScriptParameters.runByMe), PioneerConfiguration.getWorkingDirOfInstance(cmd.getUnit(), cmd.getInstance()), PioneerConfiguration.getPioneerID());
        }

        return 0;
    }

}
