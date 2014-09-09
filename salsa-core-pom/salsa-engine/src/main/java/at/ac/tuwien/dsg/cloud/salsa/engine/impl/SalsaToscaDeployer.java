package at.ac.tuwien.dsg.cloud.salsa.engine.impl;

import generated.oasis.tosca.TCapability;
import generated.oasis.tosca.TDefinitions;
import generated.oasis.tosca.TEntityTemplate;
import generated.oasis.tosca.TNodeTemplate;
import generated.oasis.tosca.TRelationshipTemplate;
import generated.oasis.tosca.TRequirement;
import generated.oasis.tosca.TServiceTemplate;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Response;

import at.ac.tuwien.dsg.cloud.salsa.cloud_connector.multiclouds.MultiCloudConnector;
import at.ac.tuwien.dsg.cloud.salsa.cloud_connector.multiclouds.SalsaCloudProviders;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.CloudService;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.ConfigurationCapability;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.SalsaEntity.ConfigurationCapabilities;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.ServiceInstance;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.ServiceTopology;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.ServiceUnit;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.enums.SalsaEntityState;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.enums.SalsaEntityType;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.enums.SalsaRelationshipType;
import at.ac.tuwien.dsg.cloud.salsa.common.interfaces.SalsaEngineServiceIntenal;
import at.ac.tuwien.dsg.cloud.salsa.common.processing.SalsaCenterConnector;
import at.ac.tuwien.dsg.cloud.salsa.common.processing.SalsaXmlDataProcess;
import at.ac.tuwien.dsg.cloud.salsa.engine.exception.SalsaEngineException;
import at.ac.tuwien.dsg.cloud.salsa.engine.services.SalsaEngineImplAll;
import at.ac.tuwien.dsg.cloud.salsa.engine.utils.EngineLogger;
import at.ac.tuwien.dsg.cloud.salsa.engine.utils.SalsaConfiguration;
import at.ac.tuwien.dsg.cloud.salsa.tosca.extension.SalsaInstanceDescription_VM;
import at.ac.tuwien.dsg.cloud.salsa.tosca.extension.SalsaMappingProperties;
import at.ac.tuwien.dsg.cloud.salsa.tosca.extension.SalsaMappingProperties.SalsaMappingProperty;
import at.ac.tuwien.dsg.cloud.salsa.tosca.processing.ToscaStructureQuery;
import at.ac.tuwien.dsg.cloud.salsa.tosca.processing.ToscaXmlProcess;

public class SalsaToscaDeployer {

	// some hard-code variables
	static final String CLOUD_NODE_NAME = SalsaEntityType.OPERATING_SYSTEM
			.getEntityTypeString();
	File configFile;
	static SalsaCenterConnector centerCon = new SalsaCenterConnector(
			SalsaConfiguration.getSalsaCenterEndpoint(), "/tmp",
			EngineLogger.logger);

	public SalsaToscaDeployer(File config) {
		configFile = config;
	}

	public void setConfigFile(File config) {
		configFile = config;
	}

	/**
	 * Deploy a new service and return a running data
	 * 
	 * @param def
	 * @return
	 */
	public CloudService deployNewService(TDefinitions def, String serviceName) throws SalsaEngineException {
		orchestating = false;
		if (configFile == null) {
			EngineLogger.logger.error("No config file specified");
			throw new SalsaEngineException("There is no SALSA configuation file specific. Please check /etc/salsa.properties.", true);			
		}

		Map<String, Integer> mapNodeAndRep = new HashMap<>();
		// UUID deployID = UUID.randomUUID();
		// UUID deployID = UUID.fromString(serviceName);
		String deployID = serviceName;
		EngineLogger.logger
				.info("Deploying service id: " + deployID.toString());

		String ogininalToscaFile = SalsaConfiguration.getServiceStorageDir()
				+ "/" + deployID.toString() + ".original";
		ToscaXmlProcess.writeToscaDefinitionToFile(def, ogininalToscaFile);

		// ENRICH
		ToscaEnricher enricher = new ToscaEnricher(def);
		enricher.enrichHighLevelTosca();

		// deploy all service Template
		List<TNodeTemplate> lst = ToscaStructureQuery
				.getNodeTemplatesOfTypeList(CLOUD_NODE_NAME, def);
		for (TNodeTemplate node : lst) {
			mapNodeAndRep.put(node.getId(), node.getMinInstances());
		}

		// register service, all state is INITIAL
		String fullToscaFile = SalsaConfiguration.getServiceStorageDir() + "/"
				+ deployID.toString();

		ToscaXmlProcess.writeToscaDefinitionToFile(def, fullToscaFile);

		// register service running data
		String fullSalsaDataFile = SalsaConfiguration.getServiceStorageDir()
				+ "/" + deployID.toString() + ".data";
		CloudService serviceData = buildRuntimeDataFromTosca(def);
		serviceData.setId(deployID.toString());
		serviceData.setName(def.getId());
		SalsaXmlDataProcess.writeCloudServiceToFile(serviceData,
				fullSalsaDataFile);

		// deploy all VM of first Topology
		// TODO: separate deployment Node of Topology
		// String topoId =
		// ToscaStructureQuery.getFirstServiceTemplate(def).getId();
		// engine.deployConcurrentVMNodes(deployID.toString(), topoId,
		// mapNodeAndRep, def);
		// call to the service to deploy multiple concurent

		for (Map.Entry<String, Integer> map : mapNodeAndRep.entrySet()) {
			centerCon.updateNodeIdCounter(deployID.toString(), serviceData
					.getTopologyOfNode(map.getKey()).getId(), map.getKey(), 0);
			EngineLogger.logger.debug("Deploying new service, Vm concurent: "
					+ map.getKey() + " - " + map.getValue());
			firstDeploymentService(deployID.toString(), serviceData
					.getTopologyOfNode(map.getKey()).getId(), map.getKey(),
					map.getValue());
		}

		EngineLogger.logger.info("Deployed VMs for service: "
				+ deployID.toString());
		return serviceData;
	}

