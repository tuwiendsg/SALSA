/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.salsa.model;

import at.ac.tuwien.dsg.salsa.model.enums.ConfigurationState;
import at.ac.tuwien.dsg.salsa.model.properties.Artifact;
import at.ac.tuwien.dsg.salsa.model.properties.Capability;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author hungld
 */
public class ServiceUnit {

    Long id;

    String uuid;
    String name;
    String type;
    Set<ServiceInstance> instances;
    int idCounter = 0;
    int min = 1;
    int max = 1;
    String reference;
    Set<Artifact> artifacts;
    Set<Capability> capabilities;
    String hostedUnitName;
    List<String> connecttoUnitName = new ArrayList<>();

    // management info
    String topologyUuid;
    ConfigurationState state;

    // custom properties: marshall/unmarshall this field manually. String is better than generic Object.
    // we use String because data persistance layer using JPA or Neo4j cannot write Map
    String properties;
//    Map<String, String> properties = new HashMap<>();

    // capability var to store properties that it will share
//    List<String> capabilityVars = new ArrayList<>();
    // this field is for determining deployment artifact to be used. Should be remove later
    String mainArtifactType;

    public ServiceUnit() {
    }

    public ServiceUnit(String uuid, String type) {
        this.uuid = uuid;
        this.type = type;
    }

    // GET SET
    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Set<ServiceInstance> getInstances() {
        return instances;
    }

    public void setInstances(Set<ServiceInstance> instances) {
        this.instances = instances;
    }

    public int getIdCounter() {
        return idCounter;
    }

    public void setIdCounter(int idCounter) {
        this.idCounter = idCounter;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public Set<Artifact> getArtifacts() {
        return artifacts;
    }

    public void setArtifacts(Set<Artifact> artifacts) {
        this.artifacts = artifacts;
    }

    public Set<Capability> getCapabilities() {
        return capabilities;
    }

    public void setCapabilities(Set<Capability> capabilities) {
        this.capabilities = capabilities;
    }

    public String getTopologyUuid() {
        return topologyUuid;
    }

    public void setTopologyUuid(String topologyUuid) {
        this.topologyUuid = topologyUuid;
    }

    public ConfigurationState getState() {
        return state;
    }

    public void setState(ConfigurationState state) {
        this.state = state;
    }

    public String getHostedUnitName() {
        return hostedUnitName;
    }

    public void setHostedUnitName(String hostedUnitName) {
        this.hostedUnitName = hostedUnitName;
    }

    public List<String> getConnecttoUnitName() {
        return connecttoUnitName;
    }

    public void setConnecttoUnitName(List<String> connecttoUnitName) {
        this.connecttoUnitName = connecttoUnitName;
    }

    public String getMainArtifactType() {
        return mainArtifactType;
    }

    public void setMainArtifactType(String mainArtifactType) {
        this.mainArtifactType = mainArtifactType;
    }

    /**
     * Get properties as String. Use read/write property instead.
     *
     * @return
     */
    public String getProperties() {
        return properties;
    }

    /**
     * Set properties as String. Use read/write property instead.
     *
     * @param properties
     */
    public void setProperties(String properties) {
        this.properties = properties;
    }

    /**
     * Get properties
     *
     * @return a Map of key/value of properties
     */
    public Map<String, String> readPropertiesAsMap() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            if (this.properties != null) {
                return mapper.readValue(this.properties, HashMap.class);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * Add a set of properties
     *
     * @param properties
     */
    public void writePropertiesFromMap(Map<String, String> properties) {
        Map<String, String> map = readPropertiesAsMap();
        if (map == null) {
            map = new HashMap<>();
        }
        if (properties != null && !properties.isEmpty()) {
            map.putAll(properties);

            ObjectMapper mapper = new ObjectMapper();
            try {
                this.properties = mapper.writeValueAsString(properties);
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
    public void writeProperty(String key, String value) {
        Map<String, String> map = readPropertiesAsMap();
        if (map == null) {
            map = new HashMap<>();
        }
        map.put(key, value);

        ObjectMapper mapper = new ObjectMapper();
        try {
            this.properties = mapper.writeValueAsString(properties);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @JsonIgnore
    public ServiceInstance getInstanceByIndex(int index) {
        for (ServiceInstance instance : instances) {
            if (instance.getIndex() == index) {
                return instance;
            }
        }
        return null;
    }

    @JsonIgnore
    // what instances of this service unit is hosting on a nother instance?
    public List<ServiceInstance> getInstanceHostOn(String hosterUnitUuid, int hosterInstanceIndex) {
        List<ServiceInstance> newLst = new ArrayList<>();
        if (getInstances() != null) {
            for (ServiceInstance instance : getInstances()) {
                if ((instance.getHostedInstanceIndex() == hosterInstanceIndex) && this.uuid.equals(hosterUnitUuid)) {
                    newLst.add(instance);
                }
            }
        }
        return newLst;
    }

    public int nextIdCounter() {
        this.idCounter += 1;
        return this.idCounter;
    }

    public ServiceUnit hasCapability(Capability capability) {
        if (this.capabilities == null) {
            this.capabilities = new HashSet<>();
        }
        this.capabilities.add(capability);
        return this;
    }

    public ServiceUnit hasArtifact(Artifact art) {
        if (this.artifacts == null) {
            this.artifacts = new HashSet<>();
        }
        this.artifacts.add(art);
        return this;
    }

    public ServiceUnit hasInstance(ServiceInstance instance) {
        if (this.instances == null) {
            this.instances = new HashSet<>();
        }
        instance.setServiceUnitUuid(this.uuid);
        this.instances.add(instance);
        return this;
    }

    @JsonIgnore
    public Capability getCapabilityByName(String capaName) {
        if (capabilities != null) {
            for (Capability po : capabilities) {
                if (po.getName().equals(capaName)) {
                    return po;
                }
            }
        }
        return null;
    }

    public void readCapabilityFromJson(String json) {
        ObjectMapper mapper = new ObjectMapper();
        TypeReference<Set<Capability>> mapType = new TypeReference<Set<Capability>>() {
        };
        try {
            Set<Capability> capaList = mapper.readValue(json, mapType);
            if (this.capabilities == null) {
                capaList = new HashSet<>();
            }
            this.capabilities.addAll(capaList);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
