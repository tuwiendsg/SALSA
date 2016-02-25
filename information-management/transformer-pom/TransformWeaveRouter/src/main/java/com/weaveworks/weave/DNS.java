
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
    "Domain",
    "Upstream",
    "Address",
    "TTL",
    "Entries"
})
public class DNS {

    @JsonProperty("Domain")
    private String Domain;
    @JsonProperty("Upstream")
    private List<String> Upstream = new ArrayList<String>();
    @JsonProperty("Address")
    private String Address;
    @JsonProperty("TTL")
    private int TTL;
    @JsonProperty("Entries")
    private List<Entry_DNS> Entries = new ArrayList<Entry_DNS>();
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * No args constructor for use in serialization
     * 
     */
    public DNS() {
    }

    /**
     * 
     * @param Domain
     * @param Address
     * @param Upstream
     * @param TTL
     * @param Entries
     */
    public DNS(String Domain, List<String> Upstream, String Address, int TTL, List<Entry_DNS> Entries) {
        this.Domain = Domain;
        this.Upstream = Upstream;
        this.Address = Address;
        this.TTL = TTL;
        this.Entries = Entries;
    }

    /**
     * 
     * @return
     *     The Domain
     */
    @JsonProperty("Domain")
    public String getDomain() {
        return Domain;
    }

    /**
     * 
     * @param Domain
     *     The Domain
     */
    @JsonProperty("Domain")
    public void setDomain(String Domain) {
        this.Domain = Domain;
    }

    /**
     * 
     * @return
     *     The Upstream
     */
    @JsonProperty("Upstream")
    public List<String> getUpstream() {
        return Upstream;
    }

    /**
     * 
     * @param Upstream
     *     The Upstream
     */
    @JsonProperty("Upstream")
    public void setUpstream(List<String> Upstream) {
        this.Upstream = Upstream;
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
     *     The TTL
     */
    @JsonProperty("TTL")
    public int getTTL() {
        return TTL;
    }

    /**
     * 
     * @param TTL
     *     The TTL
     */
    @JsonProperty("TTL")
    public void setTTL(int TTL) {
        this.TTL = TTL;
    }

    /**
     * 
     * @return
     *     The Entries
     */
    @JsonProperty("Entries")
    public List<Entry_DNS> getEntries() {
        return Entries;
    }

    /**
     * 
     * @param Entries
     *     The Entries
     */
    @JsonProperty("Entries")
    public void setEntries(List<Entry_DNS> Entries) {
        this.Entries = Entries;
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
