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
import at.ac.tuwien.dsg.cloud.salsa.cloud_connector.InstanceType;
import at.ac.tuwien.dsg.cloud.salsa.cloud_connector.VMStates;
import at.ac.tuwien.dsg.cloud.salsa.cloud_connector.flexiant.FlexiantConnector;
import at.ac.tuwien.dsg.cloud.salsa.cloud_connector.multiclouds.MultiCloudConnector;
import at.ac.tuwien.dsg.cloud.salsa.cloud_connector.multiclouds.SalsaCloudProviders;
import at.ac.tuwien.dsg.cloud.salsa.cloud_connector.openstack.jcloud.OpenStackJcloud;
import at.ac.tuwien.dsg.cloud.salsa.cloud_connector.stratuslab.StratusLabConnector;
import at.ac.tuwien.dsg.cloud.salsa.common.processing.SalsaCenterConnector;
import at.ac.tuwien.dsg.cloud.salsa.engine.utils.EngineLogger;
import at.ac.tuwien.dsg.cloud.salsa.engine.utils.SalsaConfiguration;
import at.ac.tuwien.dsg.cloud.salsa.tosca.extension.SalsaCapaReqString;
import at.ac.tuwien.dsg.cloud.salsa.tosca.processing.ToscaStructureQuery;
import at.ac.tuwien.dsg.cloud.salsa.tosca.processing.ToscaXmlProcess;

public class TestCloudConnector {
	public static void main(String[] args) throws Exception {
		
		//testOpenstackJcloud();
		
		testFlexiant();
	}
	
	private static void testFlexiant() throws Exception {
		FlexiantConnector flex = new FlexiantConnector(EngineLogger.logger, "hungld86@gmail.com", "65c02949-d9f8-38f3-898f-9e42776635b0", "thovasoi", "https://api.sd1.flexiant.net:4442", "bde1ffba-3a8e-3315-a505-3ec67e6fa771", "886ae014-0613-3cc8-a790-16251471e624", "/opt/id_rsa_hung.pub");
		System.setProperty("jsse.enableSNIExtension", "false");
		//String id = flex.createServerJade("HungTestUbuntu1", "a064bd97-c84c-38ef-aa37-c7391a8c8259", 1, 1);
		String id = flex.launchInstance("HungTest", "a064bd97-c84c-38ef-aa37-c7391a8c8259", null, null, "touch /tmp/testHung", null, 1, 1);
		System.out.println(id);
	}
	
	private static void testOpenstackJcloud() throws Exception {
		//MultiCloudConnector multoCon = new MultiCloudConnector(EngineLogger.logger, new File("/etc/cloudUserParameters.ini"));
		//multoCon.launchInstance("HungTestVM", SalsaCloudProviders.DSG_OPENSTACK, "be6ae07b-7deb-4926-bfd7-b11afe228d6a", "Hungld", "",  InstanceType.DEFAULT, 1, 1);		
		
		OpenStackJcloud con = new OpenStackJcloud(EngineLogger.logger, "http://openstack.infosys.tuwien.ac.at/identity/v2.0/", "CELAR", "hung", "Coowcyurp8", "Hungld");		
		//con.launchInstance("hungTestVM", "1a7a06ef-6ad8-4894-bd80-825476d13843", Arrays.asList("default"), "Hungld", "", InstanceType.DEFAULT, 1, 1);
		//con.listImages();
		//con.listServers();
		con.listImages();
		//con.printServerInfo("faf683d1-f9a8-45c8-8a3a-848b9c2f0044");
		//InstanceDescription des = con.getInstanceDescriptionByID("faf683d1-f9a8-45c8-8a3a-848b9c2f0044");
		//System.out.println(des.getPrivateIp());
		
	}
	
	
	
	private static void testCenterConfigurations() throws Exception{
		SalsaConfiguration config = new SalsaConfiguration();
		System.out.println(config.getSalsaCenterEndpoint());
		System.out.println(config.getSalsaCenterEndpointForCloudProvider(SalsaCloudProviders.DSG_OPENSTACK));
		System.out.println(config.getSalsaCenterEndpointForCloudProvider(SalsaCloudProviders.LAL_STRATUSLAB));
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

	
	 
	 
	 private static void teststratus() throws Exception{
			StratusLabConnector sc = new StratusLabConnector(EngineLogger.logger, "cloud.lal.stratuslab.eu", "pdisk.lal.stratuslab.eu", "hungld", "thovasoi", "/home/hungld/.ssh/id_rsa.pub", "/home/hungld/Work/celar/implementation/stratuslab");
			ArrayList<String> sec = new ArrayList<>();
			sec.add("default");
			
			long lStartTime = System.currentTimeMillis();
			
			String id = sc.launchInstance("test","KBhcU87Wm5IZNOXZYGHrczGekwp", sec, "", "echo test", "000000960", 1, 1);
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
