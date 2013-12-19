package at.ac.tuwien.dsg.cloud.salsa;

import generated.oasis.tosca.TCapability;
import generated.oasis.tosca.TDefinitions;
import generated.oasis.tosca.TRequirement;

import java.io.IOException;
import java.util.UUID;

import javax.xml.bind.JAXBException;

import at.ac.tuwien.dsg.cloud.data.InstanceDescription;
import at.ac.tuwien.dsg.cloud.salsa.common.model.enums.SalsaCloudProviders;
import at.ac.tuwien.dsg.cloud.salsa.common.processes.SalsaCenterConnector;
import at.ac.tuwien.dsg.cloud.salsa.engine.utils.EngineLogger;
import at.ac.tuwien.dsg.cloud.salsa.engine.utils.SalsaConfiguration;
import at.ac.tuwien.dsg.cloud.salsa.tosca.extension.SalsaCapabilityString;
import at.ac.tuwien.dsg.cloud.salsa.tosca.extension.SalsaInstanceDescription;
import at.ac.tuwien.dsg.cloud.salsa.tosca.processing.ToscaStructureQuery;
import at.ac.tuwien.dsg.cloud.salsa.tosca.processing.ToscaXmlProcess;
import at.ac.tuwien.dsg.cloud.stratuslab.services.impl.StratusLabConnector;

public class TestConnector {
	public static void main(String[] args) throws Exception {
		// testUserData();
		// testQuery();
		testAddCapaAndProperties();
	}

	private static void testAddCapaAndProperties() {
		SalsaCenterConnector con = new SalsaCenterConnector(
				"http://128.130.172.215:8080/salsa-center-services",
				"8e9ed272-d9ae-4ac8-a976-4a4540ed4216", "", EngineLogger.logger);
//		SalsaReplicaRelationship rel = new SalsaReplicaRelationship("os1", 0, "seed1", 0);
//		rel.setType(SalsaRelationshipType.HOSTON);
//		con.addRelationship("casandra", rel);
		
		SalsaCapabilityString capa = new SalsaCapabilityString("seedCap_IP_test", "10.0.0.41");
		con.updateReplicaCapability("casandra", "seed1", 0, capa);
		
//		SalsaInstanceDescription instance = new SalsaInstanceDescription(SalsaCloudProviders.OPENSTACK, "instanceIdTestId");
//		instance.setPrivateIp("10.0.0.4");
//		instance.setPublicIp("123.130.172.222");
//		con.updateReplicaProperty("casandra", "seed1", 0, instance);
	}

	private static void testQuery() throws IOException, JAXBException {
		TDefinitions def = ToscaXmlProcess
				.readToscaFile("/tmp/33052803-d3de-4239-8d6a-105639366950");
		TRequirement req = (TRequirement) ToscaStructureQuery
				.getRequirementOrCapabilityById("seed_os_req", def);
		TCapability cap = ToscaStructureQuery.getCapabilitySuitsRequirement(
				req, def);
		System.out.println(cap.getId());
	}

	private static void testUserData() throws IOException, JAXBException {
		// TDefinitions def =
		// ToscaXmlProcess.readToscaFile(TestDeployTosca.class.getResource("/cassandra.tosca.xml").getFile());
		TDefinitions def = ToscaXmlProcess
				.readToscaFile("/tmp/33052803-d3de-4239-8d6a-105639366950");
		String nodeId = "os1";
		UUID deployID = UUID.fromString("33052803-d3de-4239-8d6a-105639366950");

		// String userData = DeploymentEngineNodeLevel.prepareUserData(def,
		// nodeId, deployID.toString());
		// System.out.println(userData);
	}

	private static void testConfig() {
		System.out.println(SalsaConfiguration.getSalsaCenterEndpoint());
	}

	private static void teststratus() {
		StratusLabConnector sc = new StratusLabConnector(EngineLogger.logger);
		InstanceDescription id = sc.getInstanceDescriptionByID("629");
		System.out.println(id.getInstanceId());
		System.out.println(id.getPrivateIp());
		System.out.println(id.getState());
		System.out.println(id.getReplicaFQN());
	}
}
