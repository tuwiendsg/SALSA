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
package at.ac.tuwien.dsg.salsa.pioneer.queueLogger;

import at.ac.tuwien.dsg.salsa.messaging.MQTTAdaptor.MQTTPublish;
import at.ac.tuwien.dsg.salsa.messaging.messageInterface.MessageClientFactory;
import at.ac.tuwien.dsg.salsa.messaging.messageInterface.MessagePublishInterface;
import at.ac.tuwien.dsg.salsa.messaging.protocol.SalsaMessage;
import at.ac.tuwien.dsg.salsa.messaging.protocol.SalsaMessageTopic;
import at.ac.tuwien.dsg.salsa.pioneer.utils.PioneerConfiguration;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Layout;
import org.apache.log4j.MDC;
import org.apache.log4j.spi.LoggingEvent;


/**
 *
 * @author Duc-Hung Le
 */
public class QueueAppender extends AppenderSkeleton {

    private final MessagePublishInterface publish;
    private final MessageClientFactory factory = MessageClientFactory.getFactory(PioneerConfiguration.getBroker(), PioneerConfiguration.getBrokerType());

    public QueueAppender() {
        publish = factory.getMessagePublisher();
    }

    // Diagnostic Context: http://stackoverflow.com/questions/6321635/circular-dependency-when-logging-within-a-log4j-appender
    // This avoid the loop while this Appender and rootAppender use each others
    private static final String IN_APPEND_KEY = QueueAppender.class.getName() + ".inAppend";

    public QueueAppender(Layout layout) {
        publish = factory.getMessagePublisher();
        this.setLayout(layout);
    }

    @Override
    protected synchronized void append(LoggingEvent le) {
        if (le.getMDC(IN_APPEND_KEY) != null) {
            return;
        }
        MDC.put(IN_APPEND_KEY, this);
        try {
            String payload = this.layout.format(le);
            System.out.println("The logging payload is: " + payload);
            SalsaMessage msg = new SalsaMessage(SalsaMessage.MESSAGE_TYPE.salsa_log, PioneerConfiguration.getPioneerID_Structure(), SalsaMessageTopic.PIONEER_LOG, "", payload);
            publish.pushMessage(msg);
            System.out.println("The logging message is published: " + payload);
        } finally {
            MDC.remove(IN_APPEND_KEY);
        }

    }

    @Override
    public synchronized void close() {
        // close the thing?
    }

    @Override
    public boolean requiresLayout() {
        return true;
    }

}
