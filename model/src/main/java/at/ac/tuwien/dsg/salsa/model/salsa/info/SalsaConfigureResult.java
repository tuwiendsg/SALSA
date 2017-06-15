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
package at.ac.tuwien.dsg.salsa.model.salsa.info;

import at.ac.tuwien.dsg.salsa.model.enums.ConfigurationState;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Duc-Hung Le
 */
public class SalsaConfigureResult {

    String actionID;
    ConfigurationState state;
    int returnCode; // if available
    String domainID;
    String extra; // extra message in plain text to show to user

    // effects is a set of key/value variables that are created after the configuration
    // effects can be used to store custom information, e.g. assigned IP, etc.
    Map<String, String> effects;

    public SalsaConfigureResult() {
    }

    public SalsaConfigureResult(String actionID, ConfigurationState state, int returnCode, String extra) {
        this.actionID = actionID;
        this.state = state;
        this.returnCode = returnCode;
        this.extra = extra;
    }

    public String getActionID() {
        return actionID;
    }

    public String getExtra() {
        return extra;
    }

    public SalsaConfigureResult hasExtra(String extra) {
        this.extra = extra;
        return this;
    }

    public SalsaConfigureResult hasDomainID(String domainID) {
        this.domainID = domainID;
        return this;
    }

    public ConfigurationState getState() {
        return state;
    }

    public int getReturnCode() {
        return returnCode;
    }

    public String getDomainID() {
        return domainID;
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

    public static SalsaConfigureResult fromJson(String s) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(s, SalsaConfigureResult.class);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public Map<String, String> getEffects() {
        return effects;
    }

    public SalsaConfigureResult hasEffect(String key, String value) {
        if (this.effects == null) {
            this.effects = new HashMap<>();
        }
        this.effects.put(key, value);
        return this;
    }

}
