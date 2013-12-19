package at.ac.tuwien.dsg.cloud.salsa.engine.impl;

import generated.oasis.tosca.TDefinitions;
import generated.oasis.tosca.TNodeTemplate;
import generated.oasis.tosca.TServiceTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import at.ac.tuwien.dsg.cloud.salsa.common.model.SalsaCloudServiceData;
import at.ac.tuwien.dsg.cloud.salsa.common.model.SalsaComponentData;
import at.ac.tuwien.dsg.cloud.salsa.common.model.SalsaComponentReplicaData;
import at.ac.tuwien.dsg.cloud.salsa.common.model.SalsaTopologyData;
import at.ac.tuwien.dsg.cloud.salsa.common.model.enums.SalsaEntityState;
import at.ac.tuwien.dsg.cloud.salsa.common.model.enums.SalsaEntityType;
import at.ac.tuwien.dsg.cloud.salsa.common.processes.SalsaCenterConnector;
import at.ac.tuwien.dsg.cloud.salsa.common.processes.SalsaXmlDataProcess;
import at.ac.tuwien.dsg.cloud.salsa.engine.utils.EngineLogger;
import at.ac.tuwien.dsg.cloud.salsa.engine.utils.SalsaConfiguration;
import at.ac.tuwien.dsg.cloud.salsa.tosca.extension.SalsaInstanceDescription;
import at.ac.tuwien.dsg.cloud.salsa.tosca.processing.ToscaStructureQuery;
import at.ac.tuwien.dsg.cloud.salsa.tosca.processing.ToscaXmlProcess;


public class SalsaToscaDeployer {

	/**
	 * Deploy a new service and return a running data
	 * @param def
	 * @return
	 */
	public static SalsaCloudServiceData deployNewService (TDefinitions def){
		// deploy all service Template 
		List<TNodeTemplate> lst = ToscaStructureQuery.getNodeTemplatesOfTypeList("OPERATING_SYSTEM", def);
		Map<String, Integer> mapNodeAndRep = new HashMap<>();
		UUID deployID = UUID.randomUUID();
		EngineLogger.logger.info("Deploying service id: "+deployID.toString());
		
		for (TNodeTemplate node : lst) {
			mapNodeAndRep.put(node.getId(), node.getMinInstances());
		}
		DeploymentEngineNodeLevel engine = new DeploymentEngineNodeLevel();
		
		// register service, all state is INITIAL
		String fullToscaFile="/tmp/"+deployID.toString();
		//resetServiceNodeState(def);
		ToscaXmlProcess.writeToscaDefinitionToFile(def, fullToscaFile);
		engine.submitService(fullToscaFile);
		
		// register service running data		
		String fullSalsaDataFile = "/tmp/"+deployID.toString()+".data";
		SalsaCloudServiceData serviceData = buildRuntimeDataFromTosca(def);
		SalsaXmlDataProcess.writeCloudServiceToFile(serviceData, fullSalsaDataFile);
		engine.submitService(fullSalsaDataFile);
		
		// deploy all VM of first Topology
		// TODO: separate deployment Node of Topology
		String topoId = ToscaStructureQuery.getFirstServiceTemplate(def).getId();
		engine.deployConcurrentVMNodes(deployID.toString(), topoId, mapNodeAndRep, def);
		
		// delete tmp topology file
		//File file = new File(fullToscaFile);
		//file.delete();
		
		
		EngineLogger.logger.info("Deployed VMs for service: " + deployID.toString());
		
		
		
		return null;
	}
	
	private static SalsaCloudServiceData buildRuntimeDataFromTosca(TDefinitions def){
		SalsaCloudServiceData service = new SalsaCloudServiceData();
		List<TServiceTemplate> serviceTemplateLst = ToscaStructureQuery.getServiceTemplateList(def);
		for (TServiceTemplate st : serviceTemplateLst) {
			SalsaTopologyData topo = new SalsaTopologyData();
			topo.setId(st.getId());
			topo.setName(st.getName());
			List<TNodeTemplate> nodes = ToscaStructureQuery.getNodeTemplateList(st);
			for (TNodeTemplate node : nodes) {
				SalsaComponentData nodeData = new SalsaComponentData(node.getId(), node.getType().getLocalPart());
				nodeData.setState(SalsaEntityState.UNDEPLOYED);
				nodeData.setName(node.getName());								
				topo.addComponent(nodeData);
			}			
			service.addComponentTopology(topo);
		}
		return service;
	}
	
	
	
	
	public static boolean cleanAllService (String serviceId){
		// TODO: implement it
		//List<TNodeTemplate> lst = ToscaStructureQuery.getNodeTemplatesOfTypeList("OPERATING_SYSTEM", def);
		SalsaCenterConnector centerCon = new SalsaCenterConnector(SalsaConfiguration.getSalsaCenterEndpoint(), serviceId, "", EngineLogger.logger);
		SalsaCloudServiceData service = centerCon.getUpdateCloudServiceRuntime();
		if (service == null) {
			return false;
		}
		List<SalsaComponentReplicaData> repLst = service.getAllReplicaByType(SalsaEntityType.OPERATING_SYSTEM);		
		for (SalsaComponentReplicaData rep : repLst) {
			if (rep.getProperties() != null){
				SalsaInstanceDescription instance = (SalsaInstanceDescription)rep.getProperties().getAny();				
				MultiCloudConnector cloudCon= new MultiCloudConnector();
				String providerName = instance.getProvider().getCloudProviderString();
				String instanceId = instance.getInstanceId();
				EngineLogger.logger.debug("Removing virtual machine. Provider: " + providerName + "InstanceId: " + instanceId);				
				cloudCon.removeInstance(providerName, instanceId);
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
	public static void deployAdditionService(String serviceId, String deployId){
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