	public CloudService orchestrateNewService(TDefinitions def,	String serviceName) throws SalsaEngineException {
		if (configFile == null) {
			EngineLogger.logger.error("No config file specified");
			throw new SalsaEngineException("There is no SALSA configuation file specific. Please check /etc/salsa.properties.", true);
		}

		//Map<String, Integer> mapNodeAndRep = new HashMap<>();
		String deployID = serviceName;
		EngineLogger.logger.info("Orchestrating service id: "
				+ deployID.toString());

		String ogininalToscaFile = SalsaConfiguration.getServiceStorageDir()
				+ "/" + deployID.toString() + ".original";
		ToscaXmlProcess.writeToscaDefinitionToFile(def, ogininalToscaFile);

		// ENRICH
		ToscaEnricher enricher = new ToscaEnricher(def);
		enricher.enrichHighLevelTosca();

		// register service, all state is INITIAL
		String fullToscaFile = SalsaConfiguration.getServiceStorageDir() + "/" 	+ deployID.toString();

		ToscaXmlProcess.writeToscaDefinitionToFile(def, fullToscaFile);

		EngineLogger.logger.debug("debugggg Sep 8 - 1");
		
		// register service running data
		String fullSalsaDataFile = SalsaConfiguration.getServiceStorageDir()
				+ "/" + deployID.toString() + ".data";
		EngineLogger.logger.debug("debugggg Sep 8 - 2");
		CloudService serviceData = buildRuntimeDataFromTosca(def);
		EngineLogger.logger.debug("debugggg Sep 8 - 3");
		serviceData.setId(deployID.toString());
		serviceData.setName(def.getId());
		SalsaXmlDataProcess.writeCloudServiceToFile(serviceData, fullSalsaDataFile);
		EngineLogger.logger.debug("debugggg Sep 8 - 4");

		// here find all the TOP node
		List<ServiceUnit> nodes = serviceData.getAllComponent();
		List<ServiceUnit> topNodes = new ArrayList<>();
		for (ServiceUnit node : nodes) {
			boolean getIt = true;
			for (ServiceUnit t : nodes) {
				if (t.getHostedId().equals(node.getId())) {
					getIt = false;
					EngineLogger.logger.debug("Orchestating: Discard node: " + node.getId());
					break;
				}
			}
			if (getIt) {
				EngineLogger.logger.debug("Orchestating: Get top node: "
						+ node.getId());
				topNodes.add(node);
			}
		}

		SalsaEngineServiceIntenal serviceInternal = new SalsaEngineImplAll();
		for (ServiceUnit unit : topNodes) {
			EngineLogger.logger.debug("Orchestating: Creating top node: "
					+ unit.getId());
			serviceInternal.spawnInstance(serviceData.getId(), serviceData
					.getTopologyOfNode(unit.getId()).getId(), unit.getId(), 1);
		}

		return serviceData;
	}

	static boolean orchestating = false;

