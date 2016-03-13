/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.salsa.informationmanagement.client;

import at.ac.tuwien.dsg.cloud.salsa.informationmanagement.client.cache.CacheDelises;
import at.ac.tuwien.dsg.cloud.salsa.informationmanagement.client.cache.CacheGateway;
import at.ac.tuwien.dsg.cloud.salsa.informationmanagement.client.cache.CacheVNF;
import at.ac.tuwien.dsg.cloud.salsa.informationmanagement.communication.messageInterface.MessageClientFactory;
import at.ac.tuwien.dsg.cloud.salsa.informationmanagement.communication.messageInterface.MessagePublishInterface;
import at.ac.tuwien.dsg.cloud.salsa.informationmanagement.communication.messageInterface.MessageSubscribeInterface;
import at.ac.tuwien.dsg.cloud.salsa.informationmanagement.communication.messageInterface.SalsaMessageHandling;
import at.ac.tuwien.dsg.cloud.salsa.informationmanagement.communication.messagePayloads.DeliseMeta;
import at.ac.tuwien.dsg.cloud.salsa.informationmanagement.communication.protocol.DeliseMessage;
import at.ac.tuwien.dsg.cloud.salsa.informationmanagement.communication.protocol.DeliseMessageTopic;
import at.ac.tuwien.dsg.cloud.salsa.informationmanagement.communication.protocol.InfoSourceSettings;
import at.ac.tuwien.dsg.cloud.salsa.model.VirtualComputingResource.SoftwareDefinedGateway;
import at.ac.tuwien.dsg.cloud.salsa.model.VirtualNetworkResource.VNF;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The client provide a set of methods to manage and interact with distributed Delise components
 *
 * @author hungld
 */
public class QueryManager {

    MessageClientFactory FACTORY;
    /**
     * TODO: use the default topic to distinguish different client and managmeent scope E.g. each stakeholder will hold an ID, which regarding to the topic
     */
    String prefixTopic = "";
    static Logger logger = LoggerFactory.getLogger("DELISE");

    List<DeliseMeta> listOfDelise = new ArrayList<>();
    String name;

    public QueryManager(String name, String broker, String brokerType) {
        this.name = name;
        this.FACTORY = new MessageClientFactory(broker, brokerType);
    }

