/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.salsa.collector;

import at.ac.tuwien.dsg.cloud.salsa.informationmanagement.communication.protocol.InfoSourceSettings;
import at.ac.tuwien.dsg.cloud.salsa.collector.ResourceDriverImp.RawInfoCollectorFactory;
import at.ac.tuwien.dsg.cloud.salsa.collector.utils.DeliseConfiguration;
import at.ac.tuwien.dsg.cloud.salsa.model.VirtualComputingResource.Capability.Concrete.DataPoint;
import at.ac.tuwien.dsg.cloud.salsa.model.VirtualComputingResource.SoftwareDefinedGateway;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import at.ac.tuwien.dsg.cloud.salsa.informationmanagement.abstracttransformer.GatewayResourceDiscoveryInterface;
import at.ac.tuwien.dsg.cloud.salsa.informationmanagement.abstracttransformer.RouterResourceDiscoveryInterface;
import at.ac.tuwien.dsg.cloud.salsa.informationmanagement.communication.Utils.DeliseUtils;
import at.ac.tuwien.dsg.cloud.salsa.model.VirtualComputingResource.Capability.Concrete.CloudConnectivity;
import at.ac.tuwien.dsg.cloud.salsa.model.VirtualComputingResource.Capability.Concrete.ControlPoint;
import at.ac.tuwien.dsg.cloud.salsa.model.VirtualNetworkResource.VNF;
import static java.lang.System.out;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
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
        VNF vnf = null;
        if (!hasSettings()) {
            return null;
        }
        for (InfoSourceSettings.InfoSource source : settings.getSource()) {
            System.out.println("Checking resource: " + source.getType());
            RawInfoCollector rawCollector = RawInfoCollectorFactory.getCollector(source.getType());

            // note: we are collect into for a single router, thus this map at the end should have only 1 entry
            Map<String, String> rawInfo = rawCollector.getRawInformation(source);

            if (Class.forName(source.getTransformerClass()).getInterfaces()[0].getSimpleName().equals("RouterResourceDiscoveryInterface")) {
                vnf = new VNF();
                vnf.setUuid(DeliseConfiguration.getMyUUID());
                vnf.setName(DeliseUtils.getHostName());
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
        SoftwareDefinedGateway gw = null;

        if (!hasSettings()) {
            return null;
        }

        for (InfoSourceSettings.InfoSource source : settings.getSource()) {
            System.out.println("Checking resource: " + source.getType());

            RawInfoCollector rawCollector = RawInfoCollectorFactory.getCollector(source.getType());
            Map<String, String> rawInfo = rawCollector.getRawInformation(source);

            if (Class.forName(source.getTransformerClass()).getInterfaces()[0].getSimpleName().equals("GatewayResourceDiscoveryInterface")) {
                // only create if having class define in source
                gw = new SoftwareDefinedGateway();
                Class<? extends GatewayResourceDiscoveryInterface> tranformClass = (Class<? extends GatewayResourceDiscoveryInterface>) Class.forName(source.getTransformerClass());

                gw.setUuid(DeliseConfiguration.getMyUUID());
                gw.setName(DeliseUtils.getHostName());

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

    /**
     * This get the network information of current machine/container This is the connectivity on the machine of the collector, but we assume that the collector
     * is deployed on gateways, thus it is the information of the gateway.
     *
     * @return
     */
    public static List<CloudConnectivity> getGatewayConnectivity() {
        List<CloudConnectivity> cons = new ArrayList<>();

        try {
            Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
            for (NetworkInterface netint : Collections.list(nets)) {
                if (!netint.isLoopback()) {
                    out.println("No, it is not a loopback. it is: " + netint.getName());
                    Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();

                    // ok, create a new connection, geting IPv4 and MAC
                    String ipv4 = "";
                    for (InetAddress inetAddress : Collections.list(inetAddresses)) {
                        System.out.println("Checking ip: " + inetAddress);
                        if (inetAddress instanceof Inet4Address) {
                            ipv4 = inetAddress.toString();
                            ipv4 = ipv4.substring(ipv4.indexOf("/") + 1);
                        }
                    }
                    StringBuilder sb = new StringBuilder();
                    byte[] mac = netint.getHardwareAddress();
                    for (int i = 0; i < mac.length; i++) {
                        sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
                    }
                    String macStr = sb.toString();  // get MAC
                    CloudConnectivity c = new CloudConnectivity(DeliseConfiguration.getMyUUID(), "Gateway-" + DeliseConfiguration.getMeta().getIp(), "SD Gateway", ipv4, macStr);
                    cons.add(c);

                } else {
                    out.print("Loop back");
                }
                out.printf("\n+++++++++++++\n");

            }
        } catch (SocketException ex) {
            ex.printStackTrace();
        }

        return cons;
    }

    public static void main(String[] args) {
        List<CloudConnectivity> cons = getGatewayConnectivity();
        SoftwareDefinedGateway gw = new SoftwareDefinedGateway();
        gw.getCapabilities().addAll(cons);

        System.out.println("OK, this is the GW");
        System.out.println(gw.toJson());

    }

}
