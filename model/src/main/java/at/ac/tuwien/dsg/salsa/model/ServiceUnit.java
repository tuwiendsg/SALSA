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
import lombok.Data;

/**
 *
 * @author hungld
 */
@Data
public class ServiceUnit {

    // for the ID in the database
    Long id;
    // ID for SALSA
    String uuid;
    // ID internal service description
    String name;

    Set<ServiceInstance> instances;
    int idCounter = 0;
    int min = 1;
    int max = 1;
    String reference;
    Set<Artifact> artifacts;
    Map<String, Capability> capabilities;
    String hostedOn;
    List<String> connectTo = new ArrayList<>();
    String type;
    List<String> pioneerIds = new ArrayList<>();

    // management info
    String topologyUuid;
    String cloudServiceUuid;
    ConfigurationState state = ConfigurationState.UNKNOWN;

    // custom properties, abstract level
    Map<String, String> properties = new HashMap<>();

    String mainArtifactType;

    // capability var to store properties that it will share
//    List<String> capabilityVars = new ArrayList<>();
    public ServiceUnit() {
    }

    public ServiceUnit(String uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }

    public ServiceUnit hasProperty(String key, String value) {
        if (this.properties == null) {
            this.properties = new HashMap<>();
        }
        this.properties.put(key, value);
        return this;
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
            this.capabilities = new HashMap<>();
        }
        this.capabilities.put(capability.getName(), capability);
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

}
