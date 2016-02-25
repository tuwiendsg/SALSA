
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
    "ID",
    "Name",
    "TypeName"
})
public class Vport {

    @JsonProperty("ID")
    private int ID;
    @JsonProperty("Name")
    private String Name;
    @JsonProperty("TypeName")
    private String TypeName;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * No args constructor for use in serialization
     * 
     */
    public Vport() {
    }

    /**
     * 
     * @param Name
     * @param ID
     * @param TypeName
     */
    public Vport(int ID, String Name, String TypeName) {
        this.ID = ID;
        this.Name = Name;
        this.TypeName = TypeName;
    }

    /**
     * 
     * @return
     *     The ID
     */
    @JsonProperty("ID")
    public int getID() {
        return ID;
    }

    /**
     * 
     * @param ID
     *     The ID
     */
    @JsonProperty("ID")
    public void setID(int ID) {
        this.ID = ID;
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
     *     The TypeName
     */
    @JsonProperty("TypeName")
    public String getTypeName() {
        return TypeName;
    }

    /**
     * 
     * @param TypeName
     *     The TypeName
     */
    @JsonProperty("TypeName")
    public void setTypeName(String TypeName) {
        this.TypeName = TypeName;
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
