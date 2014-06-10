package at.ac.tuwien.dsg.cloud.salsa.engine.services;

import generated.oasis.tosca.TCapability;
import generated.oasis.tosca.TDefinitions;
import generated.oasis.tosca.TNodeTemplate;
import generated.oasis.tosca.TRequirement;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
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

import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.CloudService;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.SalsaEntity;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.ServiceInstance;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.ServiceInstance.Capabilities;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.ServiceInstance.Properties;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.ServiceTopology;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.ServiceTopology.SalsaReplicaRelationships;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.ServiceUnit;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.ServiceUnitRelationship;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.enums.SalsaEntityState;
import at.ac.tuwien.dsg.cloud.salsa.common.processing.SalsaCenterConnector;
import at.ac.tuwien.dsg.cloud.salsa.common.processing.SalsaXmlDataProcess;
import at.ac.tuwien.dsg.cloud.salsa.engine.impl.DeploymentEngineNodeLevel;
import at.ac.tuwien.dsg.cloud.salsa.engine.impl.SalsaToscaDeployer;
import at.ac.tuwien.dsg.cloud.salsa.engine.utils.CenterConfiguration;
import at.ac.tuwien.dsg.cloud.salsa.engine.utils.CenterLogger;
import at.ac.tuwien.dsg.cloud.salsa.engine.utils.EngineLogger;
import at.ac.tuwien.dsg.cloud.salsa.engine.utils.MutualFileAccessControl;
import at.ac.tuwien.dsg.cloud.salsa.engine.utils.SalsaConfiguration;
import at.ac.tuwien.dsg.cloud.salsa.tosca.extension.SalsaCapaReqString;
import at.ac.tuwien.dsg.cloud.salsa.tosca.extension.SalsaInstanceDescription_VM;
import at.ac.tuwien.dsg.cloud.salsa.tosca.processing.ToscaStructureQuery;
import at.ac.tuwien.dsg.cloud.salsa.tosca.processing.ToscaXmlProcess;

import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;




@Path("/")
public class SalsaEngineInternal {
	static Logger logger;
	static File configFile;

	static {
		File tmpFile=new File("/etc/cloudUserParameters.ini");
		if (tmpFile.exists()) {
			configFile = tmpFile;
		} else {
			configFile = new File(SalsaEngineInternal.class.getResource("/cloudUserParameters.ini").getFile());
		}
		logger = Logger.getLogger("EngineLogger");
	}
	
	/*
	 * MAIN SERVICES TO EXPOSE TO USERS
	 */
	
	
	/**
	 * This service deploys the whole Tosca file.
	 * The form which is posted to service must contain all parameters
	 * @param uploadedInputStream The file contents
	 * @param serviceName The ServiceName. This must be unique in whole system.
	 * @return The information
	 */
	@PUT
	@Path("/services/{serviceName}")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response deployService(
			@PathParam("serviceName") String serviceName, 
			@FormDataParam("file") InputStream uploadedInputStream) {
		logger.debug("Recieved deployment request with name: " + serviceName);
		String tmp_id = UUID.randomUUID().toString();
		String tmpFile="/tmp/salsa_tmp_"+tmp_id;
		if (!checkForServiceName(serviceName)){
			
			return Response.status(404).entity("Error. Service Name is bad: " +serviceName).build();
		}		
		try {
			MutualFileAccessControl.writeToFile(uploadedInputStream, tmpFile);
			
			TDefinitions def = ToscaXmlProcess.readToscaFile(tmpFile);
			SalsaToscaDeployer deployer = new SalsaToscaDeployer(configFile);
			CloudService service = deployer.deployNewService(def, serviceName);
			String output = "Deployed service. Id: " + service.getId();
			logger.debug(output);
			// return 201: resource created
			return Response.status(201).entity(service.getId()).build();
		} catch (JAXBException e){
			logger.error("Error when parsing Tosca: " + e);
			e.printStackTrace();
			// return 400: bad request, the XML is malformed and could not process 
			return Response.status(400).entity("Error. Unable to parse Tosca. Error: " +e).build();
		} catch (IOException e) {
			logger.error("Error reading file: " + tmpFile + ". Error: " +e);
			//return 500: intenal server error. The server cannot create and process tmp Tosca file 
			return Response.status(500).entity("Error when process Tosca file. Error: " +e).build();
		}		
	}
	
	
	
