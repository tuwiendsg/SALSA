/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.salsa.informationmanagement.client;

import at.ac.tuwien.dsg.cloud.salsa.informationmanagement.client.cache.Cache;
import at.ac.tuwien.dsg.cloud.salsa.informationmanagement.client.RelationshipManagement.NetworkGraphGenerator;
import at.ac.tuwien.dsg.cloud.salsa.informationmanagement.client.cache.CacheDelises;
import at.ac.tuwien.dsg.cloud.salsa.informationmanagement.client.cache.CacheVNF;
import at.ac.tuwien.dsg.cloud.salsa.informationmanagement.communication.messagePayloads.DeliseMeta;
import at.ac.tuwien.dsg.cloud.salsa.model.VirtualComputingResource.Capability.Concrete.CloudConnectivity;
import at.ac.tuwien.dsg.cloud.salsa.model.VirtualComputingResource.SoftwareDefinedGateway;
import at.ac.tuwien.dsg.cloud.salsa.model.VirtualNetworkResource.AccessPoint;
import at.ac.tuwien.dsg.cloud.salsa.model.VirtualNetworkResource.VNF;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author hungld
 */
public class TestNwGraph {

    public static void main(String[] args) throws Exception {
//        DeliseMeta meta1 = new DeliseMeta("uuid1", "1.1.1.", "topic");
//        DeliseMeta meta2 = new DeliseMeta("uuid2", "1.1.1.", "topic");
//        List<DeliseMeta> metas = new ArrayList<>();
//        metas.add(meta1);
//        metas.add(meta2);
//
//        CacheDelises.newInstance().writeDeliseCache(metas);
//        System.out.println("write delise meta done");
//        List<DeliseMeta> metas1 = CacheDelises.newInstance().loadDelisesCache();
//        System.out.println("get number of meta: " + metas1.size());

        
        VNF vnf1 = new VNF("test1", "protocol", new AccessPoint("1.1.1.1"));
        VNF vnf2 = new VNF("test2", "protocol", new AccessPoint("1.1.1.1"));
        vnf1.getConnectivities().add(new CloudConnectivity("testID", "aName", "no thing", "1.1.1.1", "2.2.2.2"));
        vnf2.getConnectivities().add(new CloudConnectivity("testID", "aName", "no thing", "1.1.1.1", "2.2.2.2"));
        
        List<VNF> list = new ArrayList<>();
        list.add(vnf1);
        list.add(vnf2);

        CacheVNF.newInstance().writeGatewayCache(list);

        System.out.println("Write done, item  " + list.size());
        List<VNF> routers = CacheVNF.newInstance().loadGatewaysCache();
        System.out.println("number of router: " + routers.size());
        System.out.println(routers.get(0).getName());
        

    }

}
