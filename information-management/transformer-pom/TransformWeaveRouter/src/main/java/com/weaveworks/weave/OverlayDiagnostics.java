
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
    "fastdp",
    "sleeve"
})
public class OverlayDiagnostics {

    @JsonProperty("fastdp")
    private Fastdp fastdp;
    @JsonProperty("sleeve")
    private Object sleeve;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * No args constructor for use in serialization
     * 
     */
    public OverlayDiagnostics() {
    }

    /**
     * 
     * @param sleeve
     * @param fastdp
     */
    public OverlayDiagnostics(Fastdp fastdp, Object sleeve) {
        this.fastdp = fastdp;
        this.sleeve = sleeve;
    }

    /**
     * 
     * @return
     *     The fastdp
     */
    @JsonProperty("fastdp")
    public Fastdp getFastdp() {
        return fastdp;
    }

    /**
     * 
     * @param fastdp
     *     The fastdp
     */
    @JsonProperty("fastdp")
    public void setFastdp(Fastdp fastdp) {
        this.fastdp = fastdp;
    }

    /**
     * 
     * @return
     *     The sleeve
     */
    @JsonProperty("sleeve")
    public Object getSleeve() {
        return sleeve;
    }

    /**
     * 
     * @param sleeve
     *     The sleeve
     */
    @JsonProperty("sleeve")
    public void setSleeve(Object sleeve) {
        this.sleeve = sleeve;
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
