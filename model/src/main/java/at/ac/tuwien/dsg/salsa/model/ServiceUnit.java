/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.salsa.model;

import at.ac.tuwien.dsg.salsa.model.enums.ConfigurationState;
import at.ac.tuwien.dsg.salsa.model.properties.Artifact;
import at.ac.tuwien.dsg.salsa.model.properties.Capability;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    Map<String, String> properties = new HashMap<>();

    // capability var to store properties that it will share
    List<String> capabilityVars = new ArrayList<>();

    // this field is for determining deployment artifact to be used. Should be remove later
    String mainArtifactType;

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

    public List<String> getCapabilityVars() {
        return capabilityVars;
    }

    public void setCapabilityVars(List<String> capabilityVars) {
        this.capabilityVars = capabilityVars;
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

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    public ServiceInstance getInstanceByIndex(int index) {
        for (ServiceInstance instance : instances) {
            if (instance.getIndex() == index) {
                return instance;
            }
        }
        return null;
    }

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

}
