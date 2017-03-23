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
import java.util.HashMap;
import java.util.Map;
import lombok.Data;

/**
 *
 * @author hungld
 */
@Data
public class Capability {

    Long id;

    // the name of the action
    String name;
    // name of the configuration module, will be map to the class
    String configModule;

    String command;
    // the parameters to enforce
    Map<String, String> parameters = new HashMap<>();

    Map<String, String> conditions = new HashMap<>();
    Map<String, String> effects = new HashMap<>();

    public Capability() {
    }

    public Capability(String name, String configModule) {
        this.name = name;
        this.configModule = configModule;
    }

    public Capability hasParameter(String key, String value) {
        if (this.parameters == null) {
            this.parameters = new HashMap<>();
        }
        this.parameters.put(key, value);
        return this;
    }

    /**
     * Add a single property
     *
     * @param key
     * @param value
     * @return
     */
    public Capability hasCondition(String key, String value) {
        if (this.conditions == null) {
            this.conditions = new HashMap<>();
        }
        this.conditions.put(key, value);

        return this;
    }

    public Capability hasEffect(String key, String value) {
        if (this.effects == null) {
            this.effects = new HashMap<>();
        }
        this.effects.put(key, value);

        return this;
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
