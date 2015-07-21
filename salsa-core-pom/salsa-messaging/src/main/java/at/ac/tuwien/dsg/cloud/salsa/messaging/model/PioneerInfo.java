/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.salsa.messaging.model;

import java.io.IOException;
import org.codehaus.jackson.map.ObjectMapper;

/**
 *
 * @author hungld
 */
public class PioneerInfo {

    String userName;

    String id;
    String ip;
    String service;
    String topology;
    String unit;
    int instance;

    public PioneerInfo() {
    }

    public PioneerInfo(String userName, String id, String ip, String service, String topology, String unit, int instance) {
        this.userName = userName;
        this.id = id;
        this.ip = ip;
        this.service = service;
        this.topology = topology;
        this.unit = unit;
        this.instance = instance;
    }

    public String getUserName() {
        return userName;
    }

    public String getIp() {
        return ip;
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

    public String getId() {
        return id;
    }

    public String toJson() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(this);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static PioneerInfo fromJson(String s) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(s, PioneerInfo.class);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

}