	@PUT
	@Path("/services/xml")
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.APPLICATION_XML)
	public String deployServiceFromXML(String uploadedInputStream) {
	
		String tmp_id = UUID.randomUUID().toString();
		String tmpFile="/tmp/salsa_tmp_"+tmp_id;
				
		try {
			FileUtils.writeStringToFile(new File(tmpFile), uploadedInputStream);
			
			TDefinitions def = ToscaXmlProcess.readToscaFile(tmpFile);
			SalsaToscaDeployer deployer = new SalsaToscaDeployer(configFile);
			CloudService service = deployer.deployNewService(def, "default");
			String output = "Deployed service. Id: " + service.getId();
			logger.debug(output);
			// return 201: resource created
			return tmp_id;
		} catch (JAXBException e){
			logger.error("Error when parsing Tosca: " + e);
			e.printStackTrace();
			// return 400: bad request, the XML is malformed and could not process 
			return "Error: bad request";
		} catch (IOException e) {
			logger.error("Error reading file: " + tmpFile + ". Error: " +e);
			//return 500: intenal server error. The server cannot create and process tmp Tosca file 
			return "Error when process Tosca file. Error: " +e.toString();
		}		
	}
	
	
	
	
	
	
	
	@DELETE
	@Path("/services/{serviceId}")
	public Response undeployService(@PathParam("serviceId")String serviceId){
		SalsaToscaDeployer deployer = new SalsaToscaDeployer(configFile);
		logger.debug("DELETING SERVICE: " + serviceId);		
		if (deployer.cleanAllService(serviceId)) {
			// deregister service here
			
			String fileName = CenterConfiguration.getServiceStoragePath() + "/"	+ serviceId;
			File file = new File(fileName);
			File datafile = new File(fileName.concat(".data"));
			if(file.delete() && datafile.delete()){
				logger.debug("Deregister service done: " + serviceId);
				return Response.status(200).entity("Deregistered service: "	+ serviceId).build();
    		}else{
    			logger.debug("Could not found service to deregister: " + serviceId);
    			return Response.status(500).entity("Service not found to deregister: " + serviceId).build();
    		}			
		} else {
			// return 404: not found the service to be undeployed
			return Response.status(404).entity("Error: Fail to clean service: " + serviceId).build();
		}
	}
	
		
	@POST
    @Path("/services/{serviceId}/topologies/{topologyId}/nodes/{nodeId}/instance-count/{quantity}")
    //@Produces(MediaType.APPLICATION_JSON)
    public Response spawnInstance(@PathParam("serviceId") String serviceId,
                                  @PathParam("topologyId") String topologyId,
                                  @PathParam("nodeId") String nodeId,
                                  @PathParam("quantity") int quantity) {
		SalsaToscaDeployer deployer = new SalsaToscaDeployer(configFile);
		logger.debug("SPAWNING MORE INSTANCE: " + serviceId + "/" + topologyId + "/" + nodeId + ". Quantity:" + quantity);

		SalsaCenterConnector centerCon = new SalsaCenterConnector(SalsaConfiguration.getSalsaCenterEndpoint(), serviceId, "/tmp", EngineLogger.logger);
		TDefinitions def = centerCon.getToscaDescription();
		CloudService service = centerCon.getUpdateCloudServiceRuntime();
		ServiceUnit node = service.getComponentById(topologyId, nodeId);
		if (node==null){
			EngineLogger.logger.error("May be the id of node is invalided");		
			return Response.status(500).entity("Error: Node ID is not found.").build();
		}
		centerCon.updateNodeIdCounter(topologyId, nodeId, node.getIdCounter()+quantity); // update first the number + quantity		
		String returnVal="";
		for (int i= node.getIdCounter(); i<node.getIdCounter()+quantity; i++) {
			//centerCon.addInstanceUnit(topologyId, nodeId, i);
			new Thread(new asynSpawnInstances(deployer, serviceId, topologyId, nodeId, i, def, service)).start();
			returnVal += i + " ";
		}
		return Response.status(201).entity(returnVal).build();
	}
	
	private class asynSpawnInstances implements Runnable {
		SalsaToscaDeployer deployer;
		TDefinitions def;
		CloudService service;
		String serviceId, topoId, nodeId;
		int instanceId;
		asynSpawnInstances(SalsaToscaDeployer deployer, String serviceId, String topoId, String nodeId, int instanceId, TDefinitions def, CloudService service){
			this.deployer=new SalsaToscaDeployer(configFile);			
			this.serviceId=serviceId;
			this.topoId=topoId;
			this.nodeId=nodeId;
			this.def=def;
			this.service=service;
			this.instanceId=instanceId;	
		}

		@Override
		public void run() {
			deployer.deployOneMoreInstance(serviceId, topoId, nodeId, instanceId, def, service);			
		}		
	}
	
	/**
	 * This method add new instance deployment and metadata
	 * @param serviceId The exist service
	 * @param topologyId Not require at this time, but need to be presented
	 * @param nodeId Id of node to be deployed more
	 * @param quantity Number of instances will be deployed
	 * @return
	 * 
	 */
	@PUT
	@Path("/services/{serviceId}/topologies/{topologyId}/nodes/{nodeId}/instances/{instanceId}")
	public Response deployInstance(
			@PathParam("serviceId")String serviceId,
			@PathParam("topologyId")String topologyId,
			@PathParam("nodeId")String nodeId, 
			@PathParam("instanceId")int instanceId){
		logger.debug("Deployment request for this node: " + serviceId + " - " + nodeId +" - " + instanceId);
		SalsaToscaDeployer deployer = new SalsaToscaDeployer(configFile);
		logger.debug("PUT 1 MORE INSTANCE: " + serviceId + "/" + topologyId + "/" + nodeId);

		SalsaCenterConnector centerCon = new SalsaCenterConnector(SalsaConfiguration.getSalsaCenterEndpoint(), serviceId, "/tmp", EngineLogger.logger);
		TDefinitions def = centerCon.getToscaDescription();
		CloudService service = centerCon.getUpdateCloudServiceRuntime();
		ServiceUnit node = service.getComponentById(topologyId, nodeId);
		if (node==null){
			EngineLogger.logger.error("May be the id of node is invalided");		
			return Response.status(500).entity("Error: Node ID is not found.").build();
		}
		new Thread(new asynSpawnInstances(deployer, serviceId, topologyId, nodeId, instanceId, def, service)).start();
		return Response.status(201).entity(instanceId).build();	
		//TODO: What happen if it is fail to spawn a VM ? 
	}
	
	
	/**
	 * Undeploy an instance
	 * @param serviceId
	 * @param topologyId
	 * @param nodeId
	 * @param instanceId
	 * @return
	 */
	@DELETE
	@Path("/services/{serviceId}/topologies/{topologyId}/nodes/{nodeId}/instances/{instanceId}")
	public Response destroyInstance(
			@PathParam("serviceId") String serviceId,
			@PathParam("topologyId") String topologyId,
			@PathParam("nodeId") String nodeId,
			@PathParam("instanceId") int instanceId){
		SalsaToscaDeployer deployer = new SalsaToscaDeployer(configFile);
		int instanceIdInt = instanceId;
		if (deployer.removeOneInstance(serviceId, topologyId, nodeId, instanceIdInt)){			
			// remove metadata
			try{
				MutualFileAccessControl.lockFile();
				String salsaFile = CenterConfiguration.getServiceStoragePath() + File.separator + serviceId + ".data";
				CloudService service = SalsaXmlDataProcess.readSalsaServiceFile(salsaFile);
				ServiceTopology topo = service.getComponentTopologyById(topologyId);
				ServiceUnit nodeData = topo.getComponentById(nodeId);			
				ServiceInstance instanceData = nodeData.getInstanceById(instanceIdInt);
				
				// remove hosted on node
				for (ServiceUnit unit : topo.getComponents()) {
					if (unit.getHostedId().equals(nodeData.getId())){
						for (ServiceInstance instance: unit.getInstancesList()){
							if (instance.getHostedId_Integer()==instanceData.getInstanceId()){
								unit.getInstancesList().remove(instance);
							}
						}
					}
				}
			
				nodeData.getInstancesList().remove(instanceData);				
				SalsaXmlDataProcess.writeCloudServiceToFile(service, salsaFile);
			} catch (Exception e){
				logger.error(e.getMessage());
				return Response.status(404).entity("Cannot remove instance: " + instanceId +", on node: " + nodeId + ", on service " + serviceId).build();
			} finally {
				MutualFileAccessControl.releaseFile();
			}

			return Response.status(200).entity("Undeployed instance: " + serviceId+"/"+nodeId+"/"+instanceId).build();
		} else {
			// return 404: resource not found. The instance is not found to be undeployed
			return Response.status(404).entity("Could not undeployed instance.").build();
		}
				
	}
	
	private boolean checkForServiceName(String serviceName){
		if (!serviceName.equals("")){
			return true;
		}
		return false;
	}
	
	
	
	/*
	 * INTERNAL SERVICES FOR CLOUD SERVICES
	 */
	
	/**
	 * Get service description
	 * 
	 * @param serviceName
	 *            The deployment ID of service
	 * @return XML document of service
	 */
	@GET
	@Path("/services/{serviceId}")
	@Produces(MediaType.TEXT_XML)
	public Response getService(@PathParam("serviceId") String serviceDeployId) {		
		if (serviceDeployId.equals("")){		
			return Response.status(400).entity("").build();
		}
		String fileName = CenterConfiguration.getServiceStoragePath() + "/"
				+ serviceDeployId+".data";		
		try {
			String xml = FileUtils.readFileToString(new File(fileName));		
			return Response.status(200).entity(xml).build();
		} catch (Exception e) {
			logger.error("Could not find service: " + serviceDeployId + ". Data did not be sent. Error: " + e.toString());
			return Response.status(500).entity("").build();
		}
	}
	
	/**
	 * Get service description in TOSCA
	 * 
	 * @param serviceName
	 *            The deployment ID of service
	 * @return XML document of service
	 */
	@GET
	@Path("/services/tosca/{serviceId}")
	@Produces(MediaType.TEXT_XML)
	public Response getToscaService(@PathParam("serviceId") String serviceDeployId) {
		logger.debug("Read Tosca file and return !");
		if (serviceDeployId.equals("")){
			return Response.status(400).entity("").build();
		}
		String fileName = CenterConfiguration.getServiceStoragePath() + "/"	+ serviceDeployId;
		try {
			String xml = FileUtils.readFileToString(new File(fileName));
			return Response.status(200).entity(xml).build();
		} catch (Exception e) {
			logger.error("Could not find service: " + serviceDeployId + ". Data did not be sent. Error: " + e.toString());
			logger.debug("YOU ARE HERE FAIL !!");
			return Response.status(500).entity("").build();
		}
	}
	
	

	@POST
	@Path("/services/submit")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response submitService(
			@FormDataParam("file") InputStream uploadedInputStream,
			@FormDataParam("file") FormDataContentDisposition fileDetail) {
		String serviceId = fileDetail.getFileName(); // file name is the service id
		String uploadedFileLocation = CenterConfiguration
				.getServiceStoragePath() + File.separator + serviceId;		
		MutualFileAccessControl.writeToFile(uploadedInputStream, uploadedFileLocation);
		
		// put the Service Name into
		MutualFileAccessControl.lockFile();
		String fileName = CenterConfiguration.getServiceStoragePath()
				+ File.separator + serviceId + ".data";
		try {
			CloudService service = SalsaXmlDataProcess
					.readSalsaServiceFile(fileName);			
			//service.setName(serviceName);
			
			SalsaXmlDataProcess.writeCloudServiceToFile(service, fileName);
		} catch (IOException e) {
			CenterLogger.logger.error("Could not load service data: "
					+ fileName);
		} catch (JAXBException e1) {
			e1.printStackTrace();
		} finally {
			MutualFileAccessControl.releaseFile();
		}		
		
		String output = "Commited service: " + serviceId;
		return Response.status(200).entity(output).build();
	}
	
	/*
	 * INTERFNAL SERVICES FOR TOPOLOGY
	 */
	
	/**
	 * Add a relationship on the topology
	 * @param data
	 * @param serviceId
	 * @param topologyId
	 * @return
	 */
	@POST
	@Path("/services/{serviceId}/topologies/{topologyId}/relationship")
	@Consumes(MediaType.APPLICATION_XML)
	public Response addRelationship(
			JAXBElement<ServiceUnitRelationship> data,
			@PathParam("serviceId") String serviceId,
			@PathParam("topologyId") String topologyId) {
		String salsaFile = CenterConfiguration.getServiceStoragePath()
				+ File.separator + serviceId + ".data";
		try {
			MutualFileAccessControl.lockFile();
			CloudService service = SalsaXmlDataProcess.readSalsaServiceFile(salsaFile);
			ServiceTopology topo = service.getComponentTopologyById(topologyId);
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
			MutualFileAccessControl.releaseFile();
		}
		return Response.status(200).entity("Updated relationship ").build();
		
	}
	
	/*
	 * INTERFNAL SERVICES FOR TOPOLOGY
	 */
	
