/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.salsa.model.salsa.info;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author hungld
 */
public class INFOMessage {

    public static enum SERVICE_LEVEL {
        CLOUDSERVICE, TOPOLOGY, UNIT, INSTANCE, OTHER
    }

    public static enum ACTION_TYPE {
        DEPLOY, RECONFIGURE, REMOVE, SALSA_ACTION
    }

    public static enum ACTION_STATUS {
        STARTED, PROCESSING, DONE, ERROR
    }

    ACTION_TYPE action;
    ACTION_STATUS status;
    String id;
    SERVICE_LEVEL level;
    long timestamp;
    String producer;
    String extra;

    public INFOMessage() {
    }

    public INFOMessage(ACTION_TYPE action, ACTION_STATUS status, String id, SERVICE_LEVEL level, long timestamp, String producer, String extra) {
        this.action = action;
        this.status = status;
        this.id = id;
        this.level = level;
        this.timestamp = timestamp;
//            this.producer = Thread.currentThread().getStackTrace()[1].getClassName();
        this.producer = producer;
        this.extra = extra;
    }

    public ACTION_STATUS getStatus() {
        return status;
    }

    public String getExtra() {
        return extra;
    }

    public ACTION_TYPE getAction() {
        return action;
    }

    public String getId() {
        return id;
    }

    public SERVICE_LEVEL getLevel() {
        return level;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getProducer() {
        return producer;
    }

    public String toJson() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(this);
        } catch (IOException ex) {
            return "error-message";
        }
    }
    
    public static INFOMessage fromJson(String json){
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(json, INFOMessage.class);
        } catch (IOException ex) {
            return null;
        }
    }

}
