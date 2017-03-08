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
package at.ac.tuwien.dsg.salsa.engine.services;

import at.ac.tuwien.dsg.salsa.engine.exceptions.EngineConnectionException;
import at.ac.tuwien.dsg.salsa.engine.services.enabler.PioneerManager;
import at.ac.tuwien.dsg.salsa.engine.utils.ActionIDManager;
import at.ac.tuwien.dsg.salsa.engine.utils.EventPublisher;
import at.ac.tuwien.dsg.salsa.engine.utils.SalsaConfiguration;
import at.ac.tuwien.dsg.salsa.messaging.messageInterface.MessageClientFactory;
import at.ac.tuwien.dsg.salsa.messaging.messageInterface.MessageSubscribeInterface;
import at.ac.tuwien.dsg.salsa.messaging.messageInterface.SalsaMessageHandling;
import at.ac.tuwien.dsg.salsa.messaging.protocol.SalsaMessage;
import static at.ac.tuwien.dsg.salsa.messaging.protocol.SalsaMessage.MESSAGE_TYPE.salsa_pioneerActivated;
import at.ac.tuwien.dsg.salsa.messaging.protocol.SalsaMessageTopic;
import at.ac.tuwien.dsg.salsa.model.enums.ConfigurationState;
import at.ac.tuwien.dsg.salsa.model.salsa.info.INFOMessage;
import at.ac.tuwien.dsg.salsa.model.salsa.info.PioneerInfo;
import at.ac.tuwien.dsg.salsa.model.salsa.info.SalsaConfigureResult;
import static at.ac.tuwien.dsg.salsa.model.salsa.info.SalsaConfigureResult.CONFIGURATION_STATE.PROCESSING;
import static at.ac.tuwien.dsg.salsa.model.salsa.info.SalsaConfigureResult.CONFIGURATION_STATE.SUCCESSFUL;
import at.ac.tuwien.dsg.salsa.model.salsa.info.SalsaConfigureTask;
import at.ac.tuwien.dsg.salsa.model.salsa.info.SalsaException;
import at.ac.tuwien.dsg.salsa.model.salsa.info.SalsaMsgUpdateMetadata;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.google.common.base.Joiner;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import javax.annotation.PostConstruct;
import org.apache.commons.io.FileUtils;
import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import org.apache.log4j.spi.LoggingEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Duc-Hung Le
 */
public class SalsaEngineListener {

    Logger logger = LoggerFactory.getLogger("salsa");
    MessageClientFactory factory = MessageClientFactory.getFactory(SalsaConfiguration.getBroker(), SalsaConfiguration.getBrokerType());

    ConfigurationService restConf = (ConfigurationService) JAXRSClientFactory.create("http://localhost:8080/salsa-engine/rest", ConfigurationService.class, Collections.singletonList(new JacksonJaxbJsonProvider()));

