/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.salsa.pioneer.queueLogger;

import at.ac.tuwien.dsg.cloud.salsa.messaging.MQTTAdaptor.MQTTPublish;
import at.ac.tuwien.dsg.cloud.salsa.messaging.model.SalsaMessage;
import at.ac.tuwien.dsg.cloud.salsa.messaging.model.SalsaMessageTopic;
import at.ac.tuwien.dsg.cloud.salsa.pioneer.utils.PioneerConfiguration;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Layout;
import org.apache.log4j.MDC;
import org.apache.log4j.spi.LoggingEvent;


/**
 *
 * @author hungld
 */
public class QueueAppender extends AppenderSkeleton {

    private final MQTTPublish publish;

    public QueueAppender() {
        publish = new MQTTPublish(PioneerConfiguration.getBroker());
    }

    // Diagnostic Context: http://stackoverflow.com/questions/6321635/circular-dependency-when-logging-within-a-log4j-appender
    // This avoid the loop while this Appender and rootAppender use each others
    private static final String IN_APPEND_KEY = QueueAppender.class.getName() + ".inAppend";

    public QueueAppender(Layout layout) {
        publish = new MQTTPublish(PioneerConfiguration.getBroker());
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
            SalsaMessage msg = new SalsaMessage(SalsaMessage.MESSAGE_TYPE.log, PioneerConfiguration.getPioneerID_Structure(), SalsaMessageTopic.PIONEER_LOG, "", payload);
            publish.pushMessage(msg);
            System.out.println("The logging message is published: " + payload);
        } finally {
            MDC.remove(IN_APPEND_KEY);
        }

    }

    @Override
    public synchronized void close() {
        publish.disconnect();
    }

    @Override
    public boolean requiresLayout() {
        return true;
    }

}
