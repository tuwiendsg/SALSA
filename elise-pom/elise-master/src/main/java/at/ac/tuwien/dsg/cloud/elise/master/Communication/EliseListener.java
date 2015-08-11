/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.elise.master.Communication;

import at.ac.tuwien.dsg.cloud.elise.master.QueryManagement.utils.EliseConfiguration;
import at.ac.tuwien.dsg.cloud.elise.master.RESTInterface.EliseManager;
import at.ac.tuwien.dsg.cloud.elise.master.RESTInterface.UnitInstanceDAO;
import at.ac.tuwien.dsg.cloud.elise.model.elasticunit.runtime.UnitInstance;
import at.ac.tuwien.dsg.cloud.elise.model.elasticunit.wrapper.UnitInstanceWrapper;
import at.ac.tuwien.dsg.cloud.salsa.messaging.messageInterface.MessageClientFactory;
import at.ac.tuwien.dsg.cloud.salsa.messaging.messageInterface.MessageSubscribeInterface;
import at.ac.tuwien.dsg.cloud.salsa.messaging.messageInterface.SalsaMessageHandling;
import at.ac.tuwien.dsg.cloud.salsa.messaging.model.Elise.ConductorDescription;
import at.ac.tuwien.dsg.cloud.salsa.messaging.protocol.EliseQueueTopic;
import at.ac.tuwien.dsg.cloud.salsa.messaging.protocol.SalsaMessage;
import java.io.IOException;
import java.util.Collections;
import javax.annotation.PostConstruct;
import org.apache.cxf.jaxrs.client.JAXRSClientFactory;

import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.slf4j.Logger;

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
                        ConductorDescription des;
                        try {
                            des = ConductorDescription.fromJson(message.getPayload());
                            eliseService.registerConductor(des);
                        } catch (IOException ex) {
                            logger.error("Cannot parse ConductorDescription sent from ELISE: {}", conductorID, ex);
                        }
                    }
                    case elise_queryProcessNotification: {
                        QueryManager.updateQueryStatus(message.getPayload());
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
                        UnitInstanceDAO unitInstanceDAO = (UnitInstanceDAO) JAXRSClientFactory.create(EliseConfiguration.getRESTEndpointLocal(), UnitInstanceDAO.class, Collections.singletonList(new JacksonJsonProvider()));
                        UnitInstanceWrapper wrapper = UnitInstanceWrapper.fromJson(message.getPayload());
                        for (UnitInstance unit : wrapper.getUnitInstances()) {
                            unitInstanceDAO.addUnitInstance(unit);
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
