package at.ac.tuwien.dsg.cloud.salsa.model.VirtualComputingResource.Capability;

/**
 * The class represents for control point management
 */
public class Capability {

    /**
     * The name of the capability, e.g. start/stop/reconfigureXYZ
     */
    protected String name;
    
    /**
     * The type of the capability
     */
    protected CapabilityType type;
    
    /**
     * Description
     */
    protected String description;

    /**
     * How to call the capability
     */
//    CapabilityInvocation invocation;
    

    /**
     * Constructor, get/set
     */
    public Capability() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CapabilityType getType() {
        return type;
    }

    public void setType(CapabilityType type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    


}
