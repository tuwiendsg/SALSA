/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.salsa.model;

import at.ac.tuwien.dsg.salsa.model.enums.ConfigurationState;
import at.ac.tuwien.dsg.salsa.model.enums.SalsaEntityType;
import at.ac.tuwien.dsg.salsa.model.relationship.Relationship;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author hungld
 */
public class ServiceTopology {

    // used for persistent layer, e.g. Neo4J
    Long id;
    String uuid;
    String name;
    Set<ServiceUnit> units;
    Set<Relationship> relationships;

    // management info
    String cloudServiceUuid;
    ConfigurationState state;

    public ServiceTopology() {
    }

    public Set<ServiceUnit> getUnits() {
        return units;
    }

    public void setUnits(Set<ServiceUnit> units) {
        this.units = units;
    }

    public Set<Relationship> getRelationships() {
        return relationships;
    }

    public void setRelationships(Set<Relationship> relationships) {
        this.relationships = relationships;
    }

    public String getCloudServiceUuid() {
        return cloudServiceUuid;
    }

    public void setCloudServiceUuid(String cloudServiceUuid) {
        this.cloudServiceUuid = cloudServiceUuid;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ServiceTopology hasUnit(ServiceUnit unit) {
        if (this.units == null) {
            this.units = new HashSet<>();
        }
        unit.setTopologyUuid(this.uuid);
        this.units.add(unit);
        return this;
    }

    @JsonIgnore
    public ServiceUnit getUnitByName(String name) {
        for (ServiceUnit node : units) {
            if (node.getName().equals(name)) {
                return node;
            }
        }
        return null;
    }

    @JsonIgnore
    public List<ServiceUnit> getUnitsByType(SalsaEntityType type) {
        List<ServiceUnit> lst = new ArrayList<>();
        for (ServiceUnit node : units) {
            if (SalsaEntityType.fromString(node.getType()) == type) {
                lst.add(node);
            }
        }
        return lst;
    }

}
