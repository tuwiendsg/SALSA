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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author hungld
 */
public class CapabilitiesWrapper {

    Map<String, Capability> capabilities = new HashMap<>();

    public CapabilitiesWrapper() {
    }

    public CapabilitiesWrapper(Map<String, Capability> capas) {
        if (capas != null) {
            this.capabilities = capas;
        }
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

    public static CapabilitiesWrapper fromJson(String json) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        try {
            return mapper.readValue(json, CapabilitiesWrapper.class);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public Map<String, Capability> getCapabilities() {
        return capabilities;
    }

    public void setCapabilities(Map<String, Capability> capabilities) {
        this.capabilities = capabilities;
    }

}
