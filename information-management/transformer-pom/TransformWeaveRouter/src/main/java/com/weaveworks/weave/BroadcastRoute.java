
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
    "Source",
    "Via"
})
public class BroadcastRoute {

    @JsonProperty("Source")
    private String Source;
    @JsonProperty("Via")
    private Object Via;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * No args constructor for use in serialization
     * 
     */
    public BroadcastRoute() {
    }

    /**
     * 
     * @param Source
     * @param Via
     */
    public BroadcastRoute(String Source, Object Via) {
        this.Source = Source;
        this.Via = Via;
    }

    /**
     * 
     * @return
     *     The Source
     */
    @JsonProperty("Source")
    public String getSource() {
        return Source;
    }

    /**
     * 
     * @param Source
     *     The Source
     */
    @JsonProperty("Source")
    public void setSource(String Source) {
        this.Source = Source;
    }

    /**
     * 
     * @return
     *     The Via
     */
    @JsonProperty("Via")
    public Object getVia() {
        return Via;
    }

    /**
     * 
     * @param Via
     *     The Via
     */
    @JsonProperty("Via")
    public void setVia(Object Via) {
        this.Via = Via;
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
