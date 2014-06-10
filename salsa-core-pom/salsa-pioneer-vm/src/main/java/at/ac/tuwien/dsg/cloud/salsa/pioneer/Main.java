package at.ac.tuwien.dsg.cloud.salsa.pioneer;

import generated.oasis.tosca.TDefinitions;
import generated.oasis.tosca.TNodeTemplate;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

import javax.xml.bind.JAXBException;
import javax.xml.ws.Endpoint;

import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.CloudService;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.enums.SalsaEntityState;
import at.ac.tuwien.dsg.cloud.salsa.common.processing.SalsaCenterConnector;
import at.ac.tuwien.dsg.cloud.salsa.pioneer.instruments.InstrumentShareData;
import at.ac.tuwien.dsg.cloud.salsa.pioneer.services.PioneerServiceImplementation;
import at.ac.tuwien.dsg.cloud.salsa.pioneer.utils.PioneerLogger;
import at.ac.tuwien.dsg.cloud.salsa.pioneer.utils.SalsaPioneerConfiguration;
import at.ac.tuwien.dsg.cloud.salsa.tosca.extension.SalsaCapaReqString;
import at.ac.tuwien.dsg.cloud.salsa.tosca.processing.ToscaStructureQuery;

/**
 * This Pioneer service is set up on VM node after the VM started.
 * It does the deployment for the higher level node, based on HOSTON relationship chain.
 * It assumes that all the nodes on top of this VM are belong to a same service, topology,
 * that why it will get the serviceId, topologyId from the VM properties.
 * 
 * It will be built as a Jar execution program, and can be call locally on the VM.
 * 
 * @author Le Duc Hung
 * 
 * @Usage: # java -jar salsa-pioneer-vm.jar <command> [options]<br>
 *         Available commands: <br>
 *         - startserver: start the server to listen to request from salsa-engine
 *         - deploy: start deploy the higher components on top of VM <br>
 *         - checkcapa {id}: check capability <id>. return true/false <br>
 *         - getcapa {id}: return a capability String of <id> <br>
 *         - waitcapa {CapaId}: block and wait until node capability is ready and capa is available
 *         - setcapa {id} {value}: set the capability String of <id> <br>
 *         - waitreq {reqId}: block and wait until the node requirement is ready, return the value of requirement if having
 *         - setnodestate {id> {value} : set node state<br>
 * TODO: Replica is not dedicated for upper nodes. Upper nodes get lower node replica
 */
public class Main {
	private static String serviceId;
	private static String topologyId;
	private static String nodeId;	// this is VM node ID. It may contain other nodes on it
	private static int replica;		// this is Replica number of the VM
	private static TDefinitions def;	
	private static SalsaCenterConnector centerCon;
	private static CloudService serviceRuntimeInfo;

