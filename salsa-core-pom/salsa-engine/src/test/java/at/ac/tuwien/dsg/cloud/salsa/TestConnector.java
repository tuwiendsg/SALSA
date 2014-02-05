package at.ac.tuwien.dsg.cloud.salsa;

import generated.oasis.tosca.TCapability;
import generated.oasis.tosca.TDefinitions;
import generated.oasis.tosca.TRequirement;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;

import javax.xml.bind.JAXBException;

import at.ac.tuwien.dsg.cloud.salsa.cloud_connector.InstanceDescription;
import at.ac.tuwien.dsg.cloud.salsa.cloud_connector.ServiceDeployerException;
import at.ac.tuwien.dsg.cloud.salsa.cloud_connector.multiclouds.ConnectorsEnum;
import at.ac.tuwien.dsg.cloud.salsa.cloud_connector.multiclouds.MultiCloudConnector;
import at.ac.tuwien.dsg.cloud.salsa.cloud_connector.openstack.JEC2ClientFactory;
import at.ac.tuwien.dsg.cloud.salsa.cloud_connector.openstack.OpenStackTypica;
import at.ac.tuwien.dsg.cloud.salsa.common.processing.SalsaCenterConnector;
import at.ac.tuwien.dsg.cloud.salsa.engine.utils.EngineLogger;
import at.ac.tuwien.dsg.cloud.salsa.engine.utils.SalsaConfiguration;
import at.ac.tuwien.dsg.cloud.salsa.tosca.extension.SalsaCapabilityString;
import at.ac.tuwien.dsg.cloud.salsa.tosca.processing.ToscaStructureQuery;
import at.ac.tuwien.dsg.cloud.salsa.tosca.processing.ToscaXmlProcess;

import com.xerox.amazonws.ec2.InstanceType;

public class TestConnector {
	public static void main(String[] args) throws Exception {
		// testUserData();
		// testQuery();
		//testAddCapaAndProperties();
		//teststratus();
		testMultiCloudConnector();		
	}

	private static void testMultiCloudConnector(){
		String conf = TestConnector.class.getResource("/cloudUserParameters.ini").getFile();
		File f = new File(conf);
		MultiCloudConnector mcc = new MultiCloudConnector(EngineLogger.logger, f);
		mcc.getCloudInplementation(ConnectorsEnum.LAL_STRATUSLAB);		
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

	
	
	private static void testDsgOpenStack() throws ServiceDeployerException {
		int socketTimeout = 5000;
		int connectionTimeout = 3000;
		int connectionManagerTimeout = 6000;
		int maxRetries = 10;
		int maxConnections = 10;
		JEC2ClientFactory cf = new JEC2ClientFactory(
				EngineLogger.logger,
				"98fc6ca6893343039aa286f374266aae",
				"4f8df10b2491426485b30712958b98e6",				
				"openstack.infosys.tuwien.ac.at",
				8773,
				socketTimeout, connectionTimeout,
				connectionManagerTimeout, maxRetries, maxConnections);
		long retryDelayMillis = 5000;
		int deployMaxRetries = 24;
		long deployWaitMillis = 10000;
		
		OpenStackTypica os = new OpenStackTypica(EngineLogger.logger, cf, maxRetries,
				retryDelayMillis, deployMaxRetries, deployWaitMillis);
		
	
		String newInstance = os.launchInstance("ami-00000163", Arrays.asList("default"), "salsa", "test=1", InstanceType.DEFAULT, 1, 1);
		InstanceDescription id = os.getInstanceDescriptionByID(newInstance);
		System.out.println(id.getInstanceId());
		System.out.println(id.getPrivateIp());
		System.out.println(id.getState());
	}
	
}
