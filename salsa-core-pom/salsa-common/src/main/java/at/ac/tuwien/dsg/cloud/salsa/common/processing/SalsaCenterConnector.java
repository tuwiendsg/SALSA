package at.ac.tuwien.dsg.cloud.salsa.common.processing;

import generated.oasis.tosca.TDefinitions;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;
import java.util.List;

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

import at.ac.tuwien.dsg.cloud.salsa.common.model.SalsaCloudServiceData;
import at.ac.tuwien.dsg.cloud.salsa.common.model.SalsaComponentInstanceData;
import at.ac.tuwien.dsg.cloud.salsa.common.model.SalsaComponentInstanceData.Capabilities;
import at.ac.tuwien.dsg.cloud.salsa.common.model.SalsaReplicaRelationship;
import at.ac.tuwien.dsg.cloud.salsa.common.model.enums.SalsaEntityState;
import at.ac.tuwien.dsg.cloud.salsa.tosca.extension.SalsaCapabilityString;
import at.ac.tuwien.dsg.cloud.salsa.tosca.extension.SalsaInstanceDescription;
import at.ac.tuwien.dsg.cloud.salsa.tosca.processing.ToscaXmlProcess;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

/**
 * This class is for connecting to the SalsaCenter. Each of this instance target
 * to a specific service Id, then the serviceId must be provide to the
 * construction to ensure that the serviceId is available.
 * 
 * This class is referred to the ControlService of Salsa-center-services
 * 
 * @author Le Duc Hung
 * 
 */
public class SalsaCenterConnector {
	Logger logger;
	String centerRestfulEndpoint;
	String serviceId;
	String workingDir;

	/**
	 * Create a connector to Salsa service
	 * 
	 * @param centerServiceEndpoint
	 *            The endpoint. E.g: <ip>:<port>/<path>
	 * @param serviceId
	 *            The deployment ID which connected to
	 * @param storageFolder
	 *            temporary folder to storage service files
	 * @param logger
	 *            Logger
	 */
	public SalsaCenterConnector(String centerServiceEndpoint, String serviceId,
			String workingDir, Logger logger) {
		this.centerRestfulEndpoint = centerServiceEndpoint + "/rest";
		this.logger = logger;
		this.serviceId = serviceId;
		this.workingDir = workingDir;
	}

