package at.ac.tuwien.dsg.cloud.salsa;

import generated.oasis.tosca.TDefinitions;

import java.io.File;

import javax.ws.rs.core.MediaType;

import at.ac.tuwien.dsg.cloud.salsa.common.processing.SalsaCenterConnector;
import at.ac.tuwien.dsg.cloud.salsa.engine.impl.DeploymentEngineNodeLevel;
import at.ac.tuwien.dsg.cloud.salsa.engine.impl.SalsaToscaDeployer;
import at.ac.tuwien.dsg.cloud.salsa.engine.utils.EngineLogger;
import at.ac.tuwien.dsg.cloud.salsa.tosca.processing.ToscaXmlProcess;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class TestDeployTosca {

	public static void main(String[] args) throws Exception {
		//testDeploySingle();
		//testDeployFull();
		testGenerateWorkload();
	}
	
	public static void testGenerateWorkload(){		
		SalsaCenterConnector con = new SalsaCenterConnector("http://128.130.172.215/:8080/salsa-center-service", "", "", EngineLogger.logger);
		
		String url = "http://128.130.172.215:8080/salsa-engine/rest"
				+ "/services/aecbf519-5f63-4fb5-8087-0c310ec74d0f"
				+ "/topologies/DataMarketAgence"
				+ "/nodes/agence"
				+ "/instance-count/1";
		Client client = Client.create();
		WebResource webResource = client.resource(url);
		ClientResponse response;
		response = webResource.accept(MediaType.TEXT_PLAIN).type(MediaType.TEXT_PLAIN).post(ClientResponse.class, "");
		
		System.out.println("POST done: " + response.getStatus());
				
//		int instanceNumber = 150;
//		for (int i=0; i< instanceNumber; i++){
//			response = webResource.accept(MediaType.TEXT_PLAIN).type(MediaType.TEXT_PLAIN).post(ClientResponse.class, "");
			
//		}
				
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
