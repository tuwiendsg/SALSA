/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.salsa.informationmanagement.communication.messagePayloads;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author hungld
 */
public class DeliseMeta {

    String uuid;
    String ip;
    String unicastTopic;

    public DeliseMeta() {
    }

    public DeliseMeta(String uuid, String ip, String unicastTopic) {
        this.uuid = uuid;
        this.ip = ip;
        this.unicastTopic = unicastTopic;
    }

    public String getUnicastTopic() {
        return unicastTopic;
    }

    public void setUnicastTopic(String unicastTopic) {
        this.unicastTopic = unicastTopic;
    }

    public String getUuid() {
        return uuid;
    }

    public String getIp() {
        return ip;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String toJson() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static DeliseMeta fromJson(String json) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            return mapper.readValue(json, DeliseMeta.class);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }

    }

}
