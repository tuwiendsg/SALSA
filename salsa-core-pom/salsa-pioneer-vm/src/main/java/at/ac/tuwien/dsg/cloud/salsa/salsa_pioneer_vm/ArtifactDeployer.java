package at.ac.tuwien.dsg.cloud.salsa.salsa_pioneer_vm;

import generated.oasis.tosca.TCapability;
import generated.oasis.tosca.TDefinitions;
import generated.oasis.tosca.TDeploymentArtifact;
import generated.oasis.tosca.TNodeTemplate;
import generated.oasis.tosca.TRelationshipTemplate;
import generated.oasis.tosca.TRequirement;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.ac.tuwien.dsg.cloud.salsa.common.model.SalsaCloudServiceData;
import at.ac.tuwien.dsg.cloud.salsa.common.model.SalsaComponentData;
import at.ac.tuwien.dsg.cloud.salsa.common.model.SalsaComponentInstanceData;
import at.ac.tuwien.dsg.cloud.salsa.common.model.enums.SalsaEntityState;
import at.ac.tuwien.dsg.cloud.salsa.common.model.enums.SalsaRelationshipType;
import at.ac.tuwien.dsg.cloud.salsa.common.processing.SalsaCenterConnector;
import at.ac.tuwien.dsg.cloud.salsa.salsa_pioneer_vm.instruments.BashInstrument;
import at.ac.tuwien.dsg.cloud.salsa.salsa_pioneer_vm.instruments.ChefInstrument;
import at.ac.tuwien.dsg.cloud.salsa.salsa_pioneer_vm.instruments.InstrumentInterface;
import at.ac.tuwien.dsg.cloud.salsa.salsa_pioneer_vm.type.PropertyVMExpose;
import at.ac.tuwien.dsg.cloud.salsa.salsa_pioneer_vm.type.SalsaArtifactType;
import at.ac.tuwien.dsg.cloud.salsa.salsa_pioneer_vm.utils.PioneerLogger;
import at.ac.tuwien.dsg.cloud.salsa.salsa_pioneer_vm.utils.SalsaPioneerConfiguration;
import at.ac.tuwien.dsg.cloud.salsa.tosca.extension.SalsaCapaReqString;
import at.ac.tuwien.dsg.cloud.salsa.tosca.extension.SalsaInstanceDescription_VM;
import at.ac.tuwien.dsg.cloud.salsa.tosca.processing.ToscaStructureQuery;

public class ArtifactDeployer {
	private String serviceId;
	private String topologyId;
	private String nodeId;	// this is VM node ID. It may contain other nodes on it
	private int replica;		// this is Replica number of the VM
	private TDefinitions def;	
	private SalsaCenterConnector centerCon;
	private SalsaCloudServiceData serviceRuntimeInfo;
	private Logger logger = LoggerFactory.getLogger("PioneerLogger");
	
	public ArtifactDeployer(String serviceId, String topologyId, String nodeId, int thisInstanceId, TDefinitions def, SalsaCenterConnector centerCon, SalsaCloudServiceData serviceRuntimeInfo){
		this.serviceId = serviceId;
		this.topologyId = topologyId;
		this.nodeId = nodeId;
		this.replica = thisInstanceId;
		this.def = def;
		this.centerCon = centerCon;
		this.serviceRuntimeInfo = serviceRuntimeInfo;
	}
	
	
	// Deploy upper nodes which are hosted on the current VM node
		public void deployNodeChain(TNodeTemplate thisNode)
				throws IOException {
			// download full topology from web
			//def = centerCon.updateTopology();
			//PioneerLogger.logger.debug("Update topology service: " + def.getId());
			// List of upper nodes, which will be deployed

			List<TNodeTemplate> upperNodes = ToscaStructureQuery
					.getNodeTemplateWithRelationshipChain("HOSTON", thisNode, def);
			logger.debug("Chain for node: " + thisNode.getId());
			for (TNodeTemplate chainNode : upperNodes) {
				//SalsaInstanceDescription_Artifact artiProps = new SalsaInstanceDescription_Artifact();			
				
				logger.debug("Starting deploy node: "+chainNode.getId());
				// don't need it ? setting state for ServiceUnit, not for Instance. Set -1 as instanceId will do the job
				centerCon.setNodeState(topologyId, chainNode.getId(), -1, SalsaEntityState.ALLOCATING);
								
				// Get the number of node to be deploy
				int quantity = chainNode.getMinInstances();
				logger.debug("Number of instance to deploy: " + quantity);
				serviceRuntimeInfo = centerCon.getUpdateCloudServiceRuntime();
				int startId=serviceRuntimeInfo.getComponentById(topologyId, chainNode.getId()).getIdCounter();
				logger.debug("Starting ID: " + startId);
				centerCon.updateNodeIdCounter(topologyId, chainNode.getId(), startId+quantity);
				
				List<Integer> instanceIdList = new ArrayList<>();
				// Create quantity node instances(instance of software) for this chainNode(software)
				for (int i=startId; i<startId+quantity; i++){
					instanceIdList.add(i);
					SalsaComponentInstanceData data = new SalsaComponentInstanceData(i);
					data.setHostedId_Integer(replica);
					data.setState(SalsaEntityState.ALLOCATING);	// waiting for other conditions
					centerCon.addInstanceUnitMetaData(serviceId, topologyId, chainNode.getId(), data);	// add the 					
				}
				waitingForCapabilities(chainNode, def);
				// wait for downloading and configuring artifact itself
				logger.debug("OK, we have " + instanceIdList.size() + " of instances");
				for (Integer i : instanceIdList) {
					logger.debug("Set status for instance " + i + " to CONFIGURING !");
					centerCon.setNodeState(topologyId, chainNode.getId(), i, SalsaEntityState.CONFIGURING);
				}
				downloadNodeArtifacts(chainNode, def);
				// execute multi threads for multi instance				
				multiThreadRunArtifacts(chainNode, instanceIdList);
				
			}

		}
		