	/**
	 * Deploy more instance of a node. Only support deploying VM Tosca node
	 * 
	 * @param serviceId
	 * @param topologyId
	 * @param nodeId
	 * @param quantity
	 */
	public boolean deployOneMoreInstance(String serviceId, String topologyId,
			String nodeId, int instanceId, TDefinitions def,
			CloudService service) throws SalsaEngineException {		
		CloudService newservice = centerCon.getUpdateCloudServiceRuntime(serviceId);
		ServiceUnit node = newservice.getComponentById(topologyId, nodeId);
		EngineLogger.logger.debug("Node type: " + node.getType() + ". String: "
				+ SalsaEntityType.OPERATING_SYSTEM.getEntityTypeString());
		ServiceInstance repData = new ServiceInstance(instanceId, null);
		repData.setState(SalsaEntityState.ALLOCATING);
		repData.setHostedId_Integer(2147483647);

		if (node.getType().equals(SalsaEntityType.OPERATING_SYSTEM.getEntityTypeString())) {
			if (node.getInstanceNumber() >= node.getMax()) {
				EngineLogger.logger.error("Not enough cloud resource quota for the node: " + nodeId + ". Quit !");
				// out of quota
				throw new SalsaEngineException("Not enough cloud resource quota to deploy the node: " + nodeId, false);				
//			} else {
//				// check number of OS instance with type 
//				Map<String, Integer> instNum = new HashMap<>();
//				for (ServiceInstance inst : node.getInstancesList()) {
//					SalsaInstanceDescription_VM vm = (SalsaInstanceDescription_VM) inst.getProperties().getAny();
//				}
			}			
			else {
				centerCon.addInstanceUnitMetaData(serviceId, topologyId, nodeId, repData);
				new Thread(new deployOneVmThread(serviceId, topologyId, nodeId,	instanceId, def)).start();
				//DeploymentEngineNodeLevel engine = new DeploymentEngineNodeLevel(configFile);
				//engine.deployVMNode(serviceId, topologyId, nodeId,instanceId, def);				
				return true;
			}
		} else {
			int count = 0;
			while (orchestating) {
				try {
					EngineLogger.logger.debug("Orchestrating blocked: " + nodeId + "/" + instanceId + ". Count: " + count);
					Thread.sleep(1000);
					count++;
					if (count > 50) {	// maximum 5 secs
						orchestating = false;
					}
				} catch (Exception e) {
				}
			}
			setLock();
			centerCon.addInstanceUnitMetaData(serviceId, topologyId, nodeId, repData);
			releaseLock();
			new Thread(new deployOneSoftwareThread(serviceId, topologyId, nodeId, instanceId, def)).start();
			
			return true;
		}
		
	}
	
	private static synchronized void setLock(){
		orchestating = true;
	}
	
	private static synchronized void releaseLock(){
		orchestating = false;
	}

	public boolean firstDeploymentService(String serviceId, String topologyId,
			String nodeId, int quantity) throws SalsaEngineException {
		CloudService service = centerCon
				.getUpdateCloudServiceRuntime(serviceId) ;
		ServiceUnit node = service.getComponentById(topologyId, nodeId);

		if (node.getType().equals(
				SalsaEntityType.OPERATING_SYSTEM.getEntityTypeString())) {
			centerCon.updateNodeIdCounter(serviceId, topologyId, nodeId,
					node.getIdCounter() + quantity); // update first the number
														// + quantity
			SalsaEngineServiceIntenal serviceInternal = new SalsaEngineImplAll();
			serviceInternal.spawnInstance(serviceId, topologyId, nodeId,
					quantity);
		}
		return true;
	}
	
