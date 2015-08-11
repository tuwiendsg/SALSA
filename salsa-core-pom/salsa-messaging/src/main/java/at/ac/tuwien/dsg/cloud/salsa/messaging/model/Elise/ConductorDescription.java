/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.salsa.messaging.model.Elise;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.codehaus.jackson.map.ObjectMapper;

/**
 *
 * @author Duc-Hung Le
 */
public class ConductorDescription {

    String id;
    String ip;

    List<CollectorDescription> collectors = new ArrayList<>();

    public ConductorDescription() {
    }

    public ConductorDescription(String id, String ip) {
        this.id = id;
        this.ip = ip;
    }

    public ConductorDescription hasCollector(CollectorDescription collector) {
        this.collectors.add(collector);
        return this;
    }

    public String getId() {
        return id;
    }

    public String getIp() {
        return ip;
    }

    public List<CollectorDescription> getCollectors() {
        return collectors;
    }

    public static ConductorDescription fromJson(String json) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, ConductorDescription.class);
    }

    
    public  String toJson() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(this);
        } catch (IOException ex) {
            return "Undefined conductor description";
        }
    }
}
