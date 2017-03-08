/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.salsa.model.salsa.info;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.HashMap;

/**
 *
 * @author hungld
 */
public class SalsaMsgUpdateMetadata {

    String user;
    String service;
    String topology;
    String unit;
    int instance;
    HashMap<String, String> actions;

    public SalsaMsgUpdateMetadata() {
    }

    public SalsaMsgUpdateMetadata(String user, String service, String topology, String unit, int instance, HashMap<String, String> actions) {
        this.user = user;
        this.service = service;
        this.topology = topology;
        this.unit = unit;
        this.instance = instance;
        this.actions = actions;
    }

    public SalsaMsgUpdateMetadata(SalsaConfigureTask confInfo, HashMap<String, String> actions) {
        this.user = confInfo.getUser();
        this.service = confInfo.getService();
        this.topology = confInfo.getTopology();
        this.unit = confInfo.getUnit();
        this.instance = confInfo.getInstance();
        this.actions = actions;
    }

    public String getUser() {
        return user;
    }

    public String getService() {
        return service;
    }

    public String getTopology() {
        return topology;
    }

    public String getUnit() {
        return unit;
    }

    public int getInstance() {
        return instance;
    }

    public HashMap<String, String> getActions() {
        return actions;
    }

    public String toJson() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException ex) {
            return "Cannot convert to JSON";
        }
    }

    public static SalsaMsgUpdateMetadata fromJson(String json) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(json, SalsaMsgUpdateMetadata.class);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

}
