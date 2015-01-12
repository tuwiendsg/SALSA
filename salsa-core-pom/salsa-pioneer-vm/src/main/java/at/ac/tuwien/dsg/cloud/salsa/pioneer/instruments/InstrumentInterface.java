package at.ac.tuwien.dsg.cloud.salsa.pioneer.instruments;

import generated.oasis.tosca.TNodeTemplate;

public interface InstrumentInterface {

    /**
     * Depending the node type, the environment need to be initiated or checked.
     *
     * @param node The specification of the node according to the TOSCA
     */
    public void initiate(TNodeTemplate node);

    /**
     * Deploy the artifact
     *
     * @param uri The conventional artifact name of tool
     * @param id The node ID passed from the higher stack
     * @return An monitorable object, such as java Process or an ID
     */
    public Object deployArtifact(String uri, String id);

    /**
     * Define how to get status of a running instance. E.g, with system service-based, state can be get via "system serviceName status"
     *
     * @param nodeId
     * @param instanceId
     * @return
     */
    public String getStatus(String nodeId, String instanceId);

    /**
     * Sometimes an artifact type can be configure by application-specific functions that provide more default actions than deploy/undeploy and need to be
     * implemented. The adapter which implement this will provide more actions at runtime.
     *
     * @param action The description of the action
     * @param nodeId The node ID
     * @param instanceId The instance ID
     * @return
     */
    public String executeArtifactAction(String action, String nodeId, String instanceId);

}
