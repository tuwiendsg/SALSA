package at.ac.tuwien.dsg.cloud.salsa.salsa_pioneer_vm;

import at.ac.tuwien.dsg.cloud.salsa.common.processing.SalsaCenterConnector;
import at.ac.tuwien.dsg.cloud.salsa.salsa_pioneer_vm.utils.PioneerLogger;
import at.ac.tuwien.dsg.cloud.salsa.salsa_pioneer_vm.utils.SalsaPioneerConfiguration;

public class TestPioneer {

	public static void main(String[] args) {
		SalsaCenterConnector centerCon;
		centerCon = new SalsaCenterConnector(
				SalsaPioneerConfiguration.getSalsaCenterEndpoint(), "123",
				SalsaPioneerConfiguration.getWorkingDir(), PioneerLogger.logger);
		System.out.println(SalsaPioneerConfiguration.getSalsaCenterEndpoint());

	}

}
