package at.ac.tuwien.dsg.cloud.salsa.engine.impl;

import generated.oasis.tosca.TCapability;
import generated.oasis.tosca.TDefinitions;
import generated.oasis.tosca.TEntityTemplate;
import generated.oasis.tosca.TNodeTemplate;
import generated.oasis.tosca.TRelationshipTemplate;
import generated.oasis.tosca.TServiceTemplate;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.management.ServiceNotFoundException;

import org.apache.log4j.Logger;

import at.ac.tuwien.dsg.cloud.salsa.cloud_connector.multiclouds.SalsaCloudProviders;
import at.ac.tuwien.dsg.cloud.salsa.cloud_connector.multiclouds.MultiCloudConnector;
import at.ac.tuwien.dsg.cloud.salsa.common.model.SalsaCloudServiceData;
import at.ac.tuwien.dsg.cloud.salsa.common.model.SalsaComponentData;
import at.ac.tuwien.dsg.cloud.salsa.common.model.SalsaComponentInstanceData;
import at.ac.tuwien.dsg.cloud.salsa.common.model.SalsaTopologyData;
import at.ac.tuwien.dsg.cloud.salsa.common.model.enums.SalsaEntityState;
import at.ac.tuwien.dsg.cloud.salsa.common.model.enums.SalsaEntityType;
import at.ac.tuwien.dsg.cloud.salsa.common.model.enums.SalsaRelationshipType;
import at.ac.tuwien.dsg.cloud.salsa.common.processing.SalsaCenterConnector;
import at.ac.tuwien.dsg.cloud.salsa.common.processing.SalsaXmlDataProcess;
import at.ac.tuwien.dsg.cloud.salsa.engine.utils.EngineLogger;
import at.ac.tuwien.dsg.cloud.salsa.engine.utils.SalsaConfiguration;
import at.ac.tuwien.dsg.cloud.salsa.tosca.extension.SalsaInstanceDescription_VM;
import at.ac.tuwien.dsg.cloud.salsa.tosca.processing.ToscaStructureQuery;
import at.ac.tuwien.dsg.cloud.salsa.tosca.processing.ToscaXmlProcess;


public class SalsaToscaDeployer {
	
	// some hard-code variables
	static final String CLOUD_NODE_NAME=SalsaEntityType.OPERATING_SYSTEM.getEntityTypeString();
	File configFile;
	
	public SalsaToscaDeployer(File config){
		configFile = config;
	}
	
	public void setConfigFile(File config){
		configFile = config;
	}

	/**
	 * Deploy a new service and return a running data
	 * @param def
	 * @return
	 */
	public SalsaCloudServiceData deployNewService (TDefinitions def, String serviceName){
		if (configFile == null){
			EngineLogger.logger.error("No config file specified");
			return null;
		}
		ToscaEnricher enrich= new ToscaEnricher(def, null);
		enrich.createComplexRelationship(def);		
		
		// deploy all service Template 
		List<TNodeTemplate> lst = ToscaStructureQuery.getNodeTemplatesOfTypeList(CLOUD_NODE_NAME, def);
		Map<String, Integer> mapNodeAndRep = new HashMap<>();
		UUID deployID = UUID.randomUUID();
		EngineLogger.logger.info("Deploying service id: "+deployID.toString());
		
		for (TNodeTemplate node : lst) {
			mapNodeAndRep.put(node.getId(), node.getMinInstances());
		}
		DeploymentEngineNodeLevel engine = new DeploymentEngineNodeLevel(configFile);
		
		// register service, all state is INITIAL
		String fullToscaFile="/tmp/"+deployID.toString();
		
		//resetServiceNodeState(def);
		ToscaXmlProcess.writeToscaDefinitionToFile(def, fullToscaFile);
		engine.submitService(fullToscaFile);
		
		// register service running data		
		String fullSalsaDataFile = "/tmp/"+deployID.toString()+".data";
		SalsaCloudServiceData serviceData = buildRuntimeDataFromTosca(def);
		serviceData.setId(deployID.toString());		
		serviceData.setName(serviceName);
		SalsaXmlDataProcess.writeCloudServiceToFile(serviceData, fullSalsaDataFile);
		engine.submitService(fullSalsaDataFile);
		
		// deploy all VM of first Topology
		// TODO: separate deployment Node of Topology
		String topoId = ToscaStructureQuery.getFirstServiceTemplate(def).getId();
		engine.deployConcurrentVMNodes(deployID.toString(), topoId, mapNodeAndRep, def);
		
		// delete tmp topology file
		File file = new File(fullToscaFile);
		file.delete();
		
		
		EngineLogger.logger.info("Deployed VMs for service: " + deployID.toString());
		
		
		return serviceData;
	}
	
