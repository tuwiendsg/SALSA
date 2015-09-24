/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.elise.collectorinterfaces.models;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.codehaus.jackson.map.ObjectMapper;

/**
 *
 * @author Duc-Hung LE
 */
public class CollectorsForConductor {

    String conductorID;
    List<CollectorDescription> collectors;

    public CollectorsForConductor(String conductorID) {
        this.conductorID = conductorID;
    }

    public CollectorsForConductor(String conductorID, CollectorDescription... collector) {
        this.conductorID = conductorID;
        collectors = new ArrayList<>();
        collectors.addAll(Arrays.asList(collector));
    }

    public CollectorsForConductor hasCollector(CollectorDescription c) {
        if (collectors == null) {
            this.collectors = new ArrayList<>();
        }
        this.collectors.add(c);
        return this;
    }

    public String getConductorID() {
        return conductorID;
    }
    
    public String toJson(){
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(this);
        } catch (IOException ex) {
            System.out.println("Error when parsing the CollectorsForConductor");
            return "";
        }
    }
    
    public CollectorsForConductor fromJson(String json){
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(json, CollectorsForConductor.class);
        } catch (IOException ex) {
            System.out.println("Error when unmarshall the CollectorsForConductor json: " + json);
            return null;
        }
    }

    
}
