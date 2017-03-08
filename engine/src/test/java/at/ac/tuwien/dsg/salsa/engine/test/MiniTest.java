/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.salsa.engine.test;

import at.ac.tuwien.dsg.salsa.model.CloudService;

/**
 *
 * @author hungld
 */
public class MiniTest {

    public static void main(String[] arges) {
        String json = "{\"name\":\"test\",\"uuid\":\"96be380b-2a0f-413e-a489-4b38d1a3a1c6\",\"topologies\":[{\"uuid\":\"a57a185a-97df-4ae2-ba54-6e9b84b2f209\",\"name\":\"SensorTopology\",\"units\":[{\"uuid\":\"cd517aa9-ceca-48f6-a509-5c311e204b7a\",\"name\":\"sensor\",\"type\":\"software\",\"instances\":[{\"uuid\":\"a2ba0cdb-704b-4b5b-87a0-acca3c07fef2\",\"index\":0,\"serviceUnitUuid\":\"cd517aa9-ceca-48f6-a509-5c311e204b7a\",\"identification\":null,\"state\":\"DEPLOYED\",\"hostedInstanceIndex\":0}],\"idCounter\":0,\"min\":1,\"max\":50,\"reference\":null,\"artifacts\":[{\"name\":\"Artifact_271e6855-84bc-447b-a659-4e07c54003b2\",\"artifactType\":\"shcont\",\"reference\":\"http://54.186.18.123/repo/files/sdsensor/runsensor.sh\",\"repoType\":\"HTTP\",\"tags\":null},{\"name\":\"Artifact_9d7fa492-08ef-4536-acf9-55b31c774790\",\"artifactType\":\"misc\",\"reference\":\"http://54.186.18.123/repo/files/sdsensor/sensor-artifacts.tar.gz\",\"repoType\":\"HTTP\",\"tags\":null},{\"name\":\"contractTemplate\",\"artifactType\":\"contract\",\"reference\":\"http://54.186.18.123/repo/files/contract/Contract.json\",\"repoType\":\"HTTP\",\"tags\":null}],\"capabilities\":null,\"hostedUnitName\":\"PersonalMachine\",\"connecttoUnitName\":[],\"topologyUuid\":null,\"state\":\"UNDEPLOYED\",\"properties\":null,\"mainArtifactType\":\"shcont\"},{\"uuid\":\"471ea110-787f-48d2-bb86-83dd962e95a0\",\"name\":\"PersonalMachine\",\"type\":\"os\",\"instances\":[{\"uuid\":\"0a0a4152-45fc-40dc-973e-37c87507a9af\",\"index\":0,\"serviceUnitUuid\":\"471ea110-787f-48d2-bb86-83dd962e95a0\",\"identification\":null,\"state\":\"DEPLOYED\",\"hostedInstanceIndex\":0}],\"idCounter\":0,\"min\":1,\"max\":2147483647,\"reference\":null,\"artifacts\":null,\"capabilities\":null,\"hostedUnitName\":null,\"connecttoUnitName\":[],\"topologyUuid\":null,\"state\":\"UNDEPLOYED\",\"properties\":\"{\\\"baseImage\\\":\\\"\\\",\\\"packages\\\":\\\"\\\",\\\"provider\\\":\\\"localhost\\\",\\\"instanceType\\\":\\\"\\\"}\",\"mainArtifactType\":null}],\"relationships\":null,\"cloudServiceUuid\":null,\"state\":null}],\"state\":\"UNDEPLOYED\"}";
        CloudService service = CloudService.fromJson(json);
        System.out.println("Service: " + service.getName());
    }
}
