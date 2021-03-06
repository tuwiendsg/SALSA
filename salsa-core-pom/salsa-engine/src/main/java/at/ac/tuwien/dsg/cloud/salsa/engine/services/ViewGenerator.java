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
package at.ac.tuwien.dsg.cloud.salsa.engine.services;

import generated.JaxbGangliaEntities.GangliaHostInfo;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.CloudService;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.ServiceInstance;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.ServiceTopology;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.ServiceUnit;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.enums.SalsaEntityType;
import at.ac.tuwien.dsg.cloud.salsa.common.interfaces.ApplicationInfoAPI;
import at.ac.tuwien.dsg.cloud.salsa.engine.dataprocessing.SalsaXmlDataProcess;
import at.ac.tuwien.dsg.cloud.salsa.engine.impl.base.DynamicPlacementHelper;
import at.ac.tuwien.dsg.cloud.salsa.engine.services.jsondata.ServiceJsonDataForceDirect;
import at.ac.tuwien.dsg.cloud.salsa.engine.services.jsondata.ServiceJsonDataTree;
import at.ac.tuwien.dsg.cloud.salsa.engine.services.jsondata.ServiceJsonDataTreeSimple;
import at.ac.tuwien.dsg.cloud.salsa.engine.services.jsondata.ServiceJsonList;
import at.ac.tuwien.dsg.cloud.salsa.engine.utils.EngineLogger;
import at.ac.tuwien.dsg.cloud.salsa.engine.utils.SalsaConfiguration;
import at.ac.tuwien.dsg.cloud.salsa.tosca.extension.SalsaInstanceDescription_VM;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;

@Service
@Path("/viewgenerator")
public class ViewGenerator{
	static Logger logger;
	static {
		logger = Logger.getLogger("SalsaCenterLogger");
	}

	@GET
	@Path("/cloudservice/json/compact/{serviceId}")
	@Produces(MediaType.TEXT_PLAIN)        
	public String getServiceRuntimeJsonTreeCompact(@PathParam("serviceId") String serviceDeployId) {
		if (serviceDeployId.equals("") || serviceDeployId.equals("null")) {
			return "";
		}
		try {
			String salsaFile = SalsaConfiguration.getServiceStorageDir() + "/" + serviceDeployId + ".data";
			CloudService service = SalsaXmlDataProcess.readSalsaServiceFile(salsaFile);

			ServiceJsonDataTree datatree = new ServiceJsonDataTree();
			datatree.setId(service.getName());
			datatree.setNodeType("CLOUD SERVICE");
			datatree.setState(service.getState());

			List<ServiceTopology> topos = service.getComponentTopologyList();
			for (ServiceTopology topo : topos) {
				ServiceJsonDataTree topoNode = new ServiceJsonDataTree();
				topoNode.setAbstract(true);
				topoNode.setId(topo.getId());
				topoNode.setNodeType("TOPOLOGY");
				topoNode.addProperty("Number of service units", topo.getComponents().size() + "");
				topoNode.setState(topo.getState());
				datatree.addChild(topoNode);
				List<ServiceUnit> components = topo.getComponentsByType(SalsaEntityType.OPERATING_SYSTEM);
				for (ServiceUnit compo : components) {
					ServiceJsonDataTree componode = new ServiceJsonDataTree();
					componode.loadData(compo, -1, topo); // -1 will not check instance id
					topoNode.addChild(componode);
				}
			}

			datatree.compactData(); // parent=null for root node
			datatree.reduceLargeNumberOfInstances();
			Gson json = new GsonBuilder().setPrettyPrinting().create();

			return json.toJson(datatree);

		} catch (IOException e) {
			logger.error("Cannot read service file. " + e);
		} catch (JAXBException e1) {
			logger.error("Error when parsing service file." + e1);
		}

		return "";
	}

	@GET
	@Path("/cloudservice/json/full/{serviceId}")
	@Produces(MediaType.TEXT_PLAIN)
	public String getServiceRuntimeJsonTree(@PathParam("serviceId") String serviceDeployId, @DefaultValue("0") @QueryParam("health") int health) {
		if (serviceDeployId.equals("") || serviceDeployId.equals("null")) {
			return "";
		}
		try {
			String salsaFile = SalsaConfiguration.getServiceStorageDir() + "/" + serviceDeployId + ".data";
			CloudService service = SalsaXmlDataProcess.readSalsaServiceFile(salsaFile);
			if (health == 1) {
				enrichWithGangliaInfo(service);
			}

			ServiceJsonDataTree datatree = new ServiceJsonDataTree();
			datatree.setId(service.getName());
			datatree.setNodeType("CLOUD SERVICE");
			datatree.setState(service.getState());

			List<ServiceTopology> topos = service.getComponentTopologyList();
			for (ServiceTopology topo : topos) {
				List<ServiceUnit> components = service.getAllComponentByType(SalsaEntityType.OPERATING_SYSTEM);
				for (ServiceUnit compo : components) {
					ServiceJsonDataTree componode = new ServiceJsonDataTree();
					componode.loadData(compo, -1, topo); // -1 will not check instance id
					datatree.addChild(componode);
					// logger.debug("add a child node: " + componode.getId());
				}
			}

			Gson json = new GsonBuilder().setPrettyPrinting().create();
			return json.toJson(datatree);

		} catch (IOException e) {
			logger.error("Cannot read service file. " + e);
		} catch (JAXBException e1) {
			logger.error("Error when parsing service file." + e1);
		}

		return "";
	}

