package at.ac.tuwien.dsg.cloud.salsa.engine.impl;

import generated.oasis.tosca.TCapability;
import generated.oasis.tosca.TDefinitions;
import generated.oasis.tosca.TEntityTemplate.Properties;
import generated.oasis.tosca.TNodeTemplate;
import generated.oasis.tosca.TRequirement;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;

import at.ac.tuwien.dsg.cloud.salsa.cloud_connector.CloudInterface;
import at.ac.tuwien.dsg.cloud.salsa.cloud_connector.InstanceDescription;
import at.ac.tuwien.dsg.cloud.salsa.cloud_connector.multiclouds.MultiCloudConnector;
import at.ac.tuwien.dsg.cloud.salsa.cloud_connector.multiclouds.SalsaCloudProviders;
import at.ac.tuwien.dsg.cloud.salsa.common.model.SalsaComponentInstanceData;
import at.ac.tuwien.dsg.cloud.salsa.common.model.enums.SalsaEntityState;
import at.ac.tuwien.dsg.cloud.salsa.common.processing.SalsaCenterConnector;
import at.ac.tuwien.dsg.cloud.salsa.engine.utils.EngineLogger;
import at.ac.tuwien.dsg.cloud.salsa.engine.utils.SalsaConfiguration;
import at.ac.tuwien.dsg.cloud.salsa.tosca.extension.SalsaInstanceDescription_VM;
import at.ac.tuwien.dsg.cloud.salsa.tosca.extension.SalsaMappingProperties;
import at.ac.tuwien.dsg.cloud.salsa.tosca.processing.ToscaStructureQuery;
import at.ac.tuwien.dsg.cloud.salsa.tosca.processing.ToscaXmlProcess;

import com.xerox.amazonws.ec2.InstanceType;

/**
 * This class contain methods for deploying Entities in ToscaGraph included
 * Requirement and node it self
 * 
 * @author hungld
 * TODO: 
 *  - Instances management
 *  - Running information model
 */
public class DeploymentEngineNodeLevel {

	CloudInterface cloud;
	File configFile;
	

//	private static final DeploymentEngineNodeLevel INSTANCE = new DeploymentEngineNodeLevel();
	
	public DeploymentEngineNodeLevel(File configFile){
		this.configFile = configFile;
	}

//	public static DeploymentEngineNodeLevel getInstance(File configFile) {
//		return INSTANCE;
//	}
	
	private SalsaCenterConnector getCenterConnector(String serviceId){
		return new SalsaCenterConnector(SalsaConfiguration.getSalsaCenterEndpoint(), serviceId, "/tmp", EngineLogger.logger);
	}

	/**
	 * Create a VM and return the node with properties *
	 * 
	 * @param node
	 * @param def
	 * @return A Node with capabilities properties.
	 */
	public SalsaComponentInstanceData deployVMNode(String serviceId, String topologyId, String nodeId, int instanceNumber, TDefinitions def) {
		EngineLogger.logger.info("Creating this VM node: " + nodeId);
		SalsaCenterConnector centerCon = new SalsaCenterConnector(SalsaConfiguration.getSalsaCenterEndpoint(), serviceId, "/not/use/workingdir", EngineLogger.logger);

		TNodeTemplate enhancedNode = (TNodeTemplate) ToscaStructureQuery
				.getNodetemplateById(nodeId, def);
		// create a replica of node and update state: PROLOG
		SalsaComponentInstanceData repData = new SalsaComponentInstanceData(instanceNumber, null);
		
		repData.setState(SalsaEntityState.ALLOCATING);
		
		// get the static information of TOSCA and put into SalsaComponentInstanceData property
		SalsaInstanceDescription_VM instanceDesc = new SalsaInstanceDescription_VM();
		SalsaMappingProperties mapProp = (SalsaMappingProperties) enhancedNode.getProperties().getAny();
		instanceDesc.updateFromMappingProperties(mapProp);
		
		// add the initiate properties to the InstanceData
		repData.setProperties(new SalsaComponentInstanceData.Properties());		
		repData.getProperties().setAny(instanceDesc);
		
		// add the component to server
		centerCon.addComponentData(serviceId, topologyId, nodeId, repData);	
		
		//enhancedNode.setState(SalsaEntityState.PROLOGUE.getNodeStateString());

		String userData = prepareUserData(serviceId, topologyId, nodeId, instanceNumber, instanceDesc, def);

		MultiCloudConnector mcc = new MultiCloudConnector(EngineLogger.logger, configFile);

//		String maxInstance = enhancedNode.getMaxInstances();
//		int maxInstanceInt = 0;
//		if (maxInstance.equals("unbounded")) {
//			maxInstanceInt = 100;
//		} else {
//			maxInstanceInt = Integer.parseInt(enhancedNode.getMaxInstances());
//		}
		
		System.out.println("DEBUG: " + nodeId + " --- "	+ instanceDesc.getInstanceType());
		EngineLogger.logger.debug("CLOUD PROVIDER = " + instanceDesc.getProvider() +"//" + SalsaCloudProviders.fromString(instanceDesc.getProvider()));
		
		// start the VM
		InstanceDescription indes = mcc.launchInstance(
				SalsaCloudProviders.fromString(instanceDesc.getProvider()),
				instanceDesc.getBaseImage(),
				"",	// this is the sshKeyGen, but not need anymore. When create mcc, we pass the configFile
				userData,
				InstanceType.getTypeFromString(instanceDesc.getInstanceType()),
				1, 1);	// deploy min instance number of node		
		
		// update the instance property from cloud specific to SALSA format
		updateVMProperties(instanceDesc, indes);
		
		EngineLogger.logger.debug(instanceDesc.getProvider()+" -- " + instanceDesc.getBaseImage() + " -- " + instanceDesc.getInstanceType()+" -- " + enhancedNode.getMinInstances());
		
		EngineLogger.logger.debug("A VM for " + nodeId + " has been created.");
		centerCon.updateReplicaProperty(topologyId, nodeId, instanceNumber, instanceDesc);		
		
		//centerCon.updateNodeProperty(topologyId, nodeId, replica, sid);
		// The configuration will be set until pionner is started
		centerCon.setNodeState(topologyId, nodeId, instanceNumber, SalsaEntityState.CONFIGURING);		
		
		return repData;
	}
			