	public static void main(String[] args) throws IOException, JAXBException {
		// Some ready variables		
		
		if (args[0].equals("test")){
			System.out.println("test");			
			return;
		}
		
		System.out.println("Starting pioneer ...");
		PioneerLogger.logger.info("Starting pioneer");
		
		Properties prop = new Properties();
		prop.load(new FileInputStream(SalsaPioneerConfiguration.getSalsaVariableFile()));		
		serviceId = prop.getProperty("SALSA_SERVICE_ID");
		topologyId = prop.getProperty("SALSA_TOPOLOGY_ID");
		nodeId = prop.getProperty("SALSA_NODE_ID"); // this node
		replica = Integer.parseInt(prop.getProperty("SALSA_REPLICA")); 
		
		centerCon = new SalsaCenterConnector(
				SalsaPioneerConfiguration.getSalsaCenterEndpoint(), serviceId,
				SalsaPioneerConfiguration.getWorkingDir(), PioneerLogger.logger);
		
		
		PioneerLogger.logger.debug("Service ID: " + serviceId);
		PioneerLogger.logger.debug("This node ID: " + nodeId);
		
		
		def = centerCon.getToscaDescription();// get the latest service description
		
		serviceRuntimeInfo = centerCon.getUpdateCloudServiceRuntime();
		
		PioneerLogger.logger.debug("This VM is belong to service id: "+serviceRuntimeInfo.getId());
		
		ArtifactDeployer deployer = new ArtifactDeployer(serviceId, topologyId, nodeId, replica, def, centerCon, serviceRuntimeInfo);
				
		TNodeTemplate thisNode = ToscaStructureQuery.getNodetemplateById(nodeId, def);
		String command = args[0];

		switch (command) {
		
		case "startserver":
		{
			PioneerLogger.logger.debug("Start server !");
			InstrumentShareData.startProcessMonitor();
			startService();
			break;
		}
		case "deploy":
			centerCon.updateNodeState(topologyId, nodeId, replica, SalsaEntityState.RUNNING);
			deployer.deployNodeChain(thisNode);
			InstrumentShareData.startProcessMonitor();
			// start server to listen to
			startService();
			break;
		case "checkcapa":	// remote			
			if (deployer.checkCapabilityReady(args[1])) {
				System.out.println("true");
				PioneerLogger.logger.debug("Check capability "+args[1]+": is ready");
			} else {
				System.out.println("false");
				PioneerLogger.logger.debug("Check capability "+args[1]+": is not ready");
			}
			break;
//		case "waitcapa":
//			String capaVal = deployer.waitRelationshipReady(topologyId, replica, args[1]);
//			System.out.println(capaVal);
//			break;
		case "waitreq":	// the software node when call outside couldn't recognize its instanceID
		{
			String reqResult = deployer.waitRequirement(args[1]);
			System.out.println(reqResult);
			break;
		}
		case "setcapa":
		{
			// TODO: The instanceId here is for of VM. It should be change to instanceId of upper node.			
			// How to do it: Search the HOSTON node, which have relationship with the instanceId.
			// CURRENTLY, SET CAPABILITY FOR THE FIRST INSTANCE OF THE NODE !!!
			SalsaCapaReqString capa = new SalsaCapaReqString(args[1],  args[2]);
			String nodeTmpId = ToscaStructureQuery.getNodetemplateOfRequirementOrCapability(args[1], def).getId();
			centerCon.updateInstanceUnitCapability(topologyId, nodeTmpId, 0, capa);	
			PioneerLogger.logger.debug("Set capability "+args[1]+" as " + args[2]);
			break;
		}
		case "getcapa":	// get when node is ready
		{
			String capaIdGet = args[1];
			TNodeTemplate myNodeGet = ToscaStructureQuery.getNodetemplateOfRequirementOrCapability(capaIdGet, def);
			String capaValue = centerCon.getCapabilityValue(topologyId, myNodeGet.getId(), replica, capaIdGet);
			System.out.println(capaValue);
			break;
		}
		case "getreq":
		{
			String reqIdGet = args[1];
			String reqValue = centerCon.getRequirementValue(topologyId, nodeId, 0, reqIdGet);
			System.out.println(reqValue);
			break;
		}
		case "setnodestate":
		{
			//centerCon.updateNodeState(topologyId, args[1], replica, SalsaEntityState.fromString(args[2]));
			//def = centerCon.updateTopology();
			break;
		}
		case "getprop":
		{
			deployer.getVMProperty(args[1]);
			break;
		}
		default:
			PioneerLogger.logger.error("Unknown command: " + command);
			break;
		}

	}
	
	private static void startService(){
		try {
		System.out.println("Starting the server ...");
		String ip = InetAddress.getLocalHost().getHostAddress();
		String address = "http://" + ip + ":9000/pioneer";
		Endpoint.publish(address, new PioneerServiceImplementation());
		} catch (UnknownHostException e){
			PioneerLogger.logger.error("Unknown host exception error !");			
		}		
	}
		
}