	@GET
	@Path("/cloudservice/xml/{serviceId}")
	@Produces(MediaType.TEXT_PLAIN)
	public String getServiceRuntimeXml(@PathParam("serviceId") String serviceDeployId) {
		if (serviceDeployId.equals("") || serviceDeployId.equals("null")) {
			return "";
		}
		String fileName = SalsaConfiguration.getServiceStorageDir() + "/" + serviceDeployId + ".data";
		try {
			String xml = FileUtils.readFileToString(new File(fileName));
			return xml;
		} catch (Exception e) {
			// logger.error("Could not find service: " + serviceDeployId
			// + ". Data did not be sent. Error: " + e.toString());
			return "Error";
		}
	}

	@GET
	@Path("/cloudservice/json/list")
	@Produces(MediaType.TEXT_PLAIN)
	public String getServiceJsonList() {
		String pathName = SalsaConfiguration.getServiceStorageDir();
		try {
			ServiceJsonList serviceList = new ServiceJsonList(pathName);
			Gson json = new GsonBuilder().setPrettyPrinting().create();
			return json.toJson(serviceList);
		} catch (Exception e) {
			logger.error("Could not list services");
			return "";
		}

	}

	/**
	 * This generate the JSON to view in the appstructure.html, which reuses the MELA visualization
	 * 
     * @param serviceDeployId
	 * @return
	 */
	@GET
	@Path("/cloudservice/json/structure/{serviceId}")
	@Produces(MediaType.TEXT_PLAIN)
	public String getServiceJsonStructure(@PathParam("serviceId") String serviceDeployId) {
		if (serviceDeployId.equals("") || serviceDeployId.equals("null")) {
			return "";
		}
		try {
			String salsaFile = SalsaConfiguration.getServiceStorageDir() + "/" + serviceDeployId + ".data";
			CloudService service = SalsaXmlDataProcess.readSalsaServiceFile(salsaFile);			
			ServiceJsonDataTreeSimple jsonObject = new ServiceJsonDataTreeSimple();
			// add clouservice
			jsonObject.setName(service.getName());
			jsonObject.setType("SERVICE"); // cloud service
			for (ServiceTopology topo : service.getComponentTopologyList()) {
				ServiceJsonDataTreeSimple newTopo = createServiceJsonNode(topo.getId(), "SERVICE_TOPOLOGY"); // service topology
				//newTopo.getChildren().add(createServiceJsonNode("State[" + topo.getState() + "]", "metric"));
				jsonObject.getChildren().add(newTopo);
                                
                                List<ServiceUnit> allSoftwareUnits = new ArrayList<>();
                                allSoftwareUnits.addAll(topo.getComponentsByType(SalsaEntityType.SOFTWARE));
                                allSoftwareUnits.addAll(topo.getComponentsByType(SalsaEntityType.SERVICE));
                                
				for (ServiceUnit unit : allSoftwareUnits) {
					for (ServiceInstance instance : unit.getInstancesList()) {
						ServiceJsonDataTreeSimple newInstance = createServiceJsonNode(unit.getId() + "-" + instance.getInstanceId(), "SERVICE_INSTANCE");
						//newInstance.getChildren().add(createServiceJsonNode("State[" + instance.getState() + "]", "metric"));
						// get VM host the components
						ServiceUnit hostedUnit = topo.getComponentById(unit.getHostedId());
						ServiceInstance hostedInstance = hostedUnit.getInstanceById(instance.getHostedId_Integer());
                                                
						// in the case we have more than one software stack
						while (!hostedUnit.getType().equals(SalsaEntityType.OPERATING_SYSTEM.getEntityTypeString())) {
							hostedUnit = topo.getComponentById(hostedUnit.getHostedId());
							hostedInstance = hostedUnit.getInstanceById(hostedInstance.getHostedId_Integer());
							ServiceJsonDataTreeSimple hostedInstanceJson = createServiceJsonNode("HostedBy[" + hostedUnit.getId() + "-" + hostedInstance.getInstanceId() + "]", "metric");
							if (unit.getType().equals(SalsaEntityType.OPERATING_SYSTEM.getEntityTypeString())) {
								SalsaInstanceDescription_VM vm = (SalsaInstanceDescription_VM) instance.getProperties().getAny();
								if (vm != null) {
									hostedInstanceJson.setName("HostedByVM[" + vm.getPrivateIp() + "]");
								}
							}
							//newInstance.getChildren().add(hostedInstanceJson);
						}
						newTopo.getChildren().add(newInstance);
					}
                                        // in the case that unit have no instance, set it as undeployed
                                        if(unit.getInstancesList().isEmpty()){
                                            ServiceJsonDataTreeSimple newSU_undeployed = createServiceJsonNode(unit.getId(), "SERVICE_UNIT");
                                            newTopo.getChildren().add(newSU_undeployed);
                                        }
				}
			}
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			return gson.toJson(jsonObject);

		} catch (IOException e) {
			logger.error("Cannot read service file. " + e);
			return "root:{error: " + e + "}";
		} catch (JAXBException e1) {
			logger.error("Error when parsing service file." + e1);
			return "root:{error: " + e1 + "}";
		}
	}

