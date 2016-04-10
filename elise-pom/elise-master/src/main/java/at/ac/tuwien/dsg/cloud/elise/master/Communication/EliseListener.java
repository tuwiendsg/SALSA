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
package at.ac.tuwien.dsg.cloud.elise.master.Communication;

import at.ac.tuwien.dsg.cloud.elise.master.QueryManagement.utils.EliseConfiguration;
import at.ac.tuwien.dsg.cloud.elise.master.RESTService.EliseManager;
import at.ac.tuwien.dsg.cloud.elise.model.runtime.UnitInstance;
import at.ac.tuwien.dsg.cloud.elise.model.wrapper.UnitInstanceWrapper;
import at.ac.tuwien.dsg.cloud.salsa.messaging.messageInterface.MessageClientFactory;
import at.ac.tuwien.dsg.cloud.salsa.messaging.messageInterface.MessageSubscribeInterface;
import at.ac.tuwien.dsg.cloud.salsa.messaging.messageInterface.SalsaMessageHandling;
import at.ac.tuwien.dsg.cloud.elise.collectorinterfaces.models.ConductorDescription;
import at.ac.tuwien.dsg.cloud.salsa.messaging.protocol.EliseQueueTopic;
import at.ac.tuwien.dsg.cloud.salsa.messaging.protocol.SalsaMessage;
import java.io.IOException;
import java.util.Collections;
import javax.annotation.PostConstruct;
import org.apache.cxf.jaxrs.client.JAXRSClientFactory;

import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.slf4j.Logger;
import at.ac.tuwien.dsg.cloud.elise.master.RESTService.EliseRepository;

/**
 *
 * @author Duc-Hung LE
 */
public class EliseListener {

    static Logger logger = EliseConfiguration.logger;
    MessageClientFactory factory = MessageClientFactory.getFactory(EliseConfiguration.getBroker(), EliseConfiguration.getBrokerType());

    @PostConstruct
    public void init() {
        // TODO: get query result

        // listen for the register
        MessageSubscribeInterface subscriberConductorNotification = factory.getMessageSubscriber(new SalsaMessageHandling() {

            @Override
            public void handleMessage(SalsaMessage message) {
                switch (message.getMsgType()) {
                    case elise_conductorActivated: {
                        String conductorID = message.getFromSalsa();
                        logger.debug("A conductor wants to register with ID: " + conductorID);
                        EliseManager eliseService = ((EliseManager) JAXRSClientFactory.create(EliseConfiguration.getRESTEndpointLocal(), EliseManager.class, Collections.singletonList(new JacksonJsonProvider())));
                        if (eliseService == null) {
                            logger.error("Cannot call EliseManager service !!!! eliseService == null when connecting to: " + EliseConfiguration.getRESTEndpointLocal());
                            return;
                        } else {
                            logger.debug("Created a client to elise service !!!");
                        }

                        ConductorDescription des = ConductorDescription.fromJson(message.getPayload());
                        if (des != null) {
                            eliseService.registerConductor(des);
                        } else {
                            logger.error("Cannot parse ConductorDescription sent from ELISE: {}", conductorID);
                        }
                        break;
                    }
                    case elise_queryProcessNotification: {
                        QueryManager.updateQueryStatus(message.getPayload());
                        break;
                    }
                    default: {
                        logger.error("Do not know the notification message type: " + message.getMsgType());
                    }
                }
            }
        });

        subscriberConductorNotification.subscribe(EliseQueueTopic.NOTIFICATION_TOPIC);

        MessageSubscribeInterface subscriberUpdateInstance = factory.getMessageSubscriber(new SalsaMessageHandling() {

            @Override
            public void handleMessage(SalsaMessage message) {
                logger.debug("Received a feedback message");
                switch (message.getMsgType()) {
                    case elise_instanceInfoUpdate: {
                        logger.debug("Received a message to update unit instances information");
                        EliseRepository unitInstanceDAO = (EliseRepository) JAXRSClientFactory.create(EliseConfiguration.getRESTEndpointLocal(), EliseRepository.class, Collections.singletonList(new JacksonJsonProvider()));
                        UnitInstanceWrapper wrapper = UnitInstanceWrapper.fromJson(message.getPayload());
                        for (UnitInstance unit : wrapper.getUnitInstances()) {
                            unitInstanceDAO.saveUnitInstance(unit);
                        }
                    }
                    default: {
                        logger.debug("Get message from update information topic, but unknow message type: " + message.getMsgType());
                    }
                }
            }
        });

        subscriberUpdateInstance.subscribe(EliseQueueTopic.FEEDBACK_TOPIC);
    }

}