		private void multiThreadRunArtifacts(TNodeTemplate node, List<Integer> instanceIds){
			List<Thread> threads = new ArrayList<Thread>();			
			
			for (Integer i : instanceIds){
				logger.debug("STARTING Thread for node ID :" + i);		
				centerCon.setNodeState(topologyId, node.getId(), i, SalsaEntityState.RUNNING);
				Thread thread = new Thread(new deployOneArtifactThread(node, i, def));
				thread.start();
				threads.add(thread);	
			}
			// waiting for all thread run and put node in to finish
			
		}
		
		private class deployOneArtifactThread implements Runnable {		
			TDefinitions def;
			TNodeTemplate node;
			int instanceId;

			public deployOneArtifactThread(TNodeTemplate node, int instanceId, TDefinitions def) {	
				logger.debug("Thread processind: nodeId=" + node.getId()+" -- InstanceID: " + instanceId);
				this.def = def;
				this.node = node;
				this.instanceId = instanceId;
			}

			private synchronized void executeDeploymentNode() {
				logger.debug("Executing the deployment for node: " + node.getId() +", instance: " + instanceId );
				runNodeArtifacts(node, Integer.toString(instanceId), def);
				centerCon.setNodeState(topologyId, node.getId(), instanceId, SalsaEntityState.FINISHED);
			}

			@Override
			public void run() {
				executeDeploymentNode();	
				
			}

		}		
		
		// waiting for capabilities and fulfill requirements
		private void waitingForCapabilities(TNodeTemplate node, TDefinitions def) {
			if (node.getRequirements()==null){
				return;	// node have no requirement
			}
			List<TRequirement> reqs = node.getRequirements().getRequirement();
			for (TRequirement req : reqs) {
				logger.debug("Checking requirement "+req.getId());
				TCapability cap=ToscaStructureQuery.getCapabilitySuitsRequirement(req, def);
				logger.debug("Waiting for capability: "+cap.getId());
				String value = waitRelationshipReady(topologyId, replica, cap, req);

			}
		}	
		

		// Download node artifact
		private void downloadNodeArtifacts(TNodeTemplate node, TDefinitions def) {
			if (node.getDeploymentArtifacts()==null){
				return;
			}
			if (node.getDeploymentArtifacts().getDeploymentArtifact().get(0).getArtifactType().getLocalPart().equals(SalsaArtifactType.chef.getString())){
				return;
			}
			// get Artifact list
			List<String> arts = ToscaStructureQuery
					.getDeployArtifactTemplateReferenceList(node, def);	
			for (String art : arts) {
				try {
					PioneerLogger.logger.debug("Downloading artifact for: "
							+ node.getId() + ". URL:" + art);
					URL url = new URL(art);
					String filePath = SalsaPioneerConfiguration.getWorkingDir()
							+ File.separator + node.getId()
							+ File.separator
							+ FilenameUtils.getName(url.getFile());
					// get the last file in the list
					// TODO: there could be multi mirror of an artifact, check !
					//runArt = FilenameUtils.getName(url.getFile()); 
					// download file to dir: nodeId/fileName
					PioneerLogger.logger.debug("Download file from:"+url.toString()+"\nSave to file:" + filePath);
					FileUtils.copyURLToFile(url,new File(filePath));
				} catch (IOException e) {
					PioneerLogger.logger
							.error("Error while downloading artifact for: "
									+ node.getId() + ". URL:" + art);
					PioneerLogger.logger.error(e.toString());
				}
			}
		}
		
