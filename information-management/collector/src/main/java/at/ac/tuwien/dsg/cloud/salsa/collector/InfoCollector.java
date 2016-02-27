/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.salsa.collector;

import at.ac.tuwien.dsg.cloud.salsa.collector.ResourceDriverImp.RawInfoCollectorFactory;
import at.ac.tuwien.dsg.cloud.salsa.collector.utils.DeliseConfiguration;
import at.ac.tuwien.dsg.cloud.salsa.model.VirtualComputingResource.Capability.Concrete.DataPoint;
import at.ac.tuwien.dsg.cloud.salsa.model.VirtualComputingResource.SoftwareDefinedGateway;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import at.ac.tuwien.dsg.cloud.salsa.informationmanagement.abstracttransformer.GatewayResourceDiscoveryInterface;
import at.ac.tuwien.dsg.cloud.salsa.informationmanagement.abstracttransformer.RouterResourceDiscoveryInterface;
import at.ac.tuwien.dsg.cloud.salsa.model.VirtualComputingResource.Capability.Concrete.ControlPoint;
import at.ac.tuwien.dsg.cloud.salsa.model.VirtualNetworkResource.VNF;
import org.slf4j.Logger;

/**
 * A collector include a gatherer and a information transformer Information source --> Retriever --> transformer --> VirtualDefinedGateway data model This class
 * require InfoSource configuration to point to: (1) information source and (2) the class to collect info
 *
 * @author hungld
 */
public class InfoCollector {

    /**
     * The id of the collector used among DELISE
     */
    String uuid;

    /**
     * For general regconization by human
     */
    String ip;

    /**
     * Any other meta data of the environment where collector is deployed e.g. machine name, uname -a, cpu, max ram.
     */
    Map<String, String> meta;

    static Logger logger = DeliseConfiguration.getLogger();
    static InfoSourceSettings settings;

    private static boolean hasSettings() {
        System.out.println("Loadding settings file...");
        settings = InfoSourceSettings.loadDefaultFile();
        System.out.println("Load resource file done !");
        if (settings == null || settings.getSource().isEmpty()) {
            logger.error("ERROR: No source information found. Please check configuration file.");
            System.out.println("ERROR: No source information found. Please check configuration file.");
            return false;
        }
        return true;
    }

    public static VNF getRouterInfo() throws Exception {
        System.out.println("Executing all the transformer o collect router info...");
        VNF vnf = new VNF();
        if (!hasSettings()) {
            return null;
        }
        for (InfoSourceSettings.InfoSource source : settings.getSource()) {
            System.out.println("Checking resource: " + source.getType());
            RawInfoCollector rawCollector = RawInfoCollectorFactory.getCollector(source.getType());

            // note: we are collect into for a single router, thus this map at the end should have only 1 entry
            Map<String, String> rawInfo = rawCollector.getRawInformation(source);

            if (Class.forName(source.getTransformerClass()).getInterfaces()[0].getSimpleName().equals("RouterResourceDiscoveryInterface")) {
                Class<? extends RouterResourceDiscoveryInterface> tranformClass = (Class<? extends RouterResourceDiscoveryInterface>) Class.forName(source.getTransformerClass());
                for (String routerInfo : rawInfo.keySet()) {
                    String raw = rawInfo.get(routerInfo);
                    RouterResourceDiscoveryInterface t = tranformClass.newInstance();
                    System.out.println("Created tranformer instance done: ");

                    Object domain = t.validateAndConvertToDomainModel(raw);
                    vnf = t.toVNF(domain);
                    return vnf;
                }

            }
        }
        return vnf;
    }

    public static SoftwareDefinedGateway getGatewayInfo() throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        System.out.println("Executing all the transformer to collect gateway info...");
        SoftwareDefinedGateway gw = new SoftwareDefinedGateway();

        if (!hasSettings()) {
            return null;
        }

        for (InfoSourceSettings.InfoSource source : settings.getSource()) {
            System.out.println("Checking resource: " + source.getType());

            RawInfoCollector rawCollector = RawInfoCollectorFactory.getCollector(source.getType());
            Map<String, String> rawInfo = rawCollector.getRawInformation(source);

            if (Class.forName(source.getTransformerClass()).getInterfaces()[0].getSimpleName().equals("GatewayResourceDiscoveryInterface")) {
                Class<? extends GatewayResourceDiscoveryInterface> tranformClass = (Class<? extends GatewayResourceDiscoveryInterface>) Class.forName(source.getTransformerClass());

                for (String entityURIorFilePath : rawInfo.keySet()) {
                    String raw = rawInfo.get(entityURIorFilePath);
                    GatewayResourceDiscoveryInterface t = tranformClass.newInstance();
                    System.out.println("Created tranformer instance done: ");

                    //DataPointTransformerInterface
                    Object domain = t.validateAndConvertToDomainModel(raw, entityURIorFilePath);
                    DataPoint dp = t.toDataPoint(domain);
                    List<ControlPoint> cps = t.toControlPoint(domain);

                    gw.getCapabilities().add(dp);
                    gw.getCapabilities().addAll(cps);
                }
            }

        }
        System.out.println("Getting information done. Number of datapoint: " + gw.getCapabilities().size());
        return gw;
    }

}
