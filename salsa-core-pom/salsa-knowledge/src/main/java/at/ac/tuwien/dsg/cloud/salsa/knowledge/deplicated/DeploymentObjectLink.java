package at.ac.tuwien.dsg.cloud.salsa.knowledge.deplicated;


public class DeploymentObjectLink {
	String name;
	DeploymentObject_old source;
	DeploymentObject_old target;
	
	public static DeploymentObjectLink getNewInstance(){
		return new DeploymentObjectLink();
	}
	
	// map between capability properties and requirement properties
	public void mapping(){
		
	}
	
	
}
