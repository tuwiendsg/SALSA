/*
 * Copyright (c) 2013 Technische Universitat Wien (TUW), Distributed Systems Group. http://dsg.tuwien.ac.at
 *
 * This work was partially supported by the European Commission in terms of the CELAR FP7 project (FP7-ICT-2011-8 #317790), http://www.celarcloud.eu/
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package at.ac.tuwien.dsg.cloud.salsa.messaging.model.commands;

import at.ac.tuwien.dsg.cloud.salsa.messaging.model.SalsaMessage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.codehaus.jackson.map.ObjectMapper;

/**
 *
 * @author Duc-Hung Le
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
