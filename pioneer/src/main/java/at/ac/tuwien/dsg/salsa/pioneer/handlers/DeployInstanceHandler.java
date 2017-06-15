/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.salsa.pioneer.handlers;

import at.ac.tuwien.dsg.salsa.messaging.messageInterface.MessageClientFactory;
import at.ac.tuwien.dsg.salsa.messaging.messageInterface.MessagePublishInterface;
import at.ac.tuwien.dsg.salsa.messaging.messageInterface.SalsaMessageHandling;
import at.ac.tuwien.dsg.salsa.messaging.protocol.SalsaMessage;
import at.ac.tuwien.dsg.salsa.messaging.protocol.SalsaMessageTopic;
import at.ac.tuwien.dsg.salsa.model.enums.ConfigurationState;
import at.ac.tuwien.dsg.salsa.model.salsa.info.SalsaConfigureResult;
import at.ac.tuwien.dsg.salsa.model.salsa.info.SalsaConfigureTask;
import at.ac.tuwien.dsg.salsa.pioneer.Main;
import at.ac.tuwien.dsg.salsa.pioneer.utils.PioneerConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author hungld
 */
public class DeployInstanceHandler implements SalsaMessageHandling {

    static Logger logger = LoggerFactory.getLogger("salsa");
    static final MessageClientFactory factory = new MessageClientFactory(PioneerConfiguration.getBroker(), PioneerConfiguration.getBrokerType());

    @Override
    public void handleMessage(SalsaMessage msg) {
        logger.debug("Processing deployment request ...");
        SalsaConfigureTask confInfo = SalsaConfigureTask.fromJson(msg.getPayload());
        logger.debug("Configuration info: actionID : {}, parameters: {}", confInfo.getActionID(), confInfo.getParameters().toString());
        if (confInfo.getPioneerID() == null) {
            logger.error("The request does not specify pioneer ID, not I am not sure if it is for me !");
            return;
        }
        if (!confInfo.getPioneerID().equals(PioneerConfiguration.getPioneerID())) {
            logger.debug("Received a message but not for me, it is for pioneer: " + confInfo.getPioneerID());
            return;
        }
        logger.debug("Adding a configuration task to the queue. Current queue size: " + Main.deploymentQueue.size());
        Main.deploymentQueue.add(confInfo);
        logger.debug("Received message for DEPLOYMENT: " + msg.toJson() + ", put in queue. QueueSize: " + Main.deploymentQueue.size());

        // todo: download artifact, setup
        SalsaConfigureResult confState = new SalsaConfigureResult("deploy", ConfigurationState.PROCESSING, 0, "Pioneer received the deployment request and is processing.");

        SalsaMessage reply = new SalsaMessage(SalsaMessage.MESSAGE_TYPE.salsa_configurationStateUpdate, PioneerConfiguration.getPioneerID(), SalsaMessageTopic.PIONEER_UPDATE_CONFIGURATION_STATE, "", confState.toJson());
        MessagePublishInterface publish = factory.getMessagePublisher();
        publish.pushMessage(reply);

    }

}
