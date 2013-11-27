package at.ac.tuwien.dsg.cloud.salsa.salsa_pioneer_vm;

import generated.oasis.tosca.TCapability;
import generated.oasis.tosca.TDefinitions;
import generated.oasis.tosca.TEntityTemplate;
import generated.oasis.tosca.TNodeTemplate;
import generated.oasis.tosca.TRequirement;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.xml.bind.JAXBException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import at.ac.tuwien.dsg.cloud.salsa.common.data.SalsaComponentReplicaData;
import at.ac.tuwien.dsg.cloud.salsa.common.data.SalsaEntityState;
import at.ac.tuwien.dsg.cloud.salsa.common.processes.SalsaCenterConnector;
import at.ac.tuwien.dsg.cloud.salsa.salsa_pioneer_vm.utils.PioneerLogger;
import at.ac.tuwien.dsg.cloud.salsa.salsa_pioneer_vm.utils.SalsaConfiguration;
import at.ac.tuwien.dsg.cloud.salsa.tosca.ToscaStructureQuery;
import at.ac.tuwien.dsg.cloud.tosca.extension.ScriptArtifactProperties;

/**
 * Execute on cloud instance to execute command.
 * 
 * @author hungld Usage: # java -jar salsa-pioneer-vm.jar <command> [options]
 *         Available commands: 
 *         - deploy: start deploy the higher components on top of VM 
 *         - checkcapa <id>: check capability <id>. return true/false 
 *         - getcapa <id>: return a capability String of <id> 
 *         - waitcapa <id>: block and wait until node capability is ready and capa is available
 *         - setcapa <id> <value>: set the capability String of <id> 
 *         - setnodestate <id> <value> : set node state
 */
public class Main {
	private static String serviceId;
	private static String topologyId;
	private static String nodeId;	// this is VM node ID. It may contain other node on it
	private static int replica;
	private static TDefinitions def;	
	private static SalsaCenterConnector centerCon;

	public static void main(String[] args) throws IOException, JAXBException {
		// Some ready variables		
		
		if (args[0].equals("test")){
			System.out.println("test");			
			return;
		}				
		
		Properties prop = new Properties();
		prop.load(new FileInputStream(SalsaConfiguration.getSalsaVariableFile()));
		serviceId = prop.getProperty("SALSA_SERVICE_ID");
		topologyId = prop.getProperty("SALSA_TOPOLOGY_ID");
		nodeId = prop.getProperty("SALSA_NODE_ID"); // this node
		replica = Integer.parseInt(prop.getProperty("SALSA_REPLICA")); 
		
		centerCon = new SalsaCenterConnector(
				SalsaConfiguration.getSalsaCenterEndpoint(), serviceId,
				SalsaConfiguration.getWorkingDir(), PioneerLogger.logger);
				
		
		PioneerLogger.logger.debug("Service ID: " + serviceId);
		PioneerLogger.logger.debug("This node ID: " + nodeId);
		
		def = centerCon.updateTopology();// get the latest service description
		
		PioneerLogger.logger.debug("Update topology done !");
		TNodeTemplate thisNode = ToscaStructureQuery.getNodetemplateById(nodeId, def);
		String command = args[0];

		switch (command) {
		case "deploy":			
			deployNodeChain(thisNode);
			break;
		case "checkcapa":	// remote			
			if (checkCapabilityReady(args[1], def)) {
				System.out.println("true");
				PioneerLogger.logger.debug("Check capability "+args[1]+": is ready");
			} else {
				System.out.println("false");
				PioneerLogger.logger.debug("Check capability "+args[1]+": is not ready");
			}
			break;
		case "waitcapa":
			String capaVal = waitCapability(args[1]);
			System.out.println(capaVal);
			break;
		case "setcapa":
			centerCon.setCapability(args[1], args[2], def);
			def = centerCon.updateTopology();
			PioneerLogger.logger.debug("Set capability "+args[1]+" as " + args[2]);
			break;
		case "getcapa":	// get when node is ready
			String capaValue = centerCon.getCapability(args[1], def);
			def = centerCon.updateTopology();
			System.out.println(capaValue);
			break;
		case "setnodestate":
			centerCon.setNodeState(topologyId, args[1], replica, SalsaEntityState.fromString(args[2]));
			def = centerCon.updateTopology();
			break;
		default:
			PioneerLogger.logger.error("Unknown command: " + command);
		}

	}

