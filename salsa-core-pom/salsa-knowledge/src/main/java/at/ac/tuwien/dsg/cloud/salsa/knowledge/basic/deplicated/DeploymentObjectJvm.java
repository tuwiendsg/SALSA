package at.ac.tuwien.dsg.cloud.salsa.knowledge.basic.deplicated;

import java.util.List;

import at.ac.tuwien.dsg.cloud.salsa.knowledge.deplicated.DeploymentObject_old;

public class DeploymentObjectJvm extends DeploymentObject_old {

	@Override
	public void deploy(DeploymentObject_old target) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void validate(DeploymentObject_old object) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getType() {
		// TODO Auto-generated method stub
		return "jvm";
	}


	@Override
	public List<String> getCapability() {
		addCapability("jvm");
		return capabilities;
	}

	@Override
	public List<String> getRequirement() {
		addRequirement("os");		
		return requirements;
	}

	@Override
	public String getDefaultDeploymentArtifact() {
		// TODO Auto-generated method stub
		return "";
	}

}
