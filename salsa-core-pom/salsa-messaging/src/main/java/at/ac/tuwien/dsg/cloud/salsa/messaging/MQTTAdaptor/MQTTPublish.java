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

import at.ac.tuwien.dsg.cloud.salsa.messaging.messageInterface.MessagePublishInterface;
import at.ac.tuwien.dsg.cloud.salsa.messaging.model.SalsaMessage;
import java.util.Arrays;
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
    public void pushMessage(SalsaMessage content) {
        try {
            if (this.queueClient == null) {
                connect();
            }
            System.out.println("Publishing message: " + content.getMsgType() + ", " + content.getTopic());
            if (content.getPayload().length() < 2048) {
                logger.debug("Content: " + content.getPayload());
            }
            MqttMessage message = new MqttMessage(content.toJson().getBytes());            
            message.setQos(this.qos);
            this.queueClient.publish(content.getTopic(), message);
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

    public void pushMessageAndDisconnect(SalsaMessage content) {
        pushMessage(content);
        disconnect();
    }

    public void pushCustomData(String content, String topic) {
        try {
            if (this.queueClient == null) {
                connect();
            }
            System.out.println("Publishing custom data: " + content);
            MqttMessage message = new MqttMessage(content.getBytes());
            message.setQos(this.qos);
            this.queueClient.publish(topic, message);
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
}
