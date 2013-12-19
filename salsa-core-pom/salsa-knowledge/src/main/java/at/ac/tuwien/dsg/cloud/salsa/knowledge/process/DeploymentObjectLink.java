package at.ac.tuwien.dsg.cloud.salsa.knowledge.process;


public class DeploymentObjectLink {
	String name;
	DeploymentObject source;
	DeploymentObject target;
	
	public static DeploymentObjectLink getNewInstance(){
		return new DeploymentObjectLink();
	}
	
	// map between capability properties and requirement properties
	public void mapping(){
		
	}
	
	
}
