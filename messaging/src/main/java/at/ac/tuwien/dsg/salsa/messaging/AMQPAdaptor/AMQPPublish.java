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
package at.ac.tuwien.dsg.salsa.messaging.AMQPAdaptor;

import at.ac.tuwien.dsg.salsa.messaging.messageInterface.MessagePublishInterface;
import at.ac.tuwien.dsg.salsa.messaging.protocol.SalsaMessage;
import java.io.IOException;

/**
 *
 * @author Duc-Hung Le
 */
public class AMQPPublish extends AMQPConnector implements MessagePublishInterface {

    public AMQPPublish() {
    }

    public AMQPPublish(String broker) {
        super(broker);
    }

    @Override
    public void pushMessage(SalsaMessage content) {
        connect();
        
        System.out.println("Publishing message: " + content.getMsgType() + ", " + content.getTopic());
        if (content.getPayload() != null && content.getPayload().length() < 2048) {
            logger.debug("Content: " + content);
        }
        try {            
            String EXCHANGE_NAME = content.getTopic();
            logger.debug("EXCHANGE_NAME: " + EXCHANGE_NAME);
            amqpChannel.exchangeDeclare(EXCHANGE_NAME, "fanout");
            amqpChannel.basicPublish(EXCHANGE_NAME, "", null, content.toJson().getBytes());
        } catch (IOException me) {
            System.out.println("msg " + me.getMessage());
            System.out.println("loc " + me.getLocalizedMessage());
            System.out.println("excep " + me);
            me.printStackTrace();
        }
        System.out.println("Message published");
        disconnect();

    }

    public void pushMessageAndDisconnect(SalsaMessage content) {
        pushMessage(content);
        disconnect();
    }

    public void pushCustomData(String content, String topic) {
        try {
            if (amqpChannel == null) {
                connect();
            }
            System.out.println("Publishing custom data: " + content);
            amqpChannel.exchangeDeclare(topic, "fanout");
            amqpChannel.basicPublish(topic, "", null, content.getBytes());
            System.out.println("Message published");
        } catch (IOException me) {
            System.out.println("msg " + me.getMessage());
            System.out.println("loc " + me.getLocalizedMessage());
            System.out.println("excep " + me);
            me.printStackTrace();
        }
    }
}
