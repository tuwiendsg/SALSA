/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.salsa.model;

import at.ac.tuwien.dsg.salsa.model.enums.ConfigurationState;
import at.ac.tuwien.dsg.salsa.model.salsa.info.SalsaEvent;
import at.ac.tuwien.dsg.salsa.model.salsa.info.SalsaEvents;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.Data;

/**
 *
 * @author hungld
 */
@Data
public class CloudService {

    // used for persistent layer, e.g. Neo4J
    Long id;

    String name; // identification in one scope
    String uuid; // global identification

    Set<ServiceTopology> topos;
    ConfigurationState state = ConfigurationState.UNKNOWN;

    // log configuration events
    SalsaEvents events = new SalsaEvents();

    public CloudService() {
        this.uuid = UUID.randomUUID().toString();
    }

    public CloudService hasEvent(SalsaEvent event) {
        this.events.hasEvent(event);
        return this;
    }

    public CloudService hasTopology(ServiceTopology topology) {
        if (this.topos == null) {
            this.topos = new HashSet<>();
        }
        topology.setCloudServiceUuid(this.uuid);
        this.topos.add(topology);
        return this;
    }

    @JsonIgnore
    public ServiceTopology getTopologyByName(String topologyName) {
        for (ServiceTopology topo : topos) {
            if (topo.getName().equals(topologyName)) {
                return topo;
            }
        }
        return null;
    }

    @JsonIgnore
    public ServiceUnit getUnitByName(String nodeName) {
        if (topos != null) {
            for (ServiceTopology topo : topos) {
                ServiceUnit unit = topo.getUnitByName(nodeName);
                if (unit != null) {
                    return unit;
                }
            }
        }
        return null;
    }

    @JsonIgnore
    public List<ServiceUnit> getAllUnits() {
        List<ServiceUnit> comList = new ArrayList<>();
        System.out.println("getAllUnit: topos is: " + topos);
        for (ServiceTopology topo : topos) {
            comList.addAll(topo.getUnits().values());
        }
        return comList;
    }

    @JsonIgnore
    public ServiceTopology getTopologyOfNode(String serviceUnitUuid) {
        for (ServiceTopology topo : topos) {
            for (ServiceUnit tmpUnit : topo.getUnits().values()) {
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
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(this);
        } catch (JsonProcessingException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static CloudService fromJson(String json) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.ALL, Visibility.NONE);
        mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
        try {
            return mapper.readValue(json, CloudService.class);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
