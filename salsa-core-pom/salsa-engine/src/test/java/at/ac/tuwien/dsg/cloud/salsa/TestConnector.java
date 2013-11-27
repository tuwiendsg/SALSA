package at.ac.tuwien.dsg.cloud.salsa;

import generated.oasis.tosca.TCapability;
import generated.oasis.tosca.TDefinitions;
import generated.oasis.tosca.TRequirement;

import java.io.IOException;
import java.util.UUID;

import javax.xml.bind.JAXBException;

import at.ac.tuwien.dsg.cloud.data.InstanceDescription;
import at.ac.tuwien.dsg.cloud.salsa.service.impl.DeploymentEngineNodeLevel;
import at.ac.tuwien.dsg.cloud.salsa.tosca.ToscaStructureQuery;
import at.ac.tuwien.dsg.cloud.salsa.tosca.ToscaXmlProcess;
import at.ac.tuwien.dsg.cloud.salsa.utils.EngineLogger;
import at.ac.tuwien.dsg.cloud.salsa.utils.SalsaConfiguration;
import at.ac.tuwien.dsg.cloud.stratuslab.services.impl.StratusLabConnector;

public class TestConnector {
	public static void main(String[] args) throws Exception {
		//testUserData();
		//testQuery();
		
		DeploymentEngineNodeLevel engine = new DeploymentEngineNodeLevel();
		engine.submitService("/tmp/481faa50-74c8-4339-91c7-6db080b96cf1");
		
	}
	
	private static void testQuery() throws IOException, JAXBException {
		TDefinitions def = ToscaXmlProcess.readToscaFile("/tmp/33052803-d3de-4239-8d6a-105639366950");
		TRequirement req = (TRequirement)ToscaStructureQuery.getRequirementOrCapabilityById("seed_os_req", def);
		TCapability cap = ToscaStructureQuery.getCapabilitySuitsRequirement(req, def);
		System.out.println(cap.getId());
	}
	
	private static void testUserData() throws IOException, JAXBException{		
		//TDefinitions def = ToscaXmlProcess.readToscaFile(TestDeployTosca.class.getResource("/cassandra.tosca.xml").getFile());
		TDefinitions def = ToscaXmlProcess.readToscaFile("/tmp/33052803-d3de-4239-8d6a-105639366950");
		String nodeId = "os1";
		UUID deployID = UUID.fromString("33052803-d3de-4239-8d6a-105639366950");
				
//		String userData = DeploymentEngineNodeLevel.prepareUserData(def, nodeId, deployID.toString());
//		System.out.println(userData);
	}
	
	private static void testConfig(){
		System.out.println(SalsaConfiguration.getSalsaCenterIP());
		System.out.println(SalsaConfiguration.getSalsaCenterStoragePath());
		System.out.println(SalsaConfiguration.getSSHKeyForCenter());
	}
	
	
	private static void teststratus(){
		StratusLabConnector sc = new StratusLabConnector(EngineLogger.logger);
		InstanceDescription id = sc.getInstanceDescriptionByID("629");
		System.out.println(id.getInstanceId());
		System.out.println(id.getPrivateIp());
		System.out.println(id.getState());
		System.out.println(id.getReplicaFQN());
	}
}
