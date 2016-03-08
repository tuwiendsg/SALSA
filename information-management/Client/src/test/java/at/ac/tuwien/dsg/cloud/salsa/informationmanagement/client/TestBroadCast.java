/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.salsa.informationmanagement.client;

import at.ac.tuwien.dsg.cloud.salsa.informationmanagement.client.cache.Cache;
import at.ac.tuwien.dsg.cloud.salsa.informationmanagement.client.RelationshipManagement.NetworkGraphGenerator;
import at.ac.tuwien.dsg.cloud.salsa.informationmanagement.client.cache.CacheGateway;
import at.ac.tuwien.dsg.cloud.salsa.informationmanagement.client.cache.CacheVNF;
import at.ac.tuwien.dsg.cloud.salsa.model.VirtualComputingResource.SoftwareDefinedGateway;
import at.ac.tuwien.dsg.cloud.salsa.model.VirtualNetworkResource.VNF;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author hungld
 */
public class TestBroadCast {

    public static void main(String[] args) throws Exception {
        QueryManager client = new QueryManager("myClient", "amqp://128.130.172.215", "amqp");
        client.synDelise(3000);

        List<SoftwareDefinedGateway> gateways = client.querySoftwareDefinedGatewayBroadcast();
        (new CacheGateway()).writeGatewayCache(gateways);

        List<VNF> routers = client.queryVNFBroadcast();
        (new CacheVNF()).writeGatewayCache(routers);
        System.out.println("Gateway number: " + gateways.size());
        System.out.println("Router number: " + routers.size());

        NetworkGraphGenerator generator = new NetworkGraphGenerator();
        String graph = generator.generateGraph(gateways, routers);
        System.out.println(graph);
    }

    public static void main1(String[] args) throws Exception {
        QueryManager client = new QueryManager("myClient", "amqp://128.130.172.215", "amqp");
        client.synDelise(3000);

//        List<SoftwareDefinedGateway> gateways = client.querySoftwareDefinedGatewayBroadcast();
        List<SoftwareDefinedGateway> gateways = new ArrayList<>();
        SoftwareDefinedGateway g1 = client.querySoftwareDefinedGateway("2fb8ecfa-3a8c-4d23-b9e3-0757a8121c9b");
        SoftwareDefinedGateway g2 = client.querySoftwareDefinedGateway("3db5d4ed-0d9b-4ad2-90c8-98466ec38140");
        gateways.add(g1);
        gateways.add(g2);

        (new CacheGateway()).writeGatewayCache(gateways);
//        List<VNF> routers = client.queryVNFBroadcast();
        List<VNF> routers = new ArrayList<>();
        VNF r1 = client.queryVNF("ed45039e-a5dc-4c08-b960-b41c47577310"); // cloud        
        VNF r2 = client.queryVNF("ded3f818-f05c-42ea-904b-3e01ef1f154f"); // virtual router 1
        VNF r3 = client.queryVNF("e7a0bdc3-2151-432b-93f3-4e1c9082a88b"); // iot site 1
        routers.add(r1);
        routers.add(r2);
        routers.add(r3);
        (new CacheVNF()).writeGatewayCache(routers);
        System.out.println("Gateway number: " + gateways.size());
        System.out.println("Router number: " + routers.size());

        NetworkGraphGenerator generator = new NetworkGraphGenerator();
        String graph = generator.generateGraph(gateways, routers);
        System.out.println(graph);
    }
}
