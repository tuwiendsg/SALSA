/*******************************************************************************
 * Copyright 2013 Technische Universitat Wien (TUW), Distributed Systems Group E184
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package at.ac.tuwien.dsg.cloud.salsa.salsa_center_services;

import generated.oasis.tosca.TCapability;
import generated.oasis.tosca.TDefinitions;
import generated.oasis.tosca.TNodeTemplate;
import generated.oasis.tosca.TRequirement;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import at.ac.tuwien.dsg.cloud.salsa.common.model.SalsaCloudServiceData;
import at.ac.tuwien.dsg.cloud.salsa.common.model.SalsaComponentData;
import at.ac.tuwien.dsg.cloud.salsa.common.model.SalsaComponentInstanceData;
import at.ac.tuwien.dsg.cloud.salsa.common.model.SalsaComponentInstanceData.Capabilities;
import at.ac.tuwien.dsg.cloud.salsa.common.model.SalsaComponentInstanceData.Properties;
import at.ac.tuwien.dsg.cloud.salsa.common.model.SalsaReplicaRelationship;
import at.ac.tuwien.dsg.cloud.salsa.common.model.SalsaTopologyData;
import at.ac.tuwien.dsg.cloud.salsa.common.model.SalsaTopologyData.SalsaReplicaRelationships;
import at.ac.tuwien.dsg.cloud.salsa.common.model.enums.SalsaEntityState;
import at.ac.tuwien.dsg.cloud.salsa.common.model.enums.SalsaEntityType;
import at.ac.tuwien.dsg.cloud.salsa.common.processing.SalsaXmlDataProcess;
import at.ac.tuwien.dsg.cloud.salsa.salsa_center_services.jsondata.ServiceJsonDataTree;
import at.ac.tuwien.dsg.cloud.salsa.salsa_center_services.jsondata.ServiceJsonList;
import at.ac.tuwien.dsg.cloud.salsa.salsa_center_services.utils.CenterConfiguration;
import at.ac.tuwien.dsg.cloud.salsa.salsa_center_services.utils.CenterLogger;
import at.ac.tuwien.dsg.cloud.salsa.tosca.extension.SalsaCapaReqString;
import at.ac.tuwien.dsg.cloud.salsa.tosca.extension.SalsaInstanceDescription_VM;
import at.ac.tuwien.dsg.cloud.salsa.tosca.processing.ToscaStructureQuery;
import at.ac.tuwien.dsg.cloud.salsa.tosca.processing.ToscaXmlProcess;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;


/**
 * This code is for center services of Salsa, exposes a RESTful interface.
 * 
 */
@Path("/")
public class ControlServices {
	static Logger logger;
	static private boolean AccessingFile = false;

	static {
		logger = Logger.getLogger("SalsaCenterLogger");
	}

	/**
	 * Get service description
	 * 
	 * @param serviceName
	 *            The deployment ID of service
	 * @return XML document of service
	 */
	@GET
	@Path("/getservice/{id}")
	@Produces(MediaType.TEXT_XML)
	public String getService(@PathParam("id") String serviceDeployId) {
		if (serviceDeployId.equals("")){
			return "";
		}
		String fileName = CenterConfiguration.getServiceStoragePath() + "/"
				+ serviceDeployId;
		try {
			String xml = FileUtils.readFileToString(new File(fileName));
			return xml;
		} catch (Exception e) {
			logger.error("Could not find service: " + serviceDeployId
					+ ". Data did not be sent. Error: " + e.toString());
			return "";
		}
	}

	/**
	 * This method submit the static (TOSCA) to Salsa center
	 * 
	 * @param uploadedInputStream
	 * @param fileDetail
	 * @return
	 */
	@POST
	@Path("/submit")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response submitService(
			@FormDataParam("file") InputStream uploadedInputStream,
			@FormDataParam("file") FormDataContentDisposition fileDetail) {
		String serviceId = fileDetail.getFileName(); // file name is the service
														// Id
		String uploadedFileLocation = CenterConfiguration
				.getServiceStoragePath() + File.separator + serviceId;		
		writeToFile(uploadedInputStream, uploadedFileLocation);
		
		// put the Service Name into
		lockFile();
		String fileName = CenterConfiguration.getServiceStoragePath()
				+ File.separator + serviceId + ".data";
		try {
			SalsaCloudServiceData service = SalsaXmlDataProcess
					.readSalsaServiceFile(fileName);			
			//service.setName(serviceName);
			
			SalsaXmlDataProcess.writeCloudServiceToFile(service, fileName);
		} catch (IOException e) {
			CenterLogger.logger.error("Could not load service data: "
					+ fileName);
		} catch (JAXBException e1) {
			e1.printStackTrace();
		} finally {
			releaseFile();
		}
		
		
		String output = "Commited service: " + serviceId;
		return Response.status(200).entity(output).build();
	}
	
