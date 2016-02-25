
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
    "Dest",
    "Via"
})
public class UnicastRoute {

    @JsonProperty("Dest")
    private String Dest;
    @JsonProperty("Via")
    private String Via;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * No args constructor for use in serialization
     * 
     */
    public UnicastRoute() {
    }

    /**
     * 
     * @param Via
     * @param Dest
     */
    public UnicastRoute(String Dest, String Via) {
        this.Dest = Dest;
        this.Via = Via;
    }

    /**
     * 
     * @return
     *     The Dest
     */
    @JsonProperty("Dest")
    public String getDest() {
        return Dest;
    }

    /**
     * 
     * @param Dest
     *     The Dest
     */
    @JsonProperty("Dest")
    public void setDest(String Dest) {
        this.Dest = Dest;
    }

    /**
     * 
     * @return
     *     The Via
     */
    @JsonProperty("Via")
    public String getVia() {
        return Via;
    }

    /**
     * 
     * @param Via
     *     The Via
     */
    @JsonProperty("Via")
    public void setVia(String Via) {
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
