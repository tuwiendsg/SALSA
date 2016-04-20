/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.salsa.messaging.DSGQueueAdaptorLightweight;

import at.ac.tuwien.dsg.cloud.salsa.messaging.messageInterface.MessagePublishInterface;
import at.ac.tuwien.dsg.cloud.salsa.messaging.protocol.SalsaMessage;
import at.ac.tuwien.dsg.cloud.utilities.messaging.api.Message;
import at.ac.tuwien.dsg.cloud.utilities.messaging.api.Producer;
import at.ac.tuwien.dsg.cloud.utilities.messaging.lightweight.ComotMessagingFactory;
import at.ac.tuwien.dsg.cloud.utilities.messaging.lightweight.rabbitMq.RabbitMqMessage;

/**
 *
 * @author Duc-Hung LE
 */
public class DSGQueuePublishLightweight extends DSGQueueConnector implements MessagePublishInterface {

    public DSGQueuePublishLightweight(String broker) {
        super(broker);
    }

    @Override
    public void pushMessage(SalsaMessage content) {
        Producer producer = ComotMessagingFactory.getRabbitMqProducer(config);

        Message msg = new RabbitMqMessage();
        msg.setMessage(content.toJson().getBytes());
        msg.withType(content.getMsgType().toString());

        producer.sendMessage(msg);
    }

    @Override
    public void disconnect() {
        System.out.println("Tend to disconnect DSG queue, but not neccessary !");
    }

}
