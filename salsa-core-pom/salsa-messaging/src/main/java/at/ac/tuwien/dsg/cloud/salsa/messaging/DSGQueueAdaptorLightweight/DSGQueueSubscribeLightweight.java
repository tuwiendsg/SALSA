/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.salsa.messaging.DSGQueueAdaptorLightweight;

import at.ac.tuwien.dsg.cloud.salsa.messaging.MQTTAdaptor.MQTTConnector;
import at.ac.tuwien.dsg.cloud.salsa.messaging.messageInterface.MessageSubscribeInterface;
import at.ac.tuwien.dsg.cloud.salsa.messaging.messageInterface.SalsaMessageHandling;
import at.ac.tuwien.dsg.cloud.salsa.messaging.protocol.SalsaMessage;
import at.ac.tuwien.dsg.comot.messaging.api.Consumer;
import at.ac.tuwien.dsg.comot.messaging.api.Message;
import at.ac.tuwien.dsg.comot.messaging.lightweight.ComotMessagingFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 * @author Duc-Hung LE
 */
public class DSGQueueSubscribeLightweight extends DSGQueueConnector implements MessageSubscribeInterface {

    Logger logger = LoggerFactory.getLogger(MQTTConnector.class);
    SalsaMessageHandling handler;

    public DSGQueueSubscribeLightweight(String broker, SalsaMessageHandling handling) {
        super(broker);
        this.handler = handling;
    }

    @Override
    public void subscribe(String topic) {
        Consumer consumer = ComotMessagingFactory.getRabbitMqConsumer().withLightweigthSalsaDiscovery(config);
        consumer.addMessageReceivedListener((Message message) -> {
            //handler.handleMessage(SalsaMessage.fromJson(message.getMessage()));
            new Thread(new AsynHandleMessages(SalsaMessage.fromJson(message.getMessage()))).start();
        });
    }

    @Override
    public void disconnect() {
        System.out.println("Tend to disconnect DSG queue, but not neccessary !");
    }

    private class AsynHandleMessages implements Runnable {

        SalsaMessage em;

        AsynHandleMessages(SalsaMessage em) {
            logger.debug("Spawning a new thead to handle message {}", em.getPayload());
            this.em = em;
        }

        @Override
        public void run() {
            logger.debug("Pioneer is handingling message in an asyn thread: " + em.getPayload());
            handler.handleMessage(em);
        }

    }
}
