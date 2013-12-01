package at.ac.tuwien.dsg.cloud.salsa;

import generated.oasis.tosca.TDefinitions;
import at.ac.tuwien.dsg.cloud.salsa.engine.impl.SalsaToscaDeployer;
import at.ac.tuwien.dsg.cloud.salsa.tosca.ToscaXmlProcess;

public class TestDeployTosca {

	public static void main(String[] args) {
		try {
			TDefinitions def = ToscaXmlProcess
					.readToscaFile(TestDeployTosca.class.getResource(
							"/cassandra.tosca.xml").getFile());
			SalsaToscaDeployer.deployNewService(def);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
