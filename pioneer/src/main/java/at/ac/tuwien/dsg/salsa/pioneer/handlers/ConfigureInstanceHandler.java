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
import at.ac.tuwien.dsg.salsa.model.salsa.confparameters.ShellScriptParameters;
import at.ac.tuwien.dsg.salsa.model.salsa.info.SalsaConfigureResult;
import at.ac.tuwien.dsg.salsa.model.salsa.info.SalsaConfigureTask;
import static at.ac.tuwien.dsg.salsa.pioneer.Main.executeLifecycleAction;
import at.ac.tuwien.dsg.salsa.pioneer.utils.PioneerConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author hungld
 */
public class ConfigureInstanceHandler implements SalsaMessageHandling {

    static Logger logger = LoggerFactory.getLogger("salsa");
    static final MessageClientFactory factory = new MessageClientFactory(PioneerConfiguration.getBroker(), PioneerConfiguration.getBrokerType());

    @Override
    public void handleMessage(SalsaMessage msg) {
        logger.debug("Processing reconfiguration request ...");
        SalsaConfigureTask cmd = SalsaConfigureTask.fromJson(msg.getPayload());
        logger.debug("Received a reconfiguration command for: " + cmd.getUser() + "/" + cmd.getService() + "/" + cmd.getUnit() + "/" + cmd.getInstance() + ". ActionName: " + cmd.getActionName() + ", ActionID: " + cmd.getActionID());
        // send back message that the pioneer received the command
        SalsaConfigureResult confState = new SalsaConfigureResult(cmd.getActionID(), ConfigurationState.PROCESSING, 0, "Pioneer received the request and is processing. Action ID: " + cmd.getActionID());
        SalsaMessage notifyMsg = new SalsaMessage(SalsaMessage.MESSAGE_TYPE.salsa_messageReceived, PioneerConfiguration.getPioneerID(), SalsaMessageTopic.PIONEER_UPDATE_CONFIGURATION_STATE, "", confState.toJson());
        MessagePublishInterface publish = factory.getMessagePublisher();
        publish.pushMessage(notifyMsg);
        // now reconfigure: only support script now                        
        int returnCode = executeLifecycleAction(cmd);
        // And update the configuration result
        SalsaConfigureResult confResult;
        if (returnCode == 0) {
            confResult = new SalsaConfigureResult(cmd.getActionID(), ConfigurationState.SUCCESSFUL, 0, "Action is successful: " + cmd.getParameters(ShellScriptParameters.runByMe));
        } else {
            confResult = new SalsaConfigureResult(cmd.getActionID(), ConfigurationState.SUCCESSFUL, 1, "Action is failed: " + cmd.getParameters(ShellScriptParameters.runByMe) + ". Return code: " + returnCode);
        }
        MessagePublishInterface publish_conf = factory.getMessagePublisher();
        SalsaMessage reply = new SalsaMessage(SalsaMessage.MESSAGE_TYPE.salsa_configurationStateUpdate, PioneerConfiguration.getPioneerID(), SalsaMessageTopic.PIONEER_UPDATE_CONFIGURATION_STATE, null, confResult.toJson());
        publish_conf.pushMessage(reply);
        logger.debug("Result is published !");
    }

}
