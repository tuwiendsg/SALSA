/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.salsa.messaging.messageInterface;

import at.ac.tuwien.dsg.salsa.messaging.AMQPAdaptor.AMQPPublish;
import at.ac.tuwien.dsg.salsa.messaging.AMQPAdaptor.AMQPSubscribe;
//import at.ac.tuwien.dsg.cloud.salsa.messaging.DSGQueueAdaptorLightweight.DSGQueuePublishLightweight;
//import at.ac.tuwien.dsg.cloud.salsa.messaging.DSGQueueAdaptorLightweight.DSGQueueSubscribeLightweight;
import at.ac.tuwien.dsg.salsa.messaging.MQTTAdaptor.MQTTPublish;
import at.ac.tuwien.dsg.salsa.messaging.MQTTAdaptor.MQTTSubscribe;

/**
 *
 * @author Duc-Hung LE
 */
public class MessageClientFactory {

    String broker;
    String brokerType;

    String exportBroker;
    String exportBrokerType;

    public MessageClientFactory(String broker, String brokerType) {
        this.broker = broker;
        this.brokerType = brokerType;
    }

    public static MessageClientFactory getFactory(String broker, String brokerType) {
        return new MessageClientFactory(broker, brokerType);
    }

    public MessagePublishInterface getMessagePublisher() {
        switch (getBrokerType()) {
            case "mqtt":
                return new MQTTPublish(getBroker());
            case "amqp":
                return new AMQPPublish(getBroker());
//            case "dsg":
//                return new DSGQueuePublishLightweight(getBroker());
            default:
                return null;
        }
    }

    public MessageSubscribeInterface getMessageSubscriber(SalsaMessageHandling handler) {
        switch (getBrokerType()) {
            case "mqtt":
                return new MQTTSubscribe(getBroker(), handler);
            case "amqp":
                return new AMQPSubscribe(getBroker(), handler);
//            case "dsg":
//                return new DSGQueueSubscribeLightweight(getBroker(), handler);
            default:
                return null;
        }
    }

    public String getBroker() {
        return broker;
    }

    public String getBrokerType() {
        return brokerType;
    }
    
}
