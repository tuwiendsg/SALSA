package at.ac.tuwien.dsg.cloud.salsa.knowledge.basic.deplicated;

import java.util.ArrayList;
import java.util.List;

import at.ac.tuwien.dsg.cloud.salsa.knowledge.deplicated.DeploymentObject_old;

public class DeploymentObjectSh extends DeploymentObject_old{

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
		return "sh";
	}


	@Override
	public List<String> getCapability() {		
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
