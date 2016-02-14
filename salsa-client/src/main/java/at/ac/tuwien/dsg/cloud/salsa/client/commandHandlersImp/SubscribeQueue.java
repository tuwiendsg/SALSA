/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.salsa.client.commandHandlersImp;

import at.ac.tuwien.dsg.cloud.salsa.client.CommandHandler;
import at.ac.tuwien.dsg.cloud.salsa.messaging.AMQPAdaptor.AMQPSubscribe;
import at.ac.tuwien.dsg.cloud.salsa.messaging.MQTTAdaptor.MQTTSubscribe;
import at.ac.tuwien.dsg.cloud.salsa.messaging.messageInterface.MessageSubscribeInterface;
import at.ac.tuwien.dsg.cloud.salsa.messaging.messageInterface.SalsaMessageHandling;
import at.ac.tuwien.dsg.cloud.salsa.messaging.model.Salsa.INFOMessage;
import at.ac.tuwien.dsg.cloud.salsa.messaging.model.Salsa.SalsaMsgConfigureState;
import at.ac.tuwien.dsg.cloud.salsa.messaging.protocol.SalsaMessage;
import at.ac.tuwien.dsg.cloud.salsa.messaging.protocol.SalsaMessageTopic;
import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.kohsuke.args4j.Argument;

/**
 *
 * @author hungld
 */
public class SubscribeQueue implements CommandHandler {

    @Argument(index = 0, metaVar = "queue_type", usage = "The type of the queue. SALSA support mqtt and ampq", required = true)
    String type;

    @Argument(index = 1, metaVar = "endpoint", usage = "The endpoint of the queue.", required = true)
    String endpoint;
    
    @Argument(index = 2, metaVar = "topic", usage = "The topic to subscribt.", required = false)
    String topic;
    
    @Override
    public void execute() {
        SalsaMessageHandling handler = new SalsaMessageHandling() {
            @Override
            public void handleMessage(SalsaMessage salsaMessage) {                
                INFOMessage state = INFOMessage.fromJson(salsaMessage.getPayload());                
                String msgStr = state.toJson();
                System.out.println(msgStr);
                try {
                    FileUtils.writeStringToFile(new File("events.log"), msgStr+"\n", true);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        };
        
        MessageSubscribeInterface sub;
        switch (type) {
            case "mqtt":
                sub = new MQTTSubscribe(endpoint, handler);
                break;
            case "amqp":
                sub = new AMQPSubscribe(endpoint, handler); 
                break;
            default:
                System.out.println("Undefined queue protocol: " + type +". Should be mqtt or ampq!");                
                return;
        }
        if (topic==null){
            sub.subscribe(SalsaMessageTopic.SALSA_PUBLISH_EVENT);
        } else {
            sub.subscribe(topic);
        }
    }

    @Override
    public String getCommandDescription() {
        return "Subscribe queue to get SALSA actions";
    }
    
}
