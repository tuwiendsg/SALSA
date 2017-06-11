/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.salsa.description;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Define how to deploy the service
 *
 * @author hungld
 */
public class DeploymentFile {

    String serviceName;
    String name;

    // map from unit name --> list of pioneer
    Map<String, List<String>> placements = new HashMap<>();

    public DeploymentFile(String serviceName, String name) {
        this.serviceName = serviceName;
        this.name = name;
    }

    public DeploymentFile() {
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DeploymentFile hasPlacement(String unitName, String... pioneerLabel) {
        this.placements.put(unitName, Arrays.asList(pioneerLabel));
        return this;
    }

    public Map<String, List<String>> getPlacements() {
        return placements;
    }

    public void setPlacements(Map<String, List<String>> placements) {
        this.placements = placements;
    }

    public String toYaml() {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        try {
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static DeploymentFile fromYaml(String yaml) {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
//        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            return mapper.readValue(yaml, DeploymentFile.class);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public String toJson() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        try {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(this);
        } catch (JsonProcessingException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static DeploymentFile fromJson(String json) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        try {
            return mapper.readValue(json, DeploymentFile.class);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

}
