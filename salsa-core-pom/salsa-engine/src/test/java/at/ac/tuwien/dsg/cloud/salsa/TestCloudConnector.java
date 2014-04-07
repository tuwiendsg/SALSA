package at.ac.tuwien.dsg.cloud.salsa;

import generated.oasis.tosca.TCapability;
import generated.oasis.tosca.TDefinitions;
import generated.oasis.tosca.TRequirement;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import javax.xml.bind.JAXBException;

import at.ac.tuwien.dsg.cloud.salsa.cloud_connector.InstanceDescription;
import at.ac.tuwien.dsg.cloud.salsa.cloud_connector.ServiceDeployerException;
import at.ac.tuwien.dsg.cloud.salsa.cloud_connector.VMStates;
import at.ac.tuwien.dsg.cloud.salsa.cloud_connector.multiclouds.MultiCloudConnector;
import at.ac.tuwien.dsg.cloud.salsa.cloud_connector.multiclouds.SalsaCloudProviders;
import at.ac.tuwien.dsg.cloud.salsa.cloud_connector.openstack.JEC2ClientFactory;
import at.ac.tuwien.dsg.cloud.salsa.cloud_connector.openstack.OpenStackTypica;
import at.ac.tuwien.dsg.cloud.salsa.cloud_connector.stratuslab.StratusLabConnector;
import at.ac.tuwien.dsg.cloud.salsa.common.processing.SalsaCenterConnector;
import at.ac.tuwien.dsg.cloud.salsa.engine.utils.EngineLogger;
import at.ac.tuwien.dsg.cloud.salsa.engine.utils.SalsaConfiguration;
import at.ac.tuwien.dsg.cloud.salsa.tosca.extension.SalsaCapaReqString;
import at.ac.tuwien.dsg.cloud.salsa.tosca.processing.ToscaStructureQuery;
import at.ac.tuwien.dsg.cloud.salsa.tosca.processing.ToscaXmlProcess;

import com.xerox.amazonws.ec2.InstanceType;

public class TestCloudConnector {
	public static void main(String[] args) throws Exception {
		// testUserData();
		// testQuery();
		// testAddCapaAndProperties();
		teststratus();
		//testDsgOpenStack();
		//testMultiCloudConnector();
		//testCenterConnector();
		//testAddCapaAndProperties();
		//testCenterConfigurations();
	}
	
	private static void testCenterConfigurations() throws Exception{
		SalsaConfiguration config = new SalsaConfiguration();
		System.out.println(config.getSalsaCenterEndpoint());
		System.out.println(config.getSalsaCenterEndpointForCloudProvider(SalsaCloudProviders.DSG_OPENSTACK));
		System.out.println(config.getSalsaCenterEndpointForCloudProvider(SalsaCloudProviders.LAL_STRATUSLAB));
	}
	
	private static void testCenterConnector() throws Exception{
		String serviceId = "8ee4aae3-91c2-4952-973e-7c53b6807da8";
		SalsaCenterConnector con = new SalsaCenterConnector(SalsaConfiguration.getSalsaCenterEndpoint(), serviceId, "/tmp", EngineLogger.logger);
		con.updateNodeIdCounter("DataMarketAgence", "agence_os", 10);
		
	}

	

	private static void testMultiCloudConnector() {
		String conf = TestCloudConnector.class.getResource(
				"/cloudUserParameters.ini").getFile();
		File f = new File(conf);
		MultiCloudConnector mcc = new MultiCloudConnector(EngineLogger.logger,
				f);
		//mcc.getCloudInplementation(ConnectorsEnum.LAL_STRATUSLAB);
		mcc.removeInstance(SalsaCloudProviders.LAL_STRATUSLAB, "2167");
	}

	private static void testAddCapaAndProperties() {
		SalsaCenterConnector con = new SalsaCenterConnector(
				"http://134.158.75.65:8080/salsa-center-services",
				"91d29974-b01f-4f20-9590-7fedca4c9695", "", EngineLogger.logger);

		SalsaCapaReqString capa = new SalsaCapaReqString(
				"seedCap_IP_test", "10.0.0.41");
		con.updateInstanceUnitCapability("DataMarketAgence", "agence", 29, capa);

	}

