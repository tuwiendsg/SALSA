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
package at.ac.tuwien.dsg.cloud.salsa.messaging.protocol;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.codehaus.jackson.map.ObjectMapper;

/**
 *
 * @author Duc-Hung Le
 */
public class SalsaMessage {

    MESSAGE_TYPE msgType;

    String fromSalsa;

    String topic;

    String feedbackTopic;

    String payload;

    long timeStamp;

    public SalsaMessage() {
    }

    public SalsaMessage(MESSAGE_TYPE msgType, String fromSalsa, String topic, String feedbackTopic, String payload) {
        this.fromSalsa = fromSalsa;
        this.msgType = msgType;
        this.topic = topic;
        this.feedbackTopic = feedbackTopic;
        this.payload = payload;
        this.timeStamp = System.currentTimeMillis();
    }

    public enum MESSAGE_TYPE {

        discover, // collect SALSA pioneer, elise collector, elise        

        // for SALSA core
        salsa_deploy, // first time deployment
        salsa_reconfigure, // call a lifecycle action        
        salsa_configurationStateUpdate, // update configuration stats, report error
        salsa_messageReceived, // simple notify that a message is received
        salsa_pioneerActivated, // a pioneer register itself
        salsa_log, // for sending log

        // for ELISE service        
        elise_queryInstance,
        elise_queryProvider,
        elise_queryProcessNotification,
        elise_instanceInfoUpdate,
        elise_providerInfoUpdate,
        elise_conductorActivated,
        elise_addCollector
    }

    public String toJson() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(this);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static SalsaMessage fromJson(byte[] bytes) {
        return fromJson(new String(bytes, StandardCharsets.UTF_8));
    }

    public static SalsaMessage fromJson(String s) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(s, SalsaMessage.class);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public MESSAGE_TYPE getMsgType() {
        return msgType;
    }

    public String getFromSalsa() {
        return fromSalsa;
    }

    public String getTopic() {
        return topic;
    }

    public String getFeedbackTopic() {
        return feedbackTopic;
    }

    public String getPayload() {
        return payload;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    @Override
    public String toString() {
        return "SalsaMessage{" + "MsgType=" + msgType + ", payload=" + payload + '}';
    }

}
