package at.ac.tuwien.dsg.cloud.salsa;

import java.io.File;

import at.ac.tuwien.dsg.cloud.salsa.common.processing.SalsaCenterConnector;
import at.ac.tuwien.dsg.cloud.salsa.engine.impl.DeploymentEngineNodeLevel;
import at.ac.tuwien.dsg.cloud.salsa.engine.impl.SalsaToscaDeployer;
import at.ac.tuwien.dsg.cloud.salsa.engine.utils.EngineLogger;

public class TestUndeploy {

	public static void main(String[] args) {
//		File configFile = new File(TestDeployTosca.class.getResource("/cloudUserParameters.ini").getFile());
//		SalsaToscaDeployer d = new SalsaToscaDeployer(configFile);
//		d.cleanAllService("842b4ec8-77a3-4894-a3d5-46ada7f117f9");
		SalsaCenterConnector con = new SalsaCenterConnector("http://128.130.172.215:8080/salsa-engine", "", EngineLogger.logger);
		con.logMessage("This is a log");
				
	}

}
