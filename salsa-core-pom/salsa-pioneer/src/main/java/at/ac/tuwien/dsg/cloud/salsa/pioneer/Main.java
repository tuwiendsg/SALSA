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

import at.ac.tuwien.dsg.cloud.salsa.messaging.MQTTAdaptor.MQTTPublish;
import at.ac.tuwien.dsg.cloud.salsa.messaging.MQTTAdaptor.MQTTSubscribe;
import at.ac.tuwien.dsg.cloud.salsa.messaging.messageInterface.MessagePublishInterface;
import at.ac.tuwien.dsg.cloud.salsa.messaging.messageInterface.MessageSubscribeInterface;
import at.ac.tuwien.dsg.cloud.salsa.messaging.model.SalsaMessage;
import at.ac.tuwien.dsg.cloud.salsa.messaging.model.SalsaMessageTopic;
import at.ac.tuwien.dsg.cloud.salsa.messaging.model.commands.SalsaMsgConfigureArtifact;
import at.ac.tuwien.dsg.cloud.salsa.messaging.model.commands.SalsaMsgConfigureState;
import at.ac.tuwien.dsg.cloud.salsa.messaging.model.items.ServiceCategory;
import at.ac.tuwien.dsg.cloud.salsa.pioneer.instruments.AptGetInstrument;
import at.ac.tuwien.dsg.cloud.salsa.pioneer.instruments.ArtifactConfigurationInterface;
import at.ac.tuwien.dsg.cloud.salsa.pioneer.instruments.BashContinuousInstrument;
import at.ac.tuwien.dsg.cloud.salsa.pioneer.instruments.BashContinuousManagement;
import at.ac.tuwien.dsg.cloud.salsa.pioneer.instruments.BashInstrument;
import at.ac.tuwien.dsg.cloud.salsa.pioneer.instruments.ChefSoloInstrument;
import at.ac.tuwien.dsg.cloud.salsa.pioneer.instruments.DockerConfigurator;
import at.ac.tuwien.dsg.cloud.salsa.pioneer.instruments.WarInstrument;
import at.ac.tuwien.dsg.cloud.salsa.pioneer.queueLogger.QueueAppender;
import at.ac.tuwien.dsg.cloud.salsa.pioneer.utils.PioneerConfiguration;
import at.ac.tuwien.dsg.cloud.salsa.pioneer.utils.SystemFunctions;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import java.util.logging.LogManager;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Level;
import org.apache.log4j.PatternLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Duc-Hung Le
 */
public class Main {

    static Logger logger = PioneerConfiguration.logger;

