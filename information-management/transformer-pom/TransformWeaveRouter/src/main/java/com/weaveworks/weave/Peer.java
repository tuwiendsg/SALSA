
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
    "Name",
    "NickName",
    "UID",
    "ShortID",
    "Version",
    "Connections"
})
public class Peer {

    @JsonProperty("Name")
    private String Name;
    @JsonProperty("NickName")
    private String NickName;
    @JsonProperty("UID")
    private String UID;
    @JsonProperty("ShortID")
    private int ShortID;
    @JsonProperty("Version")
    private int Version;
    @JsonProperty("Connections")
    private List<Connection> Connections = new ArrayList<Connection>();
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * No args constructor for use in serialization
     * 
     */
    public Peer() {
    }

    /**
     * 
     * @param Name
     * @param ShortID
     * @param UID
     * @param NickName
     * @param Version
     * @param Connections
     */
    public Peer(String Name, String NickName, String UID, int ShortID, int Version, List<Connection> Connections) {
        this.Name = Name;
        this.NickName = NickName;
        this.UID = UID;
        this.ShortID = ShortID;
        this.Version = Version;
        this.Connections = Connections;
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
     *     The UID
     */
    @JsonProperty("UID")
    public String getUID() {
        return UID;
    }

    /**
     * 
     * @param UID
     *     The UID
     */
    @JsonProperty("UID")
    public void setUID(String UID) {
        this.UID = UID;
    }

    /**
     * 
     * @return
     *     The ShortID
     */
    @JsonProperty("ShortID")
    public int getShortID() {
        return ShortID;
    }

    /**
     * 
     * @param ShortID
     *     The ShortID
     */
    @JsonProperty("ShortID")
    public void setShortID(int ShortID) {
        this.ShortID = ShortID;
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
     *     The Connections
     */
    @JsonProperty("Connections")
    public List<Connection> getConnections() {
        return Connections;
    }

    /**
     * 
     * @param Connections
     *     The Connections
     */
    @JsonProperty("Connections")
    public void setConnections(List<Connection> Connections) {
        this.Connections = Connections;
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
