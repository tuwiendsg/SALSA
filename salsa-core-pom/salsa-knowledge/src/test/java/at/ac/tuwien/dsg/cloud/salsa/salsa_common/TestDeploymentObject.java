package at.ac.tuwien.dsg.cloud.salsa.salsa_common;

import at.ac.tuwien.dsg.cloud.salsa.knowledge.basic.DeploymentObjectOS;
import at.ac.tuwien.dsg.cloud.salsa.knowledge.basic.DeploymentObjectTomcat;

public class TestDeploymentObject {

	public static void main(String[] args) throws Exception{
		DeploymentObjectTomcat war = new DeploymentObjectTomcat();
		DeploymentObjectOS os = new DeploymentObjectOS();
		
		System.out.println(os.exportToXML());

	}

}