	@GET
	@Path("/deregister/{serviceId}")
	@Produces(MediaType.TEXT_PLAIN)
	public Response deregisterService(@PathParam("serviceId") String serviceId) {
		String fileName = CenterConfiguration.getServiceStoragePath() + "/"
				+ serviceId;
		try {
			File file = new File(fileName);
			File datafile = new File(fileName.concat(".data"));
			if(file.delete() && datafile.delete()){
				logger.debug("Deregister service done: " + serviceId);
				return Response.status(200).entity("Deregistered service: "	+ serviceId).build();
    		}else{
    			logger.debug("Could not found service to deregister: " + serviceId);
    			return Response.status(202).entity("Service not found to deregister: " + serviceId).build();
    		}
		} catch (Exception e) {
			logger.error("Could not find service: " + serviceId
					+ ". Data did not be sent. Error: " + e.toString());
			return Response.status(201).entity("Service not found to deregister: " + serviceId).build();
		}
	}

	/**
	 * Add a new deployed node replica on the service runtime data
	 * 
	 * @return
	 */
	@POST
	@Path("/addcomponent/{serviceId}/{topologyId}/{nodeId}")
	@Consumes(MediaType.APPLICATION_XML)
	public Response addComponent(JAXBElement<SalsaComponentInstanceData> data,
			@PathParam("serviceId") String serviceId,
			@PathParam("topologyId") String topologyId,
			@PathParam("nodeId") String nodeId) {
		lockFile();
		String fileName = CenterConfiguration.getServiceStoragePath()
				+ File.separator + serviceId + ".data";
		try {
			SalsaCloudServiceData service = SalsaXmlDataProcess
					.readSalsaServiceFile(fileName);
			SalsaTopologyData topo = service
					.getComponentTopologyById(topologyId);
			SalsaComponentData compo = topo.getComponentById(nodeId);
			SalsaComponentInstanceData replicaData = data.getValue();
			CenterLogger.logger.debug("Data: id: " + replicaData.getId()
					+ " - Rep: " + replicaData.getInstanceId());
			compo.addInstance(replicaData);
			SalsaXmlDataProcess.writeCloudServiceToFile(service, fileName);
		} catch (IOException e) {
			CenterLogger.logger.error("Could not load service data: "
					+ fileName);
		} catch (JAXBException e1) {
			e1.printStackTrace();
		} finally {
			releaseFile();
		}

		return Response.status(200).entity("update done").build();
	}
	
	private void lockFile(){
		while (AccessingFile) {
			try {
				Thread.sleep(1000);
			} catch (Exception e) {
			}
		}
		AccessingFile = true;
	}
	
	private void releaseFile(){
		AccessingFile = false;
	}

	// save uploaded file to new location
	private static void writeToFile(InputStream uploadedInputStream,
			String uploadedFileLocation) {	
		try {
			OutputStream out = new FileOutputStream(new File(
					uploadedFileLocation));
			int read = 0;
			byte[] bytes = new byte[1024];
			
			while ((read = uploadedInputStream.read(bytes)) != -1) {
				out.write(bytes, 0, read);
			}
			out.flush();
			out.close();
		} catch (IOException e) {			
			CenterLogger.logger.error("Error writing to file: "
					+ uploadedFileLocation);
			CenterLogger.logger.error(e.toString());
		}		
	}

