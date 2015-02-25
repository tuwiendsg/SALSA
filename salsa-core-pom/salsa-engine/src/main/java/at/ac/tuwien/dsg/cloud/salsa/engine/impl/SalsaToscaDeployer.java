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
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Response;

import at.ac.tuwien.dsg.cloud.salsa.cloudconnector.multiclouds.MultiCloudConnector;
import at.ac.tuwien.dsg.cloud.salsa.cloudconnector.multiclouds.SalsaCloudProviders;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.CloudService;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.PrimitiveOperation;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.ServiceInstance;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.ServiceTopology;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.ServiceUnit;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.enums.SalsaEntityActions;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.enums.SalsaEntityState;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.enums.SalsaEntityType;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.enums.SalsaRelationshipType;
import at.ac.tuwien.dsg.cloud.salsa.common.interfaces.SalsaEngineServiceIntenal;
import at.ac.tuwien.dsg.cloud.salsa.common.processing.SalsaCenterConnector;
import at.ac.tuwien.dsg.cloud.salsa.common.processing.SalsaXmlDataProcess;
import at.ac.tuwien.dsg.cloud.salsa.engine.exception.SalsaEngineException;
import at.ac.tuwien.dsg.cloud.salsa.engine.services.SalsaEngineImplAll;
import at.ac.tuwien.dsg.cloud.salsa.engine.smartdeployment.ToscaEnricher;
import at.ac.tuwien.dsg.cloud.salsa.engine.utils.EngineLogger;
import at.ac.tuwien.dsg.cloud.salsa.engine.utils.SalsaConfiguration;
import at.ac.tuwien.dsg.cloud.salsa.tosca.extension.SalsaInstanceDescription_VM;
import at.ac.tuwien.dsg.cloud.salsa.tosca.extension.SalsaMappingProperties;
import at.ac.tuwien.dsg.cloud.salsa.tosca.extension.SalsaMappingProperties.SalsaMappingProperty;
import at.ac.tuwien.dsg.cloud.salsa.tosca.extension.SalsaMappingProperties.SalsaMappingProperty.Property;
import at.ac.tuwien.dsg.cloud.salsa.tosca.processing.ToscaStructureQuery;
import at.ac.tuwien.dsg.cloud.salsa.tosca.processing.ToscaXmlProcess;


public class SalsaToscaDeployer {

	// some hard-code variables
	static final String CLOUD_NODE_NAME = SalsaEntityType.OPERATING_SYSTEM.getEntityTypeString();
	File configFile;
	static SalsaCenterConnector centerCon = new SalsaCenterConnector(
                SalsaConfiguration.getSalsaCenterEndpoint().replaceAll("http://.*?:", "http://localhost:"), "/tmp", EngineLogger.logger);
			//SalsaConfiguration.getSalsaCenterEndpoint(), "/tmp", EngineLogger.logger);

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
		String deployID = serviceName;
		EngineLogger.logger.info("Deploying service id: " + deployID);

		String ogininalToscaFile = SalsaConfiguration.getServiceStorageDir()
				+ "/" + deployID + ".original";
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

