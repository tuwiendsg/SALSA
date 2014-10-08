package at.ac.tuwien.dsg.cloud.salsa.pioneer;

import generated.oasis.tosca.TCapability;
import generated.oasis.tosca.TDefinitions;
import generated.oasis.tosca.TDeploymentArtifact;
import generated.oasis.tosca.TNodeTemplate;
import generated.oasis.tosca.TRelationshipTemplate;
import generated.oasis.tosca.TRequirement;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.CloudService;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.ConfigurationCapability;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.ServiceInstance;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.ServiceUnit;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.enums.SalsaEntityState;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.enums.SalsaEntityType;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.enums.SalsaRelationshipType;
import at.ac.tuwien.dsg.cloud.salsa.common.processing.SalsaCenterConnector;
import at.ac.tuwien.dsg.cloud.salsa.engine.exception.SalsaEngineException;
import at.ac.tuwien.dsg.cloud.salsa.pioneer.StacksConfigurator.DockerConfigurator;
import at.ac.tuwien.dsg.cloud.salsa.pioneer.instruments.AptGetInstrument;
import at.ac.tuwien.dsg.cloud.salsa.pioneer.instruments.BashInstrument;
import at.ac.tuwien.dsg.cloud.salsa.pioneer.instruments.ChefInstrument;
import at.ac.tuwien.dsg.cloud.salsa.pioneer.instruments.ChefSoloInstrument;
import at.ac.tuwien.dsg.cloud.salsa.pioneer.instruments.InstrumentInterface;
import at.ac.tuwien.dsg.cloud.salsa.pioneer.instruments.InstrumentShareData;
import at.ac.tuwien.dsg.cloud.salsa.pioneer.instruments.WarInstrument;
import at.ac.tuwien.dsg.cloud.salsa.pioneer.type.PropertyVMExpose;
import at.ac.tuwien.dsg.cloud.salsa.pioneer.type.SalsaArtifactType;
import at.ac.tuwien.dsg.cloud.salsa.pioneer.utils.PioneerLogger;
import at.ac.tuwien.dsg.cloud.salsa.pioneer.utils.SalsaPioneerConfiguration;
import at.ac.tuwien.dsg.cloud.salsa.tosca.extension.SalsaCapaReqString;
import at.ac.tuwien.dsg.cloud.salsa.tosca.extension.SalsaInstanceDescription_VM;
import at.ac.tuwien.dsg.cloud.salsa.tosca.processing.ToscaStructureQuery;

public class ArtifactDeployer {
	private String serviceId;
	private String topologyId;
	private String nodeId;	// this is VM node ID. It may contain other nodes on it
	private int hostedVmInstanceId;		// this is Replica number of the VM
	private TDefinitions def;	
	private SalsaCenterConnector centerCon;
	private CloudService serviceRuntimeInfo;
	private Logger logger = LoggerFactory.getLogger("PioneerLogger");
	
