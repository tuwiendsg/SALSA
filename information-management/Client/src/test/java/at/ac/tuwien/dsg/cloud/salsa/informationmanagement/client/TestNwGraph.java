/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.salsa.informationmanagement.client;

import at.ac.tuwien.dsg.cloud.salsa.informationmanagement.client.RelationshipManagement.NetworkGraphGenerator;
import at.ac.tuwien.dsg.cloud.salsa.model.VirtualComputingResource.SoftwareDefinedGateway;
import at.ac.tuwien.dsg.cloud.salsa.model.VirtualNetworkResource.AccessPoint;
import at.ac.tuwien.dsg.cloud.salsa.model.VirtualNetworkResource.VNF;
import java.util.List;

/**
 *
 * @author hungld
 */
public class TestNwGraph {

    public static void main11(String[] args) throws Exception {
    
    
    
    VNF vnf = new VNF("test", "protocol", new AccessPoint("1.1.1.1"));
    String json = vnf.toJson();
        VNF r11= VNF.fromJson(json);
        System.out.println(r11.getName());
        
    }
    public static void main(String[] args) throws Exception {
        Cache cache1 = new Cache(Cache.CacheInfo.router);
        Cache cache2 = new Cache(Cache.CacheInfo.sdgateway);

        List<VNF> vnfs = cache1.loadRoutersCache();
        System.out.println("Load cache for VNFs done: " + vnfs.size() + " items");

        List<SoftwareDefinedGateway> gws = cache2.loadGatewaysCache();
        System.out.println("Load cache for GWs done: " + gws.size() + " items");

        NetworkGraphGenerator generator = new NetworkGraphGenerator();
        String graph = generator.generateGraph(gws, vnfs);
        System.out.println(graph);
    }
}
