
package com.weaveworks.weave;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    "Vports",
    "Flows"
})
public class Fastdp {

    @JsonProperty("Vports")
    private List<Vport> Vports = new ArrayList<Vport>();
    @JsonProperty("Flows")
    private List<Object> Flows = new ArrayList<Object>();
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * No args constructor for use in serialization
     * 
     */
    public Fastdp() {
    }

    /**
     * 
     * @param Vports
     * @param Flows
     */
    public Fastdp(List<Vport> Vports, List<Object> Flows) {
        this.Vports = Vports;
        this.Flows = Flows;
    }

    /**
     * 
     * @return
     *     The Vports
     */
    @JsonProperty("Vports")
    public List<Vport> getVports() {
        return Vports;
    }

    /**
     * 
     * @param Vports
     *     The Vports
     */
    @JsonProperty("Vports")
    public void setVports(List<Vport> Vports) {
        this.Vports = Vports;
    }

    /**
     * 
     * @return
     *     The Flows
     */
    @JsonProperty("Flows")
    public List<Object> getFlows() {
        return Flows;
    }

    /**
     * 
     * @param Flows
     *     The Flows
     */
    @JsonProperty("Flows")
    public void setFlows(List<Object> Flows) {
        this.Flows = Flows;
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
