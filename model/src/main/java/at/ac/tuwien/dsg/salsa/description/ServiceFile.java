/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.salsa.description;

import at.ac.tuwien.dsg.salsa.model.CloudService;
import at.ac.tuwien.dsg.salsa.model.ServiceTopology;
import at.ac.tuwien.dsg.salsa.model.ServiceUnit;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author hungld
 */
public class ServiceFile {

    String name;

    Map<String, ServiceUnit> service = new HashMap<>();

    public ServiceFile() {
    }

    public ServiceFile(String name) {
        this.name = name;
    }

    public ServiceFile(String name, ServiceUnit... units) {
        this.name = name;
        for (ServiceUnit unit : units) {
            service.put(unit.getName(), unit);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public CloudService toCloudService(){
        CloudService cloudService = new CloudService();
        cloudService.setName(this.name);
        Map<String, ServiceTopology> topos = new HashMap<>();                
        for (Map.Entry<String, ServiceUnit> entry: this.service.entrySet()){
            entry.getValue().setName(entry.getKey());
            String topoName = entry.getValue().getTopologyUuid();
            if (topoName == null) {
                topoName = "DefaultTopo";
            }
            ServiceTopology topo = topos.get(topoName);
            if (topo == null){
                topo = new ServiceTopology();
                topo.setName(topoName);
                topos.put(topoName, topo);
            }
            topo.hasUnit(entry.getValue());
            cloudService.hasTopology(topo);
        }        
        return cloudService;        
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

    public static ServiceFile fromYaml(String yaml) {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            return mapper.readValue(yaml, ServiceFile.class);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public String toJson() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        mapper.setSerializationInclusion(Include.NON_NULL);
        try {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(this);
        } catch (JsonProcessingException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static ServiceFile fromJson(String json) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        try {
            return mapper.readValue(json, ServiceFile.class);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public Map<String, ServiceUnit> getService() {
        return service;
    }

    public void setService(Map<String, ServiceUnit> service) {
        this.service = service;
    }

}
