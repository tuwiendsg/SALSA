/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
 * @author hungld
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
            this.queueClient = new MqttClient(this.broker, this.clientId, this.persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);            
            this.queueClient.connect(connOpts);            
            if (this.queueClient.isConnected()) {
                this.logger.debug("Connected to the broker: " + this.broker);
            } else {
                this.logger.error("Failed to connect to the broker: " + this.broker);
            }
            return true;
        } catch (MqttException ex) {
            this.logger.debug(ex.toString());
            ex.printStackTrace();
        }
        return false;
    }

    public void disconnect() {
        if ((this.queueClient != null) && (this.queueClient.isConnected())) {
            try {
                this.queueClient.disconnect();
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