	private boolean deployOneMoreInstance_Artifact(String serviceId, String topologyId,
			String nodeId, int instanceId, TDefinitions def) throws SalsaEngineException {
//		int count = 0;
//		while (orchestating) {
//			try {
//				EngineLogger.logger.debug("Orchestrating blocked: " + nodeId + "/" + instanceId + ". Count: " + count);
//				Thread.sleep(1000);
//				count++;
//				if (count > 50) {	// maximum 5 secs
//					orchestating = false;
//				}
//			} catch (Exception e) {
//			}
//		}
//		setLock();
		
		
		CloudService service = centerCon.getUpdateCloudServiceRuntime(serviceId);
		// find the hosted node of this node
		EngineLogger.logger.debug("Start the deployment of software stacks. Node id: " + nodeId);

		ServiceUnit unit = service.getComponentById(topologyId, nodeId);
		EngineLogger.logger.debug("NodeId: " + unit.getId());
		ServiceUnit hostedUnit = service.getComponentById(topologyId,	unit.getHostedId());
		EngineLogger.logger.debug("Hosted id:  " + hostedUnit.getId());
		// decide which hostedUnit will be used, or create another one
		List<ServiceInstance> hostedInstances = hostedUnit.getInstancesList();
		ServiceInstance suitableHostedInstance = null;
		int hostInstanceId = 0;
		for (ServiceInstance hostedInst : hostedInstances) {
			// List<ServiceInstance> instancesNumOnThisNode =
			// unit.getInstanceHostOn(hostedInst.getInstanceId());			
			EngineLogger.logger.debug("There are " + hostedInstances.size() + " instance(s) for " + hostedUnit.getId());
			EngineLogger.logger.debug("On node: " + hostedUnit.getId() + "/"
					+ hostedInst.getInstanceId() + " currently has "
					+ unit.getInstanceHostOn(hostedInst.getInstanceId()).size()
					+ " node " + unit.getId());
			String ids = "->";
			for (ServiceInstance instanceTmp : unit.getInstanceHostOn(hostedInst.getInstanceId())) {
				ids += instanceTmp.getInstanceId() +", ";
			}
			EngineLogger.logger.debug("And their IDs are: " + ids);
			if (unit.getInstanceHostOn(hostedInst.getInstanceId()).size() < unit.getMax()) {
				suitableHostedInstance = hostedInst;
				hostInstanceId = hostedInst.getInstanceId();
				EngineLogger.logger.debug("DEPLOY MORE INSTANCE. FOUND EXISTED HOST: " + hostedUnit.getId() + "/" + hostInstanceId);
				break;
			}
		}
		CloudService newService = null;
		// if there is no suitable host, create new one:
		if (suitableHostedInstance == null) {
			EngineLogger.logger.debug("DEPLOY MORE INSTANCE. No existing host node, create new node: "
							+ hostedUnit.getId() + " to deploy: " + nodeId);
			SalsaEngineServiceIntenal serviceLayerDeployer = new SalsaEngineImplAll();
			Response res = serviceLayerDeployer.spawnInstance(service.getId(), topologyId, hostedUnit.getId(), 1);
			if (res.getStatus() == 201) {				
				hostInstanceId = Integer.parseInt(((String) res.getEntity()).trim());
				EngineLogger.logger.debug("Just add new data of the instance: "	+ hostedUnit.getId() + "/" + hostInstanceId);				
				ServiceInstance hostInstance = null;
				while (hostInstance==null){	// wait for host instance
					newService = centerCon.getUpdateCloudServiceRuntime(service.getId());
					hostInstance = newService.getInstanceById(hostedUnit.getId(), hostInstanceId);
					try {
						Thread.sleep(2000);
					} catch (Exception e) {	}
				}				
			} else {
				EngineLogger.logger.debug("Could not create host node "
						+ hostedUnit.getId() + "/" + hostInstanceId
						+ " for deploying node: " + nodeId);
				releaseLock();
				throw new SalsaEngineException("Could not create host node " + hostedUnit.getId() + "/" + hostInstanceId + " for deploying node: " + nodeId,true);
			}
		}

		// for testing, get the first OSNode:
		EngineLogger.logger.debug("DEPLOY MORE INSTANCE. FOUND EXISTED HOST (2nd time): " + hostInstanceId);
		newService = centerCon.getUpdateCloudServiceRuntime(service.getId());
		suitableHostedInstance = newService.getInstanceById(topologyId,
				hostedUnit.getId(), hostInstanceId);

		// deployMoreArtifactInstance(deployID, nodeId, hostNodeId,
		// hostInstanceId, def);
		if (suitableHostedInstance == null) {
			// deployMoreInstance(service.getId(), topologyId,
			// hostedUnit.getId(), 1);
			EngineLogger.logger.debug("Hosted node is null");
			releaseLock();
			throw new SalsaEngineException("Couldn't find a node (null) to host node: " + nodeId, true);
		}

		EngineLogger.logger.debug("Hosted node: " + hostedUnit.getId() + "/"
				+ suitableHostedInstance.getInstanceId() + " type: "
				+ hostedUnit.getType());
		releaseLock();

		// if host in OS or DOCKER, set the status to STAGING. a Pioneer will
		// take it
		if (hostedUnit.getType().equals(SalsaEntityType.OPERATING_SYSTEM.getEntityTypeString())
		 || hostedUnit.getType().equals(SalsaEntityType.DOCKER.getEntityTypeString())
		 || hostedUnit.getType().equals(SalsaEntityType.TOMCAT.getEntityTypeString()) ) {
			newService = centerCon
					.getUpdateCloudServiceRuntime(service.getId());
			ServiceInstance data = newService.getInstanceById(topologyId,
					nodeId, instanceId);
			data.setHostedId_Integer(hostInstanceId);
			data.setState(SalsaEntityState.ALLOCATING); // waiting for other
														// conditions
			centerCon.addInstanceUnitMetaData(service.getId(), topologyId, nodeId, data);
			// waiting for hostInstance become RUNNING or FINISH
			while (!suitableHostedInstance.getState().equals(SalsaEntityState.RUNNING)
					&& !suitableHostedInstance.getState().equals(SalsaEntityState.FINISHED)) {
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
				}
				CloudService updateService = centerCon
						.getUpdateCloudServiceRuntime(service.getId());
				suitableHostedInstance = updateService.getInstanceById(
						topologyId, hostedUnit.getId(),
						suitableHostedInstance.getInstanceId());
			}
			EngineLogger.logger.debug("Set state to STAGING for node: "
					+ nodeId + "/" + instanceId + " which will be hosted on "
					+ hostedUnit.getId() + "/" + hostInstanceId);
			centerCon.updateNodeState(newService.getId(), topologyId, nodeId,
					instanceId, SalsaEntityState.STAGING);
		}

