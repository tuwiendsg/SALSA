/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.salsa.model.mock;

import at.ac.tuwien.dsg.cloud.salsa.model.VirtualComputingResource.Capability.Capability;
import at.ac.tuwien.dsg.cloud.salsa.model.VirtualComputingResource.DataStream.DataStream;
import at.ac.tuwien.dsg.cloud.salsa.model.VirtualComputingResource.DataStream.ObservedProperty;
import at.ac.tuwien.dsg.cloud.salsa.model.VirtualComputingResource.SoftwareDefineGateway;
import at.ac.tuwien.dsg.cloud.salsa.model.VirtualNetworkResource.AccessPoint;
import at.ac.tuwien.dsg.cloud.salsa.model.VirtualNetworkResource.NetworkService;
import at.ac.tuwien.dsg.cloud.salsa.model.VirtualNetworkResource.VNF;
import at.ac.tuwien.dsg.cloud.salsa.model.VirtualNetworkResource.VNFForwardGraph;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;

/**
 *
 * @author hungld
 */
public class MockData {

    public static void main(String[] arg) throws Exception {
        SoftwareDefineGateway gateway = new SoftwareDefineGateway();

        Capability capa1 = new Capability("changeSensorRate", "SALSA", Capability.InvokeProtocol.POST, "http://128.130.172.216:8080/salsa-engine/rest/services/IoTSensors/nodes/SensorUnit/instances/1/action_queue/changeRate/parameters/{1}", "");
        Capability capa2 = new Capability("setProtocolMQTT", "SALSA", Capability.InvokeProtocol.POST, "http://128.130.172.216:8080/salsa-engine/rest/services/IoTSensors/nodes/SensorUnit/instances/1/action_queue/setProtocolMQTT", "");
        Capability capa3 = new Capability("setProtocolDRY", "SALSA", Capability.InvokeProtocol.POST, "http://128.130.172.216:8080/salsa-engine/rest/services/IoTSensors/nodes/SensorUnit/instances/1/action_queue/setProtocolDRY", "");

        gateway.setCapabilities(Arrays.asList(capa1, capa2, capa3));

        AccessPoint accessPoint = new AccessPoint("172.17.0.150");
        gateway.setDefaultGateway(accessPoint);

        DataStream data1 = new DataStream(new ObservedProperty("GPS", "lat/long"), DataStream.BUFFER_TYPE.MQTT, "tcp://172.17.0.140:1883/topic", "The location of the device");
        gateway.setDataStreams(Arrays.asList(data1));

        gateway.getMeta().put("model", "G2021");
        gateway.setName("Gateway1");
        gateway.setUuid("9321a1c2-a622-4b4c-ba3d-f51e8af79460");

        // network
        NetworkService network = new NetworkService();        
        
        network.getAccessPoints().add(new AccessPoint("172.17.0.150"));
        network.getAccessPoints().add(new AccessPoint("172.17.0.152"));

        VNF vnf1 = new VNF("weave-router1", "IGRP", new AccessPoint("172.17.0.150"));
        VNF vnf2 = new VNF("weave-router2", "IGRP", new AccessPoint("172.17.0.151"));
        VNF vnf3 = new VNF("weave-router3", "IGRP", new AccessPoint("172.17.0.152"));
        VNFForwardGraph graph = new VNFForwardGraph();
        graph.setNodes(Arrays.asList(vnf1, vnf2, vnf3));
        graph.getLinks().add(new VNFForwardGraph.VNFLink("weave-router1", "weave-router2", "100Mbps"));
        graph.getLinks().add(new VNFForwardGraph.VNFLink("weave-router2", "weave-router3", "500Mbps"));

        network.setVnfForwardGraphs(Arrays.asList(graph));

        ObjectMapper mapper = new ObjectMapper();
        //System.out.println(mapper.writeValueAsString(gateway));
        System.out.println(mapper.writeValueAsString(network));

    }
}
