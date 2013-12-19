package at.ac.tuwien.dsg.cloud.salsa.knowledge.basic;

import java.util.List;

import at.ac.tuwien.dsg.cloud.salsa.knowledge.process.DeploymentObject;

public class DeploymentObjectJar extends DeploymentObject {

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
		return "jar";
	}


	@Override
	public List<String> getCapability() {
		return capabilities;
	}

	@Override
	public List<String> getRequirement() {		
		addRequirement("jvm");
		return requirements;
	}

	@Override
	public String getDefaultDeploymentArtifact() {
		return "";
	}
	

}
