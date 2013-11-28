package at.ac.tuwien.dsg.cloud.salsa.service.impl;

import generated.oasis.tosca.TCapability;
import generated.oasis.tosca.TDefinitions;
import generated.oasis.tosca.TEntityTemplate.Properties;
import generated.oasis.tosca.TNodeTemplate;
import generated.oasis.tosca.TRequirement;
import generated.oasis.tosca.TServiceTemplate;

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

import at.ac.tuwien.dsg.cloud.data.InstanceDescription;
import at.ac.tuwien.dsg.cloud.salsa.common.model.SalsaCloudServiceData;
import at.ac.tuwien.dsg.cloud.salsa.common.model.SalsaComponentData;
import at.ac.tuwien.dsg.cloud.salsa.common.model.SalsaComponentReplicaData;
import at.ac.tuwien.dsg.cloud.salsa.common.model.SalsaTopologyData;
import at.ac.tuwien.dsg.cloud.salsa.common.model.data.SalsaInstanceDescription;
import at.ac.tuwien.dsg.cloud.salsa.common.model.enums.SalsaEntityState;
import at.ac.tuwien.dsg.cloud.salsa.common.processes.SalsaCenterConnector;
import at.ac.tuwien.dsg.cloud.salsa.tosca.ToscaStructureQuery;
import at.ac.tuwien.dsg.cloud.salsa.tosca.ToscaXmlProcess;
import at.ac.tuwien.dsg.cloud.salsa.tosca.extension.ToscaVMNodeTemplatePropertiesEntend;
import at.ac.tuwien.dsg.cloud.salsa.utils.EngineLogger;
import at.ac.tuwien.dsg.cloud.salsa.utils.MultiCloudConfiguration;
import at.ac.tuwien.dsg.cloud.salsa.utils.SalsaConfiguration;
import at.ac.tuwien.dsg.cloud.services.CloudInterface;

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

	private static final DeploymentEngineNodeLevel INSTANCE = new DeploymentEngineNodeLevel();

	public static DeploymentEngineNodeLevel getInstance() {
		return INSTANCE;
	}

	/**
	 * Create a VM and return the node with properties *
	 * 
	 * @param node
	 * @param def
	 * @return A Node with capabilities properties.
	 */
	public static SalsaComponentReplicaData deployVMNode(String serviceId, String topologyId, String nodeId, int replica, TDefinitions def) {
		EngineLogger.logger.info("Creating this VM node: " + nodeId);
		SalsaCenterConnector centerCon = new SalsaCenterConnector(SalsaConfiguration.getSalsaCenterEndpoint(), serviceId, "/not/use/workingdir", EngineLogger.logger);

		TNodeTemplate enhancedNode = (TNodeTemplate) ToscaStructureQuery
				.getNodetemplateById(nodeId, def);
		// create a replica of node and update state: PROLOG
		SalsaComponentReplicaData repData = new SalsaComponentReplicaData(replica, null);
		repData.setState(SalsaEntityState.ALLOCATING);
		centerCon.addComponentData(serviceId, topologyId, nodeId, repData);		
		
		//enhancedNode.setState(SalsaEntityState.PROLOGUE.getNodeStateString());

		String userData = prepareUserData(serviceId, topologyId, nodeId, replica, def);
		ToscaVMNodeTemplatePropertiesEntend prop = (ToscaVMNodeTemplatePropertiesEntend) enhancedNode
				.getProperties().getAny();
		System.out.println(prop.toString());
		MultiCloudConnector mcc = new MultiCloudConnector();

		String maxInstance = enhancedNode.getMaxInstances();
		int maxInstanceInt = 0;
		if (maxInstance.equals("unbounded")) {
			maxInstanceInt = 100;
		} else {
			maxInstanceInt = Integer.parseInt(enhancedNode.getMaxInstances());
		}
		System.out.println("DEBUG: " + nodeId + " --- "
				+ prop.getInstanceType());
		InstanceDescription indes = mcc.launchInstance(prop.getCloudProvider(),
				prop.getBaseImage(),
				MultiCloudConfiguration.getSshKeyName(prop.getCloudProvider()),
				userData,
				InstanceType.getTypeFromString(prop.getInstanceType()),
				enhancedNode.getMinInstances(), maxInstanceInt);
			
		SalsaInstanceDescription sid = convertInstanceDescription(indes);		
		
		EngineLogger.logger.debug("A VM for " + nodeId + " has been created.");

		// build component data		
		SalsaComponentReplicaData.Properties propSalsaData = new SalsaComponentReplicaData.Properties();
		propSalsaData.setAny(sid);		
		
		//centerCon.updateNodeProperty(topologyId, nodeId, replica, sid);
		centerCon.setNodeState(topologyId, nodeId, replica, SalsaEntityState.CONFIGURING);		
		
		return repData;
	}
			
	private static SalsaInstanceDescription convertInstanceDescription(InstanceDescription inst){		
		SalsaInstanceDescription sid = new SalsaInstanceDescription();
		if (inst.getReplicaFQN()!=null){ sid.setReplicaNumber(inst.getReplicaFQN().getReplicaNum());}
		if (inst.getPrivateIp() !=null){ sid.setPrivateIp(inst.getPrivateIp().getHostName()); }
		if (inst.getPublicIp()  !=null){ sid.setPublicIp(inst.getPublicIp().getHostName()); }
		if (inst.getPrivateDNS()!=null){ sid.setPrivateDNS(inst.getPrivateDNS()); }
		if (inst.getPublicDNS() !=null){ sid.setPublicDNS(inst.getPublicDNS()); }
		//if (inst.getState()     !=null){ sid.setState(inst.getState()); }
		if (inst.getInstanceId()!=null){ sid.setInstanceId(inst.getInstanceId()); }
		
		return sid;
	}

	public static String prepareUserData(String serviceId, String topologyId, String nodeId, int replica, TDefinitions def) {
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
//		userDataBuffer.append("wget http://"
//				+ SalsaConfiguration.getSalsaCenterIP()
//				+ SalsaConfiguration.getServiceInstanceRepo() + "/" + serviceId
//				+ " \n");

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

		// download pioneer
		List<String> fileLst=Arrays.asList(SalsaConfiguration.getPioneerFiles().split(","));
		for (String file : fileLst) {
			userDataBuffer.append("wget "					
					+ SalsaConfiguration.getPioneerWeb() + "/"
					+ file + " \n");
			userDataBuffer.append("chmod +x "+file +" \n");
		}
		userDataBuffer.append("cp *.sh /usr/local/bin \n");
//		userDataBuffer.append("echo 'export PATH=$PATH:"+SalsaConfiguration.getWorkingDir() +"' >> /etc/profile \n");
//		userDataBuffer.append("echo 'export SALSA_SERVICE_ID="+serviceId+"' >> /etc/profile \n");
//		userDataBuffer.append("echo 'export SALSA_NODE_ID="+nodeId+"' >> /etc/profile \n");
//		userDataBuffer.append("echo 'export SALSA_WORKING_DIR="+SalsaConfiguration.getWorkingDir()+"' >> /etc/profile \n");
		
		
		
		// install all package dependencies
		TNodeTemplate node = ToscaStructureQuery.getNodetemplateById(nodeId,
				def);
		if (node.getProperties() != null) {
			ToscaVMNodeTemplatePropertiesEntend tprop = (ToscaVMNodeTemplatePropertiesEntend) node
					.getProperties().getAny();
			List<String> lstPkgs = tprop.getPackagesDependencies()
					.getPackageDependency();
			for (String pkg : lstPkgs) {
				// TODO: should change, now just support Ubuntu image				
				userDataBuffer.append("apt-get -y install " + pkg + " \n"); 
			}
		}
		// execute Pioneer
		fileLst=Arrays.asList(SalsaConfiguration.getPioneerFiles().split(","));
		userDataBuffer.append("echo Current dir `pwd` \n");
		userDataBuffer.append("java -jar " + fileLst.get(0) + " setnodestate "+node.getId()+" ready \n");
		userDataBuffer.append("java -jar " + fileLst.get(0) + " deploy \n");	// execute deploy script

		return userDataBuffer.toString();
	}

	/*
	 * Deploy a number of VMs at a time
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
		}
		try {
			for (Thread thread : threads) {
				thread.join();
			}
		} catch (InterruptedException e) {
		}
		
		ToscaXmlProcess.writeToscaDefinitionToFile(def, "/tmp/"+serviceId);
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

	private static class deployOneVmThread implements Runnable {		
		TDefinitions def;
		String serviceId;
		String topologyId;
		String nodeId;
		int replica;

		public deployOneVmThread(String serviceId, String topologyId, String nodeId, int replica, TDefinitions def) {			
			this.def = def;
			this.serviceId = serviceId;
			this.topologyId = topologyId;
			this.nodeId = nodeId;
			this.replica = replica;
		}

		private synchronized SalsaComponentReplicaData executeDeploymentNode() {
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
