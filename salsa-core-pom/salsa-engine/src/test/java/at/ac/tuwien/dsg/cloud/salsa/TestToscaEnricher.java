package at.ac.tuwien.dsg.cloud.salsa;

import generated.oasis.tosca.TDefinitions;
import generated.oasis.tosca.TEntityTemplate.Properties;
import generated.oasis.tosca.TNodeTemplate;
import generated.oasis.tosca.TServiceTemplate;
import generated.oasis.tosca.TTopologyTemplate;
import at.ac.tuwien.dsg.cloud.salsa.engine.smartdeployment.ToscaEnricher;
import at.ac.tuwien.dsg.cloud.salsa.tosca.extension.SalsaInstanceDescription_VM;
import at.ac.tuwien.dsg.cloud.salsa.tosca.extension.SalsaMappingProperties;
import at.ac.tuwien.dsg.cloud.salsa.tosca.processing.ToscaXmlProcess;

public class TestToscaEnricher {

	public static void main(String[] args) {
		try {
//		TDefinitions def = ToscaXmlProcess
//				.readToscaFile(TestDeployTosca.class.getResource(
//						"/cassandra_old/tosca_Cassandra_example_fakescripts.xml").getFile());
		//TDefinitions def = ToscaXmlProcess.readToscaFile("/home/hungld/test/DAASPilot/tosca_DaaS_high_level_with_SYBL.xml");
		//TDefinitions def = ToscaXmlProcess.readToscaFile("/home/hungld/test/IoTSensor/onlyIoT_multiple_topologies_wiring.xml");
		TDefinitions def = ToscaXmlProcess.readToscaFile("/home/hungld/test/tosca/4-DeployWithTomcat.xml");
		System.out.println(ToscaXmlProcess.writeToscaDefinitionToXML(def));
				
		// ENRICH
		
		ToscaEnricher enricher = new ToscaEnricher(def);
		enricher.enrichHighLevelTosca();
		
//		

//		
		ToscaXmlProcess.writeToscaDefinitionToFile(def, "/tmp/salsa/enriched.xml");
		
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
