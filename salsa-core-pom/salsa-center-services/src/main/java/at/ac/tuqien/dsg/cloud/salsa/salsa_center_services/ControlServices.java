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
package at.ac.tuqien.dsg.cloud.salsa.salsa_center_services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import at.ac.tuqien.dsg.cloud.salsa.salsa_center_services.jsondata.ServiceJsonData;
import at.ac.tuqien.dsg.cloud.salsa.salsa_center_services.jsondata.ServiceJsonList;
import at.ac.tuqien.dsg.cloud.salsa.salsa_center_services.utils.CenterConfiguration;
import at.ac.tuqien.dsg.cloud.salsa.salsa_center_services.utils.CenterLogger;
import at.ac.tuwien.dsg.cloud.salsa.common.model.SalsaCloudServiceData;
import at.ac.tuwien.dsg.cloud.salsa.common.model.SalsaComponentData;
import at.ac.tuwien.dsg.cloud.salsa.common.model.SalsaComponentReplicaData;
import at.ac.tuwien.dsg.cloud.salsa.common.model.SalsaComponentReplicaData.Capabilities;
import at.ac.tuwien.dsg.cloud.salsa.common.model.SalsaComponentReplicaData.Properties;
import at.ac.tuwien.dsg.cloud.salsa.common.model.SalsaReplicaRelationship;
import at.ac.tuwien.dsg.cloud.salsa.common.model.SalsaTopologyData;
import at.ac.tuwien.dsg.cloud.salsa.common.model.SalsaTopologyData.SalsaReplicaRelationships;
import at.ac.tuwien.dsg.cloud.salsa.common.model.data.SalsaCapabilityString;
import at.ac.tuwien.dsg.cloud.salsa.common.model.enums.SalsaEntityState;
import at.ac.tuwien.dsg.cloud.salsa.common.processes.SalsaXmlDataProcess;

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

	@GET
	@Path("/test")
	public Response test() {
		return Response.status(200).entity("This is Salsa RESTful services")
				.build();
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
		String output = "Commited service: " + serviceId;
		return Response.status(200).entity(output).build();
	}

	/**
	 * Add a new deployed node replica on the service runtime data
	 * 
	 * @return
	 */
	@POST
	@Path("/addcomponent/{serviceId}/{topologyId}/{nodeId}")
	@Consumes(MediaType.APPLICATION_XML)
	public Response addComponent(JAXBElement<SalsaComponentReplicaData> data,
			@PathParam("serviceId") String serviceId,
			@PathParam("topologyId") String topologyId,
			@PathParam("nodeId") String nodeId) {
		while (AccessingFile) {
			try {
				Thread.sleep(1000);
			} catch (Exception e) {
			}
		}
		AccessingFile = true;
		String fileName = CenterConfiguration.getServiceStoragePath()
				+ File.separator + serviceId + ".data";
		try {
			SalsaCloudServiceData service = SalsaXmlDataProcess
					.readSalsaServiceFile(fileName);
			SalsaTopologyData topo = service
					.getComponentTopologyById(topologyId);
			SalsaComponentData compo = topo.getComponentById(nodeId);
			SalsaComponentReplicaData replicaData = data.getValue();
			CenterLogger.logger.debug("Data: id: " + replicaData.getId()
					+ " - Rep: " + replicaData.getReplica());
			compo.addReplica(replicaData);
			SalsaXmlDataProcess.writeCloudServiceToFile(service, fileName);
		} catch (IOException e) {
			CenterLogger.logger.error("Could not load service data: "
					+ fileName);
		} catch (JAXBException e1) {
			e1.printStackTrace();
		}
		AccessingFile = false;
		return Response.status(200).entity("update done").build();
	}

	// save uploaded file to new location
	private void writeToFile(InputStream uploadedInputStream,
			String uploadedFileLocation) {

		try {
			OutputStream out = new FileOutputStream(new File(
					uploadedFileLocation));
			int read = 0;
			byte[] bytes = new byte[1024];

			out = new FileOutputStream(new File(uploadedFileLocation));
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
	 * @param replica
	 * @param capaId
	 * @param value
	 * @return
	 */
	@GET
	@Path("/update/capability/{serviceId}/{topologyId}/{nodeId}/{replicaInt}/{capaId}/{value}")
	public Response updateCapability(@PathParam("serviceId") String serviceId,
			@PathParam("topologyId") String topologyId,
			@PathParam("nodeId") String nodeId,
			@PathParam("replicaInt") int replicaInt,
			@PathParam("capaId") String capaId, @PathParam("value") String value) {
		while (AccessingFile) {
			try {
				Thread.sleep(1000);
			} catch (Exception e) {
			}
		}
		AccessingFile = true;
		try {
			String serviceFile = CenterConfiguration.getServiceStoragePath()
					+ File.separator + serviceId + ".data";
			SalsaCloudServiceData service = SalsaXmlDataProcess
					.readSalsaServiceFile(serviceFile);			
			SalsaComponentReplicaData rep = service.getReplicaById(topologyId, nodeId, replicaInt);
			Capabilities capas = rep.getCapabilities();
			if (capas == null){ // there is no capability list before, create a new
				capas = new Capabilities();
				rep.setCapabilities(capas);				
			}
			List<SalsaCapabilityString> capaLst = capas.getCapability();	// get the list
			capaLst.add(new SalsaCapabilityString(capaId, value));
			SalsaXmlDataProcess.writeCloudServiceToFile(service, serviceFile);			
			
		} catch (Exception e) {
			logger.error("Could not read service for update capability: "
					+ serviceId);
			logger.error(e.toString());
			AccessingFile = false;
			return Response.status(404).entity("Error update capability").build();
		}
		AccessingFile = false;
		return Response.status(200).entity("Updated capability " + capaId + " on service "
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
			JAXBElement<Object> data,
			@PathParam("serviceId") String serviceId,
			@PathParam("topologyId") String topologyId,
			@PathParam("nodeId") String nodeId,
			@PathParam("replicaInt") int replicaInt) {		
		try {
			String serviceFile = CenterConfiguration.getServiceStoragePath()
					+ File.separator + serviceId + ".data";
			SalsaCloudServiceData service = SalsaXmlDataProcess
					.readSalsaServiceFile(serviceFile);			
			SalsaComponentReplicaData rep = service.getReplicaById(topologyId, nodeId, replicaInt);
			Properties props = rep.getProperties();
			if (props == null){
				props = new Properties();
			}
			props.setAny(data);			
			SalsaXmlDataProcess.writeCloudServiceToFile(service, serviceFile);			
		} catch (Exception e) {
			logger.error("Could not read service for update property: " + serviceId);
			logger.error(e.toString());
			AccessingFile = false;
			return Response.status(404).entity("Error update node property").build();
		}
		return Response.status(200).entity("Updated capability for node: " + nodeId + ", replica: " + replicaInt+ " on service "
						+ serviceId).build();
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
		while (AccessingFile) {
			try {
				Thread.sleep(1000);
			} catch (Exception e) {
			}
		}
		AccessingFile = true;
		try {
			String salsaFile = CenterConfiguration.getServiceStoragePath()
					+ File.separator + serviceId + ".data";
			SalsaCloudServiceData service = SalsaXmlDataProcess
					.readSalsaServiceFile(salsaFile);
			SalsaTopologyData topo = service
					.getComponentTopologyById(topologyId);
			SalsaComponentData nodeData = topo.getComponentById(nodeId);
			SalsaComponentReplicaData replicaInst = nodeData
					.getReplicaById(replica);

			if (SalsaEntityState.fromString(value) != null) {
				replicaInst.setState(SalsaEntityState.fromString(value));
				SalsaXmlDataProcess.writeCloudServiceToFile(service, salsaFile);
				// ToscaXmlProcess.writeToscaDefinitionToFile(def, toscaFile);
			} else {
				AccessingFile = false;
				return Response
						.status(200)
						.entity("Unknown node state of node " + nodeId
								+ " on service " + serviceId
								+ " deployed status: " + value).build();
			}
		} catch (Exception e) {
			logger.error("Could not read service for update node status: "
					+ serviceId);
			logger.error(e.toString());
			AccessingFile = false;
			return Response.status(404).entity("Error update node status")
					.build();
		}
		AccessingFile = false;
		return Response
				.status(200)
				.entity("Updated node " + nodeId + " on service " + serviceId
						+ " deployed status: " + value).build();
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
		}
		return Response.status(200).entity("Updated relationship ").build();
		
	}
	
	
	

	@GET
	@Path("/getservicejson/{id}")
	@Produces(MediaType.TEXT_PLAIN)
	public String getServiceJson(@PathParam("id") String serviceDeployId) {
		String fileName = CenterConfiguration.getServiceStoragePath() + "/"
				+ serviceDeployId;
		try {
			ServiceJsonData serviceData = new ServiceJsonData();
			serviceData.loadService(fileName);
			Gson json = new GsonBuilder().setPrettyPrinting().create();
			return json.toJson(serviceData);
		} catch (Exception e) {
			logger.error("Could not find service: " + serviceDeployId
					+ ". Data did not be sent. Error: " + e.toString());
			return "";
		}
	}

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
	@Path("/getserviceruntimexml/{id}")
	@Produces(MediaType.TEXT_PLAIN)
	public String getServiceRuntimeXml(@PathParam("id") String serviceDeployId) {
		String fileName = CenterConfiguration.getServiceStoragePath() + "/"
				+ serviceDeployId + ".data";
		try {
			String xml = FileUtils.readFileToString(new File(fileName));
			return xml;
		} catch (Exception e) {
			logger.error("Could not find service: " + serviceDeployId
					+ ". Data did not be sent. Error: " + e.toString());
			return "Error";
		}
	}

}
