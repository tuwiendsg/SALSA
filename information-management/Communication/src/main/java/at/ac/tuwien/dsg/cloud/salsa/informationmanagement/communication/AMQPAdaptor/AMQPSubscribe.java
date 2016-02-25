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
package at.ac.tuwien.dsg.cloud.salsa.informationmanagement.communication.AMQPAdaptor;

import at.ac.tuwien.dsg.cloud.salsa.informationmanagement.communication.messageInterface.MessageSubscribeInterface;
import at.ac.tuwien.dsg.cloud.salsa.informationmanagement.communication.messageInterface.SalsaMessageHandling;
import at.ac.tuwien.dsg.cloud.salsa.informationmanagement.communication.protocol.DeliseMessage;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownSignalException;
import java.io.IOException;
import java.util.Date;

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
    public void subscribe(String topic, long timeout) {
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
            new Thread(new ThreadQueueSubscribe(consumer, handler, timeout)).start();
        } catch (IOException ex) {
            logger.error("Cannot subscribe to topic: {}", topic, ex);
        } catch (ShutdownSignalException | ConsumerCancelledException ex) {
            logger.error("Interrupt during the subscribing to topic: {}", topic, ex);
        }
    }

    @Override
    public void subscribe(String topic) {
        subscribe(topic, 0);
    }

    private class ThreadQueueSubscribe implements Runnable {

        SalsaMessageHandling handler;
        QueueingConsumer consumer;
        String topic;
        long timeout;
        long startTime;

        ThreadQueueSubscribe(QueueingConsumer consumer, SalsaMessageHandling handler, long timeout) {
            this.handler = handler;
            this.consumer = consumer;;
            this.timeout = timeout; // in miliseconds
            this.startTime = (new Date()).getTime();
        }

        @Override
        public void run() {
            logger.debug("Inside the queue subscribing thread, process is continueing...");
            try {
                while (true) {
                    logger.debug("Looping and waiting for the message, timeout: " + timeout);

                    QueueingConsumer.Delivery delivery;
                    if (timeout == 0) {
                        delivery = consumer.nextDelivery();
                    } else {
                        delivery = consumer.nextDelivery(timeout);
                    }

                    if (delivery == null) {
                        logger.debug("It seems to be timeout, so delivery is null...");
                        break;
                    }

                    logger.debug("A message arrived ");

                    String mm = new String(delivery.getBody());
                    
                    logger.debug(mm);
                    ObjectMapper mapper = new ObjectMapper();
                    DeliseMessage em = (DeliseMessage) mapper.readValue(mm, DeliseMessage.class);
                    this.topic = em.getTopic();
                    logger.debug("A message arrived. From: " + em.getFromSalsa() + ". MsgType: " + em.getMsgType() + ". Payload: " + em.getPayload());
                    new Thread(new HandlingThread(handler, em)).start();
                    logger.debug("If handle message done, it must exit and show this");
                    // quit if timeout
                    if (timeout > 0) {
                        logger.debug("YES, timeout > 0");
                        long currentTime = (new Date()).getTime();
                        logger.debug("Miliseconds left before unsubscribing: " + currentTime);
                        if (currentTime - startTime > timeout) {
                            logger.debug("RETURN BECAUSE OF TIMEOUT !");
                            break;
                        }
                    }
                }
                logger.debug("The loop that wait the message is over !");
                consumer.getChannel().getConnection().close();
            } catch (IOException ex) {
                logger.error("Cannot subscribe to topic: {}", topic, ex);
            } catch (InterruptedException | ShutdownSignalException | ConsumerCancelledException ex) {
                logger.error("Interrupt during the subscribing to topic: {}", topic, ex);
            }
            logger.debug("ThreadQueueSubscribe should exit here !");
        }
    }

    private class HandlingThread implements Runnable {

        SalsaMessageHandling handler;
        DeliseMessage em;

        HandlingThread(SalsaMessageHandling handler, DeliseMessage em) {
            this.handler = handler;
            this.em = em;
        }

        @Override
        public void run() {
            logger.debug("Inside the handling thread, process is continueing...");
            this.handler.handleMessage(em);
            logger.debug("Handle message done !");
        }

    }

}
