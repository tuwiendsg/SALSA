package at.ac.tuwien.dsg.cloud.salsa;

import generated.oasis.tosca.TDefinitions;
import at.ac.tuwien.dsg.cloud.salsa.engine.impl.SalsaToscaDeployer;
import at.ac.tuwien.dsg.cloud.salsa.engine.impl.ToscaEnricher;
import at.ac.tuwien.dsg.cloud.salsa.knowledge.process.KnowledgeGraph;
import at.ac.tuwien.dsg.cloud.salsa.tosca.processing.ToscaXmlProcess;

public class TestDeployTosca {

	public static void main(String[] args) {
		try {
			TDefinitions def = ToscaXmlProcess
					.readToscaFile(TestDeployTosca.class.getResource(
							"/cassandra.tosca.highlevel.xml").getFile());
			
			ToscaEnricher enrich = new ToscaEnricher(def, new KnowledgeGraph("/tmp/salsa_knowledge"));
			
			SalsaToscaDeployer.deployNewService(enrich.getToscaDef());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