	private static void updateVMProperties(SalsaInstanceDescription_VM sid, InstanceDescription inst){
		//if (inst.getReplicaFQN()!=null){ sid.setReplicaNumber(inst.getReplicaFQN().getReplicaNum());}
		if (inst.getPrivateIp() !=null){ sid.setPrivateIp(inst.getPrivateIp().getHostName()); }
		if (inst.getPublicIp()  !=null){ sid.setPublicIp(inst.getPublicIp().getHostName()); }
		if (inst.getPrivateDNS()!=null){ sid.setPrivateDNS(inst.getPrivateDNS()); }
		if (inst.getPublicDNS() !=null){ sid.setPublicDNS(inst.getPublicDNS()); }
		//if (inst.getState()     !=null){ sid.setState(inst.getState()); }
		if (inst.getInstanceId()!=null){ sid.setInstanceId(inst.getInstanceId()); }		
	}
	
	// TODO: Implement - Not completed yet
	public static String prepareUserDataChef(String serviceId, String topologyId, String nodeId, int replica, TDefinitions def) {		
		StringBuffer userDataBuffer = new StringBuffer();
		userDataBuffer.append("#!/bin/bash \n");
		
		// setup chef solo
		// follow the guide: http://docs.opscode.com/chef/install_workstation.html
		// and this: https://www.digitalocean.com/community/articles/how-to-install-a-chef-server-workstation-and-client-on-ubuntu-vps-instances
		userDataBuffer.append("echo \"Running the customization scripts\" \n");		
		
		userDataBuffer.append("apt-get update \n");
		userDataBuffer.append("apt-get -y install git \n");
		userDataBuffer.append("wget -O- https://opscode.com/chef/install.sh | sudo bash \n");
		userDataBuffer.append("git clone git://github.com/opscode/chef-repo.git \n");
		
		// cookbook for node VM dependencies
		
		// cookbook for hosted-on nodes
		
		// putting cookbook for the server
		// TODO: Build recipe base on node
		userDataBuffer.append("");
		userDataBuffer.append("");
		
		
		
		
		
		return userDataBuffer.toString();
	}
	
