package at.ac.tuwien.dsg.cloud.salsa.engine.impl;

import generated.oasis.tosca.TDefinitions;
import generated.oasis.tosca.TNodeTemplate;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import at.ac.tuwien.dsg.cloud.salsa.cloud_connector.CloudInterface;
import at.ac.tuwien.dsg.cloud.salsa.cloud_connector.InstanceDescription;
import at.ac.tuwien.dsg.cloud.salsa.cloud_connector.multiclouds.MultiCloudConnector;
import at.ac.tuwien.dsg.cloud.salsa.cloud_connector.multiclouds.SalsaCloudProviders;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.ServiceInstance;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.enums.SalsaEntityState;
import at.ac.tuwien.dsg.cloud.salsa.common.processing.SalsaCenterConnector;
import at.ac.tuwien.dsg.cloud.salsa.engine.utils.EngineLogger;
import at.ac.tuwien.dsg.cloud.salsa.engine.utils.SalsaConfiguration;
import at.ac.tuwien.dsg.cloud.salsa.tosca.extension.SalsaInstanceDescription_VM;
import at.ac.tuwien.dsg.cloud.salsa.tosca.extension.SalsaMappingProperties;
import at.ac.tuwien.dsg.cloud.salsa.tosca.processing.ToscaStructureQuery;
import at.ac.tuwien.dsg.cloud.salsa.tosca.processing.ToscaXmlProcess;

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
	static SalsaCenterConnector centerCon;
	
	static {		
		String endpoint = SalsaConfiguration.getSalsaCenterEndpoint();
		centerCon = new SalsaCenterConnector(endpoint, "", EngineLogger.logger);
		//centerCon = JAXRSClientFactory.create(endpoint, SalsaEngineIntenalInterface.class);
	}
	
	public DeploymentEngineNodeLevel(File configFile){
		this.configFile = configFile;
	}

	/**
	 * Create a VM and return the node with properties *
	 * 
	 * @param node
	 * @param def
	 * @return A Node with capabilities properties.
	 */
	public ServiceInstance deployVMNode(String serviceId, String topologyId, String nodeId, int instanceId, TDefinitions def) {
		EngineLogger.logger.info("Creating this VM node: " + nodeId +". Tosca ID:" + def.getId());
		
		java.util.Date date= new java.util.Date();
		EngineLogger.logger.debug("TIMESTAMP - Node: " + nodeId + "/" + instanceId + ", state: Allocating(manual)" + ", Time: " + date.getTime());
		
		TNodeTemplate enhancedNode = (TNodeTemplate) ToscaStructureQuery.getNodetemplateById(nodeId, def);
		ServiceInstance repData = centerCon.getUpdateServiceUnit(serviceId, topologyId, nodeId).getInstanceById(instanceId);		
		
		EngineLogger.logger.debug("YOUR ARE HERE TO DEPLOY 4");
		
		// get the static information of TOSCA and put into SalsaComponentInstanceData property
		SalsaInstanceDescription_VM instanceDesc = new SalsaInstanceDescription_VM();
		SalsaMappingProperties mapProp = (SalsaMappingProperties) enhancedNode.getProperties().getAny();
		instanceDesc.updateFromMappingProperties(mapProp);
		EngineLogger.logger.debug("YOUR ARE HERE TO DEPLOY 5");

		String userData = prepareUserData(serviceId, topologyId, nodeId, instanceId, instanceDesc, def);

		MultiCloudConnector mcc = new MultiCloudConnector(EngineLogger.logger, configFile);

		EngineLogger.logger.debug("DEBUG: " + nodeId + " --- "	+ instanceDesc.getInstanceType());
		EngineLogger.logger.debug("CLOUD PROVIDER = " + instanceDesc.getProvider() +"//" + SalsaCloudProviders.fromString(instanceDesc.getProvider()));
		
		// start the VM
		InstanceDescription indes = mcc.launchInstance(
				nodeId +"_"+instanceId,
				SalsaCloudProviders.fromString(instanceDesc.getProvider()),
				instanceDesc.getBaseImage(),
				"",	// this is the sshKeyGen, but not need anymore. When create mcc, we pass the configFile
				userData,
				instanceDesc.getInstanceType(),
				1, 1);	// deploy min instance number of node		
		
		// update the instance property from cloud specific to SALSA format
		updateVMProperties(instanceDesc, indes);
		
		EngineLogger.logger.debug(instanceDesc.getProvider()+" -- " + instanceDesc.getBaseImage() + " -- " + instanceDesc.getInstanceType()+" -- " + enhancedNode.getMinInstances());		
		EngineLogger.logger.debug("A VM for " + nodeId + " has been created.");
		
		centerCon.updateInstanceUnitProperty(serviceId, topologyId, nodeId, instanceId, instanceDesc);				
		centerCon.updateNodeState(serviceId, topologyId, nodeId, instanceId, SalsaEntityState.CONFIGURING);
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
		sid.setQuota(inst.getQuota());
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
		userDataBuffer.append("apt-get -q update \n");
		userDataBuffer.append("apt-get -q -y install openjdk-7-jre \n");

		// download full Tosca file
		userDataBuffer.append("mkdir " + SalsaConfiguration.getWorkingDir()	+ " \n");
		userDataBuffer.append("cd " + SalsaConfiguration.getWorkingDir() + " \n");

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
			userDataBuffer.append("wget -q "			
					+ SalsaConfiguration.getPioneerWeb() + "/" + file + " \n");
			userDataBuffer.append("chmod +x "+file +" \n");
			userDataBuffer.append("cp " + file + " /usr/local/bin \n");
		}
		
		userDataBuffer.append("export PATH=$PATH:"+SalsaConfiguration.getWorkingDir() +" \n");
		userDataBuffer.append("echo 'export PATH=$PATH:"+SalsaConfiguration.getWorkingDir() +"' >> /etc/profile \n");
		
		// install all package dependencies and ganglia
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
					userDataBuffer.append("apt-get -q -y install " + pkg + " \n"); 
				}
			}
		}
		// install ganglia client for monitoring this VM
		userDataBuffer.append("apt-get -y install ganglia-monitor gmetad \n");
		// execute Pioneer
		fileLst=Arrays.asList(SalsaConfiguration.getPioneerFiles().split(","));
		userDataBuffer.append("echo Current dir `pwd` \n");
		userDataBuffer.append("java -jar " + fileLst.get(0) + " setnodestate "+node.getId()+" ready \n");
//		userDataBuffer.append("screen -dmS pion java -jar " + fileLst.get(0) + " deploy \n");	// execute deploy script
		userDataBuffer.append("screen -dmS pion java -jar " + fileLst.get(0) + " startserver \n");	


		return userDataBuffer.toString();
	}


	public static void exportTosca(TDefinitions def, String fileName) {
		ToscaXmlProcess.writeToscaDefinitionToFile(def, fileName);
	}
	
}
