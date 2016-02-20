package at.ac.tuwien.dsg.cloud.salsa.model.VirtualComputingResource.Capability;

public class CapabilityInvocation {

    /**
     * Name of the service provide this capability, e.g. SALSA, GovOps, or local
     */
    String serviceName;

    /**
     * How to call the capability
     */
    InvokeProtocol invokeProtocol;

    /**
     * The absolute path to the service, e.g. http://example.com/rest/start/{id} Note: The parameters are put in brackets
     */
    String reference;

    /**
     * The data using in POST and PUT
     */
    String postData;

    public static enum InvokeProtocol {
        GET, POST, DELETE, PUT
    }
}
