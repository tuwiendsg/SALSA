package at.ac.tuwien.dsg.cloud.salsa.common.processing;

import generated.oasis.tosca.TDefinitions;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;
import java.net.ProtocolFamily;
import java.net.URL;
import java.util.List;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpProtocolParams;
import org.slf4j.Logger;

import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.CloudService;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.ServiceInstance;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.ServiceUnit;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.ServiceUnitRelationship;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.ServiceInstance.Capabilities;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.enums.SalsaEntityState;
import at.ac.tuwien.dsg.cloud.salsa.tosca.extension.SalsaCapaReqString;
import at.ac.tuwien.dsg.cloud.salsa.tosca.extension.SalsaInstanceDescription_VM;
import at.ac.tuwien.dsg.cloud.salsa.tosca.processing.ToscaXmlProcess;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.header.MediaTypes;

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
	 * @param centerServiceEndpoint The endpoint. E.g: <ip>:<port>/<path>
	 * @param serviceId  The deployment ID which connected to
	 * @param storageFolder  temporary folder to storage service files
	 * @param logger  Logger
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
		String url = centerRestfulEndpoint + "/services/submit";
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
	 * 
	 * @param serviceId
	 */
	public String deregisterService() {
		String url = centerRestfulEndpoint + "/services/" + serviceId;
		logger.debug("Salsa Connector query: " + url);
		return queryDataToCenter(url, HttpVerb.DELETE, "", "", "");
	}

	/**
	 * Set the state of a node instances (replcia).
	 * 
	 * @param topologyId
	 *            The topology of node
	 * @param nodeId
	 *            The node
	 * @param replica
	 *            The instance of node
	 * @param state
	 *            The state
	 */
	public String updateNodeState(String topologyId, String nodeId, int instanceId,
			SalsaEntityState state) {
		// /services/{serviceId}/topologies/{topologyId}/nodes/{nodeId}/instances/{instanceId}/state/{value}
		logger.debug("Update node state for : " + serviceId +"/"+topologyId+"/"+nodeId+"/"+instanceId+"/"+state);		
		String url = centerRestfulEndpoint 
				+ "/services/" + serviceId 
				+ "/topologies/" + topologyId 
				+ "/nodes/" + nodeId 
				+ "/instances/" + instanceId
				+ "/state/" + state.getNodeStateString();
		logger.debug("Querrying: " + url);
		return queryDataToCenter(url, HttpVerb.POST, "","","");
	}

	/**
	 * Get capability value of a instance.
	 * 
	 * @param topoId
	 * @param nodeId node of the capability
	 * @param replica instanceId
	 * @param capaId ID of capa
	 * @return TODO: Change the replica hierarchy
	 */
	public String getCapabilityValue(String topoId, String nodeId, int replica,
			String capaId) {
		System.out.println("Try to get capability value of capaid: " + capaId);
		CloudService service = getUpdateCloudServiceRuntime();
		System.out.println("Checking topo/node/inst-id: " + topoId +"/" + nodeId +"/" + replica);
		ServiceInstance rep = service.getInstanceById(topoId, nodeId, replica);
		System.out.println("Get this instance: " + rep.getInstanceId());
		Capabilities capas = rep.getCapabilities();
		
		if (capas != null) {
			System.out.println("Capa is not null !");
			List<SalsaCapaReqString> capaLst = capas.getCapability();
			for (SalsaCapaReqString capa : capaLst) {
				System.out.println("Checking capa: " + capa.getId() + " if it equals to " +capaId);
				if (capa.getId().equals(capaId)) {
					System.out.println("OK, return the capa value: " + capa.getValue());					
					return capa.getValue();
				}
			}
		} else {
			System.out.println("capa is null");
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
			// /services/tosca/{serviceId}
			String url = centerRestfulEndpoint + "/services/tosca/" + serviceId;
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
//	public void addInstanceUnit(String topologyId,
//			String nodeId, int instanceId) {
//		// /services/{serviceId}/topologies/{topologyId}/nodes/{nodeId}/instances/{instanceId}
//		String url = centerRestfulEndpoint 
//				+ "/services/" + serviceId
//				+ "/topologies/" + topologyId 
//				+ "/nodes/" + nodeId 
//				+ "/instances/" + instanceId;		
//		queryDataToCenter(url, HttpVerb.PUT, "", "", "");
//	}
	
	public void addInstanceUnitMetaData(String topologyId,
			String nodeId, ServiceInstance data) {
		// /services/{serviceId}/topologies/{topologyId}/nodes/{nodeId}/metadata
		String url = centerRestfulEndpoint 
				+ "/services/" + serviceId
				+ "/topologies/" + topologyId 
				+ "/nodes/" + nodeId + "/instance-metadata";
		try{		
			queryDataToCenter(url, HttpVerb.POST, data.convertToXML(), MediaType.APPLICATION_XML, "");
		} catch(JAXBException e){
			logger.error(e.toString());
		}
	}

	public void addRelationship(String topologyId, ServiceUnitRelationship rela) {
		// /services/{serviceId}/topologies/{topologyId}/relationship
		String url = centerRestfulEndpoint 
				+ "/services/" + serviceId 
				+ "/topologies/" + topologyId + "/relationship";
		postDataToSalsaCenter(url, rela);
	}

	/**
	 * Query the Cloud Service Object, contain all runtime replicas of the
	 * service.
	 * 
	 * @return the CloudService instance.
	 */
	public CloudService getUpdateCloudServiceRuntime() {
		try {
			String xml = getUpdateCloudServiceRuntimeXML();
//			logger.debug("IN getUpdateCloudServiceRuntime. XML: " + xml);
			if (xml == null) {
				return null;
			} else {
				return SalsaXmlDataProcess.readSalsaServiceXml(xml);
			}
			// return
			// (xml==null)?null:SalsaXmlDataProcess.readSalsaServiceXml(xml);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JAXBException e1) {
			logger.error("Error to parse ServiceRuntime file. Error: " + e1);
			e1.printStackTrace();
			
		}
		return null;
	}
	
	public ServiceUnit getUpdateServiceUnit(String serviceId, String topoId, String nodeId){
		CloudService service = getUpdateCloudServiceRuntime();
		return service.getComponentById(topoId, nodeId);
	}

	/**
	 * Query the Cloud Service Object, contain all runtime replicas of the
	 * service.
	 * 
	 * @return XML String of the object.
	 */
	public String getUpdateCloudServiceRuntimeXML() {
		// /services/{serviceId}
		String url = centerRestfulEndpoint + "/services/" + serviceId;
		return queryDataToCenter(url, HttpVerb.GET, "","", MediaType.TEXT_XML);
	}

	/*
	 * Get the json contain a list of deployed service Id
	 */
	public String getServiceListJson() {
		String url = centerRestfulEndpoint + "/viewgenerator/cloudservice/json/list";
		return queryDataToCenter(url, HttpVerb.GET, "", "", MediaType.TEXT_PLAIN);		
	}

	/*
	 * Get the json of running service to generate the tree
	 */
	public String getserviceruntimejsontree() {
		String url = centerRestfulEndpoint + "/viewgenerator/cloudservice/json/compact/"
				+ serviceId;
		return queryDataToCenter(url, HttpVerb.GET, "", "", MediaType.TEXT_PLAIN);
	}
	
	/*
	 * Get the json of running serviceto generate the tree
	 */
	public String getserviceruntimejsontreecompact() {
		String url = centerRestfulEndpoint + "/viewgenerator/cloudservice/json/full/"
				+ serviceId;
		return queryDataToCenter(url, HttpVerb.GET, "", "", MediaType.TEXT_PLAIN);
	}

	public String getRequirementValue(String topologyId,
			String nodeId, int instanceId, String reqId) {
		// /services/{serviceId}/topologies/{topologyId}/nodes/{nodeId}/instances/{instanceId}/requirement/{reqId}
		String url = centerRestfulEndpoint 
				+ "/services/" + serviceId 
				+ "/topologies/" + topologyId 
				+ "/nodes/" + nodeId 
				+ "/instances" + instanceId 
				+ "/requirement/" + reqId;
		return queryDataToCenter(url, HttpVerb.GET, "", "", MediaType.TEXT_PLAIN);
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
	public void updateInstanceUnitProperty(String topologyId, String nodeId,
			int instanceId, Object property) {
		// /services/{serviceId}/topologies/{topologyId}/nodes/{nodeId}/instances/{instanceId}/properties
		String url = centerRestfulEndpoint 
				+ "/services/" + serviceId 
				+ "/topologies/" + topologyId 
				+ "/nodes/" + nodeId 
				+ "/instances/" + instanceId + "/properties";
		try{
			String data = convertToXML(property);
			queryDataToCenter(url, HttpVerb.POST, data, MediaType.APPLICATION_XML, "");
		} catch (JAXBException e){
			logger.debug(e.toString());
		}
		//postDataToSalsaCenter(url, property);
	}

	/**
	 * Update the capability for a node replica.
	 * 
	 * @param topologyId
	 * @param nodeId
	 * @param instanceId
	 * @param value
	 */
	public void updateInstanceUnitCapability(String topologyId, String nodeId,
			int instanceId, SalsaCapaReqString capa) {
		// /services/{serviceId}/topologies{topologyId}/nodes/{nodeId}/instances/{instanceId}/capability
		String url = centerRestfulEndpoint 
				+ "/services/" + serviceId 
				+ "/topologies/" + topologyId 
				+ "/nodes/" + nodeId 
				+ "/instances/" + instanceId + "/capability";
		try{
			String data = convertToXML(capa);
			queryDataToCenter(url, HttpVerb.POST, data, MediaType.APPLICATION_XML, "");
		} catch (JAXBException e){
			logger.debug(e.toString());
		}		
	}

	/**
	 * Update the node ID counter which is use to calculate the id of multiple
	 * instances of one application node
	 * 
	 * @param serviceId
	 * @param topoId
	 * @param nodeId
	 * @param value
	 */
	public void updateNodeIdCounter(String topologyId, String nodeId, Integer value) {
		// /services/{serviceId}/topologies/{topologyId}/nodes/{nodeId}/instance-counter/{value}
		String url = centerRestfulEndpoint 
				+ "/services/" + serviceId
				+ "/topologies/" + topologyId 
				+ "/nodes/" + nodeId 
				+ "/instance-counter/" + value;
		System.out.println(url);
		queryDataToCenter(url, HttpVerb.POST, value.toString(), "", "");
	}

	/*
	 * Post a XML object to URL. Support POST method to Control Service
	 */
	private void postDataToSalsaCenter(String url, Object data) {
		logger.debug("POST data. URL: " + url);
		try {			
			HttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost(url);

			JAXBContext jaxbContext = JAXBContext // beside data.class, addition classes for contents
					.newInstance(
							data.getClass(), // e.g. when update Replica, need
												// its capability and
												// InstanceDes.
							SalsaInstanceDescription_VM.class,
							SalsaCapaReqString.class);
			Marshaller msl = jaxbContext.createMarshaller();
			msl.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			StringWriter result = new StringWriter();
			msl.marshal(data, result);

			StringEntity input = new StringEntity(result.toString());
			input.setContentType("application/xml");
			post.setEntity(input);

			HttpResponse response = client.execute(post);
			if (response.getStatusLine().getStatusCode() >= 400) {
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
	private String getData(String url) {
		logger.debug("GET Data. URL: " + url);
		Client client = Client.create();
		WebResource webResource = client.resource(url);
		ClientResponse response = webResource.accept("text/plain").get(
				ClientResponse.class);
		if (response.getStatus() >= 400) {
			logger.error("Fail to process GET request. Http error code: "
					+ response.getStatus());
			return null;
		}
		String resStr = response.getEntity(String.class);
		return resStr;
	}
	
	private String queryDataToCenter(String url, HttpVerb method, String data, String type, String accept) {
		Client client = Client.create();
				
		WebResource webResource = client.resource(url);
		ClientResponse response;
		
		if (type.equals("")){
			type=MediaType.TEXT_PLAIN;
		}
		
		if (accept.equals("")){
			accept=MediaType.TEXT_PLAIN;
		}
		logger.debug("Execute a query. URL: " + url +". Method: " +method + ". Data: " + data +". Sending type:" + type + ". Recieving type: " + accept);
		
		switch (method){
		case GET:
		{	// GET: type is for the data receiving from server
			response = webResource.accept(accept).type(type).get(ClientResponse.class);			
			break;
		}
		case POST:
		{
			response = webResource.accept(accept).type(type).post(ClientResponse.class, data);
			break;
		}
		case PUT:
		{	// PUT: type is for the sending message
			response = webResource.accept(accept).type(type).put(ClientResponse.class, data);
			break;
		}
		case DELETE:
		{
			response = webResource.accept(accept).type(type).delete(ClientResponse.class);
			break;
		}
			default:	
				response=null;
				break;
		}
		
		if (response.getStatus() >= 400) {
			logger.error("Fail to process request. Http error code: " + response.getStatus() +". Msg: " + response.getEntity(String.class));
			return null;
		}
		String resStr = response.getEntity(String.class);
		//logger.debug("IN QUERY. RESULT IS: " + resStr);
		return resStr;
	}
	

	public String getservicetemplatejsonlist() {
		String url = centerRestfulEndpoint + "/app/getservicetemplatejsonlist";
		return getData(url);
	}

	public String getartifactjsonlist() {
		String url = centerRestfulEndpoint + "/app/getartifactjsonlist";
		return getData(url);
	}

	public String removeOneInstance(String serviceId, String topologyId,
			String nodeId, int instanceId) {
		String url = centerRestfulEndpoint + "/instanceunits/" + serviceId
				+ "/" + topologyId + "/" + nodeId + "/" + instanceId;
		return queryDataToCenter(url, HttpVerb.DELETE, "", "", "");		
	}
	
	
	 public static enum HttpVerb {
	        GET, POST, PUT, DELETE, OTHER;

	        public static HttpVerb fromString(String method) {
	            try {
	                return HttpVerb.valueOf(method.toUpperCase());
	            } catch (Exception e) {
	                return OTHER;
	            }
	        }
	    }
	 
	 private String convertToXML(Object data) throws JAXBException{
		 JAXBContext jaxbContext = JAXBContext // beside data.class, addition classes for contents
					.newInstance(
							data.getClass(), // e.g. when update Replica, need
												// its capability and
												// InstanceDes.
							SalsaInstanceDescription_VM.class,
							SalsaCapaReqString.class);
			Marshaller msl = jaxbContext.createMarshaller();
			msl.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			StringWriter result = new StringWriter();
			msl.marshal(data, result);
			return result.toString();
//			StringEntity input = new StringEntity(result.toString());
//			input.setContentType("application/xml");
//			post.setEntity(input);
	 }
	

}
