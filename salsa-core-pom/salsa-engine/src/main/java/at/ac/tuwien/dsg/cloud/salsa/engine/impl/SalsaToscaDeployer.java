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

import javax.ws.rs.core.Response;

import at.ac.tuwien.dsg.cloud.salsa.cloud_connector.multiclouds.MultiCloudConnector;
import at.ac.tuwien.dsg.cloud.salsa.cloud_connector.multiclouds.SalsaCloudProviders;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.CloudService;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.ServiceInstance;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.ServiceTopology;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.ServiceUnit;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.enums.SalsaEntityState;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.enums.SalsaEntityType;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.enums.SalsaRelationshipType;
import at.ac.tuwien.dsg.cloud.salsa.common.interfaces.SalsaEngineIntenalInterface;
import at.ac.tuwien.dsg.cloud.salsa.common.processing.SalsaCenterConnector;
import at.ac.tuwien.dsg.cloud.salsa.common.processing.SalsaXmlDataProcess;
import at.ac.tuwien.dsg.cloud.salsa.engine.services.SalsaEngineInternal;
import at.ac.tuwien.dsg.cloud.salsa.engine.utils.EngineLogger;
import at.ac.tuwien.dsg.cloud.salsa.engine.utils.SalsaConfiguration;
import at.ac.tuwien.dsg.cloud.salsa.tosca.extension.SalsaInstanceDescription_VM;
import at.ac.tuwien.dsg.cloud.salsa.tosca.processing.ToscaStructureQuery;
import at.ac.tuwien.dsg.cloud.salsa.tosca.processing.ToscaXmlProcess;

public class SalsaToscaDeployer {
	
	// some hard-code variables
	static final String CLOUD_NODE_NAME=SalsaEntityType.OPERATING_SYSTEM.getEntityTypeString();
	File configFile;
	static SalsaCenterConnector centerCon = new SalsaCenterConnector(SalsaConfiguration.getSalsaCenterEndpoint(), "/tmp", EngineLogger.logger);
	
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
		
		Map<String, Integer> mapNodeAndRep = new HashMap<>();
		//UUID deployID = UUID.randomUUID();
		//UUID deployID = UUID.fromString(serviceName);
		String deployID = serviceName;
		EngineLogger.logger.info("Deploying service id: "+deployID.toString());
		
		String ogininalToscaFile = SalsaConfiguration.getServiceStorageDir()+"/"+deployID.toString() + ".original";
		ToscaXmlProcess.writeToscaDefinitionToFile(def, ogininalToscaFile);
		
		// ENRICH
		ToscaEnricher enricher = new ToscaEnricher(def);
		enricher.enrichHighLevelTosca();		
		
		// deploy all service Template 		
		List<TNodeTemplate> lst = ToscaStructureQuery.getNodeTemplatesOfTypeList(CLOUD_NODE_NAME, def);
		for (TNodeTemplate node : lst) {
			mapNodeAndRep.put(node.getId(), node.getMinInstances());
		}
		
		// register service, all state is INITIAL
		String fullToscaFile = SalsaConfiguration.getServiceStorageDir()+"/"+deployID.toString();
		
		ToscaXmlProcess.writeToscaDefinitionToFile(def, fullToscaFile);	
		
		// register service running data
		String fullSalsaDataFile = SalsaConfiguration.getServiceStorageDir()+"/"+deployID.toString()+".data";
		CloudService serviceData = buildRuntimeDataFromTosca(def);
		serviceData.setId(deployID.toString());
		serviceData.setName(def.getId());
		SalsaXmlDataProcess.writeCloudServiceToFile(serviceData, fullSalsaDataFile);
				
		// deploy all VM of first Topology
		// TODO: separate deployment Node of Topology
		//String topoId = ToscaStructureQuery.getFirstServiceTemplate(def).getId();	
		//engine.deployConcurrentVMNodes(deployID.toString(), topoId, mapNodeAndRep, def);
		// call to the service to deploy multiple concurent
				
