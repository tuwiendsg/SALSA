package at.ac.tuwien.dsg.cloud.salsa;

import generated.oasis.tosca.TDefinitions;

import java.io.File;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;

import at.ac.tuwien.dsg.cloud.salsa.common.data.SalsaCloudServiceData;
import at.ac.tuwien.dsg.cloud.salsa.common.data.SalsaComponentData;
import at.ac.tuwien.dsg.cloud.salsa.common.data.SalsaComponentReplicaData;
import at.ac.tuwien.dsg.cloud.salsa.common.data.SalsaComponentReplicaData.Properties;
import at.ac.tuwien.dsg.cloud.salsa.common.data.SalsaEntityState;
import at.ac.tuwien.dsg.cloud.salsa.common.data.SalsaTopologyData;
import at.ac.tuwien.dsg.cloud.salsa.common.processes.SalsaCenterConnector;
import at.ac.tuwien.dsg.cloud.salsa.common.processes.SalsaXmlDataProcess;
import at.ac.tuwien.dsg.cloud.salsa.tosca.ToscaStructureQuery;
import at.ac.tuwien.dsg.cloud.salsa.tosca.ToscaXmlProcess;
import at.ac.tuwien.dsg.cloud.salsa.utils.EngineLogger;
import at.ac.tuwien.dsg.cloud.salsa.utils.SalsaConfiguration;
import at.ac.tuwien.dsg.cloud.tosca.extension.SalsaInstanceDescription;

public class TestSalsaData {

	public static void main(String[] args) throws Exception {
		// register service running data
		String deployID = "testTestId";		
		TDefinitions def = ToscaXmlProcess.readToscaFile(TestSalsaData.class
				.getResource("/cassandra.tosca.1.xml").getFile());
		
		
		SalsaCloudServiceData serviceData = new SalsaCloudServiceData();
		serviceData.setId(deployID.toString());
		serviceData.setName(def.getName());
		serviceData.setState(SalsaEntityState.PROLOGUE);
		// build first topo
		SalsaTopologyData topo = new SalsaTopologyData();
		String topoID = ToscaStructureQuery.getFirstServiceTemplate(def).getId();
		topo.setId(topoID);
		serviceData.addComponentTopology(topo);
		// submit new service runtime data
		SalsaXmlDataProcess.writeCloudServiceToFile(serviceData, "/tmp/"
				+ deployID.toString() + ".data");
		
		//submitService("/tmp/testTestId.data");
		SalsaComponentData appnode = new SalsaComponentData();
		SalsaComponentReplicaData node = new SalsaComponentReplicaData();
		node.setId("seed-example");
		node.setName("A sample node");
		node.setReplica(1);
		SalsaInstanceDescription instance = new SalsaInstanceDescription();
		instance.setInstanceId("VM-id");
		instance.setPrivateIp("localhost.example.com");		
		Properties prop = new Properties();
		prop.setAny(instance);
		
		//updateComponent(deployID, topoID, node);
		SalsaCenterConnector con = new SalsaCenterConnector(SalsaConfiguration.getSalsaCenterEndpoint(), deployID, "/tmp", EngineLogger.logger);
		con.addComponentData("7ac87894-7d0b-4466-a767-f3783980b5a3", "casandra", "os1", node);
		
	}
	
	public static void updateComponent(String serviceId, String topologyId, SalsaComponentReplicaData data){
		String url=SalsaConfiguration.getSalsaCenterEndpoint()
				+ "/rest"
				+ "/addcomponent"
				+ "/"+serviceId
				+ "/"+topologyId;			
		try {
			HttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost(url);
			
			JAXBContext jaxbContext = JAXBContext.newInstance(SalsaComponentData.class, SalsaInstanceDescription.class);	// don't need Topology or Service
			Marshaller msl = jaxbContext.createMarshaller();
			msl.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			StringWriter result = new StringWriter();
			msl.marshal(data, result);
			
			
			StringEntity input = new StringEntity(result.toString());
			input.setContentType("application/xml");
			post.setEntity(input);
			
			
			HttpResponse response = client.execute(post);
			if (response.getStatusLine().getStatusCode() != 201) {
				EngineLogger.logger.error("Failed : HTTP error code : "
					+ response.getStatusLine().getStatusCode());
			}
			
			
		} catch (JAXBException e){
			EngineLogger.logger.error("Error when marshalling Component data: "+ data.getId());
		} catch (Exception e){
			EngineLogger.logger.error("Some error when sending component's data");
		}
		
		
	}
	
	
	public static void submitService(String serviceFile){
		
		String url="http://" + SalsaConfiguration.getSalsaCenterIP()
				+ ":" + SalsaConfiguration.getSalsaCenterPort()
				+ SalsaConfiguration.getSalsaCenterPath()
				+ "/submit";					
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(url);
		FileBody uploadfile=new FileBody(new File(serviceFile));
		MultipartEntity reqEntity = new MultipartEntity();
		reqEntity.addPart("file", uploadfile);
		post.setEntity(reqEntity);
		try {
			HttpResponse response = client.execute(post);
			if (response.getStatusLine().getStatusCode() != 200) {
				EngineLogger.logger.error("Server failed to register service: " + new File(serviceFile).getName());				
			}				
		} catch (Exception e){
			EngineLogger.logger.error("Error to submit service: " + new File(serviceFile).getName());
		}					
}

}
