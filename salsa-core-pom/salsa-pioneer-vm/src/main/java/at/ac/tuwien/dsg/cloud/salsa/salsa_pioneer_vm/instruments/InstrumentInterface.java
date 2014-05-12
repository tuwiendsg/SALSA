package at.ac.tuwien.dsg.cloud.salsa.salsa_pioneer_vm.instruments;

import generated.oasis.tosca.TNodeTemplate;


public interface InstrumentInterface {
	
	public void initiate(TNodeTemplate node);
	/**
	 * Deploy the artifact
	 * @param uri The conventional artifact name of tool
	 * @param id The node ID passed from the higher level
	 * @return
	 */
	public String deployArtifact(String uri, String id);
	public String getStatus(String nodeId, String instanceId);
	public String executeArtifactAction(String action, String nodeId, String instanceId);
	
}