	public ArtifactDeployer(String serviceId, String topologyId, String nodeId, int thisInstanceId, TDefinitions def, SalsaCenterConnector centerCon, CloudService serviceRuntimeInfo){
		this.serviceId = serviceId;
		this.topologyId = topologyId;
		this.nodeId = nodeId;
		this.hostedVmInstanceId = thisInstanceId;
		this.def = def;
		this.centerCon = centerCon;
		this.serviceRuntimeInfo = serviceRuntimeInfo;
	}
	
	
	// Deploy upper nodes which are hosted on the current VM node
		public void deployNodeChain(TNodeTemplate thisNode)
				throws IOException, SalsaEngineException {
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
				//centerCon.updateNodeState(topologyId, chainNode.getId(), -1, SalsaEntityState.ALLOCATING);
								
				// Get the number of node to be deploy
				int quantity = chainNode.getMinInstances();
				logger.debug("Number of instance to deploy: " + quantity);
				serviceRuntimeInfo = centerCon.getUpdateCloudServiceRuntime(serviceId);
				
				int startId=serviceRuntimeInfo.getComponentById(topologyId, chainNode.getId()).getIdCounter();
				logger.debug("Starting ID: " + startId);				
				centerCon.updateNodeIdCounter(serviceId, topologyId, chainNode.getId(), startId+quantity);
				
				// calculate the quantity of node to be deploy
				ServiceUnit unit = serviceRuntimeInfo.getComponentById(topologyId, chainNode.getId());
				int existInstanceNumber = unit.getInstanceHostOn(hostedVmInstanceId).size();
				
				List<Integer> instanceIdList = new ArrayList<>();
				// Create quantity node instances(instance of software) for this chainNode(software)
				for (int i=startId; i<startId+quantity-existInstanceNumber; i++){
					instanceIdList.add(i);
					ServiceInstance data = new ServiceInstance(i);
					data.setHostedId_Integer(hostedVmInstanceId);
					data.setState(SalsaEntityState.ALLOCATING);	// waiting for other conditions
					centerCon.addInstanceUnitMetaData(serviceId, topologyId, chainNode.getId(), data);	// add the 					
				}
				waitingForCapabilities(chainNode, def);
				// wait for downloading and configuring artifact itself
				logger.debug("OK, we have " + instanceIdList.size() + " of instances");
				for (Integer i : instanceIdList) {
					logger.debug("Set status for instance " + i + " to CONFIGURING !");
					centerCon.updateNodeState(serviceId, topologyId, chainNode.getId(), i, SalsaEntityState.CONFIGURING);
				}
				//downloadNodeArtifacts(chainNode, def);
				// execute multi threads for multi instance
				multiThreadRunArtifacts(chainNode, instanceIdList);
				
			}

		}
		
		
		public String deploySingleNode(TNodeTemplate node, int instanceId) throws SalsaEngineException{
			logger.debug("Starting deploy node: "+node.getId() + "/" + instanceId);
			centerCon.logMessage("Deploy Single Node: " + node.getId() + "/" + instanceId + " of service: " + serviceId);
			serviceRuntimeInfo = centerCon.getUpdateCloudServiceRuntime(serviceId);			
			// check if node is a docker node, install docker and start new container
			if (node.getType().getLocalPart().equals(SalsaEntityType.DOCKER.getEntityTypeString())){
				PioneerLogger.logger.debug("THIS IS THE DOCKER NODE, INSTALL IT !");
				DockerConfigurator docker = new DockerConfigurator(node.getId());
				centerCon.updateNodeState(serviceId, topologyId, node.getId(), instanceId, SalsaEntityState.CONFIGURING);
				docker.initDocker();
				docker.installDockerNode(node.getId(), instanceId);
				centerCon.updateNodeState(serviceId, topologyId, node.getId(), instanceId, SalsaEntityState.RUNNING);
				return Integer.toString(instanceId);
			}			
						
			// check if it is hosted by a docker, forward the request to that docker
			logger.debug("[123456] 1 Starting deploy node: "+node.getId() + "/" + instanceId);
			ServiceUnit unit = serviceRuntimeInfo.getComponentById(node.getId());
			logger.debug("[123456] 2 Starting deploy node: "+node.getId() + "/" + instanceId);
			ServiceInstance instance = serviceRuntimeInfo.getInstanceById(node.getId(), instanceId);
			logger.debug("[123456] 3 Starting deploy node: "+node.getId() + "/" + instanceId);
			
			ServiceUnit hostNode = serviceRuntimeInfo.getComponentById(unit.getHostedId());
			logger.debug("[123456] 4 Starting deploy node: "+node.getId() + "/" + instanceId);
			logger.debug("[123456] 4.1 unit.getHostID: "+ unit.getHostedId() + "/ instance.gethostid_integer: " + instance.getHostedId_Integer());
			ServiceInstance hostInstance = serviceRuntimeInfo.getInstanceById(unit.getHostedId(), instance.getHostedId_Integer());
			logger.debug("[123456] 5 Starting deploy node: "+node.getId() + "/" + instanceId);
			
//			PioneerLogger.logger.debug("Checking node: " + unit.getId() +"/" + instance.getInstanceId() +", hosted on: " + hostNode.getId() +"/" + hostInstance.getInstanceId() +"/type: " + hostNode.getType() +" equal? " + SalsaEntityType.DOCKER.getEntityTypeString());
//			if (hostNode.getType().equals(SalsaEntityType.DOCKER.getEntityTypeString())){
//				PioneerLogger.logger.debug("DOCKER INSTALLION DETECTED !");
//				PioneerLogger.logger.debug("The node " + unit.getId() +"/" + instance.getInstanceId() + " is hosted on node " + hostNode.getId() + "/" + hostInstance.getInstanceId());
//				DockerConfigurator docker = new DockerConfigurator(hostNode.getId());
//				String url = docker.getEndpoint(hostInstance.getInstanceId());
//				docker.initDocker();
//				docker.installDockerNode(nodeId, instanceId);
//				//logger.debug("Endpoint of the DOCKER Pioneer: " + url);
//				//SalsaPioneerInterface pioneer = JAXRSClientFactory.create(url, SalsaPioneerInterface.class);
//				return Integer.toString(instanceId);			
//			}

			// Get the number of node to be deploy
			int quantity = 1;
			logger.debug("Number of instance to deploy: " + quantity);
			serviceRuntimeInfo = centerCon.getUpdateCloudServiceRuntime(serviceId);
			//int startId=serviceRuntimeInfo.getComponentById(topologyId, node.getId()).getIdCounter();
			logger.debug("Instance ID: " + instanceId);
			//centerCon.updateNodeIdCounter(topologyId, node.getId(), startId+quantity);
			
			waitingForCapabilities(node, def);
			// wait for downloading and configuring artifact itself
			
			
			logger.debug("Set status for instance " + instanceId + " to CONFIGURING !");
			centerCon.updateNodeState(serviceId, topologyId, node.getId(), instanceId, SalsaEntityState.CONFIGURING);
			
			downloadNodeArtifacts(node, def);
			// deploy the artifact
			logger.debug("Executing the deployment for node: " + node.getId() +", instance: " + instanceId);
			centerCon.updateNodeState(serviceId, topologyId, node.getId(), instanceId, SalsaEntityState.RUNNING);

			//centerCon.updateInstanceUnitCapability(serviceId, topologyId, nodeId, instanceId, capa);
			runNodeArtifacts(node, Integer.toString(instanceId), def);
			//centerCon.updateNodeState(topologyId, node.getId(), startId, SalsaEntityState.FINISHED);			
			return Integer.toString(instanceId);
		}
		
		public void removeSingleNodeInstance(TNodeTemplate node, String instanceId){
			InstrumentShareData.killProcessInstance(serviceId, topologyId, nodeId, instanceId);
			//centerCon.removeOneInstance(serviceId, topologyId, node.getId(), Integer.parseInt(instanceId));
		}
		
		
		private void multiThreadRunArtifacts(TNodeTemplate node, List<Integer> instanceIds){
			List<Thread> threads = new ArrayList<Thread>();			
			
			for (Integer i : instanceIds){
				logger.debug("STARTING Thread for node ID :" + i);				
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

			private synchronized void executeDeploymentNode() throws SalsaEngineException {
				logger.debug("Executing the deployment for node: " + node.getId() +", instance: " + instanceId );
				//runNodeArtifacts(node, Integer.toString(instanceId), def);
				deploySingleNode(node, instanceId);
				centerCon.updateNodeState(serviceId, topologyId, node.getId(), instanceId, SalsaEntityState.FINISHED);
			}

			@Override
			public void run() {
				try {
					executeDeploymentNode();
				} catch (SalsaEngineException e){
					PioneerLogger.logger.error(e.getMessage());
				}
				
			}

		}
		
		
		// waiting for capabilities and fulfill requirements
		private void waitingForCapabilities(TNodeTemplate node, TDefinitions def) throws SalsaEngineException {
			if (node.getRequirements()==null){
				return;	// node have no requirement
			}
			List<TRequirement> reqs = node.getRequirements().getRequirement();
			for (TRequirement req : reqs) {
				logger.debug("Checking requirement "+req.getId());
				TCapability cap=ToscaStructureQuery.getCapabilitySuitsRequirement(req, def);
				logger.debug("Waiting for capability: "+cap.getId());
				String value = waitRelationshipReady(topologyId, hostedVmInstanceId, cap, req);
			}
			logger.debug("Trying to get relationship host on");
			List<TRelationshipTemplate> relas = ToscaStructureQuery.getRelationshipTemplateList(SalsaRelationshipType.HOSTON.getRelationshipTypeString(), def);
			logger.debug("relas.size: " + relas.size());
			for (TRelationshipTemplate rela : relas) {
				if (rela.getSourceElement().getRef().equals(node)){
					TNodeTemplate hostNode = (TNodeTemplate) rela.getTargetElement().getRef();
					hostNode.getId();
					CloudService service = centerCon.getUpdateCloudServiceRuntime(serviceId);
					SalsaEntityState state = SalsaEntityState.CONFIGURING;
					while (state!=SalsaEntityState.RUNNING && state!=SalsaEntityState.FINISHED){
						state = service.getComponentById(hostNode.getId()).getState();
						try{
							Thread.sleep(3000);							
						} catch (InterruptedException e) {}
					}
				}
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
					// try to get script from the actions
					ServiceUnit unit = serviceRuntimeInfo.getComponentById(node.getId());
					ConfigurationCapability cc = unit.getCapabilityByName("deploy");					
					if (!cc.equals(null)){
						if (cc.getMechanism().getExecutionType().equals(ConfigurationCapability.ExecutionType.command)){
							PioneerLogger.logger.debug("Deploying component by command: " + cc.getMechanism().getExecutionRef());
							executeCommand(cc.getMechanism().getExecutionRef());
						}
					}
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
			case war:
				instrument = new WarInstrument();
				break;
			case chef:				
				instrument = new ChefInstrument();
				break;
			case chefSolo:
				instrument = new ChefSoloInstrument();
				break;
			case apt:
				instrument = new AptGetInstrument();
				break;
			default:
				instrument = new BashInstrument();
				break;
			}		
			
			
			// if this node is part of CONNECTTO, send the IP before running artifact
			if (node.getCapabilities()!=null){
				logger.debug("This node has ConnectTo capability, set it !");
				for (TCapability capa : node.getCapabilities().getCapability()){
					TRequirement req = ToscaStructureQuery.getRequirementSuitsCapability(capa, def);
					TRelationshipTemplate rela = ToscaStructureQuery.getRelationshipBetweenTwoCapaReq(capa, req, def);
					if (rela.getType().getLocalPart().equals(SalsaRelationshipType.CONNECTTO.getRelationshipTypeString())){
						try{
							logger.debug("Sending the IP of this node to the capability of CONNECTTO");						
							String ip = InetAddress.getLocalHost().getHostAddress();
							SalsaCapaReqString capaString = new SalsaCapaReqString(capa.getId(), ip);				
							centerCon.updateInstanceUnitCapability(serviceId, topologyId, node.getId(), 0, capaString);
						} catch (UnknownHostException e){
							PioneerLogger.logger.error("Cannot get the IP of the host of node: " + node.getId());
						}
					}
				}
			}
			
			PioneerLogger.logger.debug("ArtifactDeploy prepare to initiate node.");
			instrument.initiate(node);
			Object monitorObj = instrument.deployArtifact(runArt, instanceId);
			if (monitorObj==null){
				return;
			}
			PioneerLogger.logger.debug("The artifact class is: " + monitorObj.getClass().toString());
			if (artType.equals(SalsaArtifactType.sh.getString())){
				PioneerLogger.logger.debug("Ok, we can add this to the process list for monitoring");				
				InstrumentShareData.addInstanceProcess(serviceId, topologyId, node.getId(), instanceId, (Process)monitorObj); 
			}
			
			
		}
		
		/*
		 * Check if an instance of node is deployed, then its capability will be ready
		 * Note: we don't need nodeID because capaId is unique inside a Topology
		 * CHECK THE STATE OF THE FIRST INSTANCE OF THE COMPONENT
		 * CURRENTLY, instanceId is not need, but future.
		 */
		public boolean checkCapabilityReady(String capaId) throws SalsaEngineException {
			logger.debug("Check capability for capaid: " + capaId);
			TNodeTemplate node = ToscaStructureQuery.getNodetemplateOfRequirementOrCapability(capaId, def);
			if (node==null){	// capaId is not valid
				logger.debug("Check capability. Wrong capability Id");
				return false;
			}
			
			// check if there are a replica of node with Ready state
			// it doesn't care about which node, just check if existing ONE replica
			CloudService service = centerCon.getUpdateCloudServiceRuntime(serviceId);
			logger.debug("checkCapabilityReady - service: " + service.getId());
			ServiceUnit component = service.getComponentById(node.getId());	// no topology as parameter, so we can check crossing to other topology			
			logger.debug("checkCapabilityReady - service unit: " + component.getId());
			int number=component.getInstanceNumberByState(SalsaEntityState.RUNNING)+component.getInstanceNumberByState(SalsaEntityState.FINISHED);
			logger.debug("Check capability. Checking component id " + component.getId() + "  -- " + number +" number of running or finished instances.");			
			if (number == 0){
				logger.debug("CHECK CAPABILITY FALSE. " + number);				
				return false;
			} else {
				logger.debug("CHECK CAPABILITY TRUE. " + number);
				return true;
			}	
			

		}
		
		
		
		// can handle null value (see SalsaCenterConnector)
		private String waitRelationshipReady(String topoId, int replica, TCapability capa, TRequirement req) throws SalsaEngineException{
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
			
			String value=centerCon.getCapabilityValue(serviceId, topoId, nodeOfCapa.getId(), 0, capa.getId());	// note: 0 is the ID of the first node, which provide the capability
			logger.debug("waitRelationshipReady - Get the value is: " + value);
			while (value==null){
				try{
					logger.debug("waitRelationshipReady - Get the value is: " + value);
					Thread.sleep(5000);
					value=centerCon.getCapabilityValue(serviceId, topoId, nodeOfCapa.getId(), 0, capa.getId());
					
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
		
		
		public String getVMProperty(String propName) throws SalsaEngineException{
			String res="";
			ServiceInstance nodeData = serviceRuntimeInfo.getComponentById(topologyId, nodeId).getInstanceById(hostedVmInstanceId);			
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
		
		
		
		public static String executeCommand(String cmd){
			PioneerLogger.logger.debug("Execute command: " + cmd);
			try {
				Process p = Runtime.getRuntime().exec(cmd);
			    
			 
			    BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
			    String line = "";	
			    StringBuffer output = new StringBuffer();
			    while ((line = reader.readLine())!= null) {
			    	PioneerLogger.logger.debug(line);
			    }
			    p.waitFor();			    
				return output.toString();
			} catch (InterruptedException e1){
				PioneerLogger.logger.error("Error when execute command. Error: " + e1);
			} catch (IOException e2){
				PioneerLogger.logger.error("Error when execute command. Error: " + e2);
			}
			return null;
		}
		
		
		
}
