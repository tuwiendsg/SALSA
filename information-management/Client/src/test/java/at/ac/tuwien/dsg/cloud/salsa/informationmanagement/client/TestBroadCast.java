/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.salsa.informationmanagement.client;

import at.ac.tuwien.dsg.cloud.salsa.informationmanagement.client.RelationshipManagement.NetworkGraphGenerator;
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

//        List<SoftwareDefinedGateway> gateways = client.querySoftwareDefinedGatewayBroadcast();
        List<SoftwareDefinedGateway> gateways = new ArrayList<>();
        SoftwareDefinedGateway g1 = client.querySoftwareDefinedGateway("e0e37e06-f6fc-41ca-a46d-3ea40174be31");
        SoftwareDefinedGateway g2 = client.querySoftwareDefinedGateway("80624381-9188-4b7f-a0f6-ca8c03e7682f");
        gateways.add(g1);
        gateways.add(g2);
        (new Cache<SoftwareDefinedGateway>(Cache.CacheInfo.sdgateway)).writeListOfGateways(gateways);
//        List<VNF> routers = client.queryVNFBroadcast();
        List<VNF> routers = new ArrayList<>();
        VNF r1 = client.queryVNF("8039876f-a733-4a59-b1c2-1d2ad9dadfba"); // cloud        
        VNF r2 = client.queryVNF("bb629dbf-0d2c-4060-9553-a1c64e368ef8"); // virtual router 1
        VNF r3 = client.queryVNF("b95b485e-8962-40d9-a4e4-730d8bcdf16d"); // iot site 1
        routers.add(r1);
        routers.add(r2);
        routers.add(r3);
        (new Cache<VNF>(Cache.CacheInfo.router)).writeListOfRouter(routers);
        System.out.println("Gateway number: " + gateways.size());
        System.out.println("Router number: " + routers.size());
        
        
        
        
        NetworkGraphGenerator generator = new NetworkGraphGenerator();
        String graph = generator.generateGraph(gateways, routers);
        System.out.println(graph);
    }
}
