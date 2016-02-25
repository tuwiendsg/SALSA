
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
    "Hostname",
    "Origin",
    "ContainerID",
    "Address",
    "Version",
    "Tombstone"
})
public class Entry_DNS {

    @JsonProperty("Hostname")
    private String Hostname;
    @JsonProperty("Origin")
    private String Origin;
    @JsonProperty("ContainerID")
    private String ContainerID;
    @JsonProperty("Address")
    private String Address;
    @JsonProperty("Version")
    private int Version;
    @JsonProperty("Tombstone")
    private int Tombstone;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * No args constructor for use in serialization
     * 
     */
    public Entry_DNS() {
    }

    /**
     * 
     * @param Tombstone
     * @param Address
     * @param Origin
     * @param Version
     * @param ContainerID
     * @param Hostname
     */
    public Entry_DNS(String Hostname, String Origin, String ContainerID, String Address, int Version, int Tombstone) {
        this.Hostname = Hostname;
        this.Origin = Origin;
        this.ContainerID = ContainerID;
        this.Address = Address;
        this.Version = Version;
        this.Tombstone = Tombstone;
    }

    /**
     * 
     * @return
     *     The Hostname
     */
    @JsonProperty("Hostname")
    public String getHostname() {
        return Hostname;
    }

    /**
     * 
     * @param Hostname
     *     The Hostname
     */
    @JsonProperty("Hostname")
    public void setHostname(String Hostname) {
        this.Hostname = Hostname;
    }

    /**
     * 
     * @return
     *     The Origin
     */
    @JsonProperty("Origin")
    public String getOrigin() {
        return Origin;
    }

    /**
     * 
     * @param Origin
     *     The Origin
     */
    @JsonProperty("Origin")
    public void setOrigin(String Origin) {
        this.Origin = Origin;
    }

    /**
     * 
     * @return
     *     The ContainerID
     */
    @JsonProperty("ContainerID")
    public String getContainerID() {
        return ContainerID;
    }

    /**
     * 
     * @param ContainerID
     *     The ContainerID
     */
    @JsonProperty("ContainerID")
    public void setContainerID(String ContainerID) {
        this.ContainerID = ContainerID;
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
     *     The Version
     */
    @JsonProperty("Version")
    public int getVersion() {
        return Version;
    }

    /**
     * 
     * @param Version
     *     The Version
     */
    @JsonProperty("Version")
    public void setVersion(int Version) {
        this.Version = Version;
    }

    /**
     * 
     * @return
     *     The Tombstone
     */
    @JsonProperty("Tombstone")
    public int getTombstone() {
        return Tombstone;
    }

    /**
     * 
     * @param Tombstone
     *     The Tombstone
     */
    @JsonProperty("Tombstone")
    public void setTombstone(int Tombstone) {
        this.Tombstone = Tombstone;
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
