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

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

/**
 *
 * @author Duc-Hung Le
 */
public class MQTTConnector {

    Logger logger;
    String broker = "tcp://iot.eclipse.org:1883";
    String clientId = UUID.randomUUID().toString();
    MemoryPersistence persistence = new MemoryPersistence();
    int qos = 2;

    public MQTTConnector() {
        this.logger = LoggerFactory.getLogger(MQTTConnector.class);

    }

    public MQTTConnector(String broker) {
        this.logger = LoggerFactory.getLogger(MQTTConnector.class);
        this.broker = broker;
    }

    MqttClient queueClient = null;

        
    public boolean connect() {
        try {
            queueClient = new MqttClient(this.broker, this.clientId, this.persistence);            
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(false);                     
            queueClient.connect(connOpts);            
            if (queueClient.isConnected()) {
                logger.debug("Connected to the MQTT broker: " + this.broker);
                return true;
            } else {
                this.logger.error("Failed to connect to the broker: " + this.broker);
                return false;
            }            
        } catch (MqttException ex) {
            this.logger.debug(ex.toString());
            ex.printStackTrace();
        }
        return false;
    }

    public void disconnect() {
        if ((queueClient != null) && (queueClient.isConnected())) {
            try {
                queueClient.disconnect();
            } catch (MqttException ex) {
                this.logger.debug(ex.toString());
                ex.printStackTrace();
            }
        }
    }

    public String genClientID() {
        return UUID.randomUUID().toString().substring(0, 10);
    }

    private static String byteArrayToString(byte[] bytes) {
        return new String(bytes, StandardCharsets.UTF_8);
    }

}
