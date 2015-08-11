/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.dsg.cloud.elise.model.elasticunit.generic;

import java.util.HashSet;
import java.util.Set;

import org.neo4j.graphdb.Direction;
import org.springframework.data.neo4j.annotation.Fetch;
import org.springframework.data.neo4j.annotation.Indexed;

import at.ac.tuwien.dsg.cloud.elise.model.HasUniqueId;
import at.ac.tuwien.dsg.cloud.salsa.domainmodels.DomainEntity;
import at.ac.tuwien.dsg.cloud.salsa.domainmodels.types.ServiceCategory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonMethod;
import org.codehaus.jackson.map.ObjectMapper;

import org.springframework.data.neo4j.annotation.GraphId;
import org.springframework.data.neo4j.annotation.NodeEntity;
import org.springframework.data.neo4j.annotation.RelatedTo;

/**
 *
 * @author hungld
 *
 * The generic model of service, which have a set of feature which represent the properties and set of primitive operation for the capabilities
 *
 */
@NodeEntity
public class GenericServiceUnit implements HasUniqueId {

    @GraphId
    Long graphID;

    /**
     * This is the global ID in ELISE. the DomainID is stored in the DomainEntity of the domain-models
     */
    @Indexed(unique = true)
    protected String id;

    protected String name;

    protected ServiceCategory category;

    /**
     * Blind properties, just let it be custom
     */
    HashMap<String, String> extra;

    /**
     * Known information model. This can contain a single info of this unit, or full service stack Note: I (23/07/2015) don't know how to use neo4j to store a
     * generic class. Then the domain model is translate into json. Using the parseDomainInfo method to translate back
     */
    List<String> domainInfo = new ArrayList<>();

    /**
     * All the action can be executed
     */
    @RelatedTo(direction = Direction.OUTGOING, type = "INTERNAL")
    @Fetch
    Set<Capability> capabilities;

    {
        //domainInfo = new HashSet<>();
        capabilities = new HashSet<>();
    }

    public GenericServiceUnit() {
    }

    public GenericServiceUnit(String name, ServiceCategory category) {
        this.id = "TMP:" + UUID.randomUUID().toString(); // this is only the temporary UUID, this will be replaced by ELISE when merging units
        System.out.println("Create TMP UUID: " + this.id);
        this.name = name;
        this.category = category;
    }

//    public GenericServiceUnit hasDomainModel(Class<? extends DomainEntity> domain) {
//        this.domainInfo.add(domain);
//        return this;
//    }
    public GenericServiceUnit hasCapability(Capability primitive) {
        this.capabilities.add(primitive);
        return this;
    }

    public GenericServiceUnit hasCapabilities(Set<Capability> primitives) {
        this.capabilities.addAll(primitives);
        return this;
    }

    @Override
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ServiceCategory getCategory() {
        return category;
    }

    public Set<Capability> getCapabilities() {
        return capabilities;
    }

    public Set<String> findAllCapabilities() {
        Set<String> names = new HashSet<>();
        for (Capability p : this.capabilities) {
            names.add(p.getName());
        }
        return names;
    }

    public void setCategory(ServiceCategory category) {
        this.category = category;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, String> getExtra() {
        return extra;
    }

    public List<String> getDomainInfo() {
        return domainInfo;
    }

    public void setDomainInfo(List<String> domainInfo) {
        this.domainInfo = domainInfo;
    }

    public void addDomainInfo(String domain) {
        this.domainInfo.add(domain);
    }
    
    public void addDomainInfo(DomainEntity domain) {
        this.domainInfo.add(domain.toJson());
    }

    public GenericServiceUnit hasExtra(String key, String value) {
        if (this.extra==null){
            this.extra = new HashMap<>();
        }
        this.extra.put(key, value);
        return this;
    }

    public List<DomainEntity> parseDomainInfo() {
        ObjectMapper mapper = new ObjectMapper();
        List<DomainEntity> domains = new ArrayList<>();
        for (String d : this.domainInfo) {
            try {
                DomainEntity domain = mapper.readValue(d, DomainEntity.class);
                domains.add(domain);
            } catch (IOException ex) {
                Logger.getLogger("DomainModel").log(Level.SEVERE, "Cannot parse the domain info. Data to parse: {0}", d);                                
            }
        }
        return domains;
    }
    
    public DomainEntity findDomainInfoByName(String name){
        List<DomainEntity> domains = parseDomainInfo();
        for (DomainEntity d : domains){
            if (d.getName().equals(name)){
                return d;
            }
        }
        return null;
    }
    
     public DomainEntity findDomainInfoByCategory(ServiceCategory category){
        List<DomainEntity> domains = parseDomainInfo();
        for (DomainEntity d : domains){
            if (d.getCategory().equals(category)){
                return d;
            }
        }
        return null;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 13 * hash + Objects.hashCode(this.id);
        hash = 13 * hash + Objects.hashCode(this.name);
        hash = 13 * hash + Objects.hashCode(this.category);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final GenericServiceUnit other = (GenericServiceUnit) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (this.category != other.category) {
            return false;
        }
        return true;
    }

    public String toJson() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(JsonMethod.FIELD, Visibility.ANY);
        try {
            return mapper.writeValueAsString(this);
        } catch (IOException ex) {
            Logger.getLogger(GenericServiceUnit.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

}
