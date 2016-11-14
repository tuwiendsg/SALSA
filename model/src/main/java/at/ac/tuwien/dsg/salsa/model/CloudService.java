/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.salsa.model;

import at.ac.tuwien.dsg.salsa.model.enums.ConfigurationState;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author hungld
 */
public class CloudService {

    // used for persistent layer, e.g. Neo4J
    Long id;

    String name; // identification in one scope
    String uuid; // global identification

    Set<ServiceTopology> topologies;
    ConfigurationState state;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Set<ServiceTopology> getTopologies() {
        return topologies;
    }

    public void setTopologies(Set<ServiceTopology> topologies) {
        this.topologies = topologies;
    }

    public ConfigurationState getState() {
        return state;
    }

    public void setState(ConfigurationState state) {
        this.state = state;
    }

    public CloudService hasTopology(ServiceTopology topology) {
        if (this.topologies == null) {
            this.topologies = new HashSet<>();
        }
        this.topologies.add(topology);
        return this;
    }

    public ServiceTopology getComponentTopologyById(String topologyId) {
        for (ServiceTopology topo : topologies) {
            if (topo.getUuid().equals(topologyId)) {
                return topo;
            }
        }
        return null;
    }

    public ServiceUnit getComponentByName(String topologyId, String nodeId) {
        if (topologyId != null) {
            ServiceTopology topo = getComponentTopologyById(topologyId);
            if (topo != null) {
                return topo.getComponentById(nodeId);
            }
        }
        return null;
    }

    public ServiceUnit getComponentByName(String nodeName) {
        if (topologies != null) {
            for (ServiceTopology topo : topologies) {
                ServiceUnit unit = getComponentByName(topo.getUuid(), nodeName);
                if (unit != null) {
                    return unit;
                }
            }
        }
        return null;
    }

    public List<ServiceUnit> getAllComponent() {
        List<ServiceUnit> comList = new ArrayList<>();
        for (ServiceTopology topo : topologies) {
            comList.addAll(topo.getUnits());
        }
        return comList;
    }

    public ServiceTopology getTopologyOfNode(String serviceUnitUuid) {
        for (ServiceTopology topo : topologies) {
            for (ServiceUnit tmpUnit : topo.getUnits()) {
                if (tmpUnit.getUuid().equals(serviceUnitUuid)) {
                    return topo;
                }
            }
        }
        return null;
    }

    public String toJson() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.ALL, Visibility.NONE);
        mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
        try {
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException ex) {
            ex.printStackTrace();
            return null;
        }
    }

}
