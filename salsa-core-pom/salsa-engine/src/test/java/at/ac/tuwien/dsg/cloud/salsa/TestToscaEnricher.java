/*
 * Copyright (c) 2013 Technische Universitat Wien (TUW), Distributed Systems Group. http://dsg.tuwien.ac.at
 *
 * This work was partially supported by the European Commission in terms of the CELAR FP7 project (FP7-ICT-2011-8 #317790), http://www.celarcloud.eu/
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package at.ac.tuwien.dsg.cloud.salsa;

import at.ac.tuwien.dsg.cloud.salsa.engine.exceptions.SalsaException;
import generated.oasis.tosca.TDefinitions;
import generated.oasis.tosca.TEntityTemplate.Properties;
import generated.oasis.tosca.TNodeTemplate;
import generated.oasis.tosca.TServiceTemplate;
import generated.oasis.tosca.TTopologyTemplate;
import at.ac.tuwien.dsg.cloud.salsa.engine.smartdeployment.SALSA.ToscaEnricherSALSA;
import at.ac.tuwien.dsg.cloud.salsa.tosca.extension.SalsaInstanceDescription_VM;
import at.ac.tuwien.dsg.cloud.salsa.tosca.extension.SalsaMappingProperties;
import at.ac.tuwien.dsg.cloud.salsa.engine.dataprocessing.ToscaXmlProcess;
import javax.xml.bind.JAXBException;

public class TestToscaEnricher {

	public static void main(String[] args) {
		try {
//		TDefinitions def = ToscaXmlProcess
//				.readToscaFile(TestDeployTosca.class.getResource(
//						"/cassandra_old/tosca_Cassandra_example_fakescripts.xml").getFile());
		//TDefinitions def = ToscaXmlProcess.readToscaFile("/home/hungld/test/DAASPilot/tosca_DaaS_high_level_with_SYBL.xml");
		//TDefinitions def = ToscaXmlProcess.readToscaFile("/home/hungld/test/IoTSensor/onlyIoT_multiple_topologies_wiring.xml");
//		TDefinitions def = ToscaXmlProcess.readToscaFile("/home/hungld/test/tosca/4-DeployWithTomcat.xml");
//		System.out.println(ToscaXmlProcess.writeToscaDefinitionToXML(def));
				
		// ENRICH
		
//		ToscaEnricherSALSA enricher = new ToscaEnricherSALSA(def);
//		enricher.enrichHighLevelTosca();
		
//		

//		
//		ToscaXmlProcess.writeToscaDefinitionToFile(def, "/tmp/salsa/enriched.xml");
		
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	private static void checkSalsaMapProperties() throws Exception{
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
