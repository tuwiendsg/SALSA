package at.ac.tuwien.dsg.cloud.salsa;

import org.apache.cxf.jaxrs.client.JAXRSClientFactory;

import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.CloudService;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.ServiceInstance;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.ServiceTopology;
import at.ac.tuwien.dsg.cloud.salsa.common.cloudservice.model.ServiceUnit;
import at.ac.tuwien.dsg.cloud.salsa.common.interfaces.SalsaEngineServiceIntenal;
import at.ac.tuwien.dsg.cloud.salsa.common.interfaces.SalsaPioneerInterface;
import at.ac.tuwien.dsg.cloud.salsa.common.processing.SalsaCenterConnector;
import at.ac.tuwien.dsg.cloud.salsa.engine.impl.PioneerConnector;
import at.ac.tuwien.dsg.cloud.salsa.engine.services.SalsaEngineImplAll;
import at.ac.tuwien.dsg.cloud.salsa.engine.utils.EngineLogger;
import at.ac.tuwien.dsg.cloud.salsa.tosca.extension.SalsaCapaReqString;

public class TestServiceConnector {
	public static void main(String[] args) throws Exception {
		
		SalsaCenterConnector cenCon = new SalsaCenterConnector("http://128.130.172.215:8080/salsa-engine", "/tmp", EngineLogger.logger);
		//cenCon.updateNodeIdCounter("test", "DataMarketAgence", "agence", 5);
		CloudService service = cenCon.getUpdateCloudServiceRuntime("testTomcat");
		ServiceUnit su = service.getComponentById("policeApp");
		for (ServiceInstance in : su.getInstancesList()) {
			System.out.println("aaaaaaaaaa");
			System.out.println(in.getInstanceId());
		}
		
	}

}