		// Download and run 1 node of software
		private void runNodeArtifacts(TNodeTemplate node, String instanceId, TDefinitions def) {
			PioneerLogger.logger.debug("==> Run artifact for node: " + node.getId());
			// get Artifact list
			String runArt="";
			String artType="";
			try {
				if (node.getDeploymentArtifacts() == null){
					PioneerLogger.logger.debug("node " + node.getId()+" have null artifact");
					return;
				}
				TDeploymentArtifact firstArt = node.getDeploymentArtifacts().getDeploymentArtifact().get(0);				
				artType = firstArt.getArtifactType().getLocalPart();
				PioneerLogger.logger.debug("Artifact type:" + artType);
				
				List<String> arts = ToscaStructureQuery
						.getDeployArtifactTemplateReferenceList(node, def);
				URL url = new URL(arts.get(0));	// run the first artifact
				
				runArt = FilenameUtils.getName(url.getFile());
				PioneerLogger.logger.debug("Artifact reference:" + runArt);
			} catch (Exception e){
				e.printStackTrace();
			}
						
			if (runArt.equals("")){
				PioneerLogger.logger.debug(node.getId()	+ " doesn't have a deploy artifact");
				return;
			}
			if (SalsaArtifactType.fromString(artType) == null){
				PioneerLogger.logger.debug(node.getId()	+ " use unsupport artifact type: " + artType);
				return;
			}
			
			InstrumentInterface instrument=null;
			switch (SalsaArtifactType.fromString(artType)){
			case sh:
				instrument = new BashInstrument();
				break;
			case chef:				
				instrument = new ChefInstrument();
				break;
			}
			instrument.initiate(node);
			instrument.deployArtifact(runArt, instanceId);
			
			// if this node is part of CONNECTTO, send the IP
			if (node.getCapabilities()==null){
				return;
			}
			for (TCapability capa : node.getCapabilities().getCapability()){
				TRequirement req = ToscaStructureQuery.getRequirementSuitsCapability(capa, def);
				TRelationshipTemplate rela = ToscaStructureQuery.getRelationshipBetweenTwoCapaReq(capa, req, def);
				if (rela.getType().getLocalPart().equals(SalsaRelationshipType.CONNECTTO.getRelationshipTypeString())){
					try{
						logger.debug("Sending the IP of this node to the capability of CONNECTTO");						
						String ip = InetAddress.getLocalHost().getHostAddress();
						SalsaCapaReqString capaString = new SalsaCapaReqString(capa.getId(), ip);				
						centerCon.updateInstanceUnitCapability(topologyId, node.getId(), 0, capaString);
					} catch (UnknownHostException e){
						PioneerLogger.logger.error("Cannot get the IP of the host of node: " + node.getId());
					}
				}
			}
		}
		
		/*
		 * Check if an instance of node is deployed, then its capability will be ready
		 * Note: we don't need nodeID because capaId is unique inside a Topology
		 * CHECK THE STATE OF THE FIRST INSTANCE OF THE COMPONENT
		 * CURRENTLY, instanceId is not need, but future.
		 */
		public boolean checkCapabilityReady(String capaId) {
			logger.debug("Check capability for capaid: " + capaId);
			TNodeTemplate node = ToscaStructureQuery.getNodetemplateOfRequirementOrCapability(capaId, def);
			if (node==null){	// capaId is not valid
				logger.debug("Check capability. Wrong capability Id");
				return false;
			}
			
			// check if there are a replica of node with Ready state
			// it doesn't care about which node, just check if existing ONE replica
			SalsaCloudServiceData service = centerCon.getUpdateCloudServiceRuntime();
			SalsaComponentData component = service.getComponentById(topologyId, node.getId());			
			int number=component.getInstanceNumberByState(SalsaEntityState.RUNNING)+component.getInstanceNumberByState(SalsaEntityState.FINISHED);
			logger.debug("Check capability. Checking component id " + component.getId() + "  -- " + number +" number of running or finished instances.");			
			if (number == 0){
				logger.debug("CHECK CAPABILITY FALSE. " + number);				
				return false;
			} else {
				logger.debug("CHECK CAPABILITY TRUE. " + number);
				return true;
			}	
			
			// check if there are a replica of node with Ready state
			// it doesn't care about which node, just check if existing ONE replica
//			SalsaCloudServiceData service = centerCon.getUpdateCloudServiceRuntime();
//			SalsaComponentData component = service.getComponentById(topologyId, node.getId());
//			List <SalsaComponentInstanceData> instanceLst = component.getAllInstanceList();			
//			if (instanceLst.size() == 0){
//				return false;
//			}
//			SalsaComponentInstanceData theInstance = instanceLst.get(0);			
//			String value = theInstance.getCapabilityValue(capaId);
//			if (value != null){
//				return true;
//			}
//			return false;
		}
		
