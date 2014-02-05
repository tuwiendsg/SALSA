package at.ac.tuwien.dsg.cloud.salsa.knowledge.basic.deplicated;

import java.util.List;

import at.ac.tuwien.dsg.cloud.salsa.knowledge.deplicated.DeploymentObject_old;

public class DeploymentObjectOS extends DeploymentObject_old{
	

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
