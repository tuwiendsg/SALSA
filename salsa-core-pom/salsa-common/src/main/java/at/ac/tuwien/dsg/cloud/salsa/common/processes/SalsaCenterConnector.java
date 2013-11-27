package at.ac.tuwien.dsg.cloud.salsa.common.processes;

import generated.oasis.tosca.TCapability;
import generated.oasis.tosca.TDefinitions;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;

import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;

import at.ac.tuwien.dsg.cloud.salsa.common.data.SalsaComponentReplicaData;
import at.ac.tuwien.dsg.cloud.salsa.common.data.SalsaEntityState;
import at.ac.tuwien.dsg.cloud.salsa.tosca.ToscaStructureQuery;
import at.ac.tuwien.dsg.cloud.salsa.tosca.ToscaXmlProcess;
import at.ac.tuwien.dsg.cloud.tosca.extension.SalsaInstanceDescription;
import at.ac.tuwien.dsg.cloud.tosca.extension.ToscaCapabilityString;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class SalsaCenterConnector {
	Logger logger;
	String centerServiceEndpoint;
	String serviceId;
	String workingDir;

	/**
	 * Create a connector to Salsa service
	 * @param centerServiceEndpoint The endpoint. E.g: <ip>:<port>/<path>
	 * @param serviceId The deployment ID which connected to
	 * @param storageFolder temporary folder to storage service files
	 * @param logger Logger
	 */
	public SalsaCenterConnector(String centerServiceEndpoint, String serviceId, String workingDir, Logger logger) {
		this.centerServiceEndpoint = centerServiceEndpoint+"/rest";
		this.logger = logger;
		this.serviceId = serviceId;
		this.workingDir = workingDir;
	}

	public void submitService(String serviceFile) {

		String url = centerServiceEndpoint + "/submit";
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(url);
		FileBody uploadfile = new FileBody(new File(serviceFile));
		MultipartEntity reqEntity = new MultipartEntity();
		reqEntity.addPart("file", uploadfile);
		post.setEntity(reqEntity);
		try {
			HttpResponse response = client.execute(post);
			if (response.getStatusLine().getStatusCode() != 200) {
				logger.error("Server failed to register service: "
						+ new File(serviceFile).getName());
			}
		} catch (Exception e) {
			logger.error("Error to submit service: "
					+ new File(serviceFile).getName());
		}
	}
	
	/*
	 * Set Capability on remote center
	 */
	public void setCapability(String capaId, String value,
			TDefinitions def) {
		// Send the Capa to Salsa center
		Client client = Client.create();
		String serverURL = centerServiceEndpoint
				+ "/update/capability/" + serviceId + "/" + capaId + "/"
				+ value;
		logger.debug("Querrying: "+serverURL);
		WebResource webRes = client.resource(serverURL);
		ClientResponse response = webRes.accept(MediaType.TEXT_PLAIN_TYPE).get(ClientResponse.class);
		if (response.getStatus()!=200){
			logger.error("Error when setting capability");
			return;
		}
		logger.debug(response.getEntity(String.class));
		// Get the update
		//updateTopology();
	}
	
	// set node state of a service
	public void setNodeState(String topologyId, String nodeId, int replica, SalsaEntityState state) {
		// send the update command to Salsa center
		Client client = Client.create();
		String serverURL = centerServiceEndpoint + "/update/nodestate/"
				+ serviceId + "/" + topologyId + "/" + nodeId + "/" + replica
				+ "/" + state.getNodeStateString();
		logger.debug("Querrying: "+serverURL);
		WebResource webRes = client.resource(serverURL);
		ClientResponse response = webRes.accept(MediaType.TEXT_PLAIN_TYPE).get(ClientResponse.class);
		if (response.getStatus()!=200){
			logger.error("Error when setting node state");
			return;
		}
		logger.debug("Set node "+nodeId +" state to "+state.getNodeStateString()+". "+response.getEntity(String.class));
		// get the update
		//updateTopology();
	}
	
	/*
	 * Read the capability properties from def
	 */
	public String getCapability(String capaId, TDefinitions def) {
		updateTopology();
		TCapability capa = (TCapability) ToscaStructureQuery.getRequirementOrCapabilityById(capaId, def);
		if (capa.getProperties() != null) {
			ToscaCapabilityString capaString = (ToscaCapabilityString) capa
					.getProperties().getAny();
			return capaString.getValue();
		}
		return null;
	}
	
	
	/*
	 * Download the latest full topology
	 */
	public TDefinitions updateTopology() {
		try {
			String url= centerServiceEndpoint
					+ "/getservice/" + serviceId;					
			String toscaFile = workingDir + "/"	+ serviceId;			
			FileUtils.copyURLToFile(new URL(url), new File(toscaFile));		
			TDefinitions def = ToscaXmlProcess.readToscaFile(toscaFile);
			return def;
		} catch (IOException e) {
			logger.error("Error when update service description: "+ e.toString());			
		} catch (JAXBException e1){
			logger.error(e1.toString());
		}
		return null;
	}
	
	/**
	 * Update the component data on Cloud Service. Use when deploy an addition component
	 * on service.
	 * @param serviceId The service Id which component belong to
	 * @param topologyId The topology which component belong to
	 * @param data The component object
	 */
	public void addComponentData(String serviceId, String topologyId, String nodeId, SalsaComponentReplicaData data){
		String url=centerServiceEndpoint
				+ "/addcomponent"
				+ "/"+serviceId
				+ "/"+topologyId
				+ "/"+nodeId;		
		try {
			HttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost(url);
			
			JAXBContext jaxbContext = JAXBContext.newInstance(SalsaComponentReplicaData.class, SalsaInstanceDescription.class);	// don't need Topology or Service
			Marshaller msl = jaxbContext.createMarshaller();
			msl.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			StringWriter result = new StringWriter();
			msl.marshal(data, result);			
			
			StringEntity input = new StringEntity(result.toString());
			input.setContentType("application/xml");
			post.setEntity(input);
			
			HttpResponse response = client.execute(post);
			if (response.getStatusLine().getStatusCode() != 200) {
				logger.error("Failed : HTTP error code : "
					+ response.getStatusLine().getStatusCode());
			} else {
				logger.error("Added component successfully: " + data.getId());
			}
		} catch (JAXBException e){
			logger.error("Error when marshalling Component data: "+ data.getId());
			logger.error(e.toString());
		} catch (Exception e){
			logger.error("Some error when sending component's data");
		}
		
	}
}