	@GET
	@Path("/cloudservice/json/brief/{serviceId}")
	@Produces(MediaType.TEXT_PLAIN)
	public String getServiceBriefInfo(@PathParam("serviceId") String serviceDeployId) {
		String salsaFile = SalsaConfiguration.getServiceStorageDir() + "/" + serviceDeployId + ".data";
		try {
			// logger.debug("Generating brief info for service");
			CloudService service = SalsaXmlDataProcess.readSalsaServiceFile(salsaFile);
			JsonObject root = new JsonObject();

			Date modifiedTime = new Date((new File(salsaFile)).lastModified());
			SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM dd yyyy 'at' HH:mm");
			String modTime = sdf.format(modifiedTime);

			root.addProperty("Service ID: ", service.getId());
			root.addProperty("Service Name: ", service.getId());
			root.addProperty("Deployment Time:", modTime);
			root.addProperty("Number of topologies:", service.getComponentTopologyList().size());
			root.addProperty("Number of service units: ", service.getAllComponent().size());			
			root.addProperty("Number of VMs: ", service.getAllReplicaByType(SalsaEntityType.OPERATING_SYSTEM).size());

			Gson json = new GsonBuilder().setPrettyPrinting().create();
			return json.toJson(root);
		} catch (IOException e) {
			logger.error("IOException: Error when reading data file !");
			return "Error: Service not found!";
		} catch (JAXBException e1) {
			logger.error("JAXBException: ");
			return "Intenal error when reading service data!";
		}
	}
	
