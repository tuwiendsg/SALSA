package at.ac.tuwien.dsg.cloud.salsa;

import generated.oasis.tosca.TDefinitions;

import java.io.File;

import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.CloudService;
import at.ac.tuwien.dsg.cloud.salsa.common.processing.SalsaXmlDataProcess;
import at.ac.tuwien.dsg.cloud.salsa.engine.impl.SalsaToscaDeployer;
import at.ac.tuwien.dsg.cloud.salsa.engine.services.SalsaEngineImplAll;
import at.ac.tuwien.dsg.cloud.salsa.tosca.processing.ToscaXmlProcess;

public class TestOtherFunctions {

	public static void main(String[] args) throws Exception{
		testConvertFromTosca();
	}
	
	public static void testBuildDataFromTosca() throws Exception{
		TDefinitions def = ToscaXmlProcess.readToscaFile("/home/hungld/test/tosca/2-DeployExecutableOnVM.xml");
		SalsaToscaDeployer deployer = new SalsaToscaDeployer(new File("/etc/cloudUserParameters.ini"));
		CloudService service = SalsaToscaDeployer.buildRuntimeDataFromTosca(def);
		SalsaXmlDataProcess.writeCloudServiceToFile(service, "/tmp/testSalsa.data");
	}
	
	
	
	public static void testCreateSalsaEntity() throws Exception{
//		CloudService service = new CloudService();
//		ConfigurationCapabilities confCatas = new ConfigurationCapabilities();
//		service.setConfiguationCapapabilities(confCatas);
//		ConfigurationCapability e = new ConfigurationCapability();
//		e.getMechanism().setExecutionType(ExecutionType.command);
//		e.getMechanism().setExecutionREF("/bin/date");		
//		confCatas.getConfigurationCapabilties().add(e);
//		SalsaXmlDataProcess.writeCloudServiceToFile(service, "/tmp/testSalsa.data");
		
	}
	
	public static void testConvertFromTosca() throws Exception {
		SalsaToscaDeployer deployer = new SalsaToscaDeployer(new File(SalsaEngineImplAll.class.getResource("/cloudUserParameters.ini").getFile()));
		TDefinitions def = ToscaXmlProcess.readToscaFile("/tmp/salsa/enriched1.xml");
		CloudService service = SalsaToscaDeployer.buildRuntimeDataFromTosca(def);
		System.out.println(service.getName());
		System.out.println(service.getAllComponent());
		SalsaXmlDataProcess.writeCloudServiceToFile(service, "/tmp/salsa/datafile.xml");
	}

}