	/**
	 * Update a replica capability.
	 * 
	 * @param serviceId
	 * @param instanceId
	 * @param capaId
	 * @param value
	 * @return
	 */
	@POST
	@Path("/update/capability/{serviceId}/{topologyId}/{nodeId}/{replicaInt}")
	@Consumes(MediaType.APPLICATION_XML)
	public Response updateCapability(JAXBElement<SalsaCapaReqString> data,
			@PathParam("serviceId") String serviceId,
			@PathParam("topologyId") String topologyId,
			@PathParam("nodeId") String nodeId,
			@PathParam("replicaInt") int replicaInt) {
		lockFile();
		try {
			String serviceFile = CenterConfiguration.getServiceStoragePath()
					+ File.separator + serviceId + ".data";
			SalsaCloudServiceData service = SalsaXmlDataProcess
					.readSalsaServiceFile(serviceFile);	
			SalsaComponentInstanceData rep = service.getReplicaById(topologyId, nodeId, replicaInt);
			Capabilities capas = rep.getCapabilities();
			if (capas == null){ // there is no capability list before, create a new
				capas = new Capabilities();
				rep.setCapabilities(capas);
			}
			List<SalsaCapaReqString> capaLst = capas.getCapability();	// get the list			
			capaLst.add(data.getValue());
			
			SalsaXmlDataProcess.writeCloudServiceToFile(service, serviceFile);			
			
		} catch (Exception e) {
			logger.error("Could not read service for update capability: "
					+ serviceId);
			logger.error(e.toString());
			return Response.status(404).entity("Error update capability").build();
		} finally {
			releaseFile();
		}
		
		return Response.status(200).entity("Updated capability for node: " + nodeId + " on service "
						+ serviceId).build();
	}
	
	
	
	
	
	
	/**
	 * Update the properties for a replica node instance. Properties is an AnyType Xml object
	 * and will be parsed if possible, and add all to the replica.
	 * @param data
	 * @param serviceId
	 * @param topologyId
	 * @param nodeId
	 * @param replicaInt
	 * @return
	 */
	@POST
	@Path("/update/properties/{serviceId}/{topologyId}/{nodeId}/{replicaInt}")
	@Consumes(MediaType.APPLICATION_XML)
	public Response updateReplicaProperties(
			//JAXBElement<Object> data,
			String data, 
			@PathParam("serviceId") String serviceId,
			@PathParam("topologyId") String topologyId,
			@PathParam("nodeId") String nodeId,
			@PathParam("replicaInt") int replicaInt) {		
		lockFile();
		try {
			String serviceFile = CenterConfiguration.getServiceStoragePath()
					+ File.separator + serviceId + ".data";
			SalsaCloudServiceData service = SalsaXmlDataProcess
					.readSalsaServiceFile(serviceFile);			
			SalsaComponentInstanceData rep = service.getReplicaById(topologyId, nodeId, replicaInt);
						
			Properties props = rep.getProperties();
			if (props == null){
				props = new Properties();
			}
			// marshall data and add to props
			JAXBContext context = JAXBContext.newInstance(SalsaInstanceDescription_VM.class);
			Unmarshaller um = context.createUnmarshaller();
			Object propData = um.unmarshal(new StringReader(data));	// object can be any kind of above class ??			
			props.setAny(propData);
			rep.setProperties(props);
			
			SalsaXmlDataProcess.writeCloudServiceToFile(service, serviceFile);			
		} catch (Exception e) {
			logger.error("Could not read service for update property: " + serviceId);
			logger.error(e.toString());

			return Response.status(404).entity("Error update node property").build();
		} finally {
			releaseFile();
		}
		return Response.status(200).entity("Updated capability for node: " + nodeId + ", replica: " + replicaInt+ " on service "
						+ serviceId).build();
	}
	
	@GET
	@Path("/getrequirementvalue/{serviceId}/{topologyId}/{nodeId}/{instanceId}/{reqId}")
	public Response getRequirementValue(
			@PathParam("serviceId") String serviceId,
			@PathParam("topologyId") String topologyId,
			@PathParam("nodeId") String nodeId,
			@PathParam("instanceId") int instanceId,
			@PathParam("reqId") String reqId){
		try {
		// read current TOSCA and SalsaCloudService
			String salsaFile = CenterConfiguration.getServiceStoragePath() + File.separator + serviceId + ".data";
			SalsaCloudServiceData service = SalsaXmlDataProcess.readSalsaServiceFile(salsaFile);
			SalsaTopologyData topo = service.getComponentTopologyById(topologyId);
//			SalsaComponentData nodeData = topo.getComponentById(nodeId);
//			SalsaComponentInstanceData instanceData = nodeData.getInstanceById(instanceId);		
			
			String toscaFile = CenterConfiguration.getServiceStoragePath() + File.separator + serviceId;
			TDefinitions def = ToscaXmlProcess.readToscaFile(toscaFile);
			TRequirement req = (TRequirement)ToscaStructureQuery.getRequirementOrCapabilityById(reqId, def);
			TCapability capa = ToscaStructureQuery.getCapabilitySuitsRequirement(req, def);
			String capaid = capa.getId();
			TNodeTemplate toscanode = ToscaStructureQuery.getNodetemplateOfRequirementOrCapability(capa, def);
			// get the capability of the first instance of the node which have id
			SalsaComponentData nodeData = topo.getComponentById(toscanode.getId());
			if (nodeData.getAllInstanceList().size()==0){
				return Response.status(201).entity("").build();
			}
			SalsaComponentInstanceData nodeInstanceOfCapa = nodeData.getInstanceById(0);
			String reqAndCapaValue = nodeInstanceOfCapa.getCapabilityValue(capaid);
			return Response.status(200).entity(reqAndCapaValue).build();		
		} catch (IOException e1){
			logger.error(e1);			
		} catch (JAXBException e2){
			logger.error(e2);
		}		
		return Response.status(201).entity("").build();
	}
	