	// Will be depricated because using Chef later
	public static String prepareUserData(String serviceId, String topologyId, String nodeId, int replica, SalsaInstanceDescription_VM instanceDesc, TDefinitions def) {
		StringBuffer userDataBuffer = new StringBuffer();
		userDataBuffer.append("#!/bin/bash \n");
		userDataBuffer.append("echo \"Running the customization scripts\" \n");

		// install java. It ad-hoc, will be improve later
		userDataBuffer.append("apt-get update \n");
		userDataBuffer.append("apt-get -y install openjdk-7-jre \n");

		// download full Tosca file
		userDataBuffer.append("mkdir " + SalsaConfiguration.getWorkingDir()
				+ " \n");
		userDataBuffer.append("cd " + SalsaConfiguration.getWorkingDir()
				+ " \n");

		// set some variable put in variable.properties
		userDataBuffer
				.append("echo '# Salsa properties file. Generated at deployment time.' > "
						+ SalsaConfiguration.getSalsaVariableFile() + " \n");
		userDataBuffer.append("echo 'SALSA_SERVICE_ID=" + serviceId + "' >> "
				+ SalsaConfiguration.getSalsaVariableFile() + " \n");
		userDataBuffer.append("echo 'SALSA_TOPOLOGY_ID=" + topologyId + "' >> "
				+ SalsaConfiguration.getSalsaVariableFile() + " \n");
		userDataBuffer.append("echo 'SALSA_REPLICA=" + replica + "' >> "
				+ SalsaConfiguration.getSalsaVariableFile() + " \n");
		userDataBuffer.append("echo 'SALSA_NODE_ID=" + nodeId + "' >> "
				+ SalsaConfiguration.getSalsaVariableFile() + " \n");
		userDataBuffer.append("echo 'SALSA_TOSCA_FILE="
				+ SalsaConfiguration.getWorkingDir() + "/" + serviceId + "' >> "
				+ SalsaConfiguration.getSalsaVariableFile() + " \n");
		userDataBuffer.append("echo 'SALSA_WORKING_DIR=" + SalsaConfiguration.getWorkingDir() + "' >> "
				+ SalsaConfiguration.getSalsaVariableFile() + " \n");
		userDataBuffer.append("echo 'SALSA_PIONEER_RUN=" + SalsaConfiguration.getPioneerRun() + "' >> "
				+ SalsaConfiguration.getSalsaVariableFile() + " \n");
		
		SalsaCloudProviders provider = SalsaCloudProviders.fromString(instanceDesc.getProvider());
		
		userDataBuffer.append("echo 'SALSA_CENTER_ENDPOINT=" + SalsaConfiguration.getSalsaCenterEndpointForCloudProvider(provider) + "' >> "
				+ SalsaConfiguration.getSalsaVariableFile() + " \n");

		// download pioneer
		List<String> fileLst=Arrays.asList(SalsaConfiguration.getPioneerFiles().split(","));
		for (String file : fileLst) {
			userDataBuffer.append("wget "			
					+ SalsaConfiguration.getPioneerWeb() + "/" + file + " \n");
			userDataBuffer.append("chmod +x "+file +" \n");
			userDataBuffer.append("cp " + file + " /usr/local/bin \n");
		}
		
		userDataBuffer.append("export PATH=$PATH:"+SalsaConfiguration.getWorkingDir() +" \n");
		userDataBuffer.append("echo 'export PATH=$PATH:"+SalsaConfiguration.getWorkingDir() +"' >> /etc/profile \n");
//		userDataBuffer.append("echo 'export SALSA_SERVICE_ID="+serviceId+"' >> /etc/profile \n");
//		userDataBuffer.append("echo 'export SALSA_NODE_ID="+nodeId+"' >> /etc/profile \n");
//		userDataBuffer.append("echo 'export SALSA_WORKING_DIR="+SalsaConfiguration.getWorkingDir()+"' >> /etc/profile \n");
		
		
		
		// install all package dependencies
		TNodeTemplate node = ToscaStructureQuery.getNodetemplateById(nodeId, def);
		if (node.getProperties() != null) {
			SalsaMappingProperties mapProp = (SalsaMappingProperties) node.getProperties().getAny();			
			SalsaInstanceDescription_VM tprop = new SalsaInstanceDescription_VM();
			tprop.updateFromMappingProperties(mapProp);
			
			if (tprop.getPackagesDependenciesList() != null){				
				List<String> lstPkgs = tprop.getPackagesDependenciesList()
						.getPackageDependency();
				for (String pkg : lstPkgs) {
					EngineLogger.logger.info("Installing package: " + pkg);
					// TODO: should change, now just support Ubuntu image				
					userDataBuffer.append("apt-get -y install " + pkg + " \n"); 
				}
			}
		}
		// execute Pioneer
		fileLst=Arrays.asList(SalsaConfiguration.getPioneerFiles().split(","));
		userDataBuffer.append("echo Current dir `pwd` \n");
		userDataBuffer.append("java -jar " + fileLst.get(0) + " setnodestate "+node.getId()+" ready \n");
		userDataBuffer.append("screen -dmS pion java -jar " + fileLst.get(0) + " deploy \n");	// execute deploy script

		return userDataBuffer.toString();
	}

