package at.ac.tuwien.dsg.cloud.salsa.model.VirtualComputingResource.Capability;

import at.ac.tuwien.dsg.cloud.salsa.model.VirtualComputingResource.Capability.Concrete.CloudConnectivity;
import at.ac.tuwien.dsg.cloud.salsa.model.VirtualComputingResource.Capability.Concrete.ControlPoint;
import at.ac.tuwien.dsg.cloud.salsa.model.VirtualComputingResource.Capability.Concrete.DataPoint;
import at.ac.tuwien.dsg.cloud.salsa.model.VirtualComputingResource.Capability.Concrete.ExecutionEnvironment;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * The class represents for control point management
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = DataPoint.class, name = "DataPoint"),
    @JsonSubTypes.Type(value = ControlPoint.class, name = "ControlPoint"),
    @JsonSubTypes.Type(value = CloudConnectivity.class, name = "CloudConectivity"),
    @JsonSubTypes.Type(value = ExecutionEnvironment.class, name = "ExecutionEnvironment")
})
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
     * Constructor, get/set
     */
    public Capability() {
    }

    public Capability(String name, CapabilityType type, String description) {
        this.name = name;
        this.type = type;
        this.description = description;
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
