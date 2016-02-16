package at.ac.tuwien.dsg.cloud.salsa.model.VirtualComputingResource.Capability;

/**
 * The class represents for control point management
 */
public class Capability {

    /**
     * The name of the capability, e.g. start/stop/reconfigureXYZ
     */
    String name;

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

    /**
     * The effect on an entity by the capability
     */
    CapabilityEffect effect;

    public static enum InvokeProtocol {
        GET, POST, DELETE, PUT
    }

    /**
     * Constructor, get/set
     */
    public Capability() {
    }

    public Capability(String name, String serviceName, InvokeProtocol invokeProtocol, String reference, String postData) {
        this.name = name;
        this.serviceName = serviceName;
        this.invokeProtocol = invokeProtocol;
        this.reference = reference;
        this.postData = postData;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public InvokeProtocol getInvokeProtocol() {
        return invokeProtocol;
    }

    public void setInvokeProtocol(InvokeProtocol invokeProtocol) {
        this.invokeProtocol = invokeProtocol;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getPostData() {
        return postData;
    }

    public void setPostData(String postData) {
        this.postData = postData;
    }

    public CapabilityEffect getEffect() {
        return effect;
    }

    public void setEffect(CapabilityEffect effect) {
        this.effect = effect;
    }

}