		// can handle null value (see SalsaCenterConnector)
		public String waitRelationshipReady(String topoId, int replica, TCapability capa, TRequirement req){
			TRelationshipTemplate rela = ToscaStructureQuery.getRelationshipBetweenTwoCapaReq(capa, req, def);			
			while (!checkCapabilityReady(capa.getId())){
				try{
					Thread.sleep(5000);				
				} catch (InterruptedException e) {}
			}
			// try to get value, retry every 5 secs if it is not a hoston. Because the HOSTON is always available if node is ready, but not for software
			if (rela.getType().getLocalPart().equals(SalsaRelationshipType.HOSTON.getRelationshipTypeString())){
				return "ready";	// just return for HOSTON
			}
			logger.debug("WaitRelationshipReady Mar4 - capa: " + capa.getId());
			TNodeTemplate nodeOfCapa = ToscaStructureQuery.getNodetemplateOfRequirementOrCapability(capa, def);
			
			String value=centerCon.getCapabilityValue(topoId, nodeOfCapa.getId(), 0, capa.getId());	// note: 0 is the ID of the first node, which provide the capability
			logger.debug("waitRelationshipReady - Get the value is: " + value);
			while (value==null){
				try{
					logger.debug("waitRelationshipReady - Get the value is: " + value);
					Thread.sleep(5000);
					value=centerCon.getCapabilityValue(topoId, nodeOfCapa.getId(), 0, capa.getId());
					
				} catch (InterruptedException e) {}
			}
			// set the environment variable. Write the value to node_capability_id
			// if the value is of CONNECTTO relationship, write to NodeOfCapability_IP
			// SystemFunctions.writeSystemVariable(nodeOfCapa.getId()+"_"+capa.getId(), value);
			PioneerLogger.logger.debug("123. Check the relationship: " + rela.getType().getLocalPart());
			if (rela.getType().getLocalPart().equals(SalsaRelationshipType.CONNECTTO.toString())){
				PioneerLogger.logger.debug("123. Relationship type is CONNECTTO !");
				// get the IP form the center
				String ip=value;
				try{
					SystemFunctions.writeSystemVariable(nodeOfCapa.getId()+"_IP", ip);					
					SystemFunctions.writeSystemVariable(rela.getId()+"_IP", ip);		
				} catch (Exception e){
					PioneerLogger.logger.error("Couldn't get IP of host !");
				}
			}
			
			
			
			return value;
		}
		

		
		// cannot handle null, so always return of requirement has not been updated
		// only use for CONNECTTO relationship. (not works with HOSTON relationship)
		@Deprecated
		public String waitRequirement(String reqId){			
			TRequirement req = (TRequirement)ToscaStructureQuery.getRequirementOrCapabilityById(reqId, def);
			TCapability capa = ToscaStructureQuery.getCapabilitySuitsRequirement(req, def);
			
			if (capa != null){				
				while (!checkCapabilityReady(capa.getId())){
					try{
						Thread.sleep(5000);				
					} catch (InterruptedException e) {}
				}
				// if the capa for the req ready, query the req
				return centerCon.getRequirementValue(serviceId, topologyId, nodeId, replica, reqId);
			}
			return null;
		}
		
		
		public String getVMProperty(String propName){
			String res="";
			SalsaComponentInstanceData nodeData = serviceRuntimeInfo.getComponentById(topologyId, nodeId).getInstanceById(replica);			
			SalsaInstanceDescription_VM vm= (SalsaInstanceDescription_VM)nodeData.getProperties().getAny();
			PropertyVMExpose proptype = PropertyVMExpose.fromString(propName);			
			switch (proptype){
			case ip:
			case private_ip:				
				res = vm.getPrivateIp();
				break;
			case public_ip:
				res = vm.getPublicIp();
				break;
			}
			System.out.println(res);
			return res;
		}
		
		
		
}