	@GET
	@Path("/update/nodeidcount/{serviceId}/{topologyId}/{nodeId}/{value}")
	public Response updateNodeIdCount(
			@PathParam("serviceId") String serviceId,
			@PathParam("topologyId") String topologyId,
			@PathParam("nodeId") String nodeId,
			@PathParam("value") int value){
		try{
			lockFile();
			String salsaFile = CenterConfiguration.getServiceStoragePath()
					+ File.separator + serviceId + ".data";
			SalsaCloudServiceData service = SalsaXmlDataProcess
					.readSalsaServiceFile(salsaFile);
			SalsaTopologyData topo = service
					.getComponentTopologyById(topologyId);
			SalsaComponentData nodeData = topo.getComponentById(nodeId);
			nodeData.setIdCounter(value);
			SalsaXmlDataProcess.writeCloudServiceToFile(service, salsaFile);			
		} catch (Exception e){
			logger.error(e.toString());
			return Response.status(200).entity("Updated node " + nodeId + " on service " + serviceId
					+ " node ID counter: " + value).build();
		}
		finally {
			releaseFile();
		}
		return Response.status(200).entity("Updated node " + nodeId + " on service " + serviceId
				+ " node ID counter: " + value).build();
	}
	

	/**
	 * Remove one instance of the service
	 * @param serviceId
	 * @param topologyId
	 * @param noceId
	 * @param instanceId The Id of the instance, from 0 to N. It is not the VM instance ID
	 * @return
	 */
	@GET
	@Path("/remove/instance/{serviceId}/{topologyId}/{nodeId}/{instanceId}")
	public Response removeOneNodeInstance(
			@PathParam("serviceId") String serviceId,
			@PathParam("topologyId") String topologyId,
			@PathParam("nodeId") String nodeId,
			@PathParam("instanceId") int instanceId){
		try{
			lockFile();
			String salsaFile = CenterConfiguration.getServiceStoragePath() + File.separator + serviceId + ".data";
			SalsaCloudServiceData service = SalsaXmlDataProcess.readSalsaServiceFile(salsaFile);
			SalsaTopologyData topo = service.getComponentTopologyById(topologyId);
			SalsaComponentData nodeData = topo.getComponentById(nodeId);			
			SalsaComponentInstanceData instanceData = nodeData.getInstanceById(instanceId);
			nodeData.getAllInstanceList().remove(instanceData);
			SalsaXmlDataProcess.writeCloudServiceToFile(service, salsaFile);
		} catch (Exception e){
			logger.error(e.getMessage());
		} finally {
			releaseFile();
		}
		return Response.status(200).entity("Remove instance id").build();
	}
	
