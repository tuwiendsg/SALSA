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
package at.ac.tuwien.dsg.cloud.salsa.engine.services;

import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.CloudService;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.ServiceUnit;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.enums.SalsaEntityState;
import at.ac.tuwien.dsg.cloud.salsa.common.processing.SalsaCenterConnector;
import at.ac.tuwien.dsg.cloud.salsa.engine.exception.EngineConnectionException;
import at.ac.tuwien.dsg.cloud.salsa.engine.exception.SalsaException;
import at.ac.tuwien.dsg.cloud.salsa.engine.utils.ActionIDManager;
import at.ac.tuwien.dsg.cloud.salsa.engine.utils.EngineLogger;
import at.ac.tuwien.dsg.cloud.salsa.engine.utils.PioneerManager;
import at.ac.tuwien.dsg.cloud.salsa.engine.utils.SalsaConfiguration;
import at.ac.tuwien.dsg.cloud.salsa.messaging.MQTTAdaptor.MQTTSubscribe;
import at.ac.tuwien.dsg.cloud.salsa.messaging.messageInterface.MessageSubscribeInterface;
import at.ac.tuwien.dsg.cloud.salsa.messaging.model.PioneerInfo;
import at.ac.tuwien.dsg.cloud.salsa.messaging.model.SalsaMessage;
import at.ac.tuwien.dsg.cloud.salsa.messaging.model.SalsaMessageTopic;
import at.ac.tuwien.dsg.cloud.salsa.messaging.model.commands.SalsaMsgConfigureArtifact;
import at.ac.tuwien.dsg.cloud.salsa.messaging.model.commands.SalsaMsgConfigureState;
import at.ac.tuwien.dsg.cloud.salsa.tosca.extension.SalsaCapaReqString;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Layout;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;
import org.apache.log4j.pattern.LogEvent;
import org.apache.log4j.spi.LoggingEvent;
import org.codehaus.jackson.map.ObjectMapper;

/**
 *
 * @author hungld
 */
public class SalsaEngineListener {

