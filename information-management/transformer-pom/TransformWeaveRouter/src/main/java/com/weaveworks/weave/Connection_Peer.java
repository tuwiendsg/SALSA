
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
    "Address",
    "Outbound",
    "State",
    "Info"
})
public class Connection_Peer {

    @JsonProperty("Address")
    private String Address;
    @JsonProperty("Outbound")
    private boolean Outbound;
    @JsonProperty("State")
    private String State;
    @JsonProperty("Info")
    private String Info;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * No args constructor for use in serialization
     * 
     */
    public Connection_Peer() {
    }

    /**
     * 
     * @param Outbound
     * @param State
     * @param Address
     * @param Info
     */
    public Connection_Peer(String Address, boolean Outbound, String State, String Info) {
        this.Address = Address;
        this.Outbound = Outbound;
        this.State = State;
        this.Info = Info;
    }

    /**
     * 
     * @return
     *     The Address
     */
    @JsonProperty("Address")
    public String getAddress() {
        return Address;
    }

    /**
     * 
     * @param Address
     *     The Address
     */
    @JsonProperty("Address")
    public void setAddress(String Address) {
        this.Address = Address;
    }

    /**
     * 
     * @return
     *     The Outbound
     */
    @JsonProperty("Outbound")
    public boolean isOutbound() {
        return Outbound;
    }

    /**
     * 
     * @param Outbound
     *     The Outbound
     */
    @JsonProperty("Outbound")
    public void setOutbound(boolean Outbound) {
        this.Outbound = Outbound;
    }

    /**
     * 
     * @return
     *     The State
     */
    @JsonProperty("State")
    public String getState() {
        return State;
    }

    /**
     * 
     * @param State
     *     The State
     */
    @JsonProperty("State")
    public void setState(String State) {
        this.State = State;
    }

    /**
     * 
     * @return
     *     The Info
     */
    @JsonProperty("Info")
    public String getInfo() {
        return Info;
    }

    /**
     * 
     * @param Info
     *     The Info
     */
    @JsonProperty("Info")
    public void setInfo(String Info) {
        this.Info = Info;
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
