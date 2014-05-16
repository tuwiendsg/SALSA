package at.ac.tuwien.dsg.cloud.salsa.engine.impl;

import generated.oasis.tosca.TCapability;
import generated.oasis.tosca.TDefinitions;
import generated.oasis.tosca.TEntityTemplate;
import generated.oasis.tosca.TNodeTemplate;
import generated.oasis.tosca.TRelationshipTemplate;
import generated.oasis.tosca.TRequirement;
import generated.oasis.tosca.TServiceTemplate;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import at.ac.tuwien.dsg.cloud.salsa.cloud_connector.multiclouds.MultiCloudConnector;
import at.ac.tuwien.dsg.cloud.salsa.cloud_connector.multiclouds.SalsaCloudProviders;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.CloudService;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.ServiceInstance;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.ServiceTopology;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.ServiceUnit;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.enums.SalsaEntityState;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.enums.SalsaEntityType;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.enums.SalsaRelationshipType;
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
	public CloudService deployNewService (TDefinitions def, String serviceName){
		if (configFile == null){
			EngineLogger.logger.error("No config file specified");
			return null;
		}
		ToscaEnricher enrich = new ToscaEnricher(def);
		//ToscaRefiner enrich= new ToscaRefiner(def, null);
		//enrich.createComplexRelationship(def);
		
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
		String fullToscaFile = SalsaConfiguration.getServiceStorageDir()+"/"+deployID.toString();
		
		//resetServiceNodeState(def);
		ToscaXmlProcess.writeToscaDefinitionToFile(def, fullToscaFile);	
		
		// register service running data		
		String fullSalsaDataFile = SalsaConfiguration.getServiceStorageDir()+"/"+deployID.toString()+".data";
		CloudService serviceData = buildRuntimeDataFromTosca(def);
		serviceData.setId(deployID.toString());		
		serviceData.setName(serviceName);
		SalsaXmlDataProcess.writeCloudServiceToFile(serviceData, fullSalsaDataFile);
		
		
		// deploy all VM of first Topology
		// TODO: separate deployment Node of Topology
		String topoId = ToscaStructureQuery.getFirstServiceTemplate(def).getId();	
		//engine.deployConcurrentVMNodes(deployID.toString(), topoId, mapNodeAndRep, def);
		// call to the service to deploy multiple concurent
		SalsaCenterConnector centerCon = new SalsaCenterConnector(SalsaConfiguration.getSalsaCenterEndpoint(), deployID.toString(), "/tmp", EngineLogger.logger);		
				
		for (Map.Entry<String, Integer> map : mapNodeAndRep.entrySet()) {
			centerCon.updateNodeIdCounter(topoId, map.getKey(), 0);
			EngineLogger.logger.debug("Deploying new service, Vm concurent: " + map.getKey() +" - " + map.getValue());		
			deployMoreInstance(deployID.toString(), topoId, map.getKey(),map.getValue());			
		}
		
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
		EngineLogger.logger.debug("1-----");
		TDefinitions def = centerCon.getToscaDescription();
		EngineLogger.logger.debug("2-----");
		CloudService service = centerCon.getUpdateCloudServiceRuntime();
		EngineLogger.logger.debug("3------");
		ServiceUnit node = service.getComponentById(topologyId, nodeId);
		
		if (node.getType().equals(SalsaEntityType.OPERATING_SYSTEM.getEntityTypeString())){
			centerCon.updateNodeIdCounter(topologyId, nodeId, node.getIdCounter()+quantity); // update first the number + quantity
			deployMoreInstance_VM(topologyId, nodeId, quantity, def, service);			
		} else {
			deployMoreInstance_Artifact(topologyId, nodeId, quantity, def, service);
		}
	}
	
	private void deployMoreInstance_Artifact(String topologyId, String nodeId, int quantity, TDefinitions def, CloudService service){
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
	private void deployMoreInstance_VM(String topologyId, String nodeId, int quantity, TDefinitions def, CloudService service){		
		DeploymentEngineNodeLevel engine = new DeploymentEngineNodeLevel(configFile);		
		
		// TODO: implement to manage topology, current: get the first		
		String topoId = service.getFirstTopology().getId();
		ServiceUnit node = service.getComponentById(topoId, nodeId);
		
		int startingId=node.getIdCounter();
		
		engine.deployConcurentVMNodesOfOneType(service.getId(), topoId, nodeId, quantity, startingId, def);
//		Map<String, Integer> nodeAndNumber = new HashMap<>();
//		engine.deployConcurrentVMNodes(service.getId(), topologyId, nodeAndNumber, def);
		
	}
	
	// TODO: implementation
	public static void deployMoreArtifactInstance(String deployID, String nodeId, String hostNodeId, String hostInstanceId, TDefinitions def){
		
	}
	
	public boolean removeOneInstance(String serviceId, String topologyId, String nodeId, int instanceId){
		SalsaCenterConnector centerCon = new SalsaCenterConnector(SalsaConfiguration.getSalsaCenterEndpoint(), serviceId, "/tmp", EngineLogger.logger);
		CloudService service = centerCon.getUpdateCloudServiceRuntime();		
		ServiceUnit node = service.getComponentById(topologyId, nodeId);
		// remove VM node by invoke MultiCloudConnector
		if (node.getType().equals(SalsaEntityType.OPERATING_SYSTEM.getEntityTypeString())){
			ServiceInstance vm = node.getInstanceById(instanceId);
			SalsaInstanceDescription_VM vmProps = (SalsaInstanceDescription_VM)vm.getProperties().getAny();
			
			MultiCloudConnector cloudCon= new MultiCloudConnector(EngineLogger.logger,configFile);
			String providerName = vmProps.getProvider();
			String cloudInstanceId = vmProps.getInstanceId();
			EngineLogger.logger.debug("Removing virtual machine. Provider: " + providerName + "InstanceId: " + instanceId);				
			cloudCon.removeInstance(SalsaCloudProviders.fromString(providerName), cloudInstanceId);
			
			//centerCon.removeOneInstance(serviceId, topologyId, nodeId, instanceId);
			return true;
		} else {
			return false;
		}
	}
	
	private static CloudService buildRuntimeDataFromTosca(TDefinitions def){
		EngineLogger.logger.debug("Building runtime from Tosca file");
		CloudService service = new CloudService();
		List<TServiceTemplate> serviceTemplateLst = ToscaStructureQuery.getServiceTemplateList(def);
		for (TServiceTemplate st : serviceTemplateLst) {
			ServiceTopology topo = new ServiceTopology();
			topo.setId(st.getId());
			topo.setName(st.getName());
			List<TNodeTemplate> nodes = ToscaStructureQuery.getNodeTemplateList(st);	// all other nodes
			List<TRelationshipTemplate> relas_hoston = ToscaStructureQuery.getRelationshipTemplateList(SalsaRelationshipType.HOSTON.getRelationshipTypeString(),st);
			List<TRelationshipTemplate> relas_connectto = ToscaStructureQuery.getRelationshipTemplateList(SalsaRelationshipType.CONNECTTO.getRelationshipTypeString(),st);
			EngineLogger.logger.debug("Number of HostOn relationships: " + relas_hoston.size());
			for (TNodeTemplate node : nodes) {	
				ServiceUnit nodeData = new ServiceUnit(node.getId(), node.getType().getLocalPart());
				nodeData.setState(SalsaEntityState.UNDEPLOYED);
				nodeData.setName(node.getName());
				// add the artifact type for SOFTWARE NODE
				if (!node.getType().getLocalPart().equals(SalsaEntityType.OPERATING_SYSTEM.getEntityTypeString())){
					nodeData.setArtifactType(node.getDeploymentArtifacts().getDeploymentArtifact().get(0).getArtifactType().getLocalPart());
				}
				// find what is the host of this node, add to hostId
				for (TRelationshipTemplate rela : relas_hoston) { // search on all relationship, find the host of this "node"
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
				// find the connect to node, add it to connecttoId
				// this node will be the requirement, connect to capability (reverse with the Tosca)
				
				for (TRelationshipTemplate rela:relas_connectto) {
					EngineLogger.logger.debug("buildRuntimeDataFromTosca. Let's see relationship connectto: " + rela.getId());
					TRequirement targetReq = (TRequirement)rela.getTargetElement().getRef();
					TNodeTemplate target = ToscaStructureQuery.getNodetemplateOfRequirementOrCapability(targetReq.getId(), def);
					
					if (target.getId().equals(node.getId())){
						EngineLogger.logger.debug("buildRuntimeDataFromTosca. Found the target id: " + target.getId());
						TCapability sourceCapa = (TCapability)rela.getSourceElement().getRef();
						EngineLogger.logger.debug("buildRuntimeDataFromTosca. Source capa: " + sourceCapa.getId());
						TNodeTemplate source = ToscaStructureQuery.getNodetemplateOfRequirementOrCapability(sourceCapa.getId(), def);
						EngineLogger.logger.debug("buildRuntimeDataFromTosca. Source  " + source.getId());
						nodeData.getConnecttoId().add(source.getId());		
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
		CloudService service = centerCon.getUpdateCloudServiceRuntime();
		if (service == null) {
			EngineLogger.logger.error("Cannot clean service. Service description is not found.");
			return false;
		}
		List<ServiceInstance> repLst = service.getAllReplicaByType(SalsaEntityType.OPERATING_SYSTEM);		
		for (ServiceInstance rep : repLst) {
			if (rep.getProperties() != null){
				SalsaInstanceDescription_VM instance = (SalsaInstanceDescription_VM)rep.getProperties().getAny();				
				MultiCloudConnector cloudCon= new MultiCloudConnector(EngineLogger.logger,configFile);
				String providerName = instance.getProvider();
				String instanceId = instance.getInstanceId();
				EngineLogger.logger.debug("Removing virtual machine. Provider: " + providerName + "InstanceId: " + instanceId);				
				cloudCon.removeInstance(SalsaCloudProviders.fromString(providerName), instanceId);
			}			
		}
		//centerCon.deregisterService();
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
