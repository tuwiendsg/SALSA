
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
    "FlowMisses"
})
public class CaptureStats {

    @JsonProperty("FlowMisses")
    private int FlowMisses;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * No args constructor for use in serialization
     * 
     */
    public CaptureStats() {
    }

    /**
     * 
     * @param FlowMisses
     */
    public CaptureStats(int FlowMisses) {
        this.FlowMisses = FlowMisses;
    }

    /**
     * 
     * @return
     *     The FlowMisses
     */
    @JsonProperty("FlowMisses")
    public int getFlowMisses() {
        return FlowMisses;
    }

    /**
     * 
     * @param FlowMisses
     *     The FlowMisses
     */
    @JsonProperty("FlowMisses")
    public void setFlowMisses(int FlowMisses) {
        this.FlowMisses = FlowMisses;
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
