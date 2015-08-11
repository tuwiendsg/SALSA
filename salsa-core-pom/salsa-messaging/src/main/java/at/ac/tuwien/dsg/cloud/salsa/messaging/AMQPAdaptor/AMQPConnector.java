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

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;
import java.util.concurrent.TimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Duc-Hung Le
 */
public class AMQPConnector {

    Logger logger;
    String broker;
    String clientId = UUID.randomUUID().toString();

    public AMQPConnector() {
        this.logger = LoggerFactory.getLogger(AMQPConnector.class);

    }

    public AMQPConnector(String broker) {
        this.logger = LoggerFactory.getLogger(AMQPConnector.class);
        this.broker = broker;
    }

    protected Channel amqpChannel = null;
    protected Connection connecion = null;

    public boolean connect() {
        try {
            logger.debug("Trying to connect to AMQP broker: {}", broker);
            ConnectionFactory factory = new ConnectionFactory();
            factory.setUri(broker);
            connecion = factory.newConnection();
            amqpChannel = connecion.createChannel();
            if (connecion != null && amqpChannel != null) {
                logger.debug("AMQP connected. Connection: ---, Channel: {}", amqpChannel.getChannelNumber());
            } else {
                logger.error("Cannot create a connection to AMQP broker: {}", broker);
            }
        } catch (URISyntaxException | NoSuchAlgorithmException | KeyManagementException | IOException | TimeoutException e) {
            logger.error(e.toString(), e);
            e.printStackTrace();
        }
        return false;
    }

    public void disconnect() {
        if (amqpChannel != null && connecion != null) {
            try {
                amqpChannel.close();
            } catch (IOException | TimeoutException ex) {
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
