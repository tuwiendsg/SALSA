
package com.weaveworks.weave;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
    "Version",
    "Router",
    "IPAM",
    "DNS"
})
public class WeaveRouter {

    @JsonProperty("Version")
    private String Version;
    @JsonProperty("Router")
    private com.weaveworks.weave.Router Router;
    @JsonProperty("IPAM")
    private com.weaveworks.weave.IPAM IPAM;
    @JsonProperty("DNS")
    private com.weaveworks.weave.DNS DNS;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * No args constructor for use in serialization
     * 
     */
    public WeaveRouter() {
    }

    /**
     * 
     * @param DNS
     * @param Version
     * @param Router
     * @param IPAM
     */
    public WeaveRouter(String Version, com.weaveworks.weave.Router Router, com.weaveworks.weave.IPAM IPAM, com.weaveworks.weave.DNS DNS) {
        this.Version = Version;
        this.Router = Router;
        this.IPAM = IPAM;
        this.DNS = DNS;
    }

    /**
     * 
     * @return
     *     The Version
     */
    @JsonProperty("Version")
    public String getVersion() {
        return Version;
    }

    /**
     * 
     * @param Version
     *     The Version
     */
    @JsonProperty("Version")
    public void setVersion(String Version) {
        this.Version = Version;
    }

    /**
     * 
     * @return
     *     The Router
     */
    @JsonProperty("Router")
    public com.weaveworks.weave.Router getRouter() {
        return Router;
    }

    /**
     * 
     * @param Router
     *     The Router
     */
    @JsonProperty("Router")
    public void setRouter(com.weaveworks.weave.Router Router) {
        this.Router = Router;
    }

    /**
     * 
     * @return
     *     The IPAM
     */
    @JsonProperty("IPAM")
    public com.weaveworks.weave.IPAM getIPAM() {
        return IPAM;
    }

    /**
     * 
     * @param IPAM
     *     The IPAM
     */
    @JsonProperty("IPAM")
    public void setIPAM(com.weaveworks.weave.IPAM IPAM) {
        this.IPAM = IPAM;
    }

    /**
     * 
     * @return
     *     The DNS
     */
    @JsonProperty("DNS")
    public com.weaveworks.weave.DNS getDNS() {
        return DNS;
    }

    /**
     * 
     * @param DNS
     *     The DNS
     */
    @JsonProperty("DNS")
    public void setDNS(com.weaveworks.weave.DNS DNS) {
        this.DNS = DNS;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
