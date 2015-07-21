/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.salsa.messaging.model.commands;

import at.ac.tuwien.dsg.cloud.salsa.messaging.model.SalsaMessage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.codehaus.jackson.map.ObjectMapper;

/**
 *
 * @author hungld
 */
public class SalsaMsgConfigureState {

    String actionID;
    CONFIGURATION_STATE state;
    int returnCode; // if available    
    String info;
    Map<String, String> capabilities;

    public enum CONFIGURATION_STATE {

        SUCCESSFUL,
        ERROR,
        PROCESSING
    }

    public SalsaMsgConfigureState() {
    }

    public SalsaMsgConfigureState(String actionID, CONFIGURATION_STATE state, int returnCode, String info) {
        this.actionID = actionID;
        this.state = state;
        this.returnCode = returnCode;
        this.info = info;
    }

    public String getActionID() {
        return actionID;
    }

    public CONFIGURATION_STATE getState() {
        return state;
    }

    public int getReturnCode() {
        return returnCode;
    }

    public String getInfo() {
        return info;
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

    public static SalsaMsgConfigureState fromJson(String s) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(s, SalsaMsgConfigureState.class);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public Map<String, String> getCapabilities() {
        return capabilities;
    }
    
    public void addCapability(String key, String value){
        if (this.capabilities==null){
            this.capabilities = new HashMap<>();
        }
        this.capabilities.put(key, value);
    }

}
