/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.salsa.model;

import at.ac.tuwien.dsg.salsa.model.enums.ConfigurationState;
import at.ac.tuwien.dsg.salsa.model.relationship.Relationship;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.HashSet;
import java.util.Set;
import lombok.Data;

/**
 *
 * @author hungld
 */
@Data
public class ServiceTopology {

    // used for persistent layer, e.g. Neo4J
    Long id;
    String uuid;
    String name;
    Set<ServiceUnit> units;
    Set<Relationship> relationships;

    // management info
    String cloudServiceUuid;
    ConfigurationState state = ConfigurationState.UNKNOWN;

    public ServiceTopology() {
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

}