		//
		//
		//
		//
		//
		// // if host on os
		// if
		// (hostedUnit.getType().equals(SalsaEntityType.OPERATING_SYSTEM.getEntityTypeString())){
		// EngineLogger.logger.debug("Call the pioneer to deploy the artifact");
		//
		// // create instance data with allocating state (pioneer do it before)
		// EngineLogger.logger.debug("Salsa engine add instance data");
		// newService = centerCon.getUpdateCloudServiceRuntime(service.getId());
		// ServiceInstance data = newService.getInstanceById(topologyId, nodeId,
		// instanceId);
		// data.setHostedId_Integer(hostInstanceId);
		// data.setState(SalsaEntityState.ALLOCATING); // waiting for other
		// conditions
		// centerCon.addInstanceUnitMetaData(service.getId(), topologyId,
		// nodeId, data);
		//
		// EngineLogger.logger.debug("Start checking Pioneer health");
		// // wait for the VM is spawned
		// CloudService updateService =
		// centerCon.getUpdateCloudServiceRuntime(service.getId());
		// suitableHostedInstance = updateService.getInstanceById(topologyId,
		// hostedUnit.getId(), suitableHostedInstance.getInstanceId());
		// while(!suitableHostedInstance.getState().equals(SalsaEntityState.RUNNING)){
		// try {
		// Thread.sleep(3000);
		// } catch (InterruptedException e) {}
		// updateService =
		// centerCon.getUpdateCloudServiceRuntime(service.getId());
		// suitableHostedInstance = updateService.getInstanceById(topologyId,
		// hostedUnit.getId(), suitableHostedInstance.getInstanceId());
		// }
		//
		// SalsaInstanceDescription_VM vm = (SalsaInstanceDescription_VM)
		// suitableHostedInstance.getProperties().getAny();
		// PioneerConnector pioneer = new PioneerConnector(vm.getPrivateIp());
		// // wait for the hosted instance become ready
		// System.out.println("Pioneer health: " + pioneer.checkHealth());
		// int count=0;
		// while(!pioneer.checkHealth()){
		// try {
		// System.out.println("Pioneer is not ready for node: " + nodeId +"/" +
		// instanceId);
		// Thread.sleep(5000);
		// } catch(InterruptedException e2){}
		// count++;
		// if (count>60){
		// break;
		// }
		// }
		//
		// EngineLogger.logger.debug("Connect to pionner at IP: " +
		// vm.getPrivateIp());
		// pioneer.deploySoftwareNode(nodeId, instanceId);
		// } else {
		// // HostUnit here is a DOCKER, which the vmHOST is a OS node
		// if
		// (hostedUnit.getType().equals(SalsaEntityType.DOCKER.getEntityTypeString())){
		// EngineLogger.logger.debug("This node " + nodeId +"/" + instanceId
		// +" is hosted on a DOCKER: " + hostedUnit.getId() +"/" +
		// suitableHostedInstance.getInstanceId());
		// //suitableHostedInstance
		// newService = centerCon.getUpdateCloudServiceRuntime(service.getId());
		// ServiceInstance data = newService.getInstanceById(topologyId, nodeId,
		// instanceId);
		// data.setHostedId_Integer(hostInstanceId);
		// data.setState(SalsaEntityState.ALLOCATING); // waiting for other
		// conditions
		// centerCon.addInstanceUnitMetaData(service.getId(), topologyId,
		// nodeId, data); // just update
		// // find the VM of the hostedUnit
		// ServiceUnit hostedNodeVM =
		// newService.getComponentById(hostedUnit.getHostedId());
		// ServiceInstance hostedInstanceVM =
		// hostedNodeVM.getInstanceById(suitableHostedInstance.getHostedId_Integer());
		// EngineLogger.logger.debug("This node " + nodeId +"/" + instanceId
		// +" will transit through pioneer: " + hostedNodeVM.getId() +"/" +
		// hostedInstanceVM.getInstanceId());
		// SalsaInstanceDescription_VM vm = (SalsaInstanceDescription_VM)
		// hostedInstanceVM.getProperties().getAny();
		// PioneerConnector pioneer = new PioneerConnector(vm.getPrivateIp());
		// pioneer.deploySoftwareNode(nodeId, instanceId);
		// } else {
		// EngineLogger.logger.debug("More than 3 software stacks is not supported yet: "
		// + hostedUnit.getType());
		// }
		// }
		EngineLogger.logger.debug("Deploy more instance artifact is done !");
		return true;
		// check if there is an exist node instance can be used for deploying
		// the software ?