	/**
	 * Submit a SalsaCloudService to SalsaCenter. The input is serviceFile of
	 * the serviceId
	 * 
	 * @param serviceFile
	 */
	public void submitService(String serviceFile) {
		String url = centerRestfulEndpoint + "/submit";
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
	
	/**
	 * Deregister the service on Salsa Center
	 * @param serviceId
	 */
	public String deregisterService() {
		String url = centerRestfulEndpoint + "/deregister/"+serviceId;
		logger.debug("Salsa Connector query: " + url);
		return getDataFromSalsaCenter(url);		
	}

	
	/**
	 * Set the state of a node instances (replcia).
	 * 
	 * @param topologyId The topology of node
	 * @param nodeId The node
	 * @param replica The instance of node
	 * @param state The state
	 */
	public void setNodeState(String topologyId, String nodeId, int replica,
			SalsaEntityState state) {
		String serverURL = centerRestfulEndpoint + "/update/nodestate/"
				+ serviceId + "/" + topologyId + "/" + nodeId + "/" + replica
				+ "/" + state.getNodeStateString();
		logger.debug("Querrying: " + serverURL);
		getDataFromSalsaCenter(serverURL);
	}

	/**
	 * Get capability value of a Replica instance.
	 * 
	 * @param topoId
	 * @param nodeId
	 * @param replica
	 * @param capaId
	 * @return TODO: Change the replica hierarchy
	 */
	public String getCapabilityValue(String topoId, String nodeId, int replica,
			String capaId) {
		SalsaCloudServiceData service = getUpdateCloudServiceRuntime();
		SalsaComponentInstanceData rep = service.getReplicaById(topoId, nodeId,
				replica);
		Capabilities capas = rep.getCapabilities();
		if (capas != null) {
			List<SalsaCapabilityString> capaLst = capas.getCapability();
			for (SalsaCapabilityString capa : capaLst) {
				if (capa.getId().equals(capaId)) {
					return capa.getValue();
				}
			}
		}
		return null;
	}

	/**
	 * Download latest Tosca.
	 * 
	 * @return Tosca object
	 */
	public TDefinitions getToscaDescription() {
		try {
			String url = centerRestfulEndpoint + "/getservice/" + serviceId;
			String toscaFile = workingDir + "/" + serviceId;
			FileUtils.copyURLToFile(new URL(url), new File(toscaFile));
			TDefinitions def = ToscaXmlProcess.readToscaFile(toscaFile);
			return def;
		} catch (IOException e) {
			logger.error("Error when update service description: "
					+ e.toString());
		} catch (JAXBException e1) {
			logger.error(e1.toString());
		}
		return null;
	}

	/**
	 * Update the component data on Cloud Service. Use when deploy an addition
	 * component on service.
	 * 
	 * @param serviceId
	 *            The service Id which component belong to
	 * @param topologyId
	 *            The topology which component belong to
	 * @param componentData
	 *            The component object
	 */
	public void addComponentData(String serviceId, String topologyId,
			String nodeId, SalsaComponentInstanceData componentData) {
		String url = centerRestfulEndpoint + "/addcomponent" + "/" + serviceId
				+ "/" + topologyId + "/" + nodeId;
		postDataToSalsaCenter(url, componentData);
	}

	public void addRelationship(String topologyId, SalsaReplicaRelationship rela) {
		String url = centerRestfulEndpoint + "/addrelationship" + "/"
				+ serviceId + "/" + topologyId;
		postDataToSalsaCenter(url, rela);
	}

	/**
	 * Query the Cloud Service Object, contain all runtime replicas of the
	 * service.
	 * 
	 * @return the CloudService instance.
	 */
	public SalsaCloudServiceData getUpdateCloudServiceRuntime() {
		try{
			String xml = getUpdateCloudServiceRuntimeXML();
			if (xml==null){
				return null;
			} else{
				return SalsaXmlDataProcess.readSalsaServiceXml(xml);
			}				
			//return (xml==null)?null:SalsaXmlDataProcess.readSalsaServiceXml(xml);
		} catch (IOException e){
			e.printStackTrace();			
		} catch (JAXBException e1) {
			logger.error("Error to parse ServiceRuntime file. Error: " + e1);
		}
		return null;		
	}
	
	/**
	 * Query the Cloud Service Object, contain all runtime replicas of the
	 * service.
	 * 
	 * @return XML String of the object.
	 */
	public String getUpdateCloudServiceRuntimeXML() {
		String url = centerRestfulEndpoint + "/getserviceruntimexml" + "/"
				+ serviceId;
		return getDataFromSalsaCenter(url);		
	}
	
	/*
	 * Get the json contain a list of deployed service Id
	 */
	public String getServiceListJson(){
		String url = centerRestfulEndpoint + "/getservicejsonlist";
		return getDataFromSalsaCenter(url);
	}
	

	/**
	 * Update the topology for a replica. As the property is AnyType, the
	 * property can be any Jaxb object
	 * 
	 * @param topologyId
	 * @param nodeId
	 * @param replica
	 * @param property
	 */
	public void updateReplicaProperty(String topologyId, String nodeId,
			int replica, Object property) {
		String url = centerRestfulEndpoint + "/update/properties" + "/"
				+ serviceId + "/" + topologyId + "/" + nodeId + "/" + replica;
		postDataToSalsaCenter(url, property);
	}

	/**
	 * Update the capability for a node replica.
	 * 
	 * @param topologyId
	 * @param nodeId
	 * @param replica
	 * @param capaId
	 * @param value
	 */
	public void updateReplicaCapability(String topologyId, String nodeId,
			int replica, SalsaCapabilityString capa) {
		String url = centerRestfulEndpoint + "/update/capability" + "/"
				+ serviceId + "/" + topologyId + "/" + nodeId + "/" + replica;
		postDataToSalsaCenter(url, capa);
	}

	/*
	 * Post a XML object to URL. Support POST method to Control Service
	 */
	private void postDataToSalsaCenter(String url, Object data) {
		try {
			HttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost(url);

			JAXBContext jaxbContext = JAXBContext				// beside data.class, addition classes for contents
					.newInstance(data.getClass(),				// e.g. when update Replica, need its capability and InstanceDes.
							SalsaInstanceDescription.class,
							SalsaCapabilityString.class);
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
				logger.debug("Post data successful: " + url);
			}
		} catch (JAXBException e) {
			logger.error("Error when marshalling data from class: "
					+ data.getClass());
			logger.error(e.toString());
		} catch (Exception e) {
			logger.error("Some error when posting data to: " + url);
		}
	}
	
	/*
	 * Send a GET request and return the result
	 */
	private String getDataFromSalsaCenter(String url){
		Client client = Client.create();
		WebResource webResource = client.resource(url);
		ClientResponse response = webResource.accept("text/plain").get(
				ClientResponse.class);
		if (response.getStatus() != 200) {
			logger.error("Fail to process GET request. Http error code: "
					+ response.getStatus());
			return null;
		}
		String resStr = response.getEntity(String.class);
		return resStr;		
	}

}
