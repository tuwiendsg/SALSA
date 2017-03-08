/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.salsa.model.properties;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author hungld
 */
public class Capability {

    public enum ExecutionType {
        SCRIPT, REST;
    }

    Long id;

    String name;
    String command;
    ExecutionType type = ExecutionType.SCRIPT;

    String conditions;
    String effect;

//    Map<String, String> conditions = new HashMap<>();
//    Map<String, String> effects = new HashMap<>();
    public Capability() {
    }

    public Capability(String name, String cmd) {
        this.name = name;
        this.command = cmd;
    }

    // manually marchall/unmarshall conditions
    public Map<String, String> readConditionsAsMap() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            if (this.conditions != null) {
                return mapper.readValue(this.conditions, HashMap.class);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public void writeConditionsFromMap(Map<String, String> conditionMap) {
        Map<String, String> map = readConditionsAsMap();
        if (map == null) {
            map = new HashMap<>();
        }
        if (conditionMap != null && !conditionMap.isEmpty()) {
            map.putAll(conditionMap);

            ObjectMapper mapper = new ObjectMapper();
            try {
                this.conditions = mapper.writeValueAsString(conditionMap);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Add a single property
     *
     * @param key
     * @param value
     * @return
     */
    public Capability hasCondition(String key, String value) {
        Map<String, String> map = readConditionsAsMap();
        if (map == null) {
            map = new HashMap<>();
        }
        map.put(key, value);

        ObjectMapper mapper = new ObjectMapper();
        try {
            this.conditions = mapper.writeValueAsString(map);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return this;
    }

    // manual marchall/unmarshall conditions
    public Map<String, String> readEffectsAsMap() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            if (this.effect != null) {
                return mapper.readValue(this.effect, HashMap.class);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public void writeEffectFromMap(Map<String, String> effectMap) {
        Map<String, String> map = readEffectsAsMap();
        if (map == null) {
            map = new HashMap<>();
        }
        if (effectMap != null && !effectMap.isEmpty()) {
            map.putAll(effectMap);

            ObjectMapper mapper = new ObjectMapper();
            try {
                this.effect = mapper.writeValueAsString(effectMap);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public Capability hasEffect(String key, String value) {
        Map<String, String> map = readEffectsAsMap();
        if (map == null) {
            map = new HashMap<>();
        }
        map.put(key, value);

        ObjectMapper mapper = new ObjectMapper();
        try {
            this.effect = mapper.writeValueAsString(map);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return this;
    }

    // GET/ SET
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public ExecutionType getType() {
        return type;
    }

    public void setType(ExecutionType type) {
        this.type = type;
    }

    public String getConditions() {
        return conditions;
    }

    public void setConditions(String conditions) {
        this.conditions = conditions;
    }

    public String getEffect() {
        return effect;
    }

    public void setEffect(String effect) {
        this.effect = effect;
    }

}