//	/**
//	 * Add a new deployed node replica on the service runtime data
//	 * 
//	 * @return
//	 */
//	@PUT
//	@Path("/services/{serviceId}/topologies/{topologyId}/nodes/{nodeId}")
//	@Consumes(MediaType.APPLICATION_XML)
//	public Response addInstanceUnitData(JAXBElement<SalsaComponentInstanceData> data,
//			@PathParam("serviceId") String serviceId,
//			@PathParam("topologyId") String topologyId,
//			@PathParam("nodeId") String nodeId) {
//		MutualFileAccessControl.lockFile();
//		String fileName = CenterConfiguration.getServiceStoragePath()
//				+ File.separator + serviceId + ".data";
//		try {
//			SalsaCloudServiceData service = SalsaXmlDataProcess
//					.readSalsaServiceFile(fileName);
//			SalsaTopologyData topo = service
//					.getComponentTopologyById(topologyId);
//			SalsaComponentData compo = topo.getComponentById(nodeId);
//			SalsaComponentInstanceData replicaData = data.getValue();
//			CenterLogger.logger.debug("Data: id: " + replicaData.getId()
//					+ " - Rep: " + replicaData.getInstanceId());
//			compo.addInstance(replicaData);
//			SalsaXmlDataProcess.writeCloudServiceToFile(service, fileName);
//		} catch (IOException e) {
//			CenterLogger.logger.error("Could not load service data: "
//					+ fileName);
//		} catch (JAXBException e1) {
//			e1.printStackTrace();
//		} finally {
//			MutualFileAccessControl.releaseFile();
//		}
//
//		return Response.status(200).entity("update done").build();
//	}
	
		
	@POST
	@Path("/services/{serviceId}/topologies/{topologyId}/nodes/{nodeId}/instance-counter/{value}")
	public Response updateNodeIdCounter(
			@PathParam("serviceId") String serviceId,
			@PathParam("topologyId") String topologyId,
			@PathParam("nodeId") String nodeId,
			@PathParam("value") int value){
		try{
			MutualFileAccessControl.lockFile();
			String salsaFile = CenterConfiguration.getServiceStoragePath()
					+ File.separator + serviceId + ".data";
			CloudService service = SalsaXmlDataProcess
					.readSalsaServiceFile(salsaFile);
			ServiceTopology topo = service
					.getComponentTopologyById(topologyId);
			ServiceUnit nodeData = topo.getComponentById(nodeId);
			nodeData.setIdCounter(value);
			SalsaXmlDataProcess.writeCloudServiceToFile(service, salsaFile);			
		} catch (Exception e){
			logger.error(e.toString());
			return Response.status(404).entity("Cannot uodate node " + nodeId + " on service " + serviceId
					+ " node ID counter: " + value).build();
		}
		finally {
			MutualFileAccessControl.releaseFile();
		}
		return Response.status(200).entity("Updated node " + nodeId + " on service " + serviceId
				+ " node ID counter: " + value).build();
	}
	
	/*
	 * INTERNAL SERVICES FOR INSTANCE UNITS
	 */
	
	
	
	/**
	 * This PUT the metadata only. It is called by Pioneer which already do deployment.
	 * @param serviceId The exist service
	 * @param topologyId Not require at this time, but need to be presented
	 * @param nodeId Id of node to be deployed more
	 * @param quantity Number of instances will be deployed
	 * @return
	 * 
	 */
	@POST
	@Path("/services/{serviceId}/topologies/{topologyId}/nodes/{nodeId}/instance-metadata")
	public Response addInstanceUnitMetaData(JAXBElement<ServiceInstance> data,
			@PathParam("serviceId")String serviceId,
			@PathParam("topologyId")String topologyId,
			@PathParam("nodeId")String nodeId){		
		String fileName = CenterConfiguration.getServiceStoragePath()+ File.separator + serviceId + ".data";
		try {
			MutualFileAccessControl.lockFile();
			CloudService service = SalsaXmlDataProcess.readSalsaServiceFile(fileName);
			ServiceTopology topo = service.getComponentTopologyById(topologyId);
			ServiceUnit compo = topo.getComponentById(nodeId);
			ServiceInstance replicaData = data.getValue();
			int id = replicaData.getInstanceId();
			ServiceInstance existedInstance = compo.getInstanceById(id);			
			if (existedInstance==null){
				EngineLogger.logger.debug("Create new instance meta-data");
				compo.addInstance(replicaData);
			} else {
				EngineLogger.logger.debug("Update old instance meta-data");
				compo.getInstanceById(id).setHostedId_Integer(replicaData.getHostedId_Integer());
				existedInstance = replicaData;
			}			
			SalsaXmlDataProcess.writeCloudServiceToFile(service, fileName);			
		} catch(JAXBException e1){
			logger.error(e1);
		} catch (IOException e2){
			logger.error(e2);
		} finally{
			MutualFileAccessControl.releaseFile();
		}
		MutualFileAccessControl.releaseFile();
		return Response.status(201).entity("Spawned VM").build();
		//TODO: What happen if it is fail to spawn a VM ? 
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
	@Path("/services/{serviceId}/topologies/{topologyId}/nodes/{nodeId}/instances/{instanceId}/capability")
	@Consumes(MediaType.APPLICATION_XML)
	public Response updateInstanceUnitCapability(JAXBElement<SalsaCapaReqString> data,
			@PathParam("serviceId") String serviceId,
			@PathParam("topologyId") String topologyId,
			@PathParam("nodeId") String nodeId,
			@PathParam("instanceId") int instanceId) {
		MutualFileAccessControl.lockFile();
		try {
			String serviceFile = CenterConfiguration.getServiceStoragePath()
					+ File.separator + serviceId + ".data";
			CloudService service = SalsaXmlDataProcess
					.readSalsaServiceFile(serviceFile);	
			ServiceInstance rep = service.getInstanceById(topologyId, nodeId, instanceId);
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
			MutualFileAccessControl.releaseFile();
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
	 * @param instanceId
	 * @return
	 */
	@POST
	@Path("/services/{serviceId}/topologies/{topologyId}/nodes/{nodeId}/instances/{instanceId}/properties")
	@Consumes(MediaType.APPLICATION_XML)
	public Response updateInstanceUnitProperties(
			//JAXBElement<Object> data,
			String data, 
			@PathParam("serviceId") String serviceId,
			@PathParam("topologyId") String topologyId,
			@PathParam("nodeId") String nodeId,
			@PathParam("instanceId") int instanceId) {		
		MutualFileAccessControl.lockFile();
		try {
			String serviceFile = CenterConfiguration.getServiceStoragePath()
					+ File.separator + serviceId + ".data";
			CloudService service = SalsaXmlDataProcess
					.readSalsaServiceFile(serviceFile);
			logger.debug("Setting property. Read service file: " + serviceFile);
			ServiceInstance rep = service.getInstanceById(topologyId, nodeId, instanceId);
						
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
			
		} catch (JAXBException e) {
			logger.error("Cannot parse the service file: " + serviceId);
			logger.error(e);
			return Response.status(404).entity("Error update node property").build();
		} catch (IOException e1){
			logger.error("Could not read service for update property: " + serviceId);
			logger.error(e1);
			return Response.status(500).entity("Error update node property").build();
		}
		finally {
			MutualFileAccessControl.releaseFile();
		}
		return Response.status(200).entity("Updated capability for node: " + nodeId + ", replica: " + instanceId+ " on service "
						+ serviceId).build();
	}
	
	/**
	 * Update a replica's state.
	 * 
	 * @param serviceId
	 * @param topologyId
	 * @param nodeId
	 * @param instanceId
	 * @param value
	 * @return
	 */
	@POST
	@Path("/services/{serviceId}/topologies/{topologyId}/nodes/{nodeId}/instances/{instanceId}/state/{value}")
	public Response updateNodeState(@PathParam("serviceId") String serviceId,
			@PathParam("topologyId") String topologyId,
			@PathParam("nodeId") String nodeId,
			@PathParam("instanceId") int instanceId, 
			@PathParam("value") String value) {		
		try {
			java.util.Date date= new java.util.Date();			 
			logger.debug("TIMESTAMP - Node: " + nodeId + "/" + instanceId + ", state: " + value + ", Time: " + date.getTime());
			logger.debug("UPDATE NODE STATE: " + nodeId + ", instance: " + instanceId + ", state: " + value);			
			MutualFileAccessControl.lockFile();
			String salsaFile = CenterConfiguration.getServiceStoragePath()
					+ File.separator + serviceId + ".data";
			CloudService service = SalsaXmlDataProcess.readSalsaServiceFile(salsaFile);
			ServiceTopology topo = service.getComponentTopologyById(topologyId);
			ServiceUnit nodeData = topo.getComponentById(nodeId);
			
			if (instanceId==-1){	// update for node data
				logger.debug("updateNodeState: UPDATE NODE DATA STATE 1");
				nodeData.setState(SalsaEntityState.fromString(value));				
				logger.debug("updateNodeState: UPDATE NODE DATA STATE 2");
				updateComponentStateBasedOnInstance(service);
				SalsaXmlDataProcess.writeCloudServiceToFile(service, salsaFile);				
			} else { // update for instance
				ServiceInstance replicaInst = nodeData.getInstanceById(instanceId);
				if (SalsaEntityState.fromString(value) != null) {
					replicaInst.setState(SalsaEntityState.fromString(value));
					updateComponentStateBasedOnInstance(service);	// update the Tosca node
					SalsaXmlDataProcess.writeCloudServiceToFile(service, salsaFile);
					// ToscaXmlProcess.writeToscaDefinitionToFile(def, toscaFile);
				} else {
	
					return Response.status(404).entity("Unknown node state of node " + nodeId
									+ " on service " + serviceId
									+ " deployed status: " + value).build();
				}
			}
			
		} catch (Exception e) {
			logger.error("Could not read service for update node status: "	+ nodeId +"/" + instanceId);
			logger.error(e.toString());

			return Response.status(500).entity("Error update node status").build();
		} finally {
			MutualFileAccessControl.releaseFile();
		}

		return Response.status(200)
				.entity("Updated node " + nodeId + " on service " + serviceId
						+ " deployed status: " + value).build();
	}
	
	
	@GET
	@Path("/services/{serviceId}/topologies/{topologyId}/nodes/{nodeId}/instances/{instanceId}/requirement/{reqId}")
	public Response getRequirementValue(
			@PathParam("serviceId") String serviceId,
			@PathParam("topologyId") String topologyId,
			@PathParam("nodeId") String nodeId,
			@PathParam("instanceId") int instanceId,
			@PathParam("reqId") String reqId){
		try {
		// read current TOSCA and SalsaCloudService
			String salsaFile = CenterConfiguration.getServiceStoragePath() + File.separator + serviceId + ".data";
			CloudService service = SalsaXmlDataProcess.readSalsaServiceFile(salsaFile);
			ServiceTopology topo = service.getComponentTopologyById(topologyId);
//			SalsaComponentData nodeData = topo.getComponentById(nodeId);
//			SalsaComponentInstanceData instanceData = nodeData.getInstanceById(instanceId);		
			
			String toscaFile = CenterConfiguration.getServiceStoragePath() + File.separator + serviceId;
			TDefinitions def = ToscaXmlProcess.readToscaFile(toscaFile);
			TRequirement req = (TRequirement)ToscaStructureQuery.getRequirementOrCapabilityById(reqId, def);
			TCapability capa = ToscaStructureQuery.getCapabilitySuitsRequirement(req, def);
			String capaid = capa.getId();
			TNodeTemplate toscanode = ToscaStructureQuery.getNodetemplateOfRequirementOrCapability(capa, def);
			// get the capability of the first instance of the node which have id
			ServiceUnit nodeData = topo.getComponentById(toscanode.getId());
			if (nodeData.getInstancesList().size()==0){
				return Response.status(201).entity("").build();
			}
			ServiceInstance nodeInstanceOfCapa = nodeData.getInstanceById(0);
			String reqAndCapaValue = nodeInstanceOfCapa.getCapabilityValue(capaid);
			return Response.status(200).entity(reqAndCapaValue).build();		
		} catch (IOException e1){
			logger.error(e1);			
		} catch (JAXBException e2){
			logger.error(e2);
		}		
		return Response.status(201).entity("").build();
	}
	
	
	
	@POST
	@Path("/services/{serviceId}/topologies/{topologyId}/nodes/{nodeId}/instances/{instanceId}/action/{actionName}")
	public Response executeAction(
			@PathParam("serviceId") String serviceId,
			@PathParam("topologyId") String topologyId,
			@PathParam("nodeId") String nodeId,
			@PathParam("instanceId") int instanceId,
			@PathParam("actionName") String actionName){
		
		return null;
	}
	
		
	private void updateComponentStateBasedOnInstance(SalsaEntity nodeData){		
		Map<SalsaEntityState, Integer> rankState = new HashMap<>();
		rankState.put(SalsaEntityState.UNDEPLOYED, 0);
		rankState.put(SalsaEntityState.ERROR, 1);
		rankState.put(SalsaEntityState.ALLOCATING, 2);
		rankState.put(SalsaEntityState.CONFIGURING, 3);
		rankState.put(SalsaEntityState.STOPPED, 4);
		rankState.put(SalsaEntityState.RUNNING, 5);
		rankState.put(SalsaEntityState.FINISHED, 6);
		
		List<SalsaEntity> insts = new ArrayList<>();
		if (nodeData.getClass().equals(ServiceUnit.class)){	
			List<ServiceInstance> serviceInst = ((ServiceUnit)nodeData).getInstancesList();
			for (ServiceInstance instance : serviceInst) {
				insts.add(instance);
			}
		} else if (nodeData.getClass().equals(ServiceTopology.class)){
			List<ServiceUnit> serviceUnits = ((ServiceTopology)nodeData).getComponents();
			for (ServiceUnit unit : serviceUnits) {
				updateComponentStateBasedOnInstance(unit);
				insts.add(unit);
			}
		} else if (nodeData.getClass().equals(CloudService.class)){
			List<ServiceTopology> serviceTopos = ((CloudService)nodeData).getComponentTopologyList();
			for (ServiceTopology topo : serviceTopos) {
				updateComponentStateBasedOnInstance(topo);
				insts.add(topo);
			}
		}
		
		int minState=6;
		if (insts.isEmpty()){
			nodeData.setState(SalsaEntityState.UNDEPLOYED);
			return;
		}
		for (SalsaEntity inst : insts) {
			if (minState >= rankState.get(inst.getState())){
				//EngineLogger.logger.debug("Aggregrating state for node: " + nodeData.getId() + ", to: " + inst.getState());				
				nodeData.setState(inst.getState());
			}			
		}
	}
	
}
