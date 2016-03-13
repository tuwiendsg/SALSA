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
package at.ac.tuwien.dsg.cloud.salsa.informationmanagement.communication.protocol;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * This class define type of messages between GLOBAL and LOCAL resource manager
 *
 * @author Duc-Hung Le
 */
public class DeliseMessage {

    MESSAGE_TYPE msgType;

    String fromSalsa;

    String topic;

    String feedbackTopic;

    String payload;

    long timeStamp;

    public DeliseMessage() {
    }

    public DeliseMessage(MESSAGE_TYPE msgType, String fromSalsa, String topic, String feedbackTopic, String payload) {
        this.fromSalsa = fromSalsa;
        this.msgType = msgType;
        this.topic = topic;
        this.feedbackTopic = feedbackTopic;
        this.payload = payload;
        this.timeStamp = System.currentTimeMillis();
    }

    public enum MESSAGE_TYPE {

        // broadcast: client/global->local, collect list of local manager
// broadcast: client/global->local, collect list of local manager, then the reply message
        SYN_REQUEST,
        SYN_REPLY,
        // unicast:local->global, local manager register it self 
        local_register,
        // unicast: Client->local, query information from local regarding to SD Gateway or NVF
        RPC_QUERY_SDGATEWAY_LOCAL,
        RPC_QUERY_NFV_LOCAL,
        // unicast: Client->global, query information from global (which include relationship)
        RPC_QUERY_INFORMATION_GLOBAL,
        // unicast: Client->local, send a control command to local
        RPC_CONTROL_LOCAL,
        // unicast: Client->global, send a control command to global
        RPC_CONTROL_GLOBAL,
        // unicast/broadcast: Client--> local: subscribe the changes in the gateway
        SUBSCRIBE_SDGATEWAY_LOCAL,
        SUBSCRIBE_SDGATEWAY_LOCAL_SET_PARAM,
        
        // unicast: local/global --> client: send back the response
        UPDATE_INFORMATION

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

    public static DeliseMessage fromJson(byte[] bytes) {
        return fromJson(new String(bytes, StandardCharsets.UTF_8));
    }

    public static DeliseMessage fromJson(String s) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(s, DeliseMessage.class);
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

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public void setFeedbackTopic(String feedbackTopic) {
        this.feedbackTopic = feedbackTopic;
    }

    @Override
    public String toString() {
        return "SalsaMessage{" + "MsgType=" + msgType + ", payload=" + payload + '}';
    }

}