		for (Map.Entry<String, Integer> map : mapNodeAndRep.entrySet()) {
			centerCon.updateNodeIdCounter(deployID.toString(), serviceData.getTopologyOfNode(map.getKey()).getId(), map.getKey(), 0);
			EngineLogger.logger.debug("Deploying new service, Vm concurent: " + map.getKey() +" - " + map.getValue());		
			firstDeploymentService(deployID.toString(), serviceData.getTopologyOfNode(map.getKey()).getId(), map.getKey(),map.getValue());			
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
	public void deployOneMoreInstance(String serviceId, String topologyId, String nodeId, int instanceId, TDefinitions def, CloudService service){
		ServiceUnit node = service.getComponentById(topologyId, nodeId);
		EngineLogger.logger.debug("Node type: " + node.getType() + ". String: " +SalsaEntityType.OPERATING_SYSTEM.getEntityTypeString());
		ServiceInstance repData = new ServiceInstance(instanceId, null);
		repData.setState(SalsaEntityState.ALLOCATING);
		
		if (node.getType().equals(SalsaEntityType.OPERATING_SYSTEM.getEntityTypeString())){
			if (node.getInstanceNumber() >= node.getMax()){
				EngineLogger.logger.error("Not enough cloud resource quota for this node: " +nodeId+ ". Quit !");
				return;
			} else {
				centerCon.addInstanceUnitMetaData(serviceId, topologyId, nodeId, repData);
				new Thread(new deployOneVmThread(serviceId, topologyId, nodeId, instanceId, def)).start();
			}
		} else {
			centerCon.addInstanceUnitMetaData(serviceId, topologyId, nodeId, repData);
			deployOneMoreInstance_Artifact(topologyId, nodeId, instanceId, def, service);
		}
	}
	
	
	public boolean firstDeploymentService(String serviceId, String topologyId, String nodeId, int quantity){
				
		CloudService service = centerCon.getUpdateCloudServiceRuntime(serviceId);
		ServiceUnit node = service.getComponentById(topologyId, nodeId);
		
		if (node.getType().equals(SalsaEntityType.OPERATING_SYSTEM.getEntityTypeString())){
			centerCon.updateNodeIdCounter(serviceId, topologyId, nodeId, node.getIdCounter()+quantity); // update first the number + quantity
			SalsaEngineIntenalInterface serviceInternal = new SalsaEngineInternal();
			serviceInternal.spawnInstance(serviceId, topologyId, nodeId, quantity);			
		} 
		return true;
	}
	
	
	private void deployOneMoreInstance_Artifact(String topologyId, String nodeId, int instanceId, TDefinitions def, CloudService service){
		// find the hosted node of this node
		EngineLogger.logger.debug("Start the deployment of software stacks. Node id: " + nodeId);
		
		ServiceUnit unit = service.getComponentById(topologyId, nodeId);
		EngineLogger.logger.debug("NodeId: " + unit.getId());
		ServiceUnit hostedUnit = service.getComponentById(topologyId, unit.getHostedId());
		EngineLogger.logger.debug("Hosted id:  " + hostedUnit.getId());
		// decide which hostedUnit will be used, or create another one
		List<ServiceInstance> hostedInstances = hostedUnit.getInstancesList();
		ServiceInstance suitableHostedInstance = null;
		int hostInstanceId=0;
		for (ServiceInstance hostedInst : hostedInstances) {
			//List<ServiceInstance> instancesNumOnThisNode = unit.getInstanceHostOn(hostedInst.getInstanceId());
			EngineLogger.logger.debug("On node: " + hostedUnit.getId() + "/" + hostedInst.getInstanceId() + " currently has " + unit.getInstanceHostOn(hostedInst.getInstanceId()).size() +" node " + unit.getId());
			if (unit.getInstanceHostOn(hostedInst.getInstanceId()).size() < unit.getMax()){
				suitableHostedInstance = hostedInst;
				hostInstanceId=hostedInst.getInstanceId();
				EngineLogger.logger.debug("DEPLOY MORE INSTANCE. FOUND EXISTED HOST: " + hostInstanceId);
				break;
			}
		}
		
		// if there is no suitable host, create new one:
		if (suitableHostedInstance == null){
			EngineLogger.logger.debug("DEPLOY MORE INSTANCE. No existing host node, create new node: " + hostedUnit.getId() + " to deploy: " + nodeId);
			SalsaEngineIntenalInterface serviceLayerDeployer = new SalsaEngineInternal();			
			Response res = serviceLayerDeployer.spawnInstance(service.getId(), topologyId, hostedUnit.getId(), 1);
			if (res.getStatus()==201){
				try {Thread.sleep(2000); } catch (Exception e) {}	// wait a bit for the node is update				
				hostInstanceId = Integer.parseInt(((String)res.getEntity()).trim());
				service = centerCon.getUpdateCloudServiceRuntime(service.getId());				
			} else {
				EngineLogger.logger.debug("Could not create host node " + hostedUnit.getId() + "/" + hostInstanceId + " for deploying node: " + nodeId);
				return;
			}
		}
		
		// for testing, get the first OSNode:
		EngineLogger.logger.debug("DEPLOY MORE INSTANCE. FOUND EXISTED HOST (2nd time): " + hostInstanceId);
		suitableHostedInstance = service.getInstanceById(topologyId, hostedUnit.getId(), hostInstanceId);
		
		//deployMoreArtifactInstance(deployID, nodeId, hostNodeId, hostInstanceId, def);
		if (suitableHostedInstance == null){
			//deployMoreInstance(service.getId(), topologyId, hostedUnit.getId(), 1);
			EngineLogger.logger.debug("Hosted node is null");
			return;
		}
		
		EngineLogger.logger.debug("Hosted node type: " + hostedUnit.getType());
		
		if (hostedUnit.getType().equals(SalsaEntityType.OPERATING_SYSTEM.getEntityTypeString())){			
			EngineLogger.logger.debug("Call the pioneer to deploy the artifact");
			
			// create instance data with allocating state (pioneer do it before)
			EngineLogger.logger.debug("Salsa engine add instance data");			
				CloudService newService = centerCon.getUpdateCloudServiceRuntime(service.getId());
				EngineLogger.logger.debug("Get new service data 1: " + nodeId +"/" + instanceId);
				ServiceInstance data = newService.getInstanceById(topologyId, nodeId, instanceId);
				EngineLogger.logger.debug("Get new service data 2: hostinstanceId" + hostInstanceId);				
				EngineLogger.logger.debug("Get new service data 2: data.instanceId" + data.getInstanceId());
				data.setHostedId_Integer(hostInstanceId);
				EngineLogger.logger.debug("Get new service data 3");
				EngineLogger.logger.debug("Data: " + data.getInstanceId());
				data.setHostedId_Integer(hostInstanceId);
				data.setState(SalsaEntityState.ALLOCATING);	// waiting for other conditions
				centerCon.addInstanceUnitMetaData(service.getId(), topologyId, nodeId, data);				
				
				EngineLogger.logger.debug("Start checking Pioneer health");
				// wait for the VM is spawned
				CloudService updateService = centerCon.getUpdateCloudServiceRuntime(service.getId());
				suitableHostedInstance = updateService.getInstanceById(topologyId, hostedUnit.getId(), suitableHostedInstance.getInstanceId());
				while(!suitableHostedInstance.getState().equals(SalsaEntityState.RUNNING)){
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {}					
					updateService = centerCon.getUpdateCloudServiceRuntime(service.getId());
					suitableHostedInstance = updateService.getInstanceById(topologyId, hostedUnit.getId(), suitableHostedInstance.getInstanceId());				
				}
				
					SalsaInstanceDescription_VM vm = (SalsaInstanceDescription_VM) suitableHostedInstance.getProperties().getAny();
					PioneerConnector pioneer = new PioneerConnector(vm.getPrivateIp());
					// wait for the hosted instance become ready
					System.out.println("Pioneer health: " + pioneer.checkHealth());
					int count=0;
					while(!pioneer.checkHealth()){
						try { 
							System.out.println("Pioneer is not ready for node: " + nodeId +"/" + instanceId);							
							Thread.sleep(5000); 
						} catch(InterruptedException e2){}
						count++;
						if (count>60){
							break;
						}
					}
										
					EngineLogger.logger.debug("Connect to pionner at IP: " + vm.getPrivateIp());
					pioneer.deploySoftwareNode(nodeId, instanceId);
		} else {
			// TODO: Implement if we have more than 1 software level
			EngineLogger.logger.debug("Multi software stack is not supported yet: " + hostedUnit.getType());
		}
		EngineLogger.logger.debug("Deploy software instance done !");		
		// check if there is an exist node instance can be used for deploying the software ?
		
		//PioneerConnector pioneer = new PioneerConnector(ip);
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
//	private boolean deployMoreInstance_VM(String topologyId, String nodeId, int quantity, TDefinitions def, CloudService service){		
//		DeploymentEngineNodeLevel engine = new DeploymentEngineNodeLevel(configFile);		
//		
//		// TODO: implement to manage topology, current: get the first		
//		String topoId = service.getFirstTopology().getId();
//		ServiceUnit node = service.getComponentById(topoId, nodeId);
//		
//		EngineLogger.logger.debug("VM node: " + node.getId() + " currently has: " + node.getInstanceNumber() + "/" + node.getMax());
//		if (node.getInstanceNumber() + quantity > node.getMax()){
//			EngineLogger.logger.error("Not enough cloud resource quota for this node: " +nodeId+ ". Quit !");
//			return false;
//		}
//		
//		int startingId=node.getIdCounter();
//				
//		engine.deployConcurentVMNodesOfOneType(service.getId(), topoId, nodeId, quantity, startingId, def);
//		return true;
////		Map<String, Integer> nodeAndNumber = new HashMap<>();
////		engine.deployConcurrentVMNodes(service.getId(), topologyId, nodeAndNumber, def);		
//	}
	
	private class deployOneVmThread implements Runnable {		
		TDefinitions def;
		String serviceId;
		String topologyId;
		String nodeId;
		int instanceId;

		public deployOneVmThread(String serviceId, String topologyId, String nodeId, int instanceId, TDefinitions def) {	
			EngineLogger.logger.debug("Thread processind: nodeId=" + nodeId +", instance no.=" + instanceId);			
			this.def = def;
			this.serviceId = serviceId;
			this.topologyId = topologyId;
			this.nodeId = nodeId;
			this.instanceId = instanceId;
		}

		private synchronized ServiceInstance executeDeploymentNode() {			
			//centerCon.addInstanceUnit(serviceId, topologyId, nodeId, replica);
			EngineLogger.logger.debug("Debug 4 - execute deployment node");
			DeploymentEngineNodeLevel engine = new DeploymentEngineNodeLevel(configFile);
			EngineLogger.logger.debug("Debug 5 - execute deployment node");
			return engine.deployVMNode(serviceId, topologyId, nodeId, instanceId, def);
		}

		@Override
		public void run() {
			EngineLogger.logger.debug("Debug 6 - execute deployment node");
			executeDeploymentNode();
		}

	}
	
	
	public boolean removeOneInstance(String serviceId, String topologyId, String nodeId, int instanceId){
		CloudService service = centerCon.getUpdateCloudServiceRuntime(serviceId);		
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
			EngineLogger.logger.debug("Removing a software node somewhere: " + nodeId + "/" + instanceId);
			ServiceUnit hostNode = service.getComponentById(topologyId, node.getHostedId());
			EngineLogger.logger.debug("hostNode id: " + hostNode.getId());
			while (!hostNode.getType().equals(SalsaEntityType.OPERATING_SYSTEM.getEntityTypeString())){
				hostNode = service.getComponentById(topologyId, hostNode.getId());
				EngineLogger.logger.debug("other hostNode id: " + hostNode.getId());
			}
			ServiceInstance instance = node.getInstanceById(instanceId);
			EngineLogger.logger.debug("removeOneInstance - instanceid: " + instance.getInstanceId());
			int hostInstanceId = instance.getHostedId_Integer();
			EngineLogger.logger.debug("removeOneInstance - hostInstanceId: " + hostInstanceId);
			ServiceInstance vm_instance = hostNode.getInstanceById(hostInstanceId);
			EngineLogger.logger.debug("removeOneInstance - vm instance: " + vm_instance.getInstanceId());
			SalsaInstanceDescription_VM vm = (SalsaInstanceDescription_VM) vm_instance.getProperties().getAny();
			EngineLogger.logger.debug("removeOneInstance - vm ip: " + vm.getPrivateIp());
			
			PioneerConnector pioneer = new PioneerConnector(vm.getPrivateIp());
			pioneer.removeSoftwareNode(nodeId, instanceId);
			
			return true;
		}
	}
	
	private static CloudService buildRuntimeDataFromTosca(TDefinitions def){
		EngineLogger.logger.debug("Building runtime from Tosca file");
		CloudService service = new CloudService();
		service.setState(SalsaEntityState.UNDEPLOYED);		
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
				nodeData.setMin(node.getMinInstances());				
				if (node.getMaxInstances().equals("unbounded")){
					nodeData.setMax(10);	// max for experiments
				} else {
					nodeData.setMax(Integer.parseInt(node.getMaxInstances()));					
				}
				// add the artifact type for SOFTWARE NODE
				//if (!node.getType().getLocalPart().equals(SalsaEntityType.OPERATING_SYSTEM.getEntityTypeString())){
				if (node.getType().getLocalPart().equals(SalsaEntityType.SOFTWARE.getEntityTypeString())){
					nodeData.setArtifactType(node.getDeploymentArtifacts().getDeploymentArtifact().get(0).getArtifactType().getLocalPart());
					String artID = node.getDeploymentArtifacts().getDeploymentArtifact().get(0).getArtifactRef().getLocalPart();
					String directURL = ToscaStructureQuery.getArtifactTemplateById(artID, def).getArtifactReferences().getArtifactReference().get(0).getReference();
					nodeData.setArtifactURL(directURL);
				}
				// find what is the host of this node, add to hostId
				for (TRelationshipTemplate rela : relas_hoston) { // search on all relationship, find the host of this "node"
															// note that, after convert, the capa and req are reverted.
					TEntityTemplate targetRela = (TEntityTemplate)rela.getTargetElement().getRef();
					TEntityTemplate sourceRela = (TEntityTemplate)rela.getSourceElement().getRef();
					TNodeTemplate target;
					TNodeTemplate source;
					if (targetRela.getClass().equals(TRequirement.class)){
						target = ToscaStructureQuery.getNodetemplateOfRequirementOrCapability(targetRela.getId(), def);
						source = ToscaStructureQuery.getNodetemplateOfRequirementOrCapability(sourceRela.getId(), def);
					} else {
						target = (TNodeTemplate) sourceRela;
						source = (TNodeTemplate) targetRela;
					}
					EngineLogger.logger.debug("Is the source with id: " + target.getId() + " same with " + nodeData.getId());
					if (target.getId().equals(nodeData.getId())){
						nodeData.setHostedId(source.getId());
						EngineLogger.logger.debug("Found the host of node "+nodeData.getId() +" which is id = " + source.getId());
					}
				}
				// find the connect to node, add it to connecttoId
				// this node will be the requirement, connect to capability (reverse with the Tosca)
				
				for (TRelationshipTemplate rela:relas_connectto) {
					EngineLogger.logger.debug("buildRuntimeDataFromTosca. Let's see relationship connectto: " + rela.getId());
					
					if (rela.getSourceElement().getRef().getClass().equals(TNodeTemplate.class)){
						TNodeTemplate sourceNode = (TNodeTemplate)rela.getSourceElement().getRef();
						if (sourceNode.getId().equals(node.getId())){
							TNodeTemplate targetNode = (TNodeTemplate)rela.getTargetElement().getRef();
							nodeData.getConnecttoId().add(targetNode.getId());
						}
					} else {
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
		CloudService service = centerCon.getUpdateCloudServiceRuntime(serviceId);
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
	
}