	@GET
	@Path("/cloudservice/json/forcedirect/{serviceId}")
	@Produces(MediaType.TEXT_PLAIN)
	public String getServiceRuntimeForceDirect(@PathParam("serviceId") String serviceDeployId) {
		if (serviceDeployId.equals("") || serviceDeployId.equals("null")) {
			return "";
		}

		ServiceJsonDataForceDirect jsonObject = new ServiceJsonDataForceDirect();
		try {
			String salsaFile = SalsaConfiguration.getServiceStorageDir() + "/" + serviceDeployId + ".data";
			CloudService service = SalsaXmlDataProcess.readSalsaServiceFile(salsaFile);
			// add root node
			//jsonObject.addNode(service.getId(), service.getId(), service.getState().getNodeStateString());
                        // add all the nodes
			for (ServiceTopology topo : service.getComponentTopologyList()) {
				logger.debug("getServiceRuntimeForceDirect - Checking topo size: " + service.getComponentTopologyList().size());
				logger.debug("getServiceRuntimeForceDirect - Checking topo ID  : " + topo.getId());
				// add topo node				
				jsonObject.addNode(topo.getId(), service.getId(),"TOPOLOGY", topo.getState().getNodeStateString());
				//jsonObject.addLink(service.getId(), topo.getId(), "abstract");
				for (ServiceUnit unit : topo.getComponents()) {		
					logger.debug("getServiceRuntimeForceDirect - Checking unit ID  : " + unit.getId());
					for (ServiceInstance instance : unit.getInstancesList()) {
						logger.debug("getServiceRuntimeForceDirect - Checking instance ID  : " + instance.getInstanceId());
						String fullInstanceId = unit.getId()+"_"+instance.getInstanceId();
						jsonObject.addNode(fullInstanceId, topo.getId(), "INSTANCE", instance.getState().getNodeStateString());	// group is topology id
						//jsonObject.addLink(topo.getId(), fullInstanceId, "abstract");
//						if (topo.getComponentById(unit.getHostedId()) != null){
//							if (!topo.getComponentById(unit.getHostedId()).getInstancesList().isEmpty()){
//								jsonObject.addLink(unit.getHostedId()+"_"+topo.getComponentById(unit.getHostedId()).getInstancesList().get(0).getInstanceId(), fullInstanceId, "HOSTON");
//							}
//						}
//						for (String connectUnit : unit.getConnecttoId()) {
//							if (topo.getComponentById(connectUnit)!=null){
//								if (!topo.getComponentById(connectUnit).getInstancesList().isEmpty()){
//									jsonObject.addLink(fullInstanceId, connectUnit +"_"+ topo.getComponentById(connectUnit).getInstancesList().get(0).getInstanceId(), "CONNECTTO");
//								}
//							}
//						}
					}					
				}				
			}
                        
                        // add all the link
                        List<ServiceTopology> topoTmps = new CopyOnWriteArrayList<>(service.getComponentTopologyList());
                        for (ServiceTopology topo : service.getComponentTopologyList()) {
                                for (ServiceTopology topoTmp: topoTmps){    // get rid of concurent modification of List
                                    if (topo.getId().equals(topoTmp.getId())){
                                        topoTmps.remove(topoTmp);
                                        for (ServiceTopology connectTopo: topoTmps){
                                            jsonObject.addLink(topoTmp.getId(), connectTopo.getId(), "TOPO_TOPO");
                                        }
                                    }                                
                                }
				//jsonObject.addLink(service.getId(), topo.getId(), "SERVICE_TOPO");
				for (ServiceUnit unit : topo.getComponents()) {					
					for (ServiceInstance instance : unit.getInstancesList()) {						
						String fullInstanceId = unit.getId()+"_"+instance.getInstanceId();						
						jsonObject.addLink(topo.getId(), fullInstanceId, "TOPOLOGY_INSTANCE");
						if (service.getComponentById(unit.getHostedId()) != null){
							if (!service.getComponentById(unit.getHostedId()).getInstancesList().isEmpty()){
								jsonObject.addLink(unit.getHostedId()+"_"+service.getComponentById(unit.getHostedId()).getInstancesList().get(0).getInstanceId(), fullInstanceId, "HOSTON");
							}
						}
						for (String connectUnit : unit.getConnecttoId()) {
							if (service.getComponentById(connectUnit)!=null){
								if (!service.getComponentById(connectUnit).getInstancesList().isEmpty()){
									jsonObject.addLink(fullInstanceId, connectUnit +"_"+ service.getComponentById(connectUnit).getInstancesList().get(0).getInstanceId(), "CONNECTTO");
								}
							}
						}
					}					
				}				
			}
                        // TOPO_TOPO link
                        
                        
                        
                        
		} catch (JAXBException | IOException e){
			logger.error("Error when build force direct graph", e);
		}
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		return gson.toJson(jsonObject);
	}
    
    
    

	private ServiceJsonDataTreeSimple createServiceJsonNode(String name, String type) {
		ServiceJsonDataTreeSimple newNode = new ServiceJsonDataTreeSimple();
		newNode.setName(name);
		newNode.setType(type);
		return newNode;
	}

	private void enrichWithGangliaInfo(CloudService service) {
		for (ServiceTopology topo : service.getComponentTopologyList()) {
			for (ServiceUnit unit : topo.getComponentsByType(SalsaEntityType.OPERATING_SYSTEM)) {
				for (ServiceInstance instance : unit.getInstancesList()) {
					InternalManagement moni = new InternalManagement();
					SalsaInstanceDescription_VM vm = (SalsaInstanceDescription_VM) instance.getProperties().getAny();
					String gangliaXML = moni.getVMInformation(vm);

					try {
						JAXBContext jaxbContext = JAXBContext.newInstance(GangliaHostInfo.class);
						Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
						GangliaHostInfo gangliaObj = (GangliaHostInfo) jaxbUnmarshaller.unmarshal(new StringReader(gangliaXML));
						instance.setMonitoring(gangliaObj);
					} catch (JAXBException e) {
						EngineLogger.logger.error(e.toString());
					}

				}

			}

		}

		for (ServiceInstance osNode : service.getAllReplicaByType(SalsaEntityType.OPERATING_SYSTEM)) {
			// get the ganglia info
			InternalManagement moni = new InternalManagement();

			// moni.getMonitorOfInstance(service.getId(), service.get, nodeId, instanceId)
			// String str = SalsaEngineInternal.
			// parse the XML to GangliaHostInfo

			// attach to the osNode
			// GangliaHostInfo ganglia;
			// osNode.g
		}
	}

}
