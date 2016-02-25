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
package at.ac.tuwien.dsg.cloud.salsa.informationmanagement.communication.MQTTAdaptor;

import at.ac.tuwien.dsg.cloud.salsa.informationmanagement.communication.messageInterface.MessagePublishInterface;
import at.ac.tuwien.dsg.cloud.salsa.informationmanagement.communication.protocol.DeliseMessage;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 *
 * @author Duc-Hung Le
 */
public class MQTTPublish extends MQTTConnector implements MessagePublishInterface {

    public MQTTPublish() {
    }

    public MQTTPublish(String broker) {
        super(broker);
    }

    @Override
    public void pushMessage(DeliseMessage content) {
        try {

            connect();

            System.out.println("Publishing message: " + content.getMsgType() + ", " + content.getTopic());
            if (content.getPayload() != null && content.getPayload().length() < 2048) {
                logger.debug("Content: " + content);
            }
            MqttMessage message = new MqttMessage(content.toJson().getBytes());
            message.setQos(this.qos);
            queueClient.publish(content.getTopic(), message);
            queueClient.disconnect();
            queueClient.close();
            System.out.println("Message published");
        } catch (MqttException me) {
            System.out.println("reason " + me.getReasonCode());
            System.out.println("msg " + me.getMessage());
            System.out.println("loc " + me.getLocalizedMessage());
            System.out.println("cause " + me.getCause());
            System.out.println("excep " + me);
            me.printStackTrace();
        }
    }

    public void pushCustomData(String content, String topic) {
        try {
            if (queueClient == null) {
                connect();
            }
            System.out.println("Publishing custom data: " + content);
            MqttMessage message = new MqttMessage(content.getBytes());
            message.setQos(this.qos);
            queueClient.publish(topic, message);
            queueClient.close();
            System.out.println("Message published");
        } catch (MqttException me) {
            System.out.println("reason " + me.getReasonCode());
            System.out.println("msg " + me.getMessage());
            System.out.println("loc " + me.getLocalizedMessage());
            System.out.println("cause " + me.getCause());
            System.out.println("excep " + me);
            me.printStackTrace();
        }
    }

    @Override
    public DeliseMessage callFunction(DeliseMessage content) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
