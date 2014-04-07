package at.ac.tuwien.dsg.cloud.salsa;

import generated.oasis.tosca.TDefinitions;

import java.io.File;

import at.ac.tuwien.dsg.cloud.salsa.engine.impl.DeploymentEngineNodeLevel;
import at.ac.tuwien.dsg.cloud.salsa.engine.impl.SalsaToscaDeployer;
import at.ac.tuwien.dsg.cloud.salsa.engine.impl.ToscaEnricher;
import at.ac.tuwien.dsg.cloud.salsa.knowledge.architecturerefine.process.KnowledgeGraph;
import at.ac.tuwien.dsg.cloud.salsa.tosca.processing.ToscaXmlProcess;

public class TestDeployTosca {

	public static void main(String[] args) throws Exception {
		testDeploySingle();
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
		String serviceid="f3596f7a-4644-46e8-ab95-a377f22dffb8";
		TDefinitions def = ToscaXmlProcess
				.readToscaFile(TestDeployTosca.class.getResource(
						"/dataMarketTosca/dataMarketAgence_test_DSG_Subscribe.tosca.xml").getFile());
		
		File configFile = new File(TestDeployTosca.class.getResource("/cloudUserParameters.ini").getFile());
		DeploymentEngineNodeLevel deployer = new DeploymentEngineNodeLevel(configFile);
		deployer.deployVMNode(serviceid, "DataMarketAgence", "agence_os", 1, def);
		
	}
	
}
