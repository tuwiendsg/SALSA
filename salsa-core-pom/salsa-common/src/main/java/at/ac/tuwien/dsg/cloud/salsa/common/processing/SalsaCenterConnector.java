package at.ac.tuwien.dsg.cloud.salsa.common.processing;

import generated.oasis.tosca.TDefinitions;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.commons.io.FileUtils;
import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;

import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.CloudService;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.ServiceInstance;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.ServiceInstance.Capabilities;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.ServiceUnit;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.ServiceUnitRelationship;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.enums.SalsaEntityState;
import at.ac.tuwien.dsg.cloud.salsa.common.interfaces.SalsaEngineServiceIntenal;
import at.ac.tuwien.dsg.cloud.salsa.engine.exception.SalsaEngineException;
import at.ac.tuwien.dsg.cloud.salsa.tosca.extension.SalsaCapaReqString;
import at.ac.tuwien.dsg.cloud.salsa.tosca.extension.SalsaInstanceDescription_VM;
import at.ac.tuwien.dsg.cloud.salsa.tosca.processing.ToscaXmlProcess;

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
	//String serviceId;
	String workingDir;
	SalsaEngineServiceIntenal engineInternal;

	/**
	 * Create a connector to Salsa service
	 * 
	 * @param centerServiceEndpoint The endpoint. E.g: <ip>:<port>/<path>
	 * @param serviceId  The deployment ID which connected to
	 * @param storageFolder  temporary folder to storage service files
	 * @param logger  Logger
	 */
	public SalsaCenterConnector(String centerServiceEndpoint, //String serviceId,
			String workingDir, Logger logger) {
		this.centerRestfulEndpoint = centerServiceEndpoint + "/rest";
		this.logger = logger;
		//this.serviceId = serviceId;
		this.workingDir = workingDir;
		this.engineInternal = JAXRSClientFactory.create(this.centerRestfulEndpoint, SalsaEngineServiceIntenal.class);		
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
	public String deregisterService(String serviceId) throws SalsaEngineException {
		//String url = centerRestfulEndpoint + "/services/" + serviceId;
		//logger.debug("Salsa Connector query: " + url);
		logger.debug("Deregister service: " + serviceId);
		Response res = engineInternal.undeployService(serviceId);
		return inputStreamToString((InputStream)res.getEntity());
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
	public String updateNodeState(String serviceId, String topologyId, String nodeId, int instanceId,
			SalsaEntityState state) {
		// /services/{serviceId}/topologies/{topologyId}/nodes/{nodeId}/instances/{instanceId}/state/{value}
		Response res = engineInternal.updateNodeState(serviceId, topologyId, nodeId, instanceId, state.getNodeStateString());
		return res.getEntity().toString();
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
	public String getCapabilityValue(String serviceId, String topoId, String nodeId, int replica,
			String capaId) throws SalsaEngineException {
		System.out.println("Try to get capability value of capaid: " + capaId);
		CloudService service = getUpdateCloudServiceRuntime(serviceId);
		System.out.println("Checking topo/node/inst-id: " + topoId +"/" + nodeId +"/" + replica);
		ServiceInstance rep = service.getInstanceById(nodeId, replica);
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
	public TDefinitions getToscaDescription(String serviceId) {
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

	public void addInstanceUnitMetaData(String serviceId, String topologyId,
			String nodeId, ServiceInstance data) {
		// /services/{serviceId}/topologies/{topologyId}/nodes/{nodeId}/metadata
		engineInternal.addInstanceUnitMetaData(data, serviceId, topologyId, nodeId);
	}
	
	public void unqueueActions(String serviceID, String topologyId, String nodeId, int instnanceId, String actionName){
		engineInternal.unqueueAction(serviceID, topologyId, nodeId, instnanceId, actionName);
	}

	public void addRelationship(String serviceId, String topologyId, ServiceUnitRelationship rela) {
		// /services/{serviceId}/topologies/{topologyId}/relationship		
		engineInternal.addRelationship(rela, serviceId, topologyId);		
	}

	/**
	 * Query the Cloud Service Object, contain all runtime replicas of the
	 * service.
	 * 
	 * @return the CloudService instance.
	 */
	public CloudService getUpdateCloudServiceRuntime(String serviceId) throws SalsaEngineException{
		// some time it's false to get the Cloud Service because of error, retry 10 time:
		for (int i=0; i<10; i++){
			try {
				//System.out.println("getUpdateCloudServiceRuntime. Service id: " + serviceId);
				String xml = getUpdateCloudServiceRuntimeXML(serviceId);
				//logger.debug("IN getUpdateCloudServiceRuntime. XML: " + xml);
				if (xml != null) {
					return SalsaXmlDataProcess.readSalsaServiceXml(xml);
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JAXBException e1) {
				logger.error("Error to parse ServiceRuntime file. Error: " + e1);
				e1.printStackTrace();				
			}
			sleep(1000);
		}
		return null;
	}
	
	public void sleep(int minisec){
		try{ 
			Thread.sleep(minisec);
		} catch (InterruptedException e){
			e.printStackTrace();
		}
	}
	
	public ServiceUnit getUpdateServiceUnit(String serviceId, String topoId, String nodeId) throws SalsaEngineException{
		CloudService service = getUpdateCloudServiceRuntime(serviceId);
		System.out.println("Update service: " + service.getId());
		return service.getComponentById(topoId, nodeId);
	}

	/**
	 * Query the Cloud Service Object, contain all runtime replicas of the
	 * service.
	 * 
	 * @return XML String of the object.
	 */
	public String getUpdateCloudServiceRuntimeXML(String serviceId) throws SalsaEngineException {
		// /services/{serviceId}
		String url = centerRestfulEndpoint + "/services/" + serviceId;
		Response res = engineInternal.getService(serviceId);		
		return inputStreamToString((InputStream)res.getEntity());		
		//return queryDataToCenter1(url, HttpVerb.GET, "","", MediaType.TEXT_XML);
	}
	
//	public String getUpdateCloudServiceRuntimeXMLAndLock(String serviceId){
//		Response res = engine.getServiceAndLock(serviceId);		
//		return inputStreamToString((InputStream)res.getEntity());
//	}
//	
//	public void releaseGetCloudServiceLock(String serviceId){
//		engine.getServiceToUnLock(serviceId);
//	}

	/*
	 * Get the json contain a list of deployed service Id
	 */
	public String getServiceListJson() {
		String url = centerRestfulEndpoint + "/viewgenerator/cloudservice/json/list";		
		return queryDataToCenter1(url, HttpVerb.GET, "", "", MediaType.TEXT_PLAIN);		
	}

	/*
	 * Get the json of running service to generate the tree
	 */
	public String getserviceruntimejsontree(String serviceId) {
		String url = centerRestfulEndpoint + "/viewgenerator/cloudservice/json/compact/"
				+ serviceId;
		return queryDataToCenter1(url, HttpVerb.GET, "", "", MediaType.TEXT_PLAIN);
	}
	
	/*
	 * Get the json of running serviceto generate the tree
	 */
	public String getserviceruntimejsontreecompact(String serviceId) {
		String url = centerRestfulEndpoint + "/viewgenerator/cloudservice/json/full/"
				+ serviceId;
		return queryDataToCenter1(url, HttpVerb.GET, "", "", MediaType.TEXT_PLAIN);
	}

	public String getRequirementValue(String serviceId, String topologyId,
			String nodeId, int instanceId, String reqId) {
		// /services/{serviceId}/topologies/{topologyId}/nodes/{nodeId}/instances/{instanceId}/requirement/{reqId}
		String url = centerRestfulEndpoint 
				+ "/services/" + serviceId 
				+ "/topologies/" + topologyId 
				+ "/nodes/" + nodeId 
				+ "/instances" + instanceId 
				+ "/requirement/" + reqId;
		Response res = engineInternal.getRequirementValue(serviceId, topologyId, nodeId, instanceId, reqId);
		//return res.getEntity().toString();
		return queryDataToCenter1(url, HttpVerb.GET, "", "", MediaType.TEXT_PLAIN);
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
	public void updateInstanceUnitProperty(String serviceId, String topologyId, String nodeId,
			int instanceId, Object property) {
		// /services/{serviceId}/topologies/{topologyId}/nodes/{nodeId}/instances/{instanceId}/properties
		String url = centerRestfulEndpoint 
				+ "/services/" + serviceId 
				+ "/topologies/" + topologyId 
				+ "/nodes/" + nodeId 
				+ "/instances/" + instanceId + "/properties";
		try{
			String data = convertToXML(property);
			engineInternal.updateInstanceUnitProperties(data, serviceId, topologyId, nodeId, instanceId);
			//queryDataToCenter1(url, HttpVerb.POST, data, MediaType.APPLICATION_XML, "");
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
	public void updateInstanceUnitCapability(String serviceId, String topologyId, String nodeId,
			int instanceId, SalsaCapaReqString capa) {
		// /services/{serviceId}/topologies{topologyId}/nodes/{nodeId}/instances/{instanceId}/capability
		String url = centerRestfulEndpoint 
				+ "/services/" + serviceId 
				+ "/topologies/" + topologyId 
				+ "/nodes/" + nodeId 
				+ "/instances/" + instanceId + "/capability";
		try{
			String data = convertToXML(capa);
			Response res = engineInternal.updateInstanceUnitCapability(capa, serviceId, topologyId, nodeId, instanceId);			
			//queryDataToCenter1(url, HttpVerb.POST, data, MediaType.APPLICATION_XML, "");
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
	public void updateNodeIdCounter(String serviceId, String topologyId, String nodeId, Integer value) {
		// /services/{serviceId}/topologies/{topologyId}/nodes/{nodeId}/instance-counter/{value}
		String url = centerRestfulEndpoint 
				+ "/services/" + serviceId
				+ "/topologies/" + topologyId 
				+ "/nodes/" + nodeId 
				+ "/instance-counter/" + value;
		System.out.println(url);
		//Response res = engine.updateNodeIdCounter(serviceId, topologyId, nodeId, value);
		queryDataToCenter1(url, HttpVerb.POST, value.toString(), "", "");
	}

	/*
	 * Post a XML object to URL. Support POST method to Control Service
	 */
//	private void postDataToSalsaCenter(String url, Object data) {
//		logger.debug("POST data. URL: " + url);
//		try {			
//			HttpClient client = new DefaultHttpClient();
//			HttpPost post = new HttpPost(url);
//
//			JAXBContext jaxbContext = JAXBContext // beside data.class, addition classes for contents
//					.newInstance(
//							data.getClass(), // e.g. when update Replica, need
//												// its capability and
//												// InstanceDes.
//							SalsaInstanceDescription_VM.class,
//							SalsaCapaReqString.class);
//			Marshaller msl = jaxbContext.createMarshaller();
//			msl.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
//			StringWriter result = new StringWriter();
//			msl.marshal(data, result);
//
//			StringEntity input = new StringEntity(result.toString());
//			input.setContentType("application/xml");
//			post.setEntity(input);
//
//			HttpResponse response = client.execute(post);
//			if (response.getStatusLine().getStatusCode() >= 400) {
//				logger.error("Failed : HTTP error code : "
//						+ response.getStatusLine().getStatusCode());
//			} else {
//				logger.debug("Post data successful: " + url);
//			}
//		} catch (JAXBException e) {
//			logger.error("Error when marshalling data from class: "
//					+ data.getClass());
//			logger.error(e.toString());
//		} catch (Exception e) {
//			logger.error("Some error when posting data to: " + url);
//		}
//	}

	
	private String queryDataToCenter1(String input_url, HttpVerb method, String data, String type, String accept) {
		try {
			URL url = new URL(input_url);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(method.toString());
				
			if (accept.equals("")){			
				conn.setRequestProperty("Accept", MediaType.TEXT_PLAIN);
			} else {
				conn.setRequestProperty("Accept", accept);
			}
			
			if (type.equals("")){
				conn.setRequestProperty("Type", MediaType.TEXT_PLAIN);
			} else {
				conn.setRequestProperty("Type", type);
			}
			logger.debug("Execute a query. URL: " + url +". Method: " +method + ". Data: " + data +". Sending type:" + type + ". Recieving type: " + accept);
			
			
			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
			 
			String output;
			String result = "";
			
			while ((output = br.readLine()) != null) {
				System.out.println(output);
				result+=output;
			} 
			conn.disconnect();
			
			return result;	
		} catch (Exception e){
			logger.error("Error when executing the query. Error: " + e);
			return null;
		}
	}
	
	private String inputStreamToString(InputStream input){
		BufferedReader br = new BufferedReader(new InputStreamReader(input));
		try {
			String output;
			String result = "";
			
			while ((output = br.readLine()) != null) {
				//System.out.println(output);
				result+=output;
			} 
			return result;
		} catch (IOException e) {
			logger.error("Error when reading the web service: " + e);
			return "";
		}
	}
	

	public String getservicetemplatejsonlist() {
		String url = centerRestfulEndpoint + "/app/getservicetemplatejsonlist";
		return queryDataToCenter1(url, HttpVerb.GET, "", "", "");
	}

	public String getartifactjsonlist() {
		String url = centerRestfulEndpoint + "/app/getartifactjsonlist";
		return queryDataToCenter1(url, HttpVerb.GET, "", "", "");
	}

	public String removeOneInstance(String serviceId, String topologyId,
			String nodeId, int instanceId) {
		String url = centerRestfulEndpoint + "/instanceunits/" + serviceId
				+ "/" + topologyId + "/" + nodeId + "/" + instanceId;
		return queryDataToCenter1(url, HttpVerb.DELETE, "", "", "");		
	}
	
	public String logMessage(String data){
		logger.debug("Sending log message to salsa-engine: " + data);
		Response res =  engineInternal.logMessage(data);		
		return "Logged";		
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
