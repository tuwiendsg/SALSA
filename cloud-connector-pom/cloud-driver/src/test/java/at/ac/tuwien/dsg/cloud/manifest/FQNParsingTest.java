package at.ac.tuwien.dsg.cloud.manifest;

import ch.usi.cloud.controller.common.naming.FQN;

public class FQNParsingTest {

	public static void main(String[] args) {
		FQN replicaFQN = new FQN("org", "cus", "ser", "", "frontend", 0);
		System.out.println("FQNParsingTest.main() " + replicaFQN );
		// ale_customers_ale_services_ale_vees_frontend_replicas_0
		String _serviceFQN = FQN.getServiceFQN(replicaFQN.toString())
				.toString();
		
		System.out.println(" ServiceFQN: " + _serviceFQN);
	}
}
