package at.ac.tuwien.dsg.cloud.salsa.pioneer;

import generated.oasis.tosca.TDefinitions;
import generated.oasis.tosca.TNodeTemplate;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Properties;

import javax.xml.bind.JAXBException;

import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.lifecycle.SingletonResourceProvider;

import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.CloudService;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.ServiceInstance;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.ServiceUnit;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.enums.SalsaEntityState;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.enums.SalsaEntityType;
import at.ac.tuwien.dsg.cloud.salsa.common.interfaces.SalsaPioneerInterface;
import at.ac.tuwien.dsg.cloud.salsa.common.processing.SalsaCenterConnector;
import at.ac.tuwien.dsg.cloud.salsa.engine.exception.SalsaEngineException;
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

	public static void main(String[] args) throws IOException, JAXBException, SalsaEngineException {
		// Some ready variables		
		
		if (args[0].equals("test")){
			System.out.println("test");
			//startLocalService();
			return;
		}
		
		System.out.println("Starting pioneer ...");
		PioneerLogger.logger.info("Starting pioneer");
		
		Properties prop = SalsaPioneerConfiguration.getPioneerProperties();
		
		serviceId = prop.getProperty("SALSA_SERVICE_ID");
		topologyId = prop.getProperty("SALSA_TOPOLOGY_ID");
		nodeId = prop.getProperty("SALSA_NODE_ID"); // this node
		replica = Integer.parseInt(prop.getProperty("SALSA_REPLICA")); 
		
		centerCon = new SalsaCenterConnector(
				SalsaPioneerConfiguration.getSalsaCenterEndpoint(), 
				SalsaPioneerConfiguration.getWorkingDir(), PioneerLogger.logger);
		
		
		PioneerLogger.logger.debug("Service ID: " + serviceId);
		PioneerLogger.logger.debug("This node ID: " + nodeId);
		
		
		def = centerCon.getToscaDescription(serviceId);// get the latest service description
		
		serviceRuntimeInfo = centerCon.getUpdateCloudServiceRuntime(serviceId);
		
		PioneerLogger.logger.debug("This VM is belong to service id: "+serviceRuntimeInfo.getId());
		
		ArtifactDeployer deployer = new ArtifactDeployer(serviceId, topologyId, nodeId, replica, def, centerCon, serviceRuntimeInfo);
				
		TNodeTemplate thisNode = ToscaStructureQuery.getNodetemplateById(nodeId, def);
		String command = args[0];

		switch (command) {
		
		case "startserver":
		{
			PioneerLogger.logger.debug("Start server !");			
			centerCon.updateNodeState(serviceId, topologyId, nodeId, replica, SalsaEntityState.RUNNING);
			centerCon.logMessage("Start pioneer: " + nodeId + "/" + replica);
			InstrumentShareData.startProcessMonitor();
			startPullingThread();
			//startRESTService();
			break;
		}
		case "deploy":
			centerCon.updateNodeState(serviceId, topologyId, nodeId, replica, SalsaEntityState.RUNNING);
			centerCon.logMessage("Start pioneer: " + nodeId + "/" + replica);
			//deployer.deployNodeChain(thisNode);			
			InstrumentShareData.startProcessMonitor();
			startPullingThread();
			//startRESTService();
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
		case "setcapa":
		{
			// TODO: The instanceId here is for of VM. It should be change to instanceId of upper node.			
			// How to do it: Search the HOSTON node, which have relationship with the instanceId.
			// CURRENTLY, SET CAPABILITY FOR THE FIRST INSTANCE OF THE NODE !!!
			SalsaCapaReqString capa = new SalsaCapaReqString(args[1],  args[2]);
			String nodeTmpId = ToscaStructureQuery.getNodetemplateOfRequirementOrCapability(args[1], def).getId();
			centerCon.updateInstanceUnitCapability(serviceId, topologyId, nodeTmpId, 0, capa);	
			PioneerLogger.logger.debug("Set capability "+args[1]+" as " + args[2]);
			break;
		}
		case "getcapa":	// get when node is ready
		{
			String capaIdGet = args[1];
			TNodeTemplate myNodeGet = ToscaStructureQuery.getNodetemplateOfRequirementOrCapability(capaIdGet, def);
			String capaValue = centerCon.getCapabilityValue(serviceId, topologyId, myNodeGet.getId(), replica, capaIdGet);
			System.out.println(capaValue);
			break;
		}
		case "getreq":
		{
			String reqIdGet = args[1];
			String reqValue = centerCon.getRequirementValue(serviceId, topologyId, nodeId, 0, reqIdGet);
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
//	
//	private static void startService(){
//		try {
//		System.out.println("Starting the server ...");
//		String ip = InetAddress.getLocalHost().getHostAddress();
//		String address = "http://" + ip + ":9000/pioneer";
//		Endpoint.publish(address, new PioneerServiceImplementation());
//		} catch (UnknownHostException e){
//			PioneerLogger.logger.error("Unknown host exception error !");			
//		}		
//	}
	
	private static void startRESTService(){
		try{
			System.out.println("Starting the server ...");
			String ip = InetAddress.getLocalHost().getHostAddress();
			
			JAXRSServerFactoryBean sf = new JAXRSServerFactoryBean();
	        sf.setResourceClasses(SalsaPioneerInterface.class);
	        sf.setResourceProvider(SalsaPioneerInterface.class, 
	            new SingletonResourceProvider(new PioneerServiceImplementation()));
	        //sf.setAddress("http://" + ip + ":9000/");
	        sf.setAddress("http://0.0.0.0:9000/");	        
	        sf.create();			
			
		} catch (UnknownHostException e){
			PioneerLogger.logger.error("Unknown host exception error !");			
		}
	}
	
//	
//	private static void startLocalService(){		
//		JAXRSServerFactoryBean sf = new JAXRSServerFactoryBean();
//        sf.setResourceClasses(SalsaPioneerInterface.class);
//        sf.setResourceProvider(SalsaPioneerInterface.class, 
//            new SingletonResourceProvider(new PioneerServiceImplementation()));
//        sf.setAddress("http://localhost:9000/");
//        sf.create();
//	}

	public static void startPullingThread(){
		PioneerLogger.logger.debug("Start the thread for waiting the command ...");
		Thread thread = new Thread(new pullingTaskThread());
		thread.start();
	}
	
	private static boolean checkIfPioneerIsInCharge(ServiceUnit unit, ServiceInstance instance, CloudService service){		
		if (unit.getType().equals(SalsaEntityType.WAR.getEntityTypeString())){
			ServiceUnit hostedUnit = service.getComponentById(unit.getHostedId());
			ServiceInstance hostedInstance = hostedUnit.getInstanceById(instance.getHostedId_Integer());
			if (hostedUnit.getHostedId().equals(nodeId) && hostedInstance.getInstanceId()==replica){
				return true;
			}			
		}
		return false;
	}
	
private static class pullingTaskThread implements Runnable {		
		
		@Override
		public void run()  {
			// monitor here
			PioneerLogger.logger.debug("Thread for pulling STAGE ServiceInstance is started...");		
			while (true){
				SalsaCenterConnector con = new SalsaCenterConnector(SalsaPioneerConfiguration.getSalsaCenterEndpoint(), 
										SalsaPioneerConfiguration.getWorkingDir(), PioneerLogger.logger);
				CloudService service;
				try {
					service = con.getUpdateCloudServiceRuntime(serviceId);
				} catch (SalsaEngineException e){
					PioneerLogger.logger.error("Could not retrieved service description");
					try{
						Thread.sleep(5000);
					} catch (InterruptedException e1){
						PioneerLogger.logger.error("Just to be interrupted");
					}
					continue;
				}
				List<ServiceUnit> units = service.getAllComponent();
				for (ServiceUnit unit : units) {
					// case that node is hosted directly on pioneer node
					if (unit.getHostedId().equals(nodeId)){
						List<ServiceInstance> instances = unit.getInstanceHostOn(replica);						
						for (ServiceInstance instance : instances) {							
							if (instance.getState().equals(SalsaEntityState.STAGING)){																
								PioneerLogger.logger.debug("RETRIEVE A STAGING NODE: " + unit.getId() + "/" + instance.getInstanceId());								
								PioneerServiceImplementation pioneer = new PioneerServiceImplementation();
								con.logMessage("Pioneer on node: " + nodeId + "/" + replica +" will deploy node: " + unit.getId() +"/"+ instance.getInstanceId());
								pioneer.deployNode(unit.getId(), instance.getInstanceId());
							}
							
							if (instance.getState().equals(SalsaEntityState.STAGING_ACTION)){								
								String actionName = instance.pollAction();
								if (actionName!=null){	// action==null maybe it is in process
									PioneerLogger.logger.debug("Main: Getting stating_action with queue action is: " + actionName);
									PioneerServiceImplementation pioneer = new PioneerServiceImplementation();
									// unqueue action before execute it
									con.unqueueActions(service.getId(), unit.getId(), instance.getInstanceId());
									pioneer.executeAction(unit.getId(), instance.getInstanceId(), actionName);
								}
							}
							
							// TODO: Implement the checking capability and execute
							// check action queue of the instance							
//							List<Action> actions = instance.getActions();
//							for (Action action : actions) {
//								PioneerLogger.logger.debug("Execute an action on a service instance");								
//								con.unqueueActions(serviceId, topologyId, nodeId, instance.getInstanceId(), action.getName());								
//								String cmd = action.getCommand();
//								ArtifactDeployer.executeCommand(cmd);
//							}
						}
					}
					// check 2 levels up for war artifact, e.g pioneer is at VM/docker host tomcat, node is a war
					if (unit.getType().equals(SalsaEntityType.WAR.getEntityTypeString())){
						// the unit now is the one with WAR type service unit example.war
						ServiceUnit hostedUnit = service.getComponentById(unit.getHostedId());
						// host unit would be tomcat service unit
						PioneerLogger.logger.debug("checking hostUnit:" + hostedUnit.getId());
						if (hostedUnit.getHostedId().equals(nodeId)){
							// hostUnit.gethostedID is the ID of os_OF_tomcat service unit
							PioneerLogger.logger.debug("hostedUnit.getHostId: "+hostedUnit.getHostedId());
							for (ServiceInstance instance : unit.getInstancesList()) {
								// we have a list of instance of the war file. surely that have only one instance at Allocating. Instance is at allocating so have no hostedId_Integer
								PioneerLogger.logger.debug("Checking instance: " + unit.getId() +"/" + instance.getInstanceId() + ". getHostedId_Integer: " + instance.getHostedId_Integer() +". instance state: " + instance.getInstanceState());
								// now we must get a instance of the hosted unit.
								// TODO: Just get the first instance, should be fix.
								ServiceInstance hostedInst = hostedUnit.getInstanceHostOn(replica).get(0);
								PioneerLogger.logger.debug("instance: " + unit.getId() +"/" + instance.getInstanceId() + ". getHostedId_Integer: " + instance.getHostedId_Integer());
								PioneerLogger.logger.debug("hostedInst: " + hostedInst.getId());
								if (hostedInst.getHostedId_Integer()==replica && instance.getState().equals(SalsaEntityState.STAGING)){
									PioneerLogger.logger.debug("RETRIEVE A STAGING NODE: " + unit.getId() + "/" + instance.getInstanceId());								
									PioneerServiceImplementation pioneer = new PioneerServiceImplementation();
									pioneer.deployNode(unit.getId(), instance.getInstanceId());
								}
							}
						}
					}
					
				}
				try{
					Thread.sleep(5000);
				} catch (InterruptedException e){}				
			}
		}

	}
	
		
}