	// Deploy upper nodes which are hosted on the current VM node
	private static void deployNodeChain(TNodeTemplate thisNode)
			throws IOException {
		// download full topology from web
		def = centerCon.updateTopology();
		PioneerLogger.logger.debug("Update topology service: " + def.getId());
		// List of upper nodes, which will be deployed

		List<TNodeTemplate> upperNodes = ToscaStructureQuery
				.getNodeTemplateWithRelationshipChain("HOSTON", thisNode, def);
		PioneerLogger.logger.debug("Chain for node: " + thisNode.getId());
		for (TNodeTemplate chainNode : upperNodes) {
			// submit a new node
			//SalsaComponentData data = new SalsaComponentData(chainNode.getId(), chainNode.getType().getLocalPart(), replica, null); // same replica with VM, no properties
			SalsaComponentReplicaData data = new SalsaComponentReplicaData(replica);
			data.setState(SalsaEntityState.PROLOGUE);			
			centerCon.addComponentData(serviceId, topologyId, chainNode.getId(), data);
			
			PioneerLogger.logger.debug("Starting deploy node: "+chainNode.getId());
			centerCon.setNodeState(topologyId, chainNode.getId(), replica, SalsaEntityState.PROLOGUE);
			def = centerCon.updateTopology();
			waitingForCapabilities(chainNode, def);
			downloadAndRunNodeArtifacts(chainNode, def);
		}

	}
		

	// waiting for capabilities and fulfill requirements
	private static void waitingForCapabilities(TNodeTemplate node,
			TDefinitions def) {
		if (node.getRequirements()==null){
			return;	// node have no requirement
		}
		List<TRequirement> reqs = node.getRequirements().getRequirement();
		for (TRequirement req : reqs) {
			PioneerLogger.logger.debug("Checking requirement "+req.getId());
			TCapability cap=ToscaStructureQuery.getCapabilitySuitsRequirement(req, def);
			PioneerLogger.logger.debug("Waiting for capability: "+cap.getId());
			waitCapability(cap.getId());
		}
	}

	// Download and run 1 node. Node shouldn't be VM
	private static void downloadAndRunNodeArtifacts(TNodeTemplate node,
			TDefinitions def) {
		centerCon.setNodeState(topologyId, node.getId(), replica, SalsaEntityState.PROLOGUE);
		// get Artifact list
		List<String> arts = ToscaStructureQuery
				.getDeployArtifactTemplateReferenceList(node, def);

		for (String art : arts) {
			try {
				PioneerLogger.logger.debug("Downloading artifact for: "
						+ node.getId() + ". URL:" + art);
				URL url = new URL(art);
				String filePath = SalsaConfiguration.getWorkingDir()
						+ File.separator + node.getId()
						+ File.separator
						+ FilenameUtils.getName(url.getFile());
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
		// search and run <Acrion>deploy</Action> scripts
		TEntityTemplate.Properties prop = node.getProperties();
		if (prop == null) {
			PioneerLogger.logger.debug(node.getId()
					+ " doesn't have a deploy artifact");
			return;
		}
		ScriptArtifactProperties script = (ScriptArtifactProperties) prop
				.getAny();
		if (script.getAction().equals("deploy")) {
			String runArt = script.getScriptFile();
			Process p;
			ProcessBuilder pb = new ProcessBuilder("sh",runArt);
			
			Map<String,String> env = pb.environment();
			String envPATH = env.get("PATH")+":"+SalsaConfiguration.getWorkingDir();
			env.put("PATH", envPATH);
			PioneerLogger.logger.debug("env PATH="+envPATH);
			// e.g, working in /opt/salsa/<nodeid>
			pb.directory(new File(SalsaConfiguration.getWorkingDir()+File.separator+node.getId()));
			try {
				p = pb.start();
				p.waitFor();

				BufferedReader reader = new BufferedReader(
						new InputStreamReader(p.getInputStream()));
				String line = reader.readLine();
				while (line != null) {
					line = reader.readLine();
					//PioneerLogger.logger.debug(line);
				}
				// update node state
				centerCon.setNodeState(topologyId, node.getId(), replica, SalsaEntityState.READY);	
				def = centerCon.updateTopology();
			} catch (IOException e) {
				PioneerLogger.logger.debug(e.toString());
				centerCon.setNodeState(topologyId, node.getId(), replica, SalsaEntityState.ERROR);
				def = centerCon.updateTopology();
			} catch (InterruptedException e1) {
			}
		} else {
			PioneerLogger.logger.debug(node.getId()
					+ " doesn't have a deploy artifact");
			return;
		}

	}

	

	/*
	 * Check if a node is deployed, then its capability will be ready
	 */
	private static boolean checkCapabilityReady(String capaId, TDefinitions def) {
		TNodeTemplate node = ToscaStructureQuery
				.getNodetemplateOfRequirementOrCapability(capaId, def);
		return node.getState()
				.equals(SalsaEntityState.READY.getNodeStateString());
	}
	
	private static String waitCapability(String capaId){
		while (!checkCapabilityReady(capaId, def)){
			try{
				Thread.sleep(5000);
				def = centerCon.updateTopology();
			} catch (InterruptedException e) {}
		}
		return centerCon.getCapability(capaId, def);
	}

	

	

	

}
