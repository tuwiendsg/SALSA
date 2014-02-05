package at.ac.tuwien.dsg.cloud.salsa;

import generated.oasis.tosca.TDefinitions;

import java.io.File;

import at.ac.tuwien.dsg.cloud.salsa.engine.impl.SalsaToscaDeployer;
import at.ac.tuwien.dsg.cloud.salsa.engine.impl.ToscaEnricher;
import at.ac.tuwien.dsg.cloud.salsa.knowledge.process.KnowledgeGraph;
import at.ac.tuwien.dsg.cloud.salsa.tosca.processing.ToscaXmlProcess;

public class TestDeployTosca {

	public static void main(String[] args) {
		try {
			TDefinitions def = ToscaXmlProcess
					.readToscaFile(TestDeployTosca.class.getResource(
							"/dataMarketAgence.tosca.xml").getFile());
			File configFile = new File(TestDeployTosca.class.getResource("/cloudUserParameters.ini").getFile());
			ToscaEnricher enrich = new ToscaEnricher(def, new KnowledgeGraph("/tmp/salsa_knowledge"));
			//enrich.toXMLFile("/tmp/toscaFull.xml");
			//SalsaToscaDeployer.deployNewService(enrich.getToscaDef());
			
			enrich.createComplexRelationship(def);
			ToscaXmlProcess.writeToscaDefinitionToFile(def, "/tmp/toscaFull.xml");
			
			SalsaToscaDeployer deployer = new SalsaToscaDeployer(configFile);
			deployer.deployNewService(def);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void testDeploySingle(){
		
	}
	
}
