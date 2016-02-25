
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
    "Paxos",
    "Range",
    "RangeNumIPs",
    "DefaultSubnet",
    "Entries",
    "PendingClaims",
    "PendingAllocates"
})
public class IPAM {

    @JsonProperty("Paxos")
    private Object Paxos;
    @JsonProperty("Range")
    private String Range;
    @JsonProperty("RangeNumIPs")
    private int RangeNumIPs;
    @JsonProperty("DefaultSubnet")
    private String DefaultSubnet;
    @JsonProperty("Entries")
    private List<Entry_IPAM> Entries = new ArrayList<Entry_IPAM>();
    @JsonProperty("PendingClaims")
    private Object PendingClaims;
    @JsonProperty("PendingAllocates")
    private Object PendingAllocates;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * No args constructor for use in serialization
     * 
     */
    public IPAM() {
    }

    /**
     * 
     * @param PendingClaims
     * @param Range
     * @param PendingAllocates
     * @param DefaultSubnet
     * @param RangeNumIPs
     * @param Paxos
     * @param Entries
     */
    public IPAM(Object Paxos, String Range, int RangeNumIPs, String DefaultSubnet, List<Entry_IPAM> Entries, Object PendingClaims, Object PendingAllocates) {
        this.Paxos = Paxos;
        this.Range = Range;
        this.RangeNumIPs = RangeNumIPs;
        this.DefaultSubnet = DefaultSubnet;
        this.Entries = Entries;
        this.PendingClaims = PendingClaims;
        this.PendingAllocates = PendingAllocates;
    }

    /**
     * 
     * @return
     *     The Paxos
     */
    @JsonProperty("Paxos")
    public Object getPaxos() {
        return Paxos;
    }

    /**
     * 
     * @param Paxos
     *     The Paxos
     */
    @JsonProperty("Paxos")
    public void setPaxos(Object Paxos) {
        this.Paxos = Paxos;
    }

    /**
     * 
     * @return
     *     The Range
     */
    @JsonProperty("Range")
    public String getRange() {
        return Range;
    }

    /**
     * 
     * @param Range
     *     The Range
     */
    @JsonProperty("Range")
    public void setRange(String Range) {
        this.Range = Range;
    }

    /**
     * 
     * @return
     *     The RangeNumIPs
     */
    @JsonProperty("RangeNumIPs")
    public int getRangeNumIPs() {
        return RangeNumIPs;
    }

    /**
     * 
     * @param RangeNumIPs
     *     The RangeNumIPs
     */
    @JsonProperty("RangeNumIPs")
    public void setRangeNumIPs(int RangeNumIPs) {
        this.RangeNumIPs = RangeNumIPs;
    }

    /**
     * 
     * @return
     *     The DefaultSubnet
     */
    @JsonProperty("DefaultSubnet")
    public String getDefaultSubnet() {
        return DefaultSubnet;
    }

    /**
     * 
     * @param DefaultSubnet
     *     The DefaultSubnet
     */
    @JsonProperty("DefaultSubnet")
    public void setDefaultSubnet(String DefaultSubnet) {
        this.DefaultSubnet = DefaultSubnet;
    }

    /**
     * 
     * @return
     *     The Entries
     */
    @JsonProperty("Entries")
    public List<Entry_IPAM> getEntries() {
        return Entries;
    }

    /**
     * 
     * @param Entries
     *     The Entries
     */
    @JsonProperty("Entries")
    public void setEntries(List<Entry_IPAM> Entries) {
        this.Entries = Entries;
    }

    /**
     * 
     * @return
     *     The PendingClaims
     */
    @JsonProperty("PendingClaims")
    public Object getPendingClaims() {
        return PendingClaims;
    }

    /**
     * 
     * @param PendingClaims
     *     The PendingClaims
     */
    @JsonProperty("PendingClaims")
    public void setPendingClaims(Object PendingClaims) {
        this.PendingClaims = PendingClaims;
    }

    /**
     * 
     * @return
     *     The PendingAllocates
     */
    @JsonProperty("PendingAllocates")
    public Object getPendingAllocates() {
        return PendingAllocates;
    }

    /**
     * 
     * @param PendingAllocates
     *     The PendingAllocates
     */
    @JsonProperty("PendingAllocates")
    public void setPendingAllocates(Object PendingAllocates) {
        this.PendingAllocates = PendingAllocates;
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
