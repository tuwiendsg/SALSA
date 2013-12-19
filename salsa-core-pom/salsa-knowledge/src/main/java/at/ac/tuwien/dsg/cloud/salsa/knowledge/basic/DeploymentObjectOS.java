package at.ac.tuwien.dsg.cloud.salsa.knowledge.basic;

import java.util.List;

import at.ac.tuwien.dsg.cloud.salsa.knowledge.process.DeploymentObject;

public class DeploymentObjectOS extends DeploymentObject{
	

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
		return "os";
	}

	@Override
	public List<String> getCapability() {		
		addCapability("os");
		return capabilities;
	}

	@Override
	public List<String> getRequirement() {
		addRequirement("vm");
		return requirements;
	}

	@Override
	public String getDefaultDeploymentArtifact() {
		return "";
	}


	



}
