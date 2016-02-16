package at.ac.tuwien.dsg.cloud.salsa.model.PhysicalResource;

import java.util.Map;

public class PhysicalResource {

    /**
     * The resource can be sensor/actuator/gateway/field device
     *
     */
    private PhysicalResourceType type;

    private String location;

    private Map<String, String> attributes;
    
    private String domainModel;

    public PhysicalResource() {
    }

    public PhysicalResourceType getType() {
        return type;
    }

    public void setType(PhysicalResourceType type) {
        this.type = type;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

}
