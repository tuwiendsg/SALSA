/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.salsa.messaging.MQTTAdaptor;

import at.ac.tuwien.dsg.cloud.salsa.messaging.messageInterface.MessageSubscribeInterface;
import at.ac.tuwien.dsg.cloud.salsa.messaging.model.SalsaMessage;
import at.ac.tuwien.dsg.cloud.salsa.messaging.model.SalsaMessageTopic;
import java.util.Arrays;
import org.codehaus.jackson.map.ObjectMapper;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 *
 * @author hungld
 */
public abstract class MQTTSubscribe extends MQTTConnector implements MessageSubscribeInterface {

    public MQTTSubscribe(String broker) {
        super(broker);
    }

    public MQTTSubscribe() {
    }

    @Override
    public void subscribe(String topic) {
        MqttCallback callBack = new MqttCallback() {

            @Override
            public void connectionLost(Throwable thrwbl) {
                logger.debug("Queue disconnect " + thrwbl.getMessage());
                thrwbl.printStackTrace();
            }

            @Override
            public void messageArrived(String topic, MqttMessage mm) throws Exception {                
                ObjectMapper mapper = new ObjectMapper();
                SalsaMessage em = (SalsaMessage) mapper.readValue(mm.getPayload(), SalsaMessage.class);
                if (!topic.equals(SalsaMessageTopic.PIONEER_LOG)) {
                    logger.debug("A message arrived. From: " + em.getFromSalsa() + ". MsgType: " + em.getMsgType() + "Payload: " + em.getPayload());                    
                }                
                handleMessage(em);
            }

            @Override

            public void deliveryComplete(IMqttDeliveryToken imdt) {
                logger.debug("Deliver complete. ");

            }
        };
        if (this.queueClient == null) {
            connect();
        }
        this.queueClient.setCallback(callBack);
        try {            
            this.queueClient.subscribe(topic);
            logger.info("Subscribed the topic: " + topic);
        } catch (MqttException ex) {
            logger.error("Failed to subscribed to the topic: " + topic);
            ex.printStackTrace();
        }
    }

    @Override
    public abstract void handleMessage(SalsaMessage msg);
}
