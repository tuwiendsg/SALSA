package at.ac.tuwien.dsg.cloud.salsa;

import generated.oasis.tosca.TDefinitions;

import java.io.File;

import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.CloudService;
import at.ac.tuwien.dsg.cloud.salsa.engine.impl.DeploymentEngineNodeLevel;
import at.ac.tuwien.dsg.cloud.salsa.engine.impl.SalsaToscaDeployer;
import at.ac.tuwien.dsg.cloud.salsa.tosca.processing.ToscaXmlProcess;

public class TestDeployTosca {

	public static void main(String[] args) throws Exception {
		//testDeploySingle();
		//testDeployFull();
		testConvertTosca();
	}
	
	public static void testConvertTosca() throws Exception{
		//TDefinitions def = ToscaXmlProcess.readToscaFile("/home/hungld/test/DAASPilot/tosca_DaaS_example_low_level_1_topology_DOCKER.xml");
		TDefinitions def = ToscaXmlProcess.readToscaFile("/tmp/salsa/enriched.xml");
		//SalsaToscaDeployer td = new SalsaToscaDeployer(new File("/etc//etc/cloudUserParameters.ini"));
		CloudService service = SalsaToscaDeployer.buildRuntimeDataFromTosca(def);
		System.out.println(service.getId());
	}
	
	public static void testDeployFull() throws Exception{
		TDefinitions def = ToscaXmlProcess
				.readToscaFile("/home/hungld/test/DAASPilot/tosca_Cassandra_example_fakescripts.xml");
		SalsaToscaDeployer td = new SalsaToscaDeployer(new File("/etc//etc/cloudUserParameters.ini"));
		td.deployNewService(def, "TestService");
	}
	
	public static void testHighLevel() throws Exception {
		TDefinitions def = ToscaXmlProcess
				.readToscaFile(TestDeployTosca.class.getResource(
						"/dataMarketTosca/dataMarketAgence_test_DSG_Subscribe.tosca.xml").getFile());
		File configFile = new File(TestDeployTosca.class.getResource("/cloudUserParameters.ini").getFile());
		//ToscaEnricher enrich = new ToscaEnricher(def, new KnowledgeGraph("/tmp/salsa_knowledge"));
		//enrich.toXMLFile("/tmp/toscaFull.xml");
		//SalsaToscaDeployer.deployNewService(enrich.getToscaDef());
		
		//enrich.createComplexRelationship(def);
		//ToscaXmlProcess.writeToscaDefinitionToFile(def, "/tmp/toscaFull.xml");
		
		SalsaToscaDeployer deployer = new SalsaToscaDeployer(configFile);
		//deployer.deployNewService(def);
	}
	
	public static void testDeploySingle() throws Exception{
		String serviceid="9d6b4452-5b4b-4663-b0c6-0af03a9540c5";
		TDefinitions def = ToscaXmlProcess
				.readToscaFile("/home/hungld/test/DAASPilot/tosca_Cassandra_example_fakescripts.xml");
		
		File configFile = new File("/etc/cloudUserParameters.ini");
		DeploymentEngineNodeLevel deployer = new DeploymentEngineNodeLevel(configFile);		
		deployer.deployVMNode(serviceid, "DaaSService", "OS_Datanode", 1, def);
		
	}
	
}
