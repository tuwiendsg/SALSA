/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.salsa.informationmanagement.transformweaverouter;


import at.ac.tuwien.dsg.cloud.salsa.informationmanagement.abstracttransformer.RouterResourceDiscoveryInterface;
import at.ac.tuwien.dsg.cloud.salsa.model.VirtualNetworkResource.AccessPoint;
import at.ac.tuwien.dsg.cloud.salsa.model.VirtualNetworkResource.VNF;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.weaveworks.weave.WeaveRouter;
import java.io.IOException;

/**
 *
 * @author hungld
 */
public class WeaveRouterResourceDiscovery implements RouterResourceDiscoveryInterface<WeaveRouter> {

    @Override
    public WeaveRouter validateAndConvertToDomainModel(String rawData) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(rawData, WeaveRouter.class);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public VNF toVNF(WeaveRouter weave) {
        if (weave == null) {
            System.out.println("Error: WeaveRouter information is null");
            return null;
        }

        String name = weave.getRouter().getNickName() + "-" + weave.getRouter().getName();
        String protocol = weave.getRouter().getProtocol();
        AccessPoint ap = new AccessPoint(Utils.getEth0Address());

        VNF vnf = new VNF(name, protocol, ap);

        return vnf;
    }

}
