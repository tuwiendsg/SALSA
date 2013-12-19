package at.ac.tuwien.dsg.cloud.salsa.knowledge.basic;

import java.util.List;

import at.ac.tuwien.dsg.cloud.salsa.knowledge.process.DeploymentObject;

public class DeploymentObjectJvm extends DeploymentObject {

	@Override
	public void deploy(DeploymentObject target) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void validate(DeploymentObject object) {
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