	/**
	 * Update a replica's state.
	 * 
	 * @param serviceId
	 * @param topologyId
	 * @param nodeId
	 * @param replica
	 * @param value
	 * @return
	 */
	@GET
	@Path("/update/nodestate/{serviceId}/{topologyId}/{nodeId}/{replica}/{value}")
	public Response updateNodeState(@PathParam("serviceId") String serviceId,
			@PathParam("topologyId") String topologyId,
			@PathParam("nodeId") String nodeId,
			@PathParam("replica") int replica, @PathParam("value") String value) {
		
		try {
			lockFile();
			String salsaFile = CenterConfiguration.getServiceStoragePath()
					+ File.separator + serviceId + ".data";
			SalsaCloudServiceData service = SalsaXmlDataProcess
					.readSalsaServiceFile(salsaFile);
			SalsaTopologyData topo = service
					.getComponentTopologyById(topologyId);
			SalsaComponentData nodeData = topo.getComponentById(nodeId);
			
			
			if (replica==-1){	// update for node data
				//nodeData.setState(SalsaEntityState.fromString(value));
				updateComponentStateBasedOnInstance(nodeData);	// update the Tosca node
				SalsaXmlDataProcess.writeCloudServiceToFile(service, salsaFile);				
			} else { // update for instance
				SalsaComponentInstanceData replicaInst = nodeData.getInstanceById(replica);
				if (SalsaEntityState.fromString(value) != null) {
					replicaInst.setState(SalsaEntityState.fromString(value));
					updateComponentStateBasedOnInstance(nodeData);	// update the Tosca node
					SalsaXmlDataProcess.writeCloudServiceToFile(service, salsaFile);
					// ToscaXmlProcess.writeToscaDefinitionToFile(def, toscaFile);
				} else {
	
					return Response
							.status(200)
							.entity("Unknown node state of node " + nodeId
									+ " on service " + serviceId
									+ " deployed status: " + value).build();
				}
			}
		} catch (Exception e) {
			logger.error("Could not read service for update node status: "
					+ serviceId);
			logger.error(e.toString());

			return Response.status(404).entity("Error update node status")
					.build();
		} finally {
			releaseFile();
		}

		return Response
				.status(200)
				.entity("Updated node " + nodeId + " on service " + serviceId
						+ " deployed status: " + value).build();
	}
	
	private void updateComponentStateBasedOnInstance(SalsaComponentData nodeData){
		List<SalsaComponentInstanceData> insts = nodeData.getAllInstanceList();
		List<SalsaEntityState> states = new ArrayList<>();
		if (insts.isEmpty()){
			nodeData.setState(SalsaEntityState.UNDEPLOYED);
			return;
		}
		for (SalsaComponentInstanceData inst : insts) {
			states.add(inst.getState());
		}
		if (states.contains(SalsaEntityState.ALLOCATING) || states.contains(SalsaEntityState.CONFIGURING)){
			nodeData.setState(SalsaEntityState.CONFIGURING);			
			return;
		}
		if (states.contains(SalsaEntityState.RUNNING)){
			nodeData.setState(SalsaEntityState.RUNNING);
			return;
		}
		if (states.contains(SalsaEntityState.FINISHED)){
			nodeData.setState(SalsaEntityState.FINISHED);
			return;
		}
		
	}
	
	/**
	 * Add a relationship on the topology
	 * @param data
	 * @param serviceId
	 * @param topologyId
	 * @return
	 */
	@POST
	@Path("/addrelationship/{serviceId}/{topologyId}")
	@Consumes(MediaType.APPLICATION_XML)
	public Response addRelationship(
			JAXBElement<SalsaReplicaRelationship> data,
			@PathParam("serviceId") String serviceId,
			@PathParam("topologyId") String topologyId) {
		String salsaFile = CenterConfiguration.getServiceStoragePath()
				+ File.separator + serviceId + ".data";
		try {
			lockFile();
			SalsaCloudServiceData service = SalsaXmlDataProcess.readSalsaServiceFile(salsaFile);
			SalsaTopologyData topo = service.getComponentTopologyById(topologyId);
			SalsaReplicaRelationships rels = topo.getRelationships();
			if (rels == null){
				rels = new SalsaReplicaRelationships();
				topo.setRelationships(rels);
			}
			rels.addRelationship(data.getValue());			
			SalsaXmlDataProcess.writeCloudServiceToFile(service, salsaFile);
		} catch (Exception e) {
			logger.error("Could not add relationship for: " + serviceId+", "+topologyId);
			logger.error(e.toString());			
			return Response.status(404).entity("Error update node status").build();
		} finally {
			releaseFile();
		}
		return Response.status(200).entity("Updated relationship ").build();
		
	}
	
	
//	@GET
//	@Path("/getservicejson/{id}")
//	@Produces(MediaType.TEXT_PLAIN)
//	@Deprecated
//	public String getServiceJson(@PathParam("id") String serviceDeployId) {
//		String fileName = CenterConfiguration.getServiceStoragePath() + "/"
//				+ serviceDeployId;
//		try {
//			ServiceJsonData serviceData = new ServiceJsonData();
//			serviceData.loadService(fileName);
//			Gson json = new GsonBuilder().setPrettyPrinting().create();
//			return json.toJson(serviceData);
//		} catch (Exception e) {
//			logger.error("Could not find service: " + serviceDeployId
//					+ ". Data did not be sent. Error: " + e.toString());
//			return "";
//		}
//	}

