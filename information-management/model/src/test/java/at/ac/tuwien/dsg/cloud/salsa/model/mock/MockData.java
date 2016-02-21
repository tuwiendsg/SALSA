/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.salsa.model.mock;

import at.ac.tuwien.dsg.cloud.salsa.model.VirtualComputingResource.Capability.Capability;
import at.ac.tuwien.dsg.cloud.salsa.model.VirtualComputingResource.Capability.Concrete.CloudConnectivity;
import at.ac.tuwien.dsg.cloud.salsa.model.VirtualComputingResource.Capability.Concrete.ControlPoint;
import at.ac.tuwien.dsg.cloud.salsa.model.VirtualComputingResource.Capability.Concrete.ControlPoint.InvokeProtocol;
import at.ac.tuwien.dsg.cloud.salsa.model.VirtualComputingResource.Capability.Concrete.DataPoint;
import at.ac.tuwien.dsg.cloud.salsa.model.VirtualComputingResource.DataStream.DataStream;
import at.ac.tuwien.dsg.cloud.salsa.model.VirtualComputingResource.DataStream.ObservedProperty;
import at.ac.tuwien.dsg.cloud.salsa.model.VirtualComputingResource.SoftwareDefineGateway;
import at.ac.tuwien.dsg.cloud.salsa.model.VirtualNetworkResource.AccessPoint;
import at.ac.tuwien.dsg.cloud.salsa.model.VirtualNetworkResource.NetworkService;
import at.ac.tuwien.dsg.cloud.salsa.model.VirtualNetworkResource.VNF;
import at.ac.tuwien.dsg.cloud.salsa.model.VirtualNetworkResource.VNFForwardGraph;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author hungld
 */
public class MockData {

    public static void main(String[] arg) throws Exception {
        SoftwareDefineGateway gateway = new SoftwareDefineGateway();
        
        Capability control1 = new ControlPoint("changeSensorRate", "change data rate", InvokeProtocol.POST, "http://128.130.172.216:8080/salsa-engine/rest/services/IoTSensors/nodes/SensorUnit/instances/1/action_queue/changeRate/parameters/{1}", null);
        Capability control2 = new ControlPoint("setProtocolMQTT", "change to MQTT mode", InvokeProtocol.POST, "http://128.130.172.216:8080/salsa-engine/rest/services/IoTSensors/nodes/SensorUnit/instances/1/action_queue/setProtocolMQTT", null);
        Capability control3 = new ControlPoint("setProtocolDRY", "change DRY mode", InvokeProtocol.POST, "http://128.130.172.216:8080/salsa-engine/rest/services/IoTSensors/nodes/SensorUnit/instances/1/action_queue/setProtocolDRY", null);
        gateway.getCapabilities().addAll(Arrays.asList(control1, control2, control3));

        CloudConnectivity connectivity1 = new CloudConnectivity("3G", "3G connection", "172.17.0.150", "");
        CloudConnectivity connectivity2 = new CloudConnectivity("WIFI", "WIFI connection", "10.32.0.2", "");
        gateway.getCapabilities().add(connectivity1);
        gateway.getCapabilities().add(connectivity2);

        Capability data1 = new DataPoint("temperature1", "temperature of room1", "temperature", "C", "5");
        Capability data2 = new DataPoint("humidity1", "humidity of room1", "humidity", "%", "60");        
        gateway.getCapabilities().add(data1);
        gateway.getCapabilities().add(data2);
        
        gateway.getMeta().put("model", "G2021");
        gateway.getMeta().put("owner", "tuwien");
        gateway.getMeta().put("location", "building1");
        gateway.setName("Gateway1");
        gateway.setUuid("9321a1c2-a622-4b4c-ba3d-f51e8af79460");
        
        
        ObjectMapper mapper = new ObjectMapper();
        System.out.println(mapper.writeValueAsString(gateway));

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

        
        //System.out.println(mapper.writeValueAsString(gateway));
        System.out.println(mapper.writeValueAsString(network));

    }
}
