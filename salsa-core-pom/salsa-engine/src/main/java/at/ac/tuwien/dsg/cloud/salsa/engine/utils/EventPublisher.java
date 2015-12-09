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
package at.ac.tuwien.dsg.cloud.salsa.engine.utils;

import at.ac.tuwien.dsg.cloud.salsa.messaging.messageInterface.MessageClientFactory;
import at.ac.tuwien.dsg.cloud.salsa.messaging.messageInterface.MessagePublishInterface;
import at.ac.tuwien.dsg.cloud.salsa.messaging.protocol.SalsaMessage;
import at.ac.tuwien.dsg.cloud.salsa.messaging.protocol.SalsaMessageTopic;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.codehaus.jackson.map.ObjectMapper;

/**
 *
 * @author Duc-Hung LE
 */
public class EventPublisher {

    static final MessageClientFactory FACTORY = MessageClientFactory.getFactory(SalsaConfiguration.getBrokerExport(), SalsaConfiguration.getBrokerTypeExport());
    static final MessagePublishInterface PUBLISH = FACTORY.getMessagePublisher();

    public static void publishINFO(INFOMessage msg) {
        SalsaMessage ssmsg = new SalsaMessage(SalsaMessage.MESSAGE_TYPE.salsa_log, SalsaConfiguration.getSalsaCenterEndpoint(), SalsaMessageTopic.SALSA_PUBLISH_EVENT, "", msg.toJson());
        EngineLogger.logger.info(msg.toJson());
        try {
            FileUtils.writeStringToFile(new File(SalsaConfiguration.getEventLogFile()), msg.toJson(), true);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        PUBLISH.pushMessage(ssmsg);
    }

    public static void publishInstanceEvent(String id, ACTION_TYPE type, ACTION_STATUS status, String extra) {
        INFOMessage msg = new INFOMessage(type, status, id, SERVICE_LEVEL.INSTANCE, getTimeStamp(), extra);
        publishINFO(msg);
    }

    public static void publishUnitEvent(String id, ACTION_TYPE type, ACTION_STATUS status, String extra) {
        INFOMessage msg = new INFOMessage(type, status, id, SERVICE_LEVEL.UNIT, getTimeStamp(), extra);
        publishINFO(msg);
    }

    public static void publishCloudServiceEvent(String id, ACTION_TYPE type, ACTION_STATUS status, String extra) {
        INFOMessage msg = new INFOMessage(type, status, id, SERVICE_LEVEL.CLOUDSERVICE, getTimeStamp(), extra);
        publishINFO(msg);
    }

    public static void publishSALSAEvent(String id, String extra){
        INFOMessage msg = new INFOMessage(ACTION_TYPE.SALSA_ACTION, ACTION_STATUS.STARTED, id, SERVICE_LEVEL.OTHER, getTimeStamp(), extra);
        publishINFO(msg);
    }

    private static long getTimeStamp() {
        return (new Date()).getTime();
    }

    public static enum SERVICE_LEVEL {
        CLOUDSERVICE, TOPOLOGY, UNIT, INSTANCE, OTHER
    }

    public static enum ACTION_TYPE {
        DEPLOY, RECONFIGURE, REMOVE, SALSA_ACTION
    }

    public static enum ACTION_STATUS {
        STARTED, PROCESSING, DONE, ERROR
    }

    public static class INFOMessage {

        ACTION_TYPE action;
        ACTION_STATUS status;
        String id;
        SERVICE_LEVEL level;
        long timestamp;
        String producer;
        String extra;

        public INFOMessage() {
        }

        public INFOMessage(ACTION_TYPE action, ACTION_STATUS status, String id, SERVICE_LEVEL level, long timestamp, String extra) {
            this.action = action;
            this.status = status;
            this.id = id;
            this.level = level;
            this.timestamp = timestamp;
            this.producer = Thread.currentThread().getStackTrace()[1].getClassName();
            this.extra = extra;
        }

        public ACTION_STATUS getStatus() {
            return status;
        }

        public String getExtra() {
            return extra;
        }

        public ACTION_TYPE getAction() {
            return action;
        }

        public String getId() {
            return id;
        }

        public SERVICE_LEVEL getLevel() {
            return level;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public String getProducer() {
            return producer;
        }

        public String toJson() {
            ObjectMapper mapper = new ObjectMapper();
            try {
                return mapper.writeValueAsString(this);
            } catch (IOException ex) {
                return "error-message";
            }
        }

    }
}