		// PioneerConnector pioneer = new PioneerConnector(ip);
	}

	/**
	 * 
	 * @param serviceId
	 *            exist service Id
	 * @param topologyId
	 * @param nodeId
	 * @param quantity
	 * @param def
	 * @param service
	 */
	// private boolean deployMoreInstance_VM(String topologyId, String nodeId,
	// int quantity, TDefinitions def, CloudService service){
	// DeploymentEngineNodeLevel engine = new
	// DeploymentEngineNodeLevel(configFile);
	//
	// // TODO: implement to manage topology, current: get the first
	// String topoId = service.getFirstTopology().getId();
	// ServiceUnit node = service.getComponentById(topoId, nodeId);
	//
	// EngineLogger.logger.debug("VM node: " + node.getId() + " currently has: "
	// + node.getInstanceNumber() + "/" + node.getMax());
	// if (node.getInstanceNumber() + quantity > node.getMax()){
	// EngineLogger.logger.error("Not enough cloud resource quota for this node: "
	// +nodeId+ ". Quit !");
	// return false;
	// }
	//
	// int startingId=node.getIdCounter();
	//
	// engine.deployConcurentVMNodesOfOneType(service.getId(), topoId, nodeId,
	// quantity, startingId, def);
	// return true;
	// // Map<String, Integer> nodeAndNumber = new HashMap<>();
	// // engine.deployConcurrentVMNodes(service.getId(), topologyId,
	// nodeAndNumber, def);
	// }

	private class deployOneVmThread implements Runnable {
		TDefinitions def;
		String serviceId;
		String topologyId;
		String nodeId;
		int instanceId;

		public deployOneVmThread(String serviceId, String topologyId,
				String nodeId, int instanceId, TDefinitions def) {
			EngineLogger.logger.debug("Thread processind: nodeId=" + nodeId
					+ ", instance no.=" + instanceId);
			this.def = def;
			this.serviceId = serviceId;
			this.topologyId = topologyId;
			this.nodeId = nodeId;
			this.instanceId = instanceId;
		}

		private synchronized ServiceInstance executeDeploymentNode() throws SalsaEngineException {
			// centerCon.addInstanceUnit(serviceId, topologyId, nodeId,
			// replica);
			EngineLogger.logger.debug("Debug 4 - execute deployment node");
			DeploymentEngineNodeLevel engine = new DeploymentEngineNodeLevel(configFile);
			return engine.deployVMNode(serviceId, topologyId, nodeId,instanceId, def);
		}

		@Override
		public void run() {
			EngineLogger.logger.debug("Debug 6 - execute deployment node");
			try {
				executeDeploymentNode();
			} catch (SalsaEngineException e){
				
			}
		}
	}
	
	private class deployOneSoftwareThread implements Runnable {
		TDefinitions def;
		String serviceId;
		String topologyId;
		String nodeId;
		int instanceId;

		public deployOneSoftwareThread(String serviceId, String topologyId,
				String nodeId, int instanceId, TDefinitions def) {
			EngineLogger.logger.debug("Thread processind: nodeId=" + nodeId
					+ ", instance no.=" + instanceId);
			this.def = def;
			this.serviceId = serviceId;
			this.topologyId = topologyId;
			this.nodeId = nodeId;
			this.instanceId = instanceId;
		}
		@Override
		public void run() {			
			try {
				deployOneMoreInstance_Artifact(serviceId, topologyId, nodeId, instanceId, def);
			} catch (SalsaEngineException e){
				EngineLogger.logger.error(e.getMessage());
			}
		}
	}

	public boolean removeOneInstance(String serviceId, String topologyId,
			String nodeId, int instanceId) throws SalsaEngineException {
		CloudService service = centerCon
				.getUpdateCloudServiceRuntime(serviceId);
		ServiceUnit node = service.getComponentById(topologyId, nodeId);
		// remove VM node by invoke MultiCloudConnector
		if (node.getType().equals(
				SalsaEntityType.OPERATING_SYSTEM.getEntityTypeString())) {
			ServiceInstance vm = node.getInstanceById(instanceId);
			SalsaInstanceDescription_VM vmProps = (SalsaInstanceDescription_VM) vm
					.getProperties().getAny();

			MultiCloudConnector cloudCon = new MultiCloudConnector(
					EngineLogger.logger, configFile);
			String providerName = vmProps.getProvider();
			String cloudInstanceId = vmProps.getInstanceId();
			EngineLogger.logger.debug("Removing virtual machine. Provider: "
					+ providerName + "InstanceId: " + instanceId);
			cloudCon.removeInstance(
					SalsaCloudProviders.fromString(providerName),
					cloudInstanceId);

			// centerCon.removeOneInstance(serviceId, topologyId, nodeId,
			// instanceId);
			return true;
		} else {
			EngineLogger.logger.debug("Removing a software node somewhere: "
					+ nodeId + "/" + instanceId);
			ServiceUnit hostNode = service.getComponentById(topologyId,
					node.getHostedId());
			EngineLogger.logger.debug("hostNode id: " + hostNode.getId());
			while (!hostNode.getType().equals(
					SalsaEntityType.OPERATING_SYSTEM.getEntityTypeString())) {
				hostNode = service.getComponentById(topologyId,
						hostNode.getId());
				EngineLogger.logger.debug("other hostNode id: "
						+ hostNode.getId());
			}
			ServiceInstance instance = node.getInstanceById(instanceId);
			EngineLogger.logger.debug("removeOneInstance - instanceid: "
					+ instance.getInstanceId());
			int hostInstanceId = instance.getHostedId_Integer();
			EngineLogger.logger.debug("removeOneInstance - hostInstanceId: "
					+ hostInstanceId);
			ServiceInstance vm_instance = hostNode
					.getInstanceById(hostInstanceId);
			EngineLogger.logger.debug("removeOneInstance - vm instance: "
					+ vm_instance.getInstanceId());
			SalsaInstanceDescription_VM vm = (SalsaInstanceDescription_VM) vm_instance
					.getProperties().getAny();
			EngineLogger.logger.debug("removeOneInstance - vm ip: "
					+ vm.getPrivateIp());

			PioneerConnector pioneer = new PioneerConnector(vm.getPrivateIp());
			pioneer.removeSoftwareNode(nodeId, instanceId);

			return true;
		}
	}

	public static CloudService buildRuntimeDataFromTosca(TDefinitions def) {
		EngineLogger.logger.debug("Building runtime from Tosca file");
		CloudService service = new CloudService();
		service.setState(SalsaEntityState.UNDEPLOYED);
		List<TServiceTemplate> serviceTemplateLst = ToscaStructureQuery.getServiceTemplateList(def);
		for (TServiceTemplate st : serviceTemplateLst) {
			ServiceTopology topo = new ServiceTopology();
			topo.setId(st.getId());
			topo.setName(st.getName());
			List<TNodeTemplate> nodes = ToscaStructureQuery
					.getNodeTemplateList(st); // all other nodes
			List<TRelationshipTemplate> relas_hoston = ToscaStructureQuery
					.getRelationshipTemplateList(SalsaRelationshipType.HOSTON
							.getRelationshipTypeString(), st);
			List<TRelationshipTemplate> relas_connectto = ToscaStructureQuery
					.getRelationshipTemplateList(
							SalsaRelationshipType.CONNECTTO
									.getRelationshipTypeString(), st);
			EngineLogger.logger.debug("Number of HostOn relationships: "
					+ relas_hoston.size());
			for (TNodeTemplate node : nodes) {
				ServiceUnit nodeData = new ServiceUnit(node.getId(), node
						.getType().getLocalPart());
				nodeData.setState(SalsaEntityState.UNDEPLOYED);
				nodeData.setName(node.getName());
				nodeData.setMin(node.getMinInstances());
				if (node.getMaxInstances().equals("unbounded")) {
					nodeData.setMax(10); // max for experiments
				} else {
					nodeData.setMax(Integer.parseInt(node.getMaxInstances()));
				}
				// add the artifact type for SOFTWARE NODE
				// if
				// (!node.getType().getLocalPart().equals(SalsaEntityType.OPERATING_SYSTEM.getEntityTypeString())){
				if (node.getType().getLocalPart().equals(SalsaEntityType.SOFTWARE.getEntityTypeString())) {
					if (node.getDeploymentArtifacts()!=null && node.getDeploymentArtifacts().getDeploymentArtifact().size()!=0){
						nodeData.setArtifactType(node.getDeploymentArtifacts()
								.getDeploymentArtifact().get(0).getArtifactType().getLocalPart());
						String artID = node.getDeploymentArtifacts()
								.getDeploymentArtifact().get(0).getArtifactRef()
								.getLocalPart();
						String directURL = ToscaStructureQuery
								.getArtifactTemplateById(artID, def)
								.getArtifactReferences().getArtifactReference()
								.get(0).getReference();
						nodeData.setArtifactURL(directURL);
					}
				}
				EngineLogger.logger.debug("debugggg Sep 8.1 - 1");
				// find what is the host of this node, add to hostId
				for (TRelationshipTemplate rela : relas_hoston) { // search on
																	// all
																	// relationship,
																	// find the
																	// host of
																	// this
																	// "node"
					// note that, after convert, the capa and req are reverted.
					TEntityTemplate targetRela = (TEntityTemplate) rela
							.getTargetElement().getRef();
					TEntityTemplate sourceRela = (TEntityTemplate) rela
							.getSourceElement().getRef();
					TNodeTemplate target;
					TNodeTemplate source;
					if (targetRela.getClass().equals(TRequirement.class)) {
						target = ToscaStructureQuery
								.getNodetemplateOfRequirementOrCapability(
										targetRela.getId(), def);
						source = ToscaStructureQuery
								.getNodetemplateOfRequirementOrCapability(
										sourceRela.getId(), def);
					} else {
						target = (TNodeTemplate) sourceRela;
						source = (TNodeTemplate) targetRela;
					}
					EngineLogger.logger
							.debug("Is the source with id: " + target.getId()
									+ " same with " + nodeData.getId());
					if (target.getId().equals(nodeData.getId())) {
						nodeData.setHostedId(source.getId());
						EngineLogger.logger.debug("Found the host of node "
								+ nodeData.getId() + " which is id = "
								+ source.getId());
					}
				}
				EngineLogger.logger.debug("debugggg Sep 8.1 - 2");
				// find the connect to node, add it to connecttoId
				// this node will be the requirement, connect to capability
				// (reverse with the Tosca)

				for (TRelationshipTemplate rela : relas_connectto) {
					EngineLogger.logger
							.debug("buildRuntimeDataFromTosca. Let's see relationship connectto: "
									+ rela.getId());

					if (rela.getSourceElement().getRef().getClass()
							.equals(TNodeTemplate.class)) {
						TNodeTemplate sourceNode = (TNodeTemplate) rela
								.getSourceElement().getRef();
						if (sourceNode.getId().equals(node.getId())) {
							TNodeTemplate targetNode = (TNodeTemplate) rela
									.getTargetElement().getRef();
							nodeData.getConnecttoId().add(targetNode.getId());
						}
					} else {
						TRequirement targetReq = (TRequirement) rela
								.getTargetElement().getRef();
						TNodeTemplate target = ToscaStructureQuery
								.getNodetemplateOfRequirementOrCapability(
										targetReq.getId(), def);

						if (target.getId().equals(node.getId())) {
							EngineLogger.logger
									.debug("buildRuntimeDataFromTosca. Found the target id: "
											+ target.getId());
							TCapability sourceCapa = (TCapability) rela
									.getSourceElement().getRef();
							EngineLogger.logger
									.debug("buildRuntimeDataFromTosca. Source capa: "
											+ sourceCapa.getId());
							TNodeTemplate source = ToscaStructureQuery
									.getNodetemplateOfRequirementOrCapability(
											sourceCapa.getId(), def);
							EngineLogger.logger
									.debug("buildRuntimeDataFromTosca. Source  "
											+ source.getId());
							nodeData.getConnecttoId().add(source.getId());
						}
					}
				}
				EngineLogger.logger.debug("debugggg Sep 8.1 - 3");
				// manipulate properties ConfigurationCapabilities
//				if (node.getProperties() != null && node.getProperties().getAny() != null) {					
//					ConfigurationCapabilities capa = (ConfigurationCapabilities) node.getProperties().getAny();
//					nodeData.setConfiguationCapapabilities(capa);				
//				}
				
				topo.addComponent(nodeData);
			}
			EngineLogger.logger.debug("debugggg Sep 8.1 - last");
			service.addComponentTopology(topo);
		}
		return service;
	}

	public boolean cleanAllService(String serviceId) throws SalsaEngineException {
		// TODO: implement it
		// List<TNodeTemplate> lst =
		// ToscaStructureQuery.getNodeTemplatesOfTypeList("OPERATING_SYSTEM",
		// def);
		CloudService service = centerCon
				.getUpdateCloudServiceRuntime(serviceId);
		if (service == null) {
			EngineLogger.logger
					.error("Cannot clean service. Service description is not found.");
			throw new SalsaEngineException("Cannot clean service. Service description is not found for service: " + serviceId, false);
		}
		List<ServiceInstance> repLst = service
				.getAllReplicaByType(SalsaEntityType.OPERATING_SYSTEM);
		for (ServiceInstance rep : repLst) {
			if (rep.getProperties() != null) {
				SalsaInstanceDescription_VM instance = (SalsaInstanceDescription_VM) rep
						.getProperties().getAny();
				MultiCloudConnector cloudCon = new MultiCloudConnector(
						EngineLogger.logger, configFile);
				String providerName = instance.getProvider();
				String instanceId = instance.getInstanceId();
				EngineLogger.logger
						.debug("Removing virtual machine. Provider: "
								+ providerName + "InstanceId: " + instanceId);
				cloudCon.removeInstance(
						SalsaCloudProviders.fromString(providerName),
						instanceId);
			}
		}
		// centerCon.deregisterService();
		return true;
	}

}
