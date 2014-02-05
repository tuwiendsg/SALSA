package at.ac.tuwien.dsg.cloud.salsa;

import java.io.File;

import at.ac.tuwien.dsg.cloud.salsa.engine.impl.SalsaToscaDeployer;

public class TestUndeploy {

	public static void main(String[] args) {
		File configFile = new File(TestDeployTosca.class.getResource("/cloudUserParameters.ini").getFile());
		SalsaToscaDeployer d = new SalsaToscaDeployer(configFile);
		d.cleanAllService("842b4ec8-77a3-4894-a3d5-46ada7f117f9");
	}

}