    public static void main(String[] args) {

        logger.debug("Starting pioneer ...");
        String command = "startserver";
        if (args.length > 0) {
            command = args[0];
        }

        MessageSubscribeInterface subscribe = new MQTTSubscribe(PioneerConfiguration.getBroker()) {
            @Override
            public void handleMessage(SalsaMessage msg) {

                switch (msg.getMsgType()) {
                    case discover: {
                        break;
                    }
                    case deploy: {
                        SalsaMsgConfigureArtifact confInfo = SalsaMsgConfigureArtifact.fromJson(msg.getPayload());
                        if (!confInfo.getPioneerID().equals(PioneerConfiguration.getPioneerID())) {
                            break;
                        }
                        logger.debug("Received message for DEPLOYMENT: " + msg.toJson());
                        // feedback that Pioneer is PROCESSING the request
                        SalsaMsgConfigureState confState = new SalsaMsgConfigureState(confInfo.getActionID(), SalsaMsgConfigureState.CONFIGURATION_STATE.PROCESSING, 0, "Pioneer received the request and is processing. Action ID: " + confInfo.getActionID());
                        SalsaMessage notifyMsg = new SalsaMessage(SalsaMessage.MESSAGE_TYPE.messageReceived, PioneerConfiguration.getPioneerID(), SalsaMessageTopic.PIONEER_UPDATE_STATE, "", confState.toJson());
                        MessagePublishInterface publish_deploy = new MQTTPublish(PioneerConfiguration.getBroker());
                        publish_deploy.pushMessage(notifyMsg);

                        logger.debug("The command is attracted: Target to pioneer {}, and mine is {}!", confInfo.getPioneerID(), PioneerConfiguration.getPioneerID());
                        if (confInfo.getPioneerID().equals(PioneerConfiguration.getPioneerID())) {
                            SystemFunctions.writeSystemVariable(confInfo.getEnvironment());
                            logger.debug("Executing the first deployment ! ConfInfo: " + confInfo.toJson());
                            
                            if (confInfo.getActionName().equals(CommonLifecycle.DEPLOY)) {
                                logger.debug("Yes, the action name is: deploy");
                                SalsaMsgConfigureState downloadResult = downloadArtifact(confInfo);
                                if (downloadResult.getState().equals(SalsaMsgConfigureState.CONFIGURATION_STATE.ERROR)) {
                                    MessagePublishInterface publish_conf = new MQTTPublish(PioneerConfiguration.getBroker());
                                    SalsaMessage reply = new SalsaMessage(SalsaMessage.MESSAGE_TYPE.answer, PioneerConfiguration.getPioneerID(), SalsaMessageTopic.PIONEER_UPDATE_STATE, null, downloadResult.toJson());
                                    publish_conf.pushMessage(reply);
                                    logger.error("Artifact download failed for unit: {}/{}/{}. The result is sent back to center." + confInfo.getService(), confInfo.getUnit(), confInfo.getInstance());
                                } else {
                                    logger.debug("Finished download artifacts !");
                                    ArtifactConfigurationInterface confModule = selectConfigurationModule(confInfo);
                                    logger.debug("Select module done !");
                                    SalsaMsgConfigureState confResult = confModule.configureArtifact(confInfo);
                                    logger.debug("Configuration done ! Result: " + confResult.getState() + ", Info: " + confResult.getInfo());

                                    MessagePublishInterface publish_conf = new MQTTPublish(PioneerConfiguration.getBroker());
                                    SalsaMessage reply = new SalsaMessage(SalsaMessage.MESSAGE_TYPE.answer, PioneerConfiguration.getPioneerID(), SalsaMessageTopic.PIONEER_UPDATE_STATE, null, confResult.toJson());
                                    publish_conf.pushMessage(reply);
                                    logger.debug("Result is published !");
                                }

                            }
                        }
                        break;
                    }
                    case reconfigure: {
                        SalsaMsgConfigureArtifact cmd = SalsaMsgConfigureArtifact.fromJson(msg.getPayload());
                        logger.debug("Received a reconfiguration command for: " + cmd.getUser() + "/" + cmd.getService() + "/" + cmd.getUnit() + "/" + cmd.getInstance() + ". ActionName: " + cmd.getActionName() + ", ActionID: " + cmd.getActionID());
                        // send back message that the pioneer received the command
                        SalsaMsgConfigureState confState = new SalsaMsgConfigureState(cmd.getActionID(), SalsaMsgConfigureState.CONFIGURATION_STATE.PROCESSING, 0, "Pioneer received the request and is processing. Action ID: " + cmd.getActionID());
                        SalsaMessage notifyMsg = new SalsaMessage(SalsaMessage.MESSAGE_TYPE.messageReceived, PioneerConfiguration.getPioneerID(), SalsaMessageTopic.PIONEER_UPDATE_STATE, "", confState.toJson());
                        MessagePublishInterface publish = new MQTTPublish(PioneerConfiguration.getBroker());
                        publish.pushMessage(notifyMsg);
                        // now reconfigure: only support script now
                        SystemFunctions.executeCommandGetReturnCode(cmd.getRunByMe(), PioneerConfiguration.getWorkingDirOfInstance(cmd.getUnit(), cmd.getInstance()), PioneerConfiguration.getPioneerID());
                        break;
                    }
                    case answer: {
                        break;
                    }
                    case messageReceived: {
                        break;
                    }
                    default: {
                        logger.error("Message type is not support !" + msg.getMsgType());
                        break;
                    }
                }
            }
        };
        subscribe.subscribe(SalsaMessageTopic.CENTER_REQUEST_PIONEER);

        // send information of this pioneer
        MessagePublishInterface publish = new MQTTPublish(PioneerConfiguration.getBroker());
        publish.pushMessage(new SalsaMessage(SalsaMessage.MESSAGE_TYPE.pioneer_alived, PioneerConfiguration.getPioneerID(), SalsaMessageTopic.PIONEER_SYNC, null, PioneerConfiguration.getPioneerInfo().toJson()));
    }

    private static SalsaMsgConfigureState downloadArtifact(SalsaMsgConfigureArtifact confInfo) {
        logger.debug("Inside downloadArtifact method");
        logger.debug("Preparing artifact for node: " + confInfo.getUnit());
        if (confInfo.getArtifacts() == null) {
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
            if (cmd.getRunByMe().length() == 0) {
                logger.debug("Do not find any running command for the action: " + actionName);
            }
            SystemFunctions.executeCommandGetReturnCode(cmd.getRunByMe(), PioneerConfiguration.getWorkingDirOfInstance(cmd.getUnit(), cmd.getInstance()), PioneerConfiguration.getPioneerID());
        }

        return 0;
    }

}
