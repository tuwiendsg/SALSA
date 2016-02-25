/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.salsa.informationmanagement.transformweaverouter.test;

import at.ac.tuwien.dsg.cloud.salsa.informationmanagement.transformweaverouter.WeaveRouterResourceDiscovery;
import at.ac.tuwien.dsg.cloud.salsa.model.VirtualNetworkResource.VNF;
import com.weaveworks.weave.WeaveRouter;
import java.util.Scanner;

/**
 *
 * @author hungld
 */
public class test {

    public static void main(String[] args) {
        String text = new Scanner(test.class.getResourceAsStream("/weave_report.json"), "UTF-8").useDelimiter("\\A").next();
        WeaveRouterResourceDiscovery discovery = new WeaveRouterResourceDiscovery();
        WeaveRouter weaveRouter = discovery.validateAndConvertToDomainModel(text);
        VNF vnf = discovery.toVNF(weaveRouter);

        System.out.println(vnf.getName());
        System.out.println(vnf.getNetworkInterface().get(0).getNetworkAddress());
        System.out.println(vnf.getRoutingProtocol());
    }
}
