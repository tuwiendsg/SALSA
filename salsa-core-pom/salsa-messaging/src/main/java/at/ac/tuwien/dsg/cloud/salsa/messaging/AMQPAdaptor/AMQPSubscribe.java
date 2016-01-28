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
package at.ac.tuwien.dsg.cloud.salsa.messaging.AMQPAdaptor;

import at.ac.tuwien.dsg.cloud.salsa.messaging.messageInterface.MessageSubscribeInterface;
import at.ac.tuwien.dsg.cloud.salsa.messaging.messageInterface.SalsaMessageHandling;
import at.ac.tuwien.dsg.cloud.salsa.messaging.protocol.SalsaMessage;
import at.ac.tuwien.dsg.cloud.salsa.messaging.protocol.SalsaMessageTopic;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownSignalException;
import java.io.IOException;


/**
 *
 * @author Duc-Hung Le
 */
public class AMQPSubscribe extends AMQPConnector implements MessageSubscribeInterface {

    SalsaMessageHandling handler;

    public AMQPSubscribe(String broker, SalsaMessageHandling handling) {
        super(broker);
        this.handler = handling;
    }

    @Override
    public void subscribe(String topic) {
        if (amqpChannel == null) {
            connect();
        }
        try {
            amqpChannel.exchangeDeclare(topic, "fanout");
            String queueName = amqpChannel.queueDeclare().getQueue();
            amqpChannel.queueBind(queueName, topic, "");            

            QueueingConsumer consumer = new QueueingConsumer(amqpChannel);
            amqpChannel.basicConsume(queueName, true, consumer);
            System.out.println("AMQP Subscribed. Exchange name: " + topic + ", queue name: " + queueName);
            new Thread(new ThreadQueueSubscribe(consumer, handler)).start();
        } catch (IOException ex) {
            logger.error("Cannot subscribe to topic: {}", topic, ex);
        } catch (ShutdownSignalException | ConsumerCancelledException ex) {
            logger.error("Interrupt during the subscribing to topic: {}", topic, ex);
        }
    }

    private class ThreadQueueSubscribe implements Runnable {

        SalsaMessageHandling handler;
        QueueingConsumer consumer;
        String topic;

        ThreadQueueSubscribe(QueueingConsumer consumer, SalsaMessageHandling handler) {
            this.handler = handler;
            this.consumer = consumer;;
        }

        @Override
        public void run() {
            logger.debug("Inside the queue subscribing thread, process is continueing...");
            try {
                while (true) {
                    logger.debug("Looping and waiting for the message ");
                    QueueingConsumer.Delivery delivery = consumer.nextDelivery();
                    logger.debug("A message arrived ");
                    String mm = new String(delivery.getBody());
                    logger.debug(mm);
                    ObjectMapper mapper = new ObjectMapper();
                    SalsaMessage em = (SalsaMessage) mapper.readValue(mm, SalsaMessage.class);
                    this.topic = em.getTopic();
                    if (!topic.equals(SalsaMessageTopic.PIONEER_LOG)) {
                        logger.debug("A message arrived. From: " + em.getFromSalsa() + ". MsgType: " + em.getMsgType() + ". Payload: " + em.getPayload());
                    }
                    new Thread(new HandlingThread(handler, em)).start();
                }
            } catch (IOException ex) {
                logger.error("Cannot subscribe to topic: {}", topic, ex);
            } catch (InterruptedException | ShutdownSignalException | ConsumerCancelledException ex) {
                logger.error("Interrupt during the subscribing to topic: {}", topic, ex);
            }
        }

    }
    
    
    
    private class HandlingThread implements Runnable {
        SalsaMessageHandling handler;
        SalsaMessage em;

        HandlingThread( SalsaMessageHandling handler, SalsaMessage em) {
            this.handler = handler;
            this.em = em;
        }

        @Override
        public void run() {
            logger.debug("Inside the handling thread, process is continueing...");
            this.handler.handleMessage(em);
        }

    }
    
   

}
