package at.ac.tuwien.dsg.cloud.salsa;

import generated.oasis.tosca.TDefinitions;
import generated.oasis.tosca.TEntityTemplate.Properties;
import generated.oasis.tosca.TNodeTemplate;
import generated.oasis.tosca.TServiceTemplate;
import generated.oasis.tosca.TTopologyTemplate;
import at.ac.tuwien.dsg.cloud.salsa.engine.impl.ToscaEnricher;
import at.ac.tuwien.dsg.cloud.salsa.knowledge.architecturerefine.process.KnowledgeGraph;
import at.ac.tuwien.dsg.cloud.salsa.tosca.extension.SalsaInstanceDescription_VM;
import at.ac.tuwien.dsg.cloud.salsa.tosca.extension.SalsaMappingProperties;
import at.ac.tuwien.dsg.cloud.salsa.tosca.processing.ToscaXmlProcess;

public class TestToscaEnricher {

	public static void main(String[] args) {
		try {
		TDefinitions def = ToscaXmlProcess
				.readToscaFile(TestDeployTosca.class.getResource(
						"/cassandra.tosca.highlevel.1.xml").getFile());

		System.out.println(ToscaXmlProcess.writeToscaDefinitionToXML(def));
				
//		// ENRICH
		KnowledgeGraph kgraph = new KnowledgeGraph("/tmp/salsa_knowledge");
		ToscaEnricher enrich = new ToscaEnricher(def, kgraph);
		//enrich.enrichHighLevelTosca();
		enrich.enrichHighLevelTosca();

		enrich.toXMLFile("/tmp/tosca.enrich.xml");
		
		//checkSalsaMapProperties();
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	private static void checkSalsaMapProperties(){
		TDefinitions def = new TDefinitions();
		TServiceTemplate ser = new TServiceTemplate();
		def.getServiceTemplateOrNodeTypeOrNodeTypeImplementation().add(ser);
		TNodeTemplate node = new TNodeTemplate();
		ser.setTopologyTemplate(new TTopologyTemplate());
		ser.getTopologyTemplate().getNodeTemplateOrRelationshipTemplate().add(node);
		
		node.setId("testNode");
		Properties prop = new Properties();
		
		SalsaInstanceDescription_VM ins = new SalsaInstanceDescription_VM();
		ins.setBaseImage("testBaseImage");
		
		SalsaMappingProperties maps = new SalsaMappingProperties();
		maps.put("os", "name", "Linux");
		maps.put("os", "arch", "x64");
		prop.setAny(maps);
		node.setProperties(prop);
		ToscaXmlProcess.writeToscaElementToFile(def, "/tmp/testTosca.xml");
	}

}
