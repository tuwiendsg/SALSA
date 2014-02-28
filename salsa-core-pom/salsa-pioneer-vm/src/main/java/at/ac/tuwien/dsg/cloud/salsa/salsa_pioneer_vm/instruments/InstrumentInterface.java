package at.ac.tuwien.dsg.cloud.salsa.salsa_pioneer_vm.instruments;

import generated.oasis.tosca.TNodeTemplate;


public interface InstrumentInterface {
	
	public void initiate(TNodeTemplate node);
	public String deployArtifact(String uri);
	public String getStatus(String nodeId, String instanceId);
	public String executeArtifactAction(String action, String nodeId, String instanceId);
	
}
