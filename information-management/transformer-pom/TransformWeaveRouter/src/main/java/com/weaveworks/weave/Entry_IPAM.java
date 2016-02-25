
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
    "Token",
    "Size",
    "Peer",
    "Nickname",
    "IsKnownPeer",
    "Version"
})
public class Entry_IPAM {

    @JsonProperty("Token")
    private String Token;
    @JsonProperty("Size")
    private int Size;
    @JsonProperty("Peer")
    private String Peer;
    @JsonProperty("Nickname")
    private String Nickname;
    @JsonProperty("IsKnownPeer")
    private boolean IsKnownPeer;
    @JsonProperty("Version")
    private int Version;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * No args constructor for use in serialization
     * 
     */
    public Entry_IPAM() {
    }

    /**
     * 
     * @param Nickname
     * @param IsKnownPeer
     * @param Token
     * @param Version
     * @param Peer
     * @param Size
     */
    public Entry_IPAM(String Token, int Size, String Peer, String Nickname, boolean IsKnownPeer, int Version) {
        this.Token = Token;
        this.Size = Size;
        this.Peer = Peer;
        this.Nickname = Nickname;
        this.IsKnownPeer = IsKnownPeer;
        this.Version = Version;
    }

    /**
     * 
     * @return
     *     The Token
     */
    @JsonProperty("Token")
    public String getToken() {
        return Token;
    }

    /**
     * 
     * @param Token
     *     The Token
     */
    @JsonProperty("Token")
    public void setToken(String Token) {
        this.Token = Token;
    }

    /**
     * 
     * @return
     *     The Size
     */
    @JsonProperty("Size")
    public int getSize() {
        return Size;
    }

    /**
     * 
     * @param Size
     *     The Size
     */
    @JsonProperty("Size")
    public void setSize(int Size) {
        this.Size = Size;
    }

    /**
     * 
     * @return
     *     The Peer
     */
    @JsonProperty("Peer")
    public String getPeer() {
        return Peer;
    }

    /**
     * 
     * @param Peer
     *     The Peer
     */
    @JsonProperty("Peer")
    public void setPeer(String Peer) {
        this.Peer = Peer;
    }

    /**
     * 
     * @return
     *     The Nickname
     */
    @JsonProperty("Nickname")
    public String getNickname() {
        return Nickname;
    }

    /**
     * 
     * @param Nickname
     *     The Nickname
     */
    @JsonProperty("Nickname")
    public void setNickname(String Nickname) {
        this.Nickname = Nickname;
    }

    /**
     * 
     * @return
     *     The IsKnownPeer
     */
    @JsonProperty("IsKnownPeer")
    public boolean isIsKnownPeer() {
        return IsKnownPeer;
    }

    /**
     * 
     * @param IsKnownPeer
     *     The IsKnownPeer
     */
    @JsonProperty("IsKnownPeer")
    public void setIsKnownPeer(boolean IsKnownPeer) {
        this.IsKnownPeer = IsKnownPeer;
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

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