	private static void testQuery() throws IOException, JAXBException {
		TDefinitions def = ToscaXmlProcess
				.readToscaFile("/tmp/33052803-d3de-4239-8d6a-105639366950");
		TRequirement req = (TRequirement) ToscaStructureQuery
				.getRequirementOrCapabilityById("seed_os_req", def);
		TCapability cap = ToscaStructureQuery.getCapabilitySuitsRequirement(
				req, def);
		System.out.println(cap.getId());
	}

	private static void testUserData() throws IOException, JAXBException {
		// TDefinitions def =
		// ToscaXmlProcess.readToscaFile(TestDeployTosca.class.getResource("/cassandra.tosca.xml").getFile());
		TDefinitions def = ToscaXmlProcess
				.readToscaFile("/tmp/33052803-d3de-4239-8d6a-105639366950");
		String nodeId = "os1";
		UUID deployID = UUID.fromString("33052803-d3de-4239-8d6a-105639366950");

		// String userData = DeploymentEngineNodeLevel.prepareUserData(def,
		// nodeId, deployID.toString());
		// System.out.println(userData);
	}

	private static void testConfig() {
		System.out.println(SalsaConfiguration.getSalsaCenterEndpoint());
	}

	 private static void testDsgOpenStack() throws ServiceDeployerException {
	 int socketTimeout = 5000;
	 int connectionTimeout = 3000;
	 int connectionManagerTimeout = 6000;
	 int maxRetries = 100;
	 int maxConnections = 10;
	 JEC2ClientFactory cf = new JEC2ClientFactory(
	 EngineLogger.logger,
	 "98fc6ca6893343039aa286f374266aae",
	 "4f8df10b2491426485b30712958b98e6",
	 "openstack.infosys.tuwien.ac.at",
	 8773,
	 socketTimeout, connectionTimeout,
	 connectionManagerTimeout, maxRetries, maxConnections);
	 long retryDelayMillis = 5000;
	 int deployMaxRetries = 24;
	 long deployWaitMillis = 1000;
	
	 OpenStackTypica os = new OpenStackTypica(EngineLogger.logger, cf,
	 maxRetries,
	 retryDelayMillis, deployMaxRetries, deployWaitMillis, "Hungld");
	
	 long lStartTime = System.currentTimeMillis();
		 
	 String newInstance = os.launchInstance("ami-00000163",
	 Arrays.asList("default"), "salsa", "test=1", InstanceType.LARGE, 1, 1);
	 InstanceDescription id = os.getInstanceDescriptionByID(newInstance);
	 
	 long lEndTime = System.currentTimeMillis();	 
	 long difference = lEndTime - lStartTime;	 
	 System.out.println("Elapsed milliseconds: " + difference);
	 
	 
	 System.out.println(id.getInstanceId());
	 System.out.println(id.getPrivateIp());
	 System.out.println(id.getState());
	 
	 os.removeInstance(id.getInstanceId());
	 
	 }
	 
	 
	 private static void teststratus() throws Exception{
			StratusLabConnector sc = new StratusLabConnector(EngineLogger.logger, "cloud.lal.stratuslab.eu", "pdisk.lal.stratuslab.eu", "hungld", "thovasoi", "/home/hungld/.ssh/id_rsa.pub", "/home/hungld/Work/celar/implementation/stratuslab");
			ArrayList<String> sec = new ArrayList<>();
			sec.add("default");
			
			long lStartTime = System.currentTimeMillis();
			
			String id = sc.launchInstance("KBhcU87Wm5IZNOXZYGHrczGekwp", sec, "", "echo test", InstanceType.SMALL, 1, 1);
			for (int i=0;i<10000;i++){
				InstanceDescription des = sc.getInstanceDescriptionByID(id);
				if (des.getState().equals(VMStates.Running)){
					System.out.println("RUNNING !");
					break;
				} else {
					try{
						System.out.println("state: " +des.getState().getString()+". wait for " + i);
						Thread.sleep(1000);
					} catch (Exception e){
						
					}
				}
			}
			
			 long lEndTime = System.currentTimeMillis();	 
			 long difference = lEndTime - lStartTime;	 
			 System.out.println("Elapsed milliseconds: " + difference);
			
			 try{
					Thread.sleep(5);
				} catch (Exception e){
					
				}
			 sc.removeInstance(id);
			
		}

}
