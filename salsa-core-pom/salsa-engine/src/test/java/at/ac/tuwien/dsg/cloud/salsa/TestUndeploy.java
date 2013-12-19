package at.ac.tuwien.dsg.cloud.salsa;

import at.ac.tuwien.dsg.cloud.salsa.engine.impl.SalsaToscaDeployer;

public class TestUndeploy {

	public static void main(String[] args) {
		SalsaToscaDeployer.cleanAllService("b1f4cc6c-12b9-4a74-9d41-9f4e05b3721b");		
	}

}
