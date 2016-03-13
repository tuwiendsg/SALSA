/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.salsa.informationmanagement.client;

import at.ac.tuwien.dsg.cloud.salsa.informationmanagement.communication.messageInterface.MessageClientFactory;
import at.ac.tuwien.dsg.cloud.salsa.informationmanagement.communication.messageInterface.MessagePublishInterface;
import at.ac.tuwien.dsg.cloud.salsa.informationmanagement.communication.messageInterface.MessageSubscribeInterface;
import at.ac.tuwien.dsg.cloud.salsa.informationmanagement.communication.messageInterface.SalsaMessageHandling;
import at.ac.tuwien.dsg.cloud.salsa.informationmanagement.communication.messagePayloads.DeliseMeta;
import at.ac.tuwien.dsg.cloud.salsa.informationmanagement.communication.messagePayloads.UpdateGatewayStatus;
import at.ac.tuwien.dsg.cloud.salsa.informationmanagement.communication.protocol.DeliseMessage;
import at.ac.tuwien.dsg.cloud.salsa.informationmanagement.communication.protocol.DeliseMessageTopic;
import at.ac.tuwien.dsg.cloud.salsa.model.VirtualComputingResource.SoftwareDefinedGateway;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author hungld
 */
public class SubscriptionManager {

    MessageClientFactory FACTORY;
    /**
     * TODO: use the default topic to distinguish different client and managmeent scope E.g. each stakeholder will hold an ID, which regarding to the topic
     */
    String prefixTopic = "";
    static Logger logger = LoggerFactory.getLogger("DELISE");

    String name;

    public SubscriptionManager(String name, String broker, String brokerType) {
        this.name = name;
        this.FACTORY = new MessageClientFactory(broker, brokerType);
    }

    public void querySoftwareDefinedGateway_Broadcast(long timeout) {
        System.out.println("Start broadcasting the subscription...");
        File dir = new File("log/queries/data");
        dir.mkdirs();
        System.out.println("Data is stored in: " + dir.getAbsolutePath());
        final long startTimeStamp = (new Date()).getTime();

        String eventFileName = "log/queries/" + startTimeStamp + ".event";
        String dataFileName = "log/queries/data/" + startTimeStamp + ".data";
        //final List<SoftwareDefinedGateway> gateways = new ArrayList<>();

        MessagePublishInterface pub = FACTORY.getMessagePublisher();
        // note that when using callFunction, no need to declare the feedbackTopic. This will be filled by the call
        String feedBackTopic = DeliseMessageTopic.getTemporaryTopic();

        MessageSubscribeInterface sub = FACTORY.getMessageSubscriber(new SalsaMessageHandling() {
            double currentThroughput = 0L;
            final long windowSize = 30;

            @Override
            public void handleMessage(DeliseMessage message) {
                // this technique to measure the throughput
                Cache<String, String> autoExpireCache = CacheBuilder.newBuilder()
                        .concurrencyLevel(4)
                        .weakKeys()
                        .maximumSize(10000)
                        .expireAfterWrite(windowSize, TimeUnit.SECONDS)
                        .build(
                                new CacheLoader<String, String>() {
                            public String load(String key) throws Exception {
                                return key;
                            }
                        });
                String payload = message.getPayload();
                autoExpireCache.put(payload, payload);

                System.out.println("Get a response message from " + message.getFromSalsa());
                UpdateGatewayStatus updateStatus = UpdateGatewayStatus.fromJson(message.getPayload());                
                System.out.println("update status: appear " + updateStatus.getAppear().size() + ", disappear: " + updateStatus.getDisappear().size());

                // record time 
                Long currentTS = (new Date()).getTime();
                Long transferTime = currentTS - updateStatus.getTimeStamp();
                //int currentDataSize = message.getPayload().length();
                long currentSize = autoExpireCache.size() * payload.length();
                currentThroughput = (currentThroughput + ((double) currentSize / 30) * 0.3) / 1.3;
                
                // here just record time and throughtput, no actual update yet
                

            }
        });
        sub.subscribe(feedBackTopic, timeout);

        DeliseMessage queryMessage = new DeliseMessage(DeliseMessage.MESSAGE_TYPE.RPC_QUERY_SDGATEWAY_LOCAL, name, DeliseMessageTopic.getCollectorTopicBroadcast(), feedBackTopic, "");
        pub.pushMessage(queryMessage);

        // wait for a few second
        try {
            System.out.println("Wait for " + timeout + " miliseconds ...........");
            Thread.sleep(timeout);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
}
