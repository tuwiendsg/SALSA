package at.ac.tuwien.dsg.cloud.salsa;

import org.apache.cxf.jaxrs.client.JAXRSClientFactory;

import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.CloudService;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.ServiceTopology;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.ServiceUnit;
import at.ac.tuwien.dsg.cloud.salsa.common.interfaces.SalsaEngineIntenalInterface;
import at.ac.tuwien.dsg.cloud.salsa.common.interfaces.SalsaPioneerInterface;
import at.ac.tuwien.dsg.cloud.salsa.common.processing.SalsaCenterConnector;
import at.ac.tuwien.dsg.cloud.salsa.engine.impl.PioneerConnector;
import at.ac.tuwien.dsg.cloud.salsa.engine.services.SalsaEngineInternal;
import at.ac.tuwien.dsg.cloud.salsa.engine.utils.EngineLogger;
import at.ac.tuwien.dsg.cloud.salsa.tosca.extension.SalsaCapaReqString;

public class TestServiceConnector {
	public static void main(String[] args) throws Exception {
		//SalsaEngineIntenalInterface engine = JAXRSClientFactory.create("http://128.130.172.215:8080/salsa-engine/rest/", SalsaEngineIntenalInterface.class);
		//System.out.println(engine.getService("6c31d944-414a-41fa-85be-4066cbadd87b").getStatus());
		//System.out.println(engine.getToscaService("6c31d944-414a-41fa-85be-4066cbadd87b").getDate());
		
//		PioneerConnector pioneer = new PioneerConnector("10.99.0.10");
//		System.out.println(pioneer.checkHealth());
		
		SalsaCenterConnector cenCon = new SalsaCenterConnector("http://128.130.172.215:8080/salsa-engine", "/tmp", EngineLogger.logger);
		//CloudService service =cenCon.getUpdateCloudServiceRuntime(); 
		//String str = cenCon.getUpdateCloudServiceRuntimeXML();
		//System.out.println(str);
		//System.out.println(service.getId());
		
//		ServiceUnit unit = cenCon.getUpdateServiceUnit("6c31d944-414a-41fa-85be-4066cbadd87b", "DataMarketAgence", "agence_os");
//		System.out.println(unit.getId());
//		
//		SalsaCapaReqString capaStr = new SalsaCapaReqString("ip", "bla");
//		cenCon.updateInstanceUnitCapability("91a5b948-9cce-4622-a44b-c998380e73b7", "DaaSService", "CassandraHead", 0, capaStr);
		
		
//		SalsaEngineInternal engine = new SalsaEngineInternal();
//		engine.scaleOutNode("HUNG_TEST_SCALING", "CassandraNode");
		
		CloudService service = cenCon.getUpdateCloudServiceRuntime("HUNG_TEST_SCALING");
		ServiceTopology topo = service.getTopologyOfNode("OS_Datanode");
		System.out.println(topo.getId());
		System.out.println(topo.getComponents().size());
		for (ServiceUnit unit : topo.getComponents()) {
			System.out.println(unit.getId());
		}
	}
}


