package at.ac.tuwien.dsg.cloud.salsa.knowledge.basic.deplicated;

import java.util.ArrayList;
import java.util.List;

import at.ac.tuwien.dsg.cloud.salsa.knowledge.deplicated.DeploymentObject_old;

public class DeploymentObjectTomcat extends DeploymentObject_old {

	@Override
	public void deploy(DeploymentObject_old target) {
		
		
	}

	@Override
	public void validate(DeploymentObject_old object) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getType() {
		// TODO Auto-generated method stub
		return "tomcat";
	}


	@Override
	public List<String> getCapability() {
		addCapability("tomcat");		
		return capabilities;
	}

	@Override
	public List<String> getRequirement() {
		addRequirement("jvm");		
		return requirements;
	}

	@Override
	public String getDefaultDeploymentArtifact() {
		return "tomcat7";
	}

	
	

	

}
