
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
    "Mac",
    "Name",
    "NickName",
    "LastSeen"
})
public class MAC {

    @JsonProperty("Mac")
    private String Mac;
    @JsonProperty("Name")
    private String Name;
    @JsonProperty("NickName")
    private String NickName;
    @JsonProperty("LastSeen")
    private String LastSeen;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * No args constructor for use in serialization
     * 
     */
    public MAC() {
    }

    /**
     * 
     * @param Name
     * @param LastSeen
     * @param Mac
     * @param NickName
     */
    public MAC(String Mac, String Name, String NickName, String LastSeen) {
        this.Mac = Mac;
        this.Name = Name;
        this.NickName = NickName;
        this.LastSeen = LastSeen;
    }

    /**
     * 
     * @return
     *     The Mac
     */
    @JsonProperty("Mac")
    public String getMac() {
        return Mac;
    }

    /**
     * 
     * @param Mac
     *     The Mac
     */
    @JsonProperty("Mac")
    public void setMac(String Mac) {
        this.Mac = Mac;
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
     *     The LastSeen
     */
    @JsonProperty("LastSeen")
    public String getLastSeen() {
        return LastSeen;
    }

    /**
     * 
     * @param LastSeen
     *     The LastSeen
     */
    @JsonProperty("LastSeen")
    public void setLastSeen(String LastSeen) {
        this.LastSeen = LastSeen;
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