    @PostConstruct
    public void init() {
        logger.debug("Subscribing to the control topic : " + SalsaMessageTopic.PIONEER_REGISTER_AND_HEARBEAT + " and " + SalsaMessageTopic.PIONEER_UPDATE_CONFIGURATION_STATE);

        MessageSubscribeInterface subscriber1 = factory.getMessageSubscriber(new SalsaMessageHandling() {

            @Override
            public void handleMessage(SalsaMessage msg) {

                logger.debug("Get a message from pioneer to update somethings....: " + msg.toJson());
                if (msg.getMsgType().equals(SalsaMessage.MESSAGE_TYPE.salsa_updateNodeMetadata)) {
                    logger.debug(" --> The message is for updating metadata, msgtype: " + msg.getMsgType());
                    SalsaMsgUpdateMetadata metadataInfo = SalsaMsgUpdateMetadata.fromJson(msg.getPayload());
                    try {
                        logger.debug(" --> Is about calling salsa API to update");
                        logger.debug(" --> msg.getPayload: {}", msg.getPayload());
                        logger.debug(" --> metadataInfo.getService: {}", metadataInfo.getService());
                        logger.debug(" --> metadataInfo.getTopology: {}", metadataInfo.getTopology());
                        logger.debug(" --> metadataInfo.getUnit: {}", metadataInfo.getUnit());

                        restConf.updateUnitMeta(msg.getPayload(), metadataInfo.getService(), metadataInfo.getUnit());

                        logger.debug(" --> Seem to update done");

                    } catch (EngineConnectionException ex) {
                        logger.error("Cannot connect to SALSA service in localhost: " + SalsaConfiguration.getSalsaCenterEndpointLocalhost() + ". This is a fatal error !");
                    } catch (SalsaException ex) {
                        logger.error("Cannot update the metadata");
                    }
                    return;
                }
                logger.debug(" --> The message is for updating configuration state, msgtype: " + msg.getMsgType());

                // OTHER CASE ONLY: SalsaMessage.MESSAGE_TYPE.salsa_configurationStateUpdate
                SalsaConfigureResult confResult = SalsaConfigureResult.fromJson(msg.getPayload());
                SalsaConfigureTask task = ActionIDManager.getInstanceFullID(confResult.getActionID());

                logger.debug("Current actions is pending: " + ActionIDManager.describe());
                if (task == null) {
                    logger.error("Action is not found. ID: " + confResult.getActionID());
                    return;
                }
                ConfigurationState salsaState = ConfigurationState.UNDEPLOYED;

                switch (confResult.getState()) {
                    case ERROR: {
                        logger.error("Artifact configuration failed. Instance: {},{},{}. Details: {}", task.getService(), task.getUnit(), task.getInstance(), confResult.getDomainID());
                        salsaState = ConfigurationState.ERROR;
                        break;
                    }
                    case SUCCESSFUL: {
                        if (task.getActionName().equals("deploy")) {
                            logger.debug("The deploy action for unit {}/{}/{}/{} is successful", task.getUser(), task.getService(), task.getUnit(), task.getInstance());
                            try {
                                updateInstanceCapability(task, confResult);
                            } catch (SalsaException ex) {
                                ex.printStackTrace();
                            }

                            salsaState = ConfigurationState.DEPLOYED;
                            EventPublisher.publishInstanceEvent(Joiner.on("/").join(task.getService(), task.getUnit(), task.getInstance()), INFOMessage.ACTION_TYPE.DEPLOY, INFOMessage.ACTION_STATUS.DONE, "EventListener", "The instance deployment is finished");
                        } else if (task.getActionName().equals("undeploy")) {
                            logger.debug("The undeploy action for unit {}/{}/{}/{} is successful", task.getUser(), task.getService(), task.getUnit(), task.getInstance());
                            salsaState = ConfigurationState.UNDEPLOYED;
                            EventPublisher.publishInstanceEvent(Joiner.on("/").join(task.getService(), task.getUnit(), task.getInstance()), INFOMessage.ACTION_TYPE.REMOVE, INFOMessage.ACTION_STATUS.DONE, "EventListener", "The instance deployment is finished");
                        }
                        ActionIDManager.removeAction(confResult.getActionID());
                        break;
                    }
                    case PROCESSING: {
                        logger.debug("An update for state PROCESS, but do nothing. task looks like", confResult.toJson());
                        // do nothing now
                    }

                    salsaState = ConfigurationState.CONFIGURING;
                    break;
                }

                restConf.updateInstanceState(task.getService(), task.getUnit(), task.getInstance(), salsaState.toString());

            }
        }
        );

        subscriber1.subscribe(SalsaMessageTopic.PIONEER_UPDATE_CONFIGURATION_STATE);

        MessageSubscribeInterface subscriber2 = factory.getMessageSubscriber(new SalsaMessageHandling() {
            @Override
            public void handleMessage(SalsaMessage msg) {
                if (msg.getMsgType().equals(salsa_pioneerActivated)) {

                    PioneerInfo piInfo = PioneerInfo.fromJson(msg.getPayload());
                    if (piInfo.getUserName().equals(SalsaConfiguration.getUserName())) {
                        PioneerManager.addPioneer(piInfo.getUuid(), piInfo);
                        if (!piInfo.isFree()) {
                            restConf.updateInstanceState(piInfo.getService(), piInfo.getUnit(), piInfo.getInstance(), ConfigurationState.DEPLOYED.toString());
                        }
                    }

                }
            }
        }
        );

        subscriber2.subscribe(SalsaMessageTopic.PIONEER_REGISTER_AND_HEARBEAT);

        MessageSubscribeInterface subscribe3 = factory.getMessageSubscriber(new SalsaMessageHandling() {

            @Override
            public void handleMessage(SalsaMessage msg) {
                LoggingEvent event;
                try {
                    String fileName = "./logs/salsa.pioneer.log." + msg.getFromSalsa();
                    String payload = msg.getPayload();
                    FileUtils.writeStringToFile(new File(fileName), payload.trim() + "\n", true);
                } catch (IOException ex) {
                    logger.warn("Cannot create log files for pioneer " + msg.getFromSalsa(), ex);
                }
            }
        });

        subscribe3.subscribe(SalsaMessageTopic.PIONEER_LOG);

    }

    private void updateInstanceCapability(SalsaConfigureTask confRequest, SalsaConfigureResult confState) throws SalsaException {
        restConf.updateInstanceProperties(confState, confRequest.getService(), confRequest.getUnit(), confRequest.getInstance());

    }
}
