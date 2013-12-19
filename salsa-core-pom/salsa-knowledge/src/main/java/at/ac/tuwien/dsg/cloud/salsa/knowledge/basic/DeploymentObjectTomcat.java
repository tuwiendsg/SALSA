package at.ac.tuwien.dsg.cloud.salsa.knowledge.basic;

import java.util.ArrayList;
import java.util.List;

import at.ac.tuwien.dsg.cloud.salsa.knowledge.process.DeploymentObject;

public class DeploymentObjectTomcat extends DeploymentObject {

	@Override
	public void deploy(DeploymentObject target) {
		
		
	}

	@Override
	public void validate(DeploymentObject object) {
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