	/**
	 * Deploy more instance of a node. Only support deploying VM Tosca node
	 * @param serviceId
	 * @param topologyId
	 * @param nodeId
	 * @param quantity
	 */
	public void deployMoreInstance(String serviceId, String topologyId, String nodeId, int quantity){
		SalsaCenterConnector centerCon = new SalsaCenterConnector(SalsaConfiguration.getSalsaCenterEndpoint(), serviceId, "/tmp", EngineLogger.logger);
		TDefinitions def = centerCon.getToscaDescription();
		SalsaCloudServiceData service = centerCon.getUpdateCloudServiceRuntime();		
		SalsaComponentData node = service.getComponentById(topologyId, nodeId);
		
		if (node.getType().equals(SalsaEntityType.OPERATING_SYSTEM.getEntityTypeString())){
			centerCon.updateNodeIdCounter(topologyId, nodeId, node.getIdCounter()+quantity);
			deployMoreInstance_VM(topologyId, nodeId, quantity, def, service);			
		} else {
			deployMoreInstance_Artifact(topologyId, nodeId, quantity, def, service);
		}
	}
	
	private void deployMoreInstance_Artifact(String topologyId, String nodeId, int quantity, TDefinitions def, SalsaCloudServiceData service){
		// TODO: Implementation
	}
	
	/**
	 * 
	 * @param serviceId exist service Id
	 * @param topologyId
	 * @param nodeId
	 * @param quantity
	 * @param def
	 * @param service
	 */
	private void deployMoreInstance_VM(String topologyId, String nodeId, int quantity, TDefinitions def, SalsaCloudServiceData service){		
		DeploymentEngineNodeLevel engine = new DeploymentEngineNodeLevel(configFile);		
		
		// TODO: implement to manage topology, current: get the first		
		String topoId = service.getFirstTopology().getId();
		SalsaComponentData node = service.getComponentById(topoId, nodeId);
		
		int startingId=node.getIdCounter();
		engine.deployConcurentVMNodesOfOneType(service.getId(), topoId, nodeId, quantity, startingId, def);
		
	}
	
	// TODO: implementation
	public static void deployMoreArtifactInstance(String deployID, String nodeId, String hostNodeId, String hostInstanceId, TDefinitions def){
		
	}
	
	public boolean removeOneInstance(String serviceId, String topologyId, String nodeId, int instanceId){
		SalsaCenterConnector centerCon = new SalsaCenterConnector(SalsaConfiguration.getSalsaCenterEndpoint(), serviceId, "/tmp", EngineLogger.logger);
		SalsaCloudServiceData service = centerCon.getUpdateCloudServiceRuntime();		
		SalsaComponentData node = service.getComponentById(topologyId, nodeId);
		// remove VM node by invoke MultiCloudConnector
		if (node.getType().equals(SalsaEntityType.OPERATING_SYSTEM.getEntityTypeString())){
			SalsaComponentInstanceData vm = node.getInstanceById(instanceId);
			SalsaInstanceDescription_VM vmProps = (SalsaInstanceDescription_VM)vm.getProperties().getAny();
			
			MultiCloudConnector cloudCon= new MultiCloudConnector(EngineLogger.logger,configFile);
			String providerName = vmProps.getProvider();
			String cloudInstanceId = vmProps.getInstanceId();
			EngineLogger.logger.debug("Removing virtual machine. Provider: " + providerName + "InstanceId: " + instanceId);				
			cloudCon.removeInstance(SalsaCloudProviders.fromString(providerName), cloudInstanceId);
			
			centerCon.removeOneInstance(serviceId, topologyId, nodeId, instanceId);
			return true;
		} else {
			return false;
		}
	}
	