	public CloudService orchestrateNewService(TDefinitions def, String serviceName) throws SalsaEngineException {
		if (configFile == null) {
			EngineLogger.logger.error("No config file specified");
			throw new SalsaEngineException("There is no SALSA configuation file specific. Please check /etc/salsa.properties.", true);
		}

		String deployID = serviceName;
		EngineLogger.logger.info("Orchestrating service id: " + deployID);

		String ogininalToscaFile = SalsaConfiguration.getServiceStorageDir() + "/" + deployID + ".original";
		ToscaXmlProcess.writeToscaDefinitionToFile(def, ogininalToscaFile);

		// ENRICH
		ToscaEnricher enricher = new ToscaEnricher(def);
		enricher.enrichHighLevelTosca();

		// register service, all state is INITIAL
		String fullToscaFile = SalsaConfiguration.getServiceStorageDir() + "/" 	+ deployID;

		ToscaXmlProcess.writeToscaDefinitionToFile(def, fullToscaFile);

		EngineLogger.logger.debug("debugggg Sep 8 - 1");
		
		// register service running data
		String fullSalsaDataFile = SalsaConfiguration.getServiceStorageDir() + "/" + deployID + ".data";
		EngineLogger.logger.debug("debugggg Sep 8 - 2");
		CloudService serviceData = buildRuntimeDataFromTosca(def);
		EngineLogger.logger.debug("debugggg Sep 8 - 3");
		serviceData.setId(deployID);
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
				EngineLogger.logger.debug("Orchestating: Get top node: " + node.getId());
				topNodes.add(node);
			}
		}
		
		// clone data of all reference node
		for (ServiceUnit unit : nodes){
			ServiceUnit refUnit = getReferenceServiceUnit(unit);
			if (refUnit != null){
				EngineLogger.logger.debug("orchestrateNewService-Clone ref data for node: " + unit.getId());				
				updateInstancesForReferenceNode(refUnit, serviceName, serviceData.getTopologyOfNode(unit.getId()).getId(), unit.getId());
			}
		}
		
		// deploy new node
		SalsaEngineServiceIntenal serviceInternal = new SalsaEngineImplAll();
		for (ServiceUnit unit : topNodes) {
			ServiceUnit refUnit = getReferenceServiceUnit(unit);
			if (refUnit == null && unit.getMin()>0){		// not a reference and min > 0
				EngineLogger.logger.debug("Orchestating: Creating top node: " + unit.getId());
				// try to create minimum instance of software
				for (int i=1; i<= unit.getMin(); i++){
					serviceInternal.spawnInstance(serviceData.getId(), serviceData.getTopologyOfNode(unit.getId()).getId(), unit.getId(), 1);
					try {
						Thread.sleep(100);
					} catch (InterruptedException e){
						EngineLogger.logger.error("Thread interrupted !");
					}
				}
			} 
		}
		return serviceData;
	}
	
	private void updateInstancesForReferenceNode(ServiceUnit sourceSU, String targetServiceID, String targetTopoID, String targetSUID){		
		for (ServiceInstance instance : sourceSU.getInstancesList()) {
			EngineLogger.logger.debug("Adding instance to: " + targetServiceID + "/" + targetSUID + "/" + instance.getInstanceId());
			Object tmpProp = null;
			if (instance.getProperties() != null){
				tmpProp = instance.getProperties().getAny();
			}
			instance.setProperties(null);
			centerCon.addInstanceUnitMetaData(targetServiceID, targetTopoID, targetSUID, instance);
			if (tmpProp != null){
				centerCon.updateInstanceUnitProperty(targetServiceID, targetTopoID, targetSUID, instance.getInstanceId(), tmpProp);
			}
		}
	}
	
	private ServiceUnit getReferenceServiceUnit(ServiceUnit input) throws SalsaEngineException {
		EngineLogger.logger.debug("Checking the reference for ServiceUnit: " + input.getId());
		if (input.getReference()==null){
			EngineLogger.logger.debug("Checking the reference for ServiceUnit: " + input.getId() + " => Not be a reference node.");
			return null;
		}
		EngineLogger.logger.debug("Checking the referece for ServiceUnit: " + input.getId() + " => We got the reference string: " + input.getReference());
		String[] refStr = input.getReference().split("/");
		if (refStr.length<2){
			EngineLogger.logger.debug("Checking the referece for ServiceUnit: " + input.getId() + " => Bad reference string, should be serviceID/serviceunitID, but length=" + refStr.length);
			return null;
		}		
		CloudService service = centerCon.getUpdateCloudServiceRuntime(refStr[0]);
		if (service !=null){
			EngineLogger.logger.debug("Checking the reference for ServiceUnit: " + input.getId() + " => FOUND !");
			return service.getComponentById(refStr[1]);
		} else {
			EngineLogger.logger.debug("Checking the reference for ServiceUnit: " + input.getId() + " => A reference but could not find the target service to refer to.");
			return null;
		}
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
		EngineLogger.logger.debug("Node type: " + node.getType() + ". String: "	+ SalsaEntityType.OPERATING_SYSTEM.getEntityTypeString());
		ServiceInstance repData = new ServiceInstance(instanceId, null);
		repData.setState(SalsaEntityState.ALLOCATING);
		repData.setHostedId_Integer(2147483647);

		if (node.getType().equals(SalsaEntityType.OPERATING_SYSTEM.getEntityTypeString())) {
			if (node.getInstanceNumber() >= node.getMax()) {
				EngineLogger.logger.error("Not enough cloud resource quota for the node: " + nodeId + ". Quit !");
				// out of quota
				throw new SalsaEngineException("Not enough cloud resource quota to deploy the node: " + nodeId, false);				
			}			
			else {
				centerCon.addInstanceUnitMetaData(serviceId, topologyId, nodeId, repData);
				new Thread(new deployOneVmThread(serviceId, topologyId, nodeId,	instanceId, def)).start();
				return true;
			}
		} else {			
			centerCon.addInstanceUnitMetaData(serviceId, topologyId, nodeId, repData);
			new Thread(new deployOneSoftwareThread(serviceId, topologyId, nodeId, instanceId, def)).start();
			
			return true;
		}
		
	}
	
	static String currentLock = "";
	
	private static synchronized void setLock(String log){
		int count = 0;		
		while (orchestating){
			try{
				EngineLogger.logger.debug("The node:"+ log + " is waiting for lock: "+currentLock+". Count: " + count);				
				Thread.sleep(500);
				count++;
				if (count > 100) {
					releaseLock();
				}
			} catch (Exception e) {
				EngineLogger.logger.warn("Not found");
			}
		}
		currentLock = log;
		orchestating = true;
	}
	
	private static void releaseLock(){
		if (orchestating){
			EngineLogger.logger.debug("Release current lock: " + currentLock);
		} else {
			EngineLogger.logger.debug("Release lock but it is not locked: " + currentLock);
		}
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
		setLock(nodeId + "/" + instanceId); 
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
			//setLock("Lock until adding more VM node data: " + service.getId() + "/" + hostedUnit.getId() +", in order to host node:" + nodeId +"/" + instanceId);
			Response res = serviceLayerDeployer.spawnInstance(service.getId(), topologyId, hostedUnit.getId(), 1);
			if (res.getStatus() == 201) {				
				hostInstanceId = Integer.parseInt(((String) res.getEntity()).trim());
				EngineLogger.logger.debug("The hosting node is being add new data: "	+ hostedUnit.getId() + "/" + hostInstanceId);				
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
				// not release here : releaseLock(); 
				throw new SalsaEngineException("Could not create host node " + hostedUnit.getId() + "/" + hostInstanceId + " for deploying node: " + nodeId,true);
			}
		}

		// for testing, get the first OSNode:
		EngineLogger.logger.debug("DEPLOY MORE INSTANCE. FOUND EXISTED HOST (2nd time): " + hostInstanceId);
		newService = centerCon.getUpdateCloudServiceRuntime(service.getId());
		suitableHostedInstance = newService.getInstanceById(topologyId,
				hostedUnit.getId(), hostInstanceId);

		if (suitableHostedInstance == null) {
			EngineLogger.logger.debug("Hosted node is null");
			releaseLock();
			throw new SalsaEngineException("Couldn't find a node (null) to host node: " + nodeId, true);
		}

		EngineLogger.logger.debug("Hosted node: " + hostedUnit.getId() + "/"
				+ suitableHostedInstance.getInstanceId() + " type: "
				+ hostedUnit.getType());
		// not release here : releaseLock();

		// if host in OS or DOCKER, set the status to STAGING. a Pioneer will take it
		if (hostedUnit.getType().equals(SalsaEntityType.OPERATING_SYSTEM.getEntityTypeString())
		 || hostedUnit.getType().equals(SalsaEntityType.DOCKER.getEntityTypeString())
		 || hostedUnit.getType().equals(SalsaEntityType.TOMCAT.getEntityTypeString()) ) {
			newService = centerCon.getUpdateCloudServiceRuntime(service.getId());
			ServiceInstance data = newService.getInstanceById(topologyId, nodeId, instanceId);
			data.setHostedId_Integer(hostInstanceId);
			data.setState(SalsaEntityState.ALLOCATING); 
			centerCon.addInstanceUnitMetaData(service.getId(), topologyId, nodeId, data);
			// only release lock when we add the data to inform other node that this is hosted.
			EngineLogger.logger.debug("Lock should be released here. Current Lock: " + currentLock + ". Node:" + nodeId +"/"+ data.getInstanceId());
			releaseLock();
			// waiting for hostInstance become RUNNING or FINISH
			while (!suitableHostedInstance.getState().equals(SalsaEntityState.INSTALLING)
					&& !suitableHostedInstance.getState().equals(SalsaEntityState.DEPLOYED)) {
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
				}
				CloudService updateService = centerCon.getUpdateCloudServiceRuntime(service.getId());
				suitableHostedInstance = updateService.getInstanceById(topologyId, hostedUnit.getId(),
						suitableHostedInstance.getInstanceId());
			}
                        
                        // wait for CONNECTTO relationship
                        boolean fullfilled;
                        do {
                            fullfilled = true;
                            CloudService updateService = centerCon.getUpdateCloudServiceRuntime(service.getId());
                            for (String connectNode : unit.getConnecttoId()){                                
                                ServiceUnit u = updateService.getComponentById(connectNode);
                                EngineLogger.logger.debug("Node: " + unit.getId() +"/" + instanceId +" is waiting for connectto node: " + u.getId());
                                if (u.getInstancesList()==null || u.getInstancesList().isEmpty()){                                    
                                    fullfilled = false;
                                    break;
                                }
                                if (!u.getInstancesList().get(0).getState().equals(SalsaEntityState.DEPLOYED)){
                                    EngineLogger.logger.debug("Node: " + unit.getId() +"/" + instanceId +" is waiting for connectto node: " + u.getId() + " but the state is not DEPLOYED, it is: " + u.getInstancesList().get(0).getState());
                                    fullfilled = false;                                            
                                    break;
                                }
                            }
                            try {
                                      Thread.sleep(3000);
                            } catch (InterruptedException e) {
                            }      
                        } while (fullfilled == false);
                        
                        
                        
			EngineLogger.logger.debug("Set state to STAGING for node: "	+ nodeId + "/" + instanceId + " which will be hosted on " + hostedUnit.getId() + "/" + hostInstanceId);			
			centerCon.updateNodeState(newService.getId(), topologyId, nodeId, instanceId, SalsaEntityState.STAGING);
		}
		
		EngineLogger.logger.debug("Deploy more instance artifact is done !");
		return true;
		// check if there is an exist node instance can be used for deploying the software ?

	}


	private class deployOneVmThread implements Runnable {
		TDefinitions def;
		String serviceId;
		String topologyId;
		String nodeId;
		int instanceId;

		public deployOneVmThread(String serviceId, String topologyId, String nodeId, int instanceId, TDefinitions def) {
			EngineLogger.logger.debug("Thread processind: nodeId=" + nodeId	+ ", instance no.=" + instanceId);
			this.def = def;
			this.serviceId = serviceId;
			this.topologyId = topologyId;
			this.nodeId = nodeId;
			this.instanceId = instanceId;
		}

		private synchronized ServiceInstance executeDeploymentNode() throws SalsaEngineException {
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
				EngineLogger.logger.error(e.getMessage());
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
			EngineLogger.logger.debug("Thread processind: nodeId=" + nodeId	+ ", instance no.=" + instanceId);
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
	
	private class removeOneSoftwareThread implements Runnable {
		String serviceId;
		String topologyId;
		String nodeId;
		int instanceId;

		public removeOneSoftwareThread(String serviceId, String topologyId, String nodeId, int instanceId) {
			EngineLogger.logger.debug("Thread processind: nodeId=" + nodeId	+ ", instance no.=" + instanceId);
			this.serviceId = serviceId;
			this.topologyId = topologyId;
			this.nodeId = nodeId;
			this.instanceId = instanceId;
		}
		@Override
		public void run() {			
			EngineLogger.logger.debug("Removing a software node somewhere: " + nodeId + "/" + instanceId);
			//set the state=STAGING and stagingAction=undeploy, the pioneer handle the rest			
			centerCon.updateNodeState(serviceId, topologyId, nodeId, instanceId, SalsaEntityState.STAGING_ACTION);
			centerCon.queueActions(serviceId, nodeId, instanceId, SalsaEntityActions.UNDEPLOY.getActionString());
			SalsaEntityState state = SalsaEntityState.STAGING_ACTION;
                        int count=0;
			while (state!=SalsaEntityState.UNDEPLOYED && count <100){	// wait until pioneer finish its job and inform undeployed or just wait 5 mins
				try {
					state = SalsaEntityState.fromString(centerCon.getInstanceState(serviceId, nodeId, instanceId));
				} catch (SalsaEngineException e1) {
					e1.printStackTrace();
				}
				EngineLogger.logger.debug("Wating for pioneer to undeploy node: " + serviceId + "/" + nodeId + "/" + instanceId);
				try {					
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}				
                                count+=1;
			}
			EngineLogger.logger.debug("Pioneer seems to response that undeploying node done: " + serviceId + "/" + nodeId + "/" + instanceId);
			// remove complete, delete metadata
			try {
				centerCon.removeInstanceMetadata(serviceId, nodeId, instanceId);
			} catch (SalsaEngineException e) {
				e.printStackTrace();
			}
		}
	}

	public boolean removeOneInstance(String serviceId, String topologyId,
			String nodeId, int instanceId) throws SalsaEngineException {
		CloudService service = centerCon.getUpdateCloudServiceRuntime(serviceId);
		ServiceUnit node = service.getComponentById(nodeId);
		topologyId = service.getTopologyOfNode(node.getId()).getId();
		ServiceInstance instance = node.getInstanceById(instanceId);
		if (instance.getState().equals(SalsaEntityState.ALLOCATING)){
                    EngineLogger.logger.debug("Just remove metadata");
                    //TODO: REMOVE METADATA HERE !
                    return true;
		}
                List<ServiceUnit> listUnit = service.getAllComponent();
                
                // undeploy dependency chain first. It is recursive.
                for(ServiceUnit u : listUnit){  // all the unit of the service
                    EngineLogger.logger.debug("Checking if this unit: " + u.getId() + " is HOSTED ON current removing node: " + nodeId);
                    if (u.getHostedId().equals(nodeId)){    // this is hosted on the node we want to remove. remove it first                        
                        EngineLogger.logger.debug("YES! Now check instance of node: " + u.getId() + " is HOSTED ON current removing node: " + nodeId);
                        for(ServiceInstance i : u.getInstanceHostOn(instanceId)) {   // the instance of above unit and hosted on current instance
                            EngineLogger.logger.debug("Found instance need to be remove first: " + u.getId() + "/" + i.getInstanceId());
                            centerCon.removeOneInstance(serviceId, topologyId, u.getId(), i.getInstanceId());
                        }
                    }
                    for(String connectoId : u.getConnecttoId()){    // the unit u can connect to something
                        EngineLogger.logger.debug("Checking if this unit: " + u.getId() + " is CONNECT TO current removing node: " + nodeId);
                        if (connectoId.equals(nodeId)){             // which can be this node
                            EngineLogger.logger.debug("YES! Now checking instance of node: " + u.getId() + " is CONNECT TO current removing node: " + nodeId);
                            for(ServiceInstance i : u.getInstancesList()) {   // remove all its instance
                                EngineLogger.logger.debug("Found instance need to be remove first: " + u.getId() + "/" + i.getInstanceId());
                                centerCon.removeOneInstance(serviceId, topologyId, u.getId(), i.getInstanceId());                                
                            }
                        }
                    }
                }
                EngineLogger.logger.debug("The dependency should be cleaned for the node: " + nodeId +"/"+instanceId);
                
                // check all instance of hoston and connect to are undeployed
                // do similar things and check if all the list are empty
                boolean cleaned;
                do{
                    service = centerCon.getUpdateCloudServiceRuntime(serviceId);
                    node = service.getComponentById(nodeId);
                    topologyId = service.getTopologyOfNode(node.getId()).getId();
                    listUnit = service.getAllComponent();
                    cleaned=true;
                    EngineLogger.logger.debug("Checking if dependency is really clean for node: " + nodeId +"/"+instanceId);
                    for(ServiceUnit u : listUnit){  
                        if (u.getHostedId().equals(nodeId)){   
                            EngineLogger.logger.debug("Checking if dependency is really clean for node: " + nodeId +"/"+instanceId+". Checking unit: " + u.getId() +" which number of hosted on inst: " + u.getInstanceHostOn(instanceId).size());
                            if (!u.getInstanceHostOn(instanceId).isEmpty()){                                
                                EngineLogger.logger.debug("Waiting for cleaning HOST ON nodes of: " + nodeId + "/" + instanceId + ". Nodes left: " + u.getInstanceHostOn(instanceId).size());
                                for(ServiceInstance debugI : u.getInstanceHostOn(instanceId)){
                                    EngineLogger.logger.debug("They are: " + u.getId() +"/" + debugI.getInstanceId());                                    
                                }
                                cleaned=false;
                            }
                        }                        
                        for(String connectoId : u.getConnecttoId()){    
                            if (connectoId.equals(nodeId)){
                                if (!u.getInstancesList().isEmpty()){                                    
                                    EngineLogger.logger.debug("Waiting for cleaning CONNECT TO nodes of: " + nodeId + "/" + instanceId + ". Nodes left: " + u.getInstancesList().size());
                                    cleaned = false;
                                }
                            }
                        }
                    }
                    try{                        
                        Thread.sleep(3000);
                    } catch(InterruptedException e){}
                } while (cleaned == false);
                
                EngineLogger.logger.debug("It is TRUE, the dependency is now cleaned for the node: " + nodeId +"/"+instanceId);                
                
		// remove VM node by invoke MultiCloudConnector
		if (node.getType().equals(SalsaEntityType.OPERATING_SYSTEM.getEntityTypeString())) {
			ServiceInstance vm = node.getInstanceById(instanceId);
			SalsaInstanceDescription_VM vmProps = (SalsaInstanceDescription_VM) vm.getProperties().getAny();

			MultiCloudConnector cloudCon = new MultiCloudConnector(EngineLogger.logger, configFile);
			String providerName = vmProps.getProvider();
			String cloudInstanceId = vmProps.getInstanceId();
			EngineLogger.logger.debug("Removing virtual machine. Provider: " + providerName + "InstanceId: " + instanceId);
			cloudCon.removeInstance(SalsaCloudProviders.fromString(providerName),cloudInstanceId);			
			centerCon.removeInstanceMetadata(serviceId, nodeId, instanceId);

			return true;
		} else {
			new Thread(new removeOneSoftwareThread(serviceId, topologyId, nodeId, instanceId)).start();	
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
			// all other nodes
			List<TNodeTemplate> nodes = ToscaStructureQuery.getNodeTemplateList(st); 
			List<TRelationshipTemplate> relas_hoston = ToscaStructureQuery
					.getRelationshipTemplateList(SalsaRelationshipType.HOSTON.getRelationshipTypeString(), st);
			List<TRelationshipTemplate> relas_connectto = ToscaStructureQuery
					.getRelationshipTemplateList(SalsaRelationshipType.CONNECTTO.getRelationshipTypeString(), st);
			EngineLogger.logger.debug("Number of HostOn relationships: " + relas_hoston.size());
			for (TNodeTemplate node : nodes) {
				ServiceUnit nodeData = new ServiceUnit(node.getId(), node.getType().getLocalPart());				
				nodeData.setState(SalsaEntityState.UNDEPLOYED);
				nodeData.setName(node.getName());
				nodeData.setMin(node.getMinInstances());
				nodeData.setReference(node.getReference());
				if (node.getMaxInstances().equals("unbounded")) {
					nodeData.setMax(100); // max for experiments
				} else {
					nodeData.setMax(Integer.parseInt(node.getMaxInstances()));
				}
				// Get the action property if there is. Just support command pattern
				// TODO: Support complex action.
				if (node.getProperties()!=null){
					if (node.getProperties().getAny()!=null){
						SalsaMappingProperties props = (SalsaMappingProperties) node.getProperties().getAny();
						SalsaMappingProperty p = props.getByType("action");
						if (p!=null){
							for (Property pp : p.getPropertiesList()) {
								nodeData.addPrimitiveOperation(PrimitiveOperation.newCommandType(pp.getName(), pp.getValue()));
							}
						}						
					}
				}
				// add the artifact type for SOFTWARE NODE
				if (node.getType().getLocalPart().equals(SalsaEntityType.SOFTWARE.getEntityTypeString())) {
					if (node.getDeploymentArtifacts()!=null && node.getDeploymentArtifacts().getDeploymentArtifact().size()!=0){
						nodeData.setArtifactType(node.getDeploymentArtifacts().getDeploymentArtifact().get(0).getArtifactType().getLocalPart());
						String artID = node.getDeploymentArtifacts().getDeploymentArtifact().get(0).getArtifactRef().getLocalPart();
						String directURL = ToscaStructureQuery
								.getArtifactTemplateById(artID, def)
								.getArtifactReferences().getArtifactReference()
								.get(0).getReference();
						nodeData.setArtifactURL(directURL);
					}
				}
				EngineLogger.logger.debug("debugggg Sep 8.1 - 1");
				// find what is the host of this node, add to hostId
				for (TRelationshipTemplate rela : relas_hoston) { // search on all relationship, find the host of this "node"
					// note that, after convert, the capa and req are reverted.
					TEntityTemplate targetRela = (TEntityTemplate) rela.getTargetElement().getRef();
					TEntityTemplate sourceRela = (TEntityTemplate) rela.getSourceElement().getRef();
					TNodeTemplate target;
					TNodeTemplate source;
					if (targetRela.getClass().equals(TRequirement.class)) {
						target = ToscaStructureQuery.getNodetemplateOfRequirementOrCapability(targetRela.getId(), def);
						source = ToscaStructureQuery.getNodetemplateOfRequirementOrCapability(sourceRela.getId(), def);
					} else {
						target = (TNodeTemplate) sourceRela;
						source = (TNodeTemplate) targetRela;
					}
					EngineLogger.logger.debug("Is the source with id: " + target.getId() + " same with " + nodeData.getId());
					if (target.getId().equals(nodeData.getId())) {
						nodeData.setHostedId(source.getId());
						EngineLogger.logger.debug("Found the host of node "	+ nodeData.getId() + " which is id = "	+ source.getId());
					}
				}
				EngineLogger.logger.debug("debugggg Sep 8.1 - 2");
				// find the connect to node, add it to connecttoId, this node will be the requirement, connect to capability (reverse with the Tosca)

				for (TRelationshipTemplate rela : relas_connectto) {
					EngineLogger.logger.debug("buildRuntimeDataFromTosca. Let's see relationship connectto: "+ rela.getId());

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
							EngineLogger.logger.debug("buildRuntimeDataFromTosca. Found the target id: " + target.getId());
							TCapability sourceCapa = (TCapability) rela.getSourceElement().getRef();
							EngineLogger.logger.debug("buildRuntimeDataFromTosca. Source capa: "+ sourceCapa.getId());
							TNodeTemplate source = ToscaStructureQuery.getNodetemplateOfRequirementOrCapability(sourceCapa.getId(), def);
							EngineLogger.logger.debug("buildRuntimeDataFromTosca. Source  "	+ source.getId());
							nodeData.getConnecttoId().add(source.getId());
						}
					}
				}
				EngineLogger.logger.debug("debugggg Sep 8.1 - 3");
				// manipulate properties ConfigurationCapabilities
				topo.addComponent(nodeData);
			}
			EngineLogger.logger.debug("debugggg Sep 8.1 - last");
			service.addComponentTopology(topo);
		}
		return service;
	}

	public boolean cleanAllService(String serviceId) throws SalsaEngineException {
		CloudService service = centerCon.getUpdateCloudServiceRuntime(serviceId);
		if (service == null) {
			EngineLogger.logger.error("Cannot clean service. Service description is not found.");
			throw new SalsaEngineException("Cannot clean service. Service description is not found for service: " + serviceId, false);
		}
		
		List<ServiceUnit> suList = service.getAllComponentByType(SalsaEntityType.OPERATING_SYSTEM);
		for (ServiceUnit su : suList) {
			if (su.getReference() == null){
				List<ServiceInstance> repLst = su.getInstancesList();
				//List<ServiceInstance> repLst = service.getAllReplicaByType(SalsaEntityType.OPERATING_SYSTEM);
				for (ServiceInstance rep : repLst) {
					if (rep.getProperties() != null) {
						SalsaInstanceDescription_VM instance = (SalsaInstanceDescription_VM) rep.getProperties().getAny();
						MultiCloudConnector cloudCon = new MultiCloudConnector(EngineLogger.logger, configFile);
						String providerName = instance.getProvider();
						String instanceId = instance.getInstanceId();
						EngineLogger.logger.debug("Removing virtual machine. Provider: " + providerName + "InstanceId: " + instanceId);
						cloudCon.removeInstance(SalsaCloudProviders.fromString(providerName),instanceId);
					}
				}
			}
		}		
		return true;
	}

}