	@GET
	@Path("/getservicejsonlist")
	@Produces(MediaType.TEXT_PLAIN)
	public String getServiceJsonList() {
		String pathName = CenterConfiguration.getServiceStoragePath();
		try {
			ServiceJsonList serviceList = new ServiceJsonList(pathName);
			Gson json = new GsonBuilder().setPrettyPrinting().create();
			return json.toJson(serviceList);
		} catch (Exception e) {
			logger.error("Could not list services");
			return "";
		}

	}
	
	@GET
	@Path("/getserviceruntimejsontreecompact/{serviceId}")
	@Produces(MediaType.TEXT_PLAIN)
	public String getServiceRuntimeJsonTreeCompact(@PathParam("serviceId") String serviceDeployId) {
		if (serviceDeployId.equals("") || serviceDeployId.equals("null")){
			return "";
		}
		try {
			String salsaFile = CenterConfiguration.getServiceStoragePath() + "/"	+ serviceDeployId + ".data";
			SalsaCloudServiceData service = SalsaXmlDataProcess.readSalsaServiceFile(salsaFile);
			SalsaTopologyData topo = service.getFirstTopology();
			ServiceJsonDataTree datatree = new ServiceJsonDataTree();
			datatree.setId(service.getName());
			datatree.setState(SalsaEntityState.RUNNING);
			
			//logger.debug("Create json tree with id = " + datatree.getId());
			// firstly add all VM node
			List<SalsaComponentData> components = service.getAllComponentByType(SalsaEntityType.OPERATING_SYSTEM);
			for (SalsaComponentData compo : components) {
				ServiceJsonDataTree componode = new ServiceJsonDataTree();
				componode.loadData(compo, -1, topo);	// -1 will not check instance id
				datatree.addChild(componode);				
			}
			datatree.compactData();	// parent=null for root node
			Gson json = new GsonBuilder().setPrettyPrinting().create();
			
			return json.toJson(datatree);
			
		} catch (IOException e){
			logger.error("Cannot read service file. " + e);			
		} catch (JAXBException e1){
			logger.error("Error when parsing service file." + e1);
		}
		
		
		return "";
	}
	
	@GET
	@Path("/getserviceruntimejsontree/{serviceId}")
	@Produces(MediaType.TEXT_PLAIN)
	public String getServiceRuntimeJsonTree(@PathParam("serviceId") String serviceDeployId) {
		if (serviceDeployId.equals("") || serviceDeployId.equals("null")){
			return "";
		}
		try {
			String salsaFile = CenterConfiguration.getServiceStoragePath() + "/"	+ serviceDeployId + ".data";
			SalsaCloudServiceData service = SalsaXmlDataProcess.readSalsaServiceFile(salsaFile);
			SalsaTopologyData topo = service.getFirstTopology();
			ServiceJsonDataTree datatree = new ServiceJsonDataTree();
			datatree.setId(service.getName());
			datatree.setState(SalsaEntityState.RUNNING);
			
			//logger.debug("Create json tree with id = " + datatree.getId());
			// firstly add all VM node
			List<SalsaComponentData> components = service.getAllComponentByType(SalsaEntityType.OPERATING_SYSTEM);
			for (SalsaComponentData compo : components) {
				ServiceJsonDataTree componode = new ServiceJsonDataTree();
				componode.loadData(compo, -1, topo);	// -1 will not check instance id
				datatree.addChild(componode);
				//logger.debug("add a child node: " + componode.getId());
			}
			Gson json = new GsonBuilder().setPrettyPrinting().create();		
			return json.toJson(datatree);
			
		} catch (IOException e){
			logger.error("Cannot read service file. " + e);			
		} catch (JAXBException e1){
			logger.error("Error when parsing service file." + e1);
		}
		
		
		return "";
	}
	

	@GET
	@Path("/getserviceruntimexml/{id}")
	@Produces(MediaType.TEXT_PLAIN)
	public String getServiceRuntimeXml(@PathParam("id") String serviceDeployId) {
		if (serviceDeployId.equals("") || serviceDeployId.equals("null")){
			return "";
		}
		String fileName = CenterConfiguration.getServiceStoragePath() + "/"
				+ serviceDeployId + ".data";
		try {
			String xml = FileUtils.readFileToString(new File(fileName));
			return xml;
		} catch (Exception e) {
//			logger.error("Could not find service: " + serviceDeployId
//					+ ". Data did not be sent. Error: " + e.toString());
			return "Error";
		}
	}

}
