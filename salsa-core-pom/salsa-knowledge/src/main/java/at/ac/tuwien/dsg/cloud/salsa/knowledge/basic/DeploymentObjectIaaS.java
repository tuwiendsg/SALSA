package at.ac.tuwien.dsg.cloud.salsa.knowledge.basic;

import java.util.List;

import at.ac.tuwien.dsg.cloud.salsa.knowledge.process.DeploymentObject;

public class DeploymentObjectIaaS extends DeploymentObject{

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
		return "vm";
	}


	@Override
	public List<String> getCapability() {
		addCapability("vm");
		return capabilities;
	}

	@Override
	public List<String> getRequirement() {
		addRequirement("salsa-base");
		return requirements;
	}

	@Override
	public String getDefaultDeploymentArtifact() {
		// TODO Auto-generated method stub
		return "";
	}


}
