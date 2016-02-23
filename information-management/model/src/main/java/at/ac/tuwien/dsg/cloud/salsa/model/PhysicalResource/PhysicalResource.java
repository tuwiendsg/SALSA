package at.ac.tuwien.dsg.cloud.salsa.model.PhysicalResource;

import java.util.Map;

public class PhysicalResource {

    /**
     * The resource can be sensor/actuator/gateway/field device
     *
     */
    private ResourceCategory category;

    private ResourceType type;

    private Map<String, String> attributes;

    /**
     * The actual information, e.g. a sensor. Note: It is not the list, but a single instance of resource
     */
    private String domainInfo;

    /**
     * The name of the model, which is used for the transformation This should be equal to the transformation
     */
    private String domainModelName;

    public PhysicalResource() {
    }

    public ResourceCategory getCategory() {
        return category;
    }

    public void setCategory(ResourceCategory category) {
        this.category = category;
    }

    public ResourceType getType() {
        return type;
    }

    public void setType(ResourceType type) {
        this.type = type;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

    public String getDomainInfo() {
        return domainInfo;
    }

    public void setDomainInfo(String domainInfo) {
        this.domainInfo = domainInfo;
    }

    public String getDomainModelName() {
        return domainModelName;
    }

    public void setDomainModelName(String domainModelName) {
        this.domainModelName = domainModelName;
    }

}