    /**
     * This broadcast a message, block for timeout seconds, update the list
     *
     * @param timeout The time to wait response from other Delise
     * @return
     */
    public List<DeliseMeta> synDelise(long timeout) {
        logger.debug("Start syn delise...");
        listOfDelise.clear();

        MessageSubscribeInterface sub = FACTORY.getMessageSubscriber(new SalsaMessageHandling() {
            @Override
            public void handleMessage(DeliseMessage msg) {
                logger.debug("A message arrive, from: {}, type: {}, topic: {} ", msg.getFromSalsa(), msg.getMsgType(), msg.getTopic());
                if (msg.getMsgType().equals(DeliseMessage.MESSAGE_TYPE.SYN_REPLY)) {
                    logger.debug("Yes, it is a SYN message, adding the metadata");
                    DeliseMeta meta = DeliseMeta.fromJson(msg.getPayload());
                    logger.debug("Meta: " + meta.toJson());
                    listOfDelise.add(meta);
                }
                logger.debug("Add meta finished");
            }
        });
        logger.debug("Will subscribe to the topic");
        sub.subscribe(DeliseMessageTopic.REGISTER_AND_HEARBEAT, timeout);
        logger.debug("Subscribe done, now waiting for SYN message");

        MessagePublishInterface pub = FACTORY.getMessagePublisher();
        DeliseMessage synRequestMsg = new DeliseMessage(DeliseMessage.MESSAGE_TYPE.SYN_REQUEST, this.name, DeliseMessageTopic.CLIENT_REQUEST_DELISE, "", "");
        logger.debug("Client starts to send SYN message to many DESLISE...");
        pub.pushMessage(synRequestMsg);
        try {
            Thread.sleep(timeout);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        logger.debug("Done, should close the subscribe now.. ");
        // write to cache

        (new CacheDelises()).writeDeliseCache(listOfDelise);

        return listOfDelise;
    }

    public List<DeliseMeta> ReadCacheOrSyncDElise() {
        CacheDelises dliseCache = new CacheDelises();
        List<DeliseMeta> list = dliseCache.loadDelisesCache();
        if (list == null || list.isEmpty()) {
            // return the syn command
            logger.debug("There is no d-elise cache found, try to run a query ...");
            synDelise(3000);
        }
        // second try        
        list = dliseCache.loadDelisesCache();
        if (list == null || list.isEmpty()) {
            logger.debug("No D-Elise found. Cannot send query");
            return null;
        }
        this.listOfDelise.clear();
        this.listOfDelise = list;
        return this.listOfDelise;
    }

    public SoftwareDefinedGateway querySoftwareDefinedGateway_Unicast(String deliseUUID) {
        logger.debug("Trying to query DELISE with ID: " + deliseUUID);
        String unicastDeliseTopic = DeliseMessageTopic.getCollectorTopicByID(deliseUUID);

        MessagePublishInterface pub = FACTORY.getMessagePublisher();
        // note that when using callFunction, no need to declare the feedbackTopic. This will be filled by the call
        DeliseMessage queryMessage = new DeliseMessage(DeliseMessage.MESSAGE_TYPE.RPC_QUERY_SDGATEWAY_LOCAL, name, unicastDeliseTopic, "", "");
        logger.debug("Calling the function: " + queryMessage.toJson());
        DeliseMessage responseMessage = pub.callFunction(queryMessage);
        logger.debug("Query done !");
        String gatewayInfo = responseMessage.getPayload();
        if (gatewayInfo.equals("null")) {
            logger.debug("Delise " + deliseUUID + " does not return a gateway info");
            return null;
        }
        logger.debug("Get SDG info: \n" + gatewayInfo);
        System.out.println("Get SDG info: \n" + gatewayInfo);
        return SoftwareDefinedGateway.fromJson(gatewayInfo);
    }

    public VNF queryVNF_Unicast(String deliseUUID) {
        String unicastDeliseTopic = DeliseMessageTopic.getCollectorTopicByID(deliseUUID);

        MessagePublishInterface pub = FACTORY.getMessagePublisher();
        // note that when using callFunction, no need to declare the feedbackTopic. This will be filled by the call
        DeliseMessage queryMessage = new DeliseMessage(DeliseMessage.MESSAGE_TYPE.RPC_QUERY_NFV_LOCAL, name, unicastDeliseTopic, "", "");
        DeliseMessage responseMessage = pub.callFunction(queryMessage);
        String vnfInfo = responseMessage.getPayload();
        if (vnfInfo.equals("null")) {
            logger.debug("Delise " + deliseUUID + " does not return a gateway info");
            return null;
        }
        logger.debug("Get VNF info: \n" + vnfInfo);
        System.out.println("Get VNF info: \n" + vnfInfo);
        return VNF.fromJson(vnfInfo);
    }

    public List<SoftwareDefinedGateway> querySoftwareDefinedGateway_Multicast() {
        List<DeliseMeta> delises = ReadCacheOrSyncDElise();
        List<SoftwareDefinedGateway> gateways = new ArrayList<>();
        System.out.println("Number of gateway syn: " + delises.size() + ". Now start unicast querying information...");

        for (DeliseMeta delise : delises) {
            logger.debug("Checking delise id: " + delise.getUuid() + ", ip: " + delise.getIp());
            InfoSourceSettings settings = InfoSourceSettings.fromJson(delise.getSettings());
            if (settings.getSource() != null && !settings.getSource().isEmpty()) {
                InfoSourceSettings.InfoSource firstSource = settings.getSource().get(0);
                if (firstSource.isGatewayResource()) {
                    logger.debug("It is a gateway, the delise id: " + delise.getUuid() + ", ip: " + delise.getIp());
                    SoftwareDefinedGateway g = querySoftwareDefinedGateway_Unicast(delise.getUuid());
                    gateways.add(g);
                } else {
                    logger.debug("Delise is: " + delise.getUuid() + " is not a gateway !");
                }
            }

        }

        logger.debug("Now start to write the list of gateway to cache ....");
        (new CacheGateway()).writeGatewayCache(gateways);

        return gateways;
    }

    public List<VNF> queryVNF_Multicast() {
        List<DeliseMeta> delises = ReadCacheOrSyncDElise();
        List<VNF> routers = new ArrayList<>();
        System.out.println("Number of router syn: " + delises.size() + ". Now start unicast querying information...");

        for (DeliseMeta delise : delises) {
            logger.debug("Checking router id: " + delise.getUuid() + ", ip: " + delise.getIp());
            InfoSourceSettings settings = InfoSourceSettings.fromJson(delise.getSettings());
            if (settings.getSource() != null && !settings.getSource().isEmpty()) {
                InfoSourceSettings.InfoSource firstSource = settings.getSource().get(0);
                if (firstSource.isVNFResource()) {
                    logger.debug("It is a router, the delise id: " + delise.getUuid() + ", ip: " + delise.getIp());
                    VNF g = queryVNF_Unicast(delise.getUuid());
                    routers.add(g);
                } else {
                    logger.debug("Delise is: " + delise.getUuid() + " is not a gateway !");
                }
            }

        }

        logger.debug("Now start to write the list of router to cache ....");
        (new CacheVNF()).writeGatewayCache(routers);
        return routers;
    }

    /**
     * This function broadcast a message and wait for a few seconds Also, it record the last message to log file
     *
     * @return
     */
    public List<SoftwareDefinedGateway> querySoftwareDefinedGateway_Broadcast(long timeout) {
        System.out.println("Start broadcasting the query...");
        File dir = new File("log/queries/data");
        dir.mkdirs();
        System.out.println("Data is stored in: " + dir.getAbsolutePath());
        final long startTimeStamp = (new Date()).getTime();

        String eventFileName = "log/queries/" + startTimeStamp + ".event";
        String dataFileName = "log/queries/data/" + startTimeStamp + ".data";
        //final List<SoftwareDefinedGateway> gateways = new ArrayList<>();
        final List<String> gatewayInfo = new ArrayList<>();
        final List<String> events = new LinkedList<>();

        MessagePublishInterface pub = FACTORY.getMessagePublisher();
        // note that when using callFunction, no need to declare the feedbackTopic. This will be filled by the call
        String feedBackTopic = DeliseMessageTopic.getTemporaryTopic();

        MessageSubscribeInterface sub = FACTORY.getMessageSubscriber(new SalsaMessageHandling() {
            long latestTime = 0;
            long quantity = 0;
            long currentSum = 0;

            @Override
            public void handleMessage(DeliseMessage message) {
                System.out.println("Get a response message from " + message.getFromSalsa());
                SoftwareDefinedGateway gw = SoftwareDefinedGateway.fromJson(message.getPayload());
                if (gw == null) {
                    System.out.println("Payload is null, or cannot be converted");
                    return;
                }
                Long currentTS = (new Date()).getTime();

                latestTime = currentTS - startTimeStamp;
                currentSum = currentSum + latestTime;
                quantity += 1;
                double avg = (double) currentSum / (double) quantity;
                String eventStr = gw.getUuid() + "," + currentTS + "," + latestTime + "," + String.format("%.3f", avg) + "," + quantity;
                System.out.println("Got an event: " + eventStr);

                events.add(eventStr);
//                gateways.add(gw);
                String gwStr = gw.toJson();
                gatewayInfo.add("Gateway " + gw.getUuid() + ", capas: " + gw.getCapabilities().size() + ", size: " + gwStr.length() + "," + String.format("%.3f", (double) gwStr.length() / 1024 / 1024) + " MB");

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

        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(eventFileName, true)))) {
            System.out.println("Now saving events to file: " + eventFileName);
            for (String s : events) {
                out.println(s);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // write result to cache        
        //CacheGateway gwCache = new CacheGateway();
        //gwCache.setFileName(dataFileName);        
        //gwCache.writeGatewayCache(gateways);
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(dataFileName, true)))) {
            System.out.println("Gateway ");
            for (String s : gatewayInfo) {
                out.println(s);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void redirectGateway(SoftwareDefinedGateway gw, VNF vnf) {
        // TODO: implement this
    }

    public List<DeliseMeta> getListOfDelise() {
        return listOfDelise;
    }

    public String getName() {
        return name;
    }

}
