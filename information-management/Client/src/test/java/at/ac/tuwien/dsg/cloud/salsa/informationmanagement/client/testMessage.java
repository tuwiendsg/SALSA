/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.salsa.informationmanagement.client;

import at.ac.tuwien.dsg.cloud.salsa.informationmanagement.communication.messagePayloads.DeliseMeta;
import at.ac.tuwien.dsg.cloud.salsa.model.VirtualComputingResource.SoftwareDefinedGateway;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;

/**
 *
 * @author hungld
 */
public class testMessage {

    public static void main(String[] args) throws Exception {
        /**
         * This part connect the client to the message queue, get the list of DElise
         */
        DeliseClient client = new DeliseClient("myClient", "amqp://128.130.172.215", "amqp");
        client.synDelise(2000);        

        ObjectMapper mapper = new ObjectMapper();
        System.out.println(mapper.writeValueAsString(client.getListOfDelise()));

        System.out.println("SYN DONE, GOING TO PHASE 2");
        System.out.println("There are " + client.getListOfDelise().size() +" gateway");

        /**
         * Query information from each DElise, unicast pattern
         */        
        for (DeliseMeta delise : client.getListOfDelise()) {
            System.out.println("QUERYING DELISE: " + delise.getUuid());
            SoftwareDefinedGateway gw = client.querySoftwareDefinedGateway(delise.getUuid());
            System.out.println("Number of capabilities: " + gw.getCapabilities().size());
            System.out.println("GW Info: " + gw.toJson());
        }
        
        /**
         * Query information from all, broad cast pattern + filter
         */
        
        
        /**
         * Call the control point to reconfigure the component
         */

        System.out.println("Should quit here ! (or queue are not closed yet)");

    }
}
