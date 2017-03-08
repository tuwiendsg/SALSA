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

    // we marshall/unmarshall in GET/SET function
    String events;
//    SalsaEvents events = new SalsaEvents();

    public CloudService() {
        this.uuid = UUID.randomUUID().toString();
    }

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

    public String getEvents() {
        return events;
    }

    public void setEvents(String events) {
        this.events = events;
    }

    public void addEvent(SalsaEvent event) {
        SalsaEvents ssEvents = readEvents();
        ssEvents.getEvents().add(event);
        this.events = ssEvents.toJson();
    }

    public SalsaEvents readEvents() {
        if (this.events != null && !this.events.isEmpty()) {
            return SalsaEvents.fromJson(this.events);
        }
        return new SalsaEvents();
    }

    public void writeEvents(SalsaEvents events) {
        if (events != null) {
            this.events = events.toJson();
        }
    }

    public CloudService hasTopology(ServiceTopology topology) {
        if (this.topologies == null) {
            this.topologies = new HashSet<>();
        }
        topology.setCloudServiceUuid(this.uuid);
        this.topologies.add(topology);
        return this;
    }

    @JsonIgnore
    public ServiceTopology getTopologyByName(String topologyName) {
        for (ServiceTopology topo : topologies) {
            if (topo.getName().equals(topologyName)) {
                return topo;
            }
        }
        return null;
    }

    @JsonIgnore
    public ServiceUnit getUnitByName(String nodeName) {
        if (topologies != null) {
            for (ServiceTopology topo : topologies) {
                ServiceUnit unit = topo.getUnitByName(nodeName);
                if (unit != null) {
                    return unit;
                }
            }
        }
        return null;
    }

    @JsonIgnore
    public List<ServiceUnit> getAllComponent() {
        List<ServiceUnit> comList = new ArrayList<>();
        for (ServiceTopology topo : topologies) {
            comList.addAll(topo.getUnits());
        }
        return comList;
    }

    @JsonIgnore
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
