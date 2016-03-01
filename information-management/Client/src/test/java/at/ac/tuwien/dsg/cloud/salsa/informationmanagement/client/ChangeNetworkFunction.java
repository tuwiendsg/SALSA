/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.salsa.informationmanagement.client;

import at.ac.tuwien.dsg.cloud.salsa.model.VirtualComputingResource.SoftwareDefinedGateway;
import at.ac.tuwien.dsg.cloud.salsa.model.VirtualNetworkResource.VNF;
import java.util.List;
import java.util.Map;

/**
 *
 * @author hungld
 */
public class ChangeNetworkFunction {
    public static void main(String[] arg){
        DeliseClient client = new DeliseClient("myClient", "amqp://128.130.172.215", "amqp");
        client.getListOfDelise();
        
        List<VNF> vnfs = client.queryVNFBroadcast();
        List<SoftwareDefinedGateway> gws = client.querySoftwareDefinedGatewayBroadcast();
        
        Map<SoftwareDefinedGateway, VNF> linkMap = Algorithms.reconfigureNetworks(gws, vnfs);
        for(SoftwareDefinedGateway gw: linkMap.keySet()){
            client.redirectGateway(gw, linkMap.get(gw));
        }
    }
}
