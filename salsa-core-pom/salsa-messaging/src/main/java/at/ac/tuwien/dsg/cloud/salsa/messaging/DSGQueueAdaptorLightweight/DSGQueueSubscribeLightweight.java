/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.salsa.messaging.DSGQueueAdaptorLightweight;

import at.ac.tuwien.dsg.cloud.salsa.messaging.messageInterface.MessageSubscribeInterface;
import at.ac.tuwien.dsg.cloud.salsa.messaging.messageInterface.SalsaMessageHandling;
import at.ac.tuwien.dsg.cloud.salsa.messaging.protocol.SalsaMessage;
import at.ac.tuwien.dsg.comot.messaging.api.Consumer;
import at.ac.tuwien.dsg.comot.messaging.api.Message;
import at.ac.tuwien.dsg.comot.messaging.api.MessageReceivedListener;
import at.ac.tuwien.dsg.comot.messaging.lightweight.ComotMessagingFactory;

/**
 *
 * @author Duc-Hung LE
 */
public class DSGQueueSubscribeLightweight extends DSGQueueConnector implements MessageSubscribeInterface {

    SalsaMessageHandling handler;

    public DSGQueueSubscribeLightweight(String broker, SalsaMessageHandling handling) {
        super(broker);
        this.handler = handling;
    }

    @Override
    public void subscribe(String topic) {
        Consumer consumer = ComotMessagingFactory.getRabbitMqConsumer().withLightweigthDiscovery(config);
        consumer.addMessageReceivedListener((Message message) -> {
            handler.handleMessage(SalsaMessage.fromJson(message.getMessage()));
        });
    }

    @Override
    public void disconnect() {
        System.out.println("Tend to disconnect DSG queue, but not neccessary !");
    }

}
