/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.salsa.model.properties;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import lombok.Data;

/**
 *
 * @author hungld
 */
@Data
public class Artifact {

    public enum RepoType {
        HTTP, GIT
    }

    Long id;

    String name;
    String artifactType;
    String reference;
    RepoType repoType = RepoType.HTTP; // default, how to download
    String tags;

    public Artifact() {
    }

    public Artifact(String name, String artifactType, String reference) {
        this.name = name;
        this.artifactType = artifactType;
        this.reference = reference;
    }

    public String toJson() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        try {
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static Capability fromJson(String json) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        try {
            return mapper.readValue(json, Capability.class);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

}
