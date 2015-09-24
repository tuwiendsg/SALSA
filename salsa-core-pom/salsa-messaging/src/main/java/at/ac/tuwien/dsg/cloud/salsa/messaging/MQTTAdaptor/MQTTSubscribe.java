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
package at.ac.tuwien.dsg.cloud.salsa.messaging.MQTTAdaptor;

import at.ac.tuwien.dsg.cloud.salsa.messaging.messageInterface.MessageSubscribeInterface;
import at.ac.tuwien.dsg.cloud.salsa.messaging.messageInterface.SalsaMessageHandling;
import at.ac.tuwien.dsg.cloud.salsa.messaging.protocol.SalsaMessage;
import at.ac.tuwien.dsg.cloud.salsa.messaging.protocol.SalsaMessageTopic;
import org.codehaus.jackson.map.ObjectMapper;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 *
 * @author Duc-Hung Le
 */
public class MQTTSubscribe extends MQTTConnector implements MessageSubscribeInterface {
    SalsaMessageHandling handler;

    public MQTTSubscribe(String broker, SalsaMessageHandling handling) {
        super(broker);
        this.handler = handling;
    }

    @Override
    public void subscribe(final String topic) {
        MqttCallback callBack = new MqttCallback() {
            
            @Override
            public void connectionLost(Throwable thrwbl) {
                logger.debug("MQTT is disconnected from topic: {}. Message: {}. Cause: {}", topic ,thrwbl.getMessage(), thrwbl.getCause().getMessage());
                thrwbl.printStackTrace();
            }

            @Override
            public void messageArrived(String topic, MqttMessage mm) throws Exception {                
                ObjectMapper mapper = new ObjectMapper();
                SalsaMessage em = (SalsaMessage) mapper.readValue(mm.getPayload(), SalsaMessage.class);                
                
                if (!topic.equals(SalsaMessageTopic.PIONEER_LOG)) {
                    logger.debug("A message arrived. From: " + em.getFromSalsa() + ". MsgType: " + em.getMsgType() + ". Payload: " + em.getPayload());                    
                }
                handler.handleMessage(em);
            }

            @Override

            public void deliveryComplete(IMqttDeliveryToken imdt) {
                logger.debug("Deliver complete to topic: " + topic);
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

    
}
