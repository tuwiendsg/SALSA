
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
    "Name",
    "NickName",
    "Address",
    "Outbound",
    "Established"
})
public class Connection {

    @JsonProperty("Name")
    private String Name;
    @JsonProperty("NickName")
    private String NickName;
    @JsonProperty("Address")
    private String Address;
    @JsonProperty("Outbound")
    private boolean Outbound;
    @JsonProperty("Established")
    private boolean Established;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * No args constructor for use in serialization
     * 
     */
    public Connection() {
    }

    /**
     * 
     * @param Name
     * @param Outbound
     * @param NickName
     * @param Address
     * @param Established
     */
    public Connection(String Name, String NickName, String Address, boolean Outbound, boolean Established) {
        this.Name = Name;
        this.NickName = NickName;
        this.Address = Address;
        this.Outbound = Outbound;
        this.Established = Established;
    }

    /**
     * 
     * @return
     *     The Name
     */
    @JsonProperty("Name")
    public String getName() {
        return Name;
    }

    /**
     * 
     * @param Name
     *     The Name
     */
    @JsonProperty("Name")
    public void setName(String Name) {
        this.Name = Name;
    }

    /**
     * 
     * @return
     *     The NickName
     */
    @JsonProperty("NickName")
    public String getNickName() {
        return NickName;
    }

    /**
     * 
     * @param NickName
     *     The NickName
     */
    @JsonProperty("NickName")
    public void setNickName(String NickName) {
        this.NickName = NickName;
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
     *     The Established
     */
    @JsonProperty("Established")
    public boolean isEstablished() {
        return Established;
    }

    /**
     * 
     * @param Established
     *     The Established
     */
    @JsonProperty("Established")
    public void setEstablished(boolean Established) {
        this.Established = Established;
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
