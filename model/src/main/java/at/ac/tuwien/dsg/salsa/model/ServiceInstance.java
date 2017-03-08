/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.salsa.model;

import at.ac.tuwien.dsg.salsa.model.enums.ConfigurationState;
import at.ac.tuwien.dsg.salsa.model.idmanager.GlobalIdentification;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author hungld
 */
public class ServiceInstance {

    Long id;

    String uuid; // the unique access of the instance
    int index;// the index under service unit
    String serviceUnitUuid; // uuid of the service unit
    GlobalIdentification identification; // composition of identification
    ConfigurationState state;
    int hostedInstanceIndex;

    // custom properties: marshall/unmarshall this field manually. String is better than generic Object.
    // we use String because data persistance layer using JPA or Neo4j cannot write Map
    String context;

    public ServiceInstance() {
    }

    public String getServiceUnitUuid() {
        return serviceUnitUuid;
    }

    public void setServiceUnitUuid(String serviceUnitUuid) {
        this.serviceUnitUuid = serviceUnitUuid;
    }

    public GlobalIdentification getIdentification() {
        return identification;
    }

    public void setIdentification(GlobalIdentification identification) {
        this.identification = identification;
    }

    public ConfigurationState getState() {
        return state;
    }

    public void setState(ConfigurationState state) {
        this.state = state;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getHostedInstanceIndex() {
        return hostedInstanceIndex;
    }

    public void setHostedInstanceIndex(int hostedInstanceIndex) {
        this.hostedInstanceIndex = hostedInstanceIndex;
    }

    /**
     * Get properties as String. Use read/write property instead.
     *
     * @return
     */
    public String getContext() {
        return context;
    }

    /**
     * Set properties as String. Use read/write property instead.
     *
     * @param context
     */
    public void setContext(String context) {
        this.context = context;
    }

    /**
     * Get context
     *
     * @return a Map of key/value of context
     */
    public Map<String, String> readContextAsMap() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            if (this.context != null) {
                return mapper.readValue(this.context, HashMap.class);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * Add a set of context
     *
     * @param context
     */
    public void writeContextFromMap(Map<String, String> context) {
        Map<String, String> map = readContextAsMap();
        if (map == null) {
            map = new HashMap<>();
        }
        if (context != null && !context.isEmpty()) {
            map.putAll(context);
            ObjectMapper mapper = new ObjectMapper();
            try {
                this.context = mapper.writeValueAsString(context);
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
     */
    public void writeContext(String key, String value) {
        Map<String, String> map = readContextAsMap();
        if (map == null) {
            map = new HashMap<>();
        }
        map.put(key, value);

        ObjectMapper mapper = new ObjectMapper();
        try {
            this.context = mapper.writeValueAsString(context);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
