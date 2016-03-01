/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.salsa.informationmanagement.client;

import at.ac.tuwien.dsg.cloud.salsa.model.VirtualComputingResource.SoftwareDefinedGateway;
import at.ac.tuwien.dsg.cloud.salsa.model.VirtualNetworkResource.VNF;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author hungld
 */
public class Algorithms {
    public static Map<SoftwareDefinedGateway, VNF> reconfigureNetworks(List<SoftwareDefinedGateway> gws, List<VNF> vnfs){
        Map<SoftwareDefinedGateway, VNF> map = new HashMap<>();
        // do something to map gw and vnf
        return map;
    }
}