    @PostConstruct
    public void init() {
        EngineLogger.logger.debug("Subscribing to the control topic : " + SalsaMessageTopic.PIONEER_SYNC + " and " + SalsaMessageTopic.PIONEER_UPDATE_STATE);
        MessageSubscribeInterface subscriber1 = new MQTTSubscribe(SalsaConfiguration.getBroker()) {

            @Override
            public void handleMessage(SalsaMessage msg) {
                SalsaMsgConfigureState state = SalsaMsgConfigureState.fromJson(msg.getPayload());
                SalsaCenterConnector centerCon = null;
                try {
                    centerCon = new SalsaCenterConnector(SalsaConfiguration.getSalsaCenterEndpointLocalhost(), "/tmp", EngineLogger.logger);
                } catch (EngineConnectionException ex) {
                    EngineLogger.logger.error("Cannot connect to SALSA service in localhost: " + SalsaConfiguration.getSalsaCenterEndpointLocalhost() + ". This is a fatal error !");
                }
                SalsaMsgConfigureArtifact fullID = ActionIDManager.getInstanceFullID(state.getActionID());

                EngineLogger.logger.debug("Current actions is pending: ");
                EngineLogger.logger.debug(ActionIDManager.describe());
                if (fullID == null) {
                    EngineLogger.logger.error("Action is not found. ID: " + state.getActionID());
                    return;
                }

                SalsaEntityState salsaState = SalsaEntityState.UNDEPLOYED;
                switch (state.getState()) {
                    case ERROR: {
                        EngineLogger.logger.error("Artifact configuration failed. Instance: {},{},{}. Details: {}",fullID.getService(),fullID.getUnit(),fullID.getInstance(), state.getInfo());
                        salsaState = SalsaEntityState.ERROR;
                        break;
                    }
                    case SUCCESSFUL: {
                        if (fullID.getActionName().equals("deploy")) {
                            try {
                                EngineLogger.logger.debug("The deploy action for unit {}/{}/{}/{} is successful", fullID.getUser(), fullID.getService(), fullID.getUnit(), fullID.getInstance());
                                updateInstanceCapability(fullID, state);
                                ActionIDManager.removeAction(state.getActionID());
                            } catch (SalsaException ex) {
                                EngineLogger.logger.error("Deployment action failed. ActionID: {}",fullID.getActionID());
                                ex.printStackTrace();
                            }
                        }
                        salsaState = SalsaEntityState.DEPLOYED;
                        break;
                    }
                    case PROCESSING: {
                        salsaState = SalsaEntityState.CONFIGURING;
                        break;
                    }
                    default: {
                        EngineLogger.logger.debug("The state cannot be understood. no state is updated");
                        break;
                    }
                }
                if (centerCon != null) {
                    try {
                        centerCon.updateNodeState(fullID.getService(), fullID.getTopology(), fullID.getUnit(), fullID.getInstance(), salsaState, state.getInfo());
                    } catch (SalsaException ex) {
                        EngineLogger.logger.error("An error when trying update node state", ex);
                    }
                }
            }
        };
        subscriber1.subscribe(SalsaMessageTopic.PIONEER_UPDATE_STATE);

        MessageSubscribeInterface subscriber2 = new MQTTSubscribe(SalsaConfiguration.getBroker()) {
            @Override
            public void handleMessage(SalsaMessage msg) {
                PioneerInfo piInfo = PioneerInfo.fromJson(msg.getPayload());
                if (piInfo.getUserName().equals(SalsaConfiguration.getUserName())) {
                    PioneerManager.addPioneer(piInfo.getId(), piInfo);
                    SalsaCenterConnector centerCon;
                    try {
                        centerCon = new SalsaCenterConnector(SalsaConfiguration.getSalsaCenterEndpointLocalhost(), "/tmp", EngineLogger.logger);
                        centerCon.updateNodeState(piInfo.getService(), piInfo.getTopology(), piInfo.getUnit(), piInfo.getInstance(), SalsaEntityState.DEPLOYED, "Pioneer is deployed");
                    } catch (SalsaException ex) {                        
                        EngineLogger.logger.error("Cannot connect to SALSA service in localhost: " + SalsaConfiguration.getSalsaCenterEndpointLocalhost() + ". This is a fatal error !", ex);
                    } 
                }
            }
        };
        subscriber2.subscribe(SalsaMessageTopic.PIONEER_SYNC);

        MessageSubscribeInterface subscribe3 = new MQTTSubscribe(SalsaConfiguration.getBroker()) {
            ObjectMapper mapper = new ObjectMapper();

            @Override
            public void handleMessage(SalsaMessage msg) {
                LoggingEvent event;
                try {
                    String fileName = "./logs/salsa.pioneer.log." + msg.getFromSalsa();
                    String payload = msg.getPayload();
                    FileUtils.writeStringToFile(new File(fileName), payload.trim() + "\n", true);
                } catch (IOException ex) {
                    EngineLogger.logger.warn("Cannot create log files for pioneer " + msg.getFromSalsa(), ex);
                }
            }
        };
        subscribe3.subscribe(SalsaMessageTopic.PIONEER_LOG);

    }

    private void updateInstanceCapability(SalsaMsgConfigureArtifact confRequest, SalsaMsgConfigureState confState) throws SalsaException {
        SalsaCenterConnector centerCon = new SalsaCenterConnector(SalsaConfiguration.getSalsaCenterEndpointLocalhost(), "/tmp", EngineLogger.logger);
        CloudService service = centerCon.getUpdateCloudServiceRuntime(confRequest.getService());
        ServiceUnit unit = service.getComponentById(confRequest.getUnit());
        if (!unit.getCapabilityVars().isEmpty()) {    // it need to expose capability, so what ever, just expose
            for (String capaVar : unit.getCapabilityVars()) {
                String capaValue = null;
                if (confState.getCapabilities() != null) {
                    capaValue = confState.getCapabilities().get(capaVar);
                }
                if (capaValue == null) {
                    capaValue = "salsa:localIP";
                }
                centerCon.updateInstanceUnitCapability(confRequest.getService(), confRequest.getTopology(), confRequest.getUnit(), confRequest.getInstance(), new SalsaCapaReqString(capaVar, capaValue));
            }
        } else {
            EngineLogger.logger.debug("The unit {}/{} do not have capability variable, no capability is added.", unit.getId(), confRequest.getInstance());
        }
    }
}