	/**
	 * Deploy multiple VM at the first time, all node.
	 * At first time, all the instance ids are initiate by 0
	 * 
	 * @param serviceId	add info to this service
	 * @param topologyId add info to this topology
	 * @param nodeAndReplica with each VM node, get the min instance to deploy at first time
	 * @param def the TOSCA object
	 */
	public void deployConcurrentVMNodes(String serviceId, String topologyId, Map<String,Integer> nodeAndReplica, TDefinitions def) {
		EngineLogger.logger.info("Spawning multiple Virtual Machines...");
		List<Thread> threads = new ArrayList<Thread>();
		int replica = 0;
		for (Map.Entry<String, Integer> map : nodeAndReplica.entrySet()) {
			replica=0;
			for (int i=0; i<map.getValue(); i++){		
				Thread thread = new Thread(new deployOneVmThread(serviceId, topologyId, map.getKey(), replica, def));
				thread.start();
				threads.add(thread);
				replica+=1;
			}
			SalsaCenterConnector cenCon = getCenterConnector(serviceId);
			cenCon.updateNodeIdCounter(topologyId, map.getKey(), replica);
		}
		try {
			for (Thread thread : threads) {
				thread.join();
			}
		} catch (InterruptedException e) {
		}
		
		ToscaXmlProcess.writeToscaDefinitionToFile(def, "/tmp/"+serviceId);
	}
	
	/**
	 * Deploy a number of VM on for the VM node
	 * @param serviceId
	 * @param topologyId
	 * @param nodeId
	 * @param quantity Number of VM to be created
	 * @param startId The starting Id to count up
	 * @param def 
	 */
	public void deployConcurentVMNodesOfOneType(String serviceId, String topologyId, String nodeId, int quantity, int startId, TDefinitions def){
		EngineLogger.logger.info("Spawning multiple VM for node: " + nodeId);
		List<Thread> threads = new ArrayList<Thread>();
		int instanceId = startId;
		for (int i = 0; i<quantity; i++) {
			Thread thread = new Thread(new deployOneVmThread(serviceId, topologyId, nodeId, instanceId, def));
			thread.start();
			threads.add(thread);
			instanceId+=1;		
		}
		try {
			for (Thread thread : threads) {
				thread.join();
			}
		} catch (InterruptedException e) {
		}
	}
	
	
	
	public void deployNodeReplica(String serviceId, String topologyId, String nodeId, int startRepNum, int numberOfRep, TDefinitions def){
		List<Thread> threads = new ArrayList<Thread>();
		for (int i=startRepNum; i<numberOfRep; i++){
			Thread thread = new Thread(new deployOneVmThread(serviceId, topologyId, nodeId, i, def));
			thread.start();
			threads.add(thread);
		}
		try{
			for (Thread thread : threads) {
				thread.join();
			}
		}
		catch (InterruptedException e){			
		}
	}

	private class deployOneVmThread implements Runnable {		
		TDefinitions def;
		String serviceId;
		String topologyId;
		String nodeId;
		int replica;

		public deployOneVmThread(String serviceId, String topologyId, String nodeId, int replica, TDefinitions def) {	
			EngineLogger.logger.debug("Thread processind: nodeId=" + nodeId +", instance no.=" + replica);
			this.def = def;
			this.serviceId = serviceId;
			this.topologyId = topologyId;
			this.nodeId = nodeId;
			this.replica = replica;
		}

		private synchronized SalsaComponentInstanceData executeDeploymentNode() {
			return deployVMNode(serviceId, topologyId, nodeId, replica, def);
		}

		@Override
		public void run() {
			executeDeploymentNode();	
			
		}

	}

	public static TRequirement convertCapabilityToRequirement(TCapability cap) {
		TRequirement req = new TRequirement();
		if (cap.getProperties() == null) {
			// System.out.println("Warn: No capability to match ");
			EngineLogger.logger.debug("No capability to match");
		}
		Properties pro = new Properties();
		pro.setAny(cap.getProperties().getAny());
		req.setProperties(pro);
		req.setFulfilled("true");
		return req;
	}

	public static void exportTosca(TDefinitions def, String fileName) {
		ToscaXmlProcess.writeToscaDefinitionToFile(def, fileName);
	}
	
	public void submitService(String serviceFile){
		
			String url=SalsaConfiguration.getSalsaCenterEndpoint()
					+ "/rest/submit";
			EngineLogger.logger.debug("Trying to POST the service description to: " + url);
			HttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost(url);
			FileBody uploadfile=new FileBody(new File(serviceFile));
			MultipartEntity reqEntity = new MultipartEntity();
			reqEntity.addPart("file", uploadfile);
			post.setEntity(reqEntity);
			try {
				HttpResponse response = client.execute(post);
				if (response.getStatusLine().getStatusCode() != 200) {
					EngineLogger.logger.error("Server failed to register service: " + new File(serviceFile).getName());				
				}				
			} catch (Exception e){
				EngineLogger.logger.error("Error to submit service: " + serviceFile);
			}					
	}
	
	
}
