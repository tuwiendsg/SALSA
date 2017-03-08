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
package at.ac.tuwien.dsg.salsa.engine.utils;

import at.ac.tuwien.dsg.salsa.messaging.messageInterface.MessageClientFactory;
import at.ac.tuwien.dsg.salsa.messaging.messageInterface.MessagePublishInterface;
import at.ac.tuwien.dsg.salsa.messaging.protocol.SalsaMessage;
import at.ac.tuwien.dsg.salsa.messaging.protocol.SalsaMessageTopic;
import at.ac.tuwien.dsg.salsa.model.salsa.info.INFOMessage;
import at.ac.tuwien.dsg.salsa.model.salsa.info.INFOMessage.ACTION_STATUS;
import at.ac.tuwien.dsg.salsa.model.salsa.info.INFOMessage.ACTION_TYPE;
import at.ac.tuwien.dsg.salsa.model.salsa.info.INFOMessage.SERVICE_LEVEL;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Duc-Hung LE
 */
public class EventPublisher {

    static Logger logger = LoggerFactory.getLogger("salsa");
    static final MessageClientFactory FACTORY = MessageClientFactory.getFactory(SalsaConfiguration.getBrokerExport(), SalsaConfiguration.getBrokerTypeExport());
    static final MessagePublishInterface PUBLISH = FACTORY.getMessagePublisher();

    public static void publishINFO(INFOMessage msg) {
        SalsaMessage ssmsg = new SalsaMessage(SalsaMessage.MESSAGE_TYPE.salsa_log, SalsaConfiguration.getSalsaCenterEndpoint(), SalsaMessageTopic.SALSA_PUBLISH_EVENT, "", msg.toJson());
        logger.info(msg.toJson());
        try {
            FileUtils.writeStringToFile(new File(SalsaConfiguration.getEventLogFile()), msg.toJson() + "\n", true);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        PUBLISH.pushMessage(ssmsg);
    }

    public static void publishInstanceEvent(String id, ACTION_TYPE type, ACTION_STATUS status, String producer, String extra) {
        INFOMessage msg = new INFOMessage(type, status, id, SERVICE_LEVEL.INSTANCE, getTimeStamp(), producer, extra);
        publishINFO(msg);
    }

    public static void publishUnitEvent(String id, ACTION_TYPE type, ACTION_STATUS status, String producer, String extra) {
        INFOMessage msg = new INFOMessage(type, status, id, SERVICE_LEVEL.UNIT, getTimeStamp(), producer, extra);
        publishINFO(msg);
    }

    public static void publishCloudServiceEvent(String id, ACTION_TYPE type, ACTION_STATUS status, String producer, String extra) {
        INFOMessage msg = new INFOMessage(type, status, id, SERVICE_LEVEL.CLOUDSERVICE, getTimeStamp(), producer, extra);
        publishINFO(msg);
    }

    public static void publishSALSAEvent(String id, String producer, String extra) {
        INFOMessage msg = new INFOMessage(ACTION_TYPE.SALSA_ACTION, ACTION_STATUS.STARTED, id, SERVICE_LEVEL.OTHER, getTimeStamp(), producer, extra);
        publishINFO(msg);
    }

    private static long getTimeStamp() {
        return (new Date()).getTime();
    }

}
