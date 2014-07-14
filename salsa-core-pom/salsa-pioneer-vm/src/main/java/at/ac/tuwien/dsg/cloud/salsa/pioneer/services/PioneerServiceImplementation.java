package at.ac.tuwien.dsg.cloud.salsa.pioneer.services;

import generated.oasis.tosca.TDefinitions;
import generated.oasis.tosca.TNodeTemplate;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import org.springframework.stereotype.Service;

import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.CloudService;
import at.ac.tuwien.dsg.cloud.salsa.common.interfaces.SalsaPioneerInterface;
import at.ac.tuwien.dsg.cloud.salsa.common.processing.SalsaCenterConnector;
import at.ac.tuwien.dsg.cloud.salsa.pioneer.ArtifactDeployer;
import at.ac.tuwien.dsg.cloud.salsa.pioneer.utils.PioneerLogger;
import at.ac.tuwien.dsg.cloud.salsa.pioneer.utils.SalsaPioneerConfiguration;
import at.ac.tuwien.dsg.cloud.salsa.tosca.processing.ToscaStructureQuery;

@Service
//@WebService(endpointInterface = "at.ac.tuwien.dsg.cloud.salsa.common.interfaces.SalsaPioneerInterface")
public class PioneerServiceImplementation implements SalsaPioneerInterface {
	
	@Override
	public String deployNode(String nodeID, int instanceId){
		
		PioneerLogger.logger.debug("Recieve command to deploy node: " + nodeID);
		
		Properties prop = new Properties();
		try {
			prop.load(new FileInputStream(SalsaPioneerConfiguration.getSalsaVariableFile()));
		} catch (IOException e1){
			PioneerLogger.logger.error("Error when loading configuration file: " + e1);
			return "fail";
		}
		String serviceId = prop.getProperty("SALSA_SERVICE_ID");
		String topologyId = prop.getProperty("SALSA_TOPOLOGY_ID");
		String vm_nodeId = prop.getProperty("SALSA_NODE_ID"); // this node
		int vm_instanceId = Integer.parseInt(prop.getProperty("SALSA_REPLICA"));
		
		SalsaCenterConnector centerCon = new SalsaCenterConnector(
				SalsaPioneerConfiguration.getSalsaCenterEndpoint(), 
				SalsaPioneerConfiguration.getWorkingDir(), PioneerLogger.logger);
		TDefinitions def = centerCon.getToscaDescription(serviceId);// get the latest service description		
		CloudService serviceRuntimeInfo = centerCon.getUpdateCloudServiceRuntime(serviceId);
				
		ArtifactDeployer deployer = new ArtifactDeployer(serviceId, topologyId, nodeID, vm_instanceId, def, centerCon, serviceRuntimeInfo);
		TNodeTemplate thisNode = ToscaStructureQuery.getNodetemplateById(nodeID, def);
				
		return deployer.deploySingleNode(thisNode, instanceId);		
	}

	
	@Override
	public String health(){
		PioneerLogger.logger.debug("Health checked, Pioneer server is alive !");
		return "alive";
	}
	
	@Override
	public String info(){
		PioneerLogger.logger.debug("Querying info of the pioneer ");
		
	    try {
	    	BufferedReader br = new BufferedReader(new FileReader(SalsaPioneerConfiguration.getSalsaVariableFile()));
	        StringBuilder sb = new StringBuilder();
	        String line = br.readLine();

	        while (line != null) {
	            sb.append(line);
	            sb.append("\n");
	            line = br.readLine();
	        }
	        br.close();
	        return sb.toString();
	    } catch (FileNotFoundException e1){
	    	PioneerLogger.logger.error(e1.toString());
	    } catch (IOException e2){
	    	PioneerLogger.logger.error(e2.toString());
	    } 
		
		return "alive";
	}



	@Override
	public String removeNodeInstance(String nodeID, int instanceId) {
		PioneerLogger.logger.debug("Recieve command to remove node: " + nodeID + "/" + instanceId);
		
		Properties prop = new Properties();
		try {
			prop.load(new FileInputStream(SalsaPioneerConfiguration.getSalsaVariableFile()));
		} catch (IOException e1){
			PioneerLogger.logger.error("Error when loading configuration file: " + e1);
			return "fail";
		}
		String serviceId = prop.getProperty("SALSA_SERVICE_ID");
		String topologyId = prop.getProperty("SALSA_TOPOLOGY_ID");
		int thisInstanceId = Integer.parseInt(prop.getProperty("SALSA_REPLICA"));
		
		SalsaCenterConnector centerCon = new SalsaCenterConnector(
				SalsaPioneerConfiguration.getSalsaCenterEndpoint(), 
				SalsaPioneerConfiguration.getWorkingDir(), PioneerLogger.logger);
		TDefinitions def = centerCon.getToscaDescription(serviceId);// get the latest service description		
		CloudService serviceRuntimeInfo = centerCon.getUpdateCloudServiceRuntime(serviceId);
		
		ArtifactDeployer deployer = new ArtifactDeployer(serviceId, topologyId, nodeID, thisInstanceId, def, centerCon, serviceRuntimeInfo);
		TNodeTemplate nodeForRemove = ToscaStructureQuery.getNodetemplateById(nodeID, def);
		
		deployer.removeSingleNodeInstance(nodeForRemove, Integer.toString(instanceId));		
		return null;
	}
	
	
}
