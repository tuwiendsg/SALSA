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

    public enum FetchProtocol {
        PACKED, LOCAL, HTTP, GIT
    }

    String source;
    FetchProtocol fetch = FetchProtocol.PACKED;
    String target;

    public Artifact() {
    }

    public Artifact(String source) {
        this.source = source;
    }

    public Artifact(String source, String target, FetchProtocol fetch) {
        this.source = source;
        this.target = target;
        this.fetch = fetch;
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
