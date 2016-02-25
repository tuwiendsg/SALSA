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
import at.ac.tuwien.dsg.cloud.salsa.informationmanagement.communication.protocol.DeliseMessage;
import at.ac.tuwien.dsg.cloud.salsa.informationmanagement.communication.protocol.DeliseMessageTopic;
import at.ac.tuwien.dsg.cloud.salsa.model.VirtualComputingResource.SoftwareDefinedGateway;
import at.ac.tuwien.dsg.cloud.salsa.model.VirtualNetworkResource.VNF;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The client provide a set of methods to manage and interact with distributed Delise components
 *
 * @author hungld
 */
public class DeliseClient {

    MessageClientFactory FACTORY;
    /**
     * TODO: use the default topic to distinguish different client and managmeent scope E.g. each stakeholder will hold an ID, which regarding to the topic
     */
    String prefixTopic = "";
    static Logger logger = LoggerFactory.getLogger("DELISE");

    List<DeliseMeta> listOfDelise = new ArrayList<>();
    String name;

    public DeliseClient(String name, String broker, String brokerType) {
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
        sub.subscribe(DeliseMessageTopic.REGISTER_AND_HEARBEAT,timeout);
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
        return listOfDelise;
    }

    public SoftwareDefinedGateway querySoftwareDefinedGateway(String deliseUUID) {
        logger.debug("Trying to query DELISE with ID: " + deliseUUID);
        String unicastDeliseTopic = DeliseMessageTopic.getCollectorTopicByID(deliseUUID);

        MessagePublishInterface pub = FACTORY.getMessagePublisher();
        // note that when using callFunction, no need to declare the feedbackTopic. This will be filled by the call
        DeliseMessage queryMessage = new DeliseMessage(DeliseMessage.MESSAGE_TYPE.RPC_QUERY_SDGATEWAY_LOCAL, name, unicastDeliseTopic, "", "");
        logger.debug("Calling the function: " + queryMessage.toJson());
        DeliseMessage responseMessage = pub.callFunction(queryMessage);
        logger.debug("Query done !");
        String gatewayInfo = responseMessage.getPayload();
        logger.debug("Get SDG info: \n" + gatewayInfo);
        System.out.println("Get SDG info: \n" + gatewayInfo);
        return SoftwareDefinedGateway.fromJson(gatewayInfo);
    }

    public VNF queryVNF(String deliseUUID) {
        String unicastDeliseTopic = DeliseMessageTopic.getCollectorTopicByID(deliseUUID);

        MessagePublishInterface pub = FACTORY.getMessagePublisher();
        // note that when using callFunction, no need to declare the feedbackTopic. This will be filled by the call
        DeliseMessage queryMessage = new DeliseMessage(DeliseMessage.MESSAGE_TYPE.RPC_QUERY_NFV_LOCAL, name, unicastDeliseTopic, "", "");
        DeliseMessage responseMessage = pub.callFunction(queryMessage);
        String vnfInfo = responseMessage.getPayload();
        logger.debug("Get VNF info: \n" + vnfInfo);
        System.out.println("Get VNF info: \n" + vnfInfo);
        return VNF.fromJson(vnfInfo);
    }

    public List<DeliseMeta> getListOfDelise() {
        return listOfDelise;
    }

    public String getName() {
        return name;
    }

}