	private static SalsaCloudServiceData buildRuntimeDataFromTosca(TDefinitions def){
		EngineLogger.logger.debug("Building runtime from Tosca file");
		SalsaCloudServiceData service = new SalsaCloudServiceData();
		List<TServiceTemplate> serviceTemplateLst = ToscaStructureQuery.getServiceTemplateList(def);
		for (TServiceTemplate st : serviceTemplateLst) {
			SalsaTopologyData topo = new SalsaTopologyData();
			topo.setId(st.getId());
			topo.setName(st.getName());
			List<TNodeTemplate> nodes = ToscaStructureQuery.getNodeTemplateList(st);	// all other nodes
			List<TRelationshipTemplate> relas = ToscaStructureQuery.getRelationshipTemplateList(SalsaRelationshipType.HOSTON.getRelationshipTypeString(),st);
			EngineLogger.logger.debug("Number of HostOn relationships: " + relas.size());
			for (TNodeTemplate node : nodes) {	
				SalsaComponentData nodeData = new SalsaComponentData(node.getId(), node.getType().getLocalPart());
				nodeData.setState(SalsaEntityState.UNDEPLOYED);
				nodeData.setName(node.getName());
				// find what is the host of this node, add to hostId
				for (TRelationshipTemplate rela : relas) { // search on all relationship, find the host of this "node"
															// note that, after convert, the capa and req are reverted.
					TEntityTemplate targetReq = (TEntityTemplate)rela.getTargetElement().getRef();
					TNodeTemplate target = ToscaStructureQuery.getNodetemplateOfRequirementOrCapability(targetReq.getId(), def);
					EngineLogger.logger.debug("Is the source with id: " + target.getId() + " same with " + nodeData.getId());
					if (target.getId().equals(nodeData.getId())){
						TCapability sourceCapa = (TCapability)rela.getSourceElement().getRef();
						TNodeTemplate source = ToscaStructureQuery.getNodetemplateOfRequirementOrCapability(sourceCapa.getId(), def);
						nodeData.setHostedId(source.getId());
						EngineLogger.logger.debug("Found the host of node "+nodeData.getId() +" which is id = " + source.getId());
					}
				}
				topo.addComponent(nodeData);
			}
			
			service.addComponentTopology(topo);
		}
		return service;
	}
	

	
	
	public boolean cleanAllService (String serviceId){
		// TODO: implement it
		//List<TNodeTemplate> lst = ToscaStructureQuery.getNodeTemplatesOfTypeList("OPERATING_SYSTEM", def);
		SalsaCenterConnector centerCon = new SalsaCenterConnector(SalsaConfiguration.getSalsaCenterEndpoint(), serviceId, "", EngineLogger.logger);
		SalsaCloudServiceData service = centerCon.getUpdateCloudServiceRuntime();
		if (service == null) {
			return false;
		}
		List<SalsaComponentInstanceData> repLst = service.getAllReplicaByType(SalsaEntityType.OPERATING_SYSTEM);		
		for (SalsaComponentInstanceData rep : repLst) {
			if (rep.getProperties() != null){
				SalsaInstanceDescription_VM instance = (SalsaInstanceDescription_VM)rep.getProperties().getAny();				
				MultiCloudConnector cloudCon= new MultiCloudConnector(EngineLogger.logger,configFile);
				String providerName = instance.getProvider();
				String instanceId = instance.getInstanceId();
				EngineLogger.logger.debug("Removing virtual machine. Provider: " + providerName + "InstanceId: " + instanceId);				
				cloudCon.removeInstance(SalsaCloudProviders.fromString(providerName), instanceId);
			}			
		}
		centerCon.deregisterService();
		return true;
	}
	
	/**
	 * Deploy addition component of a service
	 * @param serviceId	the existed and running service
	 * @param deployId the component which want to deploy more
	 */
	public  void deployAdditionService(String serviceId, String deployId){
		// TODO: implement it
		// Note: Static description will be queried on center
		
	}
	
	// reset all node to INITIATE state
//	private static void resetServiceNodeState(TDefinitions def){
//		List<TNodeTemplate> list = ToscaStructureQuery.getNodeTemplateList(def);
//		for (TNodeTemplate node : list) {
//			node.setState(SalsaEntityState.UNDEPLOYED.getNodeStateString());
//		}
//	}
	
	
	
}
